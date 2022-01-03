package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import com.aspose.words.Node;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaGenericLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicLoopPreviousStatus;

import java.util.Stack;

public class RpaScopeGenericLoop extends RpaScope {

    /**
     * Indica il nodo visitato. Serve nel caso di loop per ritornare al nodo dove inizia il loop.
     */
    private Node node;

    /**
     * Contenitore con tutte le informazioni di un FOR
     */
    private RpaGenericLoopValue loopValue;

    private int     forId;
    private boolean isSkipNode;
    private int     countInternalLoopToSkip;
    private Type    type;

    private RpaMnemonicLoopPreviousStatus mnemonicLoopPreviousStatus;

    public RpaScopeGenericLoop(int scopeType, RpaMainCompositore mainCompositore) {

        super(scopeType, mainCompositore);

        isSkipNode                  = false;
        countInternalLoopToSkip     = 0;
        mnemonicLoopPreviousStatus  = new RpaMnemonicLoopPreviousStatus();

    }

    public void backupMnemonicContextStack(Stack<RpaScope> scopeStack) {

        mnemonicLoopPreviousStatus.backupMnemonicContextStack(scopeStack);

    }

    public void recoverMnemonicContextStack(Stack<RpaScope> scopeStack) {

        mnemonicLoopPreviousStatus.recoverMnemonicContextStack(scopeStack);

    }


    public boolean isMnemonicMatchEntity(String mnemonicName) {

        return loopValue.isMnemonicMatchEntity(mnemonicName);

    }

    public Node getNode() {

        return node;

    }

    public void setForNode(Node node) {

        this.node = node;

    }

    public RpaGenericLoopValue getLoopValue() {

        return loopValue;

    }

    public void setLoopValue(RpaGenericLoopValue loopValue) {

        this.loopValue = loopValue;

    }

    public int getForId() {

        return forId;

    }

    public void setForId(int forId) {

        this.forId = forId;

    }

    public boolean isSkipNode() {

        return isSkipNode;

    }

    public void setIsSkipNode(boolean isSkipNode) {

        this.isSkipNode = isSkipNode;

    }

    public int getInternalLoopToSkip() {

        return countInternalLoopToSkip;

    }

    public Integer increaseInternalLoopToSkip() {

        return ++ countInternalLoopToSkip;

    }

    public int decreaseInternalLoopToSkip() {

        return -- countInternalLoopToSkip;

    }

    public Type getType() {

        return type;

    }

    public void setType(Type type) {

        this.type = type;

    }

    @Override
    public String toString() {
        return "ScopeLoop";
    }

    public enum Type {
        OLD,
        NEW
    }

}
