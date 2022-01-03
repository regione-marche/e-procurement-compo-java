package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.db.DbColumn;
import it.saga.library.db.DbConstraint;
import it.saga.library.db.DbType;
import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupVersion;

public class   RpaSetupVersion_04
       extends SetupVersion 
{
  public RpaSetupVersion_04( SetupApp setupApp ) {
    super( setupApp, new Integer( 4 ));
  }

  public SetupStep[] getSetupSteps() throws SetupException, DDLException {
    return new SetupStep[]{ 
      
      // questo mi permette di immagazzinare direttamente l'id sessione
      // dell'utente
      this.alterColumn( "rpa_comparam", new DbColumn( "id_sessione", DbType.Decimal(), DbConstraint.NOT_NULL )),
    };
  }
}
