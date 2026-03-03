package com.studioMedico.GCM.backend.funzionamento;

import com.studioMedico.GCM.backend.funzionamento.oggettiModello.*;
import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.CreazioneEliminazioneFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.LetturaFile;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

public class CreazioneEliminazionePersone
{

    /*
     * questa classe creerà i vari oggetti delle persone, poi scriverà il file
     */

    public static boolean creaPaziente(String telefono, String indirizzo, String gruppoSanguigno, String nome, String cognome, LocalDate dataNascita, String codiceFiscale) throws IOException
    {

        //Se il telefono inserito non è valido, non inserisco il paziente, stessa cosa per gli altri dati
        if(!ValidatoreDati.isTelefonoValido(telefono))
            return false;

        if(indirizzo.isEmpty() || indirizzo == null)
            return false;

        if(!ValidatoreDati.isGruppoSanguignoValido(gruppoSanguigno))
            return false;

        if(!ValidatoreDati.isCodiceFiscaleValido(codiceFiscale))
            return false;

        if(!ValidatoreDati.isDataDiNascitaValida(dataNascita))
            return false;

        //per gli altri dati controllo solo se siano validi (non vuoti)
        if(nome.isEmpty() || nome == null)
            return false;
        if(cognome.isEmpty() || cognome == null)
            return false;

        //ora controllo se l'utente attivo può creare un paziente, solo segretari, IT, amministratori
        if(ControlloLogin.utenteAttivo.charAt(0)=='M')
            return false;

        //ora posso creare l'oggetto paziente, non inizializzato
        //creo il paziente vuoto
        Paziente paziente = new Paziente();
        //creo i file del paziente, nel nome avranno il suo cui
        ScritturaFile.scriviFileCifrato(null, paziente);

        //inizializzo il paziente
        //prendo la lista dei file nella cartella
        File directory = ConfigFile.PAZIENTI_DIR.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));

        //ordino i file per data di creazione (l'ultimo creato è quello appena creato)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        //prendo il nome del primo file (es: "P11.dat")
        String nomeFileConEstensione = files[0].getName();

        //perndo il cui, tolgo il ".dat" (ultimi 4 caratteri)
        String cuiEstratto = nomeFileConEstensione.substring(0, nomeFileConEstensione.length() - 4);

        paziente.setNome(nome);
        paziente.setCognome(cognome);
        paziente.setDataNascita(dataNascita);
        paziente.setCodiceFiscale(codiceFiscale);
        paziente.setGruppoSanguigno(gruppoSanguigno);
        paziente.setTelefono(telefono);
        paziente.setIndirizzo(indirizzo);
        paziente.setCUI(cuiEstratto);
        paziente.setEta();

        //costruisco il nome del file completo
        String nomeFileFinale = cuiEstratto + ".dat";

        //unisco la cartella dei pazienti con il nome del file per ottenere il PATH COMPLETO
        //resolve() è come dire: "C:/cartella/pazienti" + "/" + "P11.dat"
        Path pathCompleto = ConfigFile.PAZIENTI_DIR.resolve(nomeFileFinale);

        ScritturaFile.scriviFileCifrato(pathCompleto, paziente);

        return true;

    }

    public static boolean creaAmministratore(String nome, String cognome, String titoloStudio, LocalDate dataNascita, String codiceFiscale) throws IOException
    {

        if(!ValidatoreDati.isCodiceFiscaleValido(codiceFiscale))
            return false;

        if(!ValidatoreDati.isDataDiNascitaValida(dataNascita))
            return false;

        //per gli altri dati controllo solo se siano validi (non vuoti)
        if(nome.isEmpty() || nome == null)
            return false;
        if(cognome.isEmpty() || cognome == null)
            return false;

        //ora controllo se l'utente attivo può creare un amministratore, solo IT
        if(ControlloLogin.utenteAttivo.charAt(0)=='M' || ControlloLogin.utenteAttivo.charAt(0)=='S' || ControlloLogin.utenteAttivo.charAt(0)=='A')
            return false;

        Amministratore amministratore = new Amministratore();
        ScritturaFile.scriviFileCifrato(null, amministratore);

        //prendo la lista dei file nella cartella
        File directory = ConfigFile.AMMINISTRATORI_DIR.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));

        //ordino i file per data di creazione (l'ultimo creato è quello appena creato)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        //prendo il nome del primo file (es: "P11.dat")
        String nomeFileConEstensione = files[0].getName();

        //perndo il cui, tolgo il ".dat" (ultimi 4 caratteri)
        String cuiEstratto = nomeFileConEstensione.substring(0, nomeFileConEstensione.length() - 4);

        amministratore.setNome(nome);
        amministratore.setCognome(cognome);
        amministratore.setDataNascita(dataNascita);
        amministratore.setCodiceFiscale(codiceFiscale);
        amministratore.setTitoloStudio(titoloStudio);
        amministratore.setEta();
        amministratore.setCUI(cuiEstratto);

        //costruisco il nome del file completo
        String nomeFileFinale = cuiEstratto + ".dat";

        Path pathCompleto = ConfigFile.AMMINISTRATORI_DIR.resolve(nomeFileFinale);

        ScritturaFile.scriviFileCifrato(pathCompleto, amministratore);

        return true;

    }

    public static boolean creaSegretario(String nome, String cognome, String turnoLavoro, LocalDate dataNascita, String codiceFiscale) throws IOException
    {

        if(!ValidatoreDati.isCodiceFiscaleValido(codiceFiscale))
            return false;

        if(!ValidatoreDati.isDataDiNascitaValida(dataNascita))
            return false;

        //per gli altri dati controllo solo se siano validi (non vuoti)
        if(nome.isEmpty() || nome == null)
            return false;
        if(cognome.isEmpty() || cognome == null)
            return false;

        //ora controllo se l'utente attivo può creare un segretario, solo amministratori, IT
        if(ControlloLogin.utenteAttivo.charAt(0)=='M' || ControlloLogin.utenteAttivo.charAt(0)=='S')
            return false;

        //ora posso creare l'oggetto segretario, non inizializzato
        //creo il segretario vuoto
        Segretario segretario = new Segretario();
        //creo i file del segretario, nel nome avranno il suo cui
        ScritturaFile.scriviFileCifrato(null, segretario);

        //inizializzo il segretario
        //prendo la lista dei file nella cartella
        File directory = ConfigFile.SEGRETARI_DIR.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));

        //ordino i file per data di creazione (l'ultimo creato è quello appena creato)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        //prendo il nome del primo file (es: "P11.dat")
        String nomeFileConEstensione = files[0].getName();

        //perndo il cui, tolgo il ".dat" (ultimi 4 caratteri)
        String cuiEstratto = nomeFileConEstensione.substring(0, nomeFileConEstensione.length() - 4);

        segretario.setNome(nome);
        segretario.setCognome(cognome);
        segretario.setDataNascita(dataNascita);
        segretario.setCodiceFiscale(codiceFiscale);
        segretario.setTurnoLavoro(turnoLavoro);
        segretario.setEta();
        segretario.setCUI(cuiEstratto);

        //costruisco il nome del file completo
        String nomeFileFinale = cuiEstratto + ".dat";

        //unisco la cartella dei segretari con il nome del file per ottenere il PATH COMPLETO
        Path pathCompleto = ConfigFile.SEGRETARI_DIR.resolve(nomeFileFinale);

        ScritturaFile.scriviFileCifrato(pathCompleto, segretario);

        return true;

    }

    public static boolean creaMedico(String nome, String cognome, String specializzazione, LocalDate dataNascita, String codiceFiscale) throws IOException
    {

        if(!ValidatoreDati.isCodiceFiscaleValido(codiceFiscale))
            return false;

        if(!ValidatoreDati.isDataDiNascitaValida(dataNascita))
            return false;

        //per gli altri dati controllo solo se siano validi (non vuoti)
        if(nome.isEmpty() || nome == null)
            return false;
        if(cognome.isEmpty() || cognome == null)
            return false;

        //ora controllo se l'utente attivo può creare un medico, solo amministratori, IT
        if(ControlloLogin.utenteAttivo.charAt(0)=='M' || ControlloLogin.utenteAttivo.charAt(0)=='S')
            return false;

        Medico medico = new Medico();
        ScritturaFile.scriviFileCifrato(null, medico);

        //prendo la lista dei file nella cartella
        File directory = ConfigFile.MEDICI_DIR.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));

        //ordino i file per data di creazione (l'ultimo creato è quello appena creato)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        //prendo il nome del primo file (es: "P11.dat")
        String nomeFileConEstensione = files[0].getName();

        //perndo il cui, tolgo il ".dat" (ultimi 4 caratteri)
        String cuiEstratto = nomeFileConEstensione.substring(0, nomeFileConEstensione.length() - 4);

        medico.setNome(nome);
        medico.setCognome(cognome);
        medico.setDataNascita(dataNascita);
        medico.setCodiceFiscale(codiceFiscale);
        medico.setSpecializzazione(specializzazione);
        medico.setEta();
        medico.setCUI(cuiEstratto);

        //costruisco il nome del file completo
        String nomeFileFinale = cuiEstratto + ".dat";

        //unisco la cartella dei segretari con il nome del file per ottenere il PATH COMPLETO
        Path pathCompleto = ConfigFile.MEDICI_DIR.resolve(nomeFileFinale);

        ScritturaFile.scriviFileCifrato(pathCompleto, medico);

        return true;

    }

    public static boolean creaIT(String gradoReparto, String titoloStudio, String nome, String cognome, LocalDate dataNascita, String codiceFiscale) throws IOException
    {

        if(!ValidatoreDati.isCodiceFiscaleValido(codiceFiscale))
            return false;

        if(!ValidatoreDati.isDataDiNascitaValida(dataNascita))
            return false;

        //per gli altri dati controllo solo se siano validi (non vuoti)
        if(nome.isEmpty() || nome == null)
            return false;
        if(cognome.isEmpty() || cognome == null)
            return false;

        //ora controllo se l'utente attivo può creare un IT, solo IT (direttore), amministratori
        if(ControlloLogin.utenteAttivo.charAt(0)=='M' || ControlloLogin.utenteAttivo.charAt(0)=='S')
            return false;
        if(ControlloLogin.utenteAttivo.charAt(0)=='I')
        {

            String CUIutenteAttivo = ControlloLogin.utenteAttivo;
            //costruisco il nome del file completo
            String nomeFileFinale = CUIutenteAttivo + ".dat";

            //unisco la cartella degli IT con il nome del file per ottenere il PATH COMPLETO
            java.nio.file.Path pathCompletoIT = ConfigFile.IT_DIR.resolve(nomeFileFinale);

            IT it = LetturaFile.leggiFileCifrato(pathCompletoIT);

            if(it.getGradoReparto().equals("impiegato"))
                return false;

        }

        //ora posso creare l'oggetto IT, non inizializzato
        IT it = new IT();
        ScritturaFile.scriviFileCifrato(null, it);

        //prendo la lista dei file nella cartella
        File directory = ConfigFile.IT_DIR.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));

        //ordino i file per data di creazione (l'ultimo creato è quello appena creato)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        //prendo il nome del primo file (es: "P11.dat")
        String nomeFileConEstensione = files[0].getName();

        //perndo il cui, tolgo il ".dat" (ultimi 4 caratteri)
        String cuiEstratto = nomeFileConEstensione.substring(0, nomeFileConEstensione.length() - 4);

        it.setNome(nome);
        it.setCognome(cognome);
        it.setDataNascita(dataNascita);
        it.setCodiceFiscale(codiceFiscale);
        it.setCUI(cuiEstratto);
        it.setEta();
        it.setGradoReparto(gradoReparto);
        it.setTitoloStudio(titoloStudio);

        //costruisco il nome del file completo
        String nomeFileFinale = cuiEstratto + ".dat";

        //unisco la cartella dei IT con il nome del file per ottenere il PATH COMPLETO
        Path pathCompleto = ConfigFile.IT_DIR.resolve(nomeFileFinale);

        ScritturaFile.scriviFileCifrato(pathCompleto, it);

        return true;

    }

    public static boolean eliminaPaziente(String CUI) throws IOException
    {

        if(ControlloLogin.utenteAttivo.charAt(0)=='S' || ControlloLogin.utenteAttivo.charAt(0)=='M')
            return false;

        Path file = ConfigFile.PAZIENTI_DIR.resolve(CUI + ".dat");

        CreazioneEliminazioneFile.eliminaFile(file);

        return true;

    }

    public static boolean eliminaMedico(String CUI) throws IOException
    {

        if(ControlloLogin.utenteAttivo.charAt(0)=='S' || ControlloLogin.utenteAttivo.charAt(0)=='M')
            return false;

        Path file = ConfigFile.MEDICI_DIR.resolve(CUI + ".dat");

        CreazioneEliminazioneFile.eliminaFile(file);

        return true;

    }

    public static boolean eliminaSegretario(String CUI) throws IOException
    {

        if(ControlloLogin.utenteAttivo.charAt(0)=='S' || ControlloLogin.utenteAttivo.charAt(0)=='M')
            return false;

        Path file = ConfigFile.SEGRETARI_DIR.resolve(CUI + ".dat");

        CreazioneEliminazioneFile.eliminaFile(file);

        return true;

    }

    public static boolean eliminaAmministratore(String CUI) throws IOException
    {

        if(ControlloLogin.utenteAttivo.charAt(0)=='S' || ControlloLogin.utenteAttivo.charAt(0)=='M' || ControlloLogin.utenteAttivo.charAt(0)=='A')
            return false;

        //non ci si può eliminare da soli
        if(CUI.equals(ControlloLogin.utenteAttivo))
            return false;

        Path file = ConfigFile.AMMINISTRATORI_DIR.resolve(CUI + ".dat");

        CreazioneEliminazioneFile.eliminaFile(file);

        return true;

    }

    public static boolean eliminaIT(String CUI) throws IOException
    {

        if(ControlloLogin.utenteAttivo.charAt(0)=='S' || ControlloLogin.utenteAttivo.charAt(0)=='M' || ControlloLogin.utenteAttivo.charAt(0)=='A')
            return false;

        //non ci si può eliminare da soli
        if(CUI.equals(ControlloLogin.utenteAttivo))
            return false;

        String CUIutenteAttivo = ControlloLogin.utenteAttivo;
        //costruisco il nome del file completo
        String nomeFileFinale = CUIutenteAttivo + ".dat";

        //unisco la cartella degli IT con il nome del file per ottenere il PATH COMPLETO
        java.nio.file.Path pathCompletoIT = ConfigFile.IT_DIR.resolve(nomeFileFinale);

        IT it = LetturaFile.leggiFileCifrato(pathCompletoIT);

        if(it.getGradoReparto().equals("impiegato"))
            return false;

        Path file = ConfigFile.IT_DIR.resolve(CUI + ".dat");

        CreazioneEliminazioneFile.eliminaFile(file);

        return true;

    }

}
