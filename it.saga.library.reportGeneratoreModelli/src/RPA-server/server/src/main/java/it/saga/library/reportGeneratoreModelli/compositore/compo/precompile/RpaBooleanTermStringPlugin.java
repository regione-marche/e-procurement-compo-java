package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMalformedBooleanConditionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaBooleanTermStringPlugin extends RpaPrecompiler {

    private static final String INSTRUCTION_REGEX               = "<\\?compo .*%(.*)%.* \\?>";
    private static final String BOOLEAN_SEPARATOR_REGEX         = "(_EQ_)|(_NE_)|(_GT_)|(_GE_)|(_LT_)|(_LE_)|(_AND_)|(_OR_)";
    private static final String BOOLEAN_TERM_NOT_STRING         = "(^ *-? *0\\.[0-9]+ *$)|(^ *-? *[1-9][0-9]*\\.[0-9]+ *$)|(^ *-? *[1-9][0-9]* *$)|(^ *\\\\.+\\\\ *$)|(^ *#.+# *$)|(^ *0 *$)";
    private static final String STRING_WITH_PERCENTAGE_REGEX    = "('[^']*%.*%[^']*')|(\"[^\"]*%.*%[^\"]*\")";

    // TODO: In caso di problemi con "STRING_WITH_PERCENTAGE_REGEX" provare le espressioni regolare qui sotto
    private static final String DOUBLE_QUOTE_STRING_WITH_PERCENTAGE_REGEX = "[^\"]*(\"[^\"%]*%[^\"%]*%[^\"%]*\")[^\"]*";
    private static final String SINGLE_QUOTE_STRING_WITH_PERCENTAGE_REGEX = "[^']*('[^'%]*%[^'%]*%[^'%]*')[^']*";

    public RpaBooleanTermStringPlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

    }

    @Override
    public void run() throws IOException {

        // System.out.println("###############################################################################");
        // System.out.println("\t\tInizio documento per 'BooleanTermStringPlugin'");
        // System.out.println("###############################################################################");
        debugMessages.precompilePrint("###############################################################################");
        debugMessages.precompilePrint("\t\tInizio documento per 'BooleanTermStringPlugin'");
        debugMessages.precompilePrint("###############################################################################");

        // System.out.println("");
        debugMessages.precompilePrint("");

        // recursiveFindInstructionDeclarations(ComposerConfiguration.getInstance().getInputDocument());
        recursiveFindInstructionDeclarations(mainCompositore.getComposerConfiguration().getInputDocument());

        // System.out.println("###############################################################################");
        // System.out.println("\t\tFine documento per 'BooleanTermStringPlugin'");
        // System.out.println("###############################################################################");
        debugMessages.precompilePrint("###############################################################################");
        debugMessages.precompilePrint("\t\tFine documento per 'BooleanTermStringPlugin'");
        debugMessages.precompilePrint("###############################################################################");

    }

    private void recursiveFindInstructionDeclarations(Node node) throws RpaMalformedBooleanConditionException {

        // CASO BASE: Ho trovato un nodo terminale (paragrafo)
        if (node.getNodeType() == NodeType.RUN) {

            Run runNode = (Run) node;

            processNodeRun(runNode);

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                recursiveFindInstructionDeclarations(childNode);

            }

        }

    }

    private void processNodeRun(Run runNode) throws RpaMalformedBooleanConditionException {

        // Identifico le condizioni booleane all'interno delle istruzioni
        String textNode                     = runNode.getText();
        StringBuilder newTextNodeBuilder    = new StringBuilder(runNode.getText());
        Matcher instructionMatcher          = Pattern.compile(INSTRUCTION_REGEX).matcher(textNode);
        Matcher stringPercentageMatcher     = Pattern.compile(STRING_WITH_PERCENTAGE_REGEX).matcher(textNode);

        while (instructionMatcher.find()) {

            stringPercentageMatcher.reset();

            // Controllo di aver trovato una espressione booleana che non sia contenuta in una stringa
            if (instructionMatcher.groupCount() == 1 && !stringPercentageMatcher.find()) {

                String booleanConditionString   = instructionMatcher.group(1);
                int booleanConditionIndexStart  = instructionMatcher.start(1);

                // Recupero ogni termine della espressione booleana
                Matcher booleanSeparatorMatcher         = Pattern.compile(BOOLEAN_SEPARATOR_REGEX).matcher(booleanConditionString);
                ArrayList<String> booleanStringTerms    = new ArrayList<String>();
                ArrayList<Integer> booleanIndexTerms    = new ArrayList<Integer>();

                int termStartIndex = 0;
                String booleanStringTerm;

                while (booleanSeparatorMatcher.find()) {

                    int booleanSeparatorStartIndex  = booleanSeparatorMatcher.start();
                    int booleanSeparatorEndIndex    = booleanSeparatorMatcher.end();

                    booleanStringTerm = booleanConditionString.substring(termStartIndex, booleanSeparatorStartIndex);

                    booleanStringTerms.add(booleanStringTerm);
                    booleanIndexTerms.add(booleanConditionIndexStart + termStartIndex);

                    termStartIndex = booleanSeparatorEndIndex;

                }

                booleanStringTerm = booleanConditionString.substring(termStartIndex, booleanConditionString.length());

                booleanStringTerms.add(booleanStringTerm);
                booleanIndexTerms.add(booleanConditionIndexStart + termStartIndex);

                // Verifico se ogni termine è vuoto, un mnemonico, un intero o una operazione matematica
                int countDoubleQuoteAdded = 0;

                for (int i = 0; i < booleanStringTerms.size(); i ++) {

                    booleanStringTerm = booleanStringTerms.get(i);

                    Matcher booleanTermNotString = Pattern.compile(BOOLEAN_TERM_NOT_STRING).matcher(booleanStringTerm);

                    if (!booleanTermNotString.find()) {

                        int booleanIndexTerm = booleanIndexTerms.get(i);

                        int doubleQuoteStartIndex   = booleanIndexTerm + countDoubleQuoteAdded;
                        int doubleQuoteEndIndex     = doubleQuoteStartIndex + 1 + booleanStringTerm.length();

                        // Ho trovato un termine che fa da stringa. Aggiungo i doppi apici "
                        newTextNodeBuilder.insert(doubleQuoteStartIndex, '"');
                        newTextNodeBuilder.insert(doubleQuoteEndIndex, '"');

                        countDoubleQuoteAdded += 2;

                    }

                }

                if (!textNode.equals(newTextNodeBuilder.toString())) {

                    // System.out.println("Instruction with boolean condition:\t" + textNode);
                    // System.out.println("Instruction formatted:\t\t\t\t" + newTextNodeBuilder.toString());
                    // System.out.println("");

                    debugMessages.precompilePrint("Instruction with boolean condition:\t" + textNode);
                    debugMessages.precompilePrint("Instruction formatted:\t\t\t\t" + newTextNodeBuilder.toString());
                    debugMessages.precompilePrint("");

                }

            }

        }

        try {

            runNode.setText(newTextNodeBuilder.toString());

        } catch (Exception exception) {

            String errorCode    = textNode;
            String errorMessage = "Errore nell'aggiornamento del testo di un nodo aspose";
            int context         = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaMalformedBooleanConditionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

    }

}
