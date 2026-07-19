package ast;

public class TemplateLiteral extends ASTNode {
    private String content;

    public TemplateLiteral(int line_number) {
        super("TemplateLiteral", line_number);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return super.toString() + Consts.printIndent(3) + content;
    }
}
