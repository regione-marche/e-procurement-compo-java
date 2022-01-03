package it.saga.library.reportGeneratoreModelli.misc;

public class RpaCostanti {

    public static final String APP_PREFIX = "rpa";

    public static final String GLOBAL_COMPO         = "global_compo_";
    public static final String GLOBAL_COMPO_PARAM   = "global_compo_param:";
    public static final String GLOBAL_COMPO_CONTEXT = "global_compo_context";

    public static final String PARAMETRO_AGGIUNGI_IN_DOC    = "RPA_SalvaInRepo";
    public static final String PARAMETRO_DESCRIZIONE_STAMPA = "global_report_description";
    public static final String PARAMETRO_NOME_FILE          = "RPA_NomeFile";
    public static final String PARAMETRO_ID_DOCUMENTO       = "RPA_id_documento";
    public static final String PARAMETRO_APP_PREFIX         = "RPA_app_prefix";
    public static final String PARAMETRO_FILE_REPOSITORY_ID = "RPA_file_repository_id";

    //aggiunto a parte, se non valorizzato viene messo uguale a RPA_SalvaInRepo
    public static final String PARAMETRO_SALVA_IN_DOC_COLLEGATI = "RPA_AddToDocColl";

    public static final String VALORE_SI = "1";
    public static final String VALORE_NO = "2";

    public static final String FORMATO_DATA_COMPOSITORE = "dd.MM.yyyy";

    public static final String PARAMETRO_SESSIONE_ID    = "80=";
    public static final String PARAMETRO_SESSIONE_USER  = "86=";
    public static final String PARAMETRO_SESSIONE_DB    = "81=";
    public static final String PARAMETRO_SESSIONE_APP   = "87=";

    //////////////////////////////////////////////////////
    // PARAMETRI GLOBALI PER CONFIGURAZIONI PARTICOLARI //
    //////////////////////////////////////////////////////

    // in caso di sql server e si deva utilizzare i driver OBDC
    public static final String PARAMETRO_GLOBALE_OBDC_ENABLE = "odbc_enable"; //1=abilitato
    public static final String PARAMETRO_GLOBALE_OBDC_DRIVER = "odbc_driver";
    public static final String PARAMETRO_GLOBALE_OBDC_SERVER = "odbc_server";
    public static final String PARAMETRO_GLOBALE_OBDC_DBNAME = "odbc_dbname";

    // in caso di oracle 11 e driver OCI, counfigurare il driver e l'url
    public static final String PARAMETRO_GLOBALE_JDBC_DRIVER = "jdbc_driver";
    public static final String PARAMETRO_GLOBALE_JDBC_URL    = "jdbc_url";

    // gestione timeout linux
    public static final String PARAMETRO_GLOBALE_PROCESS_TIMEOUT = "process_timeout";

    // integrazione con WSCompositore
    public static final String PARAMETRO_GLOBALE_WS_URL = "ws_url";
    public static final String PARAMETRO_GLOBALE_WS_APP = "ws_app";
    public static final String PARAMETRO_WS_APP_DEFAULT = "sicraweb";

    // OS LINUNX - Abilitata modalita compatibilita' 64bit
    public static final String PARAMETRO_GLOBALE_MODALITA_F64_ENABLED = "f64_enable"; //1=abilitato
    public static final String PARAMETRO_GLOBALE_NON_AGGIORNARE_ESEGUIBILE = "skip_update_engine";
    public static final String PARAMETRO_GLOBALE_FORZA_LIBGCC = "force_libgcc";

    // Nuovo Compositore Java
    public static final String PARAMETRO_GLOBALE_COMPOSITORE_JAVA = "modalita_nuovo_compositore";

    //////////////////////////////////////////////////////
    public static final String CACHE_LATO_SERVER_RPA = "rpa_cache_server";
    public static final String KEY_MAPPING_PID_LIST  = "mapping_pid_list";

    @Deprecated
    public static final String PARAMETRO_UTENTE_FILE_REPOSITORY_ID = "file_repository_id";

}
