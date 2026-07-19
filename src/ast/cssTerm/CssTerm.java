package ast.cssTerm;

import ast.ASTNode;

public abstract class CssTerm extends ASTNode {

    private String value;

    public CssTerm(String node_name, int line_number) {
        super(node_name, line_number);
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + value + " ) ";
    }
}
