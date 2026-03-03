package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Log implements Serializable
{

    private LocalDateTime dataOra;
    private String autore;
    private String operazione;

    public Log(LocalDateTime dataOra, String autore, String operazione) {
        this.dataOra = dataOra;
        this.autore = autore;
        this.operazione = operazione;
    }

    public LocalDateTime getDataOra() { return dataOra; }
    public String getAutore() { return autore; }
    public String getOperazione() { return operazione; }

    //devo passargli il path del file dei log
    //synchronized senze a fare in modo che non si possa accedere agli stessi dati di log allo stesso esetto momento da più parti
    public static synchronized void aggiungiLog(Log nuovoLog) throws IOException
    {

        Path file = ConfigFile.LOG_FILE;

        //leggo la lista esistente
        ArrayList<Log> logs;
        try
        {

            logs = LetturaFile.leggiFileCifrato(file);

        }
        catch (Exception e)
        {

            // Se il file è vuoto o corrotto, inizializzo una nuova lista
            logs = new ArrayList<>();

        }

        if (logs == null) logs = new ArrayList<>();

        //aggiungo il nuovo log in cima (indice 0)
        logs.add(0, nuovoLog);

        //limita la dimensione per non rallentare il sistema
        if (logs.size() > 50000) {
            logs.remove(logs.size() - 1); //rimuove il più vecchio
        }

        // ScritturaFile.scriviFileCifrato scriverà AUTOMATICAMENTE
        // sia in LOG_FILE che in BACK_LOG_FILE.
        ScritturaFile.scriviFileCifrato(file, logs);

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(dataOra, log.dataOra) && Objects.equals(autore, log.autore) && Objects.equals(operazione, log.operazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataOra, autore, operazione);
    }

}
