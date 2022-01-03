package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RpaFormatXDataF2 extends RpaFormatDate {

    private String  stringValue;
    private String  stringFormatted;
    private boolean isConvertedFromInteger;

    public RpaFormatXDataF2(RpaMainCompositore mainCompositore, String stringValue) {

        super(mainCompositore);

        if (RpaNumberUtils.isInteger(stringValue)) {

            isConvertedFromInteger = true;

            // Converto da intero a data
            int     countDays       = Double.valueOf(stringValue).intValue();
            Date convertedDate   = DateUtils.addDays(originDate, countDays);

            this.stringValue = super.dateFormat.format(convertedDate);

        } else {

            isConvertedFromInteger = false;

            this.stringValue = stringValue;

        }

        formatValue();

    }

    @Override
    public String getPrintedValue() {

        if (stringFormatted == null) {

            return emptyPrintedValue();

        } else {

            return stringFormatted;

        }

    }

    @Override
    public String getValueAssignment() {

        if (value == null) {

            return "";

        } else {

            return stringFormatted;

        }

    }

    @Override
    public Number getValueNumber() {

        if (value == null) {

            return 0;

        } else {

            return getDaysDifference();

        }

    }

    @Override
    public String getComparisonValue() {

        return String.valueOf(getDaysDifference());

    }

    @Override
    public String getFormattedValue() {

        return stringFormatted;

    }

    private void formatValue() {

        // Controllo se la stringa formattata è una data valida
        Date date;

        try {

            date = dateFormat.parse(stringValue);

        } catch (ParseException exception) {

            date = null;

        }

        value = date;

        if (date != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Riformatto la data
            DateFormat newDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            // Creo e salvo la nuova data formattata
            stringFormatted = newDateFormat.format(date);

        }

    }

}
