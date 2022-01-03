package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.Node;
import com.aspose.words.NodeImporter;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Section;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaIncludemodException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidInstructionDeclarationException;
import org.apache.commons.lang.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Plugin per la gestione del comando [INCLUDEMOD]: comando che include nel documento
 * un modello o una parte di modello che verrà composto come parte dello stesso
 */
public class RpaIncludemodPlugin extends RpaPrecompiler {

	// Link: https://regex101.com/r/Dp8Kqt/1
	public static final String INCLUDEMOD_REGEX = "\\[INCLUDEMOD\\] *(([A-Za-z0-9_\\\\\\/\\.\\:]+)|(\\\"[A-Za-z0-9_\\\\\\/\\.\\: ]+\\\"))";

	// Link: https://regex101.com/r/IFfM7U/2
	public static final String CORRECT_START_PATH_REGEX = "^([A-Z]:[\\\\\\/]).*$";

	public RpaIncludemodPlugin(RpaMainCompositore mainCompositore) {

		super(mainCompositore);

	}

	@Override
	public void run() throws IOException {

		debugMessages.precompilePrint("#####################################################");
		debugMessages.precompilePrint("#\t\t\tInizio gestione [INCLUDEMOD]\t\t\t#");
		debugMessages.precompilePrint("#####################################################");

		debugMessages.precompilePrint("");

		recursiveFindIncludemod(mainCompositore.getComposerConfiguration().getInputDocument());

		debugMessages.precompilePrint("");

		debugMessages.precompilePrint("#####################################################");
		debugMessages.precompilePrint("#\t\t\tFine gestione [INCLUDEMOD]\t\t\t\t#");
		debugMessages.precompilePrint("#####################################################");

		debugMessages.precompilePrint("");

	}

	private void recursiveFindIncludemod(Node node) throws RpaInvalidInstructionDeclarationException {

		// CASO BASE: Ho trovato un nodo terminale
		if (node.getNodeType() == NodeType.PARAGRAPH) {

			Paragraph paragraph = (Paragraph) node;

			// Caso speciale: Un paragrafo contiene uno shape (es: TextBox)
			if (paragraph.getChildNodes().getCount() == 1 && paragraph.getChildNodes().get(0).getNodeType() == NodeType.SHAPE) {

				recursiveFindIncludemod(paragraph.getChildNodes().get(0));

			} else {

				processNodeRun(paragraph);

			}

		}

		// CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
		else if (node.isComposite()) {

			CompositeNode<Node> compositeNode = (CompositeNode) node;

			for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

				recursiveFindIncludemod(childNode);

			}

		}

	}

	private void processNodeRun(Paragraph paragraph) {

		// Controllo se è presente l'istruzione "[INCLUDEMOD]"
		Matcher matcherIncludemod = Pattern.compile(INCLUDEMOD_REGEX).matcher(paragraph.getText());

		if (matcherIncludemod.find()) {

			// Carico il documento della "[INCLUDEMOD]"
			String documentPath = matcherIncludemod.group(1);
			documentPath = documentPath.replaceAll("\"", "");

			// Se il path comincia con un path relativo (e non è linux), lo trasformo in path assoluto (partendo dal documento)
			if (!Pattern.compile(CORRECT_START_PATH_REGEX).matcher(documentPath).find() && !SystemUtils.IS_OS_LINUX) {

				String modelPath = mainCompositore.getComposerConfiguration().getInputModel().getParent();
				documentPath = modelPath + File.separator + documentPath;

			}

			debugMessages.precompilePrint("Carico il documento '" + documentPath + "' ...");

			Document documentToInclude;

			try {

				documentToInclude = new Document(new FileInputStream(documentPath));

			} catch (Exception exception) {

				exception.printStackTrace();

				String	code	= paragraph.getText();
				String	message	= "Impossibile includere il file '" + documentPath + "'";
				int		context	= RpaComposerException.PRECOMPILE_MESSAGE;

				throw new RpaIncludemodException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

			}

			// Innesto il documento della "[INCLUDEMOD]" dopo il paragrafo
			try {

				insertDocument(paragraph, documentToInclude);

			} catch (Exception exception) {

				String	code	= paragraph.getText();
				String	message	= "Impossibile includere il file '" + documentPath + "'";
				int		context	= RpaComposerException.PRECOMPILE_MESSAGE;

				throw new RpaIncludemodException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

			}

			// Cancello il paragrafo
			paragraph.remove();

		}

	}

	public static void insertDocument(Node insertAfterNode, Document sourceDocument) throws Exception {

		// Controllo che il nodo sia un paragrafo o una tabella
		if ((insertAfterNode.getNodeType() != NodeType.PARAGRAPH) && (insertAfterNode.getNodeType() != NodeType.TABLE)) {

			return;

		}

		// I nodi del documento andranno inseriti nel padre del paragrafo
		CompositeNode destinationStory = insertAfterNode.getParentNode();

		// Creo un oggetto per importare e preservare lo stile dei nodi da inserire
		NodeImporter importer = new NodeImporter(sourceDocument, insertAfterNode.getDocument(), ImportFormatMode.KEEP_SOURCE_FORMATTING);

		// Ciclo su tutte le sezioni
		for (Section sourceSection : sourceDocument.getSections()) {

			// Ciclo su tutti i nodi (paragrafi e tabelle) della sezione
			for (Node sourceNode : (Iterable<Node>) sourceSection.getBody()) {

				// Salto il nodo se è l'ultimo paragrafo della sezione
				if (sourceNode.getNodeType() == (NodeType.PARAGRAPH)) {

					Paragraph paragraph = (Paragraph) sourceNode;

					if (paragraph.isEndOfSection() && !paragraph.hasChildNodes()) {

						continue;

					}

				}

				// Creo un clone del nodo da poter inserire
				Node newNode = importer.importNode(sourceNode, true);

				// Aggiungo il nuovo nodo
				destinationStory.insertAfter(newNode, insertAfterNode);
				insertAfterNode = newNode;

			}

		}

	}

}
