package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Persona implements Serializable
{

    /*
     * Classe astratta che contiene tutti gli attributi comuni a ogni persona
     * con i loro relativi metodi, non implementati
     */

    public Persona()
    {}

    //costruttore che verrà usato solo per permettere di creare l'IT di default senza
    //passare dai metodi per validare i dati ma inserendo manualmente
    public Persona(String CUI, String nome, String cognome, LocalDate dataNascita, int eta, String codiceFiscale)
    {

        this.CUI = CUI;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.eta = eta;
        this.codiceFiscale = codiceFiscale;

    }

    //il cui è il codice unico identificativo di sistema
    private String CUI;
    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private int eta;
    private String codiceFiscale;

    public String pulisciStringa(String stringa)
    {

        return stringa.replaceAll(" ", "");

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return eta == persona.eta && Objects.equals(CUI, persona.CUI) && Objects.equals(nome, persona.nome) && Objects.equals(cognome, persona.cognome) && Objects.equals(dataNascita, persona.dataNascita) && Objects.equals(codiceFiscale, persona.codiceFiscale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CUI, nome, cognome, dataNascita, eta, codiceFiscale);
    }

    public String getCUI() {
        return CUI;
    }

    public void setCUI(String CUI) {
        this.CUI = CUI;
    }

    public String getNome() {
        return nome;
    }

    public boolean setNome(String nome) {

        if(nome == null)
            return false;

        nome = nome.trim();

        if(nome.isEmpty())
            return false;

        this.nome = nome;

        return true;

    }

    public String getCognome() {
        return cognome;
    }

    public boolean setCognome(String cognome)
    {

        if(cognome == null)
            return false;

        cognome = cognome.trim();

        if(cognome.isEmpty())
            return false;

        this.cognome = cognome;

        return true;

    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale)
    {

        codiceFiscale = codiceFiscale.trim();

        this.codiceFiscale = codiceFiscale;

    }

    public int getEta() {
        return eta;
    }

    public void  setEta()
    {

        if(this.dataNascita == null)
        {

            throw new IllegalArgumentException("La data di nascita non può essere null.");

        }

        int eta;
        eta = Period.between(this.dataNascita, LocalDate.now()).getYears();
        this.eta = eta;

    }

}
