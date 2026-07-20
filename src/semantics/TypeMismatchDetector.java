package semantics;

import ast.*;
import ast.arithmeticExpr.ArithmeticExpression;
import ast.assignStmt.*;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.condition.*;
import ast.functionDef.FunctionDefinition;
import ast.functionDef.FunctionParameter;
import ast.simpleExpr.SimpleExpression;
import symbolTable.*;

import java.util.*;

public class TypeMismatchDetector extends ScopeAwareDetector {

    private final Map<String, DataType> variableTypes = new HashMap<>();

    @Override
    public void reset() {
        errors.clear();
        variableTypes.clear();
        currentScope = null;
    }

    @Override
    protected void enterFunction(FunctionDefinition fd) {
        if (fd.functionParameters != null && fd.functionParameters.parameters != null) {
            for (FunctionParameter fp : fd.functionParameters.parameters) {
                if (fp.id != null) {
                    variableTypes.put(fp.id, DataType.UNKNOWN);
                }
            }
        }
    }

    @Override
    protected void enterAssignment(PythonExpression var, ASTNode value, int line) {
        if (var instanceof AtomExpression ae) {
            String varName = ae.getVarName();
            if (varName != null && !isLiteralValue(varName)) {
                DataType inferredType = inferExpressionType(value);
                variableTypes.put(varName, inferredType);
            }
        }
    }

    @Override
    protected void walkArithmeticExpression(ArithmeticExpression ae) {
        super.walkArithmeticExpression(ae);
        checkArithmeticTypeMismatch(ae.line_number, ae);
    }

    @Override
    protected void walkSimpleExpression(SimpleExpression se) {
        if (se instanceof ArithmeticExpression ae) {
            walkArithmeticExpression(ae);
        }
    }

    private void checkArithmeticTypeMismatch(int line, ArithmeticExpression ae) {
        if (ae == null) return;
        boolean hasString = false;
        boolean hasNumber = false;

        if (ae.left != null) {
            DataType t = inferLiteralType(ae.left);
            if (t == DataType.STRING) hasString = true;
            if (t == DataType.NUMBER || t == DataType.ARITHMETIC) hasNumber = true;
        }
        if (ae.right != null) {
            for (PythonExpression r : ae.right) {
                DataType t = inferLiteralType(r);
                if (t == DataType.STRING) hasString = true;
                if (t == DataType.NUMBER || t == DataType.ARITHMETIC) hasNumber = true;
            }
        }
        if (hasString && hasNumber && "+".equals(ae.operator)) {
            addError(line,
                    "Semantic Error : Type mismatch — cannot concatenate string and number in arithmetic expression.");
        }
    }

    private DataType inferExpressionType(ASTNode expr) {
        if (expr == null) return DataType.UNKNOWN;
        String name = expr.node_name;
        if ("FunctionCall".equals(name)) return DataType.OBJECT;
        if ("ArithmeticExpression".equals(name)) return DataType.ARITHMETIC;
        if ("MethodAccess".equals(name)) return DataType.OBJECT;
        if ("ObjectCreation".equals(name)) return DataType.OBJECT;
        if (expr instanceof AtomExpression ae) {
            String vn = ae.getVarName();
            if (vn == null) return DataType.UNKNOWN;
            if ((vn.startsWith("\"") && vn.endsWith("\""))
                    || (vn.startsWith("'") && vn.endsWith("'"))) return DataType.STRING;
            if (vn.matches("-?\\d+(\\.\\d+)?")) return DataType.NUMBER;
            if ("True".equals(vn) || "False".equals(vn)) return DataType.BOOLEAN;
            if ("None".equals(vn)) return DataType.NONE;
            DataType mapped = variableTypes.get(vn);
            if (mapped != null) return mapped;
        }
        return DataType.UNKNOWN;
    }

    private DataType inferLiteralType(PythonExpression expr) {
        DataType t = inferExpressionType(expr);
        if (t == DataType.STRING || t == DataType.NUMBER || t == DataType.ARITHMETIC) return t;
        return DataType.UNKNOWN;
    }
}
