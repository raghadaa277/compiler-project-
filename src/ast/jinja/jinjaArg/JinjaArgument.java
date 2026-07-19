package ast.jinja.jinjaArg;

import ast.ASTNode;
import ast.jinja.jinjaExpression.JinjaExpression;

public abstract class JinjaArgument extends ASTNode {

    private JinjaExpression argument;

    public JinjaArgument(String node_name, int line_number) {
        super(node_name, line_number);
    }

    public void setArgument(JinjaExpression argument) {
        this.argument = argument;
    }

    public JinjaExpression getArgument() {
        return argument;
    }
}
