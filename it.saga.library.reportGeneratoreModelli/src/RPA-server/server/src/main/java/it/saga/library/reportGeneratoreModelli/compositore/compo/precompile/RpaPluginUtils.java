package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RpaPluginUtils {

	public static final String INIZIO_RTF_CODE	= "#INIZIORTF#";
	public static final String FINE_RTF_CODE	= "#FINERTF#";

	/**
	 * Controlla se un testo arrivato come argomento è il contenuto di un file rtf valido
	 *
	 * @param text il testo del rtf
	 * @return true se il file è un tipo rtf valido, false altrimenti
	 */
	public static boolean checkRtfFormat(final String text) {
		boolean isRtf = false;
		if (text.startsWith("{\\rtf")) {
			int inizioRtf = StringUtils.countMatches(text, "#INIZIORTF#");
			int fineRtf = StringUtils.countMatches(text, "#FINERTF#");

			// Se ho trovato una e solo una occorrenza di #INIZIORTF# e di #FINERTF# allora il formato è corretto
			if (inizioRtf == 1 && fineRtf == 1) {
				if (StringUtils.indexOf(text, "#INIZIORTF#") < StringUtils.indexOf(text, "#FINERTF#")) {
					isRtf = true;
				}
			} else {
				System.err.println("Attenzione, errore nella sintassi: i tag " +
						"#INIZIORTF# e #FINERTF# sono inseriti in maniera non corretta");
			}
		}
		return isRtf;
	}

	/**
	 * Dato il contenuto di un file rtf restituisce solo il testo
	 * compreso tra i tag #INIZIORTF# e #FINERTF#
	 *
	 * @param text
	 * @return
	 */
	public static String getRtfValidText(final String text) {
		int start = StringUtils.indexOf(text, "#INIZIORTF#") + "#INIZIORTF#".length();
		int end = StringUtils.indexOf(text, "#FINERTF#");

		String result = "";
		if (start != StringUtils.INDEX_NOT_FOUND && end != StringUtils.INDEX_NOT_FOUND) {
			result = StringUtils.substring(text, start, end);
		}

		return result;
	}

	/**
	 * Dato il contenuto di un file rtf cerca e gestisce l'eventuale mappa di caratteri a
	 * fine testo
	 *
	 * @param text Il testo grezzo del documento rtf
	 * @return Il testo rtf al netto della mappa di fine testo
	 */
	public static String checkFinetestoReferences(final String text) {
		String result = text;

		int occurrences = StringUtils.countMatches(text, "#FINETESTO#");
		if (occurrences == 1) {
			int start = StringUtils.indexOf(text, "#FINETESTO#");
			String tmp = StringUtils.substring(text, start);

			HashMap<Integer, String> endTextSymbolMap = new HashMap<Integer, String>();
			Matcher matcher = Pattern.compile("(\\d+)\\:(.*)").matcher(tmp);
			while (matcher.find()) {
				Integer kay = Integer.valueOf(matcher.group(1));
				String value = matcher.group(2);
				endTextSymbolMap.put(kay, value);
			}

			// Elimino la mappa di fine testo
			StringBuilder builder = new StringBuilder(text);
			builder.replace(builder.indexOf("#FINETESTO#"), builder.length(), "");

			matcher = Pattern.compile("#(?:>|!|C)(\\d+)#").matcher(builder.toString());
			while (matcher.find()) {
				if (endTextSymbolMap.containsKey(Integer.valueOf(matcher.group(1)))) {
					String constants = endTextSymbolMap.get(Integer.valueOf(matcher.group(1)));
					builder.replace(builder.indexOf(matcher.group()), //
							builder.indexOf(matcher.group()) + matcher.group().length(), //
							constants);
				}
			}

			result = builder.toString();

		} else if (occurrences >= 2) {
			System.err.println("Errore nella sintassi, il tag #FINETESTO# può comparire solo una volta.");
		}

		return result;
	}
}
