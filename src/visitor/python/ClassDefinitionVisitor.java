package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.compundStmt.ClassDefinition;
import ast.Statement;
import ast.argsList.ArgumentsList;
import ast.functionDef.Decorator;

import java.util.ArrayList;
import java.util.List;

public class ClassDefinitionVisitor extends PythonParserBaseVisitor<ClassDefinition> {

    @Override
    public ClassDefinition visitClass_def(PythonParser.Class_defContext ctx) {
        ClassDefinition classDef = new ClassDefinition(ctx.getStart().getLine());

        if (ctx.NAME() != null) {
            classDef.setClassName(ctx.NAME().getText());
        } else if (ctx.CLASS_NAME() != null) {
            classDef.setClassName(ctx.CLASS_NAME().getText());
        }

        if (ctx.arglist() != null) {
            ArgumentsList args = new ArgumentListVisitor().visit(ctx.arglist());
            classDef.setBaseClasses(args);
        }

        StatementVisitor stmtVisitor = new StatementVisitor();
        if (ctx.suite() != null) {
            classDef.setClassBody(stmtVisitor.visit(ctx.suite()));
        }

        return classDef;
    }
}
