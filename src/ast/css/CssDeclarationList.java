package ast.css;

import ast.ASTNode;

import java.util.List;

public class CssDeclarationList extends ASTNode {
    private List<CssDeclaration> declarations;
    public CssDeclarationList(int line_number) {
        super("CssDeclarationList", line_number);
    }


    public void setDeclarations(List<CssDeclaration> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append("( [ ");
        if(declarations != null){
            for (CssDeclaration cssDeclaration : declarations){
                stringBuilder.append(cssDeclaration.toString())
                        .append(declarations.indexOf(cssDeclaration)
                                == declarations.size() - 1 ? "" : ", ");
            }
        }
        stringBuilder.append(" ] ) ");
        return stringBuilder.toString();
    }
}
