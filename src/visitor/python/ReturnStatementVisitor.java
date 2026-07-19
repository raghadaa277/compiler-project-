package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.atom.Atom;
import ast.compundStmt.PythonExpression;
import ast.returnStmt.ComplexReturnStatement;
import ast.returnStmt.ReturnStatement;
import ast.returnStmt.SimpleReturnStatement;

public class ReturnStatementVisitor extends PythonParserBaseVisitor<ReturnStatement> {
    @Override
    public ReturnStatement visitComplexReturn(PythonParser.ComplexReturnContext ctx) {
        ComplexReturnStatement complexReturnStatement = new ComplexReturnStatement(ctx.getStart().getLine());
        PythonExpression pythonExpression = new PythonExpressionVisitor().visit(ctx.python_expr());
        complexReturnStatement.setPythonExpression(pythonExpression);
        return complexReturnStatement;
    }

    @Override
    public ReturnStatement visitSimpleReturn(PythonParser.SimpleReturnContext ctx) {
        SimpleReturnStatement statement = new SimpleReturnStatement(ctx.getStart().getLine());
        Atom atom = new AtomVisitor().visit(ctx.atom());
        statement.setAtom(atom);
        return statement;
    }
}
