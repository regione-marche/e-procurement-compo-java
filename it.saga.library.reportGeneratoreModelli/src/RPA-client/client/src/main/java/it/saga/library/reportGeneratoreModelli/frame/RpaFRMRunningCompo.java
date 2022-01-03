package it.saga.library.reportGeneratoreModelli.frame;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.authentication.AutUtils;
import it.saga.library.baseForms.BsfFRMPkBaseInternalFrame;
import it.saga.library.baseForms.BsfMsg;
import it.saga.library.common.CmnDateUtil;
import it.saga.library.commonDataTypes.CdtUtils;
import it.saga.library.commonPratiche.Ap1Images;
import it.saga.library.commonPratiche.Ap1Params;
import it.saga.library.controls.CtlWCNButton;
import it.saga.library.controls.grid.CtlGridColumnDescriptor;
import it.saga.library.controls.grid.CtlGridColumnProperty;
import it.saga.library.controls.grid.CtlGridColumnPropertyFactory;
import it.saga.library.controls.grid.CtlWCNAidedGridScroller;
import it.saga.library.controls.grid.event.CtlGridRowValueEvent;
import it.saga.library.controls.grid.event.CtlGridSelectionEvent;
import it.saga.library.controls.grid.listener.CtlGridGlobalSelectionAdapter;
import it.saga.library.controls.grid.listener.CtlGridRowValueAdapter;
import it.saga.library.controls.grid.tablecell.behaviour.CtlTableCellBehaviour_String;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaCostanti;
import it.saga.library.reportGeneratoreModelli.misc.RpaProcessInfo;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;
import it.saga.library.workflow.WkfImages;

import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class RpaFRMRunningCompo extends BsfFRMPkBaseInternalFrame{

    private CtlWCNAidedGridScroller gridProcess = new CtlWCNAidedGridScroller();
    private CtlWCNButton btnRefresh = new CtlWCNButton();
    private CtlWCNButton btnList = new CtlWCNButton();
    private CtlWCNButton btnKillAll = new CtlWCNButton();
    private CtlWCNButton btnInfoExe = new CtlWCNButton();

    private Thread refresher = null ;

    public RpaFRMRunningCompo() {
        try {
            jbInit();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {

        this.setSize(new Dimension(600, 300));
        this.setTitle("Processi compositore");
        this.setResizable(true);

        this.getBtnClear().setVisible(false);
        this.getBtnDelete().setVisible(false);
        this.getBtnSave().setVisible(false);

        btnRefresh = this.getToolbar().addNewCtlButton();
        btnRefresh.setIcon(Ap1Images.ICONA_REFRESH_24);
        btnRefresh.setSize(new Dimension(65, 20));
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnRefresh_actionPerformed(e);
            }
        });
        btnRefresh.setToolTipText("Aggiorna");
        btnRefresh.applyToolbarIconAndTextStyle();

        btnList = this.getToolbar().addNewCtlButton();
        btnList.setIcon(WkfImages.consoleRun24);
        btnList.setSize(new Dimension(65, 20));
        btnList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnList_actionPerformed(e);
            }
        });
        btnList.setToolTipText("Lista Processi");
        btnList.applyToolbarIconAndTextStyle();

        btnKillAll = this.getToolbar().addNewCtlButton();
        btnKillAll.setIcon(WkfImages.skull24);
        btnKillAll.setSize(new Dimension(65, 20));
        btnKillAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnKillAll_actionPerformed(e);
            }
        });
        btnKillAll.setToolTipText("Chiude tutti i compo zombie");
        btnKillAll.applyToolbarIconAndTextStyle();

        btnInfoExe = this.getToolbar().addNewCtlButton();
        btnInfoExe.setIcon(WkfImages.information2_24);
        btnInfoExe.setSize(new Dimension(65, 20));
        btnInfoExe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnInfoExe_actionPerformed(e);
            }
        });
        btnInfoExe.setToolTipText("Informazione file compo");
        btnInfoExe.applyToolbarIconAndTextStyle();

        gridProcess.addGridRowValueListener(new CtlGridRowValueAdapter() {
                public void rowDeleted(CtlGridRowValueEvent e) {
                    gridProcess_rowDeleted(e);
                }
            });
        gridProcess.addGridGlobalSelectionListener(new CtlGridGlobalSelectionAdapter() {
            public void selectionChanged(CtlGridSelectionEvent e) {
                gridProcess_selectionChanged(e);
            }
        });
        this.getContentPane().add(gridProcess, BorderLayout.CENTER);
    }

    private void init() throws Exception {

        gridProcess.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        gridProcess.getNewRowButton().setVisible(false);
        gridProcess.setRowDeleteEnabled(false);
        CtlGridColumnPropertyFactory pf = null;
        this.gridProcess.setColumns(
            RpaProcessInfo.class,
            new CtlGridColumnDescriptor[]{
                new CtlGridColumnDescriptor("PID","pid",-50,false),
                new CtlGridColumnDescriptor("File","nomeFile",100,false),
                new CtlGridColumnDescriptor( new CtlGridColumnProperty[] {
                    pf.newDescription("Tempo"),
                    pf.newPreferredWidth( 100 ),
                    pf.newField( "elapsedSecs" ),
                    pf.newBehaviourInstance( new CtlTableCellBehaviour_String(
                      gridProcess.getGrid(),
                      CtlTableCellBehaviour_String.HORIZONTALALIGNMENT_CENTER
                    )),
                    pf.newEditable(false)
                }),
                new CtlGridColumnDescriptor("Avviato da","logonUser",100,false),
                new CtlGridColumnDescriptor("cmd/sh","command",300,false),
            }
        );

        refresh();

        this.refresher = new Thread() {
          public void run() {
            try {
              while( true ) {
                this.sleep( 1000 );
                RpaFRMRunningCompo.this.gridProcess.getGrid().repaint();
              }
            } catch( InterruptedException exc ) {}
          }
        };
        this.refresher.start();
    }

    private void gridProcess_rowDeleted(CtlGridRowValueEvent e) {
        kill((RpaProcessInfo)e.getOldRowObject());
        refresh();
    }

    private void btnRefresh_actionPerformed(ActionEvent e){
        refresh();
    }

    private void btnList_actionPerformed(ActionEvent e){
        String lista = null;
        try{
             lista = RpaUtils.getBLG().getListaProcessi(AutUtils.getSession());
        }catch(SagaException se){
            se.show(this);
        }catch(RemoteException re){
            CdtUtils.handleRemoteExceptionsAndReturn(re).show(this);
        }

        BsfMsg.info(this, lista);
    }

    private void btnKillAll_actionPerformed(ActionEvent e){
        int out = 0;
        try{
            out = RpaUtils.getBLG().killAllProcess(AutUtils.getSession());
        }catch(SagaException se){
            se.show(this);
        }catch(RemoteException re){
            CdtUtils.handleRemoteExceptionsAndReturn(re).show(this);
        }

        if(out==0){
           BsfMsg.info(this,"Comando disponibile solo in caso di server linux");
        }

        refresh();
    }

    private void kill(RpaProcessInfo pi){
        try{
            RpaUtils.getBLG().killProcess(AutUtils.getSession(),pi.getPid());
        }catch(SagaException se){
            se.show(this);
        }catch(RemoteException re){
            CdtUtils.handleRemoteExceptionsAndReturn(re).show(this);
        }
        refresh();
    }

    private void refresh(){
        try{
            HashMap<Long,RpaProcessInfo> map = RpaUtils.getBLG().getPidList(AutUtils.getSession());

            List<RpaProcessInfo> lista = new ArrayList<RpaProcessInfo>();
            Iterator itr = map.keySet().iterator();
            while( itr.hasNext()) {
                lista.add(map.get(itr.next()));
            }
            Collections.sort(lista);

            gridProcess.setRows(lista);
        }catch(SagaException se){
            se.show(this);
        }catch(RemoteException re){
            CdtUtils.handleRemoteExceptionsAndReturn(re).show(this);
        }
    }

    private void gridProcess_selectionChanged(CtlGridSelectionEvent e) {
        gridProcess.setRowDeleteEnabled(gridProcess.getSelectedRow()>-1);
    }


    private void btnInfoExe_actionPerformed(ActionEvent e){
        try{
            AutCFGUserSession session = AutUtils.getSession();
            RpaProcessInfo rpi = RpaUtils.getBLG().getInfoEseguibile(session);
            String info = "File: " + rpi.getPathFile() + "\n"
                        + "Size: " + rpi.getSize() + "\n"
                        + "Last Update: " + CmnDateUtil.dateToString(rpi.getLastModified(), "dd/MM/yyyy  HH:mm");
            if(rpi.isF64()){
                info += "\nOS LINUNX - Abilitata modalita compatibilita' 64bit";
            }
            String val = Ap1Params.getParametroString(session, RpaCostanti.APP_PREFIX,RpaCostanti.PARAMETRO_GLOBALE_NON_AGGIORNARE_ESEGUIBILE);
            if(RpaCostanti.VALORE_SI.equals(val)){
                info += "\nAbilitata modalita: \"Non aggiornare l'eseguibile e librerie\"";
            }
            BsfMsg.info(this, info);
        }catch(SagaException se){
            se.show(this);
        }catch(RemoteException re){
            CdtUtils.handleRemoteExceptionsAndReturn(re).show(this);
        }
    }

}
