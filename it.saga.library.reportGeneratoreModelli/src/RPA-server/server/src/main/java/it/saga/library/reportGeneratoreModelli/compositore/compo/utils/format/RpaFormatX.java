package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;

public class RpaFormatX extends RpaFormat {

    private String  value;
    private int     type;

    public RpaFormatX(RpaMainCompositore mainCompositore, String value, int type) {

        super(mainCompositore);
        this.value  = value;
        this.type   = type;

    }

    @Override
    public String getPrintedValue() {

        if (value == null) {

            return emptyPrintedValue();

        } else {

            return value;

        }

    }

    @Override
    public String getValueAssignment() {

        return value;

    }

    @Override
    public Number getValueNumber() {

        String newValue = value;

        if (RpaNumberUtils.isInteger(newValue)) {

            if (RpaNumberUtils.isIntegerWithDotZero(newValue)) {

                newValue = RpaNumberUtils.integerWithoutDotZero(newValue);

            }

            return Integer.valueOf(newValue);

        } else if (RpaNumberUtils.isDouble(newValue)) {

            return Double.valueOf(newValue);

        } else {

            return null;

        }

    }

    @Override
    public String getComparisonValue() {

        return value;

    }

    @Override
    public String getFormattedValue() {

        return value;

    }

    @Override
    public int getMnemonicType() {

        return type;

    }

}