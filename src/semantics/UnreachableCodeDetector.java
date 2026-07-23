package semantics;

import ast.*;
import ast.compundStmt.*;
import ast.returnStmt.ReturnStatement;
import symbolTable.*;

import java.util.*;

public class UnreachableCodeDetector extends ScopeAwareDetector {

    @Override
    protected void walkStatement(Statement stmt) {
        if (stmt == null || stmt.compoundStatements == null) return;
        int lastReturnLine = -1;

        for (CompoundStatement cs : stmt.compoundStatements) {
            if (lastReturnLine >= 0) {
                addError(cs.line_number,
                        "Semantic Error : Unreachable code after return statement at line " + lastReturnLine + ".");
            } else {
                walkCompound(cs);
                if (cs instanceof ReturnStatement) {
                    lastReturnLine = cs.line_number;
                }
            }
        }
    }
}
