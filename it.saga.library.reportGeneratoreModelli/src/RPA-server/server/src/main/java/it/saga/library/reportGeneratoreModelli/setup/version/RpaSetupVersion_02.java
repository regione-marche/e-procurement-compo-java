package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.db.DbConstraint;
import it.saga.library.db.DbTable;
import it.saga.library.db.DbType;
import it.saga.library.ddl.DDLException;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupStepRemote;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_02 extends SetupVersion {
  public RpaSetupVersion_02(SetupApp setupApp) {
    super(setupApp, new Integer(2));
  }

  public SetupStep[] getSetupSteps() throws SetupException, DDLException {
    return new SetupStep[]{ 
            createTable(getTable_Rpa_C0campi()),
            createTable(getTable_Rpa_C0entit()),
            createTable(getTable_Rpa_Comparam()),
            
            // creato fittizio peer sostituire il passo che cera prima 
            // che caricava c0campi c0entit e che ora non c'è più.
            // Altrimenti si incasina il conteggio del setup sulle 
            // installazioni esistenti
            new SetupStepRemote(this){
                public void executeRemoteStep(AutCFGUserSession autCFGUserSession) {}
            }            
    };
  }
  
  
  public DbTable getTable_Rpa_C0campi() throws DDLException{
    DbTable table = new DbTable("rpa_c0campi");
    table.addPrimaryKeyColumnPKID();
    table.addColumnsForUserLogging();
    
    
    table.addColumn( "c0c_conta"      , DbType.Integer(),   DbConstraint.NOT_NULL );
    table.addColumn( "c0c_tip"        , DbType.Varchar(1)                         );
    table.addColumn( "c0c_chi"        , DbType.Varchar(1)                         );
    table.addColumn( "c0c_mne_ber"    , DbType.Varchar(30)                        );
    table.addColumn( "c0c_mne_uni"    , DbType.Varchar(65)                        );      
    table.addColumn( "c0c_des"        , DbType.Varchar(60)                        );
    table.addColumn( "c0c_des_frm"    , DbType.Varchar(20)                        );      
    table.addColumn( "c0c_fs"         , DbType.Varchar(10)                        );
    table.addColumn( "c0c_tab1"       , DbType.Varchar(5)                         );
    table.addColumn( "c0c_dom"        , DbType.Varchar(20)                        );
    table.addColumn( "c0c_des_web"    , DbType.Varchar(100)                       );
    table.addColumn( "app_prefix"     , DbType.Varchar(3)                         );
    
    return table;
  } 
  
  public DbTable getTable_Rpa_C0entit() throws DDLException {
    DbTable table=new DbTable("rpa_c0entit");
    
    table.addPrimaryKeyColumnPKID();
    table.addColumnsForUserLogging();

    table.addColumn("c0e_nom", DbType.Varchar(35), DbConstraint.NOT_NULL);
    table.addColumn("c0e_num", DbType.Integer());
    table.addColumn("c0e_tip", DbType.Varchar(1));
    table.addColumn("c0e_arg", DbType.Varchar(10));
    table.addColumn("c0e_des", DbType.Varchar(60));
    table.addColumn("c0e_key", DbType.Varchar(190));
    table.addColumn("c0e_nom_ex", DbType.Varchar(35));
    table.addColumn("c0e_key_ex", DbType.Varchar(190));
    table.addColumn("c0e_frm_ri", DbType.Varchar(10));
    table.addColumn("c0e_frm_ca", DbType.Varchar(10));
    table.addColumn("c0e_frm_re", DbType.Varchar(10));
    table.addColumn("app_prefix", DbType.Varchar(3));

    return table;
  }
  
  public DbTable getTable_Rpa_Comparam() throws DDLException {
    DbTable table=new DbTable("rpa_comparam");
    
    table.addPrimaryKeyColumnPKID();
    table.addColumnsForUserLogging();

    table.addColumn("id_sessione", DbType.Integer(), DbConstraint.NOT_NULL);    
    table.addColumn("codice", DbType.Varchar(30));
    table.addColumn("descr", DbType.Varchar(200));
    table.addColumn("valore", DbType.Varchar(512));

    return table;
  }
}
