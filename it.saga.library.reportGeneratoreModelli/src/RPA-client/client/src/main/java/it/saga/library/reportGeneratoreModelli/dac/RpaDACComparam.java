package it.saga.library.reportGeneratoreModelli.dac;

import it.saga.library.commonDataTypes.CdtAbstractFieldMapInfo;
import it.saga.library.commonDataTypes.CdtAllDacInfoInterface;
import it.saga.library.commonDataTypes.CdtBeanBuilder;
import it.saga.library.commonDataTypes.CdtClassMapInfo;
import it.saga.library.commonDataTypes.CdtDACLogBaseClass;
import it.saga.library.commonDataTypes.CdtPropertyFieldMapInfo;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.RpaBLG;


public class RpaDACComparam extends CdtDACLogBaseClass implements CdtAllDacInfoInterface {
  
  private Long idSessione;
  private String codice;
  private String descr;
  private String valore;

  // indica la relativa tabella su cui mappare il DAC
  private static final CdtClassMapInfo MAPINFO = new CdtClassMapInfo("rpa_comparam");
  // indica la mappatura dei campi sulle relative colonne della tabella
  private static final CdtAbstractFieldMapInfo[] MAPPINGS =
    new CdtAbstractFieldMapInfo[] { new CdtPropertyFieldMapInfo("id_sessione", "idSessione",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_LONG, false),
                                    new CdtPropertyFieldMapInfo("codice", "codice",
                                                                CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 30, true),
                                    new CdtPropertyFieldMapInfo("descr", "descr",
                                                                  CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 200, true),
                                    new CdtPropertyFieldMapInfo("valore", "valore",
                                                                  CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 512, true)
                                    
    };
  
  public RpaDACComparam() {
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

  public void setIdSessione(Long idSessione) {
    this.idSessione = idSessione;
  }

  public Long getIdSessione() {
    return idSessione;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public String getCodice() {
    return codice;
  }

  public void setDescr(String descr) {
    this.descr = descr;
  }

  public String getDescr() {
    return descr;
  }

  public void setValore(String valore) {
    this.valore = valore;
  }

  public String getValore() {
    return valore;
  }
}
