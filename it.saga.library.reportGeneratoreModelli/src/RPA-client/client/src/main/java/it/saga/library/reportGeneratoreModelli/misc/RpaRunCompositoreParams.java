package it.saga.library.reportGeneratoreModelli.misc;

import it.saga.library.repository.RepDACDocument;

import java.io.Serializable;

public class      RpaRunCompositoreParams
       implements Serializable
{
  private RepDACDocument            templateDocument     ;
  private String                    context              ;
  private RpaCompoParamDescriptor[] compoParams          ;
  private RepDACDocument            resultDocumentCarrier;

  public RpaRunCompositoreParams( 
    RepDACDocument            templateDocument,
    String                    context         ,
    RpaCompoParamDescriptor[] parameters      ) 
  {
    this( templateDocument, context, parameters, null );
  }
  
  public RpaRunCompositoreParams( 
    RepDACDocument            templateDocument     ,
    String                    context              ,
    RpaCompoParamDescriptor[] parameters           ,
    RepDACDocument            resultDocumentCarrier ) 
  {
    this.templateDocument      = templateDocument     ;
    this.context               = context              ;
    this.compoParams           = parameters           ;
    this.resultDocumentCarrier = resultDocumentCarrier;
  }

  public RepDACDocument getTemplateDocument() { return templateDocument; }

  public String getContext() { return context; }

  public RpaCompoParamDescriptor[] getCompoParams() { return compoParams; }
  
  public RepDACDocument getResultDocumentCarrier() { return resultDocumentCarrier; }
  
  public String getValore(String key){     
            
      String val = null;      
      
      if(key!=null && compoParams!=null){
          boolean found = false;
          for(int ii=0;ii<compoParams.length && !found;ii++){
              RpaCompoParamDescriptor param = compoParams[ii];
              if(key.equals(param.getCodice())){ 
                  val = param.getValore();
                  found = true;
              }
          }
      }
      
      return val;
  }
  
}
