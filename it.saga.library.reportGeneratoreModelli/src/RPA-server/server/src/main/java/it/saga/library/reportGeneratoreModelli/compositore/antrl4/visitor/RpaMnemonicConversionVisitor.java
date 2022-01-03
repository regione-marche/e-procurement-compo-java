package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidFormatLimitsException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMnemonicConversionException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDataConversionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatAInterval;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatAnn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatCapAllText;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatCapText;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatCnn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatD;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatEnn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatExtImg;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatFnn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatInn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatKmCippo;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatL;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatLInterval;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatLN;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatLowText;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatMoney;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatN;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatNn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatNumber;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatPnn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatX;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatXImg;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatXParameter;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatZnn;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatAIntervalContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatANumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatCNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatDContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatENumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatFNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatINumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatLIntervalContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatNContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatNNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatPNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ChangeFormatZNumberContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatConversionLContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatConversionLNContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatConversionNContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatConversionXContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainExtImgContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainMoneyContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXImgContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXParContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainKmCippoContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainLowTextContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainCapTextContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainCapAllTextContext;
import it.saga.library.reportGeneratoreModelli.compositore.interfaces.RpaImportExternalImageI;

import java.sql.Connection;
import java.text.ParseException;
import java.util.InvalidPropertiesFormatException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaMnemonicConversionVisitor extends RpaMnemonicConversionDateVisitor {

    // Link: https://regex101.com/r/nSDNGW/1
    private static final String PARAMETER_EXTRACTION_REGEX = "^X_PAR_([A-Za-z0-9_]+) *}$";

    public RpaMnemonicConversionVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

        super(conn, scope, mainCompositore, parentNode, childNode);

    }

    /**
     * Esempio: #DESURP.N#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatConversionN(FormatConversionNContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic         mnemonic                = dataConversionContainer.getMnemonic();

        debugMessages.print(mnemonic.getPrintedValue());

        // Controllo che il mnemonico esista e che sia un tabulato
        // if (mnemonic != null && mnemonic.getType() == AbstractMnemonic.TYPE_TABULATION) {
        if (mnemonic != null) {

            // Formatto il dato a intero
            RpaFormat customValueFormat = new RpaFormatN(mainCompositore, value, mnemonic.getType());

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        } else {

            // Lancio un errore
            String          errorCode           = context.getParent().getText();
            String          errorMessage        = "Il mnemonico non è un tabulato o un mnemonico con numero da cui estrarre l'indice";
            RpaErrorType    errorType           = RpaErrorType.CONVERSION_FAIL_TABULATION_INDEX;
            int             applicationContext  = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

    }

    /**
     * Esempio: #CFUTE_BIS_3[N]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatN(ChangeFormatNContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il dato a intero
        RpaFormat customValueFormat = new RpaFormatNumber(mainCompositore, value);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_INT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[F11.39]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatFNumber(ChangeFormatFNumberContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Formatto il dato in double
        String      format              = context.FORMAT_SUFFIX_FDOT().getText();
        RpaFormat   customValueFormat   = new RpaFormatFnn(mainCompositore, value, format);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[N25]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatNNumber(ChangeFormatNNumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Formatto il dato a intero con spazi
        String      format              = context.POSITIVE_INTEGER().getText();
        RpaFormat   customValueFormat   = new RpaFormatNn(mainCompositore, value, Integer.valueOf(format));

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_INT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[D]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatD(ChangeFormatDContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Formatto il dato a data
        RpaFormat customValueFormat;

        try {

            if (value == null) {

                customValueFormat = new RpaFormatD(mainCompositore, "");

            } else if (RpaNumberUtils.isInteger(value)) {

                if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                    value = RpaNumberUtils.integerWithoutDotZero(value);

                }

                customValueFormat = new RpaFormatD(mainCompositore, Integer.valueOf(value));

            } else if (RpaNumberUtils.isDouble(value)) {

                customValueFormat = new RpaFormatD(mainCompositore, Double.valueOf(value));

            } else {

                customValueFormat = new RpaFormatD(mainCompositore, value);

            }

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a data il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_DATA_D);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_DATE);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #NAPPFI{MONEY}#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatDomainMoney(FormatDomainMoneyContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il valore a valuta
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatMoney(mainCompositore, value);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a valuta il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_MONEY);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[I122]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatINumber(ChangeFormatINumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero il numero di caratteri per la stampa
        Integer countDigits;
        if (context.POSITIVE_INTEGER() == null || context.POSITIVE_INTEGER().getText() == null) {

            countDigits = null;

        } else {

            countDigits = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        }

        // Formatto il valore a 'double' con apice
        RpaFormat customValueFormat;

        try {

            if (countDigits != null) {

                customValueFormat = new RpaFormatInn(mainCompositore, value, countDigits);

            } else {

                customValueFormat = new RpaFormatInn(mainCompositore, value);

            }

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile formattare il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_I);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #IAGGIU.L#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatConversionL(FormatConversionLContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Converto il numero in parola
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatL(mainCompositore, value);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a parola il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_L);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[A120]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatANumber(ChangeFormatANumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic mnemonic                = dataConversionContainer.getMnemonic();

        if (mnemonic != null && mnemonic.getType() == RpaAbstractMnemonic.TYPE_TABULATION) {

            value = mnemonic.getPrintedValue();

        }

        // Recupero il numero di caratteri per la stampa
        int countDigits = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Ritorno i primi caratteri della stringa
        RpaFormat customValueFormat = new RpaFormatAnn(mainCompositore, value, countDigits);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);


    }

    /**
     * Esempio: #CFUTE_BIS_3[A(10:12)]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatAInterval(ChangeFormatAIntervalContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero i valori dell'intervallo
        int lowerLimit = Integer.valueOf(context.lowerLimit.getText());
        int upperLimit = Integer.valueOf(context.upperLimit.getText());

        // Ritorno i caratteri definiti dall'intervallo
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatAInterval(mainCompositore, value, lowerLimit, upperLimit);

        } catch (InvalidPropertiesFormatException exception) {

            String formatString = "[A(" + lowerLimit + ":" + upperLimit + ")]";

            String code     = context.parent.getText();
            String message  = "Nel formato " + formatString + " il limite superiore è minore del limite inferiore";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaInvalidFormatLimitsException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[L(12:30)]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatLInterval(ChangeFormatLIntervalContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero l'indice del blocco e il numero di caratteri per blocco
        int blockIndex  = Integer.valueOf(context.blockIndex.getText());
        int blockSize   = Integer.valueOf(context.blockSize.getText());

        // Ritorno i caratteri definiti dall'intervallo
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatLInterval(mainCompositore, value, blockIndex, blockSize);

        } catch (InvalidPropertiesFormatException exception) {

            String formatString = "[L(" + blockIndex + ":" + blockSize + ")]";

            String code     = context.parent.getText();
            String message  = "Nel formato " + formatString + " la grandezza del blocco è maggiore della lunghezza della stringa";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaInvalidFormatLimitsException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[P38]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatPNumber(ChangeFormatPNumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero il numero di caratteri per la stampa
        Integer countDigits;
        if (context.POSITIVE_INTEGER() == null || context.POSITIVE_INTEGER().getText() == null) {

            countDigits = null;

        } else {

            countDigits = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        }

        // Formatto il valore a 'double' con apice
        RpaFormat customValueFormat;

        try {

            if (countDigits != null) {

                customValueFormat = new RpaFormatPnn(mainCompositore, value, countDigits);

            } else {

                customValueFormat = new RpaFormatPnn(mainCompositore, value);

            }

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile formattare il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_I);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[E22]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatENumber(ChangeFormatENumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero il numero di caratteri per la stampa
        int countDigits = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Formatto il valore a 'double' con apice
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatEnn(mainCompositore, value, countDigits);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile formattare il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_I);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #CFUTE_BIS_3[Z8]#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitChangeFormatZNumber(ChangeFormatZNumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero il numero di caratteri per la stampa
        int countDigits = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Formatto il valore a 'double' con apice
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatZnn(mainCompositore, value, countDigits);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile formattare il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_I);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    @Override
    public RpaValue<RpaFormat> visitChangeFormatCNumber(ChangeFormatCNumberContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Recupero il numero di caratteri per la stampa
        int countDigits = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Formatto il valore a 'double' con arrotondamento
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatCnn(mainCompositore, value, countDigits);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile formattare il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_I);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #STR10{X_IMG}#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatDomainXImg(FormatDomainXImgContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il valore a immagine da inserire
        RpaFormat customValueFormat = new RpaFormatXImg(mainCompositore, value);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    @Override
    public RpaValue<RpaFormat> visitFormatConversionX(FormatConversionXContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();
        RpaAbstractMnemonic mnemonic                = dataConversionContainer.getMnemonic();
        int                     type                    = mnemonic.getType();

        // Formatto il valore
        RpaFormat customValueFormat = new RpaFormatX(mainCompositore, value, type);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(type);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainXPar(FormatDomainXParContext context) {

        // Estraggo il nome del parametro
        String  suffix              = context.DOMXPAR_SUFFIX_ID().getText();
        Matcher parameterMatcher    = Pattern.compile(PARAMETER_EXTRACTION_REGEX).matcher(suffix);
        parameterMatcher.find();
        String  parameter           = parameterMatcher.group(1);

        // Recupero il valore del parametro e ne creo una formattazione
        RpaFormat customValueFormat = new RpaFormatXParameter(mainCompositore, parameter);

        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);


    }

    /**
     * Esempio: #IAGGIU.LN#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatConversionLN(FormatConversionLNContext context) {

        // Recupero il valore da convertire
        // DataConversionContainer dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                  value                   = dataConversionContainer.getFormattedValue();

        // Converto il numero in parola
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatLN(mainCompositore, value);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a parola il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_NUMBER_L);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #STR15{EXT_IMG}#
     * @param context
     * @return
     */
    public RpaValue<RpaFormat> visitFormatDomainExtImg(FormatDomainExtImgContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Estraggo l'immagine
        RpaImportExternalImageI importExternalImage = mainCompositore.getImportExternalImage();

        if (importExternalImage == null) {

            // Lancio un errore
            String          errorCode           = context.getParent().getText();
            String          errorMessage        = "Non è stato definito un sistema per il recupero delle immagini";
            RpaErrorType    errorType           = RpaErrorType.CONVERSION_FAIL_TABULATION_INDEX;
            int             applicationContext  = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, errorCode, errorMessage, errorType);

        }

        byte[] imageBytes = importExternalImage.loadImage(mainCompositore, value);

        // Definisco il formato e restituisco il byte-code dell'immagine
        RpaFormat customValueFormat = new RpaFormatExtImg(mainCompositore, imageBytes);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #TOT5{KM_CIPPO}#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatDomainKmCippo(FormatDomainKmCippoContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il valore a valuta
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatKmCippo(mainCompositore, value);

        } catch (ParseException exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a Km-cippo il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_FAIL_KM_CIPPO);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_FLOAT);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #STR3{LOWTEXT}#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatDomainLowText(FormatDomainLowTextContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il valore a valuta
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatLowText(mainCompositore, value);

        } catch (Exception exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a low-text il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_LOW_TEXT);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #STR3{CAPTEXT}#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatDomainCapText(FormatDomainCapTextContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il valore a valuta
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatCapText(mainCompositore, value);

        } catch (Exception exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a capitalize il valore '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_CAP_TEXT);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    /**
     * Esempio: #STR3{CAPALLTEXT}#
     * @param context
     * @return
     */
    @Override
    public RpaValue<RpaFormat> visitFormatDomainCapAllText(FormatDomainCapAllTextContext context) {

        // Recupero il valore da convertire
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        String                      value                   = dataConversionContainer.getFormattedValue();

        // Formatto il valore a valuta
        RpaFormat customValueFormat;

        try {

            customValueFormat = new RpaFormatCapAllText(mainCompositore, value);

        } catch (Exception exception) {

            String code     = context.parent.getText();
            String message  = "Impossibile convertire a capitalize ogni parola per '" + value + "'";
            int applicationContext = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaMnemonicConversionException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), applicationContext, code, message, RpaErrorType.CONVERSION_CAP_ALL_TEXT);

        }

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

}
