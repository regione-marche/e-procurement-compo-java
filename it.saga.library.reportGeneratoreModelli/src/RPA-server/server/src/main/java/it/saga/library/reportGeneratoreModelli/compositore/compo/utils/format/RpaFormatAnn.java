package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaNotExistValueNumberException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatAnn extends RpaFormat {

    private String  value;
    private String  formattedValue;
    private int     countDigits;

    public RpaFormatAnn(RpaMainCompositore mainCompositore, String value, int countDigits) {

        super(mainCompositore);
        this.value          = value;
        this.countDigits    = countDigits;
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

        String code     = "[A" + countDigits + "]";
        String message  = "Il formato [Ann] non ha un valore numerico";
        int context     = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaNotExistValueNumberException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

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

        // Controllo se la lunghezza del valore è maggiore di quella definita
        else if (value.length() > countDigits) {

            // Taglio la stringa
            formattedValue = value.substring(0, countDigits);

        }
        else {

            // Salvo il valore senza modificarlo
            formattedValue = value;

        }

    }

}
