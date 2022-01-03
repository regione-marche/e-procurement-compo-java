package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaNotExistValueNumberException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.util.InvalidPropertiesFormatException;

public class RpaFormatAInterval extends RpaFormat {

    private String  value;
    private String  formattedValue;
    private int     lowerLimit;
    private int     upperLimit;

    public RpaFormatAInterval(RpaMainCompositore mainCompositore, String value, int lowerLimit, int upperLimit) throws InvalidPropertiesFormatException {

        super(mainCompositore);

        if (upperLimit < lowerLimit) {

            throw new InvalidPropertiesFormatException("Il limite superiore è minore del limite inferiore");

        }

        this.value          = value;
        this.lowerLimit     = lowerLimit;
        this.upperLimit     = upperLimit;
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

        String code     = "[A(" + lowerLimit + ":" + upperLimit + ")]";
        String message  = "Il formato [A(nn:mm)] non ha un valore numerico";
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

        // Controllo il limite inferiore
        else if (lowerLimit <= value.length()) {

            int internalLowerLimit = lowerLimit - 1;
            int internalUpperLimit = Math.min(upperLimit, value.length());

            formattedValue = value.substring(internalLowerLimit, internalUpperLimit);

        } else {

            formattedValue = null;

        }

    }

}
