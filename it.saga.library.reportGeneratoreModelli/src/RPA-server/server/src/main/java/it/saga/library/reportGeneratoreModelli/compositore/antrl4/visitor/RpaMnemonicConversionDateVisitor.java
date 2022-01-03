package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMnemonicConversionException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDataConversionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_00_0a;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_00_0n;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_00_aa;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_00_nn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_AA_003;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_Aa_002;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_aa_00;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_aa_aa;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_nn_00;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_nn_0nI;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_0_nn_nnI;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_A_00_002;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_a_00_00;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_a_aa_aa;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_00_00;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_0A_nn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_0a_nn2;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_A0_nn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_AA_nn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_Aa_nn2;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_aa_00;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_aa_0a;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_aa_0n;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_aa_aa;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_aa_nn3;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_nn_0nI;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_nn_0nU;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_nn_nn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_nn_nnI;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatD_n_nn_nnU;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatDate;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatDateElda;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatXDataF1;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatXDataF2;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatXDay;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatXMinuti;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatXOra;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatXTime;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_00_0aContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_00_0nContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_00_aaContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_00_nnContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_AA_003Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_Aa_002Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_aa_001Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_aa_aaContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_nn_00Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_nn_0nIContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_0_nn_nnIContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_A_00_002Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_a_00_001Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_a_aa_aaContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_00_00Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_0A_nn1Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_0a_nn2Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_A0_nnContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_AA_nn1Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_Aa_nn2Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_aa_00Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_aa_0aContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_aa_0nContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_aa_aaContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_aa_nn3Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_nn_0nIContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_nn_0nUContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_nn_nnContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_nn_nnIContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatD_n_nn_nnUContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainCustomDateContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainDataEldaContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXDataF1Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXDataF2Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXDayContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXMinutiContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXOraContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXTimeContext;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaMnemonicConversionDateVisitor extends RpaSqlVisitor {

    // Link: https://regex101.com/r/qN6pQq/1
    private static final String DATE_REGEX = "^([0-9]{2})[^0-9]([0-9]{2})[^0-9]([0-9]{4})$";

    private String valueToCustomFormat;

    public RpaMnemonicConversionDateVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

        super(conn, scope, mainCompositore, parentNode, childNode);

    }

    // TODO: Se ho un mnemonico la cui data è da formattare, passare al formato il ".getPrintedValue()"

    @Override
    public RpaValue<RpaFormat> visitFormatDomainDataElda(FormatDomainDataEldaContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia una data valida
        if (isValidDate(mnemonic.getPrintedValue()) || RpaNumberUtils.isInteger(mnemonic.getPrintedValue())) {

            // Formatto il dato a data
            RpaFormat customValueFormat = new RpaFormatDateElda(mainCompositore, mnemonic.getPrintedValue());

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return new RpaValue<RpaFormat>(customValueFormat);

        } else {

            // Lancio un errore
            String          errorCode           = context.getParent().getText();
            String          errorMessage        = "Il dato da convertire non è una data";
            RpaErrorType    errorType           = RpaErrorType.CONVERSION_FAIL_DATA_ELDA;
            int             applicationContext  = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainXTime(FormatDomainXTimeContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia un TIMESTAMP valido (non vuoto)
        if (mnemonic.getType() == RpaMnemonic.TYPE_TIMESTAMP) {

            // Formatto il valore del mnemonico
            valueToCustomFormat                     = mnemonic.getValue();
            RpaFormatDate           formatDate      = new RpaFormatXTime(mainCompositore, valueToCustomFormat);
            RpaValue<RpaFormatDate> formatDateValue = new RpaValue<RpaFormatDate>(formatDate);

            // Salvo il valore formattato
            dataConversionContainer.setCustomValueFormat(formatDate);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return formatDateValue;

        } else {

            // Lancio un errore
            String      errorCode       = context.getParent().getText();
            String      errorMessage    = "Il dato da convertire non è un Timestamp";
            RpaErrorType errorType      = RpaErrorType.CONVERSION_FAIL_CUSTOM_DATE;
            int applicationContext      = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainXDay(FormatDomainXDayContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia una data valida
        if (isValidDate(mnemonic.getPrintedValue()) || RpaNumberUtils.isInteger(mnemonic.getPrintedValue())) {

            // Formatto il dato a data
            RpaFormatDate customValueFormat = new RpaFormatXDay(mainCompositore, mnemonic.getPrintedValue());

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return new RpaValue<RpaFormatDate>(customValueFormat);

        } else {

            // Lancio un errore
            String          errorCode           = context.getParent().getText();
            String          errorMessage        = "Il dato da convertire non è una data";
            RpaErrorType    errorType           = RpaErrorType.CONVERSION_FAIL_DATA_ELDA;
            int             applicationContext  = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainXOra(FormatDomainXOraContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia un TIMESTAMP valido (non vuoto)
        if (mnemonic.getType() == RpaMnemonic.TYPE_TIMESTAMP) {

            // Formatto il valore del mnemonico
            valueToCustomFormat                     = mnemonic.getValue();
            RpaFormatDate           formatDate      = new RpaFormatXOra(mainCompositore, valueToCustomFormat);
            RpaValue<RpaFormatDate> formatDateValue = new RpaValue<RpaFormatDate>(formatDate);

            // Salvo il valore formattato
            dataConversionContainer.setCustomValueFormat(formatDate);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return formatDateValue;

        } else {

            // Lancio un errore
            String      errorCode       = context.getParent().getText();
            String      errorMessage    = "Il dato da convertire non è un Timestamp";
            RpaErrorType errorType      = RpaErrorType.CONVERSION_FAIL_CUSTOM_DATE;
            int applicationContext      = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainXMinuti(FormatDomainXMinutiContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia un TIMESTAMP valido (non vuoto)
        if (mnemonic.getType() == RpaMnemonic.TYPE_TIMESTAMP) {

            // Formatto il valore del mnemonico
            valueToCustomFormat                     = mnemonic.getValue();
            RpaFormatDate           formatDate      = new RpaFormatXMinuti(mainCompositore, valueToCustomFormat);
            RpaValue<RpaFormatDate> formatDateValue = new RpaValue<RpaFormatDate>(formatDate);

            // Salvo il valore formattato
            dataConversionContainer.setCustomValueFormat(formatDate);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return formatDateValue;

        } else {

            // Lancio un errore
            String      errorCode       = context.getParent().getText();
            String      errorMessage    = "Il dato da convertire non è un Timestamp";
            RpaErrorType errorType      = RpaErrorType.CONVERSION_FAIL_CUSTOM_DATE;
            int applicationContext      = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainXDataF1(FormatDomainXDataF1Context context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia una data valida
        if (isValidDate(mnemonic.getPrintedValue()) || RpaNumberUtils.isInteger(mnemonic.getPrintedValue())) {

            // Formatto il dato a data
            RpaFormatDate customValueFormat = new RpaFormatXDataF1(mainCompositore, mnemonic.getPrintedValue());

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return new RpaValue<RpaFormatDate>(customValueFormat);

        } else {

            // Lancio un errore
            String          errorCode           = context.getParent().getText();
            String          errorMessage        = "Il dato da convertire non è una data";
            RpaErrorType    errorType           = RpaErrorType.CONVERSION_FAIL_DATA_ELDA;
            int             applicationContext  = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainXDataF2(FormatDomainXDataF2Context context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia una data valida
        if (isValidDate(mnemonic.getPrintedValue()) || RpaNumberUtils.isInteger(mnemonic.getPrintedValue())) {

            // Formatto il dato a data
            RpaFormatDate customValueFormat = new RpaFormatXDataF2(mainCompositore, mnemonic.getPrintedValue());

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return new RpaValue<RpaFormatDate>(customValueFormat);

        } else {

            // Lancio un errore
            String          errorCode           = context.getParent().getText();
            String          errorMessage        = "Il dato da convertire non è una data";
            RpaErrorType    errorType           = RpaErrorType.CONVERSION_FAIL_DATA_ELDA;
            int             applicationContext  = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatDomainCustomDate(FormatDomainCustomDateContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        // Controllo che il valore sia una data valida
        if (isValidDate(mnemonic.getPrintedValue()) || RpaNumberUtils.isInteger(mnemonic.getPrintedValue())) {

            // Passo il valore al formattatore tramite visit su "domdatecustom"
            valueToCustomFormat = mnemonic.getPrintedValue();
            RpaValue<RpaFormatDate> formatDateValue = (RpaValue<RpaFormatDate>) visit(context.domdatecustom());
            valueToCustomFormat = null;

            // Salvo il valore formattato
            dataConversionContainer.setCustomValueFormat(formatDateValue.getValue());
            dataConversionContainer.setType(RpaFormat.TYPE_DATE);

            return new RpaValue<RpaFormatDate>(formatDateValue.getValue());

        } else {

            // Lancio un errore
            String      errorCode       = context.getParent().getText();
            String      errorMessage    = "Il dato da convertire non è una data";
            RpaErrorType errorType      = RpaErrorType.CONVERSION_FAIL_CUSTOM_DATE;
            int applicationContext      = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_0A_nn1(FormatD_n_0A_nn1Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_0A_nn(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_A0_nn(FormatD_n_A0_nnContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_A0_nn(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_0a_nn2(FormatD_n_0a_nn2Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_0a_nn2(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_AA_nn1(FormatD_n_AA_nn1Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_AA_nn(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_Aa_nn2(FormatD_n_Aa_nn2Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_Aa_nn2(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_aa_nn3(FormatD_n_aa_nn3Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_aa_nn3(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_aa_0n(FormatD_n_aa_0nContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_aa_0n(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_aa_00(FormatD_n_aa_00Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_aa_00(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_aa_0a(FormatD_n_aa_0aContext ctx) {

        RpaFormatDate formatDate = new RpaFormatD_n_aa_0a(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_a_aa_aa(FormatD_a_aa_aaContext context) {

        RpaFormatDate formatDate = new RpaFormatD_a_aa_aa(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_aa_aa(FormatD_0_aa_aaContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_aa_aa(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_aa_aa(FormatD_n_aa_aaContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_aa_aa(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_00_aa(FormatD_0_00_aaContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_00_aa(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_00_0a(FormatD_0_00_0aContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_00_0a(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_00_0n(FormatD_0_00_0nContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_00_0n(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_00_nn(FormatD_0_00_nnContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_00_nn(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_aa_001(FormatD_0_aa_001Context context) {

        RpaFormatDate formatDate = new RpaFormatD_0_aa_00(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_Aa_002(FormatD_0_Aa_002Context context) {

        RpaFormatDate formatDate = new RpaFormatD_0_Aa_002(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_AA_003(FormatD_0_AA_003Context context) {

        RpaFormatDate formatDate = new RpaFormatD_0_AA_003(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_00_00(FormatD_n_00_00Context context) {

        RpaFormatDate formatDate = new RpaFormatD_n_00_00(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_a_00_001(FormatD_a_00_001Context context) {

        RpaFormatDate formatDate = new RpaFormatD_a_00_00(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_A_00_002(FormatD_A_00_002Context context) {

        RpaFormatDate formatDate = new RpaFormatD_A_00_002(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_nn_nn(FormatD_n_nn_nnContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_nn_nn(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_nn_0nI(FormatD_n_nn_0nIContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_nn_0nI(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_nn_0nI(FormatD_0_nn_0nIContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_nn_0nI(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_nn_nnI(FormatD_n_nn_nnIContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_nn_nnI(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_nn_nnI(FormatD_0_nn_nnIContext context) {

        RpaFormatDate formatDate = new RpaFormatD_0_nn_nnI(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_nn_nnU(FormatD_n_nn_nnUContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_nn_nnU(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_n_nn_0nU(FormatD_n_nn_0nUContext context) {

        RpaFormatDate formatDate = new RpaFormatD_n_nn_0nU(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    @Override
    public RpaValue<RpaFormatDate> visitFormatD_0_nn_00(FormatD_0_nn_00Context context) {

        RpaFormatDate formatDate = new RpaFormatD_0_nn_00(mainCompositore, valueToCustomFormat);

        return new RpaValue<RpaFormatDate>(formatDate);

    }

    private boolean isValidDate(String dateString) {

        DateFormat  myDateFormat    = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date        date            = null;

        // TEST date da numeri
        /*
        if (RpaNumberUtils.isInteger(dateString)) {

            String newDateString = dateString;

            if (RpaNumberUtils.isIntegerWithDotZero(dateString)) {

                newDateString = RpaNumberUtils.integerWithoutDotZero(dateString);

            }

            Integer days = Integer.valueOf(newDateString);

            try {

                DateFormat  dateFormat              = new SimpleDateFormat("dd.MM.yyyy");
                Date        dateWithExtraDays       = dateFormat.parse("01.01.1000");
                Calendar    calendarWithExtraDays   = Calendar.getInstance();

                calendarWithExtraDays.setTime(dateWithExtraDays);
                calendarWithExtraDays.add(Calendar.DATE, days);

            } catch (ParseException exception) {

                return false;

            }

        }
        */

        try {

            date = myDateFormat.parse(dateString);

        } catch (ParseException exception) { }

        // Controllo che la stringa formi un data
        Matcher matcher = Pattern.compile(DATE_REGEX).matcher(dateString);

        if (!matcher.find()) {

            return false;

        }

        // Controllo che i valori di giorno, mese e anno siano validi
        String day      = matcher.group(1);
        String month    = matcher.group(2);
        String year     = matcher.group(3);

        String formattedDateString = day + "-" + month + "-" + year;

        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        dateFormat.setLenient(false);

        try {

            dateFormat.parse(formattedDateString);

            return true;

        } catch (ParseException exception) {

            return false;

        }

    }

}
