package asttojson;

import ast.*;
import ast.assignStmt.*;
import ast.atom.*;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.complexExp.*;
import ast.condition.*;
import ast.functionDef.*;
import ast.htmlContentItem.*;
import ast.htmlElement.*;
import ast.jinja.*;
import ast.jinja.jinjaArg.*;
import ast.jinja.jinjaCallExpr.*;
import ast.jinja.jinjaExpression.*;
import ast.jinja.jinjaStatment.*;
import ast.keyValue.*;
import ast.returnStmt.*;
import ast.simpleExpr.*;
import ast.tagContent.TagElementItem;

import java.util.*;

public class AstToJson {

    public String programToJson(Program program) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("type", "Program");
        root.put("line", program.line_number);
        List<Object> stmts = new ArrayList<>();
        if (program.statements != null) {
            for (Statement stmt : program.statements) {
                stmts.add(statementToMap(stmt));
            }
        }
        root.put("statements", stmts);
        return toJsonString(root);
    }

    public String htmlContentToJson(HtmlContent content) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("type", "HtmlContent");
        root.put("line", content.line_number);
        List<Object> items = new ArrayList<>();
        if (content.items != null) {
            for (HtmlContentItem item : content.items) {
                items.add(htmlItemToMap(item));
            }
        }
        root.put("items", items);
        return toJsonString(root);
    }

    private Map<String, Object> statementToMap(Statement stmt) {
        if (stmt == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", stmt.isPass ? "PassStatement" : "Statement");
        map.put("line", stmt.line_number);
        if (stmt.compoundStatements != null) {
            List<Object> cs = new ArrayList<>();
            for (CompoundStatement c : stmt.compoundStatements) {
                cs.add(compoundToMap(c));
            }
            map.put("compoundStatements", cs);
        }
        return map;
    }

    private Map<String, Object> compoundToMap(CompoundStatement cs) {
        if (cs == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", cs.node_name != null ? cs.node_name : cs.getClass().getSimpleName());
        map.put("line", cs.line_number);
        if (cs instanceof FunctionDefinition fd) {
            map.put("name", fd.functionName);
            map.put("body", statementToMap(fd.functionBody));
        } else if (cs instanceof ClassDefinition cd) {
            map.put("name", cd.className);
            map.put("body", statementToMap(cd.classBody));
        } else if (cs instanceof ImportStatement imp) {
            map.put("module", imp.getModule());
        } else if (cs instanceof AssignmentStatement as) {
            map.put("target", pyExprToMap(as.var));
            if (as instanceof PythonExpressionAssignStatement peas) {
                map.put("value", pyExprToMap(peas.value));
            } else if (as instanceof ArithmeticAssignStatement aas) {
                map.put("value", "arithmetic");
            } else if (as instanceof ComparisonAssignmentStmt cas) {
                map.put("value", "comparison");
            }
        } else if (cs instanceof ForLoop fl) {
            map.put("var", fl.var != null ? fl.var.getValue() : "?");
            map.put("iter", fl.iter != null ? pyExprToMap(fl.iter) : "?");
        } else if (cs instanceof IfStatement ifs) {
            map.put("condition", condToMap(ifs.condition));
        } else if (cs instanceof ReturnStatement rs) {
            if (rs instanceof SimpleReturnStatement srs) {
                map.put("value", srs.toString());
            } else if (rs instanceof ComplexReturnStatement crs) {
                map.put("value", pyExprToMap(crs.pythonExpression));
            }
        }
        return map;
    }

    private Map<String, Object> pyExprToMap(PythonExpression expr) {
        if (expr == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        if (expr instanceof SimpleVariable sv) {
            map.put("type", "Variable");
            map.put("name", sv.getVarName());
        } else if (expr instanceof FunctionCall fc) {
            map.put("type", "FunctionCall");
            map.put("name", fc.getVarName());
        } else if (expr instanceof ListLiteral ll) {
            map.put("type", "ListLiteral");
            map.put("items", ll.listItems != null ? ll.listItems.size() : 0);
        } else if (expr instanceof DictionaryLiteral dl) {
            map.put("type", "DictionaryLiteral");
        } else {
            map.put("type", expr.getClass().getSimpleName());
        }
        map.put("line", expr.line_number);
        return map;
    }

    private Map<String, Object> condToMap(Condition cond) {
        if (cond == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", cond.getClass().getSimpleName());
        map.put("line", cond.line_number);
        return map;
    }

    private Map<String, Object> htmlItemToMap(HtmlContentItem item) {
        if (item == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", item.node_name != null ? item.node_name : item.getClass().getSimpleName());
        map.put("line", item.line_number);

        if (item instanceof HtmlTextItem hti) {
            map.put("text", hti.text);
        } else if (item instanceof TagElement tag) {
            map.put("tagName", tag.tagName);
            List<Object> attrs = new ArrayList<>();
            if (tag.tags != null) {
                for (TagElementItem tei : tag.tags) {
                    if (tei != null) {
                        Map<String, Object> attr = new LinkedHashMap<>();
                        attr.put("name", tei.attributeName);
                        attr.put("value", tei.attributeValue);
                        attrs.add(attr);
                    }
                }
            }
            map.put("attributes", attrs);
        } else if (item instanceof JinjaForStatement jfs) {
            map.put("ids", jfs.ids);
            map.put("iterable", jfs.iterable != null ? jfs.iterable.toString() : "?");
        } else if (item instanceof JinjaIfStatement jis) {
            map.put("condition", jis.condition != null ? jis.condition.toString() : "?");
            map.put("has_elif", jis.elifConditions != null && !jis.elifConditions.isEmpty());
            map.put("has_else", jis.elseBody != null);
        } else if (item instanceof JinjaBlockStatement jbs) {
            map.put("blockName", jbs.blockName);
        } else if (item instanceof JinjaExtendStatement jes) {
            map.put("extends", jes.extended);
        } else if (item instanceof JinjaSimpleExpression jse) {
            map.put("expression", jse.expr != null ? jse.expr.toString() : "?");
        } else if (item instanceof JinjaWithStatement jws) {
            map.put("varName", jws.varName);
        }

        return map;
    }

    private String toJsonString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        buildJson(sb, map, 0);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void buildJson(StringBuilder sb, Object obj, int indent) {
        if (obj == null) {
            sb.append("null");
            return;
        }
        if (obj instanceof String s) {
            sb.append("\"").append(escapeJson(s)).append("\"");
            return;
        }
        if (obj instanceof java.lang.Number || obj instanceof Boolean) {
            sb.append(obj);
            return;
        }
        if (obj instanceof Map<?, ?> map) {
            sb.append("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) map).entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\n").append("  ".repeat(indent + 1));
                sb.append("\"").append(escapeJson(entry.getKey())).append("\": ");
                buildJson(sb, entry.getValue(), indent + 1);
            }
            if (!first) sb.append("\n").append("  ".repeat(indent));
            sb.append("}");
            return;
        }
        if (obj instanceof List<?> list) {
            sb.append("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(", ");
                first = false;
                buildJson(sb, item, indent + 1);
            }
            sb.append("]");
            return;
        }
        sb.append("\"").append(escapeJson(obj.toString())).append("\"");
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
