package it.saga.library.reportGeneratoreModelli.dac;

import it.saga.library.commonDataTypes.CdtAbstractFieldMapInfo;
import it.saga.library.commonDataTypes.CdtAllDacInfoInterface;
import it.saga.library.commonDataTypes.CdtBeanBuilder;
import it.saga.library.commonDataTypes.CdtClassMapInfo;
import it.saga.library.commonDataTypes.CdtDACLogBaseClass;
import it.saga.library.commonDataTypes.CdtPropertyFieldMapInfo;
import it.saga.library.controls.grid.CtlGridColumnDescriptor;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.RpaBLG;
import it.saga.library.search.SchSearchInfo;
import it.saga.library.search.SchSearchInterface;


public class RpaDACC0entit extends CdtDACLogBaseClass implements CdtAllDacInfoInterface, SchSearchInterface {
  
  private String c0eNom;
  private Long c0eNum;
  private String c0eTip;
  private String c0eArg;
  private String c0eDes;
  private String c0eKey;
  private String c0eNomEx;
  private String c0eKeyEx;
  private String c0eFrmRi;
  private String c0eFrmCa;
  private String c0eFrmRe;
  private String appPrefix;

  // indica la relativa tabella su cui mappare il DAC
  private static final CdtClassMapInfo MAPINFO = new CdtClassMapInfo("rpa_c0entit");
  // indica la mappatura dei campi sulle relative colonne della tabella
  private static final CdtAbstractFieldMapInfo[] MAPPINGS =
    new CdtAbstractFieldMapInfo[] { new CdtPropertyFieldMapInfo("c0e_nom", "c0eNom",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 35, true),
                                    new CdtPropertyFieldMapInfo("c0e_num", "c0eNum",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_LONG, false),
                                    new CdtPropertyFieldMapInfo("c0e_tip", "c0eTip",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 1, true),
                                    new CdtPropertyFieldMapInfo("c0e_arg", "c0eArg",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 10, true),
                                    new CdtPropertyFieldMapInfo("c0e_des", "c0eDes",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 60, true),
                                    new CdtPropertyFieldMapInfo("c0e_key", "c0eKey",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 190, true),
                                    new CdtPropertyFieldMapInfo("c0e_nom_ex", "c0eNomEx",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 35, true),
                                    new CdtPropertyFieldMapInfo("c0e_key_ex", "c0eKeyEx",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 190, true),
                                    new CdtPropertyFieldMapInfo("c0e_frm_ri", "c0eFrmRi",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 10, true),
                                    new CdtPropertyFieldMapInfo("c0e_frm_ca", "c0eFrmCa",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 10, true),
                                    new CdtPropertyFieldMapInfo("c0e_frm_re", "c0eFrmRe",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 10, true),
                                    new CdtPropertyFieldMapInfo("app_prefix", "appPrefix",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 3, true)};

  public RpaDACC0entit() {
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

  public void setC0eNom(String c0eNom) {
    this.c0eNom = c0eNom;
  }

  public String getC0eNom() {
    return c0eNom;
  }

  public void setC0eNum(Long c0eNum) {
    this.c0eNum = c0eNum;
  }

  public Long getC0eNum() {
    return c0eNum;
  }

  public void setC0eTip(String c0eTip) {
    this.c0eTip = c0eTip;
  }

  public String getC0eTip() {
    return c0eTip;
  }

  public void setC0eArg(String c0eArg) {
    this.c0eArg = c0eArg;
  }

  public String getC0eArg() {
    return c0eArg;
  }

  public void setC0eDes(String c0eDes) {
    this.c0eDes = c0eDes;
  }

  public String getC0eDes() {
    return c0eDes;
  }

  public void setC0eKey(String c0eKey) {
    this.c0eKey = c0eKey;
  }

  public String getC0eKey() {
    return c0eKey;
  }

  public void setC0eNomEx(String c0eNomEx) {
    this.c0eNomEx = c0eNomEx;
  }

  public String getC0eNomEx() {
    return c0eNomEx;
  }

  public void setC0eKeyEx(String c0eKeyEx) {
    this.c0eKeyEx = c0eKeyEx;
  }

  public String getC0eKeyEx() {
    return c0eKeyEx;
  }

  public void setC0eFrmRi(String c0eFrmRi) {
    this.c0eFrmRi = c0eFrmRi;
  }

  public String getC0eFrmRi() {
    return c0eFrmRi;
  }

  public void setC0eFrmCa(String c0eFrmCa) {
    this.c0eFrmCa = c0eFrmCa;
  }

  public String getC0eFrmCa() {
    return c0eFrmCa;
  }

  public void setC0eFrmRe(String c0eFrmRe) {
    this.c0eFrmRe = c0eFrmRe;
  }

  public String getC0eFrmRe() {
    return c0eFrmRe;
  }
  
  public void setAppPrefix(String appPrefix) {
    this.appPrefix = appPrefix;
  }

  public String getAppPrefix() {
    return appPrefix;
  }

  public SchSearchInfo getSearchInfo() {
    // Colonne da visualizzare nella maschera di ricerca
    CtlGridColumnDescriptor[] viewColumns = new CtlGridColumnDescriptor[] {
      new CtlGridColumnDescriptor("C0eNom","c0eNom"),
      new CtlGridColumnDescriptor("C0eNum","c0eNum"),
      new CtlGridColumnDescriptor("C0eDes","c0eDes"),
    };
    SchSearchInfo searchInfo = new SchSearchInfo();
    searchInfo.setRootClass(RpaDACC0entit.class);
    searchInfo.setSearchTitle("Seleziona una entit");
    searchInfo.setViewColumns(viewColumns);
    return searchInfo;
  }
}
