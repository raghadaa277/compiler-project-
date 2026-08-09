package visitor.jinja;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.ASTNode;
import ast.jinja.JinjaArgumentsList;
import ast.jinja.jinjaArg.JinjaArgument;
import ast.jinja.jinjaCallExpr.JinjaVariableAccess;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;


public class JinjaVisitor extends HtmlParserBaseVisitor<ASTNode> {

    @Override
    public JinjaVariableAccess visitJinjaVarAccessOnlyDef(HtmlParser.JinjaVarAccessOnlyDefContext ctx) {
        JinjaVariableAccess jinjaVariableAccess = new JinjaVariableAccess(ctx.start.getLine());
        StringBuilder stringBuilder = new StringBuilder();
        List<TerminalNode> names = ctx.J_NAME();
        List<TerminalNode> lengths = ctx.J_LENGTH();
        int nameIdx = 0, lenIdx = 0;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof TerminalNode) {
                TerminalNode tn = (TerminalNode) ctx.getChild(i);
                int type = tn.getSymbol().getType();
                if (type == HtmlParser.J_NAME || type == HtmlParser.J_LENGTH) {
                    if (stringBuilder.length() > 0) stringBuilder.append(".");
                    stringBuilder.append(tn.getText());
                }
            }
        }
        jinjaVariableAccess.setDottedName(stringBuilder.toString());
        return jinjaVariableAccess;
    }

    @Override
    public JinjaArgumentsList visitJinjaArgListDef(HtmlParser.JinjaArgListDefContext ctx) {
        JinjaArgumentsList jinjaArgumentsList = new JinjaArgumentsList(ctx.start.getLine());
        JinjaArgumentVisitor jinjaArgumentVisitor = new JinjaArgumentVisitor();
        List<JinjaArgument> arguments = new ArrayList<>();
        for(int i = 0; i < ctx.j_argument().size();i ++){
            JinjaArgument jinjaArgument = jinjaArgumentVisitor.visit(ctx.j_argument(i));
            arguments.add(jinjaArgument);
        }
        jinjaArgumentsList.setArguments(arguments);
        return jinjaArgumentsList;
    }
}
