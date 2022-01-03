package it.saga.library.reportGeneratoreModelli.executables;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.common.CmnDateUtil;
import it.saga.library.common.CmnFileUtils;
import it.saga.library.common.CmnUtils;
import it.saga.library.commonPratiche.Ap1Params;
import it.saga.library.logging.Log;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaCostanti;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;


public class RpaResource {

  private final static Log log=Log.getLog(RpaResource.class);

  //////////////////////////////////////////////////////////////////////////////
  public static class PathSelector {

    public static PathSelector ROOT      = new PathSelector( 0 );
    public static PathSelector CLASSPATH = new PathSelector( 1 );
    public static PathSelector LIB       = new PathSelector( 2 );

    private int value;
    private PathSelector( int value ) {
      this.value = value;
    }

    public boolean isRoot()      { return this.value == 0; }
    public boolean isClassPath() { return this.value == 1; }
    public boolean isLib()       { return this.value == 2; }

    public boolean equals( Object other ) {
      if( other == null ) return false;
      if( other == this ) return true ;
      if( !( other instanceof PathSelector )) return false;
      return ((PathSelector)other).value == this.value;
    }
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  // CAMBIARE QUESTA DATA QUANDO SI AGGIORNNO I FILE PER GARANTIRE UN CORRETTO
  // CONTROLLO DEI FILE DA AGGIORNARE SUL SERVER!
  private static final long referenceDateMs = CmnDateUtil.newDate( 2019, 9, 13, 9, 0, 00 ).getTime();
  // CAMBIARE QUESTA DATA QUANDO SI AGGIORNNO I FILE PER GARANTIRE UN CORRETTO
  // CONTROLLO DEI FILE DA AGGIORNARE SUL SERVER!
  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////

  public static final RpaResource[] CLASSPATH = new RpaResource[] {
    new RpaResource( "classpath", "jsse_jar"        , "jsse.jar"         ),
    new RpaResource( "classpath", "jtds-1_2_2_jar"  , "jtds-1.2.2.jar"   ),
    new RpaResource( "classpath", "libgcj-3_4_5_jar", "libgcj-3.4.5.jar" ),
    new RpaResource( "classpath", "ojdbc14_jar"     , "ojdbc14.jar"      ),
    new RpaResource( "classpath", "postgresql_jar"  , "postgresql.jar"   ),
  };

  public static final RpaResource[] WINDOWS_32 = new RpaResource[] {
    new RpaResource( "windows_32", "compo_ini"     , "compo.ini"      ),
    new RpaResource( "windows_32", "compo_ver"     , "compo.ver"      ),
    new RpaResource( "windows_32", "compo_jdbc_exe", "compo_jdbc.exe" ),
    new RpaResource( "windows_32", "compo_odbc_exe", "compo_odbc.exe" ),
    new RpaResource( "windows_32", "libiconv-2_dll", "libiconv-2.dll" ),
    new RpaResource( "windows_32", "libxml2_dll"   , "libxml2.dll"    ),
    new RpaResource( "windows_32", "zlib1_dll"     , "zlib1.dll"      ),
    new RpaResource( "windows_32", "separ"         , "separ"          ),
    new RpaResource( "windows_32", "template.odt"  , "template.odt"   ),
  };

  public static final RpaResource[] WINDOWS_32_DEBUG = new RpaResource[] {
      new RpaResource( "windows_32", "compo_ini_debug","compo.ini"      ),
      new RpaResource( "windows_32", "compo_ver"     , "compo.ver"      ),
      new RpaResource( "windows_32", "compo_jdbc_exe", "compo_jdbc.exe" ),
      new RpaResource( "windows_32", "compo_odbc_exe", "compo_odbc.exe" ),
      new RpaResource( "windows_32", "libiconv-2_dll", "libiconv-2.dll" ),
      new RpaResource( "windows_32", "libxml2_dll"   , "libxml2.dll"    ),
      new RpaResource( "windows_32", "zlib1_dll"     , "zlib1.dll"      ),
      new RpaResource( "windows_32", "separ"         , "separ"          ),
      new RpaResource( "windows_32", "template.odt"  , "template.odt"   ),
  };

//  public static final RpaResource[] LINUX_32_CLASSPATH = new RpaResource[] {
//    new RpaResource( "linux_32/classpath", "jsse_jar"      , "jsse.jar"       ),
//    new RpaResource( "linux_32/classpath", "jtds-1_2_2_jar", "jtds-1.2.2.jar" ),
//    new RpaResource( "linux_32/classpath", "ojdbc14_jar"   , "ojdbc14.jar"    ),
//    new RpaResource( "linux_32/classpath", "postgresql_jar", "postgresql.jar" ),
//  };

  public static final RpaResource[] LINUX_32 = new RpaResource[] {
    new RpaResource( "linux_32", "compo_ini" , "compo.ini"  ),
    new RpaResource( "linux_32", "compo_ver" , "compo.ver"  ),
    new RpaResource( "linux_32", "compo_jdbc", "compo_jdbc" ),
    new RpaResource( "linux_32", "separ"     , "separ"      ),
    new RpaResource( "linux_32", "template.odt","template.odt"),
  };

  public static final RpaResource[] LINUX_64 = new RpaResource[] {
    new RpaResource( "linux_32", "compo_ini" ,      "compo.ini"  ),
    new RpaResource( "linux_32", "compo_ver" ,      "compo.ver"  ),
    new RpaResource( "linux_32", "compo_jdbc_f64",  "compo_jdbc" ),
    new RpaResource( "linux_32", "separ" ,          "separ"      ),
    new RpaResource( "linux_32", "template.odt","template.odt"),
  };

    public static final RpaResource[] LINUX_32_DEBUG = new RpaResource[] {
      new RpaResource( "linux_32", "compo_ini_debug", "compo.ini"  ),
      new RpaResource( "linux_32", "compo_ver",       "compo.ver"  ),
      new RpaResource( "linux_32", "compo_jdbc",      "compo_jdbc" ),
      new RpaResource( "linux_32", "separ"     ,      "separ"      ),
      new RpaResource( "linux_32", "template.odt","template.odt"),
    };

    public static final RpaResource[] LINUX_64_DEBUG = new RpaResource[] {
      new RpaResource( "linux_32", "compo_ini_debug", "compo.ini"   ),
      new RpaResource( "linux_32", "compo_ver",       "compo.ver"   ),
      new RpaResource( "linux_32", "compo_jdbc_f64",  "compo_jdbc"  ),
      new RpaResource( "linux_32", "separ"     ,      "separ"       ),
      new RpaResource( "linux_32", "template.odt","template.odt"),
    };


    public static final RpaResource[] LINUX_32_LIB_NO_GCC = new RpaResource[] {
        new RpaResource( "linux_32/lib", "libgcj_so_8",     "libgcj.so.8"   ),
        new RpaResource( "linux_32/lib", "libxml2_so_2",    "libxml2.so.2"  ),
        new RpaResource( "linux_32/lib", "libz_so",         "libz.so.1"     ),
        new RpaResource( "linux_32/lib", "libstdc++_so_6",  "libstdc++.so.6"),
        // la risorsa libz_so non ha il numero di versione, ma il file generato si.
    };

    public static final RpaResource[] LINUX_32_LIB_CON_GCC = new RpaResource[] {
        new RpaResource( "linux_32/lib", "libgcj_so_8",     "libgcj.so.8"   ),
        new RpaResource( "linux_32/lib", "libxml2_so_2",    "libxml2.so.2"  ),
        new RpaResource( "linux_32/lib", "libz_so",         "libz.so.1"     ),
        new RpaResource( "linux_32/lib", "libgcc_s_so_1",   "libgcc_s.so.1" ),
        new RpaResource( "linux_32/lib", "libstdc++_so_6",  "libstdc++.so.6"),
        // la risorsa libz_so non ha il numero di versione, ma il file generato si.
    };




  private String subPackage       ;
  private String resourceName     ;
  private long   resourceSize = -1;
  private String deployFileName   ;

  private RpaResource(
    String subPackage    ,
    String resourceName  , // non posso mettere i nomi originali dei file, JBoss li riconosce e cerca di usarli
    String deployFileName )
  {
    this.subPackage     = subPackage    ;
    this.resourceName   = resourceName  ;
    this.deployFileName = deployFileName;
  }

  public String getSubPackage() { return subPackage; }

  public String getDeployFileName() { return deployFileName; }

  public boolean matches( File f ) throws SagaException {
    try {

      // controllo se matchano i nomi
      if( !this.deployFileName.equals( f.getName())) {
        return false;
      }

      // calcolo la dimensione della risorsa se manca
      if( this.resourceSize < 0 ) {
        InputStream ins = RpaResource.class.getResourceAsStream( subPackage + "/" + resourceName );
        int available     = 0;
        this.resourceSize = 0;
        while( ( available = ins.available()) > 0 ) {
          byte[] buffer = new byte[ available ];
          this.resourceSize += ins.read( buffer );
        }
        ins.close();
      }

      // controllo se matcha la dimensione
      if( this.resourceSize != f.length()) {
        return false;
      }

      // controllo se matcha la data di deploy
      if( referenceDateMs != f.lastModified()) {
        return false;
      }

      return true;

    } catch( IOException exc ) { throw new SagaException( exc ); }
  }

  public void deployTo( File path, boolean isDebug, boolean skip ) throws SagaException {
    try {

      File deployFile = new File( path, deployFileName );

      String descFile = deployFile.getAbsolutePath()
                      + " Data: " + CmnDateUtil.dateToString(new Date(referenceDateMs))
                      + " Size:" + deployFile.length() + "B";

      // nel caso che siamo in debug e il file esiste non li sovrascrive...
      // tranne per il compo.ini che in caso debug fa sostituito
      if(  !( (isDebug || skip) && deployFile.exists() && !deployFile.getName().equals("compo.ini"))){

          InputStream  ins  = RpaResource.class.getResourceAsStream( subPackage + "/" + resourceName );
          OutputStream outs = new FileOutputStream( deployFile );

          CmnFileUtils.pipe( ins, outs, false );
          outs.close();
          ins.close();

          deployFile.setLastModified( referenceDateMs );

          log.info("Aggiornato il file: " + descFile);
      }else{
          log.info("NON Aggiornato il file: " + descFile);
      }

    }
    catch( FileNotFoundException exc ) { throw new SagaException( exc ); }
    catch( IOException           exc ) { throw new SagaException( exc ); }
  }

  public static RpaResource[] getResources(
    AutCFGUserSession session,
    PathSelector selector,
    boolean isDebug,
    boolean isf64
  ) throws SagaException {

    if( CmnUtils.isWindows()) {

        if( selector.isRoot() ){
            if(isDebug){
                return WINDOWS_32_DEBUG;
            }else{
                return WINDOWS_32;
            }
        }else if( selector.isClassPath()) {
            return CLASSPATH;
        }else if( selector.isLib()){
            // in caso windows deve ritornare un elenco vuoto...
            // questa cartella esiste solo in linux
            return new RpaResource[]{};
        }

    } else if( CmnUtils.isLinux()) {
        if(      selector.isRoot()     ){
            if(isDebug){
                if(isf64){
                    return LINUX_64_DEBUG;
                }else{
                    return LINUX_32_DEBUG;
                }
            }else{
                if(isf64){
                    return LINUX_64;
                }else{
                    return LINUX_32;
                }
            }
        } else if( selector.isClassPath()){
            return CLASSPATH;
        } else if( selector.isLib()){
            if(hasSystemLibGcc(session)){
                return LINUX_32_LIB_NO_GCC;
            }else{
                return LINUX_32_LIB_CON_GCC;
            }
        }
    } else {
      throw new SagaException( new Exception( "Sistema operativo non supportato" ));
    }

    throw new SagaException( new Exception( "Selettore path non previsto" ));
  }


  private static boolean hasSystemLibGcc(AutCFGUserSession session) throws SagaException {

      boolean out = false;

      String val = Ap1Params.getParametroString(session, RpaCostanti.APP_PREFIX,RpaCostanti.PARAMETRO_GLOBALE_FORZA_LIBGCC);
      if(RpaCostanti.VALORE_SI.equals(val)){
        log.info("Forza l'installazione della libreria libgcc");
        return false;
      }

      try{

          Process processPermessi = null;
          BufferedReader brcoPermessi = null;
          BufferedReader brcoPermessiErr = null;

          try{
              // http://serverfault.com/questions/54736/how-to-check-if-a-library-is-installed
              processPermessi = Runtime.getRuntime().exec(new String[]{RpaUtils.getShell(),"-c","ldconfig -p | grep libgcc" },null);
              brcoPermessi = new BufferedReader( new InputStreamReader( processPermessi.getInputStream() ) );
              String line;
              while( (line = brcoPermessi.readLine()) != null ) {
                  log.debug("RPA-DEBUG out:" + line);
                  out = out || (line!=null && line.length()>0);
              }

              brcoPermessiErr = new BufferedReader( new InputStreamReader( processPermessi.getErrorStream() ) );
              String lineErr;
              while( (lineErr = brcoPermessiErr.readLine()) != null ) {
                  log.debug("RPA-DEBUG err:" + line);
              }

          }finally {
              if (brcoPermessi != null) {
                  brcoPermessi.close();
              }
              if (brcoPermessiErr != null) {
                  brcoPermessiErr.close();
              }
              processPermessi.destroy();
          }

      } catch (IOException ioe) {
          throw new SagaException( ioe );
      }

      return out;
  }


}
