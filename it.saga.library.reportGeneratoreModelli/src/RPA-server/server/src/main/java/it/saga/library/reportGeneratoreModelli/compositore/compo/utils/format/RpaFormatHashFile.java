package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.common.CmnFileUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaTextConversionFailException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RpaFormatHashFile extends RpaFormat {

    private String  value;
    private String  formattedValue;

    public RpaFormatHashFile(RpaMainCompositore mainCompositore, String value) {

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

        String code     = "{HASHFILE}";
        String message  = "Il formato {HASHFILE} non ha un valore numerico";
        int context     = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaTextConversionFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

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

        // Recupero il file
        File file = new File(value);

        if (!file.exists()) {

            formattedValue = "ERRORE: Il path del file '" + value + "' non è valido";
            return;

        }

        if (file.isDirectory()) {

            formattedValue = "ERRORE: Il path '" + value + "' porta ad una directory e non ad un file!";

        }

        // Eseguo la conversione
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(CmnFileUtils.readFromFile(file));

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {

                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);

            }

            formattedValue = hexString.toString();

        } catch (NoSuchAlgorithmException exception) {

            String code     = "{HASHFILE}";
            String message  = "Per la macchina corrente non esiste il sistema di codifica 'SHA-256': " + exception.getMessage();
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaTextConversionFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        } catch (IOException exception) {

            String code     = "{HASHFILE}";
            String message  = "Errore nella lettura del file '" + value + "': " + exception.getMessage();
            int context     = RpaComposerException.COMPILE_MESSAGE;

            throw new RpaTextConversionFailException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

    }

}
