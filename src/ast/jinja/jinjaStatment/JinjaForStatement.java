package ast.jinja.jinjaStatment;

import ast.Consts;
import ast.HtmlContent;
import ast.jinja.jinjaExpression.JinjaExpression;

public class JinjaForStatement extends JinjaStatement {
    public String id;
    public HtmlContent htmlContent;
    public JinjaExpression iterable;

    public JinjaForStatement(int line_number) {
        super("JinjaForStatement", line_number);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHtmlContent(HtmlContent htmlContent) {
        this.htmlContent = htmlContent;
    }

    public void setIterable(JinjaExpression iterable) {
        this.iterable = iterable;
    }

    @Override
    public String toString() {
        return super.toString() + " ( " + id +
                " in " + iterable.toString() + " ) " +
                Consts.printIndent(3) + htmlContent.toString();
    }
}
