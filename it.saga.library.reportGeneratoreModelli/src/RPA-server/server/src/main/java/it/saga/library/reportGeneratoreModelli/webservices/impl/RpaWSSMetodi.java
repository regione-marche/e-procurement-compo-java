package it.saga.library.reportGeneratoreModelli.webservices.impl;

import it.saga.library.authentication.AutBLGUtils;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.authentication.AutDACServerSideLogonResult;
import it.saga.library.common.xml.CmnXmlUtils;
import it.saga.library.logging.Log;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;
import it.saga.pubblici.anagrafe.webservices.AnaAbstractWebService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.rmi.RemoteException;
import java.util.List;


public class RpaWSSMetodi extends AnaAbstractWebService {

    public static final String UTENTE_WS_LOGIN      = "apewsuser";
    public static final String UTENTE_WS_PASSWORD   = "apewsuser";

    private final static Log log = Log.getLog(RpaWSSMetodi.class);

    public static String eseguiSelect(String sessione, String select) throws RuntimeException {

        String out;

        AutDACServerSideLogonResult logon = getLogon();
        try{
            AutCFGUserSession session = logon.getUserSession();

            Document xmlDoc = CmnXmlUtils.newXmlDocument();

            Element root = CmnXmlUtils.createTextElement("rpa_select",null,xmlDoc);
            xmlDoc.appendChild(root);

            Element righe= CmnXmlUtils.createTextElement("rows",null,xmlDoc);
            root.appendChild(righe);


            // id sessione al momento non � usato serve per uno sviluppo futuro in caso
            // di bufferizzare le query della stessa sesssione del compositore
            Long idSessione = new Long(sessione);

            List result = RpaUtils.getBLG().readCollectionSQL( session,select,new Object[]{});
            if(result!=null){
                for(int i=0;i<result.size();i++){

                    Element riga= CmnXmlUtils.createTextElement("row_"+(i+1),null,xmlDoc);
                    righe.appendChild(riga);
                    Object[] row = (Object[])result.get(i);

                    if(row!=null && row.length>0){
                        for(int ii=0;ii<row.length;ii++){
//                            String val = (String)BonUtility.transform(row[ii], String.class );
                            String val = null;
                            if(row[ii]!=null){
                                val = row[ii].toString();
                            }
                            riga.appendChild(CmnXmlUtils.createTextElement("col_"+(ii+1), val, xmlDoc));
                        }
                    }
                }
            }

            out =CmnXmlUtils.writeXmlToString(xmlDoc);

        } catch(RemoteException re){
            throw new RuntimeException(re);
        }catch(SagaException se){
            throw new RuntimeException(se);
        }finally{
            try{
                AutBLGUtils.serverSideLogoff(logon);
            }catch(SagaException se){
                throw new RuntimeException(se);
            }
        }

        return out;
    }


    public static AutDACServerSideLogonResult getLogon() throws RuntimeException {
       return getLogon(UTENTE_WS_LOGIN,UTENTE_WS_PASSWORD);
    }

    public static AutDACServerSideLogonResult getLogon(String login, String psw) throws RuntimeException {

        AutDACServerSideLogonResult logon = null;

        RpaWSSMetodi apeWSmetodi = new RpaWSSMetodi();
        String cid = apeWSmetodi.retrieveCidFromMessageContext();

        log.debug("CID=" + cid);

        if(cid!=null){
            // siccome nel metodo AnaAbstractWebService ss c'� scolpito user e password
            // nel caso di default, se non specificate gliele passo io...
            String[] splitted = cid.split(":");
            if(splitted.length==1){
                cid+= ":" + login + ":" + psw;
            }

            try{
                logon = apeWSmetodi.doSicrawebLogin(cid);
            }catch(SagaException se){
                throw new RuntimeException(se);
            }
        }

        if(logon==null){
            throw new RuntimeException("Manca la connessione a Jboss, controllare la configurazione");
        }

        return logon;
    }


}
