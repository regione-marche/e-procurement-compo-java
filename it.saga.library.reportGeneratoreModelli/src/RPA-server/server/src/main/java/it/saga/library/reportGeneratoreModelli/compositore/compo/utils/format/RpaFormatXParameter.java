package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaParametersManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RpaFormatXParameter extends RpaFormat {

    private RpaParametersManager    parametersManager;
    private String                  parameterCode;
    private String                  parameterValue;
    private Number                  parameterValueNumber;
    private DateFormat              dateFormat;
    private Date                    originDate;

    public RpaFormatXParameter(RpaMainCompositore mainCompositore, String parameterCode) {

        super(mainCompositore);

        this.parametersManager  = mainCompositore.getParametersManager();
        this.parameterCode      = parameterCode;
        this.parameterValue     = parametersManager.getValue(parameterCode);
        this.dateFormat         = new SimpleDateFormat("dd.MM.yyyy");

        try { originDate = dateFormat.parse("01.01.1000"); } catch (Exception exception) { }

    }

    @Override
    public String getPrintedValue() {

        if (parameterValue == null || parameterValue.isEmpty()) {

            return emptyPrintedValue();

        } else {

            return parameterValue;

        }

    }

    @Override
    public String getValueAssignment() {

        return parameterValue;

    }

    @Override
    public Number getValueNumber() {

        if (parameterValueNumber != null) {

            return parameterValueNumber;

        } else {

            formatParameterValueNumber();
            return parameterValueNumber;

        }

    }

    @Override
    public String getComparisonValue() {

        return parameterValue;

    }

    @Override
    public String getFormattedValue() {

        return parameterValue;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    private void formatParameterValueNumber() {

        // Controllo se il valore è vuoto
        if (parameterValue != null && !parameterValue.isEmpty()) {

            // Controllo se il valore è un intero
            if (RpaNumberUtils.isInteger(parameterValue)) {

                if (RpaNumberUtils.isIntegerWithDotZero(parameterValue)) {

                    parameterValueNumber = Integer.valueOf(RpaNumberUtils.integerWithoutDotZero(parameterValue));

                } else {

                    parameterValueNumber = Integer.valueOf(parameterValue);

                }

            }

            // Controllo se il valore è un double
            else if (RpaNumberUtils.isDouble(parameterValue)) {

                parameterValueNumber = Double.valueOf(parameterValue);

            }

            // Controllo se il valore è una data
            else {

                boolean isParameterValueDate;
                Date    date = null;

                try {

                    date = dateFormat.parse(parameterValue);
                    isParameterValueDate = true;

                } catch (ParseException exception) {

                    isParameterValueDate = false;

                }

                if (isParameterValueDate) {

                    parameterValueNumber = getDaysDifference(date);

                }

            }

        }

    }

    private long getDaysDifference(Date date) {

        // Aggiungo +1 perchè nella differenza viene perso 1 giorno
        long millisecondsDifference = date.getTime() - originDate.getTime();

        // return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS) + 1;
        return TimeUnit.DAYS.convert(millisecondsDifference, TimeUnit.MILLISECONDS);

    }

}
