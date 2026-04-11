package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.MainClass;
import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//TODO: finire gli altri todo + implementare agenda studio + tutte le altre schermate

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

    @FXML
    private void gestisciRicerca()
    {

        String query = txtRicercaRapida.getText().trim();

        if (query.isEmpty())
        {

            System.out.println("Ricerca vuota.");
            return;

        }

        if(ValidatoreDati.isAcui(query))
        {

            String queryUpper = query.toUpperCase();

            if (queryUpper.startsWith("P"))
            {

                Path filePaziente = ConfigFile.PAZIENTI_DIR.resolve(queryUpper + ".dat");

                if (!Files.exists(filePaziente))
                {

                    mostraAlert(Alert.AlertType.ERROR, "Paziente non trovato", "Nessun CUI: " + queryUpper);

                }
                else
                {

                    try
                    {

                        Paziente p = LetturaFile.leggiFileCifrato(filePaziente);
                        caricaSchermata("DettagliPaziente.fxml", p);

                    }
                    catch(IOException e)
                    {

                        e.printStackTrace();

                    }

                }

            }
            else
            {

                mostraAlert(Alert.AlertType.WARNING, "Permesso negato", "Puoi cercare solo pazienti (CUI che iniziano con P).");

            }

        }
        else
        {

            List<Paziente> risultati = new ArrayList<>();

            try (Stream<Path> files = Files.list(ConfigFile.PAZIENTI_DIR))
            {

                List<Path> pathList = files.filter(f -> f.toString().endsWith(".dat")).toList();

                for (Path file : pathList)
                {

                    Paziente p = LetturaFile.leggiFileCifrato(file);
                    if (p != null && p.getCognome().equalsIgnoreCase(query))
                    {

                        risultati.add(p);

                    }

                }

            }
            catch (IOException e)
            {

                System.err.println("Errore critico durante la scansione file: " + e.getMessage());

            }

            if (risultati.isEmpty())
            {

                mostraAlert(Alert.AlertType.INFORMATION, "Nessun risultato", "Nessun paziente trovato.");

            }
            else if (risultati.size() == 1)
            {

                caricaSchermata("DettagliPaziente.fxml", risultati.getFirst());

            }
            else
            {

                try
                {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaPazienti.fxml"));
                    Parent root = loader.load();

                    ListaPazientiController controllerLista = loader.getController();
                    controllerLista.setHomeController(this);
                    controllerLista.mostraRisultatiRicerca(risultati);

                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(root);

                }
                catch (IOException e)
                {

                    System.err.println("Errore nel caricamento della lista filtrata: " + e.getMessage());
                    mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile caricare la lista dei risultati.");

                }

            }

        }

    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String messaggio)
    {

        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();

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

        // 2. Recupera lo Stage attuale e chiudilo
        Stage currentStage = (Stage) lblNomeUtente.getScene().getWindow();
        currentStage.close();

        // 3. Rilancia la schermata di login
        try {

            MainClass.lancioIniziale(new Stage());

            System.out.println("Programma riportato alla schermata di Login.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MOTORE DI NAVIGAZIONE INTERNA
     * Carica i file FXML delle varie sezioni nel centro della finestra.
     */
    public void caricaSchermata(String fxmlFile, Paziente paziente) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();

            // SE stiamo aprendo la lista, passiamo "this" (HomeSegretarioController)
            if (controller instanceof ListaPazientiController) {
                ((ListaPazientiController) controller).setHomeController(this);
            }
            // SE stiamo aprendo i dettagli, passiamo il paziente
            else if (controller instanceof DettagliPazienteController && paziente != null) {
                ((DettagliPazienteController) controller).inizializzaDati(paziente);
            }

            contentArea.getChildren().add(root);

        } catch (IOException e) {
            System.err.println("Errore caricamento modulo: " + fxmlFile);
            e.printStackTrace();
        }
    }

    // --- PULSANTI BARRA LATERALE ---
    @FXML private void gestisciAperturaListaPazienti() { caricaSchermata("ListaPazienti.fxml", null); }

    @FXML private void gestisciAperturaAggiungiPaziente() { caricaSchermata("AggiungiPaziente.fxml", null); }

    @FXML private void gestisciAperturaPrenotaVisita() { caricaSchermata("PrenotaVisita.fxml", null); }

    @FXML private void gestisciAperturaPrenotaEsame() { caricaSchermata("PrenotaEsame.fxml", null); }

    @FXML private void gestisciAperturaModificaPaziente() { caricaSchermata("ModificaPaziente.fxml", null); }

    @FXML private void gestisciAperturaModificaPrenotazione() { caricaSchermata("ModificaPrenotazione.fxml", null); }

    @FXML private void gestisciAperturaAgenda() { caricaSchermata("AgendaStudio.fxml", null); }

}