package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.io.IOException;
import java.util.HashMap;

public class RpaPrecompilerManager {

	// Nomi statici dei precompile
	public static String RTFFORMAT_PLUGIN 							= "rtfformat-precompile";
	public static String INCLUDEMOD_PLUGIN 							= "includemod-precompile";
	public static String INCLUDE_PLUGIN								= "include-precompile";
	public static String ENDCODE_PLUGIN								= "endcode-precompile";
	public static String SIGNATUREPARSE_PLUGIN						= "signature-parse";
	public static String INSTRUCTIONS_FINDER_PLUGIN					= "instructionsFinder-precompile";
	public static String LOOP_CHECK_PLUGIN							= "loopCheck-precompile";
	public static String BOOLEAN_TERM_STRING_PLUGIN					= "boolTermString-precompile";
	public static String BOOLEAN_CONDITION_SEPARATOR_SWAP_PLUGIN	= "boolSeparatorSwap-precompile";
	public static String MNEMONIC_CLEAN_SPACE_PLUGIN				= "mnemonicCleanSpace-precompile";

	private HashMap<String, RpaICompoPlugin>	plugins;
	private RpaMainCompositore					mainCompositore;

	public RpaPrecompilerManager(RpaMainCompositore mainCompositore) {

		this.mainCompositore = mainCompositore;

		plugins = new HashMap<String, RpaICompoPlugin>();

		plugins.put(INCLUDEMOD_PLUGIN, new RpaIncludemodPlugin(mainCompositore));
		plugins.put(INCLUDE_PLUGIN, new RpaIncludePlugin(mainCompositore));
		plugins.put(ENDCODE_PLUGIN, new RpaEndCodePlugin(mainCompositore));
		plugins.put(SIGNATUREPARSE_PLUGIN, new RpaSignatureParsePlugin(mainCompositore));
		plugins.put(INSTRUCTIONS_FINDER_PLUGIN, new RpaInstructionsFinderPlugin(mainCompositore));
		plugins.put(LOOP_CHECK_PLUGIN, new RpaLoopCheckPlugin(mainCompositore));
		plugins.put(BOOLEAN_TERM_STRING_PLUGIN, new RpaBooleanTermStringPlugin(mainCompositore));
		plugins.put(BOOLEAN_CONDITION_SEPARATOR_SWAP_PLUGIN, new RpaBooleanConditionSeparatorSwaperPlugin(mainCompositore));
		plugins.put(MNEMONIC_CLEAN_SPACE_PLUGIN, new RpaMnemonicCleanSpacePlugin(mainCompositore));

	}

	/**
	 * Dato il nome di un precompile, esso viene eseguito
	 */
	public void runPlugin(String pluginName) {

		if (plugins.containsKey(pluginName)) {

			try {

				plugins.get(pluginName).run();

			} catch (IOException e) {

				System.err.println("Si è verificato un errore durante l'esecuzione del plug-in " + //
						pluginName + "\n" + e.getMessage());

				mainCompositore.setExitCode(RpaMainCompositore.EXIT_CODE_ERROR);

			}

		}

	}

}