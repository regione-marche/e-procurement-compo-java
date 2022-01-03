package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupStepCFGRemoveFromSicrawebResource;
import it.saga.library.setup.SetupStepCFGUpdateSicrawebResource;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_03 extends SetupVersion {
    public RpaSetupVersion_03(SetupApp setupApp) {
      super(setupApp, new Integer(3));
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
      return new SetupStep[]{ 
                 
        // due passi per mettere a EAGER i jar nel jnlp
        new SetupStepCFGRemoveFromSicrawebResource( 
            this, 
            "Report Generatore Modelli",
            new String[] { 
                "it.saga.library.reportGeneratoreModelli.client.jar",
                "it.saga.library.reportGeneratoreModelli.menu.jar" 
            },
            SetupStepCFGRemoveFromSicrawebResource.LAZY ),

        new SetupStepCFGUpdateSicrawebResource(
            this, 
            null, // package : se null va in EAGER
            new String[] { 
                "it.saga.library.reportGeneratoreModelli.client.jar",
                "it.saga.library.reportGeneratoreModelli.menu.jar" 
            })
      };
    }
}
