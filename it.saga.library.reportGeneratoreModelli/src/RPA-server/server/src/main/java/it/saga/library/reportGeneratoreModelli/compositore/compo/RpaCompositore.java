package it.saga.library.reportGeneratoreModelli.compositore.compo;

import com.aspose.words.Document;
import com.aspose.words.LoadFormat;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInternalException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.precompile.RpaPrecompilerManager;

import static it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore.IS_SAVE_NEW_DOCUMENT_SYNTAX;

public class RpaCompositore {

	private static final String REMOVE_DOT_SUFFIX_REGEX = "\\..*";

	private RpaComposerConfiguration composerConfiguration;
	private RpaMainCompositore mainCompositore;

	public RpaCompositore(RpaMainCompositore mainCompositore) {

		this.mainCompositore = mainCompositore;

	}

	public void eseguiCompositore(RpaComposerConfiguration composerConfiguration) {

		this.composerConfiguration = composerConfiguration;
		parseAspose();

	}

	private void parseAspose() {

		// PrecompilerManager.getPluginManager().runPlugin(PrecompilerManager.RTFFORMATPLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.INCLUDEMOD_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.INCLUDE_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.ENDCODE_PLUGIN);
		// mainCompositore.getPrecompilerManager().runPlugin(PrecompilerManager.SIGNATUREPARSE_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.INSTRUCTIONS_FINDER_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.LOOP_CHECK_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.BOOLEAN_TERM_STRING_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.BOOLEAN_CONDITION_SEPARATOR_SWAP_PLUGIN);
		mainCompositore.getPrecompilerManager().runPlugin(RpaPrecompilerManager.MNEMONIC_CLEAN_SPACE_PLUGIN);

		// DEBUG: Salvo il file
		if (IS_SAVE_NEW_DOCUMENT_SYNTAX) {

			try {

				int documentFormatCode = composerConfiguration.getInputDocument().getOriginalLoadFormat();
				String documentFormat = LoadFormat.getName(documentFormatCode);
				documentFormat = documentFormat.equals("TEXT") ? "txt" : documentFormat;
				String pathFileNameToSave = composerConfiguration.getOutputModel().getPath();
				/*
				pathFileNameToSave			= pathFileNameToSave.replaceAll(REMOVE_DOT_SUFFIX_REGEX, "");
				pathFileNameToSave			= pathFileNameToSave + "_new_syntax." + documentFormat;
				*/
				pathFileNameToSave = "./compo_new_syntax." + documentFormat;

				composerConfiguration.getInputDocument().save(pathFileNameToSave);

			} catch (Exception exception) {

				exception.printStackTrace();

			}

		}

		// Prelevo il documento aspose dalla configurazione
		Document		doc			= composerConfiguration.getInputDocument();
		RpaInterprete	interprete	= new RpaInterprete(mainCompositore);
		try {

			interprete.parse(doc);
			interprete.save(composerConfiguration.getOutputModel());

		} catch (Exception exception) {

			System.err.println("Errore durante la composizione del documento");
			exception.printStackTrace();

			if (exception instanceof RpaComposerException) {

				throw (RpaComposerException) exception;

			} else {

				if (mainCompositore.getExitCode() == RpaMainCompositore.EXIT_CODE_OK) {

					mainCompositore.setExitCode(RpaMainCompositore.EXIT_CODE_ERROR);

				}

				int context = RpaComposerException.COMPILE_MESSAGE;
				throw new RpaInternalException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, exception);

			}

		}

	}

}
