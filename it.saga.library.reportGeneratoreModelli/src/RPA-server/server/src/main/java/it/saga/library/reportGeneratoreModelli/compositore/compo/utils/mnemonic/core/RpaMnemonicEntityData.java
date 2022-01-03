package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaLackEntitiesBindException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaReachEntityException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDebugMessages;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFieldsComparator.ORDER;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNodeRoot;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class RpaMnemonicEntityData {

    private static final String PRIMARY_KEY_FIELD_NAME = "pkid";

    private RpaMainCompositore  mainCompositore;
    private RpaDebugMessages    debugMessages;

    private List<RpaMnemonicFields> mnemonicFieldsList;
    private List<RpaMnemonicFields> orderedMnemonicFieldsList;

    private RpaMnemonicGraph    mnemonicGraph;
    private RpaPathTreeNode     pathTreeNode;
    private RpaMnemonicNode     entityMnemonicNode;

    private String  name;
    private String  domain;
    private String  query;
    private Integer currentIndexOrderedList;
    private int     currentIndex;
    private String  loopBindName;

    public RpaMnemonicEntityData(RpaMainCompositore mainCompositore, String name, String domain, RpaMnemonicGraph mnemonicGraph, RpaPathTreeNode pathTreeNode) {

        this.mainCompositore    = mainCompositore;
        this.name               = name;
        this.domain             = domain;
        this.mnemonicGraph      = mnemonicGraph;
        this.pathTreeNode       = pathTreeNode;
        this.mnemonicFieldsList = new ArrayList<RpaMnemonicFields>();
        this.currentIndex       = 1;
        this.debugMessages      = mainCompositore.getDebugMessages();
        this.entityMnemonicNode = searchEntityOnGraph();

    }

    public String getName() {

        return name;

    }

    public String getQuery() {

        return query;

    }

    public void setQuery(String query) {

        this.query = query;

    }

    public List<RpaMnemonicFields> getMnemonicFieldsList() {

        if (orderedMnemonicFieldsList != null) {

            return orderedMnemonicFieldsList;

        } else {

            return mnemonicFieldsList;

        }

    }

    protected List<RpaMnemonicFields> getOrderedMnemonicFieldsList() {

        return orderedMnemonicFieldsList;

    }

    public void setMnemonicFieldsList(List<RpaMnemonicFields> mnemonicFieldsList) {

        this.mnemonicFieldsList = mnemonicFieldsList;

    }

    public RpaMnemonic getCurrentMnemonicValue(String fieldName) {

        if (orderedMnemonicFieldsList != null) {

            if ((currentIndexOrderedList - 1) < orderedMnemonicFieldsList.size()) {

                try {
                    return this.orderedMnemonicFieldsList.get(currentIndexOrderedList - 1).getValue(fieldName);
                } catch (IndexOutOfBoundsException exception) {

                    exception.printStackTrace();

                }

                return this.orderedMnemonicFieldsList.get(currentIndexOrderedList - 1).getValue(fieldName);

            } else {

                return null;

            }

        } else {

            if ((currentIndex - 1) < mnemonicFieldsList.size()) {

                try {
                    this.mnemonicFieldsList.get(currentIndex - 1).getValue(fieldName).updatePrintValue();
                    return this.mnemonicFieldsList.get(currentIndex - 1).getValue(fieldName);
                } catch (ArrayIndexOutOfBoundsException exception) {

                    exception.printStackTrace();
                    throw exception;

                }

            } else {

                return null;

            }

        }

    }

    public Integer getCurrentMnemonicValueType(String fieldName) {

        if (orderedMnemonicFieldsList != null) {

            if ((currentIndexOrderedList - 1) < orderedMnemonicFieldsList.size()) {

                return this.orderedMnemonicFieldsList.get(currentIndexOrderedList - 1).getValueType(fieldName);

            } else {

                return null;

            }

        } else {

            if ((currentIndex - 1) < mnemonicFieldsList.size()) {

                return this.mnemonicFieldsList.get(currentIndex - 1).getValueType(fieldName);

            } else {

                return null;

            }

        }

    }

    public Integer getCurrentMnemonicValueType(String fieldName, int index) {

        if (orderedMnemonicFieldsList != null) {

            if ((index - 1) < orderedMnemonicFieldsList.size()) {

                return this.orderedMnemonicFieldsList.get(index - 1).getValueType(fieldName);

            } else {

                return null;

            }

        } else {

            if ((index - 1) < mnemonicFieldsList.size()) {

                return this.mnemonicFieldsList.get(index - 1).getValueType(fieldName);

            } else {

                return null;

            }

        }

    }

    public RpaMnemonicFields getCurrentMnemonicFields() {

        if (orderedMnemonicFieldsList != null) {

            if ((currentIndexOrderedList - 1) < orderedMnemonicFieldsList.size()) {

                return this.orderedMnemonicFieldsList.get(currentIndexOrderedList - 1);

            } else {

                return null;

            }

        } else {

            if ((currentIndex - 1) < mnemonicFieldsList.size()) {

                return this.mnemonicFieldsList.get(currentIndex - 1);

            } else {

                return null;

            }

        }


    }

    public RpaMnemonic getMnemonicValue(String fieldName, int index) {

        if (orderedMnemonicFieldsList != null) {

            if ((index - 1) < orderedMnemonicFieldsList.size()) {

                return this.orderedMnemonicFieldsList.get(index - 1).getValue(fieldName);

            } else {

                return null;

            }

        } else {

            if ((index - 1) < mnemonicFieldsList.size()) {

                return this.mnemonicFieldsList.get(index - 1).getValue(fieldName);

            } else {

                return null;

            }

        }

    }

    public int getCurrentIndex() {

        if (currentIndexOrderedList != null) {

            return currentIndexOrderedList + 1;

        } else {

            return currentIndex + 1;

        }

    }

    public void setCurrentIndex(int currentIndex) {

        if (currentIndexOrderedList != null) {

            this.currentIndexOrderedList = currentIndex - 1;
            // this.currentIndexOrderedList = Math.max(currentIndex - 1, 1);

        } else {

            this.currentIndex = currentIndex - 1;
            // this.currentIndex = Math.max(currentIndex - 1, 1);

        }

    }

    public String getDomain() {

        return domain;

    }

    public boolean isEmpty() {

        return mnemonicFieldsList == null || mnemonicFieldsList.isEmpty();

    }

    public boolean isSorted() {

        return orderedMnemonicFieldsList != null;

    }

    public void setMnemonicGraphRootCondition(String rootCondition) {

        mnemonicGraph.setRootCondition(rootCondition);

    }

    public RpaPathTreeNode getPathTreeNode() {

        return pathTreeNode;

    }

    public RpaMnemonicGraph getMnemonicGraph() {

        return mnemonicGraph;

    }

    public String getLoopBindName() {

        return loopBindName;

    }

    public void setLoopBindName(String loopBindName) {

        this.loopBindName = loopBindName;

    }

    private boolean hasDataRecursive(RpaMnemonicEntityData mnemonicEntityData, String tableName) {

        // Caso base: Il "mnemonicEntityData" è associato alla tabella
        if (mnemonicEntityData.getName().equals(tableName)) {

            return !mnemonicEntityData.getMnemonicFieldsList().isEmpty();

        }

        // Caso ricorsivo: Cerco una corrispondenza in uno dei figli/eredi di "mnemonicEntityData"
        else {

            List<RpaMnemonicFields> mnemonicFieldsList = mnemonicEntityData.getMnemonicFieldsList();

            if (!mnemonicFieldsList.isEmpty()) {

                boolean isSubEntityFound = false;

                for (RpaMnemonicFields mnemonicFields : mnemonicFieldsList) {

                    Map<String, RpaMnemonicEntityData> subMnemonicEntityDataMap = mnemonicFields.getSubMnemonicEntityMap();
                    if (subMnemonicEntityDataMap != null && !subMnemonicEntityDataMap.isEmpty()) {

                        for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : subMnemonicEntityDataMap.entrySet()) {

                            String              subTableName            = subMnemonicEntityDataEntry.getKey();
                            RpaMnemonicEntityData subMnemonicEntityData   = subMnemonicEntityDataEntry.getValue();

                            isSubEntityFound = hasDataRecursive(subMnemonicEntityData, tableName);
                            if (isSubEntityFound) {

                                break;

                            }

                        }

                    }

                    if (isSubEntityFound) {

                        break;

                    }

                }

                return isSubEntityFound;

            } else {

                return false;

            }

        }

    }

    private RpaMnemonicNode searchEntityOnGraph() {

        // Cerco all'interno del grafo delle entità il nodo associato a questo "MnemonicEntityData"
        String entityLabel = this.name + "." + this.domain;

        if (mnemonicGraph.isEntityPresent(entityLabel)) {

            return mnemonicGraph.getNode(entityLabel);

        }

        // Se non ho trovato il nodo, lo aggiungo al grafo
        RpaMnemonicNode newMnemonicNode = new RpaMnemonicNode(mnemonicGraph, entityLabel);

        if (!mnemonicGraph.addNode(newMnemonicNode)) {

            String code     = entityLabel;
            String message  = "L'entità " + this.name + " non è collegata a nessun'altra entità dello scope più interno";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        } else {

            return newMnemonicNode;

        }

    }

    public void sort(List<String> fieldNameList, List<ORDER> orderList) throws Exception {

        // Recupero gli elementi di questa entità ordinati secondo i mnemonici passati in input
        // MnemonicManager mnemonicManager = MnemonicManager.getMnemonicManager();
        RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();
        List<Map<String, String>> orderedFieldsList;

        try {

            orderedFieldsList =
                    mnemonicManager.getEntitySortedFieldsList(this, fieldNameList, orderList);

        } catch (Exception exception) {

            throw new Exception("Errore nell'ordinamento");

        }

        // Creo la nuova lista ordinata
        List<RpaMnemonicFields> orderedList = new ArrayList<RpaMnemonicFields>();

        for (Map<String, String> fields : orderedFieldsList) {

            for (RpaMnemonicFields mnemonicFields : mnemonicFieldsList) {

                boolean isEveryFieldMatch = true;

                for (Map.Entry<String, String> fieldEntry : fields.entrySet()) {

                    String fieldName    = fieldEntry.getKey().toLowerCase();
                    String fieldValue   = fieldEntry.getValue();

                    if (!mnemonicFields.getValues().containsKey(fieldName)) {

                        throw new Exception("Il campo della order by non è presente nell'entità letta in precedenza");

                    }

                    String mnemonicFieldValue = mnemonicFields.getValues().get(fieldName).getOriginalValue();

                    boolean isFieldsNull = mnemonicFieldValue == null && fieldValue == null;

                    if (!isFieldsNull) {

                        if ((mnemonicFieldValue != null && fieldValue == null) || (mnemonicFieldValue == null && fieldValue != null)) {

                            isEveryFieldMatch = false;
                            break;

                        }

                        else if (!mnemonicFieldValue.equals(fieldValue)) {

                             isEveryFieldMatch = false;
                            break;

                        }

                    }

                }

                if (isEveryFieldMatch) {

                    orderedList.add(mnemonicFields);
                    break;

                }

            }

        }

        // Controllo di aver ordinato tutti gli elementi
        if (orderedList.size() != mnemonicFieldsList.size()) {

            throw new Exception("Non tutti gli elementi sono stati ordinati");

        }

        // Salvo la lista
        this.currentIndexOrderedList    = 1;
        this.orderedMnemonicFieldsList  = orderedList;

    }

    /**
     * Verifica se esiste un "MnemonicEntityData" (in esso o in uno dei suoi eredi/figli)
     * associato alla stessa tabella passata in ingresso in cui è presente almeno un "MnemonicFields"
     * @param tableName
     * @return
     */
    public boolean hasData(String tableName) {

        return hasDataRecursive(this, tableName);

    }

    public RpaMnemonicEntityData searchEntityWithLoopBind(String loopName) {

        return recursiveSearchEntityWithLoopBind(loopName, this);

    }

    private RpaMnemonicEntityData recursiveSearchEntityWithLoopBind(String loopName, RpaMnemonicEntityData mnemonicEntityData) {

        // CASO BASE
        if (mnemonicEntityData.getLoopBindName() != null && mnemonicEntityData.getLoopBindName().equals(loopName)) {

            return mnemonicEntityData;

        }

        else {

            // CASO BASE
            if (mnemonicEntityData.getMnemonicFieldsList() == null || mnemonicEntityData.getMnemonicFieldsList().isEmpty()) {

                return null;

            }

            // CASO RICORSIVO
            for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getMnemonicFieldsList()) {

                for (Map.Entry<String, RpaMnemonicEntityData> childMnemonicEntityEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                    RpaMnemonicEntityData childMnemonicEntityData = childMnemonicEntityEntry.getValue();

                    if (childMnemonicEntityData != null) {

                        RpaMnemonicEntityData childEntityDataWithBindLoop = childMnemonicEntityData.searchEntityWithLoopBind(loopName);

                        if (childEntityDataWithBindLoop != null) {

                            return childEntityDataWithBindLoop;

                        }

                    }

                }

            }

            // CASO BASE
            return null;

        }

    }

    public void sortMnemonicFieldsList(String fieldName, ORDER order) {

        List<String> fieldNameList = new ArrayList<String>();
        fieldNameList.add(fieldName);

        List<ORDER> orderList = new ArrayList<ORDER>();
        orderList.add(order);

        sortMnemonicFieldsList(fieldNameList, orderList);

    }

    public void sortMnemonicFieldsList(List<String> fieldNameList, List<ORDER> orderList) {

        // Se non ho elementi, esco
        if (mnemonicFieldsList == null || mnemonicFieldsList.isEmpty()) {

            return;

        }

        List<Integer> fieldTypeList = new ArrayList<Integer>();

        // Controllo ogni fieldName
        for (int i = 0; i < fieldNameList.size(); i ++) {

            String fieldName = fieldNameList.get(i).toLowerCase();

            // Controllo che ogni campo sia presente
            if (mnemonicFieldsList.get(0).getValueType(fieldName) == null) {

                return;

            }

            // Passo a lowercase ogni fieldName
            fieldNameList.set(i, fieldName.toLowerCase());

            // Recupero la tipologia dei campi
            fieldTypeList.add(getCurrentMnemonicValueType(fieldName));

        }

        // Copio gli elementi nella nuova lista
        List<RpaMnemonicFields> orderedList = new ArrayList<RpaMnemonicFields>();

        for (RpaMnemonicFields mnemonicFields : mnemonicFieldsList) {

            orderedList.add(mnemonicFields);

        }

        // Ordino la nuova lista
        RpaMnemonicFieldsComparator sortComparator = new RpaMnemonicFieldsComparator(fieldNameList, fieldTypeList, orderList);
        Collections.sort(orderedList, sortComparator);
        // orderedList.sort(sortComparator);

        // TODO: --- cancellare --- INIZIO TEST STAMPA
        // System.out.println("\n\nOrdered list:\n");
        debugMessages.print("\n\nOrdered list:\n");
        for (RpaMnemonicFields mnemonicFields : orderedList) {

            String fieldsConcatName = "{";

            for (String fieldName : fieldNameList) {

                fieldsConcatName += mnemonicFields.getValue(fieldName).getPrintedValue() + ",\t";

            }

            fieldsConcatName += "}";

            // System.out.println("\t" + fieldsConcatName);
            debugMessages.print("\t" + fieldsConcatName);

        }
        // System.out.println("\n\n");
        debugMessages.print("\n\n");
        // TODO: --- cancellare --- FINE TEST STAMPA

        // Salvo la lista
        this.currentIndexOrderedList    = 1;
        this.orderedMnemonicFieldsList  = orderedList;

    }

    /**
     * Ricerca il data "MnemonicEntityData" associato al mnemonico passato
     * Inoltre aggiorna il "MnemonicEntityData" sul quale viene chiamato questo metodo
     *
     * @param cachePathTreeNode
     * @param mnemonicName
     * @return
     */
    public RpaMnemonicEntityData retrieveMnemonicData(RpaPathTreeNode cachePathTreeNode, String mnemonicName, String previousMnemonicName) {

        // TODO: Completare il metodo con il controllo sulla tipologia di colonne "DATE" da gestire nella "WHERE"
        // System.out.println("[Mnemonic] Find data for " + mnemonicName);
        debugMessages.print("[Mnemonic] Find data for " + mnemonicName);

        RpaMnemonicManager  mnemonicManager = mainCompositore.getMnemonicManager();
        Stack<String>       entitySequenceStack;


        // ### Definisco la gerarchia di entità (A->B->C) ###


        // Controllo se devo forzare il collegamento del mnemonico a quello precedente
        if (previousMnemonicName != null) {

            // Recupero l'entità del mnemonico precedente
            String previousMnemonicEntityName = mnemonicManager.translateMnemonicName(previousMnemonicName);

            if (previousMnemonicEntityName == null || previousMnemonicEntityName.isEmpty()) {

                return null;

            }

            String previousTableNameWithDomain = previousMnemonicEntityName.split("\\.", 2)[1];

            // Recupero l'entità del mnemonico
            String mnemonicEntityName = mnemonicManager.translateMnemonicName(mnemonicName);

            if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

                return null;

            }

            String tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

            // Se esiste, rimuovo dal mnemonicGraph il riferimento del mnemonico corrente (elimino la sotto-radice)
            mnemonicGraph.delete(tableNameWithDomain);

            // Recupero il MnemonicGraph-node del mnemonico precedente
            RpaMnemonicNode previousNode = null;
            boolean isPreviousEntityPresent = mnemonicGraph.isEntityPresent(previousTableNameWithDomain);

            // Aggiungo l'entità del mnemonico precedente (se non è già presente)
            if (isPreviousEntityPresent) {

                previousNode = mnemonicGraph.getNode(previousTableNameWithDomain);

            } else {

                RpaMnemonicNode newMnemonicNode = new RpaMnemonicNode(mnemonicGraph, previousTableNameWithDomain);

                if (!mnemonicGraph.addNode(newMnemonicNode)) {

                    RpaMnemonicNodeRoot rootNode = mnemonicGraph.getRootNode();
                    String code = mnemonicName;
                    String message = "L'entità del mnemonico precedente '" + previousMnemonicName + "' " +
                            "non può raggiungere l'entità radice '" + rootNode.getEntityTableName() + "'";
                    int context = RpaComposerException.COMPILE_MESSAGE;

                    throw new RpaLackEntitiesBindException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

                } else {

                    previousNode = newMnemonicNode;

                }

            }

            // Creo un nuovo MnemonicGraph-node per il mnemonico corrente
            RpaMnemonicNode newNode = new RpaMnemonicNode(mnemonicGraph, tableNameWithDomain);
            if (!mnemonicGraph.addNode(previousNode, newNode)) {

                RpaMnemonicNodeRoot rootNode = mnemonicGraph.getRootNode();
                String code = mnemonicName;
                String message = "L'entità del mnemonico precedente '" + previousMnemonicName + "' " +
                        "non può raggiungere l'entità '" + tableNameWithDomain + " di #" + mnemonicName + "#'";
                int context = RpaComposerException.COMPILE_MESSAGE;

                throw new RpaLackEntitiesBindException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

            }

            // Cerco la sequenza di entità da percorrere
            entitySequenceStack = cachePathTreeNode.findEntitySequence(previousNode, newNode);

        }

        // Se non devo forzare il collegamento del mnemonico a quello precedente, effettuo una lettura del primo
        else {

            // Recupero l'entità del mnemonico
            String mnemonicEntityName = mnemonicManager.translateMnemonicName(mnemonicName);

            if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

                return null;

            }

            String tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];

            // Controllo se il mnemonico appartiene al nodo radice
            RpaMnemonicNode currentNode = null;
            boolean isEntityPresent = mnemonicGraph.isEntityPresent(tableNameWithDomain);

            // Aggiungo l'entità (se non è già presente)
            if (isEntityPresent) {

                currentNode = mnemonicGraph.getNode(tableNameWithDomain);

            } else {

                RpaMnemonicNode newMnemonicNode = new RpaMnemonicNode(mnemonicGraph, tableNameWithDomain);

                if (!mnemonicGraph.addNode(newMnemonicNode)) {

                    RpaMnemonicNodeRoot rootNode = mnemonicGraph.getRootNode();
                    String code = mnemonicName;
                    String message = "L'entità del mnemonico '" + mnemonicName + "' " +
                            "non può raggiungere l'entità radice '" + rootNode.getEntityTableName() + "'";
                    int context = RpaComposerException.COMPILE_MESSAGE;

                    throw new RpaLackEntitiesBindException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

                } else {

                    currentNode = newMnemonicNode;

                }

            }

            // Cerco la sequenza di entità da percorrere
            entitySequenceStack = cachePathTreeNode.findEntitySequence(currentNode);

        }


        // ### Se ho forzato il collegamento con l'elemento letto in precedenza, cancello parzialmente i dati ###
        if (previousMnemonicName != null) {

            removeMnemonicData(previousMnemonicName, mnemonicName);

        }


        // ### Riprendo il percorso già definito (nel caso lo completo) (A[current_index]->B[current_index]->...) ###


        // Se ho 1 solo elemento nello stack, significa che il mnemonico è nel nodo radice
        if (entitySequenceStack.size() == 1) {

            return this;

        }

        String fromSqlQuery     = "";
        String whereSqlQuery    = "";
        String joinSqlQuery     = "";

        // Inizializzo la query con l'elemento padre
        fromSqlQuery    = " FROM " + this.getName();

        // Associo "previousMnemonicEntityData" al nodo padre
        RpaMnemonicEntityData previousMnemonicEntityData = this;

        // String currentEntityFromStack = entitySequenceStack.pop();
        String currentEntityFromStack   = entitySequenceStack.pop();
        String currentTableFromStack    = currentEntityFromStack.split("\\.")[0];
        String currentDomainFromStack   = currentEntityFromStack.split("\\.")[1];

        String nextEntityName   = entitySequenceStack.peek();
        String nextTableName    = nextEntityName.split("\\.")[0];
        String nextDomainName   = nextEntityName.split("\\.")[1];

        RpaMnemonicEntityData currentMnemonicEntityData =
                this.getCurrentMnemonicFields().getSubMnemonicEntity(nextTableName);

        // Ciclo su ogni entità già definita nel documento
        while (!entitySequenceStack.isEmpty()) {

            // currentEntityFromStack = entitySequenceStack.pop();
            currentEntityFromStack  = entitySequenceStack.pop();
            currentTableFromStack   = currentEntityFromStack.split("\\.")[0];
            currentDomainFromStack  = currentEntityFromStack.split("\\.")[1];

            // Se l'entità corrente non è nulla:
            if (currentMnemonicEntityData != null) {

                // Se l'entità corrente è l'ultima
                if (entitySequenceStack.isEmpty()) {

                    // Restituisco il valore all'indice corrente
                    return currentMnemonicEntityData;

                }

                if (currentMnemonicEntityData.getName().equals(previousMnemonicEntityData.getName())) {

                    String code     = mnemonicName;
                    String message  = "Entità duplicate nel percorso";
                    int context = RpaComposerException.COMPILE_MESSAGE;

                    throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

                }

                // Aggiorno la query per recuperare gli elementi mancanti al livello più basso
                RpaMnemonicManager.EntitiesBind entitiesBind =
                        mnemonicManager.getEntitiesBind(currentMnemonicEntityData.getName(), previousMnemonicEntityData.getName());

                String currentEntityName    = currentMnemonicEntityData.getName();
                String previousEntityName   = previousMnemonicEntityData.getName();
                String parentFieldValue     = previousMnemonicEntityData.getCurrentMnemonicValue(entitiesBind.getParentBindFields()[0].toLowerCase()).getValue();
                if (previousMnemonicEntityData.getCurrentMnemonicValue(entitiesBind.getParentBindFields()[0].toLowerCase()).getType() == RpaMnemonic.TYPE_STRING) {

                    parentFieldValue = "'" + parentFieldValue + "'";

                }

                for (int i = 0; i < entitiesBind.getCurrentBindFields().length; i++) {

                    String currentBindField = entitiesBind.getCurrentBindFields()[i];
                    String parentBindField  = entitiesBind.getParentBindFields()[i];

                    if (i == 0) {

                        joinSqlQuery += " JOIN " + currentEntityName + " ON (";
                        joinSqlQuery += previousEntityName + "." + parentBindField + " = " + currentEntityName + "." + currentBindField;

                        if (entitiesBind.getCurrentBindFields().length == 1) {

                            joinSqlQuery += ")";

                        }

                    } else if (i == entitiesBind.getCurrentBindFields().length - 1) {

                        joinSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + currentEntityName + "." + currentBindField + ")";

                    } else {

                        joinSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + currentEntityName + "." + currentBindField;

                    }

                    String whereSqlFieldName    = parentBindField;
                    String whereSqlFieldValue   = previousMnemonicEntityData.getCurrentMnemonicFields().getValue(parentBindField.toLowerCase()).getValue();
                    if (previousMnemonicEntityData.getCurrentMnemonicFields().getValue(whereSqlFieldName.toLowerCase()).getType() == RpaMnemonic.TYPE_STRING) {

                        whereSqlFieldValue = "'" + whereSqlFieldValue + "'";

                    }

                    if (whereSqlQuery.isEmpty()) {

                        whereSqlQuery += " WHERE " + mnemonicGraph.getRootCondition();

                        // CASO [JOIN]: Indicizzo il risultato alla riga corrente dell'entità padre
                        if (!whereSqlQuery.toUpperCase().contains(previousEntityName.toUpperCase() + "." + parentBindField.toUpperCase())) {

                            // TODO: IF DA TESTARE!!!
                            /*
                            if (whereSqlQuery.equals(" WHERE ")) {

                                whereSqlQuery += previousEntityName + "." + parentBindField + " = " + parentFieldValue;

                            } else {

                                whereSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + parentFieldValue;

                            }
                            */
                            whereSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + parentFieldValue;

                        }

                    } else {

                        // TODO: IF DA TESTARE!!!
                        /*
                        if (whereSqlQuery.equals(" WHERE ")) {

                            whereSqlQuery += previousEntityName + "." + whereSqlFieldName + " = " + whereSqlFieldValue;

                        } else {

                            whereSqlQuery += " AND " + previousEntityName + "." + whereSqlFieldName + " = " + whereSqlFieldValue;

                        }
                        */
                        whereSqlQuery += " AND " + previousEntityName + "." + whereSqlFieldName + " = " + whereSqlFieldValue;

                    }

                }

                // Passo all'elemento successivo
                nextEntityName  = entitySequenceStack.peek();
                nextTableName   = nextEntityName.split("\\.")[0];
                nextDomainName  = nextEntityName.split("\\.")[1];

                previousMnemonicEntityData = currentMnemonicEntityData;

                currentMnemonicEntityData =
                        currentMnemonicEntityData.getCurrentMnemonicFields().getSubMnemonicEntity(nextTableName);


            }

            // Se l'entità corrente è nulla:
            else {

                // Creo una nuova entità
                RpaMnemonicEntityData newMnemonicEntityData =
                        new RpaMnemonicEntityData(mainCompositore, currentTableFromStack, currentDomainFromStack, mnemonicGraph, pathTreeNode);
                // new MnemonicEntityData(currentTableFromStack, currentDomainFromStack, mnemonicGraph, pathTreeNode);

                // Aggiorno la query
                RpaMnemonicManager.EntitiesBind entitiesBind =
                        mnemonicManager.getEntitiesBind(newMnemonicEntityData.getName(), previousMnemonicEntityData.getName());

                String currentEntityName    = newMnemonicEntityData.getName();
                String previousEntityName   = previousMnemonicEntityData.getName();
                String parentFieldValue     = previousMnemonicEntityData.getCurrentMnemonicValue(entitiesBind.getParentBindFields()[0].toLowerCase()).getValue();
                if (previousMnemonicEntityData.getCurrentMnemonicValue(entitiesBind.getParentBindFields()[0].toLowerCase()).getType() == RpaMnemonic.TYPE_STRING) {

                    parentFieldValue = "'" + parentFieldValue + "'";

                }

                for (int i = 0; i < entitiesBind.getCurrentBindFields().length; i++) {

                    String currentBindField = entitiesBind.getCurrentBindFields()[i];
                    String parentBindField  = entitiesBind.getParentBindFields()[i];

                    if (i == 0) {

                        joinSqlQuery += " JOIN " + currentEntityName +" ON (";
                        joinSqlQuery += previousEntityName + "." + parentBindField + " = " + currentEntityName + "." + currentBindField;

                        if (entitiesBind.getCurrentBindFields().length == 1) {

                            joinSqlQuery += ")";

                        }

                    } else if (i == entitiesBind.getCurrentBindFields().length - 1) {

                        joinSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + currentEntityName + "." + currentBindField + ")";

                    } else {

                        joinSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + currentEntityName + "." + currentBindField;

                    }

                    String whereSqlFieldName    = parentBindField.split("\\.")[0].toLowerCase();
                    String whereSqlFieldValue   = previousMnemonicEntityData.getCurrentMnemonicFields().getValue(whereSqlFieldName.toLowerCase()).getValue();
                    if (previousMnemonicEntityData.getCurrentMnemonicFields().getValue(whereSqlFieldName.toLowerCase()).getType() == RpaMnemonic.TYPE_STRING) {

                        whereSqlFieldValue = "'" + whereSqlFieldValue + "'";

                    }

                    if (whereSqlQuery.isEmpty()) {

                        whereSqlQuery += " WHERE " + mnemonicGraph.getRootCondition();

                        // CASO [JOIN]: Indicizzo il risultato alla riga corrente dell'entità padre
                        if (!whereSqlQuery.toUpperCase().contains(previousEntityName.toUpperCase() + "." + parentBindField.toUpperCase())) {

                            // TODO: IF DA TESTARE!
                            /*
                            if (whereSqlQuery.equals(" WHERE ")) {

                                whereSqlQuery += previousEntityName + "." + parentBindField + " = " + parentFieldValue;

                            } else {

                                whereSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + parentFieldValue;

                            }
                            */
                            whereSqlQuery += " AND " + previousEntityName + "." + parentBindField + " = " + parentFieldValue;

                        }

                    } else {

                        // TODO: IF DA TESTARE!
                        /*
                        if (whereSqlQuery.equals(" WHERE ")) {

                            whereSqlQuery += previousEntityName + "." + whereSqlFieldName + " = " + whereSqlFieldValue;

                        } else {

                            whereSqlQuery += " AND " + previousEntityName + "." + whereSqlFieldName + " = " + whereSqlFieldValue;

                        }
                        */
                        whereSqlQuery += " AND " + previousEntityName + "." + whereSqlFieldName + " = " + whereSqlFieldValue;

                    }

                }

                // Provo a definire l'entità mancante eseguendo la query
                List<RpaMnemonicFields> newNodeMnemonicFields = mnemonicManager.getNodeMnemonicFields(
                        newMnemonicEntityData,
                        fromSqlQuery,
                        joinSqlQuery,
                        whereSqlQuery);

                // Controllo se esiste un collegamento con la nuova entità
                if (newNodeMnemonicFields == null || newNodeMnemonicFields.isEmpty()) {

                    return null;

                }

                newMnemonicEntityData.setMnemonicFieldsList(newNodeMnemonicFields);

                currentMnemonicEntityData = newMnemonicEntityData;

                // Aggiungo la nuova entità a quella precedente
                previousMnemonicEntityData.getCurrentMnemonicFields().setSubMnemonicEntity(newMnemonicEntityData.getName(), newMnemonicEntityData);

                // Se l'entità corrente è l'ultima
                if (entitySequenceStack.isEmpty()) {

                    // Restituisco il valore all'indice corrente
                    return currentMnemonicEntityData;

                }

                // Passo all'elemento successivo
                previousMnemonicEntityData = currentMnemonicEntityData;

                currentMnemonicEntityData = null;

            }

        }

        RpaMnemonicNode rootNode = mnemonicGraph.getRootNode();
        String          code     = mnemonicName;
        String          message  = "L'entità del mnemonico '" + mnemonicName + "' " +
                "non può raggiungere l'entità padre '" + rootNode.getEntityTableName() + "'";
        int context = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaLackEntitiesBindException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

    }

    private void removeMnemonicData(String previousMnemonicName, String mnemonicName) {

        RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

        // Recupero l'entità del mnemonico precedente
        String previousMnemonicEntityName = mnemonicManager.translateMnemonicName(previousMnemonicName);

        if (previousMnemonicEntityName == null || previousMnemonicEntityName.isEmpty()) {

            return;

        }

        String previousTableName = previousMnemonicEntityName.split("\\.")[1];

        // Recupero l'entità del mnemonico
        String mnemonicEntityName = mnemonicManager.translateMnemonicName(mnemonicName);

        if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

            return;

        }

        String tableName = mnemonicEntityName.split("\\.")[1];

        // Se l'entità del mnemonico da leggere non è presente, esco senza far nulla
        if (!isEntityPresent(this, tableName)) {

            return;

        }

        // Altrimenti cancello tutti i riferimenti che puntano ai suoi MnemonicEntityData
        else {

            recursiveRemoveMnemonicData(this, tableName);

        }

    }

    private boolean isEntityPresent(RpaMnemonicEntityData mnemonicEntityData, String nameToFound) {

        // Caso base
        if (mnemonicEntityData.getName().equals(nameToFound)) {

            return true;

        }

        // Caso ricorsivo
        else {

            // Cerco ricorsivamente su tutti i figli (ordinati)
            if (mnemonicEntityData.getOrderedMnemonicFieldsList() != null) {

                for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getOrderedMnemonicFieldsList()) {

                    for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                        if (isEntityPresent(subMnemonicEntityDataEntry.getValue(), nameToFound)) {

                            return true;

                        }

                    }

                }

            }

            // Cerco ricorsivamente su tutti i figli
            for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getMnemonicFieldsList()) {

                for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                    if (isEntityPresent(subMnemonicEntityDataEntry.getValue(), nameToFound)) {

                        return true;

                    }

                }

            }

            return false;

        }

    }

    public void delete(String tableName) {

        recursiveRemoveMnemonicData(this, tableName);

    }

    private void recursiveRemoveMnemonicData(RpaMnemonicEntityData mnemonicEntityData, String nameToDelete) {

        // Caso base
        boolean hasMnemonicEntityChildren = false;

        if (mnemonicEntityData.getOrderedMnemonicFieldsList() != null) {

            for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getOrderedMnemonicFieldsList()) {

                if (!mnemonicFields.getSubMnemonicEntityMap().isEmpty()) {

                    hasMnemonicEntityChildren = true;
                    break;

                }

            }

        }

        if (!hasMnemonicEntityChildren) {

            for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getMnemonicFieldsList()) {

                if (!mnemonicFields.getSubMnemonicEntityMap().isEmpty()) {

                    hasMnemonicEntityChildren = true;
                    break;

                }

            }

        }

        if (!hasMnemonicEntityChildren) {

            return;

        }

        // Caso ricorsivo
        else {

            // Cerco ricorsivamente su tutti i figli (ordinati) il riferimento da togliere
            if (mnemonicEntityData.getOrderedMnemonicFieldsList() != null) {

                for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getOrderedMnemonicFieldsList()) {

                    boolean isFoundAtLeastChildToRemove = false;
                    for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                        if (subMnemonicEntityDataEntry.getKey().equals(nameToDelete)) {

                            isFoundAtLeastChildToRemove = true;

                        }

                        recursiveRemoveMnemonicData(subMnemonicEntityDataEntry.getValue(), nameToDelete);

                    }

                    if (isFoundAtLeastChildToRemove) {

                        mnemonicFields.getSubMnemonicEntityMap().remove(nameToDelete);

                    }

                }

            }

            // Cerco ricorsivamente su tutti i figli il riferimento da togliere
            for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getMnemonicFieldsList()) {

                boolean isFoundAtLeastChildToRemove = false;
                for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                    if (subMnemonicEntityDataEntry.getKey().equals(nameToDelete)) {

                        isFoundAtLeastChildToRemove = true;

                    }

                    recursiveRemoveMnemonicData(subMnemonicEntityDataEntry.getValue(), nameToDelete);

                }

                if (isFoundAtLeastChildToRemove) {

                    mnemonicFields.getSubMnemonicEntityMap().remove(nameToDelete);

                }

            }

        }

    }

    public String getEntityName() {

        return name + "." + domain;

    }

    public void checkAndCleanForConnectionToRoot(String tableNameWithDomain) {

        // Ricerco l'entityData la cui entità corrisponde a quella passata in ingresso per cancellarla
        if (this.getEntityName().equals(tableNameWithDomain)) {

            return;

        }

        recursiveCheckAndCleanForConnectionToRoot(tableNameWithDomain);

    }

    private void recursiveCheckAndCleanForConnectionToRoot(String tableNameWithDomain) {

        // CASO RISCORSIVO: Cerco tra tutti gli entityData figli quelli da cancellare
        for (RpaMnemonicFields mnemonicFields : this.getMnemonicFieldsList()) {

            for (Map.Entry<String, RpaMnemonicEntityData> childEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                String                  childKey    = childEntry.getKey();
                RpaMnemonicEntityData   child       = childEntry.getValue();

                if (child.getEntityName().equals(tableNameWithDomain)) {

                    // CANCELLO
                    mnemonicFields.getSubMnemonicEntityMap().remove(childKey);

                } else {

                    // VADO IN RICORSIONE
                    child.recursiveCheckAndCleanForConnectionToRoot(tableNameWithDomain);

                }

            }

        }

        // CASO BASE: Non ho più figli

    }

    public RpaMnemonicEntityData clone(RpaMnemonicGraph cloneMnemonicGraph, RpaPathTreeNode clonePathTreeNode) {

        RpaMnemonicEntityData clone = new RpaMnemonicEntityData(
                this.mainCompositore,
                this.name,
                this.domain,
                cloneMnemonicGraph,
                clonePathTreeNode
        );

        for (RpaMnemonicFields mnemonicFields : mnemonicFieldsList) {

            RpaMnemonicFields cloneMnemonicFields = mnemonicFields.clone(clone);
            clone.mnemonicFieldsList.add(cloneMnemonicFields);

        }

        if (orderedMnemonicFieldsList != null) {

            clone.orderedMnemonicFieldsList = new ArrayList<RpaMnemonicFields>();

            for (RpaMnemonicFields mnemonicFields : orderedMnemonicFieldsList) {

                RpaMnemonicFields cloneMnemonicFields = mnemonicFields.clone(clone);
                clone.orderedMnemonicFieldsList.add(cloneMnemonicFields);

            }

        }

        clone.query         = this.query;
        clone.currentIndex  = this.currentIndex;
        clone.loopBindName  = this.loopBindName;

        if (this.currentIndexOrderedList != null) {

            clone.currentIndexOrderedList = new Integer(this.currentIndexOrderedList.intValue());

        } else {

            clone.currentIndexOrderedList = null;

        }

        return clone;

    }

    /**
     * Stampa nella console di DEBUG le tabelle di collegamento.
     * Il formato è quello di "Graphviz"
     *
     * @param isShowEdgesLabel
     * @return
     */
    public String printGraphviz(boolean isShowEdgesLabel, String parentId) {

        // Eseguo in ricorsione la stampa da questa tabella verso tutte quelle a cui si collega
        String graphVizPortrayl             = "";
        String graphVizPortraylTable        = "";
        String graphVizPortraylBinds        = "";
        String tableName                    = "";
        String graphVizPortraylSubEntity    = "";

        if (parentId.isEmpty()) {

            tableName = this.name;

        } else {

            tableName = parentId + "_" + this.name;

        }

        int subEntityIndex = 0;

        for (RpaMnemonicFields mnemonicFields : mnemonicFieldsList) {

            // Costruisco la tabella corrente
            String      primaryKeyFieldName = null;
            RpaMnemonic primaryKeyField     = null;

            if (PRIMARY_KEY_FIELD_NAME == null) {

                Map.Entry<String, RpaMnemonic> firstEntry = mnemonicFields.getValues().entrySet().iterator().next();

                primaryKeyFieldName = firstEntry.getKey();
                primaryKeyField     = firstEntry.getValue();

            } else {

                primaryKeyFieldName = PRIMARY_KEY_FIELD_NAME;
                primaryKeyField     = mnemonicFields.getValue(PRIMARY_KEY_FIELD_NAME);

            }

            String structFieldId = primaryKeyFieldName + "_" + primaryKeyField;

            if (graphVizPortraylTable.isEmpty()) {

                graphVizPortraylTable += tableName + " [label = \" <0> " + this.name.toUpperCase();
                graphVizPortraylTable += " | <" + structFieldId + "> " + primaryKeyFieldName + " (" + primaryKeyField + ")";

            } else {

                graphVizPortraylTable += " | <" + structFieldId + "> " + primaryKeyFieldName + " (" + primaryKeyField + ")";

            }

            /*
            for (Map.Entry<String, RpaMnemonic> subValueEntry : mnemonicFields.getValues().entrySet()) {

                String      subValueFieldName   = subValueEntry.getKey();
                RpaMnemonic subValue            = subValueEntry.getValue();
                String      subValueString      = "";

                if (subValue.getValue() != null) {

                    subValueString = subValue.getValue().replaceAll("[\t\n]*", "");

                }

                if (graphVizPortraylTable.isEmpty()) {

                    graphVizPortraylTable += tableName + " [label = \"" + this.name.toUpperCase();
                    graphVizPortraylTable += " | <" + subValueFieldName + "> " + subValueFieldName + " (" + subValueString + ")";

                } else {

                    graphVizPortraylTable += " | <" + subValueFieldName + "> " + subValueFieldName + " (" + subValueString + ")";

                }

            }
            */

            // Richiamo le sotto-entità
            for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                String                  subEntityFieldName      = subMnemonicEntityDataEntry.getKey();
                RpaMnemonicEntityData   subMnemonicEntityData   = subMnemonicEntityDataEntry.getValue();
                String                  subEntityIdPrefix       = tableName + "_" + String.valueOf(subEntityIndex);

                graphVizPortraylBinds       += tableName + ":" + structFieldId + " -> " + subEntityIdPrefix + "_" + subMnemonicEntityData.name + ":0\n";
                graphVizPortraylSubEntity   += subMnemonicEntityData.printGraphviz(isShowEdgesLabel, subEntityIdPrefix);

            }

            ++ subEntityIndex;

        }

        graphVizPortraylTable   += "\"]\n";
        graphVizPortrayl        += graphVizPortraylTable;
        graphVizPortrayl        += graphVizPortraylSubEntity;
        graphVizPortrayl        += graphVizPortraylBinds;

        return graphVizPortrayl;

    }

    @Override
    public String toString() {

        String graphVizPortrayl = "";

        graphVizPortrayl += "digraph MnemonicGraph {\n";
        graphVizPortrayl += "node [shape=record];\n";
        graphVizPortrayl += "rankdir=LR;\n";
        graphVizPortrayl += printGraphviz(false, "");
        graphVizPortrayl += "}";

        return graphVizPortrayl;

    }

    /*
    private boolean delete(RpaMnemonicEntityData mnemonicEntityData, String nameToFound) {

        // Caso base
        if (mnemonicEntityData.getName().equals(nameToFound)) {

            return true;

        }

        // Caso ricorsivo
        else {

            // Cerco ricorsivamente su tutti i figli da eliminare
            for (RpaMnemonicFields mnemonicFields : mnemonicEntityData.getMnemonicFieldsList()) {

                boolean isFoundAtLeastChildToRemove = false;
                for (Map.Entry<String, RpaMnemonicEntityData> subMnemonicEntityDataEntry : mnemonicFields.getSubMnemonicEntityMap().entrySet()) {

                    if (subMnemonicEntityDataEntry.getKey().equals(nameToFound)) {

                        isFoundAtLeastChildToRemove = true;

                    }

                }

                if (isFoundAtLeastChildToRemove) {

                    mnemonicFields.getSubMnemonicEntityMap().remove(n);

                }

            }

        }

    }
    */

}
