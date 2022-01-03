package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.text.ParseException;

public class RpaFormatZnn extends RpaFormat {

    private int     countDigits;
    private Integer value;
    private String  formattedValue;

    public RpaFormatZnn(RpaMainCompositore mainCompositore, String value, int countDigits) throws ParseException {

        super(mainCompositore);
        this.countDigits = countDigits;

        // Controllo che la stringa sia un intero o un double
        if (value != null && !value.isEmpty()) {

            Double rawDouble;

            if (RpaNumberUtils.isInteger(value)) {

                Integer valueInteger;

                if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                    valueInteger = Integer.valueOf(RpaNumberUtils.integerWithoutDotZero(value));

                } else {

                    valueInteger = Integer.valueOf(value);

                }

                rawDouble = valueInteger.doubleValue();

            } else if (RpaNumberUtils.isDouble(value)) {

                rawDouble = Double.valueOf(value);

            } else {

                rawDouble = null;

            }

            if (rawDouble != null) {

                // Formatto il valore a intero
                BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);

                this.value = bigDecimal.toBigInteger().intValue();
                formatValue();

            } else {

                throw new ParseException("Impossibile formattare il valore '" + value + "'", 0);

            }

        }

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

        return value;

    }

    @Override
    public String getComparisonValue() {

        // return value.toString();
        if (formattedValue == null) {

            return null;

        } else {

            return formattedValue.trim();

        }

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

        // Trasformo l'intero in stringa
        BigDecimal  bigDecimal  = BigDecimal.valueOf(value);
        String      valueString = bigDecimal.toPlainString();

        // Controllo se la lunghezza della stringa è maggiore del numero di cifre definite
        if (valueString.length() > countDigits) {

            // Creo una stringa di soli '*'
            formattedValue = "";

            for (int i = 0; i < countDigits; i ++) {

                formattedValue += '*';

            }

        } else {

            // Aggiungo gli zeri in testa al numero
            formattedValue      = valueString;
            int countZeroToAdd  = countDigits - valueString.length();

            for (int i = 0; i < countZeroToAdd; i ++) {

                formattedValue = '0' + formattedValue;

            }

        }

    }

}
