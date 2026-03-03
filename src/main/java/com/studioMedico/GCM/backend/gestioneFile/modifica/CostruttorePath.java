package com.studioMedico.GCM.backend.gestioneFile.modifica;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;

public class CostruttorePath
{

    /*
     * Costruisce il path completo dove salvare un oggetto generico, serve solo per quando devo creare un
     * nuovo file(quindi un nuovo oggetto ovvero una nuova persona)
     */

    public static <T> Path costruisciPathPerOggetto(T oggetto) throws IOException
    {

        //leggo i contatori globali
        Map<String, Integer> contatoriGlobali = LetturaFile.leggiFileCifrato(ConfigFile.CONTATORI_FILE);

        //determino il prefisso in base al tipo di oggetto
        String tipo = oggetto.getClass().getSimpleName();
        String prefisso;
        Path dir;

        switch(tipo)
        {
            case "Medico":
                prefisso = "M";
                dir = ConfigFile.MEDICI_DIR;
                break;

            case "Paziente":
                prefisso = "P";
                dir = ConfigFile.PAZIENTI_DIR;
                break;

            case "Segretario":
                prefisso = "S";
                dir = ConfigFile.SEGRETARI_DIR;
                break;

            case "Amministratore":
                prefisso = "A";
                dir = ConfigFile.AMMINISTRATORI_DIR;
                break;

            case "IT":
                prefisso = "I";
                dir = ConfigFile.IT_DIR;
                break;

            default:
                throw new IllegalArgumentException("Tipo non valido");
        }

        //prendo il numero corrente dal contatore globale e lo incremento (serve solo per prendere che codice avrà)
        //poi il contatore lo incremento in scrittura del file
        int numero = contatoriGlobali.getOrDefault(prefisso, 0) + 1;

        //costruisco il nome del file
        String nomeFile = prefisso + numero + ".dat";

        //restituisco il path completo
        return dir.resolve(nomeFile);

    }

}
