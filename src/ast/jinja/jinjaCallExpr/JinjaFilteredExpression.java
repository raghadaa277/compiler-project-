package ast.jinja.jinjaCallExpr;

public class JinjaFilteredExpression extends JinjaCallExpression{
    private JinjaVariableAccess jinjaVariableAccess;
    private String filterName;
    public JinjaFilteredExpression( int line_number) {
        super("JinjaFilteredExpression", line_number);
    }

    public void setJinjaVariableAccess(JinjaVariableAccess jinjaVariableAccess) {
        this.jinjaVariableAccess = jinjaVariableAccess;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + jinjaVariableAccess.toString()
                + (filterName != null ? (" | " + filterName) : "") + " ) ";
    }
}
