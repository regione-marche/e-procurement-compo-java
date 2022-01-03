package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.db.DbType;
import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_11 extends SetupVersion {

    public RpaSetupVersion_11(SetupApp setupApp) {
        super(setupApp, new Integer(11));
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[] {
            dropView("c0entit"),
            addColumn("rpa_c0campi", "coc_id_split", DbType.Varchar(100))
        };
    }

}
