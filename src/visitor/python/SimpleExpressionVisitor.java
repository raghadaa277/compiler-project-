package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.condition.Condition;
import ast.simpleExpr.SimpleComparisonExpression;
import ast.simpleExpr.SimpleExpression;

public class SimpleExpressionVisitor extends PythonParserBaseVisitor<SimpleExpression> {
    @Override
    public SimpleExpression visitArithmeticExpression(PythonParser.ArithmeticExpressionContext ctx) {
        return new ArithmeticExpressionVisitor().visit(ctx.arithmetic_expr());
    }

    @Override
    public SimpleExpression visitSimpleComparisonExpression(PythonParser.SimpleComparisonExpressionContext ctx) {
        SimpleComparisonExpression simpleComparisonExpression = new SimpleComparisonExpression(ctx.getStart().getLine());
        Condition condition = new ConditionVisitor().visit(ctx.condition());
        simpleComparisonExpression.setCondition(condition);
        return simpleComparisonExpression;
    }

}
