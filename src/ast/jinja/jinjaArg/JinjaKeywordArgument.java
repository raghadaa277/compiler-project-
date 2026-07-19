package ast.jinja.jinjaArg;


public class JinjaKeywordArgument extends JinjaArgument {
    private String id;

    public JinjaKeywordArgument(int line_number) {
        super("JinjaKeywordArgument", line_number);

    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + id + " = " +getArgument().toString() + " ) ";
    }
}
