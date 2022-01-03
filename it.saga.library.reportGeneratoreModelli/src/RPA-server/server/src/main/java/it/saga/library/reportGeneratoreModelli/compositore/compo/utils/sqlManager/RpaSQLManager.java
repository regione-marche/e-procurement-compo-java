package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.sqlManager;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaCustomSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe per gestire LE SOLE query definite in un modello
 */
public class RpaSQLManager {

    private RpaMainCompositore mainCompositore;

    private RpaSQLResult                sqlResult;
    private Map<String, RpaSQLResult>   sqlResultMap;

    public RpaSQLManager(RpaMainCompositore mainCompositore) {

        this.mainCompositore    = mainCompositore;
        this.sqlResultMap       = new HashMap<String, RpaSQLResult>();

    }

    public int executeQuery(String sqlQuery) throws SQLException {

        sqlResult = new RpaSQLResult(mainCompositore, sqlQuery);

        return sqlResult.getCount();

    }

    public int executeQuery(String sqlQuery, String resultName) throws SQLException {

        RpaSQLResult newSqlResult = new RpaSQLResult(mainCompositore, sqlQuery);
        sqlResultMap.put(resultName, newSqlResult);

        return newSqlResult.getCount();

    }

    public String getValue(int rowIndex, int columnIndex) {

        // Decremento di 1 gli indici
        rowIndex    -= 1;
        columnIndex -= 1;

        return sqlResult.getValue(rowIndex, columnIndex);

    }

    public String getValue(int rowIndex, int columnIndex, String resultName) {

        // Decremento di 1 gli indici
        rowIndex    -= 1;
        columnIndex -= 1;

        RpaSQLResult sqlResultFound = sqlResultMap.get(resultName);

        if (sqlResultFound != null) {

            return sqlResultFound.getValue(rowIndex, columnIndex);

        } else {

            String  code    = "X_EXECSQL_" + columnIndex + resultName;
            String  message = "Il nome " + resultName + " non è stato stato trovato";
            int     context = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

    public Integer getValueType(int columnIndex) {

        // Decremento di 1 l'indice
        columnIndex -= 1;

        return sqlResult.getValueType(columnIndex);

    }

    public Integer getValueType(int columnIndex, String resultName) {

        // Decremento di 1 l'indice
        columnIndex -= 1;

        RpaSQLResult sqlResultFound = sqlResultMap.get(resultName);

        if (sqlResultFound != null) {

            return sqlResultFound.getValueType(columnIndex);

        } else {

            String  code    = "X_EXECSQL_" + columnIndex + resultName;
            String  message = "Il nome " + resultName + " non è stato stato trovato";
            int     context = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

    public int getCount() {

        return sqlResult.getCount();

    }

    public int getCount(String resultName) {

        RpaSQLResult sqlResultFound = sqlResultMap.get(resultName);

        if (sqlResultFound != null) {

            return sqlResultFound.getCount();

        } else {

            String  code    = "X_EXECSQL";
            String  message = "Il nome " + resultName + " non è stato stato trovato (per il conteggio dei risultati)";
            int     context = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

    public int getColumnsCount() {

        return sqlResult.getColumnsCount();

    }

    public int getColumnsCount(String resultName) {

        RpaSQLResult sqlResultFound = sqlResultMap.get(resultName);

        if (sqlResultFound != null) {

            return sqlResultFound.getColumnsCount();

        } else {

            String  code    = "X_EXECSQL";
            String  message = "Il nome " + resultName + " non è stato stato trovato (per il conteggio delle colonne)";
            int     context = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

    public int executeUpdateQuery(String updateQuery) throws SQLException {

        Connection          connection;
        PreparedStatement   preparedStatement;

        // Eseguo la query di insert / update
        connection          = mainCompositore.getComposerConfiguration().getConn();
        preparedStatement   = connection.prepareStatement(updateQuery);

        return preparedStatement.executeUpdate();

    }

}
