package ast.jinja.jinjaExpression;

import ast.htmlContentItem.HtmlContentItem;

public abstract class JinjaExpression extends HtmlContentItem {
    public JinjaExpression(String node_name, int line_number) {
        super(node_name, line_number);
    }
}
