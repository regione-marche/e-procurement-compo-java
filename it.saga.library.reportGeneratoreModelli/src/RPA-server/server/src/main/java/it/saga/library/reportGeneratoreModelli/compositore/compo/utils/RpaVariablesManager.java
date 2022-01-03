package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;

import java.util.HashMap;
import java.util.Map;

import static it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaMnemonicVisitor.STR_CUSTOM_INDEX;
import static it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaMnemonicVisitor.TOT_CUSTOM_INDEX;

public class RpaVariablesManager {

	private Map<Integer, RpaValue<RpaAbstractMnemonicConstant>> TOTMap;
	private Map<Integer, RpaValue<RpaAbstractMnemonicConstant>> STRMap;

	public RpaVariablesManager(RpaMainCompositore mainCompositore) {

		TOTMap = new HashMap<Integer, RpaValue<RpaAbstractMnemonicConstant>>();
		STRMap = new HashMap<Integer, RpaValue<RpaAbstractMnemonicConstant>>();

		// Inizializzo le varibili più importanti
		setVariableValue(Type.TOT, 0, new RpaValue(null));
		setVariableValue(Type.STR,0, new RpaValue(null));
		setVariableValue(Type.TOT, TOT_CUSTOM_INDEX, new RpaValue(new RpaMnemonicTOT(mainCompositore, 0)));
		setVariableValue(Type.TOT, STR_CUSTOM_INDEX, new RpaValue(new RpaMnemonicTOT(mainCompositore, 0)));

	}

	/**
	 * Aggiunge una nuova variabile alla lista
	 *
	 * @param type  Tipo della variabile, Numerica o di Testo
	 * @param index  Nome o indice della variabile #TOT2#
	 * @param value Valore iniziale della variabile
	 * @return True se la variabile è stata aggiunta false altrimenti
	 */
	public boolean addVariable(Type type, int index, RpaValue<RpaAbstractMnemonicConstant> value) throws IllegalArgumentException {

		// Controllo che non esista già una variabile delle stesso tipo con lo stesso nome
		if (isExists(type, index)) {

			return false;

		}

		//Se la variabile non esiste la aggiungo alla lista
		switch (type) {

			case TOT:
				TOTMap.put(index, value);
				break;

			case STR:
				STRMap.put(index, value);
				break;

		}

		return true;

	}

	/**
	 * Verifica se una variabile esiste
	 *
	 * @param type Il tipo della variabile
	 * @param index Il nome della variabile
	 * @return True se la variabile esiste, false altrimenti
	 */
	public boolean isExists(Type type, int index) {

		switch (type) {

			case TOT:
				return TOTMap.containsKey(index);

			case STR:
				return STRMap.containsKey(index);

		}

		return false;

	}

	/**
	 * Rimuove una variabile dalla lista
	 *
	 * @param index
	 */
	public void removeVariable(Type type, int index) throws IllegalArgumentException {

		switch (type) {

			case TOT:
				TOTMap.remove(index);
				break;

			case STR:
				STRMap.remove(index);
				break;

		}

	}

	/**
	 * Restituisce il valore attuale di una variabile
	 *
	 * @param type Tipo della variabile, Numerica o di Testo
	 * @param index Nome o indice della variabile #TOT2#
	 * @return Il valore della variabile
	 */
	public RpaValue<RpaAbstractMnemonicConstant> getVariableValue(Type type, int index) {

		switch (type) {

			case TOT:
				return TOTMap.get(index);

			case STR:
				return STRMap.get(index);

		}

		return null;

	}

	/**
	 * Aggiorna il valore di una variabile
	 *
	 * @param type  Tipo della variabile, Numerica o di Testo
	 * @param index  Nome o indice della variabile #TOT2#
	 * @param value Nuovo valore della variabile
	 */
	public void setVariableValue(Type type, int index, RpaValue<RpaAbstractMnemonicConstant> value) throws IllegalArgumentException {

		if (!isExists(type, index)) {

			return;

		}

		switch (type) {

			case TOT:
				TOTMap.put(index, value);
				break;

			case STR:
				STRMap.put(index, value);
				break;

		}

	}

	public enum Type { TOT, STR }

}
