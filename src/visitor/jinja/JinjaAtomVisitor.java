package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.atom.*;
import ast.atom.Number;

public class JinjaAtomVisitor extends HtmlParserBaseVisitor<Atom> {
    @Override
    public Atom visitJinjaNumberAtom(HtmlParser.JinjaNumberAtomContext ctx) {
        Number number = new Number(ctx.start.getLine());
        number.setValue(ctx.J_NUMBER().getText());
        return number;
    }

    @Override
    public Atom visitJinjaStringAtom(HtmlParser.JinjaStringAtomContext ctx) {
        Str str = new Str(ctx.start.getLine());
        str.setValue(ctx.J_STRING().getText());
        return str;
    }

    @Override
    public Atom visitJinjaTrueAtom(HtmlParser.JinjaTrueAtomContext ctx) {
        Bool bool = new Bool(ctx.start.getLine());
        bool.setValue("True");
        return bool;
    }

    @Override
    public Atom visitJinjaFalseAtom(HtmlParser.JinjaFalseAtomContext ctx) {
        Bool bool = new Bool(ctx.start.getLine());
        bool.setValue("False");
        return bool;
    }

    @Override
    public Atom visitJinjaNoneAtom(HtmlParser.JinjaNoneAtomContext ctx) {
        return new None(ctx.start.getLine());
    }

    @Override
    public Atom visitJinjaNameAtom(HtmlParser.JinjaNameAtomContext ctx) {
        Name name = new Name(ctx.start.getLine());
        name.setValue(ctx.J_NAME().getText());
        return name;
    }

    @Override
    public Atom visitJinjaLengthAtom(HtmlParser.JinjaLengthAtomContext ctx) {
        Name name = new Name(ctx.start.getLine());
        name.setValue(ctx.J_LENGTH().getText());
        return name;
    }
}
