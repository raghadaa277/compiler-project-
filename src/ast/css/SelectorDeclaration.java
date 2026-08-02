package ast.css;

import ast.ASTNode;

import java.util.List;

public class SelectorDeclaration extends ASTNode {

    public List<CssSelectorList> selectorLists;

    public SelectorDeclaration(int line_number) {
        super("SelectorDeclaration", line_number);
    }

    public void setSelectorLists(List<CssSelectorList> selectorLists) {
        this.selectorLists = selectorLists;
    }
    public String toSelectorString() {
        StringBuilder sb = new StringBuilder();
        if (selectorLists != null) {
            for (int i = 0; i < selectorLists.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(selectorLists.get(i).toSelectorString());
            }
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append(" ( [ ");
        if (selectorLists != null) {
            for (CssSelectorList cssSelectorList : selectorLists) {
                stringBuilder.append(cssSelectorList.toString())
                        .append(selectorLists.indexOf(cssSelectorList)
                                == selectorLists.size() - 1 ? "" : ", ");
            }
        }
        return stringBuilder.toString();
    }
}
