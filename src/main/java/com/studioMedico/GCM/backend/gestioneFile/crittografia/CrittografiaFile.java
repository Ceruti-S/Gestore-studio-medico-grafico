package com.studioMedico.GCM.backend.gestioneFile.crittografia;

/*
 * Questa classe cripta/decritta il contenuto dei file che riceve e lo restituisce
 */

/*
 * una stringa in base64 è una stringa normale che però rappresenta solo file binari codificati usando solo i
 * caratteri di base64
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CrittografiaFile
{

    //la chiave di criptaggio predefinita per ogni file in byte(32 byte), nessuna la può vedere perchè è hardcoded nel codice.
    //tradotta a testo normale è: "?§å+ˆOv±ÀªÓg^úË1Mr¹Õè<÷"
    private static final byte[] chiaveByte = new byte[] {
            (byte)0x3F, (byte)0xA7, (byte)0x1C, (byte)0x9D,
            (byte)0xE5, (byte)0x2B, (byte)0x88, (byte)0x4F,
            (byte)0x76, (byte)0xB1, (byte)0xC0, (byte)0xAA,
            (byte)0xD3, (byte)0x67, (byte)0x5E, (byte)0x99,
            (byte)0x12, (byte)0xFA, (byte)0xCB, (byte)0x31,
            (byte)0x4D, (byte)0x8E, (byte)0x72, (byte)0xB9,
            (byte)0x06, (byte)0xD5, (byte)0xE8, (byte)0x1A,
            (byte)0x3C, (byte)0xF7, (byte)0x95, (byte)0x0B
    };
    //converto la chiave in byte nella chiave AES effettiva così da poterna usare
    private static final SecretKey chiaveCrittaggio = new SecretKeySpec(chiaveByte, "AES");

    //decripta un array di byte che rivece in ingresso e la restituisce in una stringa decriptata
    public static String decrittaContenuto(byte[] contenutoCrittato) throws Exception, IOException
    {

        Cipher cipher = Cipher.getInstance("AES");
        //Stavolta lo inizializzo con modalità decrittaggio
        cipher.init(Cipher.DECRYPT_MODE, chiaveCrittaggio);

        //decritto l'array di byte e lo metto dentro un array di byte non criptato
        byte[] contenutoDecrittato = cipher.doFinal(contenutoCrittato);

        //returno con un metodo che converte l'array di byte decrittati in una stringa in base64
        return new String(contenutoDecrittato, StandardCharsets.UTF_8);

    }

    //cripta una stringa in base64 che rivece in ingresso e la restituisce in un array di byte criptata
    public static byte[] criptaContenuto(String contenuto) throws Exception, IOException
    {

        //converto la stringa da convertire in byte
        byte[] contenutoByte = contenuto.getBytes(StandardCharsets.UTF_8);

        //creo la classe java che permetterà di crittografare e le dico che useremo AES
        Cipher cipher = Cipher.getInstance("AES");
        //inizializzo il cipher dicendogli che vogliamo criptare con la chiave di crittaggio creata prima,
        // dato che è a 32 bit useremo AES-256
        cipher.init(Cipher.ENCRYPT_MODE, chiaveCrittaggio);

        //returno con il metodo che prende l'array di byte e lo cripta
        return  cipher.doFinal(contenutoByte);

    }

}
