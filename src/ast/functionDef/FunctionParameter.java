package ast.functionDef;

import ast.ASTNode;
import ast.atom.Atom;

public class FunctionParameter extends ASTNode {
    private String id;
    private Atom value;

    public FunctionParameter(int line_number) {
        super("FunctionParameter", line_number);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(Atom value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return id + (value == null ? "" : value.toString());
    }
}
