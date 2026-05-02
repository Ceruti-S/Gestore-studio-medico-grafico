package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Medico;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EffettuaEsameController
{

    @FXML private TextField cuiPaziente;
    @FXML private TextField nomeEsame;
    @FXML private TextArea diagnosi;
    @FXML private TextArea note;

    Path filePaziente;

    @FXML
    private void registraEsame() throws IOException
    {

        String diagnosiT = diagnosi.getText();

        String cuiPazienteT = cuiPaziente.getText().trim();

        if(cuiPazienteT==null || cuiPazienteT.isEmpty())
            return;

        boolean isAcui = ValidatoreDati.isAcui(cuiPazienteT);

        if(isAcui)
        {

            cuiPazienteT = cuiPazienteT.toUpperCase();
            char prefisso = cuiPazienteT.charAt(0);

            if(prefisso != 'P') {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Tipo sbagliato");
                alert.setHeaderText("Devi inserire un CUI di un paziente per effettuare un esame per lui.");
                alert.showAndWait();
                cuiPazienteT = null;

                return;

            }

            filePaziente = ConfigFile.PAZIENTI_DIR.resolve(cuiPazienteT + ".dat");

            if(!Files.exists(filePaziente)) {

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

        Paziente p = LetturaFile.leggiFileCifrato(filePaziente);

        String cuiMedicoT = ControlloLogin.utenteAttivo;

        if(cuiMedicoT==null || cuiMedicoT.isEmpty())
            return;

        boolean isAcui2 = ValidatoreDati.isAcui(cuiMedicoT);

        if(isAcui2)
        {

            cuiMedicoT = cuiMedicoT.toUpperCase();
            char prefisso = cuiMedicoT.charAt(0);

            if(prefisso != 'M') {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Tipo sbagliato");
                alert.setHeaderText("Devi inserire un CUI di un medico per effettuare un esame a suo nome.");
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

        String nomeEsameT = nomeEsame.getText();
        String noteVisita = note.getText().trim();

        LocalDateTime dataOraCompleta = LocalDateTime.now();

        if(noteVisita==null || noteVisita.isEmpty())
        {

            noteVisita = "-";

        }

        p.aggiungiEsameEffettuato(cuiMedicoT, diagnosiT, nomeEsameT, noteVisita);

        ScritturaFile.scriviFileCifrato(filePaziente, p);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Esame registrato");
        alert.setHeaderText("Esame registrato correttamente.");
        alert.showAndWait();

    }

}
