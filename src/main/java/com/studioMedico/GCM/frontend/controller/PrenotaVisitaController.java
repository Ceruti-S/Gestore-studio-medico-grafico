package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Medico;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PrenotaVisitaController
{

    @FXML private TextField cuiPaziente;
    @FXML private TextField cuiMedico;
    @FXML private TextField oraVisita;
    @FXML private TextField motivo;
    @FXML private TextArea note;
    @FXML private DatePicker dataVisita;

    Path filePaziente;
    Path fileMedico;

    public void prenotaVisita() throws IOException
    {

        String cuiPazienteT = cuiPaziente.getText().trim();

        if(cuiPazienteT==null || cuiPazienteT.isEmpty())
            return;

        boolean isAcui = ValidatoreDati.isAcui(cuiPazienteT);

        if(isAcui)
        {

            cuiPazienteT = cuiPazienteT.toUpperCase();
            char prefisso = cuiPazienteT.charAt(0);

            if (prefisso != 'P') {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Tipo sbagliato");
                alert.setHeaderText("Devi inserire un CUI di un paziente per prenotare una visita per lui.");
                alert.showAndWait();
                cuiPazienteT = null;

                return;

            }

            filePaziente = ConfigFile.PAZIENTI_DIR.resolve(cuiPazienteT + ".dat");

            if (!Files.exists(filePaziente)) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Nessun risultato");
                alert.setHeaderText("Nessun paziente trovato con questo CUI.");
                alert.showAndWait();
                cuiPazienteT = null;

                return;

            }

        }
        else
        {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di formato");
            alert.setHeaderText("Il testo non inserito non è un CUI.");
            alert.showAndWait();

        }

        //Se il paziente esiste lo salvo in locale
        Paziente p = LetturaFile.leggiFileCifrato(filePaziente);

        String cuiMedicoT = cuiMedico.getText().trim();

        if(cuiMedicoT==null || cuiMedicoT.isEmpty())
            return;

        boolean isAcui2 = ValidatoreDati.isAcui(cuiMedicoT);

        if(isAcui2)
        {

            cuiMedicoT = cuiMedicoT.toUpperCase();
            char prefisso = cuiMedicoT.charAt(0);

            if (prefisso != 'M') {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Tipo sbagliato");
                alert.setHeaderText("Devi inserire un CUI di un medico per prenotare una visita a suo nome.");
                alert.showAndWait();
                cuiMedicoT = null;

                return;

            }

            fileMedico = ConfigFile.MEDICI_DIR.resolve(cuiMedicoT + ".dat");

            if (!Files.exists(fileMedico)) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Nessun risultato");
                alert.setHeaderText("Nessun medico trovato con questo CUI.");
                alert.showAndWait();
                cuiMedicoT = null;

                return;

            }

        }
        else
        {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di formato");
            alert.setHeaderText("Il testo non inserito non è un CUI.");
            alert.showAndWait();

        }

        //Se il medico esiste lo salvo in locale
        Medico m = LetturaFile.leggiFileCifrato(fileMedico);

        String motivoVisita = motivo.getText().trim();
        String noteVisita = note.getText().trim();

        LocalDate data = dataVisita.getValue();
        String ora = oraVisita.getText().trim();

        if(motivoVisita==null || motivoVisita.isEmpty() || ora.isEmpty() || ora==null)
        {

            return;

        }

        if(noteVisita==null || noteVisita.isEmpty())
        {

            noteVisita = "-";

        }

        LocalDateTime dataOraCompleta = data.atTime(LocalTime.parse(ora));

        p.aggiungiVisitaPrenotata(cuiMedicoT, motivoVisita, noteVisita, dataOraCompleta);
        m.aggiungiAppuntamentoVisita(cuiPazienteT, motivoVisita, noteVisita, dataOraCompleta);

        ScritturaFile.scriviFileCifrato(filePaziente, p);
        ScritturaFile.scriviFileCifrato(fileMedico, m);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Visita prenotata");
        alert.setHeaderText("Visita prenotata correttamente.");
        alert.showAndWait();

    }

}
