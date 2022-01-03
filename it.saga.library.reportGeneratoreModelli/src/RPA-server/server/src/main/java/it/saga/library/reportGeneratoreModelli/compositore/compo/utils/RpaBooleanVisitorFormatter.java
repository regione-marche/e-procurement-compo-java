package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.extern.rpa_libs.antlr.v4.runtime.ANTLRInputStream;
import it.saga.extern.rpa_libs.antlr.v4.runtime.CommonTokenStream;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.ParseTree;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicSTR;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaLexer;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaBooleanVisitorFormatter {

    // Link: https://regex101.com/r/p98Tbc/6
    public static final String MNEMONIC_FORMAT_REGEX    = "^# *([A-Za-z0-9_]+) *((\\(.+\\))? *(\\.[A-Z]{1,2})? *(\\[.+\\])? *(\\{.+\\})?) *#$";

    // Link: https://regex101.com/r/RH8Bep/2
    public static final String FORMAT_TERMS_REGEX       = "^ *(\\(.+\\))? *(\\.[A-Z]{1,2})? *(\\[.+\\])? *(\\{.+\\})? *$";

    // Link: https://regex101.com/r/HwufIG/1
    // Link: https://regex101.com/r/U52Ph0/2
    // Link: https://regex101.com/r/MVa53G/1
    private static final String[] CONVERSIONS_TO_AVOID_REGEXPS = {
            "\\[ *A[0-9]+ *\\]",
            "\\[ *A *\\( *[0-9]+ *: *[0-9]+ *\\) *\\]",
            "\\[ *A[0-9]+\\.[0-9]+ *\\]"
    };

    private RpaMainCompositore mainCompositore;
    private RpaParser.ComparisonStatementContext context;
    private ParseTree leftComparisonParseTree;
    private ParseTree rightComparisonParseTree;

    private int                     junkMnemonicIndex;
    private RpaVariablesManager.Type   junkMnemonicType;

    public RpaBooleanVisitorFormatter(RpaMainCompositore mainCompositore, RpaParser.ComparisonStatementContext context) {

        this.mainCompositore = mainCompositore;
        this.context = context;
        createNewPathTrees();

    }

    private void createNewPathTrees() {

        // ### Controllo se applicare la stessa conversione (sinistra) a due mnemonici ###

        // Controllo se ho a sinistra un mnemonico o una operazione
        ParseTree leftTermParseTree = context.leftComparisonTerm;

        boolean isLeftTermMnemonic  = leftTermParseTree instanceof RpaParser.ComparisonTermMnemonicContext;
        boolean isLeftTermOperation = leftTermParseTree instanceof RpaParser.ComparisonTermMathOperationStatementContext;

        // Controllo se a destra ho un mnemonico o una operazione
        ParseTree rightTermParseTree = context.rightComparisonTerm;

        boolean isRightTermMnemonic     = rightTermParseTree instanceof RpaParser.ComparisonTermMnemonicContext;
        boolean isRightTermOperation    = rightTermParseTree instanceof RpaParser.ComparisonTermMathOperationStatementContext;

        String leftTermText     = leftTermParseTree.getText();
        String rightTermText    = rightTermParseTree.getText();

        if (isLeftTermMnemonic) {

            // Controllo se ho dei termini di conversione da applicare a destra
            Matcher leftMnemonicTextMatcher = Pattern.compile(MNEMONIC_FORMAT_REGEX).matcher(leftTermText);
            leftMnemonicTextMatcher.find();

            boolean hasLeftMnemonicFormat = false;

            for (int i = 3; i <= leftMnemonicTextMatcher.groupCount(); i ++) {

                String leftMnemonicFormat = leftMnemonicTextMatcher.group(i);

                if (leftMnemonicFormat != null) {

                    hasLeftMnemonicFormat = true;
                    break;

                }

            }

            // Controllo se applicare il formato ad un mnemonico di destra
            if (hasLeftMnemonicFormat && isRightTermMnemonic) {

                String leftConversion   = leftMnemonicTextMatcher.group(5);
                String leftDomain       = leftMnemonicTextMatcher.group(6);

                boolean isLeftConversionPresent = leftConversion != null && !leftConversion.isEmpty();
                boolean isLeftDomainPresent     = leftDomain != null && !leftDomain.isEmpty();

                if (isLeftConversionPresent || isLeftDomainPresent) {

                    leftComparisonParseTree = context.leftComparisonTerm;
                    rightComparisonParseTree = formatTermsFromMnemonicToMnemonic(leftTermText, rightTermText);

                } else {

                    leftComparisonParseTree     = context.leftComparisonTerm;
                    rightComparisonParseTree    = context.rightComparisonTerm;

                }

            }

            // Controllo se applicare il formato ad una operazione di destra
            else if (hasLeftMnemonicFormat && isRightTermOperation) {

                RpaParser.ComparisonTermMathOperationStatementContext rightMathTermContext =
                        (RpaParser.ComparisonTermMathOperationStatementContext) rightTermParseTree;

                String operationFormatText = rightMathTermContext.operationFormat().getText();

                if (operationFormatText == null || operationFormatText.isEmpty()) {

                    String operationText = rightMathTermContext.getText();

                    String  leftConversion          = leftMnemonicTextMatcher.group(5);
                    boolean isLeftConversionPresent = leftConversion != null && !leftConversion.isEmpty();

                    if (isLeftConversionPresent) {

                        // Controllo se la conversione è da portare a destra
                        boolean isConversionValidToSwap = isConversionValidToSwap(leftConversion);

                        if (isConversionValidToSwap) {

                            leftComparisonParseTree = context.leftComparisonTerm;
                            rightComparisonParseTree = formatTermsFromMnemonicToOperation(leftTermText, operationText);

                        } else {

                            leftComparisonParseTree     = context.leftComparisonTerm;
                            rightComparisonParseTree    = context.rightComparisonTerm;

                        }

                    } else {

                        leftComparisonParseTree     = context.leftComparisonTerm;
                        rightComparisonParseTree    = context.rightComparisonTerm;

                    }

                } else {

                    leftComparisonParseTree     = context.leftComparisonTerm;
                    rightComparisonParseTree    = context.rightComparisonTerm;

                }

            }

            else {

                leftComparisonParseTree     = context.leftComparisonTerm;
                rightComparisonParseTree    = context.rightComparisonTerm;

            }

        }

        else if (isLeftTermOperation) {

            // Controllo se ho dei termini di conversione da applicare a destra
            RpaParser.ComparisonTermMathOperationStatementContext leftMathTermContext =
                    (RpaParser.ComparisonTermMathOperationStatementContext) leftTermParseTree;

            String leftFormatText = leftMathTermContext.operationFormat().getText();

            boolean hasLeftOperationFormat = leftFormatText != null && !leftFormatText.isEmpty();

            // Controllo se applicare il formato ad un mnemonico di destra
            if (hasLeftOperationFormat && isRightTermMnemonic) {

                leftComparisonParseTree     = context.leftComparisonTerm;
                rightComparisonParseTree    = formatTermsFromOperationToMnemonic(leftFormatText, rightTermText);

            }

            // Controllo se applicarei il formato ad una operazione a destra
            else if (hasLeftOperationFormat && isRightTermOperation) {

                RpaParser.ComparisonTermMathOperationStatementContext rightOperationContext =
                        (RpaParser.ComparisonTermMathOperationStatementContext) rightTermParseTree;

                String  rightOperationFormat        = rightOperationContext.operationFormat().getText();
                boolean hasRightOperationFormat     = rightOperationFormat != null && !rightOperationFormat.isEmpty();

                if (!hasRightOperationFormat) {

                    // Controllo se la conversione è da portare a destra
                    boolean isConversionValidToSwap = isConversionValidToSwap(leftFormatText);

                    if (isConversionValidToSwap) {

                        leftComparisonParseTree = context.leftComparisonTerm;
                        rightComparisonParseTree = formatTermsFromOperationToOperation(leftFormatText, rightTermText);

                    } else {

                        leftComparisonParseTree     = context.leftComparisonTerm;
                        rightComparisonParseTree    = context.rightComparisonTerm;

                    }

                } else {

                    leftComparisonParseTree     = context.leftComparisonTerm;
                    rightComparisonParseTree    = context.rightComparisonTerm;

                }

            }

            else {

                leftComparisonParseTree     = context.leftComparisonTerm;
                rightComparisonParseTree    = context.rightComparisonTerm;

            }

        }

        else {

            leftComparisonParseTree     = context.leftComparisonTerm;
            rightComparisonParseTree    = context.rightComparisonTerm;

        }

    }

    /**
     * Elimina il mnemonico utilizzato per le conversione delle costanti
     */
    public void clear() {

        if (junkMnemonicType != null) {

            // RpaVariablesManager RpaVariablesManager = RpaVariablesManager.getVariablesManager();
            RpaVariablesManager rpaVariablesManager = mainCompositore.getVariablesManager();
            rpaVariablesManager.removeVariable(junkMnemonicType, junkMnemonicIndex);

        }

    }

    public ParseTree getLeftComparisonParseTree() {

        return leftComparisonParseTree;

    }

    public ParseTree getRightComparisonParseTree() {

        return rightComparisonParseTree;

    }

    private ParseTree formatTermsFromMnemonicToMnemonic(String leftTerm, String rightTerm) {

        ParseTree newParseTree;

        Matcher leftMatcher     = Pattern.compile(MNEMONIC_FORMAT_REGEX).matcher(leftTerm);
        Matcher rightMatcher    = Pattern.compile(MNEMONIC_FORMAT_REGEX).matcher(rightTerm);

        leftMatcher.find();
        rightMatcher.find();

        // Estraggo dominio {} e conversione [] del mnemonico di sinistra
        String leftConversion   = leftMatcher.group(5);
        String leftDomain       = leftMatcher.group(6);

        // Controllo se la conversione è da portare a destra
        boolean isConversionValidToSwap = isConversionValidToSwap(leftConversion);

        // Ricostruisco il termine di destra aggiungendo dominio {} e conversione [] del sinistro nel caso mancassero
        String newRightTerm = "#" + rightMatcher.group(1);

        for (int rightGroupIndex = 3; rightGroupIndex <= rightMatcher.groupCount(); rightGroupIndex ++) {

            String rightGroup = rightMatcher.group(rightGroupIndex);

            if (rightGroupIndex == 5 && rightGroup == null && leftConversion != null && isConversionValidToSwap) {

                newRightTerm += leftConversion;

            }

            else if (rightGroupIndex == 6 && rightGroup == null && leftDomain != null) {

                newRightTerm += leftDomain;

            }

            else {

                if (rightGroup != null) {

                    newRightTerm += rightGroup;

                }

            }

        }

        newRightTerm += "#";

        // Definisco il termine di destra come istruzione
        newRightTerm = "<?compo " + newRightTerm + " ?>";

        // Rigenero il Parse-Tree
        // RpaLexer lexer       = new RpaLexer(CharStreams.fromString(newRightTerm));
        RpaLexer lexer       = new RpaLexer(new ANTLRInputStream(newRightTerm));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        RpaParser parser      = new RpaParser(tokenStream);
        RpaParser.CodeContext codeContext = parser.code();

        newParseTree = codeContext.instruction(0).statement().mnemonic();

        return newParseTree;

    }

    private ParseTree formatTermsFromOperationToMnemonic(String leftFormatTerms, String rightTerm) {

        ParseTree newParseTree;

        Matcher leftMatcher     = Pattern.compile(FORMAT_TERMS_REGEX).matcher(leftFormatTerms);
        Matcher rightMatcher    = Pattern.compile(MNEMONIC_FORMAT_REGEX).matcher(rightTerm);

        leftMatcher.find();
        rightMatcher.find();

        // Estraggo dominio {} e conversione [] dai termini di sinistra
        String leftConversion   = leftMatcher.group(3);
        String leftDomain       = leftMatcher.group(4);

        // Controllo se la conversione è da portare a destra
        boolean isConversionValidToSwap = isConversionValidToSwap(leftConversion);

        // Ricostruisco il termine di destra aggiungendo dominio {} e conversione [] del sinistro nel caso mancassero
        String newRightTerm = "#" + rightMatcher.group(1);

        for (int rightGroupIndex = 3; rightGroupIndex <= rightMatcher.groupCount(); rightGroupIndex ++) {

            String rightGroup = rightMatcher.group(rightGroupIndex);

            if (rightGroupIndex == 5 && rightGroup == null && leftConversion != null && isConversionValidToSwap) {

                newRightTerm += leftConversion;

            }

            else if (rightGroupIndex == 6 && rightGroup == null && leftDomain != null) {

                newRightTerm += leftDomain;

            }

            else {

                if (rightGroup != null) {

                    newRightTerm += rightGroup;

                }

            }

        }

        newRightTerm += "#";

        // Definisco il termine di destra come istruzione
        newRightTerm = "<?compo " + newRightTerm + " ?>";

        // Rigenero il Parse-Tree
        // RpaLexer              lexer       = new RpaLexer(CharStreams.fromString(newRightTerm));
        RpaLexer              lexer       = new RpaLexer(new ANTLRInputStream(newRightTerm));
        CommonTokenStream       tokenStream = new CommonTokenStream(lexer);
        RpaParser             parser      = new RpaParser(tokenStream);
        RpaParser.CodeContext codeContext = parser.code();

        newParseTree = codeContext.instruction(0).statement().mnemonic();

        return newParseTree;


    }

    private ParseTree formatTermsFromMnemonicToOperation(String leftTerm, String rightTerm) {

        // Nota: Il termine di destra è una operazione senza formattazione

        ParseTree newParseTree;

        Matcher leftMatcher = Pattern.compile(MNEMONIC_FORMAT_REGEX).matcher(leftTerm);
        leftMatcher.find();

        // Estraggo la conversione [] dal termine di sinistra
        String leftConversion = leftMatcher.group(5);

        // Aggiungo la conversione al termine di destra
        String newRightTerm = rightTerm.substring(0, rightTerm.length() - 1);
        newRightTerm        = newRightTerm + leftConversion + "\\";

        // Definisco il termine di destra come istruzione
        newRightTerm = "<?compo " + newRightTerm + " ?>";

        // Rigenero il Parse-Tree
        // RpaLexer              lexer       = new RpaLexer(CharStreams.fromString(newRightTerm));
        RpaLexer              lexer       = new RpaLexer(new ANTLRInputStream(newRightTerm));
        CommonTokenStream       tokenStream = new CommonTokenStream(lexer);
        RpaParser             parser      = new RpaParser(tokenStream);
        RpaParser.CodeContext codeContext = parser.code();

        newParseTree = codeContext.instruction(0).statement().operationStatement();

        return newParseTree;

    }

    private ParseTree formatTermsFromMnemonicToConstant(String leftTerm, String rightTerm) {

        ParseTree newParseTree;
        RpaVariablesManager rpaVariablesManager = mainCompositore.getVariablesManager();
        // RpaVariablesManager rpaVariablesManager = rpaVariablesManager.getVariablesManager();

        Matcher leftMatcher = Pattern.compile(MNEMONIC_FORMAT_REGEX).matcher(leftTerm);
        leftMatcher.find();

        // Estraggo la conversione [] dal termine di sinistra
        String leftConversion = leftMatcher.group(5);

        // Controllo se il termine è un numero o una stringa
        RpaAbstractMnemonicConstant newMnemonicConstant;
        String                      newMnemonicConstantText = "";

        if (RpaNumberUtils.isInteger(rightTerm) || RpaNumberUtils.isDouble(rightTerm)) {

            Number numberValue;

            if (RpaNumberUtils.isInteger(rightTerm)) {

                numberValue = Integer.valueOf(rightTerm);

            } else {

                numberValue = Double.valueOf(rightTerm);

            }

            // Se il termine è un numero, cerco un TOT libero
            int     indexTOT = 1000;
            boolean isFreeMnemonicFound = false;

            while (!isFreeMnemonicFound) {

                RpaValue<RpaAbstractMnemonicConstant> variableValue = rpaVariablesManager.getVariableValue(RpaVariablesManager.Type.TOT, indexTOT);

                if (variableValue == null) {

                    // Innesto il termine nel TOT
                    isFreeMnemonicFound = true;

                    newMnemonicConstant = new RpaMnemonicTOT(mainCompositore, numberValue);
                    RpaValue<RpaAbstractMnemonicConstant> newValue = new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicConstant);

                    rpaVariablesManager.addVariable(RpaVariablesManager.Type.TOT, indexTOT, newValue);

                    newMnemonicConstantText = "#TOT" + indexTOT;

                    junkMnemonicType    = RpaVariablesManager.Type.TOT;
                    junkMnemonicIndex   = indexTOT;

                } else {

                    ++ indexTOT;

                }

            }

        } else {

            // Se il termine è una stringa, cerco un STR libero
            int     indexSTR = 1000;
            boolean isFreeMnemonicFound = false;

            while (!isFreeMnemonicFound) {

                RpaValue<RpaAbstractMnemonicConstant> variableValue = rpaVariablesManager.getVariableValue(RpaVariablesManager.Type.STR, indexSTR);

                if (variableValue == null) {

                    // Innesto il termine nel STR
                    isFreeMnemonicFound = true;

                    rightTerm           = rightTerm.replaceAll("\"", "");
                    newMnemonicConstant = new RpaMnemonicSTR(mainCompositore, rightTerm);
                    RpaValue<RpaAbstractMnemonicConstant> newValue = new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicConstant);

                    rpaVariablesManager.addVariable(RpaVariablesManager.Type.STR, indexSTR, newValue);

                    newMnemonicConstantText = "#STR" + indexSTR;

                    junkMnemonicType    = RpaVariablesManager.Type.STR;
                    junkMnemonicIndex   = indexSTR;

                }

            }

        }

        // Aggiungo al nuovo mnemonico (TOT o STR) la conversione [] del mnemonico di sinistra
        newMnemonicConstantText += leftConversion + "#";

        // Definisco il termine di destra come istruzione
        String newRightTerm = "<?compo " + newMnemonicConstantText + " ?>";

        // Rigenero il Parse-Tree
        // RpaLexer              lexer       = new RpaLexer(CharStreams.fromString(newRightTerm));
        RpaLexer                lexer       = new RpaLexer(new ANTLRInputStream(newRightTerm));
        CommonTokenStream       tokenStream = new CommonTokenStream(lexer);
        RpaParser               parser      = new RpaParser(tokenStream);
        RpaParser.CodeContext   codeContext = parser.code();

        newParseTree = codeContext.instruction(0).statement().mnemonic();

        return newParseTree;

    }

    private ParseTree formatTermsFromOperationToOperation(String leftFormatTerm, String rightTerm) {

        // Nota: Il termine di destra è una operazione senza formattazione

        ParseTree newParseTree;

        // Aggiungo il termine di sinistra nell'operazione di destra
        String newRightTerm = rightTerm.substring(0, rightTerm.length() - 1);
        newRightTerm        = newRightTerm + leftFormatTerm + "\\";

        // Definisco il termine di destra come istruzione
        newRightTerm = "<?compo " + newRightTerm + " ?>";

        // Rigenero il Parse-Tree
        // RpaLexer              lexer       = new RpaLexer(CharStreams.fromString(newRightTerm));
        RpaLexer              lexer       = new RpaLexer(new ANTLRInputStream(newRightTerm));
        CommonTokenStream       tokenStream = new CommonTokenStream(lexer);
        RpaParser             parser      = new RpaParser(tokenStream);
        RpaParser.CodeContext codeContext = parser.code();

        newParseTree = codeContext.instruction(0).statement().operationStatement();

        return newParseTree;

    }

    private ParseTree formatTermsFromOperationToConstant(String leftFormatTerm, String rightTerm) {

        ParseTree newParseTree;
        RpaVariablesManager rpaVariablesManager = mainCompositore.getVariablesManager();
        // RpaVariablesManager rpaVariablesManager = rpaVariablesManager.getRpaVariablesManager();

        // Controllo se il termine è un numero o una stringa
        RpaAbstractMnemonicConstant newMnemonicConstant;
        String                      newMnemonicConstantText = "";

        if (RpaNumberUtils.isInteger(rightTerm) || RpaNumberUtils.isDouble(rightTerm)) {

            Number numberValue;

            if (RpaNumberUtils.isInteger(rightTerm)) {

                numberValue = Integer.valueOf(rightTerm);

            } else {

                numberValue = Double.valueOf(rightTerm);

            }

            // Se il termine è un numero, cerco un TOT libero
            int     indexTOT = 1000;
            boolean isFreeMnemonicFound = false;

            while (!isFreeMnemonicFound) {

                RpaValue<RpaAbstractMnemonicConstant> variableValue = rpaVariablesManager.getVariableValue(RpaVariablesManager.Type.TOT, indexTOT);

                if (variableValue == null) {

                    // Innesto il termine nel TOT
                    isFreeMnemonicFound = true;

                    newMnemonicConstant = new RpaMnemonicTOT(mainCompositore, numberValue);
                    RpaValue<RpaAbstractMnemonicConstant> newValue = new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicConstant);

                    rpaVariablesManager.addVariable(RpaVariablesManager.Type.TOT, indexTOT, newValue);

                    newMnemonicConstantText = "#TOT" + indexTOT;

                    junkMnemonicType    = RpaVariablesManager.Type.TOT;
                    junkMnemonicIndex   = indexTOT;

                } else {

                    ++ indexTOT;

                }

            }

        } else {

            // Se il termine è una stringa, cerco un STR libero
            int     indexSTR = 1000;
            boolean isFreeMnemonicFound = false;

            while (!isFreeMnemonicFound) {

                RpaValue<RpaAbstractMnemonicConstant> variableValue = rpaVariablesManager.getVariableValue(RpaVariablesManager.Type.STR, indexSTR);

                if (variableValue == null) {

                    // Innesto il termine nel STR
                    isFreeMnemonicFound = true;

                    rightTerm           = rightTerm.replaceAll("\"", "");
                    newMnemonicConstant = new RpaMnemonicSTR(mainCompositore, rightTerm);
                    RpaValue<RpaAbstractMnemonicConstant> newValue = new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicConstant);

                    rpaVariablesManager.addVariable(RpaVariablesManager.Type.STR, indexSTR, newValue);

                    newMnemonicConstantText = "#STR" + indexSTR;

                    junkMnemonicType    = RpaVariablesManager.Type.STR;
                    junkMnemonicIndex   = indexSTR;

                }

            }

        }

        // Aggiungo al nuovo mnemonico (TOT o STR) la conversione [] del mnemonico di sinistra
        newMnemonicConstantText += leftFormatTerm + "#";

        // Definisco il termine di destra come istruzione
        String newRightTerm = "<?compo " + newMnemonicConstantText + " ?>";

        // Rigenero il Parse-Tree
        // RpaLexer              lexer       = new RpaLexer(CharStreams.fromString(newRightTerm));
        RpaLexer              lexer       = new RpaLexer(new ANTLRInputStream(newRightTerm));
        CommonTokenStream       tokenStream = new CommonTokenStream(lexer);
        RpaParser             parser      = new RpaParser(tokenStream);
        RpaParser.CodeContext codeContext = parser.code();

        newParseTree = codeContext.instruction(0).statement().mnemonic();

        return newParseTree;

    }

    private boolean isConversionValidToSwap(String value) {

        for (String conversionToAvoidRegex : CONVERSIONS_TO_AVOID_REGEXPS) {

            Matcher matcher = Pattern.compile(conversionToAvoidRegex).matcher(value);

            if (matcher.find()) {

                return false;

            }

        }

        return true;

    }

}
