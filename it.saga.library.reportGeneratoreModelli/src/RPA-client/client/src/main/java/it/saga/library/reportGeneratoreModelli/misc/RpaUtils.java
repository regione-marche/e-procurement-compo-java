package it.saga.library.reportGeneratoreModelli.misc;

import it.saga.library.authentication.AutCFGClientSession;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.common.CmnUtils;
import it.saga.library.documentiCollegati.DocDACCollegatiMaster;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.RpaBLG;
import it.saga.library.reportGeneratoreModelli.RpaBLGHome;
import it.saga.library.repository.RepDACDocument;
import it.saga.pubblici.praticheEdilizie.ApeUtils;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RpaUtils {
  private static RpaBLGHome blgHome = null;

  public RpaUtils() {
    super();
  }

  // Ritorna un'istanza della remote interface RpaBLG, chiamabile sia dal client che dal server
  public static RpaBLG getBLG() throws SagaException {
     try {
       if( blgHome == null ) {
         Context ctx = AutCFGClientSession.isClient() ? AutCFGClientSession.getContext() : new InitialContext();
         blgHome = (RpaBLGHome)ctx.lookup( CmnUtils.getLookupName( RpaBLG.class ));
       }
       return blgHome.create();
     }
     catch( NamingException exc ) { throw new SagaException( exc ); }
     catch( RemoteException exc ) { throw new SagaException( exc ); }
     catch( CreateException exc ) { throw new SagaException( exc ); }
  }


    public static String getFileExtension(File file){
        return getFileExtension(file.getName());
    }

    public static String getFileExtension(String url){
        String extension = "";

        if(url!=null){
            int i = url.lastIndexOf('.');
            if (i > 0) {
                extension = url.substring(i+1);
            }
        }

        return extension;
    }

    public static String getFileNameFormUrl(String url){

        if(url!=null){
            String separator = "\\";
            if(CmnUtils.isLinux()){
                separator = "/";
            }
            int idxSeparator = url.lastIndexOf(separator);

            if (idxSeparator >= 0) {
                url = url.substring(idxSeparator+1);
            }
        }
        return url;
    }

    //funzione creata perchè non ho il nome del file, ma solo il contenuto
    public static boolean isRtf(RepDACDocument doc){

        byte[] dataBuffer = doc.getContent();
        String fileInStringa =  new String(dataBuffer);

        return fileInStringa.startsWith("{\\rtf");
    }

    public static int getNextOrdineDocumentoCollezione(AutCFGUserSession session,DocDACCollegatiMaster docCollegati) throws SagaException, RemoteException {
        // calcolo il valore del campo ordine
        Integer max = null;

        if(docCollegati.getPkid()!=null){
            max =(Integer)ApeUtils.getBLG().readCollection(
                session,
                "select max(dd.ordine) from DocDACCollegatiDetails dd where dd.docCollegati.pkid=?",
                new Object[] { docCollegati.getPkid()}
            ).get(0);
        }

        if (max != null){
            max = new Integer(max.intValue()+1);
        } else{
            max = new Integer(1);
        }

        return max;
    }

    public static int getNumberFromString(String line){
        int number = -1;
        Matcher m = Pattern.compile("\\d+").matcher(line);
        while (m.find()) {
            number = new Integer(m.group()).intValue();
            break;
        }
        return number;
    }

    public static String getShell() throws SagaException {
        Properties envs = CmnUtils.getEnvironment();
        return envs.getProperty("SHELL");
    }

    /**
     * Ricreata questa funzione da utilizzare solo per il compositore perchè File.createTempFile(...) con CentOS7 a 64bit crea dei nomi file esotici
     * @param prefix prefisso
     * @param suffix suffisso
     * @param directory cartella dove generare il file
     * @return file con nome = prefisso + id univoco + suffisso
     */
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        File fileOut = null;

        int wd = 10; //limite di tentativi
        while(fileOut==null && wd>0){
            String uCompleto = UUID.randomUUID().toString();
            // prendo l'ultima parte del'UUID che dovrebbe essere sufficente per identificarlo in modo univoco
            String uLast = uCompleto.substring(uCompleto.lastIndexOf("-")+1);
            File file = new File(directory,prefix + uLast + suffix);
            // verifico che non esisti già, cosa improbabile
            if(!file.isFile()){
                fileOut = file;
            }
            wd--;
        }
        if(wd==0){
            throw new IOException("Unable to create temporary file - already exist!");
        }

        return fileOut;
    }


}
