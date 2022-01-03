package it.saga.library.reportGeneratoreModelli.compositore.compo;

import it.saga.library.gestioneDocumentale.DocWordProcessorException;
import it.saga.library.gestioneDocumentale.javaProcessor.DocAsposeWordProcessor;

public class RpaMain {

    public static void main(String[] args) {

        //init di aspose
        try {
            new DocAsposeWordProcessor();
        } catch (DocWordProcessorException dwpe) {
            dwpe.printStackTrace();
        }

        RpaMainCompositore mainCompositore = new RpaMainCompositore(args);
        mainCompositore.run();

        System.exit(mainCompositore.getExitCode());

    }

}
