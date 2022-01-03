package it.saga.library.reportGeneratoreModelli;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.check.version.ChkDACAppVersionInfoLight;
import it.saga.library.check.version.ChkVersionInfoInterface;
import it.saga.library.common.CmnAppDescriptor;
import it.saga.library.common.webservices.CmnWebServiceDescriptor;
import it.saga.library.common.webservices.CmnWebServiceProvider;
import it.saga.library.commonDataTypes.config.CdtBLGPersistenceConfigurationInterface;
import it.saga.library.commonDataTypes.exceptions.CdtEXCPersistenceException;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACC0campi;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACC0entit;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACComparam;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACLog;
import it.saga.library.reportGeneratoreModelli.dac.RpaDACSession;
import it.saga.library.reportGeneratoreModelli.misc.RpaCostanti;
import it.saga.library.reportGeneratoreModelli.reports.plugin.RpaReportPluginCustomParameters;
import it.saga.library.reportGeneratoreModelli.setup.RpaSetupApplication;
import it.saga.library.reports.RptDACReport;
import it.saga.library.reports.RptReportsPluginConfigurationInterface;
import it.saga.library.reports.plugin.RptDACReportPluginDescriptor;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupApplicationInterface;

public class RpaCFGAppDescriptor
        extends CmnAppDescriptor
        implements  CdtBLGPersistenceConfigurationInterface,
//                    MnuMenuShowable,
                    SetupApplicationInterface,
                    ChkVersionInfoInterface,
                    RptReportsPluginConfigurationInterface,
                    CmnWebServiceProvider
{

  public RpaCFGAppDescriptor() {
    super("rpa", "Report Generatore Modelli");
  }

  public Class[] getPersistentClasses() throws CdtEXCPersistenceException {
    return new Class[]{
        RpaDACC0campi.class,
        RpaDACC0entit.class,
        RpaDACComparam.class,
        //New compo:
        RpaDACLog.class,
        RpaDACSession.class
    };
  }

//  public MnuAppInfo getAppMenuInfo() {
//    return new MnuAppInfo("nomeApplicazione", "rpa", "it.saga.library.reportGeneratoreModelli.menu.jar",
//                          "it.saga.library.reportGeneratoreModelli.RpaFRMMenu", MnuMacroAreaInfo.UFFICIO_TECNICO);
//  }

  public SetupApp getSetupApplicationClass() {
    return new RpaSetupApplication();
  }

  public ChkDACAppVersionInfoLight getChkVersionInfo() {
    return new ChkDACAppVersionInfoLight(
        new String[]{
            "it.saga.library.reportGeneratoreModelli.client.jar",
//            "it.saga.library.reportGeneratoreModelli.menu.jar",
            "it.saga.library.reportGeneratoreModelli.server.jar",
//            "it.saga.library.reportGeneratoreModelli.setup.jar"
        },
        false
    );
  }


    public RptDACReportPluginDescriptor[] getRptReportPlugins(AutCFGUserSession session) {
            return new RptDACReportPluginDescriptor[]{
                new RptDACReportPluginDescriptor(
                    RpaCostanti.APP_PREFIX,
                    RpaReportPluginCustomParameters.class,
                    "Richiesta Parametri Personalizzati - Compositore"
                ).addCompatibility( RptDACReport.ReportRenderer.COMPOSITORE )
            };
    }

    public CmnWebServiceDescriptor[] getWebServices() {
        return new CmnWebServiceDescriptor[]{
            new CmnWebServiceDescriptor(
                "RpaWSSRpaSoap",
                "Web Service - RPA",
                "it.saga.library.reportGeneratoreModelli.webservices.impl.RpaWSSRpa.RpaWSSRpaLocator"
            )
        };
    }
}
