package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

public class RpaScopeUndefined extends RpaScope {

    public RpaScopeUndefined(RpaMainCompositore mainCompositore) {

        super(RpaScope.UNDEFINED_SCOPE_TYPE, mainCompositore);

    }

    @Override
    public String toString() {
        return "ScopeUndefined";
    }
}
