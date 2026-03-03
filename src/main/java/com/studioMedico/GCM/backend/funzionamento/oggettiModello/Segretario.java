package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.util.Objects;

public class Segretario extends Persona implements Serializable
{

    public Segretario()
    {}

    private String turnoLavoro; //da che ora a che ora, che giorni
    /*
     * (es. della stringa: Lunedi 8:00-18:00 \n Giovedì ecc...) verrà inserito tramite box per evitare inserimenti
     * invalidi
     */

    public String getTurnoLavoro() {
        return turnoLavoro;
    }

    public void setTurnoLavoro(String turnoLavoro) {
        this.turnoLavoro = turnoLavoro;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Segretario that = (Segretario) o;
        return Objects.equals(turnoLavoro, that.turnoLavoro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), turnoLavoro);
    }
}
