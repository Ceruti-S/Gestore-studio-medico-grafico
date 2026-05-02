package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.MainClass;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeAmministratoreController
{

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentArea;

    @FXML
    private void eseguiLogout()
    {

        ControlloLogin.utenteAttivo = null;

        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();

        try
        {

            MainClass.lancioIniziale(new Stage());

            System.out.println("Programma riportato alla schermata di Login.");

        }
        catch(Exception e)
        {

            e.printStackTrace();

        }

    }

    public void caricaSchermata(String fxmlFile, Paziente paziente)
    {

        try
        {

            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();

            contentArea.getChildren().add(root);

        }
        catch(IOException e)
        {

            System.err.println("Errore caricamento modulo: " + fxmlFile);
            e.printStackTrace();

        }

    }

    @FXML private void apriGestionePazienti() { caricaSchermata("GestioneUtentiIT.fxml", null); }
    @FXML private void apriGestioneStaff() { caricaSchermata("GestioneStaff.fxml", null); }
    @FXML private void apriCreazioneIT() {caricaSchermata("AggiungiIT.fxml", null);}
    @FXML private void apriCreazioneMedici() {caricaSchermata("AggiungiMedico.fxml", null);}
    @FXML private void apriCreazioneSegretari() {caricaSchermata("AggiungiSegretario.fxml", null);}



}
