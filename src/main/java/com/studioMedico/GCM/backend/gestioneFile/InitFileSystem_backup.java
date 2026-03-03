package com.studioMedico.GCM.backend.gestioneFile;

/*
 * Questa classe ha la stessa funzione di InitFileSystem.java ma per i file e le cartelle di backup, apparte l'inizializzazione
 * del file contatori_backup.dat e credenzialiApp_backup.dat
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Log;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.CrittografiaFile;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.PasswordUtils;

public class InitFileSystem_backup
{

    /*
     * Crea le cartelle principali e i file globali inizializzando quelli di cui si ha bisogno, se non esistono le crea,
     * se non può creare lancia @throws IOException
     */

    public static void init_backup() throws IOException
    {

        //creo le cartelle
        Files.createDirectories(ConfigFile_backup.BACK_MEDICI_DIR);
        Files.createDirectories(ConfigFile_backup.BACK_PAZIENTI_DIR);
        Files.createDirectories(ConfigFile_backup.BACK_AMMINISTRATORI_DIR);
        Files.createDirectories(ConfigFile_backup.BACK_SEGRETARI_DIR);
        Files.createDirectories(ConfigFile_backup.BACK_IT_DIR);

        //creo i file globali
        createFileIfNotExists(ConfigFile_backup.BACK_CONTATORI_FILE, true, false);
        createFileIfNotExists(ConfigFile_backup.BACK_CREDENZIALI_FILE, false, true);
        createFileIfNotExists(ConfigFile_backup.BACK_LOG_FILE, false, false);

    }

    private static void createFileIfNotExists(Path file, boolean inizializzoC, boolean inizializzoCr) throws IOException
    {

        if(Files.notExists(file))
        {

            Files.createFile(file);

            if(inizializzoC)
            {

                inizializzaContatori_backup(file);

            }
            else if(inizializzoCr)
            {

                inizializzaCredenziali_backup(file);

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
            ArrayList<Log> logs_backup = new ArrayList<>();

            //serializzo la lista vuota in memoria
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos))
            {

                oos.writeObject(logs_backup);

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

    private static void inizializzaContatori_backup(Path file) throws IOException
    {

        try
        {

            //creo la mappa dei contatori
            Map<String, Integer> contatori_backup = new HashMap<>();
            contatori_backup.put("P", 0);
            contatori_backup.put("M", 0);
            contatori_backup.put("S", 0);
            contatori_backup.put("A", 0);
            contatori_backup.put("I", 0);

            //serializzo in memoria
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos))
            {

                oos.writeObject(contatori_backup);

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

    private static void inizializzaCredenziali_backup(Path file) throws IOException
    {

        try
        {

            /*
             * La prima stringa sarà l'username (per gli utenti normali deve
             * essere CUI_liberaScelta), la seconda è la password già hashata prima
             * del cifraggio del file.
             */
            Map<String, String> credenziali_backup = new HashMap<>();

            //creo un account temporaneo IT che sarà eliminato dopo il primo utente reale
            String hashedDefaultPassword = PasswordUtils.hashPassword("defaultPassword");
            credenziali_backup.put("I0_default", hashedDefaultPassword);

            //serializzo la mappa in memoria
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos))
            {

                oos.writeObject(credenziali_backup);

            }

            //converto i byte serializzati in stringa Base64
            String credenzialiBase64 = java.util.Base64.getEncoder().encodeToString(bos.toByteArray());

            //cripto la stringa e scrivo direttamente sul file
            byte[] datiCriptati = CrittografiaFile.criptaContenuto(credenzialiBase64);

            Files.write(file, datiCriptati);

        }
        catch (Exception e)
        {

            e.printStackTrace();
            throw new IOException("Errore nella crittografia delle credenziali", e);

        }

    }

}
