package semantics;

import ast.*;
import ast.compundStmt.*;
import ast.functionDef.FunctionDefinition;
import symbolTable.*;

import java.util.*;

public class DuplicateFunctionDetector extends ScopeAwareDetector {

    // This detector is intentionally minimal.
    // Duplicate function detection is handled by ScopeAwareDetector.defineSymbol()
    // which is called by walkFunction() in the base class traversal.
    // The DuplicateFunctionDetector exists as a semantic category placeholder
    // so that the SemanticAnalyzer pipeline can be extended.
}
