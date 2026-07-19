package ast.keyValue;

import ast.ASTNode;
import ast.atom.Atom;

public abstract class KeyValue extends ASTNode {
    private Atom key;

    public KeyValue(String node_name, int line_number) {
        super(node_name, line_number);
    }

    public void setKey(Atom key) {
        this.key = key;
    }


    @Override
    public String symbolTablePrint() {
        return key.toString() + " : ";
    }

    @Override
    public String toString() {
        return key.toString() + " : ";
    }
}
