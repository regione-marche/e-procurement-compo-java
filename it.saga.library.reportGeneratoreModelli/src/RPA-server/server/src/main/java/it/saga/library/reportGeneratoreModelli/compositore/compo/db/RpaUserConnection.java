package it.saga.library.reportGeneratoreModelli.compositore.compo.db;

import it.saga.library.commonDataTypes.CdtHibernateSessionWrapper;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerStartConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Oggetto contenitore di tutte le informazioni necessarie per accedere
 * al database specificato dall'utente in fase di avvio del programma
 */
public class RpaUserConnection {

	private static final String DB_CONNECTION_POSTGRES_REGEX	= "^DBMS=([^;]+);driverClassName=([^;]+);url=([^;]+);UID=([^;]+);PWD=([^;]+)$";
	private static final String DB_CONNECTION_SQLSERVER_REGEX	= "^DBMS=([^;]+);driverClassName=([^;]+);url=([^;]+;DatabaseName=[^;]+;SelectMethod=[^;]+);UID=([^;]+);PWD=([^;]+);?(trustServerCertificate=([^;]+))?$";
	private static final String DB_CONNECTION_ORACLE_REGEX		= "^DBMS=([^;]+);driverClassName=([^;]+);url=([^;]+);UID=([^;]+);PWD=([^;]+)$";

	// Link: https://regex101.com/r/CfYzEc/4/
	private static final String DBMS_REGEX		= "(?i)dbms=([^;\\n\\r]+)";

	// Link: https://regex101.com/r/wANziO/1
	private static final String DRIVER_REGEX	= "(?i)driverClassName=([^;\\n\\r]+)";

	// Link: https://regex101.com/r/KMFqtx/1
	private static final String URL_REGEX		= "(?i)url=([^;\\n\\r]+)";

	// Link: https://regex101.com/r/9uUcZO/1
	private static final String USERNAME_REGEX	= "(?i)uid=([^;\\n\\r]+)";

	// Link: https://regex101.com/r/lEHWYR/2
	private static final String PASSWORD_REGEX	= "(?i)pwd=([^;\\n\\r]+)";

	private CdtHibernateSessionWrapper hibernateSessionWrapper;
	private String dbms;
	private String driver;
	private String url;
	private String username;
	private String password;
	private String trustServerCertificate;


	private static final String[] DB_CONNECTION_REGEXS = {
			DB_CONNECTION_POSTGRES_REGEX,
			DB_CONNECTION_SQLSERVER_REGEX,
			DB_CONNECTION_ORACLE_REGEX
	};

	public RpaUserConnection(String dbms, String driver, String url, String username, String password) {
		this.dbms = dbms;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public RpaUserConnection(String dbms, String driver, String url, String username, String password, String trustServerCertificate) {
		this.dbms = dbms;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.trustServerCertificate = trustServerCertificate;
	}

	public RpaUserConnection(CdtHibernateSessionWrapper hibernateSessionWrapper) {

		this.hibernateSessionWrapper = hibernateSessionWrapper;

	}

	/*
	 * Metodi get
	 */

	public CdtHibernateSessionWrapper getHibernateSessionWrapper() {
		return hibernateSessionWrapper;
	}

	public String getDbms() {
		return dbms;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getTrustServerCertificate() {
		return trustServerCertificate;
	}

	/**
	 * Se la configurazione per accedere al database è completa
	 * restituisce un'istanza di questo oggetto
	 *
	 * @param connectionString
	 * 			ES: DBMS=JDBC;driverClassName=oracle.jdbc.driver.OracleDriver;url=jdbc:oracle:thin:@x.x.x.x:1521:PROD01;UID=test;PWD=test
	 * @return
	 * 			Una nuova istanza di UserConnection se la configurazione è completa, null altrimenti
	 */
	public static RpaUserConnection newGetInstance(String connectionString) {

		String dbms		= null;
		String driver	= null;
		String url		= null;
		String username	= null;
		String password	= null;

		boolean isConnectionStringValid = true;

		Matcher dbmsMatcher		= Pattern.compile(DBMS_REGEX).matcher(connectionString);
		Matcher driverMatcher	= Pattern.compile(DRIVER_REGEX).matcher(connectionString);
		Matcher urlMatcher		= Pattern.compile(URL_REGEX).matcher(connectionString);
		Matcher usernameMatcher	= Pattern.compile(USERNAME_REGEX).matcher(connectionString);
		Matcher passwordMatcher	= Pattern.compile(PASSWORD_REGEX).matcher(connectionString);

		// Cerco la definizione del DBMS
		if (dbmsMatcher.find()) {

			dbms = dbmsMatcher.group(1);

		} else {

			isConnectionStringValid = false;

		}

		// Cerco la definizione del DRIVER
		if (driverMatcher.find()) {

			driver = driverMatcher.group(1);

		} else {

			isConnectionStringValid = false;

		}

		// Cerco l'url di connessione al database
		if (urlMatcher.find()) {

			url = urlMatcher.group(1);

		} else {

			isConnectionStringValid = false;

		}

		// Cerco lo username
		if (usernameMatcher.find()) {

			username = usernameMatcher.group(1);

		} else {

			isConnectionStringValid = false;

		}

		// Cerco la password
		if (passwordMatcher.find()) {

			password = passwordMatcher.group(1);

		} else {

			isConnectionStringValid = false;

		}

		if (!isConnectionStringValid) {

			System.err.println("Stringa di connessione al database non conforme");

		}

		return new RpaUserConnection(dbms, driver, url, username, password);

	}

	/**
	 * Se la configurazione per accedere al database è completa
	 * restituisce un'istanza di questo oggetto
	 *
	 * @param connectionString
	 * 			ES: DBMS=JDBC;driverClassName=oracle.jdbc.driver.OracleDriver;url=jdbc:oracle:thin:@x.x.x.x:1521:PROD01;UID=test;PWD=test
	 * @return
	 * 			Una nuova istanza di UserConnection se la configurazione è completa, null altrimenti
	 */
	public static RpaUserConnection getInstance(String connectionString) {

		String dbms		= null;
		String driver	= null;
		String url		= null;
		String username	= null;
		String password	= null;
		String trustServerCertificate = null;

		for (String dbConnectionRegex : DB_CONNECTION_REGEXS) {

			Matcher matcher = Pattern.compile(dbConnectionRegex).matcher(connectionString);

			if (matcher.find()) {

				dbms = matcher.group(1);
				driver = matcher.group(2);
				url = matcher.group(3);
				username = matcher.group(4);
				password = matcher.group(5);
				if(dbConnectionRegex.equals(DB_CONNECTION_SQLSERVER_REGEX)) {
					trustServerCertificate = matcher.group(6);
				}
				return new RpaUserConnection(dbms, driver, url, username, password, trustServerCertificate);

			}

		}

		System.err.println("Stringa di connessione al database non conforme");

		return null;

	}

	public static RpaUserConnection getInstance(RpaComposerStartConfiguration composerStartConfiguration) {

		// Matcher urlMatcher = Pattern.compile(URL_REGEX).matcher(composerStartConfiguration.getConnectionString());

		CdtHibernateSessionWrapper hibernateSessionWrapper = composerStartConfiguration.getConnection();
		String dbms		= composerStartConfiguration.getDBMSName();
		String driver	= composerStartConfiguration.getDBDriver();
		String username	= composerStartConfiguration.getDBUsername();
		String password	= composerStartConfiguration.getDBPassword();
		String url		= composerStartConfiguration.getDBConnectionUrl();
		// String url		= urlMatcher.group(1);

		if (hibernateSessionWrapper != null && !composerStartConfiguration.isForceUseDbParameters()) {

			return new RpaUserConnection(hibernateSessionWrapper);

		} else {

			return new RpaUserConnection(dbms, driver, url, username, password);

		}

	}

}
