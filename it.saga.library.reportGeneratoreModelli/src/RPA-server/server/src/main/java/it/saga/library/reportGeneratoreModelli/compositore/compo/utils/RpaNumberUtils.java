package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import com.aspose.words.CompositeNode;
import com.aspose.words.DocumentBase;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import it.saga.library.commonDataTypes.CdtUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaNumberUtils {

    /*
        ANALISI DA DEBUG "RUN":
        - Dimensione BASE: 40B
		- Numero di nodi RUN inclusi all'interno: 2
		- Funzione di calcolo: dimensione_stringa + 40 + 2 * (dimensione_stringa + 40)
     */
    private static final int NODE_RUN_SIZE          = 40;
    /*
    ANALISI DA DEBUG "PARAGRAPH":
        - Dimensione BASE: 56B
		- Dimensione ParagraphFormat (SEMPRE COSTANTE): 24B
		- Dimensione primo nodo supporto (SEMPRE COSTANTE): 160B
		- Dimensione primo nodo supporto (SEMPRE COSTANTE): 160B
		- Dimensione node-collection (BASE): 48B
		- Valore totale di BASE: 56 + 24 + 160 + 160 + 48 = 448
		- Funzione di calcolo (Se "childNodeCollection" vuoto): 448 + dimensione_stringa
     */
    private static final int NODE_PARAGRAPH_SIZE    = 448;

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

    // Link: https://regex101.com/r/oGqp28/1
    private static final String commaFpRegex = "^((0)|([1-9][1-9]*))[\\.\\,][0-9]+$";

    public static boolean isDouble(String string) {

        return Pattern.matches(fpRegex, string);

    }

    public static boolean isDoubleWithComma(String string) {

        return  Pattern.matches(commaFpRegex, string);

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

    public static Long calculateDocumentSize(DocumentBase document) {

        int CHAR_SIZE = 2;
        long sum = 0;

        // Estraggo la dimensione di TUTTI i nodi "RUN"
        NodeCollection<Run> runNodes = document.getChildNodes(NodeType.RUN, true);
        for (Run run: runNodes) {

            // Value: dimensione_stringa + NODE_RUN_SIZE + (2 * (dimensione_stringa + NODE_RUN_SIZE))
            long textSize = run.getText().length() * CHAR_SIZE;
            sum += textSize + NODE_RUN_SIZE + (2 * (textSize + NODE_RUN_SIZE));

        }

        // Estraggo la dimensione di TUTTI i nodi "PARAGRAPH"
        NodeCollection<Paragraph> paragraphNodes = document.getChildNodes(NodeType.PARAGRAPH, true);
        for (Paragraph paragraph: paragraphNodes) {

            // Value (Se "childNodeCollection" vuoto): NODE_PARAGRAPH_SIZE + dimensione_stringa
            if (!paragraph.hasChildNodes()) {

                long textSize = paragraph.getText().length() * CHAR_SIZE;
                sum += NODE_PARAGRAPH_SIZE + textSize;

            }

            // Value (Se "childNodeCollection" popolato): NODE_PARAGRAPH_SIZE
            else {

                sum += NODE_PARAGRAPH_SIZE;

            }

        }

        // Ritorno il calcolo in KB
        return sum / 1000L;

    }

    private static Long oldCalculateDocumentSize(DocumentBase document) {

        // Il calcolo della dimensione è fatta sul SOLO testo (escludendo artifici grafici e immagini)
        // Nel caso si può provare anche la funzione "getObjectSize(object)"
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/Instrumentation.html
        int CHAR_SIZE = 2;
        long textLength = document.getText().length();

        // Ritorno il calcolo in KB
        return (textLength * CHAR_SIZE) / 1000L;

    }

    private static Long oldOldCalculateDocumentSize(DocumentBase document) {

        int CHAR_SIZE = 2;
        long documentSize = recursiveCalculateDocumentSize(document);

        // Ritorno il calcolo in KB
        return (documentSize * CHAR_SIZE) / 1000L;

    }

    private static Long recursiveCalculateDocumentSize(Node node) {

        CdtUtils.getObjectSize(node);

        // Calcolo ricorsivamente la dimensione di ogni "sotto-albero"
        if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode<Node>) node;

            long sum = 0L;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                sum += recursiveCalculateDocumentSize(childNode);

            }

            return sum;

        }

        // Altrimenti, se ho un nodo "foglia", calcolo e restituisco il suo valore
        else {

            // SBAGLIATO: Gli elementi di ASPOSE non FUNZIONANO con questa classe (non essendo serializzati)
            return (long) CdtUtils.getObjectSize(node);

        }

    }

}
