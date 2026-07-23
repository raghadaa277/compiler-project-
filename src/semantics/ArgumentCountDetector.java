package semantics;

import ast.*;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.functionDef.FunctionDefinition;
import symbolTable.*;

import java.util.*;

public class ArgumentCountDetector extends ScopeAwareDetector {

    // Maps function name -> expected parameter count, populated from scope tree
    private final Map<String, Integer> functionParamCount = new HashMap<>();

    @Override
    public void reset() {
        errors.clear();
        functionParamCount.clear();
        currentScope = null;
    }

    @Override
    protected void enterFunction(FunctionDefinition fd) {
        if (fd.functionName != null) {
            int count = (fd.functionParameters != null && fd.functionParameters.parameters != null)
                    ? fd.functionParameters.parameters.size() : 0;
            functionParamCount.put(fd.functionName, count);
        }
    }

    @Override
    protected void processFunctionCall(FunctionCall fc) {
        String funcName = fc.getVarName();
        if (funcName != null && functionParamCount.containsKey(funcName)) {
            Integer expected = functionParamCount.get(funcName);
            int actual = 0;
            if (fc.argumentsList instanceof AtomArguments aa) {
                actual = (aa.getArgs() != null) ? aa.getArgs().size() : 0;
            } else if (fc.argumentsList instanceof ComplexArguments ca) {
                actual = (ca.getArguments() != null) ? ca.getArguments().size() : 0;
            }
            if (actual != expected) {
                addError(fc.line_number,
                        "Semantic Error : Wrong argument count in call to '" + funcName
                                + "'. Expected " + expected + ", got " + actual + ".");
            }
        }
    }
}
