package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.CreazioneEliminazionePersone;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class AggiungiSegretarioController
{

    @FXML
    private TextField nome;
    @FXML private TextField cognome;
    @FXML private TextField codiceFiscale;
    @FXML private DatePicker dataNascita;

    @FXML
    private void gestisciAggiunta()
    {

        String nomeInput = nome.getText().trim();
        String cognomeInput = cognome.getText().trim();
        String cfInput = codiceFiscale.getText().trim();
        LocalDate dataInput = dataNascita.getValue();

        if(nomeInput.isEmpty() || cognomeInput.isEmpty() ||
                cfInput.isEmpty() || dataInput == null) {

            mostraAlert(Alert.AlertType.ERROR, "Errore di Validazione",
                    "Tutti i campi sono obbligatori. Per favore, completa la scheda.");

            return;

        }

        try
        {

            CreazioneEliminazionePersone.creaSegretario(nomeInput, cognomeInput, dataInput, cfInput);

            mostraAlert(Alert.AlertType.INFORMATION, "Successo",
                    "Il segretario " + nomeInput + " " + cognomeInput + " è stato registrato.");

        }
        catch(Exception e)
        {

            mostraAlert(Alert.AlertType.ERROR, "Errore di Salvataggio",
                    "Impossibile salvare il segretario: " + e.getMessage());
            e.printStackTrace();

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

}
