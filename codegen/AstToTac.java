package codegen;

import ast.*;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.argument.*;
import ast.arithmeticExpr.ArithmeticExpression;
import ast.assignStmt.*;
import ast.atom.Atom;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.complexExp.*;
import ast.condition.*;
import ast.functionDef.*;
import ast.keyValue.*;
import ast.returnStmt.*;
import ast.simpleExpr.SimpleExpression;
import codegen.ir.*;

import java.util.List;
import java.util.Map;

public class AstToTac {

    private final TacProgram tac = new TacProgram();

    public TacProgram translate(Program program) {
        if (program == null || program.statements == null) return tac;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                translateCompound(cs);
            }
        }
        return tac;
    }

    private void translateCompound(CompoundStatement cs) {
        if (cs == null) return;
        if (cs instanceof FunctionDefinition fd) {
            translateFunctionDef(fd);
        } else if (cs instanceof ClassDefinition cd) {
            translateClassDef(cd);
        } else if (cs instanceof ImportStatement imp) {
            translateImport(imp);
        } else if (cs instanceof GlobalStatement gs) {
            translateGlobal(gs);
        } else if (cs instanceof ReturnStatement rs) {
            translateReturn(rs);
        } else if (cs instanceof AssignmentStatement as) {
            translateAssignment(as);
        } else if (cs instanceof ForLoop fl) {
            translateForLoop(fl);
        } else if (cs instanceof IfStatement ifs) {
            translateIfStatement(ifs);
        } else if (cs instanceof AtomExpression ae) {
            translateAtomExprEval(ae);
        } else if (cs instanceof PythonExpression pe) {
            translatePythonExprEval(pe);
        }
    }

    private void translateStatement(Statement stmt) {
        if (stmt == null || stmt.compoundStatements == null) return;
        for (CompoundStatement cs : stmt.compoundStatements) {
            translateCompound(cs);
        }
    }

    private void translateFunctionDef(FunctionDefinition fd) {
        int n = (fd.functionParameters != null && fd.functionParameters.parameters != null)
                ? fd.functionParameters.parameters.size() : 0;
        tac.add(TacInstruction.funcDef(fd.functionName, n));
        if (fd.functionBody != null) translateStatement(fd.functionBody);
        tac.add(TacInstruction.funcEnd());
    }

    private void translateClassDef(ClassDefinition cd) {
        tac.add(TacInstruction.classDef(cd.className));
        if (cd.classBody != null && cd.classBody.compoundStatements != null) {
            for (CompoundStatement cs : cd.classBody.compoundStatements) {
                translateCompound(cs);
            }
        }
        tac.add(TacInstruction.classEnd());
    }

    private void translateImport(ImportStatement imp) {
        String repr = imp.toString();
        tac.add(TacInstruction.imprt("module", repr));
    }

    private void translateGlobal(GlobalStatement gs) {
        tac.add(TacInstruction.global(gs.toString()));
    }

    private void translateReturn(ReturnStatement rs) {
        String val = "None";
        if (rs instanceof SimpleReturnStatement srs) {
            val = srs.toString().replace("SimpleReturnStatement", "").trim();
        } else if (rs instanceof ComplexReturnStatement crs) {
            if (crs.pythonExpression != null) {
                val = exprToString(crs.pythonExpression);
            }
        }
        tac.add(TacInstruction.ret(val));
    }

    private void translateAssignment(AssignmentStatement as) {
        String varStr = as.var != null ? exprToString(as.var) : "?";
        if (as instanceof PythonExpressionAssignStatement peas) {
            String val = peas.value != null ? exprToString(peas.value) : "None";
            tac.add(TacInstruction.assign(varStr, val));
        } else if (as instanceof ArithmeticAssignStatement aas) {
            String val = aas.value != null ? aas.value.toString() : "None";
            tac.add(TacInstruction.assign(varStr, val));
        } else if (as instanceof ComparisonAssignmentStmt cas) {
            String val = cas.value != null ? cas.value.toString() : "None";
            tac.add(TacInstruction.assign(varStr, val));
        } else if (as instanceof TemplateLiteralAssignmentStatement) {
            tac.add(TacInstruction.assign(varStr, "\"...\""));
        }
    }

    private void translateForLoop(ForLoop fl) {
        String varName = fl.var != null ? String.valueOf(fl.var.getValue()) : "?";
        String iterExpr = fl.iter != null ? exprToString(fl.iter) : "?";
        String startL = tac.newLabel();
        String endL = tac.newLabel();
        tac.add(TacInstruction.label(startL));
        tac.add(TacInstruction.assign("_iter", iterExpr));
        String bodyL = tac.newLabel();
        tac.add(TacInstruction.goto_(bodyL));
        tac.add(TacInstruction.label(endL));
        tac.add(TacInstruction.assign(varName, "_item"));
        if (fl.statement != null) translateStatement(fl.statement);
        tac.add(TacInstruction.goto_(startL));
        tac.add(TacInstruction.label(bodyL));
    }

    private void translateIfStatement(IfStatement ifs) {
        String elseL = tac.newLabel();
        String endL = tac.newLabel();
        String cond = ifs.condition != null ? conditionToString(ifs.condition) : "True";
        tac.add(TacInstruction.ifGoto(cond, elseL));
        if (ifs.statement != null) translateStatement(ifs.statement);
        tac.add(TacInstruction.goto_(endL));
        tac.add(TacInstruction.label(elseL));
        if (ifs.elifStatements != null) {
            for (ElIfStatement elif : ifs.elifStatements) {
                String c = elif.condition != null ? conditionToString(elif.condition) : "True";
                String nextL = tac.newLabel();
                tac.add(TacInstruction.ifGoto(c, nextL));
                if (elif.statement != null) translateStatement(elif.statement);
                tac.add(TacInstruction.goto_(endL));
                tac.add(TacInstruction.label(nextL));
            }
        }
        if (ifs.elseStatement != null) translateStatement(ifs.elseStatement);
        tac.add(TacInstruction.label(endL));
    }

    private String exprToString(PythonExpression expr) {
        if (expr == null) return "None";
        if (expr instanceof SimpleVariable sv) return sv.getVarName();
        if (expr instanceof FunctionCall fc) return fc.getVarName() + "(...)";
        if (expr instanceof AttributeAccess aa) {
            String base = aa.getVarName();
            String attrs = "?";
            if (aa.getAttributes() != null && !aa.getAttributes().isEmpty()) {
                attrs = aa.getAttributes().get(0).getValue().toString();
            }
            return base + "." + attrs;
        }
        if (expr instanceof MethodAccess ma) {
            return ma.getVarName() + ".method(...)";
        }
        if (expr instanceof ObjectCreation oc) {
            return "new " + oc.getVarName() + "(...)";
        }
        if (expr instanceof ListAccess la) {
            return la.getVarName() + "[idx]";
        }
        if (expr instanceof DictionaryAccess da) {
            return da.getVarName() + "[key]";
        }
        if (expr instanceof ListLiteral ll) {
            return "[...]";
        }
        if (expr instanceof ListComprehension lc) {
            return "[... for ...]";
        }
        if (expr instanceof DictionaryLiteral dl) {
            return "{...}";
        }
        return expr.toString();
    }

    private String conditionToString(Condition cond) {
        if (cond == null) return "True";
        String s = cond.toString();
        return s.isEmpty() ? "True" : s;
    }

    private void translateAtomExprEval(AtomExpression ae) {
        // side-effect evaluation in IR (e.g. function calls for effect)
    }

    private void translatePythonExprEval(PythonExpression pe) {
        // side-effect evaluation in IR
    }
}
