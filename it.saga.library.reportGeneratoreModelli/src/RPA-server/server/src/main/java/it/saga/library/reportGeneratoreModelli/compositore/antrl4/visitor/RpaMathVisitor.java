package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.AbsContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.CosContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LnContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LogContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationDivisionContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationExponentContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationMultiplicationContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationNegativeFloatContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationNegativeIntegerContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationParenthesisContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationPositiveFloatContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationPositiveIntegerContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationSubtractionContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationSumContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MathOperationUnaryOperationContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.NegativeNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.PositiveNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.SenContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.SqrContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.SqrtContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.TanContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.UnaryOperationContext;

import java.sql.Connection;
import java.util.Stack;

public abstract class RpaMathVisitor extends RpaBooleanVisitor {

	public RpaMathVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {
		super(conn, scope, mainCompositore, parentNode, childNode);
	}

	@Override
	public RpaValue<Number> visitMathOperationExponent(MathOperationExponentContext context) {

		if (!isMathOperationValid) { return null; }

		RpaValue<Number> leftValue		= (RpaValue<Number>) visit(context.mathOperationStatement(0));
		RpaValue<Number> rightValue	= (RpaValue<Number>) visit(context.mathOperationStatement(1));

		if (!isMathOperationValid) { return null; }

		Number leftNumber	= leftValue.getValue();
		Number rightNumber	= rightValue.getValue();

		if (leftNumber == null || rightNumber == null) {

			isMathOperationValid = false;

			return new RpaValue<Number>(null);

		}

		Number exponentNumber = Math.pow(leftNumber.doubleValue(), rightNumber.doubleValue());

		RpaValue<Number> exponentValue = new RpaValue<Number>(exponentNumber);

		return exponentValue;

	}

	@Override
	public RpaValue<Number> visitMathOperationDivision(MathOperationDivisionContext context) {

		if (!isMathOperationValid) { return null; }

		RpaValue<Number> leftValue	= (RpaValue<Number>) visit(context.mathOperationStatement(0));
		RpaValue<Number> rightValue	= (RpaValue<Number>) visit(context.mathOperationStatement(1));

		if (!isMathOperationValid) { return null; }

		Number leftNumber	= leftValue.getValue();
		Number rightNumber	= rightValue.getValue();

		if (leftNumber == null || rightNumber == null) {

			isMathOperationValid = false;

			return new RpaValue<Number>(null);

		}

		if (rightNumber.doubleValue() == 0 || rightNumber.doubleValue() == 0.0) {

			isMathOperationValid = false;

			return new RpaValue<Number>(null);

		}

		Number divisionNumber = leftNumber.doubleValue() / rightNumber.doubleValue();

		RpaValue<Number> divisionValue = new RpaValue<Number>(divisionNumber);

		return divisionValue;

	}

	@Override
	public RpaValue<Number> visitMathOperationMultiplication(MathOperationMultiplicationContext context) {

		if (!isMathOperationValid) { return null; }

		RpaValue<Number> leftValue		= (RpaValue<Number>) visit(context.mathOperationStatement(0));
		RpaValue<Number> rightValue	= (RpaValue<Number>) visit(context.mathOperationStatement(1));

		if (!isMathOperationValid) { return null; }

		Number leftNumber	= leftValue.getValue();
		Number rightNumber	= rightValue.getValue();

		if (leftNumber == null || rightNumber == null) {

			isMathOperationValid = false;

			return new RpaValue<Number>(null);

		}

		Number multiplicationNumber = leftNumber.doubleValue() * rightNumber.doubleValue();

		RpaValue<Number> multiplicationValue = new RpaValue<Number>(multiplicationNumber);

		return multiplicationValue;

	}

	@Override
	public RpaValue<Number> visitMathOperationSubtraction(MathOperationSubtractionContext context) {

		if (!isMathOperationValid) { return null; }

		RpaValue<Number> leftValue	= (RpaValue<Number>) visit(context.mathOperationStatement(0));
		RpaValue<Number> rightValue	= (RpaValue<Number>) visit(context.mathOperationStatement(1));

		if (!isMathOperationValid) { return null; }

		Number leftNumber	= leftValue.getValue();
		Number rightNumber	= rightValue.getValue();

		if (leftNumber == null && rightNumber == null) {

			// return null;
			return new RpaValue<Number>(null);

		} else if (leftNumber != null && rightNumber == null) {

			return new RpaValue<Number>(leftNumber);

		} else if (leftNumber == null && rightNumber != null) {

			return new RpaValue<Number>(rightNumber);

		}

		Number subtractionNumber = leftNumber.doubleValue() - rightNumber.doubleValue();

		RpaValue<Number> subtractionValue = new RpaValue<Number>(subtractionNumber);

		return subtractionValue;

	}

	@Override
	public RpaValue<Number> visitMathOperationSum(MathOperationSumContext context) {

		if (!isMathOperationValid) { return null; }

		RpaValue<Number> leftValue		= (RpaValue<Number>) visit(context.mathOperationStatement(0));
		RpaValue<Number> rightValue	= (RpaValue<Number>) visit(context.mathOperationStatement(1));

		if (!isMathOperationValid) { return null; }

		Number leftNumber	= leftValue.getValue();
		Number rightNumber	= rightValue.getValue();

		if (leftNumber == null && rightNumber == null) {

			// return null;
			return new RpaValue<Number>(null);

		} else if (leftNumber != null && rightNumber == null) {

			return new RpaValue<Number>(leftNumber);

		} else if (leftNumber == null && rightNumber != null) {

			return new RpaValue<Number>(rightNumber);

		}

		Number sumNumber = leftNumber.doubleValue() + rightNumber.doubleValue();

		RpaValue<Number> sumValue = new RpaValue<Number>(sumNumber);

		return sumValue;

	}

	@Override
	public RpaValue<Number> visitMathOperationMnemonic(MathOperationMnemonicContext context) {

		// Recupero il mnemonico
		RpaValue<RpaAbstractMnemonic>	abstractMnemonicValue	= (RpaValue<RpaAbstractMnemonic>) visit(context.mnemonic());
		RpaAbstractMnemonic				mnemonic				= abstractMnemonicValue.getValue();

		String	stringValue;
		Number	numberValue;
		RpaValue newValue 	= new RpaValue(null);

		switch (mnemonic.getType()) {

			case RpaAbstractMnemonic.TYPE_STRING:

				// Se ho una stringa non valida, termino l'operazione
				stringValue = mnemonic.getValue();

				// if (stringValue != null && !stringValue.isEmpty() && !stringValue.matches("^[ \\t]*$")) {
                if (stringValue == null || stringValue.isEmpty() || stringValue.matches("^[ \\t]*$")) {

					isMathOperationValid = false;

				}

				newValue.setValue(mnemonic.getValueForMath());

				break;

			case RpaAbstractMnemonic.TYPE_NUMBER:
			case RpaAbstractMnemonic.TYPE_BOOLEAN:
			case RpaAbstractMnemonic.TYPE_DATE:
			case RpaAbstractMnemonic.TYPE_TIMESTAMP:
			case RpaAbstractMnemonic.TYPE_TABULATION:
			case RpaAbstractMnemonic.TYPE_MONEY:

				numberValue = mnemonic.getValueForMath();
				newValue.setValue(numberValue);

				break;

			case RpaAbstractMnemonic.TYPE_UNDEFINED:

				numberValue = mnemonic.getValueForMath();
				newValue.setValue(numberValue);

				break;

			case RpaAbstractMnemonic.TYPE_EMPTY:

				isMathOperationValid = false;

				break;

		}



		return newValue;

	}

	@Override
	public RpaValue<Number> visitMathOperationUnaryOperation(MathOperationUnaryOperationContext context) {

		if (!isMathOperationValid) { return null; }

		RpaValue<Number> termValue	= (RpaValue<Number>) visit(context.unaryOperation());
		Number			term		= termValue.getValue();

		if (!isMathOperationValid) { return null; }

		if (term == null) {

			isMathOperationValid = false;

			return new RpaValue<Number>(null);

		}

		return (RpaValue<Number>) visit(context.unaryOperation());

	}

	@Override
	public RpaValue<Number> visitMathOperationParenthesis(MathOperationParenthesisContext context) {

		if (!isMathOperationValid) { return null; }

		return (RpaValue<Number>) visit(context.mathOperationStatement());

	}

	@Override
	public RpaValue<Number> visitMathOperationPositiveInteger(MathOperationPositiveIntegerContext context) {

		if (!isMathOperationValid) { return null; }

		Integer parsedInteger = Integer.parseInt(context.getText());

		return new RpaValue<Number>(parsedInteger);

	}

	@Override
	public RpaValue<Number> visitMathOperationPositiveFloat(MathOperationPositiveFloatContext context) {

		if (!isMathOperationValid) { return null; }

		Double parsedDouble = Double.parseDouble(context.getText());

		return new RpaValue<Number>(parsedDouble);

	}

	@Override
	public RpaValue<Number> visitMathOperationNegativeInteger(MathOperationNegativeIntegerContext context) {

		if (!isMathOperationValid) { return null; }

		Integer parsedInteger = - Integer.parseInt(context.POSITIVE_INTEGER().getText());

		return new RpaValue<Number>(parsedInteger);

	}

	@Override
	public RpaValue<Number> visitMathOperationNegativeFloat(MathOperationNegativeFloatContext context) {

		if (!isMathOperationValid) { return null; }

		Double parsedDouble = - Double.parseDouble(context.POSITIVE_FLOAT().getText());

		return new RpaValue<Number>(parsedDouble);

	}

	@Override
	public RpaValue<Number> visitSqrt(SqrtContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number sqrtNumber	= Math.sqrt(numberTerm.doubleValue());

		return new RpaValue<Number>(sqrtNumber);

	}

	@Override
	public RpaValue<Number> visitSqr(SqrContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number sqrNumber	= Math.pow(numberTerm.doubleValue(), 2);

		return new RpaValue<Number>(sqrNumber);

	}

	@Override
	public RpaValue<Number> visitTan(TanContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number tanNumber	= Math.tan(numberTerm.doubleValue());

		return new RpaValue<Number>(tanNumber);

	}

	@Override
	public RpaValue<Number> visitSen(SenContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number senNumber	= Math.sin(numberTerm.doubleValue());

		return new RpaValue<Number>(senNumber);

	}

	@Override
	public RpaValue<Number> visitCos(CosContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number cosNumber	= Math.cos(numberTerm.doubleValue());

		return new RpaValue<Number>(cosNumber);

	}

	@Override
	public RpaValue<Number> visitLn(LnContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number lnNumber		= Math.log(numberTerm.doubleValue());

		return new RpaValue<Number>(lnNumber);

	}

	@Override
	public RpaValue<Number> visitLog(LogContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number logNumber	= Math.log10(numberTerm.doubleValue());

		return new RpaValue<Number>(logNumber);

	}

	@Override
	public RpaValue<Number> visitAbs(AbsContext context) {

		if (!isMathOperationValid) { return null; }

		Number numberTerm	= extractUnaryOperationTerm(context);
		Number absNumber	= Math.abs(numberTerm.doubleValue());

		return new RpaValue<Number>(absNumber);

	}

	@Override
	public RpaValue<Number> visitPositiveNumber(PositiveNumberContext context) {

		if (!isMathOperationValid) { return null; }

		Number positiveNumber;

		if (context.POSITIVE_INTEGER() != null) {

			positiveNumber = Integer.parseInt(context.getText());

		}

		else {

			positiveNumber = Double.parseDouble(context.getText());

		}

		return new RpaValue<Number>(positiveNumber);

	}

	@Override
	public RpaValue<Number> visitNegativeNumber(NegativeNumberContext context) {

		if (!isMathOperationValid) { return null; }

		Number negativeNumber;

		if (context.POSITIVE_INTEGER() != null) {

			negativeNumber = Integer.parseInt(context.getText());

		}

		else {

			negativeNumber = Double.parseDouble(context.getText());

		}

		return new RpaValue<Number>(negativeNumber);

	}

	private Number extractUnaryOperationTerm(UnaryOperationContext context) {

		// Il parametro si trova alla seconda posizione (con e senza "[]") e può essere un
		// 		- Numero positivo (positiveNumber)
		// 		- Numero negativo (negativeNumber)
		// 		- Una operazione matematica (mathOperationStatement)

		RpaValue<Number> numberValue;

		if (context.children.size() == 2) {

			numberValue = (RpaValue<Number>) visit(context.children.get(1));

		} else {

			numberValue = (RpaValue<Number>) visit(context.children.get(2));

		}

		return numberValue.getValue();

	}



/*	@Override
	public Value<?> visitNumber(CompoParser.NumberContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui && scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		return new Value(new Integer(ctx.getText()));
	}*/

/*	@Override
	public Value<?> visitDecimal(CompoParser.DecimalContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		return new Value(new Double(ctx.getText()));
	}*/

/*	@Override
	public Value<?> visitMath(CompoParser.MathContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value ris = null;
		if (ctx.number() != null) {
			ris = visit(ctx.number());
		} else if (ctx.decimal() != null) {
			ris = visit(ctx.decimal());
		}

		return ris;
	}*/

/*	@Override
	public Value<?> visitAssignment(CompoParser.AssignmentContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value argument = visit(ctx.argument);

        *//*
		 * Se il valore dell'operazione è "null" allora questa
         * aveva una condizione if al suo interno
         * che è risultata essere non falsa quindi questa operazione
         * va ignorata.
         *//*
		if (argument.getValue() == null) {
			return Value.SKIP;
		}

		Integer index = (ctx.index != null) ? Integer.valueOf(ctx.index.getText()) : 0;

		// --> L'utente vuole modificare il valore di una variabile
		if (ctx.assignment != null) {

			// --> L'utente vuole cambiare il puntatore ad un vettore
			if (index.equals(999) | index.equals(998)) {
				if (String.valueOf(argument.getValue()).matches("^\\d+(?:\\.\\d+)?$")) {
					int pointer = Double.valueOf(String.valueOf(argument.getValue())).intValue();
					if (index.equals(999)) {
						variablesManager.setArrayPointer(variablesManager.TOTV, pointer);
					} else if (index.equals(998)) {
						variablesManager.setArrayPointer(variablesManager.STRV, pointer);
					}
				} else {
					LOG.error(new CompoException("Errore nella sintassi, l'indice che si sta cercando di puntare non è della forma corretta (Intero:nn) != " + //
							argument, new Exception()));
				}
				return Value.SKIP;
			}

			// --> L'utente vuole assegnare un valore alla variabile puntata dal vettore #TOTV#
			if (ctx.array != null) {
				variablesManager.setArrayPointerValue(VariablesManager.TOTV, argument);
				return Value.SKIP;
			}

			boolean result = variablesManager.addVariable(VariablesManager.TOT, index, argument);
			if (!result) {
				LOG.error(new CompoException("Errore nella sintassi, l'indice \"" + ctx.index.getText() + "\" è già stato inizzializzato", //
						new Exception()));
			}
		} else {
			if (!index.equals(999) && !index.equals(998)) {
				if (ctx.op.getText().equals("+") || ctx.op.getText().equals("-")) {
					// Verifico che la variabile esista
					if (variablesManager.exists(VariablesManager.TOT, index)) {
						Value tmp = variablesManager.getVariableValue(VariablesManager.TOT, index);
						Double result = (ctx.op.getText().equals("+")) ? Double.valueOf(String.valueOf(tmp.getValue())) + //
								Double.valueOf(String.valueOf(argument.getValue())) : Double.valueOf(String.valueOf(tmp.getValue())) - //
								Double.valueOf(String.valueOf(argument.getValue()));
						variablesManager.setVariableValue(VariablesManager.TOT, index, new Value(result));
					} else {
						LOG.error(new CompoException("Errore nella sintassi, l'indice \"" + ctx.getText() + "\" non è mai stato inizzializzato", //
								new Exception()));
					}
				} else {
					LOG.error(new CompoException("Errore nella sintassi, operazione \"" + ctx.getText() + "\" non è supportata: caratteri consentiti (+|-)", //
							new Exception()));
				}
			} else {
				LOG.error(new CompoException("Errore nella sintassi, non puoi accedere al vettore in questo contesto", new Exception()));
			}
		}
		return Value.SKIP;
	}*/

/*	@Override
	public Value<?> visitMathope(CompoParser.MathopeContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Double vLeft = Double.valueOf(visit(ctx.lop).getValue().toString());
		Double vRight = Double.valueOf(visit(ctx.rop).getValue().toString());

		Value result = null;
		if (ctx.MATHOP().getText().equals("+")) {
			result = new Value(vLeft + vRight);
		} else if (ctx.MATHOP().getText().equals("-")) {
			result = new Value(vLeft - vRight);
		} else if (ctx.MATHOP().getText().equals("*")) {
			result = new Value(vLeft * vRight);
		} else if (ctx.MATHOP().getText().equals("/")) {
			result = new Value(vLeft / vRight);
		} else if (ctx.MATHOP().getText().equals("^")) {
			result = new Value(Math.pow(vLeft, vRight));
		}
		return result;
	}*/

/*	@Override
	public Value<?> visitLogicExpr(CompoParser.LogicExprContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Value vLeft = visit(ctx.left);
		Value vRight = visit(ctx.right);

		boolean result = false;
		if (ctx.COMPAREOP().getText().equals("EQ")) {
			result = vLeft.compareTo(vRight) == 0;
		} else if (ctx.COMPAREOP().getText().equals("NE")) {
			result = vLeft.compareTo(vRight) != 0;
		} else if (ctx.COMPAREOP().getText().equals("GT")) {
			result = vLeft.compareTo(vRight) > 0;
		} else if (ctx.COMPAREOP().getText().equals("GE")) {
			result = vLeft.compareTo(vRight) >= 0;
		} else if (ctx.COMPAREOP().getText().equals("LT")) {
			result = vLeft.compareTo(vRight) < 0;
		} else if (ctx.COMPAREOP().getText().equals("LE")) {
			result = vLeft.compareTo(vRight) <= 0;
		} else {
			LOG.error(new CompoException("Errore nella sintassi, " + ctx.COMPAREOP().getText() + " non risulta essere un operatore valido", //
					new Exception()));
		}

		return new Value(new Boolean(result));
	}*/

/*	@Override
	public Value<?> visitMathfun(CompoParser.MathfunContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		Double argument = Double.valueOf(visit(ctx.argument).getValue().toString());

		if (ctx.sign != null) {
			argument = (ctx.sign.getText().equals("-")) ? (argument * -1) : argument;
		}

		Value result = null;
		if (ctx.op.getText().equals("sqrt")) {
			result = new Value(Math.sqrt(argument));
		} else if (ctx.op.getText().equals("sqr")) {
			result = new Value(Math.pow(argument, 2));
		} else if (ctx.op.getText().equals("cos")) {
			result = new Value(Math.cos(argument));
		} else if (ctx.op.getText().equals("sen")) {
			result = new Value(Math.sin(argument));
		} else if (ctx.op.getText().equals("tan")) {
			result = new Value(Math.tan(argument));
		} else if (ctx.op.getText().equals("ln")) {
			result = new Value(Math.log(argument));
		} else if (ctx.op.getText().equals("log")) {
			result = new Value(Math.log(argument) / Math.log(10));
		} else if (ctx.op.getText().equals("abs")) {
			result = new Value(Math.abs(argument));
		}
		return result;
	}*/

/*	@Override
	public Value<?> visitLogicop(CompoParser.LogicopContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		ArrayList<Value> logicResults = new ArrayList<Value>();
		for (int i = 0; i <= ctx.logicExpr().size() - 1; i++) {
			logicResults.add(visit(ctx.logicExpr(i)));

			if (i + 1 <= ctx.logicExpr().size() - 1) {
				logicResults.add(new Value(ctx.LOGICOP(i).getText()));
			}
		}

		// Risolvo la condizione logica
		for (int i = 0; i <= logicResults.size() - 1; i++) {
			String tmp = logicResults.get(i).getValue().toString();
			if (tmp.equals("OR") || tmp.equals("AND")) {
				if (tmp.equals("OR")) {
					if (logicResults.get(i - 1).getValue().equals(true) || logicResults.get(i + 1).getValue().equals(true)) {
						logicResults.get(i).setValue(true);
					} else {
						logicResults.get(i).setValue(false);
					}
				} else if (tmp.equals("AND")) {
					if (logicResults.get(i - 1).getValue().equals(true) && logicResults.get(i + 1).getValue().equals(true)) {
						logicResults.get(i).setValue(true);
					} else {
						logicResults.get(i).setValue(false);
					}
				}
				logicResults.remove(i - 1);
				logicResults.remove(i);
				i = i - 2;
			}
		}

		return logicResults.get(0); // L'unico elemento che resta nella arraylist è il risultato
	}*/

/*	@Override
	public Value<?> visitComplogicop(CompoParser.ComplogicopContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		return visit(ctx.logicop());
	}*/

/*	@Override
	public Value<?> visitOperation(CompoParser.OperationContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		if (!styleManager.getRtl().isEnable() && ctx.getText().matches("^\\|.*\\|$")) {
			LOG.error(new CompoException("Errore nella sintassi, si sta tentando di utilizzare la notazione RTF " //
					+ "(|<operazione matematica>|) senza averla abilitata", new Exception()));
		} else if (styleManager.getRtl().isEnable() && !ctx.getText().matches("^\\|.*\\|$")) {
			LOG.error(new CompoException("Errore nella sintassi, abilitando la notazione RTF ([RTFON]:|<operazione matematica>|) " //
					+ "è necessario scrivere le operazioni utilizzando il carattere | anzichè il carattere \\", new Exception()));
		}

		// --> \%#TOT1#_EQ_100%200\\++1\ Gestione operazioni condizionali
		if (ctx.con != null && !Boolean.TRUE.equals(visit(ctx.con).getValue())) {
			return Value.SKIP;
		}

		return styleManager.getPrec().optimize(visit(ctx.ris));
	}*/
}
