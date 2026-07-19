package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.atom.*;
import ast.atom.Number;

public class AtomVisitor extends PythonParserBaseVisitor<Atom> {
    @Override
    public Atom visitNameAtom(PythonParser.NameAtomContext ctx) {
        Name name = new Name(ctx.getStart().getLine());
        name.setValue(ctx.NAME().getText());
        return name;
    }

    @Override
    public ClassAtom visitClassAtom(PythonParser.ClassAtomContext ctx) {
        ClassAtom classAtom = new ClassAtom(ctx.getStart().getLine());
        classAtom.setValue(ctx.CLASS_NAME().getText());
        return classAtom;
    }

    @Override
    public Atom visitNumberAtom(PythonParser.NumberAtomContext ctx) {
        Number number = new Number(ctx.getStart().getLine());
        number.setValue(ctx.NUMBER().getText());
        return number;
    }

    @Override
    public Atom visitStringAtom(PythonParser.StringAtomContext ctx) {
        Str str = new Str(ctx.getStart().getLine());
        str.setValue(ctx.STRING().getText());
        return str;
    }

    @Override
    public Atom visitNoneAtom(PythonParser.NoneAtomContext ctx) {
        return new None(ctx.getStart().getLine());
    }

    @Override
    public Atom visitBooleanAtom(PythonParser.BooleanAtomContext ctx) {
        Bool bool = new Bool(ctx.getStart().getLine());
        if (ctx.getChild(0) instanceof PythonParser.TrueAtomContext) {
            bool.setValue("True");
        } else {
            bool.setValue("False");
        }
        return bool;
    }


}
