package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph;

import java.util.ArrayList;

/**
 * Rappresenta il collegamento tra un mnemonico "figlio" ed un mnemonico "padre"
 */
public class RpaMnemonicEdge {

    private String              foreignKey;
    private ArrayList<String>   foreignKeysFields;

    RpaMnemonicEdge(String foreignKey) {

        this.foreignKey = foreignKey;

    }

    /**
     * Ritorna la stringa originale che rappresenta la "foreign key"
     *
     * @return
     */
    public String getForeignKey() {

        return foreignKey;

    }

    /**
     * Ritorna tutti i campi che rappresentano la "foreign key"
     *
     * @return
     */
    public ArrayList<String> getForeignKeysFields() {

        return foreignKeysFields;

    }

    /**
     * Verifica se la "foreign key" è rappresentata da un singolo campo
     *
     * @return
     */
    public boolean hasForeignKeySingleField() {

        return foreignKeysFields.size() == 1;

    }

    public boolean equals(RpaMnemonicEdge mnemonicEdge) {

        return this.getForeignKey().equals(mnemonicEdge.getForeignKey());

    }

    public RpaMnemonicEdge clone() {

        return new RpaMnemonicEdge(foreignKey);

    }

}
