package visitor.html;

import antlr.html.HtmlParser;
import antlr.html.HtmlParserBaseVisitor;
import ast.ASTNode;
import ast.css.*;
import ast.cssTerm.CssFunctionArguments;
import ast.cssTerm.CssTerm;
import ast.htmlElement.StyleSheet;
import visitor.css.CssSelectorVisitor;
import visitor.css.CssTermVisitor;

import java.util.ArrayList;
import java.util.List;

public class HtmlStyleSheetVisitor extends HtmlParserBaseVisitor<ASTNode> {

    @Override
    public StyleSheet visitStyleSheet(HtmlParser.StyleSheetContext ctx) {
        StyleSheet styleSheet = new StyleSheet(ctx.getStart().getLine());
        List<RuleSet> ruleSetList = new ArrayList<>();
        for (int i = 0; i < ctx.ruleSet().size(); i++) {
            RuleSet ruleSet = (RuleSet) visit(ctx.ruleSet(i));
            ruleSetList.add(ruleSet);
        }
        styleSheet.setRuleSets(ruleSetList);
        return styleSheet;
    }

    @Override
    public RuleSet visitCssRule(HtmlParser.CssRuleContext ctx) {
        RuleSet ruleSet = new RuleSet(ctx.getStart().getLine());
        SelectorDeclaration selectorDeclaration = (SelectorDeclaration) visit(ctx.selector_decl());
        CssDeclarationList declarationList = (CssDeclarationList) visit(ctx.declarationList());
        ruleSet.setSelectorDeclaration(selectorDeclaration);
        ruleSet.setDeclarationList(declarationList);
        return ruleSet;
    }

    @Override
    public SelectorDeclaration visitCssSelectorDeclaration(HtmlParser.CssSelectorDeclarationContext ctx) {
        SelectorDeclaration selectorDeclaration = new SelectorDeclaration(ctx.getStart().getLine());
        List<CssSelectorList> cssSelectorLists = new ArrayList<>();
        for (int i = 0; i < ctx.css_selector_list().size(); i++) {
            CssSelectorList cssSelectorList = (CssSelectorList) visit(ctx.css_selector_list(i));
            cssSelectorLists.add(cssSelectorList);
        }
        selectorDeclaration.setSelectorLists(cssSelectorLists);
        return selectorDeclaration;
    }

    @Override
    public CssSelectorList visitCssSelectorList(HtmlParser.CssSelectorListContext ctx) {
        CssSelectorList cssSelectorList = new CssSelectorList(ctx.getStart().getLine());
        HtmlCssSelectorVisitor cssSelectorVisitor = new HtmlCssSelectorVisitor();
        List<CssSelector> cssSelectors = new ArrayList<>();
        for (int i = 0; i < ctx.css_selector().size(); i++) {
            CssSelector cssSelector = cssSelectorVisitor.visit(ctx.css_selector(i));
            cssSelectors.add(cssSelector);
        }
        cssSelectorList.setSelectors(cssSelectors);
        return cssSelectorList;
    }

    @Override
    public CssSelectorList visitCssDescendantSelector(HtmlParser.CssDescendantSelectorContext ctx) {
        CssSelectorList cssSelectorList = new CssSelectorList(ctx.getStart().getLine());
        HtmlCssSelectorVisitor cssSelectorVisitor = new HtmlCssSelectorVisitor();
        List<CssSelector> cssSelectors = new ArrayList<>();
        for (int i = 0; i < ctx.css_selector().size(); i++) {
            CssSelector cssSelector = cssSelectorVisitor.visit(ctx.css_selector(i));
            cssSelectors.add(cssSelector);
        }
        cssSelectorList.setSelectors(cssSelectors);
        return cssSelectorList;
    }

    @Override
    public CssDeclarationList visitDeclarationBlock(HtmlParser.DeclarationBlockContext ctx) {
        CssDeclarationList cssDeclarationList = new CssDeclarationList(ctx.start.getLine());
        List<CssDeclaration> declarations = new ArrayList<>();
        if (!ctx.declaration().isEmpty()) {
            for (int i = 0; i < ctx.declaration().size(); i++) {
                CssDeclaration cssDeclaration = (CssDeclaration) visit(ctx.declaration(i));
                declarations.add(cssDeclaration);
            }
        }
        cssDeclarationList.setDeclarations(declarations);
        return cssDeclarationList;
    }

    @Override
    public CssDeclaration visitCssDeclaration(HtmlParser.CssDeclarationContext ctx) {
        CssDeclaration cssDeclaration = new CssDeclaration(ctx.start.getLine());
        HtmlCssTermVisitor cssTermVisitor = new HtmlCssTermVisitor();
        List<CssTerm> terms = new ArrayList<>();
        HtmlParser.Css_valueContext valueCtx = ctx.css_value();
        if (valueCtx != null) {
            for (int i = 0; i < valueCtx.cssterm().size(); i++) {
                CssTerm cssTerm = cssTermVisitor.visit(valueCtx.cssterm(i));
                terms.add(cssTerm);
            }
        }
        cssDeclaration.setCssTermList(terms);
        cssDeclaration.setId(ctx.CSS_ID().getText());
        return cssDeclaration;
    }

    @Override
    public CssFunctionArguments visitFunctionArguments(HtmlParser.FunctionArgumentsContext ctx) {
        CssFunctionArguments cssFunctionArguments = new CssFunctionArguments(ctx.start.getLine());
        HtmlCssTermVisitor cssTermVisitor = new HtmlCssTermVisitor();
        List<CssTerm> cssTerms = new ArrayList<>();
        List<List<CssTerm>> groups = new ArrayList<>();
        List<CssTerm> currentGroup = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            Object child = ctx.getChild(i);
            if (child instanceof HtmlParser.CsstermContext) {
                CssTerm term = cssTermVisitor.visit((HtmlParser.CsstermContext) child);
                currentGroup.add(term);
                cssTerms.add(term);
            } else if (child instanceof org.antlr.v4.runtime.tree.TerminalNode) {
                org.antlr.v4.runtime.tree.TerminalNode tn =
                    (org.antlr.v4.runtime.tree.TerminalNode) child;
                if (tn.getSymbol().getType() == HtmlParser.CSS_COMMA) {
                    groups.add(currentGroup);
                    currentGroup = new ArrayList<>();
                }
            }
        }
        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }
        cssFunctionArguments.setCssTerms(cssTerms);
        cssFunctionArguments.setGroupedTerms(groups);
        return cssFunctionArguments;
    }
}
