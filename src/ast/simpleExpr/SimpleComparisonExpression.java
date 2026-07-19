package ast.simpleExpr;

import ast.condition.Condition;

public class SimpleComparisonExpression extends SimpleExpression {
    private Condition condition;

    public SimpleComparisonExpression(int line_number) {
        super("SimpleComparisonExpression", line_number);
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String symbolTablePrint() {
        return condition.symbolTablePrint();
    }

    @Override
    public String toString() {
        return condition.toString();
    }
}
