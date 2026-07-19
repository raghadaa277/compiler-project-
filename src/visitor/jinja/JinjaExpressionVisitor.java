package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.jinja.jinjaCallExpr.JinjaCallExpression;
import ast.jinja.jinjaExpression.JinjaBinaryExpression;
import ast.jinja.jinjaExpression.JinjaExpression;
import ast.jinja.jinjaExpression.JinjaSimpleExpression;

public class JinjaExpressionVisitor extends HtmlParserBaseVisitor<JinjaExpression> {
    private final JinjaCallExpressionVisitor jinjaCallExpressionVisitor
            = new JinjaCallExpressionVisitor();

    @Override
    public JinjaExpression visitJinjaBinaryExpr(HtmlParser.JinjaBinaryExprContext ctx) {
        JinjaBinaryExpression jinjaBinaryExpression = new JinjaBinaryExpression(ctx.start.getLine());
        JinjaCallExpression left = jinjaCallExpressionVisitor.visit(ctx.j_call_expr(0));
        JinjaCallExpression right = jinjaCallExpressionVisitor.visit(ctx.j_call_expr(1));
        jinjaBinaryExpression.setOperator(ctx.getChild(1).getText());
        jinjaBinaryExpression.setLeft(left);
        jinjaBinaryExpression.setRight(right);
        return jinjaBinaryExpression;
    }

    @Override
    public JinjaExpression visitJinjaSimpleExpr(HtmlParser.JinjaSimpleExprContext ctx) {
        JinjaSimpleExpression jinjaSimpleExpression = new JinjaSimpleExpression(ctx.start.getLine());
        JinjaCallExpression jinjaCallExpression = jinjaCallExpressionVisitor.visit(ctx.j_call_expr());
        jinjaSimpleExpression.setExpr(jinjaCallExpression);
        return jinjaSimpleExpression;
    }
}
