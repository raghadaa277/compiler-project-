package ast.cssTerm;

import ast.Consts;

public class FunctionTerm extends CssTerm {
    private CssFunctionArguments arguments;


    public FunctionTerm(int line_number) {
        super("Function", line_number);
    }

    public void setArguments(CssFunctionArguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return super.toString() + Consts.printIndent(3) + arguments.toString();
    }
}
