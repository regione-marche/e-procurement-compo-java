package it.saga.library.reportGeneratoreModelli.misc;

import java.io.Serializable;

public class      SaveCompoParametersResult
       implements Serializable
{
  private Long idParamsSession;
  
  public SaveCompoParametersResult( Long idParamsSession ) {
    this.idParamsSession = idParamsSession;
  }

  public Long getIdParamsSession() { return idParamsSession; }
}
