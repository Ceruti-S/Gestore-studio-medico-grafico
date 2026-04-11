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

public class PrenotaEsameController
{

    @FXML private TextField cuiPazienteEsame;
    @FXML private TextField cuiMedicoEsame;
    @FXML private TextField oraEsame;
    @FXML private TextField nomeEsame;
    @FXML private TextArea noteEsame;
    @FXML private DatePicker dataEsame;

    Path filePaziente;
    Path fileMedico;

    public void prenotaEsame() throws IOException
    {

        String cuiPazienteT = cuiPazienteEsame.getText().trim();

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

        String cuiMedicoT = cuiMedicoEsame.getText().trim();

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

        String nome = nomeEsame.getText().trim();
        String note = noteEsame.getText();
        String ora = oraEsame.getText().trim();
        LocalDate data = dataEsame.getValue();

        if(nome==null || nome.isEmpty() || ora.isEmpty() || ora==null)
        {

            return;

        }

        if(note==null || note.isEmpty())
        {

            note = "-";

        }

        LocalDateTime dataOraCompleta = data.atTime(LocalTime.parse(ora));

        p.aggiungiEsamePrenotato(cuiMedicoT, dataOraCompleta, nome, note);
        m.aggiungiAppuntamentoEsame(cuiPazienteT, dataOraCompleta, nome, note);

        ScritturaFile.scriviFileCifrato(filePaziente, p);
        ScritturaFile.scriviFileCifrato(fileMedico, m);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Esame prenotata");
        alert.setHeaderText("Esame prenotato correttamente.");
        alert.showAndWait();

    }

}
