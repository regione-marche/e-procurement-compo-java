package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

public class RpaMalformedLoopException extends RpaComposerException {

    public RpaMalformedLoopException(RpaMainCompositore mainCompositore, int context, String code, String message) {

        super(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

    }

    @Override
    public RpaErrorType getType() {

        return RpaErrorType.LOOP_ERROR;

    }

}
