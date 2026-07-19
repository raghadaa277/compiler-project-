lexer grammar CssLexer;

@header {package antlr.css;}

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