package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.atom.Atom;
import ast.jinja.JinjaArgumentsList;
import ast.jinja.jinjaCallExpr.JinjaAtom;
import ast.jinja.jinjaCallExpr.JinjaCallExpression;
import ast.jinja.jinjaCallExpr.JinjaFilteredExpression;
import ast.jinja.jinjaCallExpr.JinjaFunctionCall;
import ast.jinja.jinjaCallExpr.JinjaSliceAccess;
import ast.jinja.jinjaCallExpr.JinjaVariableAccess;

public class JinjaCallExpressionVisitor extends HtmlParserBaseVisitor<JinjaCallExpression> {
    JinjaVisitor jinjaVisitor = new JinjaVisitor();

    @Override
    public JinjaCallExpression visitJinjaFilteredExpr(HtmlParser.JinjaFilteredExprContext ctx) {
        JinjaFilteredExpression jinjaFilteredExpression = new JinjaFilteredExpression(ctx.start.getLine());
        JinjaCallExpression left = visit(ctx.j_call_expr(0));
        JinjaCallExpression right = visit(ctx.j_call_expr(1));
        if (left instanceof JinjaVariableAccess jva) {
            jinjaFilteredExpression.setJinjaVariableAccess(jva);
        } else if (left instanceof JinjaAtom ja) {
            Atom atom = ja.getAtom();
            if (atom != null && atom.getValue() != null) {
                jinjaFilteredExpression.setRawAtomValue(atom.getValue().toString());
            }
        }
        if (right instanceof JinjaVariableAccess) {
            jinjaFilteredExpression.setFilterName(((JinjaVariableAccess) right).dottedName);
        } else if (right instanceof JinjaFunctionCall jfc) {
            jinjaFilteredExpression.setFilterName(jfc.functionName);
            jinjaFilteredExpression.setFilterArgs(jfc.argumentsList);
        }
        return jinjaFilteredExpression;
    }

    @Override
    public JinjaCallExpression visitJinjaMethodCall(HtmlParser.JinjaMethodCallContext ctx) {
        JinjaVariableAccess varAccess = (JinjaVariableAccess) jinjaVisitor.visit(ctx.j_var_access());
        JinjaFunctionCall jinjaFunctionCall = new JinjaFunctionCall(ctx.start.getLine());
        jinjaFunctionCall.setFunctionName(varAccess.dottedName);
        if (ctx.j_argument_list() != null) {
            JinjaArgumentsList jinjaArgumentsList = (JinjaArgumentsList) jinjaVisitor.visit(ctx.j_argument_list());
            jinjaFunctionCall.setArgumentsList(jinjaArgumentsList);
        }
        return jinjaFunctionCall;
    }

    @Override
    public JinjaCallExpression visitJinjaFunctionCall(HtmlParser.JinjaFunctionCallContext ctx) {
        JinjaFunctionCall jinjaFunctionCall = new JinjaFunctionCall(ctx.start.getLine());
        jinjaFunctionCall.setFunctionName(ctx.J_NAME().getText());
        if (ctx.j_argument_list() != null) {
            JinjaArgumentsList jinjaArgumentsList = (JinjaArgumentsList) jinjaVisitor.visit(ctx.j_argument_list());
            jinjaFunctionCall.setArgumentsList(jinjaArgumentsList);
        }
        return jinjaFunctionCall;
    }

    @Override
    public JinjaCallExpression visitJinjaVarAccessOnly(HtmlParser.JinjaVarAccessOnlyContext ctx) {
        return (JinjaCallExpression) jinjaVisitor.visit(ctx.j_var_access());
    }



    @Override
    public JinjaCallExpression visitJinjaAtomOnly(HtmlParser.JinjaAtomOnlyContext ctx) {
        JinjaAtomVisitor jinjaAtomVisitor = new JinjaAtomVisitor();
        Atom atom = jinjaAtomVisitor.visit(ctx.j_atom());
        return new JinjaAtom(ctx.start.getLine(), atom);
    }

    @Override
    public JinjaCallExpression visitJinjaSliceAccess(HtmlParser.JinjaSliceAccessContext ctx) {
        JinjaCallExpression base = visit(ctx.j_call_expr());
        if (!(base instanceof JinjaVariableAccess jva)) {
            return base;
        }
        JinjaSliceAccess sliceAccess = new JinjaSliceAccess(ctx.start.getLine());
        sliceAccess.setBaseVariable(jva);
        if (ctx.j_slice() != null) {
            String sliceText = ctx.j_slice().getText();
            // parse slice like ":100" or "0:100" or "0:100:2"
            String[] parts = sliceText.split(":");
            if (parts.length == 1 && sliceText.startsWith(":")) {
                sliceAccess.setSliceEnd(parts[0]);
            } else if (parts.length == 1) {
                sliceAccess.setSliceStart(parts[0]);
            } else if (parts.length == 2) {
                sliceAccess.setSliceStart(parts[0].isEmpty() ? null : parts[0]);
                sliceAccess.setSliceEnd(parts[1].isEmpty() ? null : parts[1]);
            } else if (parts.length >= 3) {
                sliceAccess.setSliceStart(parts[0].isEmpty() ? null : parts[0]);
                sliceAccess.setSliceEnd(parts[1].isEmpty() ? null : parts[1]);
                sliceAccess.setSliceStep(parts[2].isEmpty() ? null : parts[2]);
            }
        }
        return sliceAccess;
    }
}
