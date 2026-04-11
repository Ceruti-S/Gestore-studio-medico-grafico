package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.nio.file.Path;
import java.time.LocalDate;

public class ModificaPazienteController
{

    @FXML private TextField cuiRicerca;
    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private TextField codiceFiscale;
    @FXML private TextField telefono;
    @FXML private TextField indirizzo;
    @FXML private DatePicker dataNascita;
    @FXML private ComboBox gruppoSanguigno;
    @FXML private TextArea allergie;
    @FXML private TextArea patologie;

    Path filePaziente;
    String cui;

    public void applicaModifiche() throws IOException
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

            Alert alert = new Alert(Alert.AlertType.ERROR);
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

        Paziente p = LetturaFile.leggiFileCifrato(filePaziente);

        if(cui==null || cui.isEmpty())
            return;

        boolean isAcui = ValidatoreDati.isAcui(cui);

        if(isAcui)
        {

            p.setNome(nomePaziente);
            p.setCognome(cognomePaziente);
            p.setDataNascita(data);
            p.setIndirizzo(indirizzoPaziente);
            p.setCodiceFiscale(codiceFiscalePaziente);
            p.setTelefono(telefonoPaziente);

            if(allergiePaziente!=null)
            {

                String[] allergieArray = allergiePaziente.split(", ");

                for(int i=0; i<allergieArray.length; i++)
                {

                    p.aggiungiAllergia(allergieArray[i]);

                }

            }
            if(patologiePaziente!=null)
            {

                String[] patologieArray = patologiePaziente.split(", ");

                for(int i=0; i<patologieArray.length; i++)
                {

                    p.aggiungiPatologia(patologieArray[i]);

                }

            }

            ScritturaFile.scriviFileCifrato(filePaziente, p);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dati modificati");
            alert.setHeaderText("Dati del paziente modificati correttamente.");
            alert.showAndWait();

        }

    }

    public void caricaDati() throws IOException
    {

        cui = cuiRicerca.getText().trim();

        if(cui==null || cui.isEmpty())
            return;

        boolean isAcui = ValidatoreDati.isAcui(cui);

        if(isAcui)
        {

            cui = cui.toUpperCase();
            char prefisso = cui.charAt(0);

            if(prefisso!='P')
            {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Permessi mancanti");
                alert.setHeaderText("Hai il permesso di caricare dati solo di pazienti.");
                alert.showAndWait();
                cui=null;

                return;

            }

            filePaziente = ConfigFile.PAZIENTI_DIR.resolve(cui + ".dat");

            if(!Files.exists(filePaziente))
            {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Nessun risultato");
                alert.setHeaderText("Nessun paziente trovato con questo CUI.");
                alert.showAndWait();
                cui=null;

                return;

            }

            Paziente p = LetturaFile.leggiFileCifrato(filePaziente);

            nome.setText(p.getNome());
            cognome.setText(p.getCognome());
            dataNascita.setValue(p.getDataNascita());
            codiceFiscale.setText(p.getCodiceFiscale());
            indirizzo.setText(p.getIndirizzo());
            telefono.setText(p.getTelefono());
            allergie.setText(p.getAllergie());
            patologie.setText(p.getPatologie());
            gruppoSanguigno.setValue(p.getGruppoSanguigno());

        }
        else
        {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dati errati");
            alert.setHeaderText("Il testo inserito non è un CUI valido.");
            alert.showAndWait();

            cui=null;

        }

    }

}
