package ast.complexExp;

import ast.compundStmt.ForLoop;

public class Generator extends ComplexExpression{
    private ForLoop forLoop;
    public Generator(int line_number) {
        super("Generator", line_number);
    }

    public void setForLoop(ForLoop forLoop) {
        this.forLoop = forLoop;
    }

    @Override
    public String symbolTablePrint() {
        return  " ( " + forLoop.symbolTablePrint() + " ) ";
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + forLoop.toString() + " ) ";
    }
}
