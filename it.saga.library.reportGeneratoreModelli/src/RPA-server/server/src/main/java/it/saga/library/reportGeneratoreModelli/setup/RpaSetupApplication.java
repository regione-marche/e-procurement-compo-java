package it.saga.library.reportGeneratoreModelli.setup;

import it.saga.library.common.CmnAppDescriptor;
import it.saga.library.common.CmnStringUtils;
import it.saga.library.db.DbConstraint;
import it.saga.library.db.DbIndex;
import it.saga.library.db.DbUniqueConstraint;
import it.saga.library.db.DbView;
import it.saga.library.setup.SetStepDescriptor;
import it.saga.library.setup.SetUtils;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupLazyApp;
import it.saga.pubblici.praticheEdilizie.setup.misc.ApeSetupUtility;

public class RpaSetupApplication extends SetupLazyApp {

  private static int NUM_CURRENT_VERSION    = 14;
  private static int NUM_REMOTE_STEP        = 2;


  public RpaSetupApplication() {
    super("rpa","Report Generatore Modelli");
  }

  public DbIndex[] getIndexes() throws SetupException {
    return new DbIndex[] { };
  }

  public DbConstraint[] getDbConstraints() throws SetupException {
    return new DbConstraint[] {
        new DbUniqueConstraint ( "rpa_session", "id_uk","id")
    };
  }

  // come creare delle viste
  public DbView[] getDbViews(CmnAppDescriptor cmnAppDescriptor) throws SetupException {
    // Aggiunta la clausola order by dalla versione 13, setup 5
    // 8/11/13 Corretta in versione 14 perchè SqlServer 2008 non gestisce order by nella creazione di viste
    String queryVistaC0entit =  "select " +
                                    "pkid, " +
                                    "c0e_nom, " +
                                    "c0e_num, " +
                                    "c0e_tip, " +
                                    "c0e_arg as coe_arg, " +
                                    "c0e_des, " +
                                    "c0e_key, " +
                                    "c0e_nom_ex, " +
                                    "c0e_key_ex as coe_key_ex, " +
                                    "c0e_frm_ri, " +
                                    "c0e_frm_ca as coe_frm_ca, " +
                                    "c0e_frm_re as coe_frm_re, " +
                                    "app_prefix " +
                                "from rpa_c0entit ";

    if(SetUtils.getDataBaseType() != SetUtils.SQLSERVER){
      queryVistaC0entit += "order by c0e_num";
    }

    DbView[] views = new DbView[] {  // nome view, query della view (occhio che non è cross platform)

        new DbView("c0campi",
                   "select " +
                        "pkid, " +
                        "c0c_conta as coc_conta, " +
                        "c0c_tip, " +
                        "c0c_chi, " +
                        " CASE " +
                        " WHEN " + ApeSetupUtility.strRight("c0c_mne_uni", "14") + " = '.APE_PRESC.APE' " +
                        "  THEN replace(c0c_mne_uni, '.APE_PRESC.APE', '.APE_V_PRESC.APE') " +
                        " WHEN " + ApeSetupUtility.strRight("c0c_mne_uni", "15") + " = '.APE_EPAREC.APE' " +
                        "  THEN replace(c0c_mne_uni, '.APE_EPAREC.APE', '.APE_V_EPAREC.APE') " +
                        " WHEN " + ApeSetupUtility.strRight("c0c_mne_uni", "14") + " = '.APE_APRES.APE' " +
                        "  THEN replace(c0c_mne_uni, '.APE_APRES.APE', '.APE_V_APRES.APE') " +
                        " ELSE c0c_mne_uni " +
                        " END AS coc_mne_uni, " +
                        "c0c_mne_ber, " +
                        "c0c_des as coc_des, " +
                        "c0c_des_frm as coc_des_frm, " +
                        "c0c_fs, " +
                        "c0c_tab1, " +
                        "c0c_dom as coc_dom, " +
                        "c0c_des_web as coc_des_web, " +
                        "app_prefix " +
                   "from rpa_c0campi"),


        new DbView("c0entit",
                   queryVistaC0entit
                  ),


        new DbView("w_comparam",
                   "select * " +
                   "from rpa_comparam"),
//        new DbView("tab1","select comune_istat, tab1cod, tab1tip, tab1desc, tab1nord from ape_tab1 union " +
//      "select comune_istat, '1007' as tab1cod, tiprat as tab1tip, despra as tab1desc, numord as tab1nord " +
//      "from ape_tabpra")
                                 };
    return views;
  }


    public Integer getCurrentVersion() {
        return new Integer(NUM_CURRENT_VERSION);
    }

    public Integer getRemoteStepsLength() {
        return new Integer(NUM_REMOTE_STEP);
    }

    protected SetStepDescriptor[] getLazySetupVersions() {
        int nVer = getCurrentVersion().intValue();
        SetStepDescriptor[] ssd = new  SetStepDescriptor[nVer];
        for(int i=0;i<nVer;i++){
            ssd[i]= new SetStepDescriptor(
                "it.saga.library.reportGeneratoreModelli.setup.version.RpaSetupVersion_"  +
                CmnStringUtils.pad(Integer.toString(i+1), 2, true, '0')
            );
        }
        return ssd;
    }

}
