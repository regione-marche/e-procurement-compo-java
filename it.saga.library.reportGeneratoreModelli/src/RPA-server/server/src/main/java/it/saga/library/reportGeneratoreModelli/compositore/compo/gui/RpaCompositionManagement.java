package it.saga.library.reportGeneratoreModelli.compositore.compo.gui;

import javax.swing.ImageIcon;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Si occupa di creare una piccala icona sulla barra degli strumenti
 * del sistema operativo da cui è possibile eseguire delle azioni.
 * Dove il sistema operativo non supporta questa opzione la funzione viene
 * ignorata.
 */
public class RpaCompositionManagement {

	private TrayIcon trayIcon;
	private SystemTray systemTray;
	private PopupMenu popupMenu;

	public RpaCompositionManagement() {

		try {

			this.popupMenu = new PopupMenu();
			initializeMenu(new String[] { "Arresta composizione" }, this.popupMenu);

			this.trayIcon = new TrayIcon(new ImageIcon(getClass().getClassLoader().getResource("build.png")).getImage());
			this.trayIcon.setPopupMenu(popupMenu);

			this.systemTray = SystemTray.getSystemTray();
			this.systemTray.add(trayIcon);

		} catch (AWTException e) {
			System.err.println("Errore durante l'inizializzazione");
			e.printStackTrace();
		}

	}

	/*
	 * Inizializza il pop-up
	 */
	private void initializeMenu(String[] labels, PopupMenu menu) {
		MenuItem item;
		for (int i = 0; i <= labels.length - 1; i++) {
			String label = labels[i];

			//Predisposizione per aggiungere nuovi elementi
			switch (i) {
				case 0:
					item = new MenuItem(label);
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							System.exit(0);
						}
					});
					break;
				default:
					item = new MenuItem();
					item.setEnabled(false);
			}
			menu.add(item);
		}
	}

	/**
	 * Factory method
	 *
	 * @return Ritorna un'istanza di questa classe se la funzione
	 * "SystemTray" è supportata dal sistema operativo
	 * altrimenti ritorna null
	 */
	/*
	public static CompositionManagement getInstance() {
		CompositionManagement compositionManagement = null;
		if (SystemTray.isSupported()) {
			try {
				compositionManagement = new CompositionManagement();
			} catch (AWTException e) {
				LOG.error("Errore durante l'inizializzazione", e);
			}
		}
		return compositionManagement;
	}
	*/

	/*
	 * Metodi get
	 */
	public TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public SystemTray getSystemTray() {
		return systemTray;
	}
}
