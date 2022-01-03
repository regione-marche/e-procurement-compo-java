package it.saga.library.reportGeneratoreModelli;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.commonDataTypes.CdtBLGPkBaseClassLocal;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.DeleteCompoParametersParams;
import it.saga.library.reportGeneratoreModelli.misc.DeleteCompoParametersResult;
import it.saga.library.reportGeneratoreModelli.misc.RpaRunCompositoreParams;
import it.saga.library.reportGeneratoreModelli.misc.RpaRunCompositoreResult;
import it.saga.library.reportGeneratoreModelli.misc.SaveCompoParametersParams;
import it.saga.library.reportGeneratoreModelli.misc.SaveCompoParametersResult;

import java.util.List;


public interface RpaBLGLocal extends CdtBLGPkBaseClassLocal {
    RpaRunCompositoreResult runCompositore(AutCFGUserSession session,
                                           RpaRunCompositoreParams params) throws SagaException;

    DeleteCompoParametersResult deleteCompoParametersNewTransaction(AutCFGUserSession session,
                                                                    DeleteCompoParametersParams params) throws SagaException;

    SaveCompoParametersResult saveCompoParametersNewTransaction(AutCFGUserSession session,
                                                                SaveCompoParametersParams params) throws SagaException;

    void svuotaC0campi(AutCFGUserSession session, String appPrefix) throws SagaException;

    void svuotaC0entit(AutCFGUserSession session, String appPrefix) throws SagaException;

    Long runCompositoreTest(AutCFGUserSession session, String urlModello, String parametri,
                            String parametriSessione, Boolean compoJava, Boolean isApplyFirma) throws SagaException;

    Long getFileLogRep(AutCFGUserSession session) throws SagaException;

    String creaFileTempDaRepo(AutCFGUserSession session, Long idRepo, boolean isApplySignature) throws SagaException;

    void cleanLogNuovoCompositore(AutCFGUserSession session) throws SagaException;

    List<Object[]> getLogNuovoCompositore(AutCFGUserSession session) throws SagaException;

}
