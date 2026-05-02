package com.studioMedico.GCM.frontend.controller;

import com.studioMedico.GCM.backend.funzionamento.CreazioneEliminazionePersone;
import com.studioMedico.GCM.backend.funzionamento.oggettiModello.*;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class GestioneStaffController {

    @FXML private TableView<Persona> tabellaStaff;
    @FXML private TableColumn<Persona, String> colCui;
    @FXML private TableColumn<Persona, String> colCodice;
    @FXML private TableColumn<Persona, String> colNome;
    @FXML private TableColumn<Persona, String> colCognome;

    @FXML
    public void initialize()
    {

        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colCodice.setCellValueFactory(new PropertyValueFactory<>("codiceFiscale"));
        colCui.setCellValueFactory(new PropertyValueFactory<>("CUI"));

        caricaDatiUtenti();
    }

    @FXML
    private void eliminaUtente() throws IOException
    {

        Object utenteSelezionato = tabellaStaff.getSelectionModel().getSelectedItem();

        if(utenteSelezionato == null)
        {

            mostraAlert(Alert.AlertType.WARNING, "Selezione mancante", "Seleziona un utente.");
            return;

        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma Eliminazione");
        conferma.setHeaderText("Eliminazione utente dello Staff");
        conferma.setContentText("Vuoi davvero eliminare questo account?");

        if(conferma.showAndWait().get() == ButtonType.OK)
        {

            String cui = "";

            if(utenteSelezionato instanceof IT)
            {

                cui = ((IT) utenteSelezionato).getCUI();
                CreazioneEliminazionePersone.eliminaIT(cui);

            }
            else if(utenteSelezionato instanceof Medico)
            {

                cui = ((Medico) utenteSelezionato).getCUI();
                CreazioneEliminazionePersone.eliminaMedico(cui);

            }
            else if(utenteSelezionato instanceof Segretario)
            {

                cui = ((Segretario) utenteSelezionato).getCUI();
                CreazioneEliminazionePersone.eliminaSegretario(cui);

            }
            else if(utenteSelezionato instanceof Amministratore)
            {

                cui = ((Amministratore) utenteSelezionato).getCUI();
                CreazioneEliminazionePersone.eliminaAmministratore(cui);

            }

        }

    }

    @FXML
    private void caricaDatiUtenti()
    {
        ObservableList<Persona> listaStaff = FXCollections.observableArrayList();

        Path[] directoryDaScansionareMedico = {ConfigFile.MEDICI_DIR};
        Path[] directoryDaScansionareSegretario = {ConfigFile.SEGRETARI_DIR};
        Path[] directoryDaScansionareIT = {ConfigFile.IT_DIR};
        Path[] directoryDaScansionareAmministratore= {ConfigFile.AMMINISTRATORI_DIR};

        for(Path dir : directoryDaScansionareAmministratore)
        {

            if(Files.exists(dir))
            {

                try(Stream<Path> stream = Files.list(dir))
                {

                    stream.filter(file -> file.toString().endsWith(".dat"))
                            .forEach(file ->
                            {

                                try
                                {

                                    Amministratore a = LetturaFile.leggiFileCifrato(file);
                                    if(a != null)
                                    {

                                        listaStaff.add(a);

                                    }

                                }
                                catch (Exception e)
                                {

                                    System.err.println("Errore lettura file: " + file);

                                }

                            });

                }
                catch(IOException e)
                {

                    System.err.println("Errore accesso directory: " + dir);

                }

            }

        }

        for(Path dir : directoryDaScansionareIT)
        {

            if(Files.exists(dir))
            {

                try(Stream<Path> stream = Files.list(dir))
                {

                    stream.filter(file -> file.toString().endsWith(".dat"))
                            .forEach(file ->
                            {

                                try
                                {

                                    IT i = LetturaFile.leggiFileCifrato(file);
                                    if(i != null)
                                    {

                                        listaStaff.add(i);

                                    }

                                }
                                catch (Exception e)
                                {

                                    System.err.println("Errore lettura file: " + file);

                                }

                            });

                }
                catch(IOException e)
                {

                    System.err.println("Errore accesso directory: " + dir);

                }

            }

        }

        for(Path dir : directoryDaScansionareSegretario)
        {

            if(Files.exists(dir))
            {

                try(Stream<Path> stream = Files.list(dir))
                {

                    stream.filter(file -> file.toString().endsWith(".dat"))
                            .forEach(file ->
                            {

                                try
                                {

                                    Segretario s = LetturaFile.leggiFileCifrato(file);
                                    if(s != null)
                                    {

                                        listaStaff.add(s);

                                    }

                                }
                                catch (Exception e)
                                {

                                    System.err.println("Errore lettura file: " + file);

                                }

                            });

                }
                catch(IOException e)
                {

                    System.err.println("Errore accesso directory: " + dir);

                }

            }

        }

        for(Path dir : directoryDaScansionareMedico)
        {

            if(Files.exists(dir))
            {

                try(Stream<Path> stream = Files.list(dir))
                {

                    stream.filter(file -> file.toString().endsWith(".dat"))
                            .forEach(file ->
                            {

                                try
                                {

                                    Medico m = LetturaFile.leggiFileCifrato(file);
                                    if(m != null)
                                    {

                                        listaStaff.add(m);

                                    }

                                }
                                catch (Exception e)
                                {

                                    System.err.println("Errore lettura file: " + file);

                                }

                            });

                }
                catch(IOException e)
                {

                    System.err.println("Errore accesso directory: " + dir);

                }

            }

        }

        listaStaff.sort(Comparator.comparing(Persona::getCognome)
                .thenComparing(Persona::getNome));

        tabellaStaff.setItems(listaStaff);

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