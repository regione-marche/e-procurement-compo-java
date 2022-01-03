package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RpaAbstractMnemonic {

    public static final int TYPE_STRING         = 0;
    public static final int TYPE_NUMBER         = 1;
    public static final int TYPE_DATE           = 2;
    public static final int TYPE_BOOLEAN        = 3;
    public static final int TYPE_TABULATION     = 4;
    public static final int TYPE_TIMESTAMP      = 5;
    public static final int TYPE_MONEY          = 6;
    public static final int TYPE_UNDEFINED      = -10;
    public static final int TYPE_EMPTY          = -11;

    public static final int NUMBER_EMPTY_CHARACTERS = 6;

    public static final String DATE_FORMAT_STRING   = "dd.MM.yyyy";
    public static final String DATE_ORIGIN_STRING   = "01.01.1000";

    // Link: https://regex101.com/r/ST2Cuc/5
    private static final String NUMBER_FORMATTED_REGEX = "^\\-?((0)|(((0[^0-9\\n\\r][0-9]+)|([1-9][0-9]*([^0-9\\n\\r][0-9]+)?)))|([1-9][0-9]{0,2}([^0-9\\n\\r][0-9]{3})*([^0-9\\n\\r][0-9]+)?))$";

    protected RpaMainCompositore mainCompositore;

    public RpaAbstractMnemonic(RpaMainCompositore mainCompositore) {

        this.mainCompositore = mainCompositore;

    }

    public abstract String getValue();

    public abstract String getPrintedValue();

    public abstract void setLastFormattedValue(RpaFormat lastFormattedValue);

    public abstract RpaFormat getLastFormattedValue();

    public abstract int getType();

    public abstract String getValueForSTR();

    public abstract String getComparisonValue();

    public abstract Number getValueForMath();

    public String emptyPrintedValue() {

        String innerValue = this.getValue();
        mainCompositore.getDebugMessages().print("CALL MNEMONIC 'emptyPrintedValue()' WITH [TRAON] = '" + mainCompositore.getStyleManager().isStrokeActive() + "' AND VALUE '" + innerValue + "'");
        if (!mainCompositore.getStyleManager().isStrokeActive()) {

            return "";

        }

        String printedValue = "";

        for (int i = 0; i < NUMBER_EMPTY_CHARACTERS; i ++ ) {

            printedValue += "_";

        }

        return printedValue;

    }

    public RpaValue<RpaAbstractMnemonic> generateValue() {

        // getPrintedValue();
        if (this.getLastFormattedValue() != null) {

            return this.getLastFormattedValue().generateValue(this);

        } else {

            return new RpaValue<RpaAbstractMnemonic>(this);

        }

    }

    @Override
    public String toString() {

        // return "";
        return getPrintedValue();

    }

    protected String formatSimpleNumber(String numberString) {

        String formattedNumber;

        RpaStyleManager styleManager        = mainCompositore.getStyleManager();
        String          decimalSeparator    = styleManager.getDecimalSeparator();
        String          integerSeparator    = styleManager.getIntegerSeparator();

        Matcher matcher = Pattern.compile(NUMBER_FORMATTED_REGEX).matcher(numberString);

        // Controllo se il valore è un numero valido
        if (!matcher.find()) {

            formattedNumber = null;

        }

        // Controllo se il valore è 0
        else if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {

            formattedNumber = "0";

        }

        // Controllo se il valore è un valore tra 0 e 1 (es: 0.2387)
        else if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {

            formattedNumber = numberString.replaceAll("[^0-9\\-]", decimalSeparator);

        }

        // Controllo se è un double senza formattazioni (es: 50000.4556)
        else if (matcher.group(7) != null && !matcher.group(7).isEmpty()) {

            String integerPart = numberString.substring(0, matcher.start(7));
            String decimalPart = matcher.group(7);

            decimalPart = decimalPart.replaceAll("[^0-9\\-]", decimalSeparator);
            integerPart = addIntegerSeparator(integerPart);

            formattedNumber = integerPart + decimalPart;

        }

        // Controllo se il valore è un intero senza formattazioni (es: 43551)
        else if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {

            formattedNumber = addIntegerSeparator(numberString);

        }

        // Controllo se è un double con formattazioni (es: 50'000.4556)
        else if (matcher.group(8) != null && !matcher.group(8).isEmpty()) {

            String integerPart = numberString.substring(0, matcher.start(10));
            String decimalPart = matcher.group(10);

            integerPart = integerPart.replaceAll("[^0-9\\-]", integerSeparator);
            decimalPart = decimalPart.replaceAll("[^0-9\\-]", decimalSeparator);

            formattedNumber = integerPart + decimalPart;

        }

        // Negli altri casi, restituisco "null"
        else {

            formattedNumber = null;

        }

        return formattedNumber;

    }

    private String addIntegerSeparator(String numberString) {

        RpaStyleManager styleManager        = mainCompositore.getStyleManager();
        String          integerSeparator    = styleManager.getIntegerSeparator();

        // Controllo se il numero è di cifre è maggiore di 3
        if (numberString.length() > 3) {

            // Ogni tre cifre, aggiungo il separatore delle centinaia
            String formattedNumber = "";

            for (int countChar = 0; countChar < numberString.length(); countChar ++) {

                int     charIndex   = (numberString.length() - countChar) - 1;
                char    character   = numberString.charAt(charIndex);

                // if ((countChar + 1) % 3 == 0) {
                if ((countChar + 1) % 3 == 0 && (countChar + 1) != numberString.length()) {

                    formattedNumber = integerSeparator + character + formattedNumber;

                } else {

                    formattedNumber = character + formattedNumber;

                }

            }

            return formattedNumber;

        } else {

            return numberString;

        }

    }

}
