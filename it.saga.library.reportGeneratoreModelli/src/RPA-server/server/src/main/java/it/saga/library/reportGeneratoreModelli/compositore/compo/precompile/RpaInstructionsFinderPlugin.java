package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.Font;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import com.aspose.words.RunCollection;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidInstructionDeclarationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaInstructionsFinderPlugin extends RpaPrecompiler {

    private static final String COMPO_START_TAG = "<?compo ";
    private static final String COMPO_END_TAG   = " ?>";

    private static final String TRANSPARENT_START_TAG   = "<@transparent ";
    private static final String TRANSPARENT_END_TAG     = " @>";

    // Espressione regolare per i trasparenti
    private static final String NO_PRINT_LINE_REGEX         = "^[ |\\t\\f]*(\\@.*)$";

    // Espressioni regolari per istruzioni a intera linea
    private static final String COMMENT_LINE_REGEX          = "(^[\\t\\f]*(\\!.+)$)";
    // Link: https://regex101.com/r/kyOreM/3
    // private static final String SQUARED_INSTRUCTION_REGEX   = "(^[ |\\t]*(\\[(?!INLINE).+\\].*)$)|(^[ |\\t]*<@transparent [ |\\t]*(\\[[^\\[\\]]+\\].*)[ |\\t]* @>[ |\\t]*$)";
    // Link: https://regex101.com/r/KU9ATB/6
    private static final String OLD_IF_REGEX                = "(^[ |\\t\\f]*(\\%[^\\%\\n\\r]+\\%[^\\%\\n\\r]+\\%).*$)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\%[^\\%\\n\\r]+\\%[^\\%\\n\\r]+\\%)[ |\\t]* @>[ |\\t\\f]*$)";
    // Link: https://regex101.com/r/AcRmwE/1
    private static final String OLD_IF_VARIANT_REGEX        = "(^[ |\\t\\f]*(\\%[^\\%]+\\%[^\\%]+\\%#) *$)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\%[^\\%]+\\%[^\\%]+\\%#)[ |\\t]* @>[ |\\t\\f]*$)";
    // Link: https://regex101.com/r/WSY2L4/7
    private static final String OLD_IF_CLOSE_OR_ELSE_REGEX  = "(^[ |\\t]*(\\% *[FE] *[0-9]+).*$)|(^[ |\\t]*<@transparent [ |\\t]*(\\% *[FE] *[0-9]+).* @>[ \\t\\f]*$)";
    // Link: https://regex101.com/r/FZg1jf/1
    private static final String OLD_IF_ELSEIF_REGEX         = "(^[ |\\t\\f]*(\\%\\%[^\\%]+\\%[^\\%]+\\%) *$)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\%\\%[^\\%]+\\%[^\\%]+\\%)[ |\\t]* @>[ |\\t\\f]*$)";
    // Link: https://regex101.com/r/zdKsuo/2/
    private static final String RECORD_DATA_REGEX           = "(^[\\t\\f]*(\\$&#[^#]+#&#[^#]+#)[ |\\t\\f]*$)";

    /*
    private static final String SQUARED_INSTRUCTION_REGEX   = "^[ |\\t]*@[ |\\t]*\\[[^\\[\\]]+\\].*$";
    private static final String OLD_IF_REGEX                = "^[ |\\t]*@[ |\\t]*\\%[^\\%]+\\%[^\\%]+\\% *$";
    private static final String OLD_IF_VARIANT_REGEX        = "^[ |\\t]*@[ |\\t]*\\%[^\\%]+\\%[^\\%]+\\%# *$";
    private static final String OLD_IF_CLOSE_OR_ELSE_REGEX  = "^[ |\\t]*@[ |\\t]*\\%[^\\%]+$";
    private static final String OLD_IF_ELSEIF_REGEX         = "^[ |\\t]*@[ |\\t]*\\%\\%[^\\%]+\\%[^\\%]+\\% *$";
    */

    // NEW INLINE LOOP START    = https://regex101.com/r/ufqh3O/1
    // OLD INLINE LOOP END      = https://regex101.com/r/MClMut/1

    // Espressioni regolari per le macro istruzioni (o istruzioni composte)
    // private static final String OLD_LOOP_DECLARATION_REGEX       = "\\$\\$ *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+)?(( *\\|\\#\\_NOENT\\_\\#)|(( *\\|\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#[\\+\\-])+))?(( *\\$\\$\\$)|( )|$)";
    private static final String OLD_LOOP_DECLARATION_REGEX          = "\\$\\$ *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))?(( *\\$\\$\\$)|( )|$)";
    private static final String OLD_LOOP_END_REGEX                  = "\\$\\$ *[A-Z|a-z|0-9|_]+(( *\\$\\$\\$)|( +)|($))";
    private static final String NEW_INLINE_LOOP_DECLARATION_REGEX   = "\\[INLINE\\] *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))? *\\[NEXTR\\]";
    private static final String NEW_INLINE_LOOP_END_REGEX           = "\\[NEXT\\] *[A-Z|a-z|0-9|_]+ *\\[NEXTR\\]";
    private static final String MATH_OPERATION_REGEX                = "\\\\[^\\\\]+\\\\";
    private static final String MATH_OPERATION_ASSIGN_REGEX         = "\\\\[^\\\\]+\\\\\\\\[^\\\\]+\\\\";
    private static final String IF_MATH_OPERATION_REGEX             = "\\\\%[^%]+%[^\\\\]+\\\\";
    private static final String IF_MATH_OPERATION_ASSIGN_REGEX      = "\\\\%[^%]+%[^\\\\]+\\\\\\\\[^\\\\]+\\\\";

    // Espressioni regolari per macro istruzioni con "[...]"
    // Link: https://regex101.com/r/18AwU9/3
    private static final String NEW_IF_REGEX                = "(^[ |\\t\\f]*)(\\[IF\\] *%[^%]+%)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[IF\\] *%[^%]+%).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/5OydMQ/1
    private static final String NEW_ENDIF_REGEX             = "(^[ |\\t\\f]*)(\\[ENDIF\\])|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[ENDIF\\]).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/fGAajj/1
    private static final String NEW_ELSE_REGEX              = "(^[ |\\t\\f]*)(\\[ELSE\\])|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[ELSE\\]).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/syA45B/1
    private static final String NEW_ELSEIF_REGEX            = "(^[ |\\t\\f]*)(\\[ELSEIF\\] *%[^%]+%)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[ELSEIF\\] *%[^%]+%).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/4uJIgj/1
    private static final String NEW_LOOP_DECLARATION_REGEX  = "\\[FOR\\] *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))?(( )|$)";
    // Link: https://regex101.com/r/CnqAXP/1
    private static final String NEW_LOOP_END_REGEX          = "(^[ |\\t\\f]*)(\\[NEXT\\] *[A-Za-z0-9_]+)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[NEXT\\] *[A-Za-z0-9_]+).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/wLV7R6/1
    private static final String BREAK_LOOP_REGEX            = "(^[ |\\t\\f]*)(\\[BREAK_LOOP\\])|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[BREAK_LOOP\\]).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/FE6CrS/1
    private static final String JOIN_REGEX                  = "(^[ |\\t\\f]*)(\\[JOIN\\] *#[^#]+#(,#[^#]+#)*=((\"[^\"]*\")|(#[^#]+#))(,((\"[^\"]*\")|(#[^#]+#)))*)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[JOIN\\] *#[^#]+#(,#[^#]+#)*=((\"[^\"]*\")|(#[^#]+#))(,((\"[^\"]*\")|(#[^#]+#)))*).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/E46cpq/1
    private static final String JOIN_END_REGEX              = "(^[ |\\t\\f]*)(\\[ENDJOIN\\])|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[ENDJOIN\\]).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/SCOZZc/1
    private static final String TOREG_REGEX                 = "(^[ |\\t\\f]*)(\\[TOREG\\] *[A-Za-z0-9_]+ *=#[^#]+#)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[TOREG\\] *[A-Za-z0-9_]+ *=#[^#]+#).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/hdIag6/2
    private static final String FROMREG_REGEX               = "(^[ |\\t\\f]*)(\\[FROMREG\\] *#[^#]+# *=[A-Za-z0-9_]+)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[FROMREG\\] *#[^#]+# *=[A-Za-z0-9_]+).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/f3m9xO/3
    private static final String INCLUDE_REGEX               = "(^[ |\\t\\f]*)(\\[INCLUDE\\] *[A-Za-z0-9_\\/\\.\\\\:]+)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[INCLUDE\\] *[A-Za-z0-9_\\/\\.\\\\:]+).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/rtgVP2/1
    private static final String LENGTH_REGEX                = "(^[ |\\t\\f]*)(\\[LENGTH\\] *#[^#]+#,#[^#]+#)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[LENGTH\\] *#[^#]+#,#[^#]+#).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/fJHSwR/1
    private static final String INPSTR_REGEX                = "(^[ |\\t\\f]*)(\\[INPSTR\\] *\"[^\"]*\"#[^#]+#)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[INPSTR\\] *\"[^\"]*\"#[^#]+#).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/78H1C8/2
    private static final String TOSTR_REGEX                 = "(^[ |\\t]*)(\\[TOSTR\\] *#[^#]+# *= *((\"[^\"]*\")|(#[^#]+#)))|(^[ |\\t]*<@transparent [ |\\t]*(\\[TOSTR\\] *#[^#]+# *= *((\"[^\"]*\")|(#[^#]+#))).* @>[ |\\t]*)";
    // Link: https://regex101.com/r/7n73HP/2
    private static final String CATSTR_REGEX                = "(^[ |\\t]*)(\\[CATSTR\\] *#[^#]+#=((#[^#]+#)|(\"[^\"]*\")) *, *((#[^#]+#)|(\"[^\"]*\")))|(^[ |\\t]*<@transparent [ |\\t]*(\\[CATSTR\\] *#[^#]+#=((#[^#]+#)|(\"[^\"]*\")) *, *((#[^#]+#)|(\"[^\"]*\"))).* @>[ |\\t]*)";
    // Link: https://regex101.com/r/joo9pA/1
    private static final String TRAON_REGEX                 = "(^[ |\\t\\f]*)(\\[TRAON\\] *[^ ]?)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[TRAON\\] *[^ ]?).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/Crhxnf/1
    private static final String TRAOFF_REGEX                = "(^[ |\\t\\f]*)(\\[TRAOFF\\])|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[TRAOFF\\]).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/0dBREV/1
    private static final String PRECISION_REGEX             = "(^[ |\\t\\f]*)(\\[PRECISION\\] *[0-9]+)|(^[ |\\t\\f]*<@transparent [ |\\t]*(\\[PRECISION\\] *[0-9]+).* @>[ |\\t\\f]*)";
    // Link: https://regex101.com/r/LKv63j/2
    private static final String NEWSYN_REGEX                = "(^[ |\\t\\f]*)(\\[NEWSYN\\])|(<@transparent [ |\\t]*\\[NEWSYN\\] @>)";
    // Link: https://regex101.com/r/SkZ03w/2
    private static final String RTLON_REGEX                 = "(^[ |\\t\\f]*)(\\[RTLON\\])|(<@transparent [ |\\t]*\\[RTLON\\] @>)";
    // Link: https://regex101.com/r/L4QMAg/1
    private static final String RTLOFF_REGEX                = "(^[ |\\t\\f]*)(\\[RTLOFF\\])|(<@transparent [ |\\t]*\\[RTLOFF\\] @>)";


    // TODO: Aggiungere la dichiarazione di inizio e fine del loop in linea (nuova sintassi)

    // Espressioni regolari per le altre istruzioni
    private static final String MNEMONIC_REGEX = "#[^#]+#";

    private ArrayList<String>                   wholeLineInstructionRegularExpressions;
    private ArrayList<String>                   complexInstructionRegularExpressions;
    private ArrayList<InnerInstruction>         instructionsFound;
    private TransparentInstruction              transparentFound;
    private InnerInstruction                    inlineInstructionFound;
    private int                                 currentRowIndex;

    public RpaInstructionsFinderPlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

        currentRowIndex             = 0;
        inlineInstructionFound      = null;

        wholeLineInstructionRegularExpressions  = new ArrayList<String>();
        complexInstructionRegularExpressions    = new ArrayList<String>();
        instructionsFound                       = new ArrayList<InnerInstruction>();

        initWholeLineInstructionRegularExpressions();
        initComplexInstructionRegularExpressions();

    }

    private void initWholeLineInstructionRegularExpressions() {

        wholeLineInstructionRegularExpressions.add(COMMENT_LINE_REGEX);
        wholeLineInstructionRegularExpressions.add(OLD_IF_REGEX);
        wholeLineInstructionRegularExpressions.add(OLD_IF_VARIANT_REGEX);
        wholeLineInstructionRegularExpressions.add(OLD_IF_CLOSE_OR_ELSE_REGEX);
        wholeLineInstructionRegularExpressions.add(OLD_IF_ELSEIF_REGEX);
        wholeLineInstructionRegularExpressions.add(RECORD_DATA_REGEX);
        wholeLineInstructionRegularExpressions.add(NEW_IF_REGEX);
        wholeLineInstructionRegularExpressions.add(NEW_ENDIF_REGEX);
        wholeLineInstructionRegularExpressions.add(NEW_ELSE_REGEX);
        wholeLineInstructionRegularExpressions.add(NEW_ELSEIF_REGEX);
        wholeLineInstructionRegularExpressions.add(BREAK_LOOP_REGEX);
        wholeLineInstructionRegularExpressions.add(NEW_LOOP_END_REGEX);
        wholeLineInstructionRegularExpressions.add(JOIN_REGEX);
        wholeLineInstructionRegularExpressions.add(JOIN_END_REGEX);
        wholeLineInstructionRegularExpressions.add(TOREG_REGEX);
        wholeLineInstructionRegularExpressions.add(FROMREG_REGEX);
        wholeLineInstructionRegularExpressions.add(INCLUDE_REGEX);
        wholeLineInstructionRegularExpressions.add(LENGTH_REGEX);
        wholeLineInstructionRegularExpressions.add(INPSTR_REGEX);
        wholeLineInstructionRegularExpressions.add(TOSTR_REGEX);
        wholeLineInstructionRegularExpressions.add(CATSTR_REGEX);
        wholeLineInstructionRegularExpressions.add(TRAON_REGEX);
        wholeLineInstructionRegularExpressions.add(TRAOFF_REGEX);
        wholeLineInstructionRegularExpressions.add(PRECISION_REGEX);
        wholeLineInstructionRegularExpressions.add(NEWSYN_REGEX);
        wholeLineInstructionRegularExpressions.add(RTLON_REGEX);
        wholeLineInstructionRegularExpressions.add(RTLOFF_REGEX);

    }

    private void initComplexInstructionRegularExpressions() {

        // Nota: E' importante l'ordine con cui vengono aggiunte le espressioni regolari!

        complexInstructionRegularExpressions.add(OLD_LOOP_DECLARATION_REGEX);
        complexInstructionRegularExpressions.add(NEW_LOOP_DECLARATION_REGEX);
        complexInstructionRegularExpressions.add(OLD_LOOP_END_REGEX);
        // complexInstructionRegularExpressions.add(NEW_LOOP_END_REGEX);
        complexInstructionRegularExpressions.add(NEW_INLINE_LOOP_DECLARATION_REGEX);
        complexInstructionRegularExpressions.add(NEW_INLINE_LOOP_END_REGEX);
        complexInstructionRegularExpressions.add(IF_MATH_OPERATION_ASSIGN_REGEX);
        complexInstructionRegularExpressions.add(IF_MATH_OPERATION_REGEX);
        complexInstructionRegularExpressions.add(MATH_OPERATION_ASSIGN_REGEX);
        complexInstructionRegularExpressions.add(MATH_OPERATION_REGEX);
        complexInstructionRegularExpressions.add(MNEMONIC_REGEX);

    }

    @Override
    public void run() throws IOException {

        debugMessages.precompilePrint("#####################################################");
        debugMessages.precompilePrint("#\t\tInizio documento per 'InstructionsFinder'\t#");
        debugMessages.precompilePrint("#####################################################");

        recursiveFindInstructionDeclarations(mainCompositore.getComposerConfiguration().getInputDocument());

        debugMessages.precompilePrint("#####################################################");
        debugMessages.precompilePrint("#\t\tFine documento per 'InstructionsFinder'\t\t#");
        debugMessages.precompilePrint("#####################################################");

        debugMessages.precompilePrint("");

    }

    private void recursiveFindInstructionDeclarations(Node node) throws RpaInvalidInstructionDeclarationException {

        // CASO BASE: Ho trovato un nodo terminale (paragrafo)
        if (node.getNodeType() == NodeType.PARAGRAPH) {

            Paragraph paragraphNode = (Paragraph) node;

            // Caso speciale: Un paragrafo contiene uno shape (es: TextBox)
            if (paragraphNode.getChildNodes().getCount() == 1 && paragraphNode.getChildNodes().get(0).getNodeType() == NodeType.SHAPE) {

                recursiveFindInstructionDeclarations(paragraphNode.getChildNodes().get(0));

            } else {

                processNodeParagraph(paragraphNode);

            }

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                recursiveFindInstructionDeclarations(childNode);

            }

        }

    }

    private void processNodeParagraph(Paragraph paragraphNode) throws RpaInvalidInstructionDeclarationException {

        // Document document = ComposerConfiguration.getInstance().getInputDocument();
        Document document = mainCompositore.getComposerConfiguration().getInputDocument();

        // Cerco la posizione di tutte le istruzioni all'interno del paragrafo
        String paragraphText = "";

        for (Run runNode : paragraphNode.getRuns()) {

            paragraphText += runNode.getText();

        }

        debugMessages.precompilePrint(paragraphText);

        // Se il paragrafo è vuoto, esco
        if (paragraphText.isEmpty()) {

            return;

        }

        String processedNodeText = processNodeText(paragraphText);

        // Se ho trovato un trasparente, definisco un unico nodo per il paragrafo
        if (transparentFound != null) {

            processedNodeText = processedNodeText.replaceAll("\\r|\\n", "");

            Run newRunNode = new Run(document, processedNodeText);

            paragraphNode.removeAllChildren();
            paragraphNode.appendChild(newRunNode);

            transparentFound        = null;
            inlineInstructionFound  = null;

            return;

        }

        // Se ho trovato una istruzione da linea intera, definisco un unico nodo per il paragrafo
        else if (inlineInstructionFound != null) {

            int instructionStart    = inlineInstructionFound.getIndexInstructionStart();
            int instructionEnd      = inlineInstructionFound.getOriginalInstructionEnd();

            String instructionText  = paragraphText.substring(instructionStart, instructionEnd);
            processedNodeText       = COMPO_START_TAG + instructionText + COMPO_END_TAG;

            Run newRunNode = new Run(document, processedNodeText);

            paragraphNode.removeAllChildren();
            paragraphNode.appendChild(newRunNode);

            inlineInstructionFound = null;

            return;

        }

        // Recupero tutte le istruzioni della riga corrente (Ordinate per posizione nel paragrafo)
        TreeMap<Integer, InnerInstruction> paragraphInstructions = new TreeMap<Integer, InnerInstruction>();

        for (int i = 0; i < instructionsFound.size(); i++) {

            InnerInstruction innerInstruction = instructionsFound.get(i);

            if ((currentRowIndex - 1) == innerInstruction.getRowIndex()) {

                paragraphInstructions.put(innerInstruction.getIndexInstructionStart(), innerInstruction);

            }

        }

        // Preparo le strutture dati per il salvataggio dei nuovi nodi
        ArrayList<Run>  newRunNodeCollection    = new ArrayList<Run>();

        // ### Normalizzo i nodi ###
        if (!paragraphInstructions.isEmpty()) {

            RunCollection       runNodes            = paragraphNode.getRuns();
            InnerInstruction    currentInstruction  = paragraphInstructions.get(paragraphInstructions.keySet().toArray()[0]);

            int currentCharacterIndexOnParagraph    = 0;
            int indexInstruction                    = 0;

            String instructionText = "";

            boolean isStateInstructionRead = false;

            // Ciclo su ogni nodo
            for (int i = 0; i < runNodes.getCount(); i++) {

                Run     runNode         = runNodes.get(i);
                String  runNodeText     = runNode.getText();
                String  newRunNodeText  = "";

                boolean isInstructionOpenPreviousNode = isStateInstructionRead;

                // Ciclo su ogni carattere di ogni nodo
                Font lastFontFound = runNode.getFont();

                for (int j = 0; j < runNodeText.length(); j++) {

                    // Recupero l'ultimo font utilizzato
                    try {

                        if (lastFontFound != null && !lastFontFound.getName().equals(runNode.getFont().getName())) {

                            lastFontFound = null;

                        }

                    } catch (Exception exception) {}

                    Character characterText =  runNodeText.charAt(j);

                    // Se ho trovato l'inizio di una istruzione, passo allo stato di lettura di una istruzione
                    if (currentCharacterIndexOnParagraph == currentInstruction.getOriginalInstructionStart()) {

                        isStateInstructionRead  = true;
                        instructionText         += characterText;

                    }

                    // Se trovo la fine di una istruzione, cambio lo stato e passo all'istruzione successiva
                    else if (currentCharacterIndexOnParagraph == currentInstruction.getOriginalInstructionEnd() && isStateInstructionRead) {

                        isStateInstructionRead  = false;

                        // Inoltre, se l'inizio dell'istruzione era in un nodo precedente, taglio il testo prima della fine
                        // dell'istruzione e aggiungo un nodo con il testo
                        if (isInstructionOpenPreviousNode) {

                            Run newRun = new Run(document, instructionText);

                            if (lastFontFound != null) {

                                try {

                                    // newRun.getFont().setStyle(lastFontFound.getStyle());
                                    newRun = (Run) runNode.deepClone(true);
                                    newRun.setText(instructionText);

                                } catch (Exception exception) { }

                            }

                            isInstructionOpenPreviousNode = false;
                            newRunNodeCollection.add(newRun);
                            // newRunNodeCollection.add(new Run(document, instructionText));

                        }

                        // Se il mnemonico inizia e finisce nello stesso nodo, lo aggiungo al nuovo testo del nodo
                        else {

                            newRunNodeText += instructionText;

                        }

                        instructionText = "";

                        // Passo all'istruzione successiva (se ne ho altre)
                        ++ indexInstruction;

                        if (indexInstruction < paragraphInstructions.size()) {

                            Integer keyToNextInstruction = (Integer) paragraphInstructions.keySet().toArray()[indexInstruction];
                            currentInstruction = paragraphInstructions.get(keyToNextInstruction);

                        }

                        // Salvo il nuovo carattere
                        newRunNodeText += characterText;

                    }

                    // Se sto leggendo il testo di una istruzione, ne registro il testo apparte
                    else if (isStateInstructionRead) {

                        instructionText += characterText;

                    }

                    // In tutti gli altri casi, sto leggendo del normale testo
                    else {

                        // Inserisco nel testo letto il carattere corrente
                        newRunNodeText += characterText;

                    }

                    ++ currentCharacterIndexOnParagraph;

                }

                // Se uscendo sono arrivato alla fine dell'istruzione, la salvo
                if (currentCharacterIndexOnParagraph == currentInstruction.getOriginalInstructionEnd()) {

                    isStateInstructionRead  = false;

                    // Inoltre, se l'inizio dell'istruzione era in un nodo precedente, taglio il testo prima della fine
                    // dell'istruzione e aggiungo un nodo con il testo
                    if (isInstructionOpenPreviousNode) {

                        Run newRun = new Run(document, instructionText);

                        if (lastFontFound != null) {

                            try {

                                // newRun.getFont().setStyle(lastFontFound.getStyle());
                                newRun = (Run) runNode.deepClone(true);
                                newRun.setText(instructionText);

                            } catch (Exception exception) { }

                        }

                        isInstructionOpenPreviousNode = false;
                        newRunNodeCollection.add(newRun);
                        // newRunNodeCollection.add(new Run(document, instructionText));

                    }

                    // Se il mnemonico inizia e finisce nello stesso nodo, lo aggiungo al nuovo testo del nodo
                    else {

                        newRunNodeText += instructionText;

                    }

                    instructionText = "";

                    // Passo all'istruzione successiva (se ne ho altre)
                    ++ indexInstruction;

                    if (indexInstruction < paragraphInstructions.size()) {

                        Integer keyToNextInstruction = (Integer) paragraphInstructions.keySet().toArray()[indexInstruction];
                        currentInstruction = paragraphInstructions.get(keyToNextInstruction);

                    }

                }

                // Arrivato alla fine del nodo, se NON E' VUOTO, salvo il nodo nella collezione dei nodi da salvare
                if (!newRunNodeText.isEmpty()) {

                    try {

                        runNode.setText(newRunNodeText);

                        // Aggiungo il nodo
                        newRunNodeCollection.add(runNode);

                    } catch (Exception exception) {

                        String errorCode    = paragraphText;
                        String errorMessage = "Errore nell'aggiornamento del testo di un nodo aspose";
                        int context         = RpaComposerException.PRECOMPILE_MESSAGE;

                        throw new RpaInvalidInstructionDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

                    }

                }

            }

        }

        // Se ho definito dei nuovi nodi per il paragrafo, li aggiungo al posto di quelli vecchi
        if (!newRunNodeCollection.isEmpty()) {

            paragraphNode.removeAllChildren();

            for (Run runNode : newRunNodeCollection) {

                paragraphNode.appendChild(runNode);

            }

        }

        // ### Inserisco i TAG ###
        newRunNodeCollection.clear();

        if (!paragraphInstructions.isEmpty()) {

            RunCollection       runNodes            = paragraphNode.getRuns();
            InnerInstruction    currentInstruction  = paragraphInstructions.get(paragraphInstructions.keySet().toArray()[0]);

            int currentCharacterIndexOnParagraph    = 0;
            int indexInstruction                    = 0;

            // Ciclo su ogni nodo
            for (int i = 0; i < runNodes.getCount(); i++) {

                Run     runNode                 = runNodes.get(i);
                String  runNodeText             = runNode.getText();
                String  newRunNodeText          = "";
                boolean isInstructionReadState  = false;

                // Ciclo su ogni carattere del nodo
                for (int j = 0; j < runNodeText.length(); j++) {

                    Character characterText = runNodeText.charAt(j);

                    // Se trovo l'inizio di una istruzione, aggiungo il TAG di apertura
                    if (currentCharacterIndexOnParagraph == currentInstruction.getOriginalInstructionStart()) {

                        isInstructionReadState = true;
                        newRunNodeText += COMPO_START_TAG;
                        newRunNodeText += characterText;

                    }

                    // Se trovo la fine di una istruzione, aggiungo il TAG di chiusura
                    else if (currentCharacterIndexOnParagraph == currentInstruction.getOriginalInstructionEnd() && isInstructionReadState) {

                        // Aggiungo il TAG di chiusura
                        isInstructionReadState = false;
                        newRunNodeText += COMPO_END_TAG;
                        newRunNodeText += characterText;

                        // Passo alla prossima istruzione (ce ne sono)
                        ++ indexInstruction;

                        if (indexInstruction < paragraphInstructions.size()) {

                            Integer keyToNextInstruction = (Integer) paragraphInstructions.keySet().toArray()[indexInstruction];
                            currentInstruction = paragraphInstructions.get(keyToNextInstruction);

                        }

                    }

                    // In tutti gli altri casi, salvo il carattere
                    else {

                        newRunNodeText += characterText;

                    }

                    ++currentCharacterIndexOnParagraph;

                }

                // Se al termine di un nodo trovo la fine di una istruzione, aggiungo il TAG di chiusura
                if (currentCharacterIndexOnParagraph == currentInstruction.getOriginalInstructionEnd() && isInstructionReadState) {

                    // Aggiungo il TAG di chiusura
                    isInstructionReadState = false;
                    newRunNodeText += COMPO_END_TAG;

                    // Passo alla prossima istruzione (ce ne sono)
                    ++ indexInstruction;

                    if (indexInstruction < paragraphInstructions.size()) {

                        Integer keyToNextInstruction = (Integer) paragraphInstructions.keySet().toArray()[indexInstruction];
                        currentInstruction = paragraphInstructions.get(keyToNextInstruction);

                    }

                }

                // Alla fine del nodo, modifico il testo
                if (!newRunNodeText.isEmpty()) {

                    try {

                        runNode.setText(newRunNodeText);

                        // Aggiungo il nodo
                        newRunNodeCollection.add(runNode);

                    } catch (Exception exception) {

                        String errorCode    = paragraphText;
                        String errorMessage = "Errore nell'aggiornamento del testo di un nodo";
                        int context = RpaComposerException.PRECOMPILE_MESSAGE;

                        throw new RpaInvalidInstructionDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

                    }

                }

            }

        }

        // Se ho definito dei nuovi nodi per il paragrafo, li aggiungo al posto di quelli vecchi
        if (!newRunNodeCollection.isEmpty()) {

            paragraphNode.removeAllChildren();

            for (Run runNode : newRunNodeCollection) {

                paragraphNode.appendChild(runNode);

            }

        }

    }

    private String processNodeText(String nodeText) throws RpaInvalidInstructionDeclarationException {

        String newNodeText = "";

        // Recupero il separatore di linee
        String lineSeparator = System.getProperty("line.separator");

        // Recupero tutte le righe del nodo
        String[] nodeLines = nodeText.split(lineSeparator);

        // Inserisco per ogni istruzione trovata, il TAG "<?compo ... ?>"
        for (String nodeLine : nodeLines) {

            if ((nodeLine != null) && !nodeLine.isEmpty()) {

                String newNodeLine = "";

                newNodeLine = encloseTransparentInstruction(nodeLine);
                newNodeLine = encloseWholeLineInstructions(newNodeLine);
                newNodeLine = encloseCompositeInstructions(newNodeLine);

                newNodeText += newNodeLine + lineSeparator;

                ++ currentRowIndex;

            }

        }

        // System.out.println(newNodeText);
        debugMessages.precompilePrint(newNodeText);

        return newNodeText;

    }

    private String encloseTransparentInstruction(String documentLine) {

        // Verifico se la riga è un trasparente
        StringBuilder   newNodeTextStringBuilder    = new StringBuilder(documentLine);
        Matcher         matcherTransparentLine      = Pattern.compile(NO_PRINT_LINE_REGEX).matcher(documentLine);

        if (matcherTransparentLine.find()) {

            // Verifico se la riga è già stata considerata come trasparente
            int lineIndex = currentRowIndex;

            // Elimino la "@" inziale
            int atsignIndex = matcherTransparentLine.start(1);
            newNodeTextStringBuilder.replace(atsignIndex, atsignIndex + 1, "");

            // Aggiungo il TAG "<@transparent " a inizio riga e il TAG " @>" a fine riga
            int newNodeTextEndIndex = newNodeTextStringBuilder.length() + TRANSPARENT_START_TAG.length();

            newNodeTextStringBuilder.insert(0, TRANSPARENT_START_TAG);
            newNodeTextStringBuilder.insert(newNodeTextEndIndex, TRANSPARENT_END_TAG);

            // Notifico la presenza di un trasparente
            transparentFound = new TransparentInstruction(
                    lineIndex,
                    atsignIndex
            );

        }

        return newNodeTextStringBuilder.toString();

    }

    private String encloseWholeLineInstructions(String documentLine) {

        StringBuilder newLineStringBuilder = new StringBuilder(documentLine);

        // Verifico se la riga è associata ad una istruzione a riga
        for (String wholeLineInstructionRegularExpression : wholeLineInstructionRegularExpressions) {

            if (inlineInstructionFound != null) {

                break;

            }

            Matcher matcherInstruction = Pattern.compile(wholeLineInstructionRegularExpression).matcher(newLineStringBuilder);

            if (matcherInstruction.find()) {

                // Estraggo l'indice riga
                int lineIndex = currentRowIndex;

                // Controllo che la riga non sia già stata considerata
                boolean isInstructionAlreadyReaded = false;

                for (InnerInstruction innerInstruction : instructionsFound) {

                    if (lineIndex == innerInstruction.getRowIndex()) {

                        isInstructionAlreadyReaded = true;

                    }

                }

                if (!isInstructionAlreadyReaded) {

                    // Recupero la posizione di inizio e fine istruzione
                    int indexStartInstruction   = -1;
                    int indexEndInstruction     = -1;

                    /*
                    int indexStartScan  = 0;
                    int indexEndScan    = documentLine.length() - 1;
                    */

                    int innerGroupId = -1;

                    if (matcherInstruction.group(2) != null) {

                        innerGroupId = 2;

                    } else {

                        // Cerco il primo gruppo TROVATO dopo l'1
                        int groupIndex = 2;
                        while (innerGroupId == -1) {

                            ++ groupIndex;
                            if (matcherInstruction.group(groupIndex) != null) {

                                innerGroupId = groupIndex + 1;

                            }

                        }


                    }

                    int indexStartScan  = matcherInstruction.start(innerGroupId);
                    int indexEndScan    = matcherInstruction.end(innerGroupId) - 1;

                    char whiteSpace = ' ';
                    char tab        = '\t';

                    do {

                        char startCharString    = newLineStringBuilder.charAt(indexStartScan);
                        char endCharString      = newLineStringBuilder.charAt(indexEndScan);

                        // Verifico se ho trovato un carattere di INIZIO diverso da TAB, spazio e '@'
                        if (indexStartInstruction == -1 && startCharString != whiteSpace && startCharString != tab) {

                            indexStartInstruction = indexStartScan;

                        }

                        // Verifico se ho trovato un carattere di FINE diverso da TAB e spazio
                        if (indexEndInstruction == -1 && endCharString != whiteSpace && endCharString != tab) {

                            indexEndInstruction = indexEndScan + 1;

                        }

                        ++ indexStartScan;
                        -- indexEndScan;

                    } while (
                            (indexStartInstruction == -1 || indexEndInstruction == -1) &&
                                    (indexStartScan <= indexEndScan)
                            );

                    // Aggiungo il TAG del compositore
                    newLineStringBuilder.insert(indexStartInstruction, COMPO_START_TAG);
                    newLineStringBuilder.insert(indexEndInstruction + COMPO_START_TAG.length(), COMPO_END_TAG);

                    // Aggiungo la linea alle istruzioni già lette
                    inlineInstructionFound = new InnerInstruction(
                            lineIndex,
                            indexStartInstruction,
                            indexEndInstruction,
                            indexStartInstruction,
                            indexEndInstruction,
                            true
                    );

                    instructionsFound.add(inlineInstructionFound);

                    /*
                    instructionsFound.add(new InnerInstruction(
                            lineIndex,
                            0,
                            documentLine.length(),
                            indexStartInstruction,
                            indexEndInstruction,
                            true
                    ));
                    */

                }

            }

        }

        // Ritorno la linea del documento
        return newLineStringBuilder.toString();

    }

    private String encloseCompositeInstructions(String documentLine) {

        int countMatch                      = 0;
        StringBuilder newLineStringBuilder  = new StringBuilder(documentLine);

        // Verifico se la riga è associata ad una istruzione complessa
        for (String complexInstructionRegularExpression : complexInstructionRegularExpressions) {

            Matcher matcherInstruction  = Pattern.compile(complexInstructionRegularExpression).matcher(documentLine);

            while (matcherInstruction.find()) {

                // ++ countMatch;

                // Estraggo l'indice riga e la posizione
                int lineIndex               = currentRowIndex;
                int indexInstructionStart   = matcherInstruction.start();
                int indexInstructionEnd     = matcherInstruction.end();

                // Rimuovo caratteri extra inclusi dalle espressioni regolari
                int countStartExtraCharsToAvoid = 0;
                int countEndExtraCharsToAvoid   = 0;

                if (documentLine.charAt(indexInstructionEnd - 1) == ' ') {

                    int charIndex = indexInstructionEnd - 1;

                    while (documentLine.charAt(charIndex) == ' ') {

                        ++ countEndExtraCharsToAvoid;
                        -- charIndex;

                    }

                }

                // Verifico che l'istruzione NON incroci una istruzione che ho già considerato
                boolean isInstructionInAnotherIstruction = false;

                for (InnerInstruction innerInstruction : instructionsFound) {

                    boolean isEveryInstructionsIndexMatch = lineIndex == innerInstruction.getRowIndex();

                    // Caso 1: indexStart <= indexEndInner      AND indexEnd >= indexEndInner
                    // Caso 2: indexStart <= indexStartInner    AND indexEnd >= indexStartInner
                    // Caso 3: indexStart >= indexStartInner    AND indexEnd <= indexEndInner
                    boolean isCurrentInstructionCrossReadedInstruction =
                            ( indexInstructionStart <= innerInstruction.getIndexInstructionEnd()    &&
                                    indexInstructionEnd   >= innerInstruction.getIndexInstructionEnd())   ||
                                    ( indexInstructionStart <= innerInstruction.getIndexInstructionStart()  &&
                                            indexInstructionEnd   >= innerInstruction.getIndexInstructionStart()) ||
                                    ( indexInstructionStart >= innerInstruction.getIndexInstructionStart()  &&
                                            indexInstructionEnd   <= innerInstruction.getIndexInstructionEnd());

                    isInstructionInAnotherIstruction =
                            isEveryInstructionsIndexMatch &&
                                    (isCurrentInstructionCrossReadedInstruction || innerInstruction.isRowInstruction);

                    if (isInstructionInAnotherIstruction) {

                        break;

                    }

                }

                if (!isInstructionInAnotherIstruction) {

                    // Conto il numero di TAG aggiunti in precedenza
                    int countPreviousInstructionsTagged = 0;

                    for (InnerInstruction innerInstruction : instructionsFound) {

                        boolean isInnerInstructionMatchRowIndex
                                = innerInstruction.getRowIndex() == lineIndex;
                        boolean isInnerInstructionBeforeCurrentInstruction
                                = innerInstruction.getOriginalInstructionStart() < indexInstructionStart;

                        if (isInnerInstructionMatchRowIndex && isInnerInstructionBeforeCurrentInstruction) {

                            ++ countPreviousInstructionsTagged;

                        }

                    }

                    // int extraStringLength = countMatch * (COMPO_START_TAG.length() + COMPO_END_TAG.length());
                    int extraStringLength = countPreviousInstructionsTagged * (COMPO_START_TAG.length() + COMPO_END_TAG.length());

                    // int newIndexInstructionStart    = indexInstructionStart + extraStringLength;
                    int newIndexInstructionStart    = indexInstructionStart + extraStringLength + countStartExtraCharsToAvoid;
                    int newIndexInstructionEnd      = indexInstructionEnd + extraStringLength - countEndExtraCharsToAvoid;

                    // Aggiungo il TAG del compositore
                    newLineStringBuilder.insert(newIndexInstructionStart, COMPO_START_TAG);
                    newLineStringBuilder.insert(COMPO_START_TAG.length() + newIndexInstructionEnd, COMPO_END_TAG);

                    // Aggiungo la linea alle istruzioni già lette
                    InnerInstruction newInnerInstruction = new InnerInstruction(
                            lineIndex,
                            indexInstructionStart + countStartExtraCharsToAvoid,
                            indexInstructionEnd - countEndExtraCharsToAvoid,
                            indexInstructionStart + countStartExtraCharsToAvoid,
                            indexInstructionEnd - countEndExtraCharsToAvoid
                    );
                    instructionsFound.add(newInnerInstruction);

                    ++ countMatch;

                }

            }

        }

        // Ritorno la linea del documento
        return newLineStringBuilder.toString();

    }

    private class TransparentInstruction {

        private int rowIndex;
        private int atsignIndex;

        public TransparentInstruction(int rowIndex, int atsignIndex) {

            this.rowIndex       = rowIndex;
            this.atsignIndex    = atsignIndex;

        }

    }

    private class InnerInstruction {

        private int rowIndex;
        private int indexInstructionStart;
        private int indexInstructionEnd;
        private int originalInstructionStart;
        private int originalInstructionEnd;
        private boolean isRowInstruction;

        public InnerInstruction(
                int rowIndex,
                int indexInstructionStart,
                int indexInstructionEnd,
                int originalInstructionStart,
                int originalInstructionEnd
        ) {

            this.rowIndex                   = rowIndex;
            this.indexInstructionStart      = indexInstructionStart;
            this.indexInstructionEnd        = indexInstructionEnd;
            this.originalInstructionStart   = originalInstructionStart;
            this.originalInstructionEnd     = originalInstructionEnd;
            this.isRowInstruction           = false;

        }

        public InnerInstruction(
                int rowIndex,
                int indexInstructionStart,
                int indexInstructionEnd,
                int originalInstructionStart,
                int originalInstructionEnd,
                boolean isRowInstruction
        ) {

            this(
                    rowIndex,
                    indexInstructionStart,
                    indexInstructionEnd,
                    originalInstructionStart,
                    originalInstructionEnd
            );

            this.isRowInstruction = isRowInstruction;

        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getIndexInstructionStart() {
            return indexInstructionStart;
        }

        public int getIndexInstructionEnd() {
            return indexInstructionEnd;
        }

        public int getOriginalInstructionStart() { return originalInstructionStart; }

        public int getOriginalInstructionEnd() { return originalInstructionEnd; }

        public boolean isRowInstruction() { return isRowInstruction; }

    }

}
