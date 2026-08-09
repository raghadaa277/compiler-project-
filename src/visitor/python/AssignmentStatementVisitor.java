package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.TemplateLiteral;
import ast.arithmeticExpr.ArithmeticExpression;
import ast.assignStmt.*;
import ast.compundStmt.PythonExpression;
import ast.condition.Condition;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTableManager;

public class AssignmentStatementVisitor extends PythonParserBaseVisitor<AssignmentStatement> {


    private final PythonExpressionVisitor pythonExpressionVisitor = new PythonExpressionVisitor();
    private final SymbolTableManager stm = SymbolTableManager.INSTANCE;

    private String extractVarName(PythonExpression expr) {

        if (expr == null) return null;

        if (expr instanceof ast.atomExpression.SimpleVariable) {
            return ((ast.atomExpression.SimpleVariable) expr).getVarName();
        }

        if (expr instanceof ast.atomExpression.ListAccess) {
            return ((ast.atomExpression.ListAccess) expr).getVarName();
        }

        if (expr instanceof ast.atomExpression.DictionaryAccess) {
            return ((ast.atomExpression.DictionaryAccess) expr).getVarName();
        }

        if (expr instanceof ast.atomExpression.AttributeAccess) {
            return ((ast.atomExpression.AttributeAccess) expr).getVarName();
        }

        if (expr instanceof ast.atomExpression.FunctionCall) {
            return ((ast.atomExpression.FunctionCall) expr).getVarName();
        }

        if (expr instanceof ast.atomExpression.MethodAccess) {
            return ((ast.atomExpression.MethodAccess) expr).getVarName();
        }

        if (expr instanceof ast.atomExpression.ObjectCreation) {
            return ((ast.atomExpression.ObjectCreation) expr).getVarName();
        }

        return null;
    }
    private void updateOrInsertSymbol(String name, String type, Object value) {
        if (name == null || name.isEmpty()) return;


        SymbolEntry entry = stm.lookup(name);

        if (entry == null) {

            entry = stm.insert(name);
        }

        if (entry != null) {
            entry.setAttribute("Type", type);
            entry.setAttribute("Value", value);
        }
    }

    @Override
    public AssignmentStatement visitComparisonAssignStmt(PythonParser.ComparisonAssignStmtContext ctx) {
        ComparisonAssignmentStmt node = new ComparisonAssignmentStmt(ctx.getStart().getLine());

        PythonExpression var = pythonExpressionVisitor.visit(ctx.python_expr());
        Condition value = new ConditionVisitor().visit(ctx.condition());

        node.setVar(var);
        node.setValue(value);


        String name = extractVarName(var);

        updateOrInsertSymbol(name, "Variable", value.symbolTablePrint());

        return node;
    }

    @Override
    public AssignmentStatement visitTemplateLiteralAssignStmt(PythonParser.TemplateLiteralAssignStmtContext ctx) {
        TemplateLiteralAssignmentStatement node = new TemplateLiteralAssignmentStatement(ctx.getStart().getLine());

        PythonExpression var = pythonExpressionVisitor.visit(ctx.python_expr());
        TemplateLiteral value = new TemplateLiteralVisitor().visit(ctx.template_literal());

        node.setVar(var);
        node.setTemplateLiteral(value);

        String name = extractVarName(var);

        updateOrInsertSymbol(name, "Variable", value.node_name);
        return node;
    }

    @Override
    public AssignmentStatement visitPythonExpressionAssignStmt(PythonParser.PythonExpressionAssignStmtContext ctx) {
        PythonExpressionAssignStatement node = new PythonExpressionAssignStatement(ctx.getStart().getLine());

        PythonExpression var = pythonExpressionVisitor.visit(ctx.python_expr(0));
        PythonExpression value = pythonExpressionVisitor.visit(ctx.python_expr(1));

        node.setVar(var);
        node.setValue(value);


        String type = (value.node_name != null) ? value.node_name : "Expression";


        String name = extractVarName(var);

        updateOrInsertSymbol(name, "Variable", value.node_name);

        return node;
    }

    @Override
    public AssignmentStatement visitArithmeticAssignStmt(PythonParser.ArithmeticAssignStmtContext ctx) {
        ArithmeticAssignStatement node = new ArithmeticAssignStatement(ctx.getStart().getLine());

        PythonExpression var = pythonExpressionVisitor.visit(ctx.python_expr());
        ArithmeticExpression value = new ArithmeticExpressionVisitor().visit(ctx.arithmetic_expr());

        node.setVar(var);
        node.setValue(value);


        String name = extractVarName(var);

        updateOrInsertSymbol(name, "Variable", value.node_name);

        return node;
    }
}