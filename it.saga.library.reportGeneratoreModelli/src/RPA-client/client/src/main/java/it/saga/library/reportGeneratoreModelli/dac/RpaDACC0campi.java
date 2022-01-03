package it.saga.library.reportGeneratoreModelli.dac;

import it.saga.library.commonDataTypes.CdtAbstractFieldMapInfo;
import it.saga.library.commonDataTypes.CdtAllDacInfoInterface;
import it.saga.library.commonDataTypes.CdtBeanBuilder;
import it.saga.library.commonDataTypes.CdtClassMapInfo;
import it.saga.library.commonDataTypes.CdtDACLogBaseClass;
import it.saga.library.commonDataTypes.CdtPropertyFieldMapInfo;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.RpaBLG;


public class RpaDACC0campi extends CdtDACLogBaseClass implements CdtAllDacInfoInterface {
  
  private Long c0cConta;  
  private String c0cTip;
  private String c0cChi;
  private String c0cMneUni;
  private String c0cMneBer;
  private String c0cDes;
  private String c0cDesFrm;
  private String c0cFs;
  private String c0cTab1;
  private String c0cDom;
  private String c0cDesWeb;
  private String appPrefix;
  private String c0cIdSplit;

  // indica la relativa tabella su cui mappare il DAC
  private static final CdtClassMapInfo MAPINFO = new CdtClassMapInfo("rpa_c0campi");
  // indica la mappatura dei campi sulle relative colonne della tabella
  private static final CdtAbstractFieldMapInfo[] MAPPINGS =
    new CdtAbstractFieldMapInfo[] { new CdtPropertyFieldMapInfo("c0c_conta", "c0cConta",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_LONG, false),                                    
                                    new CdtPropertyFieldMapInfo("c0c_tip", "c0cTip",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 1, true),
                                    new CdtPropertyFieldMapInfo("c0c_chi", "c0cChi",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 1, true),
                                    new CdtPropertyFieldMapInfo("c0c_mne_ber", "c0cMneBer",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 30, true),
                                    new CdtPropertyFieldMapInfo("c0c_mne_uni", "c0cMneUni",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 65, true),
                                    new CdtPropertyFieldMapInfo("c0c_des", "c0cDes",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 60, true),
                                    new CdtPropertyFieldMapInfo("c0c_des_frm", "c0cDesFrm",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 20, true),
                                    new CdtPropertyFieldMapInfo("c0c_fs", "c0cFs",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 10, true),
                                    new CdtPropertyFieldMapInfo("c0c_tab1", "c0cTab1",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 5, true),
                                    new CdtPropertyFieldMapInfo("c0c_dom", "c0cDom",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 20, true),
                                    new CdtPropertyFieldMapInfo("c0c_des_web", "c0cDesWeb",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 100, true),
                                    new CdtPropertyFieldMapInfo("app_prefix", "appPrefix",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 3, true),
                                    new CdtPropertyFieldMapInfo("c0c_id_split", "c0cIdSplit",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 100, true)};
  
  public RpaDACC0campi() {
    super();
  }

  @Override
  protected RpaBLG getBlg() throws SagaException {
    return CdtBeanBuilder.getBlg(RpaBLG.class, getInitialContext());
  }

  public CdtClassMapInfo getAllDacMappingInfo() {
    return MAPINFO;
  }

  public CdtAbstractFieldMapInfo[] getMappedAttributeList() {
    return MAPPINGS;
  }

  public void setC0cConta(Long c0cConta) {
    this.c0cConta = c0cConta;
  }

  public Long getC0cConta() {
    return c0cConta;
  }

  public void setC0cTip(String c0cTip) {
    this.c0cTip = c0cTip;
  }

  public String getC0cTip() {
    return c0cTip;
  }

  public void setC0cChi(String c0cChi) {
    this.c0cChi = c0cChi;
  }

  public String getC0cChi() {
    return c0cChi;
  }

  public void setC0cMneUni(String c0cMneUni) {
    this.c0cMneUni = c0cMneUni;
  }

  public String getC0cMneUni() {
    return c0cMneUni;
  }

  public void setC0cMneBer(String c0cMneBer) {
    this.c0cMneBer = c0cMneBer;
  }

  public String getC0cMneBer() {
    return c0cMneBer;
  }

  public void setC0cDes(String c0cDes) {
    this.c0cDes = c0cDes;
  }

  public String getC0cDes() {
    return c0cDes;
  }

  public void setC0cDesFrm(String c0cDesFrm) {
    this.c0cDesFrm = c0cDesFrm;
  }

  public String getC0cDesFrm() {
    return c0cDesFrm;
  }

  public void setC0cFs(String c0cFs) {
    this.c0cFs = c0cFs;
  }

  public String getC0cFs() {
    return c0cFs;
  }

  public void setC0cTab1(String c0cTab1) {
    this.c0cTab1 = c0cTab1;
  }

  public String getC0cTab1() {
    return c0cTab1;
  }

  public void setC0cDom(String c0cDom) {
    this.c0cDom = c0cDom;
  }

  public String getC0cDom() {
    return c0cDom;
  }

  public void setC0cDesWeb(String c0cDesWeb) {
    this.c0cDesWeb = c0cDesWeb;
  }

  public String getC0cDesWeb() {
    return c0cDesWeb;
  }

  public void setAppPrefix(String appPrefix) {
    this.appPrefix = appPrefix;
  }

  public String getAppPrefix() {
    return appPrefix;
  }

  public String getC0cIdSplit() { return c0cIdSplit; }

  public void setC0cIdSplit(String c0cIdSplit) { this.c0cIdSplit = c0cIdSplit; }
}
