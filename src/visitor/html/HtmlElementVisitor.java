package visitor.html;

import antlr.html.HtmlLexer;
import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.htmlElement.HtmlElement;
import ast.htmlElement.ScriptElement;
import ast.htmlElement.StyleSheet;
import ast.htmlElement.TagElement;
import ast.tagContent.TagElementItem;
import org.antlr.v4.runtime.Token;
import visitor.html.HtmlStyleSheetVisitor;

import java.util.ArrayList;
import java.util.List;

public class HtmlElementVisitor extends HtmlParserBaseVisitor<HtmlElement> {

    @Override
    public HtmlElement visitTagElement(HtmlParser.TagElementContext ctx) {
        TagElement tagElement = new TagElement(ctx.getStart().getLine());
        TagContentVisitor tagContentVisitor = new TagContentVisitor();
        if (ctx.tag_content() != null) {
            List<TagElementItem> tagElementItemList = new ArrayList<>();
            for (int i = 0; i < ctx.tag_content().size(); i++) {
                TagElementItem tagElementItem = tagContentVisitor.visit(ctx.tag_content(i));
                tagElementItemList.add(tagElementItem);
                if (ctx.tag_content(i) instanceof HtmlParser.ClosingMarkerContext) {
                    tagElement.isClosingTag = true;
                }
            }
            tagElement.setTags(tagElementItemList);
            for (TagElementItem item : tagElementItemList) {
                if (item != null && item.attributeName != null && !item.attributeName.isEmpty()) {
                    tagElement.tagName = item.attributeName;
                    break;
                }
            }
        }
        return tagElement;
    }

    @Override
    public HtmlElement visitStyleElement(HtmlParser.StyleElementContext ctx) {
        return (StyleSheet) new HtmlStyleSheetVisitor().visit(ctx.style_sheet());
    }

    @Override
    public HtmlElement visitScriptElement(HtmlParser.ScriptElementContext ctx) {
        ScriptElement scriptElement = new ScriptElement(ctx.start.getLine());
        if (ctx.SCRIPT_OPEN() != null) {
            scriptElement.setOpenTag(ctx.SCRIPT_OPEN().getText());
        }
        if (ctx.SCRIPT_CONTENT() != null) {
            scriptElement.setContent(ctx.SCRIPT_CONTENT().getText());
        }
        return scriptElement;
    }
}
