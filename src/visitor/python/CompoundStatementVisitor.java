package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.ElIfStatement;
import ast.Imported;
import ast.Statement;
import ast.atom.Atom;
import ast.atomExpression.AtomExpression;
import ast.compundStmt.*;
import ast.condition.Condition;
import ast.functionDef.Decorator;
import ast.functionDef.FunctionDefinition;
import ast.functionDef.FunctionParameters;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTableManager;
import visitor.UniversalPythonVisitor;

import java.util.ArrayList;
import java.util.List;

public class CompoundStatementVisitor extends PythonParserBaseVisitor<CompoundStatement> {

    UniversalPythonVisitor universalVisitor = new UniversalPythonVisitor();
    SymbolTableManager stm = SymbolTableManager.INSTANCE;


    private StatementVisitor sharedStatementVisitor;

    public CompoundStatementVisitor(StatementVisitor statementVisitor) {
        this.sharedStatementVisitor = new StatementVisitor(this);
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
    public CompoundStatement visitBlankStatement(PythonParser.BlankStatementContext ctx) {
        return null;
    }

    @Override
    public CompoundStatement visitPassStatement(PythonParser.PassStatementContext ctx) {
        return null;
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
        ConditionVisitor conditionVisitor = new ConditionVisitor();
        StatementVisitor statementVisitor = new StatementVisitor();

        if (ctx.condition(0) != null) {
            Condition condition = conditionVisitor.visit(ctx.condition(0));
            ifStatement.setCondition(condition);
        }

        stm.enterScope("If_Block_Line_" + ctx.getStart().getLine());

        if (ctx.suite(0) != null) {
            Statement statement = statementVisitor.visit(ctx.suite(0));
            ifStatement.setStatement(statement);
        }
        stm.exitScope();

        int elifCount = ctx.ELIF().size();
        List<ElIfStatement> elIfStatements = new ArrayList<>();
        for (int i = 0; i < elifCount; i++) {
            ElIfStatement elIfStatement = new ElIfStatement(ctx.ELIF(i).getSymbol().getLine());
            if (ctx.condition(i + 1) != null) {
                Condition elifCond = conditionVisitor.visit(ctx.condition(i + 1));
                elIfStatement.setCondition(elifCond);
            }

            stm.enterScope("Elif_Block_Line_" + ctx.ELIF(i).getSymbol().getLine());
            if (ctx.suite(i + 1) != null) {
                Statement elifStmt = statementVisitor.visit(ctx.suite(i + 1));
                elIfStatement.setStatement(elifStmt);
            }
            stm.exitScope();

            elIfStatements.add(elIfStatement);
        }
        ifStatement.setElifStatements(elIfStatements);

        if (ctx.ELSE() != null) {
            int elseStmtIndex = ctx.suite().size() - 1;
            stm.enterScope("Else_Block_Line_" + ctx.ELSE().getSymbol().getLine());
            if (elseStmtIndex >= 0 && ctx.suite(elseStmtIndex) != null) {
                Statement elseStmt = statementVisitor.visit(ctx.suite(elseStmtIndex));
                ifStatement.setElseStatement(elseStmt);
            }
            stm.exitScope();
        }

        return ifStatement;
    }

    @Override
    public CompoundStatement visitAssignmentStatement(PythonParser.AssignmentStatementContext ctx) {

        return new AssignmentStatementVisitor().visit(ctx.assign_stmt());
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

        // Handle optional decorator
        if (ctx.dec() != null) {
            Decorator decorator = (Decorator) universalVisitor.visit(ctx.dec());
            functionDefinition.setDecorator(decorator);
        }

        stm.enterScope("Function_Scope_" + funcName);


        if (ctx.parameters() != null) {
            var paramsContext = ctx.parameters().getChild(1);

            if (paramsContext instanceof PythonParser.MixedParamsContext) {
                PythonParser.MixedParamsContext mixed = (PythonParser.MixedParamsContext) paramsContext;
                for (org.antlr.v4.runtime.tree.TerminalNode param : mixed.NAME()) {
                    registerSymbol(param.getText(), "Parameter", "Mixed");
                }
            }
            else if (paramsContext instanceof PythonParser.KeywordParamsContext) {
                PythonParser.KeywordParamsContext keyword = (PythonParser.KeywordParamsContext) paramsContext;
                for (org.antlr.v4.runtime.tree.TerminalNode param : keyword.NAME()) {
                    registerSymbol(param.getText(), "Parameter", "Keyword");
                }
            }
        }

        FunctionParameters functionParameters = (FunctionParameters) universalVisitor.visit(ctx.parameters());
        System.out.println("Entering Function: " + funcName + " | Current Scope Before Exit: " + stm.toString());
        Statement statement = ctx.suite() != null ? new StatementVisitor().visit(ctx.suite()) : null;
        System.out.println("Exiting Function: " + funcName + " | Current Scope Before Exit: " + stm.toString());
        stm.exitScope();

        functionDefinition.setFunctionParameters(functionParameters);
        functionDefinition.setFunctionBody(statement);

        return functionDefinition;
    }

    @Override
    public CompoundStatement visitClassDefinition(PythonParser.ClassDefinitionContext ctx) {
        return visit(ctx.class_def());
    }

    @Override
    public CompoundStatement visitClass_def(PythonParser.Class_defContext ctx) {
        ClassDefinition classDef = new ClassDefinition(ctx.getStart().getLine());

        String className;
        if (ctx.NAME() != null) {
            className = ctx.NAME().getText();
        } else if (ctx.CLASS_NAME() != null) {
            className = ctx.CLASS_NAME().getText();
        } else {
            className = "Unknown";
        }
        classDef.setClassName(className);

        if (ctx.arglist() != null) {
            classDef.setBaseClasses(new ArgumentListVisitor().visit(ctx.arglist()));
        }

        registerSymbol(className, "Class", "Defined at line " + ctx.getStart().getLine());

        stm.enterScope("Class_Scope_" + className);

        if (ctx.suite() != null) {
            classDef.setClassBody(new StatementVisitor().visit(ctx.suite()));
        }

        stm.exitScope();

        return classDef;
    }

    @Override
    public CompoundStatement visitReturnStatement(PythonParser.ReturnStatementContext ctx) {
        return new ReturnStatementVisitor().visit(ctx.return_stmt());
    }

    @Override
    public CompoundStatement visitDeleteStatement(PythonParser.DeleteStatementContext ctx) {
        return visit(ctx.del_stmt());
    }

    @Override
    public DeleteStatement visitDelDef(PythonParser.DelDefContext ctx) {
        DeleteStatement ds = new DeleteStatement(ctx.getStart().getLine());
        AtomExpression target = new AtomExpressionVisitor().visit(ctx.atom_expr());
        ds.setTarget(target);
        return ds;
    }

    @Override
    public CompoundStatement visitTryStatement(PythonParser.TryStatementContext ctx) {
        return visit(ctx.try_stmt());
    }

    @Override
    public TryStatement visitTryExceptDef(PythonParser.TryExceptDefContext ctx) {
        TryStatement ts = new TryStatement(ctx.getStart().getLine());
        if (ctx.suite(0) != null) {
            ts.setTryBody(new StatementVisitor().visit(ctx.suite(0)));
        }
        List<ExceptClause> excepts = new ArrayList<>();
        for (PythonParser.Except_clauseContext ecCtx : ctx.except_clause()) {
            if (!(ecCtx instanceof PythonParser.ExceptClauseDefContext ec)) continue;
            ExceptClause clause = new ExceptClause();
            if (ec.atom() != null) {
                clause.setExceptionType(new AtomVisitor().visit(ec.atom()));
            }
            if (ec.NAME() != null) {
                clause.setAlias(ec.NAME().getText());
            }
            if (ec.suite() != null) {
                clause.setBody(new StatementVisitor().visit(ec.suite()));
            }
            excepts.add(clause);
        }
        ts.setExceptClauses(excepts);
        int stmtCount = ctx.suite().size();
        int exceptCount = ctx.except_clause().size();
        if (stmtCount > exceptCount + 1) {
            int elseIdx = stmtCount - (ctx.FINALLY() != null ? 2 : 1);
            if (ctx.ELSE() != null && elseIdx >= 0 && elseIdx < stmtCount) {
                ts.setElseBody(new StatementVisitor().visit(ctx.suite(elseIdx)));
            }
        }
        if (ctx.FINALLY() != null) {
            ts.setFinallyBody(new StatementVisitor().visit(ctx.suite(stmtCount - 1)));
        }
        return ts;
    }

    @Override
    public CompoundStatement visitImportStatement(PythonParser.ImportStatementContext ctx) {
        return visit(ctx.import_stmt());
    }
    @Override
    public CompoundStatement visitImportFromStatement(PythonParser.ImportFromStatementContext ctx) {
        return visit(ctx.import_from());
    }
    @Override
    public ImportStatement visitImportDef(PythonParser.ImportDefContext ctx) {
        ImportStatement importStatement = new ImportStatement(ctx.getStart().getLine());
        List<Imported> importedList = new ArrayList<>();
        // Grammar: IMPORT NAME (DOT NAME)* (AS NAME)?
        StringBuilder moduleBuilder = new StringBuilder();
        int nameCount = ctx.NAME().size();
        boolean hasAs = ctx.AS() != null;
        int aliasIndex = -1;
        if (hasAs) {
            aliasIndex = nameCount - 1;
        }
        for (int i = 0; i < nameCount; i++) {
            String part = ctx.NAME(i).getText();
            if (hasAs && i == aliasIndex) break;
            if (moduleBuilder.length() > 0) moduleBuilder.append(".");
            moduleBuilder.append(part);
        }
        importStatement.setModule(moduleBuilder.toString());
        if (hasAs) {
            Imported imp = new Imported(ctx.getStart().getLine());
            String baseName = moduleBuilder.toString();
            String alias = ctx.NAME(aliasIndex).getText();
            imp.setName(baseName);
            imp.setAlias(alias);
            importedList.add(imp);
            registerSymbol(alias, "Imported", "As " + baseName);
        } else {
            Imported imp = new Imported(ctx.getStart().getLine());
            imp.setName(moduleBuilder.toString());
            importedList.add(imp);
            registerSymbol(moduleBuilder.toString(), "Imported", "Direct");
        }
        importStatement.setImportedList(importedList);
        return importStatement;
    }
    @Override
    public ImportStatement visitImportFromDef(PythonParser.ImportFromDefContext ctx) {
        ImportStatement importStatement = new ImportStatement(ctx.getStart().getLine());

        // Walk children between FROM and IMPORT to build module path
        // Grammar: FROM NAME (DOT NAME)* IMPORT imptd (COMMA imptd)*
        StringBuilder moduleBuilder = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode tn) {
                int type = tn.getSymbol().getType();
                if (type == PythonParser.IMPORT) {
                    break;
                }
                if (type == PythonParser.NAME) {
                    if (moduleBuilder.length() > 0) moduleBuilder.append(".");
                    moduleBuilder.append(tn.getText());
                }
            }
        }
        String module = moduleBuilder.toString();

        List<Imported> importedList = new ArrayList<>();
        for (PythonParser.ImptdContext importedCtx : ctx.imptd()) {
            Imported importedNode = (Imported) universalVisitor.visit(importedCtx);
            importedList.add(importedNode);

            registerSymbol(importedNode.getName(), "Imported", "From " + module);
        }

        importStatement.setImportedList(importedList);
        importStatement.setModule(module);
        importStatement.setFrom(true);
        return importStatement;
    }

    @Override
    public CompoundStatement visitGlobalStatement(PythonParser.GlobalStatementContext ctx) {
        return (CompoundStatement) universalVisitor.visit(ctx.global_stmt());
    }

    @Override
    public CompoundStatement visitSimpleForLoop(PythonParser.SimpleForLoopContext ctx) {
        ForLoop node = new ForLoop(ctx.getStart().getLine());

        if (ctx.atom() != null) {
            Atom varAtom = new AtomVisitor().visit(ctx.atom());
            node.setVar(varAtom);

            stm.enterScope("For_Loop_Line_" + ctx.getStart().getLine());

            String loopVarName = varAtom.symbolTablePrint();
            registerSymbol(loopVarName, "LoopVariable", "Dynamic");
        }

        if (ctx.python_expr() != null) {
            PythonExpression iter = new PythonExpressionVisitor().visit(ctx.python_expr());
            if (iter != null) {
                node.setIter(iter);
            }
        }

        if (ctx.suite() != null) {
            Statement body = new StatementVisitor().visit(ctx.suite());
            node.statement = body;
        }

        if (ctx.atom() != null) {
            stm.exitScope();
        }

        return node;
    }
}