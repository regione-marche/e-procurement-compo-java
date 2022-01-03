package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

import com.aspose.words.Document;

public class RpaIncludeFileValue<T> extends RpaValue<T> {

    Document    document;

    public RpaIncludeFileValue(T value) {

        super(value);

    }

    public Document getDocument() { return document; }

    public void setDocument(Document document) { this.document = document; }

}
