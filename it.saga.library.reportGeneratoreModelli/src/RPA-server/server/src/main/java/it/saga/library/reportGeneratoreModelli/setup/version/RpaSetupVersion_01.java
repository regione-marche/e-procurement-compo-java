package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupStepCFGUpdateApplicationXml;
import it.saga.library.setup.SetupStepCFGUpdateSicrawebResource;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_01 extends SetupVersion {
    
  public RpaSetupVersion_01(SetupApp setupApp) {
    super(setupApp, new Integer(1));
  }

  public SetupStep[] getSetupSteps() throws SetupException, DDLException {

    return new SetupStep[]{         
        // risorse nel JNLP
        new SetupStepCFGUpdateSicrawebResource( 
            this, 
            "it.saga.library.reportGeneratoreModelli",
            "Report Generatore Modelli", 
            new String[] { 
                "it.saga.library.reportGeneratoreModelli.client.jar",
                "it.saga.library.reportGeneratoreModelli.menu.jar" 
            }
        ),
        // aggiungo le BLG ad application.xml
        new SetupStepCFGUpdateApplicationXml( this, "it.saga.library.reportGeneratoreModelli.server.jar" )
      }; 
  }

}
