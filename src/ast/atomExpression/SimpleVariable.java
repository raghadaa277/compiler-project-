package ast.atomExpression;

public class SimpleVariable extends AtomExpression {
    public SimpleVariable(int line_number) {
        super("SimpleVariable", line_number);
    }

    @Override
    public String symbolTablePrint() {
        return super.getVarName();
    }

    @Override
    public String toString() {
        return super.toString() + " ) ";
    }

}
