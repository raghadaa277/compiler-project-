package ast.atomExpression;

import ast.compundStmt.PythonExpression;

public abstract class AtomExpression extends PythonExpression {
    private String varName;

    public AtomExpression(String node_name, int line_number) {
        super(node_name, line_number);
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public String symbolTablePrint() {
        return " ( " + varName + " ";
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + varName + " ";
    }
}
