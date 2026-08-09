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
    private final PythonExpressionVisitor pythonExprVisitor = new PythonExpressionVisitor();

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

    @Override
    public KeyValue visitExprKeyValue(PythonParser.ExprKeyValueContext ctx) {
        AtomKeyValue kv = new AtomKeyValue(ctx.getStart().getLine());
        if (ctx.python_expr(0) != null) {
            String keyText = ctx.python_expr(0).getText();
            Atom keyAtom = new ast.atom.Name(ctx.getStart().getLine());
            keyAtom.setValue(keyText);
            kv.setKey(keyAtom);
        }
        if (ctx.python_expr(1) != null) {
            String valText = ctx.python_expr(1).getText();
            Atom valAtom = new ast.atom.Name(ctx.getStart().getLine());
            valAtom.setValue(valText);
            kv.setValue(valAtom);
        }
        return kv;
    }

}
