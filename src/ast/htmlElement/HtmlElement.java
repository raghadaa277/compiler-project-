package ast.htmlElement;

import ast.htmlContentItem.HtmlContentItem;

public abstract class HtmlElement extends HtmlContentItem {
    public HtmlElement(String node_name, int line_number) {
        super(node_name, line_number);
    }
}
