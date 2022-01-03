package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaBooleanSeparatorSwapException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RpaBooleanConditionSeparatorSwaperPlugin extends RpaPrecompiler {

    private static final String BOOLEAN_OPERATIONS_REGEX    = "((_EQ_)|(_NE_)|(_GT_)|(_GE_)|(_LT_)|(_LE_)|(_AND_)|(_OR_))";
    private static final String[] BOOLEAN_OPERATIONS        = {"EQ", "NE", "GT", "GE", "LT", "LE", "AND", "OR"};

    private static final String SEPARATOR               = "_";
    private static final String SEPARATOR_REPLACEMENT   = ".";

    private Map<String, String> mapSeparatorReplacements;

    private boolean isDocumentRTF       = false;
    private boolean isStartRTFDetected  = false;

    public RpaBooleanConditionSeparatorSwaperPlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);
        mapSeparatorReplacements = createSeparatorReplacements();

    }

    @Override
    public void run() throws IOException {

        // Controllo se il documento è un rtf
        // String documentText = readFile(mainCompositore.getComposerConfiguration().getInputModel().getPath());

        swapRecursiveBooleanSeparator(mainCompositore.getComposerConfiguration().getInputDocument());

    }

    private void swapRecursiveBooleanSeparator(Node node) throws RpaBooleanSeparatorSwapException {

        // CASO BASE: Ho trovato un nodo terminale (paragrafo di testo)
        if (node.getNodeType() == NodeType.RUN) {

            Run runNode     = (Run) node;
            String textNode = runNode.getText();

            processNodeText(runNode);

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                swapRecursiveBooleanSeparator(childNode);

            }

        }

    }

    private void processNodeText(Run runNode) throws RpaBooleanSeparatorSwapException {

        String nodeText = runNode.getText();

        // Find and replace every occurence of boolean expression in the "node"'s text
        for (Map.Entry<String, String> entrySeparatorReplacement : mapSeparatorReplacements.entrySet()) {

            String booleanExpression            = entrySeparatorReplacement.getKey();
            String booleanExpressionReplacement = entrySeparatorReplacement.getValue();

            nodeText = nodeText.replaceAll(booleanExpression, booleanExpressionReplacement);

        }

        // Update the node's text
        try {

            runNode.setText(nodeText);

        } catch (Exception exception) {

            String errorCode    = nodeText;
            String errorMessage = "Errore nell'aggiornamento del nodo per sostituire i separatori booleani";
            int context         = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaBooleanSeparatorSwapException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

    }

    private Map<String, String> createSeparatorReplacements() {

        Map<String, String> mapSeparatorReplacements = new HashMap<String, String>();

        String boolean_operation                = "";
        String boolean_operation_replacement    = "";

        // For each boolean operation, bind the separator with his replacement
        for (String booleanOperation : BOOLEAN_OPERATIONS) {

            // Rule: "_INSTRUCTION_"
            boolean_operation               = SEPARATOR + booleanOperation + SEPARATOR;
            boolean_operation_replacement   = SEPARATOR_REPLACEMENT + booleanOperation + SEPARATOR_REPLACEMENT;
            mapSeparatorReplacements.put(boolean_operation, boolean_operation_replacement);

            // TODO: Perchè avevo inserito questo tipo sostituzione nociva? (es: #ODG1_ID_ELE_DOC# -> LE_ da problemi)
            // RULE: "_INSTRUCTION"
            /*
            boolean_operation               = SEPARATOR + booleanOperation;
            boolean_operation_replacement   = SEPARATOR_REPLACEMENT + booleanOperation;
            mapSeparatorReplacements.put(boolean_operation, boolean_operation_replacement);
            */

            // RULE: "INSTRUCTION_"
            /*
            boolean_operation               = booleanOperation + SEPARATOR;
            boolean_operation_replacement   = booleanOperation + SEPARATOR_REPLACEMENT;
            mapSeparatorReplacements.put(boolean_operation, boolean_operation_replacement);
            */

        }

        return mapSeparatorReplacements;

    }

    public String test(String stringTest) throws RpaBooleanSeparatorSwapException {

        try {

            Run runNode = new Run(new Document());
            runNode.setText(stringTest);
            processNodeText(runNode);

            return runNode.getText();

        } catch (Exception exception) {

            String errorCode    = stringTest;
            String errorMessage = "Impossibile creare un node aspose per il test";
            int context = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaBooleanSeparatorSwapException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

    }

}
