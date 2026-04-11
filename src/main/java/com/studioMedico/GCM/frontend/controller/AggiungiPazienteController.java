package com.studioMedico.GCM.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import com.studioMedico.GCM.backend.funzionamento.CreazioneEliminazionePersone;

public class AggiungiPazienteController 
{

    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private DatePicker dataNascita;
    @FXML private TextField codiceFiscale;
    @FXML private ComboBox gruppoSanguigno;
    @FXML private TextArea allergie;
    @FXML private TextArea patologie;
    @FXML private TextField telefono;
    @FXML private TextField indirizzo;
    
    @FXML
    public void gestisciAggiunta() throws IOException
    {

        String nomePaziente = nome.getText().trim();
        String cognomePaziente = cognome.getText().trim();
        String codiceFiscalePaziente = codiceFiscale.getText().trim();
        String allergiePaziente = allergie.getText();
        String patologiePaziente = patologie.getText();
        LocalDate data = dataNascita.getValue();
        String telefonoPaziente = telefono.getText().trim();
        String indirizzoPaziente = indirizzo.getText().trim();

        Object selezione = gruppoSanguigno.getValue();

        if(selezione == null)
        {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dati mancanti");
            alert.setHeaderText(null);
            alert.setHeaderText("Prima di aggiungere un paziente devi compilare tutti i campi. (tranne allergie e patologie che sono facoltativi)");
            alert.showAndWait();

            return;

        }

        String gruppoSanguignoPaziente = selezione.toString();

        if(nomePaziente==null || cognomePaziente==null || codiceFiscalePaziente==null || data==null || telefonoPaziente==null || indirizzoPaziente==null)
        {

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Dati mancanti");
            alert.setHeaderText("Prima di aggiungere un paziente devi compilare tutti i campi. (tranne allergie e patologie che sono facoltativi)");
            alert.showAndWait();

            return;

        }

        if(allergiePaziente!=null)
        {

            String[] allergieArray = allergiePaziente.split(", ");

        }
        if(patologiePaziente!=null)
        {

            String[] patologieArray = patologiePaziente.split(", ");

        }

        boolean creato = false;

        if(allergiePaziente==null && patologiePaziente==null)
        {

            creato = CreazioneEliminazionePersone.creaPaziente(telefonoPaziente, indirizzoPaziente, gruppoSanguignoPaziente, nomePaziente, cognomePaziente, data, codiceFiscalePaziente, null, null);

        }

        if(allergiePaziente!=null && patologiePaziente!=null)
        {

            String[] patologieArray = patologiePaziente.split(", ");
            String[] allergieArray = allergiePaziente.split(", ");
            creato = CreazioneEliminazionePersone.creaPaziente(telefonoPaziente, indirizzoPaziente, gruppoSanguignoPaziente, nomePaziente, cognomePaziente, data, codiceFiscalePaziente, allergieArray, patologieArray);

        }
        else if(patologiePaziente!=null && allergiePaziente==null)
        {

            String[] patologieArray = patologiePaziente.split(", ");
            creato = CreazioneEliminazionePersone.creaPaziente(telefonoPaziente, indirizzoPaziente, gruppoSanguignoPaziente, nomePaziente, cognomePaziente, data, codiceFiscalePaziente, null, patologieArray);

        }
        else if(allergiePaziente!=null && patologiePaziente==null)
        {

            String[] allergieArray = allergiePaziente.split(", ");
            creato = CreazioneEliminazionePersone.creaPaziente(telefonoPaziente, indirizzoPaziente, gruppoSanguignoPaziente, nomePaziente, cognomePaziente, data, codiceFiscalePaziente, allergieArray, null);

        }

        if(creato)
        {

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Paziente creato");
            alert.setHeaderText("Paziente creato con successo.");
            alert.showAndWait();

        }
        else
        {

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Paziente non creato");
            alert.setHeaderText("Errore nella creazione del paziente.");
            alert.showAndWait();

        }

    }
    
}
