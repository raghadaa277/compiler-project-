package ast.atomExpression;

import ast.argsList.ArgumentsList;

public class ObjectCreation extends AtomExpression{
    private ArgumentsList argumentsList;
    public ObjectCreation( int line_number) {
        super("ObjectCreation", line_number);
    }

    public void setArgumentsList(ArgumentsList argumentsList) {
        this.argumentsList = argumentsList;
    }

    @Override
    public String symbolTablePrint() {
        return super.getVarName() + "(" + argumentsList.symbolTablePrint() + ")";
    }

    @Override
    public String toString() {
        return super.toString() + argumentsList.toString() + " ) ";
    }
}
