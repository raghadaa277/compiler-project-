parser grammar JinjaFlaskParser;

@header {package antlr;}


options { tokenVocab=JinjaFlaskLexer; }

prog
    : NEWLINE* (statement)* EOF         # Program
    ;

statement
    : (NEWLINE INDENT)? compound_stmt+ DEDENT?        # CompoundStatement
    | (NEWLINE INDENT)? PASS DEDENT?                  # PassStatement
    ;

compound_stmt
    : if_stmt      NEWLINE?      # IfStatement
    | assign_stmt  NEWLINE?      # AssignmentStatement
    | atom_expr    NEWLINE?      # AtomExpression
    | simple_expr  NEWLINE?      # SimpleExpression
    | for_loop     NEWLINE?      # ForLoopStatement
    | python_expr  NEWLINE?      # PythonExpression
    | func_def     NEWLINE?      # FunctionDefinition
    | return_stmt  NEWLINE?      # ReturnStatement
    | import_from  NEWLINE?      # ImportStatement
    | global_stmt  NEWLINE?      # GlobalStatement
    ;

return_stmt
    : RETURN python_expr      # ComplexReturn
    | RETURN atom             # SimpleReturn
    ;

global_stmt
    : GLOBAL NAME (COMMA NAME)*    # GlobalStatementDef
    ;

import_from
    : FROM NAME (DOT NAME)* IMPORT imptd (COMMA imptd)* # ImportFromDef
    ;

imptd
    : (NAME | CLASS_NAME) (AS (NAME | CLASS_NAME))?   # Imported
    ;

if_stmt
    : IF condition COLON statement
     ( ELIF condition COLON statement )*
     ( ELSE COLON statement )? # IfStatementDef
    ;

condition
    : bool_exp                               # BooleanCondition
    | NOT python_expr                        # NotExpression
    | python_expr (comp_op python_expr)*     # ComparisonExpression
    ;

python_expr
    : atom_expr                  # AtomComplexExpression
    | complex_expr               # ComplexExpression
    ;

atom_expr
    : atom LBRACK NUMBER RBRACK         # ListAccess
    | atom LBRACK STRING RBRACK         # DictionaryAccess
    | atom (DOT atom)+                  # AttributeAccess
    | atom (DOT atom_expr)+             # MethodAccess
    | CLASS_NAME LP arglist? RP         # ObjectCreation
    | NAME LP arglist? RP               # FunctionCall
    | atom                              # SimpleVar
    ;

complex_expr
    : LP for_loop RP               # Generator
    | LBRACK for_loop RBRACK       # ListComprehension
    | LKBRACE dict_maker? RKBRACE  # DictionaryLiteral
    | LBRACK list_items? RBRACK    # ListLiteral
    ;

comp_op
    : LT       # LessThanOperator
    | GT       # GreaterThanOperator
    | EQ       # EqualOperator
    | GTE      # GreaterEqualOperator
    | LTE      # LessEqualOperator
    | NEQ      # NotEqualOperator
    | OR       # OrOperator
    | IN       # InOperator
    | NOT IN   # NotInOperator
    | IS       # IsOperator
    | IS NOT   # IsNotOperator
    ;


assign_stmt
    : python_expr ASSIGN python_expr NEWLINE?       # PythonExpressionAssignStmt
    | python_expr ASSIGN condition NEWLINE?          # ComparisonAssignStmt
    | python_expr ASSIGN arithmetic_expr NEWLINE?    # ArithmeticAssignStmt
    | python_expr ASSIGN template_literal NEWLINE?    # TemplateLiteralAssignStmt
    ;


template_literal
   : TRIPLE_DOUBLE_START html_content  TRIPLE_DOUBLE_END      # HtmlContentDoubleTemplate
   | TRIPLE_SINGLE_START html_content  TRIPLE_SINGLE_END      # HtmlContentSingleTemplate
   ;

for_loop
    : FOR atom IN python_expr statement                  # SimpleForLoop
    | atom FOR atom IN python_expr (IF condition)?       # ComplexForLoop
    ;

func_def
    : dec? DEF NAME parameters COLON statement      # FunctionDefDef
    ;

dec
    : AT NAME (DOT NAME)* ( LP arglist? RP )? NEWLINE       # Decorator
    ;

parameters
    : LP fun_params? RP  # FunctionParameters
    ;

fun_params
    : NAME ASSIGN atom (COMMA NAME ASSIGN atom)* # KeywordParams
    | NAME (COMMA NAME)*                         # PositionalParams
    ;

atom
    : NAME   # NameAtom
    | CLASS_NAME  # ClassAtom
    | NUMBER # NumberAtom
    | STRING # StringAtom
    | NONE   # NoneAtom
    | bool_exp # BooleanAtom
    ;

bool_exp:
    TRUE   # TrueAtom
    | FALSE  # FalseAtom
    ;

list_items
    : atom (COMMA atom)* COMMA? # ListItems
    ;

dict_maker
   : key_value ( COMMA key_value )* COMMA? # KeyValuePairs
   ;

key_value
   : atom COLON atom        # AtomKeyValue
   | atom COLON simple_expr # SimpleKeyValue
   ;

simple_expr
    : arithmetic_expr                           # ArithmeticExpression
    | condition                                 # SimpleComparisonExpression
    ;

arithmetic_expr
    : python_expr (PLUS python_expr)+           # Addition
    | python_expr (MINUS python_expr)+          # Subtraction
    | python_expr (SLASH python_expr)+          # Division
    | python_expr (STAR python_expr)+          # Multiplication
    ;

arglist
    : atom (COMMA atom )* COMMA?            # AtomArgs
    | argument (COMMA argument )* COMMA?    # ComplexArgs
    ;

argument
    : python_expr                 # PositionalArgument
    | NAME ASSIGN python_expr     # KeywordArgument
    ;

//==========================HTML RULES=====================
html_content
    : html_content_item* # HtmlContent
    ;

html_content_item
    : htmlElement     # HtmlElementItem
    | HTML_TEXT       # HtmlTextItem
    | jinjaStatementBlock  # JinjaStmtItem
    | jinjaExpressionBlock # JinjaExprItem
    ;

htmlElement
    : TAG_OPEN tag_content*? (TAG_SLASH_CLOSE | TAG_CLOSE)  # TagElement
    | STYLE_OPEN style_sheet STYLE_CLOSE                    # StyleElement
    ;

tag_content
    : TAG_NAME (TAG_EQUALS ATTVALUE_VALUE)? # HtmlAttribute
    | TAG_SLASH                             # ClosingMarker
    ;

//===============CSS RULE======================

style_sheet
    : ruleSet* # StyleSheet
    ;

ruleSet
    : selector_decl CSS_LBRACE declarationList CSS_RBRACE   # CssRule
    ;

selector_decl
    : css_selector_list (CSS_COMMA css_selector_list)* # CssSelectorDeclaration
    ;

css_selector_list
    : css_selector (CSS_GT  css_selector )* # CssSelectorList
    ;

css_selector
    : CSS_ID ( CSS_DOT CSS_ID )*                    # QualifiedSelector
    | ( CSS_DOT CSS_ID CSS_ID? )+                   # StandaloneSimpleSelector
    | CSS_ID (CSS_HASH CSS_ID)*                     # TypeAndIdSelector
    | CSS_ID                                        # TypeSelector
    ;

declarationList
    : declaration* # DeclarationBlock
    ;

declaration
    : CSS_ID  CSS_COLON  cssterm+  CSS_SEMI # CssDeclaration
    ;

css_function_args
    : cssterm+ (  CSS_COMMA  cssterm+ )* # FunctionArguments
    ;


css_function_call
    : CSS_ID CSS_LPAREN  css_function_args?  CSS_RPAREN # CssFunctionCall
    ;

cssterm
    : css_function_call         # FunctionTerm
    | CSS_STRING                # StringTerm
    | CSS_HEX_COLOR             # ColorTerm
    | CSS_NUMBER CSS_UNIT       # UnitNumberTerm
    | CSS_NUMBER                # NumberTerm
    | CSS_ID                    # IdentifierTerm
    ;

//=================jinja rules======================
jinjaStatementBlock
   : JINJA_STMT_START jStatement                    # JinjaStmtBlock
   ;

jinjaExpressionBlock
    : JINJA_EXPR_START j_expression JINJA_EXPR_END  # JinjaExprBlock
    ;

jStatement
    : j_extends_stmt # JinjaExtendsStmt
    | j_block_stmt   # JinjaBlockStmt
    | j_for_stmt     # JinjaForStmt
    | j_if_stmt      # JinjaIfStmt
    ;

j_extends_stmt
    : J_EXTENDS J_STRING JINJA_STMT_END # JinjaExtendsStmtDef
    ;

j_block_stmt
    : J_BLOCK J_NAME JINJA_STMT_END
      html_content
      JINJA_STMT_START J_ENDBLOCK ( J_NAME )? JINJA_STMT_END # JinjaBlockStmtDef
    ;

j_for_stmt
    : J_FOR J_NAME J_IN j_expression JINJA_STMT_END
      html_content
      JINJA_STMT_START J_ENDFOR JINJA_STMT_END  # JinjaForStmtDef
    ;

j_if_stmt
    : J_IF j_expression JINJA_STMT_END
      html_content
      JINJA_STMT_START J_ENDIF JINJA_STMT_END   # JinjaIfStmtDef
    ;

j_expression
    : j_call_expr ( J_EQ | J_NEQ | J_GT | J_LT ) j_call_expr # JinjaBinaryExpr
    | j_call_expr # JinjaSimpleExpr
    ;

j_call_expr
    : j_var_access ( J_PIPE (J_NAME | J_LENGTH) )? # JinjaFilteredExpr
    | J_NAME J_LPAREN j_argument_list? J_RPAREN    # JinjaFunctionCall
    | j_var_access                                 # JinjaVarAccessOnly
    | j_atom                                       # JinjaAtomOnly
    ;

j_var_access
    : J_NAME ( J_DOT J_NAME )*  # JinjaVarAccessOnlyDef
    ;

j_argument_list
    : j_argument ( J_COMMA j_argument )* # JinjaArgListDef
    ;

j_argument
    : j_expression                 # JinjaPosArg
    | J_NAME J_ASSIGN j_expression # JinjaKwArg
    ;

j_atom
    : J_NUMBER # JinjaNumberAtom
    | J_STRING # JinjaStringAtom
    | J_TRUE   # JinjaTrueAtom
    | J_FALSE  # JinjaFalseAtom
    | J_NONE   # JinjaNoneAtom
    | J_NAME   # JinjaNameAtom
    ;