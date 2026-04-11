package com.studioMedico.GCM.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.Comparator;

public class ListaPazientiController {

    @FXML private TableView<Paziente> tabellaPazienti;
    @FXML private TableColumn<Paziente, String> colNome;
    @FXML private TableColumn<Paziente, String> colCognome;
    @FXML private TableColumn<Paziente, String> colCF;
    @FXML private TableColumn<Paziente, String> colTel;
    @FXML private TableColumn<Paziente, String> colCUI;

    private HomeSegretarioController homeController;

    public void mostraRisultatiRicerca(List<Paziente> risultati)
    {

        tabellaPazienti.setItems(FXCollections.observableArrayList(risultati));

    }

    public void setHomeController(HomeSegretarioController homeController)
    {

        this.homeController = homeController;

    }

    @FXML
    public void initialize() {
        // 1. Colleghiamo le colonne alle proprietà dell'oggetto Paziente
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colCF.setCellValueFactory(new PropertyValueFactory<>("codiceFiscale"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCUI.setCellValueFactory(new PropertyValueFactory<>("CUI"));

        // 2. Gestione del doppio click per i dettagli
        tabellaPazienti.setRowFactory(tv -> {
            TableRow<Paziente> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Paziente p = row.getItem();
                    if (homeController != null) {
                        homeController.caricaSchermata("DettagliPaziente.fxml", p);
                    }
                }
            });
            return row;
        });

        // 3. Carichiamo i dati dal database (file .dat)
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