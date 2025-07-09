package it.saga.library.reportGeneratoreModelli.compositore.compo;

import com.aspose.words.Body;
import com.aspose.words.Cell;
import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.DocumentBase;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Font;
import com.aspose.words.List;
import com.aspose.words.ListFormat;
import com.aspose.words.ListLevel;
import com.aspose.words.ListTemplate;
import com.aspose.words.LoadFormat;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.NumberStyle;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Run;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.aspose.words.Style;
import it.saga.extern.rpa_libs.antlr.v4.runtime.ANTLRInputStream;
import it.saga.extern.rpa_libs.antlr.v4.runtime.CommonTokenStream;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.ParseTree;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.ParseTreeVisitor;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaCompoUtils;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaImageBytesValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaImageValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaIncludeFileValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaNextValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaWalker;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeDocument;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeGenericLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeIf;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeInlineLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeLoop;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaIncludeException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMemoryReachException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaOutOfMemoryException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.precompile.RpaIncludemodPlugin;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDebugMessages;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaLexer;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaInterprete {

	private Stack<RpaScope> scopeStack;

	private Document				docWork;
	private RpaMainCompositore		mainCompositore;
	private RpaDebugMessages		debugMessages;
	private java.util.List<Node>	nodesToDelete;

	private int currentCountTableVisited = 0;

	// Link: https://regex101.com/r/viO3RS/1
	private static final String SPECIAL_CHARACTERS_REGEX = "[�����������������������������]";

	public RpaInterprete(RpaMainCompositore mainCompositore) {

		this.mainCompositore	= mainCompositore;
		this.debugMessages		= mainCompositore.getDebugMessages();
		this.nodesToDelete		= new ArrayList<Node>();

	}

	/**
	 * Esegue una composizione partendo da una stringa
	 *
	 * @param text la stringa da interpretare
	 * @throws Exception
	 */
	public void parse(String text) throws Exception {
		DocumentBuilder builder = new DocumentBuilder();
		builder.write(text);
		run(builder.getDocument());
	}

	/**
	 * Esegue una composizione partendo da un documento
	 *
	 * @param document il documento da interpretare
	 * @throws Exception
	 */
	public void parse(Document document) throws Exception {
		run(document);
	}

	/**
	 * Salva il risultato della composizione sul file di output
	 *
	 * @throws Exception
	 */
	public void save(File outputModel) throws Exception {
		if (outputModel != null) {
			this.docWork.getBuiltInDocumentProperties().setNameOfApplication(RpaCompoUtils.APP_NAME);
			this.docWork.getBuiltInDocumentProperties().setCompany(RpaCompoUtils.MAGGIOLI_SPA);

			this.docWork.save(outputModel.getPath());
		}
	}

	private void testAsposeList2(Document testDocument) {

		try {

			Body body		= testDocument.getFirstSection().getBody();
			List newList	= testDocument.getLists().add(ListTemplate.NUMBER_ARABIC_DOT);

			for (Paragraph paragraph : body.getParagraphs()) {

				if (paragraph.isListItem()) {

					ListFormat	listFormat	= paragraph.getListFormat();
					List		list		= listFormat.getList();

					if (listFormat.getListLevel().getNumberStyle() == NumberStyle.ARABIC) {

						listFormat.setList(newList);
						listFormat.getListLevel().getFont().setBold(true);

					}

				}

			}

			testDocument.updateListLabels();
			testDocument.save("./test_clone_list2.rtf");

		} catch (Exception exception) { exception.printStackTrace(); }

	}

	private void run(Document doc) throws Exception {
		if (scopeStack == null) {
			scopeStack = new Stack<RpaScope>();
			// scope.push(new Scope());
			scopeStack.push(new RpaScopeDocument(mainCompositore));
			// scope.get(0).put("PRA_PRATICHE.PKID", 1);
		}

		docWork = (Document) doc.deepClone(false);
		mainCompositore.getDebugMessages().setDocument(docWork);
		mainCompositore.getWarningMessages().setDocument(docWork);
		traverseAllNodes(doc, docWork);

		// Non cancellare! Eliminazione elementi presenti in Scope-skip
		for (Node node : nodesToDelete) {

			if (node.getParentNode() != null) {

				node.remove();

			}

		}

		// INIZIO TEST
		// testAsposeList2(docWork);
		// FINE TEST

		// removeEmptyParagraph(docWork);
		// printDocumentGraphviz();

	}

	private Node traverseAllNodes(CompositeNode<Node> parentNode, CompositeNode<Node> parentDest) {

		Node childNode = parentNode.getFirstChild();

		while (childNode != null) {

			if (childNode instanceof Run) {

				Run runNode = (Run) childNode;
				String textRunNode = runNode.getText();
				mainCompositore.setLastRunNodeRead(runNode);

				// Controllo se ho raggiungo il limite di memoria per il risultato che sto creando
				Long limitSize = mainCompositore.getComposerConfiguration().getLimitMemorySize();

				if (
					limitSize != null &&
					RpaNumberUtils.calculateDocumentSize(parentDest.getDocument()) > limitSize
				) {

					// Esco con un'errore
					String code     	= "";
					String message  	= "Raggiunto il limite pre-impostato di " + limitSize + " KB";
					int errorContext   	= RpaComposerException.COMPILE_MESSAGE;

					throw new RpaMemoryReachException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), errorContext, code, message);

				}

				// Se sono in un "inline-loop" per cui devo registrare i nodi letti, li aggiungo
				boolean isLastScopeInlineLoop = scopeStack.peek().getScopeType() == RpaScope.INLINE_LOOP_SCOPE_TYPE;

				if (isLastScopeInlineLoop) {

					RpaScopeInlineLoop scopeInlineLoop = (RpaScopeInlineLoop) scopeStack.peek();

					if (!scopeInlineLoop.isActive()) {

						scopeInlineLoop.addRunNode(runNode);

					}

				}

				// Compilo / Interpreto il contenuto del nodo
				// CompoLexer			lexer	= new CompoLexer(CharStreams.fromString(textRunNode));
				RpaLexer			lexer = new RpaLexer(new ANTLRInputStream(textRunNode));
				// lexer.addErrorListener(mainCompositore.getAntlrErrorListener());
				CommonTokenStream	tokens = new CommonTokenStream(lexer);

				RpaParser parser = new RpaParser(tokens);
				parser.addErrorListener(mainCompositore.getAntlrErrorListener());
				ParseTree tree = parser.code();

				ParseTreeVisitor<RpaValue<?>> walker = new RpaWalker(
						mainCompositore.getComposerConfiguration().getConn(),
						scopeStack,
						mainCompositore,
						parentNode,
						runNode
				);

				RpaValue value = walker.visit(tree);

				if (value != null && value.getValue() instanceof String) {

					String stringValue = (String) value.getValue();

				}

				// Controllo se un loop in linea ha generato dei nodi da aggiungere nel testo
				if (isLastScopeInlineLoop) {

					RpaScopeInlineLoop scopeInlineLoop = (RpaScopeInlineLoop) scopeStack.peek();

					if (scopeInlineLoop.hasNewNodesToAdd()) {

						// Aggiungo i nuovi nodi
						for (Run inlineLoopRunNode : scopeInlineLoop.getNewRunNodeCodeList()) {

							try {

								Node newNode = docWork.importNode(inlineLoopRunNode, true);
								if (isInLoop()) {
									addNodeBreakLoopToDelete(newNode);
								}
								parentDest.appendChild(newNode);

							} catch (Exception exception) {
							}

						}

						// Resetto l'associazione con il mnemonico
						RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();
						mnemonicManager.unbindMnemonicEntityFromLoop(scopeStack, scopeInlineLoop.getLoopValue().getIndexName());

						// Elimino dallo stack l'inline-loop
						scopeStack.pop();

					}

				}

				// Verifico se il nodo contiene solo spazi o ? vuoto
				Matcher emptyRunNodeMatcher = Pattern.compile("^[ |\t]*$").matcher(textRunNode);

				if (emptyRunNodeMatcher.find()) {

					if (!isSkipElement()) {

						try {

							Run newRunNode = (Run) runNode.deepClone(true);
							// System.out.println("HEY!!!!");
							// newRunNode.getFont().setSize(2);
							newRunNode.setText(textRunNode);
							Node newNode = docWork.importNode(newRunNode, true);
							if (isInLoop()) {
								addNodeBreakLoopToDelete(newNode);
							}
							parentDest.appendChild(newNode);

						} catch (Exception e) {

							e.printStackTrace();

						}

						childNode = childNode.getNextSibling();

						continue;

					}

				}

				if (value == null) {

					childNode = childNode.getNextSibling();

					continue;

				} else if (value instanceof RpaNextValue) {

					// Se trovo la fine di un loop, torno al nodo di inizio loop

					if (scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

						// Cancello tutti i paragrafi a cui ho tolto TUTTI i nodi "Run"
						if (parentNode.getNodeType() == NodeType.PARAGRAPH && parentDest.getNodeType() == NodeType.PARAGRAPH) {

							Paragraph parentNodeParagraph = (Paragraph) parentNode;
							Paragraph parentDestParagraph = (Paragraph) parentDest;

							if (parentDestParagraph.getRuns().getCount() != parentNodeParagraph.getRuns().getCount() &&
									parentDestParagraph.getRuns().getCount() == 0) {

								// System.out.println("Found a paragraph to delete!");
								parentDest.remove();

							}

						}

						// Torno indietro
						return ((RpaScopeLoop) scopeStack.lastElement()).getNode();

					} else {

						childNode = childNode.getNextSibling();

					}

					continue;

				} else if (value instanceof RpaLoopValue) {

					childNode = childNode.getNextSibling();

					continue;

				} else if (value instanceof RpaImageValue) {

					// Creo uno "shape"
					RpaImageValue imageValue = (RpaImageValue) value;
					Shape newShape = new Shape(docWork, ShapeType.IMAGE);

					// Inserisco nello "shape" l'immagine
					try {

						newShape.getImageData().setImage(imageValue.getPath());

					} catch (Exception exception) {
					}

					// Aggiungo lo "shape" nel nuovo documento
					try {

						Run newRunNode = (Run) runNode.deepClone(true);
						newRunNode.setText("");
						// System.out.println("OH!!!!");
						Node newNodeRun = docWork.importNode(newRunNode, true);
						Node newNodeShape = docWork.importNode(newShape, true);
						if (isInLoop()) {
							addNodeBreakLoopToDelete(newNodeRun);
							addNodeBreakLoopToDelete(newNodeShape);
						}
						parentDest.appendChild(newNodeRun);
						parentDest.appendChild(newNodeShape);

					} catch (Exception exception) {
					}

					childNode = childNode.getNextSibling();

				} else if (value instanceof RpaImageBytesValue) {

					// Creo uno "shape"
					RpaImageBytesValue imageValue = (RpaImageBytesValue) value;
					Shape newShape = new Shape(docWork, ShapeType.IMAGE);

					// Inserisco nello "shape" l'immagine
					try {

						newShape.getImageData().setImageBytes(imageValue.getImageByteCode());
						newShape.setAnchorLocked(true);
						autoResizeImage(parentDest, newShape);
						newShape.setTop(0);
						newShape.setLeft(0);

					} catch (Exception exception) {
					}

					// Aggiungo lo "shape" nel nuovo documento
					try {

						Run newRunNode = (Run) runNode.deepClone(true);
						newRunNode.setText("");
						// System.out.println("OH!!!!");
						Node newNodeRun = docWork.importNode(newRunNode, true);
						Node newNodeShape = docWork.importNode(newShape, true);
						if (isInLoop()) {
							addNodeBreakLoopToDelete(newNodeRun);
							addNodeBreakLoopToDelete(newNodeShape);
						}
						parentDest.appendChild(newNodeRun);
						parentDest.appendChild(newNodeShape);

					} catch (Exception exception) {
					}

					childNode = childNode.getNextSibling();

				} else if (value instanceof RpaIncludeFileValue) {

					RpaIncludeFileValue includeFileValue = (RpaIncludeFileValue) value;
					Node nextChildNode = childNode.getNextSibling();
					Document documentToInsert = includeFileValue.getDocument();
					String filePath = (String) value.getValue();

					try {

						// IncludemodPlugin.insertDocument(childNode.getParentNode(), documentToInsert);
						RpaIncludemodPlugin.insertDocument(parentDest, documentToInsert);

					} catch (Exception exception) {

						String code = textRunNode;
						String message = "Impossibile includere il file '" + filePath + "'";
						int context = RpaComposerException.COMPILE_MESSAGE;

						throw new RpaIncludeException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

					}

					childNode = nextChildNode;

					/*
					try {

						insertDocument(paragraph, documentToInclude);

					} catch (Exception exception) {

						String	code	= paragraph.getText();
						String	message	= "Impossibile includere il file '" + documentPath + "'";
						int		context	= ComposerException.PRECOMPILE_MESSAGE;

						throw new IncludemodException(mainCompositore.getComposerConfiguration(), context, code, message);

					}
					*/

				} else if (value.isSkip()) {

					childNode = childNode.getNextSibling();

					continue;

				} else {

					try {

						Run newRunNode = (Run) runNode.deepClone(true);
						// System.out.println("EHHHH!!!!!");
						// newRunNode.getFont().setSize(5);
						newRunNode.setText((String) value.getValue());

						if (value.isExtendCarriageReturn()) {

							newRunNode.setText(extendCarriageReturn(newRunNode.getText()));

						}

						// Se ci sono caratteri special nel nodo presente in una TABELLA, attivo la formattazione "ComplexScript" (altrimenti vengono stampati ?)
						boolean isCurrentTextComplex = newRunNode.getFont() != null && Pattern.compile(SPECIAL_CHARACTERS_REGEX).matcher(newRunNode.getText()).find();
						if (currentCountTableVisited > 0 && isCurrentTextComplex) {

							// Nota: Se un testo non cambia font-size, selezionare cambiare il font-size,
							// salvare, ripristinarlo, salvare e lanciare il compositore
							newRunNode.getFont().setComplexScript(true);

						}

						Node newNode = docWork.importNode(newRunNode, true);

						if (isInLoop()) {
							addNodeBreakLoopToDelete(newNode);
						}

						// Se ho un "Body" come contenitore e un "Run" come contenuto, cerco un "Paragraph"
						// in cui inserirlo, altrimenti ne creo uno e lo aggiungo
						if (parentDest.getNodeType() == NodeType.BODY) {

							if (parentDest.getLastChild().getNodeType() == NodeType.PARAGRAPH) {

								Paragraph lastParagraph = ((Paragraph) parentDest.getLastChild());
								lastParagraph.appendChild(newNode);
								lastParagraph.getParagraphFormat().setSuppressAutoHyphens(true);

							} else {

								Paragraph newParagraph = new Paragraph(parentDest.getDocument());
								parentDest.appendChild(newParagraph);
								newParagraph.appendChild(newNode);
								newParagraph.getParagraphFormat().setSuppressAutoHyphens(true);

							}

						} else {

							parentDest.appendChild(newNode);
							((Paragraph) parentDest).getParagraphFormat().setSuppressAutoHyphens(true);

						}

					} catch (Exception e) {

						e.printStackTrace();

					}

					childNode = childNode.getNextSibling();

				}

			} else if (childNode.getNodeType() == NodeType.TABLE) {

				RpaScope lastScope	= scopeStack.peek();
				boolean	isSkipTable = false;

				if (lastScope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

					RpaScopeLoop scopeLoop = (RpaScopeLoop) lastScope;

					isSkipTable = scopeLoop.isSkipNode();

				} else if (lastScope.getScopeType() == RpaScope.IF_SCOPE_TYPE) {

					RpaScopeIf scopeIf = (RpaScopeIf) lastScope;

					isSkipTable = scopeIf.isSkipNode();

				}

				if (!isSkipTable) {

					++ currentCountTableVisited;

					try {

						if (parentDest instanceof Paragraph) {

							parentDest = parentDest.getParentNode();
						}

						Node newNode = docWork.importNode(childNode.deepClone(false), false);
						if (isInLoop()) { addNodeBreakLoopToDelete(newNode); }
						parentDest.appendChild(newNode);

					} catch (Exception e) {

						System.err.println("Errore durante la lettura del modello");
						e.printStackTrace();

					}

					Node tmpChildNode = traverseAllNodes((CompositeNode) childNode, (CompositeNode<Node>) parentDest.getLastChild());

					-- currentCountTableVisited;

					if (tmpChildNode == null) {

						childNode = childNode.getNextSibling();

					} else {

						childNode = tmpChildNode;

					}

				} else {

					childNode = childNode.getNextSibling();

				}

			} else if (childNode.isComposite()) {

				// TODO: Risolvere il problema dei nodi "Shape" nei loop (aka immagini)

				if (childNode.getNodeType() != NodeType.SHAPE) {

					boolean isSkipElement = this.isSkipElement();

					try {

						if (parentDest instanceof Paragraph) {

							parentDest = parentDest.getParentNode();
						}

						else {

							// NON TOGLIERE QUESTO IF!!!!!
							// Altrimenti crea a-capi / spazi in pi? quando si skippano i contesti loop!!!
							// Vedi "CE_scia_conformazione.rtf" con APE_CONCES:PKID=40501 su collx:Olgiate
							// if (!isSkipElement) {

							if (childNode.getNodeType() == NodeType.PARAGRAPH) {

								// Aggiungo un nodo "Run" affinch� il paragrafo non venga cancellato! (NON FUNZIONA)
								Run newRun = new Run(parentDest.getDocument(), "");
								Node newNode = docWork.importNode(childNode.deepClone(false), false);
								if (isInLoop()) { addNodeBreakLoopToDelete(newNode); }

								/*
								if (((Paragraph)newNode).getRuns().getCount() == 0) {

									((Paragraph) newNode).appendChild(newRun);

								}
								*/

								updateParagraphList((Paragraph) childNode, (Paragraph) newNode);
								parentDest.appendChild(newNode);

								if (isSkipElement) {

									nodesToDelete.add(newNode);

								}

							} else {

								Node newNode = docWork.importNode(childNode.deepClone(false), false);
								if (isInLoop()) { addNodeBreakLoopToDelete(newNode); }

								try { parentDest.appendChild(newNode); } catch (Exception exception) { }

								// parentDest.appendChild(docWork.importNode(childNode.deepClone(false), false));

								if (isSkipElement) {

									nodesToDelete.add(newNode);

								}

							}

							/*
							} else {

								System.out.println("Found!");

							}
							*/

						}

					} catch (Exception e) {

						System.err.println("Errore durante la lettura del modello");
						e.printStackTrace();

					}

					Node tmpChildNode = traverseAllNodes((CompositeNode) childNode, (CompositeNode<Node>) parentDest.getLastChild());

					if (tmpChildNode == null) {

						childNode = childNode.getNextSibling();

					} else {

						childNode = tmpChildNode;

					}

				} else {

					// Controllo se la "shape" ? un "text-box grafico" (Rectangle)
					Shape childShape = (Shape) childNode;

					if (childShape.getShapeType() == ShapeType.RECTANGLE && childShape.getCount() > 0) {

						// Controllo se devo evitare di aggiungere il textbox
						boolean isSkipAddElement = this.isSkipElement();

						if (!isSkipAddElement) {

							try {

								Node newNode = docWork.importNode(childShape.deepClone(false), false);
								if (isInLoop()) {
									addNodeBreakLoopToDelete(newNode);
								}
								parentDest.appendChild(newNode);

							} catch (Exception exception) { exception.printStackTrace(); }

						}

						// childNode = childNode.getNextSibling();
						Node tmpChildNode = traverseAllNodes((CompositeNode) childNode, (CompositeNode<Node>) parentDest.getLastChild());

						if (tmpChildNode == null) {

							childNode = childNode.getNextSibling();

						} else {

							childNode = tmpChildNode;

						}

						/*boolean isSkipElement = this.isSkipElement();

						if (!isSkipElement) {

							for (Node node : (Iterable<Node>) childShape.getChildNodes()) {

								try {

									Node newNode = docWork.importNode(node.deepClone(false), false);

									if (isInLoop()) {
										addNodeBreakLoopToDelete(newNode);
									}

									parentDest.appendChild(newNode);

									// parentDest.appendChild(docWork.importNode(childNode.deepClone(false), false));

									if (isSkipElement) {

										nodesToDelete.add(newNode);

									}

								} catch (Exception exception) {
									exception.printStackTrace();
								}

							}

						}*/

					} else {

						try {

							// Controllo se devo evitare di aggiungere l'immagine
							boolean isSkipAddImage = this.isSkipElement();

							if (!isSkipAddImage) {

								Node newNode = docWork.importNode(childNode.deepClone(false), false);
								if (isInLoop()) {
									addNodeBreakLoopToDelete(newNode);
								}
								parentDest.appendChild(newNode);

							}

						} catch (Exception e) {

							System.err.println("Errore durante la copia di una immagine");
							e.printStackTrace();

						}

						childNode = childNode.getNextSibling();

					}

				}

			} else {

				try {

					Node newNode = docWork.importNode(childNode.deepClone(false), false);
					if (isInLoop()) { addNodeBreakLoopToDelete(newNode); }
					parentDest.appendChild(newNode);

				} catch (Exception e) {

					System.err.println("Errore durante la copia di una immagine");
					e.printStackTrace();

				}

				childNode = childNode.getNextSibling();

			}

		}

		// Cancello tutti i paragrafi a cui ho tolto TUTTI i nodi "Run"
		if (parentNode.getNodeType() == NodeType.PARAGRAPH && parentDest.getNodeType() == NodeType.PARAGRAPH) {

			Paragraph parentNodeParagraph = (Paragraph) parentNode;
			Paragraph parentDestParagraph = (Paragraph) parentDest;

			if (	parentDestParagraph.getRuns().getCount() != parentNodeParagraph.getRuns().getCount() &&
					parentDestParagraph.getRuns().getCount() == 0 ) {

				// System.out.println("Found a paragraph to delete!");
				if (parentDest.getParentNode() != null) {
					parentDest.remove();
				}

			}

		}

		return null;

	}

	private void addNodeBreakLoopToDelete(Node node) {

		mainCompositore.getNodesBreakLoopToDelete().add(node);

	}

	private boolean isSkipElement() {

		boolean isSkipAddElement = false;

		if (scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

			RpaScopeLoop scopeLoop		= (RpaScopeLoop) scopeStack.peek();
			isSkipAddElement		= scopeLoop.isSkipNode();

		} else if (scopeStack.peek().getScopeType() == RpaScope.IF_SCOPE_TYPE) {

			RpaScopeIf scopeIf		= (RpaScopeIf) scopeStack.peek();
			isSkipAddElement	= scopeIf.isSkipNode();

		}

		return isSkipAddElement;

	}

	private void updateParagraphList(Paragraph sourceParagraph, Paragraph destinationParagraph) {

		// Controllo che i paragrafi sia associati ad una lista
		if (!sourceParagraph.isListItem()) {

			return;

		}

		try {

			// Recupero i documenti associati ai paragrafi
			DocumentBase sourceDocument			= sourceParagraph.getDocument();
			DocumentBase destinationDocument	= destinationParagraph.getDocument();

			// Estraggo dal paragrafo originale il "list-format" e l'id della lista di appartenenza
			ListFormat	sourceListFormat	= sourceParagraph.getListFormat();
			int			sourceListId		= sourceListFormat.getList().getListId();

			// NOTA: Impossibile clonare il "list-format"

			// Recupero, dal nuovo documento, la "list" associata all'id recuperato e la lista del sorgente
			List destinationList	= destinationDocument.getLists().getListByListId(sourceListId);
			List sourceList			= sourceParagraph.getListFormat().getList();

			// Copio lo stile della lista del paragrafo sorgente a quello di destinazione
			if (sourceList.getStyle() != null) {

				Style copiedStyle = destinationDocument.getStyles().addCopy(sourceList.getStyle());
				copiedStyle.getListFormat().setList(destinationList);

			}

			// NOTA: Impossibile associare ad un paragrafo un nuovo "list-format"

			// Associo la "list" al "list-format"
			ListFormat destinationListFormat = destinationParagraph.getListFormat();
			destinationListFormat.setList(destinationList);

			// Aggiorno le propriet� del "list-format" di destinazione con quelle di origine
			destinationListFormat.setListLevelNumber(sourceListFormat.getListLevelNumber());

			ListLevel sourceListLevel		= sourceListFormat.getListLevel();
			ListLevel destinationListLevel	= destinationListFormat.getListLevel();

			destinationListLevel.setNumberPosition(sourceListLevel.getNumberPosition());
			destinationListLevel.setNumberFormat(sourceListLevel.getNumberFormat());
			destinationListLevel.setAlignment(sourceListLevel.getAlignment());
			destinationListLevel.setLinkedStyle(sourceListLevel.getLinkedStyle());
			destinationListLevel.setNumberStyle(sourceListLevel.getNumberStyle());
			destinationListLevel.setTabPosition(sourceListLevel.getTabPosition());
			destinationListLevel.setTextPosition(sourceListLevel.getTextPosition());
			destinationListLevel.setTrailingCharacter(sourceListLevel.getTrailingCharacter());

			Font sourceFont			= sourceListLevel.getFont();
			Font destinationFont	= destinationListLevel.getFont();

			Style sourceStyle = destinationDocument.getStyles().get(sourceFont.getStyle().getName());

			destinationFont.setStyle(sourceStyle);
			destinationFont.setBold(sourceFont.getBold());
			destinationFont.setName(sourceFont.getName());
			destinationFont.setColor(sourceFont.getColor());
			destinationFont.setSize(sourceFont.getSize());
			destinationFont.setHidden(sourceFont.getHidden());
			destinationFont.setAllCaps(sourceFont.getAllCaps());
			destinationFont.setBidi(sourceFont.getBidi());
			destinationFont.setBoldBi(sourceFont.getBoldBi());
			destinationFont.setComplexScript(sourceFont.getComplexScript());
			destinationFont.setDoubleStrikeThrough(sourceFont.getDoubleStrikeThrough());
			destinationFont.setEmboss(sourceFont.getEmboss());
			destinationFont.setEngrave(sourceFont.getEngrave());
			destinationFont.setHighlightColor(sourceFont.getHighlightColor());
			destinationFont.setItalic(sourceFont.getItalic());
			destinationFont.setItalicBi(sourceFont.getItalicBi());
			destinationFont.setKerning(sourceFont.getKerning());
			destinationFont.setLocaleId(sourceFont.getLocaleId());
			destinationFont.setLocaleIdBi(sourceFont.getLocaleIdBi());
			destinationFont.setBoldBi(sourceFont.getBoldBi());
			destinationFont.setLocaleIdFarEast(sourceFont.getLocaleIdFarEast());
			destinationFont.setNameAscii(sourceFont.getNameAscii());
			destinationFont.setNameOther(sourceFont.getNameOther());
			destinationFont.setNoProofing(sourceFont.getNoProofing());
			destinationFont.setNameBi(sourceFont.getNameBi());
			destinationFont.setNameFarEast(sourceFont.getNameFarEast());
			destinationFont.setOutline(sourceFont.getOutline());
			destinationFont.setPosition(sourceFont.getPosition());
			destinationFont.setScaling(sourceFont.getScaling());
			destinationFont.setShadow(sourceFont.getShadow());
			destinationFont.setSizeBi(sourceFont.getSizeBi());
			destinationFont.setSpacing(sourceFont.getSpacing());
			destinationFont.setStyleIdentifier(sourceFont.getStyleIdentifier());
			destinationFont.setSubscript(sourceFont.getSubscript());
			destinationFont.setSuperscript(sourceFont.getSuperscript());
			destinationFont.setTextEffect(sourceFont.getTextEffect());
			destinationFont.setUnderline(sourceFont.getUnderline());
			destinationFont.setUnderlineColor(sourceFont.getUnderlineColor());

			ParagraphFormat sourceParagraphFormat		= sourceParagraph.getParagraphFormat();
			ParagraphFormat destinationParagraphFormat	= destinationParagraph.getParagraphFormat();
			destinationParagraphFormat.setLeftIndent(sourceParagraphFormat.getLeftIndent());


		} catch (Exception exception) {

			System.err.println("Errore di applicazione dello stile del font al paragrafo di destinazione");
			exception.printStackTrace();

		}

	}

	private boolean isInLoop() {

		for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

			RpaScope scope = scopeStack.get(scopeIndex);

			if (scope instanceof RpaScopeGenericLoop) {

				return true;

			}

		}

		return false;

	}

	private String extendCarriageReturn(String value) {

		int		documentFormatCode	= mainCompositore.getComposerConfiguration().getInputDocument().getOriginalLoadFormat();
		String	documentFormat		= LoadFormat.getName(documentFormatCode);
		boolean	isDocumentTXT		= LoadFormat.TEXT == documentFormatCode;
		boolean isDocumentRTF		= LoadFormat.RTF == documentFormatCode;

	    if (isDocumentTXT || isDocumentRTF) {

	    	return value;

		} else {

			return value.replaceAll("\n", "\n\r");

		}

	}

	private void autoResizeImage(Node containerNode, Shape shapeImage) throws Exception {

		while (containerNode != null) {

			if (containerNode.getNodeType() == NodeType.CELL) {

				Cell	cell			= (Cell) containerNode;
				Double newImageWidth	= cell.getCellFormat().getWidth();
				Double newImageHeight	= cell.getParentRow().getRowFormat().getHeight();
				Double imageWidth		= Double.valueOf(shapeImage.getImageData().getImageSize().getWidthPixels());
				Double imageHeight		= Double.valueOf(shapeImage.getImageData().getImageSize().getHeightPixels());

				// Add the resize by mantain the aspect-ratio
				// (by choose the most higher between the width or height)
				if (newImageWidth > newImageHeight) {

					newImageHeight = (imageHeight * newImageWidth) / imageWidth;
					cell.getCellFormat().setBottomPadding(newImageHeight);

				} else {

					newImageWidth = (imageWidth * newImageHeight) / imageHeight;
					cell.getCellFormat().setWidth(newImageWidth);
					// cell.getCellFormat().setWidth(100.0);

				}

				shapeImage.setWidth(newImageWidth);
				shapeImage.setHeight(newImageHeight);

				break;

			}

			containerNode = containerNode.getParentNode();

		}

	}

	private void removeEmptyParagraph(Node node) {

		if (node.getNodeType() == NodeType.PARAGRAPH) {

			Paragraph paragraph = (Paragraph) node;

			if (paragraph.getRuns().getCount() == 0) {

				paragraph.remove();

			}

		}

		else if (node.isComposite()) {

			CompositeNode<Node> compositeNode = (CompositeNode) node;

			for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

				removeEmptyParagraph(childNode);

			}

		}

	}

	private void printDocumentGraphviz() {

		String graphvizString = "digraph MnemonicGraph {\n";

		graphvizString = recursiveFillDocumentGraphviz(docWork, graphvizString, 0);

		graphvizString += "}";

		// System.out.println(graphvizString);
		debugMessages.print(graphvizString);

	}

	private String recursiveFillDocumentGraphviz(Node node, String graphvizString, int index) {

		if (node.isComposite()) {

			CompositeNode<Node> compositeNode = (CompositeNode) node;

			if (compositeNode.getChildNodes().getCount() == 0) {

				System.err.println("Nodo vuoto: " + compositeNode.getClass());

			}

			for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

				graphvizString += "\"" + compositeNode.getClass() + index + "\" -> ";

				if (childNode.getNodeType() == NodeType.RUN) {

					graphvizString += "\"" + childNode.getText() + "\"\n";

				} else if (childNode.isComposite()) {

					graphvizString += "\"" + compositeNode.getClass() + (index + 1) + "\"\n";

					graphvizString = recursiveFillDocumentGraphviz(childNode, graphvizString, (index + 2));

				} else {

					System.err.println("Nodo vuoto: " + childNode.getText());

				}

			}

		}

		return graphvizString;

	}

}
