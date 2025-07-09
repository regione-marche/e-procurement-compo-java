package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicDateNow;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce tutti i mnemonici "speciali"
 * es: #INIZIORTF#, #FINERTF#, #DATAOGGI#, etc...
 */
public class RpaSpecialMnemonicManager {

    public static final String INIZIORTF    = "INIZIORTF";
    public static final String FINERTF      = "FINERTF";
    public static final String DATAOGGI     = "DATAOGGI";
    public static final String FINETESTO    = "FINETESTO";
    public static final String SIGNATURE    = "SIGNATURE";
    public static final String DEEJAY_FF    = "DEEJAY_FF";

    private RpaMainCompositore mainCompositore;
    private List<String> specialMnemonicList;

    public RpaSpecialMnemonicManager(RpaMainCompositore mainCompositore) {

        this.mainCompositore        = mainCompositore;
        this.specialMnemonicList    = new ArrayList<String>();

        this.specialMnemonicList.add(INIZIORTF);
        this.specialMnemonicList.add(FINERTF);
        this.specialMnemonicList.add(DATAOGGI);
        this.specialMnemonicList.add(FINETESTO);
        this.specialMnemonicList.add(SIGNATURE);
        this.specialMnemonicList.add(DEEJAY_FF);

    }

    public boolean isMnemonicSpecial(String mnemonicName) {

        mnemonicName = mnemonicName.replaceAll("#", "").toUpperCase();

        return specialMnemonicList.contains(mnemonicName);

    }

    public boolean isMnemonicFormattedSpecial(String mnemonicName) {

        mnemonicName = mnemonicName.replaceAll("#", "").toUpperCase();
        mnemonicName = mnemonicName.replaceAll("[^A-Za-z0-9_]+", "");

        return specialMnemonicList.contains(mnemonicName);

    }

    public RpaAbstractMnemonic getMnemonicFormatted(String mnemonicName) {

        mnemonicName = mnemonicName.replaceAll("[^A-Za-z0-9_]+", "");

        return getMnemonic(mnemonicName);

    }

    public RpaAbstractMnemonic getMnemonic(String mnemonicName) {

        System.err.println("Implementare le formattazioni per i mnemonici speciali");

        if (!isMnemonicSpecial(mnemonicName)) {

            return null;

        }

        mnemonicName = mnemonicName.replaceAll("#", "").toUpperCase();

        if (mnemonicName.equals(INIZIORTF)) {

            return null;

        }

        else if (mnemonicName.equals(FINERTF)) {

            return null;

        }

        else if (mnemonicName.equals(DATAOGGI)) {

            return new RpaMnemonicDateNow(mainCompositore);

        }

        else if (mnemonicName.equals(FINETESTO)) {

            return null;

        }

        else if (mnemonicName.equals(SIGNATURE)) {

            return new RpaMnemonicConstant(mainCompositore, "#signature#");

        }

        else if (mnemonicName.equals(DEEJAY_FF)) {

            String result = "Spengo A RADIO?!";

            try {

                if (mainCompositore.getLastRunNodeRead() != null) {

                    Run runNode = mainCompositore.getLastRunNodeRead();

                    if (runNode.getFont() != null && runNode.getFont().getSize() != 0) {

                        double fontSize = runNode.getFont().getSize();

                        if (fontSize < 8) {

                            result += " (VAI VAI! Ma ricompila il modello con un font-size piu grande!!!)";

                        }

                        else if (8 <= fontSize && fontSize <= 14) {

                            result += " (Prima invita Flavio a bere qualcosa e poi fagli spegnere la radio!)";

                        }

                        else {

                            result += " (VAIII! TACCA LA FESTA CON DEEJAY FLAVIO!!)";

                        }

                    }

                }

            } catch (Exception exception) {}

            return new RpaMnemonicConstant(mainCompositore, result);

        }

        else {

            return mainCompositore.getMnemonicEmpty();

        }

    }

}
