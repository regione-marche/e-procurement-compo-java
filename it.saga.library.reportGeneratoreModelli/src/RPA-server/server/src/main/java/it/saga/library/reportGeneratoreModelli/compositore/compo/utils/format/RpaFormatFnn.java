package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaFormatFnn extends RpaFormat {

    public static final String EXTRACT_DIGITS_COUNT_REGEX = "F([0-9]+).([0-9]+)";

    private int     countIntegerDigits;
    private int     countDecimalDigits;
    private String  value;

    public RpaFormatFnn(RpaMainCompositore mainCompositore, String value, String format) {

        super(mainCompositore);

        // Estraggo dal formato il numero di cifre intere e decimali
        Matcher matcher = Pattern.compile(EXTRACT_DIGITS_COUNT_REGEX).matcher(format);
        matcher.find();

        this.countDecimalDigits = Integer.valueOf(matcher.group(2));
        this.countIntegerDigits = Integer.valueOf(matcher.group(1)) - this.countDecimalDigits - 1;

        this.value = value;

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

        return getFormattedValue();

    }

    @Override
    public Number getValueNumber() {

        if (value == null) {

            // return 0;
            return null;

        } else {

            String formattedValue = getFormattedValue();

            return Double.valueOf(formattedValue);

        }

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_NUMBER;

    }

    public String getFormattedValue() {

        // Controllo se il numero di cifre intere è negativo
        if (countIntegerDigits < 0) {

            return "0";

        }

        // Controllo se il numero di cifre intere è 0
        else if (countIntegerDigits == 0) {

            return "0";

        }

        else {

            // Controllo se il valore è null
            if (value == null) {

                return emptyPrintedValue();

            }

            // Controllo se il valore è una stringa vuota
            else if (value.isEmpty()) {

                return "0";

            }

            // Controllo se il valore è un numero
            else if (!RpaNumberUtils.isInteger(value) && !RpaNumberUtils.isDouble(value)) {

                return "0";

            }

            // Recupero la parte intera e quella decimale
            BigDecimal bigDecimal = new BigDecimal(value);

            String integerPart;
            String decimalPart;

            if (RpaNumberUtils.isInteger(value)) {

                integerPart = bigDecimal.toBigInteger().toString();
                decimalPart = "";

            } else {

                integerPart = bigDecimal.toBigInteger().toString();
                decimalPart = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("0\\.", "");

            }

            // Controllo se sono in overflow
            if (integerPart.length() > countIntegerDigits) {

                return "0";

            }

            // Controllo se sono in underflow
            if (integerPart.length() < countIntegerDigits) {

                // Esempio stampa: "10.5466700" per 10.54667 e F11.7
                return getFormattedValue(integerPart, decimalPart);

            }

            // Negli altri casi, non formatto la parte intera
            else {

                // Esempio stampa: "10.5466700" per 10.54667 e F10.7
                return getFormattedValue(integerPart, decimalPart);

            }

        }

    }

    private String getPrintedFormattedValue() {

        RpaStyleManager styleManager = mainCompositore.getStyleManager();

        // Controllo se il numero di cifre intere è negativo
        if (countIntegerDigits < 0) {

            return null;

        }

        // Controllo se il numero di cifre intere è 0
        else if (countIntegerDigits == 0) {

            // Esempio stampa: ".*****" per countDecimalDigits == 5

            String printedValue = ".";

            for (int i = 0; i < countDecimalDigits; i ++) {

                printedValue += "*";

            }

            // printedValue = printedValue.replaceAll("\\.", styleManager.getDecimalSeparator());
            printedValue = styleManager.formatDecimal(printedValue);
            return printedValue;

        }

        else {

            // Se il valore è null, stampo solo spazi
            if (value == null) {

                String printedValue = "";

                for (int i = 0; i < countIntegerDigits + countDecimalDigits + 1; i ++) {

                    printedValue += " ";

                }

                // printedValue = printedValue.replaceAll("\\.", styleManager.getDecimalSeparator());
                printedValue = styleManager.formatDecimal(printedValue);
                return printedValue;

            }

            // Se il valore è una stringa vuota, stampo solo spazi
            else if (value.isEmpty()) {

                String printedValue = "";

                for (int i = 0; i < countIntegerDigits + countDecimalDigits + 1; i ++) {

                    printedValue += " ";

                }

                // printedValue = printedValue.replaceAll("\\.", styleManager.getDecimalSeparator());
                printedValue = styleManager.formatDecimal(printedValue);
                return printedValue;

            }

            // Controllo se il valore è numero
            else if (!RpaNumberUtils.isInteger(value) && !RpaNumberUtils.isDouble(value)) {

                return "";

            }

            // Recupero la parte intera e quella decimale
            BigDecimal bigDecimal = new BigDecimal(value);

            String integerPart;
            String decimalPart;

            if (RpaNumberUtils.isInteger(value)) {

                integerPart = bigDecimal.toBigInteger().toString();
                decimalPart = "";

            } else {

                integerPart = bigDecimal.toBigInteger().toString();
                decimalPart = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("0\\.", "");

            }

            // Controllo se sono in overflow
            if (integerPart.length() > countIntegerDigits) {

                // Esempio stampa: "*.*******" per 10.54667 e F9.7
                String printedValue = getPrintedFormatOverflow(integerPart, decimalPart);
                return styleManager.formatDecimal(printedValue);
                // return printedValue.replaceAll("\\.", styleManager.getDecimalSeparator());

            }

            // Controllo se sono in underflow
            if (integerPart.length() < countIntegerDigits) {

                // Esempio stampa: " 10.5466700" per 10.54667 e F11.7
                String printedValue = getPrintedFormatUnderflow(integerPart, decimalPart);
                return styleManager.formatDecimal(printedValue);
                // return printedValue.replaceAll("\\.", styleManager.getDecimalSeparator());

            }

            // Negli altri casi, non formatto la parte intera
            else {

                // Esempio stampa: "10.5466700" per 10.54667 e F10.7
                String printedValue = getPrintedFormat(integerPart, decimalPart);
                return printedValue;

            }

        }

    }

    private String getPrintedFormatOverflow(String integerPart, String decimalPart) {

        // Esempio stampa: "*.*******" per 10.54667 e F9.7

        String newIntegerPart = "";

        for (int i = 0; i < countIntegerDigits; i ++) {

            newIntegerPart += "*";

        }

        String newDecimalPart = "";

        for (int i = 0; i < countDecimalDigits; i ++) {

            newDecimalPart += "*";

        }

        return newIntegerPart + "." + newDecimalPart;

    }

    private String getPrintedFormatUnderflow(String integerPart, String decimalPart) {

        // Esempio stampa: " 10.5466700" per 10.54667 e F11.7

        String newIntegerPart = "";

        // Aggiungo gli spazi in test alla parte intera
        int countSpaceToAdd = countIntegerDigits - integerPart.length();

        for (int i = 0; i < countSpaceToAdd; i ++) {

            newIntegerPart += " ";

        }

        newIntegerPart += integerPart;

        String newDecimalPart = "";

        // Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
        if (decimalPart.isEmpty()) {

            for (int i = 0; i < countDecimalDigits; i ++) {

                newDecimalPart += "0";

            }

        }

        // Controllo se arrotondare la parte decimale: 23.099923311 -> 23.10
        else if (decimalPart.length() > countDecimalDigits) {

            // newDecimalPart = decimalPart.substring(0, countDecimalDigits);
            String      numberString    = integerPart + "." + decimalPart;
            BigDecimal  bigDecimal      = new BigDecimal(numberString);
            bigDecimal                  = bigDecimal.setScale(countDecimalDigits, BigDecimal.ROUND_HALF_UP);

            newIntegerPart  = bigDecimal.toBigInteger().toString();
            newDecimalPart  = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("0\\.", "");

        }

        // Controllo se aggiungere gli zeri in coda alla parte decimale: 23.34 -> 23.340000000
        else if (decimalPart.length() < countDecimalDigits) {

            int countZeroToAdd = countDecimalDigits - decimalPart.length();

            for (int i = 0; i < countZeroToAdd; i ++) {

                newDecimalPart += "0";

            }

            newDecimalPart = decimalPart + newDecimalPart;

        }

        // Negli altri casi mantengo la stessa parte decimale
        else {

            newDecimalPart = decimalPart;

        }

        return newIntegerPart + "." + newDecimalPart;

    }

    // TODO: Da riscrivere per l'arrotondamento
    private String oldGetPrintedFormatUnderflow(String integerPart, String decimalPart) {

        // Esempio stampa: " 10.5466700" per 10.54667 e F11.7

        String newIntegerPart = "";

        // Aggiungo gli spazi in test alla parte intera
        int countSpaceToAdd = countIntegerDigits - integerPart.length();

        for (int i = 0; i < countSpaceToAdd; i ++) {

            newIntegerPart += " ";

        }

        newIntegerPart += integerPart;

        String newDecimalPart = "";

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

            for (int i = 0; i < countZeroToAdd; i ++) {

                newDecimalPart += "0";

            }

            newDecimalPart = decimalPart + newDecimalPart;

        }

        // Negli altri casi mantengo la stessa parte decimale
        else {

            newDecimalPart = decimalPart;

        }

        return newIntegerPart + "." + newDecimalPart;

    }

    private String getPrintedFormat(String integerPart, String decimalPart) {

        // Esempio stampa: "10.5466700" per 10.54667 e F10.7

        String newDecimalPart = "";

        // Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
        if (decimalPart.isEmpty()) {

            // for (int i = 0; i < countIntegerDigits; i ++) {
            for (int i = 0; i < countDecimalDigits; i ++) {

                newDecimalPart += "0";

            }

        }

        // Controllo se arrotondare la parte decimale: 23.099923311 -> 23.10
        else if (decimalPart.length() > countDecimalDigits) {

            // newDecimalPart = decimalPart.substring(0, countDecimalDigits);
            String      numberString    = integerPart + "." + decimalPart;
            BigDecimal  bigDecimal      = new BigDecimal(numberString);
            bigDecimal                  = bigDecimal.setScale(countDecimalDigits, BigDecimal.ROUND_HALF_UP);

            newDecimalPart  = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("0\\.", "");

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

        return integerPart + "." + newDecimalPart;

    }

    // TODO: Da riscrivere per l'arrotondamento
    private String oldGetPrintedFormat(String integerPart, String decimalPart) {

        // Esempio stampa: "10.5466700" per 10.54667 e F10.7

        String newDecimalPart = "";

        // Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
        if (decimalPart.isEmpty()) {

            // for (int i = 0; i < countIntegerDigits; i ++) {
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

        return integerPart + "." + newDecimalPart;

    }

    private String getFormattedValue(String integerPart, String decimalPart) {

        String newDecimalPart = "";

        // Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
        if (decimalPart.isEmpty()) {

            for (int i = 0; i < countIntegerDigits; i ++) {

                newDecimalPart += "0";

            }

        }

        // Controllo se arrotondare la parte decimale: 23.099923311 -> 23.10
        else if (decimalPart.length() > countDecimalDigits) {

            // newDecimalPart = decimalPart.substring(0, countDecimalDigits);
            String      numberString    = integerPart + "." + decimalPart;
            BigDecimal  bigDecimal      = new BigDecimal(numberString);
            bigDecimal                  = bigDecimal.setScale(countDecimalDigits, BigDecimal.ROUND_HALF_UP);

            newDecimalPart  = bigDecimal.remainder(BigDecimal.ONE).toPlainString().replaceAll("0\\.", "");

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

        return integerPart + "." + newDecimalPart;

    }

    // TODO: Da riscrivere per l'arrotondamento
    private String oldGetFormattedValue(String integerPart, String decimalPart) {

        String newDecimalPart = "";

        // Controllo se aggiungere i "0" come decimali: 23 -> 23.000000
        if (decimalPart.isEmpty()) {

            for (int i = 0; i < countIntegerDigits; i ++) {

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

        return integerPart + "." + newDecimalPart;

    }

}
