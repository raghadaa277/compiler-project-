package ast.css;

import ast.ASTNode;
import ast.Consts;
import ast.cssTerm.CssTerm;

import java.util.List;

public class CssDeclaration extends ASTNode {
    private List<CssTerm> cssTermList;
    private String id;

    public CssDeclaration(int line_number) {
        super("CssDeclaration", line_number);
    }

    public void setCssTermList(List<CssTerm> cssTermList) {
        this.cssTermList = cssTermList;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append(" ( ").append(id).append(" ) ")
                .append(Consts.printIndent(7));
        stringBuilder.append(" [ ");
        if (cssTermList != null) {
            for (CssTerm cssTerm : cssTermList) {
                stringBuilder.append(cssTerm.toString())
                        .append(cssTermList.indexOf(cssTerm)
                                == cssTermList.size() - 1 ? "" : ", ");
            }
        }
        stringBuilder.append(" ] ) ");

        return stringBuilder.toString();
    }
}
