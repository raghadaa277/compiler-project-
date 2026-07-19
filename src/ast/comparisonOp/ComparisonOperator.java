package ast.comparisonOp;

import ast.ASTNode;

public class ComparisonOperator extends ASTNode {
    private String operator;

    public ComparisonOperator( int line_number) {
        super("ComparisonOperator", line_number);
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }
}
