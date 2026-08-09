package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.Statement;
import ast.compundStmt.CompoundStatement;

import java.util.ArrayList;
import java.util.List;
public class StatementVisitor extends PythonParserBaseVisitor<Statement> {

    private CompoundStatementVisitor compoundStatementVisitor;

    public StatementVisitor(CompoundStatementVisitor compoundStatementVisitor) {
        this.compoundStatementVisitor = compoundStatementVisitor;
    }

    public StatementVisitor() {
        this.compoundStatementVisitor = new CompoundStatementVisitor(this);
    }

    private Statement buildFromCompoundStmts(List<PythonParser.Compound_stmtContext> stmtCtxs, int line) {
        Statement statement = new Statement(line);
        List<CompoundStatement> compoundStatementList = new ArrayList<>();
        boolean hasPass = false;
        for (PythonParser.Compound_stmtContext stmtCtx : stmtCtxs) {
            CompoundStatement cs = compoundStatementVisitor.visit(stmtCtx);
            if (cs == null && stmtCtx instanceof PythonParser.PassStatementContext) {
                hasPass = true;
            } else if (cs != null) {
                compoundStatementList.add(cs);
            }
        }
        statement.setCompoundStatements(compoundStatementList);
        if (hasPass && compoundStatementList.isEmpty()) {
            statement.setPass(true);
        }
        return statement;
    }

    @Override
    public Statement visitSimpleStatement(PythonParser.SimpleStatementContext ctx) {
        return buildFromCompoundStmts(ctx.compound_stmt(), ctx.getStart().getLine());
    }

    @Override
    public Statement visitSimpleSuite(PythonParser.SimpleSuiteContext ctx) {
        return buildFromCompoundStmts(ctx.compound_stmt(), ctx.getStart().getLine());
    }

    @Override
    public Statement visitCompoundSuite(PythonParser.CompoundSuiteContext ctx) {
        return buildFromCompoundStmts(ctx.compound_stmt(), ctx.getStart().getLine());
    }

    @Override
    public Statement visitPassSuite(PythonParser.PassSuiteContext ctx) {
        Statement statement = new Statement(ctx.getStart().getLine());
        statement.setPass(true);
        return statement;
    }
}
