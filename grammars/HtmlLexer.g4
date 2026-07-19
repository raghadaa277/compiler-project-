lexer grammar HtmlLexer;

@header {package antlr.html;}

JINJA_EXPR_START: '{{' -> pushMode(JINJA_MODE);
JINJA_STMT_START: '{%' -> pushMode(JINJA_MODE);
JINJA_COMMENT_START: '{#' -> pushMode(JINJA_MODE);

HTML_COMMENT
    : '<!--' .*? '-->' -> channel(HIDDEN)
    ;

HTML_CONDITIONAL_COMMENT
    : '<![' .*? ']>' -> channel(HIDDEN)
    ;

XML_DECLARATION
    : '<?xml' .*? '?>' -> channel(HIDDEN)
    ;

CDATA
    : '<![CDATA[' .*? ']]>'
    ;

DTD
    : '<!' .*? '>' -> channel(HIDDEN)
    ;

SCRIPTLET
    : '<%' .*? '%>'
    ;

SEA_WS
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;

STYLE_OPEN
    : '<style' ~'>'* '>' -> pushMode(STYLE_MODE)
    ;

TAG_OPEN
    : '<' -> pushMode(TAG_MODE)
    ;

HTML_TEXT
   : ~[<{"] (~[<{"])*
   ;
// =================== TAG_MODE MODE (Inside <...>) ===================
mode TAG_MODE;

TAG_CLOSE
    : '>' -> popMode
    ;

TAG_SLASH_CLOSE
    : '/>' -> popMode
    ;

TAG_SLASH
    : '/'
    ;

TAG_EQUALS
    : '='
    ;

TAG_NAME
    : TAG_NameChar+
    ;

TAG_WHITESPACE
    : [ \t\r\n]+ -> channel(HIDDEN)
    ;

ATTVALUE_VALUE
    : '"' ~'"'* '"'
    | '\'' ~'\''* '\''
    ;

// =================== STYLE_MODE MODE (CSS Parsing) ===================
mode STYLE_MODE;

// 1. Mode Exit (Must be prioritized)
STYLE_CLOSE
    : '</style' ~'>'* '>' -> popMode
    ;

CSS_LBRACE     : '{' ;
CSS_RBRACE     : '}' ;
CSS_LPAREN     : '(' ;
CSS_RPAREN     : ')' ;
CSS_COLON      : ':' ;
CSS_SEMI       : ';' ;
CSS_COMMA      : ',' ;
CSS_DOT        : '.' ;
CSS_GT         : '>' ;
CSS_HASH       : '#' ;
CSS_HEX_COLOR  : '#' ( [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] )+ ;
CSS_NUMBER     : ( '0' | [1-9] [0-9]* ) ( '.' [0-9]+ )? ;
CSS_UNIT       : ( 'px' | 'fr' | '%' | 'em' | 'vh' | 'vw' | 'rem') ;
CSS_STRING     : '"' (~'"')* '"' | '\'' (~'\'')* '\'' ;
CSS_ID         : [a-zA-Z] [a-zA-Z0-9\-]* ;
CSS_Space      : [ \t\r\n]+ -> skip ;
CSS_Comment    : '/*' .*? '*/' -> skip ;
CSS_TILDE      : '~';
// =================== JINJA MODE (Unified Jinja Logic) ===================

mode JINJA_MODE;

JINJA_EXPR_END : '}}' -> popMode;
JINJA_STMT_END : '%}' -> popMode;

J_EXTENDS    : 'extends'  ;
J_BLOCK      : 'block'    ;
J_ENDBLOCK   : 'endblock' ;
J_FOR        : 'for'      ;
J_ENDFOR     : 'endfor'   ;
J_IF         : 'if'       ;
J_ENDIF      : 'endif'    ;
J_IN         : 'in'       ;
J_LENGTH     : 'length'   ;
J_OR         : 'or'       ;
J_AND        : 'and'      ;
J_NOT        : 'not'      ;
J_IS         : 'is'       ;
J_FILTER     : 'filter'   ;
J_TRUE       : 'true'     ;
J_FALSE      : 'false'    ;
J_NONE       : 'none'     ;

J_LPAREN     : '(' ;
J_RPAREN     : ')' ;
J_COMMA      : ',' ;
J_DOT        : '.' ;
J_PIPE       : '|' ;
J_EQ         : '==' ;
J_NEQ        : '!=' ;
J_GT         : '>' ;
J_LT         : '<' ;
J_ASSIGN     : '=' ;
J_NUMBER     : [0-9]+ ('.' [0-9]+)? ;
J_STRING     : '"' (~'"')* '"' | '\'' (~'\'')* '\'' ;
J_NAME       : [a-zA-Z_] [a-zA-Z0-9_]* ;
J_WS         : [ \t\r\n]+ -> skip ;

//// =================== FRAGMENTS (Helper Rules) ===================

fragment
TAG_NameChar
    : ~[ \t\r\n"'<>/=-]
    ;
