package ast.jinja.jinjaExpression;

import ast.jinja.jinjaCallExpr.JinjaCallExpression;

public class JinjaSimpleExpression extends JinjaExpression {
    private JinjaCallExpression expr;

    public JinjaSimpleExpression(int line_number) {
        super("JinjaSimpleExpr node", line_number);
    }

    public void setExpr(JinjaCallExpression expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return expr.toString();
    }
}
