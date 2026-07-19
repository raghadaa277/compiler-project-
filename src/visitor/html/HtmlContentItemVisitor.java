package visitor.html;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.htmlContentItem.HtmlContentItem;
import ast.htmlContentItem.HtmlTextItem;
import visitor.jinja.JinjaExpressionVisitor;
import visitor.jinja.JinjaStatementVisitor;
import visitor.jinja.JinjaVisitor;

public class HtmlContentItemVisitor extends HtmlParserBaseVisitor<HtmlContentItem> {


    @Override
    public HtmlContentItem visitHtmlElementItem(HtmlParser.HtmlElementItemContext ctx) {
        return new HtmlElementVisitor().visit(ctx.htmlElement());
    }

    @Override
    public HtmlContentItem visitHtmlTextItem(HtmlParser.HtmlTextItemContext ctx) {
        HtmlTextItem htmlTextItem = new HtmlTextItem(ctx.getStart().getLine());
        htmlTextItem.setText(ctx.HTML_TEXT().getText());
        return htmlTextItem;
    }

    @Override
    public HtmlContentItem visitJinjaStmtItem(HtmlParser.JinjaStmtItemContext ctx) {
        return visit(ctx.jinjaStatementBlock());
    }

    @Override
    public HtmlContentItem visitJinjaStmtBlock(HtmlParser.JinjaStmtBlockContext ctx) {
        return new JinjaStatementVisitor().visit(ctx.jStatement());
    }

    @Override
    public HtmlContentItem visitJinjaExprItem(HtmlParser.JinjaExprItemContext ctx) {
        return visit(ctx.jinjaExpressionBlock());
    }

    @Override
    public HtmlContentItem visitJinjaExprBlock(HtmlParser.JinjaExprBlockContext ctx) {
        return new JinjaExpressionVisitor().visit(ctx.j_expression());
    }
}
