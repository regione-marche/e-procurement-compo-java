package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RpaDebugMessages {

    private static final String SQL_INSERT_POSTGRESQL = "" +
            "INSERT INTO @ (pkid, type, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id_session, context, text) " +
            "VALUES (NEXTVAL('seq_rpa_log'), 'd', ?, ?, ?, ?, 0, ?, ?, ?)";

    private static final String SQL_INSERT_ORACLE = "" +
            "INSERT INTO @ (pkid, type, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id_session, context, text) " +
            "VALUES (seq_rpa_log.NEXTVAL, 'd', ?, ?, ?, ?, 0, ?, ?, ?)";

    private static final String SQL_INSERT_SQLSERVER = "" +
            "INSERT INTO @ (type, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id_session, context, text) " +
            "VALUES ('d', ?, ?, ?, ?, 0, ?, ?, ?)";

    public static final int PRECOMPILE_MESSAGE  = 0;
    public static final int COMPILE_MESSAGE     = 1;

    private static final int MAX_TEXT_LENGTH_POSTGRESQL = 300;
    private static final int MAX_TEXT_LENGTH_ORACLE     = 300;
    private static final int MAX_TEXT_LENGTH_SQLSERVER  = 300;

    private RpaComposerConfiguration    composerConfiguration;
    private Map<Integer, Integer>       mapMaxTextLength;

    public RpaDebugMessages(RpaComposerConfiguration composerConfiguration) {

        this.composerConfiguration  = composerConfiguration;
        this.mapMaxTextLength       = new HashMap<Integer, Integer>();

        mapMaxTextLength.put(RpaComposerConfiguration.TYPE_POSTGRESQL, MAX_TEXT_LENGTH_POSTGRESQL);
        mapMaxTextLength.put(RpaComposerConfiguration.TYPE_ORACLE, MAX_TEXT_LENGTH_ORACLE);
        mapMaxTextLength.put(RpaComposerConfiguration.TYPE_MSQL, MAX_TEXT_LENGTH_SQLSERVER);

    }

    public void precompilePrint(String message) {

        print(PRECOMPILE_MESSAGE, message);

    }

    public void print(String message) {

        print(COMPILE_MESSAGE, message);

    }

    public void priorityPrint(int context, String message) {

        System.out.println(message);

        if (!composerConfiguration.isAvoidUseNewTables()) {

            saveOnDB(context, message);

        }

    }

    public void print(int context, String message) {

        if (composerConfiguration.isSystemPrintActive()) {

            System.out.println(message);

        }

        if (composerConfiguration.isDebugMessageActive() && !composerConfiguration.isAvoidUseNewTables()) {

            saveOnDB(context, message);

        }

    }

    private void saveOnDB(int context, String message) {

        // Estraggo la connessione al DB e il nome della tabella di log
        Connection  connection  = composerConfiguration.getConn();
        String      logTable    = composerConfiguration.getDebugTableName();

        // Creo la insert-SQL
        Calendar    calendar    = Calendar.getInstance();
        Timestamp   timestamp   = new Timestamp(calendar.getTimeInMillis());

        String sqlInsert = "";

        switch (composerConfiguration.getDBType()) {

            case RpaComposerConfiguration.TYPE_POSTGRESQL:
                sqlInsert = SQL_INSERT_POSTGRESQL;
                break;
            case RpaComposerConfiguration.TYPE_ORACLE:
                sqlInsert = SQL_INSERT_ORACLE;
                break;
            case RpaComposerConfiguration.TYPE_MSQL:
                sqlInsert = SQL_INSERT_SQLSERVER;
                break;

        }

        sqlInsert = sqlInsert.replaceAll("@", logTable);

        Long    userId     = composerConfiguration.getUserId();
        Integer sessionId  = composerConfiguration.getSessionId();

        if (userId == null) {

            userId = Long.valueOf(-1);

        }

        if (sessionId == null) {

            sessionId = -1;

        }

        int maxTextLength = mapMaxTextLength.get(composerConfiguration.getDBType());

        if (message != null && message.length() > maxTextLength) {

            message = message.substring(0, maxTextLength);

        }

        // La eseguo
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setLong(2, userId);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.setLong(4, userId);
            preparedStatement.setInt(5, sessionId);
            preparedStatement.setInt(6, context);
            preparedStatement.setString(7, message);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        }

    }

}
