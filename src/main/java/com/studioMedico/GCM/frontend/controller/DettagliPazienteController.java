package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Esame;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Paziente;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Terapia;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.Visita;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.List;

public class DettagliPazienteController
{

    @FXML private Text nome, cognome, dataNascita, eta, codiceFiscale, gruppoSanguigno, allergie, patologie, telefono, indirizzo;

    @FXML private TableView<VoceVisitaStorico> tabellaStoricoVisite;
    @FXML private TableColumn<VoceVisitaStorico, String> colCuiSVisite, colDataSVisite, colDiagnosiSVisite, colNoteSVisite;

    @FXML private TableView<VoceEsameStorico> tabellaStoricoEsami;
    @FXML private TableColumn<VoceEsameStorico, String> colCuiSEsami, colDataSEsami, colRisultatoSEsami, colNoteSEsami;

    @FXML private TableView<VoceVisitaPrenotata> tabellaPrenotazioniVisite;
    @FXML private TableColumn<VoceVisitaPrenotata, String> colCuiPVisite, colDataPVisite, colMotivoPVisite, colNotePVisite;

    @FXML private TableView<VoceEsamePrenotato> tabellaPrenotazioniEsami;
    @FXML private TableColumn<VoceEsamePrenotato, String> colCuiPEsami, colDataPEsami, colNotePEsami;

    @FXML private TableView<VoceTerapia> tabellaPrenotazioniTerapie;
    @FXML private TableColumn<VoceTerapia, String> colCuiPTerapie, colDataPTerapie, colDosaggioPTerapie, colFrequenzaPTerapie;

    public void inizializzaDati(Paziente p)
    {

        if(p != null)
        {

            nome.setText("Nome: " + p.getNome());
            cognome.setText("Cognome: " + p.getCognome());
            dataNascita.setText("Data di nascita: " + p.getDataNascita());
            eta.setText("Età: " + p.getEta());
            codiceFiscale.setText("Codice fiscale: " + p.getCodiceFiscale());
            gruppoSanguigno.setText("Gruppo sanguigno: " + p.getGruppoSanguigno());
            allergie.setText(p.getAllergie());
            patologie.setText(p.getPatologie());
            telefono.setText("Numero di telefono: " + p.getTelefono());
            indirizzo.setText("Indirizzo: " + p.getIndirizzo());

            configuraColonne();

            popolaTabelle(p);

        }

    }

    private void configuraColonne()
    {

        colCuiSVisite.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colDataSVisite.setCellValueFactory(new PropertyValueFactory<>("data"));
        colDiagnosiSVisite.setCellValueFactory(new PropertyValueFactory<>("diagnosi"));
        colNoteSVisite.setCellValueFactory(new PropertyValueFactory<>("note"));

        colCuiSEsami.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colDataSEsami.setCellValueFactory(new PropertyValueFactory<>("data"));
        colRisultatoSEsami.setCellValueFactory(new PropertyValueFactory<>("risultato"));
        colNoteSEsami.setCellValueFactory(new PropertyValueFactory<>("note"));

        colCuiPVisite.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colDataPVisite.setCellValueFactory(new PropertyValueFactory<>("data"));
        colMotivoPVisite.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colNotePVisite.setCellValueFactory(new PropertyValueFactory<>("note"));

        colCuiPEsami.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colDataPEsami.setCellValueFactory(new PropertyValueFactory<>("data"));
        colNotePEsami.setCellValueFactory(new PropertyValueFactory<>("note"));

        colCuiPTerapie.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colDataPTerapie.setCellValueFactory(new PropertyValueFactory<>("data"));
        colDosaggioPTerapie.setCellValueFactory(new PropertyValueFactory<>("dosaggio"));
        colFrequenzaPTerapie.setCellValueFactory(new PropertyValueFactory<>("frequenza"));

    }

    private void popolaTabelle(Paziente p)
    {

        ObservableList<VoceVisitaStorico> datiStoricoVisite = FXCollections.observableArrayList();
        for(Visita v : p.getVisite())
        {

            datiStoricoVisite.add(new VoceVisitaStorico(v.getCUIvisita(), v.getDataOraVisita().toString(), v.getDiagnosi(), v.getNote()));

        }
        tabellaStoricoVisite.setItems(datiStoricoVisite);


        ObservableList<VoceEsameStorico> datiStoricoEsami = FXCollections.observableArrayList();
        for(Esame e : p.getEsami())
        {

            datiStoricoEsami.add(new VoceEsameStorico(e.getCUIesame(), e.getDataOraEsame().toString(), e.getRisultato(), e.getNote()));

        }
        tabellaStoricoEsami.setItems(datiStoricoEsami);


        ObservableList<VoceVisitaPrenotata> datiVisitePrenotate = FXCollections.observableArrayList();
        for(Visita v : p.getVisitePrenotate())
        {

            datiVisitePrenotate.add(new VoceVisitaPrenotata(v.getCUIvisita(), v.getDataOraVisita().toString(), v.getMotivo(), v.getNote()));

        }
        tabellaPrenotazioniVisite.setItems(datiVisitePrenotate);

        ObservableList<VoceEsamePrenotato> datiEsamiPrenotati = FXCollections.observableArrayList();
        for(Esame e : p.getEsamiPrenotati())
        {

            datiEsamiPrenotati.add(new VoceEsamePrenotato(e.getCUIesame(), e.getDataOraEsame().toString(), e.getNote()));

        }
        tabellaPrenotazioniEsami.setItems(datiEsamiPrenotati);

        ObservableList<VoceTerapia> datiTerapie = FXCollections.observableArrayList();
        for(Terapia t : p.getTerapie())
        {

            datiTerapie.add(new VoceTerapia(t.getCUIterapia(), t.getDataFine().toString(), t.getDosaggio(), t.getFrequenza()));

        }

        tabellaPrenotazioniTerapie.setItems(datiTerapie);

    }

    public static class VoceVisitaStorico
    {

        private final String cui, data, diagnosi, note;
        public VoceVisitaStorico(String cui, String data, String diagnosi, String note)
        {

            this.cui = cui; this.data = data; this.diagnosi = diagnosi; this.note = note;

        }
        public String getCui() { return cui; }
        public String getData() { return data; }
        public String getDiagnosi() { return diagnosi; }
        public String getNote() { return note; }

    }

    public static class VoceEsameStorico
    {

        private final String cui, data, risultato, note;
        public VoceEsameStorico(String cui, String data, String risultato, String note)
        {

            this.cui = cui; this.data = data; this.risultato = risultato; this.note = note;

        }
        public String getCui() { return cui; }
        public String getData() { return data; }
        public String getRisultato() { return risultato; }
        public String getNote() { return note; }

    }

    public static class VoceVisitaPrenotata
    {

        private final String cui, data, motivo, note;
        public VoceVisitaPrenotata(String cui, String data, String motivo, String note)
        {

            this.cui = cui; this.data = data; this.motivo = motivo; this.note = note;

        }
        public String getCui() { return cui; }
        public String getData() { return data; }
        public String getMotivo() { return motivo; }
        public String getNote() { return note; }

    }

    public static class VoceEsamePrenotato
    {

        private final String cui, data, note;
        public VoceEsamePrenotato(String cui, String data, String note)
        {

            this.cui = cui; this.data = data; this.note = note;

        }
        public String getCui() { return cui; }
        public String getData() { return data; }
        public String getNote() { return note; }

    }

    public static class VoceTerapia
    {

        private final String cui, data, dosaggio, frequenza;
        public VoceTerapia(String cui, String data, String dosaggio, String frequenza)
        {

            this.cui = cui; this.data = data; this.dosaggio = dosaggio; this.frequenza = frequenza;

        }

        public String getCui() { return cui; }
        public String getData() { return data; }
        public String getDosaggio() { return dosaggio; }
        public String getFrequenza() { return frequenza; }

    }

}