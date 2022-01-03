package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicParseResultSet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Definisce i collegamenti tra l'entità radice (quella passata in argomento al compilatore)
 * e tutte le entità richiamate nel documento tramite i mnemonici
 */
public class RpaMnemonicGraph {

    private ArrayList<RpaMnemonicNode>  nodes;
    private RpaMnemonicNodeRoot         rootNode;
    private Connection                  dbConnection;

    // public MnemonicGraph(String entityName, int entityPkid, MnemonicManager mnemonicManager) {
    // public MnemonicGraph(String entityName, String entityFieldName, String entityFieldValue) {
    public RpaMnemonicGraph(String entityName, String rootCondition) {

        rootNode                = new RpaMnemonicNodeRoot(this, entityName, rootCondition);
        nodes                   = new ArrayList<RpaMnemonicNode>();

    }

    public RpaMnemonicGraph(String entityName, String rootCondition, Connection dbConnection) {

        rootNode                = new RpaMnemonicNodeRoot(this, entityName, rootCondition);
        nodes                   = new ArrayList<RpaMnemonicNode>();
        this.dbConnection       = dbConnection;

    }

    private RpaMnemonicGraph() {

        nodes = new ArrayList<RpaMnemonicNode>();

    }

    private RpaMnemonicGraph(Connection dbConnection) {

        this.nodes          = new ArrayList<RpaMnemonicNode>();
        this.dbConnection   = dbConnection;

    }

    /**
     * @param dbConnection: Connessione al Database
     */
    public void setDbConnection(Connection dbConnection) {

        this.dbConnection = dbConnection;

    }

    /**
     * Aggiunge un nuovo nodo al grafo, collegandolo con TUTTI I POSSIBILI path verso
     * il nodo radice (arrivando ad aggiungere nodi aggiuntivi)
     *
     * @param newNode
     */
    public boolean addNode(RpaMnemonicNode newNode) {

        ArrayList<RpaMnemonicNode> newPath = new ArrayList<RpaMnemonicNode>();
        newPath.add(newNode);

        return recursiveConnectNode(newNode, newPath);

    }

    /**
     * Funzione ricorsiva che aggiunge un nuovo nodo al grafo.
     * Per essa, vengono definiti TUTTI i possibili percorsi dal nuovo nodo fino al nodo radice.
     * Per poter definire tutti i possibili percorsi, possono essere aggiunti altri nodi.
     *
     * @param currentNode
     * @param currentPath
     * @return
     */
    private boolean recursiveConnectNode(RpaMnemonicNode currentNode, ArrayList<RpaMnemonicNode> currentPath) {

        try {

            // CASO BASE: L'entità del nodo corrente è la stessa del nodo radice
            if (currentNode.getEntityTableName().equals(rootNode.getEntityLabel())) {

                return true;

            }

            // Recupero e controllo i parent entity del nodo
            else {

                // ResultSet resultSetNodeParents = getNodeParentEntitiesResultSet(currentNode);
                ArrayList<RpaMnemonicParseResultSet.NodeParentEntity> nodeParentEntities =
                        getNodeParentEntitiesResultSet(currentNode);

                boolean isCurrentNodeBoundToOneExistenceRootPath    = false;
                boolean isCurrentNodeBoundToOneNewRootPath          = false;

                // while (resultSetNodeParents.next()) {
                for (RpaMnemonicParseResultSet.NodeParentEntity nodeParentEntity : nodeParentEntities) {

                    // String parentTableName                  = resultSetNodeParents.getString("parent_table_name");
                    // String parentTableFieldName             = resultSetNodeParents.getString("parent_table_field_name");

                    String parentTableName      = nodeParentEntity.getParentTableName();
                    String parentTableFieldName = nodeParentEntity.getParentTableFieldName();

                    // CASO BASE: L'entità padre non ha collegamenti
                    if (parentTableName == null) {

                        // Non faccio niente e interrompo qui il percorso

                    }

                    // CASO BASE: L'entità padre del "result set" è la stessa della radice
                    else if (parentTableName.equals(rootNode.getEntityLabel())) {

                        // Collego il nodo alla radice
                        RpaMnemonicEdge newMnemonicEdge = new RpaMnemonicEdge(extractForeignKeyFromEntity(parentTableFieldName));
                        currentNode.addNewParentNode(rootNode, newMnemonicEdge);

                        // Notifico il collegamento del nodo corrente verso la radice
                        isCurrentNodeBoundToOneExistenceRootPath = true;

                    }

                    // CASO BASE: Se l'entità padre del "result set" è uguale a quella del nodo corrente, non faccio niente
                    else if (parentTableName.equals(currentNode.getEntityLabel())) {

                        // Non aggiungo niente per evitare cicli nel grafo

                    }

                    // Cerco una corrispondenza tra il "parent entity" del "result set" e le "entity" di tutti i nodi del grafo
                    else {

                        boolean isNodeEntityMatchFound = false;

                        for (RpaMnemonicNode node : nodes) {

                            // CASO BASE: L'entità padre del "result set" è stata trovata tra i vari nodi del grafo
                            if (parentTableName.equals(node.getEntityLabel())) {

                                // Collego il nodo corrente con quello trovato
                                RpaMnemonicEdge newMnemonicEdge = new RpaMnemonicEdge(extractForeignKeyFromEntity(parentTableFieldName));
                                currentNode.addNewParentNode(node, newMnemonicEdge);

                                isNodeEntityMatchFound                      = true;
                                isCurrentNodeBoundToOneExistenceRootPath    = true;

                                break;

                            }

                        }

                        // CASO RICORSIVO: Nessun "parent entity" è stato trovato nel grafo.
                        if (!isNodeEntityMatchFound) {

                            // Creo un nuovo nodo con il "parent entity" del "result
                            RpaMnemonicNode newNode     = new RpaMnemonicNode(this, parentTableName);

                            // Se ho un possibile ciclo nel grafo, lo evito
                            boolean isNewNodeLeadToRoot = false;
                            boolean isCycleDetected     = false;

                            for (RpaMnemonicNode pathNode : currentPath) {

                                if (pathNode.getEntityLabel().equals(newNode.getEntityLabel())) {

                                    isCycleDetected = true;

                                    break;

                                }

                            }

                            if (!isCycleDetected) {

                                // Verifico se il nuovo nodo porta alla radice
                                ArrayList<RpaMnemonicNode> updatedPath = new ArrayList<RpaMnemonicNode>();
                                updatedPath.addAll(currentPath);
                                updatedPath.add(currentNode);

                                isNewNodeLeadToRoot = recursiveConnectNode(newNode, updatedPath);

                                // Se il nuovo nodo porta alla radice, collego il nodo corrente a esso (al nuovo nodo)
                                if (isNewNodeLeadToRoot) {

                                    RpaMnemonicEdge newMnemonicEdge = new RpaMnemonicEdge(extractForeignKeyFromEntity(parentTableFieldName));

                                    currentNode.addNewParentNode(newNode, newMnemonicEdge);

                                    isCurrentNodeBoundToOneNewRootPath = true;

                                }

                            }

                        }

                    }

                }

                // resultSetNodeParents.close();

                // Se il nodo corrente porta alla radice, aggiungo il nodo e restituisco "true". Altrimenti restituisco "false"
                if (isCurrentNodeBoundToOneExistenceRootPath || isCurrentNodeBoundToOneNewRootPath) {

                    // Se il nodo corrente è stato già inserito nella chiamata ricorsiva, NON lo inserisco
                    boolean isCurrentNodeAlreadyPresent = false;

                    for (RpaMnemonicNode node : nodes) {

                        if (node.getEntityLabel().equals(currentNode.getEntityLabel())) {

                            isCurrentNodeAlreadyPresent = true;

                            break;

                        }

                    }

                    if (!isCurrentNodeAlreadyPresent) {

                        nodes.add(currentNode);

                    }

                    return true;

                } else {

                    return false;

                }

            }

        } catch (IllegalAccessException illegalArgumentException) {

            System.err.println("[MnemonicGraph]: Errore durante l'assegnazione di un 'parent node' per l'entità: " + currentNode.getEntityLabel());
            System.err.println(illegalArgumentException);

            return false;

        } catch (StackOverflowError stackOverflowError) {

            System.err.println("[MnemonicGraph]: Errore di limite dello 'STACK' durante l'esecuzione del nodo con entità " + currentNode.getEntityLabel());
            System.err.println(stackOverflowError);

            return false;

        }

    }

    public Stack<RpaMnemonicEdge> findTwoNodesConnection(String startEntityLabel, String endEntityLabel) throws Exception {

        RpaMnemonicNode startNode  = null;
        RpaMnemonicNode endNode    = null;

        // Controllo se il grafo contiene entrambi i nodi
        for (RpaMnemonicNode node : nodes) {

            // Controllo se l'entità di partenza è presente nel grafo
            if (node.getEntityLabel().equals(startEntityLabel)) {

                startNode = node;

            }

            // Recupero il nodo associato all'entità di destinazione
            else if (node.getEntityLabel().equals(endEntityLabel)) {

                endNode = node;

            }

        }

        // Se non è stato trovato il nodo di destinazione, lancio un errore
        /*
        if (endNode == null) {

            throw new Exception("Entità di destinazione " + endEntityLabel + " non è stata trovata");

        }
        */

        // Se non è stato trovato un nodo di partenza, aggiungo il nodo
        boolean isStartNodePresent;

        if (startNode == null) {

            startNode = new RpaMnemonicNode(this, startEntityLabel);

            isStartNodePresent = addNode(startNode);

        } else {

            isStartNodePresent = true;

        }

        // Se il nodo non è stato inserito, restituisco null
        if (!isStartNodePresent) {

            return null;

        }

        // Se non è stato trovato il nodo di destinazione, aggiungo il nodo
        boolean isEndNodePresent;

        if (endNode == null) {

            endNode = new RpaMnemonicNode(this, endEntityLabel);

            isEndNodePresent = addNode(endNode);

        } else {

            isEndNodePresent = true;

        }

        // Se il nodo non è stato inserito, restituisco null
        if (!isEndNodePresent) {

            return null;

        }

        // Creo una nuova lista di archi che andrà a inizializzare la funzione ricorsiva
        Stack<RpaMnemonicEdge> mnemonicEdgeStack = new Stack<RpaMnemonicEdge>();

        // Richiamo la funzione ricorsiva di ricerca del percorso
        return recursiveFindTwoNodesEdgeConnection(startNode, endNode, mnemonicEdgeStack);

    }

    private Stack<RpaMnemonicEdge> recursiveFindTwoNodesEdgeConnection(
            RpaMnemonicNode currentNode,
            RpaMnemonicNode endNode,
            Stack<RpaMnemonicEdge> mnemonicEdgeStack
    ) {

        // Caso base
        if (currentNode.getEntityLabel().equals(endNode.getEntityLabel())) {

            return mnemonicEdgeStack;

        }

        // Caso base
        else if (currentNode.getParentNodes() == null || currentNode.getParentNodes().isEmpty()) {

            return null;

        }

        else {

            // Caso ricorsivo
            for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentEntry : currentNode.getParentNodes().entrySet()) {

                RpaMnemonicNode parentNode = parentEntry.getValue();
                RpaMnemonicEdge parentEdge = parentEntry.getKey();

                Stack<RpaMnemonicEdge> parentPath = recursiveFindTwoNodesEdgeConnection(parentNode, endNode, mnemonicEdgeStack);

                if (parentPath != null) {

                    mnemonicEdgeStack.push(parentEdge);

                    return mnemonicEdgeStack;

                }

            }

            return null;

        }

    }

    private Stack<RpaMnemonicNode> recursiveFindTwoNodesConnection(
            RpaMnemonicNode currentNode,
            RpaMnemonicNode endNode,
            Stack<RpaMnemonicNode> mnemonicNodeStack
    ) {

        // Caso base
        if (currentNode.getEntityLabel().equals(endNode.getEntityLabel())) {

            return mnemonicNodeStack;

        }

        // Caso base
        else if (currentNode.getParentNodes() == null || currentNode.getParentNodes().isEmpty()) {

            return null;

        }

        else {

            // Caso ricorsivo
            for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentEntry : currentNode.getParentNodes().entrySet()) {

                RpaMnemonicNode parentNode = parentEntry.getValue();
                RpaMnemonicEdge parentEdge = parentEntry.getKey();

                Stack<RpaMnemonicNode> parentPath = recursiveFindTwoNodesConnection(parentNode, endNode, mnemonicNodeStack);

                if (parentPath != null) {

                    // mnemonicNodeStack.push(parentNode);
                    mnemonicNodeStack.push(currentNode);

                    return mnemonicNodeStack;

                }

            }

            return null;

        }

    }

    /**
     * Funzione SQL che cerca tutti i padri del nodo
     *
     * @param node
     * @return
     */
    // private ResultSet getNodeParentEntitiesResultSet(MnemonicNode node) {
    private ArrayList<RpaMnemonicParseResultSet.NodeParentEntity> getNodeParentEntitiesResultSet(RpaMnemonicNode node) {

        ResultSet nodeParentEntitiesResultSet = null;

        String queryStringNodeParentEntities = "" +
                "SELECT c0e_key AS parent_table_field_name, c0e_nom_ex AS parent_table_name " +
                "FROM   rpa_c0entit " +
                "WHERE  c0e_nom LIKE ? " +
                "ORDER BY c0e_nom";

        ArrayList<RpaMnemonicParseResultSet.NodeParentEntity> nodeParentEntities    = null;
        PreparedStatement preparedStatement                                         = null;

        try {

            preparedStatement = dbConnection.prepareStatement(queryStringNodeParentEntities);
            preparedStatement.setString(1, "%" + node.getEntityLabel() + "%");
            nodeParentEntitiesResultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("[MnemonicGraph]: Errore durante il recupero dei 'parent nodes' per l'entità: " + node.getEntityLabel());
            System.err.println(exception);

        }

        try {

            nodeParentEntities = RpaMnemonicParseResultSet.parseNodeParentEntities(nodeParentEntitiesResultSet);
            nodeParentEntitiesResultSet.close();
            preparedStatement.close();

        } catch (SQLException sqlException) {

            System.err.println("[MnemonicGraph]: Errore durante l'iterazione dei 'parent nodes' per l'entità: " + node.getEntityLabel());
            System.err.println(sqlException);

            return null;
        }

        return nodeParentEntities;

    }

    /**
     * Cerca di aggiungere il nodo collegandosi direttamente ad una delle entità già presenti nel grafo
     * (senza usare la ricorsione)
     * Il primo controllo verrà fatto sulla radice e poi sulle restanti entità
     * @param node
     * @return  true: Se è il collegamento è riuscito (oppure il nodo era già presente).
     *          false: Se il collegamento non è riuscito
     */
    public boolean addDirectNode(RpaMnemonicNode node) {

        // Controllo se l'entità del nodo è la stessa della radice
        if (node.getEntityTableName().equals(rootNode.getEntityLabel())) {

            return true;

        }

        // Controllo se l'entità del nodo è la stessa di uno degli altri nodi
        for (RpaMnemonicNode graphNode : nodes) {

            if (graphNode.getEntityLabel().equals(node.getEntityLabel())) {

                return true;

            }

        }

        // Recupero le entità padri dell'entità
        ArrayList<RpaMnemonicParseResultSet.NodeParentEntity> nodeParentEntities = getNodeParentEntitiesResultSet(node);

        // Controllo se l'entità passata non ha nessun padre
        boolean hasNodeParents = false;

        if (nodeParentEntities == null || nodeParentEntities.isEmpty()) {

            hasNodeParents = false;

        } else {

            for (RpaMnemonicParseResultSet.NodeParentEntity nodeParentEntity : nodeParentEntities) {

                String parentTableName = nodeParentEntity.getParentTableName();

                if (parentTableName != null && !parentTableName.isEmpty()) {

                    hasNodeParents = true;

                }

            }

        }

        if (!hasNodeParents) {

            return false;

        }

        // Controllo se riesco a collegare direttamente il nodo alla radice
        if (rootNode != null && rootNode.getEntityLabel() != null && !rootNode.getEntityLabel().isEmpty()) {

            for (RpaMnemonicParseResultSet.NodeParentEntity nodeParentEntity : nodeParentEntities) {

                String parentTableName      = nodeParentEntity.getParentTableName();
                String parentTableFieldName = nodeParentEntity.getParentTableFieldName();

                if (parentTableName != null && parentTableName.equals(rootNode.getEntityLabel())) {

                    try {

                        // Collego il nodo alla radice
                        RpaMnemonicEdge newMnemonicEdge = new RpaMnemonicEdge(extractForeignKeyFromEntity(parentTableFieldName));
                        node.addNewParentNode(rootNode, newMnemonicEdge);

                        return true;

                    } catch (IllegalAccessException illegalArgumentException) {

                        System.err.println("[MnemonicGraph]: Errore durante l'assegnazione di un 'parent node' per l'entità: " + node.getEntityLabel());
                        System.err.println(illegalArgumentException);

                        return false;

                    }

                }

            }

        }

        // Controllo se riesco a collegare direttamente il nodo agli altri nodi del grafo
        for (RpaMnemonicParseResultSet.NodeParentEntity nodeParentEntity : nodeParentEntities) {

            String parentTableName      = nodeParentEntity.getParentTableName();
            String parentTableFieldName = nodeParentEntity.getParentTableFieldName();

            for (RpaMnemonicNode graphNode : nodes) {

                if (parentTableName != null && parentTableName.equals(graphNode.getEntityLabel())) {

                    try {

                        // Collego il nuovo nodo a quello trovato nel grafo
                        RpaMnemonicEdge newMnemonicEdge = new RpaMnemonicEdge(extractForeignKeyFromEntity(parentTableFieldName));
                        node.addNewParentNode(graphNode, newMnemonicEdge);

                        return true;

                    } catch (IllegalAccessException illegalArgumentException) {

                        System.err.println("[MnemonicGraph]: Errore durante l'assegnazione di un 'parent node' per l'entità: " + node.getEntityLabel());
                        System.err.println(illegalArgumentException);

                        return false;

                    }

                }

            }

        }

        // In tutti gli altri casi, non sono riuscito a collegare il nodo
        return false;

    }

    /**
     * Cancella, se esiste, il nodo (e tutti i suo sotto-nodi) associato all'entità indicata
     * @param tableNameWithDomain
     * @return true: Se l'elemento è stato trovato e cancellato || false: Nel caso in cui l'entità non esista
     */
    public boolean delete(String tableNameWithDomain) {

        // Recupero il nodo associato all'entità passata
        RpaMnemonicNode nodeToDelete = getNode(tableNameWithDomain);

        /*
        for (RpaMnemonicNode node : nodes) {

            if (node.getEntityLabel().equals(tableNameWithDomain)) {

                nodeToDelete = node;
                break;

            }

        }
        */

        if (nodeToDelete == null) {

            return false;

        } else {

            // Cancello il nodo e il suo sotto-albero
            return delete(nodeToDelete);

        }

    }

    /**
     * Cancella, se esiste, il nodo
     * @param nodeToDelete
     * @return true: Se l'elemento è stato trovato e cancellato || false: Nel caso in cui l'entità non esista
     */
    public boolean delete(RpaMnemonicNode nodeToDelete) {

        // Controllo che il nodo appartenga a questo grafo
        boolean isNodeToDeleteFound = false;

        for (RpaMnemonicNode node : nodes) {

            if (node.getEntityLabel().equals(nodeToDelete.getEntityLabel())) {

                isNodeToDeleteFound = true;
                break;

            }

        }

        if (!isNodeToDeleteFound) {

            return false;

        }

        // Ciclo su ogni nodo del grafo per trovare i nodi da cancellare
        Map<String, RpaMnemonicNode> nodesToDeleteMap = new HashMap<String, RpaMnemonicNode>();

        for (RpaMnemonicNode node : nodes) {

            // Per ogni nodo, verifico se esiste un percorso tra il nodo considerato e quello da cancellare
            Stack<RpaMnemonicNode> nodesToDelete = new Stack<RpaMnemonicNode>();
            nodesToDelete = recursiveFindTwoNodesConnection(node, nodeToDelete, nodesToDelete);

            // Salvo tutti i nodi del percorso (evitando duplicati)
            if (nodesToDelete != null) {

                for (RpaMnemonicNode lastNodeToDelete : nodesToDelete) {

                    // Cancello solo quelli che hanno un padre
                    if (lastNodeToDelete.getParentNodes().size() == 1) {

                        nodesToDeleteMap.put(lastNodeToDelete.getEntityLabel(), lastNodeToDelete);

                    }

                }

            }

        }

        nodesToDeleteMap.put(nodeToDelete.getEntityLabel(), nodeToDelete);

        // Cancello dal grafo tutti i nodi trovati
        for (Map.Entry<String, RpaMnemonicNode> lastEntryToDelete : nodesToDeleteMap.entrySet()) {

            RpaMnemonicNode lastNodeToDelete = lastEntryToDelete.getValue();
            nodes.remove(lastNodeToDelete);

        }

        // Cancello i riferimenti degli altri nodi verso i nodi ormai cancellati
        for (RpaMnemonicNode node : nodes) {

            ArrayList<RpaMnemonicEdge> parentsReferenceToDelete = new ArrayList<RpaMnemonicEdge>();
            for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentEntry : node.getParentNodes().entrySet()) {

                RpaMnemonicEdge parentEdge  = parentEntry.getKey();
                RpaMnemonicNode parentNode  = parentEntry.getValue();

                for (Map.Entry<String, RpaMnemonicNode> lastEntryToDelete : nodesToDeleteMap.entrySet()) {

                    if (parentNode.getEntityLabel().equals(lastEntryToDelete.getValue().getEntityLabel())) {

                        parentsReferenceToDelete.add(parentEdge);

                    }

                }

            }

            for (RpaMnemonicEdge parentEdgeToRemove : parentsReferenceToDelete) {

                node.getParentNodes().remove(parentEdgeToRemove);

            }

        }

        // Cancello tutti i nodi che non hanno padri (tranne per la radice)
        ArrayList<RpaMnemonicNode> nodeWithoutParents = new ArrayList<RpaMnemonicNode>();
        for (RpaMnemonicNode node : nodes) {

            if (!node.getEntityLabel().equals(rootNode.getEntityLabel()) && node.getParentNodes().isEmpty()) {

                nodeWithoutParents.add(node);

            }

        }

        for (RpaMnemonicNode lastNodeToDelete : nodeWithoutParents) {

            nodes.remove(lastNodeToDelete);

        }

        return true;

    }

    /**
     * Connette il nuovo nodo a quello precedente.
     * Nota: Il nodo precedente deve essere già presente nel grafo e il nuovo nodo deve avere
     * una forma di collegamento con quello precedente
     * @param previousNode
     * @param newNode
     * @return  true: Se sono riuscito a collegare il nuovo nodo a quello precedente |
     *          false: Il nodo precedente non esiste nel grafo o non esiste un collegamente tra i due nodi
     */
    public boolean addNode(RpaMnemonicNode previousNode, RpaMnemonicNode newNode) {

        // Verifico che il nodo precedente sia presente nel grafo
        /*
        boolean isPreviousNodeFound = false;

        for (RpaMnemonicNode node : nodes) {

            if (previousNode.getEntityLabel().equals(node.getEntityLabel())) {

                isPreviousNodeFound = true;
                break;

            }

        }

        if (!isPreviousNodeFound) {

            return false;

        }
        */
        if (!isEntityPresent(previousNode.getEntityLabel())) {

            return false;

        }

        // Verifico se riesco a collegare il nuovo nodo a quello precedente
        ArrayList<RpaMnemonicParseResultSet.NodeParentEntity> newNodeParentEntities = getNodeParentEntitiesResultSet(newNode);

        if (newNodeParentEntities == null || newNodeParentEntities.isEmpty()) {

            return false;

        } else {

            String parentFieldFound = null;
            for (RpaMnemonicParseResultSet.NodeParentEntity nodeParentEntity : newNodeParentEntities) {

                if (    nodeParentEntity.getParentTableName() != null &&
                        nodeParentEntity.getParentTableName().equals(previousNode.getEntityLabel())) {

                    parentFieldFound = nodeParentEntity.getParentTableFieldName();
                    break;

                }

            }

            if (parentFieldFound == null) {

                return false;

            } else {

                try {

                    // Collego il nuovo nodo a quello precedente e lo aggiungo alla lista di nodi
                    RpaMnemonicEdge newMnemonicEdge = new RpaMnemonicEdge(extractForeignKeyFromEntity(parentFieldFound));
                    newNode.addNewParentNode(previousNode, newMnemonicEdge);
                    nodes.add(newNode);

                    return true;

                } catch (IllegalAccessException illegalArgumentException) {

                    System.err.println("[MnemonicGraph]: Errore durante l'assegnazione di un 'parent node' per l'entità: " + newNode.getEntityLabel());
                    System.err.println(illegalArgumentException);

                    return false;

                }

            }

        }

    }

    /**
     * Recupera il nome della chiave esterna di una tabella padre
     *
     * @param parentTableFieldName
     * @return
     */
    private String extractForeignKeyFromEntity(String parentTableFieldName) {

        return parentTableFieldName.split("\\.")[0];

    }

    /**
     * Ritorna il valore del mnemonico seguendo il path definito dall'utente.
     * (Se nel path, alcuni nodi NON SONO presenti nel grafo, essi vengono aggiunti)
     *
     * @param startNodeEntity
     * @param joinPath
     * @return
     */
    public ArrayList<Object> getMnemonicValuesByJoinPath(String startNodeEntity, String joinPath) {

        return new ArrayList<Object>();

    }

    /**
     * Permette di salvare su file il grafo di collegamento con tutti i mnemonici.
     * (Utile per ridurre i tempi di compilazione).
     *
     * @param outFilePath
     */
    public void writeOnFile(String outFilePath) {

    }

    /**
     * Permette di caricare questo grafo utilizzando una configurazione salvata su file.
     * ATTENZIONE: Tutti i dati presenti in questo oggetto verranno cancellati.
     *
     * @param inFilePath
     */
    public void loadFromFile(String inFilePath) {

    }

    /**
     * Restituisce la lista di nodi "mnemonici" trovati e legati al documento
     *
     * @return
     */
    public ArrayList<RpaMnemonicNode> getNodes() {

        return nodes;

    }

    public RpaMnemonicNodeRoot getRootNode() {

        return rootNode;

    }

    public RpaMnemonicNode getNode(String entityName) {

        if (rootNode.getEntityLabel().equals(entityName)) {

            return rootNode;

        } else {

            for (RpaMnemonicNode node : nodes) {

                if (node.getEntityLabel().equals(entityName)) {

                    return node;

                }

            }

        }

        return null;

    }

    public void setRootCondition(String rootCondition) {

        rootNode.setRootCondition(rootCondition);

    }

    public String getRootCondition() {

        return rootNode.getRootCondition();

    }

    public boolean isEntityPresent(String entityLabel) {

        return getNode(entityLabel) != null;

    }

    public boolean hasConnectionToRoot(String entityLabel) {

        if (rootNode != null && rootNode.getEntityLabel().equals(entityLabel)) {

            return false;

        }

        ResultSet           nodeParentEntitiesResultSet = null;
        PreparedStatement   preparedStatement           = null;

        String queryStringNodeParentEntities = "" +
                "SELECT c0e_nom_ex AS parent_table_name " +
                "FROM   rpa_c0entit " +
                "WHERE  c0e_nom LIKE ? " +
                "ORDER BY c0e_nom";

        try {

            // Recupero le entità padri
            preparedStatement = dbConnection.prepareStatement(queryStringNodeParentEntities);
            preparedStatement.setString(1, "%" + entityLabel + "%");
            nodeParentEntitiesResultSet = preparedStatement.executeQuery();

            // Controllo se una delle entità padre corrisponde con la radice
            while (nodeParentEntitiesResultSet.next()) {

                String parentEntity = nodeParentEntitiesResultSet.getString("parent_table_name");

                if (parentEntity != null && rootNode.getEntityLabel().equals(parentEntity)) {

                    return true;

                }

            }

        } catch (SQLException exception) {

            System.err.println("[MnemonicGraph]: Errore durante il recupero dei 'parent nodes' per l'entità: " + entityLabel);
            System.err.println(exception);

        }

        return false;

    }

    /**
     * Forza il collegamento di una entità verso la radice
     *
     * @param entityLabel
     */
    public void checkAndForceConnectionToRoot(String entityLabel) {

        // Verifico se ho già inserito l'entità
        if (isEntityPresent(entityLabel)) {

            // Verifico se l'entita è stata inserita in collegamento diretto con la radice
            boolean isEntityConnectedToRoot = false;

            RpaMnemonicNode node = null;
            for (RpaMnemonicNode currentNode : nodes) {

                if (currentNode.getEntityLabel().equals(entityLabel)) {

                    node = currentNode;
                    break;

                }

            }

            for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentEntry : node.getParentNodes().entrySet()) {

                RpaMnemonicNode parentNode = parentEntry.getValue();

                if (parentNode != null && parentNode.getEntityLabel().equals(rootNode.getEntityLabel())) {

                    isEntityConnectedToRoot = true;
                    break;

                }

            }

            // Se l'entità non è collegata con la radice, la cancello e la ricollego alla radice
            if (!isEntityConnectedToRoot) {

                delete(node);
                addNode(rootNode, new RpaMnemonicNode(this, entityLabel));

            }

        }

        // Altrimenti la inserisco collegandola direttamente alla radice
        else {

            // Aggiungo l'entità direttamente alla radice
            addNode(rootNode, new RpaMnemonicNode(this, entityLabel));

        }

    }

    public void forceConnection(String childEntity, String parentEntity) throws Exception {

        // Ricerco entrambi i nodi
        RpaMnemonicNode childNode   = null;
        RpaMnemonicNode parentNode  = null;

        for (RpaMnemonicNode node : nodes) {

            if (node.getEntityLabel().equals(childEntity)) {

                childNode = node;

            }

            if (node.getEntityLabel().equals(parentEntity)) {

                parentNode = node;

            }

            if (childNode != null && parentNode != null) {

                break;

            }

        }

        if (parentNode == null) {

            throw new Exception("L'entità " + parentEntity + " è presente nel 'PathTreeNode' ma non nel 'MnemonicGraph'");

        }

        if (childNode == null) {

            // Creo un nuovo nodo figlio da agganciare al padre
            if (!addNode(parentNode, new RpaMnemonicNode(this, childEntity))) {

                throw new Exception("Impossibile collegare l'entità figlia " + childEntity + " con l'entità " + parentEntity);

            }

        }

        else {

            // Controllo se sono già collegati fra loro
            boolean isChildSonOfParent = false;
            for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> currentParentEntry : childNode.getParentNodes().entrySet()) {

                RpaMnemonicNode currentParent = currentParentEntry.getValue();

                if (currentParent.getEntityLabel().equals(parentNode.getEntityLabel())) {

                    isChildSonOfParent = true;
                    break;

                }

            }

            if (!isChildSonOfParent) {

                // Cancello il nodo "figlio" e ne creo uno nuovo da collegare al padre
                delete(childNode);

                if (!addNode(parentNode, new RpaMnemonicNode(this, childEntity))) {

                    throw new Exception("Impossibile collegare l'entità figlia " + childEntity + " con l'entità " + parentEntity);

                }

            }

        }

    }

    /**
     * Stampa nella console di DEBUG il grafo.
     * Il formato è quello di "Graphviz"
     *
     * @param isShowEdgesLabel
     * @return
     */
    public String printGraphviz(boolean isShowEdgesLabel) {

        String graphVizPortrayl = "digraph MnemonicGraph {\n";

        // Se ho solo la radice, stampo solo quella
        if (rootNode != null && nodes.isEmpty()) {

            graphVizPortrayl += "\"" + rootNode.getEntityLabel() + "\"\n";

        } else {

            // Ciclo su ogni nodo per rappresentare ogni suo collegamento con tutti gli altri
            for (RpaMnemonicNode graphNode : nodes) {

                // Ciclo su ogni arco del nodo per rappresentare ogni collegamento con tutti gli altri
                for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> graphNodeConnection : graphNode.getParentNodes().entrySet()) {

                    if (isShowEdgesLabel) {

                        graphVizPortrayl += "\"" + graphNode.getEntityLabel() + "\" -> \"" + graphNodeConnection.getValue().getEntityLabel() + "\"";
                        graphVizPortrayl += " [ label = " + graphNodeConnection.getKey().getForeignKey() + " ];\n";

                    } else {

                        graphVizPortrayl += "\"" + graphNode.getEntityLabel() + "\" -> \"" + graphNodeConnection.getValue().getEntityLabel() + "\"\n";

                    }

                }

            }

        }

        graphVizPortrayl += "}";

        return graphVizPortrayl;

    }

    /**
     * Stampa su file una rappresentazione del grafo in formato "Graphviz"
     * Il nome del file creato / aggiornato è "mnemonic_graph_debug.gv"
     * NOTA: Il percorso del file NON DEVE avere uno "/" finale
     *
     * @param filePath
     * @param isShowEdgesLabel
     */
    public void saveGraphvizFile(String filePath, boolean isShowEdgesLabel) {

        try {

            PrintWriter printWriter = new PrintWriter(filePath + "/mnemonic_graph_debug.gv");

            printWriter.print(printGraphviz(isShowEdgesLabel));
            printWriter.flush();
            printWriter.close();

        } catch (FileNotFoundException fileNotFoundException) {

            System.err.println("Impossibile salvare il file per il path: '" + filePath + "'");
            System.err.println(fileNotFoundException);

        }

    }

    /**
     * Restituisce l'elenco di nodi che puntano al nodo
     * @param node
     * @return
     */
    private ArrayList<PairNodeEdge> getLinkedNodes(RpaMnemonicNode node) {

        ArrayList<PairNodeEdge> linkedNodes = new ArrayList<PairNodeEdge>();

        for (RpaMnemonicNode currentNode : nodes) {

            if (node != currentNode) {

                Map<RpaMnemonicEdge, RpaMnemonicNode> currentParentNodes = currentNode.getParentNodes();

                for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> entryEdgeNode : currentParentNodes.entrySet()) {

                    RpaMnemonicEdge parentEdge = entryEdgeNode.getKey();
                    RpaMnemonicNode parentNode = entryEdgeNode.getValue();

                    if (node.getEntityLabel().equals(parentNode.getEntityLabel())) {

                        PairNodeEdge newPairNodeEdge = new PairNodeEdge();
                        newPairNodeEdge.edge = parentEdge;
                        newPairNodeEdge.node = currentNode;

                        linkedNodes.add(newPairNodeEdge);

                    }

                }

            }

        }

        return linkedNodes;

    }

    private RpaMnemonicNode recursiveNodeClone(RpaMnemonicGraph graphClone, RpaMnemonicNode node) throws IllegalAccessException {

        ArrayList<PairNodeEdge> linkedNodes = getLinkedNodes(node);

        // CASO BASE: Non ho nodi che puntano al nodo corrente
        if (linkedNodes.isEmpty()) {

            // Clono il nodo corrente e lo restituisco
            return node.clone(graphClone);

        }

        // CASO RICORSIVO: Ho nodi che puntano al nodo corrente
        else {

            RpaMnemonicNode nodeClone = node.clone(graphClone);

            // Clono ogni nodo che punta a quello corrente (clonandone anche la sotto-struttura)
            for (PairNodeEdge pairNodeEdge : linkedNodes) {

                RpaMnemonicNode linkedNode = pairNodeEdge.node;
                RpaMnemonicEdge linkedEdge = pairNodeEdge.edge;

                // Se un nodo era già stato inserito, non lo inserisco
                RpaMnemonicNode linkedNodeClone = null;

                for (RpaMnemonicNode insertedCloneNode : graphClone.nodes) {

                    if (insertedCloneNode.getEntityLabel().equals(linkedNode.getEntityLabel())) {

                        linkedNodeClone = insertedCloneNode;
                        break;

                    }

                }

                if (linkedNodeClone == null) {

                    linkedNodeClone = recursiveNodeClone(graphClone, linkedNode);
                    graphClone.nodes.add(linkedNodeClone);

                }

                RpaMnemonicEdge linkedEdgeClone = linkedEdge.clone();
                linkedNodeClone.addNewParentNode(nodeClone, linkedEdgeClone);

            }

            return nodeClone;

        }

    }

    public RpaMnemonicGraph clone() {

        // Clono la struttura
        RpaMnemonicGraph graphClone;

        if (dbConnection != null) {

            graphClone = new RpaMnemonicGraph(dbConnection);

        } else {

            graphClone = new RpaMnemonicGraph();

        }

        // Clono i nodi
        if (rootNode != null) {

            try {

                graphClone.rootNode = (RpaMnemonicNodeRoot) recursiveNodeClone(graphClone, rootNode);

            } catch (IllegalAccessException exception) {

                exception.printStackTrace();

            }

            // TEST Inizio verifica nodi clonati
            boolean isEveryElementCorrectlyCloned = true;

            if (rootNode == graphClone.rootNode) {

                isEveryElementCorrectlyCloned = false;

            }

            // Controllo la diversità tra i nodi originali e quelli clonati
            for (RpaMnemonicNode originalNode : this.nodes) {

                for (RpaMnemonicNode clonedNode : graphClone.nodes) {

                    if (originalNode.getEntityLabel().equals(clonedNode.getEntityLabel())) {

                        if (originalNode == clonedNode) {

                            isEveryElementCorrectlyCloned = false;
                            break;

                        } else {

                            for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> originalParent : originalNode.getParentNodes().entrySet()) {

                                RpaMnemonicEdge originalEdge = originalParent.getKey();

                                for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> clonedParent : clonedNode.getParentNodes().entrySet()) {

                                    RpaMnemonicEdge clonedEdge = clonedParent.getKey();

                                    if (originalEdge.getForeignKey().equals(clonedEdge.getForeignKey()) && originalEdge == clonedEdge) {

                                        isEveryElementCorrectlyCloned = false;
                                        break;

                                    }

                                }

                            }

                        }

                    }

                }

            }

            // Controllo che i puntamenti siano verso
            if (!isEveryElementCorrectlyCloned) {

                System.err.println("Errore durante la clonazione");

            }
            // TEST Fine verifica nodi clonati

        }

        return graphClone;

    }

    @Override
    public String toString() {

        return printGraphviz(false);

    }

    private class PairNodeEdge {

        RpaMnemonicNode node;
        RpaMnemonicEdge edge;

    }

}
