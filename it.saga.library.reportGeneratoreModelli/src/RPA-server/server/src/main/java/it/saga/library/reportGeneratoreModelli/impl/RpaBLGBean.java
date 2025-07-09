package it.saga.library.reportGeneratoreModelli.impl;


import it.saga.library.authentication.AutCFGClientSession;
import it.saga.library.authentication.AutCFGParamScope;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.authentication.AutEXCSessionException;
import it.saga.library.authentication.AutUtils;
import it.saga.library.authentication.clustercache.AutClusterCache;
import it.saga.library.bonifiche.BonUtility;
import it.saga.library.common.CmnUtils;
import it.saga.library.commonDataTypes.CdtBLGJDBCConnectionInfo;
import it.saga.library.commonDataTypes.CdtBLGPkBaseClassBean;
import it.saga.library.commonDataTypes.CdtBLGServerUtils;
import it.saga.library.commonDataTypes.CdtDACList;
import it.saga.library.commonDataTypes.CdtDACPkBaseClass;
import it.saga.library.commonDataTypes.CdtUtils;
import it.saga.library.commonDataTypes.config.CdtBLGPersistenceConfiguration;
import it.saga.library.commonPratiche.Ap1Costanti;
import it.saga.library.commonPratiche.Ap1Params;
import it.saga.library.commonPratiche.Ap1Utils;
import it.saga.library.documentiCollegati.DocCollegatiInterface;
import it.saga.library.documentiCollegati.DocDACCollegatiDetails;
import it.saga.library.documentiCollegati.DocDACCollegatiMaster;
import it.saga.library.gestioneDocumentale.DocService;
import it.saga.library.gestioneDocumentale.DocUtils;
import it.saga.library.gestioneDocumentale.DocWordProcessorException;
import it.saga.library.gestioneDocumentale.flows.DocDACDocumenti;
import it.saga.library.gestioneDocumentale.flows.DocDACTipiDocumento;
import it.saga.library.gestioneDocumentale.flows.DocFlowsUtils;
import it.saga.library.gestioneDocumentale.javaProcessor.DocAsposeWordProcessor;
import it.saga.library.localization.LocParameter;
import it.saga.library.logging.Log;
import it.saga.library.messages.MsgDACContainer;
import it.saga.library.messages.MsgDACMessage;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.RpaBLGLocal;
import it.saga.library.reportGeneratoreModelli.RpaBLGLocalHome;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaComposerStartConfiguration;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACC0campi;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACC0entit;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACComparam;
import it.saga.library.reportGeneratoreModelli.executables.RpaResource;
import it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
import it.saga.library.reportGeneratoreModelli.genmod.ServizioCompositoreProxy;
import it.saga.library.reportGeneratoreModelli.misc.DeleteCompoParametersParams;
import it.saga.library.reportGeneratoreModelli.misc.DeleteCompoParametersResult;
import it.saga.library.reportGeneratoreModelli.misc.RpaCostanti;
import it.saga.library.reportGeneratoreModelli.misc.RpaProcessInfo;
import it.saga.library.reportGeneratoreModelli.misc.RpaRunCompositoreParams;
import it.saga.library.reportGeneratoreModelli.misc.RpaRunCompositoreResult;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;
import it.saga.library.reportGeneratoreModelli.misc.SaveCompoParametersParams;
import it.saga.library.reportGeneratoreModelli.misc.SaveCompoParametersResult;
import it.saga.library.reportGeneratoreModelli.setup.misc.RpaSetupUtility;
import it.saga.library.reports.RptDACPrinterParameters;
import it.saga.library.reports.RptDACReportExecution;
import it.saga.library.reports.RptDACReportLink;
import it.saga.library.reports.RptDACReportParameters;
import it.saga.library.reports.RptReportEngine;
import it.saga.library.reports.misc.RptExecuteParams;
import it.saga.library.reports.misc.RptExecuteReportLinkDescriptor;
import it.saga.library.reports.misc.RptExecuteResult;
import it.saga.library.repository.RepDACDocument;
import it.saga.library.repository.RepUtils;
import it.saga.pubblici.pratiche.PraDACPratiche;
import it.saga.pubblici.pratiche.PraDACPraticheDocumenti;
import it.saga.pubblici.pratiche.PraDACPraticheDocumentiFile;
import it.saga.pubblici.pratiche.PraService;
import it.saga.pubblici.pratiche.PraUtils;
import it.saga.pubblici.praticheEdilizie.ApeService;
import it.saga.pubblici.praticheEdilizie.ApeUtils;
import it.saga.pubblici.praticheEdilizie.server.impl.ApeBLGSchedulerBean;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RpaBLGBean extends CdtBLGPkBaseClassBean{
    //////////////////////////////////////////////////////////////////////////////
    private static class      FolderExcludeFilter
        implements FileFilter
    {
        public static final FolderExcludeFilter SINGLETON = new FolderExcludeFilter();
        public boolean accept( File file ) { return file.isFile(); }
    }
    //////////////////////////////////////////////////////////////////////////////

    public final static String CARATTERE_SEPARATORE="|";

    private static final String DRIVER_JDBC_ORACLE    = "oracle.jdbc.driver.OracleDriver";
    private static final String DRIVER_JDBC_SQLSERVER = "net.sourceforge.jtds.jdbc.Driver";
    private static final String DRIVER_JDBC_POSTGRES  = "org.postgresql.Driver";

    private static final String DB_TYPE_ORACLE    = "ORA";
    private static final String DB_TYPE_SQLSERVER = "MSQ";
    private static final String DB_TYPE_POSTGRES  = "POS";

    private final static Log log=Log.getLog(RpaBLGBean.class);

    public void caricaC0campi(AutCFGUserSession session,String urlFile) throws Exception{
        BufferedReader fileRpaC0campi= getBufferReaderConCharset(urlFile);
        caricaC0campi(session,fileRpaC0campi);
    }

    public void caricaC0campi(AutCFGUserSession session,BufferedReader fileRpaC0campi) throws Exception{
        //     int offsetPkid = 1;
        //        CdtDACList resultPkid = this.readCollection(session," select max(pkid) from  RpaDACC0campi",new Object[]{});
        //        if (resultPkid!=null && resultPkid.size()==0){
        //          Long pkid = (Long)resultPkid.get(0);
        //          if (pkid!=null){
        //            offsetPkid += pkid.intValue();
        //          }
        //        }
        ArrayList listC0campi=RpaSetupUtility.loadFileMap(fileRpaC0campi);
        for(int i=0;i<listC0campi.size();i++){
            //Carico in memoria gli elementi della riga
            String riga=((String)listC0campi.get(i)).trim();
            log.debug("riga="  + riga);
            if(riga.equals("")){
                continue;
            }
            String[] fields=riga.split("\\"+RpaSetupUtility.CARATTERE_SEPARATORE,-1);
            RpaDACC0campi rpaC0campi=new RpaDACC0campi();
            rpaC0campi.prepareForNewInsert();
            //Imposto il DAC elemento per elemento
            rpaC0campi.setC0cConta(new Long(0));
            rpaC0campi.setC0cTip(RpaSetupUtility.strToString(fields[0]));
            rpaC0campi.setC0cChi(RpaSetupUtility.strToString(fields[1]));
            String c0cMneUni=RpaSetupUtility.strToString(fields[2]);
            rpaC0campi.setC0cMneUni(c0cMneUni);
            // logica per estrarre dato PRAEST.APE_CONCES.APE il suffisso APE che comporrà l'app_prefix
            String c0cMneUniParts[]=c0cMneUni.split("\\.");
            rpaC0campi.setAppPrefix(RpaSetupUtility.strToString(c0cMneUniParts[c0cMneUniParts.length-1],3).toLowerCase());
            rpaC0campi.setC0cMneBer(RpaSetupUtility.strToString(fields[3]));
            rpaC0campi.setC0cDes(RpaSetupUtility.strToString(fields[4],60));
            rpaC0campi.setC0cDesFrm(RpaSetupUtility.strToString(fields[5],20));
            rpaC0campi.setC0cFs(RpaSetupUtility.strToString(fields[6]));
            rpaC0campi.setC0cTab1(RpaSetupUtility.strToString(fields[7]));
            rpaC0campi.setC0cDom(RpaSetupUtility.strToString(fields[8]));
            rpaC0campi.setC0cDesWeb(RpaSetupUtility.strToString(fields[9],100));
            if(fields.length > 10) {
                rpaC0campi.setC0cIdSplit(RpaSetupUtility.strToString(fields[10], 100));
            }

            //Cerco se esiste già la riga, se si la ggiorno, altrimenti la inserisco
            String query=" from RpaDACC0campi c where c.c0cMneBer = ? ";
            Object[] params=new Object[]{ rpaC0campi.getC0cMneBer() };
            CdtDACList result=this.readCollection(session,query,params);
            if(result!=null&&result.size()==1){
                RpaDACC0campi rpaC0campiResult=(RpaDACC0campi)result.get(0);
                rpaC0campiResult.setC0cTip(rpaC0campi.getC0cTip());
                rpaC0campiResult.setC0cChi(rpaC0campi.getC0cChi());
                rpaC0campiResult.setC0cMneUni(rpaC0campi.getC0cMneUni());
                rpaC0campiResult.setC0cMneBer(rpaC0campi.getC0cMneBer());
                rpaC0campiResult.setC0cDes(rpaC0campi.getC0cDes());
                rpaC0campiResult.setC0cDesFrm(rpaC0campi.getC0cDesFrm());
                rpaC0campiResult.setC0cFs(rpaC0campi.getC0cFs());
                rpaC0campiResult.setC0cTab1(rpaC0campi.getC0cTab1());
                rpaC0campiResult.setC0cDom(rpaC0campi.getC0cDom());
                rpaC0campiResult.setC0cDesWeb(rpaC0campi.getC0cDesWeb());
                rpaC0campiResult.setAppPrefix(rpaC0campi.getAppPrefix());
                rpaC0campiResult.setC0cIdSplit(rpaC0campi.getC0cIdSplit());

                this.save(session,rpaC0campiResult);
                log.debug("Caricamento C0Campi - Mnemonico: "+rpaC0campi.getC0cMneBer()+" Esistente: Aggiornato.");

            } else {
                //Salvo la nuova riga nella tabella
                this.save(session,rpaC0campi);
            }
        }

        ApeUtils.getDBMapping(session,true);
    }


    public void caricaC0entit(AutCFGUserSession session,String urlFile) throws Exception{
        BufferedReader fileRpaC0entit=getBufferReaderConCharset(urlFile);
        caricaC0entit(session,fileRpaC0entit);
    }

    public void caricaC0entit(AutCFGUserSession session,BufferedReader fileRpaC0entit) throws Exception{
        ArrayList listC0entit=RpaSetupUtility.loadFileMap(fileRpaC0entit);
        for(int i=0;i<listC0entit.size();i++){
            //Carico in memoria gli elementi della riga
            String riga=((String)listC0entit.get(i)).trim();
            log.debug("riga="  + riga);
            if(riga.equals("")){
                continue;
            }
            //Splittiamo per carattere separatore "|"
            String[] fields=riga.split("\\"+RpaSetupUtility.CARATTERE_SEPARATORE,-1);
            RpaDACC0entit rpaC0entit=new RpaDACC0entit();
            rpaC0entit.prepareForNewInsert();
            rpaC0entit.setC0eNum(RpaSetupUtility.strToLong(fields[1]));
            String c0eNom=RpaSetupUtility.strToString(fields[2],35);
            rpaC0entit.setC0eNom(c0eNom);
            rpaC0entit.setC0eTip(RpaSetupUtility.strToString(fields[3],1));
            rpaC0entit.setC0eArg(RpaSetupUtility.strToString(fields[4],10));
            rpaC0entit.setC0eDes(RpaSetupUtility.strToString(fields[5],60));
            rpaC0entit.setC0eKey(RpaSetupUtility.strToString(fields[6],190));
            // logica per estrarre dato APE_TAB_CC.APE il suffisso APE che comporrà l'app_prefix
            String c0eNomParts[]=c0eNom.split("\\.");
            rpaC0entit.setAppPrefix(RpaSetupUtility.strToString(c0eNomParts[c0eNomParts.length-1],3).toLowerCase());
            // Sono su due righe i valori per un singolo DAC entit
            i++;
            if(i>=listC0entit.size()){
                break;
            }
            riga=((String)listC0entit.get(i)).trim();
            if(riga.equals("")){
                continue;
            }
            fields=riga.split("\\"+RpaSetupUtility.CARATTERE_SEPARATORE,-1);
            rpaC0entit.setC0eFrmRi(RpaSetupUtility.strToString(fields[2],10));
            rpaC0entit.setC0eFrmCa(RpaSetupUtility.strToString(fields[3],10));
            rpaC0entit.setC0eFrmRe(RpaSetupUtility.strToString(fields[4],10));
            rpaC0entit.setC0eNomEx(RpaSetupUtility.strToString(fields[6],35));
            rpaC0entit.setC0eKeyEx(RpaSetupUtility.strToString(fields[7],190));

            //Cerco se esiste già la riga, se sì la aggiorno, altrimenti la inserisco
            String query=" from RpaDACC0entit c where c.c0eNom = ? ";
            Object[] params=new Object[]{ rpaC0entit.getC0eNom() };
            CdtDACList result=this.readCollection(session,query,params);
            if(result!=null&&result.size()==1){
                RpaDACC0entit rpaC0entitResult=(RpaDACC0entit)result.get(0);
                rpaC0entitResult.setC0eNum(rpaC0entit.getC0eNum());
                rpaC0entitResult.setC0eTip(rpaC0entit.getC0eTip());
                rpaC0entitResult.setC0eArg(rpaC0entit.getC0eArg());
                rpaC0entitResult.setC0eDes(rpaC0entit.getC0eDes());
                rpaC0entitResult.setC0eKey(rpaC0entit.getC0eKey());
                rpaC0entitResult.setC0eFrmRi(rpaC0entit.getC0eFrmRi());
                rpaC0entitResult.setC0eFrmCa(rpaC0entit.getC0eFrmCa());
                rpaC0entitResult.setC0eFrmRe(rpaC0entit.getC0eFrmRe());
                rpaC0entitResult.setC0eNomEx(rpaC0entit.getC0eNomEx());
                rpaC0entitResult.setC0eKeyEx(rpaC0entit.getC0eKeyEx());
                rpaC0entitResult.setAppPrefix(rpaC0entit.getAppPrefix());

                this.save(session,rpaC0entitResult);
                log.debug("Caricamento C0Entit - Mnemonico: "+rpaC0entit.getC0eNom()+" Esistente: Aggiornato.");
            } else {
                //Salvo la nuova riga nella tabella
                this.save(session,rpaC0entit);
            }
        }
    }

    private BufferedReader getBufferReaderConCharset(String urlFile)
        throws FileNotFoundException,UnsupportedEncodingException {
        // I file che prepariamo utilizzano questo charset, se il server sta su una macchina linux
        // utilizza il charset UTF-8, percui se non lo si imposta saltano tutti i caratteri accentati
        return new BufferedReader(new InputStreamReader(new FileInputStream(urlFile), "ISO-8859-1"));
//      return new BufferedReader(new InputStreamReader(new FileInputStream(urlFile), BonUtility.CHARSET_STANDARD));
    }

    public SaveCompoParametersResult saveCompoParametersNewTransaction(
        AutCFGUserSession         session,
        SaveCompoParametersParams params ) throws SagaException
    {
        if(    params.getCompoParams()        == null
            || params.getCompoParams().length == 0    ){
            return new SaveCompoParametersResult( null );
        }

        // calcolo l'id sessione usando l'id della sessione dell'utente
        // che sta invocando la stampa. Non e' antiatomico ma dovrebbe reggere
        // sufficientemente.

        //A.M.l'id sessione è troppo grande compngo un numero dato:
        // dall'utente corrente + le ultime tre cifre dell'id sessione(new Long( session.getSessionID()%1000), 3);
        Long idSessione = getIdSessionCompo(session);

        // pulizia preventiva di eventuali residui,
        // anche se non dovrebbero essercene...
        this.executeHQL(
            session                                          ,
            "delete from RpaDACComparam r where r.idSessione = ? ",
            new Object[] { idSessione }
        );

        if( log.isDebugEnabled()){
            log.debug( "Scrivo " + params.getCompoParams().length + " parametri nella tabella di scambio" );
        }

        for( int c = 0; c < params.getCompoParams().length; c++ ) {
            RpaDACComparam cPar = new RpaDACComparam();
            cPar.setIdSessione( idSessione );
            cPar.setCodice( params.getCompoParams()[c].getCodice()     );
            cPar.setDescr ( params.getCompoParams()[c].getDescrizione());
            cPar.setValore( params.getCompoParams()[c].getValore()     );
            this.save( session, cPar );
        }

        return new SaveCompoParametersResult( idSessione );
    }

    public DeleteCompoParametersResult deleteCompoParametersNewTransaction(
        AutCFGUserSession           session,
        DeleteCompoParametersParams params ) throws SagaException
    {
        if( params.getIdParamsSession() == null ) return new DeleteCompoParametersResult();

        if( log.isDebugEnabled()) log.debug( "Cancello i parametri dalla tabella di scambio" );

        this.executeHQL(
            session,
            "delete from RpaDACComparam r where r.idSessione = ? and r.codice != ? ",
            new Object[] { params.getIdParamsSession(), RpaCostanti.PARAMETRO_FILE_REPOSITORY_ID }
        );
        return new DeleteCompoParametersResult();
    }

    public RpaRunCompositoreResult runCompositore(
        AutCFGUserSession       session,
        RpaRunCompositoreParams params ) throws SagaException
    {
        try {
            try {

//        //sbianca ilm parametro altrimenti se va in errore resta il precedente
//        session.put(
//              AutCFGParamScope.SCOPE_USER_SESSION,
//              RpaCostanti.APP_PREFIX,
//              RpaCostanti.PARAMETRO_UTENTE_FILE_REPOSITORY_ID,
//              null
//          );

                File esCompoFolder    = new File( CdtBLGPersistenceConfiguration.getInstallationPath(), "compositore" );
                File esCompoClasspath = new File( esCompoFolder, "classpath" );
                File esCompoTemporary = new File( esCompoFolder, "temporary" );
                File esCompoLib       = new File( esCompoFolder, "lib" );

                this.deployFilesTo(session,esCompoFolder,esCompoClasspath,esCompoTemporary,esCompoLib);

                Long idSessione = null; // se != null indica che sono stati passati parametri

                boolean isOdbc = isOdbc(session);

//        String extFileModello = "txt";
//        if(RpaUtils.isRtf(params.getTemplateDocument())){
//            extFileModello = "rtf";
//        }
                String extFileModello = "rtf";
                String templateDocumentName = params.getTemplateDocument().getDocumentName();
                if(templateDocumentName!=null){
                    extFileModello = RpaUtils.getFileExtension(templateDocumentName);
                }

                // questi sono i due file principali
                // NOTA qui File.createTempFile va bene perchè gli viene passata la directory che dovrebbe essere già scrivibile
                File tempInputFile           = RpaUtils.createTempFile( "compo_", "_in." + extFileModello, esCompoTemporary ); //qui va cambiato il nome ed estensione del modello altrimenti fa solo rtf
                File tempOutputFileRequested = RpaUtils.createTempFile( "compo_", "_out"   , esCompoTemporary ); // ".rtf" ce lo mette il compositore

                String nomeFileTmpOut = params.getValore(RpaCostanti.PARAMETRO_NOME_FILE_TMP_OUT);

                File tempOutputFileProduced  = nomeFileTmpOut!=null? new File(nomeFileTmpOut): new File( tempOutputFileRequested.getAbsoluteFile() + "." + extFileModello);



                // questi sono i file di appoggio che il compo crea al runtime
                // (ci sarebbero anche i compilati ma per ora li rimuovo al termine
                // dell'esecuzione per non lasciare troppi file orgfani nella cartella
                // del server). Questi file si basano sul nome del file di input
                // (il modello, il cui nome varia sempre perche' e' un temp file)
                String baseInputFileName = tempInputFile.getName();
                baseInputFileName = baseInputFileName.substring( 0, baseInputFileName.length() - ".rtf".length());
                File tempOkFile  = new File( esCompoTemporary, baseInputFileName + ".ok"           );
                File tempInfFile = new File( esCompoTemporary, baseInputFileName + ".inf"          );
                File tempIdxFile = new File( esCompoTemporary, baseInputFileName + ".idx"          );
                File tempErrFile = new File( esCompoTemporary, baseInputFileName + ".err"          );
                File tempSemFile = new File( esCompoTemporary, baseInputFileName + ".rtf.semaforo" );
                String nomeFileLog = ( isOdbc ? "compo_odbc.log" : "compo_jdbc.log" );

                File logFile =  new File( esCompoFolder, nomeFileLog);

                // la lista dei file che dovro' eliminare al termine dell'esecuzione
                // (se presenti)
                List<File> deadWalkingFiles = new ArrayList<File>();
                deadWalkingFiles.add(tempInputFile );
                deadWalkingFiles.add(tempOutputFileRequested);
                if(nomeFileTmpOut==null){
                    deadWalkingFiles.add(tempOutputFileProduced);
                }
                deadWalkingFiles.add(tempOkFile);
                deadWalkingFiles.add(tempInfFile);
                deadWalkingFiles.add(tempIdxFile);
                deadWalkingFiles.add(tempErrFile);
                deadWalkingFiles.add(tempSemFile);
                deadWalkingFiles.add(logFile);


                try {
                    // scrivo i parametri nel database (se ci sono, altrimenti il save
                    // torna un idSessione nullo)
                    if(    params.getCompoParams()       != null
                        && params.getCompoParams().length > 0    ){
                        idSessione = this.getRpaBLGLocal().saveCompoParametersNewTransaction(
                            session                                                ,
                            new SaveCompoParametersParams( params.getCompoParams())
                        ).getIdParamsSession();
                    }

                    log.debug( "Scrivo il file temporaneo di input " + tempInputFile );

                    params.getTemplateDocument().writeToFile( tempInputFile );

                    String eseguibile = getEseguibile(isOdbc, esCompoFolder);

//          if( log.isDebugEnabled()) {
//
//            String csp[] = connectionString.split(";");
//            String connectionStringDebug = "";
//            for(int i=0; i<csp.length;i++){
//                if(!csp[i].startsWith("PWD=")){
//                   connectionStringDebug += csp[i];
//                }
//            }
//
//            log.debug( "Costruisco la connection string: " + connectionStringDebug );
//          }

                    // preparo la riga di comando
                    // OPZIONI:
                    // -q  Esecuzione query (verifica se il testo e' compilato)
                    // -c  Compilazione Documento
                    // -o  Composizione Documento
                    // -d  Eliminazione file composto
                    // -f  Lettura dei comandi da file


                    // in linux l'apice doppio crea dei problemi se nella stringa c'è un nome di una variabile
                    String apice = "\"";
                    if (!CmnUtils.isWindows()){
                        apice="'";
                    }

                    String strDbType = getStrDBType(session);

                    String parametri = "";
                    if(params.getContext() != null){
                        parametri = params.getContext() + " ";
                    }

                    String parametriSessione = "";
                    if(idSessione!=null){
                        parametriSessione += RpaCostanti.PARAMETRO_SESSIONE_ID + idSessione + ";";
                    }
                    parametriSessione += RpaCostanti.PARAMETRO_SESSIONE_USER + session.getLogonUser().getPkid() + ";";
                    parametriSessione += RpaCostanti.PARAMETRO_SESSIONE_DB + CdtBLGServerUtils.getDBType(session.getJ2eeUserName());

                    String appPrefix = params.getValore(RpaCostanti.PARAMETRO_APP_PREFIX);
                    if(appPrefix!=null){
                        parametriSessione += ";" + RpaCostanti.PARAMETRO_SESSIONE_APP + appPrefix;
                    }

                    boolean isCompoJava = Ap1Params.getParametroBoolean(
                            session,
                            RpaCostanti.APP_PREFIX,
                            RpaCostanti.PARAMETRO_GLOBALE_COMPOSITORE_JAVA
                    );

                    RpaComposerStartConfiguration compoConf = getConnectionsString(session, isOdbc,parametriSessione, isCompoJava);

                    //    in windows uso il percorso completo altrimenti va in errore
                    //    in linux devo basta il nome del programma, la directory di lavoro viene passata alla shell
                    String command =
                        apice + eseguibile + apice
                            + " -o "
                            + apice + tempInputFile.getAbsolutePath() + apice
                            + " "
                            + apice + compoConf.getConnectionString() + apice
                            + " " + strDbType + " "
                            + apice + tempOutputFileProduced.getAbsolutePath() + apice
                            + " "
                            + ( params.getContext() != null ? params.getContext() + " "  : "" ) // APE_CONCES:PKID=27
                            + apice + "row=1;last=1" + apice
                            + " "
                            + apice + parametriSessione + apice;

                    String commandDebug =
                        apice + eseguibile + apice
                            + " -o "
                            + apice + tempInputFile.getAbsolutePath() + apice
                            + " "
                            + apice + "***CONNECTION STRING MASCHERATA***" + apice
                            + " " + strDbType + " "
                            + apice + tempOutputFileProduced.getAbsolutePath() + apice
                            + " " + parametri// APE_CONCES:PKID=27
                            + apice + "row=1;last=1 " + apice
                            + " "
                            + apice + parametriSessione + apice;
                    log.debug( "Costruisco il comando di avvio compositore: " + commandDebug );

                    eseguiCompositoreMain(
                        session,
                        logFile,
                        esCompoFolder,
                        tempErrFile,
                        command,
                        commandDebug,
                        tempInputFile,
                        tempOutputFileProduced,
                        parametri,
                        parametriSessione,
                        baseInputFileName,
                        compoConf,
                        isCompoJava
                    );

                    // recupero (se non ho avuto errori) il file di risposta
                    log.debug( "Leggo il contenuto del file temporaneo di output " + tempOutputFileProduced );

                    String descDoc = params.getValore(RpaCostanti.PARAMETRO_DESCRIZIONE_STAMPA);
                    if(descDoc==null) {
                        descDoc = "Risultato Compositore";
                    }

                    String nomeFileRepo = params.getValore(RpaCostanti.PARAMETRO_NOME_FILE);
                    // se non l'ho specificato prendo quello del file di template
                    if(nomeFileRepo==null || "".equals(nomeFileRepo)){
                        nomeFileRepo = templateDocumentName;
                    }
                    // se anche questo non è valido, allora prendo quello standard
                    if(nomeFileRepo==null || "".equals(nomeFileRepo)){
                        nomeFileRepo = "compo_result.rtf";
                    }

                    //toglie i caratteri speciali, tranne il "."
                    nomeFileRepo =  nomeFileRepo.replaceAll("[\\W&&[^\\.]]+", "_");
                    //toglie eventuali doppi (o più) underscore
                    nomeFileRepo =  nomeFileRepo.replaceAll("_{2,}+", "_");

                    RepDACDocument result =
                        params.getResultDocumentCarrier() != null
                            ? params.getResultDocumentCarrier()
                            : new RepDACDocument(nomeFileRepo,descDoc );
                    result.readFromFile( tempOutputFileProduced );

                    RepDACDocument docOut = inserisciFileInRepository(session,params,result,idSessione);

                    inserisciInElementoDocumentale(session,params,result,docOut);

                    return new RpaRunCompositoreResult( docOut );

                } finally {

                    // pulizia finale parametri (se ci sono) e rimozione files temporanei
                    if( idSessione != null && !log.isDebugEnabled() ) {
                        // ha senso fare la delete in una nuova transazione o e' un overkill?
                        // ce la fara' il database di turno a vedere nella transazione
                        // corrente i record salvati nel requiresNew precedente?
                        // ci fidiamo? ... no.
                        this.getRpaBLGLocal().deleteCompoParametersNewTransaction(
                            session                                      ,
                            new DeleteCompoParametersParams( idSessione ));
                    }

                    if( log.isDebugEnabled()) {
                        log.debug( "Modalita' debug attiva, lascio i file temporanei sul server per ispezione" );
                    } else {
                        // cancello i file temporanei
                        for( File f: deadWalkingFiles) {
                            if(f!=null && f.exists()){
                                f.delete();
                            }
                        }
                    }

                }

            }
            catch( IOException          exc ) { throw new SagaException( exc ); }
            catch( InterruptedException exc ) { throw new SagaException( exc ); }
        } catch( SagaException exc ) {
            this.rollback();
            throw exc;
        }
    }

    private void deployFilesTo(
        AutCFGUserSession session,
        File esCompoFolder   ,
        File esCompoClasspath,
        File esCompoTemporary,
        File esCompoLib

    ) throws SagaException,IOException {
        // controllo se esiste la cartella "compositore" nel folder di configurazione
        // di jboss. Se manca lo creo e popolo con i file a seconda del sistema
        // operativo, se c'e' gia' controllo che i file siano aggiornati altrimenti
        // li rimuovo e inserisco quelli corretti.

        // mi assicuro che esistano le cartelle necessarie
        ensureFolder( esCompoFolder    );
        ensureFolder( esCompoClasspath );
        ensureFolder( esCompoTemporary );
        ensureFolder( esCompoLib       );

        boolean isf64 = isF64(session);
        boolean skipUpdate = isSkipUpdateEngine(session);

        File[]        compoFiles              = esCompoFolder.listFiles   ( FolderExcludeFilter.SINGLETON );
        File[]        compoClasspathFiles     = esCompoClasspath.listFiles( FolderExcludeFilter.SINGLETON );
        File[]        compoLibFiles           = esCompoLib.listFiles      ( FolderExcludeFilter.SINGLETON );
        RpaResource[] compoResources          = RpaResource.getResources  ( session, RpaResource.PathSelector.ROOT      ,log.isDebugEnabled(),isf64);
        RpaResource[] compoClasspathResources = RpaResource.getResources  ( session, RpaResource.PathSelector.CLASSPATH ,log.isDebugEnabled(),isf64);
        RpaResource[] compoLibResources       = RpaResource.getResources  ( session, RpaResource.PathSelector.LIB       ,log.isDebugEnabled(),isf64);

        if(    !matches( compoFiles         , compoResources          )
            || !matches( compoClasspathFiles, compoClasspathResources )
            || !matches( compoLibFiles      , compoLibResources       )) {

            log.debug( "Compo non allineato, pulisco ed effettuo nuovo deploy dei file eseguibili" );

            if(!(log.isDebugEnabled() || skipUpdate)){
                for( int c = 0; c < compoFiles.length; c++ ) {
                    if( !compoFiles[c].delete()){
                        throw new SagaException( new Exception( "Impossibile rimuovere il file " + compoFiles[c] ));
                    }
                }
                for( int c = 0; c < compoClasspathFiles.length; c++ ) {
                    if( !compoClasspathFiles[c].delete()){
                        throw new SagaException( new Exception( "Impossibile rimuovere il file " + compoClasspathFiles[c] ));
                    }
                }

                for( int c = 0; c < compoLibFiles.length; c++ ) {
                    if( !compoLibFiles[c].delete()){
                        throw new SagaException( new Exception( "Impossibile rimuovere il file " + compoClasspathFiles[c] ));
                    }
                }
            }

            for( int c = 0; c < compoResources.length; c++ ) {
                compoResources[c].deployTo( esCompoFolder, log.isDebugEnabled(),skipUpdate );
            }
            for( int c = 0; c < compoClasspathResources.length; c++ ) {
                compoClasspathResources[c].deployTo( esCompoClasspath, log.isDebugEnabled(),skipUpdate );
            }
            for( int c = 0; c < compoLibResources.length; c++ ) {
                compoLibResources[c].deployTo( esCompoLib, log.isDebugEnabled(),skipUpdate );
            }

            log.debug( "Deploy dei file eseguibili terminato" );
        }

        // in ambiente linux cambia i permessi
        if(!CmnUtils.isWindows()){
            Process processPermessi = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c","chmod +x compo_jdbc" },null,esCompoFolder);

            BufferedReader brcoPermessi = null;
            BufferedReader brcoPermessiErr = null;
            try{
                brcoPermessi = new BufferedReader( new InputStreamReader( processPermessi.getInputStream() ) );
                String line;
                while( (line = brcoPermessi.readLine()) != null ) {
                    log.debug("RPA-DEBUG:" + line);
                }

                brcoPermessiErr = new BufferedReader( new InputStreamReader( processPermessi.getErrorStream() ) );
                String lineErr;
                while( (lineErr = brcoPermessiErr.readLine()) != null ) {
                    log.debug("RPA-DEBUG:" + line);
                }

            }finally {
                if (brcoPermessi != null) {
                    brcoPermessi.close();
                }
                if (brcoPermessiErr != null) {
                    brcoPermessiErr.close();
                }
                processPermessi.destroy();
            }
        }



    }

    private void ensureFolder( File folder ) throws SagaException {
        if( !folder.exists()) {
            if( !folder.mkdir()) throw new SagaException( new Exception( "Impossibile creare la cartella " + folder + ". Controllare i diritti di accesso al file system." ));
        } else {
            if( !folder.isDirectory()) throw new SagaException( new Exception( folder + " non e' una cartella! Impossibile proseguire." ));
        }
    }

    private boolean matches( File[] files, RpaResource[] resources ) throws SagaException {

        if( files.length != resources.length ) return false;

        // riscritto il controllo di esistenza e di verifica dei file
        for(RpaResource res:resources){
            //trovo il file corrispondente e lo confronto
            File file = null;
            for(File f:files){
                if(f.getName().equals(res.getDeployFileName())){
                    file = f;
                    break;
                }
            }
            // se non ho trovato il file oppure è da aggiornare forzo l'aggiornamento
            if(file==null || !res.matches(file)){
                //al primo che non va bene esco
                return false;
            }
        }

        return true;
    }

    private RpaBLGLocal getRpaBLGLocal() throws SagaException {
        try {
            final InitialContext context = new InitialContext();
            return ((RpaBLGLocalHome)context.lookup( "java:comp/env/ejb/local/RpaBLG" )).create();
        }
        catch( NamingException exc ) { throw new SagaException( exc ); }
        catch( CreateException exc ) { throw new SagaException( exc ); }
    }


    private void inserisciInElementoDocumentale(AutCFGUserSession session,RpaRunCompositoreParams params,RepDACDocument document,RepDACDocument docOut ) throws SagaException {

        if(isAddToDocColl(params)){

            CdtDACPkBaseClass dac = null;
            String idDoc = params.getValore(RpaCostanti.PARAMETRO_ID_DOCUMENTO);
            Long idPraDoc = Ap1Utils.stringToLong(params.getValore(RpaCostanti.PARAMETRO_ID_PRATICA_DOCUMENTO));

            if(idDoc!=null && !"".equals(idDoc)){
                dac = DocService.readDocumento(session,new Long(idDoc));
            }else if(idPraDoc!=null){

                PraDACPraticheDocumenti praDoc = new PraDACPraticheDocumenti();
                praDoc.selTipologiaDocumento();
                praDoc.selRepDocument();
                praDoc.selElementoDocumentale();
                praDoc = read(session, praDoc, idPraDoc);

                if(praDoc.isGestioneDocumentiCollegati(session)){
                    //caso faldone creo l'elemento documentale
                    //se è nessario uscire prima, rimuovere la dipendeza a pra 1.3.136, lasciarla a 1.35.135
                    //e passare idPraDoc
                    dac = PraService.getOrCreateElementoDocumentale(session, praDoc);
                }else {
                    //caso pra_pratiche_documentio_files
                    addToPraDocuemtniFiles(session,praDoc,docOut);
                }
            }else{

                String ctx = params.getContext();
                String table = ctx.substring(0,ctx.indexOf(":"));
                String objPkid = ctx.substring(ctx.indexOf("=")+1);

                ArrayList listaDac = CdtBLGPersistenceConfiguration.getMappedClassFromTable(table, this.getSessionContext());
                Class classeDac = null;

                if (listaDac.size() == 1) {
                    classeDac = (Class)listaDac.get(0);

                    try{
                        CdtDACPkBaseClass daoDac = (CdtDACPkBaseClass)classeDac.newInstance();
                        dac = read(session,daoDac, new Long(objPkid));
                    } catch (Exception exc) {
                        throw new SagaException(exc);
                    }
                }
            }

            if(dac!=null && dac instanceof DocCollegatiInterface){

                DocCollegatiInterface dci = (DocCollegatiInterface)dac;

                try{
                    DocDACCollegatiMaster master= DocUtils.getDocMasterOrCreateNew(
                        session,
                        AutCFGClientSession.isClient() ? AutCFGClientSession.getContext() : new InitialContext(),
                        dci
                    );

                    // copio il documento altrimenti va in errore quando lo scrive sull'hd
                    // RptPrintingRunnable.run_Compositore() -> resultDocument.writeToFile( outputFile );
                    if(docOut==null){
                        RepDACDocument document2 = new RepDACDocument();
                        document2.copyAllFrom(document);
                        master.addDocument(document2);
                        save(session, dac);
                    }else{
                        if(master.getPkid()==null || master.isCreated()){//a volte non setta correttamente il crud
                            master = DocService.getBLGMaster().save(session, master);
                        }

                        DocDACCollegatiDetails dett = new DocDACCollegatiDetails(docOut);
                        dett.prepareForNewInsert();
                        dett.setDocCollegati(master);
                        dett.setMasterId(master.getPkid());
                        dett.setOrdine(RpaUtils.getNextOrdineDocumentoCollezione(session, master));
                        master.addDetail(dett);

                        dci.setDocCollegati(master);

                        if(dac.isRead()){
                            dac.setUpdated();
                        }

                        dac = save(session, dac);

                    }
                } catch (NamingException exc) {
                    throw new SagaException(exc);
                }catch (RemoteException re) {
                    throw CdtUtils.handleRemoteExceptionsAndReturn(re);
                }
            }
        }
    }

    private RepDACDocument inserisciFileInRepository(
        AutCFGUserSession session,
        RpaRunCompositoreParams params,
        RepDACDocument document,
        Long idSessione
    ) throws SagaException {
        RepDACDocument documentOut = document; //se non viene salvato ritorna il documento non persisisto, altrimenti si blocca la stampa a video
        if(RpaCostanti.VALORE_SI.equals(params.getValore(RpaCostanti.PARAMETRO_AGGIUNGI_IN_DOC))){
            documentOut = new RepDACDocument();
            try{
                documentOut.copyAllFrom(document);
                //imposto l'eventuale repository personalizzata per contesto
                String appPrefix = params.getValore(RpaCostanti.PARAMETRO_APP_PREFIX);
                String repLibName = Ap1Params.getParametroString(session,appPrefix,"repository_library"/*cambiare quando sarà rilasciato AP1 Ap1Params.PARAM_REPOSITORY_LIBRARY*/);
                documentOut = RepUtils.getBLG().docInsert(session,repLibName,documentOut);
//                session.put(
//                    AutCFGParamScope.SCOPE_USER_SESSION,
//                    RpaCostanti.APP_PREFIX,
//                    RpaCostanti.PARAMETRO_UTENTE_FILE_REPOSITORY_ID,
//                    documentOut.getPkid().toString()
//                );

//                Mail di Umberto Uderzo del 26.05.2016
//                """""
//                Buongiorno Alberto,
//                Se valorizzi il tutto lato server e la stampa è molto rapida, il callback che forza il reload della
//                cache lato client potrebbe arrivare in ritardo. Forse è il caso di rivedere il meccanismo.
//                Umberto
//                """""
//                quindi persistiamo il dato in DB:
                RpaDACComparam cPar = new RpaDACComparam();
                cPar.setIdSessione( idSessione );
                cPar.setCodice(RpaCostanti.PARAMETRO_FILE_REPOSITORY_ID);
                cPar.setDescr ( null);
                cPar.setValore( documentOut.getPkid().toString() );
                this.save( session, cPar );

            }catch (RemoteException re) {
                throw CdtUtils.handleRemoteExceptionsAndReturn(re);
            }
        }
        return documentOut;
    }



    public void svuotaC0campi(AutCFGUserSession session, String appPrefix) throws SagaException {

        String delete = " delete from RpaDACC0campi c ";
        Object[] par = new Object[]{};

        if(appPrefix != null && !"".equals(appPrefix) ){
            delete += " where c.appPrefix = ? ";
            par = new Object[]{appPrefix.toLowerCase()};
        }

        this.executeHQL( session,delete,par );
    }

    public void svuotaC0entit(AutCFGUserSession session, String appPrefix) throws SagaException {

        String delete = " delete from RpaDACC0entit c ";
        Object[] par = new Object[]{};

        if(appPrefix != null && !"".equals(appPrefix) ){
            delete += " where c.appPrefix = ? ";
            par = new Object[]{appPrefix.toLowerCase()};
        }

        this.executeHQL( session,delete,par );
    }

//    private String semplificaUrlSqlServer(String url){
////        jdbc:sqlserver://SERVER:PORTA;databaseName=DBNAME;
//        String[] par = url.split(";");
//        String server = "";
//        String dbName = "";
//
//        for(int i=0;i<par.length;i++){
//            if(par[i]!=null && par[i].startsWith("jdbc:sqlserver:")){
//                server = par[i];
//            }
//            if(par[i]!=null && par[i].startsWith("databaseName=")){
//                dbName = par[i];
//            }
//        }
//
//        return server + ";" + dbName + ";";
//    }

    private boolean isOdbc(AutCFGUserSession session) throws SagaException {
        // verifico se sono nella situazione SQL SERVER, JBOSS WIN
        // e forzo l'uso di accesso al db tramite ODBC
//        int dbType = CdtBLGServerUtils.getDBType(session.getJ2eeUserName());
        String odbcEnable = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_ENABLE);
        String odbcDriver = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_DRIVER);
        String odbcServer = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_SERVER);
        String odbcDbName = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_DBNAME);
        return     CmnUtils.isWindows()
//                && dbType == CdtBLGServerUtils.DBTYPE_SQL_SERVER
            && RpaCostanti.VALORE_SI.equals(odbcEnable)
            && odbcDriver!=null && !"".equals(odbcDriver)
            && odbcServer!=null && !"".equals(odbcServer)
            && odbcDbName!=null && !"".equals(odbcDbName);
    }

    private String getStrDBType(AutCFGUserSession session) throws AutEXCSessionException {
        String strDbType = "?!?";
        int dbType = CdtBLGServerUtils.getDBType(session.getJ2eeUserName());
        if(dbType == CdtBLGServerUtils.DBTYPE_ORACLE){
            strDbType = DB_TYPE_ORACLE;
        }else if(dbType == CdtBLGServerUtils.DBTYPE_SQL_SERVER){
            strDbType = DB_TYPE_SQLSERVER;
        }else if(dbType == CdtBLGServerUtils.DBTYPE_POSTGRES){
            strDbType = DB_TYPE_POSTGRES;
        }
        return strDbType;
    }


    private String getDBDriver(AutCFGUserSession session) throws AutEXCSessionException {
        String dsDriver = "???";

        int dbType = CdtBLGServerUtils.getDBType(session.getJ2eeUserName());
        if(dbType == CdtBLGServerUtils.DBTYPE_ORACLE){
            dsDriver = DRIVER_JDBC_ORACLE;
        }else if(dbType == CdtBLGServerUtils.DBTYPE_SQL_SERVER){
            dsDriver = DRIVER_JDBC_SQLSERVER;
        }else if(dbType == CdtBLGServerUtils.DBTYPE_POSTGRES){
            dsDriver = DRIVER_JDBC_POSTGRES;
        }
        return dsDriver;
    }


    private RpaComposerStartConfiguration getConnectionsString(
            AutCFGUserSession session, boolean isOdbc, String parameters, boolean isCompoJava
    ) throws SagaException {

        String connectionString = "";

        CdtBLGJDBCConnectionInfo cinfo = CdtBLGPersistenceConfiguration.getJDBCConnectionInfo( this, session);

        String dsURL = null;
        String dsDriver = null;
        String dsUsername = cinfo.getUser();
        String dsPassword = cinfo.getPwd();
        String odbcDbName = null;

        if(isOdbc){
            String odbcDriver = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_DRIVER);
            String odbcServer = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_SERVER);
            odbcDbName        = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_OBDC_DBNAME);
            //                "DRIVER=Nome_driver_odbc;SERVER=HOSTNAME\ISTANZA;UID=xxx;PWD=xxx;DATABASE=nomeDatabase;"
            connectionString = "DRIVER="    + odbcDriver + ";"
                + "SERVER="    + odbcServer + ";"
                + "UID="       + dsUsername + ";"
                + "PWD="       + dsPassword + ";"
                + "DATABASE="  + odbcDbName + ";" ;
        }else{

            // provo a leggere i paramtri di connesione dai parametri dell'applicazione. Altrimetni prendo quelli di sistema.
            // PS Il compositore non supporta la modalità OCI: "jdbc:oracle:oci:@//example.com:5521:bjava21"
            dsDriver = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_JDBC_DRIVER);
            if(dsDriver==null){
                dsDriver = getDBDriver(session);
            }

            dsURL = Ap1Params.getParametroString(session,RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_JDBC_URL);
            if(dsURL==null){
                dsURL = cinfo.getUrl();
            }

            // La connection string deve essere nella forma:
            // "DBMS=JDBC;driverClassName=org.postgresql.Driver;url=jdbc:postgresql://localhost:5432;UID=soncino;PWD=soncino"
            connectionString = "DBMS=JDBC;"
                + "driverClassName=" + dsDriver + ";"
                + "url=" + dsURL + ";"
                + "UID=" + dsUsername + ";"
                + "PWD=" + dsPassword;
        }

        if( log.isDebugEnabled()) {

            String csp[] = connectionString.split(";");
            String connectionStringDebug = "";
            for(int i=0; i<csp.length;i++){
                if(!csp[i].startsWith("PWD=")){
                    connectionStringDebug += csp[i];
                }
            }

            log.debug( "Costruisco la connection string: " + connectionStringDebug );
        }

        Long limitMemorySize = Ap1Params.getParametroLong(session, RpaCostanti.APP_PREFIX, RpaCostanti.PARAMETRO_GLOBALE_LIMIT_SIZE_KB);

        if (limitMemorySize == null || limitMemorySize <= 0) {

            log.error("Il parametro è stato CANCELLATO o E' SBAGLIATO, reimposto quello di DEFAULT (" + RpaCostanti.LIMIT_SIZE_DEFAULT_KB + " KB)");
            session.put(
                    AutCFGParamScope.SCOPE_GLOBAL,
                    RpaCostanti.APP_PREFIX,
                    RpaCostanti.PARAMETRO_GLOBALE_LIMIT_SIZE_KB,
                    RpaCostanti.LIMIT_SIZE_DEFAULT_KB.toString()
            );
            limitMemorySize = RpaCostanti.LIMIT_SIZE_DEFAULT_KB;

        }

        // Recupero il parametro di utilizzo dei parametri DB
        boolean isForceUseDbParameters = false;
        String isForceUseDbParametersString = (String) session.get(
                AutCFGParamScope.SCOPE_GLOBAL, RpaCostanti.APP_PREFIX,
                RpaCostanti.PARAMETRO_GLOBALE_IS_FORCE_USE_DB_PARAMETERS
        );

        if (isForceUseDbParametersString != null && !isForceUseDbParametersString.isEmpty()) {

            isForceUseDbParameters = !isForceUseDbParametersString.equals("0");

        }

        RpaComposerStartConfiguration composerStartConfiguration = new RpaComposerStartConfiguration(
            odbcDbName,
            dsDriver,
            dsURL,
            dsUsername,
            dsPassword,
            null,
            null,
            null,
            getStrDBType(session),
            connectionString,
            session.getLogonUser().getPkid(),
            parameters, //idSessione!=null?idSessione.intValue():-1,
            log.isDebugEnabled(),
            log.isWarnEnabled(),
            log.isErrorEnabled(),
            new RpaImportExternalImageDocumentale(session),
            limitMemorySize,
            isForceUseDbParameters
        );

        // Mi raccomando!!! Chiudere la sessione Hibernate E NON quella di JDBC dopo l'avvio del compositore!
        if (isCompoJava) {

            composerStartConfiguration.setConnection(getCdtHibernateSession(session));

        }

        return composerStartConfiguration;

    }

    private String getEseguibile(boolean isOdbc, File esCompoFolder) throws SagaException {
        String eseguibile = esCompoFolder.getAbsolutePath();
        if(isOdbc){
            eseguibile += "/compo_odbc.exe";
        }else{
            if(CmnUtils.isWindows()){
                eseguibile += "/compo_jdbc.exe";
            }else{
                eseguibile += "/compo_jdbc";
            }
        }
        return eseguibile;
    };


    private void eseguiCompositoreMain(
        AutCFGUserSession session,
        File logFile,
        File esCompoFolder,
        File tempErrFile,
        String command,
        String commandDebug,
        File tempInputFile,
        File tempOutputFileProduced,
        String parametri,
        String parametriSessione,
        String nomeFileModello,
        RpaComposerStartConfiguration compoConf,
        boolean isCompoJava
    ) throws SagaException,IOException,InterruptedException {

        //prima controllo a livello di utente, poi a livello
        String wsUrl =Ap1Params.getParametroString(
            AutCFGParamScope.SCOPE_USER,
            session,
            RpaCostanti.APP_PREFIX,
            RpaCostanti.PARAMETRO_GLOBALE_WS_URL
        );
        if(wsUrl==null){
            wsUrl =Ap1Params.getParametroString(
                AutCFGParamScope.SCOPE_GLOBAL,
                session,
                RpaCostanti.APP_PREFIX,
                RpaCostanti.PARAMETRO_GLOBALE_WS_URL
            );
        }

        if(isCompoJava){
            eseguiCompositoreJava(compoConf, tempInputFile, tempOutputFileProduced, parametri);
        }else if(wsUrl!=null){
            eseguiCompositoreWS(session,wsUrl,tempInputFile, tempOutputFileProduced, parametri, parametriSessione);
        }else{
            eseguiCompositoreLegacy(session, logFile, esCompoFolder, tempErrFile, command,commandDebug,nomeFileModello);
        }
    }

    private void eseguiCompositoreWS(
        AutCFGUserSession session,
        String wsUrl,
        File tempInputFile,
        File tempOutputFileProduced,
        String parametri,
        String parametriSessione
    ) throws SagaException, IOException {

        log.debug("Esecuzione Compositore WebService");

        String wsApp =Ap1Params.getParametroString(
            AutCFGParamScope.SCOPE_USER,
            session,
            RpaCostanti.APP_PREFIX,
            RpaCostanti.PARAMETRO_GLOBALE_WS_APP
        );
        if(wsApp==null){
            wsApp =Ap1Params.getParametroString(
                AutCFGParamScope.SCOPE_GLOBAL,
                session,
                RpaCostanti.APP_PREFIX,
                RpaCostanti.PARAMETRO_GLOBALE_WS_APP
            );
        }
        if(wsApp==null){
            wsApp = RpaCostanti.PARAMETRO_WS_APP_DEFAULT;
        }

        String table   = parametri.substring(0,parametri.indexOf(":"));
        String column  = parametri.substring(parametri.indexOf(":")+1,parametri.indexOf("="));
        String objPkid = parametri.substring(parametri.indexOf("=")+1);

        ServizioCompositoreProxy proxy = new ServizioCompositoreProxy(wsUrl);

        try {

            // riscritto per java 1.6
            byte[] arrayFileInput = new byte[(int) tempInputFile.length()];
            FileInputStream fis = new FileInputStream(tempInputFile);
            fis.read(arrayFileInput); //read file into bytes[]
            fis.close();

            byte[] risultato = proxy.componiStream(
                tempOutputFileProduced.getName(),                               // nome file input - temporaneo
                arrayFileInput,
                //Files.readAllBytes(Paths.get(tempInputFile.getAbsolutePath())), // contenuto del file in input
                table,                                                          // entita di lancio - es: APE_CONCES
                column,                                                         // campo chiave - es: PKID
                new String[] {objPkid},                                         // valore chiave - es: 1234
                wsApp,                                                          // alias della connessione al db, default "sicraweb" vedi nel WS Server ->  compositore.properties
                wsApp.toUpperCase(),                                            // alias cartella di lavoro, default "SICRAWEB" come sopra, ma in maiuscolo - vedi nel WS Server ->  compositore.properties
                parametriSessione                                               // valore registro, altri parametri aggiuntivi es: "86=50"
            );
            if(risultato!=null){
                FileOutputStream fos = new FileOutputStream(tempOutputFileProduced.getAbsolutePath());
                fos.write(risultato);
                fos.close();
            }
        } catch (CompositoreException ce) {
            throw new SagaException(ce);
        } catch (RemoteException re) {
            throw CdtUtils.handleRemoteExceptionsAndReturn(re);
        }
    }


    private void eseguiCompositoreLegacy(
        AutCFGUserSession session,
        File logFile,
        File esCompoFolder,
        File tempErrFile,
        String command,
        String commandDebug,
        String nomeFileModello
    ) throws SagaException,IOException,InterruptedException {

        log.debug("Esecuzione Compositore Legacy");

        if(log.isDebugEnabled()){
            // se sono in debug
            // creo il file di log che verra' poi popolato
            // dall'eseguibile (se lo rileva lo popola automaticamente)
            if(!logFile.exists()){
                logFile.createNewFile();
                log.debug( "Creo il log file vuoto per il compositore" );
            }
        }else if(logFile.exists()){
            //se non sono in debug, ed il file log esiste lo cancello
            logFile.delete();
        }

        // ATTENZIONE: Se non si svuota (leggendolo) l'inputStream del processo
        // questo potrebbe bloccarsi in attesa che si liberi il buffer, causando
        // pertanto un deadlock. Quindi e' necessario effettuare la lettura
        // dell'inputStream, che tornera' null solo quando il processo chiudera'
        // lo stream stesso (al termine della sua esecuzione). In questo caso
        // l'utilizzo di waitFor() dovrebbe essere superfluo, ma tant'e'...

        // Il problema principale e' che il processo invocato POTREBBE scrivere
        // anche sul suo errorStream, che potrebbe quindi causare un deadlock
        // a sua volta. Non e' questo il caso del compositore, fortunatamente,
        // ma in generale il problema esiste e per risolverlo e' necessario
        // fare in modo che ENTRAMBI i buffer vengano mantenuti svuotati.
        // Quindi e' necessario l'uso di thread oppure il redirect dell'error
        // stream sull'input stream (che ovviamente con Java 1.4
        // non e' possibile altrimenti era troppo facile)

        int            errCode   =   -1;
        BufferedReader brInp     = null;
        BufferedReader brErr     = null;
        String         line      = null;
        StringBuffer   inpBuffer = new StringBuffer();
        StringBuffer   errBuffer = new StringBuffer();
        Long pid = null;

        // Gestione timeout
        // deve essere configurato il parametro
        // LINUX: ed installato il comando timeout
        // WINDOWS: da realizzare con bat...
        Long timeoutSec = Ap1Params.getParametroLong(
            session,
            RpaCostanti.APP_PREFIX,
            RpaCostanti.PARAMETRO_GLOBALE_PROCESS_TIMEOUT
        );
        if(timeoutSec!=null && timeoutSec.intValue()>0){
            if(CmnUtils.isWindows()){
                // https://technet.microsoft.com/en-us/library/cc754891%28v=ws.11%29.aspx
                // http://stackoverflow.com/questions/13515254/how-to-set-a-timeout-for-a-process-under-windows-7
                // non ancora implementato
            }else{
                // -s 9 = kill - tempo in secondi - http://linux.die.net/man/1/timeout
                command = "timeout -s 9 " + timeoutSec + "s " + command;
            }
        }

        // esecuzione ed attesa del codice di terminazione del processo.
        // memorizzo il contenuto dell'error e dell'input in caso di problemi
        if( log.isDebugEnabled()){
            log.debug( "Eseguo il comando" );
        }
        Process pCompositore = null;
        if(CmnUtils.isWindows()){
            pCompositore = CmnUtils.execute( command, esCompoFolder.getAbsolutePath());
        }else{
            pCompositore = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c",command},null,esCompoFolder);
        }
        //aggiungo il process nella lista...
        pid = addProcesPidToMap(session,pCompositore,nomeFileModello,commandDebug);

        try {

            brInp = new BufferedReader( new InputStreamReader( pCompositore.getInputStream()));
            try {
                while( ( line = brInp.readLine()) != null ) {
                    inpBuffer.append( line + "\n" );
                    log.debug("RPA-Console Output: " + line );
                }
            } finally {
                brInp.close();
            }

            brErr = new BufferedReader( new InputStreamReader( pCompositore.getErrorStream()));
            try {
                while( ( line = brErr.readLine()) != null ){
                    errBuffer.append( line + "\n" );
                    log.debug("RPA-Console Error: " + line );
                }
            } finally {
                brInp.close();
                brErr.close();
            }

            pCompositore.waitFor();
            errCode = pCompositore.exitValue();

        } finally {
            removeProcesPidToMap(session,pid);
            pCompositore.destroy();
        }

        log.debug( "Comando terminato con codice di errore " + errCode );

        // CODICI DI ERRORE:
        // -1 errore connessione
        // -2 errore compilazione
        // -3 errore composizione
        // pare che una esecuzione corretta torni 1...

        //In linux ritorna sempre 0 :-(

        //In linux lo fa lo stesso, pazienza....
        if(errCode!=1){
            if(logFile.exists()){
                BufferedReader brFileLog = null;
                try{
                    brFileLog = new BufferedReader(new FileReader(logFile));
                    while( ( line = brFileLog.readLine()) != null ) {
                        log.info("RPA-COMPOSITORE: " + line );
                    }
                }finally{
                    if (brFileLog != null) {
                        brFileLog.close();
                    }
                }
            }else{
                log.error("File di log del compositore non trovato! ( " + logFile.getAbsolutePath() + " )");
            }
        }

        boolean isLinuxError = false;

        if( errCode < 0  || !CmnUtils.isWindows()) {

            // contenitore di eventuali messaggi di dettaglio dell'errore
            MsgDACContainer msgs = new MsgDACContainer();

            switch( errCode ) {
                case -1: {
                    msgs.addNoThrow( new MsgDACMessage(
                        MsgDACMessage.TYPE_ERROR,
                        RpaBLGBean.class        ,
                        "errCodeConnectionError" ));
                    break;
                }
                case -2: {
                    msgs.addNoThrow( new MsgDACMessage(
                        MsgDACMessage.TYPE_ERROR,
                        RpaBLGBean.class        ,
                        "errCodeCompilationError" ));
                    break;
                }
                case -3: {
                    msgs.addNoThrow( new MsgDACMessage(
                        MsgDACMessage.TYPE_ERROR,
                        RpaBLGBean.class        ,
                        "errCodeMergeError"     ));
                    break;
                }
            }

            // in questo caso controllo se esiste il file err, nel caso lo carico
            // e ne aggiungo il contenuto al messaggio
            if( tempErrFile.exists()) {
                isLinuxError = true;
                if( log.isDebugEnabled()) log.debug( "Leggo il contenuto del file .err" );
                RepDACDocument errDoc = new RepDACDocument();
                errDoc.readFromFile( tempErrFile );
                String errFileContent = new String( errDoc.getContent()); // e' un file di testo, lo converto in stringa
                msgs.addNoThrow( new MsgDACMessage(
                    MsgDACMessage.TYPE_ERROR,
                    RpaBLGBean.class        ,
                    "errFileContent"        ,
                    new LocParameter( "errFileContent", errFileContent )
                ));
            }

            // recupero il file di log (se esiste)
            if(logFile.exists()) {
                if( log.isDebugEnabled()) log.debug( "Leggo il contenuto del file .log" );
                RepDACDocument logDoc = new RepDACDocument();
                logDoc.readFromFile( logFile );
                String logFileContent = new String( logDoc.getContent()); // e' un file di testo, lo converto in stringa
                msgs.addNoThrow( new MsgDACMessage(
                    MsgDACMessage.TYPE_ERROR,
                    RpaBLGBean.class        ,
                    "logFileContent"        ,
                    new LocParameter( "logFileContent", logFileContent )
                ));
            }

            // recupero la console del processo
            if( inpBuffer.length() > 0 ) {
                msgs.addNoThrow( new MsgDACMessage(
                    MsgDACMessage.TYPE_ERROR,
                    RpaBLGBean.class        ,
                    "consoleContent"        ,
                    new LocParameter( "consoleContent", inpBuffer.toString())
                ));
            }

            // recupero l'error del processo
            if( errBuffer.length() > 0 ) {
                msgs.addNoThrow( new MsgDACMessage(
                    MsgDACMessage.TYPE_ERROR,
                    RpaBLGBean.class        ,
                    "errorContent"          ,
                    new LocParameter( "errorContent", errBuffer.toString())
                ));
            }

            Exception baseException = new Exception(
                "Si e' verificato un errore durante l'esecuzione "
                    + "del motore di stampa esterno (errCode=" + errCode + ")" );

            //in windows ci va sempre dentro, in linux solo se esiste il file di log
            if(isLinuxError || CmnUtils.isWindows()){
                if( msgs.existsNewMessages()) {
                    throw new SagaException(
                        baseException,
                        new MsgDACMessage(
                            MsgDACMessage.TYPE_ERROR,
                            RpaBLGBean.class        ,
                            "errorDetails"
                        ).setChildMessages( msgs )
                    );
                } else {
                    throw new SagaException( baseException );
                }
            }
        }
    }

    public Long runCompositoreTest(
        AutCFGUserSession session,
        String urlModello,
        String parametri,
        String parametriSessione,
        Boolean compoJava,
        Boolean isApplyFirma
    ) throws SagaException {

        Long idRepStampa = null;

        RpaComposerStartConfiguration compoConf = null;

        try {
            try {

                File esCompoFolder    = new File( CdtBLGPersistenceConfiguration.getInstallationPath(), "compositore" );
                File esCompoClasspath = new File( esCompoFolder, "classpath" );
                File esCompoTemporary = new File( esCompoFolder, "temporary" );
                File esCompoLib       = new File( esCompoFolder, "lib" );

                this.deployFilesTo(session,esCompoFolder,esCompoClasspath,esCompoTemporary,esCompoLib);

                boolean isOdbc = isOdbc(session);

                // questi sono i due file principali
                String nomeFileModello = RpaUtils.getFileNameFormUrl(urlModello);
                String extFileModello  = RpaUtils.getFileExtension(urlModello);

                if (isApplyFirma) {

                    extFileModello = "pdf";

                }

                File tempInputFile           = new File(urlModello)  ;
                File tempOutputFileRequested = RpaUtils.createTempFile( "compo_", "_out"   , esCompoTemporary ); // questo serve perchè altriemnti viene fuori casino
                File tempOutputFileProduced  = new File( tempOutputFileRequested.getAbsoluteFile() + "." + extFileModello );

                // questi sono i file di appoggio che il compo crea al runtime
                // (ci sarebbero anche i compilati ma per ora li rimuovo al termine
                // dell'esecuzione per non lasciare troppi file orgfani nella cartella
                // del server). Questi file si basano sul nome del file di input
                // (il modello, il cui nome varia sempre perche' e' un temp file)
                String baseInputFileName = tempInputFile.getName();
                baseInputFileName = baseInputFileName.substring( 0, baseInputFileName.length() - ".rtf".length());
                File tempOkFile  = new File( esCompoTemporary, baseInputFileName + ".ok"           );
                File tempInfFile = new File( esCompoTemporary, baseInputFileName + ".inf"          );
                File tempIdxFile = new File( esCompoTemporary, baseInputFileName + ".idx"          );
                File tempErrFile = new File( esCompoTemporary, baseInputFileName + ".err"          );
                File tempSemFile = new File( esCompoTemporary, baseInputFileName + ".rtf.semaforo" );
                String nomeFileLog = ( isOdbc ? "compo_odbc.log" : "compo_jdbc.log"                );
                File logFile = new File( esCompoFolder, nomeFileLog );


                // la lista dei file che dovro' eliminare al termine dell'esecuzione
                // (se presenti)
                File[] deadWalkingFiles = new File[] {
                    tempInputFile          ,
                    tempOutputFileRequested, // cancello anche il file temporaneo che non verra' usato dal compo...
                    tempOutputFileProduced ,
                    tempOkFile             ,
                    tempInfFile            ,
                    tempIdxFile            ,
                    tempErrFile            ,
                    tempSemFile
//              logFile
                };

                try {


                    // preparo la riga di comando
                    // OPZIONI:
                    // -q  Esecuzione query (verifica se il testo e' compilato)
                    // -c  Compilazione Documento
                    // -o  Composizione Documento
                    // -d  Eliminazione file composto
                    // -f  Lettura dei comandi da file


                    // in linux l'apice doppio crea dei problemi se nella stringa c'è un nome di una variabile
                    String apice = "\"";
                    if (!CmnUtils.isWindows()){
                        apice="'";
                    }

                    String strDbType = getStrDBType(session);
                    String eseguibile = getEseguibile(isOdbc, esCompoFolder);

//                + apice + ( idSessione != null ? "80=" + idSessione + ";" : "" )
//                + "86=" + session.getLogonUser().getPkid() + ";"
//                + "81=" + CdtBLGServerUtils.getDBType(session.getJ2eeUserName()) + apice;

                    if(parametriSessione!=null && !parametriSessione.equals("")){
                        parametriSessione += ";";
                    }else{
                        parametriSessione = "";
                    }
                    parametriSessione += "86=" + session.getLogonUser().getPkid() + ";";
                    parametriSessione += "81=" + CdtBLGServerUtils.getDBType(session.getJ2eeUserName());

                    compoConf = getConnectionsString(session, isOdbc, parametriSessione, compoJava);


                    //    in windows uso il percorso completo altrimenti va in errore
                    //    in linux devo basta il nome del programma, la directory di lavoro viene passata alla shell
                    String command =
                        apice + eseguibile + apice
                            + " -o "
                            + apice + tempInputFile.getAbsolutePath() + apice
                            + " "
                            + apice + compoConf.getConnectionString() + apice
                            + " " + strDbType + " "
                            + apice + tempOutputFileProduced.getAbsolutePath() + apice
                            + " "
                            + (parametri != null ? parametri + " " : "" ) // APE_CONCES:PKID=27
                            + apice + "row=1;last=1" + apice
                            + " "
                            + apice + parametriSessione + apice;


                    String commandDebug =
                        apice + eseguibile + apice
                            + " -o "
                            + apice + tempInputFile.getAbsolutePath() + apice
                            + " "
                            + apice + "***CONNECTION STRING MASCHERATA***" + apice
                            + " " + strDbType + " "
                            + apice + tempOutputFileProduced.getAbsolutePath() + apice
                            + " "
                            + ( parametri != null ? parametri + " "  : "" ) // APE_CONCES:PKID=27
                            + apice + "row=1;last=1 " + apice
                            + " "
                            + apice + parametriSessione + apice;
                    log.debug( "Costruisco il comando di avvio compositore: " + commandDebug );


                    eseguiCompositoreMain(
                        session,
                        logFile,
                        esCompoFolder,
                        tempErrFile,
                        command,
                        commandDebug,
                        tempInputFile,
                        tempOutputFileProduced,
                        parametri,
                        parametriSessione,
                        nomeFileModello,
                        compoConf,
                        compoJava
                    );

                    if(tempOutputFileProduced.exists()){
                        RepDACDocument result = new RepDACDocument(nomeFileModello,"test compositore");
                        result.readFromFile( tempOutputFileProduced );
                        result = RepUtils.getBLG().docInsert(session,result);
                        idRepStampa = result.getPkid();
                    }

                } finally {
                    if( log.isDebugEnabled()) {
                        log.debug( "Modalita' debug attiva, lascio i file temporanei sul server per ispezione" );
                    } else {
                        // cancello i file temporanei
                        for( int c = 0; c < deadWalkingFiles.length; c++ ) {
                            if( deadWalkingFiles[c].exists()){
                                deadWalkingFiles[c].delete();
                            }
                        }
                    }
                    if(compoConf!=null && compoConf.getConnection()!=null){
                        this.closeHibernateSessionBoundWithUserSession(compoConf.getConnection());
                    }
                }

            }catch( IOException exc ) {
                throw new SagaException( exc );
            }catch( InterruptedException exc ){
                throw new SagaException( exc );
            }
        } catch( SagaException exc ) {
            this.rollback();
            throw exc;
        }

        return idRepStampa;
    }


    public Long getFileLogRep(AutCFGUserSession session) throws SagaException {
        Long idLog=null;

        try {
            File esCompoFolder = new File( CdtBLGPersistenceConfiguration.getInstallationPath(), "compositore" );
            String nomeFileLog = ( isOdbc(session) ? "compo_odbc.log" : "compo_jdbc.log");
            File logFile = new File( esCompoFolder, nomeFileLog );

            if(logFile.exists()){
                RepDACDocument result = new RepDACDocument(nomeFileLog,"Log compositore");
                result.readFromFile( logFile );
                result = RepUtils.getBLG().docInsert(session,result);
                idLog = result.getPkid();
            }

        } catch (Exception exc) {
            throw new SagaException(exc);
        }

        return idLog;

    }

    // differenza di ape, non lo crea in /tmp ma nella directory temporanea del compositore
    public String creaFileTempDaRepo(AutCFGUserSession session,Long idRepo, boolean isApplySignature) throws SagaException {
        String urlFile = null;
        try{
            RepDACDocument document = (RepDACDocument) RepUtils.getBLG().read(session,new RepDACDocument(),idRepo);
            document = RepUtils.getBLG().docExtract(session,document);

            String nomeFile = document.getDocumentName();
            String estFile = ".rtf";
            String nomeDoc = document.getDocumentName();

            if(nomeDoc!=null && !"".equals(nomeDoc)){
                int idx = nomeDoc.lastIndexOf(".");
                if(idx>0){
                    nomeFile = nomeDoc.substring(0, idx);
                    estFile = nomeDoc.substring(idx);
                }else{
                    // se il nome del file è malformato lo fisso
                    nomeFile = "modello";
                }
            }else{
                // se il nome del file è malformato lo fisso
                nomeFile = "modello";
            }

            if(nomeFile==null || "".equals(nomeFile)){
                throw new SagaException(new Exception("Il file letto da repository non ha nome."));
            }

            if (isApplySignature) {

                estFile = ".pdf";

            }

            File esCompoFolder    = new File( CdtBLGPersistenceConfiguration.getInstallationPath(), "compositore" );
            File esCompoTemporary = new File( esCompoFolder, "temporary" );
            // 79896 altrimenti se per sfortuna avviano l'utility di test delle stampe prima
            // di aver avviato il compositore va in errore e non si riesce a capire il motivo
            ensureFolder(esCompoFolder);
            ensureFolder(esCompoTemporary);
            File tempInputFile = RpaUtils.createTempFile( nomeFile, estFile, esCompoTemporary );

            document.writeToFile(tempInputFile);
            urlFile = tempInputFile.getAbsolutePath();
        }catch(RemoteException re){
            throw new SagaException(re);
        } catch (IOException ioe) {
            throw new SagaException(ioe);
        }

        return urlFile;
    }

    private boolean isAddToDocColl(RpaRunCompositoreParams params){
        // utilizzato questo metodo per retrocompatibilità
        // se il nuovo parametro non viene gestito, va ad utilizzare quello "vecchio"
        String val = params.getValore(RpaCostanti.PARAMETRO_SALVA_IN_DOC_COLLEGATI);
        if(val==null){
            val = params.getValore(RpaCostanti.PARAMETRO_AGGIUNGI_IN_DOC);
        }
        return RpaCostanti.VALORE_SI.equals(val);
    }


    private Long getIdSessionCompo(AutCFGUserSession session) throws AutEXCSessionException {
        //A.M.l'id sessione è troppo grande compngo un numero dato:
        // dall'utente corrente + le ultime tre cifre dell'id sessione(new Long( session.getSessionID()%1000), 3);
        return new Long(
            session.getLogonUser().getPkid().longValue()*1000
                + session.getSessionID()%1000
        );
    }

    public Long getIdFileReopository(AutCFGUserSession session) throws SagaException {
        // lasciare questo metodo come support altrimenti cambia l'id della sessione

        Long val = null;
        Long idSessione = getIdSessionCompo(session);

        RpaDACComparam result = (RpaDACComparam)readCollection(
            session,
            " select r " +
                " from RpaDACComparam r " +
                " where r.idSessione = ? " +
                "   and r.codice = ? ",
            new Object[] { idSessione, RpaCostanti.PARAMETRO_FILE_REPOSITORY_ID }
        ).getFirstItem();

        if(result!=null && result.getValore()!=null){
            val = (Long)BonUtility.transform(result.getValore(),Long.class);
        }

        if(!log.isDebugEnabled() && result!=null){
            delete(session, result,true,true);
        }

        return val;
    }

    private Long getProcessPid(AutCFGUserSession session,Process process) throws SagaException {

        long pid = -1;

        // LINUX
        //http://stackoverflow.com/questions/4750470/how-to-get-pid-of-process-ive-just-started-within-java-program
        if(process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {

                Long timeoutSec = Ap1Params.getParametroLong(
                    session,
                    RpaCostanti.APP_PREFIX,
                    RpaCostanti.PARAMETRO_GLOBALE_PROCESS_TIMEOUT
                );
                boolean isTimeout = timeoutSec!=null && timeoutSec.intValue()>0;

                if(isTimeout){
                    //il timeout genera un altro processo diverso oltre a compo_jdbc, bisogna usare ps per ottenere il processo
                    BufferedReader brco = null;
                    try{
                        Process processInfoLinux = null;
                        try{
                            //http://serverfault.com/questions/27887/how-to-sort-ps-output-by-process-start-time
                            processInfoLinux = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c","ps -A --sort=start_time | grep compo_jdbc"},null);
                            //ottengo i processi ordinati per data avvio crescente - in caso di più di uno prendo l'ultimo
                            brco = new BufferedReader( new InputStreamReader( processInfoLinux.getInputStream() ) );
                            String line;
                            while( (line = brco.readLine()) != null) {
                                if(line!=null && line.length()>0){
                                    pid = RpaUtils.getNumberFromString(line);
                                }
                            }
                        }finally {
                            if (brco != null) {
                                brco.close();
                            }
                            processInfoLinux.destroy();
                        }
                    }catch( IOException exc ) {
                        throw new SagaException( exc );
                    }
                }else{
                    Field f = process.getClass().getDeclaredField("pid");
                    f.setAccessible(true);
                    pid = f.getLong(process);
                    f.setAccessible(false);
                }
            } catch (Exception exc) {
                throw new SagaException( exc );
            }
        }else if(    process.getClass().getName().equals("java.lang.Win32Process")
            || process.getClass().getName().equals("java.lang.ProcessImpl") ){
            // non usato, richiede librerie esterne  http://www.golesny.de/p/code/javagetpid

            //uso il comando wmic disponibile in
            // Windows Vista, Windows Server 2008, Windows 7, Windows Server 2008 R2, Windows Server 2012, Windows 8
            // https://technet.microsoft.com/en-us/library/cc754534(v=ws.11).aspx
            String eseguibile = "compo_jdbc.exe";
            if(isOdbc(session)){
                eseguibile = "compo_odbc.exe";
            }
            BufferedReader brco = null;

            try{
                Process processInfoWin = null;
                try{
                    // https://msdn.microsoft.com/en-us/library/aa394372(v=vs.85).aspx
                    // https://technet.microsoft.com/en-us/library/cc757287(v=ws.10).aspx
                    processInfoWin = CmnUtils.execute("wmic process where \"name='" + eseguibile + "'\" get ProcessID, Commandline /format:list:\"sortby=CreationDate\"");
                    //ottengo i processi ordinati per data avvio crescente - in caso di più di uno prendo l'ultimo
                    brco = new BufferedReader( new InputStreamReader( processInfoWin.getInputStream() ) );
                    String line;
                    while( (line = brco.readLine()) != null ) {
                        if(line!=null && line.startsWith("ProcessId")){
                            pid = RpaUtils.getNumberFromString(line);
                        }
                    }
                }finally {
                    if (brco != null) {
                        brco.close();
                    }
                    processInfoWin.destroy();
                }
            }catch( IOException exc ) {
                throw new SagaException( exc );
            }
        }
        return new Long(pid);
    }

    private Long addProcesPidToMap(AutCFGUserSession session,Process process, String nomeFileModello,String commandDebug) throws SagaException {
        Long pid = getProcessPid(session,process);
        if(pid>-1){
            getPidList(session).put(pid,new RpaProcessInfo(pid,nomeFileModello,commandDebug,session.getLogonUserName()));
        }
        log.debug("PID=" + pid + " - nomeFile=" + nomeFileModello );
        return pid;
    }

    private void removeProcesPidToMap(AutCFGUserSession session,Long pid) throws SagaException {
        if(pid>-1){
            getPidList(session).remove(pid);
        }
    }

    public int killProcess(AutCFGUserSession session,Long pid) throws SagaException {

        int out = -1;

        Process process = null;
        BufferedReader brcoKill = null;
        BufferedReader brcoErrKill = null;

        try{

            if (CmnUtils.isWindows()){
                process = CmnUtils.execute("taskkill /F /PID " + pid);
            }else{
                process = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c","kill -9 " + pid },null);
            }

            try{
                brcoKill = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
                String line;
                while( (line = brcoKill.readLine()) != null ) {
                    log.debug("RPA-DEBUG: kill:" + line);
                }
                brcoErrKill = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
                String lineErr;
                while( (lineErr = brcoErrKill.readLine()) != null ) {
                    log.debug("RPA-DEBUG: kill:" + line);
                }
                removeProcesPidToMap(session, pid);
                out=1;
            }finally {
                if (brcoKill != null) {
                    brcoKill.close();
                }
                if (brcoErrKill != null) {
                    brcoErrKill.close();
                }
                process.destroy();
            }

        }catch( IOException exc ) {
            throw new SagaException( exc );
        }

        return out;
    }

    public int killAllProcess(AutCFGUserSession session) throws SagaException {

        int out = -1;

        Process process = null;
        BufferedReader brcoKill = null;
        BufferedReader brcoErrKill = null;

        try{

            if (CmnUtils.isWindows()){
                String eseguibile = "compo_jdbc.exe";
                if(isOdbc(session)){
                    eseguibile = "compo_odbc.exe";
                }
                process = CmnUtils.execute("taskkill /F /IM " + eseguibile);
            }else{
                process = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c","killall -r compo_jdbc" },null);  //LINUX usa solo JDBC
            }

            try{
                brcoKill = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
                String line;
                while( (line = brcoKill.readLine()) != null ) {
                    log.debug("RPA-DEBUG: kill:" + line);
                }
                brcoErrKill = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
                String lineErr;
                while( (lineErr = brcoErrKill.readLine()) != null ) {
                    log.debug("RPA-DEBUG: kill:" + line);
                }
                out=1;
            }finally {
                if (brcoKill != null) {
                    brcoKill.close();
                }
                if (brcoErrKill != null) {
                    brcoErrKill.close();
                }
                process.destroy();
            }

        }catch( IOException exc ) {
            throw new SagaException( exc );
        }

        return out;
    }

    public String getListaProcessi(AutCFGUserSession session) throws SagaException {

        String out = "";

        Process process = null;
        BufferedReader brco = null;
        BufferedReader brcoErr = null;

        try{

            if (CmnUtils.isWindows()){
                String eseguibile = "compo_jdbc.exe";
                if(isOdbc(session)){
                    eseguibile = "compo_odbc.exe";
                }
                process = CmnUtils.execute("wmic process where \"name='" + eseguibile + "'\" get ProcessID,name /format:list");
            }else{
                process = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c","ps -A | grep compo_jdbc" },null);  //LINUX usa solo JDBC
            }

            try{
                brco = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
                String line;
                while( (line = brco.readLine()) != null ) {
                    out += line + "\n";
                    log.debug("RPA-DEBUG: list:" + line);
                }
                brcoErr = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
                String lineErr;
                while( (lineErr = brcoErr.readLine()) != null ) {
                    log.debug("RPA-DEBUG: list:" + line);
                }
            }finally {
                if (brco != null) {
                    brco.close();
                }
                if (brcoErr != null) {
                    brcoErr.close();
                }
                process.destroy();
            }

        }catch( IOException exc ) {
            throw new SagaException( exc );
        }

        return out;
    }


    public HashMap<Long,RpaProcessInfo> getPidList(AutCFGUserSession session) throws SagaException {
        return getPidList(session, false);
    }

    public HashMap<Long,RpaProcessInfo> getPidList(AutCFGUserSession session,boolean refresh) throws SagaException {

        Map mappa = AutClusterCache.getCache(
            session.getJ2eeUserName(),
            Ap1Costanti.APP_PREFIX_APE,
            RpaCostanti.CACHE_LATO_SERVER_RPA,
            AutClusterCache.Flavour.LOCAL_BYREF
        );

        HashMap<Long,RpaProcessInfo> pidList = (HashMap<Long,RpaProcessInfo>)mappa.get(RpaCostanti.KEY_MAPPING_PID_LIST);

        if(pidList==null || refresh){
            pidList = new HashMap<Long,RpaProcessInfo>();
            mappa.put(RpaCostanti.KEY_MAPPING_PID_LIST,pidList);
        }

        return pidList;
    }

    private boolean isF64(AutCFGUserSession session) throws SagaException {
        boolean enabled = false;
        if(!CmnUtils.isWindows()){
            String val = Ap1Params.getParametroString(session, RpaCostanti.APP_PREFIX,RpaCostanti.PARAMETRO_GLOBALE_MODALITA_F64_ENABLED);
            if(RpaCostanti.VALORE_SI.equals(val)){
                enabled = true;
                log.debug("OS LINUNX - Abilitata modalita compatibilita' 64bit");
            }
        }
        return enabled;
    }

    private boolean isSkipUpdateEngine(AutCFGUserSession session) throws SagaException {
        boolean enabled = false;
        String val = Ap1Params.getParametroString(session, RpaCostanti.APP_PREFIX,RpaCostanti.PARAMETRO_GLOBALE_NON_AGGIORNARE_ESEGUIBILE);
        if(RpaCostanti.VALORE_SI.equals(val)){
            enabled = true;
            log.info("Abilitata modalita: \"Non aggiornare l'eseguibile e librerie\"");
        }
        return enabled;
    }


    public RpaProcessInfo getInfoEseguibile(AutCFGUserSession session) throws SagaException {
        RpaProcessInfo rpi = new RpaProcessInfo(null,null,null,null);

        File esCompoFolder = new File( CdtBLGPersistenceConfiguration.getInstallationPath(), "compositore" );
        String pathFile = getEseguibile(isOdbc(session), esCompoFolder);
        rpi.setInfoCompositore(new File(pathFile));
        rpi.setF64(isF64(session));

        return rpi;
    }

    private void eseguiCompositoreJava(
        RpaComposerStartConfiguration compoConf,
        File tempInputFile,
        File tempOutputFileProduced,
        String parametri
    ) throws SagaException {

        log.debug("Esecuzione Compositore JAVA");

        compoConf.setDocumentFilePath(tempInputFile.getAbsolutePath());
        compoConf.setOutputFilePath(tempOutputFileProduced.getAbsolutePath());
        compoConf.setEntityDeclaration(parametri);

        //init di aspose
        try {
            new DocAsposeWordProcessor();
        } catch (DocWordProcessorException dwpe) {
            throw new SagaException(dwpe);
        }

        //Compositore Java
        RpaMainCompositore compositore = new RpaMainCompositore(compoConf);

        try {

            compositore.run();

        } catch (RpaComposerException exception) {

            throw new SagaException(new Exception(exception.toString()));

        }

        // Chiudo la session ad Hibernate (se definita)
        if (compoConf.getConnection() != null) {

            // compoConf.getConnection().close();
            this.closeHibernateSessionBoundWithUserSession(compoConf.getConnection());

        }

        // TODO: Se ci sono errori di sintassi, ritorno un warning (chiedere a Stefano se fare l'implementazione)
    }

    public void cleanLogNuovoCompositore(AutCFGUserSession session) throws SagaException {

        String sqlDelete = "DELETE FROM rpa_log";

        this.executeSQL(session, sqlDelete);

    }

    public List<Object[]> getLogNuovoCompositore(AutCFGUserSession session) throws SagaException {

        String sqlQuery =
            "SELECT rpa_log.type, rpa_log.code, rpa_log.context, rpa_log.text, rpa_log.error_stack_trace " +
                "FROM rpa_log " +
                "ORDER BY data_ins DESC";

        return this.readCollectionSQL(session, sqlQuery, new Object[] {});

    }


    public RepDACDocument eseguiCompositoreByReportLink(
        AutCFGUserSession session,
        RptDACReportLink reportLink,
        RptDACReportParameters params
    ) throws SagaException {

        RepDACDocument docRep = null;

        RptExecuteParams execParams = new RptExecuteParams();

        RptExecuteReportLinkDescriptor linkDesc = new RptExecuteReportLinkDescriptor(reportLink);
        linkDesc.setReportParameters(params);
        execParams.add(linkDesc);

        String nomeFileOutPut = null;
        File fileTmp = null;
        try {
            fileTmp = Ap1Utils.createTempFile("compo_out_web",".rtf");
        } catch (IOException ioe) {
            throw CdtUtils.handleIOExceptionAndReturn(ioe, "compo_out_web.rtf");
        }

        if(fileTmp!=null && fileTmp.isFile()){
            nomeFileOutPut = fileTmp.getAbsolutePath();
            // creo il file, mi salvo il nome e poi lo cancello
            // altrimenti quando faccio new File(nomeFileOutPut)
            // lo triova già aperto e ci aggiunge un numero al nome
            //così lo ricrea il compositore con il nome giusto
            fileTmp.delete();
        }else{
            new RuntimeException("File non creato, Directory temporanea non scrivibile");
        }

        log.debug("eseguiCompositoreByReportLink - nomeFileOutPut=" + nomeFileOutPut);

        RptDACReportExecution execution = new RptDACReportExecution(session, Boolean.FALSE);
        execution.setInteraction(RptDACReportExecution.INTERACTION_NONE);
        execution.setPrinterParameters(new RptDACPrinterParameters(
            new Long(RptDACPrinterParameters.OUTPUTTYPE_RTF_FILE),
            nomeFileOutPut
        ));
        execParams.setExecution(execution);
        params.put(RpaCostanti.GLOBAL_COMPO_PARAM + RpaCostanti.PARAMETRO_NOME_FILE_TMP_OUT,nomeFileOutPut);

        RptExecuteResult res = RptReportEngine.execute(session, execParams);

        if (!res.isEverythingSucceded()) {
            log.error("Errore durante la creazione della stampa");
            throw new RuntimeException("Errore durante l'avvio della stampa: " + linkDesc.getReportLinkFullName());
        }

//        File stampa = new File(nomeFileOutPut);
//        if(stampa.exists() && stampa.length()>0){
//            docRep = new RepDACDocument(stampa.getName(),"Risultato Compositore",stampa);
//            docRep.readFromFile(stampa);
//
//            if(!log.isDebugEnabled()){
//                stampa.delete();
//            }
//        }

        Long idRepo = getIdFileReopository(session);
        if(idRepo!=null){
            try {
                docRep = RepUtils.getBLG().readDocument(session,idRepo);
            } catch (RemoteException re) {
                throw CdtUtils.handleRemoteExceptionsAndReturn(re);
            }
        }
        //pulisco il file temporaneao creato
        File stampa = new File(nomeFileOutPut);
        if(stampa.exists() && stampa.length()>0 && !log.isDebugEnabled()){
            stampa.delete();
        }

        return docRep;
    }


    private void addToPraDocuemtniFiles(AutCFGUserSession session,PraDACPraticheDocumenti pd, RepDACDocument document) throws SagaException{
        PraDACPraticheDocumentiFile pdf = new PraDACPraticheDocumentiFile();
        pdf.setPraticaDocumento(pd);
        pdf.setRepDocument(document);
        pdf.setDes(document.getDocumentDescription());
        save(session,pdf);
    }



}


