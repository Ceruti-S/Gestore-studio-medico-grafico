# Documentazione Backend-GestioneFile - Studio Medico GCM

Questa documentazione descrive l'architettura del sistema di persistenza e sicurezza dello Studio Medico GCM. Il pacchetto si occupa della manipolazione fisica dei dati, della loro protezione tramite crittografia e della garanzia di continuità operativa attraverso meccanismi di ridondanza e backup.

## Note Generali di Architettura

* **Integrità e Ridondanza**: Il sistema opera su una struttura a "doppio binario". Ogni operazione di scrittura viene riflessa simultaneamente su una directory principale e una di backup per prevenire la perdita di dati dovuta a corruzioni accidentali.
* **Sicurezza dei Dati**: La persistenza non avviene mai in chiaro. Tutti i file con estensione `.dat` sono cifrati tramite algoritmi di crittografia simmetrica, mentre le password sono protette da hashing irreversibile.
* **Percorsi di Sistema**: La gestione dei path è centralizzata. Questo permette al software di essere portabile e di mantenere una gerarchia di cartelle (Medici, Pazienti, IT, ecc.) organizzata e prevedibile.

## Protocollo di Protezione e Ripristino

Il pacchetto `gestioneFile` è il garante della stabilità del sistema. In caso di discrepanze rilevate all'avvio, il backend utilizza queste classi per eseguire procedure di "Self-Healing" (auto-riparazione).



> **Logica di Sicurezza (Cifratura e Accesso):**
> 1. **Filtro di Lettura**: I metodi di lettura decifrano i flussi di byte trasformandoli nuovamente in oggetti Java utilizzabili dalla logica di funzionamento.
> 2. **Prevenzione Corruzione**: Durante la scrittura, il sistema valida la disponibilità dello spazio e l'integrità del percorso prima di finalizzare l'operazione.
> 3. **Indipendenza dal Sistema Operativo**: L'uso della libreria `java.nio.file` garantisce che la gestione dei file sia corretta sia su sistemi Windows che Unix-based.

---

## Classe: CrittografiaFile.java

**Tipo**: Sicurezza / Motore di Cifratura Simmetrica

### Descrizione
La classe `CrittografiaFile` costituisce il core della sicurezza del sistema GCM. Implementa l'algoritmo **AES-256** (Advanced Encryption Standard) per proteggere i dati sensibili salvati su disco. Il suo scopo è convertire le stringhe di oggetti serializzati in flussi di byte indecifrabili (Cifratura) e viceversa (Decifratura), garantendo che i file `.dat` siano leggibili esclusivamente attraverso l'applicazione.



### Standard di Sicurezza
* **Algoritmo**: AES (Advanced Encryption Standard).
* **Lunghezza Chiave**: 256 bit (32 byte), garantita dall'array `chiaveByte` di 32 elementi.
* **Codifica Caratteri**: UTF-8.

### Attributi Statici
* `private static final byte[] chiaveByte`: Array di 32 byte che contiene la chiave simmetrica master. 
    * **Valore Hardcoded**: `3F A7 1C 9D E5 2B 88 4F 76 B1 C0 AA D3 67 5E 99 12 FA CB 31 4D 8E 72 B9 06 D5 E8 1A 3C F7 95 0B`.
* `private static final SecretKey chiaveCrittaggio`: Istanza di `SecretKeySpec` che converte i byte grezzi in una chiave utilizzabile dal framework `javax.crypto`.

### Metodi

#### `public static byte[] criptaContenuto(String contenuto) throws Exception`
* **Descrizione**: Trasforma una stringa in chiaro in un array di byte crittografati.
* **Logica**:
    1. Converte la stringa di input in un array di byte utilizzando il set di caratteri **UTF-8**.
    2. Istanzia un oggetto `Cipher` configurato per l'algoritmo **AES**.
    3. Inizializza il `Cipher` in modalità `ENCRYPT_MODE` utilizzando la chiave master.
    4. Esegue l'operazione `doFinal` per restituire il dato cifrato.
* **Parametri**: `String contenuto` (tipicamente l'oggetto serializzato e convertito in stringa).
* **Return**: `byte[]` pronto per essere scritto su file.

#### `public static String decrittaContenuto(byte[] contenutoCrittato) throws Exception`
* **Descrizione**: Converte un flusso di byte cifrati recuperati dal disco in una stringa leggibile.
* **Logica**:
    1. Istanzia l'oggetto `Cipher` per l'algoritmo **AES**.
    2. Inizializza il `Cipher` in modalità `DECRYPT_MODE` con la stessa chiave usata per la cifratura.
    3. Processa l'array di byte cifrati tramite `doFinal`.
    4. Ricostruisce la stringa originale interpretando i byte decifrati come **UTF-8**.
* **Parametri**: `byte[] contenutoCrittato` (letto dal file fisico).
* **Return**: `String` contenente il dato originale (es. l'oggetto serializzato).

---

## Classe: PasswordUtils.java

**Tipo**: Sicurezza / Hashing delle Credenziali

### Descrizione
La classe `PasswordUtils` fornisce le funzionalità necessarie per la gestione sicura delle password all'interno del sistema. A differenza della cifratura utilizzata per i file (che è reversibile), questa classe implementa l'**hashing**, un processo unidirezionale che garantisce che le password originali non siano mai memorizzate in chiaro. Anche se un malintenzionato ottenesse l'accesso ai file delle credenziali, vedrebbe solo l'impronta digitale (hash) e non la password reale.



### Caratteristiche Tecniche
* **Algoritmo**: SHA-256 (Secure Hash Algorithm 256-bit).
* **Formato Output**: Stringa esadecimale (Hex).
* **Incapsulamento**: Il costruttore è privato per impedire l'istanziazione della classe, trattandosi di una classe di pura utilità statica.

### Metodi

#### `public static String hashPassword(String password)`
* **Descrizione**: Riceve una stringa in chiaro (la password inserita dall'utente) e restituisce la sua impronta digitale univoca.
* **Logica**:
    1. Ottiene un'istanza dell'algoritmo **SHA-256** tramite la classe `MessageDigest`.
    2. Converte la password in un array di byte utilizzando il set di caratteri **UTF-8**.
    3. Calcola il digest (l'hash) della password.
    4. Trasforma l'array di byte risultante in una stringa **esadecimale** leggibile:
        * Scorre ogni byte dell'hash.
        * Lo converte in una rappresentazione esadecimale a due cifre.
        * Aggiunge uno "0" iniziale se il valore esadecimale è composto da un solo carattere (padding).
* **Return**: Una stringa esadecimale di 64 caratteri rappresentante l'hash della password.
* **Gestione Eccezioni**: In caso di mancato supporto all'algoritmo SHA-256 da parte della Java Virtual Machine, solleva una `RuntimeException`.

---

## Classe: ControlloDatiIniziale.java

**Tipo**: Logica di Sistema / Self-Healing (Auto-riparazione)

### Descrizione
La classe `ControlloDatiIniziale` rappresenta il sistema immunitario dell'applicazione. Viene invocata durante la schermata di caricamento (`MainClass`) per garantire che i dati presenti nelle cartelle principali coincidano perfettamente con quelli di backup. Il sistema adotta una politica di **"Backup-First"**: in caso di discrepanza, il file di backup è considerato la sorgente di verità e viene utilizzato per sovrascrivere il file principale corrotto o alterato.



### Funzionamento e Logica di Ripristino
Il processo si articola in due fasi principali:
1.  **Validazione Strutturale**: Verifica l'esistenza fisica di tutte le directory e di tutti i file utente e globali.
2.  **Validazione del Contenuto**: Decripta e confronta gli oggetti Java contenuti nei file per identificare corruzioni silenti o modifiche non autorizzate.

#### Gerarchia di Intervento:
* **Caso Standard**: File principale mancante o diverso -> Ripristino dal Backup.
* **Caso Critico**: Backup illeggibile o mancante -> Tentativo di rigenerazione del Backup partendo dal file Principale (se integro).
* **Caso Disastroso**: Sia il principale che il backup sono corrotti -> L'applicazione tenta un ripristino di emergenza (per Log, Contatori o Credenziali) o forza l'uscita richiedendo l'intervento in **Recovery Mode** (Codice errore **4**).

### Metodi

#### `public static void validaDati() throws IOException`
* **Descrizione**: Punto di ingresso per il controllo di integrità.
* **Logica**: 
    1. Scansiona le coppie di cartelle definite in `dirs` (Medici, Pazienti, ecc.).
    2. Se mancano cartelle o file principali corrispondenti ai backup, segnala l'errore e prepara il terreno per il ripristino.
    3. Esegue lo stesso controllo sui file globali (`CONTATORI_FILE`, `CREDENZIALI_FILE`, `LOG_FILE`).
    4. Al termine, invoca `validaContenuto()` per il controllo approfondito.

#### `private static void validaContenuto(Path dirs[][]) throws IOException`
* **Descrizione**: Esegue il confronto bit-a-bit (post-decrittazione) degli oggetti.
* **Processo di "Healing"**:
    1. Legge e decripta l'oggetto dal backup.
    2. Prova a leggere e decriptare il corrispettivo principale.
    3. Se `backupObj.equals(mainObj)` è falso, o se il principale è illeggibile, sovrascrive il principale con i dati del backup.
    4. **Inversione**: Se il backup è illeggibile ma il principale è sano, ripristina il backup per mantenere la ridondanza.
* **Gestione Casi Speciali**: 
    * Se le **Credenziali** sono totalmente perse, vengono reinizializzate ai valori di default.
    * Se i **Log** o i **Contatori** sono corrotti su entrambi i binari, interviene la classe `HealingFile` per un ripristino di emergenza.

---

## Classe: CostruttorePath.java

**Tipo**: Classe di Utilità / Gestione Percorsi Dinamici

### Descrizione
La classe `CostruttorePath` ha il compito fondamentale di determinare la destinazione fisica di salvataggio per i nuovi oggetti creati nel sistema. Agisce come un "regista dei percorsi", assicurando che ogni nuova istanza di una classe modello (Medico, Paziente, ecc.) venga archiviata nella cartella corretta e riceva un nome di file univoco basato sulla gerarchia dei contatori globali.



### Funzionamento Logico
Il metodo principale utilizza i **Generics** di Java (`<T>`) per accettare qualsiasi tipo di oggetto modello. La logica segue questi step:
1.  **Identificazione**: Riconosce il tipo di classe dell'oggetto passato (es. se è un'istanza di `Medico`).
2.  **Mapping**: Associa al tipo un prefisso univoco (`M`, `P`, `S`, `A`, `I`) e la relativa directory di destinazione definita in `ConfigFile`.
3.  **Numerazione**: Consulta il file dei contatori globali per determinare il numero progressivo corretto (es. se esistono 10 medici, il prossimo sarà l'11).
4.  **Assemblaggio**: Concatena prefisso, numero ed estensione per generare il path finale.

### Metodi

#### `public static <T> Path costruisciPathPerOggetto(T oggetto) throws IOException`
* **Descrizione**: Genera il percorso completo (Directory + Nome File) per un nuovo oggetto.
* **Logica di Selezione**:
    * **Medico** $\rightarrow$ `MEDICI_DIR` / `M{n}.dat`
    * **Paziente** $\rightarrow$ `PAZIENTI_DIR` / `P{n}.dat`
    * **Segretario** $\rightarrow$ `SEGRETARI_DIR` / `S{n}.dat`
    * **Amministratore** $\rightarrow$ `AMMINISTRATORI_DIR` / `A{n}.dat`
    * **IT** $\rightarrow$ `IT_DIR` / `I{n}.dat`
* **Parametri**: `T oggetto` (L'istanza dell'oggetto da salvare).
* **Return**: `Path` completo risolto tramite il metodo `resolve()`.
* **Eccezioni**: 
    * `IOException`: Se il file dei contatori non è accessibile.
    * `IllegalArgumentException`: Se viene passato un oggetto di un tipo non previsto dal sistema.

---

## Classe: CreazioneEliminazioneFile.java

**Tipo**: Operatore di Basso Livello / Gestore Filesystem

### Descrizione
La classe `CreazioneEliminazioneFile` è responsabile dell'esecuzione fisica delle operazioni di creazione e rimozione dei file sul disco. La sua caratteristica principale è l'implementazione rigorosa del **protocollo di ridondanza**: ogni operazione eseguita sulla directory principale viene automaticamente replicata sulla corrispondente directory di backup, garantendo che i due sistemi rimangano sempre sincronizzati.



### Caratteristiche del Protocollo
* **Sincronia Obbligatoria**: Non è possibile creare o eliminare un file nel sistema principale senza che la stessa azione avvenga nel backup.
* **Risoluzione Dinamica del Backup**: La classe identifica autonomamente la cartella di backup corretta analizzando il prefisso del nome del file (es. 'M' per Medici, 'P' per Pazienti).

### Metodi

#### `public static Path creaFile(Path dir, String nomeFile) throws IOException`
* **Descrizione**: Crea un nuovo file vuoto nel percorso specificato e il suo rispettivo gemello nel backup.
* **Logica**:
    1. Risolve il percorso principale e crea il file tramite `Files.createFile` se non esiste.
    2. Determina la cartella di backup tramite uno `switch` sul primo carattere del nome file (`S`, `A`, `M`, `P`, `I`).
    3. Risolve il percorso di backup e crea il file gemello.
* **Return**: Il `Path` del file principale appena creato.

#### `public static void eliminaFile(Path file) throws IOException`
* **Descrizione**: Rimuove definitivamente un file dal sistema e la sua copia di sicurezza.
* **Logica**:
    1. Verifica l'esistenza del file principale e lo elimina tramite `Files.delete`.
    2. Estrae il nome del file dal path e ne identifica il prefisso.
    3. Localizza la directory di backup di competenza utilizzando `ConfigFile_backup`.
    4. Elimina il file di backup corrispondente, se esistente.
* **Parametri**: `Path file` (il percorso completo del file principale da rimuovere).

---

## Classe: HealingFile.java

**Tipo**: Sistema di Emergenza / Recupero Dati (Disaster Recovery)

### Descrizione
La classe `HealingFile` interviene quando i meccanismi standard di ridondanza falliscono (ovvero quando sia il file principale che quello di backup risultano corrotti o mancanti). Il suo compito principale è il **ripristino dei contatori globali** attraverso una scansione fisica del filesystem. Questo permette al sistema di ricostruire la "memoria" degli ID assegnati senza sovrascrivere file esistenti o creare duplicati di CUI.



### Funzionamento del Ripristino
Se il file `contatoriApp.dat` viene perso, il sistema non sa più quale numero assegnare al prossimo utente (es. se il prossimo Medico debba essere M11 o M12). `HealingFile` risolve il problema analizzando i nomi dei file effettivamente presenti nelle cartelle (`Medici`, `Pazienti`, ecc.), estraendo il numero più alto e rigenerando il file dei contatori da zero.

### Metodi

#### `public static void ripristinoEmergenzaContatori() throws IOException`
* **Descrizione**: Metodo pubblico che avvia la procedura di ricostruzione totale dei contatori per tutte le categorie di utenti.
* **Logica**:
    1. Crea una nuova mappa (`HashMap`) per ospitare i valori ricostruiti.
    2. Invoca `ricostruisciContatori` per ogni directory di categoria (M, P, I, S, A).
    3. Tenta di scrivere la nuova mappa cifrata nel percorso definito da `ConfigFile.CONTATORI_FILE`.
    4. Registra l'operazione di emergenza nel file dei **Log**.
* **Gestione Errori**: Se la scrittura fallisce anche in questa fase, il sistema termina con **Codice Errore 5**, rendendo obbligatorio l'uso della Recovery Mode manuale.

#### `private static int ricostruisciContatori(Path cartella, String prefisso) throws IOException`
* **Descrizione**: Analizza una specifica cartella per trovare il numero identificativo più alto.
* **Logica**:
    1. Utilizza uno `Stream<Path>` per elencare i file nella directory.
    2. **Filtro**: Seleziona solo i file che iniziano con il prefisso specificato (es. "M").
    3. **Mapping**: Per ogni file, tenta di estrarre la sottostringa numerica compresa tra il prefisso e l'eventuale underscore (es. da "M12_rossi.dat" estrae "12").
    4. **Riduzione**: Trova il valore massimo (`max()`) tra i numeri estratti.
* **Return**: Il valore massimo trovato incrementato di 1 (il prossimo ID disponibile). Se la cartella è vuota, restituisce 1.

---

## Classe: LetturaFile.java

**Tipo**: Motore di Deserializzazione / Input Dati

### Descrizione
La classe `LetturaFile` è il componente incaricato di recuperare le informazioni dal filesystem e trasformarle nuovamente in oggetti Java utilizzabili dalla logica di business. Opera in modo speculare alla classe di scrittura, gestendo l'inversione di tre strati di protezione: la lettura fisica dei byte, la decrittazione AES-256 e la decodifica Base64, concludendo con la deserializzazione dell'oggetto.



### Funzionamento del Metodo Generic
L'utilizzo dei **Generics** (`<T>`) permette a questa classe di essere universale: può restituire un oggetto `Medico`, un `Paziente`, una `Map` di credenziali o una lista di `Log` senza necessità di metodi specifici per ogni tipo di dato.

### Metodi

#### `public static <T> T leggiFileCifrato(Path file) throws IOException`
* **Descrizione**: Legge un file dal disco, lo decifra e ricostruisce l'oggetto originale.
* **Flusso Logico di Decostruzione**:
    1.  **Lettura Fisica**: Recupera l'intero contenuto del file come array di byte grezzi (`Files.readAllBytes`).
    2.  **Decrittazione**: Passa i byte alla classe `CrittografiaFile` per rimuovere lo strato di cifratura AES-256, ottenendo una stringa in formato **Base64**.
    3.  **Decodifica**: Converte la stringa Base64 in un array di byte che rappresenta l'oggetto serializzato.
    4.  **Deserializzazione**: Utilizza `ObjectInputStream` e `ByteArrayInputStream` per interpretare i byte e ricostruire l'istanza dell'oggetto Java.
* **Parametri**: `Path file` (il percorso del file `.dat` da leggere).
* **Return**: `(T)` L'oggetto ricostruito, castato automaticamente al tipo atteso.
* **Gestione Eccezioni**: Qualsiasi errore durante la catena (file mancante, chiave errata, classe non trovata) viene catturato e rilanciato come una `IOException` descrittiva per permettere al sistema di `HealingFile` di intervenire.

---

## Classe: ScritturaFile.java

**Tipo**: Motore di Persistenza / Output Dati

### Descrizione
La classe `ScritturaFile` è il componente fondamentale incaricato di trasformare gli oggetti Java in file fisici protetti. Gestisce l'intero ciclo di vita della persistenza: dalla creazione dei nuovi file (con assegnazione automatica del CUI), alla serializzazione, fino alla cifratura e alla scrittura ridondata (principale + backup). 



### Funzionamento e Ridondanza
Il sistema implementa una **scrittura a specchio**. Ogni volta che viene salvato un dato, la classe si assicura che lo stesso flusso di byte venga scritto sia nella directory operativa che in quella di backup. Se la cartella di backup non esiste, viene creata dinamicamente.

### Metodi

#### `public static <T> void scriviFileCifrato(Path file, T oggetto) throws IOException`
* **Descrizione**: Metodo principale per il salvataggio di qualsiasi oggetto di sistema.
* **Logica di Creazione**:
    1. Se il `Path` passato è `null` o il file non esiste, identifica la natura dell'oggetto.
    2. Utilizza `CostruttorePath` per generare un nuovo percorso univoco basato sui contatori.
    3. Se l'oggetto è una nuova persona, invoca `aggiornaContatori` per incrementare il CUI globale.
    4. Gestisce il caso speciale dell'utente `I0` (IT di default).
* **Flusso di Salvataggio**:
    1. Trasforma l'oggetto in un array di byte cifrati tramite `serializzaEcripta`.
    2. Scrive i byte nel file principale.
    3. Individua il percorso di backup corrispondente tramite `ConfigFile_backup` e replica la scrittura.

#### `public static void scriviSingoloFileCifrato(Path destinazione, Object dati)`
* **Descrizione**: Metodo di utilità "chirurgico" utilizzato esclusivamente dalle classi di **Self-Healing** (come `ControlloDatiIniziale`).
* **Differenza**: A differenza del metodo precedente, scrive i dati *esattamente* nel percorso fornito, senza attivare la logica di ridondanza automatica. È fondamentale per ripristinare un singolo binario corrotto senza influenzare l'altro.

#### `private static <T> void aggiornaContatori(T oggetto)`
* **Descrizione**: Aggiorna il registro globale dei contatori dopo la creazione di un nuovo utente.
* **Logica**: Legge il file dei contatori, identifica il prefisso (`M`, `P`, `S`, `A`, `I`), incrementa il valore e riscrive il file (sia principale che backup) per garantire la coerenza della prossima assegnazione.

#### `private static byte[] serializzaEcripta(Object obj)`
* **Descrizione**: Metodo helper privato che implementa il protocollo di sicurezza.
* **Sequenza**:
    1. **Serializzazione**: Converte l'oggetto in `byte[]` tramite `ObjectOutputStream`.
    2. **Codifica**: Trasforma i byte in una stringa **Base64**.
    3. **Cifratura**: Invia la stringa a `CrittografiaFile` per ottenere l'array di byte cifrato con **AES-256**.

---

## Classe: ConfigFile.java

**Tipo**: Costanti di Sistema / Configurazione dei Percorsi

### Descrizione
La classe `ConfigFile` funge da registro centralizzato per tutti i percorsi (Path) utilizzati dall'applicazione nel sistema principale. È una classe `final` con costruttore privato, progettata per non essere istanziata, ma solo consultata. Centralizzare i percorsi in questa classe permette di modificare l'intera struttura delle cartelle del software intervenendo in un unico punto, garantendo coerenza in tutto il backend.



### Struttura delle Directory
Tutti i percorsi derivano dalla root dei dati situata in `data/file`. La gerarchia è organizzata per separare logicamente le diverse tipologie di utenti e i file di configurazione globale.

#### Percorsi delle Categorie (Directory)
* `DATA_DIR`: La cartella radice (`data/file`) che contiene l'intero database cifrato.
* `MEDICI_DIR`: Destinazione per i profili dei medici (`.dat`).
* `PAZIENTI_DIR`: Destinazione per le cartelle cliniche e anagrafiche dei pazienti.
* `SEGRETARI_DIR`: Destinazione per i dati dei segretari.
* `AMMINISTRATORI_DIR`: Destinazione per i profili amministrativi.
* `IT_DIR`: Destinazione per i profili tecnici dell'IT.

#### File Globali di Sistema
* `CONTATORI_FILE`: Contiene la mappa cifrata dei contatori per l'assegnazione automatica dei CUI.
* `CREDENZIALI_FILE`: Il database delle chiavi di accesso (Username e Password hashate).
* `LOG_FILE`: Il registro cronologico delle operazioni di sistema.

---

## Classe: ConfigFile_backup.java

**Tipo**: Costanti di Sistema / Mapping della Ridondanza

### Descrizione
La classe `ConfigFile_backup` è la controparte speculare di `ConfigFile`. Gestisce la gerarchia dei percorsi all'interno della root di sicurezza `data/backup`. Oltre a definire le posizioni statiche delle directory di riserva, include la logica algoritmica per convertire "al volo" un percorso principale nel suo corrispettivo di backup, permettendo al sistema di mantenere la sincronia dei dati senza hardcoding ripetitivo.



### Struttura delle Directory di Backup
La gerarchia riflette quella principale, ma aggiunge il suffisso `_backup` per distinguere chiaramente i binari di sicurezza.

#### Percorsi delle Categorie (Backup)
* `BACK_DATA_DIR`: Cartella radice della ridondanza (`data/backup`).
* `BACK_MEDICI_DIR`: `data/backup/medici_backup`
* `BACK_PAZIENTI_DIR`: `data/backup/pazienti_backup`
* `BACK_SEGRETARI_DIR`: `data/backup/segretari_backup`
* `BACK_AMMINISTRATORI_DIR`: `data/backup/amministratori_backup`
* `BACK_IT_DIR`: `data/backup/IT_backup`

#### File Globali (Backup)
* `BACK_CONTATORI_FILE`: `data/backup/contatori_backup.dat`
* `BACK_CREDENZIALI_FILE`: `data/backup/credenzialiApp_backup.dat`
* `BACK_LOG_FILE`: `data/backup/log_backup.dat`

### Metodi di Utilità

#### `public static Path getBackupPath(Path mainPath)`
* **Descrizione**: Funzione di traduzione dinamica dei percorsi. Dato un path del sistema principale, restituisce il path dove deve essere salvata la copia di sicurezza.
* **Logica di Conversione**:
    1. **File Globali**: Se il file risiede direttamente nella cartella `file`, il metodo rinomina il file stesso (es: `log.dat` diventa `log_backup.dat`) e lo posiziona nella root del backup.
    2. **File Utenti**: Se il file risiede in una sottocartella (es: `medici/M1.dat`), il metodo mantiene il nome del file originale ma lo sposta nella cartella genitore corretta con suffisso (es: `medici_backup/M1.dat`).
* **Parametri**: `Path mainPath` (il percorso del file originale).
* **Return**: `Path` di backup risolto, oppure `null` se l'input è nullo.

---

## Classe: InitFileSystem.java

**Tipo**: Entry Point di Configurazione / Inizializzatore del Sistema

### Descrizione
La classe `InitFileSystem` è responsabile della creazione della struttura fisica del database al primo avvio assoluto dell'applicazione o in caso di perdita totale dei dati. Il suo compito è preparare l'ambiente operativo, assicurando che tutte le cartelle esistano e che i file globali siano inizializzati con strutture dati valide (mappe o liste vuote), protette da crittografia e pronte per l'uso immediato.



### Caratteristiche del Primo Avvio
* **Bootstrap Tecnico**: Crea automaticamente un utente amministrativo di default (**I0**) per consentire il primo accesso al sistema senza credenziali pregresse.
* **Inizializzazione Proattiva**: Non si limita a creare file vuoti, ma scrive al loro interno gli oggetti serializzati e criptati necessari al corretto funzionamento dei motori di lettura.

### Metodi

#### `public static void init() throws IOException`
* **Descrizione**: Coordina la creazione dell'intera alberatura del file system principale.
* **Logica**: Utilizza `Files.createDirectories` per generare le cartelle definite in `ConfigFile` (Medici, Pazienti, Segretari, Amministratori e IT). Successivamente, tenta di creare e inizializzare i file globali: `contatori.dat`, `credenzialiApp.dat` e `log.dat`.

#### `public static void inizializzaCredenziali(Path file) throws IOException`
* **Descrizione**: Configura l'accesso iniziale al software definendo l'utente tecnico primario.
* **Processo**:
    1. Genera una mappa contenente l'utente **"I0_default"** con la password **"defaultPassword"** (hashata tramite `PasswordUtils`).
    2. Serializza l'oggetto, lo codifica in Base64 e lo cifra tramite `CrittografiaFile`.
    3. Crea fisicamente il profilo IT predefinito (`I0.dat`) tramite `ScritturaFile`, garantendo che l'utente sia presente sia nel sistema principale che nel backup.

#### `private static void inizializzaContatori(Path file) throws IOException`
* **Descrizione**: Predisponde il sistema per l'assegnazione automatica degli ID (CUI).
* **Logica**: Crea una `HashMap` dove ogni prefisso di categoria (`M`, `P`, `S`, `A`, `I`) è impostato a **0**. Questo assicura che il primo inserimento di ogni categoria parta dall'ID numero 1.

#### `public static void inizializzaLog(Path file) throws IOException`
* **Descrizione**: Prepara il registro degli eventi di sistema.
* **Logica**: Inizializza il file con una `ArrayList<Log>` vuota ma correttamente serializzata e criptata, evitando errori di puntatore nullo al primo log generato.

#### `private static void createFileIfNotExists(Path file, boolean inizializzoC, boolean inizializzoCr)`
* **Descrizione**: Metodo helper che agisce da filtro: crea il file fisico solo se effettivamente mancante e delega l'inizializzazione specifica (Contatori, Credenziali o Log) in base ai flag booleani passati.

---

## Classe: InitFileSystem_backup.java

**Tipo**: Entry Point di Configurazione / Inizializzatore del Sistema di Ridondanza

### Descrizione
La classe `InitFileSystem_backup` svolge una funzione speculare a `InitFileSystem`, occupandosi della creazione e dell'inizializzazione dell'intera alberatura delle directory e dei file globali all'interno della root di sicurezza (`data/backup`). Garantisce che il "binario B" del sistema sia pronto a ricevere i dati sincronizzati fin dal primo avvio dell'applicazione.



### Caratteristiche del Backup Iniziale
* **Speculiarità**: Crea una struttura identica a quella principale, ma utilizza i suffissi `_backup` per cartelle e file (es. `medici_backup`, `log_backup.dat`).
* **Sincronia delle Strutture**: Inizializza i file globali di backup con gli stessi valori di default del sistema principale (contatori a zero, credenziali di default e lista log vuota), assicurando che il sistema di *Self-Healing* trovi dati coerenti su entrambi i lati.

### Metodi

#### `public static void init_backup() throws IOException`
* **Descrizione**: Coordina la creazione fisica delle directory di backup.
* **Logica**: Utilizza `Files.createDirectories` per generare le cartelle definite in `ConfigFile_backup`. In seguito, verifica ed eventualmente crea i file globali: `contatori_backup.dat`, `credenzialiApp_backup.dat` e `log_backup.dat`.

#### `private static void inizializzaCredenziali_backup(Path file) throws IOException`
* **Descrizione**: Inizializza il file delle password di riserva.
* **Logica**: Genera una mappa contenente l'utente tecnico primario (**I0_default**), la cripta tramite `CrittografiaFile` e la scrive nel percorso di backup. A differenza della classe principale, non crea il file profilo (`I0.dat`) poiché questo viene gestito dalla logica di scrittura ridondata di `ScritturaFile`.

#### `private static void inizializzaContatori_backup(Path file) throws IOException`
* **Descrizione**: Predisponde il sistema di ID nel settore di backup.
* **Logica**: Crea una `HashMap` con tutti i prefissi utente (`P`, `M`, `S`, `A`, `I`) impostati a **0**. Serializza e cifra la mappa per garantirne l'integrità fin dalla creazione.

#### `public static void inizializzaLog(Path file) throws IOException`
* **Descrizione**: Configura il file di log secondario.
* **Logica**: Crea una `ArrayList<Log>` vuota, la converte in Base64 e la cifra. Questo file servirà da sorgente di ripristino nel caso in cui il log principale dovesse corrompersi durante l'attività.

#### `private static void createFileIfNotExists(...)`
* **Descrizione**: Metodo helper di controllo. Verifica se il file di backup esiste; in caso contrario, procede alla sua creazione fisica e richiama il metodo di inizializzazione specifico.

---
