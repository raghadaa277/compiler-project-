package semantics;

import ast.*;
import ast.compundStmt.*;
import ast.functionDef.FunctionDefinition;
import ast.returnStmt.ReturnStatement;
import symbolTable.*;

import java.util.*;

public class ReturnOutsideFunctionDetector extends ScopeAwareDetector {

    @Override
    protected void enterReturn(ReturnStatement rs) {
        if (!isInScope(ScopeType.FUNCTION)) {
            addError(rs.line_number,
                    "Semantic Error : Return statement outside a function (global scope).");
        }
    }
}
