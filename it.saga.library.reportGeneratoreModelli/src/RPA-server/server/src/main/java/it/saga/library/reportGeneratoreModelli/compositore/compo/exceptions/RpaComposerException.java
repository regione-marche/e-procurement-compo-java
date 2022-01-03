package it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions;

import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaANTLRErrorListener;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public abstract class RpaComposerException extends ParseCancellationException {

    private static final String SQL_INSERT_POSTGRESQL = "" +
            "INSERT INTO @ (pkid, type, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id_session, code, context, text, error_stack_trace) " +
            "VALUES (NEXTVAL('seq_rpa_log'), 'e', ?, ?, ?, ?, 0, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_ORACLE = "" +
            "INSERT INTO @ (pkid, type, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id_session, code, context, text, error_stack_trace) " +
            "VALUES (seq_rpa_log.NEXTVAL, 'e', ?, ?, ?, ?, 0, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_SQLSERVER = "" +
            "INSERT INTO @ (type, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id_session, code, context, text, error_stack_trace) " +
            "VALUES ('e', ?, ?, ?, ?, 0, ?, ?, ?, ?, ?)";

    public static final int PRECOMPILE_MESSAGE  = 0;
    public static final int COMPILE_MESSAGE     = 1;

    public static final int ERROR_STACK_TRACE_MAX_LENGTH = 2000;

    private RpaANTLRErrorListener       antlrErrorListener;
    private RpaComposerConfiguration    composerConfiguration;
    private String                      code;
    private int                         context;
    private String                      errorStackTrace;

    public RpaComposerException(RpaComposerConfiguration composerConfiguration, RpaANTLRErrorListener antlrErrorListener, int context, String code, String message) {

        super(message);

        this.composerConfiguration  = composerConfiguration;
        this.antlrErrorListener     = antlrErrorListener;
        this.context                = context;
        this.code                   = code;

        if (composerConfiguration.isErrorMessageActive() && !composerConfiguration.isAvoidUseNewTables()) {

            saveOnDB();

        }

    }

    public RpaComposerException(RpaComposerConfiguration composerConfiguration, int context, String code, String message, Exception exception) {

        super(message);
        setErrorStackTrace(exception);

        this.composerConfiguration  = composerConfiguration;
        this.context                = context;
        this.code                   = code;

        if (composerConfiguration.isErrorMessageActive() && !composerConfiguration.isAvoidUseNewTables()) {

            saveOnDB();

        }

    }

    private void saveOnDB() {

        // Estraggo la connessione al DB e il nome della tabella di log
        Connection  connection  = composerConfiguration.getConn();
        String      logTable    = composerConfiguration.getDebugTableName();

        // Creo la insert-SQL
        Calendar calendar    = Calendar.getInstance();
        Timestamp timestamp   = new Timestamp(calendar.getTimeInMillis());

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

        // Filtro la lunghezza massima dell' errorStackTrace
        if (errorStackTrace == null) {

            errorStackTrace = "";

        }

        int maxErrorStackTraceLength = Math.min(errorStackTrace.length(), ERROR_STACK_TRACE_MAX_LENGTH);

        // La eseguo
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setLong(2, userId);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.setLong(4, userId);
            preparedStatement.setInt(5, sessionId);
            preparedStatement.setString(6, code);
            preparedStatement.setInt(7, context);
            preparedStatement.setString(8, super.getMessage());
            preparedStatement.setString(9, errorStackTrace.substring(0, maxErrorStackTraceLength));

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

        }

    }

    public void setErrorStackTrace(Exception exception) {

        StringWriter    writer      = new StringWriter();
        PrintWriter     printWriter = new PrintWriter(writer);
        exception.printStackTrace(printWriter);

        this.errorStackTrace = writer.toString();
        this.errorStackTrace = this.errorStackTrace
                .replaceAll("[\r\t]", "")
                .replaceAll("\n", "[*]");

    }

    public String getCode() { return code; }

    public String getErrorStackTrace() { return errorStackTrace; }

    public void setErrorStackTrace(String errorStackTrace) { this.errorStackTrace = errorStackTrace; }

    public abstract RpaErrorType getType();

    @Override
    public String toString() {

        String errorMessage = "Codice: '" + code + "' | errore: '" + getType() + "' | messaggio: '" + getMessage() + "'";

        if (!antlrErrorListener.getSyntaxErrorList().isEmpty()) {

            for (String syntaxError : antlrErrorListener.getSyntaxErrorList()) {

                errorMessage += "\n\n" + syntaxError;

            }

            errorMessage += "\n";

        }

        return errorMessage;

    }

}
