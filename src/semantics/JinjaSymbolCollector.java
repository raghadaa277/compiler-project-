package semantics;

import ast.HtmlContent;
import ast.htmlContentItem.HtmlContentItem;
import ast.htmlElement.TagElement;
import ast.htmlElement.StyleSheet;
import ast.jinja.jinjaCallExpr.*;
import ast.jinja.jinjaExpression.*;
import ast.jinja.jinjaStatment.*;
import ast.tagContent.TagElementItem;

import java.util.*;

public class JinjaSymbolCollector {

    private final Map<String, Integer> definedVars = new LinkedHashMap<>();
    private final Map<String, Integer> loopVars = new LinkedHashMap<>();
    private final Map<String, Integer> blocks = new LinkedHashMap<>();
    private final Map<String, Integer> readVars = new LinkedHashMap<>();
    private String extendsTemplate = null;
    private int extendsLine = -1;

    public void analyze(HtmlContent htmlContent) {
        if (htmlContent != null && htmlContent.items != null) {
            for (HtmlContentItem item : htmlContent.items) {
                traverseItem(item);
            }
        }
    }

    private void traverseItem(HtmlContentItem item) {
        if (item == null) return;

        if (item instanceof TagElement tag) {
            if (tag.tags != null) {
                for (TagElementItem attr : tag.tags) {
                    if (attr == null) continue;
                }
            }
        } else if (item instanceof StyleSheet) {
            // no jinja vars in raw CSS
        } else if (item instanceof JinjaBlockStatement jbs) {
            blocks.putIfAbsent(jbs.blockName, jbs.line_number);
            if (jbs.htmlContent != null && jbs.htmlContent.items != null) {
                for (HtmlContentItem child : jbs.htmlContent.items) {
                    traverseItem(child);
                }
            }
        } else if (item instanceof JinjaIfStatement jis) {
            collectVariablesFromExpr(jis.condition);
            if (jis.htmlContent != null && jis.htmlContent.items != null) {
                for (HtmlContentItem child : jis.htmlContent.items) {
                    traverseItem(child);
                }
            }
        } else if (item instanceof JinjaForStatement jfs) {
            loopVars.putIfAbsent(jfs.id, jfs.line_number);
            collectVariablesFromExpr(jfs.iterable);
            if (jfs.htmlContent != null && jfs.htmlContent.items != null) {
                for (HtmlContentItem child : jfs.htmlContent.items) {
                    traverseItem(child);
                }
            }
        } else if (item instanceof JinjaExtendStatement jes) {
            extendsTemplate = jes.extended;
            extendsLine = jes.line_number;
        } else if (item instanceof JinjaSimpleExpression jse) {
            collectVariablesFromCallExpr(jse.expr);
        } else if (item instanceof JinjaBinaryExpression jbe) {
            collectVariablesFromCallExpr(jbe.left);
            collectVariablesFromCallExpr(jbe.right);
        }
    }

    private void collectVariablesFromExpr(JinjaExpression expr) {
        if (expr == null) return;
        if (expr instanceof JinjaSimpleExpression jse) {
            collectVariablesFromCallExpr(jse.expr);
        } else if (expr instanceof JinjaBinaryExpression jbe) {
            collectVariablesFromCallExpr(jbe.left);
            collectVariablesFromCallExpr(jbe.right);
        }
    }

    private void collectVariablesFromCallExpr(JinjaCallExpression expr) {
        if (expr == null) return;
        if (expr instanceof JinjaVariableAccess jva) {
            if (jva.dottedName != null && !jva.dottedName.isEmpty()) {
                readVars.putIfAbsent(jva.dottedName, jva.line_number);
            }
        } else if (expr instanceof JinjaFilteredExpression jfe) {
            if (jfe.jinjaVariableAccess != null && jfe.jinjaVariableAccess.dottedName != null) {
                readVars.putIfAbsent(jfe.jinjaVariableAccess.dottedName, jfe.jinjaVariableAccess.line_number);
            }
        } else if (expr instanceof JinjaFunctionCall jfc) {
            if (jfc.argumentsList != null && jfc.argumentsList.arguments != null) {
                for (var arg : jfc.argumentsList.arguments) {
                    if (arg != null && arg.argument != null) {
                        collectVariablesFromExpr(arg.argument);
                    }
                }
            }
        }
    }

    public Map<String, Integer> getReadVars() {
        return readVars;
    }

    public Map<String, Integer> getLoopVars() {
        return loopVars;
    }

    public String getExtendsTemplate() {
        return extendsTemplate != null ? extendsTemplate.replaceAll("^['\"]+|['\"]+$", "") : null;
    }

    public void printTable() {
        System.out.println("========== JINJA SYMBOL TABLE ==========");

        if (extendsTemplate != null) {
            System.out.println("\n--- Template Extends ---");
            String display = extendsTemplate.replaceAll("^['\"]+|['\"]+$", "");
            System.out.println("  Line " + extendsLine + ": extends \"" + display + "\"");
        }

        if (!blocks.isEmpty()) {
            System.out.println("\n--- Blocks ---");
            for (var e : blocks.entrySet()) {
                System.out.println("  Line " + e.getValue() + ": " + e.getKey());
            }
        }

        if (!loopVars.isEmpty()) {
            System.out.println("\n--- Loop Variables ---");
            for (var e : loopVars.entrySet()) {
                System.out.println("  Line " + e.getValue() + ": " + e.getKey());
            }
        }

        if (!readVars.isEmpty()) {
            System.out.println("\n--- Variables Referenced ---");
            for (var e : readVars.entrySet()) {
                System.out.println("  Line " + e.getValue() + ": " + e.getKey());
            }
        }

        if (extendsTemplate == null && blocks.isEmpty() && loopVars.isEmpty() && readVars.isEmpty()) {
            System.out.println("  (no Jinja symbols found)");
        }

        System.out.println();
    }
}
