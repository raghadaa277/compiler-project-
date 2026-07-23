package semantics;

import ast.Program;
import ast.Statement;
import ast.atom.Atom;
import ast.atom.Str;
import ast.atomExpression.FunctionCall;
import ast.atomExpression.SimpleVariable;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.argument.Argument;
import ast.argument.KeywordArgument;
import ast.argument.PositionalArgument;
import ast.compundStmt.CompoundStatement;
import ast.functionDef.FunctionDefinition;
import ast.returnStmt.ComplexReturnStatement;
import ast.returnStmt.ReturnStatement;

import java.util.*;

public class TemplateVariableChecker {

    private static final Map<String, Set<String>> templateToVars = new HashMap<>();

    public static void reset() {
        templateToVars.clear();
    }

    public static void collectRenderTemplate(Program program, String filePath) {
        if (program == null || program.statements == null) return;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                if (cs instanceof FunctionDefinition fd) {
                    collectFromFunction(fd);
                }
            }
        }
    }

    private static void collectFromFunction(FunctionDefinition fd) {
        if (fd.functionBody == null || fd.functionBody.compoundStatements == null) return;
        for (CompoundStatement cs : fd.functionBody.compoundStatements) {
            if (cs instanceof ReturnStatement rs) {
                collectFromReturn(rs);
            }
        }
    }

    private static void collectFromReturn(ReturnStatement rs) {
        if (!(rs instanceof ComplexReturnStatement crs)) return;
        if (!(crs.pythonExpression instanceof FunctionCall fc)) return;
        if (!"render_template".equals(fc.getVarName())) return;

        String templateName = null;
        Set<String> varNames = new HashSet<>();

        if (fc.argumentsList instanceof AtomArguments aa) {
            if (aa.getArgs() != null && !aa.getArgs().isEmpty()) {
                Atom first = aa.getArgs().get(0);
                if (first instanceof Str && first.getValue() instanceof String s) {
                    templateName = s.replace("\"", "").replace("'", "");
                }
            }
        } else if (fc.argumentsList instanceof ComplexArguments ca) {
            if (ca.getArguments() != null && !ca.getArguments().isEmpty()) {
                Argument first = ca.getArguments().get(0);
                if (first instanceof PositionalArgument pa) {
                    templateName = extractTemplateNameFromExpr(pa.getArg());
                }
                for (int i = 1; i < ca.getArguments().size(); i++) {
                    Argument arg = ca.getArguments().get(i);
                    if (arg instanceof KeywordArgument kw) {
                        varNames.add(kw.argName);
                    }
                }
            }
        }

        if (templateName != null && !templateName.isEmpty()) {
            templateToVars.merge(templateName, varNames, (old, neu) -> {
                old.addAll(neu);
                return old;
            });
        }
    }

    private static String extractTemplateNameFromExpr(ast.compundStmt.PythonExpression expr) {
        if (expr instanceof SimpleVariable sv) {
            return sv.getVarName().replace("\"", "").replace("'", "");
        }
        return null;
    }

    public static Set<String> getVarsForTemplate(String templateFileName) {
        for (var entry : templateToVars.entrySet()) {
            if (entry.getKey().equals(templateFileName)) {
                return entry.getValue();
            }
        }
        return Collections.emptySet();
    }

    public static Map<String, Set<String>> getTemplateVars() {
        return templateToVars;
    }
}
