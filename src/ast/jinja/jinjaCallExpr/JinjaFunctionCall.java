package ast.jinja.jinjaCallExpr;

import ast.jinja.JinjaArgumentsList;

public class JinjaFunctionCall extends JinjaCallExpression {
    private JinjaArgumentsList argumentsList;
    private String functionName;
    public JinjaFunctionCall(int line_number) {
        super("JinjaFunctionCall", line_number);
    }

    public void setArgumentsList(JinjaArgumentsList argumentsList) {
        this.argumentsList = argumentsList;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return functionName + " ( " + argumentsList.toString() + " )";
    }
}
