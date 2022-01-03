package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Rappresenta un singolo mnemonico trovato nel documento o legato ad uno di essi
 */
public class RpaMnemonicNode {

    public static final int ENTITY_TABLE_NAME_INDEX = 0;
    public static final int ENTITY_DOMAIN_INDEX     = 1;

    private RpaMnemonicGraph                        graph;
    private Map<RpaMnemonicEdge, RpaMnemonicNode>   parentNodes;
    private String                                  entityLabel;

    public RpaMnemonicNode(RpaMnemonicGraph graph, String entityLabel) {

        this.parentNodes    = new HashMap<RpaMnemonicEdge, RpaMnemonicNode>();
        this.graph          = graph;
        this.entityLabel    = entityLabel;

    }

    public boolean addNewParentNode(RpaMnemonicNode newNode, RpaMnemonicEdge newEdge) throws IllegalAccessException {

        // Se la connessione con il nuovo padre è già presente, non lo inserisco
        boolean isNewParentConnectionAlreadyPresent = false;

        for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> graphNodeConnection : parentNodes.entrySet()) {

            RpaMnemonicEdge parentEdge = graphNodeConnection.getKey();
            RpaMnemonicNode parentNode = graphNodeConnection.getValue();

            if (parentEdge.equals(newEdge) && parentNode.equals(newNode)) {

                isNewParentConnectionAlreadyPresent = true;

                break;

            }

        }

        if (!isNewParentConnectionAlreadyPresent) {

            parentNodes.put(newEdge, newNode);

            return true;

        } else {

            return false;

        }

    }

    public String getEntityLabel() {

        return entityLabel;

    }

    public String getEntityTableName() {

        return entityLabel.split("\\.")[ENTITY_TABLE_NAME_INDEX];

    }

    public String getEntityDomain() {

        return entityLabel.split("\\.")[ENTITY_DOMAIN_INDEX];

    }

    public Map<RpaMnemonicEdge, RpaMnemonicNode> getParentNodes() {

        return parentNodes;

    }

    @Override
    public String toString() {
        return entityLabel;
    }

    public boolean equals(RpaMnemonicNode mnemonicNode) {

        return this.entityLabel.equals(mnemonicNode.getEntityLabel());

    }

    public RpaMnemonicNode clone(RpaMnemonicGraph graphClone) {

        return new RpaMnemonicNode(graphClone, entityLabel);

    }

}
