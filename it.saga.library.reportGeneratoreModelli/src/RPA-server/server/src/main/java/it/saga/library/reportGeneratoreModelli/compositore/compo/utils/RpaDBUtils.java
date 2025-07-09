package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.compo.db.RpaUserConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe per la gestione delle connessioni
 */
public class RpaDBUtils {

	public static void close(Object obj) {
		if(obj != null) {
			try {
				if (obj instanceof Connection) {
					if(!((Connection)obj).isClosed()) {
						((Connection)obj).close();
					}
				} else if (obj instanceof Statement) {
					if(!((ResultSet)obj).isClosed()) {
						((ResultSet)obj).close();
					}
				} else if (obj instanceof ResultSet) {
					if(!((ResultSet)obj).isClosed()) {
						((ResultSet)obj).close();
					}
				}
			} catch (SQLException e) {
				System.err.println("Errore durante la chiusura di una risorsa: " + e.getMessage());
			}
		}
	}

	public static Connection prepareConnection(RpaUserConnection userConnection) {
		Connection connection = null;
		// TODO: Ricontrollare il "Class.forName" dopo che collx è stato aggiornato
		// try {
		// Class.forName(userConnection.getDriver());

		// Se ho già una sessione da Hibernate, ne estraggo la connessione versione JDBC
		if (userConnection.getHibernateSessionWrapper() != null) {

			connection = userConnection.getHibernateSessionWrapper().connection();

		} else {

			try {
				if(userConnection.getTrustServerCertificate() != null) {
					userConnection.setUrl(userConnection.getUrl() + ";" + userConnection.getTrustServerCertificate());
				}

				connection = DriverManager.getConnection(userConnection.getUrl(), userConnection.getUser(), userConnection.getPassword());

			} catch (SQLException e) {

				System.err.println("Impossibile connettersi al database");
				e.printStackTrace();

			}

		}
		/*
		} catch (ClassNotFoundException e) {
			System.err.println("Driver non disponibile per questo tipo di database");
			e.printStackTrace();
		}
		*/
		return connection;
	}

}
