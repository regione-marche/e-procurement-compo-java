package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_05 extends SetupVersion {
  public RpaSetupVersion_05(SetupApp setupApp){    
    super( setupApp, new Integer( 5 ));    
  }

  public SetupStep[] getSetupSteps() throws SetupException, DDLException {
    return new SetupStep[]{   
      // Deve essere ricreata con la clausola order by
      this.dropView("c0entit")
    };
  }
}
