package ast.argument;

public class KeywordArgument extends Argument {
    private String argName;

    public KeywordArgument(int line_number) {
        super("KeywordArgument", line_number);
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    @Override
    public String toString() {
        return argName + " = " + super.toString();
    }
}
