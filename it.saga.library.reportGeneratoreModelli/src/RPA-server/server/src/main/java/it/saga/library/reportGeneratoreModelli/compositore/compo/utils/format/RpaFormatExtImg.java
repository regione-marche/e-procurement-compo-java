package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaImageBytesValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

public class RpaFormatExtImg extends RpaFormat {

    private byte[] imageByteCode;

    public RpaFormatExtImg(RpaMainCompositore mainCompositore, byte[] imageByteCode) {

        super(mainCompositore);
        this.imageByteCode = imageByteCode;

    }

    @Override
    public String getPrintedValue() {

        return imageByteCode.toString();

    }

    @Override
    public String getValueAssignment() {

        return imageByteCode.toString();

    }

    @Override
    public Number getValueNumber() {

        return null;

    }

    @Override
    public String getComparisonValue() {

        return imageByteCode.toString();

    }

    @Override
    public String getFormattedValue() {

        return imageByteCode.toString();

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> generateValue(RpaAbstractMnemonic mnemonic) {

        RpaImageBytesValue<RpaAbstractMnemonic> newImageValue = new RpaImageBytesValue<RpaAbstractMnemonic>(mnemonic);
        newImageValue.setImageByteCode(imageByteCode);

        return newImageValue;

    }

}
