package ast.returnStmt;

import ast.compundStmt.CompoundStatement;

public abstract class ReturnStatement extends CompoundStatement {
    public ReturnStatement(String node_name, int line_number) {
        super(node_name, line_number);

    }

    @Override
    public String toString() {
        return "return ";
    }
}
