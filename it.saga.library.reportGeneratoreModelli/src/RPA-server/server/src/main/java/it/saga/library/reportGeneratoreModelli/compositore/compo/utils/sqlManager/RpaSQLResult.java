package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.sqlManager;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RpaSQLResult {

    private RpaMainCompositore mainCompositore;

    private String              sqlQuery;
    private List<List<String>>  values;
    private List<Integer>       valuesType;
    private List<String>        valuesNameConverter;
    private int                 columnsCount;

    public RpaSQLResult(RpaMainCompositore mainCompositore, String sqlQuery) throws SQLException {

        this.mainCompositore        = mainCompositore;
        this.sqlQuery               = sqlQuery;
        this.values                 = new ArrayList<List<String>>();
        this.valuesType             = new ArrayList<Integer>();
        this.valuesNameConverter    = new ArrayList<String>();

        performQuery();

    }

    private void performQuery() throws SQLException {

        Connection          connection;
        PreparedStatement   preparedStatement;
        ResultSet           resultSet;
        ResultSetMetaData   resultSetMetaData;

        connection = mainCompositore.getComposerConfiguration().getConn();

        // try {

        // Eseguo la query
        preparedStatement   = connection.prepareStatement(sqlQuery);
        resultSet           = preparedStatement.executeQuery();
        resultSetMetaData   = preparedStatement.getMetaData();

        for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

            // Salvo il nome delle colonne
            String columnName = resultSetMetaData.getColumnName(columnIndex);
            valuesNameConverter.add(columnName);

            // Salvo il tipo di ogni colonna
            Integer columnType  = resultSetMetaData.getColumnType(columnIndex);
            valuesType.add(columnType);

        }

        // Salvo il numero di colonne della query
        columnsCount = resultSetMetaData.getColumnCount();

        // Salvo i risultati
        while (resultSet.next()) {

            List<String> newResult = new ArrayList<String>();

            for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                String  columnValue = resultSet.getString(columnIndex);
                newResult.add(columnValue);

            }

            values.add(newResult);

        }

        resultSet.close();
        preparedStatement.close();

    }

    public String getValue(int rowIndex, int columnIndex) {

        return values.get(rowIndex).get(columnIndex);

    }

    public Integer getValueType(int columnIndex) {

        return valuesType.get(columnIndex);

    }

    public int getCount() {

        return values.size();

    }

    public int getColumnsCount() {

        return columnsCount;

    }

}
