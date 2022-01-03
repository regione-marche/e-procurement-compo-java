package it.saga.library.reportGeneratoreModelli.compositore.compo.db;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Classe che si occupa di caricare in memoria le proprietà
 * di tutti i campi utilizzabili nella sintassi.
 */
public class RpaMnemonicDescriptionList {

	private RpaMainCompositore mainCompositore;
	private HashMap<String, MnemonicDescription> mnemonicDescriptionHashMap;

	public RpaMnemonicDescriptionList(RpaMainCompositore mainCompositore) {

		this.mainCompositore			= mainCompositore;
		this.mnemonicDescriptionHashMap	= new HashMap<String, MnemonicDescription>();

		try {
			// Connection connection = ComposerConfiguration.getInstance().getConn();
			Connection connection = mainCompositore.getComposerConfiguration().getConn();
			if (connection != null) {

				String sql = "" +
						"SELECT c0c_tip		AS enable, " +
						"		c0c_chi		AS p, " +
						"		c0c_mne_uni AS path, " +
						"		c0c_mne_ber AS mnemonico, " +
						"		c0c_des		AS description, " +
						"		c0c_des_frm	AS type, " +
						"		c0c_fs		AS default_format, " +
						"		c0c_tab1	AS code, " +
						"		c0c_dom		AS default_domain, " +
						"		c0c_des_web	AS other, " +
						"		app_prefix	AS dt " +
						"FROM	rpa_c0campi";

				PreparedStatement preparedStatement	= connection.prepareStatement(sql);
				ResultSet resultSet					= preparedStatement.executeQuery();

				while (resultSet.next()) {

					MnemonicDescription mnemonicDescription = new MnemonicDescription();
					mnemonicDescription.setEnable(resultSet.getString("enable").equals("E"));
					mnemonicDescription.setP(resultSet.getString("p"));
					mnemonicDescription.setPath(resultSet.getString("path"));
					mnemonicDescription.setMnemonico(resultSet.getString("mnemonico"));
					mnemonicDescription.setDescription(resultSet.getString("description"));
					mnemonicDescription.setType(resultSet.getString("type"));
					mnemonicDescription.setDefault_format(resultSet.getString("default_format"));
					mnemonicDescription.setCode(resultSet.getString("code")); // Tabulation code
					mnemonicDescription.setDefault_domain(resultSet.getString("default_domain"));
					mnemonicDescription.setOther(resultSet.getString("other"));
					mnemonicDescription.setDt(resultSet.getString("dt"));

					String mnemonico = mnemonicDescription.getMnemonico();

					if (!mnemonico.isEmpty()) {

						mnemonicDescriptionHashMap.put(mnemonico, mnemonicDescription);

					}

				}

				resultSet.close();
				preparedStatement.close();

			} else {

				System.err.println("Impossibile stabilire una connessione con il database Sicr@Web, inizializzazione proprietà campi skippata...");

			}

		} catch (SQLException e) {

			System.err.println("Errore durante la lettura delle proprietà dei campi");
			e.printStackTrace();

		}
	}

	/*
	public static void initStaticContent() { mnemonicDescriptionList = new MnemonicDescriptionList(); }

	public static MnemonicDescriptionList getMnemonicDescriptionList() {
		return mnemonicDescriptionList;
	}
	*/

	/**
	 * Inizializza la classe e carica in memoria gli mnemonici
	 */
	// public void init() {}

	/**
	 * Dato il nome di uno mnemonico ne restituisce tutte le proprietà
	 *
	 * @param key il nome dello mnemonico ES: NPRAT
	 * @return se esisteno delle proprietà associate allo mnemonico le restituisce, altrimenti ritorna null
	 */
	public MnemonicDescription getPropByName(final String key) {

		if (this.mnemonicDescriptionHashMap.containsKey(key)) {

			return this.mnemonicDescriptionHashMap.get(key);

		}

		return null;
	}

	/**
	 * Verifica se la parola è un mnemonico
	 *
	 * @param word
	 * @return
	 */
	public boolean isMnemonic(String word) {

		return this.mnemonicDescriptionHashMap.containsKey(word);

	}

	/**
	 * Rappresentazione di un Mnemonico del db
	 */
	public class MnemonicDescription {

		private boolean enable;
		private String p;
		private String path;
		private String mnemonico;
		private String description;
		private String type;
		private String default_format;
		private String code;
		private String default_domain;
		private String other;
		private String dt;

		MnemonicDescription() {

		}

		/*
		 * Metodi get e set
		 */
		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public String getP() {
			return p;
		}

		public void setP(String p) {
			this.p = p;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getMnemonico() {
			return mnemonico;
		}

		public void setMnemonico(String mnemonico) {
			this.mnemonico = mnemonico;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDefault_format() {
			return default_format;
		}

		public void setDefault_format(String default_format) {
			this.default_format = default_format;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDefault_domain() {
			return default_domain;
		}

		public void setDefault_domain(String default_domain) {
			this.default_domain = default_domain;
		}

		public String getOther() {
			return other;
		}

		public void setOther(String other) {
			this.other = other;
		}

		public String getDt() {
			return dt;
		}

		public void setDt(String dt) {
			this.dt = dt;
		}
	}
}
