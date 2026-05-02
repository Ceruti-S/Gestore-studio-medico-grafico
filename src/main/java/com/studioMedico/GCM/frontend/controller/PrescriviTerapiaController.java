package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Terapia;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.nio.file.Path;
import java.time.LocalDate;

public class PrescriviTerapiaController
{

    @FXML private TextField txtCuiPaziente;
    @FXML private TextField txtNomeFarmaco;
    @FXML private TextField txtDosaggio;
    @FXML private TextField txtFrequenza;
    @FXML private DatePicker dpDataInizio;
    @FXML private DatePicker dpDataFine;

    @FXML
    private void gestisciPrescrizioneTerapia()
    {

        String cuiPaziente = txtCuiPaziente.getText();
        String nomeFarmaco = txtNomeFarmaco.getText();
        String dosaggio = txtDosaggio.getText();
        String frequenza = txtFrequenza.getText();
        LocalDate dataInizio = dpDataInizio.getValue();
        LocalDate dataFine = dpDataFine.getValue();


        if(cuiPaziente.isEmpty() || nomeFarmaco.isEmpty() || dataInizio == null || dataFine == null)
        {

            mostraAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila tutti i campi obbligatori.");
            return;

        }

        try
        {

            Path pathPaziente = ConfigFile.PAZIENTI_DIR.resolve(cuiPaziente + ".dat");
            if(!pathPaziente.toFile().exists())
            {

                mostraAlert(Alert.AlertType.ERROR, "Errore", "Paziente non trovato!");
                return;

            }

            Paziente paziente = LetturaFile.leggiFileCifrato(pathPaziente);

            if(paziente != null)
            {

                String cuiMedicoLoggato = ControlloLogin.utenteAttivo;

                paziente.aggiungiTerapia(cuiMedicoLoggato, dataInizio, dataFine, nomeFarmaco, dosaggio, frequenza);

                ScritturaFile.scriviFileCifrato(pathPaziente, paziente);

                mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Terapia prescritta correttamente.");
                pulisciCampi();

            }

        }
        catch(Exception e)
        {

            e.printStackTrace();
            mostraAlert(Alert.AlertType.ERROR, "Errore di salvataggio", "Impossibile salvare la terapia.");

        }

    }

    private void pulisciCampi()
    {

        txtCuiPaziente.clear();
        txtNomeFarmaco.clear();
        txtDosaggio.clear();
        txtFrequenza.clear();
        dpDataInizio.setValue(null);
        dpDataFine.setValue(null);

    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto)
    {

        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();

    }

}
