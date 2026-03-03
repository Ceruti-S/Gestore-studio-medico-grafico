package com.studioMedico.GCM.backend.funzionamento;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Log;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile_backup;
import com.studioMedico.GCM.backend.gestioneFile.InitFileSystem;
import com.studioMedico.GCM.backend.gestioneFile.InitFileSystem_backup;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ControlloDatiIniziale;
import com.studioMedico.GCM.frontend.SchermataLogin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.io.IOException;

/*
librerie per test di stampa log

import java.nio.file.*;
import java.util.ArrayList;
import com.studioMedico.GCM.backend.gestioneFile.*;
import com.studioMedico.GCM.backend.gestioneFile.modifica.*;
*/


/*
 * si noti che un paziente non può accedere all'app quindi non avrà credenziali.
 * lancioIniziale è fatta in modo che se un utente fa logout quel tasto chiude tutte le finestre aperte in quel moemnto
 * mette utenteAttivo a null e chiama lancioIniziale
 */

public class MainClass
{

    public static void main(String[] args) throws IOException
    {

        lancioIniziale();

    }

    private static void lanciaMenu()
    {

        char tipoUtente = ControlloLogin.utenteAttivo.charAt(0);

        switch(tipoUtente)
        {

            case 'M':
                //lancio il menù GUI del medico
                break;

            case 'S':
                //lancio il menù GUI del segretario
                break;

            case 'I':
                //lancio il menù GUI dell'IT
                break;

            case 'A':
                //lancio il menù GUI dell'amministratore
                break;

            default:
                System.out.println("Errore fatale[3], tipo di utente per lanciare il menù GUI non valido.");
                System.exit(3);

        }

    }

    private static void lancioIniziale() throws IOException
    {

        //creo dei path per testare se è il primo avvio, se esiste anche solo 1 dei 2 non è il primoa avvio e non
        //si entra nell'if altrimenti ci si entra, controllo solo su quei 2 perchè se non esiste nessuno dei 2
        //non si può neanche andare in recovery percui li ricreo proprio manualemnte
        Path testEsistenzaCredenziali, testEsistenzaCredenziali_backup;
        testEsistenzaCredenziali = ConfigFile.CREDENZIALI_FILE;
        testEsistenzaCredenziali_backup = ConfigFile_backup.BACK_CREDENZIALI_FILE;
        if(Files.notExists(testEsistenzaCredenziali) && Files.notExists(testEsistenzaCredenziali_backup))
        {

            /*
             * Al primo avvio dell'app o se entrambi i file delle credenziali sono corrotti in assoluto si devono creare
             * le cartelle che conterranno i file, questo try catch lo fa
             * se le cartelle esistono già non fa niente
             */
            try
            {

                InitFileSystem_backup.init_backup(); //crea e inizializza le cartelle e i file di backup, se mancanti
                InitFileSystem.init(); //crea e inizializza le cartelle e i file, se mancanti
                System.out.println("Cartelle dei dati inizializzate correttamente");

            }
            catch(IOException e)
            {

                System.err.println("Errore fatale[1] durante la creazione delle cartelle: " + e.getMessage());
                System.exit(1);

            }

        }

        /*
         * all'inizio del main lancio la schermata di login, se le credenziali corrispondono e la modalità recovery mode
         * non è stata selezionata lancio checkFileIniziale e setto l'utente attivo col suo CUI, l'utente attivo
         * servirà per controllare le autorizzazioni a fare determinate operazioni e a scrivere l'autore dei log,
         * se è stata selezionata lancio l'app in recovery mode con utente attivo "R_CUI-utente"
         */

        //comando per lanciare la GUI di accesso (nel backend corrisponde a controlloLogin), la GUI di accesso deve
        //returnare true se l'accesso è stato effettuato con successo altrimenti returna false e io la rilancio
        while(true) //dentro al while ci sarà un if col lancio della GUI che se restituisce true esce dal while, se
        //restituisce false il while ricomincia e rilancia la GUI. ovviamente la GUI quando returna true o false si chiude
        {

            //returna F se fallimento, S success, C chiusa
            char esito = SchermataLogin.mostraLogin();

            if(esito == 'S')
                break;
            else if(esito == 'C')
                System.exit(0);

        }

        Log logTemp = new Log(LocalDateTime.now(), "Sistema", "Utente loggato con successo: " + ControlloLogin.utenteAttivo);
        try
        {

            Log.aggiungiLog(logTemp);

        }
        catch(IOException e)
        {

            System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e.getMessage());

        }

        //ora controllo utente attivo per capire se siamo in recovery o no
        //se la prima lettera dell'utente attivo è R vuol dire che siamo in recovery mode
        if(ControlloLogin.utenteAttivo.charAt(0) == 'R')
        {

            //lancio la GUI della recovery mode, senza controlloFileIniziale

        }
        else //se non siamo in recovery
        {

            //lancio il controllo iniziale dei dati
            checkFileIniziale();

            //lancio la GUI del menù iniziale, in base al tipo di utente
            //metodo per determinare quale GUI menù lanciare e lanciarla
            lanciaMenu();

        }

    }

    private static void checkFileIniziale()
    {

        //lancio un controllo che i dati in file coincidano a quelli del backup, nel caso li correggo
        try
        {

            ControlloDatiIniziale.validaDati();

        }
        catch(IOException e)
        {

            System.err.println("Errore fatale[2] durante la validazione dei dati iniziale: " + e.getMessage());
            System.exit(2);

        }

        Log logTemp = new Log(LocalDateTime.now(), "Sistema", "Controllo integrità file completato e avvenuto con successo.");
        try
        {

            Log.aggiungiLog(logTemp);

        }
        catch(IOException e)
        {

            System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e.getMessage());

        }

    }

}
