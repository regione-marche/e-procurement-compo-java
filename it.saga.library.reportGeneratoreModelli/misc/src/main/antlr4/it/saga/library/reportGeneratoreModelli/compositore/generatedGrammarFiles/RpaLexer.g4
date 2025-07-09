lexer grammar RpaLexer;

TAG_OPEN                : '<?compo '        -> pushMode(COMPOSER);
TRANSPARENT_TAG_OPEN    : '<@transparent '  -> pushMode(TRANSPARENT);
// OTHER                   : .                 -> skip;
OTHER                   : .;
// OTHER                   : .*?;
COMMENT                 : TAG_OPEN EXLAMATION .*? TAG_CLOSE;



mode TRANSPARENT;

TRANSPARENT_TAG_CLOSE           : ' @>'         -> popMode;
COMPO_TAG_OPEN                  : '<?compo '    -> pushMode(COMPOSER);
OTHER_TRANSPARENT_CHARACTERS    : .             -> skip;



mode COMPOSER;

TAG_CLOSE   : ' ?>'         -> popMode;

fragment LOWERCASE_DIGIT    : 'a'..'z';
fragment NUMBER_DIGIT       : '1'..'9';
fragment ZERO               : '0';

POSITIVE_INTEGER
        : ZERO
        | NUMBER_DIGIT
        | NUMBER_DIGIT (NUMBER_DIGIT | ZERO)+;

POSITIVE_FLOAT
        : ZERO DOT ZERO
        | ( ZERO | (NUMBER_DIGIT (NUMBER_DIGIT | ZERO)*) ) DOT (NUMBER_DIGIT | ZERO)+;

LOGICOP
        : OR
        | AND
        ;

COMPAREOP
        : EQ
        | NE
        | GT
        | GE
        | LT
        | LE
        ;

WS          : ' ';
NEWLINE     : ('\r\n'|'\n'|'\r');
TAB         : ('\t');

DOT         : '.';
COMMA       : ',';
DDOT        : ':';
HASH        : '#';
UNDERSCORE  : '_';
ATSIGN      : '@';
DOLLAR      : '$';
BKSLASH     : '\\';
APICE	   	: '\'';
DQUOTE      : '"';
PIPE        : '|';
ROPARO      : '(';
ROPARC      : ')';
SQPARO      : '[';
SQPARC      : ']';
CYPARO      : '{';
CYPARC      : '}';

MUL         : '*';
DIV         : '/';
SUB         : '-';
ADD         : '+';
EXP         : '^';
MOD         : '%';
EQUAL       : '=';
GREATER     : '>';
LESS        : '<';
EXLAMATION  : '!';
AMPERSAND   : '&';
UNEQUAL     : '!=';
EQ          : '.EQ.';
NE          : '.NE.';
GT          : '.GT.';
GE          : '.GE.';
LT          : '.LT.';
LE          : '.LE.';
OR          : '.OR.';
AND         : '.AND.';

SQRT    : 'sqrt';
SQR     : 'sqr';
COS     : 'cos';
SEN     : 'sin';
TAN     : 'tan';
LN      : 'ln';
LOG     : 'log';
ABS     : 'abs';

FORMATLN    : 'LN';
FORMATLO    : 'LO';

IF          : '%I' (ZERO | (NUMBER_DIGIT (ZERO | NUMBER_DIGIT)*));
IF_END      : '%F' (ZERO | (NUMBER_DIGIT (ZERO | NUMBER_DIGIT)*));
// ELSE        : '%E' NUMBER_DIGIT (ZERO | NUMBER_DIGIT)*;
ELSE        : '%E';
ELSE_IF     : '%%';
NEW_IF      : '[IF]';
NEW_ELSE    : '[ELSE]';
NEW_ELSE_IF : '[ELSEIF]';
NEW_IF_END  : '[ENDIF]';
NEW_LOOP    : 'FOR';
INLINE      : 'INLINE';
LOOP_NEXT   : 'NEXT';
NEXTR       : 'NEXTR';
BREAK_LOOP  : '[BREAK_LOOP]';
NOENT       : '#_NOENT_#';
JOIN        : '[JOIN]';
JOIN_END    : '[ENDJOIN]';
TOREG       : 'TOREG';
FROMREG     : 'FROMREG';
INCLUDE     : 'INCLUDE';
LENGTH      : 'LENGTH';
INPSTR      : 'INPSTR';
TOSTR       : 'TOSTR';
CATSTR      : 'CATSTR';
TRAON       : 'TRAON';
TRAOFF      : 'TRAOFF';
PRECISION   : 'PRECISION';
NEWSYN      : '[NEWSYN]';
RTLON       : '[RTLON]';
RTLOFF      : '[RTLOFF]';

MNEFOOCO    : 'CO';
MNEFOOUL    : 'UL';
MNEFOOPR    : 'PR';
MNEFOONO    : 'NO';

DOMMONEY        : 'MONEY';
DOMMONEYPOS     : 'MONEYPOS';
DOMTEL          : 'TEL';
DOMTELALL       : 'TELALL';
DOMFAX          : 'FAX';
DOMFAXALL       : 'FAXALL';
DOMSN           : 'SN';
DOMKMCIPPO      : 'KM_CIPPO';
DOMTIPSS        : 'TIP_SS';
DOMCODSS        : 'COD_SS';
DOMNUMSS        : 'NUM_SS';
DOMPROGUFF      : 'PROG_UFF';
DOMPROGCIP      : 'PROG_CIP';
DOMPROGREL      : 'PROG_REL';
DOMPROGRIF      : 'PROG_RIF';
DOMDATAELDA     : 'DATA_ELDA';
DOMXEXECSQL     : 'X_EXECSQL';
DOMXEXECSQLN    : 'X_EXECSQL_n';
DOMXSQLC        : 'X_SQLc';
DOMXDIST        : 'X_DIST';
DOMXS2N         : 'X_S2N';
DOMXEXECAGG     : 'X_EXECAGG';
DOMXDATAORA     : 'X_DATAORA';
DOMXTIME        : 'X_TIME';
DOMXORA         : 'X_ORA';
DOMXMINUTI      : 'X_MINUTI';
DOMXSECONDI     : 'X_SECONDI';
DOMXSUBSTR      : 'X_SUBSTR';
DOMXPAR         : 'X_PAR_';
DOMXDAY         : 'X_DAY';
DOMXDATAF1      : 'X_DATA_F1';
DOMXDATAF2      : 'X_DATA_F2';
DOMXUPR         : 'X_UPR';
DOMXIMG         : 'X_IMG';
DOMEXTIMG       : 'EXT_IMG';
DOMLOWTEXT      : 'LOWTEXT';
DOMCAPTEXT      : 'CAPTEXT';
DOMCAPALLTEXT   : 'CAPALLTEXT';
DOMUPPERTEXT    : 'UPPERTEXT';
DOMHASHFILE     : 'HASHFILE';
DOMFINDHASH     : 'FINDHASH';

DOMDATED_n_0A_nn    : 'D_n_0A_nn';
DOMDATED_n_A0_nn    : 'D_n_A0_nn';
DOMDATED_n_0a_nn    : 'D_n_0a_nn';
DOMDATED_n_AA_nn    : 'D_n_AA_nn';
DOMDATED_n_Aa_nn    : 'D_n_Aa_nn';
DOMDATED_n_aa_nn    : 'D_n_aa_nn';
DOMDATED_n_aa_0n    : 'D_n_aa_0n';
DOMDATED_n_aa_00    : 'D_n_aa_00';
DOMDATED_n_aa_0a    : 'D_n_aa_0a';
DOMDATED_a_aa_aa    : 'D_a_aa_aa';
DOMDATED_0_aa_aa    : 'D_0_aa_aa';
DOMDATED_n_aa_aa    : 'D_n_aa_aa';
DOMDATED_0_00_aa    : 'D_0_00_aa';
DOMDATED_0_00_0a    : 'D_0_00_0a';
DOMDATED_0_00_0n    : 'D_0_00_0n';
DOMDATED_0_00_nn    : 'D_0_00_nn';
DOMDATED_0_aa_00    : 'D_0_aa_00';
DOMDATED_0_Aa_00    : 'D_0_Aa_00';
DOMDATED_0_AA_00    : 'D_0_AA_00';
DOMDATED_n_00_00    : 'D_n_00_00';
DOMDATED_a_00_00    : 'D_a_00_00';
DOMDATED_A_00_00    : 'D_A_00_00';
DOMDATED_n_nn_nn    : 'D_n_nn_nn';
DOMDATED_n_nn_0nI   : 'D_n_nn_0nI';
DOMDATED_0_nn_0nI   : 'D_0_nn_0nI';
DOMDATED_n_nn_nnI   : 'D_n_nn_nnI';
DOMDATED_0_nn_nnI   : 'D_0_nn_nnI';
DOMDATED_n_nn_nnU   : 'D_n_nn_nnU';
DOMDATED_n_nn_0nU   : 'D_n_nn_0nU';
DOMDATED_0_nn_00    : 'D_0_nn_00';

DOMINPUTAO  : 'A.O';
DOMINPUTIO  : 'I.O';
DOMINPUTNO  : 'N.O';
DOMINPUTFO  : 'F.O';
DOMINPUTZO  : 'Z.O';
DOMINPUTDO  : 'D.O';

A       : 'A';
T       : 'T';
L       : 'L';
I       : 'I';
P       : 'P';
E       : 'E';
N       : 'N';
Z       : 'Z';
F       : 'F';
D       : 'D';
U       : 'U';
C       : 'C';
X       : 'X';
V       : 'V';

TOT     : 'TOT';
STR     : 'STR';

/* Wildcards tokens (vedi sezione "mnemonici" del parser) */
MNEMONIC_TOT        : HASH WS* TOT ([0-9]+? | 'V')? WS* HASH;
MNEMONIC_STR        : HASH WS* STR ([0-9]+? | 'V')? WS* HASH;

LOOP_ID             : DOLLAR WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* EQUAL;
LOOP_END            : DOLLAR WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+;
SQUARE_SUFFIX_ID    : SQPARC WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* EQUAL;
NEW_LOOP_END        : SQPARO LOOP_NEXT SQPARC WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+;

NUMBER_SQUARE_SUFFIX_ID             : SQPARC [0-9]+;
GENERIC_SQUARE_SUFFIX_ID            : SQPARC ([a-zA-Z] | [0-9] | UNDERSCORE)+;
EXTENSE_GENERIC_SQUARE_SUFFIX_ID    : SQPARC ([a-zA-Z] | [0-9] | UNDERSCORE | DIV | APICE | DOT | DDOT)+;
TRAON_TOKEN                         : SQPARO TRAON SQPARC ([a-zA-Z] | [0-9] | UNDERSCORE | MUL | ADD | SUB)?;

STRING  : DQUOTE ( '\\"' | . )*? DQUOTE;

MNEMONIC_GENERIC_ID             : HASH WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* HASH;
MNEMONIC_FUNCTION_SUFFIX_ID     : ATSIGN ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* HASH;
MNEMONIC_WITH_INDEX_ID          : HASH WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* ROPARO;
MNEMONIC_WITH_FORMAT_ID         : HASH WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* SQPARO;
MNEMONIC_WITH_CONVERSION_ID     : HASH WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? DOT;
MNEMONIC_WITH_DOMAIN_ID         : HASH WS* ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* CYPARO;
MNEMONIC_JOIN_SUFFIX_ID         : (UNDERSCORE | [a-zA-Z]) ([a-zA-Z] | [0-9] | UNDERSCORE)*? WS* ROPARC;
MNEMONIC_INPUT_SUFFIX_ID        : AMPERSAND ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* HASH;
MNEMONIC_INPUT_SUFFIX_FORMAT_ID : AMPERSAND ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* SQPARO;

FORMAT_SUFFIX_ADOT              : A POSITIVE_INTEGER DOT POSITIVE_INTEGER;
FORMAT_SUFFIX_FDOT              : F POSITIVE_INTEGER DOT POSITIVE_INTEGER;

DOMXEXECSQLN_ID             : DOMXEXECSQLN ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* CYPARC;
DOMXSQLCNNNAAA_SUFFIX_ID    : '_n' ([a-zA-Z] | [0-9])+? WS* CYPARC;
DOMXSQLCNNNAAAT_SUFFIX_ID   : '_n' ([a-zA-Z] | [0-9])+? '_t' ([a-zA-Z] | [0-9])+? WS* CYPARC;
DOMXPAR_SUFFIX_ID           : DOMXPAR ([a-zA-Z] | [0-9] | UNDERSCORE)+? WS* CYPARC;
