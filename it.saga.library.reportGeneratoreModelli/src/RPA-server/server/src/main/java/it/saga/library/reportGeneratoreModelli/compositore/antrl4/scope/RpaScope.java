package it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;

/**
 * Scope class
 */
public abstract class RpaScope {

	public static int UNDEFINED_SCOPE_TYPE		= -1;
	public static int DOCUMENT_SCOPE_TYPE		= 0;
	public static int IF_SCOPE_TYPE				= 4;
	public static int LOOP_SCOPE_TYPE			= 3;
	public static int INLINE_LOOP_SCOPE_TYPE	= 6;
	public static int JOIN_SCOPE_TYPE 			= 5;

	/*
	 * Variabile booleana che viene settata a true quando l'utente sta visitando lo scopeType
	 */
	private boolean enter;

	/*
	 * Variabile booleana che viene settata a true quando
	 * lo scope fa parte di un ramo annidato che deve essere ignorato
	 */
	private boolean hidden;

	/*
	 * Rappresenta il tipo di scope
	 */
	private int scopeType;

	/**
	 * Contenitore di tutti gli oggetti di utilità
	 */
	protected RpaMainCompositore mainCompositore;

	/**
	 * Indica il nodo visitato. Serve nel caso di loop per ritornare al nodo dove inizia il loop.
	 */
	// private Node forNode;

	/**
	 * Contenitore con tutte le informazioni di un FOR
	 */
	// private ForValue forValue;

	private int nodiDaSkippare = 0;

	public RpaScope(RpaMainCompositore mainCompositore) {

		this.mainCompositore = mainCompositore;

	}

	/*
	public Scope() {
		this.scopeType = 0;
	}
	*/

	protected RpaScope(int scopeType, RpaMainCompositore mainCompositore) {

		this.mainCompositore = mainCompositore;
		this.scopeType = scopeType;

	}

	/*
	 * Metodi get e set
	 */
	public boolean isEnter() {
		return enter;
	}

	public void setEnter(boolean enter) {
		this.enter = enter;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getScopeType() {
		return scopeType;
	}

	/*
	public Scope setScopeType(int scopeType) {
		this.scopeType = scopeType;
		return this;
	}
	*/

	/*
	public Node getForNode() {
		return forNode;
	}

	public void setForNode(Node forNode) {
		this.forNode = forNode;
	}
	*/

	public int getNodiDaSkippare() {
		return nodiDaSkippare;
	}

	public void setNodiDaSkippare(int nodiDaSkippare) {
		this.nodiDaSkippare = nodiDaSkippare;
	}
}
