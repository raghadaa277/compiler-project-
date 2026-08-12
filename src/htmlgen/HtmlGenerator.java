package htmlgen;

import antlr.html.HtmlLexer;
import antlr.html.HtmlParser;
import ast.HtmlContent;
import ast.Program;
import ast.Statement;
import ast.argsList.ArgumentsList;
import ast.atomExpression.AtomExpression;
import ast.atomExpression.SimpleVariable;
import ast.compundStmt.CompoundStatement;
import ast.compundStmt.PythonExpression;
import ast.functionDef.Decorator;
import ast.functionDef.FunctionDefinition;
import asttojson.AstToJson;
import codegen.AstToTac;
import codegen.PythonCodeGenerator;
import codegen.ir.TacProgram;
import contextgen.ContextGenerator;
import generationlogger.GenerationLogger;
import listener.CustomErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import outputwriter.OutputWriter;
import semantic.*;
import templaterenderer.TemplateRenderer;
import visitor.css.StyleSheetVisitor;
import visitor.html.HtmlContentVisitor;
import visitor.python.ProgramVisitor;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableManager;

public class HtmlGenerator {

    private static final List<SemanticError> sharedErrors = new ArrayList<>();
    private static String pythonSymbolTableJson = "[]";
    private static String jinjaSymbolTableJson = "[]";
    private static Map<String, String> sharedRouteTable = new LinkedHashMap<>();
    private static Map<String, String> endpointToFileMap = new LinkedHashMap<>();

    public static void setPythonSymbolTableJson(String json) {
        if (json != null) pythonSymbolTableJson = json;
    }

    public static void setJinjaSymbolTableJson(String json) {
        if (json != null) jinjaSymbolTableJson = json;
    }

    public static void addSharedErrors(List<SemanticError> errors, String sourceFileName) {
        if (errors != null) {
            for (SemanticError e : errors) {
                if (e.fileName == null) e.fileName = sourceFileName;
                sharedErrors.add(e);
            }
        }
    }

    public static void addSharedErrors(List<SemanticError> errors) {
        if (errors != null) sharedErrors.addAll(errors);
    }

    public static void addSharedError(SemanticError err) {
        if (err != null) sharedErrors.add(err);
    }

    public static void clearSharedErrors() {
        sharedErrors.clear();
    }

    public static void resetSharedRouteTable() {
        sharedRouteTable.clear();
        endpointToFileMap.clear();
    }

    private final GenerationLogger logger;
    private final OutputWriter outputWriter;
    private final ContextGenerator contextGenerator;
    private final AstToJson astToJson;
    private final String baseDir;
    private final String templateDir;

    private Program pythonProgram;
    private List<SemanticError> allSemanticErrors;
    private boolean hasSemanticErrors;

    public HtmlGenerator(String baseDir, GenerationLogger logger) {
        this.logger = logger;
        this.baseDir = baseDir;
        this.templateDir = findTemplateDir(baseDir);
        this.outputWriter = new OutputWriter(System.getProperty("user.dir"));
        this.contextGenerator = new ContextGenerator();
        this.astToJson = new AstToJson();
        this.allSemanticErrors = new ArrayList<>();
        this.hasSemanticErrors = false;
    }

    public void writePythonAst(Program program) {
        logger.parsingPython();
        logger.generatingPythonAst();
        String pythonAstJson = astToJson.programToJson(program);
        outputWriter.writeJson("ast_python.json", pythonAstJson);
    }

    public void runSemanticAnalysis(Program program, String filePath) {
        logger.semanticAnalysis();
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        hasSemanticErrors = !analyzer.analyze(program, filePath);
        allSemanticErrors.addAll(analyzer.getErrors());
    }

    public void finalizeLog() {
        String logText = logger.getLogText();
        outputWriter.writeCompilerOutput("generation_log.txt", logText);
    }

    public boolean hasErrors() {
        return hasSemanticErrors;
    }

    private Set<String> registerAllTemplates() {
        Set<String> registered = new HashSet<>();
        File tDir = new File(templateDir);
        if (tDir.exists() && tDir.isDirectory()) {
            File[] files = tDir.listFiles((dir, name) -> name.endsWith(".html") || name.endsWith(".j2"));
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    if (!registered.contains(name)) {
                        registered.add(name);
                    }
                }
            }
        }
        return registered;
    }

    public void writeJinjaAst() {
        Set<String> registered = registerAllTemplates();
        String jinjaAstJson = buildJinjaAstJson(registered);
        outputWriter.writeJson("ast_jinja.json", jinjaAstJson);
    }

    public void generateOutput(Program program, String filePath) throws Exception {
        this.pythonProgram = program;

        logger.generatingContext();
        Map<String, Object> context = contextGenerator.generateContext(program, filePath);

        logger.parsingJinja();
        logger.generatingJinjaAst();

        extractRouteTable(program);
        buildEndpointToFileMap(program);
        TemplateRenderer renderer = new TemplateRenderer(templateDir, context);
        renderer.setRouteTable(sharedRouteTable);
        renderer.setEndpointToFileMap(endpointToFileMap);

        Map<String, String> renderedTemplates = new LinkedHashMap<>();
        Map<String, Set<String>> templateToVars = TemplateVariableChecker.getTemplateVars();

        registerTemplates(renderer, new HashSet<>());

        logger.renderingTemplates();
        logger.resolvingVariables();
        Set<String> renderedNow = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : templateToVars.entrySet()) {
            String templateName = entry.getKey();
            Set<String> varNames = entry.getValue();

            String rendered = renderTemplate(templateName, varNames, renderer);
            if (rendered != null) {
                String htmlFileName = templateToHtmlFileName(templateName);
                renderedTemplates.put(htmlFileName, rendered);
                renderedNow.add(templateName);
            }
        }

        // Also render any template files not directly referenced by render_template() (e.g. base.html)
        File tDir = new File(templateDir);
        if (tDir.exists() && tDir.isDirectory()) {
            File[] files = tDir.listFiles((dir, name) -> name.endsWith(".html") || name.endsWith(".j2"));
            if (files != null) {
                for (File f : files) {
                    String templateName = f.getName();
                    if (!renderedNow.contains(templateName) && !renderedTemplates.containsKey(templateToHtmlFileName(templateName))) {
                        String rendered = renderTemplate(templateName, new HashSet<>(), renderer);
                        if (rendered != null) {
                            renderedTemplates.put(templateToHtmlFileName(templateName), rendered);
                        }
                    }
                }
            }
        }

        logger.writingHtml();
        for (var entry : renderedTemplates.entrySet()) {
            logger.generatingHtml(entry.getKey());
            String html = entry.getValue();
            html = injectCrudScripts(html, entry.getKey(), context);
            outputWriter.writeTemplate(entry.getKey(), html);
        }

        logger.validationStarted();
        validateNoJinjaSyntax(renderedTemplates);
        logger.validationPassed();

        logger.copyingAssets();
        copyAssets();

        writeCrudJs();

        logger.generationCompleted();

        String logText = logger.getLogText();
        outputWriter.writeCompilerOutput("generation_log.txt", logText);
    }

    public void generate(Program program, String filePath) throws Exception {
        writePythonAst(program);
        runSemanticAnalysis(program, filePath);
        setPythonSymbolTableJson(buildPythonSymbolTableJson());
        writeJinjaAst();
        if (allSemanticErrors != null) {
            String pyFileName = new java.io.File(filePath).getName();
            for (SemanticError e : allSemanticErrors) {
                if (e.fileName == null) e.fileName = pyFileName;
                sharedErrors.add(e);
            }
        }
        if (!hasSemanticErrors) {
            generateOutput(program, filePath);
        } else {
            logger.generatingContext();
            logger.parsingJinja();
            logger.generatingJinjaAst();
            logger.renderingTemplates();
            logger.writingHtml();
            logger.copyingAssets();
            logger.generationCompleted();
            finalizeLog();
        }
    }

    public static void writeFinalReport() {
        OutputWriter out = new OutputWriter(System.getProperty("user.dir"));
        List<SemanticError> all = new ArrayList<>(sharedErrors);
        StringBuilder report = new StringBuilder();

        java.util.LinkedHashMap<String, java.util.List<SemanticError>> categories = new java.util.LinkedHashMap<>();
        categories.put("Undefined Variables", new java.util.ArrayList<>());
        categories.put("Duplicate Functions", new java.util.ArrayList<>());
        categories.put("Scope Errors", new java.util.ArrayList<>());
        categories.put("Type Errors", new java.util.ArrayList<>());
        categories.put("Warnings", new java.util.ArrayList<>());
        categories.put("Other Errors", new java.util.ArrayList<>());

        for (SemanticError err : all) {
            String msg = err.message;
            if (msg.startsWith("CSS Error") || msg.startsWith("HTML Error") || msg.startsWith("Flask Error"))
                categories.get("Other Errors").add(err);
            else if (msg.contains("not defined") || msg.contains("undefined") || msg.contains("Undefined"))
                categories.get("Undefined Variables").add(err);
            else if (msg.contains("Duplicate") || msg.contains("duplicate"))
                categories.get("Duplicate Functions").add(err);
            else if (msg.contains("Infinite recursion") || msg.contains("infinite") || msg.contains("outside"))
                categories.get("Scope Errors").add(err);
            else if (msg.contains("Type mismatch") || msg.contains("Mismatch"))
                categories.get("Type Errors").add(err);
            else if (msg.contains("Warning") || msg.contains("warning"))
                categories.get("Warnings").add(err);
            else
                categories.get("Other Errors").add(err);
        }

        report.append("========== SEMANTIC ANALYSIS REPORT ==========\n");
        for (var entry : categories.entrySet()) {
            report.append("\n");
            report.append(entry.getKey()).append(": ").append(entry.getValue().size()).append("\n");
            for (SemanticError e : entry.getValue()) {
                report.append("  - ").append(e).append("\n");
            }
        }
        report.append("\n");
        report.append("Total Errors: ").append(all.size()).append("\n");
        report.append("==============================================\n");

        out.writeCompilerOutput("semantic_report.txt", report.toString());

        String symbolTableJson = "{\n" +
            "  \"python_symbols\": " + pythonSymbolTableJson + ",\n" +
            "  \"jinja_symbols\": " + jinjaSymbolTableJson + "\n" +
            "}\n";
        out.writeJson("symbol_table.json", symbolTableJson);
    }

    private void registerTemplates(TemplateRenderer renderer, Set<String> registered) {
        File tDir = new File(templateDir);
        if (tDir.exists() && tDir.isDirectory()) {
            File[] files = tDir.listFiles((dir, name) -> name.endsWith(".html") || name.endsWith(".j2"));
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    if (!registered.contains(name)) {
                        registered.add(name);
                        HtmlContent content = parseHtmlFile(f.getAbsolutePath());
                        if (content != null) {
                            renderer.registerParsedTemplate(name, content);
                        }
                    }
                }
            }
        }
    }

    private HtmlContent parseHtmlFile(String filePath) {
        try {
            HtmlLexer lexer = new HtmlLexer(CharStreams.fromFileName(filePath));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            HtmlParser parser = new HtmlParser(tokens);
            parser.removeErrorListeners();
            var tree = parser.html_content();
            HtmlContentVisitor visitor = new HtmlContentVisitor();
            return visitor.visit(tree);
        } catch (Exception e) {
            System.err.println("Could not parse HTML template: " + filePath + " - " + e.getMessage());
            return null;
        }
    }

    private String renderTemplate(String templateName, Set<String> varNames, TemplateRenderer renderer) {
        HtmlContent content = getTemplateContent(templateName);
        if (content == null) return null;
        return renderer.render(content, null);
    }

    private HtmlContent getTemplateContent(String templateName) {
        File templateFile = new File(templateDir, templateName);
        if (!templateFile.exists()) {
            templateFile = new File(templateDir, templateName);
            if (!templateFile.exists()) {
                System.err.println("Template not found: " + templateName);
                return null;
            }
        }
        return parseHtmlFile(templateFile.getAbsolutePath());
    }

    private String templateToHtmlFileName(String templateName) {
        if (templateName.endsWith(".html") || templateName.endsWith(".j2")) {
            return templateName;
        }
        return templateName + ".html";
    }

    private String findTemplateDir(String filePath) {
        File f = new File(filePath);
        String parent = f.getParent();
        if (parent == null) parent = ".";

        File templatesDir = new File(parent, "templates");
        if (templatesDir.exists() && templatesDir.isDirectory()) {
            return templatesDir.getAbsolutePath();
        }

        File projectDir = new File(parent);
        templatesDir = new File(projectDir, "templates");
        if (templatesDir.exists() && templatesDir.isDirectory()) {
            return templatesDir.getAbsolutePath();
        }

        return parent;
    }

    private void copyAssets() {
        File baseDirFile = new File(baseDir);
        File sampleDir = baseDirFile.getParentFile();
        if (sampleDir == null || !sampleDir.exists()) return;

        // Copy all .py files from the sample directory
        File[] pyFiles = sampleDir.listFiles((dir, name) -> name.endsWith(".py"));
        if (pyFiles != null) {
            for (File py : pyFiles) {
                outputWriter.copyFileToRoot(py.toPath());
            }
        }

        // Copy requirements.txt if it exists
        File reqFile = new File(sampleDir, "requirements.txt");
        if (reqFile.exists()) {
            outputWriter.copyFileToRoot(reqFile.toPath());
        }

        // Copy the entire static/ directory recursively if it exists
        File staticDir = new File(sampleDir, "static");
        if (staticDir.exists() && staticDir.isDirectory()) {
            outputWriter.copyDirectory(staticDir.toPath(), outputWriter.getOutputDir().resolve("static"));
        }

        // Patch models.py to use sequential IDs instead of random UUIDs so compiled URLs match DB
        patchOutputModels();
    }

    private void patchOutputModels() {
        try {
            Path modelsPath = outputWriter.getOutputDir().resolve("models.py");
            if (!modelsPath.toFile().exists()) return;
            String content = new String(java.nio.file.Files.readAllBytes(modelsPath));
            // Replace UUID-based ID generation with auto-increment counter
            content = content.replace(
                "import uuid",
                "import itertools"
            );
            content = content.replace(
                "        self.id = str(uuid.uuid4())[:8]",
                "        self.id = str(next(Product._ids))"
            );
            content = content.replace(
                "    products = {}  # تخزين مؤقت للمنتجات {id: product_data}",
                "    _ids = itertools.count(1)\n    products = {}  # تخزين مؤقت للمنتجات {id: product_data}"
            );
            java.nio.file.Files.write(modelsPath, content.getBytes());
        } catch (Exception e) {
            System.err.println("[WARN] Could not patch models.py: " + e.getMessage());
        }
    }

    private void writeCrudJs() {
        String crudJs = "var CrudManager = {\n" +
            "    STORAGE_KEY: 'products',\n" +
            "    defaultProducts: [],\n" +
            "\n" +
            "    setDefaults: function(products) {\n" +
            "        this.defaultProducts = products || [];\n" +
            "    },\n" +
            "\n" +
            "    isValidProduct: function(p) {\n" +
            "        if (!p || !p.id) return false;\n" +
            "        var n = (p.name || '').trim();\n" +
            "        var d = (p.details || p.description || '').trim();\n" +
            "        var i = (p.image_filename || '').trim();\n" +
            "        return (n.length > 0 || d.length > 0 || i.length > 0);\n" +
            "    },\n" +
            "\n" +
            "    getProducts: function() {\n" +
            "        var data = localStorage.getItem(this.STORAGE_KEY);\n" +
            "        if (!data) return [];\n" +
            "        try { return JSON.parse(data); } catch(e) { return []; }\n" +
            "    },\n" +
            "\n" +
            "    saveProducts: function(products) {\n" +
            "        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(products));\n" +
            "    },\n" +
            "\n" +
            "    readDOMProducts: function() {\n" +
            "        var domProducts = [];\n" +
            "        var cards = document.querySelectorAll('[data-product-id]');\n" +
            "        cards.forEach(function(card) {\n" +
            "            var imgVal = card.getAttribute('data-product-image') || '';\n" +
            "            if (imgVal.indexOf('/') !== -1) imgVal = imgVal.split('/').pop();\n" +
            "            domProducts.push({\n" +
            "                id: card.getAttribute('data-product-id'),\n" +
            "                name: card.getAttribute('data-product-name') || '',\n" +
            "                price: parseFloat(card.getAttribute('data-product-price')) || 0,\n" +
            "                details: card.getAttribute('data-product-details') || '',\n" +
            "                image_filename: imgVal\n" +
            "            });\n" +
            "        });\n" +
            "        if (domProducts.length === 0) {\n" +
            "            var fallback = document.querySelectorAll('.products-container .card, .product-grid .product-card');\n" +
            "            fallback.forEach(function(card) {\n" +
            "                if (card.querySelector('[data-product-id]')) return;\n" +
            "                var imgEl = card.querySelector('img');\n" +
            "                var imgSrc = imgEl ? imgEl.getAttribute('src') || '' : '';\n" +
            "                var imgFile = imgSrc.indexOf('/') !== -1 ? imgSrc.split('/').pop() : imgSrc;\n" +
            "                var h3 = card.querySelector('h3, h5, h5.card-title');\n" +
            "                var priceEl = card.querySelector('.price, .price-tag');\n" +
            "                var priceText = priceEl ? priceEl.textContent.replace(/[^0-9.]/g, '') : '0';\n" +
            "                var link = card.querySelector('a[href*=\"product_detail\"], a[href*=\"details\"]');\n" +
            "                var href = link ? link.getAttribute('href') || '' : '';\n" +
            "                var idMatch = href.match(/id=(\\d+)/);\n" +
            "                var cardId = idMatch ? idMatch[1] : '';\n" +
            "                if (!cardId) return;\n" +
            "                domProducts.push({\n" +
            "                    id: cardId,\n" +
            "                    name: h3 ? h3.textContent.trim() : '',\n" +
            "                    price: parseFloat(priceText) || 0,\n" +
            "                    details: '',\n" +
            "                    image_filename: imgFile\n" +
            "                });\n" +
            "            });\n" +
            "        }\n" +
            "        return domProducts;\n" +
            "    },\n" +
            "\n" +
            "    initFromDOM: function() {\n" +
            "        var domProducts = this.readDOMProducts();\n" +
            "        if (domProducts.length === 0) return;\n" +
            "        var existing = this.getProducts();\n" +
            "        var existingMap = {};\n" +
            "        existing.forEach(function(p) { existingMap[p.id] = p; });\n" +
            "        var domIds = {};\n" +
            "        domProducts.forEach(function(p) {\n" +
            "            existingMap[p.id] = p;\n" +
            "            domIds[p.id] = true;\n" +
            "        });\n" +
            "        var merged = [];\n" +
            "        domProducts.forEach(function(p) { merged.push(p); });\n" +
            "        existing.forEach(function(p) {\n" +
            "            if (!domIds[p.id]) merged.push(p);\n" +
            "        });\n" +
            "        this.saveProducts(merged);\n" +
            "    },\n" +
            "\n" +
            "    syncAndRender: function(containerId) {\n" +
            "        var self = this;\n" +
            "        var domProducts = this.readDOMProducts();\n" +
            "        if (domProducts.length > 0) {\n" +
            "            var existing = this.getProducts();\n" +
            "            var existingMap = {};\n" +
            "            existing.forEach(function(p) { if (self.isValidProduct(p)) existingMap[p.id] = p; });\n" +
            "            var domIds = {};\n" +
            "            domProducts.forEach(function(p) {\n" +
            "                existingMap[p.id] = p;\n" +
            "                domIds[p.id] = true;\n" +
            "            });\n" +
            "            var merged = [];\n" +
            "            domProducts.forEach(function(p) { merged.push(p); });\n" +
            "            existing.forEach(function(p) {\n" +
            "                if (!domIds[p.id] && self.isValidProduct(p)) merged.push(p);\n" +
            "            });\n" +
            "            this.saveProducts(merged);\n" +
            "        }\n" +
            "        var dirty = sessionStorage.getItem('crud_dirty');\n" +
            "        if (dirty) {\n" +
            "            sessionStorage.removeItem('crud_dirty');\n" +
            "            this.renderProductCards(containerId);\n" +
            "        } else if (domProducts.length === 0) {\n" +
            "            var stored = this.getProducts();\n" +
            "            var validStored = stored.filter(function(p) { return self.isValidProduct(p); });\n" +
            "            if (validStored.length !== stored.length) this.saveProducts(validStored);\n" +
            "            if (validStored.length > 0) {\n" +
            "                this.renderProductCards(containerId);\n" +
            "            } else if (this.defaultProducts.length > 0) {\n" +
            "                var validDefaults = this.defaultProducts.filter(function(p) { return self.isValidProduct(p); });\n" +
            "                if (validDefaults.length > 0) {\n" +
            "                    this.saveProducts(validDefaults.slice());\n" +
            "                    this.renderProductCards(containerId);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "\n" +
            "    nextId: function() {\n" +
            "        var products = this.getProducts();\n" +
            "        var maxId = 0;\n" +
            "        products.forEach(function(p) {\n" +
            "            var num = parseInt(p.id);\n" +
            "            if (!isNaN(num) && num > maxId) maxId = num;\n" +
            "        });\n" +
            "        return String(maxId + 1);\n" +
            "    },\n" +
            "\n" +
            "    addProduct: function(data) {\n" +
            "        var products = this.getProducts();\n" +
            "        var newProduct = {\n" +
            "            id: this.nextId(),\n" +
            "            name: data.name || '',\n" +
            "            price: parseFloat(data.price) || 0,\n" +
            "            details: data.details || data.description || '',\n" +
            "            image_filename: data.image || data.img || '',\n" +
            "            description: data.description || '',\n" +
            "            specification: data.specification || ''\n" +
            "        };\n" +
            "        products.push(newProduct);\n" +
            "        this.saveProducts(products);\n" +
            "        return newProduct;\n" +
            "    },\n" +
            "\n" +
            "    getProduct: function(id) {\n" +
            "        var products = this.getProducts();\n" +
            "        for (var i = 0; i < products.length; i++) {\n" +
            "            if (products[i].id === id) return products[i];\n" +
            "        }\n" +
            "        return null;\n" +
            "    },\n" +
            "\n" +
            "    updateProduct: function(id, data) {\n" +
            "        var products = this.getProducts();\n" +
            "        for (var i = 0; i < products.length; i++) {\n" +
            "            if (products[i].id === id) {\n" +
            "                products[i].name = data.name || products[i].name;\n" +
            "                products[i].price = parseFloat(data.price) || products[i].price;\n" +
            "                products[i].details = data.details || data.description || products[i].details;\n" +
            "                products[i].description = data.description || products[i].description;\n" +
            "                products[i].specification = data.specification || products[i].specification;\n" +
            "                if (data.image || data.img) products[i].image_filename = data.image || data.img;\n" +
            "                this.saveProducts(products);\n" +
            "                return products[i];\n" +
            "            }\n" +
            "        }\n" +
            "        return null;\n" +
            "    },\n" +
            "\n" +
            "    deleteProduct: function(id) {\n" +
            "        var products = this.getProducts();\n" +
            "        var filtered = [];\n" +
            "        for (var i = 0; i < products.length; i++) {\n" +
            "            if (products[i].id !== id) filtered.push(products[i]);\n" +
            "        }\n" +
            "        this.saveProducts(filtered);\n" +
            "        return filtered.length < products.length;\n" +
            "    },\n" +
            "\n" +
            "    collectFormData: function(form) {\n" +
            "        var data = {};\n" +
            "        for (var i = 0; i < form.elements.length; i++) {\n" +
            "            var el = form.elements[i];\n" +
            "            var key = el.name || '';\n" +
            "            if (!key && el.id) {\n" +
            "                key = el.id.replace(/^(add-|edit-)/, '');\n" +
            "            }\n" +
            "            if (key) data[key] = el.value;\n" +
            "        }\n" +
            "        return data;\n" +
            "    },\n" +
            "\n" +
            "    renderProductCards: function(containerId) {\n" +
            "        var container = document.getElementById(containerId);\n" +
            "        if (!container) return;\n" +
            "        var products = this.getProducts();\n" +
            "        var self = this;\n" +
            "        products = products.filter(function(p) {\n" +
            "            return self.isValidProduct(p);\n" +
            "        });\n" +
            "        container.innerHTML = '';\n" +
            "        if (products.length === 0) {\n" +
            "            return;\n" +
            "        }\n" +
            "        var isGrid = container.classList.contains('product-grid');\n" +
            "        var html = '';\n" +
            "        for (var i = products.length - 1; i >= 0; i--) {\n" +
            "            var p = products[i];\n" +
            "            var dpAttrs = ' data-product-id=\"' + p.id + '\" data-product-name=\"' + (p.name || '').replace(/\"/g, '&quot;') + '\" data-product-price=\"' + p.price + '\" data-product-details=\"' + (p.details || '').replace(/\"/g, '&quot;') + '\" data-product-image=\"' + (p.image_filename || '') + '\"';\n" +
            "            if (isGrid) {\n" +
            "                var imgSrc = p.image_filename ? ('static/uploads/' + p.image_filename) : '';\n" +
            "                html += '<div class=\"product-card\"' + dpAttrs + '>' +\n" +
            "                    (imgSrc ? '<img src=\"' + imgSrc + '\" alt=\"product image\" onerror=\"this.onerror=null;this.src=\\'static/images/' + p.image_filename + '\\';\" />' : '') +\n" +
            "                    '<h3>' + p.name + '</h3>' +\n" +
            "                    '<p class=\"price\">$' + (typeof p.price === 'number' ? p.price.toFixed(2) : p.price) + '</p>' +\n" +
            "                    '<a class=\"btn\" href=\"product_detail.html?id=' + p.id + '\">View</a> ' +\n" +
            "                    '<a class=\"btn delete\" href=\"#\" onclick=\"CrudManager.handleDelete(\\'' + p.id + '\\'); return false;\">Delete</a>' +\n" +
            "                    '</div>';\n" +
            "            } else {\n" +
            "                var imgHtml = p.image_filename\n" +
            "                    ? '<img src=\"static/uploads/' + p.image_filename + '\" alt=\"' + p.name + '\" class=\"product-img mb-3\" onerror=\"this.onerror=null;this.src=\\'static/images/' + p.image_filename + '\\';\" />'\n" +
            "                    : '<div class=\"product-img bg-light d-flex align-items-center justify-content-center mb-3\"><i class=\"fas fa-image fa-4x text-secondary\"></i></div>';\n" +
            "                var shortDetails = p.details && p.details.length > 100 ? p.details.substring(0, 100) + '...' : (p.details || '');\n" +
            "                html += '<div class=\"col-md-4 col-lg-3 mb-4\">' +\n" +
            "                    '<div class=\"card h-100\"' + dpAttrs + '>' +\n" +
            "                    '<div class=\"card-body text-center\">' + imgHtml +\n" +
            "                    '<h5 class=\"card-title\">' + p.name + '</h5>' +\n" +
            "                    '<p class=\"price-tag\">$' + (typeof p.price === 'number' ? p.price.toFixed(2) : p.price) + '</p>' +\n" +
            "                    '<p class=\"card-text text-muted\">' + shortDetails + '</p>' +\n" +
            "                    '<div class=\"btn-group w-100\" role=\"group\">' +\n" +
            "                    '<a href=\"product_detail.html?id=' + p.id + '\" class=\"btn btn-sm btn-outline-primary\"><i class=\"fas fa-eye\"></i> Details</a> ' +\n" +
            "                    '<a href=\"#\" class=\"btn btn-sm btn-outline-danger\" onclick=\"CrudManager.handleDelete(\\'' + p.id + '\\'); return false;\"><i class=\"fas fa-trash\"></i> Delete</a>' +\n" +
            "                    '</div></div></div></div>';\n" +
            "            }\n" +
            "        }\n" +
            "        container.innerHTML = html;\n" +
            "    },\n" +
            "\n" +
            "    renderProductDetail: function(containerId) {\n" +
            "        var container = document.getElementById(containerId);\n" +
            "        if (!container) return;\n" +
            "        var params = new URLSearchParams(window.location.search);\n" +
            "        var id = params.get('id');\n" +
            "        if (!id) { container.innerHTML = '<p>Product not found</p>'; return; }\n" +
            "        var p = this.getProduct(id);\n" +
            "        if (!p || !this.isValidProduct(p)) { container.innerHTML = '<p>Product not found</p>'; return; }\n" +
            "        var isFlexDetail = container.classList.contains('detail-container') || container.classList.contains('details-card');\n" +
            "        if (isFlexDetail) {\n" +
            "            var imgSrc = p.image_filename ? 'static/uploads/' + p.image_filename : '';\n" +
            "            container.innerHTML = (imgSrc ? '<img class=\"big-img\" src=\"' + imgSrc + '\" alt=\"product image\" onerror=\"this.onerror=null;this.src=\\'static/images/' + p.image_filename + '\\';\">' : '') +\n" +
            "                '<div class=\"info\">' +\n" +
            "                '<h2>' + p.name + '</h2>' +\n" +
            "                '<p class=\"price\">$' + (typeof p.price === 'number' ? p.price.toFixed(2) : p.price) + '</p>' +\n" +
            "                '<h3>Description</h3><p>' + (p.description || p.details || '') + '</p>' +\n" +
            "                '<h3>Specifications</h3><p>' + (p.specification || '') + '</p>' +\n" +
            "                '<a class=\"btn\" href=\"edit_product.html?id=' + p.id + '\">Edit</a> ' +\n" +
            "                '<a class=\"btn delete\" href=\"#\" onclick=\"CrudManager.handleDelete(\\'' + p.id + '\\'); return false;\">Delete Product</a>' +\n" +
            "                '</div>';\n" +
            "        } else {\n" +
            "            var imgHtml = p.image_filename\n" +
            "                ? '<img src=\"static/uploads/' + p.image_filename + '\" class=\"product-img mb-3\" alt=\"' + p.name + '\" onerror=\"this.onerror=null;this.src=\\'static/images/' + p.image_filename + '\\';\" />'\n" +
            "                : '<div class=\"product-img bg-light d-flex align-items-center justify-content-center mb-3\"><i class=\"fas fa-image fa-4x text-secondary\"></i></div>';\n" +
            "            container.innerHTML = '<div class=\"row\"><div class=\"col-md-6 text-center\">' + imgHtml +\n" +
            "                '</div><div class=\"col-md-6\"><h2>' + p.name + '</h2>' +\n" +
            "                '<p class=\"price-tag\">$' + (typeof p.price === 'number' ? p.price.toFixed(2) : p.price) + '</p>' +\n" +
            "                '<p>' + (p.details || '') + '</p>' +\n" +
            "                '<div class=\"btn-group w-100\">' +\n" +
            "                '<a href=\"edit_product.html?id=' + p.id + '\" class=\"btn btn-warning\"><i class=\"fas fa-edit\"></i> Edit</a> ' +\n" +
            "                '<a href=\"#\" class=\"btn btn-danger\" onclick=\"CrudManager.handleDelete(\\'' + p.id + '\\'); return false;\"><i class=\"fas fa-trash\"></i> Delete</a> ' +\n" +
            "                '<a href=\"index.html\" class=\"btn btn-secondary\"><i class=\"fas fa-arrow-right\"></i> Back</a></div></div></div>';\n" +
            "        }\n" +
            "    },\n" +
            "\n" +
            "    populateEditForm: function() {\n" +
            "        var params = new URLSearchParams(window.location.search);\n" +
            "        var id = params.get('id');\n" +
            "        if (!id) return;\n" +
            "        var p = this.getProduct(id);\n" +
            "        if (!p) return;\n" +
            "        var fields = { 'edit-name': p.name, 'edit-price': p.price, 'edit-details': p.details || p.description, 'edit-id': p.id, 'edit-specification': p.specification || '', 'edit-image': p.image_filename || '' };\n" +
            "        for (var key in fields) {\n" +
            "            var el = document.getElementById(key);\n" +
            "            if (el) el.value = fields[key];\n" +
            "        }\n" +
            "    },\n" +
            "\n" +
            "    handleAdd: function(e) {\n" +
            "        e.preventDefault();\n" +
            "        var form = e.target;\n" +
            "        var data = this.collectFormData(form);\n" +
            "        this.addProduct(data);\n" +
            "        sessionStorage.setItem('crud_dirty', '1');\n" +
            "        window.location.href = 'index.html';\n" +
            "    },\n" +
            "\n" +
            "    handleEdit: function(e) {\n" +
            "        e.preventDefault();\n" +
            "        var form = e.target;\n" +
            "        var data = this.collectFormData(form);\n" +
            "        var id = data.id || (document.getElementById('edit-id') ? document.getElementById('edit-id').value : '');\n" +
            "        if (!id) return;\n" +
            "        this.updateProduct(id, data);\n" +
            "        sessionStorage.setItem('crud_dirty', '1');\n" +
            "        window.location.href = 'product_detail.html?id=' + id;\n" +
            "    },\n" +
            "\n" +
            "    handleDelete: function(id) {\n" +
            "        if (!confirm('Are you sure you want to delete this product?')) return;\n" +
            "        this.deleteProduct(id);\n" +
            "        sessionStorage.setItem('crud_dirty', '1');\n" +
            "        window.location.href = 'index.html';\n" +
            "    },\n" +
            "\n" +
            "    handleDeleteFromCard: function(el) {\n" +
            "        var card = el.closest('.card') || el.closest('.product-card');\n" +
            "        var id = null;\n" +
            "        if (card) id = card.getAttribute('data-product-id');\n" +
            "        if (!id) { var href = el.getAttribute('href'); if (href) { var m = href.match(/id=([\\w-]+)/); if (m) id = m[1]; } }\n" +
            "        if (!id) return;\n" +
            "        this.handleDelete(id);\n" +
            "    }\n" +
            "};\n";

        try {
            java.nio.file.Path staticDir = outputWriter.getOutputDir().resolve("static");
            java.nio.file.Files.createDirectories(staticDir);
            java.nio.file.Files.write(staticDir.resolve("crud.js"), crudJs.getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("[WARN] Could not write crud.js: " + e.getMessage());
        }
    }

    private String buildJinjaAstJson(Set<String> registeredTemplates) {
        if (registeredTemplates.isEmpty()) return "{\"templates\": []}";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"templates_count\": ").append(registeredTemplates.size()).append(",\n");
        sb.append("  \"templates\": [\n");
        boolean first = true;
        for (String name : registeredTemplates) {
            if (!first) sb.append(",\n");
            first = false;
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(escapeJsonStr(name)).append("\",\n");
            File tf = new File(templateDir, name);
            if (tf.exists()) {
                HtmlContent content = parseHtmlFile(tf.getAbsolutePath());
                if (content != null) {
                    String astJson = astToJson.htmlContentToJson(content);
                    sb.append("      \"ast\": ").append(astJson);
                } else {
                    sb.append("      \"ast\": null");
                }
            } else {
                sb.append("      \"ast\": null");
            }
            sb.append("\n    }");
        }
        sb.append("\n  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    static String buildPythonSymbolTableJson() {
        SymbolTable root = SymbolTableManager.INSTANCE.getRoot();
        if (root == null) return "[]";
        Map<String, Object> map = symbolTableToMap(root);
        return mapToJsonStr(map, 0);
    }

    private static Map<String, Object> symbolTableToMap(SymbolTable table) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("scopeName", table.getScopeName());
        String parentName = table.getParent() != null ? table.getParent().getScopeName() : null;
        if (parentName != null) map.put("parent", parentName);

        List<Map<String, Object>> entries = new ArrayList<>();
        for (Map.Entry<String, SymbolEntry> e : table.getAllEntries()) {
            Map<String, Object> entryMap = new LinkedHashMap<>();
            entryMap.put("name", e.getKey());
            entryMap.put("type", e.getValue().getAttribute("Type"));
            entryMap.put("value", e.getValue().getAttribute("Value"));
            entries.add(entryMap);
        }
        map.put("entries", entries);

        List<Map<String, Object>> childList = new ArrayList<>();
        for (SymbolTable child : table.getChildren()) {
            childList.add(symbolTableToMap(child));
        }
        map.put("children", childList);

        return map;
    }

    @SuppressWarnings("unchecked")
    private static String mapToJsonStr(Object obj, int indent) {
        if (obj == null) return "null";
        if (obj instanceof String s) {
            return "\"" + escapeJsonStr(s) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) map).entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\n").append("  ".repeat(indent + 1));
                sb.append("\"").append(escapeJsonStr(entry.getKey())).append("\": ");
                sb.append(mapToJsonStr(entry.getValue(), indent + 1));
            }
            if (!first) sb.append("\n").append("  ".repeat(indent));
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof List<?> list) {
            if (list.isEmpty()) return "[]";
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\n").append("  ".repeat(indent + 1));
                sb.append(mapToJsonStr(item, indent + 1));
            }
            sb.append("\n").append("  ".repeat(indent));
            sb.append("]");
            return sb.toString();
        }
        return "\"" + escapeJsonStr(obj.toString()) + "\"";
    }

    private void extractRouteTable(Program program) {
        if (program == null || program.statements == null) return;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                if (cs instanceof FunctionDefinition fd) {
                    Decorator dec = fd.decorator;
                    if (dec != null && "app.route".equals(dec.getDecoratorName())) {
                        String endpoint = fd.functionName;
                        String routePath = extractRoutePath(dec);
                        if (routePath != null && endpoint != null && !sharedRouteTable.containsKey(endpoint)) {
                            sharedRouteTable.put(endpoint, routePath);
                        }
                    }
                }
            }
        }
    }

    /**
     * Builds a mapping from endpoint names to HTML file names by analyzing
     * which template each route function renders via render_template().
     */
    private void buildEndpointToFileMap(Program program) {
        if (program == null || program.statements == null) return;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                if (cs instanceof FunctionDefinition fd) {
                    Decorator dec = fd.decorator;
                    if (dec != null && "app.route".equals(dec.getDecoratorName())) {
                        String endpoint = fd.functionName;
                        String templateName = findRenderTemplateInBody(fd);
                        if (endpoint != null && templateName != null) {
                            endpointToFileMap.put(endpoint, templateName);
                        }
                    }
                }
            }
        }
    }

    /**
     * Searches a function body for render_template() calls and returns the first template name found.
     */
    private String findRenderTemplateInBody(FunctionDefinition fd) {
        if (fd.functionBody == null || fd.functionBody.compoundStatements == null) return null;
        for (CompoundStatement bodyStmt : fd.functionBody.compoundStatements) {
            String name = findRenderTemplateInStatement(bodyStmt);
            if (name != null) return name;
        }
        return null;
    }

    private String findRenderTemplateInStatement(CompoundStatement cs) {
        if (cs == null) return null;
        if (cs instanceof ast.returnStmt.ReturnStatement rs) {
            return findRenderTemplateInReturn(rs);
        }
        if (cs instanceof FunctionDefinition fd) {
            return findRenderTemplateInBody(fd);
        }
        if (cs instanceof ast.compundStmt.IfStatement ifStmt) {
            if (ifStmt.statement != null && ifStmt.statement.compoundStatements != null) {
                for (CompoundStatement inner : ifStmt.statement.compoundStatements) {
                    String name = findRenderTemplateInStatement(inner);
                    if (name != null) return name;
                }
            }
            if (ifStmt.elseStatement != null && ifStmt.elseStatement.compoundStatements != null) {
                for (CompoundStatement inner : ifStmt.elseStatement.compoundStatements) {
                    String name = findRenderTemplateInStatement(inner);
                    if (name != null) return name;
                }
            }
        }
        return null;
    }

    private String findRenderTemplateInReturn(ast.returnStmt.ReturnStatement rs) {
        ast.compundStmt.PythonExpression expr = null;
        if (rs instanceof ast.returnStmt.ComplexReturnStatement crs) {
            expr = crs.pythonExpression;
        } else if (rs instanceof ast.returnStmt.ConditionReturnStatement crs) {
            if (crs.condition instanceof ast.condition.ComparisonExpression ce) {
                expr = ce.baseExpr;
            }
        }
        if (expr instanceof ast.atomExpression.FunctionCall fc
                && "render_template".equals(fc.getVarName())) {
            if (fc.argumentsList instanceof ast.argsList.AtomArguments aa
                    && aa.getArgs() != null && !aa.getArgs().isEmpty()) {
                ast.atom.Atom first = aa.getArgs().get(0);
                if (first instanceof ast.atom.Str && first.getValue() instanceof String s) {
                    return s.replace("\"", "").replace("'", "");
                }
            } else if (fc.argumentsList instanceof ast.argsList.ComplexArguments ca
                    && ca.getArguments() != null && !ca.getArguments().isEmpty()) {
                ast.argument.Argument first = ca.getArguments().get(0);
                if (first instanceof ast.argument.PositionalArgument pa
                        && pa.getArg() instanceof ast.atomExpression.SimpleVariable sv) {
                    return sv.getVarName().replace("\"", "").replace("'", "");
                }
            }
        }
        return null;
    }

    private String extractRoutePath(Decorator dec) {
        if (dec.getArguments() == null) return null;
        ArgumentsList argsList = dec.getArguments();
        // Handle ComplexArguments (used when there are multiple args like @app.route('/path', methods=[...]))
        if (argsList instanceof ast.argsList.ComplexArguments ca) {
            var args = ca.getArguments();
            if (args != null && !args.isEmpty()) {
                var first = args.get(0);
                if (first != null && first.getArg() != null) {
                    return extractStringFromPythonExpr(first.getArg());
                }
            }
        }
        // Handle AtomArguments (used for single-arg decorators like @app.route('/'))
        if (argsList instanceof ast.argsList.AtomArguments aa) {
            var atoms = aa.getArgs();
            if (atoms != null && !atoms.isEmpty()) {
                var first = atoms.get(0);
                if (first != null && first.getValue() != null) {
                    String val = first.getValue().toString();
                    if (val != null) {
                        val = val.replaceAll("^[\"']+|[\"']+$", "");
                        if (val.isEmpty()) return "/";
                        return val;
                    }
                }
            }
        }
        return null;
    }

    private String extractStringFromPythonExpr(PythonExpression expr) {
        if (expr instanceof SimpleVariable sv) {
            String val = sv.getVarName();
            if (val != null) {
                val = val.replaceAll("^[\"']+|[\"']+$", "");
                if (val.isEmpty()) return "/";
                return val;
            }
        }
        return null;
    }

    private static String escapeJsonStr(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String injectCrudScripts(String html, String fileName, Map<String, Object> context) {
        if (html == null) return null;

        String crudScript = "<script src=\"static/crud.js\"></script>";

        // Inject IDs into add form elements if missing
        if ("add_product.html".equals(fileName)) {
            // Inject id="add-form" on any <form with method="POST"
            html = html.replaceAll(
                "(<form)(\\s+method=\"POST\")",
                "$1 id=\"add-form\"$2"
            );
            // Inject IDs on inputs by name attribute
            html = html.replaceAll(
                "(<input[^>]*name=\"name\"[^>]*?)(?=\\s*/?>)",
                "$1 id=\"add-name\""
            );
            html = html.replaceAll(
                "(<input[^>]*name=\"price\"[^>]*?)(?=\\s*/?>)",
                "$1 id=\"add-price\""
            );
            // Handle name="image" or name="img" for image field
            html = html.replaceAll(
                "(<input[^>]*name=\"image\"[^>]*?)(?=\\s*/?>)",
                "$1 id=\"add-image\""
            );
            html = html.replaceAll(
                "(<input[^>]*name=\"img\"[^>]*?)(?=\\s*/?>)",
                "$1 id=\"add-image\""
            );
            // Handle name="details" or name="description" for details field
            html = html.replaceAll(
                "(<textarea)(\\s+[^>]*name=\"details\")",
                "$1 id=\"add-details\"$2"
            );
            html = html.replaceAll(
                "(<textarea)(\\s+[^>]*name=\"description\")",
                "$1 id=\"add-details\"$2"
            );
            html = html.replaceAll(
                "(<textarea)(\\s+[^>]*name=\"specification\")",
                "$1 id=\"add-specification\"$2"
            );
        }

        // Inject IDs into edit form elements if missing
        if ("edit_product.html".equals(fileName)) {
            html = html.replaceAll(
                "(<form)([^>]*>)",
                "$1 id=\"edit-form\"$2"
            );
        }

        // Inject product-list ID into products container
        if ("index.html".equals(fileName)) {
            // product_manager style
            html = html.replace(
                "<div class=\"products-container\">",
                "<div class=\"products-container\" id=\"product-list\">"
            );
            html = html.replace(
                "<div class=\"row\"><!-- products loop -->",
                "<div class=\"row\" id=\"product-list\">"
            );
            // flask_project style
            html = html.replace(
                "<div class=\"product-grid\">",
                "<div class=\"product-grid\" id=\"product-list\">"
            );
            // product_management style (Arabic)
            html = html.replace(
                "<div class=\"row justify-content-center\">",
                "<div class=\"row justify-content-center\" id=\"product-list\">"
            );
            // Inject onclick on delete buttons
            html = html.replaceAll(
                "(<a\\s+class=\"delete-btn\"\\s+href=\"delete/)(\\d+)(\">)",
                "$1$2$3 onclick=\"CrudManager.handleDelete('$2'); return false;\""
            );
            html = html.replaceAll(
                "(<a\\s+class=\"btn delete\"\\s+href=\"[^\"]*?/)(\\d+)(\">)",
                "$1$2$3\" onclick=\"CrudManager.handleDelete('$2'); return false;\""
            );
            if (!html.contains("crud.js")) {
                String defaultsScript = buildDefaultsScript(context);
                html = html.replace("</body>", crudScript + defaultsScript +
                    "<script>document.addEventListener('DOMContentLoaded',function(){CrudManager.syncAndRender('product-list');});</script>\n</body>");
            }
        }

        // Inject product-detail-content ID into detail container
        if ("product_detail.html".equals(fileName)) {
            // product_manager style
            html = html.replaceAll(
                "(<div\\s+class=\"details-card\")",
                "$1 id=\"product-detail-content\""
            );
            // flask_project style
            html = html.replace(
                "<div class=\"detail-container\">",
                "<div class=\"detail-container\" id=\"product-detail-content\">"
            );
            // product_management style
            html = html.replace(
                "<div class=\"row\" id=\"product-detail-content\">",
                "<div class=\"row\" id=\"product-detail-content\">"
            );
            if (!html.contains("crud.js")) {
                html = html.replace("</body>", crudScript +
                    "<script>document.addEventListener('DOMContentLoaded',function(){CrudManager.renderProductDetail('product-detail-content');});</script>\n</body>");
            }
        }

        if ("edit_product.html".equals(fileName)) {
            if (!html.contains("crud.js")) {
                html = html.replace("</body>", crudScript +
                    "<script>document.addEventListener('DOMContentLoaded',function(){CrudManager.populateEditForm();var f=document.getElementById('edit-form');if(f)f.addEventListener('submit',function(e){CrudManager.handleEdit(e);});});</script>\n</body>");
            }
        }

        if ("add_product.html".equals(fileName)) {
            if (!html.contains("crud.js")) {
                html = html.replace("</body>", crudScript +
                    "<script>document.addEventListener('DOMContentLoaded',function(){var f=document.getElementById('add-form');if(f)f.addEventListener('submit',function(e){CrudManager.handleAdd(e);});});</script>\n</body>");
            }
        }

        if (!"index.html".equals(fileName) && !"product_detail.html".equals(fileName)
                && !"add_product.html".equals(fileName) && !"edit_product.html".equals(fileName)) {
            if (!html.contains("crud.js")) {
                html = html.replace("</body>", crudScript + "\n</body>");
            }
        }

        return html;
    }

    @SuppressWarnings("unchecked")
    private String buildDefaultsScript(Map<String, Object> context) {
        if (context == null) return "";
        Object productsObj = context.get("products");
        if (!(productsObj instanceof java.util.List<?> productList) || productList.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<script>CrudManager.setDefaults([");
        boolean first = true;
        for (Object item : productList) {
            if (!(item instanceof Map)) continue;
            Map<String, Object> map = (Map<String, Object>) item;
            if (!first) sb.append(",");
            first = false;
            sb.append("{");
            Object id = map.get("id");
            sb.append("\"id\":\"").append(escapeJsonStr(String.valueOf(id != null ? id : ""))).append("\"");
            Object name = map.get("name");
            sb.append(",\"name\":\"").append(escapeJsonStr(String.valueOf(name != null ? name : ""))).append("\"");
            Object price = map.get("price");
            if (price instanceof Number) {
                sb.append(",\"price\":").append(((Number) price).doubleValue());
            } else {
                try { sb.append(",\"price\":").append(Double.parseDouble(String.valueOf(price))); } catch (Exception e) { sb.append(",\"price\":0"); }
            }
            Object details = map.get("details");
            sb.append(",\"details\":\"").append(escapeJsonStr(String.valueOf(details != null ? details : ""))).append("\"");
            Object imgObj = map.get("image_filename");
            if (imgObj == null) imgObj = map.get("image");
            String img = String.valueOf(imgObj != null ? imgObj : "");
            if (img.indexOf('/') != -1) img = img.substring(img.lastIndexOf('/') + 1);
            sb.append(",\"image_filename\":\"").append(escapeJsonStr(img)).append("\"");
            Object desc = map.get("description");
            if (desc != null) sb.append(",\"description\":\"").append(escapeJsonStr(String.valueOf(desc))).append("\"");
            Object spec = map.get("specification");
            if (spec != null) sb.append(",\"specification\":\"").append(escapeJsonStr(String.valueOf(spec))).append("\"");
            sb.append("}");
        }
        sb.append("]);</script>\n");
        return sb.toString();
    }

    private void validateNoJinjaSyntax(Map<String, String> renderedTemplates) {
        logger.validatingNoJinjaSyntax();
        java.util.regex.Pattern jinjaVar = java.util.regex.Pattern.compile("\\{\\{.*?\\}\\}");
        java.util.regex.Pattern jinjaBlock = java.util.regex.Pattern.compile("\\{%.*?%\\}");
        for (var entry : renderedTemplates.entrySet()) {
            String content = entry.getValue();
            if (content == null) continue;
            java.util.regex.Matcher varMatcher = jinjaVar.matcher(content);
            java.util.regex.Matcher blockMatcher = jinjaBlock.matcher(content);
            if (varMatcher.find()) {
                logger.validationFailed(entry.getKey() + " contains remaining {{ }} syntax");
            }
            if (blockMatcher.find()) {
                logger.validationFailed(entry.getKey() + " contains remaining {% %} syntax");
            }
        }
        logger.validatingHtmlPages();
    }
}
