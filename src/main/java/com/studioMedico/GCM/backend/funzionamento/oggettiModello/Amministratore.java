package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.util.Objects;

public class Amministratore extends Persona implements Serializable
{

    public Amministratore()
    {}

    private String titoloStudio;

    public String getTitoloStudio()
    {

        return titoloStudio;

    }

    //settato tramite box con scelte con poi testo libero per spieficare (es. Laurea 3 anni - Informatica)
    public void setTitoloStudio(String titoloStudio)
    {

        this.titoloStudio = titoloStudio;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Amministratore that = (Amministratore) o;
        return Objects.equals(titoloStudio, that.titoloStudio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), titoloStudio);
    }
}
