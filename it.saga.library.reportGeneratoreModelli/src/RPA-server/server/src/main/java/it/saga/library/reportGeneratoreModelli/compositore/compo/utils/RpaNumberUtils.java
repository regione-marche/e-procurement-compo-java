package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaNumberUtils {

    private static final String Digits     = "(\\p{Digit}+)";
    private static final String HexDigits  = "(\\p{XDigit}+)";

    private static String[] firstTwentyWords = {
            "uno",          "due",          "tre",
            "quattro",      "cinque",       "sei",
            "sette",        "otto",         "nove",
            "dieci",        "undici",       "dodici",
            "tredici",      "quattordici",  "quindici",
            "sedici",       "diciassette",  "diciotto",
            "diciannove"
    };

    private static String[] decimalWords = {
            "venti",        "trenta",   "quaranta",
            "cinquanta",    "sessanta", "settanta",
            "ottanta",      "novanta"
    };



    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    private static final String Exp        = "[eE][+-]?"+Digits;

    private static final String fpRegex    =
            ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
                    "[+-]?(" +         // Optional sign character
                    "NaN|" +           // "NaN" string
                    "Infinity|" +      // "Infinity" string

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from the Java Language Specification, 2nd
                    // edition, section 3.10.2.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.("+Digits+")("+Exp+")?)|"+

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing "whitespace"

    // private static final String intRegex = "(^[1-9][0-9]*$)|(^0$)";
    // private static final String intRegex = "(^-?[1-9][0-9]*$)|(^0$)|(^-?[1-9][0-9]*\\.0+$)";
    private static final String intRegex = "(^-?[1-9][0-9]*$)|(^0$)|(^-?[1-9][0-9]*[\\.\\,]0+$)";

    // private static final String intDotZeroRegex = "^([1-9][0-9]*)\\.0+$";
    private static final String intDotZeroRegex = "^(\\-?[1-9][0-9]*)[\\.\\,]0+$";

    public static boolean isDouble(String string) {

        return Pattern.matches(fpRegex, string);

    }

    public static boolean isInteger(String string) {

        return Pattern.matches(intRegex, string);

    }

    public static boolean isIntegerWithDotZero(String string) {

        return Pattern.matches(intDotZeroRegex, string);

    }

    public static String integerWithoutDotZero(String string) {

        Matcher matcher = Pattern.compile(intDotZeroRegex).matcher(string);
        matcher.find();

        return matcher.group(1);

    }

    public static String NumberToText(int n) {

        // metodo wrapper
        if (n == 0) {

            return "zero";

        } else {

            return NumberToTextRecursive(n);

        }

    }

    public static String wordTranslate(double number) {

        // metodo wrapper
        int n           = (int) Math.round(number * 100);
        int integerPart = n/100;

        return NumberToTextRecursive(integerPart);
    }

    public static String wordTranslate(int number) {

        return NumberToTextRecursive(number);
    }

    private static String NumberToTextRecursive(int n) {
        if (n < 0) {
            return "meno " + NumberToTextRecursive(-n);
        } else if (n == 0){
            return "";
        } else if (n <= 19){
            return firstTwentyWords[n-1];
        } else if (n <= 99) {
            String letter = decimalWords[n / 10 - 2];
            int t = n % 10; // t è la prima cifra di n
            // se è 1 o 8 va tolta la vocale finale di letter
            if (t == 1 || t == 8 ) {
                letter = letter.substring(0, letter.length() - 1);
            }
            return letter + NumberToTextRecursive(n % 10);
        } else if (n <= 199){
            return "cento" + NumberToTextRecursive(n % 100);
        } else if (n <= 999){
            int m = n % 100;
            m /= 10; // divisione intera per 10 della variabile
            String letter = "cent";
            if (m != 8){
                letter = letter + "o";
            }
            return NumberToTextRecursive(n / 100) + letter +
                    NumberToTextRecursive(n % 100);
        }
        else if (n <= 1999){
            return "mille" + NumberToTextRecursive(n % 1000);
        }  else if (n <= 999999){
            return NumberToTextRecursive(n / 1000) + "mila" +
                    NumberToTextRecursive(n % 1000);
        }
        else if (n <= 1999999){
            return "unmilione" + NumberToTextRecursive(n % 1000000);
        } else if (n <= 999999999){
            return NumberToTextRecursive(n / 1000000) + "milioni" +
                    NumberToTextRecursive(n % 1000000);
        } else if (n <= 1999999999){
            return "unmiliardo" + NumberToTextRecursive(n % 1000000000);
        } else {
            return NumberToTextRecursive(n / 1000000000) + "miliardi" +
                    NumberToTextRecursive(n % 1000000000);
        }
    }


}
