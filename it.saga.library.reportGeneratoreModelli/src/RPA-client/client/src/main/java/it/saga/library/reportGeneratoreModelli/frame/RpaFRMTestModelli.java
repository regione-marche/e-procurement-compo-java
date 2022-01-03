package it.saga.library.reportGeneratoreModelli.frame;

import it.saga.library.authentication.AutCFGClientSession;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.baseForms.BsfFRMBaseInternalFrame;
import it.saga.library.commonDataTypes.CdtUtils;
import it.saga.library.controls.CtlWCNButton;
import it.saga.library.controls.CtlWCNFileBox;
import it.saga.library.controls.CtlWCNLabel;
import it.saga.library.controls.CtlWCNTextBox;
import it.saga.library.digitalsignature.grafometrica.DsgFirmaGrafometrica;
import it.saga.library.digitalsignature.grafometrica.DsgFirmaGrafometrica.DsgFirmaGrafometricaResult;
import it.saga.library.digitalsignature.grafometrica.DsgFirmaGrafometricaDoc;
import it.saga.library.localization.LocLocalizator;
import it.saga.library.messages.Msg;
import it.saga.library.messages.MsgDACMessage;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;
import it.saga.library.repository.RepDACDocument;
import it.saga.library.repository.RepDocDeletionEnum;
import it.saga.library.repository.RepUtils;
import it.saga.pubblici.anagrafeUnica.An1DACAnagrafeUnica;
import it.saga.pubblici.praticheEdilizie.ApeUtils;
import oracle.jdeveloper.layout.VerticalFlowLayout;
import oracle.jdeveloper.layout.XYConstraints;
import oracle.jdeveloper.layout.XYLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.rmi.RemoteException;


public class RpaFRMTestModelli extends BsfFRMBaseInternalFrame {

    private static final LocLocalizator loc = new LocLocalizator("RpaFRMTestModelli");

    private JPanel pnlMain = new JPanel();
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private CtlWCNLabel lblInfoFrame = new CtlWCNLabel();

    private JPanel pnlModello = new JPanel();

    private CtlWCNFileBox edtFileModello = new CtlWCNFileBox();

    private JPanel pnlStampa = new JPanel();
    private CtlWCNButton btnCreaStampa = new CtlWCNButton();

    private CtlWCNTextBox edtParam = new CtlWCNTextBox();
    private CtlWCNTextBox edtWCompoParam = new CtlWCNTextBox();

    private JLabel lblParam = new JLabel();
    private JLabel lblWCompoParam = new JLabel();
    private CtlWCNButton btnLog = new CtlWCNButton();
    private CtlWCNLabel lblModello = new CtlWCNLabel();
    private XYLayout xYLayout1 = new XYLayout();
    private JRadioButton jRadioButton1 = new JRadioButton();
    private JCheckBox chkNewCompo = new JCheckBox();
    private JCheckBox chkFirma = new JCheckBox();

    private CtlWCNButton btnCancellaLog = new CtlWCNButton();

    public RpaFRMTestModelli() {
        try {
            jbInit();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(new BorderLayout());
        // this.setSize(new Dimension(500, 233));
        this.setSize(new Dimension(700, 233));
        this.setTitle(loc.get("windowTitle"));

        pnlMain.setLayout(verticalFlowLayout1);

        lblInfoFrame.setText(loc.get("lblInfoFrame"));
        lblInfoFrame.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfoFrame.setHorizontalTextPosition(SwingConstants.CENTER);
        lblInfoFrame.setFont(new Font("Tahoma", 1, 13));
        lblInfoFrame.setForeground(Color.blue);
        lblInfoFrame.setPreferredSize(new Dimension(200, 14));

        pnlModello.setLayout(new BorderLayout());
        pnlModello.setPreferredSize(new Dimension(100, 20));
        pnlModello.add(edtFileModello, BorderLayout.CENTER);


        pnlStampa.setLayout(xYLayout1);
        pnlStampa.setPreferredSize(new Dimension(100, 50));
        btnCreaStampa.setText(loc.get("btnCreaStampa"));
        btnCreaStampa.setPreferredSize(new Dimension(110, 20));
        btnCreaStampa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnCreaStampa_actionPerformed(e);
            }
        });
        pnlStampa.add(chkNewCompo, new XYConstraints(145, 0, 195, 20));
        pnlStampa.add(chkFirma, new XYConstraints(355, 0, 195, 20));
        pnlStampa.add(btnCreaStampa, new XYConstraints(560, 0, 110, 20));
        pnlStampa.add(btnLog, new XYConstraints(5, 0, 110, 20));
        pnlStampa.add(btnCancellaLog, new XYConstraints(5, 25, 666, 20));
        // pnlStampa.add(btnCancellaLog, new XYConstraints(5, 25, 465, 20));
        edtParam.setPreferredSize(new Dimension(556, 20));
        edtWCompoParam.setPreferredSize(new Dimension(556, 20));
        lblParam.setText(loc.get("lblParam"));
        lblWCompoParam.setText(loc.get("lblWCompoParam"));
        btnLog.setText(loc.get("btnLog"));
        btnLog.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    btnLog_actionPerformed(e);
                }
            });
        lblModello.setText(loc.get("lblModello"));
        jRadioButton1.setText("jRadioButton1");
        chkNewCompo.setText(loc.get("chkNewCompo"));
        chkNewCompo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                chkNewCompo_itemStateChanged(e);
            }
        });
        chkFirma.setText(loc.get("chkFirma"));
        btnCancellaLog.setText(loc.get("btnCancellaLog"));
        btnCancellaLog.setVisible(false);
        btnCancellaLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancellaLog_actionPerformed(e);
            }
        });
        pnlMain.add(lblInfoFrame);
        pnlMain.add(lblModello, null);
        pnlMain.add(pnlModello,null);
        pnlMain.add(lblParam, null);
        pnlMain.add(edtParam, null);
        pnlMain.add(lblWCompoParam, null);
        pnlMain.add(edtWCompoParam, null);
        pnlMain.add(pnlStampa,null);
        this.add(pnlMain, BorderLayout.CENTER);
    }

    private void init() throws Exception {
//        edtParam.setValue(loc.get("edtParamInit"));
//        edtWCompoParam.setValue(loc.get("edtWCompoParam"));
//        btnLog.setToolTipText(loc.get("btnLogToolTip"));
    }

    public void setParamDefault(String param){
        edtParam.setText(param);
    }

    public void setWCompoParamDefault(String param){
        edtWCompoParam.setText(param);
    }

    private Long salvaFileInRepo(AutCFGUserSession session,String url)
          throws RemoteException,SagaException {
        File fileDaCaricare = new File(url);
        RepDACDocument document = new RepDACDocument(fileDaCaricare.getName(),"Modello di Stampa per prova");
        document.readFromFile(fileDaCaricare);
        document = RepUtils.getBLG().docInsert(session,document);
        return document.getPkid();
    }


    private void btnCreaStampa_actionPerformed(ActionEvent e) {
        Long idRepoOut = null;
        String urlModelloServer = null;
        Long idRepoModello = null;
        RepDACDocument compoOut = null;

        AutCFGUserSession session = AutCFGClientSession.getUserSession();

        try {
            this.setWaitCursor(true);

            boolean isApplySignature = chkFirma.isSelected() && chkNewCompo.isSelected();

            //carica il modello
            if (edtFileModello.getFile() == null) {
                new MsgDACMessage(MsgDACMessage.TYPE_INFORMATION, this.getClass(), "msgSelezionaFile").show(this);
                return;
            } else {
                urlModelloServer = edtFileModello.getFile().getAbsolutePath();
            }

            idRepoModello = salvaFileInRepo(session, urlModelloServer);
            urlModelloServer = RpaUtils.getBLG().creaFileTempDaRepo(session, idRepoModello, isApplySignature);

            if(urlModelloServer!=null){
                idRepoOut =RpaUtils.getBLG().runCompositoreTest(
                    session,
                    urlModelloServer,
                    edtParam.getText(),
                    edtWCompoParam.getText(),
                    chkNewCompo.isSelected(),
                    isApplySignature
                );
            }

            if(idRepoOut!=null){

                compoOut = (RepDACDocument)RepUtils.getBLG().read(session,new RepDACDocument(),idRepoOut);

                // Appongo la firma grafometrica
                if (isApplySignature) {

                    DsgFirmaGrafometricaDoc firmaGrafometricaDoc = new DsgFirmaGrafometricaDoc(
                            compoOut,
                            new An1DACAnagrafeUnica[]{session.getLogonUser().getAnagUnica()}
                    );

                    DsgFirmaGrafometricaResult result = DsgFirmaGrafometrica.firmaDocumenti(this, firmaGrafometricaDoc);
                    if (!DsgFirmaGrafometricaResult.Status.OK.equals(result.getStatus())) {
                        return;
                    }
                    DsgFirmaGrafometricaDoc[] signedDocs = result.getSignedDocs();
                    if (signedDocs.length > 0 && signedDocs[0] != null) {

                        // signedDocs[0].getDoc().show();

                    } else {

                        compoOut.show();

                    }

                } else {

                    compoOut.show();

                }

            }else{
                new MsgDACMessage(
                    MsgDACMessage.TYPE_ERROR,
                    this.getClass(),
                    "msgStampaKO"
                ).show(this);
            }

        } catch (Exception exc) {
            SagaException se = new SagaException(exc);
            se.getMessageContainer().addNoThrow(
                new MsgDACMessage(
                    MsgDACMessage.TYPE_ERROR,
                    this.getClass(),
                    "msgStampaKO"
                )
            );
            se.show(this);
        } finally {
            //cancella i file caricati
            try{
                //cancella il modello
                if(urlModelloServer!=null && idRepoModello!=null){
                    ApeUtils.getBLG().deleteFileTemp(session, urlModelloServer, idRepoModello);
                }

                //cancella il risultato
                if(compoOut!=null){
                    RepUtils.getBLG().docDelete(session, compoOut, RepDocDeletionEnum.DELETE_TOTAL_IMMEDIATE );
                }

                this.setWaitCursor(false);
            }catch(Exception se){
                new SagaException(se).show(this);
            }
        }
    }

    private void btnLog_actionPerformed(ActionEvent e) {

        // Se ho selezionato il nuovo compositore, ne mostro il log
        if (chkNewCompo.isSelected()) {

            RpaFRMLogNewCompo frmCompoNewLog = new RpaFRMLogNewCompo();
            this.openModal(frmCompoNewLog);

        }

        // Se non ho selezionato il nuovo compositore, mostro il log del vecchio
        else {

            Long idRepoLog = null;
            RepDACDocument compoLog = null;
            AutCFGUserSession session = AutCFGClientSession.getUserSession();

            try {

                idRepoLog = RpaUtils.getBLG().getFileLogRep(session);

                if (idRepoLog != null) {

                    compoLog = (RepDACDocument) RepUtils.getBLG().read(session, new RepDACDocument(), idRepoLog);
                    compoLog.show();

                } else {

                    new MsgDACMessage(
                            MsgDACMessage.TYPE_ERROR,
                            this.getClass(),
                            "msgLogErrore"
                    ).show(this);

                }

            } catch (Exception exc) {

                new SagaException(exc).show(this);

            } finally {

                if (idRepoLog != null && compoLog != null) {

                    try {

                        RepUtils.getBLG().docDelete(session, compoLog, RepDocDeletionEnum.DELETE_TOTAL_IMMEDIATE);

                    } catch (Exception exc) {
                        new SagaException(exc).show(this);
                    }
                }

            }

        }

    }

    private void chkNewCompo_itemStateChanged(ItemEvent event) {

        // Se la checkbox passa ad 'attiva', mostro i pulsanti per cancellare il log del nuovo compositore
        if (chkNewCompo.isSelected()) {

            btnCancellaLog.setVisible(true);

        }

        // Se la checkbox passa a NON 'attiva', nascondo i pulsanti per cancellare il log del nuovo compositore
        else {

            btnCancellaLog.setVisible(false);

        }

    }

    private void btnCancellaLog_actionPerformed(ActionEvent event) {

        // Chiedo la conferma
        if (Msg.warningYesNo(null, RpaFRMTestModelli.class, "msgConfirmCleanLog")) {

            try {

                // Svuoto il log del nuovo compositore
                AutCFGUserSession session = AutCFGClientSession.getUserSession();
                RpaUtils.getBLG().cleanLogNuovoCompositore(session);

            } catch (RemoteException re) {
                CdtUtils.handleRemoteExceptionsAndReturn(re).show(null);

            } catch (SagaException se) {
                se.show(null);
            }

        }

    }

}
