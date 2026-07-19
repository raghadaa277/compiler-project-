package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.TemplateLiteral;

public class TemplateLiteralVisitor extends PythonParserBaseVisitor<TemplateLiteral> {

    @Override
    public TemplateLiteral visitTemplateLiteral(PythonParser.TemplateLiteralContext ctx) {
        TemplateLiteral templateLiteral = new TemplateLiteral(ctx.getStart().getLine());
        templateLiteral.setContent(ctx.TRIPLE_QUOTE_STRING().getText());
        return templateLiteral;
    }
}
