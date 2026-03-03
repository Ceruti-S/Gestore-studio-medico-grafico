package com.studioMedico.GCM.backend.gestioneFile.modifica;

import java.io.IOException;
import java.nio.file.*;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import com.studioMedico.GCM.backend.gestioneFile.crittografia.CrittografiaFile;
import java.io.ByteArrayInputStream;

/*
 * questa classe restituisce l'oggetto che c'è scritto dentro al file, qualunque esso sia
 * (mappa, IT, ecc..)
 */

public class LetturaFile
{

    //a questo metodo gli devo solo passare il path da leggere
    @SuppressWarnings("unchecked")
    public static <T> T leggiFileCifrato(Path file) throws IOException
    {
        try
        {

            //leggo i byte dal file e li salvo
            byte[] datiCriptati = Files.readAllBytes(file);

            //decripto e ottengo una stringa Base64
            String base64 = CrittografiaFile.decrittaContenuto(datiCriptati);

            //decodifico la stringa da Base64 a byte serializzati
            byte[] datiSerializzati = java.util.Base64.getDecoder().decode(base64);

            //deserializzo
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(datiSerializzati)))
            {

                //restituisco l'intero oggetto quindi con tutti i suoi attributi e metodi
                return (T) ois.readObject();

            }

        }
        catch (Exception e)
        {

            throw new IOException("Errore nella lettura del file cifrato: " + file, e);

        }

    }

}
