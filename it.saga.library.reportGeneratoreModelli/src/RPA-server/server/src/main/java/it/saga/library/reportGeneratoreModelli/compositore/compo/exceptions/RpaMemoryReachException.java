package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

public class RpaMemoryReachException  extends RpaComposerException {

    public RpaMemoryReachException(RpaComposerConfiguration composerConfiguration, RpaANTLRErrorListener antlrErrorListener, int context, String code, String message) {

        super(composerConfiguration, antlrErrorListener, context, code, message);

    }

    @Override
    public RpaErrorType getType() {

        return RpaErrorType.REACH_MEMORY_LIMIT;

    }

}
