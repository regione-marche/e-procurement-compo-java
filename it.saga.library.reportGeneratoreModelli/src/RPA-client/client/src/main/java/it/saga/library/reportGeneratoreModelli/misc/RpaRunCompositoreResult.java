package it.saga.library.reportGeneratoreModelli.misc;

import it.saga.library.repository.RepDACDocument;

import java.io.Serializable;

public class      RpaRunCompositoreResult
       implements Serializable
{
  private RepDACDocument resultDocument;
  
  public RpaRunCompositoreResult( RepDACDocument resultDocument ) {
    this.resultDocument = resultDocument;
  }

  public RepDACDocument getResultDocument() { return resultDocument; }
}
