package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class RpaFormatD_0_aa_00 extends RpaFormatDate {

    private String stringValue;
    private String stringFormatted;

    public RpaFormatD_0_aa_00(RpaMainCompositore mainCompositore, String stringValue) {

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

            // Estraggo e formatto il nome del mese
            int     month       = calendar.get(Calendar.MONTH);
            String  monthString = MONTHS_NAME[month];

            // Creo e salvo la nuova data formattata
            stringFormatted = monthString;

        }

    }

}
