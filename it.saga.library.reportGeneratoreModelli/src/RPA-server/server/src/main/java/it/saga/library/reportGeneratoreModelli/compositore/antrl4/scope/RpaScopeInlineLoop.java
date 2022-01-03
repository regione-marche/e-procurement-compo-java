package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.util.ArrayList;
import java.util.List;

public class RpaScopeInlineLoop extends RpaScopeGenericLoop {

    public static final int LOOP_ON_NODE        = 0;
    public static final int LOOP_ON_MANY_NODES  = 1;

    private int     state;
    private boolean isActive;
    private boolean hasNewNodesToAdd;

    private String      wholeCodeString     = "";
    private List<Run>   runNodeCodeList     = new ArrayList<Run>();
    private List<Run>   newRunNodeCodeList  = new ArrayList<Run>();
    private Run         firstRunNode;

    public RpaScopeInlineLoop(Run firstRunNode, RpaMainCompositore mainCompositore) {

        super(INLINE_LOOP_SCOPE_TYPE, mainCompositore);

        this.isActive               = false;
        this.hasNewNodesToAdd       = false;
        this.firstRunNode           = firstRunNode;

    }

    public int getState() {

        return state;

    }

    public void setState(int state) {

        this.state = state;

    }

    public boolean isActive() {

        return isActive;

    }

    public void setActive(boolean active) {

        isActive = active;

    }

    public boolean hasNewNodesToAdd() {

        return hasNewNodesToAdd;

    }

    public void setHasNewNodesToAdd(boolean hasNewNodesToAdd) {

        this.hasNewNodesToAdd = hasNewNodesToAdd;

    }

    public void addCode(String code) {

        // Se ho un loop contenuto nello stesso nodo, aggiungo il codice in una stringa
        if (state == LOOP_ON_NODE) {

            wholeCodeString += code;

        }

        // Se ho un loop contenuto in diversi nodi, associo il codice all'ultimo Nodo Aspose inserito
        else if (state == LOOP_ON_MANY_NODES) {

            if (runNodeCodeList.isEmpty()) {

                try {

                    Run firstRunNodeClone = (Run) firstRunNode.deepClone(true);
                    firstRunNodeClone.setText("");
                    runNodeCodeList.add(firstRunNodeClone);

                } catch (Exception exception) { }

            }

            Run lastRunNode = runNodeCodeList.get(runNodeCodeList.size() - 1);
            String newLastRunNodeText = lastRunNode.getText() + code;

            try { lastRunNode.setText(newLastRunNodeText); } catch (Exception exception) { }

        }

    }

    public String getWholeCodeString() {

        return wholeCodeString;

    }

    public void addRunNode(Run runNode) {

        try { runNode.setText(""); } catch (Exception exception) { }
        runNodeCodeList.add(runNode);

    }

    public List<Run> getRunNodeCodeList() {

        return runNodeCodeList;

    }

    public List<Run> getNewRunNodeCodeList() {

        return newRunNodeCodeList;

    }

    public void setNewRunNodeCodeList(List<Run> newRunNodeCodeList) {

        this.newRunNodeCodeList = newRunNodeCodeList;

    }

}
