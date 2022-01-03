package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.db.DbColumn;
import it.saga.library.db.DbType;
import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_09 extends SetupVersion {

    public RpaSetupVersion_09(SetupApp setupApp) {
        super(setupApp, new Integer(9));
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[] {

                alterColumn("rpa_log", new DbColumn("code", DbType.Varchar(100)))

        };
    }

}
