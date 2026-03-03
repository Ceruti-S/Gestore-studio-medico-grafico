package com.studioMedico.GCM.backend.funzionamento;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.IT;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile_backup;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.PasswordUtils;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;

public class ControlloLogin
{

    //variabile globale accessibile da tutti sempre che indica che utente è attivo al momento
    //utente attivo sarà l'inzio del nome utente prima del _, quindi il CUI della persona
    //l'utente attivo sarà "R" + "_" + CUI se l'accesso è il recovery mode
    public static String utenteAttivo;

    private static String CUI;

    /*
     * questa classe controlla le credenziali di accesso, se sono valide e la recovery mode non è stata selezionata returna
     * "login_success", se la recovery mode è selezionata returna
     * "recovery_login_success", se è selezionata ma l'utente non può usarla returna "recovery_access_denied".
     *  se le credenziali di accesso non sono valide returna
     * "login_fail", returnerà "ERRORE" se c'è stato un errore nel metodo
     */

    //questo metodo ha parametri perchè quando verrà chiamato la GUI deve aver già preso i dati dalla schermata e glieli
    //deve passare già validati, ma non convertiti (la password non criptata)
    //questo metodo viene chiamato al click del tasto "login"
    //se recovery è true vuol dire che il programma vuole essere avviato in recovery
    public static String controlloCredenziali(String nomeUtente, String passwordUtente, boolean recovery) throws IOException
    {

        //cripto la password data in HASH
        String passwordHash = PasswordUtils.hashPassword(passwordUtente);

        Map<String,String> credenziali = new HashMap<>();
        Map<String,String> credenziali_backup = new HashMap<>();

        //carico in locale i dati di login salvati sul file per controllare, quelli dei dati principali, se il file esiste
        if(Files.exists(ConfigFile.CREDENZIALI_FILE))
        {

            credenziali = (Map<String, String>) LetturaFile.leggiFileCifrato(ConfigFile.CREDENZIALI_FILE);


        }
        //carico in locale i dati di login salvati sul file per controllare, quelli dei dati di backup, se il file esiste
        if(Files.exists(ConfigFile_backup.BACK_CREDENZIALI_FILE))
        {

            credenziali_backup = (Map<String, String>) LetturaFile.leggiFileCifrato(ConfigFile_backup.BACK_CREDENZIALI_FILE);

        }

        //faccio il doppio check se esistono nelle credenziali principali o in quelle di backup perchè non ho ancora fatto
        //il check di integrità dei dati quindi se uno è corrotto c'è l'altro
        if(credenziali.containsKey(nomeUtente) || credenziali_backup.containsKey(nomeUtente))
        {

            //Sto dicendo alla mappa di cercare la key nome utente e poi di darmi il valore della password
            //se l'utente non c'è nel file, passwordFile sarà "" e il confronto .equals fallirà invece di crashare
            String passwordFile = credenziali.getOrDefault(nomeUtente, "");
            String passwordBackup = credenziali_backup.getOrDefault(nomeUtente, "");

            //se la password inserita è uguale a quella trovata
            if(passwordFile.equals(passwordHash) || passwordBackup.equals(passwordHash))
            {

                //prendo solo il CUI che è all'inizio del nome utente, prima del _
                CUI = nomeUtente.substring(0, nomeUtente.indexOf('_'));

                //se la modalità recovery è stata selezionata
                if(recovery)
                {

                    //devo controllare se l'utente può usare la recovery, quindi deve essere un IT di grado capo
                    //Se si ha il permesso
                    if(controlloPermessoRecovery(nomeUtente, CUI))
                    {

                        utenteAttivo = "R" + "_" + CUI;

                        return "recovery_login_success";

                    }
                    else //Se non lo si ha
                    {

                        return "recovery_access_denied";

                    }

                }
                else //Se non è stata selezionata
                {

                    utenteAttivo = CUI;

                    //returno "login_success" per far capire alla GUI che il login è andato a buon fine
                    return "login_success";

                }

            }
            else
            {

                return "login_fail";

            }

        }
        else
        {

            return "login_fail";

        }

    }

    private static boolean controlloPermessoRecovery(String nomeUtente, String CUI) throws IOException
    {

        //prendo il tipo di utente che sta facendo l'accesso
        char tipoUtente = CUI.charAt(0);
        //prendo il contatore dell'utente
        String contatoreUtente = CUI.substring(1);
        //variabile per il file della persona
        Path file;
        Path fileBackup;

        switch (tipoUtente)
        {

            case 'I':
                file = ConfigFile.IT_DIR.resolve(tipoUtente + contatoreUtente + ".dat");
                fileBackup = ConfigFile_backup.BACK_IT_DIR.resolve(tipoUtente + contatoreUtente + ".dat");
                break;

            //va tutto in return false perchè se non è IT non può accedere alla recovery mode
            default:
                return false;

        }

        Object oggettoUtente;

        //ora che ho trovato il path del file dell'utente IT lo salvo
        //se file esiste
        if(Files.exists(file))
        {

            oggettoUtente = LetturaFile.leggiFileCifrato(file);

        }
        else if(Files.exists(fileBackup)) //se non esiste lo faccio con quello di backup
        {

            oggettoUtente = LetturaFile.leggiFileCifrato(fileBackup);

        }
        else //nel caso non esistesse returno false, perchè tanto non lo fa accedere se non ha il permesso di usare la
        {    //recovery

            return false;

        }

        //ora controllo se il grado dell'utente IT è abbastanza alto da usare la recovery
        //se oggettoUtente è un'istanza di IT
        if(oggettoUtente instanceof IT)
        {

            IT utenteIT = (IT) oggettoUtente;

            //se il grado dell'utente IT è direttore_IT
            if(utenteIT.getGradoReparto().equals("direttore_IT"))
            {

                return true;

            }
            else //Se non lo è
            {
                return false;
            }

        }
        else //se non lo è
        {

            return false;

        }

    }

}
