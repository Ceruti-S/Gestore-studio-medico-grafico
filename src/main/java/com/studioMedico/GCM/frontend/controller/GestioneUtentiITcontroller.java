package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.CreazioneEliminazionePersone;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class GestioneUtentiITcontroller
{

    @FXML private TableView<Paziente> tabellaPazienti;
    @FXML private TableColumn<Paziente, String> colNome;
    @FXML private TableColumn<Paziente, String> colCognome;
    @FXML private TableColumn<Paziente, String> colCF;
    @FXML private TableColumn<Paziente, String> colTel;
    @FXML private TableColumn<Paziente, String> colCUI;

    @FXML
    private void eliminaUtente() throws IOException
    {

        Paziente pazienteSelezionato = tabellaPazienti.getSelectionModel().getSelectedItem();

        if(pazienteSelezionato == null)
        {

            mostraAlert(Alert.AlertType.WARNING, "Selezione mancante",
                    "Per favore, seleziona un paziente dalla tabella prima di cliccare su Elimina.");
            return;

        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma Eliminazione");
        conferma.setHeaderText("Stai per eliminare il paziente: " + pazienteSelezionato.getCognome());
        conferma.setContentText("L'operazione è irreversibile. Vuoi procedere?");

        if(conferma.showAndWait().get() == ButtonType.OK)
        {

            String cui = pazienteSelezionato.getCUI();

            CreazioneEliminazionePersone.eliminaPaziente(cui);

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

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colCF.setCellValueFactory(new PropertyValueFactory<>("codiceFiscale"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCUI.setCellValueFactory(new PropertyValueFactory<>("CUI"));

        caricaDatiPazienti();

    }

    @FXML
    private void caricaDatiPazienti() {
        ObservableList<Paziente> listaPazienti = FXCollections.observableArrayList();
        Path pathPazienti = ConfigFile.PAZIENTI_DIR;

        try {
            if (Files.exists(pathPazienti)) {
                try (Stream<Path> stream = Files.list(pathPazienti)) {
                    stream.filter(file -> file.toString().endsWith(".dat"))
                            .forEach(file -> {
                                try {
                                    Paziente p = LetturaFile.leggiFileCifrato(file);
                                    if (p != null) {
                                        listaPazienti.add(p);
                                    }
                                } catch (Exception e) {
                                    System.err.println("Errore nel caricamento del file: " + file + " - " + e.getMessage());
                                }
                            });
                }
            }

            // Ordinamento alfabetico
            listaPazienti.sort(Comparator.comparing(Paziente::getNome)
                    .thenComparing(Paziente::getCognome));

            tabellaPazienti.setItems(listaPazienti);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di caricamento");
            alert.setHeaderText("Errore di caricamento della lista pazienti.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}
