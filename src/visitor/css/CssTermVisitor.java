package visitor.css;

import antlr.css.CssParser;
import antlr.css.CssParserBaseVisitor;
import ast.cssTerm.*;

public class CssTermVisitor extends CssParserBaseVisitor<CssTerm> {
    @Override
    public CssTerm visitFunctionTerm(CssParser.FunctionTermContext ctx) {
        return visit(ctx.css_function_call());
    }

    @Override
    public CssTerm visitCssFunctionCall(CssParser.CssFunctionCallContext ctx) {
        FunctionTerm functionTerm = new FunctionTerm(ctx.start.getLine());
        functionTerm.setValue(ctx.CSS_ID().getText());
        CssFunctionArguments cssFunctionArguments = (CssFunctionArguments) new StyleSheetVisitor().visit(ctx.css_function_args());
        functionTerm.setArguments(cssFunctionArguments);
        return functionTerm;
    }

    @Override
    public CssTerm visitStringTerm(CssParser.StringTermContext ctx) {
        StringTerm stringTerm = new StringTerm(ctx.start.getLine());
        stringTerm.setValue(ctx.CSS_STRING().getText());
        return stringTerm;
    }

    @Override
    public CssTerm visitColorTerm(CssParser.ColorTermContext ctx) {
        ColorTerm colorTerm = new ColorTerm(ctx.start.getLine());
        colorTerm.setValue(ctx.CSS_HEX_COLOR().getText());
        return colorTerm;
    }

    @Override
    public CssTerm visitUnitNumberTerm(CssParser.UnitNumberTermContext ctx) {
        UnitNumberTerm unitNumberTerm = new UnitNumberTerm(ctx.start.getLine());
        unitNumberTerm.setValue(ctx.CSS_NUMBER().getText() + " " + ctx.CSS_UNIT().getText());
        return unitNumberTerm;
    }

    @Override
    public CssTerm visitNumberTerm(CssParser.NumberTermContext ctx) {
        NumberTerm numberTerm = new NumberTerm(ctx.start.getLine());
        numberTerm.setValue(ctx.CSS_NUMBER().getText());
        return numberTerm;
    }

    @Override
    public CssTerm visitIdentifierTerm(CssParser.IdentifierTermContext ctx) {
        IdentifierTerm identifierTerm = new IdentifierTerm(ctx.start.getLine());
        identifierTerm.setValue(ctx.CSS_ID().getText());
        return identifierTerm;
    }
}
