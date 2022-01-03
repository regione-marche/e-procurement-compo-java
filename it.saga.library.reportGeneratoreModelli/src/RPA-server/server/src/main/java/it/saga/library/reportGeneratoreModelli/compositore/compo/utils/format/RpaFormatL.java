package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

public class RpaFormatL extends RpaFormat {

    private Double  value;
    private String  formattedValue;

    public RpaFormatL(RpaMainCompositore mainCompositore, Double value) {

        super(mainCompositore);

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(value);
        bigDecimal              = bigDecimal.setScale(2, RoundingMode.DOWN);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatL(RpaMainCompositore mainCompositore, Integer value) {

        super(mainCompositore);

        // Converto l'intero a double
        Double rawDouble = value.doubleValue();

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);
        bigDecimal              = bigDecimal.setScale(2, RoundingMode.DOWN);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatL(RpaMainCompositore mainCompositore, String value) throws ParseException {

        super(mainCompositore);

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
                BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);
                // bigDecimal              = bigDecimal.setScale(2, RoundingMode.DOWN);

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

        return value.toString();

    }

    @Override
    public String getFormattedValue() {

        return formattedValue;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    public void formatValue() {

        // Formatto la parte intera
        String integerPart = RpaNumberUtils.wordTranslate(value);

        // Formatto la parte decimale
        BigDecimal  bigDecimal  = BigDecimal.valueOf(value);
        String      decimalPart = bigDecimal.remainder(BigDecimal.ONE).toPlainString();

        decimalPart = decimalPart.replaceAll("-?0\\.", "");
        if (decimalPart.length() < 2) {

            int countSpacesToAdd = 2 - decimalPart.length();

            for (int i = 0; i < countSpacesToAdd; i ++) {

                decimalPart += '0';

            }

        }

        // Salvo il risultato
        formattedValue = integerPart + '/' + decimalPart;

    }

}
