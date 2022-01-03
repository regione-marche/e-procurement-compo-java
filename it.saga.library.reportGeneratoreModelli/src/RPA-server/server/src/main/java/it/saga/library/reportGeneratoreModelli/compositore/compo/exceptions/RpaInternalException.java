package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

public class RpaInternalException extends RpaComposerException {

    private Exception               internalException;
    private RpaANTLRErrorListener   antlrErrorListener;

    public RpaInternalException(RpaComposerConfiguration composerConfiguration, RpaANTLRErrorListener antlrErrorListener, int context, Exception exception) {

        super(composerConfiguration, context, "", exception.getMessage(), exception);

        this.internalException  = exception;
        this.antlrErrorListener = antlrErrorListener;

    }

    @Override
    public RpaErrorType getType() {

        return RpaErrorType.INTERNAL_ERROR;

    }

    @Override
    public String toString() {

        String errorMessage = "InternalException{" +
                "internalException=" + internalException +
                '}';

        if (!antlrErrorListener.getSyntaxErrorList().isEmpty()) {

            for (String syntaxError: antlrErrorListener.getSyntaxErrorList()) {

                errorMessage += "\n\n" + syntaxError;

            }

            errorMessage += "\n";

        }

        return errorMessage;

    }
}
