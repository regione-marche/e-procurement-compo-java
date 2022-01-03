package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class RpaFormatDate extends RpaFormat {

    protected Date          originDate;
    protected DateFormat    dateFormat;
    protected Date          value;
    protected DateFormat    dayFormat;
    protected DateFormat    monthFormat;
    protected DateFormat    yearFormat;

    public static final String[] MONTHS_NAME = {
            "gennaio",  "febbraio", "marzo",
            "aprile",   "maggio",   "giugno",
            "luglio",   "agosto",   "settembre",
            "ottobre",  "novembre", "dicembre"
    };


    public RpaFormatDate(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

        this.dateFormat     = new SimpleDateFormat("dd.MM.yyyy");
        this.dayFormat      = new SimpleDateFormat("dd");
        this.monthFormat    = new SimpleDateFormat("MM");
        this.yearFormat     = new SimpleDateFormat("yyyy");

        try { originDate = dateFormat.parse("01.01.1000"); } catch (Exception exception) { }

    }

    public long getDaysDifference() {

        long millisecondsDifference = value.getTime() - originDate.getTime();

        // return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;
        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS);

    }

    public Date getValue() {

        return value;

    }

    public void setValue(Date value) {

        this.value = value;

    }

    public TYPE getType(String value) {

        // Controllo se la stringa è vuota
        if (value == null || value.isEmpty()) {

            return TYPE.INVALID;

        }

        else {

            // Controllo se la stringa è un Timestamp
            try {

                Timestamp timestamp = java.sql.Timestamp.valueOf(value);

                return TYPE.TIMESTAMP;

            } catch (IllegalArgumentException exception) {}

            // Controllo se la stringa è un Date
            try {

                Date date = java.sql.Date.valueOf(value);

                return TYPE.DATE;

            } catch (IllegalArgumentException exception) {}

            // Controllo se la stringa è una data in formata Elda (dd.mm.yyyy)
            try {

                Date date = dateFormat.parse(value);

                return TYPE.ELDA;

            } catch (ParseException exception) {}

            // Negli altri casi, la data non è valida
            return TYPE.INVALID;

        }

    }

    public Timestamp convertToTimestamp(String value) {

        TYPE type = getType(value);

        if (type != TYPE.TIMESTAMP) {

            return null;

        } else {

            return java.sql.Timestamp.valueOf(value);

        }

    }

    public Date convertToDate(String value) {

        TYPE type = getType(value);

        if (type != TYPE.DATE) {

            return null;

        } else {

            return java.sql.Date.valueOf(value);

        }

    }

    public Date convertToDateElda(String value) {

        TYPE type = getType(value);

        if (type != TYPE.ELDA) {

            return null;

        } else {

            try { return dateFormat.parse(value); } catch (Exception exception) {}

            return null;

        }

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_DATE;

    }

    public enum TYPE {
        ELDA,
        DATE,
        TIMESTAMP,
        INVALID
    }

}
