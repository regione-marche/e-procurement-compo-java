package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaVariablesManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicSTR;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ConcatMnemonicStringContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ConcatTwoMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.WriteStringStatementConstantContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.WriteStringStatementMnemonicContext;

import java.sql.Connection;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RpaStringVisitor extends RpaMathVisitor {

	public RpaStringVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {
		super(conn, scope, mainCompositore, parentNode, childNode);
	}

	@Override
	public RpaValue<String> visitWriteStringStatementConstant(WriteStringStatementConstantContext context) {

		// Estraggo la stringa
		String			content = context.STRING().getText().replaceAll("\"", "");
		RpaValue<String> newValue = new RpaValue(content);
		newValue.setIsSkip(true);

		// Estraggo l'indice dello "STR"
		String indexString	= context.MNEMONIC_STR().getText().replaceAll("#", "");
		indexString			= indexString.replace("STR", "");

		int index;

		if (indexString.isEmpty()) {

			index = 0;

		} else if (indexString.equals("V")) {

			RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
					variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, RpaMnemonicVisitor.STR_CUSTOM_INDEX);

			RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

			index = mnemonicTOT.getValueNumber().intValue();

		} else {

			index = Integer.valueOf(indexString);

		}

		// Aggiorno il campo "STR"
		if (variablesManager.isExists(RpaVariablesManager.Type.STR, index)) {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, content);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicConstantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.setVariableValue(RpaVariablesManager.Type.STR, index, mnemonicConstantValue);

		} else {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, content);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicCostantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.addVariable(RpaVariablesManager.Type.STR, index, mnemonicCostantValue);

		}

		return newValue;

	}

	@Override
	public RpaValue<String> visitWriteStringStatementMnemonic(WriteStringStatementMnemonicContext context) {

		// Estraggo il valore del mnemonico
		RpaValue<RpaAbstractMnemonic> mnemonicContentValue	= (RpaValue<RpaAbstractMnemonic>) visit(context.mnemonic());
		String mnemonicContent								= null;

		if (mnemonicContentValue == null || mnemonicContentValue.getValue() == null || mnemonicContentValue.getValue().getValueForSTR() == null) {

			mnemonicContent = "";

		} else {

			mnemonicContent = mnemonicContentValue.getValue().getValueForSTR();

		}

		RpaValue<String> newValue = new RpaValue<String>(mnemonicContent);
		newValue.setIsSkip(true);

		// Estraggo l'indice dello "STR"
		String indexString	= context.MNEMONIC_STR().getText().replaceAll("#", "");
		indexString			= indexString.replace("STR", "");

		int index;

		if (indexString.isEmpty()) {

			index = 0;

		} else if (indexString.equals("V")) {

			RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
					variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, RpaMnemonicVisitor.STR_CUSTOM_INDEX);

			RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

			index = mnemonicTOT.getValueNumber().intValue();

		} else {

			index = Integer.valueOf(indexString);

		}

		// Aggiorno il campo "STR"
		if (variablesManager.isExists(RpaVariablesManager.Type.STR, index)) {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, mnemonicContent);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicConstantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.setVariableValue(RpaVariablesManager.Type.STR, index, mnemonicConstantValue);

		} else {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, mnemonicContent);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicCostantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.addVariable(RpaVariablesManager.Type.STR, index, mnemonicCostantValue);

		}

		return newValue;

	}

	@Override
	public RpaValue<?> visitConcatTwoMnemonic(ConcatTwoMnemonicContext context) {

		// Recupero il valore dei due mnemonici
		RpaValue<RpaAbstractMnemonic> leftMnemonicValue	= (RpaValue<RpaAbstractMnemonic>) visit(context.leftMnemonic);
		RpaValue<RpaAbstractMnemonic> rightMnemonicValue	= (RpaValue<RpaAbstractMnemonic>) visit(context.rightMnemonic);

		RpaAbstractMnemonic leftMnemonic	= leftMnemonicValue.getValue();
		RpaAbstractMnemonic rightMnemonic	= rightMnemonicValue.getValue();

		if (leftMnemonic == null || rightMnemonic == null) {

			RpaValue newValue = new RpaValue<Object>(null);
			newValue.setIsSkip(true);
			return newValue;

		}

		// Definisco il nuovo valore
		String leftContent	= leftMnemonic.getValueForSTR();
		String rightContent	= rightMnemonic.getValueForSTR();

		leftContent		= leftContent == null ? "" : leftContent;
		rightContent	= rightContent == null ? "" : rightContent;

		String newMnemonicContent = leftContent + rightContent;
		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		// Estraggo l'indice dello "STR"
		String indexString	= context.MNEMONIC_STR().getText().replaceAll("#", "");
		indexString			= indexString.replace("STR", "");

		int index;

		if (indexString.isEmpty()) {

			index = 0;

		} else if (indexString.equals("V")) {

			RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
					variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, RpaMnemonicVisitor.STR_CUSTOM_INDEX);

			RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

			index = mnemonicTOT.getValueNumber().intValue();

		} else {

			index = Integer.valueOf(indexString);

		}

		// Aggiorno il campo "STR"
		if (newMnemonicContent == null) {

			newMnemonicContent = "";

		}

		if (variablesManager.isExists(RpaVariablesManager.Type.STR, index)) {

			RpaMnemonicSTR mnemonicSTR						= new RpaMnemonicSTR(mainCompositore, newMnemonicContent);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicConstantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.setVariableValue(RpaVariablesManager.Type.STR, index, mnemonicConstantValue);

		} else {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, newMnemonicContent);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicCostantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.addVariable(RpaVariablesManager.Type.STR, index, mnemonicCostantValue);

		}

		return newValue;

	}

	@Override
	public RpaValue<?> visitConcatMnemonicString(ConcatMnemonicStringContext context) {

		// Recupero il valore del mnemonico e della stringa
		RpaValue<RpaAbstractMnemonic> mnemonicValue	= (RpaValue<RpaAbstractMnemonic>) visit(context.mnemonic());
		RpaAbstractMnemonic mnemonic		= mnemonicValue.getValue();
		String 					stringConstant	= context.STRING().getText().replaceAll("\"", "");

		// Definisco il nuovo valore
		String newMnemonicContent;
		String mnemonicContent = mnemonic.getValueForSTR();

		if (mnemonicContent == null) {

			mnemonicContent = "";

		}

		if (context.leftMnemonic != null) {

			newMnemonicContent = mnemonicContent + stringConstant;

		} else {

			newMnemonicContent = stringConstant + mnemonicContent;

		}

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		// Estraggo l'indice dello "STR"
		String indexString	= context.MNEMONIC_STR().getText().replaceAll("#", "");
		indexString			= indexString.replace("STR", "");

		int index;

		if (indexString.isEmpty()) {

			index = 0;

		} else if (indexString.equals("V")) {

			RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
					variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, RpaMnemonicVisitor.STR_CUSTOM_INDEX);

			RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

			index = mnemonicTOT.getValueNumber().intValue();

		} else {

			index = Integer.valueOf(indexString);

		}

		// Aggiorno il campo "STR"
		if (variablesManager.isExists(RpaVariablesManager.Type.STR, index)) {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, newMnemonicContent);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicConstantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.setVariableValue(RpaVariablesManager.Type.STR, index, mnemonicConstantValue);

		} else {

			RpaMnemonicSTR mnemonicSTR				= new RpaMnemonicSTR(mainCompositore, newMnemonicContent);
			RpaValue<RpaAbstractMnemonicConstant> mnemonicCostantValue	= new RpaValue<RpaAbstractMnemonicConstant>(mnemonicSTR);

			variablesManager.addVariable(RpaVariablesManager.Type.STR, index, mnemonicCostantValue);

		}

		return newValue;

	}

	public static int extractTOTIndex(RpaMainCompositore mainCompositore, String mnemonicNameRaw) {

		// Controllo se ho un indice da estrarre dal totalizzatore
		RpaVariablesManager variablesManager = mainCompositore.getVariablesManager();
		Matcher matcher = Pattern.compile(EXTRACT_TOT_REGEX).matcher(mnemonicNameRaw);
		matcher.find();

		String index = "";

		if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {

			index = matcher.group(1);

		}

		// Se non ho un indice, ritorno l'indice 0
		if (index.isEmpty()) {

			return 0;

		}

		// Se ho un indice pari a 'V', prendo il valore definito all'indice '999'
		else if (index.equals("V")) {

			RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
					variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, RpaMnemonicVisitor.STR_CUSTOM_INDEX);

			RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

			return mnemonicTOT.getValueNumber().intValue();

		}

		else {

			return Integer.valueOf(index);

		}

	}

	public static int extractSTRIndex(RpaMainCompositore mainCompositore, String mnemonicNameRaw) {

		// Controllo se ho un indice da estrarre dall'STR
		RpaVariablesManager variablesManager = mainCompositore.getVariablesManager();
		Matcher matcher = Pattern.compile(EXTRACT_STR_REGEX).matcher(mnemonicNameRaw);
		matcher.find();

		String index = "";

		if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {

			index = matcher.group(1);

		}

		// Se non ho un indice, ritorno l'indice 0
		if (index.isEmpty()) {

			return 0;

		}

		// Se ho un indice pari a 'V', prendo il valore definito all'indice '998'
		else if (index.equals("V")) {

			RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
					variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, STR_CUSTOM_INDEX);

			RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

			return mnemonicTOT.getValueNumber().intValue();

		}

		else {

			return Integer.valueOf(index);

		}

	}

	/*	@Override
	public Value<?> visitLength(CompoParser.LengthContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value result = new Value(String.valueOf(visit(ctx.argument).getValue()).length());
		Matcher matcher = Pattern.compile("^[A-Z]{3}([0-9]+)?.*$").matcher(ctx.variabile().var.getText());

		int name = 0;
		if (matcher.find() && matcher.group(1) != null) {
			name = Integer.valueOf(matcher.group(1));
		}

		if (ctx.variabile().var.getText().startsWith("TOT")) {
			if (variablesManager.exists(VariablesManager.TOT, name)) {
				variablesManager.setVariableValue(VariablesManager.TOT, name, result);
			} else {
				variablesManager.addVariable(VariablesManager.TOT, name, result);
			}
		} else {
			LOG.error(new CompoException("Errore sintassi, il tipo di variabile \"" + ctx.variabile().var.getText() + "\" non è applicabile", //
					new Exception()));
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitInpstr(CompoParser.InpstrContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value argument = visit(ctx.argument);

		UserDialog userDialog = new UserDialog();
		Value result = new Value(userDialog.showInputDialog(argument.getValue().toString(), UserDialog.ANY_CHARACTER, UserDialog.ANY_CHARACTER_HELP));

		Matcher matcher = Pattern.compile("^[A-Z]{3}([0-9]+)?.*$").matcher(ctx.variabile().var.getText());

		int name = 0;
		if (matcher.find() && matcher.group(1) != null) {
			name = Integer.valueOf(matcher.group(1));
		}

		if (ctx.variabile().var.getText().startsWith("STR")) {
			if (variablesManager.exists(VariablesManager.STR, name)) {
				variablesManager.setVariableValue(VariablesManager.STR, name, result);
			} else {
				variablesManager.addVariable(VariablesManager.STR, name, result);
			}
		} else {
			LOG.error(new CompoException("Errore sintassi, il tipo di variabile \"" + ctx.variabile().var.getText() + "\" non è applicabile", //
					new Exception()));
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitString(CompoParser.StringContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		if (ctx.value() != null) {
			return visit(ctx.value());
		}
		return new Value(ctx.getText().replace("\"", ""));
	}*/

/*	@Override
	public Value<?> visitStringAssignment(CompoParser.StringAssignmentContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value argument = visit(ctx.argument);
		// Controllo che la variabile sia del tipo STRnn dove nn è l'indice
		if (ctx.variable != null) {
			if (ctx.variable.var.getText().matches("^STR([\\d]+)?$")) {
				Matcher matcher = Pattern.compile("^STR([\\d]+)?$").matcher(ctx.variable.var.getText());

				Integer index = 0;
				if (matcher.find() && matcher.group(1) != null) {
					index = Integer.valueOf(matcher.group(1));
				}

				boolean result = variablesManager.addVariable(VariablesManager.STR, index, argument);
				if (!result) { // La variabile esiste già, la aggiorno
					variablesManager.setVariableValue(VariablesManager.STR, index, argument);
				}
			} else {
				LOG.error(new CompoException("Errore nella sintassi, la variabile " + ctx.variable.var + " non è applicabile in questo contesto", //
						new Exception()));
			}
		} else if (ctx.array != null) {
			if (ctx.array.var.getText().startsWith("STR")) {
				// --> L'utente vuole impostare il valore alla variabile passando dal vettore #STRV#
				variablesManager.setArrayPointerValue(VariablesManager.STRV, argument);
			} else {
				LOG.error(new CompoException("Errore nella sintassi, il vettore " + ctx.array.getText() + " non è utilizzabile in questo contesto", //
						new Exception()));
			}
		}

		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitStringConcat(CompoParser.StringConcatContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value str1 = visit(ctx.str1);
		Value str2 = visit(ctx.str2);
		Value ris = new Value(str1.getValue().toString() + str2.getValue().toString());

		if (ctx.variable != null) {
			Matcher matcher = Pattern.compile("^[A-Z]{3}([0-9]+)?.*$").matcher(ctx.variable.var.getText());

			Integer index = 0;
			if (matcher.find() && matcher.group(1) != null) {
				index = Integer.valueOf(matcher.group(1));
			}

			if (ctx.variable.var.getText().startsWith("STR")) {
				boolean result = variablesManager.addVariable(VariablesManager.STR, index, ris);
				if (!result) // La variabile esiste già, la aggiorno
				{
					variablesManager.setVariableValue(VariablesManager.STR, index, ris);
				}
			} else {
				LOG.error(new CompoException("Errore sintassi, il tipo di variabile \"" + ctx.variable.var.getText() + "\" non è applicabile", //
						new Exception()));
			}
		} else if (ctx.array != null) {
			if (ctx.array.var.getText().startsWith("STR")) {
				// --> L'utente vuole impostare il valore alla variabile passando dal vettore #STRV#
				variablesManager.setArrayPointerValue(VariablesManager.STRV, ris);
			} else {
				LOG.error(new CompoException("Errore nella sintassi, il vettore " + ctx.array.getText() + " non è utilizzabile in questo contesto", //
						new Exception()));
			}
		}
		return Value.SKIP;
	}*/
}
