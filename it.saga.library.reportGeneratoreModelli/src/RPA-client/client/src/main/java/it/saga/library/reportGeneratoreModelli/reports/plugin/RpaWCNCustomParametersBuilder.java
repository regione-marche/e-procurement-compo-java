package it.saga.library.reportGeneratoreModelli.reports.plugin;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.baseForms.BsfFRMBaseInternalFrame;
import it.saga.library.commonDataTypes.CdtDACPkBaseClass;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaCostanti;
import it.saga.library.reports.RptDACPrinterParameters;
import it.saga.library.reports.RptDACReportExecution;
import it.saga.library.reports.RptDACReportParameters;
import it.saga.library.reports.misc.RptTimedEntryHashMap;
import it.saga.library.reports.plugin.standard.customparameters.RptWCNCustomParametersBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RpaWCNCustomParametersBuilder extends RptWCNCustomParametersBuilder {


    public void loadParameters(RptDACReportParameters params, RptDACReportExecution exec) 
        throws SagaException {
        
        List keyDaEliminare = new ArrayList();     
        
        // esamino tutti i parametri con nome che inizia per "?_" 
        // e le rinomino  con ?_global_compo_param:XXXXXXXXXXX  
        
        //prima scorro la lista ed individuo le chiavi da eliminare
        RptTimedEntryHashMap map = params.getParamsInternalMap();
        Iterator itr = map.keySet().iterator();
        while( itr.hasNext()) {
            String key = (String)itr.next();
            if(     key == null 
                || !key.startsWith( "?_" ) 
                ||  key.startsWith("?_" + RpaCostanti.GLOBAL_COMPO)){
                continue;
            }else{
                keyDaEliminare.add(key);
            }
        }     
        
        //elimino l'oggetto con la vecchia chiave e lo inserisco con la nuova
        for(int i=0; i<keyDaEliminare.size();i++){
            String key = (String)keyDaEliminare.get(i);
            Object obj = map.get(key);
            map.remove(key);
            String newKey = "?_" + RpaCostanti.GLOBAL_COMPO_PARAM + key.substring(2);
            map.put(newKey,obj);
        }   
        
        exec.setGlobalShowStandardParameters(RptDACReportExecution.SHOW_STANDARD_PARAMETERS_FORCE_OFF);
        
        super.loadParameters(params, exec);
    }


    public boolean buildParameters(AutCFGUserSession userSession, BsfFRMBaseInternalFrame parent,
                                   RptDACReportParameters params, RptDACReportExecution execs) throws SagaException {
                
        boolean result = super.buildParameters(userSession, parent, params, execs);   
        
        List listaKeyParametri = new ArrayList();
        RptTimedEntryHashMap map = params.getParamsInternalMap();
        Iterator itr = map.keySet().iterator();
        while( itr.hasNext()) {
            String key = (String)itr.next();            
            if( key != null && key.startsWith(RpaCostanti.GLOBAL_COMPO_PARAM)){                
                listaKeyParametri.add(key);
            }
        }     
        
        for(int i=0; i<listaKeyParametri.size();i++){
            String key = (String)listaKeyParametri.get(i);
            Object obj = map.get(key);
            map.put(key, convertiParametro(obj));
        }
        
        //aggiunge la descrizione della stampa
        map.put(
            RpaCostanti.GLOBAL_COMPO_PARAM + RpaCostanti.PARAMETRO_DESCRIZIONE_STAMPA,
            map.get(RpaCostanti.PARAMETRO_DESCRIZIONE_STAMPA)
        );
                 
        RptDACPrinterParameters printParams = execs.getPrinterParametersAt(0);
        if(printParams!=null && printParams.getPrinterServiceName()!=null){
            File f = new File(printParams.getPrinterServiceName());
            map.put(
                RpaCostanti.GLOBAL_COMPO_PARAM + RpaCostanti.PARAMETRO_NOME_FILE ,
                f.getName()
            );
        }
         
         
        if(execs.isPreview() || !execs.getFileProcess().isSave()){
            map.remove(RpaCostanti.GLOBAL_COMPO_PARAM + RpaCostanti.PARAMETRO_AGGIUNGI_IN_DOC);    
        }
        
        return result;
    }
    
    
    public Object convertiParametro(Object obj){
        
        //Timestamp � sempre un Date
        if(obj instanceof Date){
            SimpleDateFormat format = new SimpleDateFormat( RpaCostanti.FORMATO_DATA_COMPOSITORE );
            obj = format.format((Date) obj);
        } else  if(obj instanceof CdtDACPkBaseClass){
            obj = ((CdtDACPkBaseClass)obj).getPkid();
        }        
        
        return obj;
    }
    
        
}

