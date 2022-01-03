package it.saga.library.reportGeneratoreModelli.frame;

import it.saga.library.authentication.AutCFGClientSession;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.baseForms.BsfFRMBaseInternalFrame;
import it.saga.library.bonifiche.BonUtility;
import it.saga.library.controls.grid.CtlGridColumnDescriptor;
import it.saga.library.controls.grid.CtlWCNGrid;
import it.saga.library.controls.grid.CtlWCNGridScroller;
import it.saga.library.controls.grid.methodprovider.CtlGridMethodsProvider;
import it.saga.library.controls.grid.provider.CtlGridCellTooltipValueProvider;
import it.saga.library.localization.LocLocalizator;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaUtils;

import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaFRMLogNewCompo extends BsfFRMBaseInternalFrame {

    private Map<Character, Color>   gridRowColorMapper;
    private Map<Integer, String>    compoInstructionContextMapper;

    private static final LocLocalizator loc = new LocLocalizator("RpaFRMLogNewCompo");

    private JPanel              pnlMain             = new JPanel();
    private BorderLayout        borderLayout1       = new BorderLayout();
    private CtlWCNGridScroller  grid                = new CtlWCNGridScroller();

    public RpaFRMLogNewCompo() {

        try {

            jbInit();
            init();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {

        this.setSize(new Dimension(865, 353));
        this.setTitle(loc.get("windowTitle"));
        this.setResizable(true);

        grid.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        grid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        pnlMain.setLayout(borderLayout1);
        pnlMain.add(grid);

        this.getContentPane().add(pnlMain, BorderLayout.CENTER);

    }

    private void init() {

        initGridRowColorMapper();
        initCompoInstructionContextMapper();
        initGrid();

    }

    private void initGridRowColorMapper() {

        gridRowColorMapper = new HashMap<Character, Color>();

        gridRowColorMapper.put('d', Color.white);
        gridRowColorMapper.put('w', Color.yellow);
        gridRowColorMapper.put('e', Color.red);

    }

    private void initCompoInstructionContextMapper() {

        compoInstructionContextMapper = new HashMap<Integer, String>();

        compoInstructionContextMapper.put(0, "Precompilazione");
        compoInstructionContextMapper.put(1, "Compilazione");

    }

    private void initGrid() {

        try {

            // Inizializzo le colonne
            ArrayList columnsDescriptor = new ArrayList();
            columnsDescriptor.add(new CtlGridColumnDescriptor("Testo", "textInfo", 180, false));

            CtlGridColumnDescriptor[] columnsDescriptorArray = new CtlGridColumnDescriptor[columnsDescriptor.size()];
            columnsDescriptorArray = (CtlGridColumnDescriptor[])columnsDescriptor.toArray(columnsDescriptorArray);

            grid.setColumns(GridRowLog.class, columnsDescriptorArray);
            grid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            // Inizializzo le righe
            List<GridRowLog>    rowsList    = new ArrayList<GridRowLog>();
            AutCFGUserSession   session     = AutCFGClientSession.getUserSession();
            List<Object[]>      results     = RpaUtils.getBLG().getLogNuovoCompositore(session);

            for (Object[] result : results) {

                GridRowLog newGridRowLog = new GridRowLog();

                newGridRowLog.setType((String) result[0]);
                newGridRowLog.setCode((String) result[1]);
                newGridRowLog.setContext((Integer) BonUtility.transform(result[2], Integer.class));
                newGridRowLog.setText((String) result[3]);
                newGridRowLog.setErrorStackTrace((String) result[4]);

                rowsList.add(newGridRowLog);

            }

            grid.setRows(rowsList);

        } catch (SagaException exception) {

            exception.printStackTrace();

        } catch (RemoteException exception) {

            exception.printStackTrace();

        }

    }

    private class GridRowLog implements CtlGridMethodsProvider, CtlGridCellTooltipValueProvider {


        public static final int     NUMBER_STACK_TRACE_SEGMENT      = 2;
        public static final String  ERROR_STACK_TRACE_SEGMENT_REGEX = "([^\\[]*)\\[\\*\\]";

        private String  type;
        private String  code;
        private Integer context;
        private String  text;
        private String  errorStackTrace;

        public Color[] getColorValues() {

            Character   typeCharacter   = null;
            Color       backgroundColor = null;

            // Recupero il colore il base al tipo di messaggio
            if (this.type != null) {

                typeCharacter   = this.type.charAt(0);
                backgroundColor = gridRowColorMapper.get(typeCharacter);

            }

            // Se non ho trovato nessun colore, imposto il bianco
            if (backgroundColor == null) {

                backgroundColor = Color.white;

            }

            // Imposto i colori di sfondo e del testo
            Color[] colors = new Color[] {

                    backgroundColor,
                    Color.black

            };

            return colors;

        }

        @Override
        public String getCellTooltipValue(CtlWCNGrid ctlWCNGrid, CtlGridColumnDescriptor ctlGridColumnDescriptor) {

            String instructionContext   = "";
            String instructionCode      = "";

            // Controllo che esista il contesto di esecuzione
            if (this.context != null && compoInstructionContextMapper.containsKey(this.context)) {

                instructionContext = compoInstructionContextMapper.get(this.context);

            }

            // Controllo che esista un codice sull'errore / warning
            if (this.code != null) {

                instructionCode = " " + this.code;

            }

            return instructionContext + instructionCode;

        }

        public String getErrorStackTracePreview() {

            if (errorStackTrace == null) {

                return "";

            }

            String  stackTracePreview               = "";
            Matcher detectStackTraceSegmentMatcher  = Pattern.compile(ERROR_STACK_TRACE_SEGMENT_REGEX).matcher(errorStackTrace);
            int     segmentsFoundCount              = 0;

            while (detectStackTraceSegmentMatcher.find() && segmentsFoundCount < NUMBER_STACK_TRACE_SEGMENT) {

                String segment = detectStackTraceSegmentMatcher.group(1);

                if (stackTracePreview.isEmpty()) {

                    stackTracePreview = segment;

                } else {

                    stackTracePreview = stackTracePreview + " |#####| " + segment;

                }

                ++ segmentsFoundCount;

            }

            if (stackTracePreview.isEmpty()) {

                stackTracePreview = errorStackTrace;

            }

            return stackTracePreview;

        }

        public String getTextInfo() {

            if (errorStackTrace != null && !errorStackTrace.isEmpty()) {

                return getErrorStackTracePreview();

            } else {

                return text;

            }

        }

        public void setType(String type) {
            this.type = type;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setContext(Integer context) {
            this.context = context;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public String getCode() {
            return code;
        }

        public Integer getContext() {
            return context;
        }

        public String getText() {
            return text;
        }

        public String getErrorStackTrace() {
            return errorStackTrace;
        }

        public void setErrorStackTrace(String errorStackTrace) {
            this.errorStackTrace = errorStackTrace;
        }

    }

}
