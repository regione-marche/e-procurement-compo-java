package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

public class RpaMnemonicEmpty extends RpaAbstractMnemonicConstant {

    public RpaMnemonicEmpty(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

    }

    @Override
    public void setLastFormattedValue(RpaFormat lastFormattedValue) {}

    @Override
    public String getValue() {

        return null;

    }

    @Override
    public String getPrintedValue() {

        return emptyPrintedValue();

    }

    @Override
    public RpaFormat getLastFormattedValue() {

        return null;

    }

    @Override
    public int getType() {

        return TYPE_EMPTY;

    }

    @Override
    public String getValueForSTR() {

        return null;

    }

    @Override
    public String getComparisonValue() {

        return "";

    }

    @Override
    public Number getValueForMath() {

        return null;

    }

}
