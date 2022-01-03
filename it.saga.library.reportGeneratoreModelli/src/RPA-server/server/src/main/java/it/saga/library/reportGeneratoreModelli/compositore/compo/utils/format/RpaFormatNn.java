package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;

public class RpaFormatNn extends RpaFormat {

    private String  value;
    private int     countIntegerDigits;
    private String  formattedValue;
    private int     formattedNumber;

    public RpaFormatNn(RpaMainCompositore mainCompositore, String value, int countIntegerDigits) {

        super(mainCompositore);
        this.value              = value;
        this.countIntegerDigits = countIntegerDigits;

        formatValue();

    }

    @Override
    public String getPrintedValue() {

        return formattedValue;

    }

    @Override
    public String getValueAssignment() {

        return formattedValue;

    }

    @Override
    public Number getValueNumber() {

        return formattedNumber;

    }

    @Override
    public String getComparisonValue() {

        return String.valueOf(formattedNumber);

    }

    @Override
    public String getFormattedValue() {

        return formattedValue;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_NUMBER;

    }

    private void formatValue() {

        // Controllo se il valore è null o vuoto
        if (value == null || value.isEmpty()) {

            formattedValue  = "0";
            formattedNumber = 0;

        }

        // Controllo se il valore è un numero
        else if (!RpaNumberUtils.isDouble(value) && !RpaNumberUtils.isInteger(value)) {

            formattedValue  = "0";
            formattedNumber = 0;

        }

        else {

            // Estraggo la parte intera del numero
            BigDecimal  bigDecimal = new BigDecimal(value);
            String      integerPart;

            if (RpaNumberUtils.isInteger(value)) {

                integerPart = bigDecimal.toBigInteger().toString();

            } else {

                integerPart = bigDecimal.toBigInteger().toString();

            }

            // Controllo se il numero di cifre intere supera la soglia definita
            if (integerPart.length() > countIntegerDigits) {

                formattedValue = "";

                for (int i = 0; i < countIntegerDigits; i ++) {

                    formattedValue += "*";

                }

                formattedNumber = 0;

            }

            // Aggiungo gli spazi al valore
            else {

                int     countSpacesToAdd    = countIntegerDigits - integerPart.length();
                String  extraSpace          = "";

                for (int i = 0; i < countSpacesToAdd; i ++) {

                    extraSpace += " ";

                }

                formattedValue  = extraSpace + integerPart;
                formattedNumber = Integer.valueOf(integerPart);

            }

        }

    }

}
