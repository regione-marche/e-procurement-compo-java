package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeDocument;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeJoin;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaAdvancedVisitor;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaMnemonicVisitor;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaWarningType;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInternalException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaJoinFailException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaReachEntityException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaUpdateMnemonicEntityDataIndexException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicEntityData;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFields;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFieldsComparator;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicTypeFinder;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaPathTreeNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNode;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicNodeRoot;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe per la gestione dei mnemonici
 */
public class RpaMnemonicManager {

    // TODO: Ridefinire il valore per la sequence verificando se sono su Oracle o su Postgresql o su SQLServer
    public static final String SQL_INSERT_SESSION_ID_POSTGRESQL = "" +
            "INSERT INTO rpa_session (pkid, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id, model) " +
            "VALUES (NEXTVAL('seq_rpa_session'), ?, ?, ?, ?, 0, ?, ?)";
    public static final String SQL_INSERT_SESSION_ID_ORACLE     = "" +
            "INSERT INTO rpa_session (pkid, data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id, model) " +
            "VALUES (seq_rpa_session.NEXTVAL, ?, ?, ?, ?, 0, ?, ?)";
    public static final String SQL_INSERT_SESSION_ID_SQLSERVER  = "" +
            "INSERT INTO rpa_session (data_ins, id_utente_ins, data_upd, id_utente_upd, count_upd, id, model) " +
            "VALUES (?, ?, ?, ?, 0, ?, ?)";

    public static final String FILTER_PREFIX_FIELD_REGEX = "^[^@]{2}@";

    // Link: https://regex101.com/r/NTrapH/2
    public static final String DATETIME_MILLISECONDS_REGEX = "^([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})(\\.([0-9]+))?$";

    // Link: https://regex101.com/r/4bpceV/3
    public static final String NUMBER_FORMAT_ZERO_REGEX = "^([0-9]+[1-9])(0*)$";

    // Link: https://regex101.com/r/Iam2YQ/3
    public static final String EXTENSION_SQUARE_REGEX = "^([\\[\\]])?((.*[\\n\\r\\t]*.*)*)(([\\[\\]])|([^\\[\\]]))$";

    // Link: https://regex101.com/r/6ICkKu/1
    public static final String EXTENSION_VIEW_REGEX = "^(.*[\\n\\r]*.*)(>>&[0-9]+)$";

    private static final Map<String, String>    MNEMONIC_TABULATION_MAPPER          = new HashMap<String, String>();
    private static final Map<String, String>    MNEMONIC_TABULATION_QUERY_MAPPER    = new HashMap<String, String>();
    private static final Map<String, String>    MNEMONIC_JOIN_OPERATION_MAPPER      = new HashMap<String, String>();
    private static final List<String>           SQL_INSERT_SESSION_MAPPER           = new ArrayList<String>();

    private static final int MNEMONIC_TABULATION_CHARACTER_INDEX_POSITION = 2;

    public static final int MNEMONIC_VALUE_TYPE_STRING = 0;
    public static final int MNEMONIC_VALUE_TYPE_INT    = 1;
    public static final int MNEMONIC_VALUE_TYPE_FLOAT  = 2;

    private static final int MAX_ITERATION_SEARCH_ID = 500;

    private Integer lastMnemonicValueType = null;

    private RpaMnemonicGraph    graph;
    private Connection          dbConnection;
    private RpaMainCompositore  mainCompositore;
    private RpaDebugMessages    debugMessages;

    RpaMnemonicTypeFinder mnemonicTypeFinder;

    public RpaMnemonicManager(RpaMainCompositore mainCompositore) {

        this.mainCompositore    = mainCompositore;
        this.debugMessages      = mainCompositore.getDebugMessages();

    }

    public void init(Connection dbConnection) {

        // Inizializzo la connessione al database
        this.dbConnection   = dbConnection;

        // Recupero il nome + dominio della entità (es: "APE_CONCES" sarà "APE_CONCES.APE")
        String  rootCondition = "";

        List<String[]> mnemonicRootIdentityList = mainCompositore.getComposerConfiguration().getIdentity();

        String[] mnemonicRootIdentity       = mnemonicRootIdentityList.get(0);
        String mnemonicRootNameIncomplete   = mnemonicRootIdentity[RpaMnemonicNodeRoot.MAIN_ENTITY_IDENTITY_NAME_INDEX];
        String mnemonicRootName             = retrieveEntityWithDomain(mnemonicRootNameIncomplete);
        String mnemonicRootFieldName        = mnemonicRootIdentity[RpaMnemonicNodeRoot.MAIN_ENTITY_FIELD_NAME_INDEX];
        String mnemonicRootFieldValue       = mnemonicRootIdentity[RpaMnemonicNodeRoot.MAIN_ENTITY_FIELD_VALUE_INDEX];

        mnemonicRootFieldValue  = mnemonicRootFieldValue.replaceAll("\'", "\'\'");
        String  fieldWithTable  = mnemonicRootNameIncomplete + "." + mnemonicRootFieldName;
        int     fieldType       = getColumnType(mnemonicRootNameIncomplete, mnemonicRootFieldName);

        if (fieldType == Types.VARCHAR) {


            String newRootFieldValue    = mnemonicRootFieldValue.replaceAll("\'", "\'\'");
            rootCondition               = fieldWithTable + " = '" + newRootFieldValue + "'";

        } else {

            rootCondition = fieldWithTable + " = " + mnemonicRootFieldValue + "";

        }

        for (int i = 1; i < mnemonicRootIdentityList.size(); i ++) {

            mnemonicRootIdentity = mnemonicRootIdentityList.get(i);

            mnemonicRootFieldName   = mnemonicRootIdentity[0];
            mnemonicRootFieldValue  = mnemonicRootIdentity[1];

            fieldWithTable  = mnemonicRootNameIncomplete + "." + mnemonicRootFieldName;
            fieldType       = getColumnType(mnemonicRootNameIncomplete, mnemonicRootFieldName);

            rootCondition += " AND ";

            if (fieldType == Types.VARCHAR) {


                String newRootFieldValue    = mnemonicRootFieldValue.replaceAll("\'", "\'\'");
                rootCondition               += fieldWithTable + " = '" + newRootFieldValue + "'";

            } else {

                rootCondition += fieldWithTable + " = " + mnemonicRootFieldValue + "";

            }

        }

        /*
        String[] mnemonicRootIdentity       = mainCompositore.getComposerConfiguration().getIdentity();
        String mnemonicRootNameIncomplete   = mnemonicRootIdentity[RpaMnemonicNodeRoot.MAIN_ENTITY_IDENTITY_NAME_INDEX];
        String mnemonicRootName             = retrieveEntityWithDomain(mnemonicRootNameIncomplete);
        String mnemonicRootFieldName        = mnemonicRootIdentity[RpaMnemonicNodeRoot.MAIN_ENTITY_FIELD_NAME_INDEX];
        String mnemonicRootFieldValue       = mnemonicRootIdentity[RpaMnemonicNodeRoot.MAIN_ENTITY_FIELD_VALUE_INDEX];

        mnemonicRootFieldValue  = mnemonicRootFieldValue.replaceAll("\'", "\'\'");
        String  fieldWithTable  = mnemonicRootNameIncomplete + "." + mnemonicRootFieldName;
        int     fieldType       = getColumnType(mnemonicRootNameIncomplete, mnemonicRootFieldName);

        if (fieldType == Types.VARCHAR) {


            String newRootFieldValue    = mnemonicRootFieldValue.replaceAll("\'", "\'\'");
            rootCondition               = fieldWithTable + " = '" + newRootFieldValue + "'";

        } else {

            rootCondition = fieldWithTable + " = " + mnemonicRootFieldValue + "";

        }
        */

        // Inizializzo il sistema di caching dei mnemonici e il "Mnemonic Graph"
        this.graph = new RpaMnemonicGraph(mnemonicRootName, rootCondition);
        this.graph.setDbConnection(dbConnection);

        // Inizializzo l'oggetto utilizzato per la ricerca del tipo di un mnemonico
        this.mnemonicTypeFinder = new RpaMnemonicTypeFinder(dbConnection);

        // Inizializzo il mapper per i mnemonici con tabulati
        MNEMONIC_TABULATION_MAPPER.put("x", "tab3");
        MNEMONIC_TABULATION_MAPPER.put("y", "tab3");
        MNEMONIC_TABULATION_MAPPER.put("w", "tab0");
        MNEMONIC_TABULATION_MAPPER.put("k", "tab0");
        MNEMONIC_TABULATION_MAPPER.put("z", "tab2");
        MNEMONIC_TABULATION_MAPPER.put("v", "tab2");

        // Inizializzo le query in base alla tabella dei tabulati da interrogare
        MNEMONIC_TABULATION_QUERY_MAPPER.put("tab3", "" +
                "SELECT TAB3.TAB3TIP, TAB3.TAB3DESC AS description " +
                "FROM   TAB3 " +
                "WHERE  TAB3.TAB3COD = ? AND TAB3.TAB3TIP = ?");
        MNEMONIC_TABULATION_QUERY_MAPPER.put("tab2", "" +
                "SELECT TAB2.TAB2TIP, TAB2.TAB2D2 AS description, TAB2.TAB2D1 " +
                "FROM   TAB2 " +
                "WHERE  TAB2.TAB2COD = ? AND TAB2.TAB2TIP = ?");
        MNEMONIC_TABULATION_QUERY_MAPPER.put("tab0", "" +
                "SELECT TAB0.TAB0TIP, TAB0.TAB0DESC, TAB0.TAB0VID AS description " +
                "FROM   TAB0 " +
                "WHERE  TAB0.TAB0COD = ? AND TAB0.TAB0TIP = ?");
        MNEMONIC_TABULATION_QUERY_MAPPER.put("tab5", "" +
                "SELECT TAB5.TAB5TIP, TAB5.TAB5DESC AS description " +
                "FROM   TAB5 " +
                "WHERE  TAB5.TAB5COD = ? AND TAB5.TAB5TIP = ?");

        // Note: Gli unici applicativi che usano i "TAB" differenti dal "TAB1" (ossia Suite Alice)
        // NON VOGLIONO LA "DESCRIPTION" come descrizione MA LA DESCRIZIONE ABBREVIATA

        // Inizializzo il mapper per le operazioni sulla join
        MNEMONIC_JOIN_OPERATION_MAPPER.put("|", "OR");
        MNEMONIC_JOIN_OPERATION_MAPPER.put("&", "AND");

        // Inizializzo il mapper per aggiornare l'ultimo id di sessione
        SQL_INSERT_SESSION_MAPPER.add(RpaComposerConfiguration.TYPE_ORACLE, SQL_INSERT_SESSION_ID_ORACLE);
        SQL_INSERT_SESSION_MAPPER.add(RpaComposerConfiguration.TYPE_POSTGRESQL, SQL_INSERT_SESSION_ID_POSTGRESQL);
        SQL_INSERT_SESSION_MAPPER.add(RpaComposerConfiguration.TYPE_MSQL, SQL_INSERT_SESSION_ID_SQLSERVER);

        // Recupero dal DB la sessione di esecuzione e la salvo
        if (!mainCompositore.getComposerConfiguration().isAvoidUseNewTables()) {

            mainCompositore.getComposerConfiguration().setSessionId(getSessionId());

        }

    }

    public String retrieveEntityWithDomain(String entityNameString) {

        // Ricerco il nome entità completo
        String entityFullNameQueryString = "" +
                "SELECT c0c_mne_uni AS entity_name " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ? ";

        // Lancio la query
        ResultSet entityFullNameResultSet   = null;
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(entityFullNameQueryString);
            preparedStatement.setString(1, "%." + entityNameString + ".%");
            entityFullNameResultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager] Errore durante il recupero del nome completo dell'entità: " + entityNameString);
            System.err.println(exception);

        }

        // Taglio la stringa per recuperare il nome dell'entità con tutto ciò che segue
        String rawEntityFullName = null;

        try {

            entityFullNameResultSet.next();

            rawEntityFullName = entityFullNameResultSet.getString("entity_name");
            rawEntityFullName = rawEntityFullName.replaceAll(FILTER_PREFIX_FIELD_REGEX, "");

            entityFullNameResultSet.close();
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnenmonicManager] Errore durante la lettura del nome completo dell'entità: " + entityNameString);
            System.err.println(exception);

        }

        String entityFullName = entityNameString + rawEntityFullName.split(entityNameString, 2)[1];

        return entityFullName;

    }

    public List<String> getEntitiesBindWithPriority(String fullEntityName) {

        // Eseguo la query
        String sqlQuery = "" +
                "SELECT c0e_nom     AS current_table_name, " +
                "       c0e_key     AS current_bind_fields, " +
                "       c0e_nom_ex  AS parent_table_name, " +
                "       c0e_key_ex  AS parent_bind_fields " +
                "FROM   rpa_c0entit " +
                "WHERE  c0e_nom LIKE ? " +
                "ORDER BY c0e_nom";

        // Recupero i dati
        ResultSet           resultSet;
        PreparedStatement   preparedStatement;

        try {

            preparedStatement = dbConnection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, "%" + fullEntityName + "%");
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("Errore durante il recupero dei dati per il collegamento dell'entità " + fullEntityName);
            System.err.println(exception);

            return null;

        }

        List<String> entityBindList = new ArrayList<String>();

        try {

            while (resultSet.next()) {

                if (!resultSet.getString("current_table_name").contains("J#")) {

                    String entityParentBind = resultSet.getString("parent_table_name");

                    if (entityParentBind != null && !entityParentBind.isEmpty()) {

                        entityBindList.add(entityParentBind);

                    }

                }

            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("Errore durante la lettura dei dati per il collegamento dell'entità " + fullEntityName);
            System.err.println(exception);

            return null;

        }

        return entityBindList;

    }

    public String translateMnemonicName(String mnemonicName) {

        String mnemonicEntityName = null;

        // Creo la query SQL
        String mnemonicEntityNameQueryString = "" +
                "SELECT c0c_mne_uni AS entity_name " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_ber = ?";

        // Eseguo la query SQL e ritorno il nome dell'entità
        ResultSet mnemonicNameResultSet     = null;
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(mnemonicEntityNameQueryString);
            preparedStatement.setString(1, mnemonicName);
            mnemonicNameResultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante l'acquisizione del nome dell'entità del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        try {

            if (!mnemonicNameResultSet.next()) {

                mnemonicEntityName = null;

            } else {

                mnemonicEntityName = mnemonicNameResultSet.getString("entity_name");
                mnemonicEntityName = mnemonicEntityName.replaceAll(FILTER_PREFIX_FIELD_REGEX, "");

            }

            mnemonicNameResultSet.close();
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del nome dell'entità del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return mnemonicEntityName;


    }

    public boolean isMnemonicTabulation(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicTabulation(mnemonicName);

    }

    public boolean isMnemonicTabulation(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicTabulation(mnemonic);

    }

    public String parseMnemonicTabulationValue(RpaMnemonic mnemonic) {

        // Ritrovo il codice tabulato per il mnemonico passato
        String tableField = mnemonic.getFullFieldName();

        String mnemonicTabulationCodeQueryString = "" +
                "SELECT c0c_tab1 AS tabulation_code " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        String mnemonicTabulationCode               = null;
        ResultSet mnemonicTabulationCodeResultSet   = null;
        PreparedStatement preparedStatement         = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicTabulationCodeQueryString);
            preparedStatement.setString(1, tableField);
            mnemonicTabulationCodeResultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del codice tabulato per il campo: " + tableField);
            System.err.println(exception);

        }

        try {

            if (mnemonicTabulationCodeResultSet.next()) {

                mnemonicTabulationCode = mnemonicTabulationCodeResultSet.getString("tabulation_code");

            }

            mnemonicTabulationCodeResultSet.close();
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del codice tabulato per il campo: " + tableField);
            System.err.println(exception);

        }

        // Verifico su quale tabella del tabulato dovrò eseguire la query
        String mnemonicTabulationCharacterIndex = Character.toString(mnemonicTabulationCode.charAt(MNEMONIC_TABULATION_CHARACTER_INDEX_POSITION));
        String tabulationTableName;

        if (MNEMONIC_TABULATION_MAPPER.containsKey(mnemonicTabulationCharacterIndex)) {

            tabulationTableName = MNEMONIC_TABULATION_MAPPER.get(mnemonicTabulationCharacterIndex);

        } else {

            // Controllo se devo interrogare "tab5"     (specifica di Stefano)
            if (mnemonicTabulationCode.charAt(2) == 'J' || mnemonicTabulationCode.charAt(2) == 'j') {

                tabulationTableName = "tab5";

            }

            // Controllo se devo interrogare "tab1"
            else {

                tabulationTableName = "tab1";

            }

        }

        // Interrogo la tabella del tabulato per ricavare la descrizione di ogni valore del mnemonico
        String mnemonicValue            = mnemonic.getValue();
        String mnemonicTabulationValue  = "";

        if (mnemonicValue != null) {

            ResultSet mnemonicTabulationValueResultSet  = null;

            // Recupero la descrizione del tabulato selezionato (considerando il tipo di tabella del tabulato)
            try {

                // Eseguo una interrogazione su una qualsiasi tabella diversa da "tab1"
                if (MNEMONIC_TABULATION_QUERY_MAPPER.containsKey(tabulationTableName)) {

                    String tabulationDescriptionQueryString = MNEMONIC_TABULATION_QUERY_MAPPER.get(tabulationTableName);
                    preparedStatement = dbConnection.prepareStatement(tabulationDescriptionQueryString);
                    preparedStatement.setString(1, mnemonicTabulationCode);
                    preparedStatement.setString(2, mnemonicValue);
                    mnemonicTabulationValueResultSet = preparedStatement.executeQuery();

                }

                // Eseguo una interrogazione su "tab1"
                else if (RpaNumberUtils.isInteger(mnemonicValue)) {

                    if (RpaNumberUtils.isIntegerWithDotZero(mnemonicValue)) {

                        mnemonicValue = RpaNumberUtils.integerWithoutDotZero(mnemonicValue);

                    }

                    int mnemonicValueInt = Integer.parseInt(mnemonicValue);

                    // Controllo se ho la possibilità di utilizzare un prefisso passato al modello
                    // String modelPrefixString = ComposerConfiguration.getInstance().getEntityDomain();
                    // TODO: Controllare che venga recuperato il valore al registro 87
                    String modelPrefixString = mainCompositore.getComposerConfiguration().getEntityDomain();

                    if (modelPrefixString != null && !modelPrefixString.equals("pwb")) {

                        // Eseguo una query su "tab1" con prefix
                        String tabulationDescriptionQueryString = "" +
                                "SELECT TAB1.TAB1TIP, TAB1.TAB1DESC AS description " +
                                "FROM   TAB1 " +
                                "WHERE  ( (TAB1.TAB1COD NOT LIKE 'X%' AND TAB1.TAB1COD = ? AND TAB1.APP_PREFIX = ?) OR " +
                                "         (TAB1.TAB1COD LIKE     'X%' AND TAB1.TAB1COD = ?) ) AND TAB1.TAB1TIP = ?";

                        preparedStatement = dbConnection.prepareStatement(tabulationDescriptionQueryString);
                        preparedStatement.setString(1, mnemonicTabulationCode);
                        preparedStatement.setString(2, modelPrefixString);
                        preparedStatement.setString(3, mnemonicTabulationCode);
                        preparedStatement.setInt(4, mnemonicValueInt);
                        mnemonicTabulationValueResultSet = preparedStatement.executeQuery();

                    } else {

                        // Eseguo una query su "tab1" senza prefix
                        String tabulationDescriptionQueryString = "" +
                                "SELECT TAB1.TAB1TIP, TAB1.TAB1DESC AS description " +
                                "FROM   TAB1 " +
                                "WHERE  TAB1.TAB1COD = ? AND TAB1.TAB1TIP = ?";

                        preparedStatement = dbConnection.prepareStatement(tabulationDescriptionQueryString);
                        preparedStatement.setString(1, mnemonicTabulationCode);
                        preparedStatement.setInt(2, mnemonicValueInt);
                        mnemonicTabulationValueResultSet = preparedStatement.executeQuery();

                    }

                }

                // Recupero la descrizione del tabulato
                if (mnemonicTabulationValueResultSet.next()) {

                    mnemonicTabulationValue = mnemonicTabulationValueResultSet.getString("description");

                }

                mnemonicTabulationValueResultSet.close();
                preparedStatement.close();


            } catch (SQLException exception) {

                System.err.println("[MnemonicManager]: Errore durante la lettura della descrizione del tabulato per il campo: " + tableField);
                System.err.println(exception);

            }

        }

        return mnemonicTabulationValue;

    }

    public String parseTabulationValueFromCode(String tabulationCode, String tabulationValue) {

        PreparedStatement preparedStatement;

        // Verifico su quale tabella del tabulato dovrò eseguire la query
        String mnemonicTabulationCharacterIndex = Character.toString(tabulationCode.charAt(MNEMONIC_TABULATION_CHARACTER_INDEX_POSITION));
        String tabulationTableName;

        if (MNEMONIC_TABULATION_MAPPER.containsKey(mnemonicTabulationCharacterIndex)) {

            tabulationTableName = MNEMONIC_TABULATION_MAPPER.get(mnemonicTabulationCharacterIndex);

        } else {

            // Controllo se devo interrogare "tab5"     (specifica di Stefano)
            if (tabulationCode.charAt(2) == 'J' || tabulationCode.charAt(2) == 'j') {

                tabulationTableName = "tab5";

            }

            // Controllo se devo interrogare "tab1"
            else {

                tabulationTableName = "tab1";

            }

        }

        // Interrogo la tabella del tabulato per ricavare la descrizione di ogni valore del mnemonico
        String mnemonicValue            = tabulationValue;
        String mnemonicTabulationValue  = "";

        if (mnemonicValue != null && RpaNumberUtils.isInteger(mnemonicValue)) {

            if (RpaNumberUtils.isIntegerWithDotZero(mnemonicValue)) {

                mnemonicValue = RpaNumberUtils.integerWithoutDotZero(mnemonicValue);

            }

            int mnemonicValueInt                        = Integer.parseInt(mnemonicValue);
            ResultSet mnemonicTabulationValueResultSet  = null;

            // Recupero la descrizione del tabulato selezionato (considerando il tipo di tabella del tabulato)
            try {

                // Eseguo una interrogazione su una qualsiasi tabella diversa da "tab1"
                if (MNEMONIC_TABULATION_QUERY_MAPPER.containsKey(tabulationTableName)) {

                    String tabulationDescriptionQueryString = MNEMONIC_TABULATION_QUERY_MAPPER.get(tabulationTableName);
                    preparedStatement = dbConnection.prepareStatement(tabulationDescriptionQueryString);
                    preparedStatement.setString(1, tabulationCode);
                    preparedStatement.setInt(2, mnemonicValueInt);
                    mnemonicTabulationValueResultSet = preparedStatement.executeQuery();

                }

                // Eseguo una interrogazione su "tab1"
                else {

                    // Controllo se ho la possibilità di utilizzare un prefisso passato al modello
                    // String modelPrefixString = ComposerConfiguration.getInstance().getEntityDomain();
                    String modelPrefixString = mainCompositore.getComposerConfiguration().getEntityDomain();

                    if (modelPrefixString != null) {

                        // Eseguo una query su "tab1" con prefix
                        String tabulationDescriptionQueryString = "" +
                                "SELECT TAB1.TAB1TIP, TAB1.TAB1DESC AS description " +
                                "FROM   TAB1 " +
                                "WHERE  ( (TAB1.TAB1COD NOT LIKE 'X%' AND TAB1.TAB1COD = ? AND TAB1.APP_PREFIX = ?) OR " +
                                "         (TAB1.TAB1COD LIKE     'X%' AND TAB1.TAB1COD = ?) ) AND TAB1.TAB1TIP = ?";

                        preparedStatement = dbConnection.prepareStatement(tabulationDescriptionQueryString);
                        preparedStatement.setString(1, tabulationCode);
                        preparedStatement.setString(2, modelPrefixString);
                        preparedStatement.setString(3, tabulationCode);
                        preparedStatement.setInt(4, mnemonicValueInt);
                        mnemonicTabulationValueResultSet = preparedStatement.executeQuery();

                    } else {

                        // Eseguo una query su "tab1" senza prefix
                        String tabulationDescriptionQueryString = "" +
                                "SELECT TAB1.TAB1TIP, TAB1.TAB1DESC AS description " +
                                "FROM   TAB1 " +
                                "WHERE  TAB1.TAB1COD = ? AND TAB1.TAB1TIP = ?";

                        preparedStatement = dbConnection.prepareStatement(tabulationDescriptionQueryString);
                        preparedStatement.setString(1, tabulationCode);
                        preparedStatement.setInt(2, mnemonicValueInt);
                        mnemonicTabulationValueResultSet = preparedStatement.executeQuery();

                    }

                }

                // Recupero la descrizione del tabulato
                if (mnemonicTabulationValueResultSet.next()) {

                    mnemonicTabulationValue = mnemonicTabulationValueResultSet.getString("description");

                }

                mnemonicTabulationValueResultSet.close();
                preparedStatement.close();


            } catch (SQLException exception) {

                System.err.println("[MnemonicManager]: Errore durante la lettura della descrizione del tabulato per il campo: " + tabulationCode);
                System.err.println(exception);

            }

        }

        return mnemonicTabulationValue;

    }

    public boolean isMnemonicBoolean(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicBoolean(mnemonicName);

    }

    public boolean isMnemonicBoolean(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicBoolean(mnemonic);

    }

    public boolean isMnemonicDate(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicDate(mnemonicName);

    }

    public boolean isMnemonicDate(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicDate(mnemonic);

    }

    public boolean isMnemonicTimestamp(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicTimestamp(mnemonicName);

    }

    public boolean isMnemonicTimestamp(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicTimestamp(mnemonic);

    }

    public boolean isMnemonicNumber(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicNumber(mnemonicName);

    }

    public boolean isMnemonicNumber(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicNumber(mnemonic);

    }

    public boolean isMnemonicString(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicString(mnemonicName);

    }

    public boolean isMnemonicString(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicString(mnemonic);

    }

    public boolean isMnemonicMoney(RpaMnemonic mnemonic) {

        return mnemonicTypeFinder.isMnemonicMoney(mnemonic);

    }

    public boolean isMnemonicMoney(String mnemonicName) {

        return mnemonicTypeFinder.isMnemonicMoney(mnemonicName);

    }

    public RpaAbstractMnemonic getMnemonicJoinValue(
            String leftMnemonicName,
            String rightMnemonicValue,
            RpaScopeMnemonicContext scopeMnemonicContext
    ) {

        // Se il valore del mnemonico di destra è vuoto (o nullo) restituisco null
        if (rightMnemonicValue == null || rightMnemonicValue.isEmpty()) {

            return mainCompositore.getMnemonicEmpty();

        }

        // Recupero il nome del campo del mnemonico di sinistra da visualizzare
        String mnemonicEntityName = translateMnemonicName(leftMnemonicName);

        if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

            return mainCompositore.getMnemonicEmpty();

        }

        String tableFieldName = mnemonicEntityName.split("\\.")[0].toLowerCase();

        // Eseguo la join
        Map<String, RpaMnemonic> resultFields = getMnemonicJoinFields(leftMnemonicName, rightMnemonicValue, scopeMnemonicContext);

        // Ritorno il valore corrispondente al campo del mnemonico di sinistra
        if (resultFields.isEmpty()) {

            return mainCompositore.getMnemonicEmpty();

        } else {

            return resultFields.get(tableFieldName);

        }

    }

    public Map<String, RpaMnemonic> getMnemonicJoinFields(
            String leftMnemonicName,
            String rightMnemonicValue,
            RpaScopeMnemonicContext scopeMnemonicContext
    ) {

        // Recupero l'entità del mnemonico di sinistra
        String mnemonicEntityName = translateMnemonicName(leftMnemonicName);

        if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

            return null;

        }

        PreparedStatement   preparedStatement;
        ResultSet           resultSet;

        // Recupero il valore di giunzione tra il mnemonico di sinistra e il valore del mnemonico di destra
        String joinFieldName    = null;
        String leftTableName    = mnemonicEntityName.split("\\.")[1];
        String c0eNomValue      = "J#" + leftTableName + "%";
        String leftTableDomain  = mnemonicEntityName.split("\\.")[2];

        String findMnemonicFieldJoinQuery = "" +
                "SELECT c0e_key " +
                "FROM   rpa_c0entit " +
                "WHERE  c0e_nom LIKE ?";

        try {

            preparedStatement = dbConnection.prepareStatement(findMnemonicFieldJoinQuery);
            preparedStatement.setString(1, c0eNomValue);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println(exception);

            String code     = leftMnemonicName;
            String message  = "Errore nel recupero del c0entit per avere il campo join di " + leftMnemonicName;
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaJoinFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        try {

            resultSet.next();

            joinFieldName = resultSet.getString("c0e_key");

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println(exception);

            String code     = leftMnemonicName;
            String message  = "Errore nella lettura del c0entit per avere il campo join di " + leftMnemonicName;
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaJoinFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        joinFieldName = joinFieldName.split("\\.")[0];

        // Costruisco la query di giunzione
        String joinSql;
        String whereConditionSql;

        int fieldType = getColumnType(leftTableName, joinFieldName);

        try {

            joinSql = "" +
                    "SELECT * " +
                    "FROM   " + leftTableName + " " +
                    "WHERE  ";

            // Preparo la query
            switch (fieldType) {

                case Types.INTEGER:
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.NUMERIC:

                    whereConditionSql = joinFieldName + " = " + rightMnemonicValue;

                    break;

                case Types.VARCHAR:

                    whereConditionSql = joinFieldName + " LIKE '" + rightMnemonicValue + "'";

                    break;

                default:

                    whereConditionSql = joinFieldName + " = " + rightMnemonicValue;

                    break;

            }

            joinSql += whereConditionSql;
            /*
            switch (fieldType) {

                case Types.INTEGER:
                case Types.DOUBLE:
                case Types.FLOAT:

                    joinSql = "" +
                            "SELECT * " +
                            "FROM   " + leftTableName + " " +
                            "WHERE  " + joinFieldName + " = ?";

                    break;

                case Types.VARCHAR:

                    joinSql = "" +
                            "SELECT * " +
                            "FROM   " + leftTableName + " " +
                            "WHERE  " + joinFieldName + " LIKE ?";

                    break;

                default:

                    joinSql = "" +
                            "SELECT * " +
                            "FROM   " + leftTableName + " " +
                            "WHERE  " + joinFieldName + " = ?";

                    break;

            }
            */

            preparedStatement = dbConnection.prepareStatement(joinSql);

            // Inserisco il valore di comparazione nella WHERE della query
            /*
            switch (fieldType) {

                case Types.INTEGER:

                    preparedStatement.setInt(1, Integer.valueOf(rightMnemonicValue));

                    break;

                case Types.DOUBLE:

                    preparedStatement.setDouble(1, Double.valueOf(rightMnemonicValue));

                    break;

                case Types.FLOAT:

                    preparedStatement.setFloat(1, Float.valueOf(rightMnemonicValue));

                    break;

                case Types.VARCHAR:

                    preparedStatement.setString(1, rightMnemonicValue);

                    break;

                default:

                    String code     = leftMnemonicName;
                    String message  = "Tipo della colonna " + joinFieldName + " non gestito nella join";

                    throw new JoinFailException(code, message);

            }
            */

            resultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println(exception);

            String code     = leftMnemonicName;
            String message  = "Errore nell'esecuzione della query join";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaJoinFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        // Converto i dati
        Map<String, RpaMnemonic>    resultFields        = new HashMap<String, RpaMnemonic>();
        Map<String, Integer>        resultFieldsType    = new HashMap<String, Integer>();

        try {

            if (!resultSet.next()) {

                return resultFields;

            }

            ResultSetMetaData   resultSetMetaData   = preparedStatement.getMetaData();
            boolean             isExtensible        = false;
            Long                pkid                = null;

            if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && isColumnExists(resultSetMetaData, "pkid")) {

                pkid = resultSet.getLong("pkid");

            }

            for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                String  columnName  = resultSetMetaData.getColumnName(columnIndex);
                String  columnValue = resultSet.getString(columnIndex);

                if (columnName.equalsIgnoreCase("flg_note_splitted")) {

                    isExtensible = columnValue != null && columnValue.equals("1");
                    break;

                }

            }

            for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                String  columnName      = resultSetMetaData.getColumnName(columnIndex);
                String  columnValue     = resultSet.getString(columnIndex);
                Integer columnType      = resultSetMetaData.getColumnType(columnIndex);

                if (columnType == Types.TIMESTAMP) {

                    columnValue = filterReadFieldDatetime(columnValue);

                }

                // SQLServer restituisce un 0E-9 se recupero un campo numerico (float) con un "resultSet.getString()"
                // Va preso un valore differentemente formattato
                boolean isDBTypeMSQL = mainCompositore.getComposerConfiguration().getDBType() == RpaComposerConfiguration.TYPE_MSQL;
                if (isDBTypeMSQL && columnType == Types.NUMERIC) {

                    float floatValue = resultSet.getFloat(columnIndex);
                    columnValue = String.valueOf(floatValue);

                }

                RpaMnemonic newMnemonic;

                if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && pkid != null) {

                    newMnemonic = new RpaMnemonic(mainCompositore, columnValue, columnName, leftTableName, leftTableDomain, pkid, isExtensible);

                } else {

                    newMnemonic = new RpaMnemonic(mainCompositore, columnValue, columnName, leftTableName, leftTableDomain, isExtensible);

                }

                resultFields.put(columnName.toLowerCase(), newMnemonic);
                resultFieldsType.put(columnName.toLowerCase(), columnType);

            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println(exception);

            String code     = leftMnemonicName;
            String message  = "Errore nella lettura della query join";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaJoinFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        scopeMnemonicContext.updateJoinsContext(leftTableName, leftTableDomain, leftTableName + "." + whereConditionSql, resultFields, resultFieldsType);

        // Ritorno i valori
        return resultFields;

    }

    public List<RpaMnemonicFields> getRootMnemonicFields(RpaMnemonicEntityData mnemonicEntityData) {

        RpaMnemonicNodeRoot root = graph.getRootNode();

        // Eseguo la query sulla tabella indicata dalla radice
        String sqlQuery = "" +
                "SELECT * "  +
                "FROM   "   + root.getEntityTableName() + " " +
                "WHERE  "   + root.getRootCondition();

        PreparedStatement   preparedStatement   = null;
        ResultSet           resultSet           = null;

        try {

            preparedStatement = dbConnection.prepareStatement(sqlQuery);
            mnemonicEntityData.setQuery(sqlQuery);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore nel recupero dei dati per il nodo radice");
            System.err.println(exception);

        }

        // Recupero tutti i campi del mnemonico
        try {

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            List<RpaMnemonicFields> mnemonicFieldsList = new ArrayList<RpaMnemonicFields>();

            while (resultSet.next()) {

                Map<String, RpaMnemonic>    mnemonicFieldsMap       = new HashMap<String, RpaMnemonic>();
                Map<String, Integer>        mnemonicFieldsTypeMap   = new HashMap<String, Integer>();

                Long    pkid            = null;
                boolean isExtensible    = false;

                if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && isColumnExists(resultSetMetaData, "pkid")) {

                    pkid = resultSet.getLong("pkid");

                }

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                    String  columnName  = resultSetMetaData.getColumnName(columnIndex);
                    String  columnValue = resultSet.getString(columnIndex);

                    if (columnName.equalsIgnoreCase("flg_note_splitted")) {

                        isExtensible = columnValue != null && columnValue.equals("1");
                        break;

                    }

                }

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++) {

                    String  columnName  = resultSetMetaData.getColumnName(columnIndex);
                    String  columnValue = resultSet.getString(columnIndex);
                    Integer columnType  = resultSetMetaData.getColumnType(columnIndex);

                    if (columnType == Types.TIMESTAMP) {

                        columnValue = filterReadFieldDatetime(columnValue);

                    }

                    // SQLServer restituisce un 0E-9 se recupero un campo numerico (float) con un "resultSet.getString()"
                    // Va preso un valore differentemente formattato
                    boolean isDBTypeMSQL = mainCompositore.getComposerConfiguration().getDBType() == RpaComposerConfiguration.TYPE_MSQL;
                    if (isDBTypeMSQL && columnType == Types.NUMERIC) {

                        float floatValue = resultSet.getFloat(columnIndex);
                        columnValue = String.valueOf(floatValue);

                    }

                    RpaMnemonic newMnemonic;

                    if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && pkid != null) {

                        newMnemonic = new RpaMnemonic(mainCompositore, columnValue, columnName, root.getEntityTableName(), root.getEntityDomain(), pkid, isExtensible);

                    } else {

                        newMnemonic = new RpaMnemonic(mainCompositore, columnValue, columnName, root.getEntityTableName(), root.getEntityDomain(), isExtensible);

                    }

                    mnemonicFieldsMap.put(columnName.toLowerCase(), newMnemonic);
                    mnemonicFieldsTypeMap.put(columnName.toLowerCase(), columnType);

                }

                mnemonicFieldsList.add(new RpaMnemonicFields(mnemonicEntityData, mnemonicFieldsMap, mnemonicFieldsTypeMap));

            }

            resultSet.close();
            preparedStatement.close();

            long limitReadData = mainCompositore.getComposerConfiguration().getLimitReadData();

            if (mnemonicFieldsList.size() > limitReadData) {

                RpaWarningType warningType = RpaWarningType.REACH_READ_LIMIT;
                String message = "Il limite di " + limitReadData + " elementi letti è stato superato (" + mnemonicFieldsList.size() + ")";

                mainCompositore.getWarningMessages().print(warningType, message);

                mnemonicFieldsList.subList((int) limitReadData, mnemonicFieldsList.size()).clear();

            }

            return mnemonicFieldsList;

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore nella lettura dei dati per il nodo radice");
            System.err.println(exception);

        }

        return null;

    }

    public List<RpaMnemonicFields> getNodeMnemonicFields(
            RpaMnemonicEntityData mnemonicEntityData,
            String mnemonicFromQuery,
            String mnemonicJoinQuery,
            String mnemonicWhereQuery) {

        // Ricostruisco la query con i dati dell'entità corrente
        String mnemonicSelectQuery  = "SELECT " + mnemonicEntityData.getName() + ".* ";

        String sqlQuery = mnemonicSelectQuery + mnemonicFromQuery + mnemonicJoinQuery + mnemonicWhereQuery;

        // Recupero i valori dell'entità
        PreparedStatement   preparedStatement   = null;
        ResultSet           resultSet           = null;

        // System.out.println("[Retrieve] " + sqlQuery);
        debugMessages.print("[Retrieve] " + sqlQuery);

        try {

            preparedStatement   = dbConnection.prepareStatement(sqlQuery);
            resultSet           = preparedStatement.executeQuery();

            mnemonicEntityData.setQuery(sqlQuery);


        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore nel recupero dei dati per il nodo radice");
            System.err.println(exception);

        }

        // Recupero tutti i campi del mnemonico
        try {

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            List<RpaMnemonicFields> mnemonicFieldsList = new ArrayList<RpaMnemonicFields>();

            while (resultSet.next()) {

                Map<String, RpaMnemonic>    mnemonicFieldsMap       = new HashMap<String, RpaMnemonic>();
                Map<String, Integer>        mnemonicFieldsTypeMap   = new HashMap<String, Integer>();

                Long    pkid            = null;
                boolean isExtensible    = false;

                if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && isColumnExists(resultSetMetaData, "pkid")) {

                    pkid = resultSet.getLong("pkid");

                }

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                    String  columnName  = resultSetMetaData.getColumnName(columnIndex);
                    String  columnValue = resultSet.getString(columnIndex);

                    if (columnName.equalsIgnoreCase("flg_note_splitted")) {

                        isExtensible = columnValue != null && columnValue.equals("1");
                        break;

                    }

                }

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++) {

                    // Mappo tutti i risultati
                    String  columnName  = resultSetMetaData.getColumnName(columnIndex);
                    String  columnValue = resultSet.getString(columnIndex);
                    Integer columnType  = resultSetMetaData.getColumnType(columnIndex);

                    if (columnType == Types.TIMESTAMP) {

                        columnValue = filterReadFieldDatetime(columnValue);

                    }

                    // SQLServer restituisce un 0E-9 se recupero un campo numerico (float) con un "resultSet.getString()"
                    // Va preso un valore differentemente formattato
                    boolean isDBTypeMSQL = mainCompositore.getComposerConfiguration().getDBType() == RpaComposerConfiguration.TYPE_MSQL;
                    if (isDBTypeMSQL && columnType == Types.NUMERIC) {

                        float floatValue = resultSet.getFloat(columnIndex);
                        columnValue = String.valueOf(floatValue);

                    }

                    RpaMnemonic newMnemonic;

                    if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && pkid != null) {

                        newMnemonic = new RpaMnemonic(mainCompositore, columnValue, columnName, mnemonicEntityData.getName(), mnemonicEntityData.getDomain(), pkid, isExtensible);

                    } else {

                        newMnemonic = new RpaMnemonic(mainCompositore, columnValue, columnName, mnemonicEntityData.getName(), mnemonicEntityData.getDomain(), isExtensible);

                    }

                    mnemonicFieldsMap.put(columnName.toLowerCase(), newMnemonic);
                    mnemonicFieldsTypeMap.put(columnName.toLowerCase(), columnType);

                }

                mnemonicFieldsList.add(new RpaMnemonicFields(mnemonicEntityData, mnemonicFieldsMap, mnemonicFieldsTypeMap));

            }

            resultSet.close();
            preparedStatement.close();

            long limitReadData = mainCompositore.getComposerConfiguration().getLimitReadData();

            if (mnemonicFieldsList.size() > limitReadData) {

                RpaWarningType warningType = RpaWarningType.REACH_READ_LIMIT;
                String message = "Il limite di " + limitReadData + " elementi letti è stato superato (" + mnemonicFieldsList.size() + ")";

                mainCompositore.getWarningMessages().print(warningType, message);

                mnemonicFieldsList.subList((int) limitReadData, mnemonicFieldsList.size()).clear();

            }

            return mnemonicFieldsList;

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore nella lettura dei dati per il nodo radice");
            System.err.println(exception);

        }

        return null;

    }

    public RpaScopeMnemonicContext getPathInnerJoin(Stack<RpaScope> scopeStack, String mnemonicName) {

        // Recupero l'entità del mnemonico
        RpaScopeMnemonicContext scopeMnemonicContextFound   = null;
        String                  mnemonicEntityName          = translateMnemonicName(mnemonicName);

        if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

            String code     = mnemonicName;
            String message  = "Il mnemonico " + mnemonicName + " non appartiene a nessuna entità";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        try {

            // Cerco ove l'entità del mnemonico è già stata letta come entità PRINCIPALE
            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.isSameMainEntityRead(mnemonicName, mnemonicEntityName)) {

                        return scopeMnemonicContext;

                    }

                }

            }

            // Cerco nello "ScopeMnemonicContext" più recente, un collegamento diretto con l'ultimo mnemonico letto
            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.isConnectedLastReadEntity(mnemonicName, mnemonicEntityName)) {

                        return scopeMnemonicContext;

                    }

                    break;

                }

            }

            // Cerco ove l'entità è già stata letta
            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.hasEntityRead(mnemonicName, mnemonicEntityName)) {

                        return scopeMnemonicContext;

                    }

                }

            }

            // Cerco ove l'entità ha un collegamento DIRETTO con una delle entità PRINCIPALI di ogni contesto
            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.hasEntityMainDirectConnection(mnemonicName, mnemonicEntityName)) {

                        return scopeMnemonicContext;

                    }

                }

            }

            // Cerco ove l'entità ha un collegamento DIRETTO con una delle entità di ogni contesto
            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.hasEntitySomeDirectConnection(mnemonicName, mnemonicEntityName)) {

                        return scopeMnemonicContext;

                    }

                }

            }

            // Cerco ove l'entità è in qualche modo collegata con l'entità principale di ogni contest (collegamento indiretto)
            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.hasEntitySomeConnection(mnemonicName, mnemonicEntityName)) {

                        return scopeMnemonicContext;

                    }

                }

            }

        } catch (Exception exception) {

            String code     = mnemonicName;
            String message  = exception.getMessage();
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        // In tutti gli altri casi, lancio un errore
        String code     = mnemonicName;
        String message  = "Impossibile collegare l'entità di " + mnemonicName + " ad altre entità";
        int context     = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

    }

    public RpaScopeMnemonicContext LAST_OLD_getPathInnerJoin(Stack<RpaScope> scopeStack, String mnemonicName) {

        // Recupero l'entità del mnemonico
        String mnemonicEntityName = translateMnemonicName(mnemonicName);

        if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

            String code     = mnemonicName;
            String message  = "Il mnemonico " + mnemonicName + " non appartiene a nessuna entità";
            int context = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        // Cerco il contesto più recente ([JOIN] o documento) su cui recuperare i dati del mnemonico

        RpaScopeMnemonicContext scopeMnemonicContextFound = null;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);
            if (scope instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                if (scopeMnemonicContext.isGoodForMnemonic(mnemonicName, mnemonicEntityName)) {

                    scopeMnemonicContextFound = scopeMnemonicContext;
                    break;

                }

            }

        }

        // Se non sono riuscito ad allacciare il mnemonico, lancio un errore
        if (scopeMnemonicContextFound == null) {

            String code     = mnemonicName;
            String message  = "Impossibile collegare l'entità di " + mnemonicName + " ad altre entità";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }


        // Ritorno il "ScopeMnemonicContext" trovato
        else {

            return scopeMnemonicContextFound;

        }

    }

    public RpaScopeMnemonicContext OLDgetPathInnerJoin(Stack<RpaScope> scopeStack, String mnemonicName) {

        // TODO: Rendere "OLD" questo metodo e ricrearlo con il metodo "isGoodForMnemonic" di RpaScopeMnemonicContext
        // TODO: Ciclato dall'ultimo elemento presente in "scopeStack"

        // ###  Recupero l'entità del mnemonico                                                                 ###

        String mnemonicEntityName = translateMnemonicName(mnemonicName);

        if (mnemonicEntityName == null || mnemonicEntityName.isEmpty()) {

            String code     = mnemonicName;
            String message  = "Il mnemonico " + mnemonicName + " non appartiene a nessuna entità";
            int context = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        String tableNameWithDomain = mnemonicEntityName.split("\\.", 2)[1];


        // ###  Cerco lo "ScopeMnemonicContext" che contiene dati in cache sul mnemonico                        ###

        RpaScopeMnemonicContext scopeMnemonicContextFound = null;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);
            if (scope instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                if (scopeMnemonicContext.hasReadEntity(tableNameWithDomain)) {

                    scopeMnemonicContextFound = scopeMnemonicContext;
                    break;

                }

            }

        }

        if (scopeMnemonicContextFound != null) {

            return scopeMnemonicContextFound;

        }


        // ###  Cerco lo "ScopeMnemonicContext" la cui entità principale (radice) è la stessa del mnemonico     ###

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);
            if (scope instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                if (scopeMnemonicContext.hasEntityRoot(tableNameWithDomain)) {

                    scopeMnemonicContextFound = scopeMnemonicContext;
                    break;

                }

            }

        }

        if (scopeMnemonicContextFound != null) {

            return scopeMnemonicContextFound;

        }


        // ###  Cerco lo "ScoperMnemonicContext" che ha un collegamento prioritario e diretto con l'entità      ###

        List<String> entityBindList = getEntitiesBindWithPriority(tableNameWithDomain);

        for (String entityBind : entityBindList) {

            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

                RpaScope scope = scopeStack.get(scopeIndex);
                if (scope instanceof RpaScopeMnemonicContext) {

                    RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                    if (scopeMnemonicContext.hasDirectConnection(entityBind)) {

                        scopeMnemonicContextFound = scopeMnemonicContext;
                        break;

                    }

                }

            }

            if (scopeMnemonicContextFound != null) {

                break;

            }

        }

        if (scopeMnemonicContextFound != null) {

            return scopeMnemonicContextFound;

        }


        // ###  Cerco lo "ScopeMnemonicContext" a cui poter collegare l'entità del mnemonico                    ###

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);
            if (scope instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                if (scopeMnemonicContext.hasEntityConnection(tableNameWithDomain)) {

                    scopeMnemonicContextFound = scopeMnemonicContext;
                    break;

                }

            }

        }


        // ###  Se non sono riuscito ad allacciare il mnemonico, lancio un errore                               ###

        if (scopeMnemonicContextFound == null) {

            String code     = mnemonicName;
            String message  = "Impossibile collegare l'entità di " + mnemonicName + " ad altre entità";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaReachEntityException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }


        // ###  Ritorno il "ScopeMnemonicContext" trovato                                                       ###

        else {

            return scopeMnemonicContextFound;

        }

    }

    public List<RpaMnemonicFields> performJoin(
            List<String> mnemonicNameList,
            List<String> valueList,
            List<Integer> valueMnemonicTypeList,
            List<Boolean> valueIsMnemonicList,
            Map<Integer, Boolean> isSetToNullMap,
            String mnemonicEntityName,
            RpaMnemonicEntityData mnemonicEntityData
    ) {

        // ComposerConfiguration composerConfiguration = ComposerConfiguration.getInstance();
        RpaComposerConfiguration composerConfiguration = mainCompositore.getComposerConfiguration();
        String sqlQuery;

        // Se ho un unico valore a null (o la lista è vuota), esco
        if (valueList.isEmpty() || (valueList.size() == 1 && valueList.get(0) == null)) {

            /*
            sqlQuery =
                    "SELECT * " +
                    "FROM   " + mnemonicEntityName;

            String          warningMessage  = "Confronto tra campi di " + mnemonicEntityName + " e null";
            RpaWarningType  warningType     = RpaWarningType.JOIN_ON_NULL;

            RpaWarningMessages warningMessages = mainCompositore.getWarningMessages();
            warningMessages.print(warningType, warningMessage);

            mnemonicEntityData.setMnemonicGraphRootCondition("");
            */
            mnemonicEntityData.setMnemonicGraphRootCondition("");

            String          warningMessage  = "Confronto tra campi di " + mnemonicEntityName + " e null";
            RpaWarningType  warningType     = RpaWarningType.JOIN_ON_NULL;

            RpaWarningMessages warningMessages = mainCompositore.getWarningMessages();
            warningMessages.print(warningType, warningMessage);

            return new ArrayList<RpaMnemonicFields>();

        }

        // Se come valore ho il solo '*', eseguo una query speciale
        else if (valueList.size() == 1 && valueList.get(0).equals("*")) {

            // String mnemonicField            = mnemonicManager.translateMnemonicName(mnemonicNameList.get(0));
            String mnemonicField            = translateMnemonicName(mnemonicNameList.get(0));
            mnemonicField                   = mnemonicField.split("\\.")[0].toLowerCase();
            String mnemonicFieldWithTable   = mnemonicEntityName + "." + mnemonicField;
            String sqlWhereCondition        = mnemonicFieldWithTable + " IS NOT NULL";

            sqlQuery =
                    "SELECT * " +
                            "FROM   " + mnemonicEntityName + " " +
                            "WHERE  " + sqlWhereCondition;

            mnemonicEntityData.setMnemonicGraphRootCondition(sqlWhereCondition);

        }

        else {

            // Per ogni mnemonico, estraggo il relativo campo
            List<String> mnemonicFieldList = new ArrayList<String>();

            for (String mnemonicName : mnemonicNameList) {

                // String mnemonicField = mnemonicManager.translateMnemonicName(mnemonicName);
                String mnemonicField = translateMnemonicName(mnemonicName);
                mnemonicField = mnemonicField.split("\\.")[0].toLowerCase();

                mnemonicFieldList.add(mnemonicField);

            }

            List<String> mnemonicSqlValueList = new ArrayList<String>();

            for (int i = 0; i < valueList.size(); i++) {

                // Per ogni valore di tipo mnemonico, ne estraggo il valore
                if (valueIsMnemonicList.get(i)) {

                    mnemonicSqlValueList.add(valueList.get(i));

                }

                // Per ogni valore di tipo "*", aggiungo un controllo NOT NULL al campo
                else if (valueList.get(i).equals("*")) {

                    String mnemonicField            = mnemonicFieldList.get(i);
                    String mnemonicFieldWithTable   = mnemonicEntityName + "." + mnemonicField;
                    String mnemonicFieldNotNull     = mnemonicFieldWithTable + " IS NOT NULL";

                    mnemonicSqlValueList.add(mnemonicFieldNotNull);

                }

                // Per ogni valore di tipo stringa, ne estraggo la condizione booleana SQL
                else {

                    String booleanExpression        = valueList.get(i);
                    String mnemonicField            = mnemonicFieldList.get(i);
                    String mnemonicName             = mnemonicNameList.get(i);
                    String mnemonicFieldWithTable   = mnemonicEntityName + "." + mnemonicField;
                    int mnemonicType;

                    if (isMnemonicString(mnemonicName)) {

                        mnemonicType = RpaAdvancedVisitor.BOOLEAN_MNEMONIC_STRING;

                    } else {

                        mnemonicType = RpaAdvancedVisitor.BOOLEAN_MNEMONIC_NUMBER;

                    }

                    String sqlBooleanExpression = evaluateJoinBooleanExpression(mnemonicType, mnemonicFieldWithTable, booleanExpression);
                    mnemonicSqlValueList.add(sqlBooleanExpression);

                }

            }

            // Costruisco la query per ogni coppia di elementi
            String whereSql = "";

            for (int i = 0; i < mnemonicSqlValueList.size(); i++) {

                String  mnemonicField               = mnemonicFieldList.get(i);
                String  mnemonicSqlValue            = mnemonicSqlValueList.get(i);
                String  mnemonicFieldWithTable      = mnemonicEntityName + "." + mnemonicField;
                boolean isValueMnemonic             = valueIsMnemonicList.get(i);

                if (isValueMnemonic) {

                    if (mnemonicSqlValue == null || mnemonicSqlValue.isEmpty()) {

                        // Non faccio niente
                        String      warningMessage  = "Confronto tra " + mnemonicNameList.get(i) + " e null";
                        RpaWarningType warningType  = RpaWarningType.JOIN_ON_NULL;

                        RpaWarningMessages warningMessages = mainCompositore.getWarningMessages();
                        warningMessages.print(warningType, warningMessage);

                    } else {

                        int fieldType = getColumnType(mnemonicEntityName, mnemonicField);

                        switch (fieldType) {

                            case Types.INTEGER:
                            case Types.DOUBLE:
                            case Types.FLOAT:
                            case Types.NUMERIC:

                                mnemonicSqlValue = " = " + mnemonicSqlValue;

                                break;

                            case Types.VARCHAR:

                                int dbType = composerConfiguration.getDBType();

                                // Metto due ' (singolo apice) nel caso siano presenti nella stringa
                                mnemonicSqlValue = mnemonicSqlValue.replaceAll("\'", "\'\'");

                                mnemonicSqlValue = " LIKE '" + mnemonicSqlValue + "'";

                                break;

                            default:

                                mnemonicSqlValue = " = " + mnemonicSqlValue;

                                break;


                        }

                    }

                    if (mnemonicSqlValue != null || !mnemonicSqlValue.isEmpty()) {

                        // Controllo se devo forzare la condizione "table.attribute IS NULL"
                        if (isSetToNullMap.containsKey(i) && isSetToNullMap.get(i)) {

                            if (whereSql.isEmpty()) {

                                whereSql = " WHERE " + mnemonicFieldWithTable + mnemonicSqlValue + " IS NULL ";

                            } else {

                                whereSql += " AND " + mnemonicFieldWithTable + mnemonicSqlValue + " IS NULL ";

                            }

                        } else {

                            if (whereSql.isEmpty()) {

                                whereSql = " WHERE " + mnemonicFieldWithTable + mnemonicSqlValue + " ";

                            } else {

                                whereSql += " AND " + mnemonicFieldWithTable + mnemonicSqlValue + " ";

                            }

                        }

                    }

                }

                // else if (valueType == AdvancedVisitor.JOIN_VALUE_TYPE_STRING) {
                else {

                    if (whereSql.isEmpty()) {

                        whereSql = " WHERE " + mnemonicSqlValue;

                    } else {

                        whereSql += " AND " + mnemonicSqlValue;

                    }

                }

            }

            // Eseguo la query
            sqlQuery =
                    "SELECT * " +
                            "FROM   " + mnemonicEntityName + " " +
                            whereSql;

            // Salvo la condizione "WHERE" nel "MnemonicEntityData"
            String filteredWhereCondition = whereSql.replaceAll("WHERE", "");
            mnemonicEntityData.setMnemonicGraphRootCondition(filteredWhereCondition);

        }

        // System.out.println("[JOIN] " + sqlQuery);
        debugMessages.print("[JOIN] " + sqlQuery);

        PreparedStatement   preparedStatement;
        ResultSet           resultSet;
        ResultSetMetaData   resultSetMetaData;

        try {

            preparedStatement   = dbConnection.prepareStatement(sqlQuery);
            resultSet           = preparedStatement.executeQuery();
            resultSetMetaData   = preparedStatement.getMetaData();

            mnemonicEntityData.setQuery(sqlQuery);


        } catch (SQLException exception) {

            System.err.println(exception);

            String code     = "";
            String message  = "Errore nell'esecuzione della join";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaJoinFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        // mnemonicEntityData.setQuery(sqlQuery);

        // Trasformo i dati
        List<RpaMnemonicFields> mnemonicFieldsList  = new ArrayList<RpaMnemonicFields>();
        long                    limitReadData       = mainCompositore.getComposerConfiguration().getLimitReadData();

        try {

            int countResults = 0;

            while (resultSet.next()) {

                Map<String, RpaMnemonic>    values      = new HashMap<String, RpaMnemonic>();
                Map<String, Integer>        valuesType  = new HashMap<String, Integer>();

                RpaMnemonicFields mnemonicFields;

                Long    pkid            = null;
                boolean isExtensible    = false;

                if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && isColumnExists(resultSetMetaData, "pkid")) {

                    pkid = resultSet.getLong("pkid");

                }

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                    String  columnName  = resultSetMetaData.getColumnName(columnIndex);
                    String  columnValue = resultSet.getString(columnIndex);

                    if (columnName.equalsIgnoreCase("flg_note_splitted")) {

                        isExtensible = columnValue != null && columnValue.equals("1");
                        break;

                    }

                }

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                    String  fieldName    = resultSetMetaData.getColumnName(columnIndex);
                    String  fieldValue   = resultSet.getString(columnIndex);
                    int     fieldType    = resultSetMetaData.getColumnType(columnIndex);

                    if (fieldType == Types.TIMESTAMP) {

                        fieldValue = filterReadFieldDatetime(fieldValue);

                    }

                    // SQLServer restituisce un 0E-9 se recupero un campo numerico (float) con un "resultSet.getString()"
                    // Va preso un valore differentemente formattato
                    boolean isDBTypeMSQL = mainCompositore.getComposerConfiguration().getDBType() == RpaComposerConfiguration.TYPE_MSQL;
                    if (isDBTypeMSQL && fieldType == Types.NUMERIC) {

                        float floatValue = resultSet.getFloat(columnIndex);
                        fieldValue = String.valueOf(floatValue);

                    }

                    RpaMnemonic newMnemonic;

                    if (mainCompositore.getComposerConfiguration().isStartFromSicraweb() && pkid != null) {

                        newMnemonic = new RpaMnemonic(mainCompositore, fieldValue, fieldName, mnemonicEntityName, mnemonicEntityData.getDomain(), pkid, isExtensible);

                    } else {

                        newMnemonic = new RpaMnemonic(mainCompositore, fieldValue, fieldName, mnemonicEntityName, mnemonicEntityData.getDomain(), isExtensible);

                    }

                    values.put(fieldName.toLowerCase(), newMnemonic);
                    valuesType.put(fieldName.toLowerCase(), fieldType);

                }

                mnemonicFields = new RpaMnemonicFields(mnemonicEntityData, values, valuesType);
                mnemonicFieldsList.add(mnemonicFields);

                if (countResults == limitReadData) {

                    break;

                }

                ++ countResults;

            }

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println(exception);

            String code     = "";
            String message  = "Errore durante la lettura dei dati della join";
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaJoinFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        if (mnemonicFieldsList.size() == limitReadData) {

            RpaWarningType warningType = RpaWarningType.REACH_READ_LIMIT;
            String message = "Il limite di " + limitReadData + " elementi letti è stato superato (" + mnemonicFieldsList.size() + ")";

            mainCompositore.getWarningMessages().print(warningType, message);

            mnemonicFieldsList.subList((int) limitReadData, mnemonicFieldsList.size()).clear();

        }

        return mnemonicFieldsList;

    }

    private String evaluateJoinBooleanExpression(
            int mnemonicFieldType,
            String mnemonicField,
            String booleanExpression
    ) {

        // https://regex101.com/r/0gpOSg/8
        String booleanExpressionRegex       = "^(=|!=)([^><=!|&]*)([&|](>=|<=|>|<|!=|=)[^><=!|&]*)*$";

        // https://regex101.com/r/o3GXRq/2
        String extractBooleanElementsRegex  = "([&|])(>=|<=|>|<|!=|=)([^><=!|&]+)";

        // ComposerConfiguration composerConfiguration = ComposerConfiguration.getInstance();
        RpaComposerConfiguration composerConfiguration = mainCompositore.getComposerConfiguration();

        // Verifico che il valore sia una espressione
        Matcher booleanExpressionMatcher = Pattern.compile(booleanExpressionRegex).matcher(booleanExpression);

        if (!booleanExpressionMatcher.find()) {

            if (mnemonicFieldType == RpaAdvancedVisitor.BOOLEAN_MNEMONIC_STRING) {

                int dbType = composerConfiguration.getDBType();

                booleanExpression = booleanExpression.replaceAll("\'", "\'\'");

                return " " + mnemonicField + " = '" + booleanExpression + "'";

            } else {

                return " " + mnemonicField + " = " + booleanExpression;

            }

        }

        // Estraggo il primo elemento dell'espressione booleana
        List<String> valueList              = new ArrayList<String>();
        List<String> compareOperatorList    = new ArrayList<String>();
        List<String> logicOperatorList      = new ArrayList<String>();

        String lastCompareOperator  = "";
        String lastValue            = "";
        String lastLogicOperator    = "";

        lastCompareOperator = booleanExpressionMatcher.group(1);
        lastValue           = booleanExpressionMatcher.group(2);

        valueList.add(lastValue);
        compareOperatorList.add(lastCompareOperator);

        // Estraggo gli altri elementi dell'espressione booleana
        String extraBooleanValues   = booleanExpression.substring(booleanExpressionMatcher.end(2));
        Matcher booleanValuesRegex  = Pattern.compile(extractBooleanElementsRegex).matcher(extraBooleanValues);

        while (booleanValuesRegex.find()) {

            lastLogicOperator   = booleanValuesRegex.group(1);
            lastCompareOperator = booleanValuesRegex.group(2);
            lastValue           = booleanValuesRegex.group(3);

            logicOperatorList.add(lastLogicOperator);
            compareOperatorList.add(lastCompareOperator);
            valueList.add(lastValue);

        }

        // Per ogni elemento raccolto, aggiungo una condizione nella query
        String sqlBooleanCondition = "";

        for (int i = 0; i < valueList.size(); i ++) {

            lastValue               = valueList.get(i);
            lastCompareOperator     = compareOperatorList.get(i);
            boolean isValueNumber   = RpaNumberUtils.isDouble(lastValue) || RpaNumberUtils.isInteger(lastValue);

            // TODO: L'operazione di "diverso" cambia a seconda del DB. Adattare
            if (lastCompareOperator.equals("!=")) {

                lastCompareOperator = "<>";

            }

            if (lastValue.isEmpty()) {

                lastValue = "null";

            }

            if (lastValue.equals("null")) {

                if (i == 0) {

                    sqlBooleanCondition += " " + mnemonicField + " " + lastCompareOperator + " " + lastValue + "";

                } else {

                    lastLogicOperator   = logicOperatorList.get(i - 1);
                    lastLogicOperator   = MNEMONIC_JOIN_OPERATION_MAPPER.get(lastLogicOperator);
                    sqlBooleanCondition +=
                            " " + lastLogicOperator + " " + mnemonicField + " " + lastCompareOperator + " " + lastValue;

                }

            }

            else if (mnemonicFieldType == RpaAdvancedVisitor.BOOLEAN_MNEMONIC_STRING) {

                int dbType = composerConfiguration.getDBType();

                lastValue = lastValue.replaceAll("\'", "\'\'");


                if (i == 0) {

                    sqlBooleanCondition += " " + mnemonicField + " " + lastCompareOperator + " '" + lastValue + "'";

                } else {

                    lastLogicOperator   = logicOperatorList.get(i - 1);
                    lastLogicOperator   = MNEMONIC_JOIN_OPERATION_MAPPER.get(lastLogicOperator);
                    sqlBooleanCondition +=
                            " " + lastLogicOperator + " " + mnemonicField + " " + lastCompareOperator + " '" + lastValue + "'";

                }

            }

            else {

                if (i == 0) {

                    sqlBooleanCondition += " " + mnemonicField + " " + lastCompareOperator + " " + lastValue;

                } else {

                    lastLogicOperator   = logicOperatorList.get(i - 1);
                    lastLogicOperator   = MNEMONIC_JOIN_OPERATION_MAPPER.get(lastLogicOperator);
                    sqlBooleanCondition +=
                            " " + lastLogicOperator + " " + mnemonicField + " " + lastCompareOperator + " " + lastValue;

                }

            }

        }

        return sqlBooleanCondition;

    }

    public List<Map<String, String>> getEntitySortedFieldsList(
            RpaMnemonicEntityData mnemonicEntityData,
            List<String> fieldNameList,
            List<RpaMnemonicFieldsComparator.ORDER> orderList
    ) throws Exception {

        // Ricostruisco la query
        String entityName = mnemonicEntityData.getName();
        String sqlOrderBy = "";

        for (int i = 0; i < fieldNameList.size(); i ++) {

            String  fieldName   = fieldNameList.get(i);
            String  orderString = orderList.get(i).toString();

            if (sqlOrderBy.isEmpty()) {

                sqlOrderBy = " ORDER BY " + entityName + "." + fieldName + " " + orderString;

            } else {

                sqlOrderBy += ", " + entityName + "." + fieldName + " " + orderString;

            }

        }

        String sqlQuery = mnemonicEntityData.getQuery() + sqlOrderBy;

        PreparedStatement   preparedStatement;
        ResultSet           resultSet;
        ResultSetMetaData   resultSetMetaData;

        // System.out.println("[ORDER] " + sqlQuery);
        debugMessages.print("[ORDER] " + sqlQuery);

        // Eseguo la query
        try {

            preparedStatement   = dbConnection.prepareStatement(sqlQuery);
            resultSet           = preparedStatement.executeQuery();
            resultSetMetaData   = preparedStatement.getMetaData();

        } catch (SQLException exception) {

            mainCompositore.setExitCode(RpaMainCompositore.EXIT_CODE_ERROR_DB);

            System.err.println(exception);
            throw new Exception("Errore nel recupero dei dati dell'ordinamento");

        }

        // Leggo i risultati
        List<Map<String, String>> fieldsList = new ArrayList<Map<String, String>>();

        try {

            while (resultSet.next()) {

                Map<String, String> fields = new HashMap<String, String>();

                for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++ ) {

                    String fieldName    = resultSetMetaData.getColumnName(columnIndex);
                    String fieldValue   = resultSet.getString(columnIndex);
                    int fieldType       = resultSetMetaData.getColumnType(columnIndex);

                    if (fieldType == Types.TIMESTAMP) {

                        fieldValue = filterReadFieldDatetime(fieldValue);

                    }

                    fields.put(fieldName, fieldValue);

                }

                fieldsList.add(fields);

            }

            preparedStatement.close();

        } catch (SQLException exception) {

            mainCompositore.setExitCode(RpaMainCompositore.EXIT_CODE_ERROR_DB);

            System.err.println(exception);
            throw new Exception("Errore nella lettura dei dati dell'ordinamento");

        }

        return fieldsList;

    }

    public void sortEntity(Stack<RpaScope> scopeStack, List<String> mnemonicNameList, List<RpaMnemonicFieldsComparator.ORDER> orderList) throws Exception {

        // Cerco il MnemonicEntityData più interno nello stackScope
        RpaMnemonicEntityData  mnemonicEntityData  = null;
        RpaPathTreeNode        pathTreeNode        = null;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            if (scope instanceof RpaScopeJoin) {

                RpaScopeJoin scopeJoin = (RpaScopeJoin) scope;
                mnemonicEntityData  = scopeJoin.getMnemonicEntityData();
                pathTreeNode        = scopeJoin.getCachePathTreeNode();

                // Se ho una join che è vuota, esco
                if (scopeJoin.isEmpty()) {

                    return;

                }

                break;

            } else if (scope instanceof RpaScopeDocument) {

                RpaScopeDocument scopeDocument = (RpaScopeDocument) scope;

                // Se all'avvio ho inserito una entità priva di elementi, esco
                if (scopeDocument.isMnemonicDataEmpty()) {

                    return;

                }

                mnemonicEntityData          = scopeDocument.getMnemonicEntityData();
                pathTreeNode                = scopeDocument.getCachePathTreeNode();

            }

        }

        // Per ogni mnemonico, recupero il relativo nome di campo
        List<String> fieldNameList = new ArrayList<String>();

        for (String mnemonicName : mnemonicNameList) {

            // String fieldName		= mnemonicManager.translateMnemonicName(mnemonicName);
            String fieldName		= translateMnemonicName(mnemonicName);
            fieldName 				= fieldName.split("\\.")[0];

            fieldNameList.add(fieldName);

        }

        // Cerco il MnemonicEntityData associato ai mnemonici
        // TODO: Passagli pure "null" come terzo parametro
        mnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicNameList.get(0), null);

        // Verifico se il MnemonicEntityData è vuoto
        if (mnemonicEntityData == null || mnemonicEntityData.isEmpty()) {

            return;

        }

        // Ordino il MnemonicEntityData
        mnemonicEntityData.sort(fieldNameList, orderList);

    }

    public void sortEntityOld(Stack<RpaScope> scopeStack, List<String> mnemonicNameList, List<RpaMnemonicFieldsComparator.ORDER> orderList) throws Exception {

        // Estraggo da ogni mnemonico il relativo identificativo
        List<String> mnemonicFieldList = new ArrayList<String>();

        for (String mnemonicName : mnemonicNameList) {

            String mnemonicField = translateMnemonicName(mnemonicName);
            mnemonicFieldList.add(mnemonicField);

        }

        // Verifico che ogni mnemonico appartenga alla stessa entità
        String          lastEntityFound     = "";
        List<String>    fieldNameList       = new ArrayList<String>();

        for (String mnemonicField : mnemonicFieldList) {

            String entity       = mnemonicField.split("\\.")[1];
            String fieldName    = mnemonicField.split("\\.")[0];

            fieldNameList.add(fieldName.toLowerCase());

            if (lastEntityFound.isEmpty()) {

                lastEntityFound = entity;

            } else if (!lastEntityFound.equals(entity)) {

                throw new Exception("I mnemonici dell'ordinamento non appartengono alla stessa entità");

            }

        }

        // Cerco il MnemonicEntityData più interno nello stackScope
        RpaMnemonicEntityData  mnemonicEntityData   = null;
        RpaPathTreeNode         pathTreeNode        = null;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            if (scope instanceof RpaScopeJoin) {

                RpaScopeJoin scopeJoin = (RpaScopeJoin) scope;
                mnemonicEntityData  = scopeJoin.getMnemonicEntityData();
                pathTreeNode        = scopeJoin.getCachePathTreeNode();

            } else if (scope instanceof RpaScopeDocument) {

                RpaScopeDocument scopeDocument = (RpaScopeDocument) scope;
                mnemonicEntityData          = scopeDocument.getMnemonicEntityData();
                pathTreeNode                = scopeDocument.getCachePathTreeNode();

            }

        }

        // Cerco il MnemonicEntityData associato ai mnemonici
        // TODO: Passagli pure "null" come terzo parametro
        mnemonicEntityData = mnemonicEntityData.retrieveMnemonicData(pathTreeNode, mnemonicNameList.get(0), null);

        // Verifico se il MnemonicEntityData è vuoto
        if (mnemonicEntityData == null || mnemonicEntityData.isEmpty()) {

            return;

        }

        // Se il MnemonicEntityData era già stato ordinato, lancio un errore
        if (mnemonicEntityData.isSorted()) {

            throw new Exception("L'entità " + mnemonicEntityData.getName() + " è già stata ordinata");

        }

        // Ordino il MnemonicEntityData trovato
        mnemonicEntityData.sortMnemonicFieldsList(fieldNameList, orderList);


    }

    public RpaMnemonicNodeRoot getMnemonicNodeRoot() {

        return graph.getRootNode();

    }

    public List<RpaMnemonicNode> getGraphNodes() {

        return this.graph.getNodes();

    }

    public RpaMnemonicGraph getMnemonicGraph() {

        return graph;

    }

    public EntitiesBind getEntitiesBind(String entityName, String parentEntityName) {

        // Eseguo la query
        String sqlQuery = "" +
                "SELECT c0e_nom     AS current_table_name, " +
                "       c0e_key     AS current_bind_fields, " +
                "       c0e_nom_ex  AS parent_table_name, " +
                "       c0e_key_ex  AS parent_bind_fields " +
                "FROM   rpa_c0entit " +
                "WHERE  c0e_nom LIKE ? AND c0e_nom_ex LIKE ?";

        // Recupero i dati
        ResultSet resultSet = null;

        if (entityName.equals(parentEntityName)) {

            System.err.println("ERROR");

        }

        PreparedStatement preparedStatement;

        try {

            preparedStatement = dbConnection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, "%" + entityName + "%");
            preparedStatement.setString(2, "%" + parentEntityName + "%");
            // System.out.println("[QUERY CONNECTION] " + preparedStatement.toString());
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("Errore durante il recupero dei dati per il collegamento dell'entità " + entityName);
            System.err.println(exception);

            return null;

        }

        // Separo i campi di collegamento correnti e del padre dalla virgola
        try {

            boolean isValuesFound = resultSet.next();

            String rawCurrentEntityBindFields   = resultSet.getString("current_bind_fields");
            String rawParentEntityBindFields    = resultSet.getString("parent_bind_fields");

            String[] currentEntityBindFields    = rawCurrentEntityBindFields.split("\\,");
            String[] parentEntityBindFields     = rawParentEntityBindFields.split("\\,");

            // Estraggo i campi dalle dichiarazioni
            for (int i = 0; i < currentEntityBindFields.length; i++) {

                currentEntityBindFields[i]  = currentEntityBindFields[i].split("\\.")[0];
                parentEntityBindFields[i]   = parentEntityBindFields[i].split("\\.")[0];

            }

            preparedStatement.close();

            // Ritorno i collegamenti
            return new EntitiesBind(currentEntityBindFields, parentEntityBindFields);

        } catch (SQLException exception) {

            System.err.println("Errore durante la lettura dei dati per il collegamento dell'entità " + entityName);
            System.err.println(exception);

        }

        return null;

    }

    public int getMnemonicType(String mnemonicNameRaw) {

        String extractMnemonicNameRegex	= RpaMnemonicVisitor.EXTRACT_NAME_MNEMONIC_REGEX;

        Matcher mnemonicNameMatcher = Pattern.compile(extractMnemonicNameRegex).matcher(mnemonicNameRaw);
        mnemonicNameMatcher.find();
        String mnemonicName = mnemonicNameMatcher.group();

        Matcher mnemonicSTRMatcher = Pattern.compile(RpaMnemonicVisitor.EXTRACT_STR_REGEX).matcher(mnemonicNameRaw);
        Matcher mnemonicTOTMatcher = Pattern.compile(RpaMnemonicVisitor.EXTRACT_TOT_REGEX).matcher(mnemonicNameRaw);

        if (mnemonicSTRMatcher.find()) {

            if (isMnemonicSTRNumber(mnemonicNameRaw)) {

                return RpaMnemonic.TYPE_NUMBER;

            } else {

                return RpaMnemonic.TYPE_STRING;

            }

        } else if (mnemonicTOTMatcher.find()) {

            return RpaMnemonic.TYPE_NUMBER;

        } else if (isMnemonicString(mnemonicName)) {

            return RpaMnemonic.TYPE_STRING;

        } else if (isMnemonicMoney(mnemonicName)) {

            return RpaMnemonic.TYPE_MONEY;

        } else if (isMnemonicNumber(mnemonicName)) {

            return RpaMnemonic.TYPE_NUMBER;

        } else if (isMnemonicDate(mnemonicName)) {

            return RpaMnemonic.TYPE_DATE;

        } else if (isMnemonicTimestamp(mnemonicName)) {

            return RpaMnemonic.TYPE_TIMESTAMP;

        } else if (isMnemonicBoolean(mnemonicName)) {

            return RpaMnemonic.TYPE_BOOLEAN;

        } else if (isMnemonicTabulation(mnemonicName)) {

            return RpaMnemonic.TYPE_TABULATION;

        } else {

            return RpaMnemonic.TYPE_UNDEFINED;

        }

    }

    public boolean isMnemonicSTRNumber(String rawMnemonicName) {

        Matcher matcher = Pattern.compile(RpaMnemonicVisitor.EXTRACT_STR_REGEX).matcher(rawMnemonicName);
        matcher.find();

        int index;
        String indexString = matcher.group(1);

        if (indexString == null || indexString.isEmpty()) {

            index = 0;

        } else if (indexString.equals("V")) {

            RpaVariablesManager variablesManager = mainCompositore.getVariablesManager();
            RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
                    variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, RpaMnemonicVisitor.STR_CUSTOM_INDEX);

            RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();
            index  = mnemonicTOT.getValueNumber().intValue();

        } else {

            index = Integer.valueOf(matcher.group(1));

        }

        RpaVariablesManager variablesManager = mainCompositore.getVariablesManager();
        RpaValue strValue = variablesManager.getVariableValue(RpaVariablesManager.Type.STR, index);

        if (strValue == null) {

            return false;

        }

        String strContent = variablesManager.getVariableValue(RpaVariablesManager.Type.STR, index).getValue().getValue();

        if (strContent == null) {

            return false;

        } else if (RpaNumberUtils.isDouble(strContent) || RpaNumberUtils.isInteger(strContent)) {

            return true;

        } else {

            return false;

        }

    }

    public int getCountMnemonicCharacters(RpaMnemonic mnemonic) {

        // Recupero il formato del mnemonico
        String fieldName = mnemonic.getFullFieldName();

        String sqlQuery = "" +
                "SELECT c0c_fs AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        PreparedStatement   preparedStatement;
        ResultSet           resultSet;

        try {

            preparedStatement   = dbConnection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, fieldName);
            resultSet           = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("Errore nel recupero del numero di caratteri per il mnemonico di: " + fieldName);
            System.err.println(exception);

            return -1;

        }

        try {

            // Controllo se al campo è associato un mnemonico
            boolean isMnemonicFound = resultSet.next();

            if (!isMnemonicFound) {

                return -1;

            }

            // Estraggo il formato del mnemonico
            String mnemonicType = resultSet.getString("mnemonic_format");

            // Controllo se il formato del mnemonico è stato definito
            if (mnemonicType == null || mnemonicType.isEmpty()) {

                return -1;

            }

            // Estraggo dal formato del mnemonico, il numero di caratteri
            String charactersCountString = mnemonicType.substring(1);

            if (charactersCountString == null || charactersCountString.isEmpty()) {

                return -1;

            }

            if (!RpaNumberUtils.isInteger(charactersCountString)) {

                return -1;

            }

            preparedStatement.close();

            return Integer.valueOf(charactersCountString);

        } catch(SQLException exception) {

            System.err.println("Errore nella lettura del numero di caratteri per il mnemonico di: " + fieldName);
            System.err.println(exception);

            return -1;

        }

    }

    public int getCountMnemonicDecimals(RpaMnemonic mnemonic) {

        // Recupero il formato del mnemonico
        String fieldName = mnemonic.getFullFieldName();

        String sqlQuery = "" +
                "SELECT c0c_fs AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        PreparedStatement   preparedStatement;
        ResultSet           resultSet;

        try {

            preparedStatement   = dbConnection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, fieldName);
            resultSet           = preparedStatement.executeQuery();

        } catch (SQLException exception) {

            System.err.println("Errore nel recupero del numero di caratteri per il mnemonico di: " + fieldName);
            System.err.println(exception);

            return -1;

        }

        try {

            // Controllo se al campo è associato un mnemonico
            boolean isMnemonicFound = resultSet.next();

            if (!isMnemonicFound) {

                return -1;

            }

            // Estraggo il formato del mnemonico
            String mnemonicType = resultSet.getString("mnemonic_format");

            // Controllo se il formato del mnemonico è stato definito
            if (mnemonicType == null || mnemonicType.isEmpty()) {

                return -1;

            }

            // Controllo che il formato sia definito per numeri decimali
            if (mnemonicType.charAt(0) != 'f' && mnemonicType.charAt(0) != 'F') {

                return -1;

            }

            // TODO: Chiamare questa funzione da "Mnemonic.updateFormat" e salvarne il risultato

            // Estraggo dal formato del mnemonico, il numero di decimali
            String charactersCountString = mnemonicType.substring(1);

            if (charactersCountString == null || charactersCountString.isEmpty()) {

                return -1;

            }

            if (!RpaNumberUtils.isDouble(charactersCountString)) {

                return -1;

            }

            preparedStatement.close();

            Matcher decimalMatcher = Pattern.compile("\\.([0-9]+)$").matcher(charactersCountString);

            if (!decimalMatcher.find()) {

                return -1;

            }

            return Integer.valueOf(decimalMatcher.group(1));

        } catch(SQLException exception) {

            System.err.println("Errore nella lettura del numero di caratteri per il mnemonico di: " + fieldName);
            System.err.println(exception);

            return -1;

        }

    }

    public Integer getLastMnemonicValueType() {

        return lastMnemonicValueType;

    }

    public void setLastMnemonicValueType(Integer lastMnemonicValueType) {

        this.lastMnemonicValueType = lastMnemonicValueType;

    }

    public void updateMnemonicEntityDataIndex(Stack<RpaScope> scopeStack, String loopName, int newIndex) {

        boolean isMnemonicEntityDataUpdated = false;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            if (scope instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext            = (RpaScopeMnemonicContext) scope;
                RpaMnemonicEntityData   mnemonicEntityDataWithLoopBind  = scopeMnemonicContext.searchEntityWithLoopBind(loopName);

                if (mnemonicEntityDataWithLoopBind != null) {

                    mnemonicEntityDataWithLoopBind.setCurrentIndex(newIndex);
                    isMnemonicEntityDataUpdated = true;
                    break;

                }

            }

        }

        if (!isMnemonicEntityDataUpdated) {

            String code     = loopName;
            String message  = "Impossibile trovare e aggiornare il 'MnemonicEntityData' associato al loop: " + loopName;
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaUpdateMnemonicEntityDataIndexException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

    public void unbindMnemonicEntityFromLoop(Stack<RpaScope> scopeStack, String loopName) {

        boolean isMnemonicEntityDataUpdated = false;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            if (scope instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext            = (RpaScopeMnemonicContext) scope;
                RpaMnemonicEntityData   mnemonicEntityDataWithLoopBind  = scopeMnemonicContext.searchEntityWithLoopBind(loopName);

                if (mnemonicEntityDataWithLoopBind != null) {

                    mnemonicEntityDataWithLoopBind.setCurrentIndex(2);
                    mnemonicEntityDataWithLoopBind.setLoopBindName(null);
                    isMnemonicEntityDataUpdated = true;
                    break;

                }

            }

        }

        if (!isMnemonicEntityDataUpdated) {

            String code     = loopName;
            String message  = "Impossibile trovare e resettare il 'MnemonicEntityData' associato al loop: " + loopName;
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaUpdateMnemonicEntityDataIndexException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

    public class EntitiesBind {

        private String[] parentBindFields;
        private String[] currentBindFields;

        public EntitiesBind(String[] currentBindFields, String[] parentBindFields) {

            this.currentBindFields  = currentBindFields;
            this.parentBindFields   = parentBindFields;

        }

        public String[] getParentBindFields() {
            return parentBindFields;
        }

        public String[] getCurrentBindFields() {
            return currentBindFields;
        }

    }

    private String filterReadFieldDatetime(String value) {

        // Controllo se il valore è a null
        if (value == null || value.isEmpty()) {

            return value;

        }

        // Verifico che il valore sia un Datetime
        Matcher datetimeMatcher = Pattern.compile(DATETIME_MILLISECONDS_REGEX).matcher(value);

        if (!datetimeMatcher.find()) {

            System.err.println("Il valore dovrebbe essere un 'Datetime' MA NON LO E'!");
            return value;

        }

        // Verifico se i millisecondi sono dichiarati
        if (datetimeMatcher.group(3) != null && !datetimeMatcher.group(3).isEmpty()) {

            // Cerco ed elimino gli zeri aggiuntivi
            Matcher extraZeroMatcher = Pattern.compile(NUMBER_FORMAT_ZERO_REGEX).matcher(datetimeMatcher.group(3));

            if (extraZeroMatcher.find() && extraZeroMatcher.group(2) != null && !extraZeroMatcher.group(2).isEmpty()) {

                String rawDatetime = datetimeMatcher.group(1);
                rawDatetime += "." + extraZeroMatcher.group(1);

                return rawDatetime;

            } else {

                return value;

            }

        } else {

            return value;

        }

    }

    public int getColumnType(String tableName, String columnName) {

        columnName = columnName.toLowerCase();

        String sqlQuery = "" +
                "SELECT * " +
                "FROM   " + tableName;

        ResultSetMetaData resultSetMetaData;
        PreparedStatement preparedStatement;

        try {

            preparedStatement = dbConnection.prepareStatement(sqlQuery);
            resultSetMetaData = preparedStatement.getMetaData();

        } catch (SQLException exception) {

            return -999;

        }

        try {

            for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex ++) {

                String resultSetColumnName = resultSetMetaData.getColumnName(columnIndex).toLowerCase();

                if (resultSetColumnName.equals(columnName)) {

                    int columnType = resultSetMetaData.getColumnType(columnIndex);

                    preparedStatement.close();

                    return columnType;

                }

            }

            preparedStatement.close();

        } catch (SQLException exception) {

            return -999;

        }

        return -999;

    }

    public int getSessionId() {

        // Preparo la query con tutti i dati
        PreparedStatement   preparedStatementUpdate,    preparedStatementQueryId;
        ResultSet           resultSetUpdate,            resultSetQueryId;

        Calendar calendar   = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        int dbType          = mainCompositore.getComposerConfiguration().getDBType();
        String sqlInsert    = SQL_INSERT_SESSION_MAPPER.get(dbType);

        Long    userId      = mainCompositore.getComposerConfiguration().getUserId();
        String  modelName   = mainCompositore.getComposerConfiguration().getInputModel().getName();

        if (userId == null) {

            userId = Long.valueOf(-1);

        }

        // Inserisco nella "INSERT" la maggior parte dei dati
        try {

            preparedStatementUpdate = dbConnection.prepareStatement(sqlInsert);
            preparedStatementUpdate.setTimestamp(1, timestamp);
            preparedStatementUpdate.setLong(2, userId);
            preparedStatementUpdate.setTimestamp(3, timestamp);
            preparedStatementUpdate.setLong(4, userId);
            preparedStatementUpdate.setString(6, modelName);

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            int context = RpaComposerException.PRECOMPILE_MESSAGE;
            throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, sqlException);


        }

        // Acquisico l'ultimo ID utilizzato e provo a inserire il record finchè non ci riesco
        boolean isRecordInserted    = false;
        Integer lastId              = null;
        int     currentIterator     = 0;

        String sqlQueryId = "" +
                "SELECT MAX(id) AS max_id " +
                "FROM rpa_session";

        while (!isRecordInserted && currentIterator < MAX_ITERATION_SEARCH_ID) {

            // Recupero l'ultimo ID utilizzato
            boolean isMaxIdFound    = false;

            try {

                preparedStatementQueryId    = dbConnection.prepareStatement(sqlQueryId);
                resultSetQueryId            = preparedStatementQueryId.executeQuery();
                boolean isElementFound      = resultSetQueryId.next();

                if (isElementFound) {

                    lastId = resultSetQueryId.getInt("max_id");

                }

                isMaxIdFound = isElementFound && lastId != null;

            } catch (SQLException sqlException) {

                sqlException.printStackTrace();

                int context = RpaComposerException.PRECOMPILE_MESSAGE;
                throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, sqlException);

            }

            if (isMaxIdFound) {

                lastId += 1;

            } else {

                lastId = 0;

            }

            // Controllo se posso salvare il record con il nuovo ID
            try {

                preparedStatementUpdate.setInt(5, lastId);
                preparedStatementUpdate.executeUpdate();

                isRecordInserted = true;

            } catch (SQLException sqlException) {

                isRecordInserted = false;

            }

            ++ currentIterator;

        }

        return lastId;

    }

    public void retrieveAndSaveParameters(long idSession, Map<String, String> valuesMap, Map<String, String> descriptionsMap) {

        // Preparo la query
        PreparedStatement   preparedStatement;
        ResultSet           resultSet;

        String sqlQuery = "" +
                "SELECT * " +
                "FROM rpa_comparam " +
                "WHERE id_sessione = ?";

        // Eseguo la query
        try {

            preparedStatement   = dbConnection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, idSession);
            resultSet           = preparedStatement.executeQuery();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            int context = RpaComposerException.PRECOMPILE_MESSAGE;
            throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, sqlException);

        }

        // Ciclo su tutti i risultati e salvo ogni valore e descrizione associato alla descrizione
        try {

            while (resultSet.next()) {

                String code         = resultSet.getString("codice");
                String value        = resultSet.getString("valore");
                String description  = resultSet.getString("descr");

                valuesMap.put(code, value);
                descriptionsMap.put(code, description);

            }

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            int context = RpaComposerException.PRECOMPILE_MESSAGE;
            throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, sqlException);

        }

    }

    public boolean isEmptyJoinPresent(Stack<RpaScope> scopeStack) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            if (scope instanceof RpaScopeJoin) {

                RpaScopeJoin scopeJoin = (RpaScopeJoin) scope;

                if (scopeJoin.isEmpty()) {

                    return true;

                }

            }

        }

        return false;

    }

    public boolean isMnemonicExtensible(RpaMnemonic mnemonic, String value) {

        if (true) {
            return false;
        }

        // Verifico se la riga del mnemonico è estensibile
        boolean isMnemonicExtensible = false;

        try {

            String sqlTableName     = mnemonic.getTable();
            String sqlQueryString   = "" +
                    "SELECT flg_note_splitted " +
                    "FROM   " + sqlTableName + " " +
                    "WHERE  pkid LIKE ?";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlQueryString);
            preparedStatement.setLong(1, mnemonic.getPrimaryKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            String splitIndex       = null;
            isMnemonicExtensible    = splitIndex != null && splitIndex.equals("1");

            if (mainCompositore.getComposerConfiguration().getDBType() == RpaComposerConfiguration.TYPE_MSQL) {

                splitIndex = resultSet.getString("FLG_NOTE_SPLITTED");

            } else {

                splitIndex = resultSet.getString("flg_note_splitted");

            }

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            int context = RpaComposerException.COMPILE_MESSAGE;
            throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, sqlException);

        }

        // Verifico se esiste una tabella di estensione
        boolean isExtensibleTableExists = false;

        try {

            String              tableNamePattern    = mnemonic.getTable().toLowerCase() + "_split";
            DatabaseMetaData    databaseMetaData    = dbConnection.getMetaData();
            ResultSet           resultSet           = databaseMetaData.getTables(
                    null,
                    null,
                    tableNamePattern,
                    null
            );

            isExtensibleTableExists = resultSet.next();

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            int context = RpaComposerException.COMPILE_MESSAGE;
            throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, sqlException);

        }

        // Verifico se il valore ha almeno 2000 caratteri
        boolean hasValueLength2000 = value != null && value.length() == 2000;

        // Tutte le condizioni di estensibilità devono essere rispettate
        return isMnemonicExtensible && isExtensibleTableExists && hasValueLength2000;

    }

    public String getMnemonicExtension(RpaMnemonic mnemonic, String value) {

        // Tolgo le quadre dal valore del mnemonico
        Matcher matcherValue = Pattern.compile(EXTENSION_SQUARE_REGEX).matcher(value);

        matcherValue.find();

        if (matcherValue.group(6) != null && !matcherValue.group(6).isEmpty()) {

            value = matcherValue.group(2) + matcherValue.group(6);

        } else {

            value = matcherValue.group(2);

        }

        // Caso speciale: Tolgo le estensioni per i campi delle view (testi con suffisso >>&12345)
        Matcher matcherViewCode = Pattern.compile(EXTENSION_VIEW_REGEX).matcher(value);

        if (matcherViewCode.find()) {

            value = matcherViewCode.group(1);

        }

        // Recupero il nome della colonna che permetta di filtrare la tabella "split" per pkid
        String foreignFieldName = "";

        try {

            String sqlFieldFilter = mnemonic.getFieldTable().toUpperCase() + "." + mnemonic.getTable().toUpperCase() + "%";
            String sqlQueryString = "" +
                    "SELECT c0c_id_split AS foreign_key_split " +
                    "FROM   rpa_c0campi " +
                    "WHERE  c0c_mne_uni LIKE ?";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlQueryString);
            preparedStatement.setString(1, sqlFieldFilter);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            foreignFieldName = resultSet.getString("foreign_key_split");

        } catch (SQLException sqlException) {

            mainCompositore.getWarningMessages().print(RpaWarningType.WRONG_SQL_QUERY, sqlException.getMessage());

            return value;

        }

        // Recupero tutte le estensioni del valore ordinate per "numord"
        List<String> extensions = new ArrayList<String>();

        try {

            String tableName        = mnemonic.getTable().toLowerCase() + "_split";
            String sqlQueryString   = "" +
                    "SELECT note " +
                    "FROM   " + tableName + " " +
                    "WHERE  " + foreignFieldName + " = ? " +
                    "ORDER BY numord";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlQueryString);
            preparedStatement.setLong(1, mnemonic.getPrimaryKey());
            ResultSet resultSet = preparedStatement.executeQuery();

            String debugPrefix  = "[Mnemonic Extend] ";
            String debugMessage = sqlQueryString.replaceAll("\\?", mnemonic.getPrimaryKey().toString());
            mainCompositore.getDebugMessages().print(debugPrefix + debugMessage);

            while (resultSet.next()) {

                extensions.add(resultSet.getString("note"));

            }

        } catch (SQLException sqlException) {

            mainCompositore.getWarningMessages().print(RpaWarningType.WRONG_SQL_QUERY, sqlException.getMessage());

            return value;

        }

        // Scorro le estensioni
        int countCharacters = value.length();

        for (String extension : extensions) {

            // Tolgo le quadre a ogni estensione
            Matcher matcherExtension = Pattern.compile(EXTENSION_SQUARE_REGEX).matcher(extension);

            matcherExtension.find();

            if (matcherExtension.group(6) != null && !matcherExtension.group(6).isEmpty()) {

                extension = matcherExtension.group(2) + matcherExtension.group(6);

            } else {

                extension = matcherExtension.group(2);

            }

            // Concateno ogni risultato
            value += extension;

            countCharacters += extension.length();

        }

        // Restituisco la concatenazione
        return value;

    }

    private boolean isColumnExists(ResultSetMetaData resultSetMetaData, String columnName) throws SQLException {

        boolean isExists = false;

        for (int i = 1; i < resultSetMetaData.getColumnCount(); i ++) {

            String currentColumnName = resultSetMetaData.getColumnName(i);

            if (columnName.equals(currentColumnName) || columnName.toUpperCase().equals(currentColumnName)) {

                isExists = true;
                break;

            }

        }

        return isExists;

    }

    // Regexp finale: https://regex101.com/r/Iam2YQ/2

    public void updatePreparedStatement(PreparedStatement preparedStatement, String tableName, String columnName, int index, String value) {

        try {

            int columnType = getColumnType(tableName, columnName);

            switch (columnType) {

                case Types.INTEGER:

                    if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                        value = RpaNumberUtils.integerWithoutDotZero(value);

                    }
                    int intValue = Integer.valueOf(value);
                    preparedStatement.setInt(index, intValue);

                    break;


                case Types.FLOAT:
                case Types.NUMERIC:

                    float floatValue = Float.valueOf(value);
                    preparedStatement.setFloat(index, floatValue);

                    break;


                case Types.DOUBLE:

                    double doubleValue = Double.valueOf(value);
                    preparedStatement.setDouble(index, doubleValue);

                    break;


                case Types.BOOLEAN:

                    boolean boolValue = Boolean.valueOf(value);
                    preparedStatement.setBoolean(index, boolValue);

                    break;


                case Types.TINYINT:

                    int tinyintValue = Integer.valueOf(value);
                    preparedStatement.setInt(index, tinyintValue);

                    break;


                case Types.VARCHAR:

                    preparedStatement.setString(index, value);

                    break;

                case Types.DATE:

                    Date dateValue = Date.valueOf(value);
                    preparedStatement.setDate(index, dateValue);

                    break;

                case Types.TIMESTAMP:

                    Timestamp timestampValue = Timestamp.valueOf(value);
                    preparedStatement.setTimestamp(index, timestampValue);

                    break;


            }

        } catch (SQLException exception) {

            System.err.println(exception);

        }

    }

    public String updateSQLStringParameter(String sqlString, String tableName, String columnName, String value) {

        int columnType = getColumnType(tableName, columnName);

        switch (columnType) {

            case Types.INTEGER:

                return sqlString.replaceAll("\\?", value);


            case Types.FLOAT:

                return sqlString.replaceAll("\\?", value);


            case Types.DOUBLE:

                return sqlString.replaceAll("\\?", value);


            case Types.BOOLEAN:

                return sqlString.replaceAll("\\?", value);


            case Types.TINYINT:

                return sqlString.replaceAll("\\?", value);


            case Types.VARCHAR:

                return sqlString.replaceAll("\\?", "'" + value + "'");


            case Types.DATE:

                // TODO: Effettuare un controllo a DB per settare il valore corretto
                return sqlString.replaceAll("\\?", value);


            case Types.TIMESTAMP:

                // TODO: Effettuare un controllo a DB per settare il valore corretto
                return sqlString.replaceAll("\\?", value);


        }

        return sqlString;

    }

}
