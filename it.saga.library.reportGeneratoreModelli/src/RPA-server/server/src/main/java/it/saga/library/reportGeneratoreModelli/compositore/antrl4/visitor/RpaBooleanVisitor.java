package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.ParseTree;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaWarningType;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaBooleanVisitorFormatter;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaWarningMessages;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.BooleanStatementComparisonContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.BooleanStatementComplexContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ComparisonStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ComparisonTermMathOperationStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ComparisonTermMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ComparisonTermNegativeNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ComparisonTermPositiveNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ComparisonTermStringContext;

import java.sql.Connection;
import java.util.Stack;

public class RpaBooleanVisitor extends RpaIfVisitor {

    public RpaBooleanVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {
        super(conn, scope, mainCompositore, parentNode, childNode);
    }

    @Override
    public RpaValue<Boolean> visitBooleanStatementComparison(BooleanStatementComparisonContext context) {

        // Ritorno direttamente il confronto (es: EQUAL, LESS_THAN, etc...) tra termini
        return (RpaValue<Boolean>) visit(context.comparisonStatement());

    }

    @Override
    public RpaValue<?> visitBooleanStatementComplex(BooleanStatementComplexContext context) {

        // Eseguo l'operazione logica (AND e OR) tra i due valori booleani dell'espression
        RpaValue<Boolean> leftBooleanTermValue     = (RpaValue<Boolean>) visit(context.leftBooleanStatement);
        RpaValue<Boolean> rightBooleanTermValue    = (RpaValue<Boolean>) visit(context.rightBooleanStatement);

        Boolean leftBooleanTerm     = leftBooleanTermValue.getValue();
        Boolean rightBooleanTerm    = rightBooleanTermValue.getValue();

        String  logicopString   = context.LOGICOP().getText().replaceAll("\\.", "");
        LOGICOP logicop         = LOGICOP.valueOf(logicopString);

        Boolean booleanResult = null;

        switch (logicop) {

            case AND:

                booleanResult = leftBooleanTerm && rightBooleanTerm;

                break;

            case OR:

                booleanResult = leftBooleanTerm || rightBooleanTerm;

                break;

        }

        return new RpaValue<Boolean>(booleanResult);

    }

    @Override
    public RpaValue<?> visitComparisonStatement(ComparisonStatementContext context) {

        Boolean     comparisonResult        = null;
        String      compareopString         = context.COMPAREOP().getText().replaceAll("\\.", "");
        RpaBooleanVisitor.COMPAREOP compareop  = RpaBooleanVisitor.COMPAREOP.valueOf(compareopString);


        // ### Controllo e applico la stessa conversione (sinistra) ai due termini ###

        RpaBooleanVisitorFormatter booleanVisitorFormatter = new RpaBooleanVisitorFormatter(mainCompositore, context);

        ParseTree leftComparisonParseTree   = booleanVisitorFormatter.getLeftComparisonParseTree();
        ParseTree rightComparisonParseTree  = booleanVisitorFormatter.getRightComparisonParseTree();


        // ### Estraggo i valori dei due termini ###

        RpaValue<?> leftComparisonTermValue    = visit(leftComparisonParseTree);
        RpaValue<?> rightComparisonTermValue   = visit(rightComparisonParseTree);

        // Estraggo i termini
        Object leftTerm     = extractTerm(leftComparisonTermValue);
        Object rightTerm    = extractTerm(rightComparisonTermValue);


        // ### Controllo se fare un confronto tra numeri o stringhe (in alternativa lancio un errore) ###

        // Eseguo il confronto tra stringhe
        if (leftTerm instanceof String && rightTerm instanceof String) {

            String leftComparisonTerm   = (String) leftTerm;
            String rightComparisonTerm  = (String) rightTerm;

            comparisonResult = compareString(compareop, leftComparisonTerm, rightComparisonTerm);

        }

        // Eseguo il confronto tra due numeri
        else if (leftTerm instanceof Number && rightTerm instanceof Number) {

            Number leftComparisonTermNumber     = (Number) leftTerm;
            Number rightComparisonTermNumber    = (Number) rightTerm;

            comparisonResult = compareNumber(compareop, leftComparisonTermNumber, rightComparisonTermNumber);

        }

        // Per confronto tipi diversi, faccio un confronto tra stringhe
        else {

            String leftComparisonTerm;
            String rightComparisonTerm;

            // Notifico il confronto tra tipi diversi
            String leftClassName;
            String rightClassName;

            if (leftTerm == null) {

                leftClassName = "null";

            } else {

                leftClassName = leftTerm.getClass().getName();

            }

            if (rightTerm == null) {

                rightClassName = null;

            } else {

                rightClassName = rightTerm.getClass().getName();

            }

            String infoMessage  =
                    "[IF] Per la comparazione " + context.getText() +
                    " vi è un confronto tra TIPI DIVERSI: " +
                    leftClassName + " e " + rightClassName +
                    ". Usare le formattazioni!";

            mainCompositore.getDebugMessages().print(infoMessage);

            /*
            RpaWarningType  warningType     = RpaWarningType.DIFFERENT_TYPES_COMPARISON;
            RpaWarningMessages warningMessages = mainCompositore.getWarningMessages();
            warningMessages.print(warningType, infoMessage);
            */

            // Controllo se il valore di sinistra è "null"
            if (leftTerm == null) {

                leftComparisonTerm = "";

            } else {

                leftComparisonTerm = leftTerm.toString();

            }

            // Controllo se il valore di destra è "null"
            if (rightTerm == null) {

                rightComparisonTerm = "";

            } else {

                rightComparisonTerm = rightTerm.toString();

            }

            comparisonResult = compareString(compareop, leftComparisonTerm, rightComparisonTerm);

        }

        booleanVisitorFormatter.clear();

        return new RpaValue<Boolean>(comparisonResult);

    }

    @Override
    public RpaValue<?> visitComparisonTermMnemonic(ComparisonTermMnemonicContext context) {

        return visit(context.mnemonic());

    }

    @Override
    public RpaValue<Number> visitComparisonTermPositiveNumber(ComparisonTermPositiveNumberContext context) {

        RpaValue<Number> newValue;

        String numberString = context.getText();

        if (RpaNumberUtils.isInteger(numberString)) {

            if (RpaNumberUtils.isIntegerWithDotZero(numberString)) {

                numberString = RpaNumberUtils.integerWithoutDotZero(numberString);

            }

            newValue = new RpaValue<Number>(Integer.valueOf(numberString));

        } else {

            newValue = new RpaValue<Number>(Double.valueOf(numberString));

        }

        return newValue;

    }

    @Override
    public RpaValue<Number> visitComparisonTermNegativeNumber(ComparisonTermNegativeNumberContext context) {

        RpaValue<Number> newValue;

        String numberString = context.getText();

        if (RpaNumberUtils.isInteger(numberString)) {

            if (RpaNumberUtils.isIntegerWithDotZero(numberString)) {

                numberString = RpaNumberUtils.integerWithoutDotZero(numberString);

            }

            newValue = new RpaValue<Number>(Integer.valueOf(numberString));

        } else {

            newValue = new RpaValue<Number>(Double.valueOf(numberString));

        }

        return newValue;

    }

    @Override
    public RpaValue<?> visitComparisonTermMathOperationStatement(ComparisonTermMathOperationStatementContext context) {

        RpaValue newValue          = new RpaValue<Number>(null);
        RpaValue<Number> mathValue = (RpaValue<Number>) visit(context.mathOperationStatement());

        if (!isMathOperationValid) {

            return newValue;

        }

        // Riformatto il risultato
        if (context.operationFormat() != null && context.operationFormat().children != null) {

            numberToFormat                  = Double.valueOf(mathValue.getValue().toString());
            RpaValue<RpaFormat> valueFormat = visitOperationFormat(context.operationFormat());
            RpaFormat format                = valueFormat.getValue();
            String comparisonValue          = format.getComparisonValue();
            Object result = null;

            if (RpaNumberUtils.isInteger(comparisonValue)) {

                if (RpaNumberUtils.isIntegerWithDotZero(comparisonValue)) {

                    result = RpaNumberUtils.integerWithoutDotZero(comparisonValue);

                } else {

                    result = Integer.valueOf(comparisonValue);

                }

            } else if (RpaNumberUtils.isDouble(comparisonValue)) {

                result = Double.valueOf(comparisonValue);

            } else {

                result = comparisonValue;

            }

            newValue.setValue(result);

            return newValue;

        } else {

            newValue.setValue(mathValue.getValue());

            return newValue;

        }

    }

    @Override
    public RpaValue<String> visitComparisonTermString(ComparisonTermStringContext context) {

        // Elimino dalla stringa i doppi apici "
        String string   = context.STRING().getText();
        string          = string.substring(1, string.length() - 1);

        return new RpaValue<String>(string);

    }

    private Boolean compareString(COMPAREOP compareop, String leftString, String rightString) {

        int stringComparisonResultCode = leftString.compareTo(rightString);

        // Nota: La "String.compareTo" ritorna un valore per cui se è:
        //
        //      0: Le due stringhe sono uguali
        //
        //      Positive: La prima stringa è lessicograficamente maggiore della seconda
        //
        //      Negative: La prima stringa è lessicograficamente minore della seconda
        //

        switch (compareop) {

            case EQ:

                return stringComparisonResultCode == 0;

            case NE:

                return stringComparisonResultCode != 0;

            case LT:

                return stringComparisonResultCode < 0;

            case GT:

                return stringComparisonResultCode > 0;

            case LE:

                return stringComparisonResultCode <= 0;

            case GE:

                return stringComparisonResultCode >= 0;

        }

        return null;

    }

    private Boolean compareNumber(COMPAREOP compareop, Number leftNumber, Number rightNumber) {

        double leftDouble   = leftNumber.doubleValue();
        double rightDouble  = rightNumber.doubleValue();

        switch (compareop) {

            case EQ:

                return leftDouble == rightDouble;

            case NE:

                return leftDouble != rightDouble;

            case LT:

                return leftDouble < rightDouble;

            case GT:

                return leftDouble > rightDouble;

            case LE:

                return leftDouble <= rightDouble;

            case GE:

                return leftDouble >= rightDouble;

        }

        return null;

    }

    private Object extractTerm(RpaValue<?> value) throws ParseCancellationException {

        // Estraggo il termine di "Value"
        Object term = value.getValue();

        // System.out.println("Object value term: " + term);

        // Controllo se il termine è "null"
        if (term == null) {

            return null;

        }

        // Controllo se il termine è una stringa
        else if (term instanceof String) {

            String stringTerm = (String) term;

            if (RpaNumberUtils.isInteger(stringTerm)) {

                if (RpaNumberUtils.isIntegerWithDotZero(stringTerm)) {

                    stringTerm = RpaNumberUtils.integerWithoutDotZero(stringTerm);

                }

                return Integer.valueOf(stringTerm);

            } else if (RpaNumberUtils.isDouble(stringTerm)) {

                return Double.valueOf(stringTerm);

            } else {

                return stringTerm;

            }

        }

        // Controllo se il termine è un numero
        else if (term instanceof Number) {

            return term;

        }

        // Controllo se il termine è un "mnemonico vuoto"
        else if (term instanceof RpaMnemonicEmpty) {

            return ((RpaMnemonicEmpty) term).getComparisonValue();

        }

        // Controllo se il termine è un qualsiasi mnemonico
        else if (term instanceof RpaAbstractMnemonic) {

            RpaAbstractMnemonic mnemonic = (RpaAbstractMnemonic) term;

            // Controllo se il termine è un "mnemonico" il cui valore è "null"
            if (mnemonic.getComparisonValue() == null) {
                // if (mnemonic.getValue() == null) {

                return null;

            }

            // Controllo se il termine è un "mnemonico" di tipo "numero"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_NUMBER) {

                if (RpaNumberUtils.isInteger(mnemonic.getComparisonValue())) {

                    String comparisonValue = mnemonic.getComparisonValue();

                    if (RpaNumberUtils.isIntegerWithDotZero(comparisonValue)) {

                        comparisonValue = RpaNumberUtils.integerWithoutDotZero(comparisonValue);

                    }

                    return Integer.valueOf(comparisonValue);

                } else {

                    return Double.valueOf(mnemonic.getComparisonValue());

                }

            }

            // Controllo se il termine è un "mnemonico" di tipo "stringa"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_STRING) {

                // return mnemonic.getComparisonValue();
                String stringTerm = mnemonic.getComparisonValue();

                if (RpaNumberUtils.isInteger(stringTerm)) {

                    if (RpaNumberUtils.isIntegerWithDotZero(stringTerm)) {

                        stringTerm = RpaNumberUtils.integerWithoutDotZero(stringTerm);

                    }

                    // return Integer.valueOf(stringTerm);
                    return Long.valueOf(stringTerm);

                } else if (RpaNumberUtils.isDouble(stringTerm)) {

                    return Double.valueOf(stringTerm);

                } else {

                    return stringTerm;

                }

            }

            // Controllo se il termine è un "mnemonico" di tipo "tabulato"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_TABULATION) {

                // return Integer.valueOf(mnemonic.getComparisonValue());
                return mnemonic.getComparisonValue();

            }

            // Controllo se il termine è un "mnemonico" di tipo "booleano"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_BOOLEAN) {

                if (mnemonic.getComparisonValue() == null || mnemonic.getComparisonValue().isEmpty()) {

                    return null;

                } else {

                    return Integer.valueOf(mnemonic.getComparisonValue());

                }

            }

            // Controllo se il termine è un "mnemonico" di tipo "data"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_DATE) {

                String comparisonValue = mnemonic.getComparisonValue();

                if (comparisonValue != null && !comparisonValue.isEmpty()) {

                    return Integer.valueOf(comparisonValue);

                } else {

                    return null;

                }

            }

            // Controllo se il termine è un "mnemonico" di tipo "timestamp"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_TIMESTAMP) {

                String comparisonValue = mnemonic.getComparisonValue();

                if (comparisonValue != null && !comparisonValue.isEmpty()) {

                    return Integer.valueOf(comparisonValue);

                } else {

                    return null;

                }

            }

            // Controllo se il termine è un "mnemonico" di tipo "money"
            else if (mnemonic.getType() == RpaAbstractMnemonic.TYPE_MONEY) {

                String comparisonValue = mnemonic.getComparisonValue();

                if (comparisonValue != null && !comparisonValue.isEmpty()) {

                    return Double.valueOf(comparisonValue);

                } else {

                    return null;

                }

            }

            else {

                throw new ParseCancellationException("Impossibile determinare il valore di " + term + " nella condizione booleana");

            }

        } else {

            throw new ParseCancellationException("Impossibile determinare il valore di " + term + " nella condizione booleana");

        }

    }

    public enum LOGICOP {

        AND,
        OR,

    }

    public enum COMPAREOP {

        EQ,
        NE,
        GT,
        GE,
        LT,
        LE

    }

}
