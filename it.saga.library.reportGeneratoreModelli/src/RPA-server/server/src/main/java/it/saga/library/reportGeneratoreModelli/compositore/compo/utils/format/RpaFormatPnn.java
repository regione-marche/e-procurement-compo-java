package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

public class RpaFormatPnn extends RpaFormat {

    private int     countDigits;
    private Double  value;
    private String  formattedValue;

    public RpaFormatPnn(RpaMainCompositore mainCompositore, Double value, int countDigits) {

        super(mainCompositore);
        this.countDigits = countDigits;

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(value);
        bigDecimal              = bigDecimal.setScale(2, RoundingMode.HALF_DOWN);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatPnn(RpaMainCompositore mainCompositore, Integer value, int countDigits) {

        super(mainCompositore);
        this.countDigits = countDigits;

        // Converto l'intero a double
        Double rawDouble = value.doubleValue();

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);
        bigDecimal              = bigDecimal.setScale(2, RoundingMode.HALF_DOWN);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatPnn(RpaMainCompositore mainCompositore, String value, int countDigits) throws ParseException {

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
                BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);
                bigDecimal              = bigDecimal.setScale(2, RoundingMode.HALF_DOWN);

                this.value = bigDecimal.doubleValue();
                formatValue();

            } else {

                throw new ParseException("Impossibile formattare il valore '" + value + "'", 0);

            }

        }

    }

    public RpaFormatPnn(RpaMainCompositore mainCompositore, String value) throws ParseException {

        this(mainCompositore, value, DEFAULT_LENGTH_CHARACTERS_FORMAT);

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

        // return AbstractMnemonic.TYPE_NUMBER;
        return RpaAbstractMnemonic.TYPE_STRING;

    }

    public void formatValue() {

        // Recupero i caratteri di formattazione
        char    decimalChar = mainCompositore.getStyleManager().getDecimalSeparator().charAt(0);
        char	integerChar = mainCompositore.getStyleManager().getIntegerSeparator().charAt(0);

        // Trasformo il double in stringa
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        String rawFormat = bigDecimal.toPlainString();

        // Sostituisco il carattere dei decimali (se non ci fosse, lo aggiungo)
        rawFormat = rawFormat.replaceAll("\\.", String.valueOf(decimalChar));

        if (rawFormat.indexOf(decimalChar) == -1) {

            rawFormat += decimalChar + "0";

        }

        // Conto il numero di cifre della formattazione
        int dotIndex            = rawFormat.indexOf(decimalChar);
        int integerPartSize     = dotIndex;
        int decimalDigitsCount  = rawFormat.length() - dotIndex - 1;
        int countApexesToAdd;
        int countDigitsFormat;

        if (integerPartSize > 3) {

            countApexesToAdd = (integerPartSize - 1) / 3;

        } else {

            countApexesToAdd = 0;

        }

        countDigitsFormat = integerPartSize + countApexesToAdd + 3;

        // Controllo se il numero di cifre è inferiore a quello definito
        if (countDigits < countDigitsFormat) {

            // Creo una stringa di '*'
            formattedValue = "";

            for (int i = 0; i < countDigits; i ++) {

                formattedValue += '*';

            }

        } else {

            // Controllo di avere due decimali
            if (decimalDigitsCount < 2) {

                int countZeroToAdd = 2 - decimalDigitsCount;

                for (int i = 0; i < countZeroToAdd; i ++) {

                    rawFormat = rawFormat + "0";

                }

            } else if (decimalDigitsCount > 2) {

                rawFormat = rawFormat.substring(0, dotIndex + 3);

            }

            // Cominciando dal punto, aggiungo l'apice alle centinaia
            int currentCharIndex    = dotIndex - 1;
            int countCharRead       = 1;

            while (countCharRead < integerPartSize) {

                if (countCharRead % 3 == 0) {

                    String leftPart     = rawFormat.substring(0, currentCharIndex);
                    String rightPart    = rawFormat.substring(currentCharIndex);

                    rawFormat = leftPart + integerChar + rightPart;

                }

                -- currentCharIndex;
                ++ countCharRead;

            }

            // Controllo se aggiungere gli spazi
            int countSpacesToAdd = countDigits - countDigitsFormat;

            for (int i = 0; i < countSpacesToAdd; i ++) {

                rawFormat = ' ' + rawFormat;

            }

            // Salvo il valore formattato
            formattedValue = rawFormat;

        }

    }

}
