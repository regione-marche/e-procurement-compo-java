package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;

public class RpaMnemonicDataFunctionContainer {

    private String      mnemonicName;
    private RpaFormat   rpaFormat;

    public String getMnemonicName() { return mnemonicName; }

    public void setMnemonicName(String mnemonicName) { this.mnemonicName = mnemonicName; }

    public RpaFormat getRpaFormat() { return rpaFormat; }

    public void setRpaFormat(RpaFormat rpaFormat) { this.rpaFormat = rpaFormat; }

    public void clean() {

        this.mnemonicName   = null;
        this.rpaFormat      = null;

    }

}
