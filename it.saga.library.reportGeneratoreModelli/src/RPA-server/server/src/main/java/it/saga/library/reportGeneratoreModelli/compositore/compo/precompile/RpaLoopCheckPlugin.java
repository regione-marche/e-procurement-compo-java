package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import com.aspose.words.CompositeNode;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaMnemonicVisitor;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaInvalidLoopDeclarationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaLoopCheckPlugin extends RpaPrecompiler {

    private enum LoopElementType {
        LOOP_START_FOR,     LOOP_START,
        LOOP_INLINE_START,  LOOP_INLINE_FOR_START,
        LOOP_END,           LOOP_END_FOR,
        LOOP_INLINE_END,    LOOP_INLINE_FOR_END,
        MNEMONIC
    }

    // ### START LOOP DECLARATION ###

    // Source: https://regex101.com/r/cnKugk/3/
    private static final String REGEX_START_LOOP            = "<\\?compo (\\$\\$ *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))? *) \\?>";

    // Source: https://regex101.com/r/2IBEX3/3
    private static final String REGEX_START_LOOP_FOR        = "<\\?compo (\\[FOR\\] *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))?(( *)|$)) \\?>";

    // Source: https://regex101.com/r/QY6VWi/3
    private static final String REGEX_START_INLINE_LOOP     = "<\\?compo (\\$\\$ *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+[ |\\n]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))? *\\$\\$\\$) \\?>";

    // Source: https://regex101.com/r/O6OwJX/3
    private static final String REGEX_START_INLINE_LOOP_FOR = "<\\?compo (\\[FOR\\] *[A-Z|a-z|0-9|_]+ *= *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#)) *, *(([0-9]+)|(\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\#))( *, *[0-9]+[ |\\n]+)?(( *\\| *\\#\\_NOENT\\_\\#)|(( *\\| *\\#[A-Z|a-z|0-9|_|\\[|\\]|\\{|\\}|\\+|-|\\/|\\*]+\\# *[\\+\\-]?)+))? *\\[NEXTR\\]) \\?>";



    // ### END LOOP DECLARATION ###

    // Source: https://regex101.com/r/SCVFH1/2/
    private static final String REGEX_END_LOOP              = "<\\?compo (\\$\\$ *[A-Z|a-z|0-9|_]+) \\?>";

    // Source: https://regex101.com/r/MB7it1/3
    private static final String REGEX_END_LOOP_FOR          = "<\\?compo (\\[NEXT\\] *[A-Z|a-z|0-9|_]+) \\?>";

    // Source: https://regex101.com/r/cXctbN/2/
    private static final String REGEX_END_INLINE_LOOP       = "<\\?compo (\\$\\$ *[A-Z|a-z|0-9|_]+ *\\$\\$\\$) \\?>";

    // Source: https://regex101.com/r/a18f0S/5
    private static final String REGEX_END_INLINE_LOOP_FOR   = "<\\?compo (\\[NEXT\\] *[A-Z|a-z|0-9|_]+ *\\[NEXTR\\]) \\?>";



    // ### MNEMONIC DECLARATION ###

    // Source: https://regex101.com/r/GWuSA4/5/
    private static final String REGEX_MNEMONIC      = "#([^#]+)#";

    // Source: https://regex101.com/r/ByEQkQ/2
    private static final String REGEX_MNEMONIC_JOIN = "[^(]+\\((([0-9]*[A-Za-z_][0-9]*)+)\\)";



    // ### LOOP INDEX ###

    // Source: https://regex101.com/r/tbHqk5/1
    private static final String REGEX_INDEX_LOOP            = "\\$\\$ *([A-Z|a-z|0-9|_]+) *= *";

    // Source: https://regex101.com/r/4o5akm/1
    private static final String REGEX_INDEX_LOOP_FOR        = "\\[FOR\\] *([A-Z|a-z|0-9|_]+) *= *";

    // Source: https://regex101.com/r/mil4X1/1
    private static final String REGEX_INDEX_LOOP_END        = "\\$\\$ *([A-Z|a-z|0-9|_]+)";

    // Source: https://regex101.com/r/lqSmUl/2
    private static final String REGEX_INDEX_LOOP_FOR_END    = "\\[NEXT\\] *([A-Z|a-z|0-9|_]+)";



    private Map<String, LoopElementType>            regexInstructionTypeMapper;
    private Map<Integer, LoopElementNodeCollection> loopElementNodeCollectionMap;
    private Map<Integer, Integer>                   closedLoopPerNodeMap;
    private Stack<LoopDeclaration>                  loopDeclarationStack;

    private int countNode;



    public RpaLoopCheckPlugin(RpaMainCompositore mainCompositore) {

        super(mainCompositore);

        // Inizializzo le strutture per la collezione degli elementi
        regexInstructionTypeMapper      = new LinkedHashMap<String, LoopElementType>();
        loopElementNodeCollectionMap    = new TreeMap<Integer, LoopElementNodeCollection>();
        closedLoopPerNodeMap            = new HashMap<Integer, Integer>();
        loopDeclarationStack            = new Stack<LoopDeclaration>();

        // Inizializzo il mapper "espressione regolare - tipo di istruzione"
        regexInstructionTypeMapper.put(REGEX_START_LOOP,            LoopElementType.LOOP_START);
        regexInstructionTypeMapper.put(REGEX_START_LOOP_FOR,        LoopElementType.LOOP_START_FOR);
        regexInstructionTypeMapper.put(REGEX_START_INLINE_LOOP,     LoopElementType.LOOP_INLINE_START);
        regexInstructionTypeMapper.put(REGEX_START_INLINE_LOOP_FOR, LoopElementType.LOOP_INLINE_FOR_START);
        regexInstructionTypeMapper.put(REGEX_END_LOOP,              LoopElementType.LOOP_END);
        regexInstructionTypeMapper.put(REGEX_END_LOOP_FOR,          LoopElementType.LOOP_END_FOR);
        regexInstructionTypeMapper.put(REGEX_END_INLINE_LOOP,       LoopElementType.LOOP_INLINE_END);
        regexInstructionTypeMapper.put(REGEX_END_INLINE_LOOP_FOR,   LoopElementType.LOOP_INLINE_FOR_END);
        regexInstructionTypeMapper.put(REGEX_MNEMONIC,              LoopElementType.MNEMONIC);

        // Nota: La regex dei mnemonici deve essere SEMPRE L'ULTIMA in posizione

    }

    @Override
    public void run() throws IOException {

        countNode = 0;
        loopElementNodeCollectionMap.clear();

        // Cerco nel documento tutti gli elementi relativi a loop
        // recursiveFindLoopElements(ComposerConfiguration.getInstance().getInputDocument());
        recursiveFindLoopElements(mainCompositore.getComposerConfiguration().getInputDocument());

        // Cerco tutti i mnemonici di ogni loop
        findMnemonicEachLoop();

        // Se ho dei loop che non sono stati chiusi, lancio un errore
        if (!loopDeclarationStack.empty()) {

            String errorCode    = loopDeclarationStack.peek().loopElement.loopElementString;
            String errorMessage = "Ci sono Loop che non sono stati chiusi";
            int context         = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaInvalidLoopDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

    }

    private void recursiveFindLoopElements(Node node) throws RpaInvalidLoopDeclarationException {

        // CASO BASE: Ho trovato un nodo terminale
        if (node.getNodeType() == NodeType.RUN) {

            Run runNode     = (Run) node;

            nodeFindLoopElements(runNode);

            ++ countNode;

        }

        // CASO RICORSIVO: Ho un nodo composto quindi itero sui figli del nodo
        else if (node.isComposite()) {

            CompositeNode<Node> compositeNode = (CompositeNode) node;

            for (Node childNode : (Iterable<Node>) compositeNode.getChildNodes()) {

                recursiveFindLoopElements(childNode);

            }

        }

    }

    private void nodeFindLoopElements(Run runNode) throws RpaInvalidLoopDeclarationException {

        // Ogni istruzione relativa ad un loop trovata, la inserisco in un albero ordinato
        // per posizione nel documento
        for (Map.Entry<String, LoopElementType> loopElementEntry : regexInstructionTypeMapper.entrySet()) {

            String          regexLoopElement    = loopElementEntry.getKey();
            LoopElementType loopElementType     = loopElementEntry.getValue();

            Matcher loopElementMatcher = Pattern.compile(regexLoopElement).matcher(runNode.getText());

            while (loopElementMatcher.find()) {

                // Creo un nuovo oggetto-elemento di un loop
                String  loopElementString       = loopElementMatcher.group(1);
                Integer loopElementStartIndex   = loopElementMatcher.start(1);
                Integer loopElementEndIndex     = loopElementMatcher.end(1);

                LoopElement loopElement = new LoopElement(
                        loopElementString,
                        loopElementType,
                        loopElementStartIndex,
                        loopElementEndIndex,
                        countNode
                );

                // Se ho un mnemonico, controllo che non sia di una dichiarazione di loop
                boolean isLoopElementMnemonic           = loopElementType == LoopElementType.MNEMONIC;
                boolean isMnemonicBelongLoopDeclaration = false;

                if (isLoopElementMnemonic) {

                    isMnemonicBelongLoopDeclaration = isMnemonicBelongLoopDeclaration(loopElement, countNode);

                }

                if (!isLoopElementMnemonic || !isMnemonicBelongLoopDeclaration) {

                    // Aggiungo l'elemento alla lista di quelli già trovati
                    LoopElementNodeCollection loopElementNodeCollection;

                    if (!loopElementNodeCollectionMap.containsKey(countNode)) {

                        loopElementNodeCollection = new LoopElementNodeCollection(runNode);
                        loopElementNodeCollectionMap.put(countNode, loopElementNodeCollection);

                    } else {

                        loopElementNodeCollection = loopElementNodeCollectionMap.get(countNode);

                    }

                    loopElementNodeCollection.loopElementMap.put(loopElementMatcher.start(), loopElement);

                }

            }

        }

    }

    private boolean isMnemonicBelongLoopDeclaration(LoopElement mnemonicElement, int nodeIndex) {

        boolean isMnemonicBelongLoopDeclaration = false;

        // Ciclo su tutte le possibili dichiarazioni di loop dello stesso nodo
        LoopElementNodeCollection loopElementNodeCollection = loopElementNodeCollectionMap.get(nodeIndex);

        if (loopElementNodeCollection != null) {

            for (Map.Entry<Integer, LoopElement> loopElementEntry : loopElementNodeCollection.loopElementMap.entrySet()) {

                LoopElement loopElement = loopElementEntry.getValue();

                if (    mnemonicElement.loopElementStartIndex   >=  loopElement.loopElementStartIndex &&
                        mnemonicElement.loopElementEndIndex     <=  loopElement.loopElementEndIndex ) {

                    isMnemonicBelongLoopDeclaration = true;

                    break;

                }

            }

        }

        return isMnemonicBelongLoopDeclaration;

    }

    // TODO: Ai loop non vengono associati i mnemonici corretti
    // TODO: (Vedere per il modello corrente l'associazione con il mnemonico "#TIPNOMC.N#" che NON VIENE ASSOCIATO)
    // TODO: (Usare una espressione regolare per filtrare il nome del mnemonico dal contesto dichiarativo)

    private void findMnemonicEachLoop() throws RpaInvalidLoopDeclarationException {

        int countLoop = 0;

        // Per ogni tipo di elemento di un loop, esegue delle istruzioni diverse
        for (Map.Entry<Integer, LoopElementNodeCollection> loopElementNodeEntry : loopElementNodeCollectionMap.entrySet()) {

            LoopElementNodeCollection loopElementNodeCollection = loopElementNodeEntry.getValue();

            for (Map.Entry<Integer, LoopElement> loopElementEntry : loopElementNodeCollection.loopElementMap.entrySet()) {

                LoopElement loopElement = loopElementEntry.getValue();

                switch (loopElement.type) {

                    case LOOP_START:
                    case LOOP_START_FOR:
                    case LOOP_INLINE_START:
                    case LOOP_INLINE_FOR_START:

                        processLoopDeclarationElement(loopElement, loopElementNodeCollection, countLoop);

                        ++ countLoop;

                        break;

                    case LOOP_END:
                    case LOOP_END_FOR:
                    case LOOP_INLINE_END:
                    case LOOP_INLINE_FOR_END:

                        processLoopEndElement(loopElement);

                        break;

                    case MNEMONIC:

                        processMnemonic(loopElement);

                        break;

                }

            }

        }

    }

    private void processLoopDeclarationElement(
            LoopElement loopElement,
            LoopElementNodeCollection loopElementNodeCollection,
            int countLoop
    ) {

        // Estraggo il nome dell'indice
        String loopIndexName = "";

        if (loopElement.type == LoopElementType.LOOP_START  ||
            loopElement.type == LoopElementType.LOOP_INLINE_START ) {

            Matcher loopIndexMatcher =
                    Pattern.compile(REGEX_INDEX_LOOP).matcher(loopElement.loopElementString);

            loopIndexMatcher.find();

            loopIndexName = loopIndexMatcher.group(1);

        } else {

            Matcher loopIndexMatcher =
                    Pattern.compile(REGEX_INDEX_LOOP_FOR).matcher(loopElement.loopElementString);

            loopIndexMatcher.find();

            loopIndexName = loopIndexMatcher.group(1);

        }

        // Verifico se è presente il flag "#_NOENT_#"
        boolean hasLoopNoEntity = loopElement.loopElementString.contains("#_NOENT_#");

        // Aggiungo allo stack la nuova dichiarazione
        LoopDeclaration loopDeclaration = new LoopDeclaration(
                loopElementNodeCollection.runNode,
                loopElement,
                hasLoopNoEntity,
                countLoop,
                loopIndexName
        );

        loopDeclarationStack.push(loopDeclaration);

    }

    private void processLoopEndElement(LoopElement loopElement) throws RpaInvalidLoopDeclarationException {

        // Estraggo il nome dell'indice
        String loopIndexName = "";

        if (    loopElement.type == LoopElementType.LOOP_END    ||
                loopElement.type == LoopElementType.LOOP_INLINE_END ) {

            Matcher loopIndexMatcher =
                    Pattern.compile(REGEX_INDEX_LOOP_END).matcher(loopElement.loopElementString);

            loopIndexMatcher.find();

            loopIndexName = loopIndexMatcher.group(1);

        } else {

            Matcher loopIndexMatcher =
                    Pattern.compile(REGEX_INDEX_LOOP_FOR_END).matcher(loopElement.loopElementString);

            loopIndexMatcher.find();

            loopIndexName = loopIndexMatcher.group(1);

        }

        // Controllo se lo stack è vuoto
        if (loopDeclarationStack.isEmpty()) {

            String errorCode    = loopIndexName;
            String errorMessage = "[" + loopIndexName + "] Nessun loop trovato da chiudere";
            int context = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaInvalidLoopDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

        // Controllo se l'indice è uguale all'indice del loop in testa allo stack
        if (!loopDeclarationStack.peek().indexName.equals(loopIndexName)) {

            String errorCode    = loopIndexName;
            String errorMessage = "Loop chiuso con un indice diverso";
            int context = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaInvalidLoopDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

        // Se gli indici sono uguali, rimuovo l'elemento in testa allo stack
        LoopDeclaration loopDeclaration = loopDeclarationStack.pop();

        // Aggiungo il link nel testo
        int countClosedLoop = 0;

        if (closedLoopPerNodeMap.containsKey(loopDeclaration.loopElement.nodeIndex)) {

            countClosedLoop = closedLoopPerNodeMap.get(loopDeclaration.loopElement.nodeIndex);

        }

        StringBuilder newNodeTextStringBuilder = new StringBuilder(loopDeclaration.runNode.getText());
        int indexLoopKey = loopDeclaration.loopElement.loopElementEndIndex + countClosedLoop;

        newNodeTextStringBuilder.insert(indexLoopKey, "{" + loopDeclaration.index + "}");

        closedLoopPerNodeMap.put(
                loopDeclaration.loopElement.nodeIndex,
                countClosedLoop + 2 + String.valueOf(loopDeclaration.index).length()
        );

        try {

            loopDeclaration.runNode.setText(newNodeTextStringBuilder.toString());

        } catch (Exception exception) {

            String errorCode    = "";
            String errorMessage = "Impossibile aggiornare il nodo aspose";
            int context = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaInvalidLoopDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

        // Aggiungo il mnemonico nel contesto dell'applicativo (se c'è)
        mainCompositore.getLoopInformationManager().addLoopInformation(
                loopDeclaration.index,
                loopDeclaration.mnemonic
        );

        // System.out.println("New loop text: " + newNodeTextStringBuilder.toString());
        debugMessages.precompilePrint("New loop text: " + newNodeTextStringBuilder.toString());

    }

    private void processMnemonic(LoopElement loopElement) throws RpaInvalidLoopDeclarationException {

        // Controllo se il mnemonico è un indice
        boolean                     isMnemonicLoopIndex     = false;
        Iterator<LoopDeclaration>   loopDeclarationIterator = loopDeclarationStack.iterator();

        while (loopDeclarationIterator.hasNext()) {

            LoopDeclaration loopDeclaration = loopDeclarationIterator.next();

            if (loopDeclaration.indexName.equals(loopElement.loopElementString)) {

                isMnemonicLoopIndex = true;

            }

        }

        // Controllo se il mnemonico è una join con un altro mnemonico (nel caso estraggo il nome tra parantesi)
        String  mnemonicName;
        Matcher joinMnemonicMatcher = Pattern.compile(REGEX_MNEMONIC_JOIN).matcher(loopElement.loopElementString);

        if (joinMnemonicMatcher.find()) {

            mnemonicName = joinMnemonicMatcher.group(1);

        }

        // Altrimenti, estraggo il nome del mnemonico
        else {

            Matcher mnemonicMatcher = Pattern.compile(RpaMnemonicVisitor.EXTRACT_NAME_MNEMONIC_REGEX).matcher(loopElement.loopElementString);
            mnemonicMatcher.find();
            mnemonicName = mnemonicMatcher.group();
        }

        // Verifico che il mnemonico sia presente nel c0campi
        boolean isMnemonicInDB =
                mainCompositore.getMnemonicDescriptionList().isMnemonic(mnemonicName);

        // Verifico che l'utente non abbia definito un indice di un loop con un nome del c0campi
        if (isMnemonicLoopIndex && isMnemonicInDB) {

            String errorCode    = loopElement.loopElementString;
            String errorMessage = "L'indice di un loop corrisponde al nome di un mnemonico del c0campi";
            int context = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaInvalidLoopDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, errorCode, errorMessage);

        }

        // Se non lo è, lo associo al primo loop senza mnemonico che non ha il "#_NOENT_#"
        if (!isMnemonicLoopIndex && isMnemonicInDB) {

            loopDeclarationIterator = loopDeclarationStack.iterator();

            while (loopDeclarationIterator.hasNext()) {

                LoopDeclaration loopDeclaration = loopDeclarationIterator.next();

                if (loopDeclaration.mnemonic == null && !loopDeclaration.hasNoEntity) {

                    loopDeclaration.mnemonic = mnemonicName;

                    break;

                }

            }

        }

    }

    private class LoopElement {

        LoopElementType type;
        String          loopElementString;
        Integer         loopElementStartIndex;
        Integer         loopElementEndIndex;
        Integer         nodeIndex;

        private LoopElement(String loopElementString, LoopElementType type,
                            Integer loopElementStartIndex, Integer loopElementEndIndex,
                            Integer nodeIndex) {

            this.loopElementString      = loopElementString;
            this.type                   = type;
            this.loopElementStartIndex  = loopElementStartIndex;
            this.loopElementEndIndex    = loopElementEndIndex;
            this.nodeIndex              = nodeIndex;

        }

    }

    private class LoopElementNodeCollection {

        Run                         runNode;
        Map<Integer, LoopElement>   loopElementMap;

        private LoopElementNodeCollection(Run runNode) {

            this.loopElementMap = new TreeMap<Integer, LoopElement>();
            this.runNode        = runNode;

        }

    }

    private class LoopDeclaration {

        Run         runNode;
        LoopElement loopElement;
        boolean     hasNoEntity;
        String      mnemonic;
        int         index;
        String      indexName;

        private LoopDeclaration(Run runNode, LoopElement loopElement,
                                boolean hasNoEntity, int index,
                                String indexName) {

            this.runNode        = runNode;
            this.loopElement    = loopElement;
            this.hasNoEntity    = hasNoEntity;
            this.index          = index;
            this.indexName      = indexName;

        }

        private void setMnemonic(String mnemonic) {

            this.mnemonic = mnemonic;

        }

        private String getMnemonic() {

            return this.mnemonic;

        }

    }

}
