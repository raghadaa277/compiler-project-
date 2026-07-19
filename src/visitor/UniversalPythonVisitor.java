package visitor;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.ASTNode;
import ast.Imported;
import ast.argsList.ArgumentsList;
import ast.atom.Atom;
import ast.atom.Bool;
import ast.complexExp.ListItems;
import ast.compundStmt.GlobalStatement;
import ast.functionDef.Decorator;
import ast.functionDef.FunctionParameters;
import ast.keyValue.KeyValue;
import visitor.python.ArgumentListVisitor;
import visitor.python.AtomVisitor;
import visitor.python.FunctionParametersVisitor;
import visitor.python.KeyValueVisitor;

import java.util.ArrayList;
import java.util.List;

public class UniversalPythonVisitor extends PythonParserBaseVisitor<ASTNode> {
    @Override
    public FunctionParameters visitFunctionParameters(PythonParser.FunctionParametersContext ctx) {
        FunctionParametersVisitor functionParametersVisitor = new FunctionParametersVisitor();
        if (ctx.fun_params() == null) {
            FunctionParameters functionParameters = new FunctionParameters(ctx.getStart().getLine());
            functionParameters.setParameters(new ArrayList<>());
            return functionParameters;
        }
        return functionParametersVisitor.visit(ctx.fun_params());
    }

    @Override
    public ListItems visitListItems(PythonParser.ListItemsContext ctx) {
        ListItems listItems = new ListItems(ctx.getStart().getLine());
        List<Atom> atomList = new ArrayList<>();
        AtomVisitor atomVisitor = new AtomVisitor();
        for (int i = 0; i < ctx.atom().size(); i++) {
            Atom atom = atomVisitor.visit(ctx.atom(i));
            atomList.add(atom);
        }
        listItems.setAtomList(atomList);

        return listItems;
    }

    @Override
    public GlobalStatement visitGlobalStatementDef(PythonParser.GlobalStatementDefContext ctx) {
        GlobalStatement globalStatement = new GlobalStatement(ctx.getStart().getLine());
        List<String> globals = new ArrayList<>();
        for (int i = 0; i < ctx.NAME().size(); i++) {
            globals.add(ctx.NAME(i).getText());
        }
        globalStatement.setGlobals(globals);
        return globalStatement;
    }

    @Override
    public Decorator visitDecorator(PythonParser.DecoratorContext ctx) {
        ArgumentListVisitor argumentListVisitor = new ArgumentListVisitor();
        Decorator decorator = new Decorator(ctx.getStart().getLine());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ctx.NAME(0));
        for (int i = 1; i < ctx.NAME().size(); i++) {
            stringBuilder.append(".").append(ctx.NAME(i));
        }
        decorator.setDecoratorName(stringBuilder.toString());
        if (ctx.arglist() != null) {
            ArgumentsList argumentsList = new ArgumentListVisitor().visit(ctx.arglist());
            decorator.setArguments(argumentsList);
        }
        return decorator;
    }

    @Override
    public Imported visitImported(PythonParser.ImportedContext ctx) {
        Imported imported = new Imported(ctx.getStart().getLine());
        if (ctx.NAME() == null || ctx.NAME().isEmpty()) {
            imported.setName(ctx.CLASS_NAME(0).getText());
            if (ctx.CLASS_NAME(1) != null) {
                imported.setAlias(ctx.CLASS_NAME(1).getText());
            }
        } else {
            imported.setName(ctx.NAME(0).getText());
            if (ctx.NAME(1) != null) {
                imported.setAlias(ctx.NAME(1).getText());
            }
        }
        return imported;
    }


    @Override
    public KeyValue visitKeyValuePairs(PythonParser.KeyValuePairsContext ctx) {
        KeyValueVisitor keyValueVisitor = new KeyValueVisitor();
        // Dummy
        return keyValueVisitor.visit(ctx.key_value(0));
    }

    @Override
    public Bool visitTrueAtom(PythonParser.TrueAtomContext ctx) {
        Bool bool = new Bool(ctx.getStart().getLine());
        bool.setValue("True");
        return bool;
    }

    @Override
    public Bool visitFalseAtom(PythonParser.FalseAtomContext ctx) {
        Bool bool = new Bool(ctx.getStart().getLine());
        bool.setValue("False");
        return bool;
    }

}
