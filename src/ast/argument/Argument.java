package ast.argument;

import ast.ASTNode;
import ast.compundStmt.PythonExpression;

public abstract class Argument extends ASTNode {
    private PythonExpression arg;

    public Argument(String node_name, int line_number) {
        super(node_name, line_number);
    }

    public void setArg(PythonExpression arg) {
        this.arg = arg;
    }

    @Override
    public String symbolTablePrint() {
        return arg.symbolTablePrint();
    }

    @Override
    public String toString() {
        return arg.toString();
    }

    public PythonExpression getArg() {
        return arg;
    }
}
