package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.io.IOException;

public class RpaEndCodePlugin extends RpaPrecompiler {

    private boolean isEndCodeFound = false;

    public RpaEndCodePlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

    }

    @Override
    public void run() throws IOException {

        debugMessages.precompilePrint("#####################################################");
        debugMessages.precompilePrint("#\t\t\tInizio gestione #FINETESTO#\t\t\t\t#");
        debugMessages.precompilePrint("#####################################################");

        recursiveFilterText(mainCompositore.getComposerConfiguration().getInputDocument());

        debugMessages.precompilePrint("#####################################################");
        debugMessages.precompilePrint("#\t\t\tFine gestione #FINETESTO#\t\t\t\t#");
        debugMessages.precompilePrint("#####################################################");

        debugMessages.precompilePrint("");

    }

    private void recursiveFilterText(Node node) {

        // Caso base: Ho trovato un nodo di tipo "Paragrafo"
        if (node.getNodeType() == NodeType.PARAGRAPH) {

            String paragraphText = node.getText();

            if (paragraphText.contains("#FINETESTO#")) {

                this.isEndCodeFound = true;
                node.remove();

            }

        }

        // Caso ricorsivo: Itero su tutti i nodi figli
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode<Node>) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                if (this.isEndCodeFound) {

                    debugMessages.precompilePrint("Fine testo trovato!");

                    childNode.remove();

                } else {

                    recursiveFilterText(childNode);

                }

            }

        }

    }

}
