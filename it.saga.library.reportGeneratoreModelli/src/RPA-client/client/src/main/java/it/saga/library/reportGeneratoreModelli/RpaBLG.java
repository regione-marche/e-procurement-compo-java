package it.saga.library.reportGeneratoreModelli;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.commonDataTypes.CdtBLGPkBaseClass;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaProcessInfo;
import it.saga.library.reportGeneratoreModelli.misc.RpaRunCompositoreParams;
import it.saga.library.reportGeneratoreModelli.misc.RpaRunCompositoreResult;
import it.saga.library.reports.RptDACReportLink;
import it.saga.library.reports.RptDACReportParameters;
import it.saga.library.repository.RepDACDocument;

import java.io.BufferedReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;


public interface RpaBLG extends CdtBLGPkBaseClass {
    void caricaC0campi(AutCFGUserSession session, BufferedReader fileRpaC0campi) throws Exception, RemoteException;

    void caricaC0campi(AutCFGUserSession session, String urlFile) throws Exception, RemoteException;

    void caricaC0entit(AutCFGUserSession session, BufferedReader fileRpaC0entit) throws Exception, RemoteException;

    void caricaC0entit(AutCFGUserSession session, String urlFile) throws Exception, RemoteException;

    RpaRunCompositoreResult runCompositore(AutCFGUserSession session,
                                           RpaRunCompositoreParams params) throws SagaException, RemoteException;

    void svuotaC0campi(AutCFGUserSession session, String appPrefix) throws SagaException, RemoteException;

    void svuotaC0entit(AutCFGUserSession session, String appPrefix) throws SagaException, RemoteException;

    Long runCompositoreTest(AutCFGUserSession session, String urlModello, String parametri,
                            String parametriSessione, Boolean compoJava, Boolean isApplyFirma) throws SagaException, RemoteException;

    Long getFileLogRep(AutCFGUserSession session) throws SagaException, RemoteException;

    String creaFileTempDaRepo(AutCFGUserSession session, Long idRepo, boolean isApplySignature) throws SagaException, RemoteException;

    Long getIdFileReopository(AutCFGUserSession session) throws SagaException, RemoteException;

    HashMap<Long, RpaProcessInfo> getPidList(AutCFGUserSession session) throws SagaException, RemoteException;

    int killProcess(AutCFGUserSession session, Long pid) throws SagaException, RemoteException;

    String getListaProcessi(AutCFGUserSession session) throws SagaException, RemoteException;

    int killAllProcess(AutCFGUserSession session) throws SagaException, RemoteException;

    RpaProcessInfo getInfoEseguibile(AutCFGUserSession session) throws SagaException, RemoteException;

    void cleanLogNuovoCompositore(AutCFGUserSession session) throws SagaException, RemoteException;

    List<Object[]> getLogNuovoCompositore(AutCFGUserSession session) throws SagaException, RemoteException;

    public RepDACDocument eseguiCompositoreByReportLink(
        AutCFGUserSession session,
        RptDACReportLink reportLink,
        RptDACReportParameters params
    ) throws SagaException, RemoteException;

}
