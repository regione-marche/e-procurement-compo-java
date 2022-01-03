package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.misc.ParseCancellationException;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeIf;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ElseIfNewContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ElseIfOldContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ElseStatementNewContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ElseStatementOldContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfEndNewContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfEndOldContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfStatementNewContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfStatementOldContext;

import java.sql.Connection;
import java.util.Stack;

public class RpaIfVisitor extends RpaMnemonicVisitor {

    public RpaIfVisitor(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {

        super(conn, scope, mainCompositore, parentNode, childNode);

    }

    @Override
    public RpaValue<?> visitIfStatementNew(IfStatementNewContext context) {

        // Controllo se sono già all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.isSkipNode()) {

                scopeIf.increaseInternalIfToSkip();

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

        }

        // Recupero l'esito della condizione booleana
        RpaValue<Boolean> ifConditionValue    = (RpaValue<Boolean>) visit(context.booleanStatement());
        boolean         isIfConditionTrue   = ifConditionValue.getValue();

        // Creo un nuovo scope if
        RpaScopeIf newScopeIf = new RpaScopeIf(mainCompositore);

        newScopeIf.setBooleanCondition(isIfConditionTrue);
        newScopeIf.setIsNodeToSkip(!isIfConditionTrue);

        scopeStack.push(newScopeIf);

        RpaValue newSkipValue = new RpaValue(null);
        newSkipValue.setIsSkip(true);

        return newSkipValue;

    }

    @Override
    public RpaValue<?> visitIfStatementOld(IfStatementOldContext context) {

        // if (context.getText().contains("#STR505#.EQ.2")) {

        // Controllo se sono già all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.isSkipNode()) {

                scopeIf.increaseInternalIfToSkip();

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

        }

        // Recupero l'esito della condizione booleana
        RpaValue<Boolean> ifConditionValue    = (RpaValue<Boolean>) visit(context.booleanStatement());
        boolean         isIfConditionTrue   = ifConditionValue.getValue();

        // Recupero l'id della if
        String      ifIdString  = context.IF().getText();
        ifIdString              = ifIdString.replaceAll(" ", "");
        int         ifId        = Integer.valueOf(ifIdString.substring(2));

        // Creo un nuovo scope if
        RpaScopeIf newScopeIf = new RpaScopeIf(mainCompositore);

        newScopeIf.setBooleanCondition(isIfConditionTrue);
        newScopeIf.setIsNodeToSkip(!isIfConditionTrue);
        newScopeIf.setIfId(ifId);

        scopeStack.push(newScopeIf);

        RpaValue newSkipValue = new RpaValue(null);
        newSkipValue.setIsSkip(true);

        return newSkipValue;

    }

    @Override
    public RpaValue<?> visitIfEndNew(IfEndNewContext context) {

        // Controllo se sono all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.getCountInternalIfToSkip() > 0) {

                scopeIf.decreaseInternalIfToSkip();

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

            // Controllo che la chiusura sia fatta su una if della nuova sintassi
            if (scopeIf.getIfId() != null) {

                throw new ParseCancellationException("Errata chiusura di una if [ENDIF]");

            }

            // Chiudo la if togliendola dallo scope-stack
            scopeStack.pop();

            RpaValue newSkipValue = new RpaValue(null);
            newSkipValue.setIsSkip(true);

            return newSkipValue;


        }

        // Altrimenti, lancio un errore
        else {

            throw new ParseCancellationException("Errata chiusura di una if [ENDIF]");

        }

    }

    @Override
    public RpaValue<?> visitIfEndOld(IfEndOldContext context) {

        // Controllo se sono all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.getCountInternalIfToSkip() > 0) {

                scopeIf.decreaseInternalIfToSkip();

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

            // Estraggo l'id della if da chiudere
            String  endIfIdString   = context.IF_END().getText();
            endIfIdString           = endIfIdString.replaceAll(" ", "");
            int     endIdId         = Integer.valueOf(endIfIdString.substring(2));

            // Controllo che la chiusura sia fatta su una if della vecchia sintassi
            if (scopeIf.getIfId() == null) {

                throw new ParseCancellationException("Errata chiusura di una if %F su " + mainCompositore.getLastRunNodeRead().getText());

            }

            // Controllo che la chiusura venga effettuata sulla if con lo stesso id
            else if (scopeIf.getIfId() != endIdId) {

                throw new ParseCancellationException("Errata chiusura di una if %F (aperta con %F" + scopeIf.getIfId() + ") su " +
                        mainCompositore.getLastRunNodeRead().getText());

            }

            // Chiudo la if togliendola dallo scope-stack
            scopeStack.pop();

            RpaValue newSkipValue = new RpaValue(null);
            newSkipValue.setIsSkip(true);

            return newSkipValue;


        }

        // Altrimenti, lancio un errore
        else {

            throw new ParseCancellationException("Errata chiusura di una if %F su " + mainCompositore.getLastRunNodeRead().getText());

        }

    }

    @Override
    public RpaValue<?> visitElseStatementNew(ElseStatementNewContext context) {

        // Controllo se sono all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.getCountInternalIfToSkip() > 0) {

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

            // Controllo se avevo già trovato un else
            if (scopeIf.isElseFound()) {

                throw new ParseCancellationException("Trovato un secondo else [ELSE]");

            }

            // Decido se i nodi del campo else vanno esclusi o meno
            scopeIf.setIsElseFound(true);
            scopeIf.setIsNodeToSkip(scopeIf.isBooleanConditionTrue());

            RpaValue newSkipValue = new RpaValue(null);
            newSkipValue.setIsSkip(true);

            return newSkipValue;

        }

        // Altrimenti, lancio un errore
        else {

            throw new ParseCancellationException("Errata posizione else [ELSE]");

        }

    }

    @Override
    public RpaValue<?> visitElseStatementOld(ElseStatementOldContext context) {

        // Controllo se sono all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.getCountInternalIfToSkip() > 0) {

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

            // Estraggo l'id dell'else
            int elseIfId = Integer.valueOf(context.POSITIVE_INTEGER().getText());

            // Controllo che la chiusura sia fatta su una if della vecchia sintassi
            if (scopeIf.getIfId() == null) {

                throw new ParseCancellationException("Errata posizione else %E");

            }

            // Controllo che l'id dell'else sia equivalente a quello dell'if
            else if (scopeIf.getIfId() != elseIfId) {

                throw new ParseCancellationException("Errata posizione else %E");

            }

            // Controllo se avevo già trovato un else
            else if (scopeIf.isElseFound()) {

                throw new ParseCancellationException("Trovato un secondo else %E");

            }

            // Decido se i nodi del campo else vanno esclusi o meno
            scopeIf.setIsElseFound(true);
            scopeIf.setIsNodeToSkip(scopeIf.isBooleanConditionTrue());

            RpaValue newSkipValue = new RpaValue(null);
            newSkipValue.setIsSkip(true);

            return newSkipValue;

        }

        // Altrimenti, lancio un errore
        else {

            throw new ParseCancellationException("Errata posizione else %E");

        }

    }

    @Override
    public RpaValue<?> visitElseIfNew(ElseIfNewContext context) {

        // Controllo se sono all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.getCountInternalIfToSkip() > 0) {

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

            // Controllo che l'else-if non stia dopo un else
            if (scopeIf.isElseFound()) {

                throw new ParseCancellationException("Trovato un else-if dopo un else");

            }

            // Se la condizione originale è vera, ignoro le istruzioni che seguono
            if (scopeIf.isBooleanConditionTrue()) {

                scopeIf.setIsNodeToSkip(true);

            }

            // Altrimenti, valuto la condizione dell'else-if
            else {

                // Estraggo la condizione booleana
                RpaValue<Boolean> isElseIfConditionTrueValue   = (RpaValue<Boolean>) visit(context.booleanStatement());
                boolean isElseIfConditionTrue               = isElseIfConditionTrueValue.getValue();

                // Decido se i nodi del campo else-if vanno esclusi o meno (sovrascrivendo l'esito della condizione if)
                scopeIf.setBooleanCondition(isElseIfConditionTrue);
                scopeIf.setIsNodeToSkip(!isElseIfConditionTrue);

            }

            RpaValue newSkipValue = new RpaValue(null);
            newSkipValue.setIsSkip(true);

            return newSkipValue;

        }

        // Altrimenti lancio un errore
        else {

            throw new ParseCancellationException("Errata posizione else-if %%");

        }

    }

    @Override
    public RpaValue<?> visitElseIfOld(ElseIfOldContext context) {

        // Controllo se sono all'interno di una "if"
        if (!scopeStack.empty() && scopeStack.peek() instanceof RpaScopeIf) {

            RpaScopeIf scopeIf = (RpaScopeIf) scopeStack.peek();

            // Se sono un nodo da saltare, esco
            if (scopeIf.getCountInternalIfToSkip() > 0) {

                RpaValue newSkipValue = new RpaValue(null);
                newSkipValue.setIsSkip(true);

                return newSkipValue;

            }

            // Estraggo l'id dell'else-if
            int elseIfId = Integer.valueOf(context.POSITIVE_INTEGER().getText());

            // Controllo che la chiusura sia fatta su una if della vecchia sintassi
            if (scopeIf.getIfId() == null) {

                throw new ParseCancellationException("Errata posizione else-if %%");

            }

            // Controllo che l'id dell'else-if sia equivalente a quello dell'if
            else if (scopeIf.getIfId() != elseIfId) {

                throw new ParseCancellationException("Errata posizione else-if %%");

            }

            // Controllo che l'else-if non stia dopo un else
            else if (scopeIf.isElseFound()) {

                throw new ParseCancellationException("Trovato un else-if dopo un else");

            }

            // Se la condizione originale è vera, ignoro le istruzioni che seguono
            if (scopeIf.isBooleanConditionTrue()) {

                scopeIf.setIsNodeToSkip(true);

            }

            // Altrimenti, valuto la condizione dell'else-if
            else {

                // Estraggo la condizione booleana
                RpaValue<Boolean> isElseIfConditionTrueValue   = (RpaValue<Boolean>) visit(context.booleanStatement());
                boolean isElseIfConditionTrue               = isElseIfConditionTrueValue.getValue();

                // Decido se i nodi del campo else-if vanno esclusi o meno (sovrascrivendo l'esito della condizione if)
                scopeIf.setBooleanCondition(isElseIfConditionTrue);
                scopeIf.setIsNodeToSkip(!isElseIfConditionTrue);

            }

            RpaValue newSkipValue = new RpaValue(null);
            newSkipValue.setIsSkip(true);

            return newSkipValue;

        }

        // Altrimenti lancio un errore
        else {

            throw new ParseCancellationException("Errata posizione else-if %%");

        }

    }

}
