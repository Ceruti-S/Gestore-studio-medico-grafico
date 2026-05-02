package com.studioMedico.GCM.backend.gestioneFile.modifica;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.IT;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile_backup;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.CrittografiaFile;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;

/*
 * questa classe sovrascrive totalmente quello che c'è dentro il file che gli viene passato con i dati che gli si
 * passano
 */

public class ScritturaFile
{

    /*
     * ATTENZIONE: se il file non esiste alla classe per creare il file va passata solo la cartella dove
     * salvare il file
     */
    //<T> permette di passargli qualsiasi tipo di oggetto da scrivere sul file
    //il parametro path sarà NULL se il file non esiste ancora, sarà NULL quando l'utente clicca "inserisci
    //nuova persona"
    @SuppressWarnings("unchecked")
    public static <T> void scriviFileCifrato(Path file, T oggetto) throws IOException
    {

        //se il file non esiste o è null
        if(file == null || Files.notExists(file))
        {

            //se l'oggetto è istanza di IT
            if(oggetto instanceof IT)
            {

                IT itTemp = (IT)oggetto;

                //se il suo CUI è I0 è l'utente di default
                if("I0".equals(itTemp.getCUI()))
                {

                    file = ConfigFile.IT_DIR.resolve("I0.dat");
                    file = CreazioneEliminazioneFile.creaFile(file.getParent(), file.getFileName().toString());

                }
                else
                {

                    //creo il path completo del nuovo file, se non è l'utente di default
                    file = CostruttorePath.costruisciPathPerOggetto(oggetto);

                    //estraggo il nome del path appena creato per passarlo correttamente
                    file = CreazioneEliminazioneFile.creaFile(file.getParent(), file.getFileName().toString());

                    //incremento i contatori globali
                    aggiornaContatori(oggetto);

                }

            }
            else
            {

                //creo il path completo del nuovo file, se non è l'utente di default
                file = CostruttorePath.costruisciPathPerOggetto(oggetto);

                //estraggo il nome del path appena creato per passarlo correttamente
                file = CreazioneEliminazioneFile.creaFile(file.getParent(), file.getFileName().toString());

                //incremento i contatori globali
                aggiornaContatori(oggetto);

            }

        }

        try
        {

            //serializzo e cripto l'oggetto
            byte[] datiCriptati = serializzaEcripta(oggetto);

            //scrivo il file
            Files.write(file, datiCriptati);

            //scrivo il file di backup
            Path backupFile = ConfigFile_backup.getBackupPath(file);
            if(backupFile != null)
            {

                //se la cartella di backup non esiste la creo
                if(Files.notExists(backupFile.getParent()))
                {

                    Files.createDirectories(backupFile.getParent());

                }

                Files.write(backupFile, datiCriptati);

            }

        }
        catch (Exception e)
        {

            e.printStackTrace();
            throw new IOException("Errore fatele nella scrittura del file cifrato", e);

        }

    }

    public static void aggiungiCredenziali(String username, String passwordHash) throws IOException
    {

        try
        {

            Map<String, String> credenziali = LetturaFile.leggiFileCifrato(ConfigFile.CREDENZIALI_FILE);

            credenziali.put(username, passwordHash);

            byte[] datiCriptati = serializzaEcripta(credenziali);

            Files.write(ConfigFile.CREDENZIALI_FILE, datiCriptati);

            Path backupFile = ConfigFile_backup.getBackupPath(ConfigFile.CREDENZIALI_FILE);
            if(backupFile != null)
            {

                if(Files.notExists(backupFile.getParent()))
                {

                    Files.createDirectories(backupFile.getParent());

                }

                Files.write(backupFile, datiCriptati);

            }

        }
        catch(Exception e)
        {

            throw new IOException("Errore durante l'aggiornamento del file credenziali", e);

        }

    }

    public static void rimuoviCredenziali(String username) throws IOException
    {

        try
        {

            Map<String, String> credenziali = LetturaFile.leggiFileCifrato(ConfigFile.CREDENZIALI_FILE);


            if(credenziali.containsKey(username))
            {

                credenziali.remove(username);

                byte[] datiCriptati = serializzaEcripta(credenziali);
                Files.write(ConfigFile.CREDENZIALI_FILE, datiCriptati);

                Path backupFile = ConfigFile_backup.getBackupPath(ConfigFile.CREDENZIALI_FILE);
                if(backupFile != null)
                {

                    Files.write(backupFile, datiCriptati);

                }

            }

        }
        catch(Exception e)
        {

            throw new IOException("Errore durante la rimozione delle credenziali per: " + username, e);

        }

    }

    /*
     * Metodo di utilità per il ripristino: scrive solo nel percorso indicato
     * senza attivare la logica di backup automatico in entrabe le cartelle
     */
    public static void scriviSingoloFileCifrato(Path destinazione, Object dati) throws IOException
    {

        try
        {

            //serializzazione
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos))
            {

                oos.writeObject(dati);

            }

            //criptaggio
            String base64 = java.util.Base64.getEncoder().encodeToString(bos.toByteArray());
            byte[] datiCriptati = com.studioMedico.GCM.backend.gestioneFile.crittografia.CrittografiaFile.criptaContenuto(base64);

            //scrittura fisica singola
            Files.write(destinazione, datiCriptati);

        }
        catch (Exception e)
        {

            throw new IOException("Errore nel ripristino fisico del file: " + destinazione, e);

        }

    }

    private static <T> void aggiornaContatori(T oggetto) throws IOException
    {

        try
        {

            //salvo in locale i contatori globali
            Map<String, Integer> contatori = LetturaFile.leggiFileCifrato(ConfigFile.CONTATORI_FILE);

            //prendo il tipo dell'oggetto
            String tipo = oggetto.getClass().getSimpleName();
            //prendo il prefisso dell'oggetto
            String prefisso = switch (tipo)
            {

                case "Medico" -> "M";
                case "Paziente" -> "P";
                case "Segretario" -> "S";
                case "Amministratore" -> "A";
                case "IT" -> "I";
                default -> "X";

            };

            //incremento il numero del contatore per il tipo di utente che sto per andare a creare
            int numero = contatori.getOrDefault(prefisso, 0) +1;
            contatori.put(prefisso, numero);

            //scrittura diretta, senza usare il metodo scriviFileCifrato per i byte dei contatori (anche backup)
            byte[] datiContatori = serializzaEcripta(contatori);
            Files.write(ConfigFile.CONTATORI_FILE, datiContatori);
            Files.write(ConfigFile_backup.BACK_CONTATORI_FILE, datiContatori);

        }
        catch(Exception e)
        {

            throw new IOException("Errore aggiornamento contatori: ", e);

        }

    }

    //metodo helper che trasforma in byte l'oggetto, pronto alla scrittura
    private static byte[] serializzaEcripta(Object obj) throws IOException, Exception
    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos))
        {

            oos.writeObject(obj);

        }
        String base64 = java.util.Base64.getEncoder().encodeToString(bos.toByteArray());

        return CrittografiaFile.criptaContenuto(base64);

    }

}
