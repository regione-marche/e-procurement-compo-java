package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class RpaFormatDateElda extends RpaFormatDate {

    private String  stringValue;
    private String  stringFormatted;
    private boolean isConvertedFromInteger;

    public RpaFormatDateElda(RpaMainCompositore mainCompositore, String stringValue) {

        super(mainCompositore);

        if (RpaNumberUtils.isInteger(stringValue)) {

            isConvertedFromInteger = true;

            // Converto da intero a data
            int     countDays       = Double.valueOf(stringValue).intValue();
            Date    convertedDate   = DateUtils.addDays(originDate, countDays);

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

            /*
            if (isConvertedFromInteger) {

                return getDaysDifference() + 1;

            } else {

                return getDaysDifference();

            }
            */

        }

    }

    @Override
    public String getComparisonValue() {

        return String.valueOf(getDaysDifference());
        // return stringFormatted;

    }

    @Override
    public String getFormattedValue() {

        return stringFormatted;

    }

    private void formatValue() {

        // Formatto la stringa
        stringFormatted = stringValue;

        if (stringFormatted == null || stringFormatted.isEmpty()) {

            return;

        }

        else if (stringFormatted.length() < 10) {

            int countSpaceToAdd = 10 - stringFormatted.length();

            for (int i = 0; i < countSpaceToAdd; i ++) {

                stringFormatted += " ";

            }

        }

        stringFormatted = stringValue.substring(0, 2) + "." + stringValue.substring(3, 5) + "." + stringValue.substring(6, 10);

        // Controllo se la stringa formattata è una data valida
        Date date;

        try {

            date = dateFormat.parse(stringFormatted);

        } catch (ParseException exception) {

            date = null;

        }

        value = date;

    }

}
