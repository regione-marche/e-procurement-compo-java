package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeJoin;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidFormatException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaNoMnemonicEntityFoundException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaVariablesManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicEntityData;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFields;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaPathTreeNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicSTR;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RpaAdvancedVisitor extends RpaLoopVisitor {

	public static final int JOIN_VALUE_TYPE_STRING		= -1;

	public static final int BOOLEAN_MNEMONIC_STRING		= 0;
	public static final int BOOLEAN_MNEMONIC_NUMBER		= 1;

	public RpaAdvancedVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {
		super(conn, scope, mainCompositore, parentNode, childNode);
	}

	@Override
	public RpaValue<?> visitJoin(RpaParser.JoinContext context) {

		// MnemonicManager mnemonicManager = MnemonicManager.getMnemonicManager();
		RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

		// Recupero la lista di mnemonici (sinistra)
		List<String>	mnemonicNameList			= new ArrayList<String>();
		String			extractMnemonicNameRegex	= EXTRACT_NAME_MNEMONIC_REGEX;
		String			lastMnemonicName			= "";

		for (RpaParser.MnemonicContext mnemonicContext : context.joinLeftPart().mnemonic()) {

			Matcher mnemonicNameMatcher = Pattern.compile(extractMnemonicNameRegex).matcher(mnemonicContext.getText());
			mnemonicNameMatcher.find();
			String mnemonicName = mnemonicNameMatcher.group();

			mnemonicNameList.add(mnemonicName);
			lastMnemonicName = mnemonicName;

		}

		// Recupero la lista dei valori (destra)
		List<String>			valueList				= new ArrayList<String>();
		List<Integer>			valueMnemonicTypeList	= new ArrayList<Integer>();
		List<Boolean>			valueIsMnemonicList		= new ArrayList<Boolean>();
		Map<Integer, Boolean>	isSetToNullMap			= new HashMap<Integer, Boolean>();

		int index = 0;

		for (RpaParser.JoinValueContext joinValueContext : context.joinRightPart().joinValue()) {

			if (joinValueContext.mnemonic() != null) {

				// Controllo il tipo del mnemonico
				String	mnemonicNameRaw	= joinValueContext.mnemonic().getText();
				int		mnemonicType	= mnemonicManager.getMnemonicType(joinValueContext.mnemonic().getText());

				valueIsMnemonicList.add(true);
				valueMnemonicTypeList.add(mnemonicType);

				RpaValue value = getMnemonicSimpleValue(mainCompositore, scopeStack, joinValueContext.mnemonic().getText());

				// PER JOIN CON STR "NULL" (non definiti) O VUOTI (stringa vuota)
				// DEI MODELLI DI PAOLO URBANETTO ALLA DATA 19/10/2020
				// Se ho un STR NULL o vuota, forzo la condizione "IS NULL"
				boolean isMnemonicSTR	= mnemonicNameRaw != null && mnemonicNameRaw.contains("STR");
				boolean isValueNull		= value == null || value.getValue() == null || ((RpaAbstractMnemonic)value.getValue()).getValue() == null;
				boolean isValueEmpty	= !isValueNull && ((RpaAbstractMnemonic)value.getValue()).getValue().isEmpty();

				if (isMnemonicSTR && (isValueNull || isValueEmpty)) {

					valueList.add("");
					isSetToNullMap.put(index, true);
					++ index;

				}

				// Se non ho trovato niente nel mnemonico di destra, cancello la comparazione
				else if (isValueNull) {

					int deleteIndex = valueList.size();
					mnemonicNameList.remove(deleteIndex);

				} else {

					valueList.add(((RpaAbstractMnemonic) value.getValue()).getValue());
					++ index;

				}

			} else {

				valueIsMnemonicList.add(false);
				valueMnemonicTypeList.add(null);

				valueList.add(joinValueContext.STRING().getText().replaceAll("\"", ""));
				++ index;

			}

		}

		// Controllo che il numero di mnemonici e valori sia uguale
		if (mnemonicNameList.size() != valueList.size()) {

			int mnemonicCount	= mnemonicNameList.size();
			int valueCount		= valueList.size();

			throw new ParseCancellationException("[JOIN] Il numero di mnemonici (" + mnemonicCount + ") " +
					"è diverso da quello dei valori (" + valueCount + ")");

		}

		// Verifico che ci sia un'unica entità per tutti i mnemonici di sinistra
		String lastMnemonicEntityName	= "";
		String lastMnemonicEntityLabel	= "";

		for (String mnemonicName : mnemonicNameList) {

			String mnemonicEntityLabel	= mnemonicManager.translateMnemonicName(mnemonicName);

			if (mnemonicEntityLabel == null || mnemonicEntityLabel.isEmpty()) {

				String code     	= mnemonicName;
				String message  	= "Nessuna entità trovata nel c0entit per il mnemonico " + mnemonicName;
				int errorContext   	= RpaComposerException.COMPILE_MESSAGE;

				throw new RpaNoMnemonicEntityFoundException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), errorContext, code, message);

			}

			String mnemonicEntityName = mnemonicEntityLabel.split("\\.")[1];

			if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

				throw new ParseCancellationException("[JOIN] Impossibile trovare l'entità " +
						"per il mnemonico " + mnemonicName);

			}

			if (lastMnemonicEntityName.isEmpty()) {

				lastMnemonicEntityName	= mnemonicEntityName;
				lastMnemonicEntityLabel	= mnemonicEntityLabel;

			} else if (!lastMnemonicEntityName.equals(mnemonicEntityName)) {

				throw new ParseCancellationException("[JOIN] Tutti i mnemonici di sinistra " +
						"devono appartenere alla stessa entità");

			}

		}

		// Nel caso in cui non abbia più coppie di elementi (perchè tutti i valori di destra sono a null)
		// estraggo l'entità dell'ultimo mnemonico che ho letto
		if (mnemonicNameList.isEmpty()) {

			lastMnemonicEntityLabel	= mnemonicManager.translateMnemonicName(lastMnemonicName);
			lastMnemonicEntityName	= lastMnemonicEntityLabel.split("\\.")[1];

		}

		String lastMnemonicTable		= lastMnemonicEntityLabel.split("\\.")[1];
		String lastMnemonicDomain		= lastMnemonicEntityLabel.split("\\.")[2];
		String lastMnemonicTableDomain	= lastMnemonicTable + "." + lastMnemonicDomain;

		// Connection dbConnection = ComposerConfiguration.getInstance().getConn();
		Connection dbConnection = mainCompositore.getComposerConfiguration().getConn();

		RpaMnemonicGraph mnemonicGraph		= new RpaMnemonicGraph(lastMnemonicTableDomain, "", dbConnection);
		RpaPathTreeNode pathTreeNode		= new RpaPathTreeNode(lastMnemonicTable, lastMnemonicDomain);
		RpaMnemonicEntityData mnemonicEntityData	= new RpaMnemonicEntityData(mainCompositore, lastMnemonicTable, lastMnemonicDomain, mnemonicGraph, pathTreeNode);

		// Eseguo la join tramite "MnemonicManager"
		List<RpaMnemonicFields> mnemonicFieldsList;

		try {

			mnemonicFieldsList = mnemonicManager.performJoin(
					mnemonicNameList,
					valueList,
					valueMnemonicTypeList,
					valueIsMnemonicList,
					isSetToNullMap,
					lastMnemonicEntityName,
					mnemonicEntityData
			);

		} catch (Exception exception) {

			System.err.println(exception);
			throw new ParseCancellationException("[JOIN] Errore nell'esecuzione della JOIN");

		}

		// Con i "MnemonicFields" recuperati, inizializzo lo "ScopeJoin"

		// Aggiungo la Join allo scope stack
		RpaScopeJoin newScopeJoin = new RpaScopeJoin(mainCompositore, lastMnemonicTableDomain, mnemonicEntityData, mnemonicFieldsList);
		scopeStack.push(newScopeJoin);

		// Controllo se devo lo "ScopeJoin" è vuoto
		boolean isNewScopeJoinEmpty = valueList.isEmpty() || (valueList.size() == 1 && valueList.get(0) == null);
		newScopeJoin.setIsEmpty(isNewScopeJoinEmpty);

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

	@Override
	public RpaValue<?> visitJoinEnd(RpaParser.JoinEndContext context) {

		// Controllo se lo scope al primo livello è di tipo "JOIN"
		RpaScope lastScope = scopeStack.peek();

		if (lastScope.getScopeType() != RpaScope.JOIN_SCOPE_TYPE) {

			throw new ParseCancellationException("Tentativo di chiusura di una join " +
					"quando era stato aperto un " + lastScope);

		}

		// Elimino dallo scope la join
		scopeStack.pop();

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

	@Override
	public RpaValue<?> visitReadRegisterIntoTOTStatement(RpaParser.ReadRegisterIntoTOTStatementContext context) {

		// Recupero il valore del registro
		int		registerIndex = Integer.valueOf(context.registerIndex.getText());
		String	registerValue = mainCompositore.getRegisterManager().getRegister(registerIndex);

		Number registerValueNumber;

		// Controllo il tipo del valore
		if (registerValue != null && (RpaNumberUtils.isInteger(registerValue) || RpaNumberUtils.isDouble(registerValue))) {

			// Recupero il valore numerico
			if (RpaNumberUtils.isInteger(registerValue)) {

				registerValueNumber = Integer.valueOf(registerValue);

			} else {

				registerValueNumber = Double.valueOf(registerValue);

			}

		} else if (registerValue == null) {

			// Ho "null"
			registerValueNumber = null;

		} else {

			// Non ho un numero, lancio un errore
			String code   	= context.getText();
			String message	= "Il valore del registro " + registerIndex + " che ha valore " + registerValue + " non è un numero";
			int contextCode	= RpaComposerException.COMPILE_MESSAGE;

			throw new RpaInvalidFormatException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextCode, code, message);

		}

		// Creo il nuovo TOT all'indice indicato
		int				totIndex		= RpaStringVisitor.extractTOTIndex(mainCompositore, context.MNEMONIC_TOT().getText());
		RpaMnemonicTOT	newMnemonicTOT	= new RpaMnemonicTOT(mainCompositore, registerValueNumber);
		RpaValue<RpaAbstractMnemonicConstant> newValue = new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicTOT);

		// Salvo il TOT
		if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

			variablesManager.setVariableValue(RpaVariablesManager.Type.TOT, totIndex, newValue);

		} else {

			variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, newValue);

		}

		// Esco non stampando niente
		RpaValue exitValue = new RpaValue(null);
		exitValue.setIsSkip(true);

		return exitValue;

	}

	@Override
	public RpaValue<?> visitReadRegisterIntoSTRStatement(RpaParser.ReadRegisterIntoSTRStatementContext context) {

		// Recupero il valore del registro
		int		registerIndex = Integer.valueOf(context.registerIndex.getText());
		String	registerValue = mainCompositore.getRegisterManager().getRegister(registerIndex);

		// Creo il nuovo TOT all'indice indicato
		int				strIndex		= RpaStringVisitor.extractSTRIndex(mainCompositore, context.MNEMONIC_STR().getText());
		RpaMnemonicSTR	newMnemonicSTR	= new RpaMnemonicSTR(mainCompositore, registerValue);
		RpaValue<RpaAbstractMnemonicConstant> newValue = new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicSTR);

		// Salvo l'STR
		if (variablesManager.isExists(RpaVariablesManager.Type.STR, strIndex)) {

			variablesManager.setVariableValue(RpaVariablesManager.Type.STR, strIndex, newValue);

		} else {

			variablesManager.addVariable(RpaVariablesManager.Type.STR, strIndex, newValue);

		}

		// Esco non stampando niente
		RpaValue exitValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return exitValue;

	}

	/*	@Override
	public Value<?> visitVariabile(CompoParser.VariabileContext ctx) {
		boolean prosegui = checkIfScope();

		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Matcher matcher = Pattern.compile("^[A-Z]{3}([0-9]+)?.*$").matcher(ctx.var.getText());

		Value ris = null;

		int name = 0;
		if (matcher.find() && matcher.group(1) != null) {
			name = Integer.valueOf(matcher.group(1));
		}

		if (ctx.var.getText().startsWith("STR")) {
			ris = variablesManager.getVariableValue(VariablesManager.STR, name);
		} else if (ctx.var.getText().startsWith("TOT")) {
			ris = variablesManager.getVariableValue(VariablesManager.TOT, name);
		} else {
			LOG.error(new CompoException("Errore sintassi, il tipo di variabile \"" + ctx.var.getText() + "\" non è applicabile", //
					new Exception()));
		}

		// Gestisco l'eventuale formato della variabile
		if (ris != null) {
			if (ctx.INDEX() != null) {
				// Da implementare
			} else if (ctx.format() != null) {
				ris = new Value(FormatUtils.getFormatUtils().format(ctx.format().getText(), ris.getValue().toString()));
			} else if (ctx.domain() != null) {
				ris = new Value(DomainUtils.getDomainUtils().format(ctx.domain().getText(), ris.getValue().toString()));
			}
		} else {
			LOG.error(new CompoException("Errore nella sintassi, stai cercando di accedere alla variabile " + ctx.var.getText() + //
					" senza averla definita", new Exception()));
		}
		return ris;
	}*/

/*	@Override
	public Value<?> visitVector(CompoParser.VectorContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value ris = null;
		if (ctx.var.getText().startsWith("STR")) {
			ris = variablesManager.getArrayPointerValue(VariablesManager.STRV);
		} else if (ctx.var.getText().startsWith("TOT")) {
			ris = variablesManager.getArrayPointerValue(VariablesManager.TOTV);
		} else {
			LOG.error(new CompoException("Errore nella sintassi, il vettore " + ctx.var.getText() + " non è di un tipo conosciuto", //
					new Exception()));
		}
		return ris;
	}*/

/*	@Override
	public Value<?> visitMnemonico(CompoParser.MnemonicoContext mnemonicoContext) {

		// TODO: Lo scorrimento dei valori di un mnemonico in un loop in linea, non funziona (Si blocca allo stesso indice)
		// TODO: (Questo perchè per un "inline-loop" non viene definito uno scope da cui recuperare il mnemonico)

		// Se sono in una IF con condizione "false", salta il codice del "than"
		boolean isProsegui = checkIfScope();
		if (!isProsegui || scope.lastElement().isHidden()) {

			return Value.SKIP;

		}

		// Recupero il nome dell'istruzione senza "#"
		String instructionName = mnemonicoContext.identifier().getText();

		// Caso in cui ho: #INIZIORTF# o #FINETESTO# o #FINERTF#
		if (instructionName.equals(INIZIORTF_MNEMONICO)) {

			return Value.SKIP;

		} else if (instructionName.equals(FINETESTO_MNEMONICO)) {

			return Value.SKIP;

		} else if (instructionName.equals(FINERTF_MNEMONICO)) {

			return Value.SKIP;

		}

		// Verifico se lo mnemonico è un flag di un loop (#NOME_INDICE_LOOP#)
		boolean isThisInstructionInLoop = false;

		if (scope.size() > 1) {

			// boolean isThisInstructionInLoop = scope.get(scope.size() - 2) != null;
			// boolean isInstructionLoopIndex = instructionName.equals(((ForValue) scope.get(scope.size() - 2).get(LOOP_FLAG_MNEMONIC)).getMnemonic());
			// boolean isInstructionLoopIndex = instructionName.equals(((ScopeFor) scope.get(scope.size() - 2)).getForValue().getMnemonic());

			*//*
			if (isThisInstructionInLoop && isInstructionLoopIndex) {

				// Double loopIndex = ((ForValue) scope.get(scope.size() - 2).get(LOOP_FLAG_MNEMONIC)).getCurrValue();
				Double loopIndex = ((ScopeFor) scope.get(scope.size() - 2)).getForValue().getCurrValue();

				return new Value(loopIndex);

			}
			*//*

			isThisInstructionInLoop = scope.get(scope.size() - 2) != null && scope.get(scope.size() - 2).getScopeType() == Scope.LOOP_SCOPE_TYPE;

			if (isThisInstructionInLoop) {

				boolean isInstructionLoopIndex = instructionName.equals(((ScopeFor) scope.get(scope.size() - 2)).getForValue().getIndexName());

				if (isInstructionLoopIndex) {

					// Double loopIndex = ((ScopeFor) scope.get(scope.size() - 2)).getForValue().getCurrValue();
					Integer loopIndex = ((ScopeFor) scope.get(scope.size() - 2)).getForValue().getCurrValue();

					return new Value(loopIndex);

				}

			}

		}

		String nomeMnemonico	= instructionName;
		String valoreMnemonico	= null;

		// Verifico se l'utente ha richiesto dei dati non legati ad un vero mnemonico (es: "#DATAOGGI#")
		if (customMnemoManager.itemExist(nomeMnemonico)) {

			valoreMnemonico = String.valueOf(customMnemoManager.getCustomMnemo(nomeMnemonico).getValue());

		}

		// Recupero i risultati del mnemonico
		else {

			// Controllo se il mnemonico è quello associato al loop (primo mnemonico del loop)
			boolean isMnemonicBindToLoop = false;

			if (isThisInstructionInLoop) {

				ForValue	forValue		= ((ScopeFor) scope.get(scope.size() - 2)).getForValue();
				String		forMnemonicName	= forValue.getMnemonic();

				if (nomeMnemonico.equals(forMnemonicName)) {

					isMnemonicBindToLoop = true;

					valoreMnemonico = forValue.getCurrentMnemonicValue();

				}

			}

			// Altrimenti recupero il primo valore del mnemonico
			if (!isMnemonicBindToLoop) {

				MnemonicManager mnemonicManager = MnemonicManager.getMnemonicManager();
				ArrayList<String> mnemonicValues = mnemonicManager.getMnemonicValues(nomeMnemonico);

				// Se non ho valori, riporto una stringa vuota
				if (mnemonicValues == null || mnemonicValues.isEmpty()) {

					valoreMnemonico = "";

				} else {

					valoreMnemonico = mnemonicValues.get(0);

				}

			}

			if (valoreMnemonico == null) {

				valoreMnemonico = "";

			}

		}

		// Se non ho un valore per il mnemonico, lo chiedo all'utente
		if (valoreMnemonico.isEmpty() && styleManager.getDom().isEnable()) {

			UserDialog userDialog = new UserDialog();
			valoreMnemonico = userDialog.showInputDialog("<html>In fase di compilazione è stato trovato<br/> uno mnemonico non valorizzato," + " inserisci<br/> un valore manualmente", UserDialog.ANY_CHARACTER, UserDialog.ANY_CHARACTER_HELP);

		}

		// Se non ho un valore per il mnemonico, inserisco nel testo dei trattini (se richiesto dall'utente)
		if (valoreMnemonico.isEmpty() && styleManager.getTrat().isEnable()) {

			return new Value(styleManager.getTrat().getAlternative());

		}

		// Mnemonico definito come #MNEMONICO(NUMERIC_INDEX or MNEMONICO2)#
		if (mnemonicoContext.INDEX() != null) {

			// Estraggo l'indice
			String index = mnemonicoContext.INDEX().toString();
			String s[] = index.split("[\\(\\)]");
			String indexStr = new String("");

			for (int ind = 0; ind < s.length; ind++) {

				indexStr += s[ind];

			}

			// boolean numerico = false;
			boolean isIndexNumeric = false;
			int indexValue;

			try {

				indexValue = Integer.parseInt(indexStr);

			} catch (NumberFormatException e) {

				LOG.error(e);

			}

			// Recupero il valore del mnemonico all'indice "indexValue"
			if (isIndexNumeric) {

				// TODO: Quando verrà riportato valore del mnemonico come collezione di dati, restituire quello con indice estratto

			}

			// Eseguo una join implicita con il secondo mnemonico estratto
			else {

				// TODO: Richiamare da "MnemonicManager" la funzione per le join implicite

			}

			return null;

		}

		// Mnemonico definito come #MNEMONICO[FORMATO]#
		else if (mnemonicoContext.format() != null) {

			String format = mnemonicoContext.format().getText();
			return new Value(FormatUtils.getFormatUtils().format(format, valoreMnemonico));

		}

		// Mnemonico definito come #MNEMONICO{DOMINIO}#
		else if (mnemonicoContext.domain() != null) {

			String domain = mnemonicoContext.domain().getText();
			return new Value(DomainUtils.getDomainUtils().format(domain, valoreMnemonico));

		}

		// Mnemonico definito normalmente
		else {

			Value result;
			if (customMnemoManager.itemExist(nomeMnemonico)) {

				result = customMnemoManager.getCustomMnemo(nomeMnemonico);

			} else {

				MnemonicDescriptionList.MnemonicDescription mnemonicDescription = MnemonicDescriptionList.getMnemonicDescriptionList().getPropByName(nomeMnemonico);

				// TODO: Eliminare il controllo sull'abilitazione del mnemonico
				if (mnemonicDescription != null) {

					// TODO: Gestire i controlli sul formato

					*//*
					// Applico il formato di default
					String default_format = mnemonicDescription.getDefault_format();
					if (default_format != null && !default_format.isEmpty()) {
						valoreMnemonico = FormatUtils.getFormatUtils().format(default_format, valoreMnemonico);
					}

					// Applico il dominio di default
					String default_domain = mnemonicDescription.getDefault_domain();
					if (default_domain != null && !default_domain.isEmpty()) {
						// valoreMnemonico = DomainUtils.getDomainUtils().format(default_format, valoreMnemonico);
						valoreMnemonico = DomainUtils.getDomainUtils().format(default_domain, valoreMnemonico);
					}
					*//*

	 *//*
					if (campo.isEnable()) {

						// Se esiste applico il formato di default
						String default_format = campo.getDefault_format();
						if (!default_format.isEmpty()) {
							valoreMnemonico = FormatUtils.getFormatUtils().format(default_format, valoreMnemonico);
						}

						// Se esiste applico il dominio di default
						String default_domain = campo.getDefault_domain();
						if (!default_domain.isEmpty()) {
							valoreMnemonico = DomainUtils.getDomainUtils().format(default_format, valoreMnemonico);
						}

					} else {

						String errorMessage = "Errore nella sintassi, il mnemonico \"" + nomeMnemonico + "\" è disabilitato e non si può utilizzare";

						LOG.error(new CompoException(errorMessage, new Exception()));

					}
					*//*

				}

				result = new Value(valoreMnemonico);

			}

			return result;

		}
	}*/

/*	@Override
	public Value<?> visitCustommne(CompoParser.CustommneContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		String strMnemonico = ctx.identifier().getText();
		if (!customMnemoManager.itemExist(strMnemonico)) {
			UserDialog userDialog = new UserDialog();

			Value value = null;
			if (ctx.userinputformat() != null) {
				String format = ctx.userinputformat().getText();
				if (format.equals("[A.O]")) {
					value = new Value(userDialog.showInputDialog("Inserisci un testo", UserDialog.ALPHANUMERIC, UserDialog.ALPHANUMERIC_HELP));
				} else if (format.equals("[I.O]")) {
					value = new Value(userDialog.showInputDialog("Inserisci un importo", UserDialog.AMOUNT, UserDialog.AMOUNT_HELP));
				} else if (format.equals("[N.O]")) {
					value = new Value(userDialog.showInputDialog("Inserisci un numero intero", UserDialog.INTEGER, UserDialog.INTEGER_HELP));
				} else if (format.equals("[F8.2]")) {
					value = new Value(userDialog.showInputDialog("Inserisci un numero intero o decimale", UserDialog.DECIMAL, UserDialog.DECIMAL_HELP));
				} else if (format.equals("[Z.O]")) {
					value = new Value(userDialog.showInputDialog("Inserisci un numero intero", UserDialog.INTEGER, UserDialog.INTEGER_HELP));
				} else if (format.equals("[D.O]")) {
					value = new Value(userDialog.showInputDialog("Inserisci una data", UserDialog.DATA, UserDialog.DATA_HELP));
				} else {
					LOG.error(new CompoException("Errore nella sintassi, il formato " + format + " non esiste", new Exception()));
				}
			} else {
				value = new Value(userDialog.showInputDialog("Inserisci un valore", UserDialog.ANY_CHARACTER, UserDialog.ANY_CHARACTER_HELP));
			}

			customMnemoManager.addCustomMnemo(strMnemonico, value);
		} else {
			LOG.error(new CompoException("Errore nella sintassi, " + strMnemonico + " è già stato definito e non puoi modificarlo", //
					new Exception()));
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitIfcond(CompoParser.IfcondContext ctx) {
		boolean prosegui = checkIfScope();

		if (!prosegui) {

			// scope.push((new Scope()).setScopeType(Scope.IF_SCOPE_TYPE));
			scope.push(new ScopeIfThen());
			scope.lastElement().setHidden(true);
			return Value.SKIP;

		}

		Value vCondition = visit(ctx.condexpr);

		if (Boolean.TRUE.equals(vCondition.getValue())) {

			// scope.push((new Scope()).setScopeType(Scope.IF_SCOPE_TYPE));
			scope.push(new ScopeIfThen());
			scope.lastElement().setEnter(true);

		} else {

			// scope.push((new Scope()).setScopeType(Scope.ELSE_SCOPE_TYPE));
			scope.push(new ScopeIfElse());

		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitElsecond(CompoParser.ElsecondContext ctx) {
		boolean prosegui = false;
		if (!scope.lastElement().isHidden()) {
			// if (scope.lastElement().getScopeType() == Scope.IF_SCOPE_TYPE && !scope.lastElement().isEnter()) {
			if (scope.lastElement().getScopeType() == Scope.IF_THAN_SCOPE_TYPE && !scope.lastElement().isEnter()) {
				prosegui = false;
			// } else if (scope.lastElement().getScopeType() == Scope.IF_SCOPE_TYPE && scope.lastElement().isEnter()) {
			} else if (scope.lastElement().getScopeType() == Scope.IF_THAN_SCOPE_TYPE && scope.lastElement().isEnter()) {
				prosegui = false;
			// } else if (scope.lastElement().getScopeType() == Scope.ELSE_SCOPE_TYPE && !scope.lastElement().isEnter()) {
			} else if (scope.lastElement().getScopeType() == Scope.IF_ELSE_SCOPE_TYPE && !scope.lastElement().isEnter()) {
				prosegui = true;
			// } else if (scope.lastElement().getScopeType() == Scope.ELSE_SCOPE_TYPE && scope.lastElement().isEnter()) {
			} else if (scope.lastElement().getScopeType() == Scope.IF_ELSE_SCOPE_TYPE && scope.lastElement().isEnter()) {
				LOG.warn(new CompoException("Errore nella sintassi, sei già nel ramo ELSE", new Exception()));
				prosegui = false;
			} else {
				prosegui = true;
				LOG.error(new CompoException("Errore nella sintassi, \"" + ctx.getText() + "\" non è assiociato a nessun ramo IF", //
						new Exception()));
			}
			if (!prosegui) {
				scope.lastElement().setEnter(false);
				return Value.SKIP;
			}
		} else {
			return Value.SKIP;
		}

		scope.lastElement().setEnter(true);
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitEndifcond(CompoParser.EndifcondContext ctx) {

		// if (scope.lastElement().getScopeType() == Scope.IF_SCOPE_TYPE || scope.lastElement().getScopeType() == Scope.ELSE_SCOPE_TYPE) {
		if (scope.lastElement().getScopeType() == Scope.IF_THAN_SCOPE_TYPE || scope.lastElement().getScopeType() == Scope.IF_ELSE_SCOPE_TYPE) {

			scope.pop();

		} else {

			LOG.error(new CompoException("Errore nella sintassi, \"" + ctx.getText() + "\" non è assiociato a nessun ramo IF/ELSE", //
					new Exception()));

		}

		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitUserinput(CompoParser.UserinputContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		String message = visit(ctx.demand).getValue().toString();

		UserDialog userDialog = new UserDialog();
		if (ctx.condition != null) {
			if (ctx.condition.min != null) {
				userDialog.setLowerLimit(Integer.valueOf(ctx.condition.min.getText()));
			}
			if (ctx.condition.max != null) {
				userDialog.setUpperLimit(Integer.valueOf(ctx.condition.max.getText()));
			}
		}

		Value result = null;
		if (ctx.userInput != null) {
			String inputType = ctx.userInput.getText();
			if (inputType.equals("[A]")) {
				result = new Value(userDialog.showInputDialog(message, UserDialog.ALPHANUMERIC, UserDialog.ALPHANUMERIC_HELP));
			} else if (inputType.equals("[I]")) {
				result = new Value(userDialog.showInputDialog(message, UserDialog.AMOUNT, UserDialog.AMOUNT_HELP));
			} else if (inputType.equals("[N]")) {
				result = new Value(userDialog.showInputDialog(message, UserDialog.INTEGER, UserDialog.INTEGER_HELP));
			} else if (inputType.equals("[Z]")) {
				result = new Value(userDialog.showInputDialog(message, UserDialog.INTEGER, UserDialog.INTEGER_HELP));
			} else if (inputType.equals("[F]")) {
				result = new Value(userDialog.showInputDialog(message, UserDialog.DECIMAL, UserDialog.DECIMAL_HELP));
			} else if (inputType.equals("[D]")) {
				result = new Value(userDialog.showInputDialog(message, UserDialog.DATA, UserDialog.DATA_HELP));
			} else if (inputType.equals("[M]")) {
				if (ctx.items.size() > 0) {
					ArrayList<String> itemToDisplay = null;
					// Controllo se esiste una sottolista di elementi attivi
					if (ctx.condition != null && ctx.condition.possibility != null) {
						itemToDisplay = new ArrayList<String>();
						for (int i = 0; i <= ctx.condition.possibility.size() - 1; i++) {
							itemToDisplay.add(ctx.condition.possibility.get(i).UINT().getText());
						}
					}
					ArrayList<String> items = new ArrayList<String>();
					for (int i = 0; i <= ctx.items.size() - 1; i++) { // Aggiungo gli elementi che andranno nella combobox
						Matcher matcher = Pattern.compile("^\\|(\\d+)\\)(.*)$").matcher(visit(ctx.items.get(i)).getValue().toString());

						if (matcher.find()) {
							if (matcher.group(1) != null && matcher.group(2) != null) {
								if (itemToDisplay == null || (itemToDisplay != null && //
										ArrayUtils.contains(itemToDisplay.toArray(new String[itemToDisplay.size()]), matcher.group(1)))) {
									items.add(matcher.group(2));
								}
							} // Salto l'elemento
						}
					}
					if (items.size() > 0) // C'è una lista di elementi da attivare ma non comprende nessuno degli elementi definiti
					{
						result = new Value(userDialog.showComboDialog(message, items.toArray(new String[items.size()])));
					} else {
						LOG.error(new CompoException("Errore nella sintassi, devi specificare almeno un'elemento attivo per visualizzare il menu", //
								new Exception()));
					}
				} else {
					LOG.error(new CompoException("Errore nella sintassi, per creare un menu è necessario definire almeno un elemento", //
							new Exception()));
				}
			}
		} else {
			result = new Value(userDialog.showInputDialog(message, UserDialog.ANY_CHARACTER, UserDialog.ANY_CHARACTER_HELP));
		}
		return result;
	}*/

/*	@Override
	public Value<?> visitListItem(CompoParser.ListItemContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		return new Value("|" + ctx.number().getText() + ")" + visit(ctx.item).getValue());
	}*/
}
