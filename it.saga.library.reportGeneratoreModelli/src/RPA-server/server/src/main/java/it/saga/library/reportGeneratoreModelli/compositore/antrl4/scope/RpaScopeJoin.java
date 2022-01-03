package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaDirectJoinEntityContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicEntityData;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFields;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaPathTreeNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;

import java.util.List;
import java.util.Map;

public class RpaScopeJoin extends RpaScopeMnemonicContext {

    private String  entityLabel;
    private boolean isEmpty;

    public RpaScopeJoin(RpaMainCompositore mainCompositore, String entityLabel, RpaMnemonicEntityData mnemonicEntityData, List<RpaMnemonicFields> mnemonicFieldsList) {

        super(JOIN_SCOPE_TYPE, mainCompositore);

        this.entityLabel    = entityLabel;

        // Inizializzo l'albero dei cammini percorsi
        // super.cachePathTreeNode = mnemonicEntityData.getPathTreeNode();
        super.subScopeMnemonicContext.setPathTreeNode(mnemonicEntityData.getPathTreeNode());

        // Inizializzo la catena di entità
        // super.mnemonicEntityData = mnemonicEntityData;
        super.subScopeMnemonicContext.setMnemonicEntityData(mnemonicEntityData);
        // super.mnemonicEntityData.setMnemonicFieldsList(mnemonicFieldsList);
        super.subScopeMnemonicContext.getMnemonicEntityData().setMnemonicFieldsList(mnemonicFieldsList);

        // Inizializzo il grafo delle entità per questo scope
        // super.mnemonicGraph = mnemonicEntityData.getMnemonicGraph();
        super.subScopeMnemonicContext.setMnemonicGraph(mnemonicEntityData.getMnemonicGraph());

    }

    private RpaScopeJoin(RpaMainCompositore mainCompositore) {

        super(JOIN_SCOPE_TYPE, mainCompositore);

    }

    @Override
    public String toString() {
        return "ScopeJoin";
    }

    @Override
    public RpaScopeJoin clone() {

        /*
        SUPER
        protected RpaMnemonicEntityData mnemonicEntityData;
        protected RpaPathTreeNode       cachePathTreeNode;
        protected RpaMnemonicGraph      mnemonicGraph;

        private Map<String, RpaDirectJoinEntityContext> directJoinEntityContextMap; -> DA INSERIRE ALLA FINE

        private String lastTableWithDomainRead;
        private String lastMnemonicRead;
        private String newTableWithDomainRead;
        private String newMnemonicRead;

        private String lastAccessMnemonicRead;
        private String lastAccessTableNameWithDomainRead;

        private boolean isNewEntityConnectedToPrevious = false;

        private int temporaryId;

        THIS
        private String  entityLabel;
        private boolean isEmpty;
        */

        RpaScopeJoin clone = new RpaScopeJoin(mainCompositore);

        /*
        clone.mnemonicGraph             = this.mnemonicGraph.clone();
        clone.cachePathTreeNode         = this.cachePathTreeNode.clone();
        clone.mnemonicEntityData        = this.mnemonicEntityData.clone(clone.mnemonicGraph, clone.cachePathTreeNode);
        */
        clone.subScopeMnemonicContext = this.subScopeMnemonicContext.clone();

        clone.setLastTableWithDomainRead(this.getLastTableWithDomainRead());
        clone.setLastMnemonicRead(this.getLastMnemonicRead());
        clone.setNewTableWithDomainRead(this.getNewTableWithDomainRead());
        clone.setNewMnemonicRead(this.getNewMnemonicRead());
        clone.setLastAccessMnemonicRead(this.getLastAccessMnemonicRead());
        clone.setLastAccessTableNameWithDomainRead(this.getLastAccessTableNameWithDomainRead());
        clone.setIsNewEntityConnectedToPrevious(this.isNewEntityConnectedToPrevious());
        clone.setTemporaryId(this.getTemporaryId());

        clone.entityLabel   = this.entityLabel;
        clone.isEmpty       = this.isEmpty;

        for (Map.Entry<String, RpaDirectJoinEntityContext> directJoinEntityContextEntry : this.getDirectJoinEntityContextMap().entrySet()) {

            String                      joinId                          = directJoinEntityContextEntry.getKey();
            RpaDirectJoinEntityContext  directJoinEntityContext         = directJoinEntityContextEntry.getValue();
            RpaDirectJoinEntityContext  cloneDirectJoinEntityContext    = directJoinEntityContext.clone();

            clone.getDirectJoinEntityContextMap().put(joinId, cloneDirectJoinEntityContext);

        }

        return clone;

    }

    public String getEntityLabel() {

        return entityLabel;

    }

    public String getTableName() {

        return entityLabel.split("\\.")[1];

    }

    public String getDomainName() {

        return entityLabel.split("\\.")[2];

    }

    public RpaPathTreeNode getCachePathTreeNode() {

        // return cachePathTreeNode;
        return subScopeMnemonicContext.getPathTreeNode();

    }

    public RpaMnemonicEntityData getMnemonicEntityData() {

        // return mnemonicEntityData;
        return subScopeMnemonicContext.getMnemonicEntityData();

    }

    public RpaMnemonicGraph getMnemonicGraph() {

        // return mnemonicGraph;
        return subScopeMnemonicContext.getMnemonicGraph();

    }

    public boolean isEmpty() {

        return isEmpty;

    }

    public void setIsEmpty(boolean isEmpty) {

        this.isEmpty = isEmpty;

    }

}
