package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaCompoException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidFormatException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMnemonicInstanceException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// TODO: Raccogliere dal c0campi il numero di "underscore" da stampare

/**
 * Contiene il valore di un mnemonico.
 * Viene utilizzato sia per la comparazione con altri valori, sia per stampare il valore su documento
 */
public class RpaMnemonic extends RpaAbstractMnemonic {

    private RpaMnemonicManager mnemonicManager;
    private Integer type;
    private Integer decimalPrecision;
    private String  value;
    private String  originalValue;
    private String  printedValue;
    private String  name;
    private String  fieldTable;
    private String  table;
    private String  domain;
    private Long    primaryKey;
    private Boolean isExtensible;
    private boolean isAlreadyExtended;

    private RpaFormat lastFormattedValue;

    private Map<Integer, String> booleanTranslationMap;

    public RpaMnemonic(RpaMainCompositore mainCompositore, String value, String fieldTable, String table, String domain, Long primaryKey/*, boolean isExtensible */) {

        super(mainCompositore);

        this.mnemonicManager    = mainCompositore.getMnemonicManager();
        this.value              = value;
        this.originalValue      = value;
        this.fieldTable         = fieldTable;
        this.table              = table;
        this.domain             = domain;
        this.type               = null;
        this.primaryKey         = primaryKey;
        // this.isExtensible       = isExtensible;
        this.isAlreadyExtended  = false;

        initBooleanTranslationMap();

    }

    public RpaMnemonic(RpaMainCompositore mainCompositore, String value, String fieldTable, String table, String domain/*, boolean isExtensible*/) throws RpaMnemonicInstanceException {

        this(mainCompositore, value, fieldTable, table, domain, null/*, isExtensible */);

    }

    private void initBooleanTranslationMap() {

        this.booleanTranslationMap = new HashMap<Integer, String>();

        this.booleanTranslationMap.put(1, "Si");
        this.booleanTranslationMap.put(2, "No");

    }

    public void updatePrintValue() {

        // Controllo se estendere il valore
        // if (mnemonicManager.isMnemonicExtensible(this, value)) {
        if (hasToExtend()) {

            isAlreadyExtended = true;
            value = mnemonicManager.getMnemonicExtension(this, value);

        } else {

            // Formatto la stringa con formattazione speciale (caso in cui eliminare le quadre su campi NON estensibili)
            value = mnemonicManager.removeSquareBrackets(this, value);

        }

        // Aggiorno il valore di stampa in base al tipo
        type = getType();

        // Controllo se ho "null"
        if (value == null) {

            printedValue = emptyPrintedValue();

        }

        // Controllo se ho un numero
        else if (type == TYPE_NUMBER) {

            // CASO SPECIALE: Ho un mnemonico di tipo numero ma esso è trattato come booleano
            if (value.equals("f") || value.equals("F") || value.equals("t") || value.equals("T")) {

                switch (value.charAt(0)) {

                    case 'f':
                    case 'F':
                        printedValue = "0";
                        break;

                    case 't':
                    case 'T':
                        printedValue = "1";
                        break;

                }

            }

            else if (RpaNumberUtils.isInteger(value) && !RpaNumberUtils.isIntegerWithDotZero(value)) {

                printedValue = value;

            } else {

                // StyleManager styleManager   = StyleManager.getInstance();
                RpaStyleManager styleManager    = mainCompositore.getStyleManager();
                decimalPrecision                = mnemonicManager.getCountMnemonicDecimals(this);

                if (decimalPrecision == -1) {

                    decimalPrecision = styleManager.getPrecision();

                }

                Double          doubleValue     = Double.valueOf(value);
                BigDecimal      bigDecimal      = BigDecimal.valueOf(doubleValue);

                bigDecimal = bigDecimal.setScale(decimalPrecision, RoundingMode.HALF_UP);

                printedValue = bigDecimal.toPlainString();
                printedValue = printedValue.replaceAll("[^0-9\\-]", styleManager.getDecimalSeparator());

            }

        }

        // Controllo se ho una stringa
        else if (type == TYPE_STRING) {

            printedValue = value;

        }

        // Controllo se ho un booleano
        else if (type == TYPE_BOOLEAN) {

            Integer booleanValue    = Integer.valueOf(value);
            printedValue            = booleanTranslationMap.get(booleanValue);

        }

        // Controllo se ho un tabulato
        else if (type == TYPE_TABULATION) {

            printedValue = mnemonicManager.parseMnemonicTabulationValue(this);

        }

        // Controllo se ho una data
        else if (type == TYPE_DATE) {

            DateFormat dateFormat   = new SimpleDateFormat(DATE_FORMAT_STRING);
            Date currentDate        = java.sql.Date.valueOf(value);
            printedValue            = dateFormat.format(currentDate);

        }

        // Controllo se ho una data-timestamp
        else if (type == TYPE_TIMESTAMP) {

            DateFormat dateFormat       = new SimpleDateFormat(DATE_FORMAT_STRING);
            Timestamp currentTimestamp  = java.sql.Timestamp.valueOf(value);
            printedValue                = dateFormat.format(currentTimestamp);

        }

        // Controllo se ho una valuta
        else if (type == TYPE_MONEY) {

            // Formatto il valore ad un double da 2 cifre decimali
            Double doubleValue      = Double.valueOf(value);
            BigDecimal bigDecimal   = BigDecimal.valueOf(doubleValue);
            bigDecimal              = bigDecimal.setScale(2, RoundingMode.DOWN);
            printedValue            = bigDecimal.toPlainString();

            printedValue = formatSimpleNumber(printedValue);

        }

        // Controllo se è indefinito
        else if (type == TYPE_UNDEFINED) {

            printedValue = value;

        }

        else {

            printedValue = value;

        }

    }

    @Override
    public int getType() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getMnemonicType();

        } else if (type != null) {

            return type;

        }

        // NOTA: Il controllo tabulato prima stava in fondo!
        // E' stato spostato in testa a causa di una incompatibilità con gli applicativi di Alice
        if (mnemonicManager.isMnemonicTabulation(this)) {

            return RpaMnemonic.TYPE_TABULATION;

        } else if (mnemonicManager.isMnemonicString(this)) {

            return RpaMnemonic.TYPE_STRING;

        } else if (mnemonicManager.isMnemonicMoney(this)) {

            return RpaMnemonic.TYPE_MONEY;

        } else if (mnemonicManager.isMnemonicNumber(this)) {

            return RpaMnemonic.TYPE_NUMBER;

        } else if (mnemonicManager.isMnemonicDate(this)) {

            return RpaMnemonic.TYPE_DATE;

        } else if (mnemonicManager.isMnemonicTimestamp(this)) {

            return RpaMnemonic.TYPE_TIMESTAMP;

        } else if (mnemonicManager.isMnemonicBoolean(this)) {

            return RpaMnemonic.TYPE_BOOLEAN;

        /*
        } else if (mnemonicManager.isMnemonicTabulation(this)) {

            return RpaMnemonic.TYPE_TABULATION;

        */

        } else {

            return RpaMnemonic.TYPE_UNDEFINED;

        }

    }

    @Override
    public void setLastFormattedValue(RpaFormat lastFormattedValue) {

        this.lastFormattedValue = lastFormattedValue;

    }

    @Override
    public String getValue() {

        // Controllo se estendere il valore
        // if (mnemonicManager.isMnemonicExtensible(this, value)) {
        if (hasToExtend()) {

            isAlreadyExtended = true;
            value = mnemonicManager.getMnemonicExtension(this, value);

        } else {

            // Formatto la stringa con formattazione speciale (caso in cui eliminare le quadre su campi NON estensibili)
            value = mnemonicManager.removeSquareBrackets(this, value);

        }

        return value;

    }

    @Override
    public Number getValueForMath() {

        // Controllo se estendere il valore
        // if (mnemonicManager.isMnemonicExtensible(this, value)) {
        if (hasToExtend()) {

            isAlreadyExtended = true;
            value = mnemonicManager.getMnemonicExtension(this, value);

        } else {

            // Formatto la stringa con formattazione speciale (caso in cui eliminare le quadre su campi NON estensibili)
            value = mnemonicManager.removeSquareBrackets(this, value);

        }

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueNumber();

        }

        type = getType();

        switch (type) {

            // Se ho una stringa non valida, non restituisco niente
            case TYPE_STRING:

                return null;

            // Se ho un numero o una valuta, lo converto
            case RpaAbstractMnemonic.TYPE_NUMBER:
            case RpaAbstractMnemonic.TYPE_MONEY:

                if (value == null) {

                    return null;

                }

                // CASO SPECIALE: Ho un mnemonico di tipo numero ma esso è trattato come booleano
                else if (value.equals("f") || value.equals("F") || value.equals("t") || value.equals("T")) {

                    switch (value.charAt(0)) {

                        case 'f':
                        case 'F':
                            return 0;

                        case 't':
                        case 'T':
                            return 1;

                    }

                    return null;

                }

                else if (RpaNumberUtils.isInteger(value)) {

                    String newValue = value;

                    if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                        newValue = RpaNumberUtils.integerWithoutDotZero(value);

                    }

                    return Integer.valueOf(newValue);

                }

                else {

                    return Double.valueOf(value);

                }

            case RpaAbstractMnemonic.TYPE_BOOLEAN:

                if (value == null) {

                    return null;

                } else {

                    return Integer.valueOf(value);

                }

            case RpaAbstractMnemonic.TYPE_TABULATION:

                if (value == null) {

                    return null;

                } else {

                    return Integer.valueOf(value);

                }

            case RpaAbstractMnemonic.TYPE_DATE:
            case RpaAbstractMnemonic.TYPE_TIMESTAMP:

                if (value == null) {

                    return null;

                } else {

                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);

                    try {

                        Date originDate = dateFormat.parse(DATE_ORIGIN_STRING);
                        Date currentDate;

                        // Controllo se il valore è un "Timestamp" o un "Date"
                        try {

                            Timestamp currentTimestamp = java.sql.Timestamp.valueOf(value);
                            currentDate = new Date(currentTimestamp.getTime());

                        } catch (Exception exception) {

                            currentDate = java.sql.Date.valueOf(value);

                        }

                        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
                        long millisecondsDifference = currentDate.getTime() - originDate.getTime();

                        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;

                    } catch (ParseException exception) {

                        exception.printStackTrace();
                        throw new ParseCancellationException("Errore conversione data");

                    }

                }

            case RpaAbstractMnemonic.TYPE_UNDEFINED:
            case RpaAbstractMnemonic.TYPE_EMPTY:
            default:

                return null;

        }

    }

    @Override
    public String getComparisonValue() {

        // Controllo se estendere il valore
        // if (mnemonicManager.isMnemonicExtensible(this, value)) {
        if (hasToExtend()) {

            isAlreadyExtended = true;
            value = mnemonicManager.getMnemonicExtension(this, value);

        } else {

            // Formatto la stringa con formattazione speciale (caso in cui eliminare le quadre su campi NON estensibili)
            value = mnemonicManager.removeSquareBrackets(this, value);

        }

        if (lastFormattedValue != null) {

            return lastFormattedValue.getComparisonValue();

        } else {

            // Aggiorno il valore di ritorno in base al tipo
            type = getType();

            // Controllo se ho un numero
            if (type == TYPE_NUMBER) {

                // Controllo se ho "null"
                if (value == null) {

                    // return "0";
                    return null;

                }

                // CASO SPECIALE: Ho un mnemonico di tipo numero ma esso è trattato come booleano
                else if (value.equals("f") || value.equals("F") || value.equals("t") || value.equals("T")) {

                    switch (value.charAt(0)) {

                        case 'f':
                        case 'F':
                            return "0";

                        case 't':
                        case 'T':
                            return "1";

                    }

                    return null;

                }

                else {

                    if (RpaNumberUtils.isDouble(value)) {

                        // StyleManager    styleManager    = StyleManager.getInstance();
                        RpaStyleManager styleManager    = mainCompositore.getStyleManager();
                        Double          doubleValue     = Double.valueOf(value);
                        BigDecimal      bigDecimal      = BigDecimal.valueOf(doubleValue);

                        bigDecimal = bigDecimal.setScale(styleManager.getPrecision(), RoundingMode.HALF_UP);

                        return bigDecimal.toPlainString();
                        // return bigDecimal.toString();

                    } else {

                        return value;

                    }

                }

            }

            // Controllo se ho una stringa
            else if (type == TYPE_STRING) {

                // Controllo se ho "null"
                if (value == null) {

                    return "";

                } else {

                    return value;

                }

            }

            // Controllo se ho un booleano
            else if (type == TYPE_BOOLEAN) {

                // Controllo se ho "null"
                if (value == null || value.isEmpty()) {

                    return "";

                } else {

                    return value;

                }

            }

            // Controllo se ho un tabulato
            else if (type == TYPE_TABULATION) {

                // Controllo se ho "null"
                if (value == null) {

                    return "";

                } else {

                    // return value;
                    updatePrintValue();
                    return printedValue;

                }

            }

            // Controllo se ho una data
            else if (type == TYPE_DATE) {

                if (value == null) {

                    return "";

                } else {

                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);

                    try {

                        Date originDate = dateFormat.parse(DATE_ORIGIN_STRING);
                        Date currentDate = java.sql.Date.valueOf(value);

                        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
                        long millisecondsDifference = currentDate.getTime() - originDate.getTime();
                        long daysDifference         = TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;

                        return String.valueOf(daysDifference);

                    } catch (ParseException exception) {

                        exception.printStackTrace();
                        throw new ParseCancellationException("Errore conversione data");

                    }

                }

            }

            // Controllo se ho una data-timestamp
            else if (type == TYPE_TIMESTAMP) {

                if (value == null) {

                    return "";

                } else {

                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);

                    try {

                        Date        originDate          = dateFormat.parse(DATE_ORIGIN_STRING);
                        Timestamp   currentTimestamp    = java.sql.Timestamp.valueOf(value);

                        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
                        long millisecondsDifference = currentTimestamp.getTime() - originDate.getTime();
                        long daysDifference         = TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;

                        return String.valueOf(daysDifference);

                    } catch (ParseException exception) {

                        exception.printStackTrace();
                        throw new ParseCancellationException("Errore conversione data");

                    }

                }

            }

            // Controllo se ho una valuta
            else if (type == TYPE_MONEY) {

                // Controllo se ho "null"
                if (value == null) {

                    return "0";

                } else {

                    if (RpaNumberUtils.isDouble(value)) {

                        // StyleManager    styleManager    = StyleManager.getInstance();
                        RpaStyleManager styleManager    = mainCompositore.getStyleManager();
                        Double          doubleValue     = Double.valueOf(value);
                        BigDecimal      bigDecimal      = BigDecimal.valueOf(doubleValue);

                        bigDecimal = bigDecimal.setScale(2, RoundingMode.DOWN);

                        return bigDecimal.toPlainString();
                        // return bigDecimal.toString();

                    } else {

                        return value;

                    }

                }

            }

            // Controllo se è indefinito
            else if (type == TYPE_UNDEFINED) {

                // Controllo se ho "null"
                if (value == null) {

                    return "";

                } else {

                    return value;

                }

            }

            else {

                return value;

            }

        }

    }

    @Override
    public String getPrintedValue() {

        // Controllo se estendere il valore
        // if (mnemonicManager.isMnemonicExtensible(this, value)) {
        if (hasToExtend()) {

            isAlreadyExtended = true;
            value = mnemonicManager.getMnemonicExtension(this, value);
            updatePrintValue();

        } else {

            // Formatto la stringa con formattazione speciale (caso in cui eliminare le quadre su campi NON estensibili)
            value = mnemonicManager.removeSquareBrackets(this, value);

        }

        if (lastFormattedValue != null) {

            return lastFormattedValue.getPrintedValue();

        } else {

            if (this.printedValue == null) {

                updatePrintValue();

            }

            return printedValue;

        }

    }

    @Override
    public RpaFormat getLastFormattedValue() {

        return lastFormattedValue;

    }

    @Override
    public String getValueForSTR() {

        // Controllo se estendere il valore
        // if (mnemonicManager.isMnemonicExtensible(this, value)) {
        if (hasToExtend()) {

            isAlreadyExtended = true;
            value = mnemonicManager.getMnemonicExtension(this, value);

        } else {

            // Formatto la stringa con formattazione speciale (caso in cui eliminare le quadre su campi NON estensibili)
            value = mnemonicManager.removeSquareBrackets(this, value);

        }

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueAssignment();

        }

        // Aggiorno il valore di ritorno in base al tipo
        type = getType();

        // Controllo se ho un numero
        if (type == TYPE_NUMBER) {

            // Controllo se ho "null"
            if (value == null) {

                // return "0";
                return null;

            } else {

                return value;

            }

        }

        // Controllo se ho una stringa
        else if (type == TYPE_STRING) {

            // Controllo se ho "null"
            if (value == null) {

                // return "";
                return null;

            } else {

                return value;

            }

        }

        // Controllo se ho un booleano
        else if (type == TYPE_BOOLEAN) {

            // Controllo se ho "null"
            if (value == null) {

                // return "";
                return null;

            } else {

                Integer booleanValue = Integer.valueOf(value);
                return booleanTranslationMap.get(booleanValue);

            }

        }

        // Controllo se ho un tabulato
        else if (type == TYPE_TABULATION) {

            // Controllo se ho "null"
            if (value == null) {

                // return "";
                return null;

            } else {

                return mnemonicManager.parseMnemonicTabulationValue(this);

            }

        }

        // Controllo se ho una data
        else if (type == TYPE_DATE) {

            if (value == null) {

                return null;

            } else {

                DateFormat dateFormat   = new SimpleDateFormat(DATE_FORMAT_STRING);
                Date currentDate        = java.sql.Date.valueOf(value);

                return dateFormat.format(currentDate);

            }

        }

        // Controllo se ho una data/timestamp
        else if (type == TYPE_TIMESTAMP) {

            if (value == null) {

                return null;

            } else {

                DateFormat dateFormat   = new SimpleDateFormat(DATE_FORMAT_STRING);
                Timestamp currentDate   = java.sql.Timestamp.valueOf(value);

                return dateFormat.format(currentDate);

            }

        }

        else if (type == TYPE_MONEY) {

            // Controllo se ho "null"
            if (value == null) {

                // return "0";
                return null;

            } else {

                // return value;
                return getPrintedValue();

            }

        }

        // Controllo se è indefinito
        else if (type == TYPE_UNDEFINED) {

            // Controllo se ho "null"
            if (value == null) {

                // return "";
                return null;

            } else {

                return value;

            }

        }

        else {

            return value;

        }

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> generateValue() {

        if (this.getLastFormattedValue() != null) {

            return this.getLastFormattedValue().generateValue(this);

        } else {

            return new RpaValue<RpaAbstractMnemonic>(this, hasToExtend() || isAlreadyExtended);

        }

    }

    public String getOriginalValue() {

        return originalValue;

    }

    private boolean hasToExtend() {

        try {

            boolean isStartFromSicraweb = mainCompositore.getComposerConfiguration().isStartFromSicraweb();

            if (this.isExtensible == null) {

                this.isExtensible = mainCompositore.getMnemonicManager().isMnemonicExtensible(
                        this.domain, this.table, this.fieldTable, primaryKey
                );

            }

            return isStartFromSicraweb && isExtensible && !isAlreadyExtended && value != null && value.length() >= 2000;

        } catch (RpaInvalidFormatException exception) {

            return false;

        }

    }

    public boolean isExtensible() {

        if (this.isExtensible == null) {

            this.isExtensible = mainCompositore.getMnemonicManager().isMnemonicExtensible(
                    this.domain, this.table, this.fieldTable, primaryKey
            );

        }

        return this.isExtensible;

    }

    public void setName(String name) {

        this.name = name;

    }

    public String getName() {

        return name;

    }

    public String getFieldTable() {

        return fieldTable;

    }

    public String getTable() {

        return table;

    }

    public String getDomain() {

        return domain;

    }

    public String getFullFieldName() {

        return fieldTable.toUpperCase() + "." + table.toUpperCase() + "." + domain.toUpperCase();

    }

    public Long getPrimaryKey() {

        return primaryKey;

    }

    @Override
    public RpaMnemonic clone() {

        if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && primaryKey != null) {

            return new RpaMnemonic(mainCompositore, value, fieldTable, table, domain, primaryKey/*, isExtensible */);

        } else {

            return new RpaMnemonic(mainCompositore, value, fieldTable, table, domain/*, isExtensible */);

        }

    }

}
