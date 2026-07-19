package semantics;

import ast.*;
import ast.atom.Atom;
import ast.atom.Name;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.functionDef.FunctionDefinition;
import ast.functionDef.FunctionParameter;
import symbolTable.*;

import java.util.*;

public class UndefinedSymbolDetector extends ScopeAwareDetector {

    @Override
    protected void enterFunction(FunctionDefinition fd) {
        if (fd.functionName != null) {
            defineSymbol(fd.functionName, SymbolKind.FUNCTION, fd.line_number);
        }
    }

    @Override
    protected void enterFor(ForLoop fl) {
        if (fl.var != null) {
            String loopVarName = fl.var.getValue() != null ? fl.var.getValue().toString() : null;
            if (loopVarName != null) {
                Symbol sym = defineSymbol(loopVarName, SymbolKind.LOOP_VAR, fl.line_number);
                if (sym != null) sym.initialized = true;
            }
        }
    }

    @Override
    protected void enterAssignment(PythonExpression var, ASTNode value, int line) {
        if (var instanceof AtomExpression ae) {
            String name = ae.getVarName();
            if (name != null && !isBuiltin(name) && !isLiteralValue(name)) {
                Symbol sym = resolveSymbol(name);
                if (sym == null) {
                    sym = defineSymbol(name, SymbolKind.VARIABLE, ae.line_number);
                }
                if (sym != null) {
                    sym.initialized = true;
                }
            }
        }
    }

    @Override
    protected void checkVariableReference(String name, int line) {
        if (name == null) return;
        if (isBuiltin(name) || isLiteralValue(name)) return;

        Symbol sym = resolveSymbol(name);
        if (sym == null) {
            addError(line, "Semantic Error : '" + name + "' is not defined.");
        } else if (!sym.initialized) {
            addError(line, "Semantic Error : Variable '" + name
                    + "' is used before initialization.");
        }
    }

    private boolean isBuiltin(String name) {
        return BUILTINS.contains(name);
    }
}
