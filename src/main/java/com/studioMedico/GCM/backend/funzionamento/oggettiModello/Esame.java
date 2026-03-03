package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Esame implements Serializable
{

    private String CUImedico; //anche che effettuerà l'esame
    private LocalDateTime dataOraEsame; //anche prenotato
    private String risultato;
    private String nomeEsame;
    private String note;
    private String CUIesame; //sarà E0, E1, E2 oppure per le prenotate EP1, EP2

    //per gli esami effettuati
    public Esame(String CUImedico, LocalDateTime dataOraEsame, String risultato, String nomeEsame, String note, int numEsami)
    {

        setCUImedico(CUImedico);
        setDataOraEsame(dataOraEsame);
        setRisultato(risultato);
        setNomeEsame(nomeEsame);
        setNote(note);
        setCUIesame(numEsami);

    }

    //per gli esami prenotati
    public Esame(String CUImedico, LocalDateTime dataOraEsame, String nomeEsame, int numEsamiPrenotati)
    {

        setCUImedico(CUImedico);
        setDataOraEsame(dataOraEsame);
        setNomeEsame(nomeEsame);
        setCUIesamePrenotato(numEsamiPrenotati);

    }

    public void setCUIesamePrenotato(int numEsamiPrenotati)
    {

        String temp = "EP" +  (numEsamiPrenotati+1);
        this.CUIesame = temp;

    }

    public String getCUImedico() {
        return CUImedico;
    }

    public void setCUImedico(String CUImedico) {
        this.CUImedico = CUImedico;
    }

    public LocalDateTime getDataOraEsame() {
        return dataOraEsame;
    }

    public void setDataOraEsame(LocalDateTime dataOraEsame) {
        this.dataOraEsame = dataOraEsame;
    }

    public String getRisultato() {
        return risultato;
    }

    public void setRisultato(String risultato) {
        this.risultato = risultato;
    }

    public String getNomeEsame() {
        return nomeEsame;
    }

    public void setNomeEsame(String nomeEsame) {
        this.nomeEsame = nomeEsame;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCUIesame() {
        return CUIesame;
    }

    public void setCUIesame(int numEsami)
    {

        String temp = "E" + (numEsami + 1);
        this.CUIesame = temp;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Esame esame = (Esame) o;
        return Objects.equals(CUImedico, esame.CUImedico) && Objects.equals(dataOraEsame, esame.dataOraEsame) && Objects.equals(risultato, esame.risultato) && Objects.equals(nomeEsame, esame.nomeEsame) && Objects.equals(note, esame.note) && Objects.equals(CUIesame, esame.CUIesame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CUImedico, dataOraEsame, risultato, nomeEsame, note, CUIesame);
    }
}
