package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaIncludePrecompileException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidInstructionDeclarationException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cerca ogni [INCLUDE] all'interno del documento, e sostituisce il carattere "\" con il "'"
 */
public class RpaIncludePlugin extends RpaPrecompiler {

    // Link: https://regex101.com/r/PmhZyq/1
    public static final String INCLUDE_REGEX = "\\[INCLUDE\\] *(([A-Za-z0-9_\\\\\\/\\.\\:]+)|(\\\"[A-Za-z0-9_\\\\\\/\\.\\: ]+\\\"))";

    public RpaIncludePlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

    }

    @Override
    public void run() throws IOException {

        debugMessages.precompilePrint("#####################################################");
        debugMessages.precompilePrint("#\t\t\tInizio gestione [INCLUDE]\t\t\t#");
        debugMessages.precompilePrint("#####################################################");

        debugMessages.precompilePrint("");

        recursiveFindInclude(mainCompositore.getComposerConfiguration().getInputDocument());

        debugMessages.precompilePrint("");

        debugMessages.precompilePrint("#####################################################");
        debugMessages.precompilePrint("#\t\t\tFine gestione [INCLUDE]\t\t\t\t#");
        debugMessages.precompilePrint("#####################################################");

        debugMessages.precompilePrint("");

    }

    private void recursiveFindInclude(Node node) throws RpaInvalidInstructionDeclarationException {

        // CASO BASE: Ho trovato un nodo terminale
        if (node.getNodeType() == NodeType.RUN) {

            Run runNode = (Run) node;

            processNodeRun(runNode);

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                recursiveFindInclude(childNode);

            }

        }

    }

    private void processNodeRun(Run runNode) {

        // Controllo se è presente l'istruzione "[INCLUDE]"
        Matcher matcherInclude = Pattern.compile(INCLUDE_REGEX).matcher(runNode.getText());

        if (matcherInclude.find()) {

            // Sostituisco gli "\" con i "'"
            String documentPath = matcherInclude.group(1);
            documentPath = documentPath.replaceAll("\\\\", "'");

            try {

                runNode.setText(documentPath);

            } catch (Exception exception) {

                String	code	= runNode.getText();
                String	message	= "Impossibile formattare il path della include '" + documentPath + "'";
                int		context	= RpaComposerException.PRECOMPILE_MESSAGE;

                throw new RpaIncludePrecompileException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

            }

        }

    }

}
