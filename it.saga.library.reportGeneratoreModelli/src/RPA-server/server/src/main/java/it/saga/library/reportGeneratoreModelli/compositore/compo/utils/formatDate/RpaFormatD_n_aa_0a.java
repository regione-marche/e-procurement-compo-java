package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RpaFormatD_n_aa_0a extends RpaFormatDate {

    private String stringValue;
    private String stringFormatted;

    public RpaFormatD_n_aa_0a(RpaMainCompositore mainCompositore, String stringValue) {

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

            // Estraggo e formatto il nome del mese
            int     month       = calendar.get(Calendar.MONTH);
            String  monthString = MONTHS_NAME[month];

            // Estraggo e formatto l'anno
            DateFormat  yearDateFormat  = new SimpleDateFormat("yy");
            String      yearString      = yearDateFormat.format(date);
            yearString                  = RpaNumberUtils.wordTranslate(Integer.valueOf(yearString));

            // Creo e salvo la nuova data formattata
            stringFormatted = dayString + " " + monthString + " " + yearString;

        }

    }

}
