package ast.returnStmt;

import ast.compundStmt.PythonExpression;

public class ComplexReturnStatement extends ReturnStatement {
    private PythonExpression pythonExpression;

    public ComplexReturnStatement(int line_number) {
        super("ComplexReturnStatement", line_number);
    }

    public void setPythonExpression(PythonExpression pythonExpression) {
        this.pythonExpression = pythonExpression;
    }

    @Override
    public String toString() {
        return super.toString() + pythonExpression.toString();
    }
}
