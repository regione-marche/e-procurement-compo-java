package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaNotExistValueNumberException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;

import java.util.InvalidPropertiesFormatException;

public class RpaFormatLInterval extends RpaFormat {

    private String  value;
    private String  formattedValue;
    private int     indexBlock;
    private int     sizeBlock;

    public RpaFormatLInterval(RpaMainCompositore mainCompositore, String value, int indexBlock, int sizeBlock) throws InvalidPropertiesFormatException {

        super(mainCompositore);

        if (value != null && sizeBlock > value.length()) {

            throw new InvalidPropertiesFormatException("La grandezza del blocco è maggiore della lunghezza della stringa");

        }

        this.value          = value;
        this.indexBlock     = indexBlock;
        this.sizeBlock      = sizeBlock;

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

        String code     = "[L(" + indexBlock + ":" + sizeBlock + ")]";
        String message  = "Il formato [L(nn:mm)] non ha un valore numerico";
        int context     = RpaComposerException.COMPILE_MESSAGE;

        throw new RpaNotExistValueNumberException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

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

        // ###      Controllo se il valore è vuoto  ###

        if (value == null || value.isEmpty()) {

            return;

        }

        // ###      Spezzo la stringa in blocchi    ###

        // Definisco gli indici dell'ultimo blocco
        int lastIndexStart  = 0;
        int lastIndexEnd    = 0;

        int currentCharIndex = 0;

        // Ciclo fino al blocco richiesto
        for (int i = 0; i < indexBlock && currentCharIndex < value.length(); i ++) {

            int     countBlockCharsRead         = 0;
            boolean isFirstInterruptionFound    = false;

            // Ciclo su ogni carattere della stringa
            while (currentCharIndex < value.length() && countBlockCharsRead < sizeBlock) {

                // Se ho un carattere diverso da spazio a cui segue uno spazio, ne salvo l'indice
                if (currentCharIndex == value.length() - 1) {

                    lastIndexEnd = currentCharIndex + 1;

                } else {

                    char currentChar    = value.charAt(currentCharIndex);
                    char nextChar       = value.charAt(currentCharIndex + 1);

                    if (currentChar != ' ' && nextChar == ' ') {

                        if (!isFirstInterruptionFound) {

                            lastIndexStart = lastIndexEnd;
                            isFirstInterruptionFound = true;

                        }
                        lastIndexEnd = currentCharIndex + 1;

                    }

                }

                ++ currentCharIndex;
                ++ countBlockCharsRead;

            }

            // Resetto l'indice del carattere corrente
            currentCharIndex = lastIndexEnd;

        }

        // ###      Salvo l'ultimo blocco           ###

        // Salvo la sottostringa compresa tra i due indici definiti in precedenza
        formattedValue = value.substring(lastIndexStart, lastIndexEnd);

    }

}
