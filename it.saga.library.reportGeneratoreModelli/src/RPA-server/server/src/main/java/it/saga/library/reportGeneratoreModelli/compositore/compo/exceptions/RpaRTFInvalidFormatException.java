package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

public class RpaRTFInvalidFormatException extends RpaComposerException {

    public RpaRTFInvalidFormatException(RpaComposerConfiguration composerConfiguration, RpaANTLRErrorListener antlrErrorListener, int context, String code, String message) {

        super(composerConfiguration, antlrErrorListener, context, code, message);

    }

    @Override
    public RpaErrorType getType() {

        return RpaErrorType.RTF_NOT_VALID;

    }

}
