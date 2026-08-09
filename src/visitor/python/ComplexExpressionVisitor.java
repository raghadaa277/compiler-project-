package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.complexExp.*;
import ast.compundStmt.ForLoop;
import ast.keyValue.KeyValue;
import visitor.UniversalPythonVisitor;

import java.util.ArrayList;
import java.util.List;

public class ComplexExpressionVisitor extends PythonParserBaseVisitor<ComplexExpression> {
    @Override
    public ComplexExpression visitGenerator(PythonParser.GeneratorContext ctx) {
        Generator generator = new Generator(ctx.getStart().getLine());
        ForLoopVisitor forLoopVisitor = new ForLoopVisitor();
        ForLoop forLoop = forLoopVisitor.visit(ctx.for_loop());
        generator.setForLoop(forLoop);
        return generator;
    }


    @Override
    public ComplexExpression visitListComprehension(PythonParser.ListComprehensionContext ctx) {
        ListComprehension listComprehension = new ListComprehension(ctx.getStart().getLine());
        ForLoop forLoop = new ForLoopVisitor().visit(ctx.for_loop());
        listComprehension.setForLoop(forLoop);
        return listComprehension;
    }

    @Override
    public ComplexExpression visitDictionaryLiteral(PythonParser.DictionaryLiteralContext ctx) {
        if (ctx.dict_maker() == null) {
            return new DictionaryLiteral(ctx.getStart().getLine());
        }
        return visit(ctx.dict_maker());
    }

    @Override
    public ComplexExpression visitKeyValuePairs(PythonParser.KeyValuePairsContext ctx) {
        DictionaryLiteral dictionaryLiteral = new DictionaryLiteral(ctx.getStart().getLine());
        List<KeyValue> keyValueList = new ArrayList<>();
        for (int i = 0; i < ctx.key_value().size(); i++) {
            KeyValue keyValue = new KeyValueVisitor().visit(ctx.key_value(i));
            keyValueList.add(keyValue);
        }
        dictionaryLiteral.setKeyValues(keyValueList);

        return dictionaryLiteral;
    }

    @Override
    public ComplexExpression visitListLiteral(PythonParser.ListLiteralContext ctx) {
        ListLiteral listLiteral = new ListLiteral(ctx.getStart().getLine());
        if (ctx.list_items() == null){
            listLiteral.setListItems(new ArrayList<>());
            return listLiteral;
        }
        ListItems listItems = (ListItems) new UniversalPythonVisitor().visit(ctx.list_items());

        listLiteral.setListItems(listItems.getItems());

        return listLiteral;
    }

    @Override
    public ComplexExpression visitSetLiteral(PythonParser.SetLiteralContext ctx) {
        SetLiteral setLiteral = new SetLiteral(ctx.getStart().getLine());
        List<ast.atomExpression.AtomExpression> items = new ArrayList<>();
        AtomExpressionVisitor atomExprVisitor = new AtomExpressionVisitor();
        for (int i = 0; i < ctx.atom_expr().size(); i++) {
            items.add(atomExprVisitor.visit(ctx.atom_expr(i)));
        }
        setLiteral.setItems(items);
        return setLiteral;
    }


}
