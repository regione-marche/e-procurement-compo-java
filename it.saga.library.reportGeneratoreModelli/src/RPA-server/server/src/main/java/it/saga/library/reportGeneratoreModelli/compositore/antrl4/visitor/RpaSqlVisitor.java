package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaWarningType;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaCustomSQLException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDataConversionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatExecSql;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatResultSql;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format.RpaFormatXExecAgg;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.sqlManager.RpaSQLManager;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainExecuteSQLC1Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainExecuteSQLContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXExecAggContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXSQLCC1Context;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXSQLCCTContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.FormatDomainXSQLCContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaSqlVisitor extends RpaBasicVisitor {

    // Link: https://regex101.com/r/aFuGDV/1/
    private static final String TABULATION_SQL_REGEX = "^_n([A-Za-z0-9]+)_t([A-Za-z0-9]+) *\\}$";

    private static final boolean IS_DISABLE_SQL_INSERT_UPDATE = true;

    public RpaSqlVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

        super(conn, scope, mainCompositore, parentNode, childNode);

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainExecuteSQL(FormatDomainExecuteSQLContext context) {

        RpaSQLManager sqlManager                            = mainCompositore.getSQLManager();
        RpaDataConversionContainer dataConversionContainer  = mainCompositore.getDataConversionContainer();

        // Estraggo la query
        RpaAbstractMnemonic mnemonic = mainCompositore.getDataConversionContainer().getMnemonic();

        String sqlQuery = mnemonic.getValue();

        if (sqlQuery == null || sqlQuery.isEmpty()) {

            String  code        = parentNode.getText();
            String  message     = "La query è vuota o nulla";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        debugMessages.print("[EXECUTE SQL] " + sqlQuery);

        // Nel caso in cui la query sia corretta
        try {

            // Eseguo la query e salvo i risultati
            int countResults = sqlManager.executeQuery(sqlQuery);
            String countResultsString = String.valueOf(countResults);

            // Creo una formattazione per il mnemonico
            RpaFormat customValueFormat = new RpaFormatExecSql(mainCompositore, countResultsString);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        // Nel caso in cui la query fosse sbagliata
        catch (SQLException exception) {

            // Notifico l'errore
            mainCompositore.getWarningMessages().print(
                    RpaWarningType.WRONG_SQL_QUERY,"[WRONG SQL QUERY] " + exception.getMessage()
            );

            // Creo una formattazione per il mnemonico con zero risultati
            RpaFormat customValueFormat = new RpaFormatExecSql(mainCompositore, String.valueOf(0));

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainExecuteSQLC1(FormatDomainExecuteSQLC1Context context) {

        RpaSQLManager sqlManager              = mainCompositore.getSQLManager();
        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();

        // Estraggo il nome del risultato
        String resultName   = context.DOMXEXECSQLN_ID().getText();
        resultName          = resultName.replaceAll("X_EXECSQL_n", "");
        resultName          = resultName.replaceAll("[ \\}]+", "");

        // Estraggo la query
        RpaAbstractMnemonic mnemonic = mainCompositore.getDataConversionContainer().getMnemonic();

        String sqlQuery = mnemonic.getValue();

        if (sqlQuery == null || sqlQuery.isEmpty()) {

            String  code        = parentNode.getText();
            String  message     = "La query è vuota o nulla";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        debugMessages.print("[EXECUTE SQL] " + sqlQuery);

        // Nel caso in cui la query sia corretta
        try {

            // Eseguo la query e salvo i risultati
            int countResults = sqlManager.executeQuery(sqlQuery, resultName);
            String countResultsString = String.valueOf(countResults);

            // Creo una formattazione per il mnemonico
            RpaFormat customValueFormat = new RpaFormatExecSql(mainCompositore, countResultsString);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        // Nel caso in cui la query fosse sbagliata
        catch (SQLException exception) {

            // Notifico l'errore
            mainCompositore.getWarningMessages().print(
                    RpaWarningType.WRONG_SQL_QUERY,"[WRONG SQL QUERY] " + exception.getMessage()
            );

            // Creo una formattazione per il mnemonico con zero risultati
            RpaFormat customValueFormat = new RpaFormatExecSql(mainCompositore, String.valueOf(0));

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainXSQLC(FormatDomainXSQLCContext context) {

        RpaSQLManager               sqlManager              = mainCompositore.getSQLManager();
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();

        // Estraggo l'indice della colonna
        int columnIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Estraggo l'indice di riga
        String rawRowIndex = dataConversionContainer.getValue();

        boolean isRawRowIndexEmpty  = rawRowIndex == null || rawRowIndex.isEmpty();
        boolean isRawRowIndexNumber = RpaNumberUtils.isInteger(rawRowIndex) && RpaNumberUtils.isDouble(rawRowIndex);

        if (isRawRowIndexEmpty || !isRawRowIndexNumber) {

            String  code        = parentNode.getText();
            String  message     = "Il valore del mnemonico non è un numero per l'indice riga";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Converto l'indice di riga
        int rowIndex;

        if (RpaNumberUtils.isInteger(rawRowIndex)) {

            if (RpaNumberUtils.isIntegerWithDotZero(rawRowIndex)) {

                rawRowIndex = RpaNumberUtils.integerWithoutDotZero(rawRowIndex);

            }

            rowIndex = Integer.valueOf(rawRowIndex);

        } else {

            double doubleRowIndex   = Double.valueOf(rawRowIndex);
            rowIndex                = (int) doubleRowIndex;

        }

        // Controllo se l'indice della colonna è valida
        int columnsCount = sqlManager.getColumnsCount();

        if (columnIndex - 1 < 0 || columnIndex - 1 >= columnsCount) {

            String  code        = "X_EXECSQL";
            String  message     = "L'indice di colonna " + columnIndex + " non è valida (il numero di colonne è " + columnsCount + ")";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Controllo se l'indice di riga è valido
        if (rowIndex - 1< 0 || rowIndex - 1 >= sqlManager.getCount()) {

            // Creo una formattazione vuota per il mnemonico
            RpaFormat customValueFormat = new RpaFormatResultSql(mainCompositore, null, RpaFormatResultSql.SQL_TYPE_EMPTY);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_STRING);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        // Recupero il risultato
        String  result  = sqlManager.getValue(rowIndex, columnIndex);
        int     sqlType = sqlManager.getValueType(columnIndex);

        // Creo una formattazione per il mnemonico
        RpaFormat customValueFormat = new RpaFormatResultSql(mainCompositore, result, sqlType);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainXSQLCC1(FormatDomainXSQLCC1Context context) {

        RpaSQLManager               sqlManager              = mainCompositore.getSQLManager();
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();

        // Estraggo il nome del risultato
        String resultName   = context.DOMXSQLCNNNAAA_SUFFIX_ID().getText();
        resultName          = resultName.replaceAll("_n", "");
        resultName          = resultName.replaceAll("[ \\}]+", "");

        // Estraggo l'indice della colonna
        int columnIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Estraggo l'indice di riga
        String rawRowIndex = dataConversionContainer.getValue();

        boolean isRawRowIndexEmpty  = rawRowIndex == null || rawRowIndex.isEmpty();
        boolean isRawRowIndexNumber = RpaNumberUtils.isInteger(rawRowIndex) && RpaNumberUtils.isDouble(rawRowIndex);

        if (isRawRowIndexEmpty || !isRawRowIndexNumber) {

            String  code        = parentNode.getText();
            String  message     = "Il valore del mnemonico non è un numero per l'indice riga";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Converto l'indice di riga
        int rowIndex;

        if (RpaNumberUtils.isInteger(rawRowIndex)) {

            if (RpaNumberUtils.isIntegerWithDotZero(rawRowIndex)) {

                rawRowIndex = RpaNumberUtils.integerWithoutDotZero(rawRowIndex);

            }

            rowIndex = Integer.valueOf(rawRowIndex);

        } else {

            double doubleRowIndex   = Double.valueOf(rawRowIndex);
            rowIndex                = (int) doubleRowIndex;

        }

        // Controllo se l'indice della colonna è valida
        int columnsCount = sqlManager.getColumnsCount(resultName);

        if (columnIndex - 1 < 0 || columnIndex - 1 >= columnsCount) {

            String  code        = "X_EXECSQL" + resultName;
            String  message     = "L'indice di colonna " + columnIndex + " non è valida (il numero di colonne è " + columnsCount + ")";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Controllo se l'indice di riga è valido
        if (rowIndex - 1 < 0 || rowIndex - 1 >= sqlManager.getCount(resultName)) {

            // Creo una formattazione vuota per il mnemonico
            RpaFormat customValueFormat = new RpaFormatResultSql(mainCompositore, null, RpaFormatResultSql.SQL_TYPE_EMPTY);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_STRING);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        // Recupero il risultato
        String  result  = sqlManager.getValue(rowIndex, columnIndex, resultName);
        int     sqlType = sqlManager.getValueType(columnIndex, resultName);

        // Creo una formattazione per il mnemonico
        RpaFormat customValueFormat = new RpaFormatResultSql(mainCompositore, result, sqlType);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainXSQLCCT(FormatDomainXSQLCCTContext context) {

        // TODO: Da testare su un modello indicato da Flavio

        RpaSQLManager sqlManager                            = mainCompositore.getSQLManager();
        RpaDataConversionContainer dataConversionContainer  = mainCompositore.getDataConversionContainer();
        RpaMnemonicManager mnemonicManager                  = mainCompositore.getMnemonicManager();

        // Identifico il dominio
        Matcher matcherSqlDomain = Pattern.compile(TABULATION_SQL_REGEX).matcher(context.DOMXSQLCNNNAAAT_SUFFIX_ID().getText());
        matcherSqlDomain.find();

        // Estraggo il nome del risultato
        String resultName = matcherSqlDomain.group(1);

        // Estraggo il codice del tabulato
        String tabulationCode = matcherSqlDomain.group(2);

        // Estraggo l'indice della colonna
        int columnIndex = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Estraggo l'indice di riga
        String rawRowIndex = dataConversionContainer.getValue();

        boolean isRawRowIndexEmpty  = rawRowIndex == null || rawRowIndex.isEmpty();
        boolean isRawRowIndexNumber = RpaNumberUtils.isInteger(rawRowIndex) && RpaNumberUtils.isDouble(rawRowIndex);

        if (isRawRowIndexEmpty || !isRawRowIndexNumber) {

            String  code        = parentNode.getText();
            String  message     = "Il valore del mnemonico non è un numero per l'indice riga";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Converto l'indice di riga
        int rowIndex;

        if (RpaNumberUtils.isInteger(rawRowIndex)) {

            if (RpaNumberUtils.isIntegerWithDotZero(rawRowIndex)) {

                rawRowIndex = RpaNumberUtils.integerWithoutDotZero(rawRowIndex);

            }

            rowIndex = Integer.valueOf(rawRowIndex);

        } else {

            double doubleRowIndex   = Double.valueOf(rawRowIndex);
            rowIndex                = (int) doubleRowIndex;

        }

        // Controllo se l'indice della colonna è valida
        int columnsCount = sqlManager.getColumnsCount(resultName);

        if (columnIndex - 1 < 0 || columnIndex - 1 >= columnsCount) {

            String  code        = "nX_SQLCCT" + resultName;
            String  message     = "L'indice di colonna " + columnIndex + " non è valida (il numero di colonne è " + columnsCount + ")";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Controllo se l'indice di riga è valido
        if (rowIndex - 1< 0 || rowIndex - 1 >= sqlManager.getCount(resultName)) {

            // Creo una formattazione vuota per il mnemonico
            RpaFormat customValueFormat = new RpaFormatResultSql(mainCompositore, null, RpaFormatResultSql.SQL_TYPE_EMPTY);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_STRING);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        // Recupero il risultato
        String tabulationIndex = sqlManager.getValue(rowIndex, columnIndex, resultName);

        // Controllo che il risultato sia un intero
        if (!RpaNumberUtils.isInteger(tabulationIndex)) {

            String  code        = parentNode.getText();
            String  message     = "Il risultato non è un tabellato-intero";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        // Converto il tabellato
        String result = mnemonicManager.parseTabulationValueFromCode(tabulationCode, tabulationIndex);

        // Creo una formattazione per il mnemonico
        RpaFormat customValueFormat = new RpaFormatResultSql(mainCompositore, result, Types.VARCHAR);

        dataConversionContainer.setCustomValueFormat(customValueFormat);
        dataConversionContainer.setType(RpaFormat.TYPE_STRING);

        return new RpaValue<RpaFormat>(customValueFormat);

    }

    @Override
    public RpaValue<RpaFormat> visitFormatDomainXExecAgg(FormatDomainXExecAggContext context) {

        RpaSQLManager sqlManager                            = mainCompositore.getSQLManager();
        RpaDataConversionContainer dataConversionContainer  = mainCompositore.getDataConversionContainer();

        // Estraggo la query
        RpaAbstractMnemonic mnemonic = mainCompositore.getDataConversionContainer().getMnemonic();

        String sqlQuery = mnemonic.getValue();

        if (IS_DISABLE_SQL_INSERT_UPDATE) {

            mainCompositore.getWarningMessages().print(RpaWarningType.INSERT_UPDATE_SQL_QUERY, sqlQuery);

            // Creo una formattazione per il mnemonico con zero risultati
            RpaFormat customValueFormat = new RpaFormatXExecAgg(mainCompositore, 0);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        if (sqlQuery == null || sqlQuery.isEmpty()) {

            String  code        = parentNode.getText();
            String  message     = "La query è vuota o nulla";
            int     contextId   = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaCustomSQLException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), contextId, code, message);

        }

        debugMessages.print("[EXECUTE SQL] " + sqlQuery);

        // Nel caso in cui la query sia corretta
        try {

            // Eseguo la query di aggiornamento
            int countResults = sqlManager.executeUpdateQuery(sqlQuery);
            String countResultsString = String.valueOf(countResults);

            // Creo una formattazione per il mnemonico
            RpaFormat customValueFormat = new RpaFormatXExecAgg(mainCompositore, countResults);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

        // Nel caso in cui la query fosse sbagliata
        catch (SQLException exception) {

            // Notifico l'errore
            mainCompositore.getWarningMessages().print(
                    RpaWarningType.WRONG_SQL_QUERY,"[WRONG SQL QUERY] " + exception.getMessage()
            );

            // Creo una formattazione per il mnemonico con zero risultati
            RpaFormat customValueFormat = new RpaFormatXExecAgg(mainCompositore, 0);

            dataConversionContainer.setCustomValueFormat(customValueFormat);
            dataConversionContainer.setType(RpaFormat.TYPE_INT);

            return new RpaValue<RpaFormat>(customValueFormat);

        }

    }
}
