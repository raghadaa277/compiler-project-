// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/PythonParser.g4 by ANTLR 4.13.2
package antlr.python;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PythonParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PythonParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code Program}
	 * labeled alternative in {@link PythonParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(PythonParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CompoundStatement}
	 * labeled alternative in {@link PythonParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundStatement(PythonParser.CompoundStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PassStatement}
	 * labeled alternative in {@link PythonParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPassStatement(PythonParser.PassStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(PythonParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignmentStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(PythonParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomExpression(PythonParser.AtomExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleExpression(PythonParser.SimpleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForLoopStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoopStatement(PythonParser.ForLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PythonExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPythonExpression(PythonParser.PythonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionDefinition}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDefinition(PythonParser.FunctionDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(PythonParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ImportStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportStatement(PythonParser.ImportStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GlobalStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalStatement(PythonParser.GlobalStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComplexReturn}
	 * labeled alternative in {@link PythonParser#return_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexReturn(PythonParser.ComplexReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleReturn}
	 * labeled alternative in {@link PythonParser#return_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleReturn(PythonParser.SimpleReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GlobalStatementDef}
	 * labeled alternative in {@link PythonParser#global_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalStatementDef(PythonParser.GlobalStatementDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ImportFromDef}
	 * labeled alternative in {@link PythonParser#import_from}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportFromDef(PythonParser.ImportFromDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Imported}
	 * labeled alternative in {@link PythonParser#imptd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImported(PythonParser.ImportedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStatementDef}
	 * labeled alternative in {@link PythonParser#if_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatementDef(PythonParser.IfStatementDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BooleanCondition}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanCondition(PythonParser.BooleanConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(PythonParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComparisonExpression}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonExpression(PythonParser.ComparisonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomComplexExpression}
	 * labeled alternative in {@link PythonParser#python_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomComplexExpression(PythonParser.AtomComplexExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComplexExpression}
	 * labeled alternative in {@link PythonParser#python_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexExpression(PythonParser.ComplexExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListAccess(PythonParser.ListAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DictionaryAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictionaryAccess(PythonParser.DictionaryAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AttributeAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeAccess(PythonParser.AttributeAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MethodAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodAccess(PythonParser.MethodAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ObjectCreation}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectCreation(PythonParser.ObjectCreationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(PythonParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleVar}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleVar(PythonParser.SimpleVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Generator}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerator(PythonParser.GeneratorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListComprehension}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListComprehension(PythonParser.ListComprehensionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DictionaryLiteral}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictionaryLiteral(PythonParser.DictionaryLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListLiteral}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListLiteral(PythonParser.ListLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LessThanOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessThanOperator(PythonParser.LessThanOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GreaterThanOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGreaterThanOperator(PythonParser.GreaterThanOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualOperator(PythonParser.EqualOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GreaterEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGreaterEqualOperator(PythonParser.GreaterEqualOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LessEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessEqualOperator(PythonParser.LessEqualOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotEqualOperator(PythonParser.NotEqualOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OrOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrOperator(PythonParser.OrOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInOperator(PythonParser.InOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotInOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotInOperator(PythonParser.NotInOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IsOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsOperator(PythonParser.IsOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IsNotOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsNotOperator(PythonParser.IsNotOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PythonExpressionAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPythonExpressionAssignStmt(PythonParser.PythonExpressionAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComparisonAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonAssignStmt(PythonParser.ComparisonAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticAssignStmt(PythonParser.ArithmeticAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TemplateLiteralAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateLiteralAssignStmt(PythonParser.TemplateLiteralAssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TemplateLiteral}
	 * labeled alternative in {@link PythonParser#template_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateLiteral(PythonParser.TemplateLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleForLoop}
	 * labeled alternative in {@link PythonParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleForLoop(PythonParser.SimpleForLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComplexForLoop}
	 * labeled alternative in {@link PythonParser#for_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexForLoop(PythonParser.ComplexForLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionDefDef}
	 * labeled alternative in {@link PythonParser#func_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDefDef(PythonParser.FunctionDefDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Decorator}
	 * labeled alternative in {@link PythonParser#dec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecorator(PythonParser.DecoratorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionParameters}
	 * labeled alternative in {@link PythonParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParameters(PythonParser.FunctionParametersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code KeywordParams}
	 * labeled alternative in {@link PythonParser#fun_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeywordParams(PythonParser.KeywordParamsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PositionalParams}
	 * labeled alternative in {@link PythonParser#fun_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositionalParams(PythonParser.PositionalParamsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NameAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameAtom(PythonParser.NameAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ClassAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassAtom(PythonParser.ClassAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberAtom(PythonParser.NumberAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringAtom(PythonParser.StringAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NoneAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNoneAtom(PythonParser.NoneAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BooleanAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAtom(PythonParser.BooleanAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueAtom}
	 * labeled alternative in {@link PythonParser#bool_exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueAtom(PythonParser.TrueAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseAtom}
	 * labeled alternative in {@link PythonParser#bool_exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseAtom(PythonParser.FalseAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListItems}
	 * labeled alternative in {@link PythonParser#list_items}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListItems(PythonParser.ListItemsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code KeyValuePairs}
	 * labeled alternative in {@link PythonParser#dict_maker}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyValuePairs(PythonParser.KeyValuePairsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomKeyValue}
	 * labeled alternative in {@link PythonParser#key_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomKeyValue(PythonParser.AtomKeyValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleKeyValue}
	 * labeled alternative in {@link PythonParser#key_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleKeyValue(PythonParser.SimpleKeyValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpression}
	 * labeled alternative in {@link PythonParser#simple_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpression(PythonParser.ArithmeticExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleComparisonExpression}
	 * labeled alternative in {@link PythonParser#simple_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleComparisonExpression(PythonParser.SimpleComparisonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Addition}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddition(PythonParser.AdditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Subtraction}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtraction(PythonParser.SubtractionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Division}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDivision(PythonParser.DivisionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Multiplication}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplication(PythonParser.MultiplicationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomArgs}
	 * labeled alternative in {@link PythonParser#arglist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomArgs(PythonParser.AtomArgsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ComplexArgs}
	 * labeled alternative in {@link PythonParser#arglist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexArgs(PythonParser.ComplexArgsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PositionalArgument}
	 * labeled alternative in {@link PythonParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositionalArgument(PythonParser.PositionalArgumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code KeywordArgument}
	 * labeled alternative in {@link PythonParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeywordArgument(PythonParser.KeywordArgumentContext ctx);
}