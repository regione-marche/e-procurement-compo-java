package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

public class RpaImageValue<T> extends RpaValue {

    private String path;

    public RpaImageValue(Object value) {

        super(value);

    }

    public String getPath() {

        return path;

    }

    public void setPath(String path) {

        this.path = path;

    }

}
