package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.ElIfStatement;
import ast.Imported;
import ast.Statement;
import ast.atom.Atom;
import ast.compundStmt.*;
import ast.condition.Condition;
import ast.functionDef.FunctionDefinition;
import ast.functionDef.FunctionParameters;
import org.antlr.v4.runtime.tree.TerminalNode;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTableManager;
import visitor.UniversalPythonVisitor;

import java.util.ArrayList;
import java.util.List;

public class CompoundStatementVisitor extends PythonParserBaseVisitor<CompoundStatement> {

    UniversalPythonVisitor universalVisitor = new UniversalPythonVisitor();
    SymbolTableManager stm = SymbolTableManager.INSTANCE;

    private final AssignmentStatementVisitor assignmentStatementVisitor = new AssignmentStatementVisitor();
    private final ConditionVisitor conditionVisitor = new ConditionVisitor();
    private final AtomVisitor atomVisitor = new AtomVisitor();
    private final PythonExpressionVisitor pythonExpressionVisitor = new PythonExpressionVisitor();

    private StatementVisitor sharedStatementVisitor;

    public CompoundStatementVisitor(StatementVisitor statementVisitor) {
        this.sharedStatementVisitor = statementVisitor;
    }

    private void registerSymbol(String name, String type, Object value) {
        SymbolEntry entry = stm.insert(name);
        if (entry != null) {
            entry.setAttribute("Type", type);
            entry.setAttribute("Value", value);
        }
    }

    @Override
    public CompoundStatement visitAtomExpression(PythonParser.AtomExpressionContext ctx) {
        return new AtomExpressionVisitor().visit(ctx.atom_expr());
    }

    @Override
    public CompoundStatement visitSimpleExpression(PythonParser.SimpleExpressionContext ctx) {
        return new SimpleExpressionVisitor().visit(ctx.simple_expr());
    }

    @Override
    public CompoundStatement visitIfStatement(PythonParser.IfStatementContext ctx) {
        return visit(ctx.if_stmt());
    }

    @Override
    public CompoundStatement visitIfStatementDef(PythonParser.IfStatementDefContext ctx) {
        IfStatement ifStatement = new IfStatement(ctx.getStart().getLine());
        List<ElIfStatement> elIfStatements = new ArrayList<>();

        int stmtIndex = 0;
        int conditionIndex = 0;

        Condition ifCondition = conditionVisitor.visit(ctx.condition(conditionIndex++));
        ifStatement.setCondition(ifCondition);

        stm.enterScope("If_Block_Line_" + ctx.getStart().getLine());

        List<Statement> ifBody = new ArrayList<>();

        ifBody.add(this.sharedStatementVisitor.visit(ctx.statement(stmtIndex++)));

        while (stmtIndex < ctx.statement().size() && isStatementBeforeNextBlock(ctx, stmtIndex)) {
            ifBody.add(this.sharedStatementVisitor.visit(ctx.statement(stmtIndex++)));
        }
        stm.exitScope();

        ifStatement.setStatement(ifBody.isEmpty() ? null : ifBody.get(0));

        int elifCount = ctx.ELIF().size();
        for (int i = 0; i < elifCount; i++) {
            ElIfStatement elIfStatement = new ElIfStatement(ctx.ELIF(i).getSymbol().getLine());
            Condition elifCond = conditionVisitor.visit(ctx.condition(conditionIndex++));
            elIfStatement.setCondition(elifCond);

            stm.enterScope("Elif_Block_Line_" + ctx.ELIF(i).getSymbol().getLine());
            List<Statement> elifBody = new ArrayList<>();

            elifBody.add(this.sharedStatementVisitor.visit(ctx.statement(stmtIndex++)));


            while (stmtIndex < ctx.statement().size() && isStatementBeforeNextBlock(ctx, stmtIndex)) {
                elifBody.add(this.sharedStatementVisitor.visit(ctx.statement(stmtIndex++)));
            }
            stm.exitScope();

            elIfStatement.setStatement(elifBody.isEmpty() ? null : elifBody.get(0));
            elIfStatements.add(elIfStatement);
        }
        ifStatement.setElifStatements(elIfStatements);

        if (ctx.ELSE() != null) {
            stm.enterScope("Else_Block_Line_" + ctx.ELSE().getSymbol().getLine());
            List<Statement> elseBody = new ArrayList<>();


            while (stmtIndex < ctx.statement().size()) {
                elseBody.add(this.sharedStatementVisitor.visit(ctx.statement(stmtIndex++)));
            }
            stm.exitScope();

            ifStatement.setElseStatement(elseBody.isEmpty() ? null : elseBody.get(0));
        }

        return ifStatement;
    }

    private boolean isStatementBeforeNextBlock(PythonParser.IfStatementDefContext ctx, int stmtIndex) {

        int stmtTokenIndex = ctx.statement(stmtIndex).getStart().getTokenIndex();

        for (var elifToken : ctx.ELIF()) {
            if (stmtTokenIndex > elifToken.getSymbol().getTokenIndex()) {
                continue;
            }
            return stmtTokenIndex < elifToken.getSymbol().getTokenIndex();
        }

        if (ctx.ELSE() != null) {
            return stmtTokenIndex < ctx.ELSE().getSymbol().getTokenIndex();
        }

        return true;
    }



    @Override
    public CompoundStatement visitAssignmentStatement(PythonParser.AssignmentStatementContext ctx) {
        return assignmentStatementVisitor.visit(ctx.assign_stmt());
    }

    @Override
    public CompoundStatement visitFunctionDefinition(PythonParser.FunctionDefinitionContext ctx) {
        return visit(ctx.func_def());
    }


    @Override
    public CompoundStatement visitFunctionDefDef(PythonParser.FunctionDefDefContext ctx) {
        FunctionDefinition functionDefinition = new FunctionDefinition(ctx.getStart().getLine());
        String funcName = ctx.NAME().getText();

        registerSymbol(funcName, "Function", "Defined at line " + ctx.getStart().getLine());
        functionDefinition.setFunctionName(funcName);

        stm.enterScope("Function_Scope_" + funcName);

        if (ctx.parameters() != null) {
            var paramsContext = ctx.parameters().getChild(1);

            if (paramsContext instanceof PythonParser.PositionalParamsContext) {
                PythonParser.PositionalParamsContext positional = (PythonParser.PositionalParamsContext) paramsContext;
                for (org.antlr.v4.runtime.tree.TerminalNode param : positional.NAME()) {
                    registerSymbol(param.getText(), "Parameter", "Positional");
                }
            } else if (paramsContext instanceof PythonParser.KeywordParamsContext) {
                PythonParser.KeywordParamsContext keyword = (PythonParser.KeywordParamsContext) paramsContext;
                for (org.antlr.v4.runtime.tree.TerminalNode param : keyword.NAME()) {
                    registerSymbol(param.getText(), "Parameter", "Keyword");
                }
            }
        }

        FunctionParameters functionParameters = (FunctionParameters) universalVisitor.visit(ctx.parameters());
        System.out.println("Entering Function: " + funcName + " | Current Scope: " + stm.toString());

        Statement functionBodyWrapper = new Statement(ctx.getStart().getLine());
        List<CompoundStatement> compoundStatementList = new ArrayList<>();

        for (PythonParser.StatementContext stmtCtx : ctx.statement()) {
            Object visitedResult = this.sharedStatementVisitor.visit(stmtCtx);

            if (visitedResult == null) {
                continue;
            }

            if (visitedResult instanceof Statement) {
                Statement childStatement = (Statement) visitedResult;
                if (childStatement.getCompoundStatements() != null) {
                    compoundStatementList.addAll(childStatement.getCompoundStatements());
                }
            } else if (visitedResult instanceof CompoundStatement) {
                compoundStatementList.add((CompoundStatement) visitedResult);
            }
        }

        System.out.println("Exiting Function: " + funcName + " | Current Scope Before Exit: " + stm.toString());
        stm.exitScope();

        functionBodyWrapper.setCompoundStatements(compoundStatementList);
        functionDefinition.setFunctionParameters(functionParameters);
        functionDefinition.setFunctionBody(functionBodyWrapper);

        return functionDefinition;
    }



    @Override
    public CompoundStatement visitReturnStatement(PythonParser.ReturnStatementContext ctx) {
        return new ReturnStatementVisitor().visit(ctx.return_stmt());
    }

    @Override
    public CompoundStatement visitImportStatement(PythonParser.ImportStatementContext ctx) {
        return visit(ctx.import_from());
    }

    @Override
    public ImportStatement visitImportFromDef(PythonParser.ImportFromDefContext ctx) {
        ImportStatement importStatement = new ImportStatement(ctx.getStart().getLine());

        StringBuilder moduleBuilder = new StringBuilder();
        List<TerminalNode> names = ctx.NAME();

        int modulePartsCount = names.size() - ctx.imptd().size();
        for (int i = 0; i < modulePartsCount; i++) {
            if (i > 0) moduleBuilder.append(".");
            moduleBuilder.append(names.get(i).getText());
        }
        String module = moduleBuilder.toString();

        List<Imported> importedList = new ArrayList<>();
        for (PythonParser.ImptdContext importedCtx : ctx.imptd()) {
            Imported importedNode = (Imported) universalVisitor.visit(importedCtx);
            importedList.add(importedNode);

            registerSymbol(importedNode.toString(), "Imported", "From " + module);
        }

        importStatement.setImportedList(importedList);
        importStatement.setModule(module);
        return importStatement;
    }

    @Override
    public CompoundStatement visitGlobalStatement(PythonParser.GlobalStatementContext ctx) {
        return (CompoundStatement) universalVisitor.visit(ctx.global_stmt());
    }

    @Override
    public CompoundStatement visitSimpleForLoop(PythonParser.SimpleForLoopContext ctx) {
        ForLoop node = new ForLoop(ctx.getStart().getLine());

        Atom varAtom = atomVisitor.visit(ctx.atom());
        PythonExpression iter = pythonExpressionVisitor.visit(ctx.python_expr());

        node.setVar(varAtom);
        node.setIter(iter);

        stm.enterScope("For_Loop_Line_" + ctx.getStart().getLine());
        String loopVarName = varAtom.symbolTablePrint();
        registerSymbol(loopVarName, "LoopVariable", "Dynamic");

        Statement loopBodyWrapper = new Statement(ctx.getStart().getLine());
        List<CompoundStatement> compoundStatementList = new ArrayList<>();

        for (PythonParser.StatementContext stmtCtx : ctx.statement()) {
            Object visitedResult = this.sharedStatementVisitor.visit(stmtCtx);
            if (visitedResult == null) continue;

            if (visitedResult instanceof Statement) {
                Statement childStatement = (Statement) visitedResult;
                if (childStatement.getCompoundStatements() != null) {
                    compoundStatementList.addAll(childStatement.getCompoundStatements());
                }
            } else if (visitedResult instanceof CompoundStatement) {
                compoundStatementList.add((CompoundStatement) visitedResult);
            }
        }

        stm.exitScope();

        loopBodyWrapper.setCompoundStatements(compoundStatementList);
        node.setBody(loopBodyWrapper);

        return node;
    }
}