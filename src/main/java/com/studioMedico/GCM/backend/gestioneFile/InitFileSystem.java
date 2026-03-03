package com.studioMedico.GCM.backend.gestioneFile;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Log;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.PasswordUtils;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.CrittografiaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.IT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Questa classe serve a creare le cartelle e i file globali necessarie all'avvio dell'applicazione
 * Lo fa utilizzando i percorsi creati in ConfigFile.java
 */

public class InitFileSystem
{

    /*
     * Crea le cartelle principali e i file globali inizializzando quelli di cui si ha bisogno, se non esistono le crea,
     * se non può creare lancia @throws IOException
     */
    public static void init() throws IOException
    {

        //creo le cartelle
        Files.createDirectories(ConfigFile.MEDICI_DIR);
        Files.createDirectories(ConfigFile.PAZIENTI_DIR);
        Files.createDirectories(ConfigFile.AMMINISTRATORI_DIR);
        Files.createDirectories(ConfigFile.SEGRETARI_DIR);
        Files.createDirectories(ConfigFile.IT_DIR);

        //creo i file globali
        createFileIfNotExists(ConfigFile.CONTATORI_FILE, true, false);
        createFileIfNotExists(ConfigFile.CREDENZIALI_FILE, false, true);
        createFileIfNotExists(ConfigFile.LOG_FILE, false, false);

    }

    //Se durante questo metodo il programma non trova i file vuol dire che non esistono, li creerà e li inizializzerà
    private static void createFileIfNotExists(Path file, boolean inizializzoC, boolean inizializzoCr) throws IOException
    {

        if(Files.notExists(file))
        {

            Files.createFile(file);

            if(inizializzoC)
            {

                inizializzaContatori(file);

            }
            else if(inizializzoCr)
            {

                inizializzaCredenziali(file);

            }
            else
            {

                inizializzaLog(file);

            }

        }

    }

    //in questo metodo inizializzo il file di log con una struttura a arraylist di log
    public static void inizializzaLog(Path file) throws IOException
    {

        try
        {

            //creo un'array list per contenere tutti i log
            ArrayList<Log> logs = new ArrayList<>();

            //serializzo la lista vuota in memoria
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos))
            {

                oos.writeObject(logs);

            }

            //la converto in base64 e la cripto
            String base64 = java.util.Base64.getEncoder().encodeToString(bos.toByteArray());
            byte[] datiCriptati = CrittografiaFile.criptaContenuto(base64);

            //scrivo sul file
            Files.write(file, datiCriptati);

        }
        catch(Exception e)
        {

            e.printStackTrace();
            throw new IOException("Errore nell'indicizzazione del file log", e);

        }

    }

    /*
     * Inizializza i contatori di medici, pazienti, segretari, amministratori e IT
     * Serializza l'oggetto in memoria, lo cripta e poi lo scrive sul file.
     */
    private static void inizializzaContatori(Path file) throws IOException
    {

        try
        {

            //creo la mappa dei contatori
            Map<String, Integer> contatori = new HashMap<>();
            contatori.put("P", 0);
            contatori.put("M", 0);
            contatori.put("S", 0);
            contatori.put("A", 0);
            contatori.put("I", 0);

            //serializzo in memoria
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos))
            {

                oos.writeObject(contatori);

            }

            //converto i byte serializzati in stringa Base64 (stringa normale) per poterla passare al metodo che la cripta
            String contatoriBase64 = java.util.Base64.getEncoder().encodeToString(bos.toByteArray());

            //cripto la stringa Base64 e la restituisco come array di byte
            byte[] datiCriptati = CrittografiaFile.criptaContenuto(contatoriBase64);

            //scrivo direttamente sul file l'array di byte criptato
            Files.write(file, datiCriptati);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            throw new IOException("Errore nella crittografia dei contatori", e);

        }

    }

    public static void inizializzaCredenziali(Path file) throws IOException
    {

        try
        {

            /*
             * La prima stringa sarà l'username (per gli utenti normali deve
             * essere CUI_liberaScelta), la seconda è la password già hashata prima
             * del cifraggio del file.
             */
            Map<String, String> credenziali = new HashMap<>();

            //creo un account temporaneo IT che sarà eliminato dopo il primo utente reale
            String hashedDefaultPassword = PasswordUtils.hashPassword("defaultPassword");
            credenziali.put("I0_default", hashedDefaultPassword);

            //serializzo la mappa in memoria
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos))
            {

                oos.writeObject(credenziali);

            }

            //converto i byte serializzati in stringa Base64
            String credenzialiBase64 = java.util.Base64.getEncoder().encodeToString(bos.toByteArray());

            //cripto la stringa e scrivo direttamente sul file
            byte[] datiCriptati = CrittografiaFile.criptaContenuto(credenzialiBase64);

            Files.write(file, datiCriptati);
            Path itDefaultFile = ConfigFile.IT_DIR.resolve("I0.dat");

            //non uso Files.createFile(itDefaultFile) perchè lo fa giò scruttura file
            IT itDefault = new IT("I0", "DEFAULT", "USER", LocalDate.of(2000,1,1), 24, "XX", "N/A", "N/A");

            //questo scrive data/file/IT/IT_default.dat E data/backup/IT_backup/IT_default.dat
            ScritturaFile.scriviFileCifrato(itDefaultFile, itDefault);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            throw new IOException("Errore nella crittografia delle credenziali", e);

        }

    }

}
