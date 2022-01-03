package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * NOTA:    - Per assegnamenti a mnemonici TOT restituisco i secondi dal 1970
 *          - Per assegnamenti a mnemonici STR restituisco la data formattata
 *          - Per la stampa generica restituisco la data formattata
 *
 */
public class RpaMnemonicDateNow extends RpaAbstractMnemonicConstant {

    private Date        originDate;
    private DateFormat  dateFormat;
    private Date        value;
    private RpaFormat   lastFormattedValue;

    public RpaMnemonicDateNow(RpaMainCompositore mainCompositore) {

        super(mainCompositore);
        this.value      = new Date();
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        try { originDate = dateFormat.parse("01.01.1000"); } catch (Exception exception) { }

    }

    @Override
    public String getValue() {

        // return String.valueOf(value.getTime());
        return String.valueOf(getDaysDifference());

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

            return dateFormat.format(value);

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

            return TYPE_DATE;

        }

    }

    @Override
    public String getValueForSTR() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueAssignment();

        } else {


            if (value == null) {

                return null;

            } else {

                return dateFormat.format(value);

            }

        }

    }

    @Override
    public String getComparisonValue() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getComparisonValue();

        } else {

            // return String.valueOf(value.getTime());
            return String.valueOf(getDaysDifference());

        }

    }

    @Override
    public Number getValueForMath() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueNumber();

        } else {

            return getDaysDifference();

        }

    }

    public Date getValueDate() {

        return value;

    }

    public DateFormat getDateFormat() {

        return dateFormat;

    }

    public void setDateFormat(DateFormat dateFormat) {

        this.dateFormat = dateFormat;

    }

    public long getDaysDifference() {

        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
        long millisecondsDifference = value.getTime() - originDate.getTime();

        // return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;
        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS);

    }

}
