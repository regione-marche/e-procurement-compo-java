package it.saga.library.reportGeneratoreModelli.compositore.compo.codes;

public enum RpaWarningType {

    DIFFERENT_TYPES_COMPARISON("W000", "Confronto tra tipi diversi"),
    JOIN_ON_NULL("W001", "[JOIN] eseguita su un mnemonico null"),
    REACH_READ_LIMIT("W002", "Superato il limite di lettura dati"),
    WRONG_SQL_QUERY("W003", "La query è sbagliata"),
    INSERT_UPDATE_SQL_QUERY("W004", "La funzione di modifica e inserimento dati su Database non è più supportata!");

    private String code;
    private String description;

    private RpaWarningType(String code, String description) {

        this.code           = code;
        this.description    = description;

    }

    public String getCode() {

        return code;

    }

    public String getDescription() {

        return description;

    }

    public String getMessage() {

        return "[" + code + "]" + description;

    }


    @Override
    public String toString() {

        return getMessage();

    }

}
