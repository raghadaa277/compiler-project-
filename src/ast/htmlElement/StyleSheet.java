package ast.htmlElement;

import ast.Consts;
import ast.css.RuleSet;

import java.util.List;

public class StyleSheet extends HtmlElement {
    private List<RuleSet> ruleSets;

    public StyleSheet(int line_number) {
        super("StyleSheet", line_number);
    }

    public void setRuleSets(List<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        if (ruleSets != null) {
            for (RuleSet ruleSet : ruleSets) {
                stringBuilder.append(Consts.printIndent(5)).append(ruleSet.toString());
            }
        }
        return stringBuilder.toString();
    }
}
