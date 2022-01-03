package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph;

public class RpaMnemonicNodeRoot extends RpaMnemonicNode {

    public static final int MAIN_ENTITY_IDENTITY_NAME_INDEX = 0;
    public static final int MAIN_ENTITY_FIELD_NAME_INDEX    = 1;
    public static final int MAIN_ENTITY_FIELD_VALUE_INDEX   = 2;

    private String rootCondition;

    RpaMnemonicNodeRoot(RpaMnemonicGraph graph, String entityName, String rootCondition) {

        super(graph, entityName);

        this.rootCondition = rootCondition;

    }

    @Override
    public boolean addNewParentNode(RpaMnemonicNode newNode, RpaMnemonicEdge newEdge) throws IllegalAccessException {

        throw new IllegalAccessException("Non si può inserire un nodo superiore al nodo 'root'");

    }

    public void setRootCondition(String rootCondition) {

        this.rootCondition = rootCondition;

    }

    public String getRootCondition() {

        return rootCondition;

    }

    @Override
    public RpaMnemonicNode clone(RpaMnemonicGraph graphClone) {

        return new RpaMnemonicNodeRoot(graphClone, super.getEntityLabel(), rootCondition);

    }

}
