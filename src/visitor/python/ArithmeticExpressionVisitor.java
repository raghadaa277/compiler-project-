package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.arithmeticExpr.ArithmeticExpression;
import ast.arithmeticExpr.Operator;
import ast.compundStmt.PythonExpression;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticExpressionVisitor extends PythonParserBaseVisitor<ArithmeticExpression> {
    private final PythonExpressionVisitor pythonExpressionVisitor = new PythonExpressionVisitor();

    @Override
    public ArithmeticExpression visitAddition(PythonParser.AdditionContext ctx) {
        return evaluate(Operator.Addition, ctx.getStart().getLine(), ctx.python_expr());
    }

    @Override
    public ArithmeticExpression visitSubtraction(PythonParser.SubtractionContext ctx) {
        return evaluate(Operator.Subtraction, ctx.getStart().getLine(), ctx.python_expr());
    }

    @Override
    public ArithmeticExpression visitDivision(PythonParser.DivisionContext ctx) {
        return evaluate(Operator.Division, ctx.getStart().getLine(), ctx.python_expr());
    }

    @Override
    public ArithmeticExpression visitMultiplication(PythonParser.MultiplicationContext ctx) {
        return evaluate(Operator.Multiplication, ctx.getStart().getLine(), ctx.python_expr());

    }

    private ArithmeticExpression evaluate(Operator operator, int line,
                                          List<PythonParser.Python_exprContext> pythonExprs) {
        ArithmeticExpression arithmeticExpression =
                new ArithmeticExpression("Addition", line);
        arithmeticExpression.setOperator(operator);
        PythonExpression left = pythonExpressionVisitor.visit(pythonExprs.getFirst());
        List<PythonExpression> right = new ArrayList<>();
        for (int i = 1; i < pythonExprs.size(); i++) {
            PythonExpression pythonExpression = pythonExpressionVisitor.visit(pythonExprs.get(i));
            right.add(pythonExpression);
        }
        arithmeticExpression.setLeft(left);
        arithmeticExpression.setRight(right);
        return arithmeticExpression;
    }
}
