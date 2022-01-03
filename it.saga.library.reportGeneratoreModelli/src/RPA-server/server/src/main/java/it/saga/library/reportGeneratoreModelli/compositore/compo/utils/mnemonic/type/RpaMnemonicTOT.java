package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RpaMnemonicTOT extends RpaAbstractMnemonicConstant {

    private Number value;
    private RpaFormat lastFormattedValue;

    public RpaMnemonicTOT(RpaMainCompositore mainCompositore, Number value) {

        super(mainCompositore);
        this.value = value;

    }

    @Override
    public String getValue() {

        if (value == null) {

            return "0";

        } else {

            return value.toString();

        }

    }

    @Override
    public String getPrintedValue() {

        if (lastFormattedValue != null) {

            String formattedValue = lastFormattedValue.getPrintedValue();

            if (formattedValue == null) {

                return formattedDouble();

            } else {

                return formattedValue;

            }

        } else {

            return formattedDouble();

        }

    }

    @Override
    public void setLastFormattedValue(RpaFormat lastFormattedValue) {

        this.lastFormattedValue = lastFormattedValue;

    }

    @Override
    public RpaFormat getLastFormattedValue() {

        return lastFormattedValue;

    }

    @Override
    public int getType() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getMnemonicType();

        } else {

            return TYPE_NUMBER;

        }

    }

    @Override
    public String getValueForSTR() {

        if (lastFormattedValue != null ) {

            String formattedValue = lastFormattedValue.getValueAssignment();

            if (formattedValue == null) {

                return formattedDouble();

            } else {

                return formattedValue;

            }

        } else {

            // return value.toString();
            return formattedDouble();

        }

    }

    @Override
    public String getComparisonValue() {

        if (lastFormattedValue != null) {

            String formattedValue = lastFormattedValue.getComparisonValue();

            if (formattedValue == null) {

                return "0";

            } else {

                return formattedValue;

            }

        } else {

            if (value == null) {

                return "0";

            } else {

                if (value instanceof Double) {

                    // StyleManager    styleManager    = StyleManager.getInstance();
                    RpaStyleManager styleManager    = mainCompositore.getStyleManager();
                    Double          doubleValue     = (Double) value;
                    BigDecimal      bigDecimal      = BigDecimal.valueOf(doubleValue);

                    bigDecimal = bigDecimal.setScale(styleManager.getPrecision(), RoundingMode.HALF_UP);

                    return bigDecimal.toString();

                } else {

                    return value.toString();

                }

            }

        }

    }

    @Override
    public Number getValueForMath() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueNumber();

        } else if (value == null) {

            return 0;

        } else {

            return value;

        }

    }

    public Number getValueNumber() {

        if (value == null) {

            return 0;

        } else {

            return value;

        }

    }

    private String formattedDouble() {

        RpaStyleManager styleManager    = mainCompositore.getStyleManager();

        // Controllo se "value" è null
        if (value == null) {

            String  newDecimalPart      = "";
            int     countDecimalDigits  = styleManager.getPrecision();

            for (int i = 0; i < countDecimalDigits; i ++) {

                newDecimalPart += "0";

            }

            return "0" + styleManager.getDecimalSeparator() + newDecimalPart;

        }

        // Recupero la parte intera e decimale
        BigDecimal bigDecimal = new BigDecimal(value.toString());

        String integerPart;
        String decimalPart;

        if (value instanceof Integer) {

            integerPart = value.toString();
            decimalPart = "";

        } else {

            integerPart = bigDecimal.toBigInteger().toString();
            decimalPart = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("-?0\\.", "");

        }

        String  newDecimalPart      = "";
        int     countDecimalDigits  = styleManager.getPrecision();

        // Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
        if (decimalPart.isEmpty()) {

            for (int i = 0; i < countDecimalDigits; i ++) {

                newDecimalPart += "0";

            }

        }

        // Controllo se tagliare la parte decimale: 23.090923311 -> 23.09
        else if (decimalPart.length() > countDecimalDigits) {

            newDecimalPart = decimalPart.substring(0, countDecimalDigits);

        }

        // Controllo se aggiungere gli zeri in coda alla parte decimale: 23.34 -> 23.340000000
        else if (decimalPart.length() < countDecimalDigits) {

            int countZeroToAdd = countDecimalDigits - decimalPart.length();

            newDecimalPart = decimalPart;

            for (int i = 0; i < countZeroToAdd; i ++) {

                newDecimalPart += "0";

            }

        }

        // Negli altri casi mantengo la stessa parte decimale
        else {

            newDecimalPart = decimalPart;

        }

        // Aggiungo il "-" alla parte intera se il numero è negativo
        if (value.doubleValue() < 0) {

            // integerPart = "-" + integerPart;

        }

        // Se non ho decimali, aggiungo a essi uno "0"
        if (newDecimalPart.isEmpty()) {

            newDecimalPart = "0";

        }

        return integerPart + styleManager.getDecimalSeparator() + newDecimalPart;

    }

}
