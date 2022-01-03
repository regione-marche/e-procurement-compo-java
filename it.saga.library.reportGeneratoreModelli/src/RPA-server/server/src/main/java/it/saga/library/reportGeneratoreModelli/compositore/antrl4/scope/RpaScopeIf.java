package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

public class RpaScopeIf extends RpaScope {

    private boolean isSkipNode;
    private boolean isBooleanConditionTrue;
    private boolean isElseFound;
    private Integer ifId;
    private int     countInternalIfToSkip;

    public RpaScopeIf(RpaMainCompositore mainCompositore) {

        super(IF_SCOPE_TYPE, mainCompositore);

        isElseFound             = false;
        ifId                    = null;
        countInternalIfToSkip   = 0;

    }


    public boolean isSkipNode() {

        return isSkipNode;

    }

    public boolean isBooleanConditionTrue() {

        return isBooleanConditionTrue;

    }

    public void setIsNodeToSkip(boolean isSkipNode) {

        this.isSkipNode = isSkipNode;

    }

    public void setBooleanCondition(boolean booleanCondition) {

        this.isBooleanConditionTrue = booleanCondition;

    }

    public boolean isElseFound() {

        return isElseFound;

    }

    public void setIsElseFound(boolean isElseFound) {

        this.isElseFound = isElseFound;

    }

    public Integer getIfId() {

        return ifId;

    }

    public void setIfId(Integer ifId) {

        this.ifId = ifId;

    }

    public int getCountInternalIfToSkip() {

        return countInternalIfToSkip;

    }

    public Integer increaseInternalIfToSkip() {

        return ++ countInternalIfToSkip;

    }

    public int decreaseInternalIfToSkip() {

        return -- countInternalIfToSkip;

    }

    @Override
    public String toString() {
        return "ScopeIf";
    }
}
