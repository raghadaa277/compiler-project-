package codegen;

import ast.*;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.argument.*;
import ast.arithmeticExpr.ArithmeticExpression;
import ast.assignStmt.*;
import ast.atom.Atom;
import ast.atomExpression.*;
import ast.comparisonOp.ComparisonOperator;
import ast.compundStmt.*;
import ast.complexExp.*;
import ast.condition.*;import ast.functionDef.*;
import ast.keyValue.*;
import ast.returnStmt.*;import ast.simpleExpr.SimpleExpression;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PythonCodeGenerator {

    private final Set<String> localModules;
    private StringBuilder out;
    private int indent;

    public PythonCodeGenerator() {
        this(Collections.emptySet());
    }

    public PythonCodeGenerator(Set<String> localModules) {
        this.localModules = localModules != null ? localModules : Collections.emptySet();
    }

    public String generate(Program program) {
        out = new StringBuilder();
        indent = 0;
        if (program != null && program.statements != null) {
            for (int i = 0; i < program.statements.size(); i++) {
                generateStatement(program.statements.get(i));
                if (i < program.statements.size() - 1) {
                    out.append("\n");
                }
            }
        }
        return out.toString();
    }

    private void generateTry(TryStatement ts) {
        emit("try:");
        indent++;
        if (ts.getTryBody() != null) generateStatement(ts.getTryBody());
        indent--;
        if (ts.getExceptClauses() != null) {
            for (ExceptClause ec : ts.getExceptClauses()) {
                StringBuilder sb = new StringBuilder("except");
                if (ec.getExceptionType() != null) {
                    sb.append(" ").append(ec.getExceptionType().getValue());
                }
                if (ec.getAlias() != null) {
                    sb.append(" as ").append(ec.getAlias());
                }
                sb.append(":");
                emit(sb.toString());
                indent++;
                if (ec.getBody() != null) generateStatement(ec.getBody());
                indent--;
            }
        }
        if (ts.getElseBody() != null) {
            emit("else:");
            indent++;
            generateStatement(ts.getElseBody());
            indent--;
        }
        if (ts.getFinallyBody() != null) {
            emit("finally:");
            indent++;
            generateStatement(ts.getFinallyBody());
            indent--;
        }
        out.append("\n");
    }

    private void emit(String s) {
        for (int i = 0; i < indent; i++) out.append("    ");
        out.append(s).append("\n");
    }

    private void generateStatement(Statement stmt) {
        if (stmt == null) return;
        if (stmt.isPass) { emit("pass"); return; }
        if (stmt.compoundStatements != null) {
            for (CompoundStatement cs : stmt.compoundStatements) {
                generateCompound(cs);
            }
        }
    }

    private void generateCompound(CompoundStatement cs) {
        if (cs == null) return;
        if (cs instanceof FunctionDefinition fd) generateFunctionDef(fd);
        else if (cs instanceof ClassDefinition cd) generateClassDef(cd);
        else if (cs instanceof ImportStatement imp) generateImport(imp);
        else if (cs instanceof GlobalStatement gs) generateGlobal(gs);
        else if (cs instanceof ReturnStatement rs) generateReturn(rs);
        else if (cs instanceof AssignmentStatement as) generateAssignment(as);
        else if (cs instanceof ForLoop fl) generateForLoop(fl);
        else if (cs instanceof IfStatement ifs) generateIfStatement(ifs);
        else if (cs instanceof TryStatement ts) generateTry(ts);
        else if (cs instanceof DeleteStatement ds) generateDelete(ds);
        else if (cs instanceof ArithmeticExpression arith) emit(arithExprToString(arith));
        else if (cs instanceof AtomExpression ae) emit(atomExprToString(ae));
        else if (cs instanceof PythonExpression pe) emit(pyExprToString(pe));
        else if (cs instanceof SimpleExpression se) emit(se.toString());
    }

    private void generateFunctionDef(FunctionDefinition fd) {
        if (fd.decorator != null) {
            emit(decoratorToString(fd.decorator));
        }
        StringBuilder sig = new StringBuilder("def ").append(fd.functionName).append("(");
        if (fd.functionParameters != null && fd.functionParameters.parameters != null) {
            for (int i = 0; i < fd.functionParameters.parameters.size(); i++) {
                if (i > 0) sig.append(", ");
                sig.append(fd.functionParameters.parameters.get(i).id);
            }
        }
        sig.append("):");
        emit(sig.toString());
        indent++;
        if (fd.functionBody != null) generateStatement(fd.functionBody);
        else emit("pass");
        indent--;
        out.append("\n");
    }

    private String decoratorToString(Decorator d) {
        StringBuilder sb = new StringBuilder("@").append(d.getDecoratorName());
        if (d.getArguments() != null) {
            sb.append("(").append(argsListToString(d.getArguments())).append(")");
        }
        return sb.toString();
    }

    private void generateClassDef(ClassDefinition cd) {
        StringBuilder sig = new StringBuilder("class ").append(cd.className);
        if (cd.baseClasses != null) {
            sig.append("(").append(argsListToString(cd.baseClasses)).append(")");
        }
        sig.append(":");
        emit(sig.toString());
        indent++;
        if (cd.classBody != null && cd.classBody.compoundStatements != null) {
            for (CompoundStatement cs : cd.classBody.compoundStatements) {
                generateCompound(cs);
            }
        } else {
            emit("pass");
        }
        indent--;
        out.append("\n");
    }

    private void generateImport(ImportStatement imp) {
        String module = imp.getModule();
        if (module == null || module.isEmpty()) {
            emit("# import (module name unavailable in AST)");
            return;
        }
        String adjustedModule = adjustModuleName(module);
        if (!imp.isFrom()) {
            // Regular import: import X[.Y][ as Z]
            if (imp.getImportedList() != null && !imp.getImportedList().isEmpty()) {
                Imported item = imp.getImportedList().get(0);
                if (item.getAlias() != null) {
                    emit("import " + adjustedModule + " as " + item.getAlias());
                } else {
                    emit("import " + adjustedModule);
                }
            }
            return;
        }
        // From import: from X import Y [as Z], ...
        StringBuilder s = new StringBuilder("from ").append(adjustedModule).append(" import ");
        if (imp.getImportedList() != null) {
            for (int i = 0; i < imp.getImportedList().size(); i++) {
                if (i > 0) s.append(", ");
                Imported item = imp.getImportedList().get(i);
                s.append(item.getName());
                if (item.getAlias() != null) {
                    s.append(" as ").append(item.getAlias());
                }
            }
        }
        emit(s.toString());
    }

    private String adjustModuleName(String module) {
        if (module == null) return null;
        int dotIndex = module.indexOf('.');
        String firstComponent = dotIndex >= 0 ? module.substring(0, dotIndex) : module;
        if (localModules.contains(firstComponent)) {
            String suffix = dotIndex >= 0 ? module.substring(dotIndex) : "";
            return firstComponent + "_generated" + suffix;
        }
        return module;
    }

    private void generateDelete(DeleteStatement ds) {
        String target = ds.getTarget() != null ? atomExprToString(ds.getTarget()) : "";
        emit("del " + target);
    }

    private void generateGlobal(GlobalStatement gs) {
        if (gs.getGlobals() != null) {
            for (String g : gs.getGlobals()) {
                emit("global " + g);
            }
        }
    }

    private void generateReturn(ReturnStatement rs) {
        StringBuilder s = new StringBuilder("return ");
        if (rs instanceof SimpleReturnStatement srs) {
            s.append(atomToString(srs.getAtom()));
        } else if (rs instanceof ComplexReturnStatement crs) {
            s.append(pyExprToString(crs.pythonExpression));
        } else if (rs instanceof ConditionReturnStatement crs) {
            s.append(condToPython(crs.getCondition()));
        }
        emit(s.toString());
    }

    private void generateAssignment(AssignmentStatement as) {
        String varStr = as.var != null ? pyExprToString(as.var) : "?";
        StringBuilder s = new StringBuilder(varStr).append(" = ");
        if (as instanceof PythonExpressionAssignStatement peas) {
            s.append(pyExprToString(peas.value));
        } else if (as instanceof ArithmeticAssignStatement aas) {
            s.append(arithExprToString(aas.value));
        } else if (as instanceof ComparisonAssignmentStmt cas) {
            s.append(condToPython(cas.value));
        } else if (as instanceof TemplateLiteralAssignmentStatement tlas) {
            if (tlas.templateLiteral != null && tlas.templateLiteral.getContent() != null) {
                s.append("\"\"\"").append(tlas.templateLiteral.getContent()).append("\"\"\"");
            } else {
                s.append("\"\"\"...\"\"\"");
            }
        }
        emit(s.toString());
    }

    private void generateForLoop(ForLoop fl) {
        String varName = fl.var != null ? String.valueOf(fl.var.getValue()) : "?";
        String iterExpr = fl.iter != null ? pyExprToString(fl.iter) : "?";
        emit("for " + varName + " in " + iterExpr + ":");
        indent++;
        if (fl.statement != null) generateStatement(fl.statement);
        else emit("pass");
        indent--;
        out.append("\n");
    }

    private void generateIfStatement(IfStatement ifs) {
        String cond = ifs.condition != null ? condToPython(ifs.condition) : "True";
        emit("if " + cond + ":");
        indent++;
        if (ifs.statement != null) generateStatement(ifs.statement);
        indent--;
        if (ifs.elifStatements != null) {
            for (ElIfStatement elif : ifs.elifStatements) {
                String c = elif.condition != null ? condToPython(elif.condition) : "True";
                emit("elif " + c + ":");
                indent++;
                if (elif.statement != null) generateStatement(elif.statement);
                indent--;
            }
        }
        if (ifs.elseStatement != null) {
            emit("else:");
            indent++;
            generateStatement(ifs.elseStatement);
            indent--;
        }
        out.append("\n");
    }

    private String pyExprToString(PythonExpression expr) {
        if (expr == null) return "None";
        if (expr instanceof AtomExpression ae) return atomExprToString(ae);
        if (expr instanceof ListLiteral ll) return listLitToString(ll);
        if (expr instanceof DictionaryLiteral dl) return dictLitToString(dl);
        if (expr instanceof ListComprehension lc) return listCompToString(lc);
        if (expr instanceof Generator gen) return generatorToString(gen);
        if (expr instanceof SetLiteral sl) return setLitToString(sl);
        return expr.toString();
    }

    private String setLitToString(SetLiteral sl) {
        StringBuilder sb = new StringBuilder("{");
        if (sl.items != null) {
            for (int i = 0; i < sl.items.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(sl.items.get(i) != null ? atomExprToString(sl.items.get(i)) : "");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String atomExprToString(AtomExpression ae) {
        if (ae == null) return "None";
        if (ae instanceof FStringAtomExpression fs) return "f\"" + fs.getRawValue() + "\"";
        if (ae instanceof SimpleVariable sv) return sv.getVarName();
        if (ae instanceof FunctionCall fc) return funcCallToString(fc);
        if (ae instanceof AttributeAccess aa) return attribAccessToString(aa);
        if (ae instanceof MethodAccess ma) return methodAccessToString(ma);
        if (ae instanceof ObjectCreation oc) return objCreateToString(oc);
        if (ae instanceof ListAccess la) return la.getVarName() + "[" + la.getIndex() + "]";
        if (ae instanceof DictionaryAccess da) return da.getVarName() + "[" + da.getKey() + "]";
        if (ae instanceof Subscript ss) return subscriptToString(ss);
        return ae.toString();
    }

    private String funcCallToString(FunctionCall fc) {
        return fc.getVarName() + "(" + argsListToString(fc.argumentsList) + ")";
    }

    private String attribAccessToString(AttributeAccess aa) {
        StringBuilder sb = new StringBuilder(aa.getVarName());
        if (aa.getAttributes() != null) {
            for (Atom a : aa.getAttributes()) {
                sb.append(".").append(a.getValue());
            }
        }
        return sb.toString();
    }

    private String methodAccessToString(MethodAccess ma) {
        StringBuilder sb = new StringBuilder(ma.getVarName() != null ? ma.getVarName() : "");
        if (ma.getMethodCalls() != null) {
            for (AtomExpression ae : ma.getMethodCalls()) {
                sb.append(".");
                if (ae instanceof FunctionCall fc) {
                    sb.append(fc.getVarName()).append("(").append(argsListToString(fc.argumentsList)).append(")");
                } else {
                    sb.append(atomExprToString(ae));
                }
            }
        }
        return sb.toString();
    }

    private String subscriptToString(Subscript ss) {
        String target = ss.getTarget() != null ? atomExprToString(ss.getTarget()) : "";
        String idx = ss.getIndex() != null ? ss.getIndex() : "";
        return target + "[" + idx + "]";
    }

    private String objCreateToString(ObjectCreation oc) {
        return oc.getVarName() + "(" + argsListToString(oc.getArgumentsList()) + ")";
    }

    private String argsListToString(ast.argsList.ArgumentsList al) {
        if (al == null) return "";
        StringBuilder sb = new StringBuilder();
        if (al instanceof AtomArguments aa && aa.getArgs() != null) {
            for (int i = 0; i < aa.getArgs().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(atomToString(aa.getArgs().get(i)));
            }
        } else if (al instanceof ComplexArguments ca && ca.getArguments() != null) {
            for (int i = 0; i < ca.getArguments().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(argToString(ca.getArguments().get(i)));
            }
        }
        return sb.toString();
    }

    private String argToString(Argument arg) {
        if (arg == null) return "";
        if (arg instanceof PositionalArgument pa) return pyExprToString(pa.getArg());
        if (arg instanceof KeywordArgument ka) {
            String val = ka.getArg() != null ? pyExprToString(ka.getArg()) : "None";
            return ka.argName + "=" + val;
        }
        return arg.toString();
    }

    private String listLitToString(ListLiteral ll) {
        StringBuilder sb = new StringBuilder("[");
        if (ll.listItems != null) {
            for (int i = 0; i < ll.listItems.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(atomExprToString(ll.listItems.get(i)));
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String dictLitToString(DictionaryLiteral dl) {
        StringBuilder sb = new StringBuilder("{");
        List<KeyValue> kvs = dl.getKeyValues();
        if (kvs != null) {
            for (int i = 0; i < kvs.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(keyValueToString(kvs.get(i)));
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String keyValueToString(KeyValue kv) {
        if (kv instanceof AtomKeyValue akv) {
            return atomToString(kv.getKey()) + " : " + atomToString(akv.getAtomValue());
        }
        if (kv instanceof SimpleKeyValue skv) {
            return atomToString(kv.getKey()) + " : " + simpleExprToString(skv.value);
        }
        return kv.toString();
    }

    private String simpleExprToString(SimpleExpression se) {
        if (se instanceof ArithmeticExpression ae) return arithExprToString(ae);
        return se.symbolTablePrint();
    }

    private String listCompToString(ListComprehension lc) {
        return forLoopToGeneratorString(lc.forLoop, "[", "]");
    }

    private String generatorToString(Generator gen) {
        return forLoopToGeneratorString(gen.forLoop, "(", ")");
    }

    private String forLoopToGeneratorString(ForLoop fl, String open, String close) {
        if (fl == null) return open + close;
        String varName = fl.var != null ? String.valueOf(fl.var.getValue()) : "?";
        String iterExpr = fl.iter != null ? pyExprToString(fl.iter) : "?";
        StringBuilder sb = new StringBuilder(open);
        sb.append(varName).append(" for ").append(varName).append(" in ").append(iterExpr);
        if (fl.condition != null) {
            sb.append(" if ").append(condToPython(fl.condition));
        }
        sb.append(close);
        return sb.toString();
    }

    private String condToPython(Condition cond) {
        if (cond == null) return "True";
        if (cond instanceof BooleanCondition bc) return bc.toString();
        if (cond instanceof NotExpression ne) {
            String inner = ne.condition != null ? condToPython(ne.condition) : "True";
            return "not " + inner;
        }
        if (cond instanceof ComparisonExpression ce) {
            StringBuilder sb = new StringBuilder();
            sb.append(pyExprToString(ce.baseExpr));
            if (ce.operatorPythonExpressionMap != null) {
                for (Map.Entry<ComparisonOperator, PythonExpression> entry : ce.operatorPythonExpressionMap.entrySet()) {
                    String op = entry.getKey() != null ? entry.getKey().toString() : "?";
                    String rhs = entry.getValue() != null ? pyExprToString(entry.getValue()) : "None";
                    sb.append(" ").append(op).append(" ").append(rhs);
                }
            }
            return sb.toString();
        }
        if (cond instanceof AndCondition ac) {
            String left = ac.left != null ? condToPython(ac.left) : "True";
            String right = ac.right != null ? condToPython(ac.right) : "True";
            return left + " and " + right;
        }
        if (cond instanceof OrCondition oc) {
            String left = oc.left != null ? condToPython(oc.left) : "True";
            String right = oc.right != null ? condToPython(oc.right) : "True";
            return left + " or " + right;
        }
        return cond.symbolTablePrint();
    }

    private String arithExprToString(ArithmeticExpression expr) {
        if (expr == null) return "None";
        String left = expr.left != null ? pyExprToString(expr.left) : "?";
        StringBuilder sb = new StringBuilder(left);
        String op = expr.operator != null ? expr.operator : "?";
        if (expr.right != null) {
            for (PythonExpression r : expr.right) {
                sb.append(" ").append(op).append(" ").append(pyExprToString(r));
            }
        }
        return sb.toString();
    }

    private String atomToString(Atom atom) {
        if (atom == null) return "None";
        Object val = atom.getValue();
        if (val == null) return "None";
        String s = val.toString();
        if (atom instanceof ast.atom.Str) {
            s = s.replaceAll("^[\"']+|[\"']+$", "");
            return "\"" + s.replace("\"", "\\\"") + "\"";
        }
        return s;
    }
}
