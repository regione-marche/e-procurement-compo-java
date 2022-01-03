package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RpaMnemonicConstant extends RpaAbstractMnemonicConstant {

    // TODO: Restituire SEMPRE E SOLO value
    private String      value;
    private RpaFormat   lastFormattedValue;
    private DateFormat  dateFormat;
    private Date        originDate;

    public RpaMnemonicConstant(RpaMainCompositore mainCompositore, String value) {

        super(mainCompositore);
        this.value = value;

    }

    @Override
    public String getValue() {

        return value;

    }

    @Override
    public String getPrintedValue() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getPrintedValue();

        } else {

            return value;

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

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    @Override
    public String getValueForSTR() {

        return value;

    }

    @Override
    public String getComparisonValue() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getComparisonValue();

        } else {

            return value;

        }

    }

    @Override
    public Number getValueForMath() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueNumber();

        }  else if (value != null) {

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

    private long getDaysDifference(Date date) {

        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
        long millisecondsDifference = date.getTime() - originDate.getTime();

        // return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;
        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS);

    }

}
