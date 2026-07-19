package ast;

import ast.compundStmt.CompoundStatement;

import java.util.List;

public class Statement extends ASTNode {

    public List<CompoundStatement> compoundStatements;
    public boolean isPass = false;

    public Statement(int line_number) {
        super("Statement", line_number);
    }

    public void setCompoundStatements(List<CompoundStatement> compoundStatements) {
        this.compoundStatements = compoundStatements;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    @Override
    public String toString() {
        if (isPass) {
            super.setNode_name("PassStatement");
            return super.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (CompoundStatement compoundStatement : compoundStatements) {
            stringBuilder.append((compoundStatements.indexOf(compoundStatement) == 0) ? "" : Consts.printIndent(1))
                    .append(compoundStatement == null ? "Null"
                            : compoundStatement.toString());
        }
        return stringBuilder.toString();
    }
}
