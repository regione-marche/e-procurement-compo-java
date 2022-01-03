package it.saga.library.reportGeneratoreModelli.compositore.compo.codes;

import static it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType.Type.CRITICAL;
import static it.saga.library.reportGeneratoreModelli.compositore.compo.codes.RpaErrorType.Type.PRECOMPILE;

public enum RpaErrorType {

    IMPOSSIBLE_SWAP_BOOLEAN_SEPARATOR("P000", PRECOMPILE, "Errore nella sostituzione dei caratteri delle espressioni booleane"),
    INVALID_DECLARATION_INSTRUCTION("P001", PRECOMPILE, "Errore nel riconoscimento delle istruzioni"),
    INVALID_DECLARATION_LOOP("P002", PRECOMPILE, "Errore nel riconoscimento del flusso del loop"),
    MALFORMED_BOOLEAN_CONDITION("P003", PRECOMPILE, "Errore nella dichiarazione della condizione booleana"),
    RTF_NOT_VALID("P004", PRECOMPILE, "RTF non valido"),
    INCLUDEMOD_IMPORT_ERROR("P004", PRECOMPILE, "Errore nell'importazione del documento"),
    SIGNATURE_PARSE_ERROR("P005", PRECOMPILE, "Errore durante il parse del TAG della firma grafometrica"),
    CONVERSION_FAIL_DATA_ELDA("E000", CRITICAL, "Impossibile convertire nel formato DATA_ELDA"),
    CONVERSION_FAIL_TABULATION_INDEX("E001", CRITICAL, "Impossibile acquisire l'indice del tabulato"),
    CONVERSION_FAIL_DATA_D("E002", CRITICAL, "Impossibile convertire la stringa in data"),
    CONVERSION_FAIL_MONEY("E003", CRITICAL, "Impossibile convertire la stringa a data"),
    CONVERSION_FAIL_NUMBER_I("E004", CRITICAL, "Impossibile formattare la stringa a decimale con apici"),
    CONVERSION_FAIL_NUMBER_L("E005", CRITICAL, "Impossibile convertire il numero a parola"),
    NOT_EXIST_VALUE_NUMBER("E006", CRITICAL, "Impossibile avere un valore numerico per il mnemonico / formato"),
    INVALID_FORMAT_LIMITS("E007", CRITICAL, "I limiti della formattazione non sono validi"),
    INVALID_MNEMONIC_INDEX("E008", CRITICAL, "Il mnemonico non può avere un indice"),
    CONVERSION_FAIL_CUSTOM_DATE("E009", CRITICAL, "Impossibile convertire il valore in data"),
    REACH_ENTITY_FAIL("E010", CRITICAL, "Impossibile raggiungere l'entità per il mnemonico inserito"),
    LACK_ENTITIES_BIND("E011", CRITICAL, "Le due entità non sono collegate"),
    JOIN_FAIL("E012", CRITICAL, "Impossibile eseguire la join"),
    NO_LOOP_FOR_BREAK("E013", CRITICAL, "Impossibile trovare un loop da poter terminare con BREAK_LOOP"),
    NO_MNEMONIC_ENTITY_DATA_INDEX_TO_UPDATE("E014", CRITICAL, "Impossibile trovare l'entità associata al loop di cui aggiornare l'indice"),
    INCLUDE_IMPORT_ERROR("E015", CRITICAL, "Errore nell'importazione del documento"),
    CUSTOM_SQL_OPERATION_ERROR("E016", CRITICAL, "Errore nell'esecuzione della query o nel recupero di un dato"),
    NO_MAIN_ELEMENTS_ERROR("E017", CRITICAL, "Nessun elemento trovato per l'entità passata in ingresso"),
    MALFORMED_OPTIONS_DECLARATION("E018", CRITICAL, "La stringa con la dichiarazione dei registri non è definita correttamente"),
    NO_MNEMONIC_ENTITY_FOUND("E019", CRITICAL, "Nessuna entità / tabella trovato per il mnemonico"),
    PRECOMPILE_INCLUDE_ERROR("E020", CRITICAL, "Errore nell'aggiornamento del path della INCLUDE durante la fase di precompilazione (Da '\\' a '''"),
    INVALID_FORMAT("E021", CRITICAL, "Il valore di ingresso è in un formato non corretto"),
    INTERNAL_ERROR("C000", CRITICAL, "Errore interno al compositore"),
    DOCUMENT_NOT_FOUND("C001", CRITICAL, "File non trovato"),
    ENTITY_DECLARATION("C002", CRITICAL, "Errore nella definizione dell'entità"),
    OUT_OF_MEMORY("E022", CRITICAL, "Errore di memoria non controllabile"),
    WRONG_MNEMONIC_INSTANCE("E023", CRITICAL, "I mnemonici sono stati istanziati per un applicativo sbagliato"),
    LOAD_EXTERNAL_IMAGE_ERROR("E024", CRITICAL, "Errore nel caricamento di una immagine"),
    CONVERSION_FAIL_KM_CIPPO("E025", CRITICAL, "Impossibile convertire la stringa al formato chilometraggio"),
    CONVERSION_FAIL_TEXT("E026", CRITICAL, "Impossibile convertire il testo"),
    CONVERSION_LOW_TEXT("E027", CRITICAL, "Impossibile rendere a minusolo le lettere del testo"),
    CONVERSION_CAP_TEXT("E028", CRITICAL, "Impossibile capitalizzare il testo"),
    CONVERSION_CAP_ALL_TEXT("E029", CRITICAL, "Impossibile capitalizzare tutte le parole del testo");

    private String  code;
    private Type    type;
    private String  description;

    private RpaErrorType(String code, Type type, String description) {

        this.code           = code;
        this.type           = type;
        this.description    = description;

    }

    public String getCode() {

        return code;

    }

    public Type getType() {

        return type;

    }

    public String getDescription() {

        return description;

    }

    public String getMessage() {

        return "[" + type.getName() + ":" + code + "]" + description;

    }

    @Override
    public String toString() {

        return getMessage();

    }

    public enum Type {

        CRITICAL("CRITICO"),
        NORMAL("NORMALE"),
        PRECOMPILE("PRECOMPILAZIONE");

        String name;

        private Type(String name) {

            this.name = name;

        }

        public String getName() {

            return name;

        }

    }

}
