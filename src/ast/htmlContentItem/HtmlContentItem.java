package ast.htmlContentItem;

import ast.ASTNode;

public abstract class HtmlContentItem extends ASTNode {
    public HtmlContentItem(String node_name, int line_number) {
        super(node_name, line_number);
    }
}
