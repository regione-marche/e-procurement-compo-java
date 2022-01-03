package it.saga.library.reportGeneratoreModelli.frame;

import it.saga.library.baseForms.BsfFRMPkBaseInternalFrame;
import it.saga.library.baseForms.BsfMsg;
import it.saga.library.commonDataTypes.CdtDACPkBaseClass;
import it.saga.library.controls.CtlWCNLabel;
import it.saga.library.controls.CtlWCNNumericBox;
import it.saga.library.controls.CtlWCNTextBox;
import it.saga.library.localization.LocLocalizator;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACC0entit;
import oracle.jdeveloper.layout.VerticalFlowLayout;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Dimension;


public class RpaFRMC0entitDetail extends BsfFRMPkBaseInternalFrame {
  private static final LocLocalizator locale = new LocLocalizator("RpaFRMC0entitDetail", LocLocalizator.getUserLocale());
  
  private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JPanel jPanel3 = new JPanel();
  private JPanel jPanel4 = new JPanel();
  private JPanel jPanel5 = new JPanel();
  private JPanel jPanel6 = new JPanel();
  private JPanel jPanel7 = new JPanel();
  private JPanel jPanel8 = new JPanel();
  private JPanel jPanel9 = new JPanel();
  private JPanel jPanel10 = new JPanel();
  private JPanel jPanel11 = new JPanel();
  private JPanel jPanel12 = new JPanel();
  private JPanel jPanel13 = new JPanel();
  private CtlWCNLabel c0eNumLabel = new CtlWCNLabel();  
  private CtlWCNLabel c0eNomLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eTipLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eArgLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eDesLabel = new CtlWCNLabel();  
  private CtlWCNLabel c0eKeyLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eFrmRiLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eFrmCaLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eFrmReLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eNomExLabel = new CtlWCNLabel();
  private CtlWCNLabel c0eKeyExLabel = new CtlWCNLabel();
  private CtlWCNLabel appPrefixLabel = new CtlWCNLabel();
  private CtlWCNNumericBox c0eNumNumericBox = new CtlWCNNumericBox();  
  private CtlWCNTextBox c0eNomTextBox = new CtlWCNTextBox();  
  private CtlWCNTextBox c0eTipTextBox = new CtlWCNTextBox();  
  private CtlWCNTextBox c0eArgTextBox = new CtlWCNTextBox();  
  private CtlWCNTextBox c0eDesTextBox = new CtlWCNTextBox();  
  private CtlWCNTextBox c0eKeyTextBox = new CtlWCNTextBox();
  private CtlWCNTextBox c0eFrmRiTextBox = new CtlWCNTextBox();
  private CtlWCNTextBox c0eFrmCaTextBox = new CtlWCNTextBox();
  private CtlWCNTextBox c0eFrmReTextBox = new CtlWCNTextBox();
  private CtlWCNTextBox c0eNomExTextBox = new CtlWCNTextBox();
  private CtlWCNTextBox c0eKeyExTextBox = new CtlWCNTextBox();
  private CtlWCNTextBox appPrefixTextBox = new CtlWCNTextBox();
  private JSeparator jSeparator1 = new JSeparator();
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();
  private BorderLayout borderLayout4 = new BorderLayout();
  private BorderLayout borderLayout5 = new BorderLayout();
  private BorderLayout borderLayout6 = new BorderLayout();
  private BorderLayout borderLayout7 = new BorderLayout();
  private BorderLayout borderLayout8 = new BorderLayout();
  private BorderLayout borderLayout9 = new BorderLayout();
  private BorderLayout borderLayout10 = new BorderLayout();
  private BorderLayout borderLayout11 = new BorderLayout();  
  private BorderLayout borderLayout12 = new BorderLayout();
  
  public RpaFRMC0entitDetail() {
    super();
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(new Dimension(419, 276));
    jPanel1.setLayout(verticalFlowLayout1);
    jPanel2.setLayout(borderLayout1);
    jPanel3.setLayout(borderLayout2);
    jPanel4.setLayout(borderLayout3);
    jPanel5.setLayout(borderLayout4);
    jPanel6.setLayout(borderLayout5);
    jPanel7.setLayout(borderLayout6);
    jPanel8.setLayout(borderLayout7);
    jPanel9.setLayout(borderLayout8);
    jPanel10.setLayout(borderLayout9);
    jPanel11.setLayout(borderLayout10);
    jPanel12.setLayout(borderLayout11);
    jPanel13.setLayout(borderLayout12);
    c0eNumLabel.setText(locale.get("c0e_num"));    
    c0eNomLabel.setText(locale.get("c0e_nom"));    
    c0eTipLabel.setText(locale.get("c0e_tip"));    
    c0eArgLabel.setText(locale.get("c0e_arg"));    
    c0eDesLabel.setText(locale.get("c0e_des"));    
    c0eKeyLabel.setText(locale.get("c0e_key"));
    c0eFrmRiLabel.setText(locale.get("c0e_frm_ri"));
    c0eFrmCaLabel.setText(locale.get("c0e_frm_ca"));
    c0eFrmReLabel.setText(locale.get("c0e_frm_re"));
    c0eNomExLabel.setText(locale.get("c0e_nom_ex"));
    c0eKeyExLabel.setText(locale.get("c0e_key_ex"));
    appPrefixLabel.setText(locale.get("app_prefix"));
    c0eNumNumericBox.setPreferredSize(new Dimension(350, 20));
    c0eNumNumericBox.setDataField("c0eNum");
    c0eNomTextBox.setPreferredSize(new Dimension(350, 20));
    c0eNomTextBox.setDataField("c0eNom");
    c0eNomTextBox.setMaxLength(35);
    c0eTipTextBox.setPreferredSize(new Dimension(350, 20));
    c0eTipTextBox.setDataField("c0eTip");
    c0eTipTextBox.setMaxLength(1);
    c0eArgTextBox.setPreferredSize(new Dimension(350, 20));
    c0eArgTextBox.setDataField("c0eArg");
    c0eArgTextBox.setMaxLength(10);
    c0eDesTextBox.setPreferredSize(new Dimension(350, 20));
    c0eDesTextBox.setDataField("c0eDes");
    c0eDesTextBox.setMaxLength(60);
    c0eKeyTextBox.setPreferredSize(new Dimension(350, 20));
    c0eKeyTextBox.setDataField("c0eKey");
    c0eKeyTextBox.setMaxLength(190);
    c0eFrmRiTextBox.setPreferredSize(new Dimension(350, 20));
    c0eFrmRiTextBox.setDataField("c0eFrmRi");
    c0eFrmRiTextBox.setMaxLength(10);
    c0eFrmCaTextBox.setPreferredSize(new Dimension(350, 20));
    c0eFrmCaTextBox.setDataField("c0eFrmCa");
    c0eFrmCaTextBox.setMaxLength(10);
    c0eFrmReTextBox.setPreferredSize(new Dimension(350, 20));
    c0eFrmReTextBox.setDataField("c0eFrmRe");
    c0eFrmReTextBox.setMaxLength(10);
    c0eNomExTextBox.setPreferredSize(new Dimension(350, 20));
    c0eNomExTextBox.setDataField("c0eNomEx");
    c0eNomExTextBox.setMaxLength(35);
    c0eKeyExTextBox.setPreferredSize(new Dimension(350, 20));
    c0eKeyExTextBox.setDataField("c0eKeyEx");
    c0eKeyExTextBox.setMaxLength(190);
    appPrefixTextBox.setPreferredSize(new Dimension(350, 20));
    appPrefixTextBox.setDataField("appPrefix");        
    appPrefixTextBox.setMaxLength(3);
    jPanel2.add(c0eNumLabel, BorderLayout.WEST);
    jPanel2.add(c0eNumNumericBox, BorderLayout.EAST);
    jPanel3.add(c0eNomLabel, BorderLayout.WEST);
    jPanel3.add(c0eNomTextBox, BorderLayout.EAST);
    jPanel4.add(c0eTipLabel, BorderLayout.WEST);
    jPanel4.add(c0eTipTextBox, BorderLayout.EAST);
    jPanel5.add(c0eArgLabel, BorderLayout.WEST);
    jPanel5.add(c0eArgTextBox, BorderLayout.EAST);
    jPanel6.add(c0eDesLabel, BorderLayout.WEST);
    jPanel6.add(c0eDesTextBox, BorderLayout.EAST);
    jPanel7.add(c0eKeyLabel, BorderLayout.WEST);
    jPanel7.add(c0eKeyTextBox, BorderLayout.EAST);
    jPanel8.add(c0eFrmRiLabel, BorderLayout.WEST);
    jPanel8.add(c0eFrmRiTextBox, BorderLayout.EAST);
    jPanel9.add(c0eFrmCaTextBox, BorderLayout.EAST);
    jPanel9.add(c0eFrmCaLabel, BorderLayout.WEST);
    jPanel10.add(c0eFrmReLabel, BorderLayout.WEST);
    jPanel10.add(c0eFrmReTextBox, BorderLayout.EAST);
    jPanel11.add(c0eNomExLabel, BorderLayout.WEST);
    jPanel11.add(c0eNomExTextBox, BorderLayout.EAST);
    jPanel12.add(c0eKeyExLabel, BorderLayout.WEST);
    jPanel12.add(c0eKeyExTextBox, BorderLayout.EAST);
    jPanel13.add(appPrefixLabel, BorderLayout.WEST);
    jPanel13.add(appPrefixTextBox, BorderLayout.EAST);
    jPanel1.add(jPanel2, null);
    jPanel1.add(jPanel3, null);
    jPanel1.add(jPanel4, null);    
    jPanel1.add(jPanel5, null);
    jPanel1.add(jPanel6, null);
    jPanel1.add(jPanel7, null);
    jPanel1.add(jSeparator1, null); 
    jPanel1.add(jPanel8, null);      
    jPanel1.add(jPanel9, null);
    jPanel1.add(jPanel10, null);
    jPanel1.add(jPanel11, null);
    jPanel1.add(jPanel12, null);
    jPanel1.add(jPanel13, null);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);

    /* JButton ok = this.getOkButton();
    ok.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent actionEvent) {
          validateUI();
        }
      });*/
  }
  
  private void init() throws Exception {
    setDataSourceType(RpaDACC0entit.class);
  }
  
  public void setDataSource(CdtDACPkBaseClass cdtDACPkBaseClass) {
    super.setDataSource(cdtDACPkBaseClass);
  } 
  
  protected boolean validateUI() // chiamata dalla save
  {
    RpaDACC0entit fromUI = (RpaDACC0entit)this.getDataSourceFromUI();
    if(fromUI!=null){
      if(fromUI.getC0eNum() == null){
        BsfMsg.error(this, locale.get("c0e_numNull"));
        return false;
      }
    }else{
      return false;
    }
    return true;    
  }
  
  /*protected boolean saveAction()
  {
    boolean salvato = super.saveAction();
    if(salvato){
      this.closeAction();
    }
    return salvato;
  }*/
  
  /*protected boolean beforeClose(){
    if (isSelectionOk()) {
      if (!validateUI()) {
        return false;
      }
    }
    return super.beforeClose();
  }*/

  // metodo chiamato alla pressione del tasto verde di conferma
  protected boolean validateSelection() {
    return (super.validateSelection() && validateUI());
  }
}
