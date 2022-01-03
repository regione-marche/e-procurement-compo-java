package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RpaMnemonicSTR extends RpaAbstractMnemonicConstant {

    private String      value;
    private RpaFormat   lastFormattedValue;
    private DateFormat  dateFormat;
    private Date        originDate;
    private String      formattedValue;

    public RpaMnemonicSTR(RpaMainCompositore mainCompositore, String value) {

        super(mainCompositore);
        this.value      = value;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        try { originDate = dateFormat.parse("01.01.1000"); } catch (Exception exception) { }

    }

    @Override
    public String getValue() {

        return value;

    }

    @Override
    public String getPrintedValue() {

        if (lastFormattedValue != null) {

            String formattedValue = lastFormattedValue.getPrintedValue();

            if (formattedValue == null) {

                return emptyPrintedValue();

            } else {

                return formattedValue;

            }

        } else {

            if (value == null) {

                return emptyPrintedValue();

            } else {

                if (formattedValue == null) {

                    formatValue();
                    return formattedValue;

                } else {

                    return formattedValue;

                }

            }

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

            return TYPE_STRING;

        }

    }

    @Override
    public String getValueForSTR() {

        if (lastFormattedValue != null) {

            String formattedValue = lastFormattedValue.getValueAssignment();

            if (formattedValue == null) {

                return null;

            } else {

                return formattedValue;

            }

        }

        if (value == null) {

            return null;

        } else {

            return value;

        }

    }

    @Override
    public String getComparisonValue() {

        if (lastFormattedValue != null) {

            String formattedValue = lastFormattedValue.getComparisonValue();

            if (formattedValue == null) {

                return "";

            } else {

                return formattedValue;

            }

        } else {

            if (value == null) {

                return "";

            } else {

                return value;

            }

        }

    }

    @Override
    public Number getValueForMath() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueNumber();

        } else if (value != null) {

            // Controllo se ho un intero
            if (RpaNumberUtils.isInteger(value)) {

                String rawInteger = value;

                if (RpaNumberUtils.isIntegerWithDotZero(rawInteger)) {

                    rawInteger = RpaNumberUtils.integerWithoutDotZero(rawInteger);

                }

                return Integer.valueOf(rawInteger);

            // Controllo se ho un double
            } else if (RpaNumberUtils.isDouble(value)) {

                return Double.valueOf(value);

            // Controllo se ho una data
            } else {

                boolean isParameterValueDate;
                Date date = null;

                try {

                    date = dateFormat.parse(value);
                    isParameterValueDate = true;

                } catch (ParseException exception) {

                    isParameterValueDate = false;

                }

                if (isParameterValueDate) {

                    return getDaysDifference(date);

                } else {

                    return null;

                }

            }

        } else {

            return null;

        }

    }

    private void formatValue() {

        RpaStyleManager styleManager = mainCompositore.getStyleManager();

        // Controllo se il valore è vuoto
        if (value != null && !value.isEmpty()) {

            // Controllo se il valore è un intero
            if (RpaNumberUtils.isInteger(value)) {

                formattedValue = value;

            }

            // Controllo se il valore è un double
            else if (RpaNumberUtils.isDouble(value)) {

                formattedValue = value.replaceAll("\\.", styleManager.getDecimalSeparator());

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

        } else {

            // Nel caso di valore vuoto ritorno la stampa vuota
            formattedValue = emptyPrintedValue();

        }

    }

    private long getDaysDifference(Date date) {

        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
        long millisecondsDifference = date.getTime() - originDate.getTime();

        // return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;
        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS);

    }

}
