package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaMnemonicVisitor;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;

import java.util.ArrayList;

public class RpaGenericLoopValue<T> extends RpaValue<T> {

	private RpaMainCompositore mainCompositore;

	private String mnemonic						= null;
	private String tableName					= null;
	private ArrayList<String> mnemonicValues	= null;
	private String indexName					= null;
	private Integer lowerLimit					= null;
	private Integer upperLimit					= null;
	private Integer step						= null;
	private Integer currentValue				= null;
	private boolean isMnemonicEntityFound		= false;

	public RpaGenericLoopValue(T value, RpaMainCompositore mainCompositore) {

		super(value);
		this.mainCompositore = mainCompositore;
		setIsSkip(true);
		updateTableName();

	}

    /**
     * Controlla se il mnemonico passato è della stessa entità del loop
     * @param mnemonicName: Deve essere formattato come solo nome (del mnemonico)
     * @return
     */
	public boolean isMnemonicMatchEntity(String mnemonicName) {

		// Verifico se il mnemonico è un TOT o un STR o l'indice di un loop
		boolean isMnemonicTOT = RpaMnemonicVisitor.checkIfMnemonicTOT("#" + mnemonicName + "#");
		boolean isMnemonicSTR = RpaMnemonicVisitor.checkIfMnemonicSTR("#" + mnemonicName + "#");

		// Verifico se il loop è associato ad un mnemonico
		if (mnemonic != null && !mnemonic.isEmpty() && !isMnemonicTOT && !isMnemonicSTR) {

			try {

				// Recupero l'entità del mnemonico
				RpaMnemonicManager	mnemonicManager	= mainCompositore.getMnemonicManager();
				String				entityName		= mnemonicManager.translateMnemonicName(mnemonicName);
				String				tableName		= entityName.split("\\.")[1];

				return this.tableName.equals(tableName);

			} catch (NullPointerException exception) {

				throw exception;

			}

		}

		else {

			return false;

		}

	}

	public Integer getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(Integer lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public Integer getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(Integer upperLimit) {
		this.upperLimit = upperLimit;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public Integer getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Integer currentValue) {
		this.currentValue = currentValue;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public ArrayList<String> getMnemonicValues() {
		return mnemonicValues;
	}

	public boolean isMnemonicEntityFound() { return isMnemonicEntityFound; }

	public void setIsMnemonicEntityFound(boolean isMnemonicEntityFound) { this.isMnemonicEntityFound = isMnemonicEntityFound; }

	public void setMnemonic(String mnemonic) {

		this.mnemonic = mnemonic;
		updateTableName();

	}

	public String getCurrentMnemonicValue() {

		return mnemonicValues.get(currentValue - 1);

	}

	public void setMnemonicValues(ArrayList<String> mnemonicValues) {
		this.mnemonicValues = mnemonicValues;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		RpaGenericLoopValue<T> cloneObject = new RpaGenericLoopValue<T>(value, mainCompositore);
		cloneObject.setCurrentValue(currentValue);
		cloneObject.setLowerLimit(lowerLimit);
		cloneObject.setMnemonic(mnemonic);
		cloneObject.setMnemonicValues(mnemonicValues);
		cloneObject.setIndexName(indexName);
		cloneObject.setException(getException());
		cloneObject.setIsSkip(isSkip());
		cloneObject.setStep(step);
		cloneObject.setUpperLimit(upperLimit);
		return cloneObject;
	}

	private void updateTableName() {

		// Recupero la tabella associata al mnemonico
		if (mnemonic != null && !mnemonic.isEmpty()) {

			// MnemonicManager	mnemonicManager	= MnemonicManager.getMnemonicManager();
			RpaMnemonicManager mnemonicManager	= mainCompositore.getMnemonicManager();
			String			entityName		= mnemonicManager.translateMnemonicName(mnemonic);
			this.tableName					= entityName.split("\\.")[1];

		}

	}

}
