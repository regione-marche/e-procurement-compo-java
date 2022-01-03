package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

public class RpaMnemonicLoopIndex extends RpaAbstractMnemonicConstant {

    private Integer value;
    private RpaFormat lastFormattedValue;

    public RpaMnemonicLoopIndex(RpaMainCompositore mainCompositore, Integer value) {

        super(mainCompositore);
        this.value = value;

    }

    @Override
    public String getValue() {

        if (value == null) {

            return null;

        } else {

            return value.toString();

        }

    }

    @Override
    public String getPrintedValue() {

        if (lastFormattedValue != null) {

            String formattedValue = lastFormattedValue.getPrintedValue();

            if (formattedValue == null) {

                return emptyPrintedValue();

            } else {

                return formattedValue;

            }

        }

        else if (value == null) {

            return emptyPrintedValue();

        } else {

            return value.toString();

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

        if (lastFormattedValue != null) {

            return lastFormattedValue.getMnemonicType();

        } else {

            return TYPE_NUMBER;

        }

    }

    @Override
    public String getValueForSTR() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueAssignment();

        } else if (value == null) {

            return null;

        } else {

            return value.toString();

        }

    }

    @Override
    public String getComparisonValue() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getComparisonValue();

        } else if (value == null) {

            return "0";

        } else {

            return value.toString();

        }

    }

    @Override
    public Number getValueForMath() {

        if (lastFormattedValue != null) {

            return lastFormattedValue.getValueNumber();

        } else {

            return value;

        }

    }

    public Integer getValueNumber() {

        return value;

    }

}
