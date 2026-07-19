package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.atom.Atom;
import ast.keyValue.AtomKeyValue;
import ast.keyValue.KeyValue;
import ast.keyValue.SimpleKeyValue;
import ast.simpleExpr.SimpleExpression;

public class KeyValueVisitor extends PythonParserBaseVisitor<KeyValue> {
    private final AtomVisitor atomVisitor = new AtomVisitor();

    @Override
    public KeyValue visitAtomKeyValue(PythonParser.AtomKeyValueContext ctx) {
        AtomKeyValue atomKeyValue = new AtomKeyValue(ctx.getStart().getLine());
        Atom key = atomVisitor.visit(ctx.atom(0));
        Atom value = atomVisitor.visit(ctx.atom(1));
        atomKeyValue.setKey(key);
        atomKeyValue.setValue(value);
        return atomKeyValue;
    }

    @Override
    public KeyValue visitSimpleKeyValue(PythonParser.SimpleKeyValueContext ctx) {
        SimpleKeyValue simpleKeyValue = new SimpleKeyValue(ctx.getStart().getLine());
        Atom key = atomVisitor.visit(ctx.atom());
        SimpleExpression simpleExpression = new SimpleExpressionVisitor().visit(ctx.simple_expr());
        simpleKeyValue.setKey(key);
        simpleKeyValue.setValue(simpleExpression);

        return simpleKeyValue;
    }

}
