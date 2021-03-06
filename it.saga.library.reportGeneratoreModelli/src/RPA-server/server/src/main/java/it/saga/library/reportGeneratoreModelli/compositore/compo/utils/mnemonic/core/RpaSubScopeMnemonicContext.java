package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNode;

import java.util.List;

public class RpaSubScopeMnemonicContext {

    private RpaMnemonicEntityData   mnemonicEntityData;
    private RpaPathTreeNode         pathTreeNode;
    private RpaMnemonicGraph        mnemonicGraph;
    private String                  lastTableWithDomainRead;

    public RpaMnemonicEntityData getMnemonicEntityData() { return mnemonicEntityData; }

    public void setMnemonicEntityData(RpaMnemonicEntityData mnemonicEntityData) { this.mnemonicEntityData = mnemonicEntityData; }

    public RpaPathTreeNode getPathTreeNode() { return pathTreeNode; }

    public void setPathTreeNode(RpaPathTreeNode pathTreeNode) { this.pathTreeNode = pathTreeNode; }

    public RpaMnemonicGraph getMnemonicGraph() { return mnemonicGraph; }

    public void setMnemonicGraph(RpaMnemonicGraph mnemonicGraph) { this.mnemonicGraph = mnemonicGraph; }

    public String getLastTableWithDomainRead() { return lastTableWithDomainRead; }

    public void setLastTableWithDomainRead(String lastTableWithDomainRead) { this.lastTableWithDomainRead = lastTableWithDomainRead; }

    public boolean isSameMainEntityRead(String tableNameWithDomain) {

        return mnemonicGraph.getRootNode().getEntityLabel().equals(tableNameWithDomain);

    }

    public boolean hasDirectConnectionToRootEntity(String tableNameWithDomain) {

        // Controllo se ho un collegamento con la radice
        if (mnemonicGraph.hasConnectionToRoot(tableNameWithDomain)) {

            mnemonicGraph.checkAndForceConnectionToRoot(tableNameWithDomain);
            pathTreeNode.checkAndForceConnectionToRoot(tableNameWithDomain);
            mnemonicEntityData.checkAndCleanForConnectionToRoot(tableNameWithDomain);

            return true;

        } else {

            return false;

        }

    }

    public boolean hasReadEntity(String tableName) {

        // return mnemonicEntityData.hasData(tableName);
        return getMnemonicEntityData().hasData(tableName);

    }

    public boolean hasDirectEntityConnection(String tableNameWithDomain, RpaMnemonicManager mnemonicManager) throws Exception {

        // Verifico se ho un collegamento con una delle entit?? lette tramite "PathTree"
        RpaPathTreeNode pathTreeNodeFound = pathTreeNode.checkCouldBeEntityConnectedToTree(tableNameWithDomain, mnemonicManager);

        if (pathTreeNodeFound != null) {

            // Forzo la connessione della entit?? passata con quella trovata all'interno del MnemonicGraph
            mnemonicGraph.forceConnection(tableNameWithDomain, pathTreeNodeFound.getEntityLabel());

            return true;

        } else {

            return false;

        }

    }

    public boolean hasDirectConnectionToLastEntityRead(String tableNameWithDomain, RpaMnemonicManager mnemonicManager) {

        // Controllo se l'entit?? ?? gi?? stata letta
        String tableName = tableNameWithDomain.split("\\.")[0];

        if (hasReadEntity(tableName)) {

            return true;

        }

        // Controllo se l'ultima entit?? letta ?? null
        if (lastTableWithDomainRead == null) {

            return false;

        }

        // Controllo se riesco a collegare l'entit?? passata con l'ultima letta
        RpaMnemonicNode lastReadMnemonicNode    = mnemonicGraph.getNode(lastTableWithDomainRead);
        List<String>    parentEntities          = mnemonicManager.getEntitiesBindWithPriority(tableNameWithDomain);

        boolean isNodesConnectible = false;
        for (String parentEntity : parentEntities) {

            if (lastReadMnemonicNode.getEntityLabel().equals(parentEntity)) {

                isNodesConnectible = true;
                break;

            }

        }

        if (!isNodesConnectible) {

            return false;

        } else {

            // Usare un sistema di riconoscimento di collegamento tra le due entit??, e in caso di match ricollegare
            // l'entit?? passata con l'ultima letta

            // Controllo se l'entit?? da collegare esiste gi??, e nel caso la cancello
            mnemonicGraph.delete(tableNameWithDomain);

            // Forzo la connessione tra i due nodi nel mnemonicGraph
            RpaMnemonicNode newMnemonicNode = new RpaMnemonicNode(mnemonicGraph, tableNameWithDomain);

            if (mnemonicGraph.addNode(lastReadMnemonicNode, newMnemonicNode)) {

                // Faccio la stessa cosa nel pathTreeNode
                RpaPathTreeNode parentTreeNode = pathTreeNode.findEntityPathTreeNode(lastReadMnemonicNode.getEntityTableName());
                pathTreeNode.forceConnection(parentTreeNode, newMnemonicNode);

                // Cancello i riferimenti dell'entit?? figlia nel MnemonicEntityData
                mnemonicEntityData.delete(tableNameWithDomain);

                return true;

            }

            return false;

        }

    }

    @Override
    public RpaSubScopeMnemonicContext clone() {

        RpaSubScopeMnemonicContext clone = new RpaSubScopeMnemonicContext();

        clone.mnemonicGraph         = this.mnemonicGraph.clone();
        clone.pathTreeNode          = this.pathTreeNode.clone();
        clone.mnemonicEntityData    = this.mnemonicEntityData.clone(clone.mnemonicGraph, clone.pathTreeNode);

        return clone;

    }

}
