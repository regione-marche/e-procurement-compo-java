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


public class RpaDACLog
    extends     CdtDACLogBaseClass
    implements  CdtAllDacInfoInterface,
                Serializable,
                CtlGridMethodsProvider,
                CdtDACWithJoin
{

    // indica la relativa tabella su cui mappare il DAC
    private static final CdtClassMapInfo MAPINFO = new CdtClassMapInfo("rpa_log");

    private RpaDACSession session;

    private String type;
    private String code;
    private Long context;
    private String text;
    private String errorStackTrace;

    // indica la mappatura dei campi sulle relative colonne della tabella
    private static final CdtAbstractFieldMapInfo[] MAPPINGS =
      new CdtAbstractFieldMapInfo[] {
        new CdtPropertyFieldMapInfo("type",             "type",             CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 1,    true),
        new CdtPropertyFieldMapInfo("code",             "code",             CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 8,    true),
        new CdtPropertyFieldMapInfo("context",          "context",          CdtPropertyFieldMapInfo.DATA_TYPE_LONG,         true),
        new CdtPropertyFieldMapInfo("text",             "text",             CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 300,  true),
        new CdtPropertyFieldMapInfo("error_stack_trace","errorStackTrace",  CdtPropertyFieldMapInfo.DATA_TYPE_STRING, 2000, true)
    };

    // indica le relazioni tra DAC
    private static final CdtDACRelation[] RELATIONS = new CdtDACRelation[]{
        new CdtDACRelation(RpaDACSession.class, "session",  "id_session",   true),
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


    public void setSession(RpaDACSession session) {
        this.session = session;
    }

    public RpaDACSession getSession() {
        return session;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setContext(Long context) {
        this.context = context;
    }

    public Long getContext() {
        return context;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }

    public String getErrorStackTrace() {
        return errorStackTrace;
    }
}
