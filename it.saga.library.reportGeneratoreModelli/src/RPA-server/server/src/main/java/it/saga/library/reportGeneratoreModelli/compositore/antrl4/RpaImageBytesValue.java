package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

public class RpaImageBytesValue<T> extends RpaValue {

    private byte[] imageByteCode;

    public RpaImageBytesValue(Object value) {

        super(value);

    }

    public byte[] getImageByteCode() {

        return imageByteCode;

    }

    public void setImageByteCode(byte[] imageByteCode) {

        this.imageByteCode = imageByteCode;

    }

}
