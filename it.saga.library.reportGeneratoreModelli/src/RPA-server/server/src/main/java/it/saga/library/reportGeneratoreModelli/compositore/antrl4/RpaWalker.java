package it.saga.library.reportGeneratoreModelli.compositore.antrl4;

import com.aspose.words.Node;
import com.aspose.words.Run;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.visitor.RpaAdvancedVisitor;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

import java.sql.Connection;
import java.util.Stack;

/**
 * Classe che eredita da tutti i visitor i metodi per gestire la sintassi.
 *
 * Unico entry point per visitare AST
 */
public class RpaWalker extends RpaAdvancedVisitor {

	public RpaWalker(Connection conn, Stack<RpaScope> scope, RpaMainCompositore mainCompositore, Node parentNode, Run childNode) {
		super(conn, scope, mainCompositore, parentNode, childNode);
	}

}