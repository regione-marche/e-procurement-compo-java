package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaFormatKmCippo extends RpaFormat {

    // Link: https://regex101.com/r/VJBZlx/2
    private static final String NUMBER_PARTS_REGEX  = "([0-9]+)(([,\\.])([0-9]+))?";
    private static final int    COUNT_DECIMAL       = 3;

    private Double  value;
    private String  formattedValue;

    public RpaFormatKmCippo(RpaMainCompositore mainCompositore, Double value) {

        super(mainCompositore);

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(value);
        bigDecimal              = bigDecimal.setScale(COUNT_DECIMAL, RoundingMode.FLOOR);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatKmCippo(RpaMainCompositore mainCompositore, Integer value) {

        super(mainCompositore);

        // Converto l'intero a double
        Double rawDouble = value.doubleValue();

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);
        bigDecimal              = bigDecimal.setScale(COUNT_DECIMAL, RoundingMode.FLOOR);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatKmCippo(RpaMainCompositore mainCompositore, String value) throws ParseException {

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
                bigDecimal              = bigDecimal.setScale(COUNT_DECIMAL, RoundingMode.FLOOR);

                this.value = bigDecimal.doubleValue();
                formatValue();

            } else {

                throw new ParseException("Impossibile convertire a KM-cippo il valore '" + value + "'", 0);

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

        return RpaAbstractMnemonic.TYPE_NUMBER;

    }

    public void formatValue() {

        // Trasformo il double in stringa
        BigDecimal  bigDecimal  = BigDecimal.valueOf(value);
        String      rawFormat   = bigDecimal.toPlainString();

        // Estraggo la parte intera e quella decimale
        Matcher numberMatcher = Pattern.compile(NUMBER_PARTS_REGEX).matcher(rawFormat);
        numberMatcher.find();

        String integerPart = "";
        String decimalPart = "";

        integerPart = numberMatcher.group(1);

        if (numberMatcher.group(2) != null && numberMatcher.group(2).length() > 0) {

            decimalPart = numberMatcher.group(4);

        }

        // Nel caso, espando o tronco la parte decimale a "COUNT_DECIMAL"
        if (decimalPart.length() < COUNT_DECIMAL) {

            int countDigitsToAdd = COUNT_DECIMAL - decimalPart.length();

            for (int i = 0; i < countDigitsToAdd; i++) {

                decimalPart += "0";

            }

        }
        else if (decimalPart.length() > COUNT_DECIMAL) {

            decimalPart = decimalPart.substring(0, COUNT_DECIMAL);

        }

        // Ricostruisco il valore numerico finale con un "+" come separatore
        formattedValue = integerPart + "+" + decimalPart;

    }

}
