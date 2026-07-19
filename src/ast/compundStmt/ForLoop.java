package ast.compundStmt;

import ast.Statement;
import ast.atom.Atom;
import ast.condition.Condition;

import java.util.List;

public class ForLoop extends CompoundStatement {
    public Atom var;
    public PythonExpression iter;
    public Condition condition;
    public Statement statement;

    public ForLoop(int line_number) {
        super("ForLoop", line_number);
    }

    public void setVar(Atom var) {
        this.var = var;
    }

    public void setIter(PythonExpression iter) {
        this.iter = iter;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String symbolTablePrint() {
        return "for " +
                var.toString() + " in " +
                iter.symbolTablePrint() + (condition == null ? ""
                : " if " + condition.symbolTablePrint());
    }

    @Override
    public String toString() {
        return super.toString() +
                " ( " + var.toString() + " in " +
                iter.toString() + (condition == null ? ""
                : condition.toString()) + " ) ";
    }
}
