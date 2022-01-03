package it.saga.library.reportGeneratoreModelli;

import it.saga.library.controls.CtlWCNMenu;
import it.saga.library.controls.CtlWCNMenuBar;
import it.saga.library.controls.CtlWCNMenuItem;
import it.saga.library.localization.LocLocalizator;
import it.saga.library.reportGeneratoreModelli.frame.RpaFRMRicaricaTabelleConfigurazione;
import it.saga.pubblici.menu.MnuFRMBaseMenu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RpaFRMMenu extends MnuFRMBaseMenu {
  private CtlWCNMenuBar menuBar = new CtlWCNMenuBar();
  private CtlWCNMenu menuAttivita = new CtlWCNMenu();
  private CtlWCNMenuItem menuRicaricaTabelle = new CtlWCNMenuItem();
  
  private static final LocLocalizator locale = new LocLocalizator("RpaFRMMenu", LocLocalizator.getUserLocale());
  
  public RpaFRMMenu() {
    super();
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(new Dimension(402, 312));
    menuAttivita.setText(locale.get("menuAttivita"));
    menuRicaricaTabelle.setText(locale.get("menuRicaricaTabelle"));
    menuRicaricaTabelle.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent actionEvent) {
        }
      });
    menuAttivita.add(menuRicaricaTabelle);
    menuBar.add(menuAttivita);
    setJMenuBar(menuBar);
  }
  
  private void menuRicaricaTabelle_actionPerformed(ActionEvent e){
    RpaFRMRicaricaTabelleConfigurazione ricaricaTabelleFRM = new RpaFRMRicaricaTabelleConfigurazione();
    
    this.openForm(ricaricaTabelleFRM);
  }
}
