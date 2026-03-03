package com.studioMedico.GCM.backend.funzionamento;

import com.studioMedico.GCM.backend.gestioneFile.ConfigFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.time.*;

/*
 * questa classe sarà usata praticamente solo dalla GUI per controllare che i dati siano validi prima di passarli al
 * backend
 */

public class ValidatoreDati
{

    //regex (modello) per username valido, formato da Tipo(1 lettera) + Numero (es. M1, I20) + _ + Resto
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[MISAP]\\d+_.*$");

    //regex per simboli, controlla se c'è almeno un carattere che NON è lettera o numero
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    /*
     * controlla che il nome utente segua il formato standard (es. M1_rossi)
     */
    public static boolean isUsernameValido(String username)
    {

        if (username == null || username.isEmpty())
            return false;

        //returna true se è compatibile con la versione standard dell'username
        return USERNAME_PATTERN.matcher(username).matches();

    }

    /*
     * valida la sicurezza della password:
     * - Almeno 8 caratteri
     * - Almeno una Maiuscola
     * - Almeno una Minuscola
     * - Almeno un Numero
     * - Almeno un Simbolo (@, #, !, $, %, ecc.)
     */
    public static boolean isPasswordSicura(String password)
    {

        if (password == null || password.length() < 8)
            return false;

        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSymbol = SYMBOL_PATTERN.matcher(password).find();

        //returna true se ha tutti i requisiti
        return hasUpper && hasLower && hasDigit && hasSymbol;

    }

    public static boolean isTelefonoValido(String telefono)
    {

        if(telefono == null || telefono.length() != 10)
            return false;

        if(telefono.matches("\\d+"))
            return true;
        else
            return false;

    }

    //se esiste già returna true
    public static boolean controllaEsistenzaCui(String CUI)
    {

        char prefisso = CUI.charAt(0);
        Path cartella;

        switch (prefisso)
        {

            case 'M':

                cartella = ConfigFile.MEDICI_DIR;

                break;

            case 'P':

                cartella = ConfigFile.PAZIENTI_DIR;

                break;

            case 'A':

                cartella = ConfigFile.AMMINISTRATORI_DIR;

                break;

            case 'S':

                cartella = ConfigFile.SEGRETARI_DIR;

                break;

            case 'I':

                cartella = ConfigFile.IT_DIR;

                break;

            default: //il CUI è in un formato non valido, ma lo gestisco già da un'altra parte
                return false;

        }

        //costruisco il percorso del file: cartella/CUI.dat
        Path percorsoFile = cartella.resolve(CUI.toUpperCase() + ".dat");

        //returna true se il file esiste sul disco
        return Files.exists(percorsoFile);

    }

    public static boolean isDataDiNascitaValida(LocalDate dataDiNascita)
    {

        if (dataDiNascita == null)
            return false;

        LocalDate oggi = LocalDate.now();
        LocalDate limitePassato = oggi.minusYears(150); //limite ragionevole di 150 anni fa

        //la data deve essere nel passato E dopo il limite dei 150 anni
        if (dataDiNascita.isAfter(oggi))
            return false; //non può essere nato nel futuro

        if (dataDiNascita.isBefore(limitePassato))
            return false; //data troppo passata

        return true;

    }

    //check solo della lunghezza, non se il codice fiscale sia veramente giusto
    public static boolean isCodiceFiscaleValido(String codice)
    {

        if(codice == null || codice.isEmpty())
            return false;

        if(codice.length() != 16)
            return false;

        return true;

    }

    //se i l gruppo sanguigno è valido returno true
    public static boolean isGruppoSanguignoValido(String gruppoSanguigno)
    {

        switch(gruppoSanguigno)
        {

            case "A+":
            case "A-":
            case "B+":
            case "B-":
            case "AB+":
            case "AB-":
            case "0+":
            case "0-":
                return true;

            default:
                return false;

        }

    }

}
