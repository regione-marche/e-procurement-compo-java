package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaGenericLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeGenericLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDataConversionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicDataFunctionContainer;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaNumberUtils;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaSpecialMnemonicManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaVariablesManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonicConstant;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicEmpty;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicLoopIndex;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonicTOT;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicDirectJoinContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicFormattedContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicIndexedContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicMultiConversionContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicSimpleContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicWithDomainContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.MnemonicWithFunctionContext;

import java.sql.Connection;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaMnemonicVisitor extends RpaMnemonicConversionVisitor {

    public static final int TOT_CUSTOM_INDEX = 999;
    public static final int STR_CUSTOM_INDEX = 998;

    public static final String EXTRACT_NAME_MNEMONIC_REGEX  = "[A-Za-z0-9\\_]+";
    // public static final String EXTRACT_TOT_REGEX            = "^#TOT([0-9]+|V)?";
    // public static final String EXTRACT_STR_REGEX            = "^#STR([0-9]+|V)?";
    public static final String EXTRACT_TOT_REGEX            = "^#TOT(([0-9]*)|(V))([#.\\[\\{\\(\\)])";
    public static final String EXTRACT_STR_REGEX            = "^#STR(([0-9]*)|(V))([#.\\[\\{\\(\\)])";

    private RpaSpecialMnemonicManager specialMnemonicManager;

    public RpaMnemonicVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

        super(conn, scope, mainCompositore, parentNode, childNode);

        specialMnemonicManager = new RpaSpecialMnemonicManager(mainCompositore);

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicSimple(MnemonicSimpleContext context) {

        String mnemonicNameRaw  = context.mnemonicName.getText();
        mnemonicNameRaw         = mnemonicNameRaw.replaceAll(" ", "");

        // Controllo se il mnemonico è uno di quelli "speciali"
        if (specialMnemonicManager.isMnemonicSpecial(mnemonicNameRaw)) {

            // Nota: Non riconosce i mnemonici con formattazione
            RpaAbstractMnemonic mnemonic = specialMnemonicManager.getMnemonic(mnemonicNameRaw);

            if (mnemonic == null) {

                RpaValue newValue = new RpaValue(null);
                newValue.setIsSkip(true);

                return newValue;

            } else {

                // return new Value<AbstractMnemonic>(mnemonic);
                return mnemonic.generateValue();

            }

        }

        // Controllo se il mnemonico è associato ad un loop
        Integer loopIndex = null;

        if (checkIfMnemonicIsLoopIndex(scopeStack, mnemonicNameRaw) == null) {

            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);

                // if (scope.getScopeType() == Scope.LOOP_SCOPE_TYPE || scope.getScopeType() == Scope.INLINE_LOOP_SCOPE_TYPE) {
                if (scope instanceof RpaScopeGenericLoop) {

                    RpaScopeGenericLoop scopeGenericLoop = (RpaScopeGenericLoop) scope;
                    Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicNameRaw);
                    mnemonicNameMatcher.find();
                    String mnemonicName = mnemonicNameMatcher.group();

                    if (scopeGenericLoop.isMnemonicMatchEntity(mnemonicName)) {

                        loopIndex = scopeGenericLoop.getLoopValue().getCurrentValue();
                        break;

                    }

                }

            }

        }

        RpaValue<RpaAbstractMnemonic> mnemonicValue;
        if (loopIndex != null) {

            mnemonicValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw, loopIndex);

        } else {

            mnemonicValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        mnemonicValue.getValue().getPrintedValue();
        return mnemonicValue;

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicIndexed(MnemonicIndexedContext context) {

        String  mnemonicNameRaw = context.MNEMONIC_WITH_INDEX_ID().getText();
        mnemonicNameRaw         = mnemonicNameRaw.replaceAll(" ", "");
        int     index           = Integer.valueOf(context.POSITIVE_INTEGER().getText());

        // Controllo se il mnemonico è uno di quelli "speciali"
        if (specialMnemonicManager.isMnemonicSpecial(mnemonicNameRaw)) {

            // Nota: Non riconosce i mnemonici con formattazione
            RpaAbstractMnemonic mnemonic = specialMnemonicManager.getMnemonic(mnemonicNameRaw);

            if (mnemonic == null || mnemonic instanceof RpaMnemonicEmpty) {

                RpaValue newValue = new RpaValue(null);
                newValue.setIsSkip(true);

                return newValue;

            } else {

                // return new Value<AbstractMnemonic>(mnemonic);
                return mnemonic.generateValue();

            }

        }


        RpaValue<RpaAbstractMnemonic> mnemonicValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw, index);

        // Applico le formattazioni
        if (mnemonicValue == null || mnemonicValue.getValue() == null || mnemonicValue.getValue() instanceof RpaMnemonicEmpty) {

            return mnemonicValue;

        }

        // DataConversionContainer     dataConversionContainer = DataConversionContainer.getInstance();
        RpaDataConversionContainer  dataConversionContainer = mainCompositore.getDataConversionContainer();
        RpaAbstractMnemonic         mnemonic                = mnemonicValue.getValue();

        dataConversionContainer.setValue(mnemonic.getValue());
        dataConversionContainer.setMnemonic(mnemonic);

        // Eseguo la conversione
        if (context.formatConversion() != null) {

            visit(context.formatConversion());

            mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        }

        // Eseguo le altre formattazioni
        if (context.changeFormat() != null) {

            visit(context.changeFormat());

            mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        }

        if (context.formatDomain() != null) {

            visit(context.formatDomain());

            mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        }

        dataConversionContainer.clear();

        // Restituisco il risultato
        // return new Value<AbstractMnemonic>(mnemonic);
        return mnemonic.generateValue();

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicDirectJoin(MnemonicDirectJoinContext context) {

        // TODO: Implementare le formattazioni per i mnemonici con join implicite
        System.err.println("Implementare le formattazioni per i mnemonici con join implicite");

        String rightMnemonicName = context.MNEMONIC_JOIN_SUFFIX_ID().getText();

        // Recupero lo "ScopeMnemonicContext" più interno
        RpaScopeMnemonicContext scopeMnemonicContext = null;

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            if (scope instanceof RpaScopeMnemonicContext) {

                scopeMnemonicContext = (RpaScopeMnemonicContext) scope;
                break;

            }

        }

        // Se sono in un loop, ne recupero l'indice
        Integer loopIndex = null;

        if (checkIfMnemonicIsLoopIndex(scopeStack, rightMnemonicName) == null) {

            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);

                if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE || scope.getScopeType() == RpaScope.INLINE_LOOP_SCOPE_TYPE) {

                    RpaScopeGenericLoop scopeGenericLoop = (RpaScopeGenericLoop) scope;
                    Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(rightMnemonicName);
                    mnemonicNameMatcher.find();
                    String rightMnemonicNameClean = mnemonicNameMatcher.group();

                    if (scopeGenericLoop.isMnemonicMatchEntity(rightMnemonicNameClean)) {

                        loopIndex = scopeGenericLoop.getLoopValue().getCurrentValue();
                        break;

                    }

                }

            }

        }

        // Recupero il valore del mnemonico di destra
        RpaValue rightMnemonicValue  = null;

        if (loopIndex != null) {

            rightMnemonicValue = getMnemonicSimpleValue(mainCompositore, scopeStack, "#" + rightMnemonicName, loopIndex);

        } else {

            rightMnemonicValue = getMnemonicSimpleValue(mainCompositore, scopeStack, "#" + rightMnemonicName);

        }

        // Controllo se la join è su un STR o su un TOT
        if (checkIfMnemonicSTR("#" + rightMnemonicName) || checkIfMnemonicTOT("#" + rightMnemonicName)) {

            // MnemonicManager     mnemonicManager     = MnemonicManager.getMnemonicManager();
            RpaMnemonicManager mnemonicManager     = mainCompositore.getMnemonicManager();
            RpaAbstractMnemonic rightMnemonic       = (RpaAbstractMnemonic) rightMnemonicValue.getValue();

            // Estraggo il nome del mnemonico di sinistra
            String leftMnemonicNameRaw  = context.MNEMONIC_WITH_INDEX_ID().getText();
            leftMnemonicNameRaw         = leftMnemonicNameRaw.replaceAll(" ", "");
            String leftMnemonicName     = "";
            Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(leftMnemonicNameRaw);

            if (mnemonicNameMatcher.find()) {

                leftMnemonicName = mnemonicNameMatcher.group();

            }

            // Recupero il valore della join
            try {

                // Se ho una stringa, effettuo una JOIN SQL
                if (checkIfMnemonicSTR("#" + rightMnemonicName)) {

                    RpaAbstractMnemonic joinValue = mnemonicManager.getMnemonicJoinValue(
                            leftMnemonicName,
                            rightMnemonic.getValue(),
                            scopeMnemonicContext
                    );

                    return joinValue.generateValue();

                }

                // Se ho un numero, prendo il mnemonico all'indice del numero
                else {

                    int     index;
                    String  indexString = rightMnemonic.getValue();

                    if (RpaNumberUtils.isDouble(indexString)) {

                        Double indexDouble = Double.valueOf(indexString);
                        index = indexDouble.intValue();

                    } else {

                        index = Integer.valueOf(indexString);

                    }

                    return getMnemonicSimpleValue(mainCompositore, scopeStack, leftMnemonicNameRaw, index);

                }

            } catch (Exception exception) {

                throw new ParseCancellationException("[MNEMONIC JOIN] Errore nel recupero della join");

            }

        }

        // Controllo se la join è su un indice di un loop
        else if (checkIfMnemonicIsLoopIndex(scopeStack, "#" + rightMnemonicName) != null) {

            // MnemonicManager mnemonicManager     = MnemonicManager.getMnemonicManager();
            RpaMnemonicManager mnemonicManager     = mainCompositore.getMnemonicManager();
            Integer         mnemonicLoopIndex   = checkIfMnemonicIsLoopIndex(scopeStack, "#" + rightMnemonicName);

            // Estraggo il nome del mnemonico di sinistra
            String leftMnemonicNameRaw  = context.MNEMONIC_WITH_INDEX_ID().getText();
            leftMnemonicNameRaw         = leftMnemonicNameRaw.replaceAll(" ", "");
            String leftMnemonicName     = "";
            Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(leftMnemonicNameRaw);

            if (mnemonicNameMatcher.find()) {

                leftMnemonicName = mnemonicNameMatcher.group();

            }

            // Recupero il mnemonico all'indice indicato
            try {

                // TODO: IMPORTANTE: VIENE RECUPERATO IL MNEMONICO DI SINISTRA ALL'INDICE DEL LOOP
                // TODO: PER IL TEST "totalizzatori.rtf"
                RpaValue<RpaAbstractMnemonic> leftMnemonicValue = getMnemonicSimpleValue(
                        mainCompositore,
                        scopeStack,
                        leftMnemonicNameRaw,
                        mnemonicLoopIndex
                );

                return leftMnemonicValue;

                /*
                AbstractMnemonic joinValue = mnemonicManager.getMnemonicJoinValue(
                        leftMnemonicName,
                        String.valueOf(mnemonicLoopIndex),
                        scopeMnemonicContext
                );

                return joinValue.generateValue();
                */

            } catch (Exception exception) {

                throw new ParseCancellationException("[MNEMONIC JOIN] Errore nel recupero della join");

            }

        }

        // Ho a destra un mnemonico da DB
        else {

            rightMnemonicName = rightMnemonicName.substring(0, rightMnemonicName.length() - 1);

            if (rightMnemonicValue.getValue() == null) {

                return new RpaValue<RpaAbstractMnemonic>(mainCompositore.getMnemonicEmpty());

            } else {

                // MnemonicManager mnemonicManager = MnemonicManager.getMnemonicManager();
                RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

                // Estraggo il nome del mnemonico di sinistra
                String leftMnemonicNameRaw  = context.MNEMONIC_WITH_INDEX_ID().getText();
                leftMnemonicNameRaw         = leftMnemonicNameRaw.replaceAll(" ", "");
                String leftMnemonicName     = "";
                Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(leftMnemonicNameRaw);

                if (mnemonicNameMatcher.find()) {

                    leftMnemonicName = mnemonicNameMatcher.group();

                }

                RpaAbstractMnemonic rightMnemonic = (RpaAbstractMnemonic) rightMnemonicValue.getValue();

                // Recupero il valore della join
                try {

                    // ScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, leftMnemonicName);

                    RpaAbstractMnemonic joinValue = mnemonicManager.getMnemonicJoinValue(
                            leftMnemonicName,
                            rightMnemonic.getValue(),
                            scopeMnemonicContext
                    );

                    return joinValue.generateValue();

                } catch (Exception exception) {

                    System.err.println(exception);
                    throw new ParseCancellationException("[MNEMONIC JOIN] Errore nel recupero della join");

                }

            }

        }

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicMultiConversion(MnemonicMultiConversionContext context) {

        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();

        // Estraggo il valore del mnemonico
        String  mnemonicNameRaw         = context.MNEMONIC_WITH_CONVERSION_ID().getText();
        mnemonicNameRaw                 = mnemonicNameRaw.replaceAll(" ", "");
        RpaValue mnemonicContentValue   = null;
        Integer loopIndex               = getLoopIndexOfMnemonic(scopeStack, mnemonicNameRaw);

        // Controllo se il mnemonico è uno di quelli speciali
        if (specialMnemonicManager.isMnemonicFormattedSpecial(mnemonicNameRaw)) {

            mnemonicContentValue = specialMnemonicManager.getMnemonicFormatted(mnemonicNameRaw).generateValue();

        }

        // Controllo se sono dentro ad un loop
        else if (loopIndex != null) {

            mnemonicContentValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw, loopIndex);

        }

        // Altrimenti sono in un mnemonico semplice
        else {

            mnemonicContentValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        if (mnemonicContentValue == null || mnemonicContentValue.getValue() == null || mnemonicContentValue.getValue() instanceof RpaMnemonicEmpty) {

            RpaValue newValue = new RpaValue(null);
            newValue.setIsSkip(true);

            return newValue;

        }

        RpaAbstractMnemonic mnemonic = (RpaAbstractMnemonic) mnemonicContentValue.getValue();

        dataConversionContainer.setValue(mnemonic.getValue());
        dataConversionContainer.setMnemonic(mnemonic);

        // Eseguo la conversione
        visit(context.formatConversion());

        mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        // Eseguo le altre formattazioni
        if (context.changeFormat() != null) {

            visit(context.changeFormat());

            mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        }

        if (context.formatDomain() != null) {

            visit(context.formatDomain());

            mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        }

        dataConversionContainer.clear();

        // Restituisco il risultato
        return mnemonic.generateValue();

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicFormatted(MnemonicFormattedContext context) {

        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();

        // Estraggo il valore del mnemonico
        String  mnemonicNameRaw         = context.MNEMONIC_WITH_FORMAT_ID().getText();
        mnemonicNameRaw                 = mnemonicNameRaw.replaceAll(" ", "");
        RpaValue mnemonicContentValue   = null;
        Integer loopIndex               = getLoopIndexOfMnemonic(scopeStack, mnemonicNameRaw);

        // Controllo se il mnemonico è uno di quelli speciali
        if (specialMnemonicManager.isMnemonicFormattedSpecial(mnemonicNameRaw)) {

            mnemonicContentValue = specialMnemonicManager.getMnemonicFormatted(mnemonicNameRaw).generateValue();

        }

        // Controllo se sono dentro ad un loop
        else if (loopIndex != null) {

            mnemonicContentValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw, loopIndex);

        }

        // Altrimenti sono in un mnemonico semplice
        else {

            mnemonicContentValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        if (mnemonicContentValue == null || mnemonicContentValue.getValue() == null | mnemonicContentValue.getValue() instanceof RpaMnemonicEmpty) {

            RpaValue newValue = new RpaValue(null);
            newValue.setIsSkip(true);

            return newValue;

        }

        RpaAbstractMnemonic mnemonic = (RpaAbstractMnemonic) mnemonicContentValue.getValue();

        dataConversionContainer.setValue(mnemonic.getValue());
        dataConversionContainer.setMnemonic(mnemonic);

        // Eseguo la conversione
        visit(context.changeFormat());

        mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        // Eseguo le altre formattazioni
        if (context.formatDomain() != null) {

            visit(context.formatDomain());

            mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());

        }

        dataConversionContainer.clear();

        // Restituisco il risultato
        return mnemonic.generateValue();

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicWithDomain(MnemonicWithDomainContext context) {

        RpaDataConversionContainer dataConversionContainer = mainCompositore.getDataConversionContainer();

        // Estraggo il valore del mnemonico
        String  mnemonicNameRaw         = context.MNEMONIC_WITH_DOMAIN_ID().getText();
        mnemonicNameRaw                 = mnemonicNameRaw.replaceAll(" ", "");
        RpaValue mnemonicContentValue   = null;
        Integer loopIndex               = getLoopIndexOfMnemonic(scopeStack, mnemonicNameRaw);

        // Controllo se il mnemonico è uno di quelli speciali
        if (specialMnemonicManager.isMnemonicFormattedSpecial(mnemonicNameRaw)) {

            mnemonicContentValue = specialMnemonicManager.getMnemonicFormatted(mnemonicNameRaw).generateValue();

        }

        // Controllo se sono dentro ad un loop
        else if (loopIndex != null) {

            mnemonicContentValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw, loopIndex);

        }

        // Altrimenti sono in un mnemonico semplice
        else {

            mnemonicContentValue = getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        if (mnemonicContentValue == null || mnemonicContentValue.getValue() == null || mnemonicContentValue.getValue() instanceof RpaMnemonicEmpty || ((RpaAbstractMnemonic) mnemonicContentValue.getValue()).getValue() == null) {

            RpaValue newValue = new RpaValue(null);
            newValue.setIsSkip(true);

            return newValue;

        }

        RpaAbstractMnemonic mnemonic = (RpaAbstractMnemonic) mnemonicContentValue.getValue();

        dataConversionContainer.setValue(mnemonic.getValue());
        dataConversionContainer.setMnemonic(mnemonic);

        // Eseguo la formattazione
        visit(context.formatDomain());

        mnemonic.setLastFormattedValue(dataConversionContainer.getCustomValueFormat());
        dataConversionContainer.clear();

        // Restituisco il risultato
        return mnemonic.generateValue();

    }

    @Override
    public RpaValue<RpaAbstractMnemonic> visitMnemonicWithFunction(MnemonicWithFunctionContext context) {

        // Per ora lancio un messaggio di warning

        // Inoltro il nome del mnemonico alla specifica funzione
        RpaMnemonicDataFunctionContainer mnemonicDataFunctionContainer = mainCompositore.getMnemonicDataFunctionContainer();
        mnemonicDataFunctionContainer.setMnemonicName(context.MNEMONIC_FUNCTION_SUFFIX_ID().getText());

        // TODO: Implementare le visite alle funzioni prefisse al mnemonico

        // Eseguo la funzione associata al mnemonico e restituisco il risultato
        visit(context.prefixFunction());
        // return mnemonicDataFunctionContainer.getRpaFormat().generateValue();

        return mainCompositore.getMnemonicEmpty().generateValue();

    }

    public static RpaValue<RpaAbstractMnemonic> getMnemonicSimpleValue(RpaMainCompositore mainCompositore, Stack<RpaScope> scopeStack, String mnemonicNameRaw) {

        Integer mnemonicLoopIndex = checkIfMnemonicIsLoopIndex(scopeStack, mnemonicNameRaw);

        // Controllo se il mnemonico è un indice di loop
        if (mnemonicLoopIndex != null) {

            return new RpaValue<RpaAbstractMnemonic>(new RpaMnemonicLoopIndex(mainCompositore, mnemonicLoopIndex));

        }

        // Controllo se il mnemonico è un totalizzatore
        if (checkIfMnemonicTOT(mnemonicNameRaw)) {

            RpaAbstractMnemonic mnemonic = getMnemonicTOT(mainCompositore, mnemonicNameRaw);

            if (mnemonic != null) {

                return mnemonic.generateValue();

            } else {

                return new RpaValue<RpaAbstractMnemonic>(null);

            }

        }

        // Controllo se il mnemonico è un STR
        if (checkIfMnemonicSTR(mnemonicNameRaw)) {

            RpaAbstractMnemonic mnemonic = getMnemonicSTR(mainCompositore, mnemonicNameRaw);

            if (mnemonic != null) {

                return mnemonic.generateValue();

            } else {

                return new RpaValue<RpaAbstractMnemonic>(null);

            }

        }

        // Estraggo il mnemonico
        else {

            RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

            // Controllo se sono in un Scope-Join vuoto
            if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

                return mainCompositore.getMnemonicEmpty().generateValue();

            }

            // Estraggo il valore dal Database

            // Estraggo il nome dal mnemonico
            String  mnemonicName        = "";
            Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicNameRaw);

            if (mnemonicNameMatcher.find()) {

                mnemonicName = mnemonicNameMatcher.group();

            }

            // Recupero il cammino dei mnemonici letti
            RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, mnemonicName);
            RpaAbstractMnemonic mnemonicValue = scopeMnemonicContext.requestMnemonic(mnemonicName);

            scopeMnemonicContext.updateLastReadMnemonic();

            if (mnemonicValue != null) {

                return mnemonicValue.generateValue();

            } else {

                return new RpaValue<RpaAbstractMnemonic>(null);

            }


        }

    }

    public static RpaValue<RpaAbstractMnemonic> getMnemonicSimpleValue(RpaMainCompositore mainCompositore, Stack<RpaScope> scopeStack, String mnemonicNameRaw, int index) {

        Integer mnemonicLoopIndex = checkIfMnemonicIsLoopIndex(scopeStack, mnemonicNameRaw);

        // Controllo se il mnemonico è un indice di loop
        if (mnemonicLoopIndex != null) {

            return getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        // Controllo se il mnemonico è un totalizzatore
        if (checkIfMnemonicTOT(mnemonicNameRaw)) {

            return getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        // Controllo se il mnemonico è un STR
        if (checkIfMnemonicSTR(mnemonicNameRaw)) {

            return getMnemonicSimpleValue(mainCompositore, scopeStack, mnemonicNameRaw);

        }

        // Estraggo il mnemonico
        else {

            RpaMnemonicManager mnemonicManager = mainCompositore.getMnemonicManager();

            // Controllo se sono in un Scope-Join vuoto
            if (mnemonicManager.isEmptyJoinPresent(scopeStack)) {

                return mainCompositore.getMnemonicEmpty().generateValue();

            }

            // Estraggo il valore dal Database

            // Estraggo il nome dal mnemonico
            String  mnemonicName        = "";
            Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicNameRaw);

            if (mnemonicNameMatcher.find()) {

                mnemonicName = mnemonicNameMatcher.group();

            }

            // Recupero il mnemonico dalla catena di entità
            try {

                // Recupero il cammino dei mnemonici letti
                RpaScopeMnemonicContext scopeMnemonicContext = mnemonicManager.getPathInnerJoin(scopeStack, mnemonicName);

                // Restituisco il mnemonico all'indice indicato
                RpaAbstractMnemonic mnemonicValue = scopeMnemonicContext.requestMnemonic(mnemonicName, index);

                scopeMnemonicContext.updateLastReadMnemonic();

                if (mnemonicValue != null) {

                    return mnemonicValue.generateValue();

                } else {

                    return new RpaValue<RpaAbstractMnemonic>(null);

                }

            } catch (Exception exception) {

                System.err.println(exception);
                throw new ParseCancellationException("Errore nel recupero del mnemonico");

            }

        }

    }

    public static RpaAbstractMnemonicConstant getMnemonicTOT(RpaMainCompositore mainCompositore, String mnemonicNameRaw) {

        // Controllo se ho un indice da estrarre dal totalizzatore
        RpaVariablesManager variablesManager = mainCompositore.getVariablesManager();
        Matcher matcher = Pattern.compile(EXTRACT_TOT_REGEX).matcher(mnemonicNameRaw);
        matcher.find();

        String index = "";

        if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {

            index = matcher.group(1);

        }

        // Se non ho un indice, prendo il valore alla posizione 0
        if (index.isEmpty()) {

            RpaValue<RpaAbstractMnemonicConstant> variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, 0);

            if (variableValue == null) {

                return new RpaMnemonicTOT(mainCompositore, 0);

            } else {

                // Resetto la precedente formattazione
                variableValue.getValue().setLastFormattedValue(null);
                return variableValue.getValue();

            }

        }

        // Se ho un indice pari a 'V', prendo il valore definito all'indice '999'
        else if (index.equals("V")) {

            // TODO: MODIFICATO PER FAR FUNZIONARE "totalizzatori.rtf"
            RpaValue<RpaAbstractMnemonicConstant> variableValue =
                    variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, TOT_CUSTOM_INDEX);

            /*
            Value<AbstractMnemonicConstant> indexValueNode =
                    variablesManager.getVariableValue(VariablesManager.TOT, TOT_CUSTOM_INDEX);
            MnemonicTOT mnemonicTOT = (MnemonicTOT) indexValueNode.getValue();

            Integer indexValue = mnemonicTOT.getValueNumber().intValue();

            Value<AbstractMnemonicConstant> variableValue = variablesManager.getVariableValue(VariablesManager.TOT, indexValue);
            */

            if (variableValue == null) {

                return new RpaMnemonicTOT(mainCompositore, 0);

            } else {

                // Resetto la precedente formattazione
                variableValue.getValue().setLastFormattedValue(null);
                return variableValue.getValue();

            }

        }

        // Se è un numero, prendo il valore definito
        else if (RpaNumberUtils.isInteger(index)) {

            Integer indexValue = Integer.valueOf(index);

            RpaValue<RpaAbstractMnemonicConstant> variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, indexValue);

            if (variableValue == null) {

                return new RpaMnemonicTOT(mainCompositore, 0);

            } else {

                // Resetto la precedente formattazione
                variableValue.getValue().setLastFormattedValue(null);
                return variableValue.getValue();

            }

        }

        // Negli altri casi, lancio un errore
        else {

            throw new ParseCancellationException("[MNEMONIC TOT] L'indice per il totalizzatore non è valido");

        }

    }

    public static RpaAbstractMnemonicConstant getMnemonicSTR(RpaMainCompositore mainCompositore, String mnemonicNameRaw) {

        // Controllo se ho un indice da estrarre dall'STR
        RpaVariablesManager variablesManager = mainCompositore.getVariablesManager();
        Matcher matcher = Pattern.compile(EXTRACT_STR_REGEX).matcher(mnemonicNameRaw);
        matcher.find();

        String index = "";

        if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {

            index = matcher.group(1);

        }

        // Se non ho un indice, prendo il valore alla posizione 0
        if (index.isEmpty()) {

            RpaValue<RpaAbstractMnemonicConstant> variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.STR, 0);

            if (variableValue == null) {

                return mainCompositore.getMnemonicEmpty();

            } else {

                // Resetto la precedente formattazione
                variableValue.getValue().setLastFormattedValue(null);
                return variableValue.getValue();

            }

        }

        // Se ho un indice pari a 'V', prendo il valore definito all'indice '998'
        else if (index.equals("V")) {

            /*
            Value<AbstractMnemonicConstant> variableValue =
                    variablesManager.getVariableValue(VariablesManager.STR, STR_CUSTOM_INDEX);
            */

            RpaValue<RpaAbstractMnemonicConstant> indexValueNode =
                    variablesManager.getVariableValue(RpaVariablesManager.Type.TOT, STR_CUSTOM_INDEX);

            RpaMnemonicTOT mnemonicTOT = (RpaMnemonicTOT) indexValueNode.getValue();

            Integer indexValue = mnemonicTOT.getValueNumber().intValue();

            RpaValue<RpaAbstractMnemonicConstant> variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.STR, indexValue);

            if (variableValue == null) {

                return mainCompositore.getMnemonicEmpty();

            } else {

                // Resetto la precedente formattazione
                variableValue.getValue().setLastFormattedValue(null);
                return variableValue.getValue();

            }

        }

        // Se è un numero, prendo il valore definito
        else if (RpaNumberUtils.isInteger(index)) {

            Integer indexValue = Integer.valueOf(index);

            RpaValue<RpaAbstractMnemonicConstant> variableValue = variablesManager.getVariableValue(RpaVariablesManager.Type.STR, indexValue);

            if (variableValue == null) {

                return mainCompositore.getMnemonicEmpty();

            } else {

                // Resetto la precedente formattazione
                variableValue.getValue().setLastFormattedValue(null);
                return variableValue.getValue();

            }

        }

        // Negli altri casi, lancio un errore
        else {

            throw new ParseCancellationException("[MNEMONIC STR] L'indice per la stringa richiesta non è valido");

        }

    }

    public static Integer checkIfMnemonicIsLoopIndex(Stack<RpaScope> scopeStack, String mnemonicNameRaw) {

        // Estraggo il nome dal mnemonico
        Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicNameRaw);

        if (mnemonicNameMatcher.find()) {

            String mnemonicName = mnemonicNameMatcher.group();

            // Ciclo sullo stack dell'applicativo per trovare un match con un indice di loop
            for (RpaScope scope : scopeStack) {

                int scopeType = scope.getScopeType();
                if (scopeType == RpaScope.LOOP_SCOPE_TYPE || scopeType == RpaScope.INLINE_LOOP_SCOPE_TYPE) {

                    RpaScopeGenericLoop scopeLoop   = (RpaScopeGenericLoop) scope;
                    RpaGenericLoopValue loopValue   = scopeLoop.getLoopValue();

                    if (loopValue.getIndexName().equals(mnemonicName)) {

                        return loopValue.getCurrentValue();

                    }

                }

            }

            return null;

        }

        else {

            return null;

        }

    }

    public static Integer getLoopIndexOfMnemonic(Stack<RpaScope> scopeStack, String mnemonicNameRaw) {

        Integer loopIndex = null;

        if (checkIfMnemonicIsLoopIndex(scopeStack, mnemonicNameRaw) == null) {

            for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex--) {

                RpaScope scope = scopeStack.get(scopeIndex);

                // if (scope.getScopeType() == Scope.LOOP_SCOPE_TYPE || scope.getScopeType() == Scope.INLINE_LOOP_SCOPE_TYPE) {
                if (scope instanceof RpaScopeGenericLoop) {

                    RpaScopeGenericLoop scopeGenericLoop = (RpaScopeGenericLoop) scope;
                    Matcher mnemonicNameMatcher = Pattern.compile(EXTRACT_NAME_MNEMONIC_REGEX).matcher(mnemonicNameRaw);
                    mnemonicNameMatcher.find();
                    String mnemonicName = mnemonicNameMatcher.group();

                    if (scopeGenericLoop.isMnemonicMatchEntity(mnemonicName)) {

                        loopIndex = scopeGenericLoop.getLoopValue().getCurrentValue();
                        break;

                    }

                }

            }

        }

        return loopIndex;

    }

    public static boolean checkIfMnemonicTOT(String mnemonicNameRaw) {

        Matcher matcher = Pattern.compile(EXTRACT_TOT_REGEX).matcher(mnemonicNameRaw);

        return matcher.find();

    }

    public static boolean checkIfMnemonicSTR(String mnemonicNameRaw) {

        Matcher matcher = Pattern.compile(EXTRACT_STR_REGEX).matcher(mnemonicNameRaw);

        return matcher.find();

    }

}
