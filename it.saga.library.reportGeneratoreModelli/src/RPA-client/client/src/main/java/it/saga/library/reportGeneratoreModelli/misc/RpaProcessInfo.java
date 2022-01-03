package it.saga.library.reportGeneratoreModelli.misc;

import it.saga.library.baseForms.BsfFRMBaseInternalFrame;
import it.saga.library.controls.grid.CtlGridColumnDescriptor;
import it.saga.library.controls.grid.CtlGridParentFrameBoundedRow;
import it.saga.library.controls.grid.CtlWCNGrid;
import it.saga.library.controls.grid.methodprovider.CtlGridMethodsProvider;
import it.saga.library.controls.grid.provider.CtlGridCellTooltipValueProvider;
import it.saga.library.workflow.WkfUtils;
import org.eigenbase.xom.StringEscaper;

import java.io.File;
import java.io.Serializable;
import java.util.Date;


public class RpaProcessInfo
        implements  Serializable,
                    Comparable,
                    CtlGridMethodsProvider,
                    CtlGridCellTooltipValueProvider,
                    CtlGridParentFrameBoundedRow
{

    private Long pid;
    private String nomeFile;
    private String command;
    private Date dataInizio;
    private String logonUser;
    //InfoCompositore
    private String pathFile;
    private Date lastModified;
    private long size;
    private boolean f64 = false;

    public RpaProcessInfo(Long pid, String nomeFile,String command,String logonUser){
        this.pid = pid;
        this.nomeFile = nomeFile;
        this.command = command;
        this.dataInizio = new Date();
        this.logonUser=logonUser;
    }

    public void setPid(Long pid) { this.pid = pid; }
    public Long getPid() { return pid; }

    public void setNomeFile(String nomeFile) { this.nomeFile = nomeFile; }
    public String getNomeFile() { return nomeFile; }

    public void setDataInizio(Date dataInizio) { this.dataInizio = dataInizio; }
    public Date getDataInizio() { return dataInizio; }

    public void setCommand(String command) { this.command = command; }
    public String getCommand() { return command; }

    public void setLogonUser(String logonUser) { this.logonUser = logonUser; }
    public String getLogonUser() { return logonUser;}

    public void setPathFile(String pathFile) { this.pathFile = pathFile; }
    public String getPathFile() { return pathFile; }

    public void setLastModified(Date lastModified) {this.lastModified = lastModified; }
    public Date getLastModified() { return lastModified;}

    public void setSize(long size) {this.size = size;}
    public long getSize() {return size; }

    public void setF64(boolean f64) { this.f64 = f64; }
    public boolean isF64() { return f64; }

    public void setInfoCompositore(File file){
        if(file!=null){
            pathFile = file.getAbsolutePath();
            lastModified = new Date(file.lastModified());
            size = file.length();
        }
    }

    //-------------------------------------------------------

    public Double getElapsedSecs() {
      return new Double( WkfUtils.getDeltaSecs( dataInizio, new Date() ));
    }

    public String getElapsedSecsStringValue() {
      return WkfUtils.getDeltaString( this.getElapsedSecs().doubleValue(), "elapsed", "", 0, 0, WkfUtils.DELTATIME_TYPE_DESCRIPTION, 0 );
    }

    @Override
    public int compareTo(Object o) {

        int out = 0;

        Date d1 = getDataInizio();
        Date d2 = null;
        if(o!=null){
            d2 = ((RpaProcessInfo)o).getDataInizio();
        }

        if(d1==null && d2==null){
            out = 0;
        } else if(d1==null && d2!=null){
            out = -1;
        }else if(d1!=null && d2==null){
            out = 1;
        }else{
            out = d1.compareTo(d2);
        }

        return out;
    }

    /**************************************************************************/
    /***  INTERFACCIA  CtlGridParentFrameBoundedRow                         ***/
    /***                                                                    ***/
    /***  Permette di avere la form chiamante quando il DAC � nella griglia ***/
    /**************************************************************************/
    private transient BsfFRMBaseInternalFrame parentFrame = null;

    @Override
    public void setParentFrameBoundValue( BsfFRMBaseInternalFrame parentFrame ){
      this.parentFrame = parentFrame;
    }


    /**************************************************************************/
    /***  INTERFACCIA  CtlGridCellTooltipValueProvider                      ***/
    /**************************************************************************/

    @Override
    public String getCellTooltipValue(CtlWCNGrid ctlWCNGrid, CtlGridColumnDescriptor ctlGridColumnDescriptor) {
        return  "<html><body>" +
                "<b>PID</b>= " + pid + "<br/>" +
                "<b>Avviato il</b>: " + dataInizio.toString() + " <b>da</b> " + logonUser +" <br/>" +
                "<br/>" +
                StringEscaper.htmlEscaper.escapeString(command) +
                "</body></html>";
    }



}
