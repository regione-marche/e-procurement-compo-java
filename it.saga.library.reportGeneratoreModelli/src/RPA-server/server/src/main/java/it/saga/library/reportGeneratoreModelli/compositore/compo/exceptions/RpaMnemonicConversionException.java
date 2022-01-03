package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

public class RpaMnemonicConversionException extends RpaComposerException {

    private RpaErrorType errorType;

    public RpaMnemonicConversionException(RpaComposerConfiguration composerConfiguration, RpaANTLRErrorListener antlrErrorListener, int context, String code, String message, RpaErrorType errorType) {

        super(composerConfiguration, antlrErrorListener, context, code, message);

        this.errorType = errorType;

    }

    @Override
    public RpaErrorType getType() {

        return errorType;

    }

}
