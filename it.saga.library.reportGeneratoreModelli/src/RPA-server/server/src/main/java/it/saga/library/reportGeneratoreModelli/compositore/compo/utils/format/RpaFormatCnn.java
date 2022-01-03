package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

public class RpaFormatCnn extends RpaFormat {

    private int     countDigits;
    private Double  value;
    private String  formattedValue;

    public RpaFormatCnn(RpaMainCompositore mainCompositore, String value, int countDigits) throws ParseException {

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

                // Formatto il valore ad un double da 2 cifre decimali
                BigDecimal bigDecimal = BigDecimal.valueOf(rawDouble);
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_DOWN);

                this.value = bigDecimal.doubleValue();
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

        return RpaAbstractMnemonic.TYPE_NUMBER;

    }

    private void formatValue() {

        // Trasformo l'intero in stringa
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        String valueString = bigDecimal.toPlainString();

        // Controllo se la stringa è più lunga del numero di cifre
        if (valueString.length() > countDigits) {

            // Creo una stringa di soli '*'
            formattedValue = "";

            for (int i = 0; i < countDigits; i++) {

                formattedValue += '*';

            }

        } else if (valueString.length() < countDigits) {

            // Aggiungo una serie di spazi come prefisso
            // int countSpaceToAdd;

            /*
            if (valueString.indexOf('.') != -1) {

                countSpaceToAdd = countDigits - valueString.length() - 1;

            } else {

                countSpaceToAdd = countDigits - valueString.length();

            }
            */

            int countSpaceToAdd = countDigits - valueString.length();

            formattedValue = valueString;

            for (int i = 0; i < countSpaceToAdd; i ++) {

                formattedValue = " " + formattedValue;

            }

        } else {

            // Salvo il valore così com'è
            formattedValue = valueString;

        }

        // Sostituisco il separatore dei decimali
        formattedValue = formattedValue.replace('.', mainCompositore.getStyleManager().getDecimalSeparator().charAt(0));

    }

}
