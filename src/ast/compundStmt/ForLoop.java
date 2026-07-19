package ast.compundStmt;

import ast.atom.Atom;
import ast.condition.Condition;
import ast.Statement;



public class ForLoop extends CompoundStatement {
    private Atom var;
    private PythonExpression iter;
    private Condition condition;
    private Statement body;

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

    public void setBody(Statement body) {
        this.body = body;
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
                : condition.toString()) + " ) " +
                (body == null ? "" : "\n" + body.toString());
    }
}

//package ast.compundStmt;
//
//import ast.atom.Atom;
//import ast.condition.Condition;
//
//import java.util.List;
//
//public class ForLoop extends CompoundStatement {
//    private Atom var;
//    private PythonExpression iter;
//    private Condition condition;
//
//    public ForLoop(int line_number) {
//        super("ForLoop", line_number);
//    }
//
//    public void setVar(Atom var) {
//        this.var = var;
//    }
//
//    public void setIter(PythonExpression iter) {
//        this.iter = iter;
//    }
//
//    public void setCondition(Condition condition) {
//        this.condition = condition;
//    }
//
//    @Override
//    public String symbolTablePrint() {
//        return "for " +
//                var.toString() + " in " +
//                iter.symbolTablePrint() + (condition == null ? ""
//                : " if " + condition.symbolTablePrint());
//    }
//
//    @Override
//    public String toString() {
//        return super.toString() +
//                " ( " + var.toString() + " in " +
//                iter.toString() + (condition == null ? ""
//                : condition.toString()) + " ) ";
//    }
//}
