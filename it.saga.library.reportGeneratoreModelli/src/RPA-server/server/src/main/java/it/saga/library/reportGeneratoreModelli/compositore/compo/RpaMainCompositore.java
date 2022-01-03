// package it.maggioli.compo;
package it.saga.library.reportGeneratoreModelli.compositore.compo;

import com.aspose.words.BuiltInDocumentProperties;
import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaCompoUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.db.RpaMnemonicDescriptionList;
import it.saga.library.reportGeneratoreModelli.compositore.compo.db.RpaUserConnection;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaCompoException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaDocumentNotFoundException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaEntityDeclarationException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.gui.RpaCompositionManagement;
import it.saga.library.reportGeneratoreModelli.compositore.compo.precompile.RpaPrecompilerManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDBUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDataConversionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDebugMessages;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDocAsposeSetup;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaLoopInformationManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicDataFunctionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaParametersManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaRegisterManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaVariablesManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaWarningMessages;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.sqlManager.RpaSQLManager;
import it.saga.library.reportGeneratoreModelli.compositore.interfaces.RpaImportExternalImageI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaMainCompositore {

	public static final boolean IS_AUTO_OPEN_RESULT			= true;
	public static final boolean IS_SAVE_NEW_DOCUMENT_SYNTAX	= false;

	public static final int EXIT_CODE_OK		= 1;
	public static final int EXIT_CODE_ERROR_DB	= -1;
	public static final int EXIT_CODE_ERROR		= -2;

	private int		exitCode;
	private boolean isRunning;

	private String[] args;
	private RpaComposerStartConfiguration	composerStartConfiguration;
	private List<Node>						nodesBreakLoopToDelete;
	private Run								lastRunNodeRead;

	private RpaCompositionManagement			compositionManagement;
	private RpaComposerConfiguration			composerConfiguration;
	private RpaMnemonicDescriptionList			mnemonicDescriptionList;
	private RpaMnemonicManager					mnemonicManager;
	private RpaPrecompilerManager				precompilerManager;
	private RpaVariablesManager					variablesManager;
	private RpaStyleManager						styleManager;
	private RpaLoopInformationManager			loopInformationManager;
	private RpaDataConversionContainer			dataConversionContainer;
	private RpaMnemonicDataFunctionContainer	mnemonicDataFunctionContainer;
	private RpaSQLManager						sqlManager;
	private RpaParametersManager				parametersManager;
	private RpaRegisterManager					registerManager;
	private RpaANTLRErrorListener				antlrErrorListener;
	private RpaWarningMessages					warningMessages;
	private RpaDebugMessages					debugMessages;
	private RpaMnemonicEmpty					mnemonicEmpty;
	private RpaImportExternalImageI				importExternalImage;

	public RpaMainCompositore(String[] args) {

		this.args					= args;
		this.nodesBreakLoopToDelete	= new ArrayList<Node>();
		this.mnemonicEmpty			= new RpaMnemonicEmpty(this);

	}

	public RpaMainCompositore(RpaComposerStartConfiguration composerStartConfiguration) {

		this.composerStartConfiguration	= composerStartConfiguration;
		this.nodesBreakLoopToDelete		= new ArrayList<Node>();
		this.mnemonicEmpty				= new RpaMnemonicEmpty(this);

	}

	public void run() throws RpaComposerException {

		if (isRunning) {

			return;

		} else {

			isRunning = true;

		}

		exitCode = EXIT_CODE_OK;

		if (args == null) {

			runFromApplication();

		} else {

			runFromCommandLine();

		}

	}

	private void runFromCommandLine() {

		// bypassAspose();
		initContent();

		CommandLine line = parseArguments(args);

		composerConfiguration.setWorkDir(new File(System.getProperty("user.dir")));

		if(line.hasOption("u")) {

			RpaUserConnection userConnection = RpaUserConnection.getInstance(line.getOptionValue("u"));

			if(userConnection != null) {

				composerConfiguration.setConn(RpaDBUtils.prepareConnection(userConnection));

			} else {

				System.err.println("Configurazione insufficente, impossibile stabilire una connessione con il database specificato");

			}

		} else {

			composerConfiguration.setConn(RpaDBUtils.sicraweb());

		}

		System.out.println("[Main] Connessione al database:");
		System.out.println("[Main] " + composerConfiguration.getConn());

		// Se non ho una conessione al database, esco con un messaggio di errore
		if (composerConfiguration.getConn() == null) {

			System.err.println("[Main] Nessuna connessione al database trovata");
			System.exit(EXIT_CODE_ERROR_DB);

		}

		if (line.hasOption("o")) {

			File inputModel = new File(line.getOptionValue("o"));
			if (inputModel.exists() && inputModel.canRead()) {

				composerConfiguration.setInputModel(inputModel);

			} else {

				System.err.println(new RpaCompoException("Il file di modello indicato come input: " + inputModel.getPath() + //
						" non esiste o è protetto", new IOException()));
				System.exit(EXIT_CODE_ERROR);

			}

		} else {

			System.err.println("Impossibile proseguire --> Nessun modello di input è stato specificato come parametro");
			return;

		}

		// Se presente, verifica se il documento è già stato composto
		if (line.hasOption("q")) {

			BuiltInDocumentProperties properties = composerConfiguration.getInputDocument().getBuiltInDocumentProperties();


			/** Se i metadati corrispondono e quelli impostati dopo la composizione
			 * e se la data di creazione è uguale a quella di ultima modifica allora il
			 * documento è già stato composto e non sono necessarie ulteriori modifiche.*/

			if(((	properties.getNameOfApplication().equals(RpaCompoUtils.APP_NAME) ||
					properties.getCompany().equals(RpaCompoUtils.MAGGIOLI_SPA)) &&
					properties.getCreatedTime().equals(properties.getLastSavedTime()	))) {

				System.err.println(new RpaCompoException("Il file di modello indicato come input è già un modello composto", //
						new IllegalArgumentException()));
				System.exit(EXIT_CODE_ERROR);

			}

		}

		if (line.hasOption("e")) {

			String param = line.getOptionValue("e");
			Matcher matcher = Pattern.compile("^(([^:]+)\\:([^:=]+)\\=([^:=;]+))(;([^;:=]+\\=[^;:=]+))*$").matcher(param);

			if (matcher.find()) {

				List<String[]> identities = new ArrayList<String[]>();

				String[] identity = new String[] { matcher.group(2), matcher.group(3), matcher.group(4) };
				identities.add(identity);

				if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {

					Matcher matcherExtraParameter = Pattern.compile(";([^;:=]+)\\=([^;:=]+)").matcher(matcher.group(5));

					while (matcherExtraParameter.find()) {

						String[] extraIdentity = new String[] { matcherExtraParameter.group(1), matcherExtraParameter.group(2) };
						identities.add(extraIdentity);

					}

				}

				composerConfiguration.setIdentity(identities);

			} else {

				System.err.println("L'entità passata come parametro non sembra essere valida");
				System.exit(EXIT_CODE_ERROR);

			}

		} else {

			System.err.println("Impossibile proseguire --> Nessuna entità di base è stata specificata come parametro");
			return;

		}

		if(line.hasOption("f")) {

			String path = line.getOptionValue("f");
			File out = new File(path);

			if(out.exists()) {

				out.delete();

			}

			composerConfiguration.setOutputModel(out);

		} else {

			// Recupero l'estensione del file
			Matcher fileExtensionMatcher = Pattern.compile(".*\\.(.+)$").matcher(line.getOptionValue("o"));
			fileExtensionMatcher.find();
			String fileExtension = fileExtensionMatcher.group(1);

			composerConfiguration.setOutputModel(new File(composerConfiguration.getWorkDir().getPath() + "\\new." + fileExtension));

		}

		if (line.hasOption("p")) {

			// Recupero la stringa di dichiarazione
			String registersParameter = line.getOptionValue("p");

			// Aggiorno l'elenco dei registri
			registerManager.updateRegisters(registersParameter);

			// Recupero il dominio dell'entità (es: "ape" o "rpa") e l'id di sessione
			composerConfiguration.setExtraOptions(registerManager);

		}

		if (line.hasOption('t')) {

			String dbTypeString = line.getOptionValue('t');
			composerConfiguration.setDBType(dbTypeString);

		}

		if (line.hasOption('d')) {

			String debugLevel = line.getOptionValue('d');
			composerConfiguration.setDebugLevel(debugLevel);

		}

		if (line.hasOption('n')) {

			styleManager.updateDecimalCharacterSeparators(line.getOptionValue('n'));

		}

		if (line.hasOption('l')) {

			composerConfiguration.setLimitReadData(Long.valueOf(line.getOptionValue('l')));

		}

		if (line.hasOption('a')) {

			composerConfiguration.setApplicationStart(line.getOptionValue('a'));

		}

		// Inizializzo i parametri globali
		composerConfiguration.setIsSystemPrintActive(true);

		mnemonicDescriptionList = new RpaMnemonicDescriptionList(this);
		mnemonicManager.init(composerConfiguration.getConn());

		// Se ho un id sessione, ne recupero i codici associati
		if (composerConfiguration.getSessionParametersId() != null) {

			parametersManager.init(composerConfiguration.getSessionParametersId());

		}

		printStartComposer();

		RpaCompositore compositore = new RpaCompositore(this);
		compositore.eseguiCompositore(composerConfiguration);

		if (line.hasOption("r")) {

			composerConfiguration.getOutputModel().delete();

		}

		// Stampo il grafo dei mnemonici in graphviz (Richiamare quello dei "DocumentScope")
		// MnemonicManager.getMnemonicManager().saveMnemonicGraphGraphviz(".", true);

		// Apro il file con l'applicativo del sistema
		if (IS_AUTO_OPEN_RESULT) {

			try {

				Desktop.getDesktop().open(composerConfiguration.getOutputModel());

			} catch (IOException exception) {

				exception.printStackTrace();

			}

		}

		printEndComposer();

		// Chiudo la connessione al DB
		RpaDBUtils.close(composerConfiguration.getConn());

		if (compositionManagement != null) { // <-- Elimino la SystemTray icon se presente e termino il programma
			compositionManagement.getSystemTray().remove(compositionManagement.getTrayIcon());
		}

	}

	private static CommandLine parseArguments(String[] args) {

		Options options = new Options();

		Option option = new Option("u", true, "Stringa di connessione al database");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("o", true, "Path del documento di input");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("q", false, "Verifica se il documento è già compilato");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("e", true, "Entità base per la compilazione (ES: APE_CONCES:PKID=24361)");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("f", true, "Path del documento di output");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("p", true, "Parametri opzionali (ES: 87=ape;80=48831)");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("r", false, "Eliminazione file composto");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("h", false, "Visualizza questa guida");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("t", true, "Definisce il tipo di database usato");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("d", true, "Definisce il livello di debug");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("n", true, "Definisce la formattazione di decimali e centinaia");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("l", true, "Limite di lettura dei dati");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("a", true, "Applicativo di avvio");
		option.setRequired(false);
		options.addOption(option);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine line = null;

		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Comando sconosciuto");
			formatter.printHelp("Compositore-Java", options);
			System.exit(1);
		}

		if (line.hasOption("h") || line.getOptions().length == 0) {
			formatter.printHelp("Compositore-Java", options);
			System.exit(0);
		}

		return line;

	}

	private void runFromApplication() throws RpaComposerException {

		initContent();

		composerConfiguration.initLogParameters(composerStartConfiguration);
		composerConfiguration.setIsSystemPrintActive(false);

		// Inizializzo la connessione al database
		RpaUserConnection userConnection = RpaUserConnection.getInstance(composerStartConfiguration);

		if (userConnection != null) {

			composerConfiguration.setConn(RpaDBUtils.prepareConnection(userConnection));

		} else {

			System.err.println("Configurazione insufficente, impossibile stabilire una connessione con il database specificato");

		}

		// Inizializzo il file-documento da processare
		File inputModel = new File(composerStartConfiguration.getDocumentFilePath());

		if (inputModel.exists() && inputModel.canRead()) {

			composerConfiguration.setInputModel(inputModel);

		} else {

			String code		= "";
			String message	= "Il file di modello indicato come input: " + inputModel.getPath() + " non esiste o è protetto";
			int context		= RpaComposerException.PRECOMPILE_MESSAGE;

			throw new RpaDocumentNotFoundException(composerConfiguration, antlrErrorListener, context, code, message);

		}

		// Inizializzo il nome dell'entità
		String	entityDeclaration	= composerStartConfiguration.getEntityDeclaration();
		Matcher entityMatcher		= Pattern.compile("^(([^:]+)\\:([^:=]+)\\=([^:=;]+))(;([^;:=]+\\=[^;:=]+))*$").matcher(entityDeclaration);

		if (entityMatcher.find()) {

			List<String[]> identities = new ArrayList<String[]>();

			String[] identity = new String[] { entityMatcher.group(2), entityMatcher.group(3), entityMatcher.group(4) };
			identities.add(identity);

			if (entityMatcher.group(5) != null && !entityMatcher.group(5).isEmpty()) {

				Matcher matcherExtraParameter = Pattern.compile(";([^;:=]+)\\=([^;:=]+)").matcher(entityMatcher.group(5));

				while (matcherExtraParameter.find()) {

					String[] extraIdentity = new String[] { matcherExtraParameter.group(1), matcherExtraParameter.group(2) };
					identities.add(extraIdentity);

				}

			}

			composerConfiguration.setIdentity(identities);

		} else {

			String code		= "";
			String message	= "L'entità passata come parametro non sembra essere valida";
			int context = RpaComposerException.PRECOMPILE_MESSAGE;

			throw new RpaEntityDeclarationException(composerConfiguration, antlrErrorListener, context, code, message);

		}

		// Inizializzo il tipo di database
		String dbTypeString = composerStartConfiguration.getDbType();
		composerConfiguration.setDBType(dbTypeString);

		// Inizializzo il path su cui salvare il modello
		composerConfiguration.setOutputModel(new File(composerStartConfiguration.getOutputFilePath()));

		// Inizializzo gli eventuali parametri opzionali
		if (composerStartConfiguration.getParameters() != null && !composerStartConfiguration.getParameters().isEmpty()) {

			registerManager.updateRegisters(composerStartConfiguration.getParameters());
			composerConfiguration.setExtraOptions(registerManager);

		}

		// Controllo se ho caratteri di formattazione per i decimali
		if (composerStartConfiguration.getDecimalSeparator() != null || composerStartConfiguration.getIntegerSeparator() != null) {

			Character decimalCharacter = composerStartConfiguration.getDecimalSeparator();
			Character integerCharacter = composerStartConfiguration.getIntegerSeparator();

			styleManager.updateDecimalCharacterSeparators(decimalCharacter, integerCharacter);

		}

		// Controllo se ho definito un limite per il numero di dati da leggere
		if (composerStartConfiguration.getLimitReadData() != null) {

			composerConfiguration.setLimitReadData(composerStartConfiguration.getLimitReadData());

		}

		// Inizializzo il gestore dei mnemonici
		mnemonicDescriptionList = new RpaMnemonicDescriptionList(this);
		mnemonicManager.init(composerConfiguration.getConn());

		// Se ho un id sessione, ne recupero i codici associati
		if (composerConfiguration.getSessionParametersId() != null) {

			parametersManager.init(composerConfiguration.getSessionParametersId());

		}

		// Inizializzo il sistema di recupero di immagini dall'esterno
		this.importExternalImage = composerStartConfiguration.getImportExternalImage();

		// Eseguo il compositore
		printStartComposer();

		RpaCompositore compositore = new RpaCompositore(this);
		compositore.eseguiCompositore(composerConfiguration);

		// Stampo il grafo dei mnemonici in graphviz (Richiamare quello dei "DocumentScope")
		// MnemonicManager.getMnemonicManager().saveMnemonicGraphGraphviz(".", true);

		printEndComposer();

		// Chiudo la connessione al DB
		RpaDBUtils.close(composerConfiguration.getConn());

	}

	private static void bypassAspose() {

		try {
			// SERVE A TOGLIERE IL TESTO: "Evaluation Only. Created with Aspose.Words. Copyright 2003-2015 Aspose Pty Ltd."
			// E A ELIMINARE LA TRONCATURA AUTOMATICA CON TESTO: "This document was truncated here because it was created using Aspose.Words in Evaluation Mode."
			RpaDocAsposeSetup.apply();
		} catch (Exception e) {
			System.err.println("Impossibile configurare Aspose");
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void initContent() {

		// compositionManagement	    = new CompositionManagement();
		composerConfiguration	        = new RpaComposerConfiguration(this);
		debugMessages			        = new RpaDebugMessages(composerConfiguration);
		warningMessages			        = new RpaWarningMessages(composerConfiguration);
		mnemonicManager			        = new RpaMnemonicManager(this);
		precompilerManager		        = new RpaPrecompilerManager(this);
		variablesManager		        = new RpaVariablesManager(this);
		styleManager			        = new RpaStyleManager();
		loopInformationManager	        = new RpaLoopInformationManager();
		dataConversionContainer         = new RpaDataConversionContainer();
		mnemonicDataFunctionContainer   = new RpaMnemonicDataFunctionContainer();
		sqlManager				        = new RpaSQLManager(this);
		registerManager			        = new RpaRegisterManager(this);
		parametersManager		        = new RpaParametersManager(this);
		antlrErrorListener				= new RpaANTLRErrorListener(this);

	}

	private void printStartComposer() {

		// Estraggo il nome del file e l'entità principale
		String fileName			= composerConfiguration.getInputModel().getName();
		String rootCondition	= mnemonicManager.getMnemonicNodeRoot().getRootCondition();
		String parametersList	= null;
		String dateTimeString	= null;

		// Estraggo la definizione dei registri (se esiste)
		if (registerManager.getRegistersDeclaration() != null && !registerManager.getRegistersDeclaration().isEmpty()) {

			parametersList = registerManager.getRegistersDeclaration();

		}

		// Definisco l'ora corrente
		Timestamp timestamp		= new Timestamp(System.currentTimeMillis());
		DateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dateTimeString			= dateFormat.format(timestamp);

		// Ricostruisco il messaggio di avvio e lo stampo
		int context		= RpaComposerException.PRECOMPILE_MESSAGE;
		String message	= null;

		if (parametersList == null || parametersList.isEmpty()) {

			message = "*** [" + dateTimeString + "] Avvio del compositore su " + fileName + " con " + rootCondition + " ***";

		} else {

			message = "*** [" + dateTimeString + "] Avvio del compositore su " + fileName + " con " + rootCondition + " usando " + parametersList + " ***";

		}

		debugMessages.priorityPrint(context, message);

	}

	private void printEndComposer() {

		Timestamp timestamp		= new Timestamp(System.currentTimeMillis());
		DateFormat dateFormat	= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateTimeString	= dateFormat.format(timestamp);

		int context		= RpaComposerException.COMPILE_MESSAGE;
		String message	= "*** [" + dateTimeString + "] Fine del compositore ***";

		debugMessages.priorityPrint(context, message);

	}

	public RpaMnemonicEmpty getMnemonicEmpty() { return mnemonicEmpty; }

	public RpaImportExternalImageI getImportExternalImage() { return importExternalImage; }

	public int getExitCode() { return exitCode; }

	public void setExitCode(int exitCode) { this.exitCode = exitCode; }

	public void setLastRunNodeRead(Run lastRunNodeRead) { this.lastRunNodeRead = lastRunNodeRead; }

	public Run getLastRunNodeRead() { return lastRunNodeRead; }

	public RpaComposerConfiguration getComposerConfiguration() { return composerConfiguration; }

	public List<Node> getNodesBreakLoopToDelete() { return nodesBreakLoopToDelete; }

	public RpaMnemonicDescriptionList getMnemonicDescriptionList() { return mnemonicDescriptionList; }

	public RpaMnemonicManager getMnemonicManager() { return mnemonicManager; }

	public RpaPrecompilerManager getPrecompilerManager() { return precompilerManager; }

	public RpaVariablesManager getVariablesManager() { return variablesManager; }

	public RpaStyleManager getStyleManager() { return styleManager; }

	public RpaLoopInformationManager getLoopInformationManager() { return loopInformationManager; }

	public RpaDataConversionContainer getDataConversionContainer() { return dataConversionContainer; }

	public RpaMnemonicDataFunctionContainer getMnemonicDataFunctionContainer() { return mnemonicDataFunctionContainer; }

	public RpaSQLManager getSQLManager() { return sqlManager; }

	public RpaParametersManager getParametersManager() { return parametersManager; }

	public RpaRegisterManager getRegisterManager() { return registerManager; }

	public RpaANTLRErrorListener getAntlrErrorListener() { return antlrErrorListener; }

	public RpaWarningMessages getWarningMessages() { return warningMessages; }

	public RpaDebugMessages getDebugMessages() { return debugMessages; }

	public boolean isDebugStop = false;

}
