package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.compundStmt.PythonExpression;
import ast.condition.Condition;
import ast.returnStmt.ConditionReturnStatement;
import ast.returnStmt.ComplexReturnStatement;
import ast.returnStmt.ReturnStatement;

public class ReturnStatementVisitor extends PythonParserBaseVisitor<ReturnStatement> {
    @Override
    public ReturnStatement visitConditionReturn(PythonParser.ConditionReturnContext ctx) {
        ConditionReturnStatement crs = new ConditionReturnStatement(ctx.getStart().getLine());
        Condition condition = new ConditionVisitor().visit(ctx.condition());
        crs.setCondition(condition);
        return crs;
    }

    @Override
    public ReturnStatement visitComplexReturn(PythonParser.ComplexReturnContext ctx) {
        ComplexReturnStatement complexReturnStatement = new ComplexReturnStatement(ctx.getStart().getLine());
        PythonExpression pythonExpression = new PythonExpressionVisitor().visit(ctx.python_expr());
        complexReturnStatement.setPythonExpression(pythonExpression);
        return complexReturnStatement;
    }
}
