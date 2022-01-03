package it.saga.library.reportGeneratoreModelli.misc;

import java.io.Serializable;

public class      RpaCompoParamDescriptor
       implements Serializable
{
  private String codice     ;
  private String descrizione;
  private String valore     ;
  
  public RpaCompoParamDescriptor(
    String codice     ,
    String descrizione,
    String valore     )
  {
    this.codice      = codice     ;
    this.descrizione = descrizione;
    this.valore      = valore     ;
  }

  public String getCodice() { return codice; }

  public String getDescrizione() { return descrizione; }

  public String getValore() { return valore; }
}
