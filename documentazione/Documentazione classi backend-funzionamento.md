# Documentazione Backend-Funzionamento - Studio Medico GCM

Questa documentazione descrive in modo esaustivo la logica di business e gli oggetti modello del backend dello Studio Medico GCM. Il sistema è progettato per gestire personale medico, amministrativo, tecnico e pazienti, garantendo l'integrità dei dati tramite un sistema di persistenza cifrata.

## Note Generali di Implementazione

* **Visibilità**: Tutti gli attributi sono da considerarsi `private`, se non diversamente specificato.
* **Costruttori**: Ogni classe dispone di un costruttore vuoto di default per garantire la compatibilità con la serializzazione e la creazione di oggetti vuoti, a meno che non sia indicato diversamente.
* **Validazione Dati**: **ATTENZIONE**: Tutti i dati devono essere validati (formato, lunghezza, obbligatorietà) tramite lo strato di validazione preposto prima di essere passati ai metodi delle classi qui documentate, se non specificato diversamente.

## Protocollo di Sincronizzazione (Controller)

Il backend adotta un approccio "stateless" per quanto riguarda il salvataggio automatico. È responsabilità del **Controller** (Frontend) gestire la persistenza dopo ogni modifica.

> **Esempio di Flusso Operativo (Doppia Scrittura):**
> 1. Recupero degli oggetti necessari tramite `LetturaFile`.
> 2. Esecuzione del metodo sulla prima classe (es. `paziente.aggiungiVisitaPrenotata(...)`).
> 3. Esecuzione del metodo sulla seconda classe correlata (es. `medico.aggiungiAppuntamentoVisita(...)`).
> 4. Invocazione obbligatoria di `ScritturaFile.scriviFileCifrato(...)` per **entrambi** gli oggetti per garantire la coerenza dell'agenda e della cartella clinica.

---

## Classe: Amministratore.java

**Eredità**: Estende la classe `Persona`
**Interfacce**: Implementa `Serializable`

### Attributi
* `private String titoloStudio`: Rappresenta il livello di istruzione e la specifica del titolo conseguito dall'amministratore.

### Funzionamento
La classe funge da modello per la creazione e la gestione degli oggetti di tipo Amministratore. Oltre ai dati anagrafici ereditati da `Persona.java`, gestisce le informazioni relative al percorso di studi. È progettata per essere serializzata e salvata in file cifrati con prefisso CUI "A".

### Metodi

#### `public Amministratore()`
* **Descrizione**: Costruttore vuoto di default. Necessario per la deserializzazione e per la creazione di istanze inizialmente prive di dati.

#### `public String getTitoloStudio()`
* **Descrizione**: Restituisce il titolo di studio dell'amministratore.
* **Return**: `String` contenente il titolo (es. "Laurea 3 anni - Informatica").

#### `public void setTitoloStudio(String titoloStudio)`
* **Descrizione**: Imposta il titolo di studio.
* **Parametri**: `String titoloStudio` (già validata).
* **Nota per il Controller**: Questo dato deve essere popolato nella GUI tramite un box a scelta multipla per il livello (es. Laurea) combinato con un campo di testo libero per la specifica (es. Informatica).

#### `public boolean equals(Object o)`
* **Descrizione**: Verifica l'uguaglianza tra due oggetti `Amministratore`.
* **Logica**: Confronta gli attributi della classe base (`Persona`) e l'attributo `titoloStudio`. Fondamentale per la logica di confronto del sistema di recovery.

#### `public int hashCode()`
* **Descrizione**: Genera l'hash univoco dell'oggetto.
* **Logica**: Basato sugli attributi ereditati e sul `titoloStudio`.

---

## Classe: Esame.java

**Interfacce**: Implementa `Serializable`

### Attributi
* `private String CUImedico`: CUI del medico che ha effettuato l'esame o che lo effettuerà (in caso di prenotazione).
* `private LocalDateTime dataOraEsame`: Data e ora dell'esecuzione dell'esame o della prenotazione.
* `private String risultato`: Esito dell'esame (presente solo per esami già effettuati).
* `private String nomeEsame`: Denominazione dell'esame clinico.
* `private String note`: Eventuali osservazioni aggiuntive.
* `private String CUIesame`: Codice Univoco Identificativo dell'esame. Utilizza il prefisso **"E"** per esami effettuati e **"EP"** per esami prenotati.

### Funzionamento
La classe funge da modello per la gestione delle prestazioni diagnostiche del paziente. Gestisce sia lo stato di "prenotato" che quello di "effettuato" attraverso costruttori specifici e logiche di generazione del CUI differenziate.

### Metodi

#### `public Esame(String CUImedico, LocalDateTime dataOraEsame, String risultato, String nomeEsame, String note, int numEsami)`
* **Descrizione**: Costruttore specifico per **esami effettuati**.
* **Logica**: Inizializza tutti i campi e genera un CUI con prefisso **"E"** incrementando l'indice fornito.

#### `public Esame(String CUImedico, LocalDateTime dataOraEsame, String nomeEsame, int numEsamiPrenotati)`
* **Descrizione**: Costruttore specifico per **esami prenotati**.
* **Logica**: Inizializza i campi necessari alla prenotazione e genera un CUI con prefisso **"EP"** incrementando l'indice fornito.

#### `public void setCUIesamePrenotato(int numEsamiPrenotati)`
* **Descrizione**: Genera e imposta il CUI per una prenotazione.
* **Logica**: Concatena la stringa "EP" con il valore `numEsamiPrenotati + 1`.

#### `public void setCUIesame(int numEsami)`
* **Descrizione**: Genera e imposta il CUI per un esame effettuato.
* **Logica**: Concatena la stringa "E" con il valore `numEsami + 1`.

#### `public String getCUImedico()` / `public void setCUImedico(String CUImedico)`
* **Descrizione**: Getter e setter per il CUI del medico associato.

#### `public LocalDateTime getDataOraEsame()` / `public void setDataOraEsame(LocalDateTime dataOraEsame)`
* **Descrizione**: Getter e setter per il timestamp dell'esame.

#### `public String getRisultato()` / `public void setRisultato(String risultato)`
* **Descrizione**: Getter e setter per l'esito diagnostico.

#### `public String getNomeEsame()` / `public void setNomeEsame(String nomeEsame)`
* **Descrizione**: Getter e setter per il nome della prestazione.

#### `public String getNote()` / `public void setNote(String note)`
* **Descrizione**: Getter e setter per le note cliniche.

#### `public String getCUIesame()`
* **Descrizione**: Restituisce il codice identificativo dell'esame (formato E... o EP...).

#### `public boolean equals(Object o)` / `public int hashCode()`
* **Descrizione**: Metodi standard per il confronto e la generazione dell'hash dell'oggetto, basati su tutti i campi della classe.

---

## Classe: IT.java

**Eredità**: Estende la classe `Persona`
**Interfacce**: Implementa `Serializable`

### Attributi
* `private String gradoReparto`: Definisce il livello di autorizzazione all'interno del sistema (es. "direttore_IT" o "impiegato").
* `private String titoloStudio`: Rappresenta il percorso accademico o professionale del tecnico.

### Funzionamento
La classe definisce il profilo del supporto tecnico (IT Support). Oltre ai dati anagrafici ereditati da `Persona.java`, gestisce attributi specifici per la gerarchia di accesso. Il `gradoReparto` è fondamentale per la logica dei permessi: un "direttore_IT" ha facoltà di eseguire operazioni critiche come la Recovery o la gestione di altri account IT, mentre un "impiegato" ha permessi limitati. Gli oggetti di questa classe vengono salvati in file `.dat` cifrati.

### Metodi

#### `public IT()`
* **Descrizione**: Costruttore vuoto di default. Utilizzato per la creazione di istanze da popolare tramite setter o per i processi di deserializzazione.

#### `public IT(String CUI, String nome, String cognome, LocalDate dataNascita, int eta, String codiceFiscale, String titoloStudio, String gradoReparto)`
* **Descrizione**: Costruttore parametrizzato completo.
* **Logica**: Utilizza `super` per inizializzare i dati della classe `Persona`. Viene utilizzato principalmente per la creazione del primo utente IT di default durante l'inizializzazione del sistema (`InitFileSystem`).

#### `public boolean equals(Object o)`
* **Descrizione**: Verifica l'uguaglianza tra due oggetti `IT`.
* **Logica**: Utilizza `super.equals(o)` per validare i dati comuni (CUI, nome, ecc.) e confronta poi specificamente `gradoReparto` e `titoloStudio`. Fondamentale per le operazioni di confronto nel sistema di riparazione file (Healing).

#### `public int hashCode()`
* **Descrizione**: Genera l'hash univoco dell'oggetto basandosi sui campi ereditati e su quelli specifici della classe.

#### `public String getGradoReparto()` / `public void setGradoReparto(String gradoReparto)`
* **Descrizione**: Getter e setter per il grado gerarchico.
* **Nota per il Controller**: Il valore deve essere impostato tramite una scelta guidata nella GUI (box con scelte predefinite) per garantire la coerenza con i controlli sui permessi del backend.

#### `public String getTitoloStudio()` / `public void setTitoloStudio(String titoloStudio)`
* **Descrizione**: Getter e setter per il titolo di studio.
* **Nota per il Controller**: Nella GUI, questo campo viene popolato tramite una combinazione di box a scelta multipla (per il livello) e testo libero (per la specifica dell'indirizzo di studi, es. "Informatica").

---

## Classe: Log.java

**Interfacce**: Implementa `Serializable`

### Attributi
* `private LocalDateTime dataOra`: Timestamp preciso dell'avvenuta operazione.
* `private String autore`: CUI dell'utente che ha compiuto l'azione.
* `private String operazione`: Descrizione testuale dell'attività svolta (es. "Creazione Paziente P10").

### Funzionamento
La classe funge da modello per la registrazione delle attività di sistema (audit log). È progettata per gestire uno storico centralizzato in un file cifrato. La classe include una logica di auto-pulizia per mantenere le prestazioni ottimali, limitando il numero massimo di voci salvate. I log vengono inseriti in modalità "LIFO" (Last In, First Out), rendendo i più recenti immediatamente visibili in cima alla lista.

### Metodi

#### `public Log(LocalDateTime dataOra, String autore, String operazione)`
* **Descrizione**: Costruttore parametrizzato per la creazione di un'istanza di log.
* **Logica**: Inizializza i dati dell'operazione prima che questa venga passata al metodo di persistenza.

#### `public static synchronized void aggiungiLog(Log nuovoLog) throws IOException`
* **Descrizione**: Metodo statico per l'aggiunta di un log al file di sistema.
* **Logica**: 
    1. Legge la lista esistente dal file definito in `ConfigFile.LOG_FILE`.
    2. Aggiunge il `nuovoLog` all'indice `0` dell'ArrayList (in cima).
    3. Controlla la dimensione della lista: se supera i **50.000** elementi, rimuove l'ultimo (il più vecchio).
    4. Sovrascrive il file originale. Grazie alla logica interna di `ScritturaFile`, l'aggiornamento avviene contemporaneamente sia sul file principale che sul file di backup.
* **Sincronizzazione**: L'uso della keyword `synchronized` previene le "race condition", impedendo a più thread di scrivere contemporaneamente sul file dei log e corrompere i dati.

#### `public LocalDateTime getDataOra()` / `public String getAutore()` / `public String getOperazione()`
* **Descrizione**: Metodi getter per recuperare i singoli dati del log (utilizzati tipicamente per popolare tabelle nella GUI).

#### `public boolean equals(Object o)` / `public int hashCode()`
* **Descrizione**: Metodi standard per il confronto basati su timestamp, autore e tipo di operazione.

---

## Classe: Medico.java

**Eredità**: Estende la classe `Persona`
**Interfacce**: Implementa `Serializable`

### Attributi
* `private String specializzazione`: Branca medica di competenza del professionista.
* `private List<String> pazientiInCarico`: Lista dei CUI dei pazienti associati formalmente al medico.
* `private List<Visita> agendaAppuntamentiVisite`: Elenco delle visite programmate (prenotate).
* `private List<Esame> agendaAppuntamentiEsame`: Elenco degli esami diagnostici in agenda.
* `private int numEventiAgendaVisite`: Contatore interno per la generazione dei CUI delle visite prenotate.
* `private int numEventiAgendaEsame`: Contatore interno per la generazione dei CUI degli esami prenotati.

### Funzionamento
La classe modella la figura del Medico e ne gestisce l'agenda professionale. È strutturata per mantenere una separazione netta tra visite ed esami programmati. Include logiche per l'associazione dei pazienti e la gestione dinamica degli appuntamenti (inserimento, modifica data e rimozione). Ogni modifica a questa classe deve essere sincronizzata con l'oggetto `Paziente` corrispondente a cura del Controller.

### Metodi

#### `public Medico()`
* **Descrizione**: Costruttore vuoto di default.

#### `public List<String> getPazientiInCarico()` / `getAgendaAppuntamentiVisite()` / `getAgendaAppuntamentiEsami()`
* **Descrizione**: Metodi getter per le liste.
* **Logica**: Restituiscono una **nuova istanza di ArrayList** (copia) per proteggere l'integrità dei dati originali della classe (incapsulamento).

#### `public boolean aggiungiAppuntamentoVisita(String CUIpaziente, String motivo, String note, LocalDateTime dataOraPrenotazione)`
* **Descrizione**: Registra una nuova visita nell'agenda del medico.
* **Logica**: Crea un oggetto `Visita` (usando `numEventiAgendaVisite` per il CUI progressivo), lo aggiunge alla lista e incrementa il contatore.
* **Return**: `true` ad operazione completata.

#### `public boolean modificaAppuntamentoVisita(String CUIappuntamento, LocalDateTime nuovaData)`
* **Descrizione**: Cambia la data/ora di una visita esistente.
* **Logica**: Ricerca la visita tramite CUI e ne aggiorna il timestamp.
* **Return**: `true` se trovato e modificato, `false` altrimenti.

#### `public boolean rimuoviAppuntamentoVisita(String CUIappuntamento)`
* **Descrizione**: Cancella un appuntamento dall'agenda.
* **Logica**: Scorre la lista e rimuove l'oggetto il cui CUI corrisponde a quello fornito.

#### `public boolean aggiungiAppuntamentoEsame(String CUIpaziente, LocalDateTime dataOraEsame, String nomeEsame)`
* **Descrizione**: Registra un nuovo esame nell'agenda del medico.
* **Logica**: Simile alle visite, utilizza un oggetto `Esame` e il contatore `numEventiAgendaEsame`.

#### `public boolean modificaAppuntamentoEsame(String CUIappuntamento, LocalDateTime nuovaData)` / `rimuoviAppuntamentoEsame(...)`
* **Descrizione**: Metodi speculari a quelli delle visite, operanti sulla lista degli esami.

#### `public boolean aggiungiPaziente(String CUIpaziente)`
* **Descrizione**: Associa un nuovo paziente al medico.
* **Logica**: Controlla che il CUI non sia nullo e non sia già presente in lista prima di aggiungerlo.
* **Return**: `true` se aggiunto, `false` se già presente.

#### `public boolean rimuoviPaziente(String CUIpaziente)`
* **Descrizione**: Rimuove l'associazione con un paziente.
* **Return**: `true` se il paziente è stato trovato e rimosso.

#### `public String getSpecializzazione()` / `setSpecializzazione(String s)`
* **Descrizione**: Getter e setter per la specializzazione medica.
* **Nota per il Controller**: Il valore deve essere selezionato tramite box a scelta multipla nella GUI.

#### `public boolean equals(Object o)` / `hashCode()`
* **Descrizione**: Verificano l'uguaglianza profonda includendo lo stato delle agende, dei contatori e dei pazienti in carico, oltre ai dati anagrafici di `Persona`.

---

## Classe: Paziente.java

**Eredità**: Estende la classe `Persona`
**Interfacce**: Implementa `Serializable`

### Attributi
* `private String telefono`: Recapito telefonico (senza prefisso internazionale).
* `private String indirizzo`: Indirizzo di residenza o domicilio.
* `private String gruppoSanguigno`: Gruppo sanguigno (es. "A+", "0-").
* `private List<String> allergie`: Elenco delle allergie del paziente.
* `private List<String> patologie`: Elenco delle patologie croniche o pregresse.
* `private List<Visita> visite`: Storico delle visite mediche effettuate.
* `private List<Visita> visitePrenotate`: Appuntamenti futuri per visite.
* `private List<Terapia> terapie`: Elenco delle terapie farmacologiche prescritte.
* `private List<Esame> esami`: Storico degli esami diagnostici eseguiti.
* `private List<Esame> esamiPrenotati`: Appuntamenti futuri per esami.
* `private int numVisite / numTerapie / numEsami`: Contatori interni per la generazione dei CUI degli eventi effettuati.
* `private int numVisitePrenotate / numEsamiPrenotate`: Contatori interni per la generazione dei CUI degli eventi prenotati.

### Funzionamento
La classe `Paziente` rappresenta il nucleo della cartella clinica digitale. Gestisce l'intero storico medico dell'utente, comprese prenotazioni, terapie ed esami. Implementa una logica di permessi granulare (`hasPermesso`) per garantire che solo le figure autorizzate (es. il medico curante o il segretario, a seconda dell'operazione) possano modificare i dati. 

**Nota Cruciale per il Controller**: 
1. I dati devono essere validati prima del passaggio ai metodi. 
2. Le prestazioni effettuate (visite, esami, terapie) **non possono essere eliminate**, ma solo modificate nei dettagli non strutturali. 
3. Il salvataggio su file deve essere gestito esternamente dal Controller sovrascrivendo l'intero oggetto.

### Metodi

#### `public Paziente()`
* **Descrizione**: Costruttore vuoto di default per la serializzazione.

#### `public boolean aggiungi[Visita/Esame/Terapia]Effettuata(...)`
* **Descrizione**: Registra una prestazione medica già avvenuta.
* **Logica**: Verifica l'esistenza del CUI Medico e i permessi di scrittura (`hasPermessoAggiungere`). Genera automaticamente un CUI univoco per l'evento e registra l'azione nel Log di sistema.

#### `public boolean aggiungi[Visita/Esame]Prenotata(...)`
* **Descrizione**: Inserisce una nuova prenotazione in agenda.
* **Logica**: Richiede il permesso `hasPermessoPrenotare`. A differenza degli eventi effettuati, le prenotazioni generano CUI con prefissi specifici (es. "VP" o "EP").

#### `public boolean modifica[Visita/Esame/Terapia](...)`
* **Descrizione**: Aggiorna i dettagli di un record esistente (diagnosi, risultati, note o date di prenotazione).
* **Logica**: Ricerca l'oggetto tramite CUI nelle liste interne e verifica i permessi dell'utente attivo rispetto al medico che ha originato il record.

#### `public boolean elimina[Visita/Esame]Prenotata(String CUI)`
* **Descrizione**: Rimuove una prenotazione futura.
* **Logica**: Impedita ad Amministratori e account in Recovery. Un medico può eliminare solo prenotazioni intestate a se stesso.

#### `private boolean hasPermessoAggiungere(String CUImedico)`
* **Descrizione**: Controllo di sicurezza interno.
* **Restrizioni**: Restituisce `false` se l'utente attivo è Amministratore, Segretario o un Medico diverso da quello indicato nel record.

#### `private boolean hasPermessoPrenotare(String CUImedico)`
* **Descrizione**: Simile a `hasPermessoAggiungere`, ma consente l'operazione anche ai Segretari.

#### `public boolean aggiungiPatologia(String p)` / `aggiungiAllergia(String a)`
* **Descrizione**: Inserisce nuove voci nelle liste cliniche.
* **Logica**: Include un controllo interno per evitare l'inserimento di duplicati (`controllaDoppioni`).

#### `public boolean setGruppoSanguigno(String gs)` / `setTelefono(String t)` / `setIndirizzo(String i)`
* **Descrizione**: Setter condizionali.
* **Logica**: I dati vengono puliti (trim/rimozione spazi) e validati tramite `ValidatoreDati` prima dell'assegnazione.

#### `public List<...> get...()`
* **Descrizione**: Getter per le liste di record medici.
* **Logica**: Restituiscono sempre una **copia** (`new ArrayList<>`) della lista per impedire manipolazioni esterne non controllate dei dati originali.

---

## Classe: Persona.java

**Tipo**: Classe Astratta
**Interfacce**: Implementa `Serializable`

### Attributi
* `private String CUI`: Codice Unico Identificativo di sistema.
* `private String nome`: Nome della persona.
* `private String cognome`: Cognome della persona.
* `private LocalDate dataNascita`: Data di nascita (formato ISO-8601).
* `private int eta`: Età calcolata automaticamente in base alla data di nascita.
* `private String codiceFiscale`: Codice fiscale dell'utente.

### Funzionamento
Questa classe funge da base comune per tutte le entità umane del sistema (`Paziente`, `Medico`, `Amministratore`, `Segretario`, `IT`). Essendo `abstract`, non può essere istanziata direttamente. Contiene gli attributi anagrafici essenziali e i metodi di utilità per la pulizia delle stringhe e il calcolo automatico dell'età. Fornisce inoltre i criteri base per il confronto tra oggetti tramite `equals` e `hashCode`.

### Metodi

#### `public Persona()`
* **Descrizione**: Costruttore vuoto di default per la serializzazione.

#### `public Persona(String CUI, String nome, String cognome, LocalDate dataNascita, int eta, String codiceFiscale)`
* **Descrizione**: Costruttore parametrizzato.
* **Logica**: Utilizzato esclusivamente per permettere la creazione manuale dell'utente IT di default (bypassando i controlli di validazione standard) durante l'inizializzazione del filesystem.

#### `public String pulisciStringa(String stringa)`
* **Descrizione**: Metodo di utilità per la formattazione.
* **Logica**: Rimuove tutti gli spazi bianchi all'interno della stringa passata.

#### `public boolean setNome(String nome)` / `setCognome(String cognome)`
* **Descrizione**: Imposta il nome o il cognome.
* **Logica**: Effettua un `trim()` per rimuovere spazi iniziali/finali e verifica che la stringa non sia nulla o vuota. 
* **Return**: `true` se il dato è valido e impostato, `false` altrimenti.

#### `public void setEta()`
* **Descrizione**: Calcola e imposta l'età dell'utente.
* **Logica**: Utilizza la classe `Period` per calcolare la differenza in anni tra `dataNascita` e la data attuale (`LocalDate.now()`).
* **Eccezioni**: Solleva `IllegalArgumentException` se `dataNascita` non è stata preventivamente impostata.

#### `public void setCodiceFiscale(String codiceFiscale)`
* **Descrizione**: Imposta il codice fiscale previa rimozione degli spazi bianchi superflui.

#### `public String getCUI()` / `getNome()` / `getCognome()` / `getDataNascita()` / `getEta()` / `getCodiceFiscale()`
* **Descrizione**: Metodi getter standard per l'accesso agli attributi privati.

#### `public boolean equals(Object o)` / `public int hashCode()`
* **Descrizione**: Metodi per il confronto tra oggetti. L'uguaglianza è verificata sulla corrispondenza di tutti i campi anagrafici e del CUI.

---

## Classe: Segretario.java

**Eredità**: Estende la classe `Persona`
**Interfacce**: Implementa `Serializable`

### Attributi
* `private String turnoLavoro`: Descrizione testuale della disponibilità oraria e settimanale del segretario (es. "Lunedì 8:00-18:00 \n Giovedì 8:00-14:00").

### Funzionamento
La classe modella il profilo del personale di segreteria dello studio. Oltre ai dati ereditati da `Persona.java`, gestisce le informazioni relative ai turni di lavoro. All'interno del sistema, il Segretario ha permessi specifici che gli consentono di gestire le anagrafiche dei pazienti e le prenotazioni, ma gli negano l'inserimento o la modifica di dati clinici sensibili (visite effettuate, esami eseguiti o terapie).

### Metodi

#### `public Segretario()`
* **Descrizione**: Costruttore vuoto di default per la serializzazione.

#### `public String getTurnoLavoro()`
* **Descrizione**: Restituisce la stringa contenente i dettagli dei turni.
* **Return**: `String` formattata con orari e giorni.

#### `public void setTurnoLavoro(String turnoLavoro)`
* **Descrizione**: Imposta la pianificazione dei turni.
* **Nota per il Controller**: Per garantire la validità del formato, questo dato deve essere popolato nella GUI tramite box di selezione predefiniti o controlli guidati, evitando l'inserimento manuale libero da parte dell'utente.

#### `public boolean equals(Object o)`
* **Descrizione**: Verifica l'uguaglianza tra due oggetti `Segretario`.
* **Logica**: Estende il controllo della classe base `Persona` includendo la verifica sull'attributo `turnoLavoro`.

#### `public int hashCode()`
* **Descrizione**: Genera l'hash univoco basato sui campi ereditati e sul turno di lavoro.

---

## Classe: Terapia.java

**Interfacce**: Implementa `Serializable`

### Attributi
* `private String CUImedico`: Codice identificativo del medico che ha prescritto la terapia.
* `private LocalDate dataInizio`: Data di inizio del trattamento farmacologico.
* `private LocalDate dataFine`: Data prevista per la conclusione del trattamento.
* `private String nomeFarmaco`: Denominazione del medicinale prescritto.
* `private String dosaggio`: Quantità di farmaco da assumere (es. "50mg", "1 compressa").
* `private String frequenza`: Cadenza delle assunzioni (es. "2 volte al giorno", "ogni 8 ore").
* `private String CUIterapia`: Codice Univoco Identificativo della terapia, caratterizzato dal prefisso **"T"**.

### Funzionamento
La classe modella una prescrizione farmacologica assegnata a un paziente. A differenza di visite ed esami, le terapie non prevedono uno stato di "prenotazione" ma solo di "prescrizione attiva o passata". Include la logica per la generazione automatica del CUI basata su un contatore progressivo.

### Metodi

#### `public Terapia(String CUImedico, LocalDate dataInizio, LocalDate dataFine, String nomeFarmaco, String dosaggio, String frequenza, int numTerapie)`
* **Descrizione**: Costruttore parametrizzato per la creazione di una nuova terapia.
* **Logica**: Inizializza tutti i campi relativi alla posologia e al periodo di validità. Genera il CUI univoco richiamando il metodo `setCUIterapia`.

#### `public void setCUIterapia(int numTerapie)`
* **Descrizione**: Genera e imposta il codice identificativo della terapia.
* **Logica**: Concatena il prefisso **"T"** con il valore intero `numTerapie + 1`.

#### `public String getCUImedico()` / `setCUImedico(String CUI)`
* **Descrizione**: Getter e setter per il medico prescrittore.

#### `public LocalDate getDataInizio()` / `setDataInizio(LocalDate d)`
* **Descrizione**: Getter e setter per la data di decorrenza.

#### `public LocalDate getDataFine()` / `setDataFine(LocalDate d)`
* **Descrizione**: Getter e setter per la data di termine.

#### `public String getNomeFarmaco()` / `setNomeFarmaco(String n)`
* **Descrizione**: Getter e setter per il nome del farmaco.

#### `public String getDosaggio()` / `setDosaggio(String d)`
* **Descrizione**: Getter e setter per la dose.

#### `public String getFrequenza()` / `setFrequenza(String f)`
* **Descrizione**: Getter e setter per la frequenza di assunzione.

#### `public String getCUIterapia()`
* **Descrizione**: Restituisce il CUI della terapia (formato T...).

#### `public boolean equals(Object o)` / `public int hashCode()`
* **Descrizione**: Metodi per il confronto e la generazione dell'identificativo hash basati sulla totalità degli attributi della classe.

---

## Classe: Visita.java

**Interfacce**: Implementa `Serializable`

### Attributi
* `private String CUImedico`: Codice identificativo del medico che effettua la visita o presso cui è prenotata.
* `private LocalDateTime dataOraVisita`: Timestamp dell'evento. Rappresenta il momento dell'effettiva esecuzione (per visite concluse) o la data/ora fissata (per le prenotazioni).
* `private String motivo`: Ragione della visita espressa dal paziente o dal medico.
* `private String diagnosi`: Esito clinico della visita (presente solo per visite effettuate).
* `private String note`: Osservazioni aggiuntive o dettagli clinici extra.
* `private String CUIvisita`: Codice Univoco Identificativo della visita. Utilizza il prefisso **"V"** per le visite effettuate e **"VP"** per le visite prenotate.

### Funzionamento
La classe modella l'interazione clinica tra medico e paziente. Gestisce la transizione logica tra la fase di prenotazione e quella di effettiva esecuzione tramite due costruttori distinti. In fase di prenotazione, la diagnosi viene inizializzata come stringa vuota. La classe include la logica per la generazione automatica dei codici identificativi (CUI) basata sui contatori forniti dal sistema.

### Metodi

#### `public Visita(String CUImedico, String motivo, String diagnosi, String note, int numVisite)`
* **Descrizione**: Costruttore specifico per **visite effettuate**.
* **Logica**: Inizializza tutti i campi clinici, imposta `dataOraVisita` al momento attuale (`LocalDateTime.now()`) e genera un CUI con prefisso **"V"**.

#### `public Visita(String CUImedico, String motivo, String note, int numVisitePrenotate, LocalDateTime dataOraVisitaPrenotata)`
* **Descrizione**: Costruttore specifico per **visite prenotate**.
* **Logica**: Inizializza i dati della prenotazione, imposta la diagnosi come vuota e genera un CUI con prefisso **"VP"**.

#### `public void setCUIvisitaPrenotata(int numVisitePrenotate)`
* **Descrizione**: Genera e imposta il CUI per una prenotazione.
* **Logica**: Concatena la stringa **"VP"** con il valore `numVisitePrenotate + 1`.

#### `public void setCUIvisita(int numVisite)`
* **Descrizione**: Genera e imposta il CUI per una visita effettuata.
* **Logica**: Concatena la stringa **"V"** con il valore `numVisite + 1`.

#### `public void setDataPrenotazione(LocalDateTime data)`
* **Descrizione**: Metodo setter per aggiornare o impostare la data di una prenotazione.

#### `public void setDataOraVisita()`
* **Descrizione**: Overload del setter che imposta automaticamente il timestamp attuale (usato per le visite effettuate al momento).

#### `public String getMotivo()` / `setMotivo(String m)`
* **Descrizione**: Getter e setter per la motivazione del consulto.

#### `public String getDiagnosi()` / `setDiagnosi(String d)`
* **Descrizione**: Getter e setter per l'esito clinico.

#### `public String getNote()` / `setNote(String n)`
* **Descrizione**: Getter e setter per le annotazioni del medico.

#### `public String getCUIvisita()`
* **Descrizione**: Restituisce l'identificativo univoco della visita (formato V... o VP...).

#### `public String getCUImedico()` / `setCUImedico(String CUI)`
* **Descrizione**: Getter e setter per il CUI del medico associato.

#### `public boolean equals(Object o)` / `public int hashCode()`
* **Descrizione**: Metodi per il confronto e la generazione dell'hash basati su tutti i campi della classe.

---

## Classe: ControlloLogin.java

**Tipo**: Logica di Sistema / Sicurezza

### Attributi Statici
* `public static String utenteAttivo`: Variabile globale che identifica l'utente attualmente loggato nel sistema. Se l'accesso avviene in modalità standard, contiene il **CUI** (es. "M1"); se l'accesso è in Recovery, contiene il prefisso **"R_"** seguito dal CUI (es. "R_I1").
* `private static String CUI`: Variabile di appoggio per memorizzare temporaneamente il Codice Unico Identificativo estratto dal nome utente.

### Funzionamento
Questa classe gestisce il punto di accesso principale all'applicazione. Si occupa di verificare la validità delle credenziali (Username e Password) confrontandole con i dati cifrati presenti nel sistema. Implementa una logica di ridondanza consultando sia il file delle credenziali principale che quello di backup per garantire l'accesso anche in caso di corruzione parziale dei dati. Inoltre, gestisce il filtraggio degli accessi per la modalità di emergenza (Recovery Mode).



### Metodi

#### `public static String controlloCredenziali(String nomeUtente, String passwordUtente, boolean recovery) throws IOException`
* **Descrizione**: Metodo principale chiamato dalla GUI al click del tasto "Login".
* **Logica**:
    1. Converte la password fornita in un Hash tramite `PasswordUtils`.
    2. Carica le mappe delle credenziali dai file `ConfigFile` e `ConfigFile_backup`.
    3. Verifica la presenza del `nomeUtente` in almeno una delle due mappe.
    4. Confronta l'Hash della password inserita con quello salvato nel file.
    5. Se le credenziali sono corrette ed è richiesta la **Recovery**: chiama `controlloPermessoRecovery`.
    6. Se le credenziali sono corrette in modalità **Standard**: imposta `utenteAttivo = CUI`.
* **Return**: 
    * `"login_success"`: Accesso standard riuscito.
    * `"recovery_login_success"`: Accesso Recovery riuscito (per direttori IT).
    * `"recovery_access_denied"`: Credenziali corrette, ma l'utente non ha i privilegi per la Recovery.
    * `"login_fail"`: Credenziali errate o utente non trovato.

#### `private static boolean controlloPermessoRecovery(String nomeUtente, String CUI) throws IOException`
* **Descrizione**: Verifica se l'utente che richiede la modalità Recovery ha i requisiti gerarchici necessari.
* **Logica**:
    1. Controlla il primo carattere del CUI: solo se inizia con **'I'** (IT) l'analisi prosegue.
    2. Localizza il file `.dat` specifico del profilo IT (usando i path principali o di backup).
    3. Deserializza l'oggetto e verifica tramite `instanceof` che sia effettivamente un profilo `IT`.
    4. Controlla il `gradoReparto`: il permesso è concesso solo se il grado è esattamente **"direttore_IT"**.
* **Return**: `true` se l'utente è un Direttore IT, `false` per qualsiasi altro caso (inclusi Segretari, Medici o Amministratori).

---

## Classe: CreazioneEliminazionePersone.java

**Tipo**: Gestore Logica di Business / CRUD Utenti

### Funzionamento
Questa classe funge da "fabbrica" per la gestione del ciclo di vita di tutte le tipologie di utenti del sistema (`Paziente`, `Medico`, `Segretario`, `Amministratore`, `IT`). Si occupa di validare i dati anagrafici, controllare i permessi dell'utente attivo e gestire la persistenza fisica dei dati. 

La logica di creazione segue un processo in due step:
1. Generazione di un file vuoto per permettere al sistema di assegnare il primo **CUI** disponibile (gestito internamente da `ScritturaFile`).
2. Recupero del CUI assegnato (tramite scansione della directory per data di modifica), inizializzazione completa dell'oggetto e sovrascrittura del file con i dati finali.

### Metodi di Creazione

#### `public static boolean creaPaziente(...)`
* **Logica**: Valida telefono, gruppo sanguigno, CF e data di nascita tramite `ValidatoreDati`. Impedisce la creazione ai Medici (permesso riservato a Segretari, IT e Amministratori).
* **Processo**: Crea l'oggetto `Paziente`, estrae il CUI dal file system e salva il profilo completo in `PAZIENTI_DIR`.

#### `public static boolean creaIT(...)`
* **Logica**: Metodo ad alta sicurezza. Impedisce la creazione a Medici e Segretari. Se l'utente attivo è un IT, deve obbligatoriamente avere il grado di **"direttore_IT"**.
* **Processo**: Inizializza l'oggetto `IT` con grado e titolo di studio, salvandolo in `IT_DIR`.

#### `public static boolean creaMedico(...)` / `creaSegretario(...)` / `creaAmministratore(...)`
* **Logica**: Metodi speculari per la creazione delle rispettive figure. 
* **Permessi**: La creazione di Medici e Segretari è preclusa a loro stessi e richiede un grado amministrativo o tecnico superiore.

### Metodi di Eliminazione

#### `public static boolean eliminaPaziente(String CUI)` / `eliminaMedico(String CUI)` / `eliminaSegretario(String CUI)`
* **Logica**: Impedisce l'eliminazione a Medici e Segretari. 
* **Azione**: Localizza il file `.dat` nella cartella di competenza tramite il CUI e invoca `CreazioneEliminazioneFile.eliminaFile(file)`.

#### `public static boolean eliminaAmministratore(String CUI)` / `eliminaIT(String CUI)`
* **Logica**: 
    * Impedisce l'auto-eliminazione (un utente non può cancellare il proprio profilo).
    * L'eliminazione di un Amministratore è permessa solo agli utenti IT.
    * L'eliminazione di un IT è permessa solo ad altri IT con grado **"direttore_IT"**.

---

## Classe: MainClass.java

**Tipo**: Entry Point / Controller del Ciclo di Vita

### Funzionamento
Questa classe rappresenta il punto di ingresso dell'intera applicazione. Gestisce l'avvio del sistema, l'inizializzazione del filesystem, il processo di autenticazione e il controllo dell'integrità dei dati. È progettata per gestire tre scenari principali:
1. **Primo Avvio**: Inizializzazione automatica delle directory e dei file necessari (compresi quelli di backup).
2. **Accesso Standard**: Verifica delle credenziali, controllo di integrità dei dati (Self-Healing) e reindirizzamento al menù specifico dell'utente.
3. **Recovery Mode**: Accesso limitato alle funzioni di riparazione del sistema per gli amministratori IT.



### Metodi

#### `public static void main(String[] args)`
* **Descrizione**: Punto di ingresso standard della JVM. Invoca `lancioIniziale()`.

#### `private static void lancioIniziale() throws IOException`
* **Descrizione**: Coordina la sequenza di boot dell'applicazione.
* **Logica**:
    1. **Check Filesystem**: Se i file delle credenziali (principale e backup) sono assenti, invoca `InitFileSystem` e `InitFileSystem_backup`.
    2. **Ciclo di Login**: Lancia la `SchermataLogin` in un loop continuo finché l'utente non esegue l'accesso con successo o chiude l'app.
    3. **Logging**: Registra l'avvenuto login nel sistema di Log.
    4. **Bivio Operativo**: 
        * Se `ControlloLogin.utenteAttivo` inizia con **"R"**, devia il flusso verso la GUI di Recovery.
        * Altrimenti, esegue `checkFileIniziale()` e lancia il menù utente tramite `lanciaMenu()`.

#### `private static void checkFileIniziale()`
* **Descrizione**: Garantisce che i dati dell'applicazione siano integri prima di consentire l'uso del software.
* **Logica**: Invoca `ControlloDatiIniziale.validaDati()` per confrontare i file principali con quelli di backup. In caso di discrepanze, il sistema tenta la correzione automatica. 
* **Gestione Errori**: Se il controllo fallisce in modo critico, l'applicazione termina con codice d'uscita **2**.

#### `private static void lanciaMenu()`
* **Descrizione**: Agisce come dispatcher per le interfacce grafiche.
* **Logica**: Legge il primo carattere di `utenteAttivo` (M, S, I, A) e avvia la GUI corrispondente al ruolo (Medico, Segretario, IT o Amministratore).
* **Sicurezza**: Se il tipo utente non è riconosciuto, il sistema termina forzatamente per prevenire accessi non autorizzati (Codice d'uscita **3**).

---

## Classe: ValidatoreDati.java

**Tipo**: Classe di Utilità / Validazione

### Descrizione
Questa classe centralizza tutta la logica di controllo formale dei dati. Viene utilizzata prevalentemente dal **Frontend (GUI)** per intercettare errori di inserimento prima che le informazioni vengano processate dal backend. Utilizza Espressioni Regolari (Regex) e controlli logici sulle date e sul filesystem per garantire l'integrità del database.

### Attributi Statici (Pattern Regex)
* `private static final Pattern USERNAME_PATTERN`: Definisce lo standard dei nomi utente. Deve iniziare con un prefisso di ruolo (`M, I, S, A, P`), seguito da un identificativo numerico e un underscore (es. `M1_rossi`).
* `private static final Pattern SYMBOL_PATTERN`: Identifica la presenza di caratteri speciali (non alfanumerici).

### Metodi di Validazione

#### `public static boolean isUsernameValido(String username)`
* **Logica**: Verifica che la stringa non sia nulla e che rispetti rigorosamente il `USERNAME_PATTERN`.

#### `public static boolean isPasswordSicura(String password)`
* **Descrizione**: Implementa i criteri di sicurezza per le credenziali.
* **Criteri**: Lunghezza minima di **8 caratteri**, presenza di almeno una maiuscola, una minuscola, un numero e un simbolo speciale.

#### `public static boolean isTelefonoValido(String telefono)`
* **Logica**: Controlla che il campo sia composto esattamente da **10 cifre** numeriche.

#### `public static boolean isDataDiNascitaValida(LocalDate data)`
* **Logica**: Verifica che la data non sia nel futuro e che rientri in un intervallo realistico (non più di 150 anni fa rispetto alla data attuale).

#### `public static boolean isGruppoSanguignoValido(String gruppo)`
* **Logica**: Esegue uno switch-case sui valori ammessi (`A+, A-, B+, B-, AB+, AB-, 0+, 0-`). Restituisce `false` per qualsiasi altra stringa.

#### `public static boolean isCodiceFiscaleValido(String codice)`
* **Logica**: Verifica la lunghezza standard di **16 caratteri**. Nota: non effettua il calcolo dell'omocodia o il controllo dei caratteri di controllo (cin), limitandosi alla validazione formale della stringa.

### Metodi di Controllo File

#### `public static boolean controllaEsistenzaCui(String CUI)`
* **Descrizione**: Verifica se un CUI è già presente nel database fisico.
* **Logica**:
    1. Estrae il prefisso dal CUI per determinare la cartella di competenza (es. 'M' -> `MEDICI_DIR`).
    2. Costruisce il percorso del file `.dat` corrispondente.
    3. Utilizza `Files.exists(percorsoFile)` per confermare la presenza del file sul disco.
* **Return**: `true` se il CUI esiste già, `false` se è disponibile o non valido.

---
