package ast.compundStmt;

import ast.Consts;
import ast.condition.Condition;
import ast.ElIfStatement;
import ast.Statement;

import java.util.List;

public class IfStatement extends CompoundStatement {
    private Condition condition;
    private Statement statement;
    private List<ElIfStatement> elifStatements;
    private Statement elseStatement;

    public IfStatement(int line_number) {
        super("IfStatement", line_number);
    }

    public void setElseStatement(Statement elseStatement) {
        this.elseStatement = elseStatement;
    }

    public void setElifStatements(List<ElIfStatement> elifStatements) {
        this.elifStatements = elifStatements;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append(", ( ")
                .append(condition == null ? "Null" : condition.toString()).append(" )")
                .append(Consts.printIndent(2)).append(statement == null ? "Null"
                        : statement.toString());
        if (elifStatements != null) {
            for (ElIfStatement elIfStatement : elifStatements) {
                stringBuilder.append(Consts.printIndent(1)).append(elIfStatement.toString());
            }
        }
        if (elseStatement != null) {
            elseStatement.setNode_name("ElseStatement");
            stringBuilder.append(Consts.printIndent(1)).append(elseStatement.toString());
        }
        return stringBuilder.toString();
    }
}
