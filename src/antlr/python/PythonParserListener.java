// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/PythonParser.g4 by ANTLR 4.13.2
package antlr.python;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PythonParser}.
 */
public interface PythonParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code Program}
	 * labeled alternative in {@link PythonParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProgram(PythonParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Program}
	 * labeled alternative in {@link PythonParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProgram(PythonParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CompoundStatement}
	 * labeled alternative in {@link PythonParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(PythonParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CompoundStatement}
	 * labeled alternative in {@link PythonParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(PythonParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PassStatement}
	 * labeled alternative in {@link PythonParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterPassStatement(PythonParser.PassStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PassStatement}
	 * labeled alternative in {@link PythonParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitPassStatement(PythonParser.PassStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(PythonParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(PythonParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStatement(PythonParser.AssignmentStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStatement(PythonParser.AssignmentStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtomExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterAtomExpression(PythonParser.AtomExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtomExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitAtomExpression(PythonParser.AtomExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(PythonParser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(PythonParser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForLoopStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterForLoopStatement(PythonParser.ForLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForLoopStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitForLoopStatement(PythonParser.ForLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PythonExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterPythonExpression(PythonParser.PythonExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PythonExpression}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitPythonExpression(PythonParser.PythonExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionDefinition}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition(PythonParser.FunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionDefinition}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition(PythonParser.FunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(PythonParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(PythonParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ImportStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterImportStatement(PythonParser.ImportStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ImportStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitImportStatement(PythonParser.ImportStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GlobalStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterGlobalStatement(PythonParser.GlobalStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GlobalStatement}
	 * labeled alternative in {@link PythonParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitGlobalStatement(PythonParser.GlobalStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComplexReturn}
	 * labeled alternative in {@link PythonParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void enterComplexReturn(PythonParser.ComplexReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComplexReturn}
	 * labeled alternative in {@link PythonParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void exitComplexReturn(PythonParser.ComplexReturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleReturn}
	 * labeled alternative in {@link PythonParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimpleReturn(PythonParser.SimpleReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleReturn}
	 * labeled alternative in {@link PythonParser#return_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimpleReturn(PythonParser.SimpleReturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GlobalStatementDef}
	 * labeled alternative in {@link PythonParser#global_stmt}.
	 * @param ctx the parse tree
	 */
	void enterGlobalStatementDef(PythonParser.GlobalStatementDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GlobalStatementDef}
	 * labeled alternative in {@link PythonParser#global_stmt}.
	 * @param ctx the parse tree
	 */
	void exitGlobalStatementDef(PythonParser.GlobalStatementDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ImportFromDef}
	 * labeled alternative in {@link PythonParser#import_from}.
	 * @param ctx the parse tree
	 */
	void enterImportFromDef(PythonParser.ImportFromDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ImportFromDef}
	 * labeled alternative in {@link PythonParser#import_from}.
	 * @param ctx the parse tree
	 */
	void exitImportFromDef(PythonParser.ImportFromDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Imported}
	 * labeled alternative in {@link PythonParser#imptd}.
	 * @param ctx the parse tree
	 */
	void enterImported(PythonParser.ImportedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Imported}
	 * labeled alternative in {@link PythonParser#imptd}.
	 * @param ctx the parse tree
	 */
	void exitImported(PythonParser.ImportedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStatementDef}
	 * labeled alternative in {@link PythonParser#if_stmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStatementDef(PythonParser.IfStatementDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStatementDef}
	 * labeled alternative in {@link PythonParser#if_stmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStatementDef(PythonParser.IfStatementDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BooleanCondition}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterBooleanCondition(PythonParser.BooleanConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BooleanCondition}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitBooleanCondition(PythonParser.BooleanConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterNotExpression(PythonParser.NotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitNotExpression(PythonParser.NotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComparisonExpression}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterComparisonExpression(PythonParser.ComparisonExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComparisonExpression}
	 * labeled alternative in {@link PythonParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitComparisonExpression(PythonParser.ComparisonExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtomComplexExpression}
	 * labeled alternative in {@link PythonParser#python_expr}.
	 * @param ctx the parse tree
	 */
	void enterAtomComplexExpression(PythonParser.AtomComplexExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtomComplexExpression}
	 * labeled alternative in {@link PythonParser#python_expr}.
	 * @param ctx the parse tree
	 */
	void exitAtomComplexExpression(PythonParser.AtomComplexExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComplexExpression}
	 * labeled alternative in {@link PythonParser#python_expr}.
	 * @param ctx the parse tree
	 */
	void enterComplexExpression(PythonParser.ComplexExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComplexExpression}
	 * labeled alternative in {@link PythonParser#python_expr}.
	 * @param ctx the parse tree
	 */
	void exitComplexExpression(PythonParser.ComplexExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterListAccess(PythonParser.ListAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitListAccess(PythonParser.ListAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DictionaryAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterDictionaryAccess(PythonParser.DictionaryAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DictionaryAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitDictionaryAccess(PythonParser.DictionaryAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttributeAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterAttributeAccess(PythonParser.AttributeAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttributeAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitAttributeAccess(PythonParser.AttributeAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MethodAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterMethodAccess(PythonParser.MethodAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MethodAccess}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitMethodAccess(PythonParser.MethodAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ObjectCreation}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterObjectCreation(PythonParser.ObjectCreationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ObjectCreation}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitObjectCreation(PythonParser.ObjectCreationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(PythonParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(PythonParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleVar}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleVar(PythonParser.SimpleVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleVar}
	 * labeled alternative in {@link PythonParser#atom_expr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleVar(PythonParser.SimpleVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Generator}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void enterGenerator(PythonParser.GeneratorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Generator}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void exitGenerator(PythonParser.GeneratorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListComprehension}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void enterListComprehension(PythonParser.ListComprehensionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListComprehension}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void exitListComprehension(PythonParser.ListComprehensionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DictionaryLiteral}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void enterDictionaryLiteral(PythonParser.DictionaryLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DictionaryLiteral}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void exitDictionaryLiteral(PythonParser.DictionaryLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListLiteral}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void enterListLiteral(PythonParser.ListLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListLiteral}
	 * labeled alternative in {@link PythonParser#complex_expr}.
	 * @param ctx the parse tree
	 */
	void exitListLiteral(PythonParser.ListLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LessThanOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterLessThanOperator(PythonParser.LessThanOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LessThanOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitLessThanOperator(PythonParser.LessThanOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GreaterThanOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterGreaterThanOperator(PythonParser.GreaterThanOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GreaterThanOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitGreaterThanOperator(PythonParser.GreaterThanOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterEqualOperator(PythonParser.EqualOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitEqualOperator(PythonParser.EqualOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GreaterEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterGreaterEqualOperator(PythonParser.GreaterEqualOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GreaterEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitGreaterEqualOperator(PythonParser.GreaterEqualOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LessEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterLessEqualOperator(PythonParser.LessEqualOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LessEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitLessEqualOperator(PythonParser.LessEqualOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterNotEqualOperator(PythonParser.NotEqualOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotEqualOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitNotEqualOperator(PythonParser.NotEqualOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OrOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterOrOperator(PythonParser.OrOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OrOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitOrOperator(PythonParser.OrOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterInOperator(PythonParser.InOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitInOperator(PythonParser.InOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotInOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterNotInOperator(PythonParser.NotInOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotInOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitNotInOperator(PythonParser.NotInOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IsOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterIsOperator(PythonParser.IsOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IsOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitIsOperator(PythonParser.IsOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IsNotOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterIsNotOperator(PythonParser.IsNotOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IsNotOperator}
	 * labeled alternative in {@link PythonParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitIsNotOperator(PythonParser.IsNotOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PythonExpressionAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void enterPythonExpressionAssignStmt(PythonParser.PythonExpressionAssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PythonExpressionAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void exitPythonExpressionAssignStmt(PythonParser.PythonExpressionAssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComparisonAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void enterComparisonAssignStmt(PythonParser.ComparisonAssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComparisonAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void exitComparisonAssignStmt(PythonParser.ComparisonAssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArithmeticAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticAssignStmt(PythonParser.ArithmeticAssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArithmeticAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticAssignStmt(PythonParser.ArithmeticAssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TemplateLiteralAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void enterTemplateLiteralAssignStmt(PythonParser.TemplateLiteralAssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TemplateLiteralAssignStmt}
	 * labeled alternative in {@link PythonParser#assign_stmt}.
	 * @param ctx the parse tree
	 */
	void exitTemplateLiteralAssignStmt(PythonParser.TemplateLiteralAssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TemplateLiteral}
	 * labeled alternative in {@link PythonParser#template_literal}.
	 * @param ctx the parse tree
	 */
	void enterTemplateLiteral(PythonParser.TemplateLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TemplateLiteral}
	 * labeled alternative in {@link PythonParser#template_literal}.
	 * @param ctx the parse tree
	 */
	void exitTemplateLiteral(PythonParser.TemplateLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleForLoop}
	 * labeled alternative in {@link PythonParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterSimpleForLoop(PythonParser.SimpleForLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleForLoop}
	 * labeled alternative in {@link PythonParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitSimpleForLoop(PythonParser.SimpleForLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComplexForLoop}
	 * labeled alternative in {@link PythonParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void enterComplexForLoop(PythonParser.ComplexForLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComplexForLoop}
	 * labeled alternative in {@link PythonParser#for_loop}.
	 * @param ctx the parse tree
	 */
	void exitComplexForLoop(PythonParser.ComplexForLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionDefDef}
	 * labeled alternative in {@link PythonParser#func_def}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefDef(PythonParser.FunctionDefDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionDefDef}
	 * labeled alternative in {@link PythonParser#func_def}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefDef(PythonParser.FunctionDefDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Decorator}
	 * labeled alternative in {@link PythonParser#dec}.
	 * @param ctx the parse tree
	 */
	void enterDecorator(PythonParser.DecoratorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Decorator}
	 * labeled alternative in {@link PythonParser#dec}.
	 * @param ctx the parse tree
	 */
	void exitDecorator(PythonParser.DecoratorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionParameters}
	 * labeled alternative in {@link PythonParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterFunctionParameters(PythonParser.FunctionParametersContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionParameters}
	 * labeled alternative in {@link PythonParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitFunctionParameters(PythonParser.FunctionParametersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code KeywordParams}
	 * labeled alternative in {@link PythonParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void enterKeywordParams(PythonParser.KeywordParamsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code KeywordParams}
	 * labeled alternative in {@link PythonParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void exitKeywordParams(PythonParser.KeywordParamsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PositionalParams}
	 * labeled alternative in {@link PythonParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void enterPositionalParams(PythonParser.PositionalParamsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PositionalParams}
	 * labeled alternative in {@link PythonParser#fun_params}.
	 * @param ctx the parse tree
	 */
	void exitPositionalParams(PythonParser.PositionalParamsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NameAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNameAtom(PythonParser.NameAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NameAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNameAtom(PythonParser.NameAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ClassAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterClassAtom(PythonParser.ClassAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ClassAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitClassAtom(PythonParser.ClassAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNumberAtom(PythonParser.NumberAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNumberAtom(PythonParser.NumberAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterStringAtom(PythonParser.StringAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitStringAtom(PythonParser.StringAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NoneAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNoneAtom(PythonParser.NoneAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NoneAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNoneAtom(PythonParser.NoneAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BooleanAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterBooleanAtom(PythonParser.BooleanAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BooleanAtom}
	 * labeled alternative in {@link PythonParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitBooleanAtom(PythonParser.BooleanAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TrueAtom}
	 * labeled alternative in {@link PythonParser#bool_exp}.
	 * @param ctx the parse tree
	 */
	void enterTrueAtom(PythonParser.TrueAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TrueAtom}
	 * labeled alternative in {@link PythonParser#bool_exp}.
	 * @param ctx the parse tree
	 */
	void exitTrueAtom(PythonParser.TrueAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FalseAtom}
	 * labeled alternative in {@link PythonParser#bool_exp}.
	 * @param ctx the parse tree
	 */
	void enterFalseAtom(PythonParser.FalseAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FalseAtom}
	 * labeled alternative in {@link PythonParser#bool_exp}.
	 * @param ctx the parse tree
	 */
	void exitFalseAtom(PythonParser.FalseAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListItems}
	 * labeled alternative in {@link PythonParser#list_items}.
	 * @param ctx the parse tree
	 */
	void enterListItems(PythonParser.ListItemsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListItems}
	 * labeled alternative in {@link PythonParser#list_items}.
	 * @param ctx the parse tree
	 */
	void exitListItems(PythonParser.ListItemsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code KeyValuePairs}
	 * labeled alternative in {@link PythonParser#dict_maker}.
	 * @param ctx the parse tree
	 */
	void enterKeyValuePairs(PythonParser.KeyValuePairsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code KeyValuePairs}
	 * labeled alternative in {@link PythonParser#dict_maker}.
	 * @param ctx the parse tree
	 */
	void exitKeyValuePairs(PythonParser.KeyValuePairsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtomKeyValue}
	 * labeled alternative in {@link PythonParser#key_value}.
	 * @param ctx the parse tree
	 */
	void enterAtomKeyValue(PythonParser.AtomKeyValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtomKeyValue}
	 * labeled alternative in {@link PythonParser#key_value}.
	 * @param ctx the parse tree
	 */
	void exitAtomKeyValue(PythonParser.AtomKeyValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleKeyValue}
	 * labeled alternative in {@link PythonParser#key_value}.
	 * @param ctx the parse tree
	 */
	void enterSimpleKeyValue(PythonParser.SimpleKeyValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleKeyValue}
	 * labeled alternative in {@link PythonParser#key_value}.
	 * @param ctx the parse tree
	 */
	void exitSimpleKeyValue(PythonParser.SimpleKeyValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArithmeticExpression}
	 * labeled alternative in {@link PythonParser#simple_expr}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticExpression(PythonParser.ArithmeticExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArithmeticExpression}
	 * labeled alternative in {@link PythonParser#simple_expr}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticExpression(PythonParser.ArithmeticExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleComparisonExpression}
	 * labeled alternative in {@link PythonParser#simple_expr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleComparisonExpression(PythonParser.SimpleComparisonExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleComparisonExpression}
	 * labeled alternative in {@link PythonParser#simple_expr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleComparisonExpression(PythonParser.SimpleComparisonExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Addition}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void enterAddition(PythonParser.AdditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Addition}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void exitAddition(PythonParser.AdditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Subtraction}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void enterSubtraction(PythonParser.SubtractionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Subtraction}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void exitSubtraction(PythonParser.SubtractionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Division}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void enterDivision(PythonParser.DivisionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Division}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void exitDivision(PythonParser.DivisionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Multiplication}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplication(PythonParser.MultiplicationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Multiplication}
	 * labeled alternative in {@link PythonParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplication(PythonParser.MultiplicationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AtomArgs}
	 * labeled alternative in {@link PythonParser#arglist}.
	 * @param ctx the parse tree
	 */
	void enterAtomArgs(PythonParser.AtomArgsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AtomArgs}
	 * labeled alternative in {@link PythonParser#arglist}.
	 * @param ctx the parse tree
	 */
	void exitAtomArgs(PythonParser.AtomArgsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ComplexArgs}
	 * labeled alternative in {@link PythonParser#arglist}.
	 * @param ctx the parse tree
	 */
	void enterComplexArgs(PythonParser.ComplexArgsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ComplexArgs}
	 * labeled alternative in {@link PythonParser#arglist}.
	 * @param ctx the parse tree
	 */
	void exitComplexArgs(PythonParser.ComplexArgsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PositionalArgument}
	 * labeled alternative in {@link PythonParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterPositionalArgument(PythonParser.PositionalArgumentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PositionalArgument}
	 * labeled alternative in {@link PythonParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitPositionalArgument(PythonParser.PositionalArgumentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code KeywordArgument}
	 * labeled alternative in {@link PythonParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterKeywordArgument(PythonParser.KeywordArgumentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code KeywordArgument}
	 * labeled alternative in {@link PythonParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitKeywordArgument(PythonParser.KeywordArgumentContext ctx);
}