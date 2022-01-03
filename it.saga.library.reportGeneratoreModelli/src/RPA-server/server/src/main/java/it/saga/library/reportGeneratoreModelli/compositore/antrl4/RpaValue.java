package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

import java.math.BigDecimal;

/**
 * Valore generico per gestire i risultati della composizione
 *
 * @param <T>
 *     		Tipo della istanza
 */
public class RpaValue<T extends Object> implements Comparable<RpaValue> {

	// Value.NULL
	public static final RpaValue NULL = new RpaValue();

	// Value.SKIP
	public static final RpaValue SKIP = new RpaValue(true);

	protected T value;

	private boolean skip;
	private Exception exception;
	private boolean isExtendCarriageReturn;

	private RpaValue() {
	}

	private RpaValue(boolean isSkip) {
		this(null);
		setIsSkip(isSkip);
		this.isExtendCarriageReturn = false;
	}

	public RpaValue(T value, boolean isExtendCarriageReturn) {

		this.value					= value;
		this.isExtendCarriageReturn = isExtendCarriageReturn;

	}

	public RpaValue(T value) {
		this.value = value;
	}

	/*
	 * Metodi get e set
	 */
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setIsSkip(boolean skip) {
		this.skip = skip;
	}

	public boolean isException() {
		return exception == null;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public boolean isExtendCarriageReturn() { return isExtendCarriageReturn; }

	public void setExtendCarriageReturn(boolean extendCarriageReturn) { isExtendCarriageReturn = extendCarriageReturn; }

	@Override
	public int compareTo(RpaValue o) {
		if (value instanceof String) {
			if (o.value instanceof String) {
				return ((String) value).compareTo((String) o.value);
			} else if (o.value instanceof Integer) {
				if (((String) value).matches("\\d+")) {
					return Integer.valueOf((String) value).compareTo((Integer) o.value);
				}
			} else if (o.value instanceof Double) {
				if (((String) value).matches("\\d+\\.?\\d+")) {
					return Double.valueOf((String) value).compareTo((Double) o.value);
				}
			}
			return -1;
		}
		if (value instanceof Integer) {
			if (o.value instanceof Integer) {
				return ((Integer) value).compareTo((Integer) o.value);
			} else if (o.value instanceof Double) {
				return (BigDecimal.valueOf((Integer) value)).compareTo(BigDecimal.valueOf((Double) o.value));
			} else if (o.value instanceof String) {
				if (((String) o.value).matches("\\d+")) {
					return Integer.valueOf((String) o.value).compareTo((Integer) value);
				}
			}
			return -1;
		}
		if (value instanceof Double) {
			if (o.value instanceof Double) {
				return ((Double) value).compareTo((Double) o.value);
			} else if (o.value instanceof Integer) {
				return (BigDecimal.valueOf((Double) value)).compareTo(BigDecimal.valueOf((Integer) o.value));
			} else if (o.value instanceof String) {
				if (((String) o.value).matches("\\d+\\.?\\d+")) {
					return Double.valueOf((String) o.value).compareTo((Double) value);
				}
			}
			return -1;
		}
		return -1;
	}

	@Override
	public String toString() {
		return "Value{" + "value=" + value + '}';
	}
}
