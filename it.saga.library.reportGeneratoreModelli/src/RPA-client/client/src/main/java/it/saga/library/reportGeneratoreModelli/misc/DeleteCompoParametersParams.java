package it.saga.library.reportGeneratoreModelli.misc;

import java.io.Serializable;

public class      DeleteCompoParametersParams
       implements Serializable
{
  private Long idParamsSession;
  
  public DeleteCompoParametersParams( Long idParamsSession ) {
    this.idParamsSession = idParamsSession;
  }

  public Long getIdParamsSession() { return idParamsSession; }
}
