package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.HtmlContent;
import ast.jinja.jinjaExpression.JinjaExpression;
import ast.jinja.jinjaStatment.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import visitor.UniversalPythonVisitor;
import visitor.html.HtmlContentVisitor;

import java.util.List;

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
        List<TerminalNode> names = ctx.J_NAME();
        if (names != null && !names.isEmpty()) {
            jinjaBlockStatement.setBlockName(names.getFirst().getText());
        }
        HtmlParser.Html_contentContext htmlCtx = ctx.html_content();
        if (htmlCtx == null) {
            System.err.println("WARNING: html_content is null for block at line " + ctx.start.getLine());
        } else {
            HtmlContent htmlContent = (HtmlContent) universalVisitor.visit(htmlCtx);
            jinjaBlockStatement.setHtmlContent(htmlContent);
        }
        return jinjaBlockStatement;
    }

    @Override
    public JinjaStatement visitJinjaForStmt(HtmlParser.JinjaForStmtContext ctx) {
        return visit(ctx.j_for_stmt());
    }

    @Override
    public JinjaForStatement visitJinjaForStmtDef(HtmlParser.JinjaForStmtDefContext ctx) {
        JinjaForStatement jinjaForStatement = new JinjaForStatement(ctx.start.getLine());
        List<String> ids = new java.util.ArrayList<>();
        for (int i = 0; i < ctx.J_NAME().size(); i++) {
            ids.add(ctx.J_NAME(i).getText());
        }
        jinjaForStatement.setIds(ids);
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
        if (ctx.j_expression(0) != null) {
            JinjaExpression jinjaExpression = new JinjaExpressionVisitor().visit(ctx.j_expression(0));
            jinjaIfStatement.setCondition(jinjaExpression);
        }
        if (ctx.html_content(0) != null) {
            HtmlContent htmlContent = (HtmlContent) universalVisitor.visit(ctx.html_content(0));
            jinjaIfStatement.setHtmlContent(htmlContent);
        }
        int elifCount = ctx.J_ELIF().size();
        int exprIdx = 1;
        int htmlIdx = 1;
        for (int i = 0; i < elifCount; i++) {
            if (ctx.j_expression(exprIdx) != null) {
                JinjaExpression elifCond = new JinjaExpressionVisitor().visit(ctx.j_expression(exprIdx));
                exprIdx++;
                HtmlContent elifBody = ctx.html_content(htmlIdx) != null
                        ? (HtmlContent) universalVisitor.visit(ctx.html_content(htmlIdx)) : null;
                htmlIdx++;
                jinjaIfStatement.addElif(elifCond, elifBody);
            }
        }
        if (ctx.J_ELSE() != null) {
            if (ctx.html_content(htmlIdx) != null) {
                HtmlContent elseBody = (HtmlContent) universalVisitor.visit(ctx.html_content(htmlIdx));
                jinjaIfStatement.setElseBody(elseBody);
            }
        }
        return jinjaIfStatement;
    }

    @Override
    public JinjaStatement visitJinjaWithStmt(HtmlParser.JinjaWithStmtContext ctx) {
        return visit(ctx.j_with_stmt());
    }

    @Override
    public JinjaWithStatement visitJinjaWithStmtDef(HtmlParser.JinjaWithStmtDefContext ctx) {
        JinjaWithStatement jinjaWithStatement = new JinjaWithStatement(ctx.start.getLine());
        jinjaWithStatement.setVarName(ctx.J_NAME().getText());
        JinjaExpression valueExpr = new JinjaExpressionVisitor().visit(ctx.j_expression());
        jinjaWithStatement.setValueExpr(valueExpr);
        HtmlContent htmlContent = (HtmlContent) universalVisitor.visit(ctx.html_content());
        jinjaWithStatement.setHtmlContent(htmlContent);
        return jinjaWithStatement;
    }
}
