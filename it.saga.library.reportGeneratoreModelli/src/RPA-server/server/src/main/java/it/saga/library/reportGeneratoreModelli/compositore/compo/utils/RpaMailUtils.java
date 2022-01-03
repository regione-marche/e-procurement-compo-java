package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe per la gestione della direttiva [MAILTO]
 * <p>
 * Es: [MAILTO]<TotalizzatoreRet>=<mittente>,<destinatario>,<titolo>,[HTML:]<corpo>
 */
public class RpaMailUtils {

	private static String SMTP;
	private static Integer PORT;
	private static String USER;
	private static String PSW;

	private RpaMailUtils() {
	}

	/**
	 * Factory method, se la configurazione è completa ritorna una istanza di questo oggetto.
	 *
	 * @return
	 */
	public static RpaMailUtils getInstance() {
		File config = new File("compo.ini");
		if (config.exists()) {
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(config));
				String currentLine;
				while ((currentLine = bufferedReader.readLine()) != null) {
					if (currentLine.startsWith("SMTP=")) {
						SMTP = (currentLine.split("\\=").length == 2) ? currentLine.split("\\=")[1] : null;
					} else if (currentLine.startsWith("PORTA=")) {
						PORT = (currentLine.split("\\=").length == 2) ? Integer.parseInt(currentLine.split("\\=")[1]) : null;
					} else if (currentLine.startsWith("USER=")) {
						USER = (currentLine.split("\\=").length == 2) ? currentLine.split("\\=")[1] : null;
					} else if (currentLine.startsWith("PSW=")) {
						PSW = (currentLine.split("\\=").length == 2) ? currentLine.split("\\=")[1] : null;
					}
				}
			} catch (IOException e) {
				System.err.println("Impossibile configurare il servizio mail");
				e.printStackTrace();
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// La configurazione è completa
					if (SMTP != null && PORT != null && USER != null && PSW != null) {
						return new RpaMailUtils();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Invia una mail utilizzando la configurazione statica del file compo.ini
	 *
	 * @param mittente     mittente della e-mail
	 * @param destinatario destinatario della e-mail
	 * @param titolo       titolo/oggetto della e-mail
	 * @param corpo        corpo/testo della email (In formato HTML o no)
	 * @throws EmailException
	 */
	public boolean sendEmail(final String mittente, final String destinatario, final String titolo, final boolean html, final String corpo) throws EmailException {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(SMTP);
		email.setSmtpPort(PORT);
		email.setAuthenticator(new DefaultAuthenticator(USER, PSW));
		email.addTo(destinatario);
		email.setFrom(mittente);
		email.setSubject(titolo);

		// Verifico se il corpo è di tipo HTML e imposto il messaggio
		if (html) {
			email.setHtmlMsg(corpo);
		} else {
			email.setMsg(corpo);
		}

		email.setSSLOnConnect(true);
		email.setSSLCheckServerIdentity(true);
		String result = email.send();

		return result.isEmpty();
	}
}
