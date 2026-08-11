package templaterenderer;

import ast.HtmlContent;
import ast.htmlContentItem.HtmlContentItem;
import ast.htmlContentItem.HtmlTextItem;
import ast.htmlElement.*;
import ast.htmlElement.StyleSheet;
import ast.jinja.*;
import ast.jinja.jinjaArg.*;
import ast.jinja.jinjaCallExpr.*;
import ast.jinja.jinjaExpression.*;
import ast.jinja.jinjaStatment.*;
import ast.tagContent.TagElementItem;
import ast.atom.Atom;
import ast.css.*;
import ast.cssTerm.*;

import ast.atom.Str;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluates a Jinja AST against a context and produces static HTML.
 * <p>
 * This class is the core of the static code generation phase.
 * It walks the parsed template AST, resolves variables from the context,
 * expands for-loops, evaluates if/elif/else conditionals, applies filters,
 * and outputs pure HTML with no remaining Jinja syntax.
 */
public class TemplateRenderer {

    private static final Pattern JINJA_VAR_PATTERN =
            Pattern.compile("\\{\\{(.*?)\\}\\}", Pattern.DOTALL);

    private final String templateDir;
    private final Map<String, HtmlContent> parsedTemplates;
    private final Map<String, Object> globalContext;
    private Map<String, Object> localContext;
    private final Set<String> loading;
    private Map<String, String> routeTable;
    private Map<String, String> endpointToFileMap;

    public TemplateRenderer(String templateDir, Map<String, Object> globalContext) {
        this.templateDir = templateDir;
        this.globalContext = globalContext;
        this.localContext = new HashMap<>();
        this.parsedTemplates = new HashMap<>();
        this.loading = new HashSet<>();
        this.routeTable = new HashMap<>();
        this.endpointToFileMap = new HashMap<>();
    }

    public void setRouteTable(Map<String, String> table) {
        if (table != null) this.routeTable = table;
    }

    public void setEndpointToFileMap(Map<String, String> map) {
        if (map != null) this.endpointToFileMap = map;
    }

    public String render(HtmlContent content, Map<String, Object> templateVars) {
        Map<String, Object> merged = new HashMap<>(globalContext);
        if (templateVars != null) merged.putAll(templateVars);
        this.localContext = new HashMap<>(merged);
        return renderItems(content);
    }

    private String evaluateJinjaExprString(String expr) {
        if (expr == null || expr.isEmpty()) return "";
        // handle filters: expr|filter1|filter2
        if (expr.contains("|")) {
            String[] parts = expr.split("\\|");
            String baseExpr = parts[0].trim();
            String baseVal = evaluateJinjaExprString(baseExpr);
            Object rawObj = evaluateJinjaExprRaw(baseExpr);
            for (int i = 1; i < parts.length; i++) {
                String filterPart = parts[i].trim();
                String filterName = filterPart;
                String filterArg = null;
                int parenIdx = filterPart.indexOf('(');
                if (parenIdx > 0) {
                    filterName = filterPart.substring(0, parenIdx);
                    String inside = filterPart.substring(parenIdx + 1, filterPart.length() - 1);
                    filterArg = inside.trim();
                }
                baseVal = applyFilterSimple(rawObj, baseVal, filterName, filterArg);
                rawObj = baseVal;
            }
            return baseVal;
        }
        // handle url_for(...)
        if (expr.startsWith("url_for(") && expr.endsWith(")")) {
            return resolveUrlFor(expr);
        }
        // handle string literals
        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length() - 1);
        }
        // handle numeric literals
        if (expr.matches("-?\\d+(\\.\\d+)?")) {
            return expr;
        }
        // handle boolean literals
        if ("true".equalsIgnoreCase(expr) || "false".equalsIgnoreCase(expr)) {
            return expr;
        }
        // handle binary expressions with + (concat)
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+", 2);
            String left = evaluateJinjaExprString(parts[0].trim());
            String right = evaluateJinjaExprString(parts[1].trim());
            return left + right;
        }
        // handle method calls like expr.split('x')[-1] or expr.method()
        if (expr.contains("(") && expr.contains(")")) {
            return evaluateMethodCallExpr(expr);
        }
        // handle subscript access like expr[-1] or expr[0]
        if (expr.contains("[") && expr.contains("]")) {
            return evaluateSubscriptExpr(expr);
        }
        // handle variable access (dotted name)
        String val = resolveDottedVariable(expr);
        if (val != null && !val.isEmpty()) return val;
        // try as a direct context lookup
        Object obj = localContext.get(expr);
        if (obj == null) obj = globalContext.get(expr);
        return obj != null ? String.valueOf(obj) : "";
    }

    /**
     * Evaluates expressions containing method calls like product.img.split('static/')[-1].
     * Handles chained method calls and subscripts.
     */
    private String evaluateMethodCallExpr(String expr) {
        // Try to match pattern: base.method('arg')[index] or base.method('arg')
        // Split on the first '(' to get base and method call part
        int parenStart = expr.indexOf('(');
        if (parenStart < 0) return "";
        String baseExpr = expr.substring(0, parenStart).trim();
        // Find matching closing paren
        int depth = 1;
        int parenEnd = -1;
        for (int i = parenStart + 1; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') {
                depth--;
                if (depth == 0) { parenEnd = i; break; }
            }
        }
        if (parenEnd < 0) return "";
        String argsStr = expr.substring(parenStart + 1, parenEnd).trim();
        String remainder = expr.substring(parenEnd + 1).trim();

        // Resolve the base object
        String baseVal = resolveDottedVariable(baseExpr);
        Object baseObj = resolveVariableObject(baseExpr);

        // Handle .split('separator') method
        if (baseExpr.endsWith(".split") || baseExpr.matches(".*\\.split$")) {
            String methodName = "split";
            String[] methodArgs = splitArgs(argsStr);
            if (baseObj instanceof String s && methodArgs.length > 0) {
                String separator = evaluateJinjaExprString(methodArgs[0]);
                String[] parts = s.split(java.util.regex.Pattern.quote(separator), -1);
                Object result = parts;
                // Handle subscript like [-1]
                if (remainder.startsWith("[")) {
                    result = applySubscript(parts, remainder);
                }
                return String.valueOf(result);
            }
        }

        // Handle .replace('old', 'new') method
        if (baseExpr.endsWith(".replace") || baseExpr.matches(".*\\.replace$")) {
            String[] methodArgs = splitArgs(argsStr);
            if (baseObj instanceof String s && methodArgs.length >= 2) {
                String oldStr = evaluateJinjaExprString(methodArgs[0]);
                String newStr = evaluateJinjaExprString(methodArgs[1]);
                return s.replace(oldStr, newStr);
            }
        }

        // Handle .strftime('format') method
        if (baseExpr.endsWith(".strftime") || baseExpr.matches(".*\\.strftime$")) {
            String[] methodArgs = splitArgs(argsStr);
            if (methodArgs.length > 0) {
                String format = evaluateJinjaExprString(methodArgs[0]);
                return baseVal; // Can't format at static time, return raw value
            }
        }

        // Generic: try to resolve the full expression as a variable
        String fullResult = resolveDottedVariable(expr);
        if (fullResult != null && !fullResult.isEmpty()) return fullResult;

        // Apply subscript to base value if present
        if (remainder.startsWith("[")) {
            Object subscriptResult = applySubscriptToObject(baseObj, remainder);
            return String.valueOf(subscriptResult);
        }

        // Fallback: return empty for unresolvable expressions
        return "";
    }

    /**
     * Splits a comma-separated argument string, respecting nested parentheses and quotes.
     */
    private String[] splitArgs(String argsStr) {
        if (argsStr == null || argsStr.isEmpty()) return new String[0];
        List<String> args = new ArrayList<>();
        int depth = 0;
        boolean inQuote = false;
        char quoteChar = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < argsStr.length(); i++) {
            char c = argsStr.charAt(i);
            if (inQuote) {
                current.append(c);
                if (c == quoteChar && (i == 0 || argsStr.charAt(i - 1) != '\\')) {
                    inQuote = false;
                }
            } else if (c == '\'' || c == '"') {
                inQuote = true;
                quoteChar = c;
                current.append(c);
            } else if (c == '(' || c == '[' || c == '{') {
                depth++;
                current.append(c);
            } else if (c == ')' || c == ']' || c == '}') {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0) {
                args.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            args.add(current.toString().trim());
        }
        return args.toArray(new String[0]);
    }

    /**
     * Applies a subscript like [-1] or [0] to an array (String[]).
     */
    private String applySubscript(String[] arr, String subscript) {
        try {
            String inner = subscript.substring(1, subscript.lastIndexOf(']')).trim();
            int idx = Integer.parseInt(inner);
            if (idx < 0) idx = arr.length + idx;
            if (idx >= 0 && idx < arr.length) return arr[idx];
        } catch (Exception ignored) {}
        return "";
    }

    /**
     * Applies a subscript like [-1] or [0] to an Object.
     */
    private Object applySubscriptToObject(Object obj, String subscript) {
        try {
            String inner = subscript.substring(1, subscript.lastIndexOf(']')).trim();
            int idx = Integer.parseInt(inner);
            if (obj instanceof List<?> list) {
                if (idx < 0) idx = list.size() + idx;
                if (idx >= 0 && idx < list.size()) return list.get(idx);
            }
            if (obj instanceof Object[] arr) {
                if (idx < 0) idx = arr.length + idx;
                if (idx >= 0 && idx < arr.length) return arr[idx];
            }
            if (obj instanceof String[] arr) {
                if (idx < 0) idx = arr.length + idx;
                if (idx >= 0 && idx < arr.length) return arr[idx];
            }
        } catch (Exception ignored) {}
        return "";
    }

    /**
     * Evaluates expressions containing subscripts like expr[-1] or expr[0].
     */
    private String evaluateSubscriptExpr(String expr) {
        int bracketStart = expr.indexOf('[');
        if (bracketStart < 0) return "";
        String baseExpr = expr.substring(0, bracketStart).trim();
        String subscript = expr.substring(bracketStart).trim();

        Object baseObj = resolveVariableObject(baseExpr);
        if (baseObj != null) {
            Object result = applySubscriptToObject(baseObj, subscript);
            return String.valueOf(result);
        }

        // Fallback
        return "";
    }

    /**
     * Resolves a dotted variable name to the raw Object value (not String).
     */
    private Object resolveVariableObject(String dottedName) {
        if (dottedName == null || dottedName.isEmpty()) return null;
        String[] parts = dottedName.split("\\.");
        Object current = localContext.get(parts[0]);
        if (current == null) current = globalContext.get(parts[0]);
        if (current == null) return null;

        for (int i = 1; i < parts.length; i++) {
            if (current == null) return null;
            current = accessProperty(current, parts[i]);
        }
        return current;
    }

    private Object evaluateJinjaExprRaw(String expr) {
        if (expr == null || expr.isEmpty()) return null;
        if (expr.startsWith("url_for(") && expr.endsWith(")")) {
            return resolveUrlFor(expr);
        }
        String first = expr.contains(".") ? expr.substring(0, expr.indexOf('.')) : expr;
        Object base = localContext.get(first);
        if (base == null) base = globalContext.get(first);
        if (base == null) return null;
        return resolveVariable(expr);
    }

    private String resolveUrlFor(String expr) {
        // expr = url_for('endpoint', kwarg=val, ...)
        String inner = expr.substring(8, expr.length() - 1).trim();
        // parse positional and keyword arguments
        List<String> positional = new ArrayList<>();
        Map<String, String> kwargs = new LinkedHashMap<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < inner.length(); i++) {
            char c = inner.charAt(i);
            if (c == '(' || c == '[' || c == '{') { depth++; current.append(c); }
            else if (c == ')' || c == ']' || c == '}') { depth--; current.append(c); }
            else if (depth == 0 && c == '=') {
                // keyword arg — flush as key=value
                String kvPair = current.toString().trim();
                if (!kvPair.isEmpty()) {
                    // already contains key= from previous iteration
                }
                current.append(c);
            }
            else if (depth == 0 && c == ',') {
                flushUrlArg(current.toString().trim(), positional, kwargs);
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            flushUrlArg(current.toString().trim(), positional, kwargs);
        }

        if (positional.isEmpty()) return "/";
        String endpoint = positional.get(0);
        endpoint = endpoint.replaceAll("^[\"']+|[\"']+$", "");

        // static file url
        if ("static".equals(endpoint)) {
            String filename = kwargs.get("filename");
            if (filename == null && positional.size() > 1) filename = positional.get(1);
            if (filename != null) {
                String resolved = evaluateJinjaExprString(filename);
                return "static/" + (resolved != null ? resolved.replaceAll("^[\"']+|[\"']+$", "") : filename.replaceAll("^[\"']+|[\"']+$", ""));
            }
            return "static/";
        }

        // For non-static endpoints, map to the corresponding HTML file
        // using the endpoint-to-file mapping built from render_template calls
        String htmlFile = endpointToFileMap.get(endpoint);
        if (htmlFile != null) {
            String url = htmlFile;
            boolean first = true;
            for (Map.Entry<String, String> kwarg : kwargs.entrySet()) {
                String val = evaluateJinjaExprString(kwarg.getValue());
                if (val == null || val.isEmpty()) val = kwarg.getValue();
                val = val.replaceAll("^[\"']+|[\"']+$", "");
                url += (first ? "?" : "&") + kwarg.getKey() + "=" + val;
                first = false;
            }
            return url;
        }

        // For endpoints without templates (e.g. delete, redirect),
        // build URL from route path + resolved kwargs
        String routePath = routeTable.get(endpoint);
        if (routePath != null) {
            String url = routePath;
            for (Map.Entry<String, String> kwarg : kwargs.entrySet()) {
                String val = evaluateJinjaExprString(kwarg.getValue());
                if (val == null || val.isEmpty()) val = kwarg.getValue();
                val = val.replaceAll("^[\"']+|[\"']+$", "");
                url = url.replaceFirst("<[^>]*" + kwarg.getKey() + "\\s*>", val);
            }
            if (url.contains("<")) {
                // couldn't resolve all params, use hash
                return "#";
            }
            return url.startsWith("/") ? url.substring(1) : url;
        }

        // Fallback: return hash for endpoints without templates (e.g. redirects)
        return "#";
    }

    private void flushUrlArg(String arg, List<String> positional, Map<String, String> kwargs) {
        if (arg.isEmpty()) return;
        int eqIdx = arg.indexOf('=');
        // check if this is a keyword argument (key=value, where key is a simple identifier)
        if (eqIdx > 0 && !arg.startsWith("\"") && !arg.startsWith("'")) {
            String key = arg.substring(0, eqIdx).trim();
            String val = arg.substring(eqIdx + 1).trim();
            kwargs.put(key, val);
        } else {
            positional.add(arg);
        }
    }

    public void registerParsedTemplate(String name, HtmlContent content) {
        parsedTemplates.put(name, content);
    }

    private String renderItems(HtmlContent content) {
        if (content == null || content.items == null) return "";
        StringBuilder sb = new StringBuilder();

        List<HtmlContentItem> items = content.items;

        boolean hasExtends = false;
        String extendsTemplate = null;
        Map<String, String> blockContents = new LinkedHashMap<>();

        for (HtmlContentItem item : items) {
            if (item instanceof JinjaExtendStatement jes) {
                hasExtends = true;
                extendsTemplate = jes.extended.replaceAll("^['\"]+|['\"]+$", "");
            }
        }

        if (hasExtends && extendsTemplate != null) {
            for (HtmlContentItem item : items) {
                if (item instanceof JinjaBlockStatement jbs) {
                    String renderedBlock = renderBlockBody(jbs.htmlContent);
                    blockContents.put(jbs.blockName, renderedBlock);
                } else if (item instanceof JinjaExtendStatement) {
                    continue;
                }
            }
            return stripRemainingJinjaSyntax(renderExtendedTemplate(extendsTemplate, blockContents));
        }

        for (HtmlContentItem item : items) {
            sb.append(renderItem(item));
        }
        return stripRemainingJinjaSyntax(sb.toString());
    }

    /**
     * Safety-net: strips any remaining {{ expr }} or {% stmt %} patterns
     * that the evaluator could not resolve to concrete values.
     */
    private String stripRemainingJinjaSyntax(String html) {
        if (html == null || html.isEmpty()) return "";
        // Strip {{ ... }} patterns (variable expressions)
        html = html.replaceAll("\\{\\{.*?\\}\\}", "");
        // Strip {% ... %} patterns (block statements)
        html = html.replaceAll("\\{%.*?%\\}", "");
        return html;
    }

    private String renderExtendedTemplate(String templateName, Map<String, String> blockOverrides) {
        if (loading.contains(templateName)) {
            return "[recursive extends: " + templateName + "]";
        }
        loading.add(templateName);

        String templatePath = templateDir + File.separator + templateName;
        HtmlContent parentContent = parsedTemplates.get(templateName);
        if (parentContent == null) {
            loading.remove(templateName);
            return "[template not found: " + templateName + "]";
        }

        StringBuilder sb = new StringBuilder();
        if (parentContent.items != null) {
            for (HtmlContentItem item : parentContent.items) {
                if (item instanceof JinjaExtendStatement jes) {
                    String parentName = jes.extended.replaceAll("^['\"]+|['\"]+$", "");
                    Map<String, String> mergedBlocks = new LinkedHashMap<>(blockOverrides);
                    for (HtmlContentItem childItem : parentContent.items) {
                        if (childItem instanceof JinjaBlockStatement jbs) {
                            if (!mergedBlocks.containsKey(jbs.blockName)) {
                                mergedBlocks.put(jbs.blockName, renderBlockBody(jbs.htmlContent));
                            }
                        }
                    }
                    String result = renderExtendedTemplate(parentName, mergedBlocks);
                    sb.append(result);
                } else if (item instanceof JinjaBlockStatement jbs) {
                    String override = blockOverrides.get(jbs.blockName);
                    if (override != null) {
                        sb.append(override);
                    } else {
                        sb.append(renderBlockBody(jbs.htmlContent));
                    }
                } else {
                    sb.append(renderItem(item));
                }
            }
        }

        loading.remove(templateName);
        return sb.toString();
    }

    private String renderBlockBody(HtmlContent content) {
        if (content == null || content.items == null) return "";
        StringBuilder sb = new StringBuilder();
        for (HtmlContentItem item : content.items) {
            sb.append(renderItem(item));
        }
        return sb.toString();
    }

    private String renderItem(HtmlContentItem item) {
        if (item == null) return "";

        if (item instanceof HtmlTextItem hti) {
            return hti.text != null ? resolveInlineExpr(hti.text) : "";
        }

        if (item instanceof TagElement tag) {
            return renderTag(tag);
        }

        if (item instanceof ScriptElement se) {
            String openTag = (se.openTag != null) ? se.openTag : "<script>";
            String content = se.content != null ? se.content : "";
            return openTag + content + "</script>\n";
        }

        if (item instanceof StyleSheet ss) {
            return renderStyleSheet(ss);
        }

        if (item instanceof JinjaSimpleExpression jse) {
            return renderJinjaCallExpr(jse.expr);
        }

        if (item instanceof JinjaBinaryExpression jbe) {
            return renderJinjaBinaryExpr(jbe);
        }

        if (item instanceof JinjaForStatement jfs) {
            return renderForLoop(jfs);
        }

        if (item instanceof JinjaIfStatement jis) {
            return renderIfStatement(jis);
        }

        if (item instanceof JinjaBlockStatement jbs) {
            return renderBlockBody(jbs.htmlContent);
        }

        if (item instanceof JinjaExtendStatement) {
            return "";
        }

        if (item instanceof JinjaWithStatement jws) {
            return renderWithStatement(jws);
        }

        return item.toString();
    }

    private String renderTag(TagElement tag) {
        if (tag.tagName == null || tag.tagName.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        if (tag.isClosingTag) {
            sb.append("</").append(tag.tagName).append(">");
            return sb.toString();
        }
        boolean isVoid = isVoidElement(tag.tagName);
        sb.append("<").append(tag.tagName);
        if (tag.tags != null) {
            for (TagElementItem attr : tag.tags) {
                if (attr == null) continue;
                if (attr.attributeName != null && !attr.attributeName.isEmpty()) {
                    if (attr.attributeName.equals(tag.tagName) && attr.attributeValue == null) continue;
                    sb.append(" ").append(attr.attributeName);
                    if (attr.attributeValue != null) {
                        String val = attr.attributeValue;
                        val = val.replaceAll("^\"+", "").replaceAll("\"+$", "");
                        val = resolveInlineExpr(val);
                        sb.append("=\"").append(val).append("\"");
                    }
                }
            }
        }
        if (isVoid) {
            sb.append(" />");
        } else {
            sb.append(">");
        }
        return sb.toString();
    }

    private boolean isVoidElement(String tagName) {
        return "br".equals(tagName) || "hr".equals(tagName) || "img".equals(tagName)
                || "input".equals(tagName) || "meta".equals(tagName) || "link".equals(tagName)
                || "area".equals(tagName) || "base".equals(tagName) || "col".equals(tagName)
                || "embed".equals(tagName) || "source".equals(tagName) || "track".equals(tagName)
                || "wbr".equals(tagName);
    }

    private String renderStyleSheet(StyleSheet ss) {
        if (ss.ruleSets == null || ss.ruleSets.isEmpty()) return "<style>\n</style>\n";
        StringBuilder sb = new StringBuilder();
        sb.append("<style>\n");
        for (RuleSet ruleSet : ss.ruleSets) {
            if (ruleSet.selectorDeclaration != null) {
                sb.append(ruleSet.selectorDeclaration.toSelectorString());
            }
            sb.append(" {\n");
            if (ruleSet.declarationList != null && ruleSet.declarationList.declarations != null) {
                for (CssDeclaration decl : ruleSet.declarationList.declarations) {
                    if (decl.id != null) {
                        sb.append("  ").append(decl.id).append(": ");
                        if (decl.cssTermList != null) {
                            for (int i = 0; i < decl.cssTermList.size(); i++) {
                                if (i > 0) {
                                    if (decl.commaBefore != null && i < decl.commaBefore.size() && Boolean.TRUE.equals(decl.commaBefore.get(i))) {
                                        sb.append(", ");
                                    } else {
                                        sb.append(" ");
                                    }
                                }
                                sb.append(cssTermValue(decl.cssTermList.get(i)));
                            }
                        }
                        sb.append(";\n");
                    }
                }
            }
            sb.append("}\n");
        }
        sb.append("</style>\n");
        return sb.toString();
    }

    private String cssTermValue(CssTerm term) {
        if (term instanceof FunctionTerm ft) {
            StringBuilder sb = new StringBuilder();
            sb.append(term.value != null ? term.value : "");
            sb.append("(");
            if (ft.arguments != null && ft.arguments.cssTerms != null) {
                if (ft.arguments.groupedTerms != null && !ft.arguments.groupedTerms.isEmpty()) {
                    for (int g = 0; g < ft.arguments.groupedTerms.size(); g++) {
                        if (g > 0) sb.append(", ");
                        List<CssTerm> group = ft.arguments.groupedTerms.get(g);
                        for (int i = 0; i < group.size(); i++) {
                            if (i > 0) sb.append(" ");
                            sb.append(cssTermValue(group.get(i)));
                        }
                    }
                } else {
                    for (int i = 0; i < ft.arguments.cssTerms.size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(cssTermValue(ft.arguments.cssTerms.get(i)));
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        }
        if (term instanceof UnitNumberTerm) {
            if (term.value != null) return term.value.replace(" ", "");
            return "";
        }
        if (term instanceof StringTerm) {
            return term.value != null ? term.value : "";
        }
        return term.value != null ? term.value : "";
    }

    /**
     * Expands a for-loop by iterating over the resolved iterable from context,
     * setting loop variables for each item, and rendering the body.
     * Supports nested for-loops by recursively calling renderBlockBody.
     */
    private String renderForLoop(JinjaForStatement jfs) {
        if (jfs.ids == null || jfs.ids.isEmpty()) return "";

        // Resolve the iterable from context
        String iterableName = getIterableName(jfs.iterable);
        Object iterableObj = null;
        if (iterableName != null) {
            iterableObj = localContext.get(iterableName);
            if (iterableObj == null) iterableObj = globalContext.get(iterableName);
        }
        if (iterableObj == null) {
            iterableObj = evaluateExpression(jfs.iterable);
        }

        // Convert to list for iteration
        List<Object> items = toIterable(iterableObj);
        if (items == null || items.isEmpty()) return "";

        // Save and restore local context around the loop
        Map<String, Object> savedContext = new HashMap<>(localContext);
        StringBuilder sb = new StringBuilder();

        String loopVarName = jfs.ids.get(0);

        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            // Set the loop variable
            localContext.put(loopVarName, item);

            // If multiple ids (tuple unpacking), try to unpack dicts
            if (jfs.ids.size() > 1 && item instanceof Map<?, ?> map) {
                // Not common for tuple unpacking in Jinja for-loops with maps
                // Skip for now
            }

            // Render the body with current context
            if (jfs.htmlContent != null) {
                sb.append(renderBlockBody(jfs.htmlContent));
            }
        }

        // Restore context
        localContext = savedContext;
        return sb.toString();
    }

    /**
     * Converts an object to a list of items suitable for iteration.
     */
    private List<Object> toIterable(Object obj) {
        if (obj instanceof List<?> list) {
            return new ArrayList<>(list);
        }
        if (obj instanceof Object[] arr) {
            return Arrays.asList(arr);
        }
        return null;
    }

    /**
     * Evaluates an if/elif/else statement by testing each condition
     * and rendering only the first matching branch.
     */
    private String renderIfStatement(JinjaIfStatement jis) {
        // Evaluate the main condition
        if (jis.condition != null && evaluateAsBoolean(jis.condition)) {
            if (jis.htmlContent != null) {
                return renderBlockBody(jis.htmlContent);
            }
            return "";
        }

        // Evaluate elif conditions
        if (jis.elifConditions != null) {
            for (int i = 0; i < jis.elifConditions.size(); i++) {
                if (evaluateAsBoolean(jis.elifConditions.get(i))) {
                    if (i < jis.elifBodies.size() && jis.elifBodies.get(i) != null) {
                        return renderBlockBody(jis.elifBodies.get(i));
                    }
                    return "";
                }
            }
        }

        // Evaluate else branch
        if (jis.elseBody != null) {
            return renderBlockBody(jis.elseBody);
        }
        return "";
    }

    /**
     * Evaluates a Jinja binary expression (comparisons) and returns the result.
     */
    private String renderJinjaBinaryExpr(JinjaBinaryExpression jbe) {
        if (jbe == null || jbe.left == null) return "";
        String leftVal = renderJinjaCallExpr(jbe.left);
        String rightVal = jbe.right != null ? renderJinjaCallExpr(jbe.right) : null;
        String op = jbe.operator;
        if (op == null || rightVal == null) return leftVal;

        // Handle comparison operators
        if ("==".equals(op)) return String.valueOf(leftVal.equals(rightVal));
        if ("!=".equals(op)) return String.valueOf(!leftVal.equals(rightVal));
        try {
            double l = Double.parseDouble(leftVal);
            double r = Double.parseDouble(rightVal);
            switch (op) {
                case ">": return String.valueOf(l > r);
                case "<": return String.valueOf(l < r);
                case ">=": return String.valueOf(l >= r);
                case "<=": return String.valueOf(l <= r);
                case "+": {
                    if (leftVal.contains(".") || rightVal.contains(".")) return String.valueOf(l + r);
                    return String.valueOf((int)(l + r));
                }
                case "-": return String.valueOf(l - r);
                case "*": return String.valueOf(l * r);
                case "/": return String.valueOf(r != 0 ? l / r : 0);
            }
        } catch (NumberFormatException ignored) {}

        // String comparisons
        if ("==".equals(op)) return String.valueOf(leftVal.equals(rightVal));
        if ("!=".equals(op)) return String.valueOf(!leftVal.equals(rightVal));
        return leftVal;
    }

    /**
     * Evaluates a with-statement: sets a local variable, renders the body,
     * then restores the previous context.
     */
    private String renderWithStatement(JinjaWithStatement jws) {
        if (jws.varName == null) return "";
        String val = jws.valueExpr != null ? evaluateExpressionAsString(jws.valueExpr) : "";
        Object saved = localContext.get(jws.varName);
        localContext.put(jws.varName, val);
        String result = jws.htmlContent != null ? renderBlockBody(jws.htmlContent) : "";
        if (saved != null) {
            localContext.put(jws.varName, saved);
        } else {
            localContext.remove(jws.varName);
        }
        return result;
    }

    private String jinjaCallExprToCleanString(JinjaCallExpression expr) {
        if (expr == null) return "";
        if (expr instanceof JinjaVariableAccess jva) {
            return jva.dottedName;
        }
        if (expr instanceof JinjaFilteredExpression jfe) {
            StringBuilder sb = new StringBuilder();
            if (jfe.jinjaVariableAccess != null) {
                sb.append(jfe.jinjaVariableAccess.dottedName);
            } else if (jfe.rawAtomValue != null) {
                sb.append(jfe.rawAtomValue);
            }
            sb.append("|").append(jfe.filterName);
            if (jfe.filterArgs != null && jfe.filterArgs.arguments != null && !jfe.filterArgs.arguments.isEmpty()) {
                sb.append("(");
                for (int i = 0; i < jfe.filterArgs.arguments.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(jinjaArgToCleanString(jfe.filterArgs.arguments.get(i)));
                }
                sb.append(")");
            }
            return sb.toString();
        }
        if (expr instanceof JinjaFunctionCall jfc) {
            return jinjaFunctionCallToCleanString(jfc);
        }
        if (expr instanceof JinjaAtom ja) {
            Atom atom = ja.getAtom();
            if (atom != null && atom.getValue() != null) {
                String val = atom.getValue().toString();
                if (atom instanceof ast.atom.Str) {
                    val = val.replaceAll("^[\"']+|[\"']+$", "");
                    return "'" + val + "'";
                }
                return val;
            }
            return "";
        }
        if (expr instanceof JinjaSliceAccess jsa) {
            return evaluateSliceAccess(jsa);
        }
        return expr.toString();
    }

    private String jinjaFunctionCallToCleanString(JinjaFunctionCall jfc) {
        StringBuilder sb = new StringBuilder();
        sb.append(jfc.functionName).append("(");
        if (jfc.argumentsList != null && jfc.argumentsList.arguments != null) {
            for (int i = 0; i < jfc.argumentsList.arguments.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(jinjaArgToCleanString(jfc.argumentsList.arguments.get(i)));
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private String jinjaArgToCleanString(JinjaArgument arg) {
        if (arg == null) return "";
        if (arg instanceof JinjaKeywordArgument kwarg) {
            return kwarg.id + "=" + jinjaExprToCleanString(kwarg.argument);
        }
        return jinjaExprToCleanString(arg.argument);
    }

    private String jinjaExprToCleanString(JinjaExpression expr) {
        if (expr == null) return "";
        if (expr instanceof JinjaSimpleExpression jse) {
            return jinjaCallExprToCleanString(jse.expr);
        }
        if (expr instanceof JinjaBinaryExpression jbe) {
            return jinjaCallExprToCleanString(jbe.left) + " " + jbe.operator + " " + jinjaCallExprToCleanString(jbe.right);
        }
        return expr.toString();
    }

    private String renderJinjaCallExpr(JinjaCallExpression expr) {
        if (expr == null) return "";
        if (expr instanceof JinjaVariableAccess jva) {
            String val = resolveDottedVariable(jva.dottedName);
            return val != null ? val : "";
        }
        if (expr instanceof JinjaFilteredExpression jfe) {
            return renderFiltered(jfe);
        }
        if (expr instanceof JinjaFunctionCall jfc) {
            return jinjaFunctionCallToValue(jfc);
        }
        if (expr instanceof JinjaAtom ja) {
            Atom atom = ja.getAtom();
            if (atom != null && atom.getValue() != null) {
                String val = atom.getValue().toString();
                if (atom instanceof Str) {
                    val = val.replaceAll("^[\"']+|[\"']+$", "");
                    return val;
                }
                return val;
            }
            return "";
        }
        if (expr instanceof JinjaSliceAccess jsa) {
            return evaluateSliceAccess(jsa);
        }
        return expr.toString();
    }

    /**
     * Evaluates a JinjaFunctionCall and returns its resolved value.
     * Resolves url_for() to actual paths using the route table.
     */
    private String jinjaFunctionCallToValue(JinjaFunctionCall jfc) {
        if ("url_for".equals(jfc.functionName)) {
            String argsStr = jinjaFunctionCallToCleanString(jfc);
            return resolveUrlFor(argsStr);
        }
        return jinjaFunctionCallToCleanString(jfc);
    }

    private String resolveDottedVariable(String dottedName) {
        if (dottedName == null || dottedName.isEmpty()) return "";
        String[] parts = dottedName.split("\\.");
        Object current = localContext.get(parts[0]);
        if (current == null) {
            current = globalContext.get(parts[0]);
        }
        if (current == null) return "";

        for (int i = 1; i < parts.length; i++) {
            if (current == null) return "";
            current = accessProperty(current, parts[i]);
        }

        return String.valueOf(current);
    }

    private Object accessProperty(Object obj, String property) {
        if (obj instanceof Map<?, ?> map) {
            return map.get(property);
        }
        if (obj instanceof List<?> list) {
            try {
                int idx = Integer.parseInt(property);
                if (idx >= 0 && idx < list.size()) return list.get(idx);
            } catch (NumberFormatException ignored) {}
        }
        String className = obj.getClass().getSimpleName();
        if (obj instanceof String s) {
            if ("length".equals(property)) return s.length();
        }
        return "";
    }

    private String renderFiltered(JinjaFilteredExpression jfe) {
        String varValue = "";
        Object rawObj = null;
        if (jfe.jinjaVariableAccess != null) {
            varValue = resolveDottedVariable(jfe.jinjaVariableAccess.dottedName);
            String dottedName = jfe.jinjaVariableAccess.dottedName;
            String firstPart = dottedName.contains(".") ? dottedName.substring(0, dottedName.indexOf('.')) : dottedName;
            rawObj = localContext.getOrDefault(firstPart, globalContext.get(firstPart));
        } else if (jfe.rawAtomValue != null) {
            varValue = jfe.rawAtomValue;
        }
        String filterName = jfe.filterName != null ? jfe.filterName.toLowerCase() : "";

        if ("format".equals(filterName)) {
            String fmt = varValue;
            if (fmt != null) fmt = fmt.replaceAll("^[\"']+|[\"']+$", "");
            if (fmt == null || fmt.isEmpty()) fmt = "%s";
            Object[] formatArgs = resolveFilterArgs(jfe.filterArgs);
            if (formatArgs.length == 0) formatArgs = new Object[]{""};
            // convert string args to numbers when format specifier requires it
            for (int i = 0; i < formatArgs.length; i++) {
                if (formatArgs[i] instanceof String s) {
                    try {
                        if (s.contains(".")) {
                            formatArgs[i] = Double.parseDouble(s);
                        } else {
                            formatArgs[i] = Long.parseLong(s);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            String result;
            try {
                result = String.format(java.util.Locale.US, fmt, formatArgs);
            } catch (Exception e) {
                result = String.format(java.util.Locale.US, "%s", formatArgs.length > 0 ? formatArgs[0] : "");
            }
            return result;
        }

        return applyFilter(rawObj, varValue, filterName, jfe.filterArgs);
    }

    private Object[] resolveFilterArgs(JinjaArgumentsList argsList) {
        if (argsList == null || argsList.arguments == null || argsList.arguments.isEmpty()) return new Object[0];
        List<Object> result = new ArrayList<>();
        for (JinjaArgument arg : argsList.arguments) {
            if (arg.argument != null) {
                result.add(evaluateExpressionAsString(arg.argument));
            }
        }
        return result.toArray();
    }

    private String applyFilter(Object rawObj, String varValue, String filterName, JinjaArgumentsList filterArgs) {
        switch (filterName) {
            case "length":
                if (rawObj instanceof List) return String.valueOf(((List<?>) rawObj).size());
                if (rawObj instanceof Map) return String.valueOf(((Map<?, ?>) rawObj).size());
                if (rawObj instanceof Set) return String.valueOf(((Set<?>) rawObj).size());
                if (rawObj instanceof String) return String.valueOf(((String) rawObj).length());
                if (rawObj instanceof Object[]) return String.valueOf(((Object[]) rawObj).length);
                return String.valueOf(varValue.length());
            case "lower":
                return varValue.toLowerCase();
            case "upper":
                return varValue.toUpperCase();
            case "trim":
                return varValue.trim();
            case "capitalize":
                if (varValue.isEmpty()) return "";
                return Character.toUpperCase(varValue.charAt(0)) + varValue.substring(1).toLowerCase();
            case "title":
                StringBuilder t = new StringBuilder();
                boolean nextCap = true;
                for (char c : varValue.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        nextCap = true;
                        t.append(c);
                    } else if (nextCap) {
                        t.append(Character.toUpperCase(c));
                        nextCap = false;
                    } else {
                        t.append(Character.toLowerCase(c));
                    }
                }
                return t.toString();
            case "first":
                if (rawObj instanceof List && !((List<?>) rawObj).isEmpty())
                    return String.valueOf(((List<?>) rawObj).get(0));
                return varValue.isEmpty() ? "" : String.valueOf(varValue.charAt(0));
            case "last":
                if (rawObj instanceof List && !((List<?>) rawObj).isEmpty()) {
                    List<?> list = (List<?>) rawObj;
                    return String.valueOf(list.get(list.size() - 1));
                }
                return varValue.isEmpty() ? "" : String.valueOf(varValue.charAt(varValue.length() - 1));
            case "join":
                if (rawObj instanceof List) {
                    List<?> list = (List<?>) rawObj;
                    String sep = ", ";
                    if (filterArgs != null && filterArgs.arguments != null && !filterArgs.arguments.isEmpty()) {
                        String resolved = evaluateExpressionAsString(filterArgs.arguments.get(0).argument);
                        if (resolved != null) sep = resolved;
                    }
                    return list.stream().map(Object::toString).collect(java.util.stream.Collectors.joining(sep));
                }
                return varValue;
            case "default":
            case "d":
                if (varValue == null || varValue.isEmpty() || "null".equals(varValue)) {
                    if (filterArgs != null && filterArgs.arguments != null && !filterArgs.arguments.isEmpty()) {
                        return evaluateExpressionAsString(filterArgs.arguments.get(0).argument);
                    }
                    return "";
                }
                return varValue;
            case "replace":
                if (filterArgs != null && filterArgs.arguments != null && filterArgs.arguments.size() >= 2) {
                    String oldStr = evaluateExpressionAsString(filterArgs.arguments.get(0).argument);
                    String newStr = evaluateExpressionAsString(filterArgs.arguments.get(1).argument);
                    if (oldStr != null && newStr != null) {
                        return varValue.replace(oldStr, newStr);
                    }
                }
                return varValue;
            case "safe":
                return varValue;
            case "escape":
            case "e":
                return varValue
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#39;");
            case "striptags":
                return varValue.replaceAll("<[^>]*>", "");
            case "sort":
                if (rawObj instanceof List) {
                    List<?> list = new ArrayList<>((List<?>) rawObj);
                    Collections.sort(list, (a, b) -> String.valueOf(a).compareTo(String.valueOf(b)));
                    return list.toString();
                }
                return varValue;
            case "reverse":
                if (rawObj instanceof List) {
                    List<?> list = new ArrayList<>((List<?>) rawObj);
                    Collections.reverse(list);
                    return list.toString();
                }
                return new StringBuilder(varValue).reverse().toString();
            case "int":
                try {
                    return String.valueOf((int) Double.parseDouble(varValue));
                } catch (NumberFormatException e) {
                    return "0";
                }
            case "float":
                try {
                    return String.valueOf(Double.parseDouble(varValue));
                } catch (NumberFormatException e) {
                    return "0.0";
                }
            case "abs":
                try {
                    double d = Double.parseDouble(varValue);
                    return String.valueOf(Math.abs(d));
                } catch (NumberFormatException e) {
                    return varValue;
                }
            case "round":
                try {
                    double d = Double.parseDouble(varValue);
                    return String.valueOf(Math.round(d));
                } catch (NumberFormatException e) {
                    return varValue;
                }
            default:
                return varValue;
        }
    }

    private String applyFilterSimple(Object rawObj, String varValue, String filterName, String filterArg) {
        // handle filters that need arguments without constructing Jinja AST nodes
        switch (filterName) {
            case "join":
                String sep = (filterArg != null && !filterArg.isEmpty())
                        ? evaluateJinjaExprString(filterArg) : ", ";
                if (rawObj instanceof List) {
                    List<?> list = (List<?>) rawObj;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        if (i > 0) sb.append(sep);
                        sb.append(list.get(i));
                    }
                    return sb.toString();
                }
                return varValue;
            case "default":
            case "d":
                if (varValue == null || varValue.isEmpty()) {
                    return filterArg != null ? evaluateJinjaExprString(filterArg) : "";
                }
                return varValue;
            case "replace":
                if (filterArg != null) return varValue;
                return varValue;
            case "format":
                String fmtStr = varValue;
                if (fmtStr == null || fmtStr.isEmpty()) fmtStr = "%s";
                String argVal = filterArg != null ? evaluateJinjaExprString(filterArg) : "";
                if (argVal.isEmpty()) return fmtStr;
                try {
                    // try numeric parse for %f and %d format specifiers
                    if (fmtStr.contains("%f") || fmtStr.contains("%.")) {
                        double num = Double.parseDouble(argVal);
                        return String.format(java.util.Locale.US, fmtStr, num);
                    }
                    if (fmtStr.contains("%d")) {
                        long num = Long.parseLong(argVal);
                        return String.format(java.util.Locale.US, fmtStr, num);
                    }
                    return String.format(java.util.Locale.US, fmtStr, argVal);
                } catch (Exception e) {
                    return String.format(java.util.Locale.US, "%s", argVal);
                }
            default:
                // for other filters, call applyFilter without args
                return applyFilter(rawObj, varValue, filterName, null);
        }
    }

    /**
     * Resolves all {{ expr }} patterns embedded in raw HTML text
     * by evaluating them against the current context.
     */
    private String resolveInlineExpr(String text) {
        if (text == null || text.isEmpty()) return "";
        StringBuffer result = new StringBuffer();
        Matcher matcher = JINJA_VAR_PATTERN.matcher(text);
        while (matcher.find()) {
            String expr = matcher.group(1);
            if (expr == null) continue;
            String evaluated = evaluateJinjaExprString(expr.trim());
            matcher.appendReplacement(result, Matcher.quoteReplacement(evaluated));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String renderFunctionCall(JinjaFunctionCall jfc) {
        if (jfc == null) return "";
        if ("url_for".equals(jfc.functionName)) {
            return jinjaFunctionCallToValue(jfc);
        }
        return "";
    }

    private String evaluateExpressionAsString(JinjaExpression expr) {
        if (expr instanceof JinjaSimpleExpression jse && jse.expr != null) {
            if (jse.expr instanceof JinjaVariableAccess jva) {
                return resolveDottedVariable(jva.dottedName);
            }
            if (jse.expr instanceof JinjaSliceAccess jsa) {
                return evaluateSliceAccess(jsa);
            }
            if (jse.expr instanceof JinjaAtom ja) {
                return ja.getAtom() != null && ja.getAtom().getValue() != null
                        ? ja.getAtom().getValue().toString().replaceAll("^[\"']+|[\"']+$", "") : "";
            }
        }
        return expr != null ? expr.toString() : "";
    }

    private String evaluateSliceAccess(JinjaSliceAccess jsa) {
        if (jsa == null || jsa.baseVariable == null) return "";
        String baseName = jsa.baseVariable.dottedName;
        Object baseObj = resolveVariable(baseName);
        if (baseObj == null) return "";
        int start = 0;
        int end = -1;
        if (jsa.sliceStart != null && !jsa.sliceStart.isEmpty()) {
            try { start = Integer.parseInt(jsa.sliceStart); } catch (NumberFormatException ignored) {}
        }
        if (jsa.sliceEnd != null && !jsa.sliceEnd.isEmpty()) {
            try { end = Integer.parseInt(jsa.sliceEnd); } catch (NumberFormatException ignored) {}
        }
        String str = baseObj.toString();
        if (start < 0) start = str.length() + start;
        if (end < 0 && end != -1) end = str.length() + end;
        if (end == -1) return str.substring(Math.max(0, start));
        return str.substring(Math.max(0, start), Math.min(str.length(), end));
    }

    private boolean evaluateAsBoolean(JinjaExpression expr) {
        return isTruthy(evaluateExpression(expr));
    }

    private Object evaluateExpression(JinjaExpression expr) {
        if (expr == null) return null;
        if (expr instanceof JinjaSimpleExpression jse) {
            if (jse.expr instanceof JinjaVariableAccess jva) {
                return resolveVariable(jva.dottedName);
            }
            if (jse.expr instanceof JinjaFilteredExpression jfe) {
                if (jfe.jinjaVariableAccess != null) {
                    String name = jfe.jinjaVariableAccess.dottedName;
                    String first = name.contains(".") ? name.substring(0, name.indexOf('.')) : name;
                    Object raw = localContext.getOrDefault(first, globalContext.get(first));
                    String filterName = jfe.filterName != null ? jfe.filterName.toLowerCase() : "";
                    return applyFilter(raw, resolveDottedVariable(name), filterName, jfe.filterArgs);
                }
                return null;
            }
            if (jse.expr instanceof JinjaFunctionCall jfc) {
                if ("url_for".equals(jfc.functionName)) return "/some-url";
                return null;
            }
            if (jse.expr instanceof JinjaAtom ja) {
                Atom atom = ja.getAtom();
                if (atom != null && atom.getValue() != null) {
                    String val = atom.getValue().toString();
                    if (atom instanceof ast.atom.Str) {
                        return val.replaceAll("^[\"']+|[\"']+$", "");
                    }
                    if (atom instanceof ast.atom.Number) {
                        try { return Integer.parseInt(val); } catch (NumberFormatException e) {}
                        try { return Double.parseDouble(val); } catch (NumberFormatException e) {}
                    }
                    if ("true".equalsIgnoreCase(val)) return true;
                    if ("false".equalsIgnoreCase(val)) return false;
                    return val;
                }
                return null;
            }
            return null;
        }
        if (expr instanceof JinjaBinaryExpression jbe) {
            Object left = evaluateCallExpression(jbe.left);
            Object right = evaluateCallExpression(jbe.right);
            String op = jbe.operator;
            if (op == null) return null;
            // Numeric comparison
            if (left instanceof Number || right instanceof Number) {
                try {
                    double l = toDouble(left);
                    double r = toDouble(right);
                    switch (op) {
                        case "==": return l == r;
                        case "!=": return l != r;
                        case ">":  return l > r;
                        case "<":  return l < r;
                        case ">=": return l >= r;
                        case "<=": return l <= r;
                    }
                } catch (Exception ignored) {}
            }
            // String / object comparison
            String ls = left != null ? String.valueOf(left) : "";
            String rs = right != null ? String.valueOf(right) : "";
            switch (op) {
                case "==": return Objects.equals(ls, rs);
                case "!=": return !Objects.equals(ls, rs);
                case ">":  return ls.compareTo(rs) > 0;
                case "<":  return ls.compareTo(rs) < 0;
                case ">=": return ls.compareTo(rs) >= 0;
                case "<=": return ls.compareTo(rs) <= 0;
            }
            return null;
        }
        return null;
    }

    private Object resolveVariable(String name) {
        if (name == null) return null;
        String first = name.contains(".") ? name.substring(0, name.indexOf('.')) : name;
        Object base = localContext.get(first);
        if (base == null) base = globalContext.get(first);
        if (base == null) return null;
        if (!name.contains(".")) return base;
        String rest = name.substring(name.indexOf('.') + 1);
        String[] parts = rest.split("\\.");
        Object current = base;
        for (String part : parts) {
            if (current == null) return null;
            current = accessProperty(current, part);
        }
        return current;
    }

    private String getIterableName(JinjaExpression expr) {
        if (expr instanceof JinjaSimpleExpression jse && jse.expr instanceof JinjaVariableAccess jva) {
            return jva.dottedName;
        }
        return null;
    }

    private Object evaluateCallExpression(JinjaCallExpression expr) {
        if (expr == null) return null;
        if (expr instanceof JinjaVariableAccess jva) {
            return resolveVariable(jva.dottedName);
        }
        if (expr instanceof JinjaFilteredExpression jfe) {
            return renderFiltered(jfe);
        }
        if (expr instanceof JinjaFunctionCall jfc) {
            return renderFunctionCall(jfc);
        }
        if (expr instanceof JinjaAtom ja) {
            Atom atom = ja.getAtom();
            if (atom != null && atom.getValue() != null) {
                String val = atom.getValue().toString();
                if (atom instanceof ast.atom.Str) {
                    return val.replaceAll("^[\"']+|[\"']+$", "");
                }
                if (atom instanceof ast.atom.Number) {
                    try { return Integer.parseInt(val); } catch (NumberFormatException e) {}
                    try { return Double.parseDouble(val); } catch (NumberFormatException e) {}
                }
                if ("true".equalsIgnoreCase(val)) return true;
                if ("false".equalsIgnoreCase(val)) return false;
                return val;
            }
            return null;
        }
        return null;
    }

    private boolean isTruthy(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean b) return b;
        if (obj instanceof String s) return !s.isEmpty();
        if (obj instanceof Number n) return n.doubleValue() != 0;
        if (obj instanceof List<?> l) return !l.isEmpty();
        if (obj instanceof Map<?, ?> m) return !m.isEmpty();
        if (obj instanceof Set<?> s) return !s.isEmpty();
        if (obj instanceof Collection<?> c) return !c.isEmpty();
        return true;
    }

    private double toDouble(Object o) {
        if (o instanceof Number n) return n.doubleValue();
        if (o instanceof String s) return Double.parseDouble(s);
        return 0;
    }
}
