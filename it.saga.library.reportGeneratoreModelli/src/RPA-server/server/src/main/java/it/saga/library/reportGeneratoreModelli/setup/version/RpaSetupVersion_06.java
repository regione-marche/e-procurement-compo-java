package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupStepCFGAddPathServerSide;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_06 extends SetupVersion {
    public RpaSetupVersion_06(SetupApp setupApp){    
      super( setupApp, new Integer( 6 ));
    }

    @Override
    public SetupStep[] getSetupSteps() throws SetupException {
        return new SetupStep[] {new SetupStepCFGAddPathServerSide(this,"lib/rpa_libs",SetupStepCFGAddPathServerSide.FOLDER_SERVER)};
    }
}
