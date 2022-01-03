package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatXExecAgg extends RpaFormat {

    private int value;

    public RpaFormatXExecAgg(RpaMainCompositore mainCompositore, int value) {

        super(mainCompositore);
        this.value = value;

    }

    @Override
    public String getPrintedValue() {
        return super.emptyPrintedValue();
    }

    @Override
    public String getValueAssignment() {
        return String.valueOf(value);
    }

    @Override
    public Number getValueNumber() {
        return value;
    }

    @Override
    public String getComparisonValue() {
        return String.valueOf(value);
    }

    @Override
    public String getFormattedValue() {
        return super.emptyPrintedValue();
    }

    @Override
    public int getMnemonicType() {
        return RpaAbstractMnemonic.TYPE_NUMBER;
    }

}
