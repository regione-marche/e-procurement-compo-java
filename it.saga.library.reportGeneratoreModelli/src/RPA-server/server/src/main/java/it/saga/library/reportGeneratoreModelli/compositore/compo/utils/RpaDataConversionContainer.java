package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaDataConversionContainer {

    private String              value;
    private RpaFormat           customValueFormat;
    private Integer             type;
    private RpaAbstractMnemonic mnemonic;

    public RpaDataConversionContainer() { }

    public String getValue() {

        return value;

    }

    public void setValue(String value) {

        this.value = value;

    }

    public RpaFormat getCustomValueFormat() {

        return customValueFormat;

    }

    public void setCustomValueFormat(RpaFormat customValueFormat) {

        this.customValueFormat = customValueFormat;

    }

    public int getType() {

        return type;

    }

    public void setType(int type) {

        this.type = type;

    }

    public RpaAbstractMnemonic getMnemonic() {

        return mnemonic;

    }

    public void setMnemonic(RpaAbstractMnemonic mnemonic) {

        this.mnemonic = mnemonic;

    }

    public void clear() {

        value               = null;
        type                = null;
        customValueFormat   = null;
        mnemonic            = null;

    }

    public String getFormattedValue() {

        if (customValueFormat != null) {

            return customValueFormat.getFormattedValue();

        } else {

            return value;

        }

    }

    public String getPrintedFormattedValue() {

        if (customValueFormat != null) {

            return customValueFormat.getPrintedValue();

        } else {

            return value;

        }

    }

    public String getFormattedAssignmentValue() {

        if (customValueFormat != null) {

            return customValueFormat.getValueAssignment();

        } else {

            return value;

        }

    }

    public String getFormattedComparisonValue() {

        if (customValueFormat != null) {

            return customValueFormat.getComparisonValue();

        } else {

            return value;

        }

    }

}
