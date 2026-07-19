package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.atom.Atom;
import ast.jinja.JinjaArgumentsList;
import ast.jinja.jinjaCallExpr.JinjaAtom;
import ast.jinja.jinjaCallExpr.JinjaCallExpression;
import ast.jinja.jinjaCallExpr.JinjaFilteredExpression;
import ast.jinja.jinjaCallExpr.JinjaFunctionCall;
import ast.jinja.jinjaCallExpr.JinjaVariableAccess;

public class JinjaCallExpressionVisitor extends HtmlParserBaseVisitor<JinjaCallExpression> {
    JinjaVisitor jinjaVisitor = new JinjaVisitor();

    @Override
    public JinjaCallExpression visitJinjaFilteredExpr(HtmlParser.JinjaFilteredExprContext ctx) {
        JinjaFilteredExpression jinjaFilteredExpression = new JinjaFilteredExpression(ctx.start.getLine());
        JinjaVariableAccess jinjaVariableAccess = (JinjaVariableAccess) jinjaVisitor.visit(ctx.j_var_access());
        jinjaFilteredExpression.setJinjaVariableAccess(jinjaVariableAccess);
        if (ctx.getChild(2) != null) {
            jinjaFilteredExpression.setFilterName(ctx.getChild(2).getText());
        }
        return jinjaFilteredExpression;
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
}
