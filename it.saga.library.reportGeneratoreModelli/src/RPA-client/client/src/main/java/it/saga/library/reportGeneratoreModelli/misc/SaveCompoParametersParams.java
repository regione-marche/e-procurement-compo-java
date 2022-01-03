package it.saga.library.reportGeneratoreModelli.misc;

import java.io.Serializable;

public class      SaveCompoParametersParams
       implements Serializable
{
  private RpaCompoParamDescriptor[] compoParams;
  
  public SaveCompoParametersParams( RpaCompoParamDescriptor[] compoParams ) {
    this.compoParams = compoParams;
  }

  public RpaCompoParamDescriptor[] getCompoParams() { return compoParams; }
}
