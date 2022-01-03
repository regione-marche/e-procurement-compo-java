package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaIncludeFileValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaIncludeException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDataConversionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaVariablesManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.HideEmptyMnemonicStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfOperationAssignContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfOperationAssignDecreaseContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfOperationAssignIncreaseContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfOperationSimpleContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IncludeFileStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NewsynStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NumberPrecisionStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OperationFormatContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OperationStatementAssignContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OperationStatementAssignDecreaseContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OperationStatementAssignIncreaseContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.OperationStatementSimpleContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ShowEmptyMnemonicStatementContext;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Stack;

public abstract class RpaBasicVisitor extends RpaVisitor {

	public static final int OPERATION_DECIMAL_DIGIT_COUNT = 2;

	protected boolean isMathOperationValid = true;

	protected Number numberToFormat;

	public RpaBasicVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

		super(conn, scope, mainCompositore, parentNode, childNode);

	}

	@Override
	public RpaValue<?> visitOperationStatementSimple(OperationStatementSimpleContext context) {

		isMathOperationValid = true;

		// Recupero il risultato dell'operazione
		RpaValue value = visit(context.mathOperationStatement());

		// Controllo se l'operazione è stata annullata
		if (!isMathOperationValid) {

			return new RpaValue(mainCompositore.getMnemonicEmpty());
		}

		// Controllo se il valore è null
		if (value.getValue() == null) {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Riformatto il risultato
		if (context.operationFormat() != null && context.operationFormat().children != null) {

			numberToFormat              	= Double.valueOf(value.getValue().toString());
			RpaValue<RpaFormat> valueFormat	= visitOperationFormat(context.operationFormat());
			String result               	= valueFormat.getValue().getPrintedValue();
			value.setValue(result);

			return value;

		} else {

			String result = value.getValue().toString();
			result = formattedValue(result);
			value.setValue(result);

			return value;

		}

	}

	@Override
	public RpaValue<?> visitOperationStatementAssign(OperationStatementAssignContext context) {

		// Recupero l'indice del TOT da settare
        int totIndex;

        if (context.index == null) {

            totIndex = 0;

        } else if (context.V() != null) {

            totIndex = RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

        } else {

            totIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        }

		// Recupero il valore
		isMathOperationValid			= true;
		RpaValue<Number> resultValue	= (RpaValue<Number>) visit(context.mathOperationStatement());

		// Controllo se l'operazione è stata annullata
		if (!isMathOperationValid) {

            // Resetto il totalizzatore
            if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

                variablesManager.removeVariable(RpaVariablesManager.Type.TOT, totIndex);

            }

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Controllo se il valore è null
		if (resultValue.getValue() == null) {

            // Resetto il totalizzatore
            if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

                variablesManager.removeVariable(RpaVariablesManager.Type.TOT, totIndex);

            }

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Recupero il risultato dell'operazione
		RpaAbstractMnemonicConstant				newMnemonicTOT	= new RpaMnemonicTOT(mainCompositore, resultValue.getValue());
		RpaValue<RpaAbstractMnemonicConstant>	newValue		= new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicTOT);

		// Recupero la formattazione definita dall'utente (se esiste)
		RpaFormat format = null;

		if (context.operationFormat() != null && context.operationFormat().children != null) {

			numberToFormat					= resultValue.getValue();
			RpaValue<RpaFormat> formatValue	= visitOperationFormat(context.operationFormat());
			format							= formatValue.getValue();

			newMnemonicTOT = new RpaMnemonicTOT(mainCompositore, format.getValueNumber());
			newValue.setValue(newMnemonicTOT);

		}

		// Assegno al totalizzatore il risultato
		if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

			variablesManager.setVariableValue(RpaVariablesManager.Type.TOT, totIndex, newValue);

		} else {

			variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, newValue);

		}

		// Riformatto il risultato
		if (format == null) {

			String result = resultValue.getValue().toString();
			result = formattedValue(result);

			// Ritorno / Stampo il risultato
			return new RpaValue(result);

		} else {

			return new RpaValue(format.getPrintedValue());

		}

	}

	@Override
	public RpaValue<?> visitOperationStatementAssignIncrease(OperationStatementAssignIncreaseContext context) {

		isMathOperationValid		= true;
		RpaValue<Number> resultValue	= (RpaValue<Number>) visit(context.mathOperationStatement());

		// Controllo se l'operazione è stata annullata
		if (!isMathOperationValid) {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Controllo se il valore è null
		if (resultValue.getValue() == null) {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Recupero il risultato dell'operazione
		Number mathOperationResult = resultValue.getValue();

		// Recupero la formattazione definita dall'utente (se esiste)
		RpaFormat format = null;

		if (context.operationFormat() != null && context.operationFormat().children != null) {

			numberToFormat				= resultValue.getValue();
			RpaValue<RpaFormat> formatValue	= visitOperationFormat(context.operationFormat());
			format						= formatValue.getValue();

		}

		// Aggiungo il risultato nel valore del totalizzatore (se non c'è, lo inizializzo con il risultato)
		int totIndex;

		if (context.index == null) {

			totIndex = 0;

		} else if (context.V() != null) {

			totIndex = RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

		} else {

			totIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

		}

		RpaValue<RpaAbstractMnemonicConstant> variableValue;

		if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

			variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, totIndex);

		} else {

			RpaAbstractMnemonicConstant newMnemonic	= new RpaMnemonicTOT(mainCompositore, mathOperationResult);
			variableValue							= new RpaValue<RpaAbstractMnemonicConstant>(newMnemonic);
			variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, variableValue);

		}

		Number newVariableValue;
		RpaMnemonicTOT mnemonicTOT	= (RpaMnemonicTOT) variableValue.getValue();
		Number oldVariableValue = mnemonicTOT.getValueNumber();

		if (format != null && RpaNumberUtils.isInteger(format.getValueNumber().toString())) {

			newVariableValue = oldVariableValue.intValue() + format.getValueNumber().intValue();

		} else if (format != null && RpaNumberUtils.isDouble(format.getValueNumber().toString())) {

			newVariableValue = oldVariableValue.doubleValue() + format.getValueNumber().doubleValue();

		} else if (oldVariableValue instanceof Integer && mathOperationResult instanceof Integer) {

			newVariableValue = oldVariableValue.intValue() + mathOperationResult.intValue();

		} else {

			newVariableValue = oldVariableValue.doubleValue() + mathOperationResult.doubleValue();

		}

		RpaAbstractMnemonicConstant newMnemonic = new RpaMnemonicTOT(mainCompositore, newVariableValue);
		variableValue.setValue(newMnemonic);

		// Riformatto il risultato
		if (format == null) {

			String result = resultValue.getValue().toString();
			result = formattedValue(result);

			// Ritorno il risultato
			return new RpaValue(result);

		} else {

			return new RpaValue(format.getPrintedValue());

		}

	}

	@Override
	public RpaValue<?> visitOperationStatementAssignDecrease(OperationStatementAssignDecreaseContext context) {

		isMathOperationValid = true;
		RpaValue<Number> resultValue = (RpaValue<Number>) visit(context.mathOperationStatement());

		// Controllo se l'operazione è stata annullata
		if (!isMathOperationValid) {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Controllo se il valore è null
		if (resultValue.getValue() == null) {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

		// Recupero il risultato dell'operazione
		Number mathOperationResult = resultValue.getValue();

		// Recupero la formattazione definita dall'utente (se esiste)
		RpaFormat format = null;

		if (context.operationFormat() != null && context.operationFormat().children != null) {

			numberToFormat = resultValue.getValue();
			RpaValue<RpaFormat> formatValue = visitOperationFormat(context.operationFormat());
			format = formatValue.getValue();

		}

		// Aggiungo il risultato nel valore del totalizzatore (se non c'è, lo inizializzo con il risultato)
		int totIndex;

		if (context.index == null) {

			totIndex = 0;

		} else if (context.V() != null) {

			totIndex = RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

		} else {

			totIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

		}

		RpaValue<RpaAbstractMnemonicConstant> variableValue;

		if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

			variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, totIndex);

		} else {

			RpaAbstractMnemonicConstant newMnemonic	= new RpaMnemonicTOT(mainCompositore, mathOperationResult);
			variableValue							= new RpaValue<RpaAbstractMnemonicConstant>(newMnemonic);
			variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, variableValue);

		}

		Number newVariableValue;
		RpaMnemonicTOT mnemonicTOT	= (RpaMnemonicTOT) variableValue.getValue();
		Number oldVariableValue = mnemonicTOT.getValueNumber();

		if (format != null && RpaNumberUtils.isInteger(format.getValueNumber().toString())) {

			newVariableValue = oldVariableValue.intValue() - format.getValueNumber().intValue();

		} else if (format != null && RpaNumberUtils.isDouble(format.getValueNumber().toString())) {

			newVariableValue = oldVariableValue.doubleValue() - format.getValueNumber().doubleValue();

		} else if (oldVariableValue instanceof Integer && mathOperationResult instanceof Integer) {

			newVariableValue = oldVariableValue.intValue() - mathOperationResult.intValue();

		} else {

			newVariableValue = oldVariableValue.doubleValue() - mathOperationResult.doubleValue();

		}

		RpaAbstractMnemonicConstant newMnemonic = new RpaMnemonicTOT(mainCompositore, newVariableValue);
		variableValue.setValue(newMnemonic);

		// Riformatto il risultato
		if (format == null) {

			String result = resultValue.getValue().toString();
			result = formattedValue(result);

			// Ritorno il risultato
			return new RpaValue(result);

		} else {

			return new RpaValue(format.getPrintedValue());

		}

	}

	@Override
	public RpaValue<?> visitIfOperationSimple(IfOperationSimpleContext context) {

		// Recupero l'esito della condizione booleana
		RpaValue<Boolean> booleanConditionValue	= (RpaValue<Boolean>) visit(context.booleanStatement());
		Boolean			isBooleanConditionTrue	= booleanConditionValue.getValue();

		// Se la condizione è vera, eseguo l'operazione matematica e ritorno il risultato
		if (isBooleanConditionTrue) {

			isMathOperationValid = true;

			RpaValue<Number> mathOperationResultValue = (RpaValue<Number>) visit(context.mathOperationStatement());

			// Controllo se l'operazione è stata annullata
			if (!isMathOperationValid) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Controllo se il valore è null
			if (mathOperationResultValue.getValue() == null) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Riformatto il risultato
			if (context.operationFormat() != null && context.operationFormat().children != null) {

				numberToFormat              = Double.valueOf(mathOperationResultValue.getValue().toString());
				RpaValue<RpaFormat> valueFormat   = visitOperationFormat(context.operationFormat());
				String result               = valueFormat.getValue().getPrintedValue();

				return new RpaValue(result);

			} else {

				String result = mathOperationResultValue.getValue().toString();
				result = formattedValue(result);

				return new RpaValue(result);

			}

		}

		// Se la condizione è false, ritorno null
		else {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

	}

	@Override
	public RpaValue<?> visitIfOperationAssign(IfOperationAssignContext context) {

		// Recupero l'esito della condizione booleana
		RpaValue<Boolean> booleanConditionValue	= (RpaValue<Boolean>) visit(context.booleanStatement());
		Boolean			isBooleanConditionTrue	= booleanConditionValue.getValue();

		// Se la condizione è vera, eseguo l'operazione matematica, eseguo l'assegnamento e ritorno il risultato
		if (isBooleanConditionTrue) {

			isMathOperationValid		= true;
			RpaValue<Number> resultValue	= (RpaValue<Number>) visit(context.mathOperationStatement());

			// Controllo se il valore è null
			if (resultValue.getValue() == null) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Recupero il risultato dell'operazione
			RpaAbstractMnemonicConstant newMnemonicTOT	= new RpaMnemonicTOT(mainCompositore, resultValue.getValue());
			RpaValue<RpaAbstractMnemonicConstant> newValue		= new RpaValue<RpaAbstractMnemonicConstant>(newMnemonicTOT);

			// Controllo se l'operazione è stata annullata
			if (!isMathOperationValid) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Recupero la formattazione definita dall'utente (se esiste)
			RpaFormat format = null;

			if (context.operationFormat() != null && context.operationFormat().children != null) {

				numberToFormat				= resultValue.getValue();
				RpaValue<RpaFormat> formatValue	= visitOperationFormat(context.operationFormat());
				format						= formatValue.getValue();

				newMnemonicTOT = new RpaMnemonicTOT(mainCompositore, format.getValueNumber());
				newValue.setValue(newMnemonicTOT);
				// newMnemonicTOT.setLastFormattedValue(format);

			}

			// Assegno al totalizzatore il risultato
			int totIndex;

			if (context.V() != null) {

				totIndex = RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

			} else {

				totIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

			}

			if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

				variablesManager.setVariableValue(RpaVariablesManager.Type.TOT, totIndex, newValue);

			} else {

				variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, newValue);

			}

			// Riformatto il risultato
			if (format == null) {

				String result = resultValue.getValue().toString();
				result = formattedValue(result);

				// Ritorno / Stampo il risultato
				return new RpaValue(result);

			} else {

				return new RpaValue(format.getPrintedValue());

			}

		}

		// Se la condizione è false, ritorno null
		else {

			// return new Value<Number>(null);
			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

	}

	@Override
	public RpaValue<?> visitIfOperationAssignIncrease(IfOperationAssignIncreaseContext context) {

		// Recupero l'esito della condizione booleana
		RpaValue<Boolean> booleanConditionValue	= (RpaValue<Boolean>) visit(context.booleanStatement());
		Boolean			isBooleanConditionTrue	= booleanConditionValue.getValue();

		// Se la condizione è vera, eseguo l'operazione matematica, eseguo l'incremento e ritorno il risultato
		if (isBooleanConditionTrue) {

			isMathOperationValid		= true;
			RpaValue<Number> resultValue	= (RpaValue<Number>) visit(context.mathOperationStatement());

			// Controllo se il valore è null
			if (resultValue.getValue() == null) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Recupero il risultato dell'operazione
			Number mathOperationResult	= resultValue.getValue();

			// Controllo se l'operazione è stata annullata
			if (!isMathOperationValid) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Recupero la formattazione definita dall'utente (se esiste)
			RpaFormat format = null;

			if (context.operationFormat() != null && context.operationFormat().children != null) {

				numberToFormat				= resultValue.getValue();
				RpaValue<RpaFormat> formatValue	= visitOperationFormat(context.operationFormat());
				format						= formatValue.getValue();

			}

			// Aggiungo il risultato nel valore del totalizzatore (se non c'è, lo inizializzo con il risultato)
			Integer totIndex;

			if (context.V() != null) {

				totIndex = RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

			} else {

				totIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

			}

			RpaValue<RpaAbstractMnemonicConstant> variableValue;

			if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

				variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, totIndex);

			} else {

				RpaAbstractMnemonicConstant newMnemonic	= new RpaMnemonicTOT(mainCompositore, mathOperationResult);
				variableValue							= new RpaValue<RpaAbstractMnemonicConstant>(newMnemonic);
				variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, variableValue);

			}

			Number newVariableValue;
			RpaMnemonicTOT mnemonicTOT	= (RpaMnemonicTOT) variableValue.getValue();
			Number oldVariableValue = mnemonicTOT.getValueNumber();

			if (format != null && RpaNumberUtils.isInteger(format.getValueNumber().toString())) {

				newVariableValue = oldVariableValue.intValue() + format.getValueNumber().intValue();

			} else if (format != null && RpaNumberUtils.isDouble(format.getValueNumber().toString())) {

				newVariableValue = oldVariableValue.doubleValue() + format.getValueNumber().doubleValue();

			} else if (oldVariableValue instanceof Integer && mathOperationResult instanceof Integer) {

				newVariableValue = oldVariableValue.intValue() + mathOperationResult.intValue();

			} else {

				newVariableValue = oldVariableValue.doubleValue() + mathOperationResult.doubleValue();

			}

			RpaAbstractMnemonicConstant newMnemonic = new RpaMnemonicTOT(mainCompositore, newVariableValue);
			variableValue.setValue(newMnemonic);

			// Riformatto il risultato
			if (format == null) {

				String result = resultValue.getValue().toString();
				result = formattedValue(result);

				// Ritorno il risultato
				return new RpaValue(result);

			} else {

				return new RpaValue(format.getPrintedValue());

			}

		}

		// Se la condizione è falsa, ritorno l'istanza vuota
		else {

			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

	}

	@Override
	public RpaValue<?> visitIfOperationAssignDecrease(IfOperationAssignDecreaseContext context) {

		// Recupero l'esito della condizione booleana
		RpaValue<Boolean> booleanConditionValue	= (RpaValue<Boolean>) visit(context.booleanStatement());
		Boolean			isBooleanConditionTrue	= booleanConditionValue.getValue();

		// Se la condizione è vera, eseguo l'operazione matematica, eseguo il decremento e ritorno il risultato
		if (isBooleanConditionTrue) {

			isMathOperationValid		= true;
			RpaValue<Number> resultValue	= (RpaValue<Number>) visit(context.mathOperationStatement());

			// Controllo se il valore è null
			if (resultValue.getValue() == null) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Recupero il risultato dell'operazione
			Number mathOperationResult = resultValue.getValue();

			// Controllo se l'operazione è stata annullata
			if (!isMathOperationValid) {

				return new RpaValue(mainCompositore.getMnemonicEmpty());

			}

			// Recupero la formattazione definita dall'utente (se esiste)
			RpaFormat format = null;

			if (context.operationFormat() != null && context.operationFormat().children != null) {

				numberToFormat				= resultValue.getValue();
				RpaValue<RpaFormat> formatValue	= visitOperationFormat(context.operationFormat());
				format						= formatValue.getValue();

			}

			// Aggiungo il risultato nel valore del totalizzatore (se non c'è, lo inizializzo con il risultato)
			int totIndex;

			if (context.V() != null) {

				totIndex = RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

			} else {

				totIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

			}

			RpaValue<RpaAbstractMnemonicConstant> variableValue;

			if (variablesManager.isExists(RpaVariablesManager.Type.TOT, totIndex)) {

				variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, totIndex);

			} else {

				RpaAbstractMnemonicConstant newMnemonic	= new RpaMnemonicTOT(mainCompositore, mathOperationResult);
				variableValue							= new RpaValue<RpaAbstractMnemonicConstant>(newMnemonic);
				variablesManager.addVariable(RpaVariablesManager.Type.TOT, totIndex, variableValue);

			}

			Number newVariableValue;
			RpaMnemonicTOT mnemonicTOT	= (RpaMnemonicTOT) variableValue.getValue();
			Number oldVariableValue = mnemonicTOT.getValueNumber();

			if (format != null && RpaNumberUtils.isInteger(format.getValueNumber().toString())) {

				newVariableValue = oldVariableValue.intValue() - format.getValueNumber().intValue();

			} else if (format != null && RpaNumberUtils.isDouble(format.getValueNumber().toString())) {

				newVariableValue = oldVariableValue.doubleValue() - format.getValueNumber().doubleValue();

			} else if (oldVariableValue instanceof Integer && mathOperationResult instanceof Integer) {

				newVariableValue = oldVariableValue.intValue() - mathOperationResult.intValue();

			} else {

				newVariableValue = oldVariableValue.doubleValue() - mathOperationResult.doubleValue();

			}

			RpaAbstractMnemonicConstant newMnemonic = new RpaMnemonicTOT(mainCompositore, newVariableValue);
			variableValue.setValue(newMnemonic);

			// Riformatto il risultato
			if (format == null) {

				String result = resultValue.getValue().toString();
				result = formattedValue(result);

				// Ritorno il risultato
				return new RpaValue(result);

			} else {

				return new RpaValue(format.getPrintedValue());

			}

		}

		// Se la condizione è falsa, ritorno null
		else {

			// return new Value<Number>(null);
			return new RpaValue(mainCompositore.getMnemonicEmpty());

		}

	}

	@Override
	public RpaValue<RpaFormat> visitOperationFormat(OperationFormatContext context) {

		RpaDataConversionContainer	dataConversionContainer = mainCompositore.getDataConversionContainer();
		String 						lastFormattedValue 		= String.valueOf(numberToFormat);
		RpaFormat					lastFormat				= null;

		// Eseguo il cambio di formato dell'operazione
		if (context.changeFormat() != null) {

			dataConversionContainer.setValue(lastFormattedValue);
			visit(context.changeFormat());

			lastFormat				= dataConversionContainer.getCustomValueFormat();
			lastFormattedValue		= lastFormat.getFormattedValue();

		}

		dataConversionContainer.clear();

		// Restituisco il risultato
		return new RpaValue<RpaFormat>(lastFormat);

	}

	@Override
	public RpaValue<?> visitNumberPrecisionStatement(NumberPrecisionStatementContext context) {

		String countDigitString = context.NUMBER_SQUARE_SUFFIX_ID().getText();
		countDigitString		= countDigitString.substring(1);

		int countDigit = Integer.valueOf(countDigitString);
		mainCompositore.getStyleManager().setPrecision(countDigit);

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

	@Override
	public RpaValue<?> visitShowEmptyMnemonicStatement(ShowEmptyMnemonicStatementContext context) {

		// Modifico la funzione "emptyPrintedValue" sia per i mnemonici che per le formattazioni
		// StyleManager.getInstance().setIsStrokeActive(true);
		mainCompositore.getStyleManager().setIsStrokeActive(true);
		debugMessages.print("[TRAON] ACTIVATE");

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

	@Override
	public RpaValue<?> visitHideEmptyMnemonicStatement(HideEmptyMnemonicStatementContext context) {

		// Modifico la funzione "emptyPrintedValue" sia per i mnemonici che per le formattazioni
		// StyleManager.getInstance().setIsStrokeActive(false);
		mainCompositore.getStyleManager().setIsStrokeActive(false);
		debugMessages.print("[TRAOFF] ACTIVATE");

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

	@Override
	public RpaValue<?> visitNewsynStatement(NewsynStatementContext ctx) {

		RpaValue newValue = new RpaValue(null);
		newValue.setIsSkip(true);

		return newValue;

	}

	/**
	 * Formatta il risultato di una operazione matematica nel formato "[I]"
	 * @param stringValue
	 * @return
	 */
	private String formattedValue(String stringValue) {

		String	formattedValue		= null;
		String	rawFormat			= stringValue;
		int		countDigits			= RpaFormat.DEFAULT_LENGTH_CHARACTERS_FORMAT;
		char	decimalChar			= mainCompositore.getStyleManager().getDecimalSeparator().charAt(0);
		char	integerChar			= mainCompositore.getStyleManager().getIntegerSeparator().charAt(0);

		// Sostituisco il carattere dei decimali (se non ci fosse, lo aggiungo)
		rawFormat = rawFormat.replaceAll("\\.", String.valueOf(decimalChar));

		if (rawFormat.indexOf(decimalChar) == -1) {

			rawFormat += decimalChar + "0";

		}

		// Conto il numero di cifre della formattazione
		int dotIndex            = rawFormat.indexOf(decimalChar);
		int integerPartSize     = dotIndex;
		int decimalDigitsCount  = rawFormat.length() - dotIndex - 1;
		int countApexesToAdd;
		int countDigitsFormat;

		if (integerPartSize > 3) {

			countApexesToAdd = (integerPartSize - 1) / 3;

		} else {

			countApexesToAdd = 0;

		}

		countDigitsFormat = integerPartSize + countApexesToAdd + 3;

		// Controllo se il numero di cifre è inferiore a quello definito
		if (countDigits < countDigitsFormat) {

			// Creo una stringa di '*'
			formattedValue = "";

			for (int i = 0; i < countDigits; i ++) {

				formattedValue += '*';

			}

		} else {

			// Controllo di avere due decimali
			if (decimalDigitsCount < 2) {

				int countZeroToAdd = 2 - decimalDigitsCount;

				for (int i = 0; i < countZeroToAdd; i ++) {

					rawFormat = rawFormat + "0";

				}

			} else if (decimalDigitsCount > 2) {

				rawFormat = rawFormat.substring(0, dotIndex + 3);

			}

			// Cominciando dal punto, aggiungo l'apice alle centinaia
			int currentCharIndex    = dotIndex - 1;
			int countCharRead       = 1;

			while (countCharRead < integerPartSize) {

				if (countCharRead % 3 == 0) {

					String leftPart     = rawFormat.substring(0, currentCharIndex);
					String rightPart    = rawFormat.substring(currentCharIndex);

					rawFormat = leftPart + integerChar + rightPart;

				}

				-- currentCharIndex;
				++ countCharRead;

			}

			// Controllo se aggiungere gli spazi
			int countSpacesToAdd = countDigits - countDigitsFormat;

			for (int i = 0; i < countSpacesToAdd; i ++) {

				rawFormat = ' ' + rawFormat;

			}

			// Sostituisco i caratteri-separatori
			RpaStyleManager styleManager = mainCompositore.getStyleManager();
			rawFormat = styleManager.formatDecimal(rawFormat);

			// Salvo il valore formattato
			formattedValue = rawFormat;

		}

		return formattedValue;

	}

	/**
	 * Utilizzata per mantenere sempre e solo 2 cifre dopo la virgola
	 * @param stringValue
	 * @return
	 */
	private String oldFormattedValue(String stringValue) {

		// Estraggo la parte decimale
		BigDecimal	bigDecimal	= new BigDecimal(stringValue);
		String		integerPart = "";
		String		decimalPart = "";

		if (RpaNumberUtils.isInteger(stringValue)) {

			integerPart = stringValue;
			decimalPart = "";

		} else {

			integerPart = bigDecimal.toBigInteger().toString();
			decimalPart = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("0\\.", "");

		}

		String  newDecimalPart      = "";
		int     countDecimalDigits  = OPERATION_DECIMAL_DIGIT_COUNT;

		// Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
		if (decimalPart.isEmpty()) {

			for (int i = 0; i < countDecimalDigits; i ++) {

				newDecimalPart += "0";

			}

		}

		// Controllo se tagliare la parte decimale: 23.090923311 -> 23.09
		else if (decimalPart.length() > countDecimalDigits) {

			newDecimalPart = decimalPart.substring(0, countDecimalDigits);

		}

		// Controllo se aggiungere gli zeri in coda alla parte decimale: 23.34 -> 23.340000000
		else if (decimalPart.length() < countDecimalDigits) {

			int countZeroToAdd = countDecimalDigits - decimalPart.length();

			newDecimalPart = decimalPart;

			for (int i = 0; i < countZeroToAdd; i ++) {

				newDecimalPart += "0";

			}

		}

		// Negli altri casi mantengo la stessa parte decimale
		else {

			newDecimalPart = decimalPart;

		}

		return integerPart + "." + newDecimalPart;

	}

	@Override
	public RpaValue<?> visitIncludeFileStatement(IncludeFileStatementContext context) {

		// TODO: Implementare l'aggiunta del documento in "Interprete.java"
		// TODO: Passando da qui un "IncludeValue" che include il documento
		// TODO: Nota -> Dopo l'aggiunta dei nodi, quelli dovranno essere saltati dall'interprete

		// Recupero il documento dal path
		String documentPath = context.EXTENSE_GENERIC_SQUARE_SUFFIX_ID().getText();
		documentPath = documentPath.substring(1, documentPath.length());

		// Esco restituendo il documento tramite "IncludeFileValue"
		Document documentToInclude;
		try {

			documentToInclude = new Document(new FileInputStream(documentPath));

		} catch (Exception exception) {

			String	code		= context.getText();
			String	message		= "Impossibile includere il file '" + documentPath + "'";
			int		contextId	= RpaComposerException.COMPILE_MESSAGE;

			throw new RpaIncludeException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

		}

		RpaIncludeFileValue includeFileValue = new RpaIncludeFileValue(documentPath);
		includeFileValue.setDocument(documentToInclude);

		return includeFileValue;

		/*
		String	code		= context.getText();
		String	message		= "[INCLUDE] Non implementata";
		int		contextId	= ComposerException.COMPILE_MESSAGE;

		throw new IncludeException(mainCompositore.getComposerConfiguration(), contextId, code, message);
		*/

	}

	/*	@Override
	public Value<?> visitNewsyn(CompoParser.NewsynContext ctx) {
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitPhrase(CompoParser.PhraseContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		return new Value<Object>(ctx.getText());
	}*/

/*	@Override
	public Value<?> visitStyDirective(CompoParser.StyDirectiveContext ctx) {
		boolean prosegui = checkIfScope();

		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		String directive = ctx.getText();
		if (ctx.styPrec() != null) { // [PRECISION]
			String precision = String.valueOf(visit(ctx.styPrec().prec).getValue());
			styleManager.getPrec().setPrecision(Integer.valueOf(precision));
		} else if (ctx.styTra() != null) {
			if (directive.startsWith("[TRAON")) {
				styleManager.getTrat().setEnabe(true);

				// Imposto l'eventuale carattere personalizzato
				if (ctx.styTra().sym != null) {
					styleManager.getTrat().setSym(ctx.styTra().sym.getText().charAt(0));
				}
			} else {
				styleManager.getTrat().setEnabe(false);
			}
		} else if (ctx.styDom() != null) {
			styleManager.getDom().setEnabe(directive.startsWith("[DOMON"));
		} else if (ctx.styEur() != null) {
			styleManager.getEur().setEnabe(directive.startsWith("[EUROON"));
		} else if (ctx.styPro() != null) {
			styleManager.getPro().setEnabe(directive.startsWith("[PROON"));
		} else if (ctx.styImp() != null) {
			styleManager.getImp().setEnabe(directive.startsWith("[IMPON"));
		} else if (ctx.styRtl() != null) {
			styleManager.getRtl().setEnabe(directive.startsWith("[RTLON"));
		} else {
			LOG.error(new Exception("Errore nella sintassi, la notazione " + directive + " non è una direttiva di stile valida", //
					new Exception()));
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitMailto(CompoParser.MailtoContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		MailUtils mailUtils = MailUtils.getInstance();
		if (mailUtils != null) {

			boolean html = false;
			if (ctx.html != null) {
				html = true;
			}

			boolean result = false;
			try {
				result = mailUtils.sendEmail(visit(ctx.sender).getValue().toString(), visit(ctx.recipient).getValue().toString(), //
						visit(ctx.title).getValue().toString(), html, visit(ctx.body).getValue().toString());
			} catch (EmailException e) {
				LOG.error(new CompoException("Non è stato possibile inviare il messaggio, controllare la configurazione", new EmailException()));
			} finally {
				if (ctx.ris.var.getText().startsWith("TOT")) {
					Matcher matcher = Pattern.compile("^TOT([\\d]+)?$").matcher(ctx.ris.var.getText());

					Integer index = 0;
					if (matcher.find() && matcher.group(1) != null) {
						index = Integer.valueOf(matcher.group(1));
					}

					if (!variablesManager.addVariable(VariablesManager.TOT, index, new Value(BooleanUtils.toInteger(!result)))) {
						variablesManager.setVariableValue(VariablesManager.TOT, index, new Value(BooleanUtils.toInteger(!result)));
					}
				} else {
					LOG.error(new CompoException("Errore sintassi, il tipo di variabile \"" + ctx.ris.var.getText() + //
							"\" non è applicabile", new Exception()));
				}
			}
		} else {
			LOG.error(new CompoException("Impossibile inizzializzare il servizio email, controllare il file di configurazione \'compo.ini\'", //
					new ConfigurationException()));
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitInclude(CompoParser.IncludeContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value result = null;

		String path = ctx.getText().replace("[INCLUDE]", "");
		File file = new File(path);
		if (file.exists() && file.canRead()) {
			try {
				result = new Value(new String(FileUtils.readFileToByteArray(file)));
			} catch (IOException e) {
				LOG.error(new CompoException("Errore durante la lettura del file da includere", new IOException()));
			}
		} else {
			LOG.error(new CompoException("Non è stato possibile include il file indicato: " + path + " file non esistente o non leggibile", //
					new Exception()));
		}

		return (result != null) ? result : Value.SKIP;
	}*/

/*	@Override
	public Value visitIncludemod(CompoParser.IncludemodContext ctx) {
		*//*
	 * Non dovresti essere qui --> Questo comando dovrebbe essere stato gestito
	 * dal plug-in in fase di avvio oppure:
	 *  - il documento non è un rtf valido quindi la direttiva non dovrebbe essere presente
	 *  - Il documento che si voleva includere non era un tipo rtf valido qundi non è stato incluso
	 *//*
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitComment(CompoParser.CommentContext ctx) {
		String text = ctx.COMMENT().getText();
		if (text.matches("^\\@(.*)$")) {
			try {
				Matcher matcher = Pattern.compile("^\\@(.*)$").matcher(text);
				if (matcher.find()) {
					new Interprete().parse(text.substring(1, text.length())); // <-- Eseguo una interpretazione del testo del commento
				}
			} catch (Exception e) {
				LOG.error(e);
			}
		} else {
			LOG.error(new CompoException("Errore nella sintassi, il commento " + ctx.COMMENT().getText() + " non è formattato in miniera corretta", //
					new Exception()));
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitInlinecomment(CompoParser.InlinecommentContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		return new Value(ctx.INLINE_COMMENT().getText());
	}*/
}
