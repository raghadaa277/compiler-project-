package ast.simpleExpr;

import ast.compundStmt.CompoundStatement;

public abstract class SimpleExpression extends CompoundStatement {
    public SimpleExpression(String node_name, int line_number) {
        super(node_name, line_number);
    }
}
