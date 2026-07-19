package ast.assignStmt;

import ast.arithmeticExpr.ArithmeticExpression;

public class ArithmeticAssignStatement extends AssignmentStatement {
    private ArithmeticExpression value;
    public ArithmeticAssignStatement(int line_number) {
        super("ArithmeticAssignStatement", line_number);
    }

    public void setValue(ArithmeticExpression value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + value.toString() + " ) ";
    }
}
