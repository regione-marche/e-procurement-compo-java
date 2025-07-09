package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.ANTLRInputStream;
import it.saga.extern.rpa_libs.antlr.v4.runtime.CommonTokenStream;
import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaLoopInlineValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaNextValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeInlineLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMalformedLoopException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaNoLoopForBreakFoundException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaLoopInformationManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicEntityData;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFieldsComparator.ORDER;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaLexer;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.BreakLoopContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.CodeContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.InlineLoopEndContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LoopLimitMathOperationContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LoopLimitMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LoopLimitPositiveIntegerContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LoopSingleMnemonicOrderContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NewInlineLoopContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NewInlineLoopWithOrderContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NewLoopContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NewLoopEndContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NewLoopWithOrderContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OldInlineLoopContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OldInlineLoopWithOrderContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OldLoopContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OldLoopEndContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OldLoopWithOrderContext;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RpaLoopVisitor extends RpaStringVisitor {

	// Link: https://regex101.com/r/zBha5y/2
	private static final String OLD_INLINE_LOOP_END_REGEX       = "\\$\\$ *[A-Za-z0-9_]+ *\\$\\$\\$";

	// Link: https://regex101.com/r/WLBec9/1
	private static final String NEW_INLINE_LOOP_END_REGEX       = "\\[NEXT\\] *[A-Za-z0-9_]+ *\\[NEXTR\\]";

	// Link: https://regex101.com/r/bOV78z/2
    private static final String INLINE_LOOP_ID_EXTRACTION_REGEX = "^((\\$ *)|(\\[NEXT\\] *))([A-Za-z0-9_]+)$";

	public RpaLoopVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

		super(conn, scope, mainCompositore, parentNode, childNode);

	}

	/*
	        #########################################################################################
	        #                                                                                       #
	        #                           DICHIARAZIONI INIZIO VECCHIO LOOP                           #
	        #                                                                                       #
	        #########################################################################################
	 */

	@Override
	public RpaLoopValue<?> visitOldLoop(OldLoopContext context) {

		RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

		// Verifico se sono tornato indietro (ossia se sono in uno scope con lo stesso id del loop)
		RpaScopeLoop scopeLoop;
		int			loopId		= Integer.valueOf(context.id.getText());

		if (scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

			RpaScopeLoop lastScopeLoop = (RpaScopeLoop) scopeStack.peek();

			// Controllo se sono tornato indietro
			if (loopId == lastScopeLoop.getForId()) {

				scopeLoop				= lastScopeLoop;
				RpaLoopValue loopValue	= (RpaLoopValue) scopeLoop.getLoopValue();

				// Incremento il contatore dell'indice
				loopValue.setCurrentValue(loopValue.getCurrentValue() + loopValue.getStep());

				// Verifico se l'indice ha superato il limite superiore
				scopeLoop.setIsSkipNode(loopValue.getCurrentValue() > loopValue.getUpperLimit());

				// Aggiorno l'indice del mnemonico / entità associata al loop
				if (loopValue.isMnemonicEntityFound()) {
					mnemonicManager.updateMnemonicEntityDataIndex(scopeStack, loopValue.getIndexName(), loopValue.getCurrentValue() + 1);
				}

				// Resetto il contesto dei mnemonici
				scopeLoop.recoverMnemonicContextStack(scopeStack);

				// Esco
				return loopValue;
			}

            // Controllo se sono in un loop da saltare
            else if (lastScopeLoop.isSkipNode()) {

			    lastScopeLoop.increaseInternalLoopToSkip();

				// Esco
				return new RpaLoopValue(null, mainCompositore);

            }

		}

		scopeLoop = new RpaScopeLoop(mainCompositore);

		// Controllo se al loop è associato un mnemonico
		RpaLoopInformationManager.LoopInformation loopInformation =
				mainCompositore.getLoopInformationManager().getLoopInformation(loopId);

		String				loopMnemonicName	= null;
		ArrayList<String>	mnemonicValues		= null;

		RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.oldLoopPrefix().loopLimitInf);
		RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.oldLoopPrefix().loopLimitSup);

		int declaredLowerLimit = declaredLowerLimitValue.getValue();
		int declaredUpperLimit = declaredUpperLimitValue.getValue();

		int lowerLimit = declaredLowerLimit;
		int upperLimit;

		int mnemonicLoopRowsCount = Integer.MAX_VALUE;

		// Estraggo l'indice del loop
		String indexName	= context.oldLoopPrefix().LOOP_ID().getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopValue loopValue = new RpaLoopValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		if (context.NOENT() == null && loopInformation.getMnemonicName() != null) {

			loopMnemonicName = loopInformation.getMnemonicName();

			try {

				if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

					mnemonicLoopRowsCount = 0;

				} else {

					// Chiamo la funzione che mi da il numero di risultati del mnemonico e
					// definisco il limite superiore in base al numero di risultati trovati
					RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
					mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

					scopeMnemonicContext.updateLastReadMnemonic();

				}

			} catch (RpaComposerException composerException) {

				throw composerException;

			} catch (Exception exception) {

				exception.printStackTrace();

				String code			= context.getText();
				String message		= "[OLD LOOP] Errore nel recupero del numero di istanze " +
						"per il mnemonico " + loopMnemonicName;
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;

				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
		upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

		// Controllo che l'indice non sia stato definito in un altro loop
		for (RpaScope scope : scopeStack) {

			if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

				RpaScopeLoop stackScopeLoop = (RpaScopeLoop) scope;
				RpaLoopValue stackLoopValue = (RpaLoopValue) stackScopeLoop.getLoopValue();

				if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;

					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

				}

			}

		}

		// Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

		// Aggiungo uno scope di tipo "loop"
		loopValue.setIndexName(indexName);
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

		scopeLoop.setLoopValue(loopValue);
		scopeLoop.setForNode(this.parentNode);
		scopeLoop.setIsSkipNode(lowerLimit > upperLimit);
		scopeLoop.setForId(loopId);
		scopeLoop.setType(RpaScopeLoop.Type.OLD);
		scopeLoop.backupMnemonicContextStack(scopeStack);

		scopeStack.push(scopeLoop);

		return loopValue;

	}

	@Override
	public RpaValue<?> visitOldLoopWithOrder(OldLoopWithOrderContext context) {

		RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

        // Verifico se sono tornato indietro (ossia se sono in uno scope con lo stesso id del loop)
        RpaScopeLoop scopeLoop;
        int			loopId		= Integer.valueOf(context.id.getText());

        if (scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

            RpaScopeLoop lastScopeLoop = (RpaScopeLoop) scopeStack.peek();

            // Controllo se sono tornato indietro
            if (loopId == lastScopeLoop.getForId()) {

                scopeLoop			= lastScopeLoop;
                RpaLoopValue loopValue	= (RpaLoopValue) scopeLoop.getLoopValue();

                // Incremento il contatore dell'indice
				loopValue.setCurrentValue(loopValue.getCurrentValue() + loopValue.getStep());

                // Verifico se l'indice ha superato il limite superiore
                scopeLoop.setIsSkipNode(loopValue.getCurrentValue() > loopValue.getUpperLimit());

				// Aggiorno l'indice del mnemonico / entità associata al loop
				if (loopValue.isMnemonicEntityFound()) {
					mnemonicManager.updateMnemonicEntityDataIndex(scopeStack, loopValue.getIndexName(), loopValue.getCurrentValue() + 1);
				}

				// Resetto il contesto dei mnemonici
				scopeLoop.recoverMnemonicContextStack(scopeStack);

                // Esco
                return loopValue;
            }

            // Controllo se sono in un loop da saltare
            else if (lastScopeLoop.isSkipNode()) {

                lastScopeLoop.increaseInternalLoopToSkip();

                // Esco
                return new RpaLoopValue(null, mainCompositore);

            }

        }

        scopeLoop = new RpaScopeLoop(mainCompositore);

        String				loopMnemonicName	= null;
        ArrayList<String>	mnemonicValues		= null;

        RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.oldLoopPrefix().loopLimitInf);
        RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.oldLoopPrefix().loopLimitSup);

        int declaredLowerLimit = declaredLowerLimitValue.getValue();
        int declaredUpperLimit = declaredUpperLimitValue.getValue();

        int lowerLimit = declaredLowerLimit;
        int upperLimit;

        int mnemonicLoopRowsCount;
		loopMnemonicName = context.loopSingleMnemonicOrder(0).mnemonic().getText();
		loopMnemonicName = loopMnemonicName.replaceAll("#", "");

		RpaMnemonicEntityData mnemonicEntityData;

		// Estraggo l'indice del loop
		String indexName	= context.oldLoopPrefix().LOOP_ID().getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopValue loopValue = new RpaLoopValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		try {

			if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

				mnemonicLoopRowsCount = 0;

			} else {

				// Chiamo la funzione che mi da il numero di risultati del mnemonico e
				// definisco il limite superiore in base al numero di risultati trovati
				RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
				mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

				scopeMnemonicContext.updateLastReadMnemonic();

			}

		} catch (Exception exception) {

			System.err.println(exception);

			String code			= context.getText();
			String message		= "[OLD LOOP] Errore nel recupero del numero di istanze " +
					"per il mnemonico " + loopMnemonicName;
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;

			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

        // Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
        upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

        // Controllo che l'indice non sia stato definito in un altro loop
        for (RpaScope scope : scopeStack) {

            if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

                RpaScopeLoop stackScopeLoop	= (RpaScopeLoop) scope;
                RpaLoopValue stackLoopValue	= (RpaLoopValue) stackScopeLoop.getLoopValue();

                if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;

					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

                }

            }

        }

        // Controllo se è stato definito lo step con cui incrementare l'indice
        int step = 1;

        if (context.step != null) {

            step = Integer.valueOf(context.step.getText());

        }

		// Estraggo i nomi dei mnemonici e il loro ordine
		List<LoopSingleMnemonicOrderContext> loopSingleMnemonicOrderContextList = context.loopSingleMnemonicOrder();

        List<String>	mnemonicNameList	= new ArrayList<String>();
        List<ORDER>		mnemonicOrderList	= new ArrayList<ORDER>();

        for (LoopSingleMnemonicOrderContext loopSingleMnemonicOrderContext : loopSingleMnemonicOrderContextList) {

        	String	mnemonicName		= loopSingleMnemonicOrderContext.mnemonic().getText();
			Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicName);
			mnemonicNameMatcher.find();
			mnemonicName = mnemonicNameMatcher.group();

        	ORDER 	order;

        	if (loopSingleMnemonicOrderContext.ADD() != null) {

        		order = ORDER.ASC;

			} else if (loopSingleMnemonicOrderContext.SUB() != null) {

        		order = ORDER.DESC;

			} else {

        		order = ORDER.ASC;

			}

			mnemonicNameList.add(mnemonicName);
        	mnemonicOrderList.add(order);

		}

		// Controllo che i mnemonici di ordinamento, e quello del primo loop, appartengano tutti alla stessa entità
		String loopFullEntityName	= mnemonicManager.translateMnemonicName(loopMnemonicName);
		String lastEntityFound		= loopFullEntityName.split("\\.", 2)[1];

		List<String> fieldNameList = new ArrayList<String>();

		for (String mnemonicName : mnemonicNameList) {

			String fieldName		= mnemonicManager.translateMnemonicName(mnemonicName);
			String fullEntityName	= fieldName.split("\\.", 2)[1];
			fieldName 				= fieldName.split("\\.")[0];

			fieldNameList.add(fieldName);

			if (!lastEntityFound.equals(fullEntityName)) {

				String code			= context.getText();
				String message		= "Tutti i mnemonici dell'ordinamento " +
						"devono appartenere alla stessa entità di #" + loopMnemonicName + "#";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;

				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ordino l'entityManager che verrà utilizzato dal loop
		try {

			mnemonicManager.sortEntity(scopeStack, mnemonicNameList, mnemonicOrderList);

		} catch (Exception exception) {

			System.err.println(exception);

			String code			= context.getText();
			String message		= "Errore nell'ordinamento dell'entità";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;

			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

        // Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

        scopeLoop.setLoopValue(loopValue);
        scopeLoop.setForNode(this.parentNode);
        scopeLoop.setIsSkipNode(lowerLimit > upperLimit);
        scopeLoop.setForId(loopId);
        scopeLoop.setType(RpaScopeLoop.Type.OLD);
		scopeLoop.backupMnemonicContextStack(scopeStack);

        scopeStack.push(scopeLoop);

        return loopValue;

	}

	@Override
	public RpaValue<?> visitOldInlineLoop(OldInlineLoopContext context) {

		String				instructionText = context.getParent().getParent().getText();
		RpaScopeInlineLoop	scopeInlineLoop = new RpaScopeInlineLoop(super.childNode, mainCompositore);

		// Controllo se il nodo in cui è contenuto il loop contiene anche la fine del loop
		String codeContextText = context.getParent().getParent().getParent().getText();

		Matcher oldInlineLoopEndMatcher = Pattern.compile(OLD_INLINE_LOOP_END_REGEX).matcher(codeContextText);
		Matcher newInlineLoopEndMatcher = Pattern.compile(NEW_INLINE_LOOP_END_REGEX).matcher(codeContextText);

		int inlineLoopState;
		if (oldInlineLoopEndMatcher.find() || newInlineLoopEndMatcher.find()) {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_NODE;

		} else {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_MANY_NODES;

		}

		scopeInlineLoop.setState(inlineLoopState);

		// Creo uno "ScopeInlineLoop" nella stessa maniera con cui creo uno "ScopeLoop"
		RpaMnemonicManager	mnemonicManager	= mainCompositore.getMnemonicManager();
		int					loopId			= Integer.valueOf(context.oldInlineLoopSuffix().id.getText());

		RpaLoopInformationManager.LoopInformation loopInformation =
				mainCompositore.getLoopInformationManager().getLoopInformation(loopId);

		String				loopMnemonicName	= null;
		ArrayList<String>	mnemonicValues		= null;

		RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.oldInlineLoopPrefix().loopLimitInf);
		RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.oldInlineLoopPrefix().loopLimitSup);

		int declaredLowerLimit = declaredLowerLimitValue.getValue();
		int declaredUpperLimit = declaredUpperLimitValue.getValue();

		int lowerLimit = declaredLowerLimit;
		int upperLimit;

		int mnemonicLoopRowsCount = Integer.MAX_VALUE;

		// Estraggo l'indice del loop
		String indexName	= context.oldInlineLoopPrefix().LOOP_ID().getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopInlineValue loopValue = new RpaLoopInlineValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		if (context.NOENT() == null && loopInformation.getMnemonicName() != null) {

			loopMnemonicName = loopInformation.getMnemonicName();

			try {

				if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

					mnemonicLoopRowsCount = 0;

				} else {

					// Chiamo la funzione che mi da il numero di risultati del mnemonico e
					// definisco il limite superiore in base al numero di risultati trovati
					RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
					mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

					scopeMnemonicContext.updateLastReadMnemonic();

				}

			} catch (Exception exception) {

				System.err.println(exception);

				String code			= context.getText();
				String message		= "[OLD LOOP] Errore nel recupero del numero di istanze " +
						"per il mnemonico " + loopMnemonicName;
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;

				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
		upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

		// Controllo che l'indice non sia stato definito in un altro loop
		for (RpaScope scope : scopeStack) {

			if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

				RpaScopeLoop stackScopeLoop	= (RpaScopeLoop) scope;
				RpaLoopValue stackLoopValue	= (RpaLoopValue) stackScopeLoop.getLoopValue();

				if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;

					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

				}

			}

		}

		// Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

		// Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

		scopeInlineLoop.setLoopValue(loopValue);
		scopeInlineLoop.setForNode(this.parentNode);
		scopeInlineLoop.setIsSkipNode(lowerLimit > upperLimit);
		scopeInlineLoop.setForId(loopId);
		scopeInlineLoop.setType(RpaScopeLoop.Type.OLD);
		scopeInlineLoop.backupMnemonicContextStack(scopeStack);

		scopeStack.push(scopeInlineLoop);

		return loopValue;

	}

	@Override
	public RpaValue<?> visitOldInlineLoopWithOrder(OldInlineLoopWithOrderContext context) {

		String				instructionText = context.getParent().getParent().getText();
		RpaScopeInlineLoop	scopeInlineLoop = new RpaScopeInlineLoop(super.childNode, mainCompositore);

		// Controllo se il nodo in cui è contenuto il loop contiene anche la fine del loop
		String codeContextText = context.getParent().getParent().getParent().getText();

		Matcher oldInlineLoopEndMatcher = Pattern.compile(OLD_INLINE_LOOP_END_REGEX).matcher(codeContextText);
		Matcher newInlineLoopEndMatcher = Pattern.compile(NEW_INLINE_LOOP_END_REGEX).matcher(codeContextText);

		int inlineLoopState;
		if (oldInlineLoopEndMatcher.find() || newInlineLoopEndMatcher.find()) {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_NODE;

		} else {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_MANY_NODES;

		}

		scopeInlineLoop.setState(inlineLoopState);

		// Creo uno "ScopeInlineLoop" nella stessa maniera con cui creo uno "ScopeLoop"
		RpaMnemonicManager	mnemonicManager	= mainCompositore.getMnemonicManager();
		int					loopId			= Integer.valueOf(context.oldInlineLoopSuffix().id.getText());

		RpaLoopInformationManager.LoopInformation loopInformation =
				mainCompositore.getLoopInformationManager().getLoopInformation(loopId);

		String				loopMnemonicName	= null;
		ArrayList<String>	mnemonicValues		= null;

		RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.oldInlineLoopPrefix().loopLimitInf);
		RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.oldInlineLoopPrefix().loopLimitSup);

		int declaredLowerLimit = declaredLowerLimitValue.getValue();
		int declaredUpperLimit = declaredUpperLimitValue.getValue();

		int lowerLimit = declaredLowerLimit;
		int upperLimit;

		int mnemonicLoopRowsCount = Integer.MAX_VALUE;

		loopMnemonicName = context.loopSingleMnemonicOrder(0).mnemonic().getText();
		loopMnemonicName = loopMnemonicName.replaceAll("#", "");

		// Estraggo l'indice del loop
		String indexName	= context.oldInlineLoopPrefix().LOOP_ID().getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopInlineValue loopValue = new RpaLoopInlineValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		try {

			if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

				mnemonicLoopRowsCount = 0;

			} else {

				// Chiamo la funzione che mi da il numero di risultati del mnemonico e
				// definisco il limite superiore in base al numero di risultati trovati
				RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
				mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

				scopeMnemonicContext.updateLastReadMnemonic();

			}

		} catch (Exception exception) {

			System.err.println(exception);

			String code			= context.getText();
			String message		= "[OLD LOOP] Errore nel recupero del numero di istanze " +
					"per il mnemonico " + loopMnemonicName;
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;

			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

		// Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
		upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

		// Controllo che l'indice non sia stato definito in un altro loop
		for (RpaScope scope : scopeStack) {

			if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

				RpaScopeLoop stackScopeLoop	= (RpaScopeLoop) scope;
				RpaLoopValue stackLoopValue	= (RpaLoopValue) stackScopeLoop.getLoopValue();

				if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;

					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

				}

			}

		}

		// Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

		// Estraggo i nomi dei mnemonici e il loro ordine
		List<LoopSingleMnemonicOrderContext> loopSingleMnemonicOrderContextList = context.loopSingleMnemonicOrder();

		List<String>	mnemonicNameList	= new ArrayList<String>();
		List<ORDER>		mnemonicOrderList	= new ArrayList<ORDER>();

		for (LoopSingleMnemonicOrderContext loopSingleMnemonicOrderContext : loopSingleMnemonicOrderContextList) {

			String	mnemonicName		= loopSingleMnemonicOrderContext.mnemonic().getText();
			Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicName);
			mnemonicNameMatcher.find();
			mnemonicName = mnemonicNameMatcher.group();

			ORDER 	order;

			if (loopSingleMnemonicOrderContext.ADD() != null) {

				order = ORDER.ASC;

			} else if (loopSingleMnemonicOrderContext.SUB() != null) {

				order = ORDER.DESC;

			} else {

				order = ORDER.ASC;

			}

			mnemonicNameList.add(mnemonicName);
			mnemonicOrderList.add(order);

		}

		// Controllo che i mnemonici di ordinamento, e quello del primo loop, appartengano tutti alla stessa entità
		String loopFullEntityName	= mnemonicManager.translateMnemonicName(loopMnemonicName);
		String lastEntityFound		= loopFullEntityName.split("\\.", 2)[1];

		List<String> fieldNameList = new ArrayList<String>();

		for (String mnemonicName : mnemonicNameList) {

			String fieldName		= mnemonicManager.translateMnemonicName(mnemonicName);
			String fullEntityName	= fieldName.split("\\.", 2)[1];
			fieldName 				= fieldName.split("\\.")[0];

			fieldNameList.add(fieldName);

			if (!lastEntityFound.equals(fullEntityName)) {

				String code			= context.getText();
				String message		= "Tutti i mnemonici dell'ordinamento " +
						"devono appartenere alla stessa entità di #" + loopMnemonicName + "#";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;

				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ordino l'entityManager che verrà utilizzato dal loop
		try {

			mnemonicManager.sortEntity(scopeStack, mnemonicNameList, mnemonicOrderList);

		} catch (Exception exception) {

			System.err.println(exception);
			String code			= context.getText();
			String message		= "Errore nell'ordinamento dell'entità";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

		// Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

		scopeInlineLoop.setLoopValue(loopValue);
		scopeInlineLoop.setForNode(this.parentNode);
		scopeInlineLoop.setIsSkipNode(lowerLimit > upperLimit);
		scopeInlineLoop.setForId(loopId);
		scopeInlineLoop.setType(RpaScopeLoop.Type.OLD);
		scopeInlineLoop.backupMnemonicContextStack(scopeStack);

		scopeStack.push(scopeInlineLoop);

		return loopValue;

	}

	/*
	        #########################################################################################
	        #                                                                                       #
	        #                           DICHIARAZIONI INIZIO NUOVO LOOP                             #
	        #                                                                                       #
	        #########################################################################################
	 */

    @Override
    public RpaLoopValue<?> visitNewLoop(NewLoopContext context) {

		RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

        // Verifico se sono tornato indietro (ossia se sono in uno scope con lo stesso id del loop)
        RpaScopeLoop scopeLoop;
        int			loopId		= Integer.valueOf(context.id.getText());

        if (scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

            RpaScopeLoop lastScopeLoop = (RpaScopeLoop) scopeStack.peek();

            // Controllo se sono tornato indietro
            if (loopId == lastScopeLoop.getForId()) {

                scopeLoop				= lastScopeLoop;
                RpaLoopValue loopValue	= (RpaLoopValue) scopeLoop.getLoopValue();

                // Incremento il contatore dell'indice
				loopValue.setCurrentValue(loopValue.getCurrentValue() + loopValue.getStep());

                // Verifico se l'indice ha superato il limite superiore
                scopeLoop.setIsSkipNode(loopValue.getCurrentValue() > loopValue.getUpperLimit());

				// Aggiorno l'indice del mnemonico / entità associata al loop
				if (loopValue.isMnemonicEntityFound()) {
					mnemonicManager.updateMnemonicEntityDataIndex(scopeStack, loopValue.getIndexName(), loopValue.getCurrentValue() + 1);
				}

				// Resetto il contesto dei mnemonici
				scopeLoop.recoverMnemonicContextStack(scopeStack);

                // Esco
                return loopValue;

            }

            // Controllo se sono in un loop da saltare
            else if (lastScopeLoop.isSkipNode()) {

                lastScopeLoop.increaseInternalLoopToSkip();

                // Esco
                return new RpaLoopValue(null, mainCompositore);

            }

        }

        scopeLoop = new RpaScopeLoop(mainCompositore);

        // Controllo se al loop è associato un mnemonico
        RpaLoopInformationManager.LoopInformation loopInformation =
				mainCompositore.getLoopInformationManager().getLoopInformation(loopId);

        String				loopMnemonicName	= null;
        ArrayList<String>	mnemonicValues		= null;

        RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.newLoopPrefix().loopLimitInf);
        RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.newLoopPrefix().loopLimitSup);

        int declaredLowerLimit = declaredLowerLimitValue.getValue();
        int declaredUpperLimit = declaredUpperLimitValue.getValue();

        int lowerLimit = declaredLowerLimit;
        int upperLimit;

        int mnemonicLoopRowsCount = Integer.MAX_VALUE;

		// Estraggo l'indice del loop
		String indexName	= context.newLoopPrefix().SQUARE_SUFFIX_ID().getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopValue loopValue = new RpaLoopValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

        if (context.NOENT() == null && loopInformation.getMnemonicName() != null) {

            loopMnemonicName = loopInformation.getMnemonicName();

			try {

				if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

					mnemonicLoopRowsCount = 0;

				} else {

					// Chiamo la funzione che mi da il numero di risultati del mnemonico e
					// definisco il limite superiore in base al numero di risultati trovati
					RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
					mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

					scopeMnemonicContext.updateLastReadMnemonic();

				}

			} catch (Exception exception) {

				System.err.println(exception);
				String code			= context.getText();
				String message		= "[NEW LOOP] Errore nel recupero del numero di istanze " +
						"per il mnemonico " + loopMnemonicName;
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

        }

        // Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
        upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

        // Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

        // Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

        scopeLoop.setLoopValue(loopValue);
        scopeLoop.setForNode(this.parentNode);
        scopeLoop.setIsSkipNode(lowerLimit > upperLimit);
        scopeLoop.setForId(loopId);
        scopeLoop.setType(RpaScopeLoop.Type.NEW);
        scopeLoop.backupMnemonicContextStack(scopeStack);

        scopeStack.push(scopeLoop);

        return loopValue;

    }

	@Override
	public RpaValue<?> visitNewLoopWithOrder(NewLoopWithOrderContext context) {

		RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

		// Verifico se sono tornato indietro (ossia se sono in uno scope con lo stesso id del loop)
		RpaScopeLoop scopeLoop;
		int			loopId		= Integer.valueOf(context.id.getText());

		if (scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

			RpaScopeLoop lastScopeLoop = (RpaScopeLoop) scopeStack.peek();

			// Controllo se sono tornato indietro
			if (loopId == lastScopeLoop.getForId()) {

				scopeLoop			= lastScopeLoop;
				RpaLoopValue loopValue	= (RpaLoopValue) scopeLoop.getLoopValue();

				// Incremento il contatore dell'indice
				loopValue.setCurrentValue(loopValue.getCurrentValue() + loopValue.getStep());

				// Verifico se l'indice ha superato il limite superiore
				scopeLoop.setIsSkipNode(loopValue.getCurrentValue() > loopValue.getUpperLimit());

				// Aggiorno l'indice del mnemonico / entità associata al loop
				if (loopValue.isMnemonicEntityFound()) {
					mnemonicManager.updateMnemonicEntityDataIndex(scopeStack, loopValue.getIndexName(), loopValue.getCurrentValue() + 1);
				}

				// Resetto il contesto dei mnemonici
				scopeLoop.recoverMnemonicContextStack(scopeStack);

				// Esco
				return loopValue;
			}

			// Controllo se sono in un loop da saltare
			else if (lastScopeLoop.isSkipNode()) {

				lastScopeLoop.increaseInternalLoopToSkip();

				// Esco
				return new RpaLoopValue(null, mainCompositore);

			}

		}

		scopeLoop = new RpaScopeLoop(mainCompositore);

		String				loopMnemonicName	= null;
		ArrayList<String>	mnemonicValues		= null;

		RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.newLoopPrefix().loopLimitInf);
		RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.newLoopPrefix().loopLimitSup);

		int declaredLowerLimit = declaredLowerLimitValue.getValue();
		int declaredUpperLimit = declaredUpperLimitValue.getValue();

		int lowerLimit = declaredLowerLimit;
		int upperLimit;

		int mnemonicLoopRowsCount;
		loopMnemonicName = context.loopSingleMnemonicOrder(0).mnemonic().getText();
		loopMnemonicName = loopMnemonicName.replaceAll("#", "");

		RpaMnemonicEntityData mnemonicEntityData;

		// Estraggo l'indice del loop
		String indexName	= context.newLoopPrefix().SQUARE_SUFFIX_ID().getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopValue loopValue = new RpaLoopValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		try {

			if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

				mnemonicLoopRowsCount = 0;

			} else {

				// Chiamo la funzione che mi da il numero di risultati del mnemonico e
				// definisco il limite superiore in base al numero di risultati trovati
				RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
				mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

				scopeMnemonicContext.updateLastReadMnemonic();

			}

		} catch (Exception exception) {

			System.err.println(exception);
			String code			= context.getText();
			String message		= "[NEW LOOP] Errore nel recupero del numero di istanze " +
					"per il mnemonico " + loopMnemonicName;
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

		// Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
		upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

		// Controllo che l'indice non sia stato definito in un altro loop
		for (RpaScope scope : scopeStack) {

			if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

				RpaScopeLoop stackScopeLoop	= (RpaScopeLoop) scope;
				RpaLoopValue stackLoopValue	= (RpaLoopValue) stackScopeLoop.getLoopValue();

				if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;
					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

				}

			}

		}

		// Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

		// Estraggo i nomi dei mnemonici e il loro ordine
		List<LoopSingleMnemonicOrderContext> loopSingleMnemonicOrderContextList = context.loopSingleMnemonicOrder();

		List<String>	mnemonicNameList	= new ArrayList<String>();
		List<ORDER>		mnemonicOrderList	= new ArrayList<ORDER>();

		for (LoopSingleMnemonicOrderContext loopSingleMnemonicOrderContext : loopSingleMnemonicOrderContextList) {

			String	mnemonicName		= loopSingleMnemonicOrderContext.mnemonic().getText();
			Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicName);
			mnemonicNameMatcher.find();
			mnemonicName = mnemonicNameMatcher.group();

			ORDER 	order;

			if (loopSingleMnemonicOrderContext.ADD() != null) {

				order = ORDER.ASC;

			} else if (loopSingleMnemonicOrderContext.SUB() != null) {

				order = ORDER.DESC;

			} else {

				order = ORDER.ASC;

			}

			mnemonicNameList.add(mnemonicName);
			mnemonicOrderList.add(order);

		}

		// Controllo che i mnemonici di ordinamento, e quello del primo loop, appartengano tutti alla stessa entità
		String loopFullEntityName	= mnemonicManager.translateMnemonicName(loopMnemonicName);
		String lastEntityFound		= loopFullEntityName.split("\\.", 2)[1];

		List<String> fieldNameList = new ArrayList<String>();

		for (String mnemonicName : mnemonicNameList) {

			String fieldName		= mnemonicManager.translateMnemonicName(mnemonicName);
			String fullEntityName	= fieldName.split("\\.", 2)[1];
			fieldName 				= fieldName.split("\\.")[0];

			fieldNameList.add(fieldName);

			if (!lastEntityFound.equals(fullEntityName)) {

				String code			= context.getText();
				String message		= "Tutti i mnemonici dell'ordinamento " +
						"devono appartenere alla stessa entità di #" + loopMnemonicName + "#";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ordino l'entityManager che verrà utilizzato dal loop
		try {

			mnemonicManager.sortEntity(scopeStack, mnemonicNameList, mnemonicOrderList);

		} catch (Exception exception) {

			System.err.println(exception);
			String code			= context.getText();
			String message		= "Errore nell'ordinamento dell'entità";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

		// Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

		scopeLoop.setLoopValue(loopValue);
		scopeLoop.setForNode(this.parentNode);
		scopeLoop.setIsSkipNode(lowerLimit > upperLimit);
		scopeLoop.setForId(loopId);
		scopeLoop.setType(RpaScopeLoop.Type.NEW);
		scopeLoop.backupMnemonicContextStack(scopeStack);

		scopeStack.push(scopeLoop);

		return loopValue;

	}

	@Override
	public RpaValue<?> visitNewInlineLoop(NewInlineLoopContext context) {

		String			instructionText = context.getParent().getParent().getText();
		RpaScopeInlineLoop scopeInlineLoop = new RpaScopeInlineLoop(super.childNode, mainCompositore);

		// Controllo se il nodo in cui è contenuto il loop contiene anche la fine del loop
		String codeContextText = context.getParent().getParent().getParent().getText();

		Matcher oldInlineLoopEndMatcher = Pattern.compile(OLD_INLINE_LOOP_END_REGEX).matcher(codeContextText);
		Matcher newInlineLoopEndMatcher = Pattern.compile(NEW_INLINE_LOOP_END_REGEX).matcher(codeContextText);

		int inlineLoopState;
		if (oldInlineLoopEndMatcher.find() || newInlineLoopEndMatcher.find()) {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_NODE;

		} else {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_MANY_NODES;

		}

		scopeInlineLoop.setState(inlineLoopState);

		// Creo uno "ScopeInlineLoop" nella stessa maniera con cui creo uno "ScopeLoop"
		RpaMnemonicManager mnemonicManager	= mainCompositore.getMnemonicManager();
		int				loopId			= Integer.valueOf(context.newInlineLoopSuffix().id.getText());

		RpaLoopInformationManager.LoopInformation loopInformation =
				mainCompositore.getLoopInformationManager().getLoopInformation(loopId);

		String				loopMnemonicName	= null;
		ArrayList<String>	mnemonicValues		= null;

		RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.newInlineLoopPrefix().loopLimitInf);
		RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.newInlineLoopPrefix().loopLimitSup);

		int declaredLowerLimit = declaredLowerLimitValue.getValue();
		int declaredUpperLimit = declaredUpperLimitValue.getValue();

		int lowerLimit = declaredLowerLimit;
		int upperLimit;

		int mnemonicLoopRowsCount = Integer.MAX_VALUE;

		// Estraggo l'indice del loop
		String indexName	= context.newInlineLoopPrefix().loopIdName.getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopInlineValue loopValue = new RpaLoopInlineValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		if (context.NOENT() == null && loopInformation.getMnemonicName() != null) {

			loopMnemonicName = loopInformation.getMnemonicName();

			try {

				if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

					mnemonicLoopRowsCount = 0;

				} else {

					// Chiamo la funzione che mi da il numero di risultati del mnemonico e
					// definisco il limite superiore in base al numero di risultati trovati
					RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
					mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

					scopeMnemonicContext.updateLastReadMnemonic();

				}

			} catch (Exception exception) {

				System.err.println(exception);
				String code			= context.getText();
				String message		= "[OLD LOOP] Errore nel recupero del numero di istanze " +
						"per il mnemonico " + loopMnemonicName;
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
		upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

		// Controllo che l'indice non sia stato definito in un altro loop
		for (RpaScope scope : scopeStack) {

			if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

				RpaScopeLoop stackScopeLoop	= (RpaScopeLoop) scope;
				RpaLoopValue stackLoopValue	= (RpaLoopValue) stackScopeLoop.getLoopValue();

				if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;
					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

				}

			}

		}

		// Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

		// Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

		scopeInlineLoop.setLoopValue(loopValue);
		scopeInlineLoop.setForNode(this.parentNode);
		scopeInlineLoop.setIsSkipNode(lowerLimit > upperLimit);
		scopeInlineLoop.setForId(loopId);
		scopeInlineLoop.setType(RpaScopeLoop.Type.OLD);
		scopeInlineLoop.backupMnemonicContextStack(scopeStack);

		scopeStack.push(scopeInlineLoop);

		return loopValue;

	}

	@Override
	public RpaValue<?> visitNewInlineLoopWithOrder(NewInlineLoopWithOrderContext context) {

		String			instructionText = context.getParent().getParent().getText();
		RpaScopeInlineLoop scopeInlineLoop = new RpaScopeInlineLoop(super.childNode, mainCompositore);

		// Controllo se il nodo in cui è contenuto il loop contiene anche la fine del loop
		String codeContextText = context.getParent().getParent().getParent().getText();

		Matcher oldInlineLoopEndMatcher = Pattern.compile(OLD_INLINE_LOOP_END_REGEX).matcher(codeContextText);
		Matcher newInlineLoopEndMatcher = Pattern.compile(NEW_INLINE_LOOP_END_REGEX).matcher(codeContextText);

		int inlineLoopState;
		if (oldInlineLoopEndMatcher.find() || newInlineLoopEndMatcher.find()) {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_NODE;

		} else {

			inlineLoopState = RpaScopeInlineLoop.LOOP_ON_MANY_NODES;

		}

		scopeInlineLoop.setState(inlineLoopState);

		// Creo uno "ScopeInlineLoop" nella stessa maniera con cui creo uno "ScopeLoop"
		RpaMnemonicManager	mnemonicManager	= mainCompositore.getMnemonicManager();
		int					loopId			= Integer.valueOf(context.newInlineLoopSuffix().id.getText());

		RpaLoopInformationManager.LoopInformation loopInformation =
				mainCompositore.getLoopInformationManager().getLoopInformation(loopId);

		String				loopMnemonicName	= null;
		ArrayList<String>	mnemonicValues		= null;

		RpaValue<Integer> declaredLowerLimitValue = (RpaValue<Integer>) visit(context.newInlineLoopPrefix().loopLimitInf);
		RpaValue<Integer> declaredUpperLimitValue = (RpaValue<Integer>) visit(context.newInlineLoopPrefix().loopLimitSup);

		int declaredLowerLimit = declaredLowerLimitValue.getValue();
		int declaredUpperLimit = declaredUpperLimitValue.getValue();

		int lowerLimit = declaredLowerLimit;
		int upperLimit;

		int mnemonicLoopRowsCount = Integer.MAX_VALUE;

		loopMnemonicName = context.loopSingleMnemonicOrder(0).mnemonic().getText();
		loopMnemonicName = loopMnemonicName.replaceAll("#", "");

		// Estraggo l'indice del loop
		String indexName	= context.newInlineLoopPrefix().loopIdName.getText();
		indexName			= indexName.substring(1, indexName.length() - 1);
		indexName			= indexName.trim();

		RpaLoopInlineValue loopValue = new RpaLoopInlineValue(loopId, mainCompositore);
		loopValue.setIndexName(indexName);

		try {

			if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

				mnemonicLoopRowsCount = 0;

			} else {

				// Chiamo la funzione che mi da il numero di risultati del mnemonico e
				// definisco il limite superiore in base al numero di risultati trovati
				RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, loopMnemonicName);
				mnemonicLoopRowsCount = scopeMnemonicContext.requestMnemonicOccurrenceCount(loopMnemonicName, loopValue);

				scopeMnemonicContext.updateLastReadMnemonic();

			}

		} catch (Exception exception) {

			System.err.println(exception);
			String code			= context.getText();
			String message		= "[OLD LOOP] Errore nel recupero del numero di istanze " +
					"per il mnemonico " + loopMnemonicName;
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

		// Ricerco il vero limite superiore (tra quello dichiarato e quello del mnemonico)
		upperLimit = Math.min(declaredUpperLimit, mnemonicLoopRowsCount);

		// Controllo che l'indice non sia stato definito in un altro loop
		for (RpaScope scope : scopeStack) {

			if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

				RpaScopeLoop stackScopeLoop	= (RpaScopeLoop) scope;
				RpaLoopValue stackLoopValue	= (RpaLoopValue) stackScopeLoop.getLoopValue();

				if (stackLoopValue.getIndexName().equals(indexName)) {

					String code			= context.getText();
					String message		= "[OLD LOOP] Indice di loop duplicato";
					int errorContext	= RpaComposerException.COMPILE_MESSAGE;
					throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

				}

			}

		}

		// Controllo se è stato definito lo step con cui incrementare l'indice
		int step = 1;

		if (context.step != null) {

			step = Integer.valueOf(context.step.getText());

		}

		// Estraggo i nomi dei mnemonici e il loro ordine
		List<LoopSingleMnemonicOrderContext> loopSingleMnemonicOrderContextList = context.loopSingleMnemonicOrder();

		List<String>	mnemonicNameList	= new ArrayList<String>();
		List<ORDER>		mnemonicOrderList	= new ArrayList<ORDER>();

		for (LoopSingleMnemonicOrderContext loopSingleMnemonicOrderContext : loopSingleMnemonicOrderContextList) {

			String	mnemonicName		= loopSingleMnemonicOrderContext.mnemonic().getText();
			Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicName);
			mnemonicNameMatcher.find();
			mnemonicName = mnemonicNameMatcher.group();

			ORDER 	order;

			if (loopSingleMnemonicOrderContext.ADD() != null) {

				order = ORDER.ASC;

			} else if (loopSingleMnemonicOrderContext.SUB() != null) {

				order = ORDER.DESC;

			} else {

				order = ORDER.ASC;

			}

			mnemonicNameList.add(mnemonicName);
			mnemonicOrderList.add(order);

		}

		// Controllo che i mnemonici di ordinamento, e quello del primo loop, appartengano tutti alla stessa entità
		String loopFullEntityName	= mnemonicManager.translateMnemonicName(loopMnemonicName);
		String lastEntityFound		= loopFullEntityName.split("\\.", 2)[1];

		List<String> fieldNameList = new ArrayList<String>();

		for (String mnemonicName : mnemonicNameList) {

			String fieldName		= mnemonicManager.translateMnemonicName(mnemonicName);
			String fullEntityName	= fieldName.split("\\.", 2)[1];
			fieldName 				= fieldName.split("\\.")[0];

			fieldNameList.add(fieldName);

			if (!lastEntityFound.equals(fullEntityName)) {

				String code			= context.getText();
				String message		= "Tutti i mnemonici dell'ordinamento " +
						"devono appartenere alla stessa entità di #" + loopMnemonicName + "#";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Ordino l'entityManager che verrà utilizzato dal loop
		try {

			mnemonicManager.sortEntity(scopeStack, mnemonicNameList, mnemonicOrderList);

		} catch (Exception exception) {

			System.err.println(exception);
			String code			= context.getText();
			String message		= "Errore nell'ordinamento dell'entità";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

		// Aggiungo uno scope di tipo "loop"
		loopValue.setStep(step);
		loopValue.setLowerLimit(lowerLimit);
		loopValue.setUpperLimit(upperLimit);
		loopValue.setCurrentValue(lowerLimit);
		loopValue.setMnemonic(loopMnemonicName);
		loopValue.setMnemonicValues(mnemonicValues);

		scopeInlineLoop.setLoopValue(loopValue);
		scopeInlineLoop.setForNode(this.parentNode);
		scopeInlineLoop.setIsSkipNode(lowerLimit > upperLimit);
		scopeInlineLoop.setForId(loopId);
		scopeInlineLoop.setType(RpaScopeLoop.Type.OLD);
		scopeInlineLoop.backupMnemonicContextStack(scopeStack);

		scopeStack.push(scopeInlineLoop);

		return loopValue;

	}

	/*
	        #########################################################################################
	        #                                                                                       #
	        #                               DICHIARAZIONI FINE LOOP                                 #
	        #                                                                                       #
	        #########################################################################################
	 */

	@Override
	public RpaValue<?> visitOldLoopEnd(OldLoopEndContext context) {

		// Controllo se sono all'interno di un loop
		if (!scopeStack.empty() && scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

			RpaScopeLoop scopeLoop = (RpaScopeLoop) scopeStack.peek();
			RpaLoopValue loopValue = (RpaLoopValue) scopeLoop.getLoopValue();

			// Controllo di non essere in un loop interno
			if (scopeLoop.getInternalLoopToSkip() > 0) {

				scopeLoop.decreaseInternalLoopToSkip();

				// Esco
				RpaValue skipValue = new RpaValue(null);
				skipValue.setIsSkip(true);

				return skipValue;

			}

			// Verifico che l'apertura del loop abbia la vecchia sintassi
			if (scopeLoop.getType() == RpaScopeLoop.Type.NEW) {

				String nodeText = mainCompositore.getLastRunNodeRead().getText();
				String code			= context.getText();
				String message		= "[OLD LOOP END] Loop chiuso con una sintassi diversa (" + nodeText + ")";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

			// Estraggo l'indice del loop
			String loopEndIndex	= context.LOOP_END().getText();
			loopEndIndex		= loopEndIndex.substring(1);
			loopEndIndex		= loopEndIndex.trim();

			// Controllo che l'indice di fine-loop corrisponda a quello di inizio inizio-loop
			if (loopEndIndex.equals(loopValue.getIndexName())) {

				// Svuoto la lista dei nodi da cancellare da break-loop
				mainCompositore.getNodesBreakLoopToDelete().clear();

				// Se ho terminato il ciclo loop, elimino il suo scope dallo stack
				if (scopeLoop.isSkipNode()) {

					scopeStack.pop();

					RpaValue skipValue = new RpaValue(null);
					skipValue.setIsSkip(true);

					if (loopValue.isMnemonicEntityFound()) {

						// Resetto l'associazione con il mnemonico
						RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();
						mnemonicManager.unbindMnemonicEntityFromLoop(scopeStack, loopValue.getIndexName());

					}

					return skipValue;

				} else {

					// Ritorno il valore di chiusura loop
					RpaNextValue nextValue = new RpaNextValue(loopEndIndex);
					nextValue.setMnemonic(loopValue.getMnemonic());

					return nextValue;

				}

			}

			// Altrimenti lancio un errore
			else {

				String nodeText = mainCompositore.getLastRunNodeRead().getText();
				String code			= context.getText();
				String message		= "[OLD LOOP END] Nome errato di fine loop $$ (" + nodeText + ")";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Altrimenti lancio un errore
		else {

			String nodeText = mainCompositore.getLastRunNodeRead().getText();
			String code			= context.getText();
			String message		= "[OLD LOOP END] Posizione errata di fine loop $$ (" + nodeText + ")";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

	}

	@Override
	public RpaValue<?> visitNewLoopEnd(NewLoopEndContext context) {

		// Controllo se sono all'interno di un loop
		if (!scopeStack.empty() && scopeStack.peek().getScopeType() == RpaScope.LOOP_SCOPE_TYPE) {

			RpaScopeLoop scopeLoop = (RpaScopeLoop) scopeStack.peek();
			RpaLoopValue loopValue = (RpaLoopValue) scopeLoop.getLoopValue();

			// Controllo di non essere in un loop interno
			if (scopeLoop.getInternalLoopToSkip() > 0) {

				scopeLoop.decreaseInternalLoopToSkip();

				// Esco
				RpaValue skipValue = new RpaValue(null);
				skipValue.setIsSkip(true);

				return skipValue;

			}

			// Verifico che l'apertura del loop abbia la vecchia sintassi
			if (scopeLoop.getType() == RpaScopeLoop.Type.OLD) {

				String nodeText = mainCompositore.getLastRunNodeRead().getText();
				String code			= context.getText();
				String message		= "[NEW LOOP END] Loop chiuso con una sintassi diversa (" + nodeText + ")";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

			// Estraggo l'indice del loop
			String loopEndIndex	= context.NEW_LOOP_END().getText();
			loopEndIndex		= loopEndIndex.substring(6);
			loopEndIndex		= loopEndIndex.trim();

			// Controllo che l'indice di fine-loop corrisponda a quello di inizio inizio-loop
			if (loopEndIndex.equals(loopValue.getIndexName())) {

				// Svuoto la lista dei nodi da cancellare da break-loop
				mainCompositore.getNodesBreakLoopToDelete().clear();

				// Se ho terminato il ciclo loop, elimino il suo scope dallo stack
				if (scopeLoop.isSkipNode()) {

					scopeStack.pop();

					RpaValue skipValue = new RpaValue(null);
					skipValue.setIsSkip(true);

					if (loopValue.isMnemonicEntityFound()) {

						// Resetto l'associazione con il mnemonico
						RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();
						mnemonicManager.unbindMnemonicEntityFromLoop(scopeStack, loopValue.getIndexName());

					}

					return skipValue;

				} else {

					// Ritorno il valore di chiusura loop
					RpaNextValue nextValue = new RpaNextValue(loopEndIndex);
					nextValue.setMnemonic(loopValue.getMnemonic());

					return nextValue;

				}

			}

			// Altrimenti lancio un errore
			else {

				String nodeText = mainCompositore.getLastRunNodeRead().getText();
				String code			= context.getText();
				String message		= "[NEW LOOP END] Nome errato di fine loop (" + nodeText + ")";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

		}

		// Altrimenti lancio un errore
		else {

			String nodeText = mainCompositore.getLastRunNodeRead().getText();
			String code			= context.getText();
			String message		= "[NEW LOOP END] Posizione errata di fine loop (" + nodeText + ")";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

	}

	@Override
	public RpaValue<?> visitInlineLoopEnd(InlineLoopEndContext context) {

		// Controllo di avere in cima allo stack un inline-loop
		if (scopeStack.peek() instanceof RpaScopeInlineLoop) {

		    RpaScopeInlineLoop scopeInlineLoop = (RpaScopeInlineLoop) scopeStack.peek();

		    // Controllo che il nome dei due indici corrisponda
            String  rawLoopEndIndexName     = context.loopIdName.getText();
            Matcher loopEndIndexNameMatcher = Pattern.compile(INLINE_LOOP_ID_EXTRACTION_REGEX).matcher(rawLoopEndIndexName);
			loopEndIndexNameMatcher.find();
            String	loopEndIndexName		= loopEndIndexNameMatcher.group(4);
            String	loopScopeIndexName		= scopeInlineLoop.getLoopValue().getIndexName();

            if (!loopScopeIndexName.equals(loopEndIndexName)) {

				String code			= context.getText();
				String message		= "[INLINE-LOOP END] Nome errato di fine loop";
				int errorContext	= RpaComposerException.COMPILE_MESSAGE;
				throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

			}

			if (scopeInlineLoop.getState() == RpaScopeInlineLoop.LOOP_ON_NODE) {

				// Attivo l'inline-loop (per avviare l'interpretazione di testo e istruzioni)
				scopeInlineLoop.setActive(true);

				// Eseguo il corpo del loop diverse volte
				int upperLimit	= scopeInlineLoop.getLoopValue().getUpperLimit();
				int lowerLimit	= scopeInlineLoop.getLoopValue().getLowerLimit();
				int step		= scopeInlineLoop.getLoopValue().getStep();

				String codeText				= scopeInlineLoop.getWholeCodeString();
				String processedCodeText	= "";

				for (int currentIndex = lowerLimit; currentIndex <= upperLimit; currentIndex += step) {

					// Resetto il contesto dei mnemonici
					scopeInlineLoop.recoverMnemonicContextStack(scopeStack);

					// Aggiorno il contatore dell'indice del loop nello scope
					scopeInlineLoop.getLoopValue().setCurrentValue(currentIndex);

					// Rigenero il Parse-Tree
					// CompoLexer				lexer       = new CompoLexer(CharStreams.fromString(codeText));
                    RpaLexer				lexer       = new RpaLexer(new ANTLRInputStream(codeText));
					CommonTokenStream		tokenStream = new CommonTokenStream(lexer);
					RpaParser				parser      = new RpaParser(tokenStream);
					CodeContext	codeContext = parser.code();

					// Eseguo il codice del parse-tree e salvo il risultato
					RpaValue codeValue = visitCode(codeContext);

					if (codeValue != null && codeValue.getValue() != null) {

						processedCodeText += codeValue.getValue().toString();

					}

				}

				// Elimino dallo stack degli scope l'inline-loop
				scopeStack.pop();

				if (scopeInlineLoop.getLoopValue().isMnemonicEntityFound()) {

					// Resetto l'associazione con il mnemonico
					RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();
					mnemonicManager.unbindMnemonicEntityFromLoop(scopeStack, loopScopeIndexName);

				}

				// Ritorno il risultato del loop
				return new RpaValue(processedCodeText);

			}

			else {

				// Attivo l'inline-loop (per avviare l'interpretazione di testo e istruzioni)
				scopeInlineLoop.setActive(true);

				// Creo la nuova lista di nodi "Run" che sostituirà quella vecchia
				List<Run> newLoopRunNodeList = new ArrayList<Run>();

				// Eseguo il loop
				RpaLoopInlineValue loopValue = (RpaLoopInlineValue) scopeInlineLoop.getLoopValue();

				int upperLimit	= loopValue.getUpperLimit();
				int lowerLimit	= loopValue.getLowerLimit();
				int step		= loopValue.getStep();

				for (int currentIndex = lowerLimit; currentIndex <= upperLimit; currentIndex += step) {

					// Resetto il contesto dei mnemonici
					scopeInlineLoop.recoverMnemonicContextStack(scopeStack);

					// Aggiorno l'indice del loop nello scope
					loopValue.setCurrentValue(currentIndex);

					// Ciclo su ogni nodo che rappresenta il corpo del loop
					for (Run runNode : scopeInlineLoop.getRunNodeCodeList()) {

						// Elaboro il contenuto del nodo
						String loopCodeText = runNode.getText();

						// CompoLexer				lexer       = new CompoLexer(CharStreams.fromString(loopCodeText));
						RpaLexer			lexer		= new RpaLexer(new ANTLRInputStream(loopCodeText));
						CommonTokenStream	tokenStream = new CommonTokenStream(lexer);
						RpaParser			parser      = new RpaParser(tokenStream);
						CodeContext			codeContext = parser.code();

						// Eseguo il codice del parse-tree e salvo il risultato
						RpaValue codeValue = visitCode(codeContext);

						if (codeValue != null && codeValue.getValue() != null) {

							Run newRunNode = null;

							try {

								newRunNode = (Run) runNode.deepClone(true);
								newRunNode.setText(codeValue.getValue().toString());


							} catch (Exception exception) { }

							newLoopRunNodeList.add(newRunNode);

						}

					}

				}

				// Aggiungo e notifico la presenza dei nuovi nodi per il loop
				scopeInlineLoop.setNewRunNodeCodeList(newLoopRunNodeList);
				scopeInlineLoop.setHasNewNodesToAdd(true);

				// Esco
				RpaValue newValue = new RpaValue(null);
				newValue.setIsSkip(true);

				return newValue;

			}

		}

		// Altrimenti lancio un errore
		else {

			String nodeText = mainCompositore.getLastRunNodeRead().getText();
			String code			= context.getText();
			String message		= "[INLINE-LOOP] Errata posizione di chiusura del loop in linea (" + nodeText + ")";
			int errorContext	= RpaComposerException.COMPILE_MESSAGE;
			throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

		}

	}

	/*
	        #########################################################################################
	        #                                                                                       #
	        #                                   ELEMENTI DI LOOP                                    #
	        #                                                                                       #
	        #########################################################################################
	 */

    @Override
    public RpaValue<Integer> visitLoopLimitPositiveInteger(LoopLimitPositiveIntegerContext context) {

        Integer loopLimit = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        return new RpaValue<Integer>(loopLimit);

    }

    @Override
    public RpaValue<Integer> visitLoopLimitMathOperation(LoopLimitMathOperationContext context) {

	    RpaValue<Number> loopLimitNumberValue    = (RpaValue<Number>) visit(context.mathOperationStatement());
	    Number          loopLimitNumber         = loopLimitNumberValue.getValue();
	    Integer         loopLimitInteger        = (int) loopLimitNumber.doubleValue();

	    return new RpaValue<Integer>(loopLimitInteger);

    }

    @Override
    public RpaValue<Integer> visitLoopLimitMnemonic(LoopLimitMnemonicContext context) {

		RpaValue<RpaAbstractMnemonic>	abstractMnemonicValue	= (RpaValue<RpaAbstractMnemonic>) visit(context.mnemonic());
		RpaAbstractMnemonic				mnemonic				= abstractMnemonicValue.getValue();

        String  loopLimitString = mnemonic.getValueForMath().toString();
        Integer loopLimitInteger;

        if (RpaNumberUtils.isInteger(loopLimitString)) {

        	if (RpaNumberUtils.isIntegerWithDotZero(loopLimitString)) {

				loopLimitString = RpaNumberUtils.integerWithoutDotZero(loopLimitString);

			}

            loopLimitInteger = Integer.valueOf(loopLimitString);

        } else {

            String code			= context.getText();
            String message		= "Il limite del loop deve essere un mnemonico di tipo intero";
            int errorContext	= RpaComposerException.COMPILE_MESSAGE;
            throw new RpaMalformedLoopException(mainCompositore, errorContext, code, message);

        }

        return new RpaValue<Integer>(loopLimitInteger);

    }

	@Override
	public RpaValue<?> visitBreakLoop(BreakLoopContext context) {

		// Elimino i nodi inseriti nell'ultima iterazione del loop (e svuoto la lista)
		for (int i = 0; i < mainCompositore.getNodesBreakLoopToDelete().size(); i ++) {

			Node node = mainCompositore.getNodesBreakLoopToDelete().get(i);
			if (node.getParentNode() != null) {

				node.remove();

			}

		}
		mainCompositore.getNodesBreakLoopToDelete().clear();

    	// Cerco il loop più interno nello stack
		RpaScopeLoop scopeLoopFound = null;

		for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

			RpaScope scope = scopeStack.get(scopeIndex);

			if (scope instanceof RpaScopeLoop) {

				scopeLoopFound = (RpaScopeLoop) scope;
				break;

			}

		}

		if (scopeLoopFound == null) {

			String code				= context.getText();
			String message			= "Nessun loop trovato per il BREAK_LOOP";
			int applicationContext	= RpaComposerException.COMPILE_MESSAGE;

			throw new RpaNoLoopForBreakFoundException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message);

		}

		// Forzo la terminazione del loop
		scopeLoopFound.setIsSkipNode(true);

		// Elimino dallo scope-stack tutti gli elementi successivi al loop
		while (scopeStack.peek() != scopeLoopFound) {

			scopeStack.pop();

		}

		// Esco
		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

    /*
	@Override
	public Value visitLoop(CompoParser.LoopContext ctx) {

		LoopInformationManager loopInformationManager = LoopInformationManager.getLoopInformationManager();

		boolean isProsegui = checkIfScope();

		if (!isProsegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		// Double lLimit = Double.valueOf(visit(ctx.lLimit).getValue().toString());
		// Double uLimit = Double.valueOf(visit(ctx.uLimit).getValue().toString());

		// Double step = new Double(1);

		Integer lLimit = Integer.valueOf(visit(ctx.lLimit).getValue().toString());
		Integer uLimit = Integer.valueOf(visit(ctx.uLimit).getValue().toString());

		Integer step = new Integer(1);

		if (ctx.step != null) {

			// step = Double.valueOf(visit(ctx.step).getValue().toString());
			step = Integer.valueOf(visit(ctx.step).getValue().toString());

		}

		String flag	= ctx.flag.getText();

		// Double val	= lLimit;
		Integer val = lLimit;

		ForValue forValue = null;

		// if (scope.lastElement().getForNode() == null) {
		if (scope.lastElement().getScopeType() != Scope.LOOP_SCOPE_TYPE) {

			// Recupero il primo mnenmonico del loop
			LoopInformationManager.LoopInformation loopInformation =
					loopInformationManager.getLoopInformation(Integer.valueOf(ctx.index.getText()));

			// String mnemonicDeclaration	= loopInformation.getMnemonicDeclaration();
			String mnemonicName			= loopInformation.getMnemonicName();

			// Controllo se un mnemonico è stato definito per il loop
			ArrayList<String> mnemonicLoopValues	= null;
			boolean isSaveMnemonicValues			= false;

			if (mnemonicName != null) {

				System.out.println("Il nuovo loop " + ctx.flag.getText() + " ha indice: " + ctx.index.getText() +
						" con mnemonico: " + mnemonicName);

				// Recupero le informazioni sul mnemonico
				mnemonicLoopValues = MnemonicManager.getMnemonicManager().getMnemonicValues(mnemonicName);

				// Aggiorno l'indice superiore del loop
				uLimit = Math.min(uLimit, mnemonicLoopValues.size());

				// Notifica il salvataggio dei valori del mnemonico
				isSaveMnemonicValues = true;

			}

			// Se non ho mnemonici, salto il ciclo
			else {

				uLimit = 0;

			}

			forValue = new ForValue(null);
			forValue.setSkip(true);
			forValue.setCurrValue(val);
			forValue.setLower(lLimit);
			// forValue.setMnemonic(flag);
			forValue.setIndexName(flag);
			forValue.setUpper(uLimit);
			forValue.setStep(step);

			if (isSaveMnemonicValues) {

				forValue.setMnemonic(mnemonicName);
				forValue.setMnemonicValues(mnemonicLoopValues);

			}

			//Sono alla prima iterazione;
			// Scope scopeLocal = new Scope();
			ScopeFor scopeLocal = new ScopeFor();
			scopeLocal.setForNode(runNode);
			// scopeLocal.put(LOOP_FLAG_MNEMONIC, forValue);
			scopeLocal.setForValue(forValue);
			scope.push(scopeLocal);

			// Se ho raggiunto il limite, salto il corpo del loop
			if (val.compareTo(uLimit) > 0) {
				scopeLocal.setHidden(true);
				return forValue;
			}

		} else {
			// forValue = (ForValue) scope.lastElement().get(LOOP_FLAG_MNEMONIC);
			ScopeFor scopeFor	= (ScopeFor) scope.lastElement();
			forValue			= scopeFor.getForValue();
		}

		// scope.push(new Scope());
		scope.push(new ScopeUndefined());

		return forValue;
	}
*/

/*
	@Override
	public Value<?> visitNext(CompoParser.NextContext ctx) {

		String flag = ctx.flag.getText();
		boolean prosegui = checkIfScope();

		if (!prosegui) {
			return Value.SKIP;
		}

		// if (scope.lastElement().isHidden() && scope.lastElement().getForNode() != null) {
		if (scope.lastElement().isHidden() && scope.lastElement().getScopeType() == Scope.LOOP_SCOPE_TYPE) {

			ScopeFor scopeFor = (ScopeFor) scope.lastElement();

			// if (scope.lastElement().get(LOOP_FLAG_MNEMONIC) != null && flag.equals(((ForValue) scope.lastElement().get(LOOP_FLAG_MNEMONIC)).getMnemonic())) {
			if (scopeFor.getForValue() != null && flag.equals(scopeFor.getForValue().getIndexName())) {

				scope.pop();

				return Value.SKIP;

			}

		}

		if (scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		scope.pop();

		// Controllo di essere effettivamente in un ciclo, altrimenti c'è un errore nella sintassi

		// if (scope.lastElement().getForNode() != null) {
		if (scope.lastElement().getScopeType() == Scope.LOOP_SCOPE_TYPE) {

			ScopeFor scopeFor = (ScopeFor) scope.lastElement();

			// if (scope.lastElement().get(LOOP_FLAG_MNEMONIC) != null && flag.equals(((ForValue) scope.lastElement().get(LOOP_FLAG_MNEMONIC)).getMnemonic())) {
			if (scopeFor.getForValue() != null && flag.equals(scopeFor.getForValue().getIndexName())) {

				//Verifico se sono nel next corrispondente così chiudo il for
				// ForValue v = ((ForValue) scope.lastElement().get(LOOP_FLAG_MNEMONIC));
				ForValue v = scopeFor.getForValue();
				v.setCurrValue(v.getCurrValue() + v.getStep());

				if (v.getCurrValue().compareTo(v.getUpper()) > 0) {

					scope.pop();

					return Value.SKIP;

				} else {

					NextValue vNext = new NextValue(null);
					vNext.setSkip(true);
					vNext.setMnemonic(flag);

					return vNext;

				}

			}

		}

		LOG.error(new CompoException("Errore nella sintassi, la direttiva " + ctx.getText() + " non è associata a nessun ciclo.", //
				new Exception()));

		return Value.SKIP;

	}
*/

/*
	@Override
	public Value visitBreakloop(CompoParser.BreakloopContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		if (scope.lastElement() != null) {
			// ForValue loop = ((ForValue) scope.lastElement().get(LOOP_FLAG_MNEMONIC));
			ForValue loop = ((ScopeFor) scope.lastElement()).getForValue();
			loop.setCurrValue(loop.getUpper() + 1);
		}

		return new Value(ctx.getText());
	}
*/

/*	@Override
	public Value<?> visitLineLoop(CompoParser.LineLoopContext ctx) {

		boolean isProsegui = checkIfScope();

		if (!isProsegui || scope.lastElement().isHidden()) {

			return Value.SKIP;

		}

		if (ctx.sflag.getText().equals(ctx.eflag.getText())) {

			*//*
			Double lLimit = Double.valueOf(visit(ctx.lLimit).getValue().toString());
			Double uLimit = Double.valueOf(visit(ctx.uLimit).getValue().toString());

			Double step = new Double(1);
			*//*

			Integer lLimit = Integer.valueOf(visit(ctx.lLimit).getValue().toString());
			Integer uLimit = Integer.valueOf(visit(ctx.uLimit).getValue().toString());

			Integer step = new Integer(1);

			if (ctx.step != null) {

				// step = Double.valueOf(visit(ctx.step).getValue().toString());
				step = Integer.valueOf(visit(ctx.step).getValue().toString());

			}

			String flag = ctx.sflag.getText();

			// Recupero il primo mnenmonico del loop
			LoopInformationManager loopInformationManager = LoopInformationManager.getLoopInformationManager();

			LoopInformationManager.LoopInformation loopInformation =
					loopInformationManager.getLoopInformation(Integer.valueOf(ctx.index.getText()));

			// String mnemonicDeclaration	= loopInformation.getMnemonicDeclaration();
			String mnemonicName			= loopInformation.getMnemonicName();

			// Controllo se un mnemonico è stato definito per il loop
			ArrayList<String> mnemonicLoopValues	= null;
			boolean isSaveMnemonicValues			= false;

			if (mnemonicName != null) {

				System.out.println("Il nuovo loop " + flag + " ha indice: " + ctx.index.getText() +
						" con mnemonico: " + mnemonicName);

				// Recupero le informazioni sul mnemonico
				mnemonicLoopValues = MnemonicManager.getMnemonicManager().getMnemonicValues(mnemonicName);

				// Aggiorno l'indice superiore del loop
				uLimit = Math.min(uLimit, mnemonicLoopValues.size());

				// Notifica il salvataggio dei valori del mnemonico
				isSaveMnemonicValues = true;

			}

			ForValue forValue = new ForValue(null);
			forValue.setCurrValue(lLimit);
			forValue.setLower(lLimit);
			forValue.setIndexName(flag);
			forValue.setUpper(uLimit);
			forValue.setStep(step);

			if (isSaveMnemonicValues) {

				forValue.setMnemonic(mnemonicName);
				forValue.setMnemonicValues(mnemonicLoopValues);

			}

			ScopeFor scopeFor = new ScopeFor();
			scopeFor.setForNode(runNode);
			scopeFor.setForValue(forValue);

			scope.push(scopeFor);
			scope.push(new ScopeUndefined());

			StringBuilder builder = new StringBuilder();
			while (forValue.getCurrValue() <= forValue.getUpper()) {

				builder.append(visit(ctx.mod).getValue().toString());
				forValue.setCurrValue(forValue.getCurrValue() + forValue.getStep());

			}

			scope.pop();
			scope.pop();

			return new Value(builder.toString());
		} else {
			LOG.error(new CompoException("Errore nella sintassi, gli indici " + ctx.sflag.getText() + " e " + ctx.eflag.getText() //
					+ " non sono uguali. Loop non valido.", new Exception()));
		}
		return Value.SKIP;
	}*/
}
