package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupStepCFGRemoveFromSicrawebResource;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_07 extends SetupVersion {
    public RpaSetupVersion_07(SetupApp setupApp){    
      super( setupApp, new Integer( 7 ));
    }

    @Override
    public SetupStep[] getSetupSteps() throws SetupException {
        return new SetupStep[] {
            
            new SetupStepCFGRemoveFromSicrawebResource( 
                this, 
                "Report Generatore Modelli",
                new String[] {"it.saga.library.reportGeneratoreModelli.menu.jar"},
                SetupStepCFGRemoveFromSicrawebResource.EAGER 
            ),       
                       
        };
    }
}
