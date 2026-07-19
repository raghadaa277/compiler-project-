parser grammar CssParser;

@header {package antlr.css;}

options { tokenVocab=CssLexer; }

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