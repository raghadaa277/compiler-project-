package ast.jinja.jinjaCallExpr;

import ast.atom.Atom;

public class JinjaAtom extends JinjaCallExpression {
    private Atom atom;
    public JinjaAtom(int line_number, Atom atom) {
        super(atom.node_name, line_number);
        this.atom = atom;
    }

    @Override
    public String toString() {
        return atom.toString();
    }
}
