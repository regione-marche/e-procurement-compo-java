package it.saga.library.reportGeneratoreModelli.setup.misc;

import it.saga.library.setup.SetupException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RpaSetupUtility {

  public final static String PATTERN_DATA = "yyyy-MM-dd HH:mm:ss";
  public final static String DATA_DI_DEFAULT = "1900-01-01 00:00:00";
  public final static String CARATTERE_SEPARATORE = "|";
  
  public RpaSetupUtility() {
    super();
  }
  
  public static BufferedReader mapFile(String urlFile, Class classe){
    //Creo degli inputStream che mi consentono di leggere i file di testo 
    //direttamente dal file jar
    InputStream file     =  classe.getResourceAsStream ( urlFile );
    //Mappo i file da cui prelevare i dati da inserire nelle tabelle
    return  new BufferedReader( new InputStreamReader ( file ) );  
  };

  /////////////////////////////
  // Funzioni di conversione //
  /////////////////////////////

  public static Long strToLong(String dato) {
    dato = strToString(dato);
    //Se " dato " � null ritorno null
    if (dato == null)
      return null;
    //Altrimenti il dato trasformato in Long
    else
      return new Long(dato);
  }


  public static Double strToDouble(String dato) {
    dato = strToString(dato);
    //Se " dato " � null ritorno null
    if (dato == null)
      return null;
    //Altrimenti il dato trasformato in Double
    else
      return new Double(dato);
  }

  //nel caso di stringa vuota ritorna null

  public static String strToString(String dato) {
    if (dato != null && dato.trim().equals("")) {
      return null;
    } else {
      return dato.trim();
    }
  }

  public static String strToString(String dato, int lungMax) {
    String stringa = strToString(dato);
    if (stringa != null && stringa.length() > lungMax) {
      stringa = stringa.substring(0, lungMax);
    }
    return stringa;
  }

  public static Date strToDate(String dato) throws SetupException {
    return strToDate(dato, false);
  }

  public static Date strToDate(String dato, boolean valorizzaSempre) throws SetupException {
    try {
      dato = strToString(dato);
      //Se " dato " � null ritorno null
      if (dato == null) {
        if (valorizzaSempre) {
          dato = DATA_DI_DEFAULT;
        } else {
          return null;
        }
        //Altrimenti il dato trasformato in Date
      }

      //Creo il formato della data da decodificare
      SimpleDateFormat format = new SimpleDateFormat(PATTERN_DATA);
      // Il metodo setLenient impostato a false impedisce all'oggetto format,
      // nel caso in cui riceva in ingresso una data non valida, di tentare
      // di trovare una data facoltativa
      format.setLenient(false);
      //Creo la nuova data
      Date data = format.parse(dato);
      return data;
    } catch (ParseException exc) {
      throw new SetupException(exc);
    }
  }

  public static ArrayList loadFileMap(BufferedReader fileMap) throws SetupException {
    //Riga del file che corrisponde alla riga della tabella
    String line = null;
    //Array dimamico in cui inserisco le righe lette
    ArrayList totalLines = new ArrayList();
    try {
      //Scorro tutto il file riga per riga e carico in memoria le righe del file letto
      while ((line = fileMap.readLine()) != null)
        totalLines.add(line.trim());
      //Ritorno l'arraylist appena caricato
      return totalLines;
    } catch (IOException exc) {
      throw new SetupException(exc);
    }
  }
}
