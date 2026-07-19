package ast.complexExp;

import ast.atom.Atom;

import java.util.List;

public class ListItems extends ComplexExpression {
    private List<Atom> atomList;

    public ListItems(int line_number) {
        super("ExpressionList", line_number);
    }

    public void setAtomList(List<Atom> atomList) {
        this.atomList = atomList;
    }

    public List<Atom> getAtomList() {
        return atomList;
    }
}
