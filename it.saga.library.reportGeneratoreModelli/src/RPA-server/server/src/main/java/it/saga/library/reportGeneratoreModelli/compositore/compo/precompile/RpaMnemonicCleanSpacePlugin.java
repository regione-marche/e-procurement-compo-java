package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaMnemonicCleanSpacePlugin extends RpaPrecompiler {

    // Link: https://regex101.com/r/64fKAE/1
    // private static final String MNEMONIC_SEARCH_REGEX = "(#[^# ]+#)|(#[^#]+#)";
    private static final String MNEMONIC_SEARCH_REGEX = "#[^#\\n\\r]+#";

    public RpaMnemonicCleanSpacePlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

    }

    @Override
    public void run() throws IOException {

        recursiveCleanMnemonicElements(mainCompositore.getComposerConfiguration().getInputDocument());

    }

    private void recursiveCleanMnemonicElements(Node node) throws IOException {

        // CASO BASE: Ho trovato un nodo terminale (paragrafo)
        if (node.getNodeType() == NodeType.RUN) {

            Run runNode = (Run) node;

            processNodeRun(runNode);

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                recursiveCleanMnemonicElements(childNode);

            }

        }

    }

    private void processNodeRun(Run runNode) throws IOException {

        // Imposto il nuovo testo del nodo run
        String          runNodeText             = runNode.getText();
        StringBuilder   newTextStringBuilder    = new StringBuilder(runNodeText);

        // Cerco tutti i gli spazi all'interno dei mnemonici
        List<Integer>   spacePositionList   = new ArrayList<Integer>();
        Matcher         mnemonicMatcher     = Pattern.compile(MNEMONIC_SEARCH_REGEX).matcher(runNodeText);

        while (mnemonicMatcher.find()) {

            String mnemonicString = mnemonicMatcher.group();

            if (mnemonicString.contains(" ")) {

                for (int charIndex = 0; charIndex < mnemonicString.length(); charIndex ++) {

                    char mnemonicChar = mnemonicString.charAt(charIndex);

                    if (mnemonicChar == ' ') {

                        int newSpacePosition = mnemonicMatcher.start() + charIndex;
                        spacePositionList.add(newSpacePosition);

                    }

                }

            }

        }

        // Elimino tutti gli spazi trovati
        for (int spaceIndex = 0; spaceIndex < spacePositionList.size(); spaceIndex ++) {

            int spaceIndexToDelete          = spacePositionList.get(spaceIndex);
            int correctSpaceIndexToDelete   = spaceIndexToDelete - spaceIndex;

            newTextStringBuilder.deleteCharAt(correctSpaceIndexToDelete);

        }

        // Salvo il nuovo testo
        try {

            runNode.setText(newTextStringBuilder.toString());

        } catch (Exception exception) {

            throw new IOException(exception);

        }

    }

}
