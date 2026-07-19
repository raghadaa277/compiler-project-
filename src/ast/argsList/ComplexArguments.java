package ast.argsList;

import ast.argument.Argument;

import java.util.List;

public class ComplexArguments extends ArgumentsList {
    private List<Argument> arguments;
    public ComplexArguments(int line_number) {
        super("ComplexArguments", line_number);
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    @Override
    public String symbolTablePrint() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Argument arg : arguments) {
            stringBuilder.append(arg.symbolTablePrint())
                    .append((arguments.indexOf(arg) == arguments.size() - 1) ? "" : ", ");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Argument arg : arguments) {
            stringBuilder.append(arg.toString())
                    .append((arguments.indexOf(arg) == arguments.size() - 1) ? "" : ", ");
        }
        return stringBuilder.toString();
    }
}
