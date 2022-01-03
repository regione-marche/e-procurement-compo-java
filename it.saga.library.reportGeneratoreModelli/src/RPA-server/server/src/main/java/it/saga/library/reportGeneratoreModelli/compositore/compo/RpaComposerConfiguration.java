package it.saga.library.reportGeneratoreModelli.compositore.compo;

import com.aspose.words.Document;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaOutOfMemoryException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaRegisterManager;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configurazione compositore
 */
public class RpaComposerConfiguration {

	public static final int TYPE_DB_UNDEFINED	= -1;
	public static final int TYPE_ORACLE			= 0;
	public static final int TYPE_POSTGRESQL		= 1;
	public static final int TYPE_MSQL			= 2;

	private static final String[] MAP_DB_TYPE = {
			"ORA",
			"POS",
			"MSQ"
	};

	private static final long DEFAULT_LIMIT_READ_DATA = 10000;

	private int dbTypeIndex = TYPE_DB_UNDEFINED;

	private RpaMainCompositore mainCompositore;

	// Entità base divisa in una lista di array (Es singolo elemento: { APE_CONCES , PKID , 24361 } --> { TABELLA , COLONNA , VALORE })
	private List<String[]> identity;
	// File o modello da comporre
	private File inputModel;
	// Oggetto document aspose
	private Document inputDocument;
	// File di uscita che contiene il risultato della composizione
	private File outputModel;
	// Cartella di lavoro della sessione
	private File workDir;
	// Connessione della sessione
	private Connection conn;
	// True se il modello da comporre è un file RTF
	private boolean rtf;
	// Si associa il dominio dell'entità come "ape" o "rpa" (utilizzata per gestire mnemonici tabellari)
	private String entityDomain;
	// Path di connessione al Database
	private String dbPathConnection;
	// Nome della tabella in cui salvare i messaggi di log
	private String debugTableName = "rpa_log";
	// Attiva o meno la stampa di debug
	private boolean isDebugMessageActive = false;
	// Attiva o meno la stampa di warning
	private boolean isWarningMessageActive = false;
	// Attiva o meno la stampa di errori
	private boolean isErrorMessageActive = false;
	// Attiva o meno il System.print
	private boolean isSystemPrintActive;
	// Id dell'utente
	private Long userId;
	// Id della sessione di esecuzione
	private Integer sessionId;
	// Id della sessione dei parametri
	private Long sessionParametersId;
	// Limite lettura dati da SQL
	private Long limitReadData;
	// Evita di usare le nuove tabelle (rpa_session e rpa_log)
	private boolean isAvoidUseNewTables = false;
	// Controlla se il compositore è stato esplicitamente avviato da un applicativo diverso da sicraweb
	private boolean isStartFromSicraweb = true;

	public RpaComposerConfiguration(RpaMainCompositore mainCompositore) {

		this.mainCompositore	= mainCompositore;
		this.inputModel			= null;
		this.inputDocument		= null;
		this.outputModel		= null;
		this.rtf				= false;
		this.limitReadData		= DEFAULT_LIMIT_READ_DATA;

		/*
		Random random = new Random();
		sessionId = random.nextInt();
		*/
		// this.sessionId = mainCompositore.getMnemonicManager().getSessionId();

	}

	public void setInputModel(final File inputModel) {

		this.inputModel = inputModel;

		// Calcolo il nuovo "Aspose document"
		try {

			inputDocument = new Document(new FileInputStream(inputModel.getPath()));

		} catch (Exception e) {

			System.err.println("Errore durante il calcolo del documento aspose");
			e.printStackTrace();

		} catch (OutOfMemoryError error) {

			String code     	= "";
			String message  	= "ASPOSE OutOfMemory: Provare a modificare il documento (esportare e reimportare le icone come jpg) o aggiornare " +
					"ASPOSE ad una versione piu recente (chiedere a infrastruttura)";
			int errorContext   	= RpaComposerException.PRECOMPILE_MESSAGE;

			throw new RpaOutOfMemoryException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), errorContext, code, message);

		}

	}

	public void setOutputModel(File outputModel) {

		if (outputModel.exists()) {

			outputModel.delete();

		}

		this.outputModel = outputModel;

	}

	public void setDBType(String typeString) {

		for (int i = 0; i < MAP_DB_TYPE.length; i ++) {

			if (MAP_DB_TYPE[i].equals(typeString)) {

				dbTypeIndex = i;
				break;

			}

		}

	}

	public void setDebugLevel(String debugLevel) {

		// Verifico se il livello è definito
		if (debugLevel.isEmpty()) {

			return;

		}

		// Controllo se i livelli di debug sono stati dichiarati correttamente

		// Link: https://regex101.com/r/ufC6ej/2
		Matcher debugLevelMatcher = Pattern.compile("^([dwe])(?!\\1)([dwe])?(?!\\1)(?!\\2)([dwe])?$").matcher(debugLevel);

		if (!debugLevelMatcher.find()) {

			System.err.println("Livello di debug definito in maniera non corretta");
			return;

		}

		// Cerco se abilitare il 'debug' (d), il 'warning" (w) o il 'error' (e)
		for (int groupIndex = 1; groupIndex <= debugLevel.length(); groupIndex ++) {

			char debugType = debugLevelMatcher.group(groupIndex).toCharArray()[0];
			switch (debugType) {

				case 'd':
					isDebugMessageActive = true;
					break;
				case 'w':
					isWarningMessageActive = true;
					break;
				case 'e':
					isErrorMessageActive = true;
					break;

			}

		}

	}

	public void initLogParameters(RpaComposerStartConfiguration composerStartConfiguration) {

		this.isDebugMessageActive	= composerStartConfiguration.isDebugMessageActive();
		this.isWarningMessageActive	= composerStartConfiguration.isWarningMessageActive();
		this.isErrorMessageActive	= composerStartConfiguration.isErrorMessageActive();
		this.userId					= composerStartConfiguration.getUserId();
		this.setDBType(composerStartConfiguration.getDbType());

	}

	public void setExtraOptions(RpaRegisterManager registerManager) {

		// Verifico se è stato definito il prefisso per la lettura di "Tab1"
		if (registerManager.isPresent(RpaRegisterManager.TAB1_REGISTER_INDEX)) {

			entityDomain = registerManager.getRegister(RpaRegisterManager.TAB1_REGISTER_INDEX);

		}

		// Verifico se è stato definito il valore di sessione
		if (registerManager.isPresent(RpaRegisterManager.SESSION_REGISTER_INDEX)) {

			sessionParametersId = Long.valueOf(registerManager.getRegister(RpaRegisterManager.SESSION_REGISTER_INDEX));

		}

	}

	public void setApplicationStart(String applicationName) {

		if (applicationName != null && !applicationName.toLowerCase().equals("sicraweb")) {

			isStartFromSicraweb = false;

		}

	}

	/*
	 * Metodi get e set
	 */
	public List<String[]> getIdentity() { return this.identity; }

	public void setIdentity(List<String[]> identity) { this.identity = identity; }

	public File getInputModel() { return inputModel; }

	public Document getInputDocument() { return inputDocument; }

	public File getOutputModel() { return outputModel; }

	public int getDBType() { return dbTypeIndex; }

	public File getWorkDir() { return workDir; }

	public void setWorkDir(File workDir) { this.workDir = workDir; }

	public Connection getConn() { return conn; }

	public void setConn(Connection conn) { this.conn = conn; }

	public boolean isRtf() { return rtf; }

	public void setRtf(boolean rtf) { this.rtf = rtf; }

	public String getEntityDomain() { return entityDomain; }

	public void setEntityDomain(String entityDomain) { this.entityDomain = entityDomain; }

	public String getDebugTableName() { return debugTableName; }

	public void setDebugTableName(String debugTableName) { this.debugTableName = debugTableName; }

	public String getDBPathConnection() { return dbPathConnection; }

	public void setDBPathConnection(String dbPathConnection) { this.dbPathConnection = dbPathConnection; }

	public Long getUserId() { return userId; }

	public void setUserId(Long userId) { this.userId = userId; }

	public void setIsSystemPrintActive(boolean isSystemPrintActive) { this.isSystemPrintActive = isSystemPrintActive; }

	public Integer getSessionId() { return sessionId; }

	public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

	public void setLimitReadData(Long limitReadData) { this.limitReadData = limitReadData; }

	public Long getLimitReadData() { return limitReadData; }

	public Long getSessionParametersId() { return sessionParametersId; }

	public boolean isDebugMessageActive() { return isDebugMessageActive; }

	public boolean isWarningMessageActive() { return isWarningMessageActive; }

	public boolean isErrorMessageActive() { return isErrorMessageActive; }

	public boolean isSystemPrintActive() { return isSystemPrintActive; }

	public boolean isAvoidUseNewTables() { return isAvoidUseNewTables; }

	public boolean isStartFromSicraweb() { return isStartFromSicraweb; }

}
