package it.saga.library.reportGeneratoreModelli.compositore.compo.style;

/**
 * Classe wrapper di tutte le direttive di stile
 */
public class RpaStyleManager {

	public static final int		DEFAULT_PRECISION			= 5;
	public static final char	DEFAULT_DECIMAL_SEPARATOR	= ',';
	public static final char	DEFAULT_INTEGER_SEPARATOR	= '.';

	// [PRECISION]
	private int precision;

	// [TRATON]/[TRATOFF]
	private boolean isStrokeActive;

	// [DOMON]/[DOMOFF]
	private boolean isDomainActive;

	// [EUROON]/[EUROOFF]
	private boolean isEuroFormatActive;

	// [PROON]/[PROOFF]
	private boolean isTableFormatActive;

	// [IMPON]/[IMPOFF]
	private boolean isLayoutActive;

	// [RTLON]/[RTLOFF]
	private boolean isRTLFormatActive;

	// DECIMALE
	private String decimalSeparator;

	// MIGLIAIA
	private String integerSeparator;

	public RpaStyleManager() {

		this.precision				= DEFAULT_PRECISION;
		this.decimalSeparator		= String.valueOf(DEFAULT_DECIMAL_SEPARATOR);
		this.integerSeparator		= String.valueOf(DEFAULT_INTEGER_SEPARATOR);
		this.isStrokeActive			= true;
		this.isDomainActive			= false;
		this.isEuroFormatActive		= false;
		this.isTableFormatActive	= false;
		this.isLayoutActive			= false;
		this.isRTLFormatActive		= false;

	}

	public String formatDecimal(String decimal) {

		String	newDecimal				= "";
		boolean	isDecimalSeparatorFound	= false;

		for (int i = decimal.length() - 1; i >= 0; i --) {

			char newChar;
			char currentChar = decimal.charAt(i);

			if (String.valueOf(currentChar).matches("[^0-9 *-]")) {

				// Ho trovato il separatore dei decimali
				if (!isDecimalSeparatorFound) {

					newChar = decimalSeparator.charAt(0);
					isDecimalSeparatorFound = true;

				}

				// Ho trovato il separatore delle centinaia
				else {

					newChar = integerSeparator.charAt(0);

				}

			}

			// Ho trovato una cifra numerica (o un carattere di formattazione da lasciare)
			else {

				newChar = currentChar;

			}

			newDecimal = newChar + newDecimal;

		}

		return newDecimal;

	}

	public void updateDecimalCharacterSeparators(String value) {

		if (value == null || value.isEmpty()) {

			return;

		} else {

			decimalSeparator = String.valueOf(value.charAt(0));

		}

		if (value.length() > 1) {

			integerSeparator = String.valueOf(value.charAt(1));

		}

	}

	public void updateDecimalCharacterSeparators(Character decimalCharacter, Character integerCharacter) {

		if (decimalCharacter != null) {

			decimalSeparator = String.valueOf(decimalCharacter);

		}

		if (integerCharacter != null) {

			integerSeparator = String.valueOf(integerCharacter);

		}

	}

	/**
	 * Ritorna la direttiva [PRECISION]
	 *
	 * @return L'oggetto 'precision'
	 */
	public int getPrecision() {

		return precision;

	}

	public String getDecimalSeparator() {

		return decimalSeparator;

	}

	public String getIntegerSeparator() {

		return integerSeparator;

	}

	/**
	 * Ritorna la direttiva [TRATON]/[TRATOFF]
	 *
	 * @return L'oggetto 'isStrokeActive'
	 */
	public boolean isStrokeActive() {

		return isStrokeActive;

	}

	/**
	 * Ritorna la direttiva [DOMON]/[DOMOFF]
	 *
	 * @return L'oggetto 'isDomainActive'
	 */
	public boolean isDomainActive() {

		return isDomainActive;

	}

	/**
	 * Ritorna la direttiva [EUROON]/[EUROOFF]
	 *
	 * @return L'oggetto 'isEuroFormatActive'
	 */
	public boolean isEuroFormatActive() {

		return isEuroFormatActive;

	}

	/**
	 * Ritorna la direttiva [PROON]/[PROOFF]
	 *
	 * @return L'oggetto 'isTableFormatActive'
	 */
	public boolean isTableFormatActive() {

		return isTableFormatActive;

	}

	/**
	 * Ritorna la direttiva [IMPON]/[IMPOFF]
	 *
	 * @return L'oggetto 'isLayoutActive'
	 */
	public boolean isLayoutActive() {

		return isLayoutActive;

	}

	/**
	 * Ritorna la direttiva [RTLON]/[RTLOFF]
	 *
	 * @return L'oggetto 'isRTLFormatActive'
	 */
	public boolean isRTLFormatActive() {

		return isRTLFormatActive;

	}

	public void setPrecision(int precision) {

		this.precision = precision;

	}

	public void setIsStrokeActive(boolean strokeActive) {

		isStrokeActive = strokeActive;

	}

	public void setIsDomainActive(boolean domainActive) {

		isDomainActive = domainActive;

	}

	public void setEuroFormatActive(boolean euroFormatActive) {

		isEuroFormatActive = euroFormatActive;
	}

	public void setIsTableFormatActive(boolean tableFormatActive) {

		isTableFormatActive = tableFormatActive;

	}

	public void setLayoutActive(boolean layoutActive) {

		isLayoutActive = layoutActive;

	}

	public void setRTLFormatActive(boolean RTLFormatActive) {

		isRTLFormatActive = RTLFormatActive;

	}

	public void setDecimalSeparator(String decimalSeparator) {

		this.decimalSeparator = decimalSeparator;

	}

	public void setIntegerSeparator(String integerSeparator) {

		this.integerSeparator = integerSeparator;

	}

}
