package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeSegretarioController {

    @FXML private Label lblNomeUtente;
    @FXML private TextField txtRicercaRapida;
    @FXML private StackPane contentArea;

    /**
     * Inizializzazione: imposta l'ID dell'utente loggato nell'header.
     */
    @FXML
    public void initialize() {
        if (ControlloLogin.utenteAttivo != null) {
            lblNomeUtente.setText(ControlloLogin.utenteAttivo);
        } else {
            lblNomeUtente.setText("GUEST_USER");
        }
    }

    /**
     * LOGICA RICERCA (Gestita direttamente qui)
     * Legge il testo dalla barra in alto e stampa il comando.
     */
    @FXML
    private void gestisciRicerca() {
        String query = txtRicercaRapida.getText().trim();

        if (query.isEmpty()) {
            System.out.println("Ricerca vuota: inserire un CUI o Cognome.");
            return;
        }

        // Qui integrerai la chiamata al tuo database/lista pazienti
        System.out.println("Eseguo ricerca nel database per: " + query);

        // Esempio: potresti voler svuotare il centro e mostrare un messaggio
        contentArea.getChildren().clear();
        Label lblRisultato = new Label("Risultati ricerca per: " + query);
        lblRisultato.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");
        contentArea.getChildren().add(lblRisultato);
    }

    /**
     * LOGICA LOGOUT (Gestita direttamente qui)
     * Chiude la sessione e la finestra attuale.
     */
    @FXML
    private void gestisciLogout() {
        System.out.println("Esecuzione Logout per l'utente: " + ControlloLogin.utenteAttivo);

        // 1. Resetta la sessione nel backend
        ControlloLogin.utenteAttivo = null;

        // 2. Chiude lo Stage (la finestra) attuale
        // Prendiamo la scena da un qualsiasi elemento grafico (es. lblNomeUtente)
        Stage stage = (Stage) lblNomeUtente.getScene().getWindow();
        stage.close();

        // Nota: Il tuo MainClass rileverà la chiusura e mostrerà di nuovo il Login
    }

    /**
     * MOTORE DI NAVIGAZIONE INTERNA
     * Carica i file FXML delle varie sezioni nel centro della finestra.
     */
    private void caricaSchermata(String fxmlFile) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            System.err.println("Errore caricamento modulo: " + fxmlFile);
            e.printStackTrace();
        }
    }

    // --- PULSANTI BARRA LATERALE ---
    @FXML private void gestisciAperturaListaPazienti() { caricaSchermata("ListaPazienti.fxml"); }
    @FXML private void gestisciAperturaAggiungiPaziente() { caricaSchermata("AggiungiPaziente.fxml"); }
    @FXML private void gestisciAperturaPrenotaVisita() { caricaSchermata("PrenotaVisita.fxml"); }
    @FXML private void gestisciAperturaPrenotaEsame() { caricaSchermata("PrenotaEsame.fxml"); }
    @FXML private void gestisciAperturaModificaPaziente() { caricaSchermata("ModificaPaziente.fxml"); }
    @FXML private void gestisciAperturaModificaPrenotazione() { caricaSchermata("ModificaPrenotazione.fxml"); }
    @FXML private void gestisciAperturaAgenda() { caricaSchermata("AgendaStudio.fxml"); }
}