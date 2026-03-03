package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Terapia implements Serializable
{

    //le terapie ovviamente non possono essere prenotate, daato che saranno solo terapie farmacologiche
    private String CUImedico; //anche che effettuerà la visita
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String nomeFarmaco;
    private String dosaggio;
    private String frequenza;
    private String CUIterapia; //sarà T0, T1, T2

    public Terapia(String CUImedico, LocalDate dataInizio, LocalDate dataFine, String nomeFarmaco, String dosaggio, String frequenza, int numTerapie)
    {

        setCUImedico(CUImedico);
        setDataInizio(dataInizio);
        setDataFine(dataFine);
        setNomeFarmaco(nomeFarmaco);
        setDosaggio(dosaggio);
        setFrequenza(frequenza);
        setCUIterapia(numTerapie);

    }

    public String getCUImedico() {
        return CUImedico;
    }

    public void setCUImedico(String CUImedico) {
        this.CUImedico = CUImedico;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getNomeFarmaco() {
        return nomeFarmaco;
    }

    public void setNomeFarmaco(String nomeFarmaco) {
        this.nomeFarmaco = nomeFarmaco;
    }

    public String getDosaggio() {
        return dosaggio;
    }

    public void setDosaggio(String dosaggio) {
        this.dosaggio = dosaggio;
    }

    public String getFrequenza() {
        return frequenza;
    }

    public void setFrequenza(String frequenza) {
        this.frequenza = frequenza;
    }

    //crea il cui della terapia con il prefisso T e il numero delle terapie effettuate +1
    public void setCUIterapia(int numTerapie)
    {

        String temp = "T" + (numTerapie + 1);
        this.CUIterapia = temp;

    }

    public String getCUIterapia() {
        return CUIterapia;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Terapia terapia = (Terapia) o;
        return Objects.equals(CUImedico, terapia.CUImedico) && Objects.equals(dataInizio, terapia.dataInizio) && Objects.equals(dataFine, terapia.dataFine) && Objects.equals(nomeFarmaco, terapia.nomeFarmaco) && Objects.equals(dosaggio, terapia.dosaggio) && Objects.equals(frequenza, terapia.frequenza) && Objects.equals(CUIterapia, terapia.CUIterapia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CUImedico, dataInizio, dataFine, nomeFarmaco, dosaggio, frequenza, CUIterapia);
    }
}
