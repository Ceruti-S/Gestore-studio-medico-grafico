package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class IT extends Persona implements Serializable
{

    /*
     * Classe che definisce la persona IT support generica
     */

    //questo costruttore verrà solo usato per la creazione del primo IT di default
    //il resto delle volte ci saranno dei metodi dedicati
    public IT(String CUI, String nome, String cognome, LocalDate dataNascita, int eta, String codiceFiscale, String titoloStudio, String gradoReparto)
    {

        super(CUI, nome, cognome, dataNascita, eta, codiceFiscale);

        this.gradoReparto = gradoReparto; //direttore_IT / impiegato
        this.titoloStudio = titoloStudio;

    }
    public IT()
    {



    }

    private String gradoReparto;
    private String titoloStudio;

    //equals e hashCode devo aggiungerli anche alle altre classi di tipi di persone
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Fondamentale! Controlla nome, CUI, ecc.
        IT it = (IT) o;
        return Objects.equals(gradoReparto, it.gradoReparto) &&
                Objects.equals(titoloStudio, it.titoloStudio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gradoReparto, titoloStudio);
    }

    public String getGradoReparto()
    {

        return this.gradoReparto;

    }

    //settato tramite box con scelte
    public void setGradoReparto(String gradoReparto)
    {

        this.gradoReparto = gradoReparto;

    }

    public String getTitoloStudio()
    {

        return titoloStudio;

    }

    //settato tramite box con scelte con poi testo libero per spieficare (es. Laurea 3 anni - Informatica)
    public void setTitoloStudio(String titoloStudio)
    {

        this.titoloStudio = titoloStudio;

    }

}
