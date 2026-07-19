package ast.atomExpression;

import ast.atom.Atom;

import java.util.List;

public class AttributeAccess extends AtomExpression {
    private List<Atom> attributes;

    public AttributeAccess(int line_number) {
        super("AttributeAccess", line_number);
    }

    public void setAttributes(List<Atom> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String symbolTablePrint() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.getVarName());
        for (Atom atom : attributes) {
            stringBuilder.append(".").append(atom.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        for (Atom atom : attributes) {
            stringBuilder.append(".").append(atom.toString());
        }
        stringBuilder.append(" ) ");
        return stringBuilder.toString();
    }
}
