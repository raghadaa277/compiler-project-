parser grammar PythonParser;

@header {package antlr.python;}


options { tokenVocab=PythonLexer; }

prog
    : NEWLINE* (statement)* EOF         # Program
    ;

statement
    : compound_stmt NEWLINE?   # CompoundStatement
    | PASS NEWLINE?           # PassStatement
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
    : IF condition COLON NEWLINE? INDENT statement+ DEDENT
     ( ELIF condition COLON NEWLINE? INDENT statement+ DEDENT )*
     ( ELSE COLON NEWLINE? INDENT statement+ DEDENT )? # IfStatementDef
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
    : TRIPLE_QUOTE_STRING  # TemplateLiteral;

for_loop
    : FOR atom IN python_expr COLON NEWLINE? INDENT statement+ DEDENT     # SimpleForLoop
    | atom FOR atom IN python_expr (IF condition)?                        # ComplexForLoop
    ;

func_def
    : dec? DEF NAME parameters COLON NEWLINE? INDENT statement+ DEDENT      # FunctionDefDef
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