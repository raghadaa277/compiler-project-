package ast.atomExpression;

public class ListAccess extends AtomExpression {
    private String index;

    public ListAccess(int line_number) {
        super("ListAccess", line_number);
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + index + "]" + " ) ";
    }
}
