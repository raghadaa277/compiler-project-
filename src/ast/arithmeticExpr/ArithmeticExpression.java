package ast.arithmeticExpr;

import ast.compundStmt.PythonExpression;
import ast.simpleExpr.SimpleExpression;

import java.util.List;

public class ArithmeticExpression extends SimpleExpression {
    private PythonExpression left;
    private List<PythonExpression> right;
    private String operator;

    public ArithmeticExpression(String node_name, int line_number) {
        super(node_name, line_number);
    }

    public void setLeft(PythonExpression left) {
        this.left = left;
    }

    public void setRight(List<PythonExpression> right) {
        this.right = right;
    }

    public void setOperator(Operator operator) {
        switch (operator){
            case Operator.Addition -> this.operator = "+";
            case Operator.Multiplication -> this.operator = "*";
            case Operator.Division -> this.operator = "/";
            case Operator.Subtraction -> this.operator = "-";
        }
    }

    @Override
    public String symbolTablePrint() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.symbolTablePrint());
        if(right != null){
            for(PythonExpression pythonExpression : right){
                stringBuilder.append(operator).append(" ").append(pythonExpression.symbolTablePrint());
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.toString());
        if(right != null){
            for(PythonExpression pythonExpression : right){
                stringBuilder.append(operator).append(pythonExpression.toString());
            }
        }
        return stringBuilder.toString();
    }
}
