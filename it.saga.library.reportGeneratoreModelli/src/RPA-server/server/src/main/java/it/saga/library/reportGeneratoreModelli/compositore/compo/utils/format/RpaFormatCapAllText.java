package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaTextConversionFailException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaFormatCapAllText extends RpaFormat {

    // Link: https://regex101.com/r/aa60sD/1
    private static final String SPACE_SUFFIX_REGEX = " ([^ ])";

    private String  value;
    private String  formattedValue;

    public RpaFormatCapAllText(RpaMainCompositore mainCompositore, String value) {

        super(mainCompositore);
        this.value = value;
        formatValue();

    }

    @Override
    public String getPrintedValue() {

        if (formattedValue == null) {

            return emptyPrintedValue();

        } else {

            return formattedValue;

        }

    }

    @Override
    public String getValueAssignment() {

        return formattedValue;

    }

    @Override
    public Number getValueNumber() {

        String code     = "{CAPALLTEXT}";
        String message  = "Il formato {CAPALLTEXT} non ha un valore numerico";
        int context     = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaTextConversionFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

    }

    @Override
    public String getComparisonValue() {

        return formattedValue;

    }

    @Override
    public String getFormattedValue() {

        return formattedValue;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    private void formatValue() {

        // Controllo se il valore è vuoto
        if (value == null || value.isEmpty()) {

            formattedValue = value;

        }

        // Se ho un solo carattere, lo restituisco in "maiuscolo"
        if (value.length() == 1) {

            formattedValue = value.toUpperCase();

        }

        // Altrimenti, converto a "capitalize" tutte le parole del testo
        else {

            StringBuffer stringBuffer = new StringBuffer(value.toLowerCase());
            stringBuffer = stringBuffer.replace(0, 1, stringBuffer.substring(0, 1).toUpperCase());
            Matcher matcher = Pattern.compile(SPACE_SUFFIX_REGEX).matcher(value);

            while (matcher.find()) {

                String upperLetter = matcher.group(1).toUpperCase();
                stringBuffer = stringBuffer.replace(matcher.start(), matcher.end(), " " + upperLetter);

            }

            formattedValue = stringBuffer.toString();

        }

    }

}
