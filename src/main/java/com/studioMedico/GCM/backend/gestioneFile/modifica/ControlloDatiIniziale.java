package com.studioMedico.GCM.backend.gestioneFile.modifica;

/*
 * Questa classe controlla che tutti i file e le cartelle presenti in file e backup coincidano a quelli in backup, inoltre
 * controlla anche che il contenuto dei file coincidano, nel caso ci sia qualche discrepanza sovrascrivo i dati in file
 * con quelli di backup. Questo controllo avviene dentro la schermata di caricamento dell'app
 * Lancia IOException se c'è un errore nell'accesso dei file
 * nel caso i file di backup siano corrotti ma quelli principali siano leggibili sovrascrivo il backup con i file principali, ma questo è l'unico caso
 * dove i principali possono sovrascrivere il backup e non il contrario.
 * se entrambi i file sono corrotti bisogna andare in recovery mode
 */

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Log;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile_backup;
import com.studioMedico.GCM.backend.gestioneFile.InitFileSystem;
import com.studioMedico.GCM.backend.gestioneFile.InitFileSystem_backup;

public class ControlloDatiIniziale
{

    //controlla che i file presenti in backup siano presenti anche in file
    public static void validaDati() throws IOException
    {

        //lista per seganrsi errori trovati durante il controllo
        List<String> errors = new ArrayList<>();

        //coppie di cartelle: {backup, principale}
        Path[][] dirs = {
                {ConfigFile_backup.BACK_MEDICI_DIR, ConfigFile.MEDICI_DIR},
                {ConfigFile_backup.BACK_PAZIENTI_DIR, ConfigFile.PAZIENTI_DIR},
                {ConfigFile_backup.BACK_SEGRETARI_DIR, ConfigFile.SEGRETARI_DIR},
                {ConfigFile_backup.BACK_AMMINISTRATORI_DIR, ConfigFile.AMMINISTRATORI_DIR},
                {ConfigFile_backup.BACK_IT_DIR, ConfigFile.IT_DIR}
        };

        //ciclo su tutte le coppie di cartelle per controllare i file degli utenti
        /*
         * Questo for controlla che tutti i file e le cartelle, non i file globali, presenti in backup siano
         * presenti anche in file, se non ci sono segna l'errore nella lista
         */
        //più di preciso questo for cicla ogni cartella
        for (Path[] pair : dirs)
        {

            Path backupDir = pair[0];   //cartella backup
            Path mainDir = pair[1];     //cartella principale

            //controllo che la cartella che sto controllando esista in file, se non esiste la creo
            if(Files.notExists(mainDir))
            {

                errors.add("Manca la cartella principale: " + mainDir);
                Files.createDirectories(mainDir);

            }

            //se la cartella di backup esiste
            if (Files.exists(backupDir))
            {

                //legge tutti i file presenti nella cartella di backup e li salva
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir))
                {

                    //questo for cicla ogni file dentro la singola cartella
                    for (Path backupFile : stream)
                    {

                        //costruisce il percorso del file corrispondente nella cartella principale
                        Path correspondingMainFile = mainDir.resolve(backupFile.getFileName());
                        //se il file principale non esiste, segnala un errore e lo crea
                        if (Files.notExists(correspondingMainFile))
                        {

                            //non uso il metodo apposito perchè faccio prima a fare così
                            errors.add("Manca il file principale corrispondente a backup: " + backupFile);

                        }

                    }

                }

            }

        }

        //controllo dei file globali (contatori, credenziali, log)
        Path[][] globalFiles = {
                {ConfigFile_backup.BACK_CONTATORI_FILE, ConfigFile.CONTATORI_FILE},
                {ConfigFile_backup.BACK_CREDENZIALI_FILE, ConfigFile.CREDENZIALI_FILE},
                {ConfigFile_backup.BACK_LOG_FILE, ConfigFile.LOG_FILE}
        };

        /*
         * questo for fa la stessa cosa dell'altro ma controlla solo i file globali
         */
        for (Path[] pair : globalFiles)
        {

            //se il file di backup esiste ma il file principale no, segnala un errore e lo crea
            if (Files.exists(pair[0]) && Files.notExists(pair[1]))
            {

                errors.add("Manca il file principale corrispondente a backup: " + pair[0]);

            }

        }

        //usato per controllo errori
        //stampo i risultati del controllo se le cartelle e i file esistono nella console, per scopi di debug/info
        if (errors.isEmpty())
        {

            System.out.println("Controllo backup-esistenza file/cartelle completato, nessuna violazione dell'integrità rilevata.\nTutti i file e le cartelle corrispondono.");

        }
        else
        {

            System.out.println("Controllo backup-esistenza file/cartelle completato, violazioni dell'integrità rilevate e corrette: ");
            errors.forEach(System.out::println); //stampa ogni errore trovato

        }

        /*
         * ora che sono sicuro che i file e le cartelle esistono controllo che il contenuto dei file coincida, altrimenti
         * sovrascrivo il contenuto di file con quello di backup
         */
        validaContenuto(dirs);

    }

    private static void validaContenuto(Path dirs[][]) throws IOException
    {

        /*
         * procedura da fare:
         * 1)leggere il file di backup (decriptandolo).
         * 2)leggi il file principale (decriptandolo).
         * 3)confronti i dati.
         * 4)se non coincidono sovrascrivi il file principale con il contenuto del backup (decriptato).
         * 5)cripta il contenuto del file principale in cui hai appena scritto il contenuto del file di backup decriptato
         *
         * Prima di poter implementare questo metodo devo implementare criptaggio/decrittaggio file e scrittuta/lettura file
         */

        //ciclo per controllare ogni cartella di file
        for (Path[] pair : dirs)
        {

            Path backupDir = pair[0];
            Path mainDir = pair[1];

            //ora controllo dal backup al principale
            //se la cartella di backup esiste
            if (Files.exists(backupDir))
            {

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir))
                {
                    //controlla ogni file dentro quella cartella
                    for (Path backupFile : stream)
                    {

                        Path mainFile = mainDir.resolve(backupFile.getFileName());

                        //se il file principale esiste
                        if (Files.exists(mainFile))
                        {

                            try
                            {

                                //leggo il backup
                                Object backupObj = LetturaFile.leggiFileCifrato(backupFile);

                                try
                                {
                                    //provo a leggere il principale
                                    Object mainObj = LetturaFile.leggiFileCifrato(mainFile);

                                    //confronto
                                    if (!backupObj.equals(mainObj))
                                    {

                                        //se è diverso vado nel catch
                                        throw new Exception("Dati diversi");

                                    }

                                }
                                catch (Exception e)
                                {

                                    //se il principale è illeggibile o diverso, RIPRISTINO
                                    ScritturaFile.scriviSingoloFileCifrato(mainFile, backupObj);
                                    System.out.println("Ripristinato file utente: " + mainFile.getFileName());

                                }

                            }
                            catch (Exception e) // se il backup è illeggibile
                            {

                                System.err.println("Errore: Backup illeggibile per " + backupFile);

                                try
                                {

                                    //leggo il file principale
                                    Object mainObj = LetturaFile.leggiFileCifrato(mainFile);

                                    //se riesco a leggerlo (non va nel catch) ripristino il file di backup
                                    ScritturaFile.scriviSingoloFileCifrato(backupFile, mainObj);

                                }
                                catch (Exception e1)
                                {

                                    //se anche il file principale è illeggibile bisogna andare perforza in recovery mode
                                    System.err.println("Errore fatale[4]: Backup e file principale illeggibile per " + backupFile + " " + mainFile + "\nAndare in recovery mode per risolvere il problema.");
                                    Log log = new Log(LocalDateTime.now(), "Sistema", "File corrotto irreparabilmente (il secondo è di backup): " + mainFile.getFileName() + " e " + backupFile.getFileName());
                                    try
                                    {

                                        Log.aggiungiLog(log);

                                    }
                                    catch(IOException e2)
                                    {

                                        System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e2.getMessage());

                                    }
                                    System.exit(4);

                                }

                            }

                        }
                        else //il file principal enon esiste proprio, lo creo
                        {

                            //lo creo subito prendendolo dal backup
                            try
                            {

                                Object backupObj = LetturaFile.leggiFileCifrato(backupFile);
                                ScritturaFile.scriviSingoloFileCifrato(mainFile, backupObj);
                                System.out.println("File mancante ripristinato dal backup: " + mainFile.getFileName());

                            }
                            catch (Exception e)
                            {

                                System.err.println("Impossibile ripristinare file mancante, errore fatale[4]: backup corrotto.\nAndare in recovery mode.");
                                Log log = new Log(LocalDateTime.now(), "Sistema", "File corrotto irreparabilmente (il secondo è di backup): " + mainFile.getFileName() + " e " + backupFile.getFileName());
                                try
                                {

                                    Log.aggiungiLog(log);

                                }
                                catch(IOException e2)
                                {

                                    System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e2.getMessage());

                                }
                                System.exit(4);

                            }

                        }

                    }

                }

            }

            //ora controllo dal principale al backup
            //se la cartella principale esiste
            if (Files.exists(mainDir))
            {

                try (DirectoryStream<Path> streamMain = Files.newDirectoryStream(mainDir))
                {

                    for (Path mainFile : streamMain)
                    {

                        //cerco il corrispettivo nel backup
                        Path backupFile = backupDir.resolve(mainFile.getFileName());

                        //se il file NON esiste nel backup, lo creo partendo dal principale
                        if (Files.notExists(backupFile))
                        {

                            try
                            {

                                Object mainObj = LetturaFile.leggiFileCifrato(mainFile);
                                ScritturaFile.scriviSingoloFileCifrato(backupFile, mainObj);
                                System.out.println("File di backup mancante rigenerato dal principale: " + backupFile.getFileName());

                            }
                            catch (Exception e)
                            {

                                //Se il principale è illeggibile, non posso creare il backup.
                                //Non è fatale qui, verrà gestito al prossimo riavvio o ciclo inverso
                                System.err.println("Impossibile creare backup, file principale corrotto: " + mainFile.getFileName());

                            }

                        }

                    }

                }

            }

        }

        //controllo dei file globali (contatori, credenziali, log)
        Path[][] globalFiles = {
                {ConfigFile_backup.BACK_CONTATORI_FILE, ConfigFile.CONTATORI_FILE},
                {ConfigFile_backup.BACK_CREDENZIALI_FILE, ConfigFile.CREDENZIALI_FILE},
                {ConfigFile_backup.BACK_LOG_FILE, ConfigFile.LOG_FILE}
        };

        /*
         * questo for fa la stessa cosa dell'altro ma controlla solo i file globali
         */
        for (Path[] pair : globalFiles)
        {

            Path backupFile = pair[0];
            Path mainFile = pair[1];

            //Caso 1: Il file di backup esiste
            if (Files.exists(backupFile))
            {

                try
                {

                    //leggo SEMPRE il backup
                    Object backupObj = LetturaFile.leggiFileCifrato(backupFile);

                    try
                    {
                        //provo a leggere il file principale
                        Object mainObj = LetturaFile.leggiFileCifrato(mainFile);

                        //Se riesco a leggerlo, controllo se è uguale
                        if (!backupObj.equals(mainObj))
                        {

                            throw new Exception("Contenuto diverso"); //forzo il ripristino, andando nel catch

                        }

                    }
                    catch (Exception e)
                    {

                        //se il principale è corrotto (Exception) o diverso, lo ripristino
                        ScritturaFile.scriviSingoloFileCifrato(mainFile, backupObj);
                        System.out.println("RIPRISTINO: Il file " + mainFile.getFileName() + " era corrotto o alterato ed è stato curato dal backup.");

                    }

                }
                catch (Exception e) //se il backup è illeggibile
                {

                    System.err.println("Errore: Backup illeggibile per " + backupFile);

                    try
                    {

                        //leggo il file principale
                        Object mainObj = LetturaFile.leggiFileCifrato(mainFile);

                        //se riesco a leggerlo (non va nel catch) ripristino il file di backup
                        //NOTA: mainObj viene scritto su backupFile, mantenendo il nome corretto (es. log_backup.dat)
                        ScritturaFile.scriviSingoloFileCifrato(backupFile, mainObj);

                    }
                    catch (Exception e1)
                    {

                        //se anche il file principale è illeggibile bisogna andare perforza in recovery mode
                        System.err.println("Errore fatale[4]: Backup e file principale illeggibile per " + backupFile + " " + mainFile + "\nAndare in recovery mode per risolvere il problema.");
                        Log log = new Log(LocalDateTime.now(), "Sistema", "File corrotto irreparabilmente (il secondo è di backup): " + mainFile.getFileName() + " e " + backupFile.getFileName());
                        try
                        {

                            Log.aggiungiLog(log);

                        }
                        catch(IOException e2)
                        {

                            System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e2.getMessage());

                        }
                        System.exit(4);

                    }

                }

            }
            else //Caso 2: Il backup NON esiste
            {

                //Se il backup non esiste, provo a crearlo dal principale
                try
                {

                    Object mainObj = LetturaFile.leggiFileCifrato(mainFile);
                    //Scrivo sul path del backup (che ha il nome _backup.dat corretto)
                    ScritturaFile.scriviSingoloFileCifrato(backupFile, mainObj);
                    System.out.println("File mancante ripristinato dai file principali: " + backupFile.getFileName());

                }
                catch (Exception e)
                {

                    //Se arrivo qui, il backup manca E il principale è corrotto/mancante.
                    System.err.println("Impossibile ripristinare file mancante, errore fatale[4]: file principale e di backup corrotto.\nAndare in recovery mode.");
                    //estraggo il nome del file log per il confronto
                    String nomeFileLog = ConfigFile.LOG_FILE.getFileName().toString();
                    //estraggo il nome del file contatori per il confronto
                    String nomeFileContatori = ConfigFile.CONTATORI_FILE.getFileName().toString();

                    if(mainFile.getFileName().toString().equals(nomeFileLog))
                    {

                        System.err.println("Impossibile ripristinare file mancante, errore non fatale[2]: file principale di log e di backup di log corrotti.\nRipristino...");
                        //elimino i vecchi file di log corrotti
                        CreazioneEliminazioneFile.eliminaFile(mainFile);
                        //li creo nuovi puliti
                        CreazioneEliminazioneFile.creaFile(ConfigFile.LOG_FILE, "log.dat");
                        //li inizializzo
                        InitFileSystem.inizializzaLog(ConfigFile.LOG_FILE);
                        InitFileSystem_backup.inizializzaLog(ConfigFile_backup.BACK_LOG_FILE);

                        Log log = new Log(LocalDateTime.now(), "Sistema", "Nuova lista dei log creata.");
                        try
                        {

                            ScritturaFile.scriviFileCifrato(ConfigFile.LOG_FILE, log);

                        }
                        catch(Exception e1)
                        {

                            System.out.println("Errore non fatale[1] nella scrittura di un log: " +  e1.getMessage());

                        }

                        return;

                    }
                    else if(mainFile.getFileName().toString().equals(nomeFileContatori))
                    {

                        System.err.println("Impossibile ripristinare file mancante, errore non fatale[2.2]: file principale di contatori e di backup di contatori corrotti.\nRipristino...");
                        HealingFile.ripristinoEmergenzaContatori();
                        return;

                    }
                    else
                    {

                        //qui se le credenziali di entrambi i file sono corrotti

                        //elimino i file delle credenziali corrotti
                        CreazioneEliminazioneFile.eliminaFile(mainFile);
                        //creo i file nuovi
                        CreazioneEliminazioneFile.creaFile(ConfigFile.CREDENZIALI_FILE, "credenzialiApp.dat");
                        //inizializzo il file principale, con le credenziali di default, poi rilancio il controllo
                        //valida contenuto così lo scrive pure su quello di backup
                        InitFileSystem.inizializzaCredenziali(ConfigFile.CREDENZIALI_FILE);
                        validaDati();

                        Log log = new Log(LocalDateTime.now(), "Sistema", "Nuova lista delle credenziali creata.");
                        try
                        {

                            ScritturaFile.scriviFileCifrato(ConfigFile.LOG_FILE, log);

                        }
                        catch(Exception e1)
                        {

                            System.out.println("Errore non fatale[1] nella scrittura di un log: " +  e1.getMessage());

                        }

                        return;

                    }

                }

            }

        }

    }

}
