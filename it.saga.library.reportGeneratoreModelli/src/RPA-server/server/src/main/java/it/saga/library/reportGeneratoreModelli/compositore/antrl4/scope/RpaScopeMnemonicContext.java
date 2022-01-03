package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaGenericLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaReachEntityException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaDirectJoinEntityContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicEntityData;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicLoopPreviousStatus;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaPathTreeNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaSubScopeMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public abstract class RpaScopeMnemonicContext extends RpaScope {

    /*
    protected RpaMnemonicEntityData mnemonicEntityData;
    protected RpaPathTreeNode       cachePathTreeNode;
    protected RpaMnemonicGraph      mnemonicGraph;
    */
    protected RpaSubScopeMnemonicContext subScopeMnemonicContext;

    private Map<String, RpaDirectJoinEntityContext> directJoinEntityContextMap;

    private String lastTableWithDomainRead;
    private String lastMnemonicRead;
    private String newTableWithDomainRead;
    private String newMnemonicRead;

    private String lastAccessMnemonicRead;
    private String lastAccessTableNameWithDomainRead;

    private RpaSubScopeMnemonicContext selectedSubScopeMnemonicContext;

    private boolean isNewEntityConnectedToPrevious = false;

    private int temporaryId;

    public RpaScopeMnemonicContext(int scopeType, RpaMainCompositore mainCompositore) {

        super(scopeType, mainCompositore);

        subScopeMnemonicContext     = new RpaSubScopeMnemonicContext();
        directJoinEntityContextMap  = new LinkedHashMap<String, RpaDirectJoinEntityContext>();

    }

    public abstract RpaMnemonicGraph getMnemonicGraph();

    public abstract RpaPathTreeNode getCachePathTreeNode();

    public abstract RpaMnemonicEntityData getMnemonicEntityData();

    public int getTemporaryId() { return temporaryId; }

    public void setTemporaryId(int temporaryId) { this.temporaryId = temporaryId; }

    public String getLastMnemonicRead() { return lastMnemonicRead; }

    public String getLastTableWithDomainRead() { return lastTableWithDomainRead; }

    public Map<String, RpaDirectJoinEntityContext> getDirectJoinEntityContextMap() { return directJoinEntityContextMap; }

    public boolean isSameMainEntityRead(String mnemonicName, String mnemonicEntityName) {

        // Recupero l'entità del mnemonico
        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

        // Controllo se l'entità del mnemonico è la stessa di una delle JOIN dirette
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

            if (directJoinEntityContext.isSameMainEntityRead(tableNameWithDomain)) {

                // lastMnemonicRead                = mnemonicName;
                // lastTableWithDomainRead         = tableNameWithDomain;
                selectedSubScopeMnemonicContext = directJoinEntityContext;
                selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
                return true;

            }

        }

        // Controllo se l'entità del mnemonico è la stessa del contesto attuale
        if (subScopeMnemonicContext.isSameMainEntityRead(tableNameWithDomain)) {

            // lastMnemonicRead                = mnemonicName;
            // lastTableWithDomainRead         = tableNameWithDomain;
            selectedSubScopeMnemonicContext = subScopeMnemonicContext;
            selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
            return true;

        }

        return false;

    }

    public boolean isConnectedLastReadEntity(String mnemonicName, String mnemonicEntityName) {

        // Controllo di aver letto un mnemonico in questo scope
        if (selectedSubScopeMnemonicContext == null) {

            return false;

        }

        // Recupero l'entità del mnemonico
        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

        // Verifico se ho un collegamento diretto con l'ultima entità letta
        // if (selectedSubScopeMnemonicContext.hasDirectConnectionToRootEntity(tableNameWithDomain)) {
        if (selectedSubScopeMnemonicContext.hasDirectConnectionToLastEntityRead(tableNameWithDomain, mainCompositore.getMnemonicManager())) {

            selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);

            return true;

        } else {

            return false;

        }

    }

    public boolean hasEntityRead(String mnemonicName, String mnemonicEntityName) {

        // Recupero l'entità del mnemonico
        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

        // Verifico se l'entità è stata letta (tramite il PathTree) tra le varie join con parentesi
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext>   directJoinContextEntry  = iterator.previous();
            RpaDirectJoinEntityContext                      directJoinContext       = directJoinContextEntry.getValue();

            if (directJoinContext.hasReadEntity(tableNameWithDomain.split("\\.")[0])) {

                selectedSubScopeMnemonicContext = directJoinContext;
                selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
                return true;

            }

        }

        if (subScopeMnemonicContext.hasReadEntity(tableNameWithDomain.split("\\.")[0])) {

            selectedSubScopeMnemonicContext = subScopeMnemonicContext;
            selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
            return true;

        }

        return false;

    }

    public boolean hasEntityMainDirectConnection(String mnemonicName, String mnemonicEntityName) {

        // Recupero l'entità del mnemonico
        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

        // Cerco ove l'entità ha un collegamento DIRETTO con una delle entità PRINCIPALI di ogni sotto-contesto
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext>   directJoinContextEntry  = iterator.previous();
            RpaDirectJoinEntityContext                      directJoinContext       = directJoinContextEntry.getValue();

            if (directJoinContext.hasDirectConnectionToRootEntity(tableNameWithDomain)) {

                selectedSubScopeMnemonicContext = directJoinContext;
                selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
                return true;

            }

        }

        if (subScopeMnemonicContext.hasDirectConnectionToRootEntity(tableNameWithDomain)) {

            selectedSubScopeMnemonicContext = subScopeMnemonicContext;
            selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
            return true;

        }

        return false;

    }

    public boolean hasEntitySomeDirectConnection(String mnemonicName, String mnemonicEntityName) throws Exception {

        // Recupero l'entità del mnemonico
        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

        // Cerco ove l'entità ha un collegamento DIRETTO con una delle entità di ogni contesto
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext>   directJoinContextEntry  = iterator.previous();
            RpaDirectJoinEntityContext                      directJoinContext       = directJoinContextEntry.getValue();

            if (directJoinContext.hasDirectEntityConnection(tableNameWithDomain, mainCompositore.getMnemonicManager())) {

                selectedSubScopeMnemonicContext = directJoinContext;
                selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
                return true;

            }

        }

        if (subScopeMnemonicContext.hasDirectEntityConnection(tableNameWithDomain, mainCompositore.getMnemonicManager())) {

            selectedSubScopeMnemonicContext = subScopeMnemonicContext;
            selectedSubScopeMnemonicContext.setLastTableWithDomainRead(tableNameWithDomain);
            return true;

        }

        return false;

    }

    public boolean hasEntitySomeConnection(String mnemonicName, String mnemonicEntityName) {

        // TODO: Da completare seguendo il MnemonicGraph
        return false;

    }

    public boolean isGoodForMnemonic(String mnemonicName, String mnemonicEntityName) {

        // ###  Recupero l'entità del mnemonico                                                                             ###

        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];


        // ###  Controllo se l'entità del mnemonico è la stessa dell'entità precedentemente letta                           ###

        if (lastTableWithDomainRead != null && lastTableWithDomainRead.equals(tableNameWithDomain)) {

            newMnemonicRead = mnemonicName;

            isNewEntityConnectedToPrevious = false;

            return true;

        }


        // ###  Controllo se l'entità del mnemonico si collega a quella precedentemente letta                               ###

        if (lastTableWithDomainRead != null) {

            List<String> entityBindList = mnemonicManager.getEntitiesBindWithPriority(tableNameWithDomain);

            for (String entityBind : entityBindList) {

                // Tolgo il suffisso numerico dal campo stringa
                String parentTableNameWithDomain = entityBind.replaceAll("#[0-9]+$", "");

                if (lastTableWithDomainRead.equals(parentTableNameWithDomain)) {

                    newMnemonicRead         = mnemonicName;
                    newTableWithDomainRead  = tableNameWithDomain;

                    isNewEntityConnectedToPrevious = true;

                    return true;

                }

            }

        }


        // ###  Verifico se ho dati in cache del mnemonico                                                                  ###

        if (this.hasReadEntity(tableNameWithDomain)) {

            newMnemonicRead         = mnemonicName;
            newTableWithDomainRead  = tableNameWithDomain;

            isNewEntityConnectedToPrevious = false;

            return true;

        }


        // ###  Verifico se l'entità principale (radice) è la stessa del mnemonico                                          ###

        if (this.hasEntityRoot(tableNameWithDomain)) {

            newMnemonicRead         = mnemonicName;
            newTableWithDomainRead  = tableNameWithDomain;

            isNewEntityConnectedToPrevious = false;

            return true;

        }


        // ###  Verifico se l'entità del mnemonico ha un collegamento prioritario e diretto con questo ScopeMnemonicContext ###

        List<String> entityBindList = mnemonicManager.getEntitiesBindWithPriority(tableNameWithDomain);

        for (String entityBind : entityBindList) {

            if (this.hasDirectConnection(entityBind)) {

                newMnemonicRead         = mnemonicName;
                newTableWithDomainRead  = tableNameWithDomain;

                isNewEntityConnectedToPrevious = false;

                return true;

            }

        }


        // ###  Verifico se riesco a collegare in maniera diretta l'entità ad una delle entità già presenti     ###

        if (this.hasEntityConnection(tableNameWithDomain)) {

            newMnemonicRead         = mnemonicName;
            newTableWithDomainRead  = tableNameWithDomain;

            isNewEntityConnectedToPrevious = false;

            return true;

        }


        // ###  Se non sono riuscito ad allacciare il mnemonico, ritorno false                                  ###

        isNewEntityConnectedToPrevious = false;

        return false;

    }

    public boolean hasReadEntity(String tableNameWithDomain) {

        // Estraggo il nome della tabella
        String tableName = tableNameWithDomain.split("\\.")[0];

        // Controllo in tutti i "MnemonicEntityData" dei "DirectJoinEntityContext" l'entità passata
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

            if (directJoinEntityContext.hasReadEntity(tableName)) {

                return true;

            }

        }

        // Controllo in tutti i nodi del "MnemonicEntityData" di questo scope, se c'è l'entità passata
        // return mnemonicEntityData.hasData(tableName);
        return subScopeMnemonicContext.getMnemonicEntityData().hasData(tableName);

    }

    public boolean hasEntityRoot(String tableNameWithDomain) {

        // Controllo in tutti i "MnemonicGraph" dei "DirectJoinEntityContext" se la radice è l'entità passata
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext     = mapEntry.getValue();

            if (directJoinEntityContext.hasEntityRoot(tableNameWithDomain)) {

                return true;

            }

        }

        // Controllo nel "MnemonicGraph" se la radice è l'entità passata
        // return mnemonicGraph.getRootNode().getEntityLabel().equals(tableNameWithDomain);
        return subScopeMnemonicContext.getMnemonicGraph().getRootNode().getEntityLabel().equals(tableNameWithDomain);

    }

    public boolean hasDirectConnection(String fullEntityName) {

        // Tolgo il suffisso numerico dal campo stringa
        String entityNameWithDomain = fullEntityName.replaceAll("#[0-9]+$", "");

        // Verifico se l'entità passata combacia con la radice delle join dirette, e nel caso, di questo "Scope"
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext     = mapEntry.getValue();

            if (directJoinEntityContext.hasEntityRoot(entityNameWithDomain)) {

                return true;

            }

        }

        // Controllo nel "MnemonicGraph" se la radice è l'entità passata
        // return mnemonicGraph.getRootNode().getEntityLabel().equals(entityNameWithDomain);
        return subScopeMnemonicContext.getMnemonicGraph().getRootNode().getEntityLabel().equals(entityNameWithDomain);

    }

    public boolean hasEntityConnection(String tableNameWithDomain) {

        // Controllo se riesco a connettere l'entità al "MnemonicGraph" di uno dei "DirectJoinEntityContext"
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

            // if (directJoinEntityContext.hasEntityConnection(tableNameWithDomain)) {
            if (directJoinEntityContext.hasDirectEntityConnectionOld(tableNameWithDomain)) {

                return true;

            }

        }

        // Controllo in tutti i nodi del "MnemonicGraph" di questo scope, se c'è l'entità passata (e nel caso, l'aggiungo)
        // boolean isEntityPresentInGraph = mnemonicGraph.isEntityPresent(tableNameWithDomain);
        boolean isEntityPresentInGraph = subScopeMnemonicContext.getMnemonicGraph().isEntityPresent(tableNameWithDomain);
        if (isEntityPresentInGraph) {

            return true;

        } else {

            // RpaMnemonicNode newNode = new RpaMnemonicNode(mnemonicGraph, tableNameWithDomain);
            RpaMnemonicNode newNode = new RpaMnemonicNode(subScopeMnemonicContext.getMnemonicGraph(), tableNameWithDomain);
            // return mnemonicGraph.addNode(newNode);
            return subScopeMnemonicContext.getMnemonicGraph().addNode(newNode);

        }

    }

    public void updateJoinsContext(String tableName, String tableDomain, String whereConditionSql, Map<String, RpaMnemonic> values, Map<String, Integer> valuesType) {

        // Creo un nuovo "DirectJoinEntityContext"
        RpaDirectJoinEntityContext newDirectJoinEntityContext = new RpaDirectJoinEntityContext(
                mainCompositore,
                tableName,
                tableDomain,
                whereConditionSql,
                values,
                valuesType
        );

        // Verifico se una join è già stata definita per l'entità passata in ingresso
        if (directJoinEntityContextMap.containsKey(tableName)) {

            // Controllo che la query di aggiornamento sia diversa
            RpaDirectJoinEntityContext directJoinEntityContext = directJoinEntityContextMap.get(tableName);
            if (!directJoinEntityContext.isMatchQueryParameters(tableName, tableDomain, whereConditionSql)) {

                // Se l'ho trovato, elimino il vecchio e metto il nuovo in coda
                String loopBindName = directJoinEntityContext.getMnemonicEntityData().getLoopBindName();
                newDirectJoinEntityContext.getMnemonicEntityData().setLoopBindName(loopBindName);
                directJoinEntityContextMap.remove(tableName);
                directJoinEntityContextMap.put(tableName, newDirectJoinEntityContext);

            }

        }

        else {

            // Se non l'ho trovato, aggiungo il nuovo in coda
            directJoinEntityContextMap.put(tableName, newDirectJoinEntityContext);

        }

    }

    public int requestMnemonicOccurrenceCount(String mnemonicName, RpaGenericLoopValue loopValue) {

        MnemonicDataAccessSet mnemonicDataAccessSet = extractMnemonicDataAccessSet(mnemonicName);
        return extractMnemonicOccurrenceCount(mnemonicDataAccessSet, loopValue);

    }

    public RpaAbstractMnemonic requestMnemonic(String mnemonicName, int index) {

        MnemonicDataAccessSet mnemonicDataAccessSet = extractMnemonicDataAccessSet(mnemonicName);
        return extractMnemonic(mnemonicDataAccessSet, index);

    }

    public RpaAbstractMnemonic requestMnemonic(String mnemonicName) {

        MnemonicDataAccessSet mnemonicDataAccessSet = extractMnemonicDataAccessSet(mnemonicName);
        return extractMnemonic(mnemonicDataAccessSet);

    }

    private MnemonicDataAccessSet extractMnemonicDataAccessSet(String mnemonicName) {

        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              mnemonicEntityName  = mnemonicManager.translateMnemonicName(mnemonicName);

        String fieldName    = mnemonicEntityName.split("\\.")[0];
        String tableName    = mnemonicEntityName.split("\\.")[1];
        String domainName   = mnemonicEntityName.split("\\.")[2];

        fieldName = fieldName.toLowerCase();

        return new MnemonicDataAccessSet(
                selectedSubScopeMnemonicContext.getMnemonicEntityData(),
                selectedSubScopeMnemonicContext.getPathTreeNode(),
                mnemonicName,
                fieldName
        );

    }

    /**
     * Cerca il contesto migliore (Contesto di una join implicita o questo scope)
     * da cui richiedere i dati del mnemonico
     * @param mnemonicName
     * @return
     */
    private MnemonicDataAccessSet extractMnemonicDataAccessSetOld(String mnemonicName) {

        // Estraggo l'entità del dominio
        // MnemonicManager mnemonicManager     = MnemonicManager.getMnemonicManager();
        RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
        String              mnemonicEntityName  = mnemonicManager.translateMnemonicName(mnemonicName);

        String fieldName    = mnemonicEntityName.split("\\.")[0];
        String tableName    = mnemonicEntityName.split("\\.")[1];
        String domainName   = mnemonicEntityName.split("\\.")[2];

        fieldName = fieldName.toLowerCase();

        // Preparo il dataset da scorrere
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        // ###      Cerco ove l'entità precedente è la stessa del mnemonico         ###
        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext     = mapEntry.getValue();

            String joinEntityLastTableWithDomainRead = directJoinEntityContext.getLastTableNameWithDomainRead();

            if (joinEntityLastTableWithDomainRead != null && joinEntityLastTableWithDomainRead.equals(tableName + "." + domainName)) {

                // TODO: Non sarebbe corretto forzare a false la condizione "isNewEntityConnectedToPrevious" a "false"
                // TODO: Questo potrebbe creare problemi con le [JOIN]
                isNewEntityConnectedToPrevious = false;

                return new MnemonicDataAccessSet(
                        directJoinEntityContext.getMnemonicEntityData(),
                        directJoinEntityContext.getPathTreeNode(),
                        mnemonicName,
                        fieldName
                );

            }

        }

        if (lastAccessTableNameWithDomainRead != null && lastAccessTableNameWithDomainRead.equals(tableName + "." + domainName)) {

            /*
            return new MnemonicDataAccessSet(
                    mnemonicEntityData,
                    cachePathTreeNode,
                    mnemonicName,
                    fieldName
            );
            */
            return new MnemonicDataAccessSet(
                    subScopeMnemonicContext.getMnemonicEntityData(),
                    subScopeMnemonicContext.getPathTreeNode(),
                    mnemonicName,
                    fieldName
            );

        }

        if (lastTableWithDomainRead != null && lastTableWithDomainRead.equals(tableName + "." + domainName)) {

            // TODO: Questa è una azione sbagliata. Ho scoperto che i dati per "lastTableWithDomainRead" sono stati cancellati
            /*
            return new MnemonicDataAccessSet(
                    mnemonicEntityData,
                    cachePathTreeNode,
                    mnemonicName,
                    fieldName
            );
            */
            return new MnemonicDataAccessSet(
                    subScopeMnemonicContext.getMnemonicEntityData(),
                    subScopeMnemonicContext.getPathTreeNode(),
                    mnemonicName,
                    fieldName
            );

        }

        // ###      Cerco ove l'entità del mnemonico si collega a quella precedente ###
        if (isNewEntityConnectedToPrevious) {

            iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

            while (iterator.hasPrevious()) {

                Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
                RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

                String joinEntityLastTableWithDomainRead = directJoinEntityContext.getLastTableNameWithDomainRead();

                List<String> entityBindList = mnemonicManager.getEntitiesBindWithPriority(tableName + "." + domainName);

                if (joinEntityLastTableWithDomainRead != null) {

                    for (String entityBind : entityBindList) {

                        String parentTableNameWithDomain = entityBind.replaceAll("#[0-9]+$", "");

                        if (joinEntityLastTableWithDomainRead.equals(parentTableNameWithDomain)) {

                            directJoinEntityContext.setLastMnemonicRead(mnemonicName);
                            directJoinEntityContext.setLastTableNameWithDomainRead(tableName + "." + domainName);

                            return new MnemonicDataAccessSet(
                                    directJoinEntityContext.getMnemonicEntityData(),
                                    directJoinEntityContext.getPathTreeNode(),
                                    mnemonicName,
                                    fieldName
                            );

                        }

                    }

                }

            }

            if (lastAccessTableNameWithDomainRead != null) {

                List<String> entityBindList = mnemonicManager.getEntitiesBindWithPriority(tableName + "." + domainName);

                for (String entityBind : entityBindList) {

                    String parentTableNameWithDomain = entityBind.replaceAll("#[0-9]+$", "");

                    if (lastAccessTableNameWithDomainRead.equals(parentTableNameWithDomain)) {

                        lastAccessMnemonicRead = mnemonicName;
                        lastAccessTableNameWithDomainRead = tableName + "." + domainName;
                        /*
                        return new MnemonicDataAccessSet(
                                mnemonicEntityData,
                                cachePathTreeNode,
                                mnemonicName,
                                fieldName
                        );
                        */
                        return new MnemonicDataAccessSet(
                                subScopeMnemonicContext.getMnemonicEntityData(),
                                subScopeMnemonicContext.getPathTreeNode(),
                                mnemonicName,
                                fieldName
                        );

                    }

                }

            }

        }

        // ###      Cerco il mnemonico nella cache delle join e di questo scope     ###
        iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext     = mapEntry.getValue();

            if (directJoinEntityContext.hasReadEntity(tableName)) {

                directJoinEntityContext.setLastMnemonicRead(mnemonicName);
                directJoinEntityContext.setLastTableNameWithDomainRead(tableName + "." + domainName);

                return new MnemonicDataAccessSet(
                        directJoinEntityContext.getMnemonicEntityData(),
                        directJoinEntityContext.getPathTreeNode(),
                        mnemonicName,
                        fieldName
                );

            }

        }

        // if (mnemonicEntityData.hasData(tableName)) {
        if (subScopeMnemonicContext.getMnemonicEntityData().hasData(tableName)) {

            lastAccessMnemonicRead              = mnemonicName;
            lastAccessTableNameWithDomainRead   = tableName + "." + domainName;

            // return extractMnemonicOccurrenceCount(mnemonicEntityData, cachePathTreeNode, mnemonicName, fieldName);
            /*
            return new MnemonicDataAccessSet(
                    mnemonicEntityData,
                    cachePathTreeNode,
                    mnemonicName,
                    fieldName
            );
            */
            return new MnemonicDataAccessSet(
                    subScopeMnemonicContext.getMnemonicEntityData(),
                    subScopeMnemonicContext.getPathTreeNode(),
                    mnemonicName,
                    fieldName
            );

        }

        // ###      Cerco ove l'entità del mnemonico è la radice/entità principale      ###
        iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

            if (directJoinEntityContext.hasEntityRoot(tableName + "." + domainName)) {

                directJoinEntityContext.setLastMnemonicRead(mnemonicName);
                directJoinEntityContext.setLastTableNameWithDomainRead(tableName + "." + domainName);

                return new MnemonicDataAccessSet(
                        directJoinEntityContext.getMnemonicEntityData(),
                        directJoinEntityContext.getPathTreeNode(),
                        mnemonicName,
                        fieldName
                );

            }

        }

        // if (mnemonicGraph.getRootNode().getEntityLabel().equals(tableName + "." + domainName)) {
        if (subScopeMnemonicContext.getMnemonicGraph().getRootNode().getEntityLabel().equals(tableName + "." + domainName)) {

            lastAccessMnemonicRead              = mnemonicName;
            lastAccessTableNameWithDomainRead   = tableName + "." + domainName;
            /*
            return new MnemonicDataAccessSet(
                    mnemonicEntityData,
                    cachePathTreeNode,
                    mnemonicName,
                    fieldName
            );
            */
            return new MnemonicDataAccessSet(
                    subScopeMnemonicContext.getMnemonicEntityData(),
                    subScopeMnemonicContext.getPathTreeNode(),
                    mnemonicName,
                    fieldName
            );

        }

        // ###      Cerco il collegamento prioritario con l'entità                      ###
        List<String> entityBindList = mnemonicManager.getEntitiesBindWithPriority(tableName + "." + domainName);

        for (String entityBind : entityBindList) {

            String entityBindNameWithDomain = entityBind.replaceAll("#[0-9]+$", "");
            iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

            while (iterator.hasPrevious()) {

                Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
                RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

                if (directJoinEntityContext.hasEntityRoot(entityBindNameWithDomain)) {

                    directJoinEntityContext.setLastMnemonicRead(mnemonicName);
                    directJoinEntityContext.setLastTableNameWithDomainRead(tableName + "." + domainName);

                    return new MnemonicDataAccessSet(
                            directJoinEntityContext.getMnemonicEntityData(),
                            directJoinEntityContext.getPathTreeNode(),
                            mnemonicName,
                            fieldName
                    );

                }

            }

            // Controllo nel "MnemonicGraph" se la radice è l'entità passata
            // if (mnemonicGraph.getRootNode().getEntityLabel().equals(entityBindNameWithDomain)) {
            if (subScopeMnemonicContext.getMnemonicGraph().getRootNode().getEntityLabel().equals(entityBindNameWithDomain)) {

                lastAccessMnemonicRead              = mnemonicName;
                lastAccessTableNameWithDomainRead   = tableName + "." + domainName;
                /*
                return new MnemonicDataAccessSet(
                        mnemonicEntityData,
                        cachePathTreeNode,
                        mnemonicName,
                        fieldName
                );
                */
                return new MnemonicDataAccessSet(
                        subScopeMnemonicContext.getMnemonicEntityData(),
                        subScopeMnemonicContext.getPathTreeNode(),
                        mnemonicName,
                        fieldName
                );

            }

        }

        // ###      Cerco ove è possibile collegare l'entità del mnemonico              ###
        iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

            // if (directJoinEntityContext.hasEntityConnection(tableName + "." + domainName)) {
            if (directJoinEntityContext.hasDirectEntityConnectionOld(tableName + "." + domainName)) {

                directJoinEntityContext.setLastMnemonicRead(mnemonicName);
                directJoinEntityContext.setLastTableNameWithDomainRead(tableName + "." + domainName);

                return new MnemonicDataAccessSet(
                        directJoinEntityContext.getMnemonicEntityData(),
                        directJoinEntityContext.getPathTreeNode(),
                        mnemonicName,
                        fieldName
                );

            }

        }

        // boolean isEntityPresentInGraph = mnemonicGraph.isEntityPresent(tableName + "." + domainName);
        boolean isEntityPresentInGraph = subScopeMnemonicContext.getMnemonicGraph().isEntityPresent(tableName + "." + domainName);
        if (isEntityPresentInGraph) {

            lastAccessMnemonicRead              = mnemonicName;
            lastAccessTableNameWithDomainRead   = tableName + "." + domainName;

            /*
            return new MnemonicDataAccessSet(
                    mnemonicEntityData,
                    cachePathTreeNode,
                    mnemonicName,
                    fieldName
            );
            */
            return new MnemonicDataAccessSet(
                    subScopeMnemonicContext.getMnemonicEntityData(),
                    subScopeMnemonicContext.getPathTreeNode(),
                    mnemonicName,
                    fieldName
            );

        } else {

            // RpaMnemonicNode newNode = new RpaMnemonicNode(mnemonicGraph, tableName + "." + domainName);
            RpaMnemonicNode newNode = new RpaMnemonicNode(subScopeMnemonicContext.getMnemonicGraph(), tableName + "." + domainName);
            // if (mnemonicGraph.addNode(newNode)) {
            if (subScopeMnemonicContext.getMnemonicGraph().addNode(newNode)) {

                lastAccessMnemonicRead              = mnemonicName;
                lastAccessTableNameWithDomainRead   = tableName + "." + domainName;

                /*
                return new MnemonicDataAccessSet(
                        mnemonicEntityData,
                        cachePathTreeNode,
                        mnemonicName,
                        fieldName
                );
                */

                return new MnemonicDataAccessSet(
                        subScopeMnemonicContext.getMnemonicEntityData(),
                        subScopeMnemonicContext.getPathTreeNode(),
                        mnemonicName,
                        fieldName
                );

            }

        }

        // ###      In tutti gli altri casi, lancio un errore                           ###
        String code     = mnemonicName;
        String message  = "Impossibile collegare l'entità di " + mnemonicName + " ad altre entità";
        int context     = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

    }

    private RpaAbstractMnemonic extractMnemonic(MnemonicDataAccessSet mnemonicDataAccessSet) {

        RpaMnemonicEntityData   mnemonicEntityData  = mnemonicDataAccessSet.mnemonicEntityData;
        RpaPathTreeNode         pathTreeNode        = mnemonicDataAccessSet.pathTreeNode;
        String                  mnemonicName        = mnemonicDataAccessSet.mnemonicName;
        String                  fieldName           = mnemonicDataAccessSet.fieldName;

        return extractMnemonic(mnemonicEntityData, pathTreeNode, mnemonicName, fieldName);

    }

    private RpaAbstractMnemonic extractMnemonic(RpaMnemonicEntityData mnemonicEntityData, RpaPathTreeNode pathTreeNode, String mnemonicName, String fieldName) {

        RpaMnemonicEntityData retrievedMnemonicEntityData;

        if (isNewEntityConnectedToPrevious) {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, lastMnemonicRead);

        } else {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, null);

        }

        if (retrievedMnemonicEntityData != null && !retrievedMnemonicEntityData.isEmpty()) {

            return retrievedMnemonicEntityData.getCurrentMnemonicValue(fieldName);

        } else {

            return mainCompositore.getMnemonicEmpty();

        }

    }

    private RpaAbstractMnemonic extractMnemonic(MnemonicDataAccessSet mnemonicDataAccessSet, int index) {

        RpaMnemonicEntityData   mnemonicEntityData  = mnemonicDataAccessSet.mnemonicEntityData;
        RpaPathTreeNode         pathTreeNode        = mnemonicDataAccessSet.pathTreeNode;
        String                  mnemonicName        = mnemonicDataAccessSet.mnemonicName;
        String                  fieldName           = mnemonicDataAccessSet.fieldName;

        return extractMnemonic(mnemonicEntityData, pathTreeNode, mnemonicName, fieldName, index);

    }

    private RpaAbstractMnemonic extractMnemonic(RpaMnemonicEntityData mnemonicEntityData, RpaPathTreeNode pathTreeNode, String mnemonicName, String fieldName, int index) {

        RpaMnemonicEntityData retrievedMnemonicEntityData;

        if (isNewEntityConnectedToPrevious) {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, lastMnemonicRead);

        } else {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, null);

        }


        if (retrievedMnemonicEntityData != null) {

            // TODO: ATTENZIONE! IL SETTAGGIO DELL'INDICE CORRENTE E' NECESSARIO PER IL TEST "totalizzatori.rtf"
            // TODO: (OSSIA IN "Il nome della 5° impresa contata con una variabile numerica è: #NOMIMP#" OVE L'INDICE
            // TODO: SARA' SEMPRE 5)
            // TODO: LA MODIFICA ANDREBBE TESTATA SU MODELLI REALI!
            retrievedMnemonicEntityData.setCurrentIndex(index + 1);
            return retrievedMnemonicEntityData.getMnemonicValue(fieldName, index);

        } else {

            return mainCompositore.getMnemonicEmpty();

        }

    }

    private int extractMnemonicOccurrenceCount(MnemonicDataAccessSet mnemonicDataAccessSet, RpaGenericLoopValue loopValue) {

        RpaMnemonicEntityData   mnemonicEntityData  = mnemonicDataAccessSet.mnemonicEntityData;
        RpaPathTreeNode         pathTreeNode        = mnemonicDataAccessSet.pathTreeNode;
        String                  mnemonicName        = mnemonicDataAccessSet.mnemonicName;

        return extractMnemonicOccurrenceCount(mnemonicEntityData, pathTreeNode, mnemonicName, loopValue);

    }

    private int extractMnemonicOccurrenceCount(RpaMnemonicEntityData mnemonicEntityData, RpaPathTreeNode pathTreeNode, String mnemonicName, RpaGenericLoopValue loopValue) {

        RpaMnemonicEntityData retrievedMnemonicEntityData;

        if (isNewEntityConnectedToPrevious) {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, lastMnemonicRead);

        } else {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, null);

        }

        if (retrievedMnemonicEntityData != null) {

            String loopName = loopValue.getIndexName();
            retrievedMnemonicEntityData.setLoopBindName(loopName);
            loopValue.setIsMnemonicEntityFound(true);

            return retrievedMnemonicEntityData.getMnemonicFieldsList().size();

        } else {

            return 0;

        }

    }

    private Integer extractMnemonicType(MnemonicDataAccessSet mnemonicDataAccessSet) {

        RpaMnemonicEntityData   mnemonicEntityData  = mnemonicDataAccessSet.mnemonicEntityData;
        RpaPathTreeNode         pathTreeNode        = mnemonicDataAccessSet.pathTreeNode;
        String                  mnemonicName        = mnemonicDataAccessSet.mnemonicName;
        String                  fieldName           = mnemonicDataAccessSet.fieldName;

        return extractMnemonicType(mnemonicEntityData, pathTreeNode, mnemonicName, fieldName);

    }

    private Integer extractMnemonicType(RpaMnemonicEntityData mnemonicEntityData, RpaPathTreeNode pathTreeNode, String mnemonicName, String fieldName)  {

        RpaMnemonicEntityData retrievedMnemonicEntityData;

        if (isNewEntityConnectedToPrevious) {

            retrievedMnemonicEntityData =
                    mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, lastMnemonicRead);

        } else {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, null);

        }

        if (retrievedMnemonicEntityData != null) {

            // Recupero il nome del campo tabella del mnemonico
            // MnemonicManager  mnemonicManager     = MnemonicManager.getMnemonicManager();
            RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
            String              mnemonicEntityName  = mnemonicManager.translateMnemonicName(mnemonicName);
            String              tableFieldName      = mnemonicEntityName.split("\\.")[0].toLowerCase();

            return retrievedMnemonicEntityData.getCurrentMnemonicValueType(tableFieldName);

        } else {

            return null;

        }

    }

    private Integer extractMnemonicType(MnemonicDataAccessSet mnemonicDataAccessSet, int index) {

        RpaMnemonicEntityData   mnemonicEntityData  = mnemonicDataAccessSet.mnemonicEntityData;
        RpaPathTreeNode         pathTreeNode        = mnemonicDataAccessSet.pathTreeNode;
        String                  mnemonicName        = mnemonicDataAccessSet.mnemonicName;
        String                  fieldName           = mnemonicDataAccessSet.fieldName;

        return extractMnemonicType(mnemonicEntityData, pathTreeNode, mnemonicName, fieldName, index);

    }

    private Integer extractMnemonicType(RpaMnemonicEntityData mnemonicEntityData, RpaPathTreeNode pathTreeNode, String mnemonicName, String fieldName, int index) {

        RpaMnemonicEntityData retrievedMnemonicEntityData;

        if (isNewEntityConnectedToPrevious) {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, lastMnemonicRead);

        } else {

            retrievedMnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicName, null);

        }

        if (retrievedMnemonicEntityData != null) {

            // Recupero il nome del campo tabella del mnemonico
            // MnemonicManager  mnemonicManager     = MnemonicManager.getMnemonicManager();
            RpaMnemonicManager  mnemonicManager     = mainCompositore.getMnemonicManager();
            String              mnemonicEntityName  = mnemonicManager.translateMnemonicName(mnemonicName);
            String              tableFieldName      = mnemonicEntityName.split("\\.")[0].toLowerCase();

            return retrievedMnemonicEntityData.getCurrentMnemonicValueType(tableFieldName, index);

        } else {

            return null;

        }

    }

    public RpaMnemonicEntityData searchEntityWithLoopBind(String loopName) {

        // Cerco il riferimento loop sul "MnemonicEntityData" di ogni "DirectJoin"
        ArrayList mapArrayList = new ArrayList<Map.Entry<String, RpaDirectJoinEntityContext>>(directJoinEntityContextMap.entrySet());
        ListIterator<Map.Entry<String, RpaDirectJoinEntityContext>> iterator = mapArrayList.listIterator(directJoinEntityContextMap.size());

        while (iterator.hasPrevious()) {

            Map.Entry<String, RpaDirectJoinEntityContext> mapEntry = iterator.previous();
            RpaDirectJoinEntityContext directJoinEntityContext = mapEntry.getValue();

            RpaMnemonicEntityData mnemonicEntityData       = directJoinEntityContext.getMnemonicEntityData();
            RpaMnemonicEntityData entityDataWithLoopBind   = mnemonicEntityData.searchEntityWithLoopBind(loopName);

            if (entityDataWithLoopBind != null) {

                return entityDataWithLoopBind;

            }

        }

        // Cerco il riferimento loop sul "MnemonicEntityData" di questo "Scope"
        // RpaMnemonicEntityData entityDataWithLoopBind = mnemonicEntityData.searchEntityWithLoopBind(loopName);
        RpaMnemonicEntityData entityDataWithLoopBind = subScopeMnemonicContext.getMnemonicEntityData().searchEntityWithLoopBind(loopName);

        if (entityDataWithLoopBind != null) {

            return entityDataWithLoopBind;

        } else {

            return null;

        }

    }

    public void recover(RpaMnemonicLoopPreviousStatus.MnemonicBackupContextStatus mnemonicBackupContextStatus) {

        // Resetto il contesto generale
        this.lastMnemonicRead           = mnemonicBackupContextStatus.getLastMnemonicRead();
        this.lastTableWithDomainRead    = mnemonicBackupContextStatus.getLastTableWithDomainRead();

        // Resetto le join implicite
        for (Map.Entry<String, RpaDirectJoinEntityContext> directJoinEntityContextEntry : directJoinEntityContextMap.entrySet()) {

            RpaDirectJoinEntityContext joinContext = directJoinEntityContextEntry.getValue();

            int joinId = joinContext.getTemporaryId();

            String lastMnemonicReadBackup           = mnemonicBackupContextStatus.getLastMnemonicReadDirectJoinMap().get(joinId);
            String lastTableWithDomainReadBackup    = mnemonicBackupContextStatus.getLastTableWithDomainReadDirectJoinMap().get(joinId);

            joinContext.setLastMnemonicRead(lastMnemonicReadBackup);
            joinContext.setLastTableNameWithDomainRead(lastTableWithDomainReadBackup);

        }

    }

    public void updateLastReadMnemonic() {

        if (newMnemonicRead != null) {

            lastMnemonicRead        = newMnemonicRead;
            lastTableWithDomainRead = newTableWithDomainRead;

            newMnemonicRead         = null;
            newTableWithDomainRead  = null;

        }

    }

    public abstract RpaScopeMnemonicContext clone();

    protected void setLastTableWithDomainRead(String lastTableWithDomainRead) { this.lastTableWithDomainRead = lastTableWithDomainRead; }

    protected void setLastMnemonicRead(String lastMnemonicRead) { this.lastMnemonicRead = lastMnemonicRead; }

    protected void setNewTableWithDomainRead(String newTableWithDomainRead) { this.newTableWithDomainRead = newTableWithDomainRead; }

    protected void setNewMnemonicRead(String newMnemonicRead) { this.newMnemonicRead = newMnemonicRead; }

    protected void setLastAccessMnemonicRead(String lastAccessMnemonicRead) { this.lastAccessMnemonicRead = lastAccessMnemonicRead; }

    protected void setLastAccessTableNameWithDomainRead(String lastAccessTableNameWithDomainRead) { this.lastAccessTableNameWithDomainRead = lastAccessTableNameWithDomainRead; }

    protected void setIsNewEntityConnectedToPrevious(boolean isNewEntityConnectedToPrevious) { this.isNewEntityConnectedToPrevious = isNewEntityConnectedToPrevious; }

    protected String getNewTableWithDomainRead() { return this.newTableWithDomainRead; }

    protected String getNewMnemonicRead() { return this.newMnemonicRead; }

    protected String getLastAccessMnemonicRead() { return this.lastAccessMnemonicRead; }

    protected String getLastAccessTableNameWithDomainRead() { return this.lastAccessTableNameWithDomainRead; }

    protected boolean isNewEntityConnectedToPrevious() { return isNewEntityConnectedToPrevious; }

    private class MnemonicDataAccessSet {

        RpaMnemonicEntityData   mnemonicEntityData;
        RpaPathTreeNode         pathTreeNode;
        String                  mnemonicName;
        String                  fieldName;

        public MnemonicDataAccessSet(RpaMnemonicEntityData mnemonicEntityData, RpaPathTreeNode pathTreeNode, String mnemonicName, String fieldName) {

            this.mnemonicEntityData = mnemonicEntityData;
            this.pathTreeNode       = pathTreeNode;
            this.mnemonicName       = mnemonicName;
            this.fieldName          = fieldName;

        }

    }

}
