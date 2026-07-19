package visitor.html;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import antlr.python.PythonParser;
import ast.HtmlContent;
import ast.htmlContentItem.HtmlContentItem;

import java.util.ArrayList;
import java.util.List;

public class HtmlContentVisitor extends HtmlParserBaseVisitor<HtmlContent> {

    @Override
    public HtmlContent visitHtmlContent(HtmlParser.HtmlContentContext ctx) {
        HtmlContent htmlContent = new HtmlContent(ctx.getStart().getLine());
        List<HtmlContentItem> htmlContentItems = new ArrayList<>();
        HtmlContentItemVisitor htmlContentItemVisitor = new HtmlContentItemVisitor();
        for(int i = 0; i < ctx.html_content_item().size(); i ++){
            HtmlContentItem htmlContentItem = htmlContentItemVisitor
                    .visit(ctx.html_content_item(i));
            htmlContentItems.add(htmlContentItem);
        }
        htmlContent.setItems(htmlContentItems);
        return htmlContent;
    }
}
