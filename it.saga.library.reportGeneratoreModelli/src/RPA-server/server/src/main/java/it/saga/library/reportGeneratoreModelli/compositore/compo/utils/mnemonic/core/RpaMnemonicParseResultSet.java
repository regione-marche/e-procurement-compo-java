package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe che converte i resultSet per la gestione dei mnemonici in oggetti
 * (Conversione necessaria per gestire le connessione su Oracle Database)
 */
public class RpaMnemonicParseResultSet {

    public static ArrayList<NodeParentEntity> parseNodeParentEntities(ResultSet resultSet)
            throws SQLException {

        ArrayList<NodeParentEntity> nodeParentEntities = new ArrayList<NodeParentEntity>();

        while (resultSet.next()) {

            NodeParentEntity nodeParentEntity = new RpaMnemonicParseResultSet().new NodeParentEntity(resultSet);
            nodeParentEntities.add(nodeParentEntity);

        }

        return nodeParentEntities;

    }

    public class NodeParentEntity {

        private String parentTableFieldName;
        private String parentTableName;

        public NodeParentEntity(ResultSet resultSet) throws SQLException {

            this.parentTableFieldName   = resultSet.getString("parent_table_field_name");
            this.parentTableName        = resultSet.getString("parent_table_name");

        }

        public String getParentTableFieldName() { return parentTableFieldName; }

        public String getParentTableName() { return parentTableName; }
    }

}
