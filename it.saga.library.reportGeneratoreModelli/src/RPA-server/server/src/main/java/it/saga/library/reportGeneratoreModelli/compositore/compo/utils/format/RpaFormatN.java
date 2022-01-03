package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatN extends RpaFormat {

    String value;
    int mnemonicType;

    public RpaFormatN(RpaMainCompositore mainCompositore, String value, int mnemonicType) {

        super(mainCompositore);
        this.value = value;
        this.mnemonicType = mnemonicType;

    }

    @Override
    public String getPrintedValue() {

        return getPrintedFormattedValue();

    }

    @Override
    public String getValueAssignment() {

        return getFormattedValue();

    }

    @Override
    public String getComparisonValue() {

        if (value == null) {

            return null;

        } else {

            return getFormattedValue();

        }

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_NUMBER;

    }

    @Override
    public Number getValueNumber() {

        if (value == null) {

            return null;

        } else {

            return Integer.valueOf(value);

        }

    }

    public String getFormattedValue() {

        // Controllo il tipo del mnemonico

        // Controllo se il valore è null
        if (value == null) {

            return "0";

        }

        // Controllo se il valore è una stringa vuota
        else if (value.isEmpty()) {

            return "0";

        }

        // Controllo se il valore non è un numero
        else if (!RpaNumberUtils.isInteger(value) && !RpaNumberUtils.isDouble(value)) {

            return "0";

        }

        // Controllo se il valore è un double
        else if (RpaNumberUtils.isDouble(value)) {

            Double  doubleNumber    = Double.valueOf(value);
            Integer integerNumber   = doubleNumber.intValue();

            return integerNumber.toString();

        }

        // Negli altri casi, ho un intero
        else {

            return value;

        }

    }

    private String getPrintedFormattedValue() {

        // Controllo il tipo del mnemonico

        // Controllo se il valore è null
        if (value == null) {

            return emptyPrintedValue();

        }

        // Controllo se il valore è una stringa vuota
        else if (value.isEmpty()) {

            return emptyPrintedValue();

        }

        // Controllo se il valore non è un numero
        else if (!RpaNumberUtils.isInteger(value) && !RpaNumberUtils.isDouble(value)) {

            return emptyPrintedValue();

        }

        // Controllo se il valore è un double
        else if (RpaNumberUtils.isDouble(value)) {

            Double  doubleNumber    = Double.valueOf(value);
            Integer integerNumber   = doubleNumber.intValue();

            return integerNumber.toString();

        }

        // Negli altri casi, ho un intero
        else {

            return value;

        }

    }

}
