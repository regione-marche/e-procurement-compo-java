package it.saga.library.reportGeneratoreModelli.compositore.compo.precompile;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.RpaDebugMessages;

import java.io.IOException;

abstract class RpaPrecompiler implements RpaICompoPlugin {

	protected RpaMainCompositore	mainCompositore;
	protected RpaDebugMessages		debugMessages;

	public RpaPrecompiler(RpaMainCompositore mainCompositore) {

		this.mainCompositore	= mainCompositore;
		this.debugMessages		= mainCompositore.getDebugMessages();

	}

	@Override
	public abstract void run() throws IOException;

}
