package ast.condition;

import ast.Consts;
import ast.comparisonOp.ComparisonOperator;
import ast.compundStmt.PythonExpression;

import java.util.Map;

public class ComparisonExpression extends Condition {
    private PythonExpression baseExpr;
    private Map<ComparisonOperator, PythonExpression> operatorPythonExpressionMap;

    public ComparisonExpression(int line_number) {
        super("ComparisonExpression", line_number);
    }

    public void setBaseExpr(PythonExpression baseExpr) {
        this.baseExpr = baseExpr;
    }

    public void setOperatorPythonExpressionMap(Map<ComparisonOperator,
            PythonExpression> operatorPythonExpressionMap) {
        this.operatorPythonExpressionMap = operatorPythonExpressionMap;
    }


    @Override
    public String symbolTablePrint() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseExpr == null ? "Null" : baseExpr.symbolTablePrint());
        if(operatorPythonExpressionMap != null){
            for (ComparisonOperator comparisonOperator : operatorPythonExpressionMap.keySet()) {
                PythonExpression compExpr = operatorPythonExpressionMap.get(comparisonOperator);
                stringBuilder.append(" ").append(comparisonOperator == null ? "Null" : comparisonOperator.toString()).append(" ")
                        .append(compExpr == null ? "Null" : compExpr.symbolTablePrint());
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append(Consts.printIndent(3))
                .append(baseExpr == null ? "Null" : baseExpr.toString());
        if(operatorPythonExpressionMap != null){
            for (ComparisonOperator comparisonOperator : operatorPythonExpressionMap.keySet()) {
                PythonExpression compExpr = operatorPythonExpressionMap.get(comparisonOperator);
                stringBuilder.append(Consts.printIndent(3)).append(comparisonOperator == null ? "Null" : comparisonOperator.toString()).append(" ")
                        .append(compExpr == null ? "Null" : compExpr.toString());
            }
        }
        return stringBuilder.toString();
    }
}
