package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaSignatureParseException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaSignatureParsePlugin extends RpaPrecompiler {

    // Link: https://regex101.com/r/YTMUo5/1/
    private static final String SIGNATURE_REGEX = "(#signature#)(@ep\\[[A-Za-z]+=[^|=]+(\\|[A-Za-z]+=[^|=]+)*\\])?";

    public RpaSignatureParsePlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

    }

    @Override
    public void run() throws IOException {

        recursiveReplaceSignature(mainCompositore.getComposerConfiguration().getInputDocument());

    }

    private void recursiveReplaceSignature(Node node) {

        // CASO BASE: Ho trovato un nodo terminale
        if (node.getNodeType() == NodeType.RUN) {

            Run runNode = (Run) node;

            processNodeRun(runNode);

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                recursiveReplaceSignature(childNode);

            }

        }

    }

    private void processNodeRun(Run runNode) {

        String  runNodeText         = runNode.getText();
        Matcher signatureMatcher    = Pattern.compile(SIGNATURE_REGEX).matcher(runNodeText);

        // Controllo se ho trovato la dichiarazione di firma grafometrica
        if (signatureMatcher.find()) {

            // Estraggo il prefisso "#signature#" e lo sostituisco con "*signature*"
            int signatureStartIndex = signatureMatcher.start(1);
            int signatureEndIndex   = signatureMatcher.end(1);

            String newTextPrefix    = runNodeText.substring(0, signatureStartIndex) + "*";
            String newTextSuffix    = runNodeText.substring(signatureStartIndex + 1, signatureEndIndex - 1) + "*";
            newTextSuffix           = newTextSuffix + runNodeText.substring(signatureEndIndex, runNodeText.length());

            try {

                runNodeText = newTextPrefix + newTextSuffix;
                runNode.setText(runNodeText);

            } catch (Exception exception) {

                String	code	= runNodeText;
                String	message	= "Impossibile modificare il TAG della firma grafometrica";
                int		context	= RpaComposerException.PRECOMPILE_MESSAGE;

                throw new RpaSignatureParseException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

            }

        }


    }

}
