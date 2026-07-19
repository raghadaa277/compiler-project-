// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/CssParser.g4 by ANTLR 4.13.2
package antlr.css;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CssParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CssParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code StyleSheet}
	 * labeled alternative in {@link CssParser#style_sheet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleSheet(CssParser.StyleSheetContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link CssParser#ruleSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssRule(CssParser.CssRuleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssSelectorDeclaration}
	 * labeled alternative in {@link CssParser#selector_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelectorDeclaration(CssParser.CssSelectorDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssSelectorList}
	 * labeled alternative in {@link CssParser#css_selector_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelectorList(CssParser.CssSelectorListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code QualifiedSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedSelector(CssParser.QualifiedSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StandaloneSimpleSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStandaloneSimpleSelector(CssParser.StandaloneSimpleSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeAndIdSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeAndIdSelector(CssParser.TypeAndIdSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeSelector}
	 * labeled alternative in {@link CssParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSelector(CssParser.TypeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeclarationBlock}
	 * labeled alternative in {@link CssParser#declarationList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationBlock(CssParser.DeclarationBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssDeclaration}
	 * labeled alternative in {@link CssParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssDeclaration(CssParser.CssDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionArguments}
	 * labeled alternative in {@link CssParser#css_function_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArguments(CssParser.FunctionArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssFunctionCall}
	 * labeled alternative in {@link CssParser#css_function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionCall(CssParser.CssFunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionTerm(CssParser.FunctionTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringTerm(CssParser.StringTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ColorTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColorTerm(CssParser.ColorTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnitNumberTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnitNumberTerm(CssParser.UnitNumberTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberTerm(CssParser.NumberTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdentifierTerm}
	 * labeled alternative in {@link CssParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierTerm(CssParser.IdentifierTermContext ctx);
}