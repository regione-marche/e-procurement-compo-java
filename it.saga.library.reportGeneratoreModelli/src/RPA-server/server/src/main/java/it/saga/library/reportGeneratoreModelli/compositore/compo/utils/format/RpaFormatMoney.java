package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

public class RpaFormatMoney extends RpaFormat {

    private Double  value;
    private String  formattedValue;

    public RpaFormatMoney(RpaMainCompositore mainCompositore, Double value) {

        super(mainCompositore);

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(value);
        bigDecimal              = bigDecimal.setScale(2, RoundingMode.HALF_UP);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatMoney(RpaMainCompositore mainCompositore, Integer value) {

        super(mainCompositore);

        // Converto l'intero a double
        Double rawDouble = value.doubleValue();

        // Formatto il valore ad un double da 2 cifre decimali
        BigDecimal bigDecimal   = BigDecimal.valueOf(rawDouble);
        bigDecimal              = bigDecimal.setScale(2, RoundingMode.HALF_UP);

        this.value = bigDecimal.doubleValue();
        formatValue();

    }

    public RpaFormatMoney(RpaMainCompositore mainCompositore, String value) throws ParseException {

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
                bigDecimal              = bigDecimal.setScale(2, RoundingMode.HALF_UP);

                this.value = bigDecimal.doubleValue();
                formatValue();

            } else {

                throw new ParseException("Impossibile convertire a valuta il valore '" + value + "'", 0);

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

        if (rawFormat.indexOf('.') == -1) {

            rawFormat += ".0";

        }

        // Controllo di avere due decimali
        int dotIndex            = rawFormat.indexOf('.');
        int decimalDigitsCount  = rawFormat.length() - dotIndex - 1;

        if (decimalDigitsCount < 2) {

            int countZeroToAdd = 2 - decimalDigitsCount;

            for (int i = 0; i < countZeroToAdd; i ++) {

                rawFormat = rawFormat + "0";

            }

        } else if (decimalDigitsCount > 2) {

            rawFormat = rawFormat.substring(0, dotIndex + 3);

        }

        // Cominciando dal punto, aggiungo l'apice alle centinaia
        int integerPartSize     = dotIndex;
        int currentCharIndex    = dotIndex - 1;
        int countCharRead       = 1;

        while (countCharRead < integerPartSize) {

            if (countCharRead % 3 == 0) {

                String leftPart     = rawFormat.substring(0, currentCharIndex);
                String rightPart    = rawFormat.substring(currentCharIndex);

                rawFormat = leftPart + '\'' + rightPart;

            }

            -- currentCharIndex;
            ++ countCharRead;

        }

        // Formatto i separatori delle centinaia e dei decimali
        RpaStyleManager styleManager = mainCompositore.getStyleManager();
        // rawFormat = rawFormat.replaceAll("\\.", styleManager.getDecimalSeparator());
        // rawFormat = rawFormat.replaceAll("\\'", styleManager.getDecimalSeparator());
        rawFormat = styleManager.formatDecimal(rawFormat);

        // Salvo il valore formattato
        formattedValue = rawFormat;

    }

}
