package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rappresenta una entità su cui è stata fatta una join diretta (#mnemonic_a(mnemonic_b)#)
 * Questa entità è scollegata dalle altre
 */
public class RpaDirectJoinEntityContext extends RpaSubScopeMnemonicContext {

    private String                  tableName;
    private String                  domain;
    private String                  sqlCondition;
    /*
    private RpaMnemonicGraph        mnemonicGraph;
    private RpaPathTreeNode         pathTreeNode;
    private RpaMnemonicEntityData   mnemonicEntityData;
    */
    private String                  lastMnemonicRead;
    private String                  lastTableNameWithDomainRead;

    private int temporaryId;

    public RpaDirectJoinEntityContext(RpaMainCompositore mainCompositore, String tableName, String domain, String sqlCondition, Map<String, RpaMnemonic> values, Map<String, Integer> valuesType) {

        // Inizializzo i campi
        this.tableName          = tableName;
        this.domain             = domain;
        this.sqlCondition       = sqlCondition;

        // Creo il grafo delle entità
        // Connection dbConnection = ComposerConfiguration.getInstance().getConn();
        Connection dbConnection = mainCompositore.getComposerConfiguration().getConn();
        // this.mnemonicGraph      = new RpaMnemonicGraph(tableName + "." + domain, sqlCondition, dbConnection);
        super.setMnemonicGraph(new RpaMnemonicGraph(tableName + "." + domain, sqlCondition, dbConnection));

        // Creo il "PathTree", ossia l'albero delle entità visitate
        // this.pathTreeNode = new RpaPathTreeNode(tableName, domain);
        super.setPathTreeNode(new RpaPathTreeNode(tableName, domain));

        // Creo il "MnemonicEntityData" per la ricerca dei dati dei mnemonici
        // this.mnemonicEntityData                     = new RpaMnemonicEntityData(mainCompositore, tableName, domain, mnemonicGraph, pathTreeNode);
        super.setMnemonicEntityData(new RpaMnemonicEntityData(mainCompositore, tableName, domain, super.getMnemonicGraph(), super.getPathTreeNode()));
        // RpaMnemonicFields newMnemonicFields         = new RpaMnemonicFields(mnemonicEntityData, values, valuesType);
        RpaMnemonicFields newMnemonicFields         = new RpaMnemonicFields(super.getMnemonicEntityData(), values, valuesType);
        List<RpaMnemonicFields> mnemonicFieldsList  = new ArrayList<RpaMnemonicFields>();
        mnemonicFieldsList.add(newMnemonicFields);
        // this.mnemonicEntityData.setMnemonicFieldsList(mnemonicFieldsList);
        this.getMnemonicEntityData().setMnemonicFieldsList(mnemonicFieldsList);

    }

    private RpaDirectJoinEntityContext() {


    }

    public boolean hasEntityRoot(String tableNameWithDomain) {

        // return mnemonicGraph.getRootNode().getEntityLabel().equals(tableNameWithDomain);
        return getMnemonicGraph().getRootNode().getEntityLabel().equals(tableNameWithDomain);

    }

    public boolean hasEntityConnection(String tableNameWithDomain) {

        // boolean isEntityPresentInGraph = mnemonicGraph.isEntityPresent(tableNameWithDomain);
        boolean isEntityPresentInGraph = getMnemonicGraph().isEntityPresent(tableNameWithDomain);
        if (isEntityPresentInGraph) {

            return true;

        } else {

            // RpaMnemonicNode newNode = new RpaMnemonicNode(mnemonicGraph, tableNameWithDomain);
            RpaMnemonicNode newNode = new RpaMnemonicNode(getMnemonicGraph(), tableNameWithDomain);
            // return mnemonicGraph.addNode(newNode);
            return getMnemonicGraph().addNode(newNode);

        }

    }

    public boolean hasDirectEntityConnectionOld(String tableNameWithDomain) {

        // boolean isEntityPresentInGraph = mnemonicGraph.isEntityPresent(tableNameWithDomain);
        boolean isEntityPresentInGraph = getMnemonicGraph().isEntityPresent(tableNameWithDomain);
        if (isEntityPresentInGraph) {

            return true;

        } else {



            // TODO: QUI HO IL PROBLEMA!!! Se non ce nel grafo, non lo aggiungo (ma allora quando lo aggiungo?)
            // TODO: Forse va fatto un altro controllo di presenza (ciclo negli elementi del grafo e verifico se riesco
            // TODO: a collegare il nuovo nodo ai SOLO esistenti)
            // RpaMnemonicNode newNode = new RpaMnemonicNode(mnemonicGraph, tableNameWithDomain);
            RpaMnemonicNode newNode = new RpaMnemonicNode(getMnemonicGraph(), tableNameWithDomain);
            // return mnemonicGraph.addDirectNode(newNode);
            return getMnemonicGraph().addDirectNode(newNode);

            // return false;

        }

    }

    public boolean isMatchQueryParameters(String tableName, String domain, String sqlCondition) {

        return this.tableName.equals(tableName) && this.domain.equals(domain) && this.sqlCondition.equals(sqlCondition);

    }

    public void setLastMnemonicRead(String lastMnemonicRead) {

        this.lastMnemonicRead = lastMnemonicRead;

    }

    public void setLastTableNameWithDomainRead(String lastTableNameWithDomainRead) {

        this.lastTableNameWithDomainRead = lastTableNameWithDomainRead;

    }

    public void setTemporaryId(int temporaryId) {

        this.temporaryId = temporaryId;

    }

    // public RpaMnemonicEntityData getMnemonicEntityData() { return mnemonicEntityData; }

    // public RpaPathTreeNode getPathTreeNode() { return pathTreeNode; }

    public String getLastMnemonicRead() { return lastMnemonicRead; }

    public String getLastTableNameWithDomainRead() { return lastTableNameWithDomainRead; }

    public int getTemporaryId() { return temporaryId; }

    @Override
    public RpaDirectJoinEntityContext clone() {

        RpaDirectJoinEntityContext clone = new RpaDirectJoinEntityContext();

        clone.tableName                     = this.tableName;
        clone.domain                        = this.domain;
        clone.sqlCondition                  = this.sqlCondition;
        /*
        clone.mnemonicGraph                 = this.mnemonicGraph.clone();
        clone.pathTreeNode                  = this.pathTreeNode.clone();
        clone.mnemonicEntityData            = this.mnemonicEntityData.clone(clone.mnemonicGraph, clone.pathTreeNode);
        */
        clone.lastMnemonicRead              = this.lastMnemonicRead;
        clone.lastTableNameWithDomainRead   = this.lastTableNameWithDomainRead;
        clone.temporaryId                   = this.temporaryId;

        clone.setMnemonicGraph(this.getMnemonicGraph().clone());
        clone.setPathTreeNode(this.getPathTreeNode().clone());
        clone.setMnemonicEntityData(this.getMnemonicEntityData().clone(clone.getMnemonicGraph(), clone.getPathTreeNode()));

        return clone;

    }

}
