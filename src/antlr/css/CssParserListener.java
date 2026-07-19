// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/CssParser.g4 by ANTLR 4.13.2
package antlr.css;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CssParser}.
 */
public interface CssParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code StyleSheet}
	 * labeled alternative in {@link CssParser#style_sheet}.
	 * @param ctx the parse tree
	 */
	void enterStyleSheet(CssParser.StyleSheetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleSheet}
	 * labeled alternative in {@link CssParser#style_sheet}.
	 * @param ctx the parse tree
	 */
	void exitStyleSheet(CssParser.StyleSheetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link CssParser#ruleSet}.
	 * @param ctx the parse tree
	 */
	void enterCssRule(CssParser.CssRuleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link CssParser#ruleSet}.
	 * @param ctx the parse tree
	 */
	void exitCssRule(CssParser.CssRuleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssSelectorDeclaration}
	 * labeled alternative in {@link CssParser#selector_decl}.
	 * @param ctx the parse tree
	 */
	void enterCssSelectorDeclaration(CssParser.CssSelectorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssSelectorDeclaration}
	 * labeled alternative in {@link CssParser#selector_decl}.
	 * @param ctx the parse tree
	 */
	void exitCssSelectorDeclaration(CssParser.CssSelectorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssSelectorList}
	 * labeled alternative in {@link CssParser#css_selector_list}.
	 * @param ctx the parse tree
	 */
	void enterCssSelectorList(CssParser.CssSelectorListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssSelectorList}
	 * labeled alternative in {@link CssParser#css_selector_list}.
	 * @param ctx the parse tree
	 */
	void exitCssSelectorList(CssParser.CssSelectorListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code QualifiedSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedSelector(CssParser.QualifiedSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code QualifiedSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedSelector(CssParser.QualifiedSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StandaloneSimpleSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterStandaloneSimpleSelector(CssParser.StandaloneSimpleSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StandaloneSimpleSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitStandaloneSimpleSelector(CssParser.StandaloneSimpleSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeAndIdSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterTypeAndIdSelector(CssParser.TypeAndIdSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeAndIdSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitTypeAndIdSelector(CssParser.TypeAndIdSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterTypeSelector(CssParser.TypeSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitTypeSelector(CssParser.TypeSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeclarationBlock}
	 * labeled alternative in {@link CssParser#declarationList}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationBlock(CssParser.DeclarationBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeclarationBlock}
	 * labeled alternative in {@link CssParser#declarationList}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationBlock(CssParser.DeclarationBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssDeclaration}
	 * labeled alternative in {@link CssParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterCssDeclaration(CssParser.CssDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssDeclaration}
	 * labeled alternative in {@link CssParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitCssDeclaration(CssParser.CssDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionArguments}
	 * labeled alternative in {@link CssParser#css_function_args}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArguments(CssParser.FunctionArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionArguments}
	 * labeled alternative in {@link CssParser#css_function_args}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArguments(CssParser.FunctionArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssFunctionCall}
	 * labeled alternative in {@link CssParser#css_function_call}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionCall(CssParser.CssFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssFunctionCall}
	 * labeled alternative in {@link CssParser#css_function_call}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionCall(CssParser.CssFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterFunctionTerm(CssParser.FunctionTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitFunctionTerm(CssParser.FunctionTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterStringTerm(CssParser.StringTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitStringTerm(CssParser.StringTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ColorTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterColorTerm(CssParser.ColorTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ColorTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitColorTerm(CssParser.ColorTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnitNumberTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterUnitNumberTerm(CssParser.UnitNumberTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnitNumberTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitUnitNumberTerm(CssParser.UnitNumberTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterNumberTerm(CssParser.NumberTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitNumberTerm(CssParser.NumberTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierTerm(CssParser.IdentifierTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierTerm(CssParser.IdentifierTermContext ctx);
}