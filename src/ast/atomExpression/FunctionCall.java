package ast.atomExpression;

import ast.argsList.ArgumentsList;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.argument.Argument;
import ast.atom.Atom;

import java.util.List;

public class FunctionCall extends AtomExpression {

    private ArgumentsList argumentsList;

    public FunctionCall(int line_number) {
        super("FunctionCall", line_number);
    }

    public void setArgumentsList(ArgumentsList argumentsList) {
        this.argumentsList = argumentsList;
    }

    @Override
    public String symbolTablePrint() {
        return super.getVarName() + " ( " + argumentsList.symbolTablePrint() + " ) ";
    }

    @Override
    public String toString() {

        if (argumentsList == null) {
            return getVarName() + "()";
        }

        return getVarName() + argumentsList.toString();
    }
}
