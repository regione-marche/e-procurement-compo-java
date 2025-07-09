package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaTextConversionFailException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RpaFormatFindHash extends RpaFormat {

    private String  value;
    private String  formattedValue;

    public RpaFormatFindHash(RpaMainCompositore mainCompositore, String value) {

        super(mainCompositore);
        this.value = value;
        formatValue();

    }

    @Override
    public String getPrintedValue() {

        if (formattedValue == null) {

            return emptyPrintedValue();

        } else {

            return formattedValue;

        }

    }

    @Override
    public String getValueAssignment() {

        return formattedValue;

    }

    @Override
    public Number getValueNumber() {

        if (value == null) {

            return 0;

        }

        else if (RpaNumberUtils.isInteger(value)) {

            if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                return Integer.valueOf(RpaNumberUtils.integerWithoutDotZero(value));

            } else {

                return Integer.valueOf(value);

            }

        } else if (RpaNumberUtils.isDouble(value)) {

            return Double.valueOf(value);

        } else {

            return 0;

        }

    }

    @Override
    public String getComparisonValue() {

        return formattedValue;

    }

    @Override
    public String getFormattedValue() {

        return formattedValue;

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

    private void formatValue() {

        // Controllo se il valore è vuoto
        if (value == null || value.isEmpty()) {

            formattedValue = value;
            return;

        }

        // Recupero il valore numerico (corrispondente al pkid del documentale)
        Integer pkid = null;

        if (RpaNumberUtils.isInteger(value)) {

            if (RpaNumberUtils.isIntegerWithDotZero(value)) {

                pkid = Integer.valueOf(RpaNumberUtils.integerWithoutDotZero(value));

            } else {

                pkid = Integer.valueOf(value);

            }

        }

        if (pkid == null) {

            formattedValue = value;
            return;

        }

        // Eseguo la query sul documentale
        Connection connection = mainCompositore.getComposerConfiguration().getConn();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sqlQuery = "" +
                "SELECT documento.sha_256_digest AS sha256 " +
                "FROM   rep_documenti documento " +
                "WHERE  pkid = ? ";

        try {

            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, pkid);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                formattedValue = resultSet.getString("sha256");

            } else {

                formattedValue = "Nessun documento trovato (pkid: '" + value + "')";

            }

        } catch (SQLException exception) {

            String code     = "{FINDHASH}";
            String message  = "Errore del documentale con pkid '" + value + "': " + exception.getMessage();
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaTextConversionFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        if (formattedValue == null || formattedValue.isEmpty()) {

            formattedValue = "Il documento non ha uno SHA-256 (pkid: '" + value + "')";

        }

    }

}
