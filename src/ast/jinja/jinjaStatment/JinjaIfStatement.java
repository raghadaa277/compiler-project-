package ast.jinja.jinjaStatment;

import ast.Consts;
import ast.HtmlContent;
import ast.jinja.jinjaExpression.JinjaExpression;

public class JinjaIfStatement extends JinjaStatement {
    private JinjaExpression condition;
    private HtmlContent htmlContent;

    public JinjaIfStatement(int line_number) {
        super("JinjaIfStatement", line_number);
    }

    public void setCondition(JinjaExpression condition) {
        this.condition = condition;
    }

    public void setHtmlContent(HtmlContent htmlContent) {
        this.htmlContent = htmlContent;
    }

    @Override
    public String toString() {
        return super.toString() +
                " ( " + condition.toString() + " ) " +
                Consts.printIndent(3) + htmlContent.toString();
    }
}

