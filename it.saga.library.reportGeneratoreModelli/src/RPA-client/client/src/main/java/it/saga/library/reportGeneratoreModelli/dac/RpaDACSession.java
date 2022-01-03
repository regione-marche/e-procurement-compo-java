package it.saga.library.reportGeneratoreModelli.dac;

import it.saga.library.commonDataTypes.CdtAbstractFieldMapInfo;
import it.saga.library.commonDataTypes.CdtAllDacInfoInterface;
import it.saga.library.commonDataTypes.CdtBeanBuilder;
import it.saga.library.commonDataTypes.CdtClassMapInfo;
import it.saga.library.commonDataTypes.CdtDACLogBaseClass;
import it.saga.library.commonDataTypes.CdtDACRelation;
import it.saga.library.commonDataTypes.CdtDACWithJoin;
import it.saga.library.commonDataTypes.CdtPropertyFieldMapInfo;
import it.saga.library.controls.grid.methodprovider.CtlGridMethodsProvider;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.RpaBLG;

import javax.swing.Icon;
import java.io.Serializable;


public class RpaDACSession
    extends     CdtDACLogBaseClass
    implements  CdtAllDacInfoInterface,
                Serializable,
                CtlGridMethodsProvider,
                CdtDACWithJoin
{

    // indica la relativa tabella su cui mappare il DAC
    private static final CdtClassMapInfo MAPINFO = new CdtClassMapInfo("rpa_session");

    private Long id;
    private String model;

    // indica la mappatura dei campi sulle relative colonne della tabella
    private static final CdtAbstractFieldMapInfo[] MAPPINGS =
      new CdtAbstractFieldMapInfo[] {
        new CdtPropertyFieldMapInfo("id",   "id",   CdtPropertyFieldMapInfo.DATA_TYPE_LONG,         true),
        new CdtPropertyFieldMapInfo("model","model",CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 300,  true)
    };

    // indica le relazioni tra DAC
    private static final CdtDACRelation[] RELATIONS = new CdtDACRelation[]{
    //        new CdtDACRelation(ApeDACConces.class,              "conces",       "id_conces",        true),
    };

    @Override
    protected RpaBLG getBlg() throws SagaException {
        return CdtBeanBuilder.getBlg(RpaBLG.class, getInitialContext());
    }

    @Override
    public CdtClassMapInfo getAllDacMappingInfo() {
      return MAPINFO;
    }

    @Override
    public CdtAbstractFieldMapInfo[] getMappedAttributeList() {
      return MAPPINGS;
    }

    @Override
    public CdtDACRelation[] getJoins() {
        return RELATIONS;
    }

    @Override
    public boolean isDeferredLoaded() { return false; }

    @Override
    public Icon getIcon(int i) { return null; }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
