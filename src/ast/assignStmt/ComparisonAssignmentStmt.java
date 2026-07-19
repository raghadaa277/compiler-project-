package ast.assignStmt;

import ast.condition.Condition;
public class ComparisonAssignmentStmt extends AssignmentStatement {
    private Condition value;

    public ComparisonAssignmentStmt(int line_number) {
        super("ComparisonAssignStmt", line_number);
    }

    public void setValue(Condition value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + value.toString() + " ) ";
    }
}
