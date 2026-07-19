// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/HtmlParser.g4 by ANTLR 4.13.2
package antlr.html;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link HtmlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface HtmlParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code HtmlContent}
	 * labeled alternative in {@link HtmlParser#html_content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlContent(HtmlParser.HtmlContentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code HtmlElementItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlElementItem(HtmlParser.HtmlElementItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code HtmlTextItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlTextItem(HtmlParser.HtmlTextItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaStmtItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaStmtItem(HtmlParser.JinjaStmtItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExprItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExprItem(HtmlParser.JinjaExprItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TagElement}
	 * labeled alternative in {@link HtmlParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagElement(HtmlParser.TagElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StyleElement}
	 * labeled alternative in {@link HtmlParser#htmlElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleElement(HtmlParser.StyleElementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code HtmlAttribute}
	 * labeled alternative in {@link HtmlParser#tag_content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHtmlAttribute(HtmlParser.HtmlAttributeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ClosingMarker}
	 * labeled alternative in {@link HtmlParser#tag_content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClosingMarker(HtmlParser.ClosingMarkerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StyleSheet}
	 * labeled alternative in {@link HtmlParser#style_sheet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStyleSheet(HtmlParser.StyleSheetContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link HtmlParser#ruleSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssRule(HtmlParser.CssRuleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssSelectorDeclaration}
	 * labeled alternative in {@link HtmlParser#selector_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelectorDeclaration(HtmlParser.CssSelectorDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssSelectorList}
	 * labeled alternative in {@link HtmlParser#css_selector_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssSelectorList(HtmlParser.CssSelectorListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code QualifiedSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedSelector(HtmlParser.QualifiedSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StandaloneSimpleSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStandaloneSimpleSelector(HtmlParser.StandaloneSimpleSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeAndIdSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeAndIdSelector(HtmlParser.TypeAndIdSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TypeSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSelector(HtmlParser.TypeSelectorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeclarationBlock}
	 * labeled alternative in {@link HtmlParser#declarationList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationBlock(HtmlParser.DeclarationBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssDeclaration}
	 * labeled alternative in {@link HtmlParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssDeclaration(HtmlParser.CssDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionArguments}
	 * labeled alternative in {@link HtmlParser#css_function_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArguments(HtmlParser.FunctionArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CssFunctionCall}
	 * labeled alternative in {@link HtmlParser#css_function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCssFunctionCall(HtmlParser.CssFunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionTerm(HtmlParser.FunctionTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringTerm(HtmlParser.StringTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ColorTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColorTerm(HtmlParser.ColorTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnitNumberTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnitNumberTerm(HtmlParser.UnitNumberTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberTerm(HtmlParser.NumberTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdentifierTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierTerm(HtmlParser.IdentifierTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaStmtBlock}
	 * labeled alternative in {@link HtmlParser#jinjaStatementBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaStmtBlock(HtmlParser.JinjaStmtBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExprBlock}
	 * labeled alternative in {@link HtmlParser#jinjaExpressionBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExprBlock(HtmlParser.JinjaExprBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExtendsStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExtendsStmt(HtmlParser.JinjaExtendsStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBlockStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlockStmt(HtmlParser.JinjaBlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaForStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaForStmt(HtmlParser.JinjaForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaIfStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaIfStmt(HtmlParser.JinjaIfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaExtendsStmtDef}
	 * labeled alternative in {@link HtmlParser#j_extends_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaExtendsStmtDef(HtmlParser.JinjaExtendsStmtDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBlockStmtDef}
	 * labeled alternative in {@link HtmlParser#j_block_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBlockStmtDef(HtmlParser.JinjaBlockStmtDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaForStmtDef}
	 * labeled alternative in {@link HtmlParser#j_for_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaForStmtDef(HtmlParser.JinjaForStmtDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaIfStmtDef}
	 * labeled alternative in {@link HtmlParser#j_if_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaIfStmtDef(HtmlParser.JinjaIfStmtDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaBinaryExpr}
	 * labeled alternative in {@link HtmlParser#j_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaBinaryExpr(HtmlParser.JinjaBinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaSimpleExpr}
	 * labeled alternative in {@link HtmlParser#j_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaSimpleExpr(HtmlParser.JinjaSimpleExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaFilteredExpr}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFilteredExpr(HtmlParser.JinjaFilteredExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaFunctionCall}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFunctionCall(HtmlParser.JinjaFunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaVarAccessOnly}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaVarAccessOnly(HtmlParser.JinjaVarAccessOnlyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaAtomOnly}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaAtomOnly(HtmlParser.JinjaAtomOnlyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaVarAccessOnlyDef}
	 * labeled alternative in {@link HtmlParser#j_var_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaVarAccessOnlyDef(HtmlParser.JinjaVarAccessOnlyDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaArgListDef}
	 * labeled alternative in {@link HtmlParser#j_argument_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaArgListDef(HtmlParser.JinjaArgListDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaPosArg}
	 * labeled alternative in {@link HtmlParser#j_argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaPosArg(HtmlParser.JinjaPosArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaKwArg}
	 * labeled alternative in {@link HtmlParser#j_argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaKwArg(HtmlParser.JinjaKwArgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaNumberAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNumberAtom(HtmlParser.JinjaNumberAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaStringAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaStringAtom(HtmlParser.JinjaStringAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaTrueAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaTrueAtom(HtmlParser.JinjaTrueAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaFalseAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaFalseAtom(HtmlParser.JinjaFalseAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaNoneAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNoneAtom(HtmlParser.JinjaNoneAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JinjaNameAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJinjaNameAtom(HtmlParser.JinjaNameAtomContext ctx);
}