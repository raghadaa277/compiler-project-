package ast;

import java.util.List;

public class Program extends ASTNode{

    private List<Statement> statements;

    public Program(int line_number, List<Statement> statements) {
        super("Program", line_number);
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        for(Statement s : this.statements){
            stringBuilder.append(Consts.printIndent(1)).append(s.toString());
        }
        return stringBuilder.toString();
    }
}
