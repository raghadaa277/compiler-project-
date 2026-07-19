package ast.htmlElement;

import ast.Consts;
import ast.tagContent.TagElementItem;

import java.util.List;

public class TagElement extends HtmlElement {
    private List<TagElementItem> tags;

    public TagElement(int line_number) {
        super("TagElement", line_number);
    }

    public void setTags(List<TagElementItem> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        if (tags != null) {
            for (TagElementItem tagElementItem : tags) {
                if (tagElementItem != null) {
                    stringBuilder.append(Consts.printIndent(3)).append(tagElementItem);
                }
            }
        }
        return stringBuilder.toString();
    }
}
