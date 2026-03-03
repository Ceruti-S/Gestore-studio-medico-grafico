package com.studioMedico.GCM.backend.gestioneFile;

import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Questa classe ha lo stesso scopo di ConfigFile.java ma per i file e le cartelle di backup
 */

public class ConfigFile_backup
{

    //il costruttore privato perchè la classe non deve essere istanziata
    private ConfigFile_backup()
    {
    }

    //prendo la cartella dove ci sono tutti i file di backup
    public static final Path BACK_DATA_DIR = Paths.get("data", "backup");

    //prendo le sottocartelle dove sono i tipi di utente di backup
    public static final Path BACK_MEDICI_DIR = BACK_DATA_DIR.resolve("medici_backup");
    public static final Path BACK_PAZIENTI_DIR = BACK_DATA_DIR.resolve("pazienti_backup");
    public static final Path BACK_SEGRETARI_DIR = BACK_DATA_DIR.resolve("segretari_backup");
    public static final Path BACK_AMMINISTRATORI_DIR = BACK_DATA_DIR.resolve("amministratori_backup");
    public static final Path BACK_IT_DIR = BACK_DATA_DIR.resolve("IT_backup");

    //prendo i file globali
    public static final Path BACK_CONTATORI_FILE = BACK_DATA_DIR.resolve("contatori_backup.dat");
    public static final Path BACK_CREDENZIALI_FILE = BACK_DATA_DIR.resolve("credenzialiApp_backup.dat");
    public static final Path BACK_LOG_FILE = BACK_DATA_DIR.resolve("log_backup.dat");

    //metodo per tradurre un percorso per i file principali a quelli di backup
    public static Path getBackupPath(Path mainPath)
    {

        if (mainPath == null) return null;

        String filename = mainPath.getFileName().toString();
        String parentDirName = mainPath.getParent().getFileName().toString();

        //CASO 1: File globali (quelli che stanno direttamente in data/file)
        //Trasforma "contatori.dat" in "contatori_backup.dat"
        if (parentDirName.equals("file"))
        {

            String newFileName = filename.replace(".dat", "_backup.dat");
            return BACK_DATA_DIR.resolve(newFileName);

        }

        //CASO 2: File degli utenti (Medici, Pazienti, ecc.)
        //La cartella genitore riceve "_backup", ma il file (es. M1.dat) resta uguale
        String backupFolderName = parentDirName + "_backup";

        return BACK_DATA_DIR.resolve(backupFolderName).resolve(filename);

    }

}
