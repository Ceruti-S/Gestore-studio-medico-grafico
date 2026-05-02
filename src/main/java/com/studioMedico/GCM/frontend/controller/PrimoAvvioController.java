package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.CreazioneEliminazionePersone;
import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class PrimoAvvioController
{

    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private DatePicker dataNascita;
    @FXML private TextField codiceFiscale;
    @FXML private ComboBox<String> titoloStudio;

    @FXML
    private void gestisciAggiunta()
    {

        String nomeInput = nome.getText().trim();
        String cognomeInput = cognome.getText().trim();
        String cfInput = codiceFiscale.getText().trim();
        LocalDate dataInput = dataNascita.getValue();

        String titoloInput = "";
        if(titoloStudio.getValue() != null)
        {

            titoloInput = titoloStudio.getValue().toString();

        }

        if(nomeInput.isEmpty() || cognomeInput.isEmpty() ||
                cfInput.isEmpty() || dataInput == null || titoloInput.isEmpty()) {

            mostraAlert(Alert.AlertType.ERROR, "Errore di Validazione",
                    "Tutti i campi sono obbligatori. Per favore, completa la scheda.");

            return;

        }

        if(!ValidatoreDati.isCodiceFiscaleValido(cfInput) || !ValidatoreDati.isDataDiNascitaValida(dataInput))
        {

            mostraAlert(Alert.AlertType.ERROR, "Errore validazioen dati", "Il codice fiscale/data di nascita inserito non è valido.Samuele");

        }

        try
        {

            ControlloLogin.utenteAttivo = "I0";

            CreazioneEliminazionePersone.creaIT("direttore_IT", titoloInput, nomeInput, cognomeInput, dataInput, cfInput);
            CreazioneEliminazionePersone.eliminaIT("I0");

            mostraAlert(Alert.AlertType.INFORMATION, "Successo",
                    "Il direttore IT " + nomeInput + " " + cognomeInput + " è stato registrato e l'utente di default eliminato, il nuovo utente ha la password di default 'defaultPassword' e username 'I1_" + cognomeInput + "'.");

        }
        catch(Exception e)
        {

            mostraAlert(Alert.AlertType.ERROR, "Errore di Salvataggio",
                    "Impossibile salvare il direttore IT: " + e.getMessage());
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
