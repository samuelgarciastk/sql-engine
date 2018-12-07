grammar SqlBase;

singleStatement
    : statement EOF 
    ;

statement
    : SELECT expressionSeq fromClause (WHERE where=booleanExpression)?
    ;

expressionSeq
    : primaryExpression (',' primaryExpression)*
    | WILDCARD
    ;

booleanExpression
    : left=primaryExpression operator=EQ right=primaryExpression 
    ; 

primaryExpression
    : IDENTIFIER
    | constant
    | tbl=IDENTIFIER '.' col=IDENTIFIER
    ;

fromClause
    : FROM relation (',' relation)* 
    ;

relation
    : relationPrimary joinRelation*
    ;

relationPrimary
    : IDENTIFIER
    ;

joinRelation
    : JOIN right=relationPrimary ON booleanExpression 
    ;

constant
    : number
    ;

number
    : INTEGER_VALUE
    ;

SELECT: 'SELECT';

FROM: 'FROM';

JOIN: 'JOIN';

ON: 'ON';

WHERE: 'WHERE';

EQ: '=';

WILDCARD: '*';

INTEGER_VALUE
    : DIGIT+
    ;

IDENTIFIER
    : (LETTER | DIGIT | '_')+
    ;

fragment DIGIT
    : [0-9]
    ;

fragment LETTER
    : [A-Za-z]
    ;

WS
    : [ \r\n\t]+ -> channel(HIDDEN)
    ;
