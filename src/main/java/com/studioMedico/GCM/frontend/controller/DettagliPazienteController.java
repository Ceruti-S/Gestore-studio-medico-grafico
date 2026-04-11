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

    @FXML private TableView<VoceTabella> tabellaStoricoVisite;
    @FXML private TableColumn<VoceTabella, String> colCuiSVisite, colDataSVisite;

    @FXML private TableView<VoceTabella> tabellaStoricoEsami;
    @FXML private TableColumn<VoceTabella, String> colCuiSEsami, colDataSEsami;

    @FXML private TableView<VoceTabella> tabellaPrenotazioniVisite;
    @FXML private TableColumn<VoceTabella, String> colCuiPVisite, colDataPVisite;

    @FXML private TableView<VoceTabella> tabellaPrenotazioniEsami;
    @FXML private TableColumn<VoceTabella, String> colCuiPEsami, colDataPEsami;

    @FXML private TableView<VoceTabella> tabellaPrenotazioniTerapie;
    @FXML private TableColumn<VoceTabella, String> colCuiPTerapie, colDataPTerapie;

    public void inizializzaDati(Paziente p)
    {

        if (p != null)
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

            configuraColonne(colCuiSVisite, colDataSVisite);
            configuraColonne(colCuiSEsami, colDataSEsami);
            configuraColonne(colCuiPVisite, colDataPVisite);
            configuraColonne(colCuiPEsami, colDataPEsami);
            configuraColonne(colCuiPTerapie, colDataPTerapie);

            configuraDoppioClick(tabellaStoricoVisite, "STORICO_VISITA");
            configuraDoppioClick(tabellaStoricoEsami, "STORICO_ESAME");
            configuraDoppioClick(tabellaPrenotazioniVisite, "PRENOTAZIONE_VISITA");
            configuraDoppioClick(tabellaPrenotazioniEsami, "PRENOTAZIONE_ESAME");
            configuraDoppioClick(tabellaPrenotazioniTerapie, "PRENOTAZIONE_TERAPIA");

            //recupero le liste dal backend
            List<Visita> storicoVisite = p.getVisite();
            List<Visita> visitePrenotate = p.getVisitePrenotate();
            List<Esame> storicoEsami = p.getEsami();
            List<Esame> esamiPrenotati = p.getEsamiPrenotati();
            List<Terapia> terapie = p.getTerapie(); //entrambe, sia storico sia in corso

            //trasformo le liste in dati utilizzabili dalla tabella
            ObservableList<VoceTabella> datiStoricoVisite = FXCollections.observableArrayList();
            for (Visita v : storicoVisite)
            {

                datiStoricoVisite.add(new VoceTabella(v.getCUIvisita(), v.getDataOraVisita().toString()));

            }
            ObservableList<VoceTabella> datiVisite = FXCollections.observableArrayList();
            for (Visita v : visitePrenotate)
            {

                datiVisite.add(new VoceTabella(v.getCUIvisita(), v.getDataOraVisita().toString()));

            }

            ObservableList<VoceTabella> datiStoricoEsami = FXCollections.observableArrayList();
            for (Esame e :storicoEsami)
            {

                datiStoricoEsami.add(new VoceTabella(e.getCUIesame(), e.getDataOraEsame().toString()));

            }
            ObservableList<VoceTabella> datiEsami = FXCollections.observableArrayList();
            for (Esame e : esamiPrenotati)
            {

                datiEsami.add(new VoceTabella(e.getCUIesame(), e.getDataOraEsame().toString()));

            }

            ObservableList<VoceTabella> datiTerapie = FXCollections.observableArrayList();
            for (Terapia t : terapie)
            {

                datiTerapie.add(new VoceTabella(t.getCUIterapia(), t.getDataFine().toString()));

            }

            //metto i dati nella tabella
            tabellaStoricoVisite.setItems(datiStoricoVisite);
            tabellaPrenotazioniVisite.setItems(datiVisite);
            tabellaStoricoEsami.setItems(datiStoricoEsami);
            tabellaPrenotazioniEsami.setItems(datiEsami);
            tabellaPrenotazioniTerapie.setItems(datiTerapie);

        }

    }

    private void configuraColonne(TableColumn<VoceTabella, String> colCui, TableColumn<VoceTabella, String> colData)
    {

        colCui.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

    }

    private void configuraDoppioClick(TableView<VoceTabella> tabella, String tipoModulo)
    {

        tabella.setRowFactory(tv ->
        {

            TableRow<VoceTabella> row = new TableRow<>();
            row.setOnMouseClicked(event ->
            {

                if (event.getClickCount() == 2 && (!row.isEmpty()))
                {

                    VoceTabella voce = row.getItem();
                    gestisciDettaglioSpecifico(voce.getCui(), tipoModulo);

                }

            });

            return row;

        });

    }

    private void gestisciDettaglioSpecifico(String cui, String tipoModulo)
    {

        System.out.println("Apertura modulo [" + tipoModulo + "] per CUI: " + cui);
        // Switch per caricare il giusto FXML di dettaglio in base al prefisso o al tipoModulo
        switch (tipoModulo)
        {

            case "STORICO_VISITA" -> System.out.println("Caricamento referto visita...");
            case "PRENOTAZIONE_VISITA" -> System.out.println("Caricamento prenotazione...");
            // ... eccetera //TODO:implementare apertura dettagli
        }

    }

    /**
     * Classe di supporto per rappresentare una riga nelle mini-tabelle.
     * Deve avere i getter pubblici per PropertyValueFactory.
     */
    public static class VoceTabella
    {

        private final String cui;
        private final String data;

        public VoceTabella(String cui, String data)
        {

            this.cui = cui;
            this.data = data;

        }

        public String getCui() { return cui; }
        public String getData() { return data; }

    }
}