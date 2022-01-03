package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaTextConversionFailException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatCapText extends RpaFormat {

    private String  value;
    private String  formattedValue;

    public RpaFormatCapText(RpaMainCompositore mainCompositore, String value) {

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

        String code     = "{CAPTEXT}";
        String message  = "Il formato {CAPTEXT} non ha un valore numerico";
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

        // Altrimenti, converto a "capitalize" il testo
        else {

            formattedValue = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();

        }

    }

}
