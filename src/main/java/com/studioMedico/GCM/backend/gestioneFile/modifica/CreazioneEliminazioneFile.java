package com.studioMedico.GCM.backend.gestioneFile.modifica;

import com.studioMedico.GCM.backend.gestioneFile.ConfigFile_backup;

import java.io.IOException;
import java.nio.file.*;

/*
 * questa classe serve esclusivamente a creare/eliminare un file col nome e dove il path gli dicono
 */

public class CreazioneEliminazioneFile
{

    public static Path creaFile(Path dir, String nomeFile) throws IOException
    {

        //costruisci il percorso completo per il file dentro la cartella che gli è stata passata
        Path file = dir.resolve(nomeFile);
        if(Files.notExists(file))
        {

            //crea il file, se non esiste già
            Files.createFile(file);

        }

        //ora lo faccio per il backup, dir_backup è inizializzata solo perchè obbligatorio
        Path file_backup, dir_backup = dir;
        char prefisso = nomeFile.charAt(0);

        switch(prefisso)
        {

            case 'S':
                dir_backup = ConfigFile_backup.BACK_SEGRETARI_DIR;
                break;

            case 'A':
                dir_backup = ConfigFile_backup.BACK_AMMINISTRATORI_DIR;
                break;

            case 'M':
                dir_backup = ConfigFile_backup.BACK_MEDICI_DIR;
                break;

            case 'P':
                dir_backup = ConfigFile_backup.BACK_PAZIENTI_DIR;
                break;

            case 'I':
                dir_backup = ConfigFile_backup.BACK_IT_DIR;
                break;

        }

        file_backup = dir_backup.resolve(nomeFile);

        if(Files.notExists(file_backup))
        {

            //crea il file, se non esiste già
            Files.createFile(file_backup);

        }

        //returna il percorso del file
        return file;

    }

    public static void eliminaFile(Path file) throws IOException
    {

        //Se il file al path passato esiste lo elimino
        if(Files.exists(file))
        {

            Files.delete(file);

        }

        //ora lo faccio per il backup, dir_backup è inizializzata solo perchè obbligatorio
        Path file_backup, dir_backup = file;
        String nomeFile = file.getFileName().toString();

        char prefisso = nomeFile.charAt(0);

        switch(prefisso)
        {

            case 'S':
                dir_backup = ConfigFile_backup.BACK_SEGRETARI_DIR;
                break;

            case 'A':
                dir_backup = ConfigFile_backup.BACK_AMMINISTRATORI_DIR;
                break;

            case 'M':
                dir_backup = ConfigFile_backup.BACK_MEDICI_DIR;
                break;

            case 'P':
                dir_backup = ConfigFile_backup.BACK_PAZIENTI_DIR;
                break;

            case 'I':
                dir_backup = ConfigFile_backup.BACK_IT_DIR;
                break;

        }

        file_backup = dir_backup.resolve(nomeFile);

        if(Files.exists(file_backup))
        {

            Files.delete(file_backup);

        }

    }

}
