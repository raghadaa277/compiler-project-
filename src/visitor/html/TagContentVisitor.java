package visitor.html;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.tagContent.TagElementItem;

public class TagContentVisitor extends HtmlParserBaseVisitor<TagElementItem> {

    @Override
    public TagElementItem visitHtmlAttribute(HtmlParser.HtmlAttributeContext ctx) {
        TagElementItem tagElementItem = new TagElementItem(ctx.start.getLine());
        tagElementItem.setAttributeName(ctx.TAG_NAME().getText());
        if (ctx.ATTVALUE_VALUE() != null) {
            tagElementItem.setAttributeValue(ctx.ATTVALUE_VALUE().getText());
        }
        return tagElementItem;
    }

//    @Override
//    public TagContent visitClosingMarker(HtmlParser.ClosingMarkerContext ctx) {
//        TagContent tagContent = new TagContent(ctx.start.getLine());
//        tagContent.setAttributeName("");
//        tagContent.setAttributeValue("");
//        return tagContent;
//    }

}
