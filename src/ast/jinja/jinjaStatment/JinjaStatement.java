package ast.jinja.jinjaStatment;

import ast.htmlContentItem.HtmlContentItem;

public abstract class JinjaStatement extends HtmlContentItem {
    public JinjaStatement(String node_name, int line_number) {
        super(node_name, line_number);
    }

}
