package ast.css;

import ast.ASTNode;
import ast.Consts;

public class RuleSet extends ASTNode {
    private SelectorDeclaration selectorDeclaration;
    private CssDeclarationList declarationList;
    public RuleSet(int line_number) {
        super("RuleSet", line_number);
    }

    public void setSelectorDeclaration(SelectorDeclaration selectorDeclaration) {
        this.selectorDeclaration = selectorDeclaration;
    }

    public void setDeclarationList(CssDeclarationList declarationList) {
        this.declarationList = declarationList;
    }

    @Override
    public String toString() {
        return super.toString() + " ( "
                + selectorDeclaration.toString()
                + Consts.printIndent(6) + declarationList.toString();
    }
}
