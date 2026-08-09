package visitor.html;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.cssTerm.*;

public class HtmlCssTermVisitor extends HtmlParserBaseVisitor<CssTerm> {
    @Override
    public CssTerm visitFunctionTerm(HtmlParser.FunctionTermContext ctx) {
        return visit(ctx.css_function_call());
    }

    @Override
    public CssTerm visitCssFunctionCall(HtmlParser.CssFunctionCallContext ctx) {
        FunctionTerm functionTerm = new FunctionTerm(ctx.start.getLine());
        functionTerm.setValue(ctx.CSS_ID().getText());
        HtmlStyleSheetVisitor visitor = new HtmlStyleSheetVisitor();
        CssFunctionArguments cssFunctionArguments = (CssFunctionArguments) visitor.visit(ctx.css_function_args());
        functionTerm.setArguments(cssFunctionArguments);
        return functionTerm;
    }

    @Override
    public CssTerm visitStringTerm(HtmlParser.StringTermContext ctx) {
        StringTerm stringTerm = new StringTerm(ctx.start.getLine());
        stringTerm.setValue(ctx.CSS_STRING().getText());
        return stringTerm;
    }

    @Override
    public CssTerm visitColorTerm(HtmlParser.ColorTermContext ctx) {
        ColorTerm colorTerm = new ColorTerm(ctx.start.getLine());
        colorTerm.setValue(ctx.CSS_HEX_COLOR().getText());
        return colorTerm;
    }

    @Override
    public CssTerm visitUnitNumberTerm(HtmlParser.UnitNumberTermContext ctx) {
        UnitNumberTerm unitNumberTerm = new UnitNumberTerm(ctx.start.getLine());
        String prefix = ctx.CSS_MINUS() != null ? "-" : "";
        unitNumberTerm.setValue(prefix + ctx.CSS_NUMBER().getText() + " " + ctx.CSS_UNIT().getText());
        return unitNumberTerm;
    }

    @Override
    public CssTerm visitNumberTerm(HtmlParser.NumberTermContext ctx) {
        NumberTerm numberTerm = new NumberTerm(ctx.start.getLine());
        String prefix = ctx.CSS_MINUS() != null ? "-" : "";
        numberTerm.setValue(prefix + ctx.CSS_NUMBER().getText());
        return numberTerm;
    }

    @Override
    public CssTerm visitIdentifierTerm(HtmlParser.IdentifierTermContext ctx) {
        IdentifierTerm identifierTerm = new IdentifierTerm(ctx.start.getLine());
        identifierTerm.setValue(ctx.CSS_ID().getText());
        return identifierTerm;
    }
}
