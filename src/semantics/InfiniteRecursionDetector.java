package semantics;

import ast.*;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.functionDef.FunctionDefinition;
import symbolTable.*;

import java.util.*;

public class InfiniteRecursionDetector extends ScopeAwareDetector {

    private final Deque<String> functionStack = new ArrayDeque<>();

    @Override
    public void reset() {
        errors.clear();
        functionStack.clear();
        currentScope = null;
    }

    @Override
    protected void enterFunction(FunctionDefinition fd) {
        if (fd.functionName != null) {
            functionStack.push(fd.functionName);
        }
    }

    @Override
    protected void exitFunction(FunctionDefinition fd) {
        if (!functionStack.isEmpty()) {
            functionStack.pop();
        }
    }

    @Override
    protected void processFunctionCall(FunctionCall fc) {
        String funcName = fc.getVarName();
        if (funcName == null) return;

        if (!functionStack.isEmpty() && functionStack.peek().equals(funcName)) {
            addError(fc.line_number,
                    "Semantic Error : Infinite recursion — function '" + funcName + "' calls itself directly.");
        }
    }
}
