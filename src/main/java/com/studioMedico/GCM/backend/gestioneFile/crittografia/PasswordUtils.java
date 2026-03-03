package com.studioMedico.GCM.backend.gestioneFile.crittografia;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils
{

    //costruttore privato perchè non si deve istanziale la classe
    private PasswordUtils()
    {}

    public static String hashPassword(String password)
    {

        try
        {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            //converto byte[] in stringa esadecimale
            StringBuilder hexString = new StringBuilder();
            for(byte b : hash)
            {

                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1)
                    hexString.append("0");

                hexString.append(hex);

            }

            return hexString.toString();

        }
        catch(NoSuchAlgorithmException e)
        {

            throw new RuntimeException("Algoritmo SHA-256 non disponibile " , e);

        }

    }

}
