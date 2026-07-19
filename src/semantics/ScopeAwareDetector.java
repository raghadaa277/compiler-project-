package semantics;

import ast.*;
import ast.argument.Argument;
import ast.argument.KeywordArgument;
import ast.argument.PositionalArgument;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.arithmeticExpr.ArithmeticExpression;
import ast.assignStmt.*;
import ast.atom.Atom;
import ast.atom.Name;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.complexExp.*;
import ast.condition.*;
import ast.functionDef.FunctionDefinition;
import ast.functionDef.FunctionParameter;
import ast.returnStmt.*;
import ast.simpleExpr.SimpleComparisonExpression;
import ast.simpleExpr.SimpleExpression;
import symbolTable.*;

import java.util.*;

public abstract class ScopeAwareDetector implements ErrorDetector {

    protected final List<SemanticError> errors = new ArrayList<>();
    protected Scope currentScope;

    protected static final Set<String> BUILTINS = new HashSet<>(Arrays.asList(
            "Flask", "render_template", "redirect", "url_for",
            "request", "session", "g", "escape", "Markup",
            "True", "False", "None", "len", "range", "int", "str",
            "float", "list", "dict", "set", "tuple", "print",
            "type", "isinstance", "hasattr", "getattr", "setattr",
            "open", "super", "staticmethod", "classmethod",
            "property", "enumerate", "zip", "map", "filter",
            "sorted", "reversed", "min", "max", "sum", "abs",
            "any", "all", "bool", "ord", "chr", "repr", "format",
            "next", "iter", "input", "id", "dir", "vars", "hex", "oct",
            "bin", "callable", "delattr", "frozenset", "memoryview",
            "__name__", "__file__", "__doc__", "__dict__"
    ));



    protected void pushScope(ScopeType type, int line) {
        currentScope = new Scope(currentScope, type, line);
    }

    protected void popScope() {
        if (currentScope != null && currentScope.parent != null) {
            currentScope = currentScope.parent;
        }
    }

    protected ScopeType getCurrentScopeType() {
        return currentScope != null ? currentScope.scopeType : null;
    }

    protected boolean isInScope(ScopeType type) {
        Scope s = currentScope;
        while (s != null) {
            if (s.scopeType == type) return true;
            s = s.parent;
        }
        return false;
    }

    protected String getEnclosingFunctionName() {
        Scope s = currentScope;
        while (s != null) {
            if (s.scopeType == ScopeType.FUNCTION) {
                if (s.parent != null) {
                    for (Symbol sym : s.parent.symbols.values()) {
                        if (sym.kind == SymbolKind.FUNCTION) {
                            return sym.name;
                        }
                    }
                }
                return null;
            }
            s = s.parent;
        }
        return null;
    }



    protected Symbol defineSymbol(String name, SymbolKind kind, int line) {
        if (currentScope == null) return null;
        Symbol existing = currentScope.resolveLocal(name);
        if (existing != null) {
            errors.add(new SemanticError(line,
                    "Semantic Error: Duplicate declaration '" + name
                            + "' in scope " + currentScope.getScopeLabel()
                            + " (first declared at line " + existing.declaredLine + ")."));
            return existing;
        }
        return currentScope.define(name, kind, line);
    }

    protected Symbol resolveSymbol(String name) {
        if (currentScope == null) return null;
        Symbol sym = currentScope.resolve(name);
        if (sym == null && BUILTINS.contains(name)) {
            sym = new Symbol(name, SymbolKind.BUILTIN, -1);
            sym.initialized = true;
        }
        return sym;
    }

    protected Symbol resolveLocalSymbol(String name) {
        if (currentScope == null) return null;
        return currentScope.resolveLocal(name);
    }

    protected void markInitialized(String name) {
        if (currentScope != null) {
            currentScope.markInitialized(name);
        }
    }

    protected boolean isInitialized(String name) {
        Symbol sym = resolveSymbol(name);
        return sym != null && sym.initialized;
    }

    protected boolean isLiteralValue(String name) {
        if (name == null) return true;
        if ((name.startsWith("\"") && name.endsWith("\""))
                || (name.startsWith("'") && name.endsWith("'"))) return true;
        if (name.matches("-?\\d+(\\.\\d+)?")) return true;
        return "True".equals(name) || "False".equals(name) || "None".equals(name);
    }

    // ================ Flow-Sensitive Branch State ================

    private final Deque<Map<String, Boolean>> branchSnapshotStack = new ArrayDeque<>();

    protected void saveBranchState() {
        Map<String, Boolean> snapshot = new HashMap<>();
        if (currentScope != null) {
            captureInitState(currentScope, snapshot);
        }
        branchSnapshotStack.push(snapshot);
    }

    protected void restoreBranchState() {
        if (!branchSnapshotStack.isEmpty()) {
            branchSnapshotStack.pop();
        }
    }

    protected void mergeBranchStates(Map<String, Boolean> initialState) {
        Map<String, Boolean> ifState = branchSnapshotStack.isEmpty()
                ? new HashMap<>() : branchSnapshotStack.pop();
        Map<String, Boolean> merged = new HashMap<>(initialState);
        for (var entry : ifState.entrySet()) {
            merged.merge(entry.getKey(), entry.getValue(), (a, b) -> a && b);
        }
        for (var entry : merged.entrySet()) {
            Symbol sym = resolveSymbol(entry.getKey());
            if (sym != null) sym.initialized = entry.getValue();
        }
    }

    private void captureInitState(Scope scope, Map<String, Boolean> out) {
        for (var entry : scope.symbols.entrySet()) {
            out.put(entry.getKey(), entry.getValue().initialized);
        }
        if (scope.parent != null) {
            captureInitState(scope.parent, out);
        }
    }

    // ================ Error Helpers ================

    protected void addError(int line, String message) {
        errors.add(new SemanticError(line, message));
    }

    // ================ ErrorDetector Interface ================

    @Override
    public void detect(Program program) {
        detach();
        walkProgram(program);
    }

    @Override
    public List<SemanticError> getErrors() {
        return errors;
    }

    @Override
    public void reset() {
        detach();
    }

    private void detach() {
        errors.clear();
        currentScope = null;
    }

    // ================ AST Traversal ================

    protected void walkProgram(Program program) {
        if (program == null || program.statements == null) return;
        enterProgram(program);
        pushScope(ScopeType.GLOBAL, program.line_number);
        for (Statement stmt : program.statements) {
            walkStatement(stmt);
        }
        popScope();
        exitProgram(program);
    }

    protected void walkStatement(Statement stmt) {
        if (stmt == null || stmt.compoundStatements == null) return;
        enterStatement(stmt);
        for (CompoundStatement cs : stmt.compoundStatements) {
            walkCompound(cs);
        }
        exitStatement(stmt);
    }

    protected void walkCompound(CompoundStatement cs) {
        if (cs == null) return;

        if (cs instanceof FunctionDefinition fd) {
            walkFunction(fd);
        } else if (cs instanceof IfStatement is) {
            walkIf(is);
        } else if (cs instanceof ForLoop fl) {
            walkFor(fl);
        } else if (cs instanceof ReturnStatement rs) {
            walkReturn(rs);
        } else if (cs instanceof PythonExpressionAssignStatement pe) {
            enterAssignment(pe.var, pe.value, pe.line_number);
            if (pe.value != null) walkPythonExpression(pe.value);
            exitAssignment(pe.var, pe.value, pe.line_number);
        } else if (cs instanceof ArithmeticAssignStatement aa) {
            enterAssignment(aa.var, aa.value, aa.line_number);
            if (aa.value != null) walkArithmeticExpression(aa.value);
            exitAssignment(aa.var, aa.value, aa.line_number);
        } else if (cs instanceof ComparisonAssignmentStmt ca) {
            enterAssignment(ca.var, ca.value, ca.line_number);
            if (ca.value != null) walkCondition(ca.value);
            exitAssignment(ca.var, ca.value, ca.line_number);
        } else if (cs instanceof TemplateLiteralAssignmentStatement ta) {
            enterAssignment(ta.var, null, ta.line_number);
            exitAssignment(ta.var, null, ta.line_number);
        } else if (cs instanceof ImportStatement imp) {
            enterImport(imp);
            exitImport(imp);
        } else if (cs instanceof AtomExpression ae) {
            walkAtomExpression(ae);
        } else if (cs instanceof SimpleExpression se) {
            walkSimpleExpression(se);
        } else if (cs instanceof PythonExpression pe) {
            walkPythonExpression(pe);
        } else {
            walkGenericCompound(cs);
        }
    }

    // ================ Hook Methods (override in subclasses) ================

    protected void enterProgram(Program program) {}
    protected void exitProgram(Program program) {}
    protected void enterStatement(Statement stmt) {}
    protected void exitStatement(Statement stmt) {}

    protected void enterAssignment(PythonExpression var, ASTNode value, int line) {}
    protected void exitAssignment(PythonExpression var, ASTNode value, int line) {}

    protected void enterImport(ImportStatement imp) {}
    protected void exitImport(ImportStatement imp) {}

    // ================ Function Scope ================

    protected void walkFunction(FunctionDefinition fd) {
        enterFunction(fd);
        pushScope(ScopeType.FUNCTION, fd.line_number);
        if (fd.functionParameters != null && fd.functionParameters.parameters != null) {
            for (FunctionParameter fp : fd.functionParameters.parameters) {
                if (fp.id != null) defineSymbol(fp.id, SymbolKind.PARAMETER, fp.line_number);
            }
        }
        if (fd.functionBody != null) {
            walkStatement(fd.functionBody);
        }
        popScope();
        exitFunction(fd);
    }

    protected void enterFunction(FunctionDefinition fd) {}
    protected void exitFunction(FunctionDefinition fd) {}

    // ================ If/Elif/Else Scopes ================

    protected void walkIf(IfStatement is) {
        if (is.condition != null) walkCondition(is.condition);

        saveBranchState();
        Map<String, Boolean> initialState = branchSnapshotStack.isEmpty()
                ? new HashMap<>() : new HashMap<>(branchSnapshotStack.peek());

        enterIf(is);
        pushScope(ScopeType.IF, is.line_number);
        if (is.statement != null) walkStatement(is.statement);
        popScope();
        exitIf(is);

        if (is.elifStatements != null) {
            for (ElIfStatement elif : is.elifStatements) {
                if (elif.condition != null) walkCondition(elif.condition);
                enterElif(elif);
                pushScope(ScopeType.ELIF, elif.line_number);
                if (elif.statement != null) walkStatement(elif.statement);
                popScope();
                exitElif(elif);
            }
        }

        if (is.elseStatement != null) {
            enterElse(is);
            pushScope(ScopeType.ELSE, is.elseStatement.line_number);
            walkStatement(is.elseStatement);
            popScope();
            exitElse(is);
        }

        if (is.elseStatement == null) {
            restoreBranchState();
        } else {
            mergeBranchStates(initialState);
        }
    }

    protected void enterIf(IfStatement is) {}
    protected void exitIf(IfStatement is) {}
    protected void enterElif(ElIfStatement elif) {}
    protected void exitElif(ElIfStatement elif) {}
    protected void enterElse(IfStatement is) {}
    protected void exitElse(IfStatement is) {}

    // ================ For Loop Scope ================

    protected void walkFor(ForLoop fl) {
        if (fl.iter != null) walkPythonExpression(fl.iter);

        enterFor(fl);
        pushScope(ScopeType.FOR, fl.line_number);
        if (fl.statement != null) walkStatement(fl.statement);
        popScope();
        exitFor(fl);
    }

    protected void enterFor(ForLoop fl) {}
    protected void exitFor(ForLoop fl) {}

    // ================ Return ================

    protected void walkReturn(ReturnStatement rs) {
        enterReturn(rs);
        if (rs instanceof ComplexReturnStatement crs && crs.pythonExpression != null) {
            walkPythonExpression(crs.pythonExpression);
        }
        exitReturn(rs);
    }

    protected void enterReturn(ReturnStatement rs) {}
    protected void exitReturn(ReturnStatement rs) {}

    // ================ Expression Walkers ================

    protected void walkAtomExpression(AtomExpression ae) {
        if (ae == null) return;
        enterAtomExpression(ae);
        String varName = ae.getVarName();

        if (ae instanceof FunctionCall fc) {
            walkFunctionCall(fc);
        } else if (ae instanceof AttributeAccess aa) {
            checkVariableReference(varName, aa.line_number);
        } else if (ae instanceof MethodAccess ma) {
            checkVariableReference(varName, ma.line_number);
        } else if (ae instanceof DictionaryAccess da) {
            checkVariableReference(varName, da.line_number);
        } else if (ae instanceof ListAccess la) {
            checkVariableReference(varName, la.line_number);
        } else if (ae instanceof ObjectCreation oc) {
            checkVariableReference(varName, oc.line_number);
        } else if (ae instanceof SimpleVariable sv) {
            checkVariableReference(varName, sv.line_number);
        }
        exitAtomExpression(ae);
    }

    protected void enterAtomExpression(AtomExpression ae) {}
    protected void exitAtomExpression(AtomExpression ae) {}

    protected void walkFunctionCall(FunctionCall fc) {
        String funcName = fc.getVarName();
        checkVariableReference(funcName, fc.line_number);

        if (fc.argumentsList instanceof AtomArguments aa && aa.getArgs() != null) {
            for (Atom arg : aa.getArgs()) {
                if (arg instanceof Name n) {
                    String name = n.getValue() != null ? n.getValue().toString() : null;
                    checkVariableReference(name, arg.line_number);
                }
            }
        }
        if (fc.argumentsList instanceof ComplexArguments ca && ca.getArguments() != null) {
            for (Argument arg : ca.getArguments()) {
                walkArgument(arg);
            }
        }
        processFunctionCall(fc);
    }

    protected void processFunctionCall(FunctionCall fc) {}

    protected void walkArgument(Argument arg) {
        if (arg instanceof KeywordArgument ka) {
            if (ka.getArg() instanceof SimpleVariable sv) {
                checkVariableReference(sv.getVarName(), ka.line_number);
            } else if (ka.getArg() instanceof FunctionCall innerFc) {
                walkFunctionCall(innerFc);
            } else if (ka.getArg() instanceof AtomExpression ae) {
                walkAtomExpression(ae);
            }
        } else if (arg instanceof PositionalArgument pa) {
            if (pa.getArg() instanceof FunctionCall innerFc) {
                walkFunctionCall(innerFc);
            } else if (pa.getArg() instanceof SimpleVariable sv) {
                checkVariableReference(sv.getVarName(), pa.line_number);
            } else if (pa.getArg() instanceof AtomExpression ae) {
                walkAtomExpression(ae);
            }
        }
    }

    protected void checkVariableReference(String name, int line) {
        // override in subclasses that need to check undefined variables
    }

    protected void walkPythonExpression(PythonExpression expr) {
        if (expr == null) return;
        if (expr instanceof AtomExpression ae) {
            walkAtomExpression(ae);
        } else if (expr instanceof ComplexExpression ce) {
            walkComplexExpression(ce);
        }
    }

    protected void walkComplexExpression(ComplexExpression ce) {
        if (ce instanceof ListComprehension lc) {
            if (lc.forLoop != null) {
                if (lc.forLoop.iter != null) walkPythonExpression(lc.forLoop.iter);
                if (lc.forLoop.statement != null) walkStatement(lc.forLoop.statement);
            }
        } else if (ce instanceof Generator g) {
            if (g.forLoop != null) {
                if (g.forLoop.iter != null) walkPythonExpression(g.forLoop.iter);
                if (g.forLoop.statement != null) walkStatement(g.forLoop.statement);
            }
        } else if (ce instanceof ListLiteral ll) {
            if (ll.listItems != null) {
                for (Atom a : ll.listItems) {
                    if (a instanceof Name n) {
                        String name = n.getValue() != null ? n.getValue().toString() : null;
                        checkVariableReference(name, a.line_number);
                    }
                }
            }
        }
    }

    protected void walkCondition(Condition cond) {
        if (cond == null) return;
        if (cond instanceof ComparisonExpression ce) {
            if (ce.baseExpr != null) walkPythonExpression(ce.baseExpr);
            if (ce.operatorPythonExpressionMap != null) {
                for (PythonExpression pe : ce.operatorPythonExpressionMap.values()) {
                    walkPythonExpression(pe);
                }
            }
        } else if (cond instanceof NotExpression ne) {
            if (ne.pythonExpression != null) walkPythonExpression(ne.pythonExpression);
        }
    }

    protected void walkArithmeticExpression(ArithmeticExpression ae) {
        if (ae == null) return;
        if (ae.left != null) walkPythonExpression(ae.left);
        if (ae.right != null) {
            for (PythonExpression r : ae.right) {
                walkPythonExpression(r);
            }
        }
    }

    protected void walkSimpleExpression(SimpleExpression se) {
        if (se instanceof SimpleComparisonExpression sce) {
            if (sce.condition != null) walkCondition(sce.condition);
        } else if (se instanceof ArithmeticExpression ae) {
            walkArithmeticExpression(ae);
        }
    }

    protected void walkGenericCompound(CompoundStatement cs) {
    }
}
