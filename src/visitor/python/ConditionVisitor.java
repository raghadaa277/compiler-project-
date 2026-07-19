package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.atom.Bool;
import ast.comparisonOp.ComparisonOperator;
import ast.compundStmt.PythonExpression;
import ast.condition.BooleanCondition;
import ast.condition.ComparisonExpression;
import ast.condition.Condition;
import ast.condition.NotExpression;
import visitor.UniversalPythonVisitor;

import java.util.HashMap;
import java.util.Map;

public class ConditionVisitor extends PythonParserBaseVisitor<Condition> {
    PythonExpressionVisitor pythonExpressionVisitor = new PythonExpressionVisitor();

    @Override
    public Condition visitBooleanCondition(PythonParser.BooleanConditionContext ctx) {
        BooleanCondition booleanCondition = new BooleanCondition(ctx.getStart().getLine());
        Bool bool = (Bool) new UniversalPythonVisitor().visit(ctx.bool_exp());
        booleanCondition.setBoolValue(bool);
        return booleanCondition;
    }

    @Override
    public Condition visitNotExpression(PythonParser.NotExpressionContext ctx) {
        NotExpression notExpression = new NotExpression(ctx.getStart().getLine());
        notExpression.setPythonExpression(pythonExpressionVisitor.visit(ctx.python_expr()));
        return notExpression;
    }

    @Override
    public Condition visitComparisonExpression(PythonParser.ComparisonExpressionContext ctx) {
        ComparisonExpression comparisonExpression = new ComparisonExpression(ctx.getStart().getLine());
        PythonExpression baseExpr = pythonExpressionVisitor.visit(ctx.python_expr(0));
        comparisonExpression.setBaseExpr(baseExpr);
        Map<ComparisonOperator, PythonExpression> pythonExpressionMap = new HashMap<>();
        for (int i = 0; i < ctx.comp_op().size(); i++) {
            ComparisonOperator comparisonOperator = new ComparisonOperator(ctx.comp_op(i).start.getLine());
            comparisonOperator.setOperator(ctx.comp_op(i).getText());
            PythonExpression pythonExpression = pythonExpressionVisitor.visit(ctx.python_expr(i + 1));
            pythonExpressionMap.put(comparisonOperator, pythonExpression);
        }
        comparisonExpression.setOperatorPythonExpressionMap(pythonExpressionMap);

        return comparisonExpression;
    }
}
