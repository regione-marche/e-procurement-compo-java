package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import static it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager.FILTER_PREFIX_FIELD_REGEX;

public class RpaMnemonicTypeFinder {

    private static final String     MNEMONIC_TYPE_BOOLEAN_SN    = "SN";
    private static final String     MNEMONIC_TYPE_BOOLEAN_SNNN  = "SNNN";
    private static final String     MNEMONIC_TYPE_DATA          = "DATA_ELDA";
    private static final String     MNEMONIC_TYPE_MONEY         = "MONEY";
    private static final Character  MNEMONIC_FORMAT_INTEGER     = 'N';
    private static final Character  MNEMONIC_FORMAT_FLOAT       = 'F';
    private static final Character  MNEMONIC_FORMAT_STRING      = 'A';

    Connection dbConnection;

    public RpaMnemonicTypeFinder(Connection dbConnection) {

        this.dbConnection = dbConnection;

    }

    public boolean isMnemonicTabulation(String mnemonicName) {

        // Il mnemonico è tabellare se il campo "c0c_tab1" non è NULL
        String checkMnemonicTabulationQueryString = "" +
                "SELECT * " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_ber = ? AND c0c_tab1 IS NOT NULL";

        ResultSet checkMnemonicTabulationResultSet  = null;
        PreparedStatement preparedStatement         = null;

        Boolean isMnemonicTabulation = null;

        try {

            preparedStatement                   = dbConnection.prepareStatement(checkMnemonicTabulationQueryString);
            preparedStatement.setString(1, mnemonicName);
            checkMnemonicTabulationResultSet    = preparedStatement.executeQuery();

            isMnemonicTabulation = isMnemonicTabulation(checkMnemonicTabulationResultSet);

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la verifica (QUERY) del tipo tabulato per il mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return isMnemonicTabulation;

    }

    public boolean isMnemonicTabulation(RpaMnemonic mnemonic) {

        // Il mnemonico è tabellare se il campo "c0c_tab1" non è NULL
        String fullFieldName = mnemonic.getFullFieldName();

        String checkMnemonicTabulationQueryString = "" +
                "SELECT * " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ? AND c0c_tab1 IS NOT NULL";

        ResultSet checkMnemonicTabulationResultSet  = null;
        PreparedStatement preparedStatement         = null;

        Boolean isMnemonicTabulation = null;

        try {

            preparedStatement                   = dbConnection.prepareStatement(checkMnemonicTabulationQueryString);
            preparedStatement.setString(1, fullFieldName);
            checkMnemonicTabulationResultSet    = preparedStatement.executeQuery();

            isMnemonicTabulation = isMnemonicTabulation(checkMnemonicTabulationResultSet);

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la verifica (QUERY) del tipo tabulato per il campo: " + fullFieldName);
            System.err.println(exception);

        }

        return isMnemonicTabulation;

    }

    private boolean isMnemonicTabulation(ResultSet checkMnemonicTabulationResultSet) {

        try {

            // Il mnemonico è tabellare se ho almeno un risultato nel "result set"
            boolean isMnemonicTabulation = checkMnemonicTabulationResultSet.next();

            checkMnemonicTabulationResultSet.close();

            return isMnemonicTabulation;


        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la verifica (RESULT_SET) del tipo tabulato per il mnemonico");
            System.err.println(exception);

        }

        return false;

    }

    public boolean isMnemonicBoolean(String mnemonicName) {

        // Recupero il tipo di mnemonico dal database
        String mnemonicTypeQueryString = "" +
                "SELECT c0c_dom AS mnemonic_type " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_ber = ?";

        ResultSet mnemonicTypeResultSet     = null;
        PreparedStatement preparedStatement = null;

        Boolean isMnemonicBoolean = null;

        try {

            preparedStatement       = dbConnection.prepareStatement(mnemonicTypeQueryString);
            preparedStatement.setString(1, mnemonicName);
            mnemonicTypeResultSet   = preparedStatement.executeQuery();

            isMnemonicBoolean = isMnemonicBoolean(mnemonicTypeResultSet);
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo booleano) del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return isMnemonicBoolean;

    }

    public boolean isMnemonicBoolean(RpaMnemonic mnemonic) {

        // Recupero il tipo di mnemonico dal database
        String fullFieldName = mnemonic.getFullFieldName();

        String mnemonicTypeQueryString = "" +
                "SELECT c0c_dom AS mnemonic_type " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        ResultSet mnemonicTypeResultSet     = null;
        PreparedStatement preparedStatement = null;

        Boolean isMnemonicBoolean = null;

        try {

            preparedStatement       = dbConnection.prepareStatement(mnemonicTypeQueryString);
            preparedStatement.setString(1, fullFieldName);
            mnemonicTypeResultSet   = preparedStatement.executeQuery();

            isMnemonicBoolean = isMnemonicBoolean(mnemonicTypeResultSet);
            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo booleano) per il campo" + fullFieldName);
            System.err.println(exception);

        }

        return isMnemonicBoolean;

    }

    private boolean isMnemonicBoolean(ResultSet mnemonicTypeResultSet) {

        // Recupero il tipo di mnemonico dal database
        try {

            if (mnemonicTypeResultSet.next()) {

                String mnemonicType = mnemonicTypeResultSet.getString("mnemonic_type");

                mnemonicTypeResultSet.close();
                // preparedStatement.close();

                if (mnemonicType != null && (mnemonicType.equals(MNEMONIC_TYPE_BOOLEAN_SN) || mnemonicType.equals(MNEMONIC_TYPE_BOOLEAN_SNNN))) {

                    return true;

                }
                else {

                    return false;

                }

            }

            mnemonicTypeResultSet.close();
            // preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del tipo (per controllo booleano) del mnemonico");
            System.err.println(exception);

        }

        return false;

    }

    public boolean isMnemonicDate(String mnemonicName) {

        int mnemonicDateType = getMnemonicDateType(mnemonicName);

        return mnemonicDateType == Types.DATE;

    }

    public boolean isMnemonicDate(RpaMnemonic mnemonic) {

        int mnemonicDateType = getMnemonicDateType(mnemonic);

        return mnemonicDateType == Types.DATE;

    }

    public boolean isMnemonicTimestamp(String mnemonicName) {

        int mnemonicDateType = getMnemonicDateType(mnemonicName);

        return mnemonicDateType == Types.TIMESTAMP;

    }

    public boolean isMnemonicTimestamp(RpaMnemonic mnemonic) {

        int mnemonicDateType = getMnemonicDateType(mnemonic);

        return mnemonicDateType == Types.TIMESTAMP;

    }

    public boolean isMnemonicNumber(String mnemonicName) {

        // Costruisco ed eseguo la query
        String mnemonicNumberCheckSqlQuery = "" +
                "SELECT c0c_fs AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_ber = ?";

        ResultSet mnemonicNumberCheckResultSet  = null;
        PreparedStatement preparedStatement     = null;

        Boolean isMnemonicNumber = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicNumberCheckSqlQuery);
            preparedStatement.setString(1, mnemonicName);
            mnemonicNumberCheckResultSet    = preparedStatement.executeQuery();

            isMnemonicNumber = isMnemonicNumber(mnemonicNumberCheckResultSet);

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo numero) del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return isMnemonicNumber;

    }

    public boolean isMnemonicNumber(RpaMnemonic mnemonic) {

        // Costruisco ed eseguo la query
        String fullFieldName = mnemonic.getFullFieldName();

        String mnemonicNumberCheckSqlQuery = "" +
                "SELECT c0c_fs AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        ResultSet mnemonicNumberCheckResultSet  = null;
        PreparedStatement preparedStatement     = null;

        Boolean isMnemonicNumber = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicNumberCheckSqlQuery);
            preparedStatement.setString(1, fullFieldName);
            mnemonicNumberCheckResultSet    = preparedStatement.executeQuery();

            isMnemonicNumber = isMnemonicNumber(mnemonicNumberCheckResultSet);

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo numero) per il campo: " + fullFieldName);
            System.err.println(exception);

        }

        return isMnemonicNumber;

    }

    private boolean isMnemonicNumber(ResultSet mnemonicNumberCheckResultSet) {

        try {

            // Se il primo carattere è "N" o "F", ho un numero (N = integer, F = float)
            if (mnemonicNumberCheckResultSet.next()) {

                String      mnemonicFormatFullCodeString    = mnemonicNumberCheckResultSet.getString("mnemonic_format");
                Character   mnemonicFormatCodeCharacter     = mnemonicFormatFullCodeString.charAt(0);

                mnemonicNumberCheckResultSet.close();

                return mnemonicFormatCodeCharacter.equals(MNEMONIC_FORMAT_INTEGER) || mnemonicFormatCodeCharacter.equals(MNEMONIC_FORMAT_FLOAT);

            } else {

                mnemonicNumberCheckResultSet.close();

                return false;

            }


        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del tipo (per controllo numero) del mnemonico");
            System.err.println(exception);

        }

        return false;

    }

    public boolean isMnemonicString(String mnemonicName) {

        // Costruisco ed eseguo la query
        String mnemonicStringCheckSqlQuery = "" +
                "SELECT c0c_tab1 AS tabulation_code, c0c_dom AS mnemonic_type, c0c_fs AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_ber = ?";

        ResultSet mnemonicStringCheckResultSet  = null;
        PreparedStatement preparedStatement     = null;

        Boolean isMnemonicString = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicStringCheckSqlQuery);
            preparedStatement.setString(1, mnemonicName);
            mnemonicStringCheckResultSet    = preparedStatement.executeQuery();

            isMnemonicString = isMnemonicString(mnemonicStringCheckResultSet);

            preparedStatement.close();


        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo stringa) del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return isMnemonicString;

    }

    public boolean isMnemonicString(RpaMnemonic mnemonic) {

        // Costruisco ed eseguo la query
        String fullFieldName = mnemonic.getFullFieldName();

        String mnemonicStringCheckSqlQuery = "" +
                "SELECT c0c_tab1 AS tabulation_code, c0c_dom AS mnemonic_type, c0c_fs AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        ResultSet mnemonicStringCheckResultSet  = null;
        PreparedStatement preparedStatement     = null;

        Boolean isMnemonicString = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicStringCheckSqlQuery);
            preparedStatement.setString(1, fullFieldName);
            mnemonicStringCheckResultSet    = preparedStatement.executeQuery();

            isMnemonicString = isMnemonicString(mnemonicStringCheckResultSet);

            preparedStatement.close();


        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo stringa) per il campo: " + fullFieldName);
            System.err.println(exception);

        }

        return isMnemonicString;

    }

    private boolean isMnemonicString(ResultSet mnemonicStringCheckResultSet) {

        try {

            // Se il primo carattere del formato è "A"
            // e nel tipo del mnemonico ho un valore diverso da: "SN", "SNNN", "DATA_ELDA"
            // e nel campo del tabulato ho "null", allora ho una stringa
            if (mnemonicStringCheckResultSet.next()) {

                // Leggo i risultato
                String mnemonicFormatFullCodeString     = mnemonicStringCheckResultSet.getString("mnemonic_format");
                String mnemonicTypeString               = mnemonicStringCheckResultSet.getString("mnemonic_type");
                String mnemonicTabulationCodeString     = mnemonicStringCheckResultSet.getString("tabulation_code");

                mnemonicStringCheckResultSet.close();

                Character mnemonicFormatCodeCharacter   = mnemonicFormatFullCodeString.charAt(0);

                // Se ho un numero, esco
                if (mnemonicFormatCodeCharacter.equals('N') || mnemonicFormatCodeCharacter.equals('F')) {

                    return false;

                }

                // Se ho un tabulato, esco
                if (mnemonicFormatCodeCharacter.equals('A') && mnemonicTabulationCodeString != null && !mnemonicTabulationCodeString.isEmpty()) {

                    return false;

                }

                // Se il "mnemonic-type" è "null" e il carattere di formato è "A" e non ho codici tabulati, allora ho una stringa
                if (mnemonicFormatCodeCharacter.equals('A') && mnemonicTypeString == null && mnemonicTabulationCodeString == null) {

                    return true;

                }

                // Controllo se ho una data o un booleano

                boolean isMnemonicFormatCodeStringType  = mnemonicFormatCodeCharacter.equals(MNEMONIC_FORMAT_STRING);
                boolean isMnemonicTypeStringType        =
                        (!mnemonicTypeString.equals(MNEMONIC_TYPE_BOOLEAN_SN))
                                && (!mnemonicTypeString.equals(MNEMONIC_TYPE_BOOLEAN_SNNN))
                                && (!mnemonicTypeString.equals(MNEMONIC_TYPE_DATA));
                boolean isMnemonicTabulationCodeEmpty   = mnemonicTabulationCodeString == null || mnemonicTabulationCodeString.isEmpty();

                return isMnemonicFormatCodeStringType && isMnemonicTypeStringType && isMnemonicTabulationCodeEmpty;

            } else {

                mnemonicStringCheckResultSet.close();

                return false;

            }

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del tipo (per controllo stringa) del mnemonico");
            System.err.println(exception);

        }

        return false;

    }

    public int getMnemonicDateType(RpaMnemonic mnemonic) {

        String fullFieldName = mnemonic.getFullFieldName();

        // Costruisco ed eseguo la query
        String mnemonicDataCheckSqlQuery = "" +
                "SELECT c0c_dom AS mnemonic_type, c0c_mne_uni AS field_table " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        ResultSet           mnemonicDataCheckResultSet          = null;
        PreparedStatement   preparedStatement                   = null;
        ResultSetMetaData   mnemonicDataCheckResultSetMetaData  = null;

        Boolean isMnemonicDate = null;

        try {

            preparedStatement           = dbConnection.prepareStatement(mnemonicDataCheckSqlQuery);
            preparedStatement.setString(1, fullFieldName);
            mnemonicDataCheckResultSet  = preparedStatement.executeQuery();

            isMnemonicDate = isMnemonicDateBase(mnemonicDataCheckResultSet);

            if (isMnemonicDate) {

                // Recupero il tipo della colonna
                String fieldTableFull   = mnemonicDataCheckResultSet.getString("field_table");
                fieldTableFull          = fieldTableFull.replaceAll(FILTER_PREFIX_FIELD_REGEX, "");
                String fieldTable = fieldTableFull.split("\\.")[1];
                String fieldName = fieldTableFull.split("\\.")[0];

                preparedStatement.close();

                String mnemonicFieldTypeCheckSqlQuery = "" +
                        "SELECT " + fieldName + " " +
                        "FROM   " + fieldTable;

                preparedStatement = dbConnection.prepareStatement(mnemonicFieldTypeCheckSqlQuery);
                mnemonicDataCheckResultSetMetaData = preparedStatement.getMetaData();

                int fieldType = mnemonicDataCheckResultSetMetaData.getColumnType(1);
                preparedStatement.close();

                return fieldType;

            } else {

                preparedStatement.close();

                return -1;

            }

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo data) per il campo: " + fullFieldName);
            System.err.println(exception);

        }

        return -1;

    }

    public int getMnemonicDateType(String mnemonicName) {

        // Costruisco ed eseguo la query
        String mnemonicDataCheckSqlQuery = "" +
                "SELECT c0c_dom AS mnemonic_type, c0c_mne_uni AS field_table " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_ber = ?";

        ResultSet           mnemonicDataCheckResultSet          = null;
        PreparedStatement   preparedStatement                   = null;
        ResultSetMetaData   mnemonicDataCheckResultSetMetaData  = null;

        Boolean isMnemonicDate = null;
        try {

            // Controllo che il mnemonico sia dichiarato come data (DATA_ELDA)
            preparedStatement           = dbConnection.prepareStatement(mnemonicDataCheckSqlQuery);
            preparedStatement.setString(1, mnemonicName);
            mnemonicDataCheckResultSet  = preparedStatement.executeQuery();

            isMnemonicDate = isMnemonicDateBase(mnemonicDataCheckResultSet);

            if (isMnemonicDate) {

                // Recupero il tipo della colonna
                String fieldTableFull   = mnemonicDataCheckResultSet.getString("field_table");
                fieldTableFull          = fieldTableFull.replaceAll(FILTER_PREFIX_FIELD_REGEX, "");
                String fieldTable = fieldTableFull.split("\\.")[1];
                String fieldName = fieldTableFull.split("\\.")[0];

                preparedStatement.close();

                String mnemonicFieldTypeCheckSqlQuery = "" +
                        "SELECT " + fieldName + " " +
                        "FROM   " + fieldTable;

                preparedStatement = dbConnection.prepareStatement(mnemonicFieldTypeCheckSqlQuery);
                mnemonicDataCheckResultSetMetaData = preparedStatement.getMetaData();

                int fieldType = mnemonicDataCheckResultSetMetaData.getColumnType(1);

                preparedStatement.close();

            } else {

                preparedStatement.close();

                return -1;

            }

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo data) del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return -1;

    }

    private boolean isMnemonicDateBase(ResultSet mnemonicDataCheckResultSet) {

        try {

            // Se "DATA_ELDA" è presente, allora ho una data
            if (mnemonicDataCheckResultSet.next()) {

                String mnemonicTypeString = mnemonicDataCheckResultSet.getString("mnemonic_type");

                return mnemonicTypeString != null && !mnemonicTypeString.isEmpty() && mnemonicTypeString.equals(MNEMONIC_TYPE_DATA);

            } else {

                return false;

            }

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del tipo (per controllo data) del mnemonico");
            System.err.println(exception);

        }

        return false;

    }

    public boolean isMnemonicMoney(RpaMnemonic mnemonic) {

        String fullFieldName = mnemonic.getFullFieldName();

        // Costruisco ed eseguo la query
        String mnemonicNumberCheckSqlQuery = "" +
                "SELECT c0c_dom AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        ResultSet mnemonicNumberCheckResultSet  = null;
        PreparedStatement preparedStatement     = null;

        Boolean isMnemonicMoney = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicNumberCheckSqlQuery);
            preparedStatement.setString(1, fullFieldName);
            mnemonicNumberCheckResultSet    = preparedStatement.executeQuery();

            isMnemonicMoney = isMnemonicMoney(mnemonicNumberCheckResultSet);

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo numero) del mnemonico: " + fullFieldName);
            System.err.println(exception);

        }

        return isMnemonicMoney;

    }

    public boolean isMnemonicMoney(String mnemonicName) {

        // Costruisco ed eseguo la query
        String mnemonicNumberCheckSqlQuery = "" +
                "SELECT c0c_dom AS mnemonic_format " +
                "FROM   rpa_c0campi " +
                "WHERE  c0c_mne_uni LIKE ?";

        ResultSet mnemonicNumberCheckResultSet  = null;
        PreparedStatement preparedStatement     = null;

        Boolean isMnemonicMoney = null;

        try {

            preparedStatement               = dbConnection.prepareStatement(mnemonicNumberCheckSqlQuery);
            preparedStatement.setString(1, mnemonicName);
            mnemonicNumberCheckResultSet    = preparedStatement.executeQuery();

            isMnemonicMoney = isMnemonicMoney(mnemonicNumberCheckResultSet);

            preparedStatement.close();

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante il recupero del tipo (per controllo numero) del mnemonico: " + mnemonicName);
            System.err.println(exception);

        }

        return isMnemonicMoney;

    }

    private boolean isMnemonicMoney(ResultSet mnemonicMoneyCheckResultSet) {

        try {

            if (mnemonicMoneyCheckResultSet.next()) {

                String mnemonicType = mnemonicMoneyCheckResultSet.getString("mnemonic_format");

                return mnemonicType != null && !mnemonicType.isEmpty() && mnemonicType.equals(MNEMONIC_TYPE_MONEY);

            } else {

                return false;

            }

        } catch (SQLException exception) {

            System.err.println("[MnemonicManager]: Errore durante la lettura del tipo (per controllo money) del mnemonico");
            System.err.println(exception);

        }

        return false;

    }

}
