package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaGenericLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaNoMainElementsFoundException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaDirectJoinEntityContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicEntityData;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFields;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaPathTreeNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNodeRoot;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;

import java.util.List;
import java.util.Map;

public class RpaScopeDocument extends RpaScopeMnemonicContext {

    private boolean isAllowedEmptyMnemonicData = true;
    private boolean isMnemonicDataEmpty;

    public RpaScopeDocument(RpaMainCompositore mainCompositore) {

        super(DOCUMENT_SCOPE_TYPE, mainCompositore);

        // MnemonicManager mnemonicManager = MnemonicManager.getMnemonicManager();
        RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

        // Salvo il "MnemonicGraph" principale
        // super.mnemonicGraph = mnemonicManager.getMnemonicGraph();
        super.subScopeMnemonicContext.setMnemonicGraph(mnemonicManager.getMnemonicGraph());

        // Inizializzo l'albero dei cammini percorsi
        /*
        super.cachePathTreeNode = new RpaPathTreeNode(
                mnemonicManager.getMnemonicNodeRoot().getEntityTableName(),
                mnemonicManager.getMnemonicNodeRoot().getEntityDomain()
        );
        */
        super.subScopeMnemonicContext.setPathTreeNode(
                new RpaPathTreeNode(
                        mnemonicManager.getMnemonicNodeRoot().getEntityTableName(),
                        mnemonicManager.getMnemonicNodeRoot().getEntityDomain()
                )
        );


        // Inizializzo la catena di entità
        RpaMnemonicNodeRoot root    = mnemonicManager.getMnemonicNodeRoot();
        // super.mnemonicEntityData    = new RpaMnemonicEntityData(mainCompositore, root.getEntityTableName(), root.getEntityDomain(), mnemonicGraph, cachePathTreeNode);
        super.subScopeMnemonicContext.setMnemonicEntityData(
                new RpaMnemonicEntityData(
                        mainCompositore,
                        root.getEntityTableName(),
                        root.getEntityDomain(),
                        this.subScopeMnemonicContext.getMnemonicGraph(),
                        this.subScopeMnemonicContext.getPathTreeNode()
                )
        );

        // List<RpaMnemonicFields> mnemonicFields = mnemonicManager.getRootMnemonicFields(mnemonicEntityData);
        List<RpaMnemonicFields> mnemonicFields = mnemonicManager.getRootMnemonicFields(subScopeMnemonicContext.getMnemonicEntityData());
        // super.mnemonicEntityData.setMnemonicFieldsList(mnemonicFields);
        super.subScopeMnemonicContext.getMnemonicEntityData().setMnemonicFieldsList(mnemonicFields);

        if (mnemonicFields.isEmpty() && !isAllowedEmptyMnemonicData) {

            String code     = "";
            String message  = "Nessun elemento trovato per '" + root.getRootCondition() + "'";
            int context     = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaNoMainElementsFoundException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        isMnemonicDataEmpty = mnemonicFields.isEmpty();

    }

    private RpaScopeDocument(RpaMainCompositore mainCompositore, boolean isAllowedEmptyMnemonicData, boolean isMnemonicDataEmpty) {

        super(DOCUMENT_SCOPE_TYPE, mainCompositore);

        this.isAllowedEmptyMnemonicData = isAllowedEmptyMnemonicData;
        this.isMnemonicDataEmpty        = isMnemonicDataEmpty;

    }

    @Override
    public RpaMnemonicGraph getMnemonicGraph() {

        // return mnemonicGraph;
        return subScopeMnemonicContext.getMnemonicGraph();

    }

    public RpaPathTreeNode getCachePathTreeNode() {

        // return cachePathTreeNode;
        return subScopeMnemonicContext.getPathTreeNode();

    }

    public RpaMnemonicEntityData getMnemonicEntityData() {

        // return mnemonicEntityData;
        return subScopeMnemonicContext.getMnemonicEntityData();

    }

    @Override
    public int requestMnemonicOccurrenceCount(String mnemonicName, RpaGenericLoopValue loopValue) {

        // Se ho inserito per il documento una entità che non ha dati, esco
        if (isMnemonicDataEmpty) {

            return 0;

        } else {

            return super.requestMnemonicOccurrenceCount(mnemonicName, loopValue);

        }

    }

    @Override
    public RpaAbstractMnemonic requestMnemonic(String mnemonicName, int index) {

        // Se ho inserito per il documento una entità che non ha dati, esco
        if (isMnemonicDataEmpty) {

            return mainCompositore.getMnemonicEmpty();

        } else {

            return super.requestMnemonic(mnemonicName, index);

        }

    }

    @Override
    public RpaAbstractMnemonic requestMnemonic(String mnemonicName) {

        // Se ho inserito per il documento una entità che non ha dati, esco
        if (isMnemonicDataEmpty) {

            return mainCompositore.getMnemonicEmpty();

        } else {

            return super.requestMnemonic(mnemonicName);

        }

    }

    public boolean isMnemonicDataEmpty() { return isMnemonicDataEmpty; }

    @Override
    public String toString() {
        return "ScopeDocument";
    }

    @Override
    public RpaScopeDocument clone() {

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
        */

        RpaScopeDocument clone = new RpaScopeDocument(mainCompositore, isAllowedEmptyMnemonicData, isMnemonicDataEmpty);

        /*
        clone.mnemonicGraph             = this.mnemonicGraph.clone();
        clone.cachePathTreeNode         = this.cachePathTreeNode.clone();
        clone.mnemonicEntityData        = this.mnemonicEntityData.clone(clone.mnemonicGraph, clone.cachePathTreeNode);
        */
        clone.subScopeMnemonicContext   = this.subScopeMnemonicContext.clone();

        clone.setLastTableWithDomainRead(this.getLastTableWithDomainRead());
        clone.setLastMnemonicRead(this.getLastMnemonicRead());
        clone.setNewTableWithDomainRead(this.getNewTableWithDomainRead());
        clone.setNewMnemonicRead(this.getNewMnemonicRead());
        clone.setLastAccessMnemonicRead(this.getLastAccessMnemonicRead());
        clone.setLastAccessTableNameWithDomainRead(this.getLastAccessTableNameWithDomainRead());
        clone.setIsNewEntityConnectedToPrevious(this.isNewEntityConnectedToPrevious());
        clone.setTemporaryId(this.getTemporaryId());

        for (Map.Entry<String, RpaDirectJoinEntityContext> directJoinEntityContextEntry : this.getDirectJoinEntityContextMap().entrySet()) {

            String                      joinId                          = directJoinEntityContextEntry.getKey();
            RpaDirectJoinEntityContext  directJoinEntityContext         = directJoinEntityContextEntry.getValue();
            RpaDirectJoinEntityContext  cloneDirectJoinEntityContext    = directJoinEntityContext.clone();

            clone.getDirectJoinEntityContextMap().put(joinId, cloneDirectJoinEntityContext);

        }

        return clone;

    }

}
