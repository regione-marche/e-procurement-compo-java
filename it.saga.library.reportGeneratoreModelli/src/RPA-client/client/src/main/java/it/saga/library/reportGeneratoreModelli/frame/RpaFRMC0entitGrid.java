package it.saga.library.reportGeneratoreModelli.frame;

import it.saga.library.authentication.AutCFGClientSession;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.baseForms.BsfFRMBaseInternalFrame;
import it.saga.library.baseForms.BsfMsg;
import it.saga.library.common.CmnService;
import it.saga.library.common.look.CmnImages;
import it.saga.library.commonDataTypes.CdtDACGenericDbOperation;
import it.saga.library.commonDataTypes.CdtDACPkBaseClass;
import it.saga.library.commonDataTypes.CdtDACQuery;
import it.saga.library.commonDataTypes.CdtUtils;
import it.saga.library.controls.grid.CtlGridColumnDescriptor;
import it.saga.library.controls.grid.CtlGridColumnProperty;
import it.saga.library.controls.grid.CtlGridColumnPropertyFactory;
import it.saga.library.controls.grid.CtlWCNAidedGridScroller;
import it.saga.library.controls.grid.datasource.CtlGridQueryDataSource;
import it.saga.library.controls.grid.event.CtlGridRowValueEvent;
import it.saga.library.controls.grid.event.CtlGridRowValueVetoableEvent;
import it.saga.library.controls.grid.event.CtlGridSelectionEvent;
import it.saga.library.controls.grid.listener.CtlGridRowValueListener;
import it.saga.library.localization.LocLocalizator;
import it.saga.library.messages.MsgDACContainer;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACC0entit;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;


public class RpaFRMC0entitGrid extends BsfFRMBaseInternalFrame {
  private static final LocLocalizator locale = new LocLocalizator("RpaFRMC0entitGrid", LocLocalizator.getUserLocale());
  
  private CtlWCNAidedGridScroller gridC0entit = new CtlWCNAidedGridScroller();
  private RpaDACC0entit selezionato = null;
  
  public RpaFRMC0entitGrid() {
    super();
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(new Dimension(415, 334));
    this.getContentPane().add(gridC0entit, BorderLayout.CENTER);
    //gridC0entit.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    this.setTitle(locale.get("titolo"));
    this.setResizable(true);
    gridC0entit.setDeletedRowsVisible(true);
    this.addStatusbarToContentPane(); // Quella sotto ad una finestra che d� un po' di messaggi
    gridC0entit.setToolbarLocation(BorderLayout.NORTH); // sposta la toolbar in alto
    JButton save = new JButton(CmnImages.Save); 
    save.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
             saveAction();
        }
      });
    gridC0entit.getToolBar().add(save);
    JButton detail = new JButton(CmnImages.Details);
    detail.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
             detailAction2();
        }
      });
    JButton refresh = new JButton(CmnImages.Refresh);
    refresh.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
             refreshGrid2();
        }
      });
    gridC0entit.getToolBar().add(detail);
    gridC0entit.getToolBar().add(refresh);    
    // Rimuove il tasto per aggiungere una riga. N.B. l'utente pu� ancora usare gli shortcut da tastiera
    // gridC0entit.getToolBar().remove(0);
    // Evento che funziona solo al CAMBIO di riga. Se la riga resta la stessa, anche selezionata, non serve a nulla.
    /*gridC0entit.addGridGlobalSelectionListener(new CtlGridGlobalSelectionListener() {
               public void selectionChanged(CtlGridSelectionEvent e) {
                   gridC0entit_selectionChanged(e);
               }
           });*/
    gridC0entit.addGridRowValueListener(new CtlGridRowValueListener(){
        public void rowAdding(CtlGridRowValueVetoableEvent e) {                 
          RpaFRMC0entitDetail detail = new RpaFRMC0entitDetail();               
          detail.setSaveAllowed(false); // impedisce il salvataggio del dac dalla frame
          detail.addSelectionButton(); // aggiunge un bottone ok, annulla
          detail.setPersistedDACSelection(false); // dice che non occorre questo dac abbia il pkid settato
          openModal(detail);          
          // chiamato dal tastino verde aggiunto con addOkButton
          RpaDACC0entit nuovo = (RpaDACC0entit)detail.getSelectedObject();
          if (nuovo != null) {             
            e.setNewRowObject(nuovo);                       
          }else{
            e.setVeto(); // interrompe la catena di eventi, quindi non aggiunge una riga vuota
          }
        }

        public void rowAdded(CtlGridRowValueEvent e) {
          int rowAddedIdx = e.getRowIndex(); 
          setStatusMessage("Added.");
        }

        public void rowChanging(CtlGridRowValueVetoableEvent e) {
          setStatusMessage("Changing.");
        }

        public void rowChanged(CtlGridRowValueEvent e) {
          setStatusMessage("Changed.");
        }

        public void rowDeleting(CtlGridRowValueVetoableEvent e) {
        }

        public void rowDeleted(CtlGridRowValueEvent e) {
        }
      });
  }
  
  private void init() throws Exception {    
    gridC0entit.setColumns(RpaDACC0entit.class,
                           new CtlGridColumnDescriptor[] { new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_nom"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eNom"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(200),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(true),
                                                                                                                     CtlGridColumnPropertyFactory.newTooltip("c0e_nom")}),
                                                           /*new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_num"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eNum"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(12),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_tip"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eTip"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(1),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("coe_arg"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("coeArg"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(10),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),*/
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_des"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eDes"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(460),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(true) }),
                                                           /*new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_key"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eKey"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(190),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_nom_ex"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eNomEx"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(35),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("coe_key_ex"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("coeKeyEx"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(190),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("c0e_frm_ri"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("c0eFrmRi"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(10),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("coe_frm_ca"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("coeFrmCa"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(10),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) }),
                                                           new CtlGridColumnDescriptor(new CtlGridColumnProperty[] { CtlGridColumnPropertyFactory.newDescription("coe_frm_re"),
                                                                                                                     CtlGridColumnPropertyFactory.newField("coeFrmRe"),
                                                                                                                     CtlGridColumnPropertyFactory.newPreferredWidth(10),
                                                                                                                     CtlGridColumnPropertyFactory.newEditable(false) })*/
           });
    //refreshGrid1();
    refreshGrid2();
  }
  
  private void refreshGrid1() {   
    List c0entit;
    try {
      c0entit = CdtDACGenericDbOperation.readCollectionClientSide("from ApeDACC0entit order by c0e_nom");
      c0entit=CdtUtils.postProcessHQLResult(c0entit);
      gridC0entit.setRows(c0entit);     
    } catch (SagaException e) {
      e.show(this);
    }
  }
  
  private void refreshGrid2(){ // da preferirsi perch� fa query paginate (default 100 righe)
    List c0entit;
    try {
      CdtDACQuery queryUtenze=new CdtDACQuery();
      queryUtenze
        .from("ApeDACC0entit f")
        .orderBy("f.c0eNom")
        .withAlias("f"); // OBBLIGATORIO
      CtlGridQueryDataSource dataSource=new CtlGridQueryDataSource(queryUtenze);
      gridC0entit.setDataSource(dataSource);    // sorgente dati per la griglia
    } catch (SagaException e) {
      e.show(this);
    }
  }
  
  protected boolean saveAction()
  {
    try
    {
      setWaitCursor(true); // trasforma il contatore in clessidra (usare sempre in un try catch)
      gridC0entit.forceStopCellEditing();
      List dacs = gridC0entit.getAlteredDACs(); // ritorna tutti i dac con lo stato modificato (CRUD)
      if (dacs.size() > 0)
      {
        setStatusMessage(locale.get("saving")); // messaggio sulla barra sotto
        // salva tutti i dac (uno per volta). Questo metodo chiama direttamente l'ejb di commonService. Non il dac
        MsgDACContainer msg = CmnService.updateCollection(dacs);
        if (msg != null) msg.show(this);
        setStatusMessage(locale.get("saveOK"));
      }
      else
      {
        BsfMsg.info(this, locale.get("nothingToSave"));
      }
      return true;
    }
    catch (SagaException ex)
    {
      setStatusMessage("");
      ex.show(this);
      return false;
    }
    finally
    {
      setWaitCursor(false); // toglie la clessidra
      gridC0entit.clear(); // cancella la cache della griglia
      refreshGrid2(); // refresha la griglia
    }
  }
  
  protected boolean saveAction2() // uguale all'1
  {
    try {
      setWaitCursor(true);
      gridC0entit.forceStopCellEditing();

      AutCFGUserSession session = AutCFGClientSession.getUserSession();

      List dacs = gridC0entit.getAlteredDACs();
      if (dacs.size() > 0) {
        setStatusMessage(locale.get("saving"));
        try {
          MsgDACContainer msg = RpaUtils.getBLG().updateCollection(session, dacs); // Ritorna l'Ape ejb (updateCollection ereditato)
          if (msg != null)
            msg.show(this);
        } catch (RemoteException e) {
          throw new SagaException(e);
        }
        setStatusMessage(locale.get("saveOK"));
      } else {
        BsfMsg.info(this, locale.get("nothingToSave"));
      }
      return true;
    } catch (SagaException ex) {
      setStatusMessage("");
      ex.show(this);
      return false;
    } finally {
      setWaitCursor(false);
      gridC0entit.clear();
      refreshGrid2();
    }
  }
  
  // aggiorna i dac chiamando i giusti metodi (save se update, delete se delete) dac per dac 
  // dalla lista dei modificati, in base al CRUD
  protected boolean saveAction3()
  {
    try {
      setWaitCursor(true);
      gridC0entit.forceStopCellEditing();

      AutCFGUserSession session = AutCFGClientSession.getUserSession();

      List dacs = gridC0entit.getAlteredDACs();
      if (dacs.size() > 0) {
        setStatusMessage(locale.get("saving"));
        Iterator it = dacs.iterator();
        while (it.hasNext()) {
          try {
            CdtDACPkBaseClass dac = (CdtDACPkBaseClass)it.next();
            if(dac.isDeleted()){ // legge lo stato CRUD del dac
              RpaUtils.getBLG().delete(session, dac);
            }else{
              RpaUtils.getBLG().save(session, dac);
            }
          } catch (RemoteException e) {
            throw new SagaException(e);
          }
        }
      }
      return true;
    } catch (SagaException ex) {
      setStatusMessage("");
      ex.show(this);
      return false;
    } finally {
      setWaitCursor(false);
      gridC0entit.clear();
      refreshGrid2();
    }
  }
  
  /**
   * Invocato prima della chiusura della maschera permette 
   * @return true se si vuole procedere con la chiusura della maschera, 
   *         false per bloccare la chiusura
   */
  protected boolean beforeClose()
  {
    /*if (!Msg.conferma("Stai chiudendo la maschera. Sei veramente sicuro?")) return false;
    return super.beforeClose()*/
    if(altered()){
      // ritorna false se annullo. Se l'utente clicca su salva e esci, viene chiamata la save (di chi?).
      if(!askToSaveIfAltered(true)) // true come argomento permette di uscire senza salvare
        return false; // non chiudo la finestra.
     // BsfMsg.warn(this, "Hai modificato dati!");
      
    }
    return true;
  }

  
  /**
   * Indica se i dati a video sono stati modificati rispetto a quelli caricati
   * @return true --> dati modificati 
   */
  protected boolean altered()
  {  
    if (gridC0entit.isDirty()) return true;

    return super.altered();
  }
  
  private void gridC0entit_selectionChanged(CtlGridSelectionEvent e) {   
  }
  
  protected boolean detailAction(){
    RpaFRMC0entitDetail detail = new RpaFRMC0entitDetail();
    // quando la frmPKBase salva, chiama il dac, quindi deve essere implementato il metodo initEJB
    detail.setDataSource(selezionato);           
    openModal(detail);
    if(detail.hasBeenSaved()){
      this.refreshGrid1();      
    }    
    return true;
  }
  
  protected boolean detailAction2(){
    selezionato = (RpaDACC0entit)gridC0entit.getSelectedObject();

    if (selezionato != null) {
      RpaFRMC0entitDetail detail = new RpaFRMC0entitDetail();
      detail.setSaveAllowed(false); // impedisce il salvataggio del dac dalla frame
      detail.addSelectionButton(); // aggiunge un bottone ok, annulla
      detail.setPersistedDACSelection(false);
      // quando la frmPKBase salva, chiama il dac, quindi deve essere implementato il metodo initEJB
      detail.editDataSource(selezionato);
      detail.setCloseOnSaveOrDelete(true); // chiude la finestra dopo un salvataggio o una cancellazione
      int rowIndex = gridC0entit.getRowIndex(selezionato); // ritorna l'indice della riga selezionata
      openModal(detail);
      selezionato = (RpaDACC0entit)detail.getSelectedObject(); // chiamato dal tastino verde aggiunto con addOkButton
      if (selezionato != null) {
        gridC0entit.updateDAC(rowIndex, selezionato);
      }
    }
    return true;
  }
}
