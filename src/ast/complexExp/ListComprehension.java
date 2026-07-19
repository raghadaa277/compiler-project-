package ast.complexExp;

import ast.compundStmt.ForLoop;

public class ListComprehension extends ComplexExpression {
    private ForLoop forLoop;

    public ListComprehension(int line_number) {
        super("ListComprehension", line_number);
    }

    public void setForLoop(ForLoop forLoop) {
        this.forLoop = forLoop;
    }

    @Override
    public String symbolTablePrint() {
        return " [ " + forLoop.symbolTablePrint() + " ] ";
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + forLoop.toString() + " ) ";
    }
}
