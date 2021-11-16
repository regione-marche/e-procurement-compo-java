# e-procurement-compo-java
Il compositore documenti (compo-java) è uno strumento che, partendo da un modello contenente istruzioni particolari, permette di ottenere un documento finito, con riportati automaticamente i dati prelevati dal database.
E' in grado di accedere alla base dati ed estrarre le informazioni mediante appisiti tag inseriti nel modello, denominati "mnemonici" dei campi, ma è anche possibile eseguire istruzioni SQL di tipo "select" per estrarre i dati con la massima flessibilità.
Il compositore interpreta il codice di programmazione inserito nel modello, sulla base di una propria sintassi (documentata) che comprende variabili, vettori, istruzioni if, endif, else, quindi cicli for e while, ecc.
Potendo inserire la sintassi in un modello di documento (docx) è possibile anche sfuttare tutta la potenza di formattazione del word processor per ottenere poi documenti anche esteticamente gradevoli.

Principali funzionalità:
- Composizione automatica di modelli di documento
- Acquisizione dati dal database mediante tag
- Estrazione dati mediante script SQL
- Interprete comandi (variabili, if, for, ecc.)
- Supporto della formattazione del modello 
- Supporto formato rtf, docx

## Librerie commerciali
compo-java utilizza le seguenti librerie commerciali per le quali occorre dotarsi, in sviluppo, di opportuna licenza:

* **Aspose cells, words e pdf.**

La licenza di queste libreria va inserita in questa classe: it.saga.library.documentiCollegati\src\DOC-client\client\src\main\java\it\saga\library\gestioneDocumentale\javaProcessor\DocAsposeSetup.java

Queste librerie non prevedono una chiave di attivazione.
