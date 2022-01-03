package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.db.DbConstraint;
import it.saga.library.db.DbTable;
import it.saga.library.db.DbType;
import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_08 extends SetupVersion {

    public RpaSetupVersion_08(SetupApp setupApp) {
        super(setupApp, new Integer(8));
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[]{
            createTable(getTable_rpa_session()),
            createTable(getTable_rpa_log()),
        };
    }

    public DbTable getTable_rpa_session() throws DDLException{
        DbTable table = new DbTable("rpa_session");
        table.addPrimaryKeyColumnPKID();
        table.addColumnsForUserLogging();
        table.addColumn( "id"   , DbType.Integer(),     DbConstraint.NOT_NULL   );
        table.addColumn( "model", DbType.Varchar(300)                           );//CONSTRAINT id_unq UNIQUE (id)
        return table;
    }

    public DbTable getTable_rpa_log() throws DDLException{
        DbTable table = new DbTable("rpa_log");
        table.addPrimaryKeyColumnPKID();
        table.addColumnsForUserLogging();
        table.addColumn( "type"             , DbType.Varchar(1)                             );
        table.addColumn( "code"             , DbType.Varchar(8)                             );
        table.addColumn( "context"          , DbType.Integer()                              );
        table.addColumn( "text"             , DbType.Varchar(300)                           );
        table.addColumn( "id_session"       , DbType.Integer(),     DbConstraint.NOT_NULL   );
        table.addColumn( "error_stack_trace", DbType.Varchar(2000)                          );
        return table;
    }

}
