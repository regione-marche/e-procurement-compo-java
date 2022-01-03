package it.saga.library.reportGeneratoreModelli.impl;

import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.commonDataTypes.CdtReadCollectionParams;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaLoadExternalImageException;
import it.saga.library.reportGeneratoreModelli.compositore.interfaces.RpaImportExternalImageI;
import it.saga.library.repository.RepDACDocument;
import it.saga.library.repository.RepUtils;

import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaImportExternalImageDocumentale implements RpaImportExternalImageI {

    private static final String CHECK_IS_IMAGE_REGEX = "^.+\\.((jpg)|(JPG)|(png)|(PNG)|(jpeg)|(JPEG)|(gif)|(GIF))$";

    private AutCFGUserSession session;

    public RpaImportExternalImageDocumentale(AutCFGUserSession session) {

        this.session = session;

    }

    @Override
    public byte[] loadImage(RpaMainCompositore mainCompositore, String content) throws RpaLoadExternalImageException {

        try {

            // Leggo il pkid
            Long pkid = Long.valueOf(content);

            // Recupero il documento associato al pkid
            String documentDetailHQLString = "" +
                    "SELECT documentDetail.repDocument " +
                    "FROM   DocDACCollegatiDetails documentDetail " +
                    "WHERE  documentDetail.pkid = ? ";
            CdtReadCollectionParams<RepDACDocument> readCollectionParams = new CdtReadCollectionParams<RepDACDocument>(
                    documentDetailHQLString, pkid
            );
            RepDACDocument repDoc = RepUtils.getBLG().readFirstElement(session, readCollectionParams);

            if (repDoc == null) {

                return null;

            }

            if (repDoc.getContent(session) == null)  {

                repDoc = RepUtils.getBLG().docExtract(session, repDoc);

            }

            // Controllo che il documento sia un'immagine
            String  documentName                = repDoc.getDocumentName();
            Matcher checkDocumentIsImageMatcher = Pattern.compile(CHECK_IS_IMAGE_REGEX).matcher(documentName);

            if (checkDocumentIsImageMatcher.find()) {

                // Estraggo e restituisco il byteCode del documento
                byte[] byteCode = repDoc.getContent(session);
                return byteCode;

            }

            return null;

        } catch (SagaException exception) {

            exception.printStackTrace();
            String message = "Errore di lettura del documentale";

            throw new RpaLoadExternalImageException(mainCompositore.getComposerConfiguration(), message, exception);

        } catch (RemoteException exception) {

            String message = "Errore di accesso alle risorse remote";

            throw new RpaLoadExternalImageException(mainCompositore.getComposerConfiguration(), message, exception);

        } catch (Exception exception) {

            String message = "Errore generico durante il recupero del documento";

            throw new RpaLoadExternalImageException(mainCompositore.getComposerConfiguration(), message, exception);

        }

    }

}
