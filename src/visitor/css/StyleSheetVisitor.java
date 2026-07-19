package visitor.css;

import antlr.css.CssParser;
import antlr.css.CssParserBaseVisitor;
import ast.ASTNode;
import ast.css.*;
import ast.cssTerm.CssFunctionArguments;
import ast.cssTerm.CssTerm;
import ast.htmlElement.StyleSheet;

import java.util.ArrayList;
import java.util.List;

public class StyleSheetVisitor extends CssParserBaseVisitor<ASTNode> {

    @Override
    public StyleSheet visitStyleSheet(CssParser.StyleSheetContext ctx) {
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
    public RuleSet visitCssRule(CssParser.CssRuleContext ctx) {
        RuleSet ruleSet = new RuleSet(ctx.getStart().getLine());
        SelectorDeclaration selectorDeclaration = (SelectorDeclaration) visit(ctx.selector_decl());
        CssDeclarationList declarationList = (CssDeclarationList) visit(ctx.declarationList());
        ruleSet.setSelectorDeclaration(selectorDeclaration);
        ruleSet.setDeclarationList(declarationList);
        return ruleSet;
    }

    @Override
    public SelectorDeclaration visitCssSelectorDeclaration(CssParser.CssSelectorDeclarationContext ctx) {
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
    public CssSelectorList visitCssSelectorList(CssParser.CssSelectorListContext ctx) {
        CssSelectorList cssSelectorList = new CssSelectorList(ctx.getStart().getLine());
        CssSelectorVisitor cssSelectorVisitor = new CssSelectorVisitor();
        List<CssSelector> cssSelectors = new ArrayList<>();
        for (int i = 0; i < ctx.css_selector().size(); i++) {
            CssSelector cssSelector = cssSelectorVisitor.visit(ctx.css_selector(i));
            cssSelectors.add(cssSelector);
        }
        cssSelectorList.setSelectors(cssSelectors);
        return cssSelectorList;
    }

    @Override
    public CssDeclarationList visitDeclarationBlock(CssParser.DeclarationBlockContext ctx) {
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
    public CssDeclaration visitCssDeclaration(CssParser.CssDeclarationContext ctx) {
        CssDeclaration cssDeclaration = new CssDeclaration(ctx.start.getLine());
        CssTermVisitor cssTermVisitor = new CssTermVisitor();
        List<CssTerm> terms = new ArrayList<>();
        for (int i = 0; i < ctx.cssterm().size(); i++) {
            CssTerm cssTerm = cssTermVisitor.visit(ctx.cssterm(i));
            terms.add(cssTerm);
        }
        cssDeclaration.setCssTermList(terms);
        cssDeclaration.setId(ctx.CSS_ID().getText());

        return cssDeclaration;
    }


    @Override
    public CssFunctionArguments visitFunctionArguments(CssParser.FunctionArgumentsContext ctx) {
        CssFunctionArguments cssFunctionArguments = new CssFunctionArguments(ctx.start.getLine());
        CssTermVisitor cssTermVisitor = new CssTermVisitor();
        List<CssTerm> cssTerms = new ArrayList<>();
        for(int i = 0; i < ctx.cssterm().size(); i ++ ){
            CssTerm cssTerm = cssTermVisitor.visit(ctx.cssterm(i));
            cssTerms.add(cssTerm);
        }
        cssFunctionArguments.setCssTerms(cssTerms);

        return cssFunctionArguments;
    }
}
