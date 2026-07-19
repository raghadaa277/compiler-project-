lexer grammar PythonLexer;

// Tokens are defined in the parser file, but we redefine the virtual ones here for reference
tokens { INDENT, DEDENT }

options {
    superClass = PythonLexerBase;
}

@lexer::header {
package antlr.python;
}

// =================== LEXER RULES (Hybrid Indentation) ===================


STRING: '\'' (~['\r\n])* '\'' | '"' (~["\r\n])* '"';

NUMBER: [0-9]+ ('.' [0-9]+)? ;

// Tokens (Top-level Python/Flask code)
PASS: 'pass';
DEF: 'def';
RETURN: 'return';
FROM: 'from';
IMPORT: 'import';
AS: 'as';
IF: 'if';
ELIF: 'elif';
ELSE: 'else';
FOR: 'for';
IN: 'in';
GLOBAL: 'global';
AND: 'and';
OR: 'or';
NOT: 'not';
TRUE: 'True';
FALSE: 'False';
NONE: 'None';
IS: 'is';
WHILE : 'while';
ARROW : '->';

AT: '@';
EQ: '==';
NEQ: '!=';
GTE: '>=';
LTE: '<=';
SLASHSLASH: '//';
POW: '**';

ASSIGN: '=';
GT: '>';
LT: '<';
SLASH: '/';
STAR: '*';

COLON: ':';
SEMI: ';';
COMMA: ',';
PLUS: '+';
MINUS: '-';
MOD: '%';


NEWLINE: ({this.atStartOfInput()}? SPACES
       | ( '\r'? '\n' | '\r' | '\f') SPACES?) {this.onNewLine();};

CLASS_NAME: [A-Z][a-zA-Z0-9_]*;
NAME: [a-zA-Z_][a-zA-Z0-9_]*;


LP:  '(' {this.openBrace();};
RP: ')'  {this.closeBrace();};
LBRACK:  '[' {this.openBrace();};
RBRACK:  ']' {this.closeBrace();};
LKBRACE: '{' {this.openBrace();};
RKBRACE:  '}' {this.closeBrace();};



SKIP_: ( COMMENT | LINE_JOINING) -> skip;
SPACES_INLINE: [ \t]+ -> skip;
DOT: '.';

TRIPLE_QUOTE_STRING : '"""' .*? '"""'
{String content = getText();
setText(content.substring(3, content.length() - 3));}
;

fragment SPACES: [ \t]+;
fragment LINE_JOINING: '\\' SPACES? ( '\r'? '\n' | '\r' | '\f');
fragment COMMENT: '#' ~[\r\n\f]*;