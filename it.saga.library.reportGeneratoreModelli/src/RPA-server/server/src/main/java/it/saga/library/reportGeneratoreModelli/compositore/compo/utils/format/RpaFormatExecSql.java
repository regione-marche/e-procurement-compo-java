package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatExecSql extends RpaFormat {

    String value;

    public RpaFormatExecSql(RpaMainCompositore mainCompositore, String value) {

        super(mainCompositore);
        this.value = value;

    }

    @Override
    public String getPrintedValue() {

        if (value == null || value.isEmpty()) {

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

        return Integer.valueOf(value);

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

        return RpaAbstractMnemonic.TYPE_NUMBER;

    }

}
