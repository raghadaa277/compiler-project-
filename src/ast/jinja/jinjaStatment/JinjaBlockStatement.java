package ast.jinja.jinjaStatment;

import ast.Consts;
import ast.HtmlContent;

public class JinjaBlockStatement extends JinjaStatement {
    private String blockName;
    private HtmlContent htmlContent;
    public JinjaBlockStatement(int line_number) {
        super("JinjaBlockStatement", line_number);
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public void setHtmlContent(HtmlContent htmlContent) {
        this.htmlContent = htmlContent;
    }

    @Override
    public String toString() {
        return super.toString() +
                " ( " + blockName + Consts.printIndent(3) + htmlContent.toString();
    }
}
