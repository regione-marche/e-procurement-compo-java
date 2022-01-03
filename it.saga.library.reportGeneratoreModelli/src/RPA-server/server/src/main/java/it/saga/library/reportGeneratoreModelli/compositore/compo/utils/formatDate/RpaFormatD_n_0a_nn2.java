package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class RpaFormatD_n_0a_nn2 extends RpaFormatDate {

    private String stringValue;
    private String stringFormatted;

    public RpaFormatD_n_0a_nn2(RpaMainCompositore mainCompositore, String stringValue) {

        super(mainCompositore);
        this.stringValue = stringValue;

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

            // Estraggo il giorno, mese ed anno
            String dayString    = dayFormat.format(date);
            String yearString   = yearFormat.format(date);

            // Estraggo e formatto il nome del mese
            int     month       = calendar.get(Calendar.MONTH);
            String  monthString = MONTHS_NAME[month];
            monthString = monthString.substring(0, 3);

            // Creo e salvo la nuova data formattata
            stringFormatted = dayString + " " + monthString + " " + yearString;

        }

    }

}
