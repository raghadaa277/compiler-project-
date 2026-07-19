package ast.complexExp;

import ast.compundStmt.PythonExpression;

public abstract class ComplexExpression extends PythonExpression {
    public ComplexExpression(String node_name, int line_number) {
        super(node_name, line_number);
    }
}
