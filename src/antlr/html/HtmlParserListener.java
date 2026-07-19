// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/HtmlParser.g4 by ANTLR 4.13.2
package antlr.html;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HtmlParser}.
 */
public interface HtmlParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code HtmlContent}
	 * labeled alternative in {@link HtmlParser#html_content}.
	 * @param ctx the parse tree
	 */
	void enterHtmlContent(HtmlParser.HtmlContentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HtmlContent}
	 * labeled alternative in {@link HtmlParser#html_content}.
	 * @param ctx the parse tree
	 */
	void exitHtmlContent(HtmlParser.HtmlContentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code HtmlElementItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void enterHtmlElementItem(HtmlParser.HtmlElementItemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HtmlElementItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void exitHtmlElementItem(HtmlParser.HtmlElementItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code HtmlTextItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void enterHtmlTextItem(HtmlParser.HtmlTextItemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HtmlTextItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void exitHtmlTextItem(HtmlParser.HtmlTextItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaStmtItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void enterJinjaStmtItem(HtmlParser.JinjaStmtItemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaStmtItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void exitJinjaStmtItem(HtmlParser.JinjaStmtItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExprItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExprItem(HtmlParser.JinjaExprItemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExprItem}
	 * labeled alternative in {@link HtmlParser#html_content_item}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExprItem(HtmlParser.JinjaExprItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TagElement}
	 * labeled alternative in {@link HtmlParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterTagElement(HtmlParser.TagElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TagElement}
	 * labeled alternative in {@link HtmlParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitTagElement(HtmlParser.TagElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StyleElement}
	 * labeled alternative in {@link HtmlParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void enterStyleElement(HtmlParser.StyleElementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleElement}
	 * labeled alternative in {@link HtmlParser#htmlElement}.
	 * @param ctx the parse tree
	 */
	void exitStyleElement(HtmlParser.StyleElementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code HtmlAttribute}
	 * labeled alternative in {@link HtmlParser#tag_content}.
	 * @param ctx the parse tree
	 */
	void enterHtmlAttribute(HtmlParser.HtmlAttributeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HtmlAttribute}
	 * labeled alternative in {@link HtmlParser#tag_content}.
	 * @param ctx the parse tree
	 */
	void exitHtmlAttribute(HtmlParser.HtmlAttributeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ClosingMarker}
	 * labeled alternative in {@link HtmlParser#tag_content}.
	 * @param ctx the parse tree
	 */
	void enterClosingMarker(HtmlParser.ClosingMarkerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ClosingMarker}
	 * labeled alternative in {@link HtmlParser#tag_content}.
	 * @param ctx the parse tree
	 */
	void exitClosingMarker(HtmlParser.ClosingMarkerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StyleSheet}
	 * labeled alternative in {@link HtmlParser#style_sheet}.
	 * @param ctx the parse tree
	 */
	void enterStyleSheet(HtmlParser.StyleSheetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StyleSheet}
	 * labeled alternative in {@link HtmlParser#style_sheet}.
	 * @param ctx the parse tree
	 */
	void exitStyleSheet(HtmlParser.StyleSheetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link HtmlParser#ruleSet}.
	 * @param ctx the parse tree
	 */
	void enterCssRule(HtmlParser.CssRuleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssRule}
	 * labeled alternative in {@link HtmlParser#ruleSet}.
	 * @param ctx the parse tree
	 */
	void exitCssRule(HtmlParser.CssRuleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssSelectorDeclaration}
	 * labeled alternative in {@link HtmlParser#selector_decl}.
	 * @param ctx the parse tree
	 */
	void enterCssSelectorDeclaration(HtmlParser.CssSelectorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssSelectorDeclaration}
	 * labeled alternative in {@link HtmlParser#selector_decl}.
	 * @param ctx the parse tree
	 */
	void exitCssSelectorDeclaration(HtmlParser.CssSelectorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssSelectorList}
	 * labeled alternative in {@link HtmlParser#css_selector_list}.
	 * @param ctx the parse tree
	 */
	void enterCssSelectorList(HtmlParser.CssSelectorListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssSelectorList}
	 * labeled alternative in {@link HtmlParser#css_selector_list}.
	 * @param ctx the parse tree
	 */
	void exitCssSelectorList(HtmlParser.CssSelectorListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code QualifiedSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedSelector(HtmlParser.QualifiedSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code QualifiedSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedSelector(HtmlParser.QualifiedSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StandaloneSimpleSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterStandaloneSimpleSelector(HtmlParser.StandaloneSimpleSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StandaloneSimpleSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitStandaloneSimpleSelector(HtmlParser.StandaloneSimpleSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeAndIdSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterTypeAndIdSelector(HtmlParser.TypeAndIdSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeAndIdSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitTypeAndIdSelector(HtmlParser.TypeAndIdSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void enterTypeSelector(HtmlParser.TypeSelectorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeSelector}
	 * labeled alternative in {@link HtmlParser#css_selector}.
	 * @param ctx the parse tree
	 */
	void exitTypeSelector(HtmlParser.TypeSelectorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeclarationBlock}
	 * labeled alternative in {@link HtmlParser#declarationList}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationBlock(HtmlParser.DeclarationBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeclarationBlock}
	 * labeled alternative in {@link HtmlParser#declarationList}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationBlock(HtmlParser.DeclarationBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssDeclaration}
	 * labeled alternative in {@link HtmlParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterCssDeclaration(HtmlParser.CssDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssDeclaration}
	 * labeled alternative in {@link HtmlParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitCssDeclaration(HtmlParser.CssDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionArguments}
	 * labeled alternative in {@link HtmlParser#css_function_args}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArguments(HtmlParser.FunctionArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionArguments}
	 * labeled alternative in {@link HtmlParser#css_function_args}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArguments(HtmlParser.FunctionArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CssFunctionCall}
	 * labeled alternative in {@link HtmlParser#css_function_call}.
	 * @param ctx the parse tree
	 */
	void enterCssFunctionCall(HtmlParser.CssFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CssFunctionCall}
	 * labeled alternative in {@link HtmlParser#css_function_call}.
	 * @param ctx the parse tree
	 */
	void exitCssFunctionCall(HtmlParser.CssFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterFunctionTerm(HtmlParser.FunctionTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitFunctionTerm(HtmlParser.FunctionTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterStringTerm(HtmlParser.StringTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitStringTerm(HtmlParser.StringTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ColorTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterColorTerm(HtmlParser.ColorTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ColorTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitColorTerm(HtmlParser.ColorTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnitNumberTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterUnitNumberTerm(HtmlParser.UnitNumberTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnitNumberTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitUnitNumberTerm(HtmlParser.UnitNumberTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterNumberTerm(HtmlParser.NumberTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitNumberTerm(HtmlParser.NumberTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierTerm(HtmlParser.IdentifierTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierTerm}
	 * labeled alternative in {@link HtmlParser#cssterm}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierTerm(HtmlParser.IdentifierTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaStmtBlock}
	 * labeled alternative in {@link HtmlParser#jinjaStatementBlock}.
	 * @param ctx the parse tree
	 */
	void enterJinjaStmtBlock(HtmlParser.JinjaStmtBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaStmtBlock}
	 * labeled alternative in {@link HtmlParser#jinjaStatementBlock}.
	 * @param ctx the parse tree
	 */
	void exitJinjaStmtBlock(HtmlParser.JinjaStmtBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExprBlock}
	 * labeled alternative in {@link HtmlParser#jinjaExpressionBlock}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExprBlock(HtmlParser.JinjaExprBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExprBlock}
	 * labeled alternative in {@link HtmlParser#jinjaExpressionBlock}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExprBlock(HtmlParser.JinjaExprBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExtendsStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExtendsStmt(HtmlParser.JinjaExtendsStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExtendsStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExtendsStmt(HtmlParser.JinjaExtendsStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBlockStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlockStmt(HtmlParser.JinjaBlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBlockStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlockStmt(HtmlParser.JinjaBlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaForStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaForStmt(HtmlParser.JinjaForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaForStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaForStmt(HtmlParser.JinjaForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaIfStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void enterJinjaIfStmt(HtmlParser.JinjaIfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaIfStmt}
	 * labeled alternative in {@link HtmlParser#jStatement}.
	 * @param ctx the parse tree
	 */
	void exitJinjaIfStmt(HtmlParser.JinjaIfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaExtendsStmtDef}
	 * labeled alternative in {@link HtmlParser#j_extends_stmt}.
	 * @param ctx the parse tree
	 */
	void enterJinjaExtendsStmtDef(HtmlParser.JinjaExtendsStmtDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaExtendsStmtDef}
	 * labeled alternative in {@link HtmlParser#j_extends_stmt}.
	 * @param ctx the parse tree
	 */
	void exitJinjaExtendsStmtDef(HtmlParser.JinjaExtendsStmtDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBlockStmtDef}
	 * labeled alternative in {@link HtmlParser#j_block_stmt}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBlockStmtDef(HtmlParser.JinjaBlockStmtDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBlockStmtDef}
	 * labeled alternative in {@link HtmlParser#j_block_stmt}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBlockStmtDef(HtmlParser.JinjaBlockStmtDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaForStmtDef}
	 * labeled alternative in {@link HtmlParser#j_for_stmt}.
	 * @param ctx the parse tree
	 */
	void enterJinjaForStmtDef(HtmlParser.JinjaForStmtDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaForStmtDef}
	 * labeled alternative in {@link HtmlParser#j_for_stmt}.
	 * @param ctx the parse tree
	 */
	void exitJinjaForStmtDef(HtmlParser.JinjaForStmtDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaIfStmtDef}
	 * labeled alternative in {@link HtmlParser#j_if_stmt}.
	 * @param ctx the parse tree
	 */
	void enterJinjaIfStmtDef(HtmlParser.JinjaIfStmtDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaIfStmtDef}
	 * labeled alternative in {@link HtmlParser#j_if_stmt}.
	 * @param ctx the parse tree
	 */
	void exitJinjaIfStmtDef(HtmlParser.JinjaIfStmtDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaBinaryExpr}
	 * labeled alternative in {@link HtmlParser#j_expression}.
	 * @param ctx the parse tree
	 */
	void enterJinjaBinaryExpr(HtmlParser.JinjaBinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaBinaryExpr}
	 * labeled alternative in {@link HtmlParser#j_expression}.
	 * @param ctx the parse tree
	 */
	void exitJinjaBinaryExpr(HtmlParser.JinjaBinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaSimpleExpr}
	 * labeled alternative in {@link HtmlParser#j_expression}.
	 * @param ctx the parse tree
	 */
	void enterJinjaSimpleExpr(HtmlParser.JinjaSimpleExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaSimpleExpr}
	 * labeled alternative in {@link HtmlParser#j_expression}.
	 * @param ctx the parse tree
	 */
	void exitJinjaSimpleExpr(HtmlParser.JinjaSimpleExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaFilteredExpr}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFilteredExpr(HtmlParser.JinjaFilteredExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaFilteredExpr}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFilteredExpr(HtmlParser.JinjaFilteredExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaFunctionCall}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFunctionCall(HtmlParser.JinjaFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaFunctionCall}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFunctionCall(HtmlParser.JinjaFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaVarAccessOnly}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaVarAccessOnly(HtmlParser.JinjaVarAccessOnlyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaVarAccessOnly}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaVarAccessOnly(HtmlParser.JinjaVarAccessOnlyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaAtomOnly}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void enterJinjaAtomOnly(HtmlParser.JinjaAtomOnlyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaAtomOnly}
	 * labeled alternative in {@link HtmlParser#j_call_expr}.
	 * @param ctx the parse tree
	 */
	void exitJinjaAtomOnly(HtmlParser.JinjaAtomOnlyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaVarAccessOnlyDef}
	 * labeled alternative in {@link HtmlParser#j_var_access}.
	 * @param ctx the parse tree
	 */
	void enterJinjaVarAccessOnlyDef(HtmlParser.JinjaVarAccessOnlyDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaVarAccessOnlyDef}
	 * labeled alternative in {@link HtmlParser#j_var_access}.
	 * @param ctx the parse tree
	 */
	void exitJinjaVarAccessOnlyDef(HtmlParser.JinjaVarAccessOnlyDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaArgListDef}
	 * labeled alternative in {@link HtmlParser#j_argument_list}.
	 * @param ctx the parse tree
	 */
	void enterJinjaArgListDef(HtmlParser.JinjaArgListDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaArgListDef}
	 * labeled alternative in {@link HtmlParser#j_argument_list}.
	 * @param ctx the parse tree
	 */
	void exitJinjaArgListDef(HtmlParser.JinjaArgListDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaPosArg}
	 * labeled alternative in {@link HtmlParser#j_argument}.
	 * @param ctx the parse tree
	 */
	void enterJinjaPosArg(HtmlParser.JinjaPosArgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaPosArg}
	 * labeled alternative in {@link HtmlParser#j_argument}.
	 * @param ctx the parse tree
	 */
	void exitJinjaPosArg(HtmlParser.JinjaPosArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaKwArg}
	 * labeled alternative in {@link HtmlParser#j_argument}.
	 * @param ctx the parse tree
	 */
	void enterJinjaKwArg(HtmlParser.JinjaKwArgContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaKwArg}
	 * labeled alternative in {@link HtmlParser#j_argument}.
	 * @param ctx the parse tree
	 */
	void exitJinjaKwArg(HtmlParser.JinjaKwArgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaNumberAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNumberAtom(HtmlParser.JinjaNumberAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaNumberAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNumberAtom(HtmlParser.JinjaNumberAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaStringAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaStringAtom(HtmlParser.JinjaStringAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaStringAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaStringAtom(HtmlParser.JinjaStringAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaTrueAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaTrueAtom(HtmlParser.JinjaTrueAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaTrueAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaTrueAtom(HtmlParser.JinjaTrueAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaFalseAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaFalseAtom(HtmlParser.JinjaFalseAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaFalseAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaFalseAtom(HtmlParser.JinjaFalseAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaNoneAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNoneAtom(HtmlParser.JinjaNoneAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaNoneAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNoneAtom(HtmlParser.JinjaNoneAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code JinjaNameAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void enterJinjaNameAtom(HtmlParser.JinjaNameAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code JinjaNameAtom}
	 * labeled alternative in {@link HtmlParser#j_atom}.
	 * @param ctx the parse tree
	 */
	void exitJinjaNameAtom(HtmlParser.JinjaNameAtomContext ctx);
}