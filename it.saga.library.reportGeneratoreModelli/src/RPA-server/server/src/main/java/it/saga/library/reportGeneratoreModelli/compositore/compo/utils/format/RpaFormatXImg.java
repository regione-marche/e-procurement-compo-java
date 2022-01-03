package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaImageValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatXImg extends RpaFormat {

    private String path;

    public RpaFormatXImg(RpaMainCompositore mainCompositore, String path) {

        super(mainCompositore);
        this.path = path;

    }

    @Override
    public String getPrintedValue() {

        return path;

    }

    @Override
    public String getValueAssignment() {

        return path;

    }

    @Override
    public Number getValueNumber() {

        return null;

    }

    @Override
    public String getComparisonValue() {

        return path;

    }

    @Override
    public String getFormattedValue() {

        return path;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> generateValue(RpaAbstractMnemonic mnemonic) {

        RpaImageValue<RpaAbstractMnemonic> newImageValue = new RpaImageValue<RpaAbstractMnemonic>(mnemonic);
        newImageValue.setPath(path);

        return newImageValue;

    }

}
