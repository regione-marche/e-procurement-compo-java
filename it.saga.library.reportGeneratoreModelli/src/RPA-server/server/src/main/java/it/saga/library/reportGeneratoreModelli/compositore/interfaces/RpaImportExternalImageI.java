package it.saga.library.reportGeneratoreModelli.compositore.interfaces;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaLoadExternalImageException;

public interface RpaImportExternalImageI {

    byte[] loadImage(RpaMainCompositore mainCompositore, String content) throws RpaLoadExternalImageException;

}
