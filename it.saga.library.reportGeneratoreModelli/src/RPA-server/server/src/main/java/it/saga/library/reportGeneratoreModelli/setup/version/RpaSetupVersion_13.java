package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_13 extends SetupVersion {
    public RpaSetupVersion_13(SetupApp setupApp) {
        super(setupApp, 13);
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[] {
            dropView("c0campi"),
        };
    }
}