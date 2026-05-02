package com.studioMedico.GCM.backend.funzionamento;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Log;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile_backup;
import com.studioMedico.GCM.backend.gestioneFile.InitFileSystem;
import com.studioMedico.GCM.backend.gestioneFile.InitFileSystem_backup;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ControlloDatiIniziale;
import com.studioMedico.GCM.frontend.UI.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClass extends Application
{

    public static void main(String[] args)
    {
        //lancia start
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {

        lancioIniziale(primaryStage);

    }

    private static void lanciaMenu(Stage stage)
    {

        char tipoUtente = ControlloLogin.utenteAttivo.charAt(0);

        switch(tipoUtente)
        {

            case 'M':

                HomeMedicoUI homeMedicoUI = new HomeMedicoUI();
                homeMedicoUI.mostraHome(stage);

                break;

            case 'S':

                SchermataHomeSegretarioUI homeSegretarioUI = new SchermataHomeSegretarioUI();
                homeSegretarioUI.mostraHomeSegretario(stage);

                break;

            case 'I':

                String controlloUtente = ControlloLogin.utenteAttivo.substring(0, 2);

                if(controlloUtente.compareTo("I0") == 0)
                {

                    //se è il primo avvio lancio una schermata speciale
                    PrimoAvvioUI primoAvvioUI = new PrimoAvvioUI();
                    primoAvvioUI.mostraSchermata(stage);

                }
                else
                {

                    //se non è il primo avvio lancio normale
                    SchermataHomeItUI homeItUI = new SchermataHomeItUI();
                    homeItUI.mostraHomeIT(stage);

                }

                break;

            case 'A':

                HomeAmministratoreUI homeAmministratore = new HomeAmministratoreUI();
                homeAmministratore.mostraHome(stage);

                break;

            default:
                System.out.println("Errore fatale[3], tipo di utente per lanciare il menù GUI non valido.");
                System.exit(3);

        }

    }

    public static void lancioIniziale(Stage stage) throws IOException
    {

        Path testEsistenzaCredenziali = ConfigFile.CREDENZIALI_FILE;
        Path testEsistenzaCredenziali_backup = ConfigFile_backup.BACK_CREDENZIALI_FILE;

        if(Files.notExists(testEsistenzaCredenziali) && Files.notExists(testEsistenzaCredenziali_backup))
        {

            try
            {

                InitFileSystem_backup.init_backup();
                InitFileSystem.init();
                System.out.println("Cartelle dei dati inizializzate correttamente");

            }
            catch(IOException e)
            {

                System.err.println("Errore fatale[1] durante la creazione delle cartelle: " + e.getMessage());
                System.exit(1);

            }

        }

        //gestione login javafx
        SchermataLoginUI loginUI = new SchermataLoginUI();

        while(true)
        {

            char esito = loginUI.mostraLogin(stage);

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

        if(ControlloLogin.utenteAttivo.charAt(0) == 'R')
        {

            // Lancio la GUI della recovery mode
            System.out.println("Avvio in Recovery Mode...");

        }
        else
        {

            checkFileIniziale();
            lanciaMenu(stage);

        }

    }

    private static void checkFileIniziale()
    {

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