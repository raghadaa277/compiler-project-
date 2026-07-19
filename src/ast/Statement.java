package ast;

import ast.compundStmt.CompoundStatement;
import java.util.List;

public class Statement extends ASTNode {

    private List<CompoundStatement> compoundStatements;
    private boolean isPass = false;

    public Statement(int line_number) {
        super("Statement", line_number);
    }

    public List<CompoundStatement> getCompoundStatements() {
        return this.compoundStatements;
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
        if (compoundStatements != null) {
            for (CompoundStatement compoundStatement : compoundStatements) {
                stringBuilder.append((compoundStatements.indexOf(compoundStatement) == 0) ? "" : Consts.printIndent(1))
                        .append(compoundStatement == null ? "Null" : compoundStatement.toString());
            }
        }
        return stringBuilder.toString();
    }
}

//package ast;
//
//import ast.compundStmt.CompoundStatement;
//
//import java.util.List;
//
//public class Statement extends ASTNode {
//
//    private List<CompoundStatement> compoundStatements;
//    private boolean isPass = false;
//
//    public Statement(int line_number) {
//        super("Statement", line_number);
//    }
//
//    public void setCompoundStatements(List<CompoundStatement> compoundStatements) {
//        this.compoundStatements = compoundStatements;
//    }
//
//    public void setPass(boolean pass) {
//        isPass = pass;
//    }
//
//    @Override
//    public String toString() {
//        if (isPass) {
//            super.setNode_name("PassStatement");
//            return super.toString();
//        }
//        StringBuilder stringBuilder = new StringBuilder();
//        for (CompoundStatement compoundStatement : compoundStatements) {
//            stringBuilder.append((compoundStatements.indexOf(compoundStatement) == 0) ? "" : Consts.printIndent(1))
//                    .append(compoundStatement == null ? "Null"
//                            : compoundStatement.toString());
//        }
//        return stringBuilder.toString();
//    }
//}
