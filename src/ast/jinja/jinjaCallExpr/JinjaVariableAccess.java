package ast.jinja.jinjaCallExpr;


public class JinjaVariableAccess extends JinjaCallExpression {
    public String dottedName;
    public JinjaVariableAccess(int line_number) {
        super("JinjaVariableAccess", line_number);
    }

    public void setDottedName(String dottedName) {
        this.dottedName = dottedName;
    }

    @Override
    public String toString() {
        return dottedName;
    }
}
