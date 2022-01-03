package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

public class RpaNextValue<T> extends RpaValue<T> {

	private String mnemonic = null;

	public RpaNextValue(T value) {

		super(value);

		setIsSkip(true);

	}

	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

}
