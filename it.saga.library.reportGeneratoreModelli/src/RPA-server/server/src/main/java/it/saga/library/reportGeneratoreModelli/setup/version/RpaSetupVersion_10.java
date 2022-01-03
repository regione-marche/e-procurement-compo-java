package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;
import it.saga.pubblici.praticheEdilizie.setup.misc.ApeSetupFakeStepDML;

public class RpaSetupVersion_10  extends SetupVersion {

    public RpaSetupVersion_10(SetupApp setupApp) {
        super(setupApp, new Integer(10));
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[] {

            new ApeSetupFakeStepDML(this), //dropView("noteowf"),
            dropView("c0entit"),
            dropView("c0campi"),

        };
    }

}
