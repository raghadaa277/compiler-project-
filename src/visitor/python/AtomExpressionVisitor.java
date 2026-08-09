package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.argsList.ArgumentsList;
import ast.atom.*;
import ast.atomExpression.*;

import java.util.ArrayList;
import java.util.List;

public class AtomExpressionVisitor extends PythonParserBaseVisitor<AtomExpression> {
    private final AtomVisitor atomVisitor = new AtomVisitor();

    @Override
    public AtomExpression visitMethodAccess(PythonParser.MethodAccessContext ctx) {
        AtomExpression target = visit(ctx.atom_expr(0));
        MethodAccess methodAccess;
        List<AtomExpression> calls;
        if (target instanceof MethodAccess innerMa) {
            methodAccess = innerMa;
            calls = innerMa.getMethodCalls();
            if (calls == null) {
                calls = new ArrayList<>();
                innerMa.setMethodCalls(calls);
            }
        } else {
            methodAccess = new MethodAccess(ctx.getStart().getLine());
            methodAccess.setVarName(ctx.atom_expr(0).getText());
            calls = new ArrayList<>();
            methodAccess.setMethodCalls(calls);
        }
        AtomExpression call = visit(ctx.atom_expr(1));
        calls.add(call);
        return methodAccess;
    }

    @Override
    public AtomExpression visitFunctionCall(PythonParser.FunctionCallContext ctx) {
        FunctionCall functionCall = new FunctionCall(ctx.getStart().getLine());
        String funcName = ctx.atom_expr().getText();
        functionCall.setVarName(funcName);
        if (ctx.arglist() != null) {
            ArgumentsList argumentsList = new ArgumentListVisitor().visit(ctx.arglist());
            functionCall.setArgumentsList(argumentsList);
        }
        return functionCall;
    }

    @Override
    public AtomExpression visitSimpleVar(PythonParser.SimpleVarContext ctx) {
        Atom atom = atomVisitor.visit(ctx.atom());
        if (atom.getValue() == null) {
            SimpleVariable sv = new SimpleVariable(ctx.getStart().getLine());
            sv.setVarName("None");
            return sv;
        }
        String val = atom.getValue().toString();
        if (val.startsWith("f\"") || val.startsWith("f'")) {
            String inner = val.substring(val.indexOf('"') + 1, val.length() - 1);
            return new FStringAtomExpression(ctx.getStart().getLine(), inner);
        }
        SimpleVariable simpleVariable = new SimpleVariable(ctx.getStart().getLine());
        simpleVariable.setVarName(atom.getValue().toString());
        return simpleVariable;
    }

    @Override
    public AtomExpression visitSubscript(PythonParser.SubscriptContext ctx) {
        Subscript subscript = new Subscript(ctx.getStart().getLine());
        subscript.setTarget(visit(ctx.atom_expr()));
        subscript.setIndex(ctx.python_expr().getText());
        return subscript;
    }

    @Override
    public AtomExpression visitSlice(PythonParser.SliceContext ctx) {
        Subscript subscript = new Subscript(ctx.getStart().getLine());
        subscript.setTarget(visit(ctx.atom_expr()));
        StringBuilder sliceStr = new StringBuilder();
        if (ctx.python_expr().size() > 0) sliceStr.append(ctx.python_expr(0).getText());
        sliceStr.append(":");
        if (ctx.python_expr().size() > 1) sliceStr.append(ctx.python_expr(1).getText());
        if (ctx.python_expr().size() > 2) {
            sliceStr.append(":").append(ctx.python_expr(2).getText());
        }
        subscript.setIndex(sliceStr.toString());
        return subscript;
    }
}
