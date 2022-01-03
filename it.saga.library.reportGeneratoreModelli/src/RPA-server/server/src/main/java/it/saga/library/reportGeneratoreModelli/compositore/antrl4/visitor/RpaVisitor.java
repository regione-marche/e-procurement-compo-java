package it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.ParseTree;
import it.saga.extern.rpa_libs.antlr.v4.runtime.tree.TerminalNode;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaGenericLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaImageBytesValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaImageValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaIncludeFileValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaLoopValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaNextValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.RpaValue;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeGenericLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeIf;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeInlineLoop;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeLoop;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.style.RpaStyleManager;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDebugMessages;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaVariablesManager;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.CodeContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.CommentContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ElseIfContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.ElseStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.GenericTextContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfEndContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.IfStatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.InlineLoopEndContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.InstructionContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LoopContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.LoopEndContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.StatementContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParser.TransparentContext;
import it.saga.library.reportGeneratoreModelli.compositore.generatedGrammarFiles.RpaParserBaseVisitor;

import java.sql.Connection;
import java.util.Stack;

public abstract class RpaVisitor extends RpaParserBaseVisitor<RpaValue<?>> {

    private static final String COMPO_START_TAG_REGEX   = "\\<\\?compo ";
    private static final String COMPO_END_TAG_REGEX     = " \\?\\>";

    protected final Connection      conn;
    protected Stack<RpaScope>       scopeStack;
    protected RpaMainCompositore    mainCompositore;
    protected RpaDebugMessages      debugMessages;
    protected RpaVariablesManager   variablesManager;
    protected RpaStyleManager       styleManager;
    protected Node                  parentNode;
    protected Run                   childNode;

    boolean isCodeContainsEmptyCharacters = false;

    public RpaVisitor(Connection conn, Stack<RpaScope> scopeStack, RpaMainCompositore mainCompositore, final Node parentNode, Run childNode) {

        this.conn               = conn;
        this.scopeStack         = scopeStack;
        this.mainCompositore    = mainCompositore;
        this.variablesManager   = mainCompositore.getVariablesManager();
        this.styleManager       = mainCompositore.getStyleManager();
        this.parentNode         = parentNode;
        this.childNode          = childNode;
        this.debugMessages      = mainCompositore.getDebugMessages();

    }

    @Override
    public RpaValue<?> visitTerminal(TerminalNode node) {
        return RpaValue.SKIP;
    }

    @Override
    public RpaValue<?> visitCode(CodeContext context) {

        if (context.children != null && context.children.size() > 0) {

            StringBuffer    stringBuffer    = new StringBuffer("");
            boolean         isSkip          = true;

            String  contextString           = context.getText();
            boolean isExtendCarriageReturn  = false;

            isCodeContainsEmptyCharacters = contextString.equals("\n") || contextString.matches("^[ |\t]*$");

            for (int index = 0; index < context.children.size(); index++) {

                RpaValue value = visit(context.children.get(index));

                if (value != null && value.isExtendCarriageReturn()) {

                    isExtendCarriageReturn = true;

                }

                boolean isSkipNode      = (value == null)? true : value.isSkip();
                String  childrenText    = context.children.get(index).getText();

                if (value == null) {

                    continue;

                }

                if (value instanceof RpaLoopValue) {

                    return value;

                }

                else if (value instanceof RpaNextValue) {

                    return value;

                }

                else if (value instanceof RpaImageValue) {

                    return value;

                }

                else if (value instanceof RpaIncludeFileValue) {

                    return value;

                }

                else if (value instanceof RpaImageBytesValue) {

                    return value;

                }

                if (value.isSkip()) {

                    continue;

                }

                isSkip = false;

                if (value.getValue() == null) {

                    stringBuffer.append("");

                } else {

                    stringBuffer.append(value.getValue());

                }

            }

            RpaValue returnValue = new RpaValue<Object>(stringBuffer.toString());
            returnValue.setIsSkip(isSkip);
            returnValue.setExtendCarriageReturn(isExtendCarriageReturn);

            return returnValue;

        } else {

            return super.visitCode(context);

        }

    }

    @Override
    public RpaValue<String> visitGenericText(GenericTextContext context) {

        // Controllo se devo saltare il nodo (check if o for)
        RpaValue checkIfSkipNodeValue          = checkIfSkipNode(context);
        RpaValue checkLoopSkipNodeValue        = checkLoopSkipNode(context);
        RpaValue checkInlineLoopSkipNodeValue  = checkInlineLoopSkipNode(context);

        if (checkIfSkipNodeValue != null) {

            return checkIfSkipNodeValue;

        } else if (checkLoopSkipNodeValue != null) {

            return checkLoopSkipNodeValue;

        } else if (checkInlineLoopSkipNodeValue != null) {

            // TODO: All'interno della funzione "checkInlineLoopSkipNodeValue(...)"
            // TODO: salvare il contesto/contenuto di questo testo
            return checkInlineLoopSkipNodeValue;

        }

        RpaValue<String> genericTextValue;
        String          genericText         = context.getText();

        // boolean isContainEmptyCharacters = genericText.equals("\n") || genericText.matches("^[ |\t]*$");

        // if (genericText == null || genericText.isEmpty() || isContainEmptyCharacters) {
        if (genericText == null || genericText.isEmpty() || isCodeContainsEmptyCharacters) {

            genericTextValue = new RpaValue<String>(null);

            genericTextValue.setIsSkip(true);

        } else {

            genericTextValue = new RpaValue<String>(genericText);

        }

        return genericTextValue;

    }

    @Override
    public RpaValue<?> visitInstruction(InstructionContext context) {

        String instructionText = context.getText();

        // Controllo se ho un commento
        if (context.comment() != null) {

            // Restituisco null (visitComment restituisce null)
            return visit(context.comment());

        }

        else {

            // Restituisco il valore dell'unica istruzione definita internamente
            return visit(context.statement());

        }

    }

    @Override
    public RpaValue<?> visitTransparent(TransparentContext context) {

        // Eseguo tutte le istruzioni del trasparente (se ce ne sono)
        RpaValue lastChildValue = null;

        for (ParseTree child : context.children) {

            if (child instanceof StatementContext) {
                lastChildValue = visit(child);
            }

        }

        // Restituisco il valore che segnala l'inizio o la fine di un "loop" o di una "if"
        if (lastChildValue instanceof RpaNextValue || lastChildValue instanceof RpaLoopValue) {

            return lastChildValue;

        }

        // Restituisco un null (devo evitare di stampare il testo del trasparente)
        return null;

    }

    @Override
    public RpaValue<?> visitStatement(StatementContext context) {

        RpaValue checkIfSkipNodeValue          = checkIfSkipNode(context);
        RpaValue checkLoopSkipNodeValue        = checkLoopSkipNode(context);
        RpaValue checkInlineLoopSkipNodeValue  = checkInlineLoopSkipNode(context);

        if (checkIfSkipNodeValue != null) {

            return checkIfSkipNodeValue;

        } else if (checkLoopSkipNodeValue != null) {

            return checkLoopSkipNodeValue;

        } else if (checkInlineLoopSkipNodeValue != null) {

            return checkInlineLoopSkipNodeValue;

        }

        // Restituisco il valore dell'operazione
        return visit(context.getChild(0));

    }

    @Override
    public RpaValue<String> visitComment(CommentContext context) {

        // Controllare se devo saltare l'istruzione
        RpaValue checkIfSkipNodeValue          = checkIfSkipNode(context);
        RpaValue checkLoopSkipNodeValue        = checkLoopSkipNode(context);

        if (checkIfSkipNodeValue != null) {

            return checkIfSkipNodeValue;

        } else if (checkLoopSkipNodeValue != null) {

            return checkLoopSkipNodeValue;

        }

        // Riporto il testo contenuto
        String comment = context.getText();
        comment = comment.replaceAll(COMPO_START_TAG_REGEX, "");
        comment = comment.replaceAll(COMPO_END_TAG_REGEX, "");

        return new RpaValue<String>(comment);

    }

    private RpaValue<?> checkIfSkipNode(StatementContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in un loop in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    return null;

                }

            }

            // Controllo per verificare il salto dell'istruzione all'if (ifCheck)
            else if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    ParseTree statement = context.getChild(0);

                    boolean isStatementIf = statement instanceof IfStatementContext;
                    boolean isStatementElse = statement instanceof ElseStatementContext;
                    boolean isStatementElseIf = statement instanceof ElseIfContext;
                    boolean isStatementEndIf = statement instanceof IfEndContext;

                    // Se ho una "if" o "else" o "else if" o "end if" come prossima istruzione, la eseguo
                    if (isStatementIf || isStatementElse || isStatementElseIf || isStatementEndIf) {

                        return visit(context.getChild(0));

                    }

                    // Negli altri casi, ritorno un "Value" per saltare questo nodo
                    RpaValue newSkipValue = new RpaValue(null);
                    newSkipValue.setIsSkip(true);

                    return newSkipValue;

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkIfSkipNode(GenericTextContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in un loop in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    return null;

                }

            } else if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    // Ritorno un "Value" per saltare questo nodo
                    RpaValue newSkipValue = new RpaValue(null);
                    newSkipValue.setIsSkip(true);

                    return newSkipValue;

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkIfSkipNode(CommentContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in un loop in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    return null;

                }

            } else if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    // Ritorno un "Value" per saltare questo nodo
                    RpaValue newSkipValue = new RpaValue(null);
                    newSkipValue.setIsSkip(true);

                    return newSkipValue;

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkLoopSkipNode(StatementContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in una if in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    return null;

                }

            } else if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    ParseTree statement = context.getChild(0);

                    boolean isStatementLoopStart    = statement instanceof LoopContext;
                    boolean isStatementLoopEnd      = statement instanceof LoopEndContext;

                    // Se ho un next-loop, lo eseguo
                    if (isStatementLoopStart || isStatementLoopEnd) {

                        return visit(context.getChild(0));

                    }

                    // Ritorno un "Value" per saltare questo nodo
                    RpaValue newSkipValue = new RpaValue(null);
                    newSkipValue.setIsSkip(true);

                    return newSkipValue;

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkLoopSkipNode(GenericTextContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in una if in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    return null;

                }

            } else if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    // Ritorno un "Value" per saltare questo nodo
                    RpaValue newSkipValue = new RpaValue(null);
                    newSkipValue.setIsSkip(true);

                    return newSkipValue;

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkLoopSkipNode(CommentContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in una if in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    return null;

                }

            } else if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    // Ritorno un "Value" per saltare questo nodo
                    RpaValue newSkipValue = new RpaValue(null);
                    newSkipValue.setIsSkip(true);

                    return newSkipValue;

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkInlineLoopSkipNode(StatementContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in una if in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    return null;

                }

            }

            // Controllo se sono in un loop in cui bisogna saltare il nodo
            else if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    return null;

                }

            }

            // Controllo se sono in un inline-loop
            else if (scope instanceof RpaScopeInlineLoop) {

                RpaScopeInlineLoop scopeInlineLoop = (RpaScopeInlineLoop) scope;

                // Se l'inline-loop è "attivo" eseguo l'istruzione
                if (scopeInlineLoop.isActive()) {

                    return visit(context.getChild(0));

                }

                // Se l'inline-loop non è "attivo" salvo il contesto dell'istruzione o eseguo la fine del loop
                else {

                    ParseTree instructionContext = context.getChild(0);

                    // Controllo se l'istruzione è quella di fine dell'inline-loop
                    if (instructionContext instanceof InlineLoopEndContext) {

                        return visit(instructionContext);

                    }

                    // Altrimenti, salvo l'istruzione
                    else {

                        String parentContextText = context.getParent().getText();
                        scopeInlineLoop.addCode(parentContextText);

                        RpaValue newValue = new RpaValue(null);
                        newValue.setIsSkip(true);

                        return newValue;

                    }

                }

            }

        }

        return null;

    }

    private RpaValue<?> checkInlineLoopSkipNode(GenericTextContext context) {

        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);

            // Controllo se sono in una if in cui bisogna saltare il nodo
            if (scope instanceof RpaScopeIf) {

                RpaScopeIf scopeIf = (RpaScopeIf) scope;

                if (scopeIf.isSkipNode()) {

                    return null;

                }

            }

            // Controllo se sono in un loop in cui bisogna saltare il nodo
            else if (scope instanceof RpaScopeLoop) {

                RpaScopeLoop scopeLoop = (RpaScopeLoop) scope;

                if (scopeLoop.isSkipNode()) {

                    return null;

                }

            }

            // Controllo se sono in un inline-loop
            else if (scope instanceof RpaScopeInlineLoop) {

                RpaScopeInlineLoop scopeInlineLoop = (RpaScopeInlineLoop) scope;

                // Se l'inline-loop è "attivo" eseguo l'istruzione
                if (scopeInlineLoop.isActive()) {

                    return new RpaValue(context.getText());

                }

                // Se l'inline-loop non è "attivo" salvo il testo
                else {

                    String genericText = context.getText();
                    scopeInlineLoop.addCode(genericText);

                    RpaValue newValue = new RpaValue(null);
                    newValue.setIsSkip(true);

                    return newValue;

                }

            }

        }

        return null;

    }

    /**
     * Restituisce l'indice del loop più interno dello stack degli scope.
     * Restituisce null se non ce nessun loop
     * @param scopeStack
     * @return
     */
    public static Integer getInternalLoopIndex(Stack<RpaScope> scopeStack) {

        Integer loopIndex = null;
        for (int scopeIndex = scopeStack.size() - 1; scopeIndex >= 0; scopeIndex --) {

            RpaScope scope = scopeStack.get(scopeIndex);
            if (scope.getScopeType() == RpaScope.LOOP_SCOPE_TYPE || scope.getScopeType() == RpaScope.INLINE_LOOP_SCOPE_TYPE) {

                RpaGenericLoopValue loopValue = ((RpaScopeGenericLoop) scope).getLoopValue();
                loopIndex = loopValue.getCurrentValue();

                break;

            }

        }

        return loopIndex;

    }

/*	@Override
	public Value<?> visitModello(CompoParser.ModelloContext ctx) {
		if (ctx.children != null && ctx.children.size() > 0) {
			StringBuffer ret = new StringBuffer("");
			boolean skip = true;
			for (int ind = 0; ind < ctx.children.size(); ind++) {
				Value v = visit(ctx.children.get(ind));
				if (v == null) {
					continue;
				}
				if (v instanceof ForValue) {
					return v;
				} else if (v instanceof NextValue) {
					return v;
				}
				if (v.isSkip()) {
					continue;
				}
				skip = false;
				if (v.getValue() == null) {
					ret.append("");
				} else {
					ret.append(v.getValue());
				}
			}
			Value retValue = new Value<Object>(ret.toString());
			retValue.setSkip(skip);
			return retValue;
		} else {
			return super.visitModello(ctx);
		}
	}*/

/*	@Override
	public Value<?> visitComplex(CompoParser.ComplexContext ctx) {
		boolean prosegui = checkIfScope();
		if (!prosegui || scope.lastElement().isHidden()) {
			return Value.SKIP;
		}

		if (ctx.expression() instanceof CompoParser.MnemoContext) {
			return visit(ctx.expression());
		}
		return super.visitComplex(ctx);
	}*/

/*	@Override
	public Value<?> visitEndtext(CompoParser.EndtextContext ctx) {
		// Il plug-in dovrebbe aver eliminato questa dichiarazione, c'è sicuramente un errore!
		LOG.error(new CompoException("Errore nella sitantassi, il richiamo a fine testo " + ctx.getText() + " non è dichiarato " + //
				"correttamente o il file non è un tipo RTF valido", new Exception()));
		return Value.SKIP;
	}*/

/*	protected boolean checkIfScope() {
		boolean prosegui = false;
		// if (scope.lastElement().getScopeType() == Scope.IF_SCOPE_TYPE && scope.lastElement().isEnter()) {
		if (scope.lastElement().getScopeType() == Scope.IF_THAN_SCOPE_TYPE && scope.lastElement().isEnter()) {
			prosegui = true;
		// } else if (scope.lastElement().getScopeType() == Scope.IF_SCOPE_TYPE && !scope.lastElement().isEnter()) {
		} else if (scope.lastElement().getScopeType() == Scope.IF_THAN_SCOPE_TYPE && !scope.lastElement().isEnter()) {
			prosegui = false;
		// } else if (scope.lastElement().getScopeType() == Scope.ELSE_SCOPE_TYPE && scope.lastElement().isEnter()) {
		} else if (scope.lastElement().getScopeType() == Scope.IF_ELSE_SCOPE_TYPE && scope.lastElement().isEnter()) {
			prosegui = true;
		// } else if (scope.lastElement().getScopeType() == Scope.ELSE_SCOPE_TYPE && !scope.lastElement().isEnter()) {
		} else if (scope.lastElement().getScopeType() == Scope.IF_ELSE_SCOPE_TYPE && !scope.lastElement().isEnter()) {
			prosegui = false;
		} else {
			prosegui = true;
		}
		return prosegui;
	}*/
}
