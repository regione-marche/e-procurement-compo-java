package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaFormatResultSql extends RpaFormat {

    // https://regex101.com/r/ePbq6N/7
    public static final String  NUMBER_FORMAT_REGEX = "((0)|(-?[1-9][0-9]*))[\\.\\,]([0-9]*[^0\\n\\r])?(0*)";
    public static final int     SQL_TYPE_EMPTY      = -999;

    private String      value;
    private int         sqlType;
    private Number      valueNumber;
    private Date        originDate;
    private DateFormat  dateFormat;
    private String      formattedValue;

    public RpaFormatResultSql(RpaMainCompositore mainCompositore, String value, int sqlType) {

        super(mainCompositore);
        this.value      = value;
        this.sqlType    = sqlType;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        try { originDate = dateFormat.parse("01.01.1000"); } catch (Exception exception) { }

    }

    @Override
    public String getPrintedValue() {

        if (value == null || value.isEmpty()) {

            return emptyPrintedValue();

        } else if (formattedValue == null) {

            formatValue();
            return formattedValue;

        } else {

            return formattedValue;

        }

    }

    @Override
    public String getValueAssignment() {

        if (value == null || value.isEmpty()) {

            return value;

        } else {

            formatValue();
            return formattedValue;

        }

    }

    @Override
    public Number getValueNumber() {

        // TODO: La chiamata di questo metodo può generare errori
        // return Double.valueOf(value);

        if (valueNumber != null) {

            return valueNumber;

        } else {

            formatParameterValueNumber();
            return valueNumber;

        }

    }

    @Override
    public String getComparisonValue() {

        return value;

    }

    @Override
    public String getFormattedValue() {

        return value;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    private void formatParameterValueNumber() {

        // Controllo se il valore è vuoto
        if (value != null && !value.isEmpty()) {

            // Controllo se il valore è un intero
            if (RpaNumberUtils.isInteger(value)) {

                if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                    valueNumber = Integer.valueOf(RpaNumberUtils.integerWithoutDotZero(value));

                } else {

                    valueNumber = Integer.valueOf(value);

                }

            }

            // Controllo se il valore è un double
            else if (RpaNumberUtils.isDouble(value)) {

                valueNumber = Double.valueOf(value);

            }

            // Controllo se il valore è una data
            else {

                Date currentDate = null;

                boolean isValueDate         = false;
                boolean isValueTimestamp    = false;

                // Controllo se il valore è un "Timestamp"
                try {

                    Timestamp currentTimestamp = java.sql.Timestamp.valueOf(value);
                    currentDate = new Date(currentTimestamp.getTime());
                    isValueDate = true;

                } catch (Exception exception) {

                    isValueDate = false;

                }

                // Controllo se il valore è un "Date"
                if (!isValueDate) {

                    try {

                        currentDate         = java.sql.Date.valueOf(value);
                        isValueTimestamp    = true;

                    } catch (Exception exception) {

                        isValueTimestamp = false;

                    }

                }

                if (isValueDate || isValueTimestamp) {

                    valueNumber = getDaysDifference(currentDate);

                }

                // In tutti gli altri casi, ritorno "null"
                else {

                    valueNumber = null;

                }

            }

        }

    }

    private void formatValue() {

        RpaStyleManager styleManager = mainCompositore.getStyleManager();

        // Controllo se il valore è vuoto
        if (value != null && !value.isEmpty()) {

            // Controllo se il valore è un numero
            // if (RpaNumberUtils.isInteger(value) || RpaNumberUtils.isDouble(value)) {
            if (    sqlType == Types.INTEGER || sqlType == Types.DECIMAL || sqlType == Types.FLOAT ||
                    sqlType == Types.BIGINT || sqlType == Types.DOUBLE || sqlType == Types.NUMERIC      ) {

                formattedValue = formatValueNumber();

            }

            // Controllo se il valore è una data
            else {

                Date currentDate = null;

                boolean isValueDate         = false;
                boolean isValueTimestamp    = false;

                // Controllo se il valore è un "Timestamp"
                try {

                    Timestamp currentTimestamp = java.sql.Timestamp.valueOf(value);
                    currentDate = new Date(currentTimestamp.getTime());
                    isValueDate = true;

                } catch (Exception exception) {

                    isValueDate = false;

                }

                // Controllo se il valore è un "Date"
                if (!isValueDate) {

                    try {

                        currentDate         = java.sql.Date.valueOf(value);
                        isValueTimestamp    = true;

                    } catch (Exception exception) {

                        isValueTimestamp = false;

                    }

                }

                if (isValueDate || isValueTimestamp) {

                    formattedValue = dateFormat.format(currentDate);

                }

                // In tutti gli altri casi, ritorno "value"
                else {

                    formattedValue = value;

                }

            }

        }

    }

    private String formatValueNumber() {

        // Se ho un intero, lo ritorno
        if (RpaNumberUtils.isInteger(value)) {

            if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                return RpaNumberUtils.integerWithoutDotZero(value);

            } else {

                return value;

            }

        }

        // Altrimenti se ho un Double, lo formatto
        else {

            // Recupero il matcher
            String  valueNumberString   = value;
            Matcher numberFormatMatcher = Pattern.compile(NUMBER_FORMAT_REGEX).matcher(valueNumberString);
            numberFormatMatcher.find();

            // Controllo se ho soli '0' dopo la virgola (nel caso ritorno la sola parte intera)
            if (numberFormatMatcher.group(4) == null || numberFormatMatcher.group(4).isEmpty()) {

                return numberFormatMatcher.group(1);

            }

            // Altrimenti prendo la parte intera e quella decimale fino alla cifra diversa da '0'
            else {

                Character decimalSeparator = mainCompositore.getStyleManager().getDecimalSeparator().charAt(0);
                return numberFormatMatcher.group(1) + decimalSeparator + numberFormatMatcher.group(4);

            }

        }

    }

    private long getDaysDifference(Date date) {

        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
        long millisecondsDifference = date.getTime() - originDate.getTime();

        // return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;
        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS);

    }

}
