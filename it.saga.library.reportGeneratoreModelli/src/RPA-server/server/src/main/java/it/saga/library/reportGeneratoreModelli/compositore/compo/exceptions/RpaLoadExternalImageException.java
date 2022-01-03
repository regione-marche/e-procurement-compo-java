package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

public class RpaLoadExternalImageException extends RpaComposerException {

    public RpaLoadExternalImageException(RpaComposerConfiguration composerConfiguration, RpaANTLRErrorListener antlrErrorListener, int context, String code, String message) {

        super(composerConfiguration, antlrErrorListener, context, code, message);

    }

    public RpaLoadExternalImageException(RpaComposerConfiguration composerConfiguration, String message, Exception exception) {

        super(composerConfiguration, RpaComposerException.COMPILE_MESSAGE, "{EXT_IMG}", message, exception);

    }

    @Override
    public RpaErrorType getType() {

        return RpaErrorType.LOAD_EXTERNAL_IMAGE_ERROR;

    }

}
