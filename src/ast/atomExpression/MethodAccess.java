package ast.atomExpression;

import java.util.List;

public class MethodAccess extends AtomExpression {
    private List<AtomExpression> methodCalls;

    public MethodAccess(int line_number) {
        super("MethodAccess", line_number);
    }

    public void setMethodCalls(List<AtomExpression> methodCalls) {
        this.methodCalls = methodCalls;
    }

    @Override
    public String symbolTablePrint() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getVarName());
        for (AtomExpression atomExpression : methodCalls) {
            stringBuilder.append(".").append(atomExpression.symbolTablePrint());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        for (AtomExpression atomExpression : methodCalls) {
            stringBuilder.append(".").append(atomExpression.toString());
        }
        return stringBuilder.toString() + " ) ";
    }
}
