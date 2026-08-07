package app;

import antlr.css.CssLexer;
import antlr.css.CssParser;
import antlr.html.HtmlLexer;
import antlr.html.HtmlParser;
import antlr.python.PythonLexer;
import antlr.python.PythonParser;
import ast.ASTNode;
import ast.HtmlContent;
import ast.Program;
import ast.htmlElement.StyleSheet;
import codegen.AstToTac;
import codegen.PythonCodeGenerator;
import codegen.ir.TacProgram;
import generationlogger.GenerationLogger;
import htmlgen.HtmlGenerator;
import listener.CustomErrorListener;
import semantic.CssSemanticAnalyzer;
import semantic.HtmlSemanticAnalyzer;
import semantic.JinjaSymbolCollector;
import semantic.JinjaTemplateVariableDetector;
import semantic.SemanticError;
import semantic.TemplateVariableChecker;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import symbolTable.Scope;
import symbolTable.Symbol;
import symbolTable.SymbolTableManager;
import visitor.css.StyleSheetVisitor;
import visitor.html.HtmlContentVisitor;
import visitor.python.ProgramVisitor;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//ula
public class App {

    private static final Set<String> SKIP_DIRS = Set.of(
        "venv", "env", ".venv", ".git", "__pycache__",
        "node_modules", ".mypy_cache", ".pytest_cache",
        "egg-info", "dist", "build", ".idea", ".vscode",
        "output", "compiler_output"
    );

    private static Set<String> localModules = new HashSet<>();
    private static final List<Map<String, Object>> jinjaTemplateDataList = new ArrayList<>();

    private static boolean shouldSkip(Path path) {
        for (int i = 0; i < path.getNameCount(); i++) {
            if (SKIP_DIRS.contains(path.getName(i).toString())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        HtmlGenerator.clearSharedErrors();
        HtmlGenerator.resetSharedRouteTable();
        jinjaTemplateDataList.clear();

        Path outputDir = Paths.get("output");
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            System.err.println("Could not create output directory: " + e.getMessage());
        }

        if (args.length != 1) {
            System.err.println("Usage: java app.App <directory_path_or_file>");
            return;
        }

        Path startPath = Paths.get(args[0]);

        System.out.println("PATH = " + startPath.toAbsolutePath());
        System.out.println("EXISTS = " + Files.exists(startPath));

        if (!Files.exists(startPath)) {
            System.err.println("Path does not exist!");
            return;
        }

        try {

            if (Files.isDirectory(startPath)) {

                // Pass 1: Collect render_template info from Python files
                try (Stream<Path> paths = Files.walk(startPath)) {
                    paths.filter(p -> !shouldSkip(p))
                            .filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".py"))
                            .forEach(path -> collectTemplateVars(path.toString()));
                }

                // Collect local Python module names for import resolution
                localModules = new HashSet<>();
                try (Stream<Path> modulePaths = Files.walk(startPath)) {
                    modulePaths.filter(p -> !shouldSkip(p))
                            .filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".py"))
                            .forEach(path -> {
                                String fn = path.getFileName().toString();
                                localModules.add(fn.substring(0, fn.lastIndexOf('.')));
                            });
                }

                // Pass 2: Process all files
                try (Stream<Path> paths = Files.walk(startPath)) {
                    paths.filter(p -> !shouldSkip(p))
                            .filter(Files::isRegularFile)
                            .forEach(path -> {
                                String fileName = path.toString();
                                System.out.println("\n--- Processing: " + fileName + " ---");
                                processFile(fileName);
                            });
                }

            }

            else {

                // Single file mode
                localModules = new HashSet<>();
                if (startPath.toString().endsWith(".py")) {
                    collectTemplateVars(startPath.toString());
                    String fn = startPath.getFileName().toString();
                    localModules.add(fn.substring(0, fn.lastIndexOf('.')));
                }

                System.out.println("\n--- Processing: " + startPath + " ---");
                processFile(startPath.toString());
            }

            String jinjaSymbolTableJson = buildJinjaSymbolTableJson();
            HtmlGenerator.setJinjaSymbolTableJson(jinjaSymbolTableJson);
            HtmlGenerator.writeFinalReport();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String fileName) {
        PrintStream originalOut = System.out;
        String baseName = deriveBaseName(fileName);
        Path sourcePath = Paths.get(baseName);
        String pureFileName = sourcePath.getFileName().toString();
        try {

            // ================= PYTHON =================
            if (fileName.endsWith(".py")) {
                SymbolTableManager.INSTANCE.reset();

                PythonLexer lexer =
                        new PythonLexer(CharStreams.fromFileName(fileName));

                CommonTokenStream tokens =
                        new CommonTokenStream(lexer);

                PythonParser parser =
                        new PythonParser(tokens);

                CustomErrorListener errorListener = new CustomErrorListener();
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);

                ParseTree tree = parser.prog();

                ProgramVisitor visitor = new ProgramVisitor();
                Program program = visitor.visit(tree);

                // Preserve existing TAC and PythonCodeGenerator components (run silently)
                AstToTac astToTac = new AstToTac();
                TacProgram tac = astToTac.translate(program);
                PythonCodeGenerator pyGen = new PythonCodeGenerator(localModules);
                String generatedCode = pyGen.generate(program);

                // ===== Generation pipeline (always writes compiler_output) =====
                System.setOut(originalOut);
                System.out.println("[GEN] Running generation pipeline...");
                try {
                    GenerationLogger genLogger = new GenerationLogger();
                    genLogger.setOutput(System.out);
                    HtmlGenerator htmlGen = new HtmlGenerator(fileName, genLogger);
                    htmlGen.generate(program, fileName);
                    if (htmlGen.hasErrors()) {
                        System.out.println("[GEN] Semantic errors detected - see compiler_output/semantic_report.txt");
                    } else {
                        System.out.println("[GEN] Generation completed successfully.");
                        System.out.println("[GEN] Output: output/ and compiler_output/");
                    }
                } catch (Exception e) {
                    System.out.println("[GEN] Generation pipeline error: " + e.getMessage());
                    e.printStackTrace();
                }
                // ===== END =====

                // end of Python file processing
            }

            // ================= HTML / J2 =================
            else if (fileName.endsWith(".html")
                    || fileName.endsWith(".j2")) {

                HtmlLexer lexer =
                        new HtmlLexer(CharStreams.fromFileName(fileName));

                CommonTokenStream tokens =
                        new CommonTokenStream(lexer);

                HtmlParser parser =
                        new HtmlParser(tokens);

                CustomErrorListener errorListener = new CustomErrorListener();
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);

                ParseTree tree = parser.html_content();

                HtmlContentVisitor visitor =
                        new HtmlContentVisitor();

                HtmlContent htmlContent = visitor.visit(tree);

                String htmlShortName = new java.io.File(fileName).getName();
                JinjaSymbolCollector jinjaCollector = new JinjaSymbolCollector();
                jinjaCollector.analyze(htmlContent);
                JinjaTemplateVariableDetector jinjaVarDetector = new JinjaTemplateVariableDetector();
                jinjaVarDetector.analyze(jinjaCollector, fileName);
                HtmlGenerator.addSharedErrors(jinjaVarDetector.getErrors(), htmlShortName);

                HtmlSemanticAnalyzer htmlAnalyzer = new HtmlSemanticAnalyzer();
                htmlAnalyzer.analyze(htmlContent);
                HtmlGenerator.addSharedErrors(htmlAnalyzer.getErrors(), htmlShortName);

                Map<String, Object> jinjaData = new LinkedHashMap<>();
                jinjaData.put("file", htmlShortName);
                jinjaData.put("extends", jinjaCollector.getExtendsTemplate());
                List<Map<String, Object>> blocksList = new ArrayList<>();
                for (var e : jinjaCollector.getBlocks().entrySet()) {
                    Map<String, Object> bm = new LinkedHashMap<>();
                    bm.put("name", e.getKey());
                    bm.put("line", e.getValue());
                    blocksList.add(bm);
                }
                jinjaData.put("blocks", blocksList);
                List<Map<String, Object>> loopVarsList = new ArrayList<>();
                for (var e : jinjaCollector.getLoopVars().entrySet()) {
                    Map<String, Object> lvm = new LinkedHashMap<>();
                    lvm.put("name", e.getKey());
                    lvm.put("line", e.getValue());
                    loopVarsList.add(lvm);
                }
                jinjaData.put("loop_vars", loopVarsList);
                List<Map<String, Object>> readVarsList = new ArrayList<>();
                for (var e : jinjaCollector.getReadVars().entrySet()) {
                    Map<String, Object> rvm = new LinkedHashMap<>();
                    rvm.put("name", e.getKey());
                    rvm.put("line", e.getValue());
                    readVarsList.add(rvm);
                }
                jinjaData.put("read_vars", readVarsList);
                if (jinjaCollector.getRootScope() != null) {
                    jinjaData.put("scope_tree", scopeToMap(jinjaCollector.getRootScope()));
                }
                jinjaTemplateDataList.add(jinjaData);

                System.setOut(originalOut);
                if (errorListener.hasErrors()) {
                    System.out.println("[SYNTAX ERRORS] " + pureFileName);
                } else {
                    System.out.println("[OK] " + pureFileName);
                }
            }

            // ================= CSS =================
            else if (fileName.endsWith(".css")) {

                CssLexer lexer =
                        new CssLexer(CharStreams.fromFileName(fileName));

                CommonTokenStream tokens =
                        new CommonTokenStream(lexer);

                CssParser parser =
                        new CssParser(tokens);

                CustomErrorListener errorListener = new CustomErrorListener();
                parser.removeErrorListeners();
                parser.addErrorListener(errorListener);

                ParseTree tree = parser.style_sheet();

                StyleSheetVisitor visitor =
                        new StyleSheetVisitor();

                String cssShortName = new java.io.File(fileName).getName();
                ASTNode styleSheet = visitor.visit(tree);
                if (styleSheet instanceof StyleSheet ss) {
                    CssSemanticAnalyzer cssAnalyzer = new CssSemanticAnalyzer();
                    cssAnalyzer.analyze(ss);
                    HtmlGenerator.addSharedErrors(cssAnalyzer.getErrors(), cssShortName);
                }

                System.setOut(originalOut);
                if (errorListener.hasErrors()) {
                    System.out.println("[SYNTAX ERRORS] " + pureFileName);
                } else {
                    System.out.println("[OK] " + pureFileName);
                }
            }

        } catch (Exception e) {

            System.setOut(originalOut);
            System.err.println(
                    "Error parsing " + fileName + ": " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private static void collectTemplateVars(String fileName) {
        try {
            PythonLexer lexer = new PythonLexer(CharStreams.fromFileName(fileName));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PythonParser parser = new PythonParser(tokens);
            parser.removeErrorListeners();
            ParseTree tree = parser.prog();
            ProgramVisitor visitor = new ProgramVisitor();
            Program program = visitor.visit(tree);
            TemplateVariableChecker.collectRenderTemplate(program, fileName);
        } catch (Exception e) {
            // ignore — will be caught again in processFile
        }
    }

    private static void writeSyntaxErrors(CustomErrorListener errorListener) {
        if (errorListener.hasErrors()) {
            System.out.println("========== SYNTAX ERRORS (" + errorListener.getSyntaxErrors().size() + ") ==========");
            int i = 1;
            for (SemanticError e : errorListener.getSyntaxErrors()) {
                System.out.println("  " + i + ". " + e);
                i++;
            }
            System.out.println("===============================================================");
        }
    }

    private static boolean validateGeneratedPython(String filePath) {
        // Try multiple Python commands (python on Windows, python3 on Unix, py on Windows launcher)
        String[][] attempts = {
            {"python", "-m", "py_compile", filePath},
            {"python3", "-m", "py_compile", filePath},
            {"py", "-m", "py_compile", filePath}
        };
        for (String[] cmd : attempts) {
            try {
                ProcessBuilder pb = new ProcessBuilder(cmd);
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) return true;
            } catch (IOException e) {
                // Command not found, try next
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private static String deriveBaseName(String filePath) {
        String name = filePath.replace("\\", "/");
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf('/') + 1);
        }
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        return name;
    }

    private static Map<String, Object> scopeToMap(Scope scope) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("scopeType", scope.scopeType.name());
        map.put("entryLine", scope.entryLine);
        List<Map<String, Object>> syms = new ArrayList<>();
        for (Symbol sym : scope.getSymbolsInScope()) {
            Map<String, Object> sm = new LinkedHashMap<>();
            sm.put("name", sym.name);
            sm.put("kind", sym.kind.name());
            sm.put("type", sym.type.name());
            sm.put("declaredLine", sym.declaredLine);
            sm.put("initialized", sym.initialized);
            sm.put("mutable", sym.mutable);
            syms.add(sm);
        }
        map.put("symbols", syms);
        List<Map<String, Object>> childScopes = new ArrayList<>();
        for (Scope child : scope.children) {
            childScopes.add(scopeToMap(child));
        }
        map.put("children", childScopes);
        return map;
    }

    private static String buildJinjaSymbolTableJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        boolean first = true;
        for (Map<String, Object> data : jinjaTemplateDataList) {
            if (!first) sb.append(",\n");
            first = false;
            sb.append(mapToJson(data, 1));
        }
        sb.append("\n]\n");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String mapToJson(Object obj, int indent) {
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
                sb.append(mapToJson(entry.getValue(), indent + 1));
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
                sb.append(mapToJson(item, indent + 1));
            }
            sb.append("\n").append("  ".repeat(indent));
            sb.append("]");
            return sb.toString();
        }
        return "\"" + escapeJsonStr(obj.toString()) + "\"";
    }

    private static String escapeJsonStr(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void showParseTree(
            String[] ruleNames,
            ParseTree parseTree
    ) {

        TreeViewer viewer = new TreeViewer(
                java.util.Arrays.asList(ruleNames),
                parseTree
        );

        viewer.setScale(1.5);

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(viewer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainPanel);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        JPanel controlPanel = new JPanel();

        JButton zoomInButton = new JButton("Zoom In");

        JButton zoomOutButton = new JButton("Zoom Out");

        JButton resetButton = new JButton("Reset Zoom");

        zoomInButton.addActionListener(e -> {

            viewer.setScale(viewer.getScale() * 1.2);

            viewer.repaint();
        });

        zoomOutButton.addActionListener(e -> {

            viewer.setScale(viewer.getScale() / 1.2);

            viewer.repaint();
        });

        resetButton.addActionListener(e -> {

            viewer.setScale(1.0);

            viewer.repaint();
        });

        controlPanel.add(zoomInButton);

        controlPanel.add(zoomOutButton);

        controlPanel.add(resetButton);

        JFrame frame = new JFrame("Parse Tree Viewer");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(scrollPane, BorderLayout.CENTER);

        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(1000, 640);

        frame.setVisible(true);
    }

    private static void debugTokenStream(
            CommonTokenStream tokens,
            Lexer lexer
    ) {

        tokens.fill();

        List<Token> allTokens = tokens.getTokens();

        System.out.println("\n--- LEXER TOKEN DEBUG OUTPUT ---");

        for (Token t : allTokens) {

            if (t.getChannel() == Token.DEFAULT_CHANNEL) {

                String tokenName =
                        PythonLexer.VOCABULARY.getSymbolicName(t.getType());

                String tokenText =
                        t.getText()
                                .replace("\n", "\\n")
                                .replace("\r", "\\r");

                if (tokenName == null) {
                    tokenName = "VirtualType(" + t.getType() + ")";
                }

                System.out.printf(
                        "Line %d | %-20s | Text: '%s'\n",
                        t.getLine(),
                        tokenName,
                        tokenText
                );
            }
        }

        System.out.println("--------------------------------\n");
    }
}