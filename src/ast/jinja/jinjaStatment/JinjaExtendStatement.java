package ast.jinja.jinjaStatment;

public class JinjaExtendStatement extends JinjaStatement {
    private String extended;

    public JinjaExtendStatement(int line_number) {
        super("JinjaExtendStatement", line_number);
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }

    @Override
    public String toString() {
        return super.toString() + " ( extended : " + extended + " ) ";
    }
}
