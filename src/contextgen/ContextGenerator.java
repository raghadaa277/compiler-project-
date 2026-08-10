package contextgen;

import ast.Program;
import ast.Statement;
import ast.assignStmt.*;
import ast.atom.*;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.complexExp.*;
import ast.functionDef.FunctionDefinition;
import ast.functionDef.FunctionParameter;
import ast.keyValue.*;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.argument.KeywordArgument;
import ast.argument.PositionalArgument;

import java.util.*;
import java.util.regex.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Extracts runtime template data from a Python AST.
 * <p>
 * This class walks top-level assignments in the Python program and builds
 * an internal context map (variable name &rarr; evaluated value) that is
 * passed to the Jinja AST evaluator.  It deliberately avoids the Symbol
 * Table, which belongs only to semantic analysis.
 * <p>
 * Supported value types:
 * <ul>
 *   <li>Strings, numbers, booleans, None</li>
 *   <li>List literals (including lists of dictionary literals)</li>
 *   <li>Dictionary literals (including nested dicts and dict values)</li>
 *   <li>Set literals</li>
 *   <li>Object creation expressions (mapped to maps)</li>
 *   <li>Function calls (recorded as placeholders)</li>
 *   <li>Arithmetic expressions (evaluated when possible)</li>
 *   <li>Attribute access (resolved when possible)</li>
 * </ul>
 */
public class ContextGenerator {

    private final Map<String, Object> context;

    public ContextGenerator() {
        this.context = new LinkedHashMap<>();
    }

    /**
     * Walks all top-level assignment statements and builds the context.
     * Also extracts sample data from initializer functions.
     */
    public Map<String, Object> generateContext(Program program) {
        return generateContext(program, null);
    }

    /**
     * Walks all top-level assignment statements and builds the context.
     * Also extracts sample data from initializer functions.
     * If filePath is provided, also parses raw Python source for constructor calls.
     */
    public Map<String, Object> generateContext(Program program, String filePath) {
        context.clear();
        if (program == null || program.statements == null) return context;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                if (cs instanceof AssignmentStatement as) {
                    processAssignment(as);
                } else if (cs instanceof FunctionDefinition fd) {
                    processFunctionDefForRoutes(fd);
                    processFunctionDefForSampleData(fd);
                } else if (cs instanceof ClassDefinition cd) {
                    extractInitParamNames(cd);
                }
            }
        }
        processModuleLevelCalls(program);

        if (filePath != null && !context.containsKey("products") && !context.containsKey("items")) {
            extractDataFromRawSource(filePath);
        }

        return context;
    }

    private void processAssignment(AssignmentStatement as) {
        String varName = extractVarName(as.var);
        if (varName == null) return;

        Object val = null;
        if (as instanceof PythonExpressionAssignStatement peas) {
            val = evaluatePythonExpression(peas.value);
        } else if (as instanceof ArithmeticAssignStatement aas) {
            val = evaluateArithmeticString(aas.value != null ? aas.value.toString() : "0");
        } else if (as instanceof ComparisonAssignmentStmt cas) {
            val = true;
        } else if (as instanceof TemplateLiteralAssignmentStatement) {
            val = "";
        }
        if (val != null) {
            context.put(varName, val);
        }
    }

    // ---- Python expression evaluation ----

    private Object evaluatePythonExpression(PythonExpression expr) {
        if (expr == null) return null;
        if (expr instanceof AtomExpression ae) return evaluateAtomExpr(ae);
        if (expr instanceof ListLiteral ll) return evaluateListLiteral(ll);
        if (expr instanceof DictionaryLiteral dl) return evaluateDictionaryLiteral(dl);
        if (expr instanceof SetLiteral sl) return evaluateSetLiteral(sl);
        if (expr instanceof ListComprehension) return new ArrayList<>();
        if (expr instanceof Generator) return new ArrayList<>();
        if (expr instanceof ListItems li) return evaluateListItems(li);
        return expr.toString();
    }

    private Object evaluateListItems(ListItems li) {
        List<Object> list = new ArrayList<>();
        if (li.getItems() != null) {
            for (AtomExpression item : li.getItems()) {
                Object val = evaluateAtomExpr(item);
                if (val != null) list.add(val);
            }
        }
        return list;
    }

    // ---- Atom expression evaluation ----

    private Object evaluateAtomExpr(AtomExpression ae) {
        if (ae == null) return null;
        if (ae instanceof SimpleVariable sv) {
            return resolveSimpleVariable(sv.getVarName());
        }
        if (ae instanceof FunctionCall fc) {
            Object result = evaluateFunctionCall(fc);
            if (result != null) return result;
            if (fc.getVarName() != null && !fc.getVarName().isEmpty()
                    && Character.isUpperCase(fc.getVarName().charAt(0))) {
                return evaluateClassConstructor(fc);
            }
            return null;
        }
        if (ae instanceof ObjectCreation oc) {
            return evaluateObjectCreation(oc);
        }
        if (ae instanceof ListAccess la) {
            return "[list access]";
        }
        if (ae instanceof DictionaryAccess da) {
            return "{dict access}";
        }
        if (ae instanceof AttributeAccess aa) {
            return evaluateAttributeAccess(aa);
        }
        if (ae instanceof Subscript ss) {
            return "[subscript]";
        }
        if (ae instanceof FStringAtomExpression fs) {
            return fs.getRawValue();
        }
        return null;
    }

    /**
     * Resolves a simple variable name to a concrete value.
     * Handles numeric literals, boolean literals, None, and quoted strings.
     */
    private Object resolveSimpleVariable(String name) {
        if (name == null) return null;
        // Numeric
        if (name.matches("-?\\d+(\\.\\d+)?")) {
            if (name.contains(".")) return Double.parseDouble(name);
            return Integer.parseInt(name);
        }
        // Booleans
        if ("True".equals(name)) return true;
        if ("False".equals(name)) return false;
        if ("None".equals(name)) return null;
        // Quoted strings
        if ((name.startsWith("\"") && name.endsWith("\""))
                || (name.startsWith("'") && name.endsWith("'"))) {
            return name.substring(1, name.length() - 1);
        }
        // Reference to another variable in context
        Object fromCtx = context.get(name);
        if (fromCtx != null) return fromCtx;
        return name;
    }

    /**
     * Evaluates an Atom value (not AtomExpression) to extract a concrete value.
     * Handles strings, numbers, booleans, None, and nested structures.
     */
    private Object evaluateAtomValue(Atom atom) {
        if (atom == null || atom.getValue() == null) return null;
        Object val = atom.getValue();
        if (atom instanceof Str) {
            String s = val.toString();
            return s.replaceAll("^['\"]+|['\"]+$", "");
        }
        if (atom instanceof ast.atom.Number) {
            String s = val.toString();
            if (s.contains(".")) {
                try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
            }
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
            return s;
        }
        if (atom instanceof Bool) {
            return "True".equals(val.toString());
        }
        return val;
    }

    // ---- Function call evaluation ----

    private Object evaluateFunctionCall(FunctionCall fc) {
        // int(), float(), str() casts
        if ("int".equals(fc.getVarName()) || "float".equals(fc.getVarName())
                || "str".equals(fc.getVarName())) {
            if (fc.argumentsList instanceof AtomArguments aa && aa.getArgs() != null && !aa.getArgs().isEmpty()) {
                Object inner = evaluateAtomValue(aa.getArgs().get(0));
                if (inner instanceof java.lang.Number n) {
                    if ("int".equals(fc.getVarName())) return n.intValue();
                    if ("float".equals(fc.getVarName())) return n.doubleValue();
                    return String.valueOf(n);
                }
                if (inner instanceof String s) {
                    try {
                        if (s.contains(".")) return Double.parseDouble(s);
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {}
                }
                return inner != null ? inner.toString() : "";
            }
        }
        // len() call
        if ("len".equals(fc.getVarName())) {
            if (fc.argumentsList instanceof AtomArguments aa && aa.getArgs() != null && !aa.getArgs().isEmpty()) {
                Object inner = evaluateAtomValue(aa.getArgs().get(0));
                if (inner instanceof List<?> l) return l.size();
                if (inner instanceof Map<?, ?> m) return m.size();
                if (inner instanceof String s) return s.length();
                if (inner instanceof Set<?> s) return s.size();
            }
            return 0;
        }
        return null;
    }

    // ---- Object creation evaluation ----

    /**
     * Evaluates an object creation expression into a Map.
     * Uses the class name as a prefix and maps constructor arguments
     * to named fields.
     */
    private Object evaluateObjectCreation(ObjectCreation oc) {
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("_class", oc.getVarName());
        if (oc.getArgumentsList() instanceof AtomArguments aa && aa.getArgs() != null) {
            for (int i = 0; i < aa.getArgs().size(); i++) {
                Object val = evaluateAtomValue(aa.getArgs().get(i));
                obj.put("arg" + i, val != null ? val : "");
            }
        } else if (oc.getArgumentsList() instanceof ComplexArguments ca && ca.getArguments() != null) {
            for (int i = 0; i < ca.getArguments().size(); i++) {
                var arg = ca.getArguments().get(i);
                if (arg instanceof ast.argument.KeywordArgument ka) {
                    Object val = ka.getArg() != null ? evaluatePythonExpression(ka.getArg()) : "";
                    obj.put(ka.argName, val != null ? val : "");
                } else {
                    Object val = arg.getArg() != null ? evaluatePythonExpression(arg.getArg()) : "";
                    obj.put("arg" + i, val != null ? val : "");
                }
            }
        }
        return obj;
    }

    // ---- Attribute access evaluation ----

    private Object evaluateAttributeAccess(AttributeAccess aa) {
        String baseName = aa.getVarName();
        Object base = context.get(baseName);
        if (base == null) return baseName + ".attr";
        if (aa.getAttributes() != null) {
            for (Atom attr : aa.getAttributes()) {
                String attrName = attr.getValue() != null ? attr.getValue().toString() : null;
                if (attrName != null && base instanceof Map<?, ?> map) {
                    base = map.get(attrName);
                    if (base == null) return "";
                } else {
                    return baseName + "." + attrName;
                }
            }
        }
        return base;
    }

    // ---- Complex literal evaluation ----

    private List<Object> evaluateListLiteral(ListLiteral ll) {
        List<Object> list = new ArrayList<>();
        if (ll.listItems != null) {
            for (AtomExpression item : ll.listItems) {
                Object val = evaluateAtomExpressionForList(item);
                if (val != null) {
                    list.add(val);
                }
            }
        }
        return list;
    }

    /**
     * Evaluates an AtomExpression for use as a list item.
     * Uses runtime class checks to handle DictionaryLiteral and ListLiteral
     * items that may be present at runtime despite the compile-time type.
     */
    private Object evaluateAtomExpressionForList(AtomExpression item) {
        if (item == null) return null;
        // Use getClass() to handle runtime type mismatches (DictionaryLiteral, ListLiteral)
        // that may be in the list due to ANTLR visitor casting
        Class<?> cls = item.getClass();
        if (cls.getSimpleName().equals("DictionaryLiteral")) {
            return evaluateDictionaryLiteral((DictionaryLiteral) (Object) item);
        }
        if (cls.getSimpleName().equals("ListLiteral")) {
            return evaluateListLiteral((ListLiteral) (Object) item);
        }
        Object val = evaluateAtomExpr(item);
        if (val instanceof String s && (s.startsWith("fn:") || s.startsWith("obj:"))) {
            return null;
        }
        return val;
    }

    /**
     * Evaluates a dictionary literal into a Map.
     * Handles both AtomKeyValue (simple atom values) and SimpleKeyValue
     * (expression values including nested dicts, lists, numbers).
     */
    private Map<String, Object> evaluateDictionaryLiteral(DictionaryLiteral dl) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (dl.getKeyValues() != null) {
            for (KeyValue kv : dl.getKeyValues()) {
                if (kv == null) continue;
                String key = kv.getKey() != null && kv.getKey().getValue() != null
                        ? kv.getKey().getValue().toString() : null;
                if (key == null) continue;

                Object val = null;
                if (kv instanceof AtomKeyValue akv) {
                    val = akv.getAtomValue() != null ? akv.getAtomValue().getValue() : null;
                    // Strip quotes from string values
                    if (val instanceof String s) {
                        val = s.replaceAll("^['\"]+|['\"]+$", "");
                    }
                } else if (kv instanceof SimpleKeyValue skv) {
                    val = evaluateSimpleExpressionForValue(skv.value);
                }
                map.put(key, val);
            }
        }
        return map;
    }

    /**
     * Evaluates a SimpleExpression to extract a value for the context.
     * This bridges SimpleKeyValue values into the context evaluation.
     */
    private Object evaluateSimpleExpressionForValue(ast.simpleExpr.SimpleExpression se) {
        if (se == null) return null;
        if (se.getClass().getSimpleName().equals("ArithmeticExpression")) {
            // ArithmeticExpression extends SimpleExpression
            // Evaluate it by walking left/right using toString as fallback
            return evaluateArithmeticString(se.toString());
        }
        // Try general toString fallback for other SimpleExpression types
        String strVal = se.toString();
        if (strVal != null && !strVal.isEmpty()) {
            return resolveSimpleVariable(strVal);
        }
        return null;
    }

    /**
     * Evaluates an arithmetic expression string like "price + 10" or "3 * 2".
     */
    private Object evaluateArithmeticString(String expr) {
        if (expr == null) return null;
        expr = expr.trim();
        // Try simple addition
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+", 2);
            Object left = resolveSimpleVariable(parts[0].trim());
            Object right = resolveSimpleVariable(parts[1].trim());
            if (left instanceof java.lang.Number && right instanceof java.lang.Number) {
                double l = ((java.lang.Number) left).doubleValue();
                double r = ((java.lang.Number) right).doubleValue();
                double result = l + r;
                if (result == Math.floor(result) && !Double.isInfinite(result)) return (int) result;
                return result;
            }
        }
        return resolveSimpleVariable(expr);
    }

    private double toDouble(Object o) {
        if (o instanceof java.lang.Number n) return n.doubleValue();
        if (o instanceof String s) return Double.parseDouble(s);
        return 0;
    }

    private Set<Object> evaluateSetLiteral(SetLiteral sl) {
        Set<Object> set = new LinkedHashSet<>();
        if (sl.items != null) {
            for (AtomExpression item : sl.items) {
                Object val = evaluateAtomExpr(item);
                if (val instanceof String s && (s.startsWith("fn:") || s.startsWith("obj:"))) continue;
                if (val != null) set.add(val);
            }
        }
        return set;
    }

    // ---- Sample data extraction from function bodies ----

    private Map<String, List<String>> classInitParamNames = new LinkedHashMap<>();

    /**
     * Extracts __init__ parameter names from class definitions.
     */
    private void extractInitParamNames(ClassDefinition cd) {
        if (cd.className == null || cd.classBody == null || cd.classBody.compoundStatements == null) return;
        for (CompoundStatement cs : cd.classBody.compoundStatements) {
            if (cs instanceof FunctionDefinition fd && "__init__".equals(fd.functionName)) {
                List<String> params = new ArrayList<>();
                if (fd.functionParameters != null && fd.functionParameters.parameters != null) {
                    for (FunctionParameter fp : fd.functionParameters.parameters) {
                        if (fp != null && fp.id != null && !"self".equals(fp.id)) {
                            params.add(fp.id);
                        }
                    }
                }
                classInitParamNames.put(cd.className, params);
            }
        }
    }

    /**
     * Processes module-level bare function calls (e.g., init_sample_products()).
     * Walks into the called function's body to extract sample data.
     */
    private void processModuleLevelCalls(Program program) {
        if (program == null || program.statements == null) return;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                if (cs instanceof FunctionCall fc) {
                    String fnName = fc.getVarName();
                    if (fnName != null) {
                        findAndProcessInitFunction(program, fnName);
                    }
                }
            }
        }
    }

    private void findAndProcessInitFunction(Program program, String funcName) {
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                if (cs instanceof FunctionDefinition fd && funcName.equals(fd.functionName)) {
                    processFunctionDefForSampleData(fd);
                }
            }
        }
    }

    private void processFunctionDefForSampleData(FunctionDefinition fd) {
        if (fd.functionBody == null || fd.functionBody.compoundStatements == null) return;
        for (CompoundStatement cs : fd.functionBody.compoundStatements) {
            extractSampleDataFromStatement(cs);
        }
    }

    private void extractSampleDataFromStatement(CompoundStatement cs) {
        if (cs == null) return;
        if (cs instanceof PythonExpressionAssignStatement peas) {
            String varName = extractVarName(peas.var);
            if (varName != null && peas.value instanceof ListLiteral ll) {
                List<Object> items = new ArrayList<>();
                if (ll.listItems != null) {
                    for (AtomExpression item : ll.listItems) {
                        Object val = evaluateAtomExpr(item);
                        if (val != null) items.add(val);
                    }
                }
                if (!items.isEmpty() && !context.containsKey(varName)) {
                    context.put(varName, items);
                }
            }
        }
        if (cs instanceof IfStatement ifStmt) {
            if (ifStmt.statement != null && ifStmt.statement.compoundStatements != null) {
                for (CompoundStatement inner : ifStmt.statement.compoundStatements) {
                    extractSampleDataFromStatement(inner);
                }
            }
            if (ifStmt.elseStatement != null && ifStmt.elseStatement.compoundStatements != null) {
                for (CompoundStatement inner : ifStmt.elseStatement.compoundStatements) {
                    extractSampleDataFromStatement(inner);
                }
            }
        }
    }

    /**
     * Evaluates a class constructor call like Product('name', 2499, 'details', 'img.png').
     * Maps positional arguments to __init__ parameter names extracted from the class definition.
     */
    @SuppressWarnings("unchecked")
    private Object evaluateClassConstructor(FunctionCall fc) {
        String className = fc.getVarName();
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("_class", className);

        List<String> paramNames = classInitParamNames.getOrDefault(className, new ArrayList<>());
        int argIdx = 0;

        if (fc.argumentsList instanceof AtomArguments aa && aa.getArgs() != null) {
            for (int i = 0; i < aa.getArgs().size(); i++) {
                Object val = evaluateAtomValue(aa.getArgs().get(i));
                if (argIdx < paramNames.size()) {
                    obj.put(paramNames.get(argIdx), val != null ? val : "");
                } else {
                    obj.put("arg" + argIdx, val != null ? val : "");
                }
                argIdx++;
            }
        } else if (fc.argumentsList instanceof ComplexArguments ca && ca.getArguments() != null) {
            for (int i = 0; i < ca.getArguments().size(); i++) {
                var arg = ca.getArguments().get(i);
                if (arg instanceof KeywordArgument ka) {
                    Object val = ka.getArg() != null ? evaluatePythonExpression(ka.getArg()) : "";
                    obj.put(ka.argName, val != null ? val : "");
                } else {
                    Object val = arg.getArg() != null ? evaluatePythonExpression(arg.getArg()) : "";
                    if (argIdx < paramNames.size()) {
                        obj.put(paramNames.get(argIdx), val != null ? val : "");
                    } else {
                        obj.put("arg" + argIdx, val != null ? val : "");
                    }
                    argIdx++;
                }
            }
        }

        obj.put("id", String.valueOf(context.size() + 1));
        return obj;
    }

    /**
     * Extracts template variables from route function definitions.
     * When a function has @app.route decorator and calls render_template,
     * this extracts the variable names passed to render_template.
     */
    private void processFunctionDefForRoutes(FunctionDefinition fd) {
        // Record route endpoints for potential template variable association
        if (fd.decorator != null && fd.decorator.getDecoratorName() != null
                && fd.decorator.getDecoratorName().contains("route")) {
            // This function is a route handler
            if (fd.functionBody != null && fd.functionBody.compoundStatements != null) {
                for (CompoundStatement bodyStmt : fd.functionBody.compoundStatements) {
                    if (bodyStmt instanceof AssignmentStatement as) {
                        processAssignment(as);
                    }
                }
            }
        }
    }

    // ---- Raw source fallback parsing ----

    /**
     * Fallback: reads raw Python source and extracts ClassName(...) constructor calls
     * to populate context with sample data when AST doesn't capture it.
     */
    private void extractDataFromRawSource(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            java.io.File dir = file.getParentFile();

            scanSiblingFilesForClassDefs(dir);

            String source = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
            extractConstructorCalls(source);
        } catch (Exception e) {
            // Silently fail
        }
    }

    /**
     * Scans all .py files in the given directory for class definitions
     * and extracts __init__ parameter names.
     */
    private void scanSiblingFilesForClassDefs(java.io.File dir) {
        if (dir == null || !dir.isDirectory()) return;
        java.io.File[] pyFiles = dir.listFiles((d, name) -> name.endsWith(".py"));
        if (pyFiles == null) return;

        for (java.io.File pyFile : pyFiles) {
            try {
                String source = new String(Files.readAllBytes(pyFile.toPath()), "UTF-8");
                extractClassInitParams(source);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Extracts __init__ parameter names from raw Python source using regex.
     * Matches: class ClassName: ... def __init__(self, param1, param2, ...):
     */
    private void extractClassInitParams(String source) {
        Pattern classPattern = Pattern.compile("class\\s+(\\w+)\\s*[:\\(]");
        Pattern initPattern = Pattern.compile("def\\s+__init__\\s*\\(\\s*self\\s*,?([^)]*)\\)");

        Matcher classMatcher = classPattern.matcher(source);
        while (classMatcher.find()) {
            String className = classMatcher.group(1);
            int searchStart = classMatcher.end();

            Matcher initMatcher = initPattern.matcher(source);
            initMatcher.region(searchStart, source.length());
            if (initMatcher.find()) {
                String paramsStr = initMatcher.group(1).trim();
                List<String> paramNames = new ArrayList<>();
                if (!paramsStr.isEmpty()) {
                    for (String param : paramsStr.split(",")) {
                        param = param.trim();
                        if (param.contains("=")) param = param.substring(0, param.indexOf('=')).trim();
                        if (!param.isEmpty()) paramNames.add(param);
                    }
                }
                if (!paramNames.isEmpty()) {
                    classInitParamNames.put(className, paramNames);
                }
            }
        }
    }

    private void extractConstructorCalls(String source) {
        Pattern classPattern = Pattern.compile("(\\w+)\\s*\\(");
        Set<String> classNames = new LinkedHashSet<>();

        Matcher m = classPattern.matcher(source);
        while (m.find()) {
            String name = m.group(1);
            if (name != null && !name.isEmpty() && Character.isUpperCase(name.charAt(0))
                    && !name.equals("Flask") && !name.equals("ProductForm")) {
                classNames.add(name);
            }
        }

        for (String className : classNames) {
            List<Map<String, Object>> instances = extractInstances(source, className);
            if (!instances.isEmpty()) {
                String pluralName = className.toLowerCase() + "s";
                context.put(pluralName, instances);
            }
        }
    }

    private List<Map<String, Object>> extractInstances(String source, String className) {
        List<Map<String, Object>> results = new ArrayList<>();
        String escaped = Pattern.quote(className);
        Pattern callPattern = Pattern.compile(
                escaped + "\\s*\\(([^)]*(?:\\([^)]*\\)[^)]*)*)\\)",
                Pattern.DOTALL
        );
        Matcher m = callPattern.matcher(source);
        int id = 1;
        while (m.find()) {
            String argsStr = m.group(1).trim();
            if (argsStr.isEmpty()) continue;
            if (argsStr.contains("form.") || argsStr.contains("request.") || argsStr.contains("os.")) continue;

            List<String> args = splitTopLevelArgs(argsStr);
            if (args.isEmpty()) continue;

            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("_class", className);
            obj.put("id", String.valueOf(id++));

            List<String> paramNames = classInitParamNames.getOrDefault(className, new ArrayList<>());
            for (int i = 0; i < args.size(); i++) {
                String argVal = cleanPythonString(args.get(i).trim());
                String key = (i < paramNames.size()) ? paramNames.get(i) : "arg" + i;
                obj.put(key, argVal);
            }
            results.add(obj);
        }
        return results;
    }

    private List<String> splitTopLevelArgs(String argsStr) {
        List<String> args = new ArrayList<>();
        int depth = 0;
        boolean inString = false;
        char stringChar = 0;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < argsStr.length(); i++) {
            char c = argsStr.charAt(i);

            if (!inString && (c == '\'' || c == '"' || c == 'f' && i + 1 < argsStr.length()
                    && (argsStr.charAt(i + 1) == '\'' || argsStr.charAt(i + 1) == '"'))) {
                if (c == 'f') {
                    current.append(c);
                    i++;
                    c = argsStr.charAt(i);
                }
                inString = true;
                stringChar = c;
                current.append(c);
            } else if (inString && c == stringChar && (i == 0 || argsStr.charAt(i - 1) != '\\')) {
                inString = false;
                current.append(c);
            } else if (!inString && c == '(') {
                depth++;
                current.append(c);
            } else if (!inString && c == ')') {
                depth--;
                current.append(c);
            } else if (!inString && c == ',' && depth == 0) {
                args.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            args.add(current.toString());
        }
        return args;
    }

    private String cleanPythonString(String s) {
        s = s.trim();
        if (s.startsWith("f'") || s.startsWith("f\"")) {
            s = s.substring(2);
            if (s.endsWith("'") || s.endsWith("\"")) s = s.substring(0, s.length() - 1);
            s = s.replaceAll("\\{[^}]*\\}", "");
            return s;
        }
        if ((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    // ---- Utility ----

    private String extractVarName(PythonExpression expr) {
        if (expr instanceof SimpleVariable sv) return sv.getVarName();
        if (expr instanceof ListAccess la) return la.getVarName();
        if (expr instanceof DictionaryAccess da) return da.getVarName();
        if (expr instanceof AttributeAccess aa) return aa.getVarName();
        return null;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
