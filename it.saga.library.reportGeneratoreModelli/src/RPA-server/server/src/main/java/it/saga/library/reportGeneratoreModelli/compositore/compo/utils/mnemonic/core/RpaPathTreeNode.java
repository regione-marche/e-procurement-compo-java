package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicEdge;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class RpaPathTreeNode {

    private String tableName;
    private String entityDomain;
    private Map<String, RpaPathTreeNode> children;
    private RpaPathTreeNode parent;

    public RpaPathTreeNode(String tableName, String entityDomain) {

        this.tableName      = tableName;
        this.entityDomain   = entityDomain;
        this.children       = new HashMap<String, RpaPathTreeNode>();

    }

    protected void addChild(RpaPathTreeNode child) {

        children.put(child.getTableName(), child);

    }

    protected void removeChild(RpaPathTreeNode child) {

        children.remove(child.getTableName());

    }

    public RpaPathTreeNode findEntityPathTreeNode(String entityName) {

        return findEntityPathTreeNode(this, entityName);

    }

    private RpaPathTreeNode findEntityPathTreeNode(RpaPathTreeNode pathTreeNode, String entityName) {

        // Caso base
        if (pathTreeNode.getTableName().equals(entityName)) {

            return pathTreeNode;

        }

        // Caso ricorsivo
        else {

            for (Entry<String, RpaPathTreeNode> childSet : pathTreeNode.children.entrySet()) {

                RpaPathTreeNode child = childSet.getValue();

                RpaPathTreeNode pathTreeNodeFound = findEntityPathTreeNode(child, entityName);

                if (pathTreeNodeFound != null) {

                    return pathTreeNodeFound;

                }

            }

            return null;

        }

    }

    public Stack<String> findEntitySequence(RpaMnemonicNode node) {

        Stack<String> entityStack = new Stack<String>();

        RpaPathTreeNode pathTreeNodeFound = updateTree(node);

        while (pathTreeNodeFound != null) {

            entityStack.push(pathTreeNodeFound.getEntityLabel());

            pathTreeNodeFound = pathTreeNodeFound.getParent();

        }

        return entityStack;

    }

    public Stack<String> findEntitySequence(RpaMnemonicNode previousNode, RpaMnemonicNode newNode) {

        Stack<String>   entityStack = new Stack<String>();
        RpaPathTreeNode pathTreeNodeFound;

        // Controllo se il nuovo nodo è già collegato al nodo precedente indicato
        RpaPathTreeNode previousPathTreeNode    = findEntityPathTreeNode(previousNode.getEntityTableName());
        RpaPathTreeNode newNodePathTreeNode     = findEntityPathTreeNode(newNode.getEntityTableName());

        boolean isPreviousAndNewNodeConnected = false;
        try {
            if (newNodePathTreeNode != null && newNodePathTreeNode.getParent().getTableName().equals(previousPathTreeNode.getTableName())) {

                isPreviousAndNewNodeConnected = true;

            }
        } catch (NullPointerException exception) {

            exception.printStackTrace();

        }

        // Se sono già connesse, recupero il loro percorso
        if (isPreviousAndNewNodeConnected) {

            pathTreeNodeFound = newNodePathTreeNode;

        }

        // Altrimenti verifico se l'entità del mnemonico da leggere esiste, percui vado a cancellarla
        else if (newNodePathTreeNode != null) {

            // Elimino (se presente) dall'albero il nodo da reinserire
            delete(newNodePathTreeNode);

            // Ricreo il pathTreeNode per l'entità corrente
            newNodePathTreeNode = new RpaPathTreeNode(newNode.getEntityTableName(), newNode.getEntityDomain());

            // Collego il nodo
            newNodePathTreeNode.setParent(previousPathTreeNode);

            // Utilizzo il nuovo come path da seguire
            pathTreeNodeFound = newNodePathTreeNode;

        }

        // Negli altri casi, collego il nodo del Mnemonico da leggere al nodo precedente e restituisco il percorso
        else {

            // Creo il pathTreeNode per l'entità corrente
            newNodePathTreeNode = new RpaPathTreeNode(newNode.getEntityTableName(), newNode.getEntityDomain());

            // Collego il nodo
            newNodePathTreeNode.setParent(previousPathTreeNode);

            // Utilizzo il nuovo come path da seguire
            pathTreeNodeFound = newNodePathTreeNode;

        }

        // Riempio e restituisco lo stack
        while (pathTreeNodeFound != null) {

            entityStack.push(pathTreeNodeFound.getEntityLabel());

            pathTreeNodeFound = pathTreeNodeFound.getParent();

        }

        return entityStack;

    }

    private RpaPathTreeNode updateTree(RpaMnemonicNode currentNode) {

        RpaPathTreeNode pathTreeNodeFound = findEntityPathTreeNode(currentNode.getEntityTableName());

        if (currentNode.getEntityTableName().equals(this.tableName)) {

            // Caso base: l'entità è alla radice
            return this;

        }

        if (pathTreeNodeFound != null) {

            // Caso base: Ritorno il path trovato
            return pathTreeNodeFound;

        }

        RpaMnemonicNode parentNode = null;

        if (currentNode.getParentNodes().isEmpty()) {

            System.err.println("Errore! Nessun collegamento con l'albero delle entità (generale o di join) per il nodo " + currentNode);

        }

        // Caso base: Controllo se ho un collegamento diretto con la radice
        for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentMnemonicNodeEntry : currentNode.getParentNodes().entrySet()) {

            parentNode                  = parentMnemonicNodeEntry.getValue();
            pathTreeNodeFound           = findEntityPathTreeNode(parentNode.getEntityTableName());

            if (pathTreeNodeFound != null && pathTreeNodeFound.getTableName().equals(this.tableName)) {

                RpaPathTreeNode newPathTreeNode = new RpaPathTreeNode(currentNode.getEntityTableName(), currentNode.getEntityDomain());
                newPathTreeNode.setParent(pathTreeNodeFound);

                return newPathTreeNode;

            }

        }

        for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentMnemonicNodeEntry : currentNode.getParentNodes().entrySet()) {

            parentNode          = parentMnemonicNodeEntry.getValue();
            pathTreeNodeFound   = findEntityPathTreeNode(parentNode.getEntityTableName());

            // Caso base: Creo un nuovo nodo, lo collego a quello trovato e ritorno quest'ultimo
            if (pathTreeNodeFound != null) {

                RpaPathTreeNode newPathTreeNode = new RpaPathTreeNode(currentNode.getEntityTableName(), currentNode.getEntityDomain());
                newPathTreeNode.setParent(pathTreeNodeFound);

                return newPathTreeNode;

            }

        }

        // Caso ricorsivo: Non ho trovato nessun padre nell'albero, quindi provo con il livello superiore
        RpaPathTreeNode newPathTreeNode    = new RpaPathTreeNode(currentNode.getEntityTableName(), currentNode.getEntityDomain());
        RpaPathTreeNode nextPathTreeNode   = updateTree(parentNode);

        newPathTreeNode.setParent(nextPathTreeNode);

        return newPathTreeNode;

    }

    public boolean delete(RpaPathTreeNode treeNodeToDelete) {

        // Controllo se il nodo ha un padre (in quel caso è una radice e non faccio niente)
        if (treeNodeToDelete.getParent() == null) {

            return false;

        }

        // Se ce, recupero il padre e taglio il collegamento con il figlio
        else {

            RpaPathTreeNode parentTreeNode = treeNodeToDelete.getParent();
            parentTreeNode.removeChild(treeNodeToDelete);

            // TODO: Occhio, potrebbe generare un'altra radice? Da controllare
            // treeNodeToDelete.setParent(null);
            treeNodeToDelete.cleanParent();

            return true;

        }

    }

    public void checkAndForceConnectionToRoot(String entityName) {

        // Verifico di essere la radice
        if (this.parent != null) {

            return;

        }

        // Verifico se ho già inserito l'entità
        RpaPathTreeNode node = findEntityPathTreeNode(entityName);

        if (node != null) {

            // Verifico se l'entita è stata inserita in collegamento diretto con la radice
            boolean isEntityConnectedToRoot = false;
            for (Map.Entry<String, RpaPathTreeNode> childSet : this.children.entrySet()) {

                RpaPathTreeNode child = childSet.getValue();

                if (child.getEntityLabel().equals(entityName)) {

                    isEntityConnectedToRoot = true;
                    break;

                }

            }

            // Se l'entità non è collegata con la radice, la cancello e la ricollego alla radice
            if (!isEntityConnectedToRoot) {

                delete(node);

                String          tableName   = entityName.split("\\.")[0];
                String          domainName  = entityName.split("\\.")[1];
                RpaPathTreeNode newNode     = new RpaPathTreeNode(tableName, domainName);

                // this.children.put(entityName, newNode);
                newNode.setParent(this);

            }

        }

        // Altrimenti la inserisco collegandola direttamente alla radice
        else {

            String          tableName   = entityName.split("\\.")[0];
            String          domainName  = entityName.split("\\.")[1];
            RpaPathTreeNode newNode     = new RpaPathTreeNode(tableName, domainName);

            // this.children.put(entityName, newNode);
            newNode.setParent(this);

        }

    }

    public RpaPathTreeNode checkCouldBeEntityConnectedToTree(String entityName, RpaMnemonicManager mnemonicManager) {

        // Recupero i padri della entità
        List<String> listParentEntity = mnemonicManager.getEntitiesBindWithPriority(entityName);

        // Scorro ricorsivamente su tutti i nodi (DAL BASSO VERSO L'ALTO) alla ricerca di quello che si collega con l'entità
        List<RpaPathTreeNode> bottomNodes = new ArrayList<RpaPathTreeNode>();
        findTreeChildren(bottomNodes);

        RpaPathTreeNode nodeFound = recursiveFindBottomNodeToConnect(entityName, bottomNodes, listParentEntity);

        if (nodeFound != null) {

            String tableName    = entityName.split("\\.")[0];
            String domain       = entityName.split("\\.")[1];

            RpaPathTreeNode newChild = new RpaPathTreeNode(tableName, domain);
            // nodeFound.children.put(entityName, newChild);
            newChild.setParent(nodeFound);

            return nodeFound;

        } else {

            return null;

        }

    }

    private RpaPathTreeNode recursiveFindBottomNodeToConnect(String entityName, List<RpaPathTreeNode> bottomNodes, List<String> listParentEntity) {

        // CASO BASE: Ho trovato un nodo che si lega all'entità
        for (RpaPathTreeNode currentNode : bottomNodes) {

            for (String parentEntity : listParentEntity) {

                if (currentNode.getEntityLabel().equals(parentEntity)) {

                    return currentNode;

                }

            }

        }

        // CASO RICORSIVO: Richiamo la funzione su ogni padre dei nodi
        List<RpaPathTreeNode> upperNodes = new ArrayList<RpaPathTreeNode>();

        for (RpaPathTreeNode bottomNode : bottomNodes) {

            RpaPathTreeNode parentNode                  = bottomNode.getParent();
            boolean         isBottomNodeAlreadyPresent  = false;

            for (RpaPathTreeNode upperNode : upperNodes) {

                if (parentNode.getEntityLabel().equals(upperNode.getEntityLabel())) {

                    isBottomNodeAlreadyPresent = true;
                    break;

                }

            }

            if (!isBottomNodeAlreadyPresent && !this.getEntityLabel().equals(parentNode.getEntityLabel())) {

                upperNodes.add(parentNode);

            }

        }

        if (!upperNodes.isEmpty()) {

            return recursiveFindBottomNodeToConnect(entityName, upperNodes, listParentEntity);

        } else {

            return null;

        }

    }

    private void findTreeChildren(List<RpaPathTreeNode> listChild) {

        // CASO BASE: Ho trovato un nodo che non ha figli
        if (this.children.isEmpty()) {

            boolean isNodeAlreadyPresent = false;

            for (RpaPathTreeNode node : listChild) {

                if (this.getEntityLabel().equals(node.getEntityLabel())) {

                    isNodeAlreadyPresent = true;
                    break;

                }

            }

            if (!isNodeAlreadyPresent) {

                listChild.add(this);

            }

        }

        // CASO RICORSIVO: Scorro ricorsivamente su tutti i figli del nodo corrente
        else {

            for (Map.Entry<String, RpaPathTreeNode> pathTreeNodeEntry : this.children.entrySet()) {

                RpaPathTreeNode child = pathTreeNodeEntry.getValue();
                child.findTreeChildren(listChild);

            }

        }

    }

    public boolean forceConnection(RpaPathTreeNode parentPathTreeNode, RpaMnemonicNode childMnemonicNode) {

        // Controllo che il nodo padre esista
        if (findEntityPathTreeNode(parentPathTreeNode.getTableName()) == null) {

            return false;

        }

        // Verifico che non via un nodo figlio già presente nell'albero (nel caso se c'è lo cancello)
        RpaPathTreeNode nodeToDelete = findEntityPathTreeNode(childMnemonicNode.getEntityTableName());

        if (nodeToDelete != null) {

            delete(nodeToDelete);

        }

        // Verifico che vi sia un match di entità tra il "parentPathTreeNode" e uno dei padri del "childMnemonicNode"
        for (Map.Entry<RpaMnemonicEdge, RpaMnemonicNode> parentEntry : childMnemonicNode.getParentNodes().entrySet()) {

            String parentEntity = parentEntry.getValue().getEntityLabel();

            if (parentPathTreeNode.getEntityLabel().equals(parentEntity)) {

                // Creo il nuovo nodo figlio e lo aggancio al padre
                RpaPathTreeNode newPathTreeNode = new RpaPathTreeNode(childMnemonicNode.getEntityTableName(), childMnemonicNode.getEntityDomain());

                // parentPathTreeNode.addChild(newPathTreeNode);
                newPathTreeNode.setParent(parentPathTreeNode);

                return true;

            }

        }

        return false;

    }

    public RpaPathTreeNode getParent() {

        return parent;

    }

    public void setParent(RpaPathTreeNode parent) {

        this.parent = parent;
        parent.addChild(this);

    }

    protected void cleanParent() {

        this.parent = null;

    }

    public String getTableName() {

        return tableName;

    }

    public String getEntityLabel() {

        return tableName + "." + entityDomain;

    }

    /**
     * Stampa nella console di DEBUG il grafo.
     * Il formato è quello di "Graphviz"
     *
     * @param isShowEdgesLabel
     * @return
     */
    private String printGraphviz(boolean isShowEdgesLabel) {

        String graphVizPortrayl = "digraph MnemonicGraph {\n";
        graphVizPortrayl        += printRecursiveGraphviz(isShowEdgesLabel);
        graphVizPortrayl        += "}";

        return graphVizPortrayl;

    }

    private String printRecursiveGraphviz(boolean isShowEdgesLabel) {

        String graphVizPortrayl = "";

        // Stampo il legame tra il nodo corrente e tutti i suoi figli
        for (Map.Entry<String, RpaPathTreeNode> entryChild : children.entrySet()) {

            RpaPathTreeNode child       = entryChild.getValue();
            String          tableBind   = entryChild.getKey();

            if (isShowEdgesLabel) {

                graphVizPortrayl += "\"" + this.getEntityLabel() + "\" -> \"" + child.getEntityLabel() + "\"";
                graphVizPortrayl += " [ label = " + tableBind + " ];\n";

            } else {

                graphVizPortrayl += "\"" + this.getEntityLabel() + "\" -> \"" + child.getEntityLabel() + "\"\n";

            }

            graphVizPortrayl += child.printRecursiveGraphviz(isShowEdgesLabel);

        }

        return graphVizPortrayl;

    }

    @Override
    public RpaPathTreeNode clone() {

        // CASO BASE: Il nodo corrente non ha figli
        if (this.children.isEmpty()) {

            // Clono e restituisco il nodo corrente
            return new RpaPathTreeNode(this.tableName, this.entityDomain);

        }

        // CASO RICORSIVO: Il nodo ha dei figli
        else {

            RpaPathTreeNode cloneNode = new RpaPathTreeNode(this.tableName, this.entityDomain);

            // Clono tutti i figli del nodo
            for (Map.Entry<String, RpaPathTreeNode> entryChild : this.children.entrySet()) {

                String          tableNameChild  = entryChild.getKey();
                RpaPathTreeNode child           = entryChild.getValue();
                RpaPathTreeNode cloneChild      = child.clone();

                // cloneChild.setParent(cloneNode);
                cloneChild.parent = cloneNode;
                cloneNode.children.put(tableNameChild, cloneChild);

            }

            return cloneNode;

        }

    }

    @Override
    public String toString() {

        return printGraphviz(false);

    }

}
