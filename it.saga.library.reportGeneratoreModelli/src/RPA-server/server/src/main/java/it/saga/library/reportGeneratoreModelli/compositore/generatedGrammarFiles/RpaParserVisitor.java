// Generated from it\saga\library\reportGeneratoreModelli\compositore\generatedGrammarFiles\RpaParser.g4 by ANTLR 4.5
package it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles;
import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.NotNull;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RpaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RpaParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RpaParser#positiveNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositiveNumber(RpaParser.PositiveNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#negativeNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegativeNumber(RpaParser.NegativeNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_0A_nn1}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_0A_nn1(RpaParser.FormatD_n_0A_nn1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_A0_nn}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_A0_nn(RpaParser.FormatD_n_A0_nnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_0a_nn2}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_0a_nn2(RpaParser.FormatD_n_0a_nn2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_AA_nn1}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_AA_nn1(RpaParser.FormatD_n_AA_nn1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_Aa_nn2}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_Aa_nn2(RpaParser.FormatD_n_Aa_nn2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_aa_nn3}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_aa_nn3(RpaParser.FormatD_n_aa_nn3Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_aa_0n}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_aa_0n(RpaParser.FormatD_n_aa_0nContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_aa_00}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_aa_00(RpaParser.FormatD_n_aa_00Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_aa_0a}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_aa_0a(RpaParser.FormatD_n_aa_0aContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_a_aa_aa}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_a_aa_aa(RpaParser.FormatD_a_aa_aaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_aa_aa}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_aa_aa(RpaParser.FormatD_0_aa_aaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_aa_aa}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_aa_aa(RpaParser.FormatD_n_aa_aaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_00_aa}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_00_aa(RpaParser.FormatD_0_00_aaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_00_0a}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_00_0a(RpaParser.FormatD_0_00_0aContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_00_0n}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_00_0n(RpaParser.FormatD_0_00_0nContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_00_nn}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_00_nn(RpaParser.FormatD_0_00_nnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_aa_001}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_aa_001(RpaParser.FormatD_0_aa_001Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_Aa_002}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_Aa_002(RpaParser.FormatD_0_Aa_002Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_AA_003}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_AA_003(RpaParser.FormatD_0_AA_003Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_00_00}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_00_00(RpaParser.FormatD_n_00_00Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_a_00_001}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_a_00_001(RpaParser.FormatD_a_00_001Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_A_00_002}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_A_00_002(RpaParser.FormatD_A_00_002Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_nn_nn}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_nn_nn(RpaParser.FormatD_n_nn_nnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_nn_0nI}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_nn_0nI(RpaParser.FormatD_n_nn_0nIContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_nn_0nI}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_nn_0nI(RpaParser.FormatD_0_nn_0nIContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_nn_nnI}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_nn_nnI(RpaParser.FormatD_n_nn_nnIContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_nn_nnI}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_nn_nnI(RpaParser.FormatD_0_nn_nnIContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_nn_nnU}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_nn_nnU(RpaParser.FormatD_n_nn_nnUContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_n_nn_0nU}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_n_nn_0nU(RpaParser.FormatD_n_nn_0nUContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatD_0_nn_00}
	 * labeled alternative in {@link RpaParser#domdatecustom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatD_0_nn_00(RpaParser.FormatD_0_nn_00Context ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(RpaParser.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#genericText}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericText(RpaParser.GenericTextContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(RpaParser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(RpaParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStatementNew}
	 * labeled alternative in {@link RpaParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatementNew(RpaParser.IfStatementNewContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStatementOld}
	 * labeled alternative in {@link RpaParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatementOld(RpaParser.IfStatementOldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanStatementComparison}
	 * labeled alternative in {@link RpaParser#booleanStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanStatementComparison(RpaParser.BooleanStatementComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanStatementComplex}
	 * labeled alternative in {@link RpaParser#booleanStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanStatementComplex(RpaParser.BooleanStatementComplexContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#comparisonStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonStatement(RpaParser.ComparisonStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonTermMnemonic}
	 * labeled alternative in {@link RpaParser#comparisonTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonTermMnemonic(RpaParser.ComparisonTermMnemonicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonTermPositiveNumber}
	 * labeled alternative in {@link RpaParser#comparisonTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonTermPositiveNumber(RpaParser.ComparisonTermPositiveNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonTermNegativeNumber}
	 * labeled alternative in {@link RpaParser#comparisonTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonTermNegativeNumber(RpaParser.ComparisonTermNegativeNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonTermMathOperationStatement}
	 * labeled alternative in {@link RpaParser#comparisonTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonTermMathOperationStatement(RpaParser.ComparisonTermMathOperationStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparisonTermString}
	 * labeled alternative in {@link RpaParser#comparisonTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonTermString(RpaParser.ComparisonTermStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifEndOld}
	 * labeled alternative in {@link RpaParser#ifEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfEndOld(RpaParser.IfEndOldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifEndNew}
	 * labeled alternative in {@link RpaParser#ifEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfEndNew(RpaParser.IfEndNewContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseStatementOld}
	 * labeled alternative in {@link RpaParser#elseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseStatementOld(RpaParser.ElseStatementOldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseStatementNew}
	 * labeled alternative in {@link RpaParser#elseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseStatementNew(RpaParser.ElseStatementNewContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseIfOld}
	 * labeled alternative in {@link RpaParser#elseIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfOld(RpaParser.ElseIfOldContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elseIfNew}
	 * labeled alternative in {@link RpaParser#elseIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfNew(RpaParser.ElseIfNewContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operationStatementAssign}
	 * labeled alternative in {@link RpaParser#operationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationStatementAssign(RpaParser.OperationStatementAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operationStatementAssignIncrease}
	 * labeled alternative in {@link RpaParser#operationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationStatementAssignIncrease(RpaParser.OperationStatementAssignIncreaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operationStatementAssignDecrease}
	 * labeled alternative in {@link RpaParser#operationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationStatementAssignDecrease(RpaParser.OperationStatementAssignDecreaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operationStatementSimple}
	 * labeled alternative in {@link RpaParser#operationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationStatementSimple(RpaParser.OperationStatementSimpleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationSubtraction}
	 * labeled alternative in {@link RpaParser#mathOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationSubtraction(RpaParser.MathOperationSubtractionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationSum}
	 * labeled alternative in {@link RpaParser#mathOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationSum(RpaParser.MathOperationSumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationAtomStatement}
	 * labeled alternative in {@link RpaParser#mathOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationAtomStatement(RpaParser.MathOperationAtomStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationExponent}
	 * labeled alternative in {@link RpaParser#mathOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationExponent(RpaParser.MathOperationExponentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationDivision}
	 * labeled alternative in {@link RpaParser#mathOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationDivision(RpaParser.MathOperationDivisionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationMultiplication}
	 * labeled alternative in {@link RpaParser#mathOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationMultiplication(RpaParser.MathOperationMultiplicationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationParenthesis}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationParenthesis(RpaParser.MathOperationParenthesisContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationMnemonic}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationMnemonic(RpaParser.MathOperationMnemonicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationUnaryOperation}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationUnaryOperation(RpaParser.MathOperationUnaryOperationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationPositiveInteger}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationPositiveInteger(RpaParser.MathOperationPositiveIntegerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationPositiveFloat}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationPositiveFloat(RpaParser.MathOperationPositiveFloatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationNegativeInteger}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationNegativeInteger(RpaParser.MathOperationNegativeIntegerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathOperationNegativeFloat}
	 * labeled alternative in {@link RpaParser#mathAtomStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperationNegativeFloat(RpaParser.MathOperationNegativeFloatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sqrt}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSqrt(RpaParser.SqrtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sqr}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSqr(RpaParser.SqrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tan}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTan(RpaParser.TanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sen}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSen(RpaParser.SenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cos}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCos(RpaParser.CosContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ln}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLn(RpaParser.LnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code log}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLog(RpaParser.LogContext ctx);
	/**
	 * Visit a parse tree produced by the {@code abs}
	 * labeled alternative in {@link RpaParser#unaryOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbs(RpaParser.AbsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifOperationAssign}
	 * labeled alternative in {@link RpaParser#ifWithOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfOperationAssign(RpaParser.IfOperationAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifOperationAssignIncrease}
	 * labeled alternative in {@link RpaParser#ifWithOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfOperationAssignIncrease(RpaParser.IfOperationAssignIncreaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifOperationAssignDecrease}
	 * labeled alternative in {@link RpaParser#ifWithOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfOperationAssignDecrease(RpaParser.IfOperationAssignDecreaseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifOperationSimple}
	 * labeled alternative in {@link RpaParser#ifWithOperationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfOperationSimple(RpaParser.IfOperationSimpleContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#operationFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationFormat(RpaParser.OperationFormatContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(RpaParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#transparent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransparent(RpaParser.TransparentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oldLoop}
	 * labeled alternative in {@link RpaParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldLoop(RpaParser.OldLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oldLoopWithOrder}
	 * labeled alternative in {@link RpaParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldLoopWithOrder(RpaParser.OldLoopWithOrderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newLoop}
	 * labeled alternative in {@link RpaParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewLoop(RpaParser.NewLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newLoopWithOrder}
	 * labeled alternative in {@link RpaParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewLoopWithOrder(RpaParser.NewLoopWithOrderContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#oldLoopPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldLoopPrefix(RpaParser.OldLoopPrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#newLoopPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewLoopPrefix(RpaParser.NewLoopPrefixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code loopLimitPositiveInteger}
	 * labeled alternative in {@link RpaParser#loopLimit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopLimitPositiveInteger(RpaParser.LoopLimitPositiveIntegerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code loopLimitMnemonic}
	 * labeled alternative in {@link RpaParser#loopLimit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopLimitMnemonic(RpaParser.LoopLimitMnemonicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code loopLimitMathOperation}
	 * labeled alternative in {@link RpaParser#loopLimit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopLimitMathOperation(RpaParser.LoopLimitMathOperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#loopSingleMnemonicOrder}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopSingleMnemonicOrder(RpaParser.LoopSingleMnemonicOrderContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#breakLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakLoop(RpaParser.BreakLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oldLoopEnd}
	 * labeled alternative in {@link RpaParser#loopEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldLoopEnd(RpaParser.OldLoopEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newLoopEnd}
	 * labeled alternative in {@link RpaParser#loopEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewLoopEnd(RpaParser.NewLoopEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oldInlineLoop}
	 * labeled alternative in {@link RpaParser#inlineLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldInlineLoop(RpaParser.OldInlineLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oldInlineLoopWithOrder}
	 * labeled alternative in {@link RpaParser#inlineLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldInlineLoopWithOrder(RpaParser.OldInlineLoopWithOrderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newInlineLoop}
	 * labeled alternative in {@link RpaParser#inlineLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewInlineLoop(RpaParser.NewInlineLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newInlineLoopWithOrder}
	 * labeled alternative in {@link RpaParser#inlineLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewInlineLoopWithOrder(RpaParser.NewInlineLoopWithOrderContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#oldInlineLoopPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldInlineLoopPrefix(RpaParser.OldInlineLoopPrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#oldInlineLoopSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOldInlineLoopSuffix(RpaParser.OldInlineLoopSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#newInlineLoopPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewInlineLoopPrefix(RpaParser.NewInlineLoopPrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#newInlineLoopSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewInlineLoopSuffix(RpaParser.NewInlineLoopSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#inlineLoopEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineLoopEnd(RpaParser.InlineLoopEndContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoin(RpaParser.JoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#joinLeftPart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinLeftPart(RpaParser.JoinLeftPartContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#joinRightPart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinRightPart(RpaParser.JoinRightPartContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#joinValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinValue(RpaParser.JoinValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#joinEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoinEnd(RpaParser.JoinEndContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#unicode_write_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnicode_write_statement(RpaParser.Unicode_write_statementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code readRegisterIntoTOTStatement}
	 * labeled alternative in {@link RpaParser#readRegisterStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadRegisterIntoTOTStatement(RpaParser.ReadRegisterIntoTOTStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code readRegisterIntoSTRStatement}
	 * labeled alternative in {@link RpaParser#readRegisterStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadRegisterIntoSTRStatement(RpaParser.ReadRegisterIntoSTRStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#includeFileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncludeFileStatement(RpaParser.IncludeFileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#length_mnemonic_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLength_mnemonic_statement(RpaParser.Length_mnemonic_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#require_string_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRequire_string_statement(RpaParser.Require_string_statementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code writeStringStatementConstant}
	 * labeled alternative in {@link RpaParser#writeStringStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWriteStringStatementConstant(RpaParser.WriteStringStatementConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code writeStringStatementMnemonic}
	 * labeled alternative in {@link RpaParser#writeStringStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWriteStringStatementMnemonic(RpaParser.WriteStringStatementMnemonicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code concatTwoMnemonic}
	 * labeled alternative in {@link RpaParser#concatStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatTwoMnemonic(RpaParser.ConcatTwoMnemonicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code concatMnemonicString}
	 * labeled alternative in {@link RpaParser#concatStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatMnemonicString(RpaParser.ConcatMnemonicStringContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#showEmptyMnemonicStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShowEmptyMnemonicStatement(RpaParser.ShowEmptyMnemonicStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#hideEmptyMnemonicStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHideEmptyMnemonicStatement(RpaParser.HideEmptyMnemonicStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#numberPrecisionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberPrecisionStatement(RpaParser.NumberPrecisionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#recordDataStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordDataStatement(RpaParser.RecordDataStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#newsynStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewsynStatement(RpaParser.NewsynStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#enableRtlStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnableRtlStatement(RpaParser.EnableRtlStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RpaParser#disableRtlStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisableRtlStatement(RpaParser.DisableRtlStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicSimple}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicSimple(RpaParser.MnemonicSimpleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicIndexed}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicIndexed(RpaParser.MnemonicIndexedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicDirectJoin}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicDirectJoin(RpaParser.MnemonicDirectJoinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicMultiConversion}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicMultiConversion(RpaParser.MnemonicMultiConversionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicFormatted}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicFormatted(RpaParser.MnemonicFormattedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicWithDomain}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicWithDomain(RpaParser.MnemonicWithDomainContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mnemonicWithFunction}
	 * labeled alternative in {@link RpaParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonicWithFunction(RpaParser.MnemonicWithFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatANumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatANumber(RpaParser.ChangeFormatANumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatANumberDotNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatANumberDotNumber(RpaParser.ChangeFormatANumberDotNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatAInterval}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatAInterval(RpaParser.ChangeFormatAIntervalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatTNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatTNumber(RpaParser.ChangeFormatTNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatT}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatT(RpaParser.ChangeFormatTContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatLInterval}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatLInterval(RpaParser.ChangeFormatLIntervalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatLNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatLNumber(RpaParser.ChangeFormatLNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatINumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatINumber(RpaParser.ChangeFormatINumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatPNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatPNumber(RpaParser.ChangeFormatPNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatENumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatENumber(RpaParser.ChangeFormatENumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatNNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatNNumber(RpaParser.ChangeFormatNNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatN}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatN(RpaParser.ChangeFormatNContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatZNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatZNumber(RpaParser.ChangeFormatZNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatFNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatFNumber(RpaParser.ChangeFormatFNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatD}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatD(RpaParser.ChangeFormatDContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatUNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatUNumber(RpaParser.ChangeFormatUNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code changeFormatCNumber}
	 * labeled alternative in {@link RpaParser#changeFormat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChangeFormatCNumber(RpaParser.ChangeFormatCNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatConversionL}
	 * labeled alternative in {@link RpaParser#formatConversion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatConversionL(RpaParser.FormatConversionLContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatConversionLN}
	 * labeled alternative in {@link RpaParser#formatConversion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatConversionLN(RpaParser.FormatConversionLNContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatConversionLO}
	 * labeled alternative in {@link RpaParser#formatConversion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatConversionLO(RpaParser.FormatConversionLOContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatConversionN}
	 * labeled alternative in {@link RpaParser#formatConversion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatConversionN(RpaParser.FormatConversionNContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatConversionX}
	 * labeled alternative in {@link RpaParser#formatConversion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatConversionX(RpaParser.FormatConversionXContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainMoney}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainMoney(RpaParser.FormatDomainMoneyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainMoneyPOS}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainMoneyPOS(RpaParser.FormatDomainMoneyPOSContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainTel}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainTel(RpaParser.FormatDomainTelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainTellAll}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainTellAll(RpaParser.FormatDomainTellAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainFax}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainFax(RpaParser.FormatDomainFaxContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainFaxAll}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainFaxAll(RpaParser.FormatDomainFaxAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainSN}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainSN(RpaParser.FormatDomainSNContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainKmCippo}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainKmCippo(RpaParser.FormatDomainKmCippoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainTipSS}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainTipSS(RpaParser.FormatDomainTipSSContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainCodSS}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainCodSS(RpaParser.FormatDomainCodSSContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainNumSS}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainNumSS(RpaParser.FormatDomainNumSSContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainProgUff}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainProgUff(RpaParser.FormatDomainProgUffContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainProgCIP}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainProgCIP(RpaParser.FormatDomainProgCIPContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainProgRel}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainProgRel(RpaParser.FormatDomainProgRelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainProgRif}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainProgRif(RpaParser.FormatDomainProgRifContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainDataElda}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainDataElda(RpaParser.FormatDomainDataEldaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainCustomDate}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainCustomDate(RpaParser.FormatDomainCustomDateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainExecuteSQL}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainExecuteSQL(RpaParser.FormatDomainExecuteSQLContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainExecuteSQLC1}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainExecuteSQLC1(RpaParser.FormatDomainExecuteSQLC1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXSQLC}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXSQLC(RpaParser.FormatDomainXSQLCContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXSQLCC1}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXSQLCC1(RpaParser.FormatDomainXSQLCC1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXSQLCCT}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXSQLCCT(RpaParser.FormatDomainXSQLCCTContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXDist}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXDist(RpaParser.FormatDomainXDistContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXS2N}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXS2N(RpaParser.FormatDomainXS2NContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXExecAgg}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXExecAgg(RpaParser.FormatDomainXExecAggContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXDataOra}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXDataOra(RpaParser.FormatDomainXDataOraContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXTime}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXTime(RpaParser.FormatDomainXTimeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXOra}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXOra(RpaParser.FormatDomainXOraContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXMinuti}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXMinuti(RpaParser.FormatDomainXMinutiContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fomatDomainXSecondi}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFomatDomainXSecondi(RpaParser.FomatDomainXSecondiContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXSubStr}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXSubStr(RpaParser.FormatDomainXSubStrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXSubStrInterval}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXSubStrInterval(RpaParser.FormatDomainXSubStrIntervalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXPar}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXPar(RpaParser.FormatDomainXParContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXDay}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXDay(RpaParser.FormatDomainXDayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXDataF1}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXDataF1(RpaParser.FormatDomainXDataF1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXDataF2}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXDataF2(RpaParser.FormatDomainXDataF2Context ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXUPR}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXUPR(RpaParser.FormatDomainXUPRContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainXImg}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainXImg(RpaParser.FormatDomainXImgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainExtImg}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainExtImg(RpaParser.FormatDomainExtImgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainLowText}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainLowText(RpaParser.FormatDomainLowTextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainCapText}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainCapText(RpaParser.FormatDomainCapTextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainCapAllText}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainCapAllText(RpaParser.FormatDomainCapAllTextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainUpperText}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainUpperText(RpaParser.FormatDomainUpperTextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainHashFile}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainHashFile(RpaParser.FormatDomainHashFileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formatDomainFindHash}
	 * labeled alternative in {@link RpaParser#formatDomain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatDomainFindHash(RpaParser.FormatDomainFindHashContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixFunctionCO}
	 * labeled alternative in {@link RpaParser#prefixFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixFunctionCO(RpaParser.PrefixFunctionCOContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixFunctionUL}
	 * labeled alternative in {@link RpaParser#prefixFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixFunctionUL(RpaParser.PrefixFunctionULContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixFunctionPR}
	 * labeled alternative in {@link RpaParser#prefixFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixFunctionPR(RpaParser.PrefixFunctionPRContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixFunctionNO}
	 * labeled alternative in {@link RpaParser#prefixFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixFunctionNO(RpaParser.PrefixFunctionNOContext ctx);
}