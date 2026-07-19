package ast.condition;

import ast.ASTNode;

public abstract class Condition extends ASTNode {
    public Condition(String node_name, int line_number) {
        super(node_name, line_number);
    }
}
