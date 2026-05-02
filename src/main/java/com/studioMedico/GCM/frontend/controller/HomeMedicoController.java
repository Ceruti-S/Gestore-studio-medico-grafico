package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.MainClass;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeMedicoController
{

    @FXML private Label lblNomeUtente;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize()
    {

        if(ControlloLogin.utenteAttivo != null)
        {

            lblNomeUtente.setText(ControlloLogin.utenteAttivo);

        }
        else
        {

            lblNomeUtente.setText("GUEST_USER");

        }

    }

    @FXML
    private void eseguiLogout()
    {

        System.out.println("Esecuzione Logout per l'utente: " + ControlloLogin.utenteAttivo);

        ControlloLogin.utenteAttivo = null;

        Stage currentStage = (Stage) lblNomeUtente.getScene().getWindow();
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

            if(controller instanceof ListaPazientiController)
            {

                ((ListaPazientiController) controller).setHomeController(this);

            }
            else if(controller instanceof DettagliPazienteController && paziente != null)
            {

                ((DettagliPazienteController) controller).inizializzaDati(paziente);

            }

            contentArea.getChildren().add(root);

        }
        catch(IOException e)
        {

            System.err.println("Errore caricamento modulo: " + fxmlFile);
            e.printStackTrace();

        }

    }

    @FXML private void gestisciAperturaListaPazienti() { caricaSchermata("ListaPazienti.fxml", null); }
    @FXML private void gestisciAperturaPrenotaVisita() { caricaSchermata("PrenotaVisita.fxml", null); }
    @FXML private void gestisciAperturaPrenotaEsame() { caricaSchermata("PrenotaEsame.fxml", null); }
    @FXML private void gestisciAperturaPrescriviTerapia() { caricaSchermata("PrescriviTerapia.fxml", null); }
    @FXML private void gestisciAperturaEffettuaVisita() { caricaSchermata("EffetuaVisita.fxml", null); }
    @FXML private void gestisciAperturaEffettuaEsame() { caricaSchermata("EffettuaEsame.fxml", null); }
    @FXML private void gestisciAperturaAggiungiPaziente() { caricaSchermata("AggiungiPaziente.fxml", null); }
    @FXML private void gestisciAperturaModificaPaziente() { caricaSchermata("ModificaPaziente.fxml", null); }

}
