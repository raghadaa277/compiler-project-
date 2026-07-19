package ast.assignStmt;

import ast.TemplateLiteral;
public class TemplateLiteralAssignmentStatement extends AssignmentStatement {
    private TemplateLiteral templateLiteral;
    public TemplateLiteralAssignmentStatement(int line_number) {
        super("TemplateLiteralAssignmentStatement", line_number);
    }

    public void setTemplateLiteral(TemplateLiteral templateLiteral) {
        this.templateLiteral = templateLiteral;
    }

    @Override
    public String toString() {
        return super.toString() + templateLiteral.toString() + " ) ";
    }
}
