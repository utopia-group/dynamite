grammar Datalog;

// parser rules

program : decls rules facts ;

decls : (decl)* ;

decl : inputDecl | outputDecl | relationDecl | typeDecl ;

inputDecl : INPUT ID paramMap? ;

outputDecl : OUTPUT ID paramMap? ;

paramMap : LEFT_PAREN entry (COMMA entry)* RIGHT_PAREN ;

entry : ID EQ (STRING | ID) ;

relationDecl : RELATION ID LEFT_PAREN attrs RIGHT_PAREN ;

attrs : attr (COMMA attr)* | ;

attr : ID COLON ID ;

typeDecl : TYPE ID ;

rules : (datalogRule)* ;

datalogRule : heads COLON_DASH bodies PERIOD ;

heads : relPred (COMMA relPred)* | ;

relPred : ID LEFT_PAREN expList RIGHT_PAREN ;

expList : exp (COMMA exp)* | ;

exp : cat | ID | INT | STRING ;

cat : CAT LEFT_PAREN exp COMMA exp RIGHT_PAREN ;

bodies : datalogPred (COMMA datalogPred)* | ;

datalogPred : biPred | relPred ;

biPred : exp op exp ;

op : EQ | NE | LT | LE | GT | GE ;

facts : (fact)* ;

fact : relPred PERIOD ;

// lexer rules

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

INPUT : '.input' ;

OUTPUT : '.output' ;

RELATION : '.decl' ;

TYPE : '.type' ;

CAT : 'cat' ;

COMMA : ',' ;

PERIOD : '.' ;

Q_MARK : '?' ;

LEFT_PAREN : '(' ;

RIGHT_PAREN : ')' ;

COLON : ':' ;

COLON_DASH : ':-' ;

MULTIPLY : '*' ;

ADD : '+' ;

EQ : '=' ;

NE : '!=' ;

LT : '<' ;

LE : '<=' ;

GT : '>' ;

GE : '>=' ;

INT : ('0'..'9')+ ;

STRING : '"' ~('\r' | '\n' | '"')* '"';

ID : ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_' | Q_MARK)* ;
