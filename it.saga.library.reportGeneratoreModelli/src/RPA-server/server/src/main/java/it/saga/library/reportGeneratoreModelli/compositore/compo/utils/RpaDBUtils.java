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

	public static Connection sicraweb() {
		try {
			// return DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "root");
			// return DriverManager.getConnection("jdbc:postgresql://localhost:5432/soncino", "postgres", "postgres");
			// return DriverManager.getConnection("jdbc:oracle:thin:@elda13:1521:prod01", "CONCE_450", "CONCE_450");
			// return DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.13:1521:PROD01", "test_compo", "test_compo");

			// return DriverManager.getConnection("jdbc:postgresql://localhost:5432/soncino", "postgres", "postgres");
			// return DriverManager.getConnection("jdbc:postgresql://wf81collx:5432/olgiate", "olgiate", "olgiate");
			return DriverManager.getConnection("jdbc:postgresql://wf81uff:5432/olgiate", "olgiate", "olgiate");
		} catch (SQLException e) {
			System.err.println("Impossibile connettersi al database");
			e.printStackTrace();
			return null;
		}
	}

	public static Connection prepareConnection(RpaUserConnection userConnection) {
		Connection connection = null;
		// TODO: Ricontrollare il "Class.forName" dopo che collx è stato aggiornato
		// try {
		// Class.forName(userConnection.getDriver());
		try {
			connection = DriverManager.getConnection(userConnection.getUrl(), userConnection.getUser(), userConnection.getPassword());
		} catch (SQLException e) {
			System.err.println("Impossibile connettersi al database");
			e.printStackTrace();
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
