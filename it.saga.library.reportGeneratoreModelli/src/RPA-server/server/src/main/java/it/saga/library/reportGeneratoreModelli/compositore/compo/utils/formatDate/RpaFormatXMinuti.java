package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RpaFormatXMinuti extends RpaFormatDate {

    private static final String TIMESTAMP_FORMAT_STRING = "mm";

    private String  stringValue;
    private String  stringFormatted;

    public RpaFormatXMinuti(RpaMainCompositore mainCompositore, String stringValue) {

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

        if (stringValue != null) {

            // Recupero il timestamp dal valore di stringa passato
            super.value = java.sql.Timestamp.valueOf(stringValue);

            // Formatto il valore a "minuti"
            DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_STRING);
            stringFormatted = dateFormat.format(value);

        }

    }

}
