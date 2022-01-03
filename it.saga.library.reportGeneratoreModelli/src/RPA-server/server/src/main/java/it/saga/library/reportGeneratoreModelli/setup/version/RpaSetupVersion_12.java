package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.db.DbType;
import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_12 extends SetupVersion {
    public RpaSetupVersion_12(SetupApp setupApp) {
        super(setupApp, new Integer(12));
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[] {
            dropColumn("rpa_c0campi", "coc_id_split"),
            addColumn("rpa_c0campi", "c0c_id_split", DbType.Varchar(100))
        };
    }
}
