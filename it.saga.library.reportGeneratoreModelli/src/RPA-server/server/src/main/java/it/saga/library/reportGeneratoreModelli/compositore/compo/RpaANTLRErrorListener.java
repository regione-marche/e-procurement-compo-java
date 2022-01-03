package it.saga.library.reportGeneratoreModelli.compositore.compo;

import com.aspose.words.Run;
import it.saga.extern.rpa_libs.antlr.v4.runtime.BaseErrorListener;
import it.saga.extern.rpa_libs.antlr.v4.runtime.RecognitionException;
import it.saga.extern.rpa_libs.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaANTLRErrorListener extends BaseErrorListener {

    private static final String TAG_SEARCH_REGEX = "<\\?compo | \\?>";

    private RpaMainCompositore  mainCompositore;
    private List<String>        syntaxErrorList;

    public RpaANTLRErrorListener(RpaMainCompositore mainCompositore) {

        this.mainCompositore = mainCompositore;
        this.syntaxErrorList = new ArrayList<String>();

    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

        // Recupero l'ultimo nodo "Run" letto
        Run runNode                     = mainCompositore.getLastRunNodeRead();
        String textCode                 = runNode.getText();
        int updatedCharPositionInLine   = charPositionInLine;

        // Tolgo (se ci sono) i tag che contengono l'istruzione, aggiornando la relativa posizione dell'errore
        Matcher tagSearchMatcher = Pattern.compile(TAG_SEARCH_REGEX).matcher(textCode);

        while (tagSearchMatcher.find()) {

            if (charPositionInLine > tagSearchMatcher.end()) {

                updatedCharPositionInLine -= tagSearchMatcher.group().length();

            }

        }

        textCode = textCode.replaceAll(TAG_SEARCH_REGEX, "");

        // Costruisco l'errore di sintassi
        String syntaxErrorMessage = "";

        syntaxErrorMessage =  "[p:" + updatedCharPositionInLine + "] errore di sintassi per \n";
        syntaxErrorMessage += textCode;

        // Aggiungo alla lista il messaggio di errore
        syntaxErrorList.add(syntaxErrorMessage);

    }

    public List<String> getSyntaxErrorList() {
        return syntaxErrorList;
    }

}
