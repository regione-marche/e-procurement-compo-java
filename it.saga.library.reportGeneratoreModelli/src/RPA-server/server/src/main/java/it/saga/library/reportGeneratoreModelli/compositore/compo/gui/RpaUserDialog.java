package it.saga.library.reportGeneratoreModelli.compositore.compo.gui;

import org.apache.commons.lang.math.NumberUtils;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;

public class RpaUserDialog {

	/*
	 * Consente di inserire qualsiasi carattere
	 */
	public static final String ANY_CHARACTER = "^.*$";
	public static final String ANY_CHARACTER_HELP = "<html>\u00C8 possibile inserire qualsiasi carattere</html>";

	/*
	 * Consente di inserire caratteri ASII e numeri
	 */
	public static final String ALPHANUMERIC = "[\\w|\\s|\\d]*"; //^(?:(?:\d+|\w+)\s*?)+$
	public static final String ALPHANUMERIC_HELP = "<html>Sono consentiti solo caratteri ASII, numeri, e spazi</html>";

	/*
	 * Consente di inserire numeri interi e decimali separati dal carattere '.' oppure dal carattere ','
	 */
	public static final String AMOUNT = "^\\d+(?:(?:\\.|\\,)\\d+)?$";
	public static final String AMOUNT_HELP = "<html>L'importo inserito deve essere intero o decimale<br/>separato dal carattere punto(.) oppure dal carattere virgola(,)</html>";

	/*
	 * Consente di inserire solo numeri interi
	 */
	public static final String INTEGER = "\\d+";
	public static final String INTEGER_HELP = "<html>Il numero inserito deve essere intero della forma (nn)</html>";

	/*
	 * Consente di inserire numeri interi e decimali
	 */
	public static final String DECIMAL = "^\\d+(?:(?:\\.|\\,)\\d+)?$";
	public static final String DECIMAL_HELP = "<html>Il numero inserito deve essere intero (nn) oppure decimale (nn.mm)<br/>e pu\u00F2 avere come separatore il carattere punto(.) oppure il carattere virgola(,)</html>";

	/*
	 * Consente di inserire una data nel formato {DATA_ELDA} cioè gg.mm.yyyy
	 */
	public static final String DATA = "^\\d{1,2}\\.\\d{1,2}\\.\\d{3,4}$";
	public static final String DATA_HELP = "<html>La data inserita deve essere della<br/>forma gg.mm.yyyy oppure della forma g.m.yyy</html>";

	/*
	 * Negli input numerici rappresenta il limite interiore
	 */
	private Integer lowerLimit;

	/*
	 * Negli input numerici rappresenta il limite superiore
	 */
	private Integer upperLimit;

	public RpaUserDialog() {
	}

	/**
	 * Visualizza una finestra di dialogo che consente di inserire un valore
	 *
	 * @param message Messaggio visualizzato nella finestra
	 * @param filter  Filtro da applicare alla TextField
	 * @param help    Un testo che aiuta l'utente a inserire input corretto
	 * @return Il valore inserito dall'utente
	 */
	public String showInputDialog(String message, final String filter, String help) {
		JLabel messageLabel = new JLabel(message);

		JLabel helpLabel = new JLabel(help);
		helpLabel.setVisible(false);

		JTextField textField = new JTextField();
		textField.setToolTipText(help);

		JLabel limitLabel = new JLabel();
		if ((lowerLimit != null || upperLimit != null) && filter != DATA) {
			if (filter == ALPHANUMERIC) {
				limitLabel.setText("<html>Con input numerico il valore deve rispettare i seguenti limiti:");
			} else {
				limitLabel.setText("<html>Il numero inserito deve rispettare i seguenti limiti:");
			}
			if (lowerLimit != null) {
				limitLabel.setText(limitLabel.getText() + "<br/>" + "Essere maggiore uguale a " + lowerLimit);
			}
			if (upperLimit != null) {
				limitLabel.setText(limitLabel.getText() + "<br/>" + "Essere minore uguale a " + upperLimit);
			}
			limitLabel.setText(limitLabel.getText() + "</html>");
		}

		boolean check = true;
		do {
			JOptionPane.showMessageDialog(null, new Object[] { messageLabel, textField, helpLabel, limitLabel }, //
					"Compositore Java", JOptionPane.QUESTION_MESSAGE, null);

			if (textField.getText().matches(filter)) {
				String tmp = textField.getText().replace(",", ".");
				if (NumberUtils.isNumber(tmp)) {
					if (lowerLimit == null && upperLimit == null) {
						check = false; // I limiti non sono applicabili
					} else if (lowerLimit != null && upperLimit == null) {
						check = !(Double.valueOf(tmp) >= getLowerLimit());
					} else if (lowerLimit == null && upperLimit != null) {
						check = !(Double.valueOf(tmp) <= getUpperLimit());
					} else if (lowerLimit != null && upperLimit != null) {
						check = !(Double.valueOf(tmp) >= getLowerLimit() && Double.valueOf(tmp) <= getUpperLimit());
					}
				} else {
					check = false;
				}
			}

			if (check) {
				textField.setBackground(new Color(255, 81, 81));
				helpLabel.setVisible(true);
			}
		} while (check);
		return textField.getText();
	}

	/*
	 * Metodi get e set
	 */
	public Integer getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(Integer lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public Integer getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(Integer upperLimit) {
		this.upperLimit = upperLimit;
	}

	/**
	 * Visualizza una finestra di dialogo che consente all'utente di effettuare una scelta
	 *
	 * @param message Messaggio visualizzato nella finestra
	 * @param items   Elementi della scelta
	 * @return La scelta dell'utente
	 */
	public String showComboDialog(String message, String[] items) {
		JLabel messageLabel = new JLabel(message);
		JComboBox comboBox = new JComboBox(items);

		JOptionPane.showMessageDialog(null, new Object[] { messageLabel, comboBox }, //
				"Compositore Java", JOptionPane.QUESTION_MESSAGE, null);

		return comboBox.getSelectedItem().toString();
	}
}
