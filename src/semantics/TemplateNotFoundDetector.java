package semantics;

import ast.*;
import ast.argsList.AtomArguments;
import ast.atom.Atom;
import ast.atomExpression.*;
import ast.compundStmt.*;
import ast.functionDef.FunctionDefinition;
import ast.returnStmt.ComplexReturnStatement;
import ast.returnStmt.ReturnStatement;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TemplateNotFoundDetector implements ErrorDetector {

    private final List<SemanticError> errors = new ArrayList<>();
    private final Set<String> templatesRendered = new HashSet<>();
    private String baseDir = "";
    private String filePath = "";

    @Override
    public void reset() {
        errors.clear();
        templatesRendered.clear();
    }

    @Override
    public void detect(Program program) {
        if (program == null) return;
        for (Statement stmt : program.statements) {
            if (stmt == null || stmt.isPass || stmt.compoundStatements == null) continue;
            for (CompoundStatement cs : stmt.compoundStatements) {
                analyzeCompoundStatement(cs);
            }
        }
        checkTemplatesExist();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.baseDir = new File(filePath).getParent();
    }

    private void analyzeCompoundStatement(CompoundStatement cs) {
        if (cs == null) return;
        if (cs instanceof FunctionDefinition fd) {
            if (fd.functionBody != null) analyzeStatement(fd.functionBody);
        } else if (cs instanceof IfStatement is) {
            if (is.statement != null) analyzeStatement(is.statement);
            if (is.elifStatements != null) {
                for (ElIfStatement elif : is.elifStatements) {
                    if (elif.statement != null) analyzeStatement(elif.statement);
                }
            }
            if (is.elseStatement != null) analyzeStatement(is.elseStatement);
        } else if (cs instanceof ForLoop fl) {
            if (fl.statement != null) analyzeStatement(fl.statement);
        } else if (cs instanceof ReturnStatement rs) {
            if (rs instanceof ComplexReturnStatement crs && crs.pythonExpression != null) {
                PythonExpression expr = crs.pythonExpression;
                if (expr instanceof AtomExpression ae) analyzeAtomExpression(ae);
            }
        } else if (cs instanceof AtomExpression ae) {
            analyzeAtomExpression(ae);
        }
    }

    private void analyzeStatement(Statement stmt) {
        if (stmt.isPass || stmt.compoundStatements == null) return;
        for (CompoundStatement cs : stmt.compoundStatements) {
            analyzeCompoundStatement(cs);
        }
    }

    private void analyzeAtomExpression(AtomExpression ae) {
        if (ae instanceof FunctionCall fc) {
            String funcName = fc.getVarName();
            if ("render_template".equals(funcName)) {
                String templateName = null;
                if (fc.argumentsList instanceof AtomArguments aa) {
                    if (aa.getArgs() != null && !aa.getArgs().isEmpty()) {
                        Atom first = aa.getArgs().get(0);
                        if (first != null && first.getValue() instanceof String s) {
                            templateName = s.replace("\"", "").replace("'", "");
                        }
                    }
                }
                if (templateName != null) {
                    templatesRendered.add(templateName);
                }
            }
        }
    }

    private void checkTemplatesExist() {
        for (String tmpl : templatesRendered) {
            boolean found = false;
            String[] pathsToCheck = {
                    baseDir + File.separator + tmpl,
                    baseDir + File.separator + "templates" + File.separator + tmpl,
                    filePath.substring(0, filePath.lastIndexOf(File.separator) + 1) + "templates" + File.separator + tmpl
            };
            for (String p : pathsToCheck) {
                if (Files.exists(Paths.get(p))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                errors.add(new SemanticError(-1,
                        "Semantic Error : Template '" + tmpl + "' not found. Checked: " + pathsToCheck[0] + ", " + pathsToCheck[1]));
            }
        }
    }

    @Override
    public List<SemanticError> getErrors() {
        return errors;
    }
}
