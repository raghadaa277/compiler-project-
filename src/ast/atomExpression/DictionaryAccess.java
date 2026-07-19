package ast.atomExpression;

public class DictionaryAccess extends AtomExpression {
    private String key;

    public DictionaryAccess(int line_number) {
        super("DictionaryAccess", line_number);
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String symbolTablePrint() {
        return super.getVarName() + "[" + key + "]";
    }

    @Override
    public String toString() {
        return super.toString() + "[" + key + "]" + " ) ";
    }
}
