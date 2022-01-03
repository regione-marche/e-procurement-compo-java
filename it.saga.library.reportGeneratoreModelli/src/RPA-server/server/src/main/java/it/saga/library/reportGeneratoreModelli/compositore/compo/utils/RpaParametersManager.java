package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.util.HashMap;
import java.util.Map;

public class RpaParametersManager {

    private RpaMainCompositore mainCompositore;

    private Map<String, String> valuesMap;
    private Map<String, String> descriptionsMap;

    private long idSession;

    public RpaParametersManager(RpaMainCompositore mainCompositore) {

        this.mainCompositore    = mainCompositore;
        this.valuesMap          = new HashMap<String, String>();
        this.descriptionsMap    = new HashMap<String, String>();

    }

    public void init(long idSession) {

        this.idSession                      = idSession;
        RpaMnemonicManager mnemonicManager  = mainCompositore.getMnemonicManager();

        mnemonicManager.retrieveAndSaveParameters(idSession, valuesMap, descriptionsMap);

    }

    public String getValue(String code) {

        if (valuesMap.containsKey(code)) {

            return valuesMap.get(code);

        } else {

            return null;

        }

    }

    public String getDescription(String code) {

        if (descriptionsMap.containsKey(code)) {

            return descriptionsMap.get(code);

        } else {

            return null;

        }

    }

    public long getIdSession() { return idSession; }
}
