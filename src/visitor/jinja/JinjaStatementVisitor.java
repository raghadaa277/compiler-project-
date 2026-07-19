package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.HtmlContent;
import ast.jinja.jinjaExpression.JinjaExpression;
import ast.jinja.jinjaStatment.*;
import visitor.UniversalPythonVisitor;
import visitor.html.HtmlContentVisitor;

public class JinjaStatementVisitor extends HtmlParserBaseVisitor<JinjaStatement> {
    private final HtmlContentVisitor universalVisitor = new HtmlContentVisitor();

    @Override
    public JinjaStatement visitJinjaExtendsStmt(HtmlParser.JinjaExtendsStmtContext ctx) {
        return visit(ctx.j_extends_stmt());
    }

    @Override
    public JinjaExtendStatement visitJinjaExtendsStmtDef(HtmlParser.JinjaExtendsStmtDefContext ctx) {
        JinjaExtendStatement jinjaExtendStatement = new JinjaExtendStatement(ctx.start.getLine());
        jinjaExtendStatement.setExtended(ctx.J_STRING().getText());
        return jinjaExtendStatement;
    }

    @Override
    public JinjaStatement visitJinjaBlockStmt(HtmlParser.JinjaBlockStmtContext ctx) {
        return visit(ctx.j_block_stmt());
    }

    @Override
    public JinjaBlockStatement visitJinjaBlockStmtDef(HtmlParser.JinjaBlockStmtDefContext ctx) {
        JinjaBlockStatement jinjaBlockStatement = new JinjaBlockStatement(ctx.start.getLine());
        jinjaBlockStatement.setBlockName(ctx.J_NAME().getFirst().getText());
        HtmlContent htmlContent = (HtmlContent) universalVisitor.visit(ctx.html_content());
        jinjaBlockStatement.setHtmlContent(htmlContent);
        return jinjaBlockStatement;
    }

    @Override
    public JinjaStatement visitJinjaForStmt(HtmlParser.JinjaForStmtContext ctx) {
        return visit(ctx.j_for_stmt());
    }

    @Override
    public JinjaForStatement visitJinjaForStmtDef(HtmlParser.JinjaForStmtDefContext ctx) {
        JinjaForStatement jinjaForStatement = new JinjaForStatement(ctx.start.getLine());
        jinjaForStatement.setId(ctx.J_NAME().getText());
        JinjaExpressionVisitor jinjaExpressionVisitor = new JinjaExpressionVisitor();
        JinjaExpression jinjaExpression = jinjaExpressionVisitor.visit(ctx.j_expression());
        HtmlContent htmlContent = (HtmlContent) universalVisitor.visit(ctx.html_content());
        jinjaForStatement.setIterable(jinjaExpression);
        jinjaForStatement.setHtmlContent(htmlContent);
        return jinjaForStatement;
    }

    @Override
    public JinjaStatement visitJinjaIfStmt(HtmlParser.JinjaIfStmtContext ctx) {
        return visit(ctx.j_if_stmt());
    }

    @Override
    public JinjaStatement visitJinjaIfStmtDef(HtmlParser.JinjaIfStmtDefContext ctx) {
        JinjaIfStatement jinjaIfStatement = new JinjaIfStatement(ctx.start.getLine());
        JinjaExpression jinjaExpression = new JinjaExpressionVisitor().visit(ctx.j_expression());
        HtmlContent htmlContent = (HtmlContent) universalVisitor.visit(ctx.html_content());
        jinjaIfStatement.setHtmlContent(htmlContent);
        jinjaIfStatement.setCondition(jinjaExpression);
        return jinjaIfStatement;
    }
}
