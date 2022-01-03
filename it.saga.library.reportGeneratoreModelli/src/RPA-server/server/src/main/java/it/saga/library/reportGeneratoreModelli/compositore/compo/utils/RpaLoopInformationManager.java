package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import java.util.HashMap;
import java.util.Map;

public class RpaLoopInformationManager {

    // private static LoopInformationManager loopInformationManager = new LoopInformationManager();
    // private static LoopInformationManager loopInformationManager;

    private Map<Integer, LoopInformation> loopInformationMap = new HashMap<Integer, LoopInformation>();

    public RpaLoopInformationManager() { }

    /*
    public static void initStaticContent() { loopInformationManager = new LoopInformationManager(); }

    public static LoopInformationManager getLoopInformationManager() {

        return loopInformationManager;

    }
    */

    public void addLoopInformation(Integer loopKeyCode, String mnemonicName) {

        LoopInformation loopInformation = new LoopInformation(loopKeyCode, mnemonicName);
        loopInformationMap.put(loopKeyCode, loopInformation);

    }

    public LoopInformation getLoopInformation(Integer loopKeyCode) {

        return loopInformationMap.get(loopKeyCode);

    }

    public class LoopInformation {

        private Integer loopKeyCode;
        private String  mnemonicName;

        public LoopInformation(Integer loopKeyCode, String mnemonicName) {

            this.loopKeyCode = loopKeyCode;
            this.mnemonicName = mnemonicName;

        }

        public Integer getLoopKeyCode() {

            return loopKeyCode;

        }

        public String getMnemonicName() {

            return mnemonicName;

        }

    }

}
