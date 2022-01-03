package it.saga.library.reportGeneratoreModelli.frame;

import it.saga.library.authentication.AutCFGClientSession;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.baseForms.BsfFRMBaseInternalFrame;
import it.saga.library.controls.CtlWCNButton;
import it.saga.library.controls.CtlWCNFileBox;
import it.saga.library.controls.CtlWCNLabel;
import it.saga.library.localization.LocLocalizator;
import it.saga.library.messages.MsgDACMessage;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;
import oracle.jdeveloper.layout.VerticalFlowLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RpaFRMRicaricaTabelleConfigurazione extends BsfFRMBaseInternalFrame {
  private static final LocLocalizator loc = new LocLocalizator("RpaFRMRicaricaTabelleConfigurazione");

  private JPanel pnlMain = new JPanel();
  private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  private CtlWCNLabel jLabelSoloSviluppo = new CtlWCNLabel();
  private CtlWCNLabel jLabelStessaMacchina = new CtlWCNLabel();

  private JPanel pnlC0campi = new JPanel();
  private CtlWCNFileBox edtFileC0campi = new CtlWCNFileBox();
  private CtlWCNButton btnCaricaC0campi = new CtlWCNButton();

  private BorderLayout borderLayout1 = new BorderLayout();

  private JPanel pnlC0entit = new JPanel();
  private CtlWCNFileBox edtFileC0entit = new CtlWCNFileBox();
  private CtlWCNButton btnCaricaC0entit = new CtlWCNButton();

  public RpaFRMRicaricaTabelleConfigurazione() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(new BorderLayout());
    this.setSize(new Dimension(400, 279));
    this.setTitle(loc.get("windowTitle"));

    btnCaricaC0campi.setText(loc.get("btnCaricaC0campi"));
    btnCaricaC0campi.setPreferredSize(new Dimension(110, 20));
    btnCaricaC0campi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnCaricaC0campi_actionPerformed(e);
      }
    });
    pnlMain.setLayout(verticalFlowLayout1);

    jLabelSoloSviluppo.setText(loc.get("jLabelSoloSviluppo"));
    jLabelSoloSviluppo.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelSoloSviluppo.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabelSoloSviluppo.setFont(new Font("Tahoma", 1, 11));
    jLabelSoloSviluppo.setForeground(Color.red);
    jLabelSoloSviluppo.setPreferredSize(new Dimension(200, 14));

    jLabelStessaMacchina.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelStessaMacchina.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabelStessaMacchina.setText(loc.get("jLabelStessaMacchina"));
    jLabelStessaMacchina.setFont(new Font("Tahoma", 1, 11));
    jLabelStessaMacchina.setForeground(Color.red);

    pnlC0campi.setLayout(new BorderLayout());
    pnlC0campi.setPreferredSize(new Dimension(100, 20));
    pnlC0campi.add(edtFileC0campi, BorderLayout.CENTER);
    pnlC0campi.add(btnCaricaC0campi, BorderLayout.EAST);
    pnlMain.add(jLabelSoloSviluppo);
    pnlMain.add(jLabelStessaMacchina);
    pnlMain.add(pnlC0campi);


    pnlC0entit.setLayout(new BorderLayout());
    pnlC0entit.setPreferredSize(new Dimension(100, 20));
    btnCaricaC0entit.setText(loc.get("btnCaricaC0entit"));
    btnCaricaC0entit.setPreferredSize(new Dimension(110, 21));
    btnCaricaC0entit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnCaricaC0entit_actionPerformed(e);
      }
    });
    pnlC0entit.add(edtFileC0entit, BorderLayout.CENTER);
    pnlC0entit.add(btnCaricaC0entit, BorderLayout.EAST);
    pnlMain.add(pnlC0entit);
    this.add(pnlMain, BorderLayout.CENTER);
  }

  private void btnCaricaC0campi_actionPerformed(ActionEvent e) {
    try {
      this.setWaitCursor(true);
      AutCFGUserSession session = AutCFGClientSession.getUserSession();
      String urlC0campi = null;
      if (edtFileC0campi.getFile() == null) {
        new MsgDACMessage(MsgDACMessage.TYPE_INFORMATION, this.getClass(), "msgSelezionaFile").show(this);
        return;
      } else {
        urlC0campi = edtFileC0campi.getFile().getAbsolutePath();
      }
      RpaUtils.getBLG().caricaC0campi(session, urlC0campi);
      //RpaUtils.getDBMapping(true);
      new MsgDACMessage(MsgDACMessage.TYPE_INFORMATION, this.getClass(), "msgC0campi").show(this);

    } catch (Exception exc) {
      new SagaException(exc).show(this);
    } finally {
      this.setWaitCursor(false);
    }

  }

  private void btnCaricaC0entit_actionPerformed(ActionEvent e) {
    try {
      this.setWaitCursor(true);
      AutCFGUserSession session = AutCFGClientSession.getUserSession();
      String urlC0entit = null;
      // metodo getFile ritorna un oggetto di tipo java.io.File
      if (edtFileC0entit.getFile() == null) {
        new MsgDACMessage(MsgDACMessage.TYPE_INFORMATION, this.getClass(), "msgSelezionaFile").show(this);
        return;
      } else {
        urlC0entit = edtFileC0entit.getFile().getAbsolutePath();
      }
      RpaUtils.getBLG().caricaC0entit(session, urlC0entit);

      new MsgDACMessage(MsgDACMessage.TYPE_INFORMATION, this.getClass(), "msgC0entit").show(this);

    } catch (Exception exc) {
      new SagaException(exc).show(this);
    } finally {
      this.setWaitCursor(false);
    }
  }
}
