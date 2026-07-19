package ast;

public class Imported extends ASTNode {
    private String name;
    private String alias;

    public Imported(int line_number) {
        super("Imported", line_number);
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + ", ( name : " + name + (alias == null ? "" : alias) + " )";
    }
}
