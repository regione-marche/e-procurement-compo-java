parser grammar RpaParser;

options { tokenVocab=RpaLexer; }

// NOTA: I token definiti in maniera esplicita nel parser (esempio: something: 'ex' content) sono poi utilizzati
//       dal parser per "tokenizzare" la stringa prima di applicare il parser
//       (in pratica è come se 'ex' fosse un nuovo token definito nel lexer)


// NOTA: In una normale grammatica sarebbe possibile delegare ad altre variabili la ricorsione, ossia:
//
//      A : 'a' B | 'c'
//      B : A 'd' | 'f'
//
// La grammatica di sopra da errore in Antlr!!!
// Tutte le grammatiche con ricorsione NON possono avere variabili di supporto per reindirizzare la ricorsione.
// Perciò la grammatica di sopra DEVE diventare:
//
//      A
//          : 'a' B
//          | 'c'
//          | A 'd'
//          | 'f'
//
// Questo porta a creare grammatiche molto più "caotiche"
//
// Guardare: https://stackoverflow.com/questions/41017948/antlr4-the-following-sets-of-rules-are-mutually-left-recursive



/* ###      Regole basilari                     ### */

// NOTA:    Ho definito qui nel Parser delle regole da "lexer" (piuttosto che nel Lexer) per evitare
//          l'errore "Mismatched Input x expecting x"
//          Vedere: https://stackoverflow.com/questions/29777778/antlr-4-5-mismatched-input-x-expecting-x

// NOTA:    In riferimento a quello scritto sopra, alcune le regole con wildcard possono essere definite nel parser
//          (in questo caso i numeri)

/*
positive_integer
        : NUMBER_DIGIT (ZERO|NUMBER_DIGIT)*;

positive_float
        : (ZERO|NUMBER_DIGIT)+ DOT (ZERO|NUMBER_DIGIT)+;
*/

positiveNumber
        : POSITIVE_INTEGER
        | POSITIVE_FLOAT;

negativeNumber
        : SUB POSITIVE_INTEGER
        | SUB POSITIVE_FLOAT;

domdatecustom
    : DOMDATED_n_0A_nn  #formatD_n_0A_nn1
    | DOMDATED_n_A0_nn  #formatD_n_A0_nn
    | DOMDATED_n_0a_nn  #formatD_n_0a_nn2
    | DOMDATED_n_AA_nn  #formatD_n_AA_nn1
    | DOMDATED_n_Aa_nn  #formatD_n_Aa_nn2
    | DOMDATED_n_aa_nn  #formatD_n_aa_nn3
    | DOMDATED_n_aa_0n  #formatD_n_aa_0n
    | DOMDATED_n_aa_00  #formatD_n_aa_00
    | DOMDATED_n_aa_0a  #formatD_n_aa_0a
    | DOMDATED_a_aa_aa  #formatD_a_aa_aa
    | DOMDATED_0_aa_aa  #formatD_0_aa_aa
    | DOMDATED_n_aa_aa  #formatD_n_aa_aa
    | DOMDATED_0_00_aa  #formatD_0_00_aa
    | DOMDATED_0_00_0a  #formatD_0_00_0a
    | DOMDATED_0_00_0n  #formatD_0_00_0n
    | DOMDATED_0_00_nn  #formatD_0_00_nn
    | DOMDATED_0_aa_00  #formatD_0_aa_001
    | DOMDATED_0_Aa_00  #formatD_0_Aa_002
    | DOMDATED_0_AA_00  #formatD_0_AA_003
    | DOMDATED_n_00_00  #formatD_n_00_00
    | DOMDATED_a_00_00  #formatD_a_00_001
    | DOMDATED_A_00_00  #formatD_A_00_002
    | DOMDATED_n_nn_nn  #formatD_n_nn_nn
    | DOMDATED_n_nn_0nI #formatD_n_nn_0nI
    | DOMDATED_0_nn_0nI #formatD_0_nn_0nI
    | DOMDATED_n_nn_nnI #formatD_n_nn_nnI
    | DOMDATED_0_nn_nnI #formatD_0_nn_nnI
    | DOMDATED_n_nn_nnU #formatD_n_nn_nnU
    | DOMDATED_n_nn_0nU #formatD_n_nn_0nU
    | DOMDATED_0_nn_00  #formatD_0_nn_00;



/* ###      Lista delle istruzioni              ### */
code

        : (genericText (instruction | transparent) genericText)*
        // | (genericText transparent genericText)*
        | genericText;

genericText

        : OTHER*;

instruction
        : TAG_OPEN statement TAG_CLOSE
        | comment;
        // | transparent;

statement
        : ifStatement
        | elseStatement
        | elseIf
        | ifEnd
        | inlineLoop
        | inlineLoopEnd
        | loop
        | breakLoop
        | loopEnd
        | operationStatement
        | ifWithOperationStatement
        | join
        | joinEnd
        | unicode_write_statement
        | readRegisterStatement
        | includeFileStatement
        | length_mnemonic_statement
        | require_string_statement
        | writeStringStatement
        | concatStatement
        | showEmptyMnemonicStatement
        | hideEmptyMnemonicStatement
        | enableRtlStatement
        | disableRtlStatement
        | mnemonic
        | numberPrecisionStatement
        | recordDataStatement
        | newsynStatement;



/* ###      Istruzioni e comandi booleani       ### */
ifStatement

        // Esempio: [IF] %5_EQ_5_OR_6_EQ_6%
        : NEW_IF WS* MOD WS* booleanStatement WS* MOD   #ifStatementNew

        // Esempio: %Inn %condizione%
        | IF WS* MOD WS* booleanStatement WS* MOD       #ifStatementOld;

booleanStatement

        : comparisonStatement                                                                                       #booleanStatementComparison

        // Esempio: 3_LT_27_AND_8_GT_1
        | leftBooleanStatement = booleanStatement WS* LOGICOP WS* rightBooleanStatement = booleanStatement          #booleanStatementComplex;

comparisonStatement

        // Esempio: #NPRAT#_GE_120
        : WS* leftComparisonTerm = comparisonTerm WS* COMPAREOP WS* rightComparisonTerm = comparisonTerm WS*;

comparisonTerm
        : mnemonic                                                          #comparisonTermMnemonic
        | positiveNumber                                                    #comparisonTermPositiveNumber
        | negativeNumber                                                    #comparisonTermNegativeNumber
        | BKSLASH WS* mathOperationStatement operationFormat? WS* BKSLASH   #comparisonTermMathOperationStatement
        | STRING                                                            #comparisonTermString;

ifEnd

        // Esempio: %Fnn
        : IF_END            #ifEndOld

        // Esempio: [ENDIF]
        | NEW_IF_END        #ifEndNew;

elseStatement

        // Esempio: %Enn
        : ELSE POSITIVE_INTEGER #elseStatementOld

        // Esempio: [ELSE]
        | NEW_ELSE              #elseStatementNew;

elseIf

        // Esempio: %%nn %condizione2%
        : ELSE_IF WS* POSITIVE_INTEGER WS* MOD WS* booleanStatement WS* MOD     #elseIfOld

        // Esempio: [ELSEIF] %condizione2%
        | NEW_ELSE_IF WS* MOD WS* booleanStatement WS* MOD                      #elseIfNew;



/* ###      Istruzioni e comandi matematici     ### */
operationStatement

        // Esempio: \12+#NUMERA#\\++1\
        : BKSLASH WS* mathOperationStatement WS* operationFormat? WS* BKSLASH BKSLASH WS* ADD WS* ADD WS* index = (POSITIVE_INTEGER | V)? WS* BKSLASH   #operationStatementAssign

        // Esempio: \12+#NUMERA#\\+1\
        | BKSLASH WS* mathOperationStatement WS* operationFormat? WS* BKSLASH BKSLASH WS* ADD WS* index = (POSITIVE_INTEGER | V)? WS* BKSLASH           #operationStatementAssignIncrease

        // Esempio: \12+#NUMERA#\\-1\
        | BKSLASH WS* mathOperationStatement WS* operationFormat? WS* BKSLASH BKSLASH WS* SUB WS* index = (POSITIVE_INTEGER | V)? WS* BKSLASH           #operationStatementAssignDecrease

        // Esempio: \12+#NUMERA#\
        | BKSLASH WS* mathOperationStatement WS* operationFormat? WS* BKSLASH                                                                           #operationStatementSimple;

mathOperationStatement
        : mathOperationStatement WS* EXP WS* mathOperationStatement     #mathOperationExponent
        | mathOperationStatement WS* DIV WS* mathOperationStatement     #mathOperationDivision
        | mathOperationStatement WS* MUL WS* mathOperationStatement     #mathOperationMultiplication
        | mathOperationStatement WS* SUB WS* mathOperationStatement     #mathOperationSubtraction
        | mathOperationStatement WS* ADD WS* mathOperationStatement     #mathOperationSum
        | mathAtomStatement                                             #mathOperationAtomStatement;

mathAtomStatement
        : ROPARO WS* mathOperationStatement WS* ROPARC  #mathOperationParenthesis
        | mnemonic                                      #mathOperationMnemonic
        | unaryOperation                                #mathOperationUnaryOperation
        | POSITIVE_INTEGER                              #mathOperationPositiveInteger
        | POSITIVE_FLOAT                                #mathOperationPositiveFloat
        | SUB POSITIVE_INTEGER                          #mathOperationNegativeInteger
        | SUB POSITIVE_FLOAT                            #mathOperationNegativeFloat;

unaryOperation

        // Esempio: sqrt(8)
        // : (SQRT | SQR | TAN | SEN | COS | LN | LOG | ABS) ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC

        // Esempio: sqrt(8)
        : SQRT ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC   #sqrt
        | SQR ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC    #sqr
        | TAN ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC    #tan
        | SEN ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC    #sen
        | COS ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC    #cos
        | LN ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC     #ln
        | LOG ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC    #log
        | ABS ROPARO (positiveNumber | negativeNumber | mathOperationStatement) ROPARC    #abs

        // Esempio: sqrt 8
        // | (SQRT | SQR | TAN | SEN | COS | LN | LOG | ABS) WS (positiveNumber | negativeNumber | mathOperationStatement);

        // Esempio: sqrt 8
        | SQRT WS? (positiveNumber | negativeNumber | mathOperationStatement)              #sqrt
        | SQR WS? (positiveNumber | negativeNumber | mathOperationStatement)               #sqr
        | TAN WS? (positiveNumber | negativeNumber | mathOperationStatement)               #tan
        | SEN WS? (positiveNumber | negativeNumber | mathOperationStatement)               #sen
        | COS WS? (positiveNumber | negativeNumber | mathOperationStatement)               #cos
        | LN WS? (positiveNumber | negativeNumber | mathOperationStatement)                #ln
        | LOG WS? (positiveNumber | negativeNumber | mathOperationStatement)               #log
        | ABS WS? (positiveNumber | negativeNumber | mathOperationStatement)               #abs;

ifWithOperationStatement

        // Esempio: \%condizione%valore1 operatore1 valore2 ... operatorenn-1 valorenn \\++nn\
        : BKSLASH MOD booleanStatement MOD WS* mathOperationStatement WS* operationFormat? WS* BKSLASH BKSLASH WS* ADD WS* ADD WS* (POSITIVE_INTEGER | V) WS* BKSLASH   #ifOperationAssign

        // Esempio: \%condizione%valore1 operatore1 valore2 ... operatorenn-1 valorenn \\+nn\
        | BKSLASH MOD booleanStatement MOD WS* mathOperationStatement WS* operationFormat? WS* BKSLASH BKSLASH WS* ADD WS* (POSITIVE_INTEGER | V) WS* BKSLASH           #ifOperationAssignIncrease

        // Esempio: \%condizione%valore1 operatore1 valore2 ... operatorenn-1 valorenn \\-nn\
        | BKSLASH MOD booleanStatement MOD WS* mathOperationStatement WS* operationFormat? WS* BKSLASH BKSLASH WS* SUB WS* (POSITIVE_INTEGER | V) WS* BKSLASH           #ifOperationAssignDecrease

        // Esempio: \%condizione%valore1 operatore1 valore2 ... operatorenn-1 valorenn \
        | BKSLASH MOD booleanStatement MOD WS* mathOperationStatement WS* operationFormat? WS* BKSLASH                                                                  #ifOperationSimple;

operationFormat

        : SQPARO WS* changeFormat;
        // : (DOT formatConversion)? (SQPARO changeFormat)?;
        // : (DOT formatConversion)? (SQPARO changeFormat)? (CYPARO formatDomain)?;



/* ###      Commenti                            ### */
// Esempio: !Questo è un commento in cui non verrà eseguita nessuna istruzione come #CFUTE#
comment: (TAB | WS)* COMMENT;



/* ###      Transparenti                        ### */
// Esempio: Questo è un testo con operazione \1+1\\++1\ che non verra stampato
transparent: TRANSPARENT_TAG_OPEN (COMPO_TAG_OPEN statement TAG_CLOSE)* TRANSPARENT_TAG_CLOSE;



/* ###      Istruzioni e comandi loop           ### */
loop

        // Esempio: $$nome_ind = lim_inf, lim_sup | #_NOENT_#{nn}
        : oldLoopPrefix WS* PIPE WS* NOENT CYPARO id = POSITIVE_INTEGER CYPARC                                                      #oldLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup, passo | #_NOENT_#{nn}
        | oldLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* PIPE WS* NOENT CYPARO id = POSITIVE_INTEGER CYPARC                #oldLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+{nn}
        | oldLoopPrefix WS* loopSingleMnemonicOrder+ CYPARO id = POSITIVE_INTEGER CYPARC                                            #oldLoopWithOrder

        // Esempio: $$nome_ind = lim_inf, lim_sup, passo | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+{nn}
        | oldLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* loopSingleMnemonicOrder+ CYPARO POSITIVE_INTEGER CYPARC           #oldLoopWithOrder

        // Esempio: $$nome_ind = lim_inf, lim_sup{nn}
        | oldLoopPrefix CYPARO id = POSITIVE_INTEGER CYPARC                                                                         #oldLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup, passo{nn}
        | oldLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER CYPARO id = POSITIVE_INTEGER CYPARC                                   #oldLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup | #_NOENT_#{nn}
        | newLoopPrefix WS* PIPE WS* NOENT CYPARO id = POSITIVE_INTEGER CYPARC                                                      #newLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup, passo | #_NOENT_#{nn}
        | newLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* PIPE WS* NOENT CYPARO id = POSITIVE_INTEGER CYPARC                #newLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+{nn}
        | newLoopPrefix loopSingleMnemonicOrder+ CYPARO id = POSITIVE_INTEGER CYPARC                                                #newLoopWithOrder

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup, passo | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+{nn}
        | newLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER loopSingleMnemonicOrder+ CYPARO id = POSITIVE_INTEGER CYPARC          #newLoopWithOrder

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup{nn}
        | newLoopPrefix CYPARO id = POSITIVE_INTEGER CYPARC                                                                         #newLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup, passo{nn}
        | newLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER CYPARO id = POSITIVE_INTEGER CYPARC                                   #newLoop;

oldLoopPrefix

        // Esempio: $$nome_ind = lim_inf, lim_sup
        : DOLLAR LOOP_ID WS* loopLimitInf = loopLimit WS* COMMA WS* loopLimitSup = loopLimit;

newLoopPrefix

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup
        : SQPARO NEW_LOOP SQUARE_SUFFIX_ID WS* loopLimitInf = loopLimit WS* COMMA WS* loopLimitSup = loopLimit;

loopLimit
        : POSITIVE_INTEGER                          #loopLimitPositiveInteger
        | mnemonic                                  #loopLimitMnemonic
        | BKSLASH mathOperationStatement BKSLASH    #loopLimitMathOperation;

loopSingleMnemonicOrder
        : WS* PIPE WS* mnemonic WS* (ADD | SUB)?;

breakLoop

        // Esempio: [BREAK_LOOP]
        : BREAK_LOOP;

loopEnd

        // Esempio: $$nome_ind
        : DOLLAR LOOP_END           #oldLoopEnd

        // Esempio: [NEXT]nome_ind
        | NEW_LOOP_END              #newLoopEnd;

inlineLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup $$${nn}
        : oldInlineLoopPrefix WS* oldInlineLoopSuffix                                                                           #oldInlineLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup | #_NOENT_# $$${nn}
        | oldInlineLoopPrefix WS* PIPE WS* NOENT WS* oldInlineLoopSuffix                                                        #oldInlineLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup, passo $$${nn}
        | oldInlineLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* oldInlineLoopSuffix                                     #oldInlineLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup, passo | #_NOENT_# $$${nn}
        | oldInlineLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* PIPE WS* NOENT WS* oldInlineLoopSuffix                  #oldInlineLoop

        // Esempio: $$nome_ind = lim_inf, lim_sup | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+ $$${nn}
        | oldInlineLoopPrefix WS* loopSingleMnemonicOrder+ WS* oldInlineLoopSuffix                                              #oldInlineLoopWithOrder

        // Esempio: $$nome_ind = lim_inf, lim_sup, passo | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+ $$${nn}
        | oldInlineLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* loopSingleMnemonicOrder+ WS* oldInlineLoopSuffix        #oldInlineLoopWithOrder

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup [NEXTR]{nn}
        | newInlineLoopPrefix WS* newInlineLoopSuffix                                                                           #newInlineLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup | #_NOENT_# [NEXTR]{nn}
        | newInlineLoopPrefix WS* PIPE WS* NOENT WS* newInlineLoopSuffix                                                        #newInlineLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup, passo [NEXTR]{nn}
        | newInlineLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* newInlineLoopSuffix                                     #newInlineLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup, passo | #_NOENT_# [NEXTR]{nn}
        | newInlineLoopPrefix WS* COMMA WS* step = POSITIVE_INTEGER WS* PIPE WS* NOENT WS* newInlineLoopSuffix                  #newInlineLoop

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+ [NEXTR]{nn}
        | newInlineLoopPrefix WS* loopSingleMnemonicOrder+ WS* newInlineLoopSuffix                                              #newInlineLoopWithOrder

        // Esempio: [FOR]nome_ind = lim_inf, lim_sup, passo | #nome_mne1# +| #nome_mne2# -|...|#nome_mnenn#+ [NEXTR]{nn}
        | newInlineLoopPrefix WS* WS* COMMA WS* step = POSITIVE_INTEGER WS* loopSingleMnemonicOrder+ WS* newInlineLoopSuffix    #newInlineLoopWithOrder;

        // TODO: Testare i nuovi "inline-loop" qui sul parser

oldInlineLoopPrefix

        : DOLLAR loopIdName = LOOP_ID WS* loopLimitInf = loopLimit WS* COMMA WS* loopLimitSup = loopLimit;

oldInlineLoopSuffix

        : DOLLAR DOLLAR DOLLAR CYPARO id = POSITIVE_INTEGER CYPARC;

newInlineLoopPrefix

        // : SQPARO NEW_LOOP loopIdName = SQUARE_SUFFIX_ID WS* loopLimitInf = loopLimit WS* COMMA WS* loopLimitSup = loopLimit;
        : SQPARO INLINE loopIdName = SQUARE_SUFFIX_ID WS* loopLimitInf = loopLimit WS* COMMA WS* loopLimitSup = loopLimit;

newInlineLoopSuffix

        : SQPARO NEXTR SQPARC CYPARO id = POSITIVE_INTEGER CYPARC;



inlineLoopEnd

        // Esempio: $$nome_ind$$$
        : DOLLAR loopIdName = LOOP_END WS* DOLLAR DOLLAR DOLLAR

        // Esempio: [NEXT]nome_ind[NEXTR]
        | loopIdName = NEW_LOOP_END WS* SQPARO NEXTR SQPARC;



/* ###      Istruzioni e comandi della JOIN         ### */

join

        // Esempio: [JOIN]#nome_mneA1#,#nome_mneA2#,...,#nome_mneAnn#=valore1,valore2,...,valorenn
        : JOIN WS* joinLeftPart WS* EQUAL WS* joinRightPart;

joinLeftPart

        : mnemonic (WS* COMMA WS* mnemonic)*;

joinRightPart

        : joinValue (WS* COMMA WS* joinValue)*;

joinValue
        : mnemonic
        | STRING;

/*
join_value
        : positiveNumber
        | negativeNumber
        | mnemonic
        | STRING;
*/

joinEnd

        // Esempio: [ENDJOIN]
        : JOIN_END;



/* ###      Istruzioni e comandi registri uniface   ### */
unicode_write_statement

        // Esempio: [TOREG]nn=#TOTmm#
        : SQPARO TOREG SQUARE_SUFFIX_ID MNEMONIC_TOT

        // Esempio: [TOREG]nn=#STRmm#
        | SQPARO TOREG SQUARE_SUFFIX_ID MNEMONIC_STR;

readRegisterStatement

        // Esempio: [FROMREG]#TOTmm#=nn
        : SQPARO FROMREG SQPARC MNEMONIC_TOT EQUAL registerIndex = POSITIVE_INTEGER     #readRegisterIntoTOTStatement

        // Esempio: [FROMREG]#STRmm#=mm
        | SQPARO FROMREG SQPARC MNEMONIC_STR EQUAL registerIndex = POSITIVE_INTEGER     #readRegisterIntoSTRStatement;



/* ###      Istruzioni per includere file           ### */
includeFileStatement

        // Esempio: [INCLUDE]nomefile
        // : SQPARO INCLUDE EXTENSE_GENERIC_SQUARE_SUFFIX_ID;
        : SQPARO INCLUDE EXTENSE_GENERIC_SQUARE_SUFFIX_ID;



/* ###      Istruzioni per lunghezza mnemonico      ### */
length_mnemonic_statement

        // Esempio: [LENGTH]#nome_mnemonico#,#TOTnn#
        : SQPARO LENGTH SQPARC mnemonic COMMA mnemonic;
        // : SQPARO LENGTH SQPARC mnemonic COMMA MNEMONIC_TOT;



/* ###      Istruzioni per richiedere una stringhe  ### */
require_string_statement

        // Esempio: [INPSTR]"..."#STRnn#
        // : SQPARO INPSTR SQPARC SQUARE_SUFFIX_STRING MNEMONIC_STR;
        : SQPARO INPSTR SQPARC STRING MNEMONIC_STR;



/* ###      Istruzione per creare una stringa       ### */
writeStringStatement

        // Esempio: [TOSTR]#STRnn#="..."
        // : SQPARO TOSTR SQPARC MNEMONIC_STR ASSIGN_SUFFIX_STRING
        : SQPARO TOSTR SQPARC WS* MNEMONIC_STR WS* EQUAL WS* STRING     #writeStringStatementConstant

        // Esempio: [TOSTR]#STRnn#=#nome_mnemonico#
        | SQPARO TOSTR SQPARC WS* MNEMONIC_STR WS* EQUAL WS* mnemonic   #writeStringStatementMnemonic;



/* ###      Istruzione per concatenare stringhe     ### */
concatStatement

        // Esempio: [CATSTR]#STRnn#=#nome_mnemonico#,#nome_mnemonico#
        : SQPARO CATSTR SQPARC MNEMONIC_STR EQUAL leftMnemonic = mnemonic WS* COMMA WS* rightMnemonic = mnemonic    #concatTwoMnemonic

        // Esempio: [CATSTR]#STRnn#=#nome_mnemonico#,"..."
        | SQPARO CATSTR SQPARC MNEMONIC_STR EQUAL leftMnemonic = mnemonic WS* COMMA WS* rightString = STRING        #concatMnemonicString

        // Esempio: [CATSTR]#STRnn#="...",#nome_mnemonico#
        | SQPARO CATSTR SQPARC MNEMONIC_STR EQUAL leftString = STRING WS* COMMA WS* rightMnemonic = mnemonic        #concatMnemonicString;



/* ###      Istruzione per evidenziare campi vuoti  ### */
showEmptyMnemonicStatement

        // Esempio: [TRAON]carattere
        : TRAON_TOKEN;



/* ###      Istruzione per nascondere campi vuoti   ### */
hideEmptyMnemonicStatement

        // Esempio: [TRAOFF]
        : SQPARO TRAOFF SQPARC;



/* ###      Istruzione per la precisione dei numeri ### */
numberPrecisionStatement

        // Esempio: [PRECISION]0
        : SQPARO PRECISION NUMBER_SQUARE_SUFFIX_ID;



/* ###      Istruzione per spostare dati mnemonici  ### */
recordDataStatement

        // Esempio: $&#nome_mne_sor#&#nome_mne_des#
        : DOLLAR AMPERSAND mnemonic AMPERSAND mnemonic;



/* ###      Istruzione per tipo di sintassi usata   ### */
newsynStatement

        // Esempio: [NEWSYN]
        : NEWSYN;



/* ###      Istruzione per abilitare l'RTL          ### */
enableRtlStatement

        // Esempio: [RTLON]
        : RTLON;



/* ###      Istruzione per disabilitare l'RTL       ### */
disableRtlStatement

        // Esempio: [RTLOFF]
        : RTLOFF;



/* ###      Istruzioni e comandi dei mnemonico      ### */

// NOTA:    Le istruzioni con testo (esempio: "_EQ_", "_GT_", etc...) si confondono con il testo del mnemonico.
//          Per cui è stato sostituito il carattere "_" con "." (esempio: ".EQ.", ".GT.", etc..)

// NOTA:    Tutti gli ID dei mnemonici (definiti nel lexer come TOKEN) INCLUDONO i separatori che li delimitano.
//          Questo perchè i token sono stati creati con wildcards che cercano
//          di "includere"/"divorare" TUTTI i caratteri che trovano, perciò un carattere terminale identifica
//          lo STOP per la wildcard. L'aggiunta di un token all'inizio del token-wildcard premette di differenziare
//          i vari token-wildcard.
//
//
//          Esempio: La stringa "#MNEMONICO_A[T]#" avrà come token:
//
//          - "#MMNEMONICO_A["
//            (includo "#" e "[" rispettivamente per far iniziare e terminare il ciclo effettuato dalla wildcard +?
//             del token MNEMONIC_GENERIC_ID)
//
//          - "T"
//            (semplice token identificato da "T")
//
//          - "]"
//            (semplice token identificato da CYPARC)
//
//          - "#"
//            (semplice token identificato da HASH)
//
//
//          Consiglio caldamente di leggere il link sottostante PRIMA DI AGGIUNGERE/MODIFICARE le regole sui mnemonici
//          https://github.com/antlr/antlr4/blob/master/doc/wildcard.md

mnemonic

        /*
        // Esempio: #TOT1#
        : MNEMONIC_TOT                                                                                                                  #mnemonicTot

        // Esempio: #STR12#
        | MNEMONIC_STR                                                                                                                  #mnemonicStr
        */

        // Esempio: #CFUTE_BIS_3#
        : mnemonicName = (MNEMONIC_GENERIC_ID | MNEMONIC_STR | MNEMONIC_TOT)                                                                                    #mnemonicSimple

        // Esempio: #CFUTE_BIS_3(10)#
        | MNEMONIC_WITH_INDEX_ID WS* POSITIVE_INTEGER WS* ROPARC (DOT formatConversion)?  WS*(SQPARO WS* changeFormat)? (CYPARO WS* formatDomain)? WS* HASH     #mnemonicIndexed

        // Esempio: #CFUTE_BIS_3(mnemonico_b)#
        | MNEMONIC_WITH_INDEX_ID WS* MNEMONIC_JOIN_SUFFIX_ID (DOT formatConversion )? WS* (SQPARO WS* changeFormat)? WS* (CYPARO WS* formatDomain)? WS* HASH    #mnemonicDirectJoin

        // Esempio: #IAGGIU.L#
        | MNEMONIC_WITH_CONVERSION_ID WS* formatConversion WS* (SQPARO WS* changeFormat)? (CYPARO WS* formatDomain)? WS* HASH                                   #mnemonicMultiConversion

        // Esempio: #CFUTE_BIS_3[A120]#
        | MNEMONIC_WITH_FORMAT_ID WS* changeFormat WS* (CYPARO formatDomain)? WS* HASH                                                                          #mnemonicFormatted

        // Esempio: #NAPPFI{MONEY}#
        | MNEMONIC_WITH_DOMAIN_ID WS* formatDomain WS* HASH                                                                                                     #mnemonicWithDomain

        // Esempio: #UL@ARRIDAT#
        | prefixFunction MNEMONIC_FUNCTION_SUFFIX_ID                                                                                                            #mnemonicWithFunction;

// mnemonicFormat
changeFormat

        // Esempio: #CFUTE_BIS_3[A120]#
        : A POSITIVE_INTEGER WS* SQPARC                                                                                     #changeFormatANumber

        // Esempio: #CFUTE_BIS_3[A120.10]#
        | FORMAT_SUFFIX_ADOT WS* SQPARC                                                                                     #changeFormatANumberDotNumber

        // Esempio: #CFUTE_BIS_3[A(10:12)]#
        | A WS* ROPARO lowerLimit = POSITIVE_INTEGER WS* DDOT WS* upperLimit = POSITIVE_INTEGER WS* ROPARC WS* SQPARC       #changeFormatAInterval

        // Esempio: #CFUTE_BIS_3[T.60]#
        | T DOT POSITIVE_INTEGER WS* SQPARC                                                                                 #changeFormatTNumber

        // Esempio: #CFUTE_BIS_3[T]#
        | T WS* SQPARC                                                                                                      #changeFormatT

        // Esempio: #CFUTE_BIS_3[L(12:30)]#
        | L WS* ROPARO WS* blockIndex = POSITIVE_INTEGER WS* DDOT WS* blockSize = POSITIVE_INTEGER WS* ROPARC WS* SQPARC    #changeFormatLInterval

        // Esempio: #CFUTE_BIS_3[L(15)]#
        | L WS* ROPARO WS* POSITIVE_INTEGER WS* ROPARC WS* SQPARC                                                           #changeFormatLNumber

        // Esempio: #CFUTE_BIS_3[I122]#
        | I POSITIVE_INTEGER? WS* SQPARC                                                                                    #changeFormatINumber

        // Esempio: #CFUTE_BIS_3[P38]#
        | P POSITIVE_INTEGER? WS* SQPARC                                                                                    #changeFormatPNumber

        // Esempio: #CFUTE_BIS_3[E22]#
        | E POSITIVE_INTEGER WS* SQPARC                                                                                     #changeFormatENumber

        // Esempio: #CFUTE_BIS_3[N25]#
        | N POSITIVE_INTEGER WS* SQPARC                                                                                     #changeFormatNNumber

        // Esempio: #CFUTE_BIS_3[N]#
        | N WS* SQPARC                                                                                                      #changeFormatN

        // Esempio: #CFUTE_BIS_3[Z8]#
        | Z POSITIVE_INTEGER WS* SQPARC                                                                                     #changeFormatZNumber

        // Esempio: #CFUTE_BIS_3[F11.39]#
        | FORMAT_SUFFIX_FDOT WS* SQPARC                                                                                     #changeFormatFNumber

        // Esempio: #CFUTE_BIS_3[D]#
        | D WS* SQPARC                                                                                                      #changeFormatD

        // Esempio: #CFUTE_BIS_3[U82]#
        | U POSITIVE_INTEGER WS* SQPARC                                                                                     #changeFormatUNumber

        // Esempio: #CFUTE_BIS_3[C7]#
        | C POSITIVE_INTEGER WS* SQPARC                                                                                     #changeFormatCNumber;

// mnemonicConversion
formatConversion

        // Esempio: #IAGGIU.L#
        : L         #formatConversionL

        // Esempio: #IAGGIU.LN#
        | FORMATLN  #formatConversionLN

        // Esempio: #ORAINIZ.LO#
        | FORMATLO  #formatConversionLO

        // Esempio: #DESURP.N#
        | N         #formatConversionN

        // Esempio: #IAGGIU.X#
        | X         #formatConversionX;

// mnemonicDomain
formatDomain

        // Esempio: #NAPPFI{MONEY}#
        : DOMMONEY WS* CYPARC                                                                               #formatDomainMoney

        // Esempio: #NAPPFI{MONEYPOS}#
        | DOMMONEYPOS WS* CYPARC                                                                            #formatDomainMoneyPOS

        // Esempio: #NTEL{TEL}#
        | DOMTEL WS* CYPARC                                                                                 #formatDomainTel

        // Esempio: #NTEL{TELALL}#
        | DOMTELALL WS* CYPARC                                                                              #formatDomainTellAll

        // Esempio: #NFAX{FAX}#
        | DOMFAX WS* CYPARC                                                                                 #formatDomainFax

        // Esempio: #NFAX{FAXALL}#
        | DOMFAXALL WS* CYPARC                                                                              #formatDomainFaxAll

        // Esempio: #IS_HUMAN{SN}#
        | DOMSN WS* CYPARC                                                                                  #formatDomainSN

        // Esempio: #AUTOSTRADA{KM_CIPPO}#
        | DOMKMCIPPO WS* CYPARC                                                                             #formatDomainKmCippo

        // Esempio: #AUTOSTRADA{TIP_SS}#
        | DOMTIPSS WS* CYPARC                                                                               #formatDomainTipSS

        // Esempio: #AUTOSTRADA{COD_SS}#
        | DOMCODSS WS* CYPARC                                                                               #formatDomainCodSS

        // Esempio: #AUTOSTRADA{NUM_SS}#
        | DOMNUMSS WS* CYPARC                                                                               #formatDomainNumSS

        // Esempio: #AUTOSTRADA{PROG_UFF}#
        | DOMPROGUFF WS* CYPARC                                                                             #formatDomainProgUff

        // Esempio: #AUTOSTRADA{PROG_CIP}#
        | DOMPROGCIP WS* CYPARC                                                                             #formatDomainProgCIP

        // Esempio: #AUTOSTRADA{PROG_REL}#
        | DOMPROGREL WS* CYPARC                                                                             #formatDomainProgRel

        // Esempio: #AUTOSTRADA{PROG_RIF}#
        | DOMPROGRIF WS* CYPARC                                                                             #formatDomainProgRif

        // Esempio: #DATAOGGI{DATA_ELDA}#
        | DOMDATAELDA WS* CYPARC                                                                            #formatDomainDataElda

        // Esempio: #DATAOGGI{D_n_0A_nn}#
        | domdatecustom WS* CYPARC                                                                          #formatDomainCustomDate

        // Esempio: #MYQUERY{X_EXECSQL}#
        | DOMXEXECSQL WS* CYPARC                                                                            #formatDomainExecuteSQL

        // Esempio: #MYQUERY{X_EXECSQL_nC1}#
        | DOMXEXECSQLN_ID                                                                                   #formatDomainExecuteSQLC1

        // Esempio: #CFUTE{X_SQLc100}#
        | DOMXSQLC POSITIVE_INTEGER WS* CYPARC                                                              #formatDomainXSQLC

        // Esempio: #CFUTE{X_SQLc100_nC1}#
        | DOMXSQLC POSITIVE_INTEGER DOMXSQLCNNNAAA_SUFFIX_ID                                                #formatDomainXSQLCC1

        // Esempio: #CFUTE{X_SQLc4_nAEEIO_tG6315}
        | DOMXSQLC POSITIVE_INTEGER DOMXSQLCNNNAAAT_SUFFIX_ID                                               #formatDomainXSQLCCT

        // Esempio: #NAUTOSTRADA{X_DIST}#
        | DOMXDIST WS* CYPARC                                                                               #formatDomainXDist

        // Esempio: #CFUTE{X_S2N}#
        | DOMXS2N WS* CYPARC                                                                                #formatDomainXS2N

        // Esempio: #MYQUERY{X_EXECAGG}#
        | DOMXEXECAGG WS* CYPARC                                                                            #formatDomainXExecAgg

        // Esempio: #DATAOGGI{X_DATAORA}#
        | DOMXDATAORA WS* CYPARC                                                                            #formatDomainXDataOra

        // Esempio: #DATAOGGI{X_TIME}#
        | DOMXTIME WS* CYPARC                                                                               #formatDomainXTime

        // Esempio: #DATAOGGI{X_ORA}#
        | DOMXORA WS* CYPARC                                                                                #formatDomainXOra

        // Esempio: #DATAOGGI{X_MINUTI}#
        | DOMXMINUTI WS* CYPARC                                                                             #formatDomainXMinuti

        // Esempio: #DATAOGGI{X_SECONDI}#
        | DOMXSECONDI WS* CYPARC                                                                            #fomatDomainXSecondi

        // Esempio: #CFUTE{X_SUBSTR(5)}#
        | DOMXSUBSTR WS* ROPARO WS* POSITIVE_INTEGER WS* ROPARC WS* CYPARC                                  #formatDomainXSubStr

        // Esempio: #CFUTE{X_SUBSTR(5,3)}#
        | DOMXSUBSTR WS* ROPARO WS* POSITIVE_INTEGER WS* COMMA WS* POSITIVE_INTEGER WS* ROPARC WS* CYPARC   #formatDomainXSubStrInterval

        // Esempio: #CONFIG{X_PAR_My_parameter}#
        | DOMXPAR_SUFFIX_ID                                                                                 #formatDomainXPar

        // Esempio: #DATAOGGI{X_DAY}#
        | DOMXDAY WS* CYPARC                                                                                #formatDomainXDay

        // Esempio: #DATAOGGI{X_DATA_F1}#
        | DOMXDATAF1 WS* CYPARC                                                                             #formatDomainXDataF1

        // Esempio: #DATAOGGI{X_DATA_F2}#
        | DOMXDATAF2 WS* CYPARC                                                                             #formatDomainXDataF2

        // Esempio: #CFUTE{X_UPR}#
        | DOMXUPR WS* CYPARC                                                                                #formatDomainXUPR

        // Esempio: #STR10{X_IMG}#
        | DOMXIMG WS* CYPARC                                                                                #formatDomainXImg

        // Esempio: #STR15{EXT_IMG}#
        | DOMEXTIMG WS* CYPARC                                                                              #formatDomainExtImg

        // Esempio: #STR3{LOWTEXT}#
        | DOMLOWTEXT WS* CYPARC                                                                             #formatDomainLowText

        // Esempio: #STR3{CAPTEXT}#
        | DOMCAPTEXT WS* CYPARC                                                                             #formatDomainCapText

        // Esempio: #STR3{CAPALLTEXT}#
        | DOMCAPALLTEXT WS* CYPARC                                                                          #formatDomainCapAllText

        // Esempio: #STR3{UPPERTEXT}#
        | DOMUPPERTEXT WS* CYPARC                                                                           #formatDomainUpperText

        // Esempio: #STR3{HASHFILE}#
        | DOMHASHFILE WS* CYPARC                                                                            #formatDomainHashFile

        // Esempio: #TOT50{FINDHASH}#
        | DOMFINDHASH WS* CYPARC                                                                            #formatDomainFindHash;


prefixFunction

        // Esempio: #CO@MNEMO#
        : HASH MNEFOOCO     #prefixFunctionCO

        // Esempio: #UL@CFUTE#
        | HASH MNEFOOUL     #prefixFunctionUL

        // Esempio: #PR@IAGGIU#
        | HASH MNEFOOPR     #prefixFunctionPR

        // Esempio: #NO@DESURP#
        | HASH MNEFOONO     #prefixFunctionNO;

// TEST:
// \5+#dfas_OR_asdda23_AND_asd23_EQ_#-#mnemonic(sda23)#+#sdf_AND_OR_[A10]#+#asd[A12.23]#+#test_OR__AND[A(12:10)]#+#sdsdf_OR_[T.23]#*#asdg_OR_[T]#+#su_23__AND_[L(12:30)]#+#gv_AND_I_L[L(15)]#+#njks_OR__23[P38]#+#_OR__AND_P38[E22]#-#_OR__AND_P38[N25]#^#_OR__AND_P38[Z82]#/#_OR__AND_P38[F11.39]#+#_OR__AND_P38[D]#+#_OR__AND_P38[U283]#+#_OR__AND_P38[C7]#\
// %I2 %sqr(2)_EQ_2*2+#mnemonic[A10.10]#_AND_2*3_LT_2*3.01_OR_1_EQ_sqrt(#mnemonic[A(12:10)]#)_AND_1_GT_#mnemonic[T.230]#_AND_#mnemonica_2[T]#_NE_1_OR_1+1_GT_#mnemonic[L(12:30)]#_AND_#mnemonicAd[L(12)]#_EQ_1_AND_#MNEMONIC[I23]#_EQ_#MNEMONIC[P38]#_AND_#MNEMONIC[P38]#_EQ_#MNEMONIC[E22]#_AND_#MNEMONIC[N25]#_EQ_#MNEMONIC[Z8]#_AND_#MNEMONIC[F11.39]#_EQ_#MNEMONIC[D]#_AND_#MNEMONIC[U82]#_EQ_#MNEMONIC[C7]#%
// %I2 %\sqr(2)\.EQ.\2*2+#mnemonic[A10.10]#\.AND.\2*3\.LT.\2*3.01\.OR.0.12.EQ.\sqrt(#mnemonic[A(12:10)]#)\.AND.1.GT.#mnemonic[T.230]#.AND.#mnemonica_2[T]#.NE.1.OR.\1+1\.GT.#mnemonic[L(12:30)]#.AND.#mnemonicAd[L(12)]#.EQ.1.AND.#MNEMONIC[I23]#.EQ.#MNEMONIC[P38]#.AND.#MNEMONIC[P38]#.EQ.#MNEMONIC[E22]#.AND.#MNEMONIC[N25]#.EQ.#MNEMONIC[Z8]#.AND.#MNEMONIC[F11.39]#.EQ.#MNEMONIC[D]#.AND.#MNEMONIC[U82]#.EQ.#MNEMONIC[C7]#%
// %I2 %sqr(2)§EQ§2*2+#mnemonic[A10.10]#§AND§2*3§LT§2*3.01§OR§1§EQ§sqrt(#mnemonic[A(12:10)]#)§AND§1§GT§#mnemonic[T.230]#§AND§#mnemonica_2[T]#§NE§1§OR§1+1§GT§#mnemonic[L(12:30)]#§AND§#mnemonicAd[L(12)]#§EQ§1§AND§#MNEMONIC[I23]#§EQ§#MNEMONIC[P38]#§AND§#MNEMONIC[P38]#§EQ§#MNEMONIC[E22]#§AND§#MNEMONIC[N25]#§EQ§#MNEMONIC[Z8]#§AND§#MNEMONIC[F11.39]#§EQ§#MNEMONIC[D]#§AND§#MNEMONIC[U82]#§EQ§#MNEMONIC[C7]#%
// %I2 %\sqr(2)\.EQ.\2*2+#mnemonic[A10.10]#\.AND.\2*3\.LT.\2*3.01\.OR.1.EQ.\sqrt(#mnemonic[A(12:10)]#)\.AND.1.GT.\#mnemonic[T.230]#\.AND.\#mnemonica_2[T]#\.NE.1.OR.\1+1\.GT.\#mnemonic[L(12:30)]#\.AND.\#mnemonicAd[L(12)]#\.EQ.1.AND.\#MNEMONIC[I23]#\.EQ.\#MNEMONIC[P38]#\.AND.\#MNEMONIC[P38]#\.EQ.\#MNEMONIC[E22]#\.AND.\#MNEMONIC[N25]#\.EQ.\#MNEMONIC[Z8]#\.AND.\#MNEMONIC[F11.39]#\.EQ.\#MNEMONIC[D]#\.AND.\#MNEMONIC[U82]#\.EQ.\#MNEMONIC_OR_[C7]#+#CO@_COUL_23_#\.OR.\#UL@my_mnemonic#/2\.EQ.\#NO@my_other_mnemonic_23#\.OR.\#my_mnemonic.L#/2\.EQ.\#my_other_mnemonic_23.LN#\.OR.\#my_mnemonic.LO#/2\.EQ.\#my_other_mnemonic_23.N#\.OR.\#UL@my_mnemonic#/2\.EQ.\#my_other_mnemonic_23.X#\.OR.\#my_mnemonic{MONEY}#/2\.EQ.\#my_other_mnemonic_23{MONEYPOS}#\%
// <?compo [JOIN]#mnemonic#,#other_mnemonic#,#something[A10]#,#DATAOGGI{X_DAY}#=1,12,"=something|!=something_else|=|!=else",#SPECIAL_DAY{X_DAY}#,"" ?>jh ji u@#jkh# jkibj <?compo \%#mnemonico#.EQ.23.AND.#mnemonico_b#.LT.23.768%2\\++1\ ?> <?compo [ENDJOIN] ?>
// <?compo [INPSTR]"kj hu6)(/(=? yugfy <<> tty"#STR5354# ?> asds <?compo [TOSTR]#STR45#="..asd2£E$\"fsd." ?> <?compo [JOIN]#CODTOR_G#="=GARA01|=GARA02" ?> <?compo [TOSTR]#STR23#=#nome_mnemonico# ?> asd <?compo [CATSTR]#STR45#=#nome_mnemonico#,"asd d..." ?> <?compo [CATSTR]#STR23#="asd asd ...",#nome_mnemonico# ?> sad ss eio j <?compo [TRAON]x ?> <?compo [TRAOFF] ?>
// [IF] %sqr(2)§EQ§2*2+#mnemonic[A10.10]#§AND§2*3§LT§2*3.01§OR§1§EQ§sqrt(#mnemonic[A(12:10)]#)§AND§sqr(#my_mnemonic{X_SUBSTR(12,10)}#)§LT§#my_other_mnemonic#§OR§#my_mnemonic{X_SQLc100_nC1}#§LT§#my_other_mnemonic{D_0_Aa_00}#%
// <?compo %I2 %\sqr(2)\.EQ.\2*2+#mnemonic[A10.10]#\.AND.\2*3\.LT.\2*3.01\.OR.1.EQ.\sqrt(#mnemonic[A(12:10)]#)\.AND.1.GT.\#mnemonic[T.230]#\.AND.\#mnemonica_2[T]#\.NE.1.OR.\1+1\.GT.\#mnemonic[L(12:30)]#\.AND.\#mnemonicAd[L(12)]#\.EQ.1.AND.\#MNEMONIC[I23]#\.EQ.\#MNEMONIC[P38]#\.AND.\#MNEMONIC[P38]#\.EQ.\#MNEMONIC[E22]#\.AND.\#MNEMONIC[N25]#\.EQ.\#MNEMONIC[Z8]#\.AND.\#MNEMONIC[F11.39]#\.EQ.\#MNEMONIC[D]#\.AND.\#MNEMONIC[U82]#\.EQ.\#MNEMONIC_OR_[C7]#+#_COUL_23_#\.OR.\#my_mnemonic#/2\.EQ.\#my_other_mnemonic_23#\.OR.\#my_mnemonic.L#/2\.EQ.\#my_other_mnemonic_23.LN#\.OR.\#my_mnemonic.LO#/2\.EQ.\#my_other_mnemonic_23.N#\.OR.\#my_mnemonic#/2\.EQ.\#my_other_mnemonic_23.X#\.OR.\#my_mnemonic{MONEY}#/2\.EQ.\#my_other_mnemonic_23{MONEYPOS}#\.OR.#my_mnemonic{X_SQLc4_nAAA_t1004}#.EQ.5% ?>