package com.studioMedico.GCM.backend.gestioneFile.modifica;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Log;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.Map;
import java.time.LocalDateTime;

public class HealingFile
{

    //nel caso entrambi i file dei contatori siano corrotti DEVO perforza fare in modo di ricostruirli senza perdere
    //il conto, quindi questo metodo li ricrea
    /*
     * metodo per ricostruire un contatore perso scansionando i file fisici.
     * il primo parametro è il percorso della cartella da scansionare (es: data/medici)
     * il secondo parametro è il prefisso identificativa (es: "M", "P", "I", "S")
     * returna il numero massimo trovato + 1
     */
    private static int ricostruisciContatori(Path cartella, String prefisso) throws IOException
    {

        int max = 0;

        //uso stream per andare tra i file della cartella
        try(Stream<Path> stream = Files.list(cartella))
        {

            //trasformo il path in una stringa normale, .filter filtra per i file che iniziano solo col prefisso prestabilito
            //.mapToInt trasforma ogni nome del file in un numero identificativo che contiene
            max = stream.map(Path::getFileName).map(Path::toString).filter(name -> name.startsWith(prefisso)).mapToInt(name ->
            {

                try
                {

                    //estrae il numero tra il prefisso (la lettera) e il primo _
                    String numeroString = name.substring(prefisso.length(), name.indexOf("_"));
                    //converte la stringa in un numero
                    return Integer.parseInt(numeroString);

                }
                catch(Exception e)
                {

                    return 0; //se il nome del file non è valido lo ignora

                }

            }).max().orElse(0); //.max prende il numero trovato più alto, se la cartella è vuota returna 0

        }
        catch(IOException e)
        {

            System.err.println("Impossibile scansionare la cartella per il ripristino: " + e.getMessage());

        }

        //returno il numero massimo trovato +1
        return max+1;

    }

    public static void ripristinoEmergenzaContatori() throws IOException
    {

        //carico la mappa dei contatori (quella che di solito sta nel file .dat)
        Map<String, Integer> nuoviContatori = new HashMap<>();

        //ripristino ogni categoria di contatore
        nuoviContatori.put("M", ricostruisciContatori(ConfigFile.MEDICI_DIR, "M"));
        nuoviContatori.put("P", ricostruisciContatori(ConfigFile.PAZIENTI_DIR, "P"));
        nuoviContatori.put("I", ricostruisciContatori(ConfigFile.IT_DIR, "I"));
        nuoviContatori.put("S", ricostruisciContatori(ConfigFile.SEGRETARI_DIR, "S"));
        nuoviContatori.put("A", ricostruisciContatori(ConfigFile.AMMINISTRATORI_DIR, "A"));

        //salvo la nuova mappa nei file di log
        try
        {

            ScritturaFile.scriviFileCifrato(ConfigFile.CONTATORI_FILE, nuoviContatori);
            Log log = new Log(LocalDateTime.now(), "Sistema", "File dei contatori ripristinati correttamente, col ripristino di emergenza.");
            try
            {

                ScritturaFile.scriviFileCifrato(ConfigFile.LOG_FILE, log);

            }
            catch(Exception e)
            {

                System.out.println("Errore non fatale[1] nella scrittura di un log: " +  e.getMessage());

            }

        }
        catch(IOException e)
        {

            System.err.println("Errore fatale[5], impossibile eseguire il ripristino di emergenza di un file, andare in recovery: " +  e.getMessage());
            System.exit(5);

        }

    }

}
