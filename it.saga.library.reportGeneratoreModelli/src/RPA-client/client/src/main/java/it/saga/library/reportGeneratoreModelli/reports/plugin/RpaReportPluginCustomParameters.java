package it.saga.library.reportGeneratoreModelli.reports.plugin;

import it.saga.library.messages.SagaException;
import it.saga.library.reports.RptDACReport;
import it.saga.library.reports.controls.RptWCNReportParametersPanel;
import it.saga.library.reports.plugin.standard.customparameters.RptReportPluginCustomParameters;

public class RpaReportPluginCustomParameters 
    extends RptReportPluginCustomParameters 
    {
        
    
    /** interfaccia RptReportPluginRuntimeReportParametersBuilderInterface **/
    public RptWCNReportParametersPanel getReportPluginRuntimeReportParametersBuilderPanel(
        RptDACReport report ) throws SagaException{
        
        return new RpaWCNCustomParametersBuilder();
    }
    
}
