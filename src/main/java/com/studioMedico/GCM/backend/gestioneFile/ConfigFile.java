package com.studioMedico.GCM.backend.gestioneFile;

import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Questa classe serve per configurare i percorsi dei file dell'applicazione
 * Contiene i path dei file con i dati cifrati e delle cartelle principali
 */

public final class ConfigFile
{

    //il costruttore privato perchè la classe non deve essere istanziata
    private ConfigFile()
    {
    }

    //prendo la cartella dove ci sono tutti i file
    public static final Path DATA_DIR = Paths.get("data", "file");

    //prendo le sottocartelle dove sono i tipi di utente
    public static final Path MEDICI_DIR = DATA_DIR.resolve("medici");
    public static final Path PAZIENTI_DIR = DATA_DIR.resolve("pazienti");
    public static final Path SEGRETARI_DIR = DATA_DIR.resolve("segretari");
    public static final Path AMMINISTRATORI_DIR = DATA_DIR.resolve("amministratori");
    public static final Path IT_DIR = DATA_DIR.resolve("IT");

    //prendo i file globali
    public static final Path CONTATORI_FILE = DATA_DIR.resolve("contatori.dat");
    public static final Path CREDENZIALI_FILE = DATA_DIR.resolve("credenzialiApp.dat");
    public static final Path LOG_FILE = DATA_DIR.resolve("log.dat");

}
