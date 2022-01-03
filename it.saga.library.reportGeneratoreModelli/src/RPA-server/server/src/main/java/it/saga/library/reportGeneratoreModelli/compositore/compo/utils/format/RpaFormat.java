package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public abstract class RpaFormat {

    public static final int TYPE_STRING = 0;
    public static final int TYPE_INT    = 1;
    public static final int TYPE_FLOAT  = 2;
    public static final int TYPE_DATE   = 3;

    public static final int DEFAULT_LENGTH_CHARACTERS_FORMAT = 15;

    protected RpaMainCompositore mainCompositore;

    public RpaFormat(RpaMainCompositore mainCompositore) {

        this.mainCompositore = mainCompositore;

    }

    public abstract String getPrintedValue();

    public abstract String getValueAssignment();

    public abstract Number getValueNumber();

    public abstract String getComparisonValue();

    public abstract String getFormattedValue();

    public abstract int getMnemonicType();

    public String emptyPrintedValue() {

        mainCompositore.getDebugMessages().print("CALL MNEMONIC-FORMAT 'emptyPrintedValue()' WITH [TRAON] = '" + mainCompositore.getStyleManager().isStrokeActive() + "'");
        if (!mainCompositore.getStyleManager().isStrokeActive()) {

            return "";

        }

        String printedValue = "";

        for (int i = 0; i < RpaAbstractMnemonic.NUMBER_EMPTY_CHARACTERS; i ++) {

            printedValue += "_";

        }

        return printedValue;

    }

    public RpaValue<RpaAbstractMnemonic> generateValue(RpaAbstractMnemonic mnemonic) {

        return new RpaValue<RpaAbstractMnemonic>(mnemonic);

    }

    @Override
    public String toString() {

        return getPrintedValue();

    }

}
