package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Visita implements Serializable
{

    public Visita(String CUImedico, String motivo, String diagnosi, String note, int numVisite)
    {

        setCUImedico(CUImedico);
        setMotivo(motivo);
        setDiagnosi(diagnosi);
        setNote(note);
        setDataOraVisita();
        setCUIvisita(numVisite);

    }

    public Visita(String CUImedico, String motivo, String note, int numVisitePrenotate, LocalDateTime dataOraVisitaPrenotata)
    {

        setCUImedico(CUImedico);
        setMotivo(motivo);
        setNote(note);
        setCUIvisitaPrenotata(numVisitePrenotate);
        setDataPrenotazione(dataOraVisitaPrenotata);
        this.diagnosi = "";

    }

    private String CUImedico;
    private LocalDateTime dataOraVisita; //anche prenotata
    private String motivo;
    private String diagnosi;
    private String note;
    private String CUIvisita; //sarà V0, V1, V2 oppure per le prenotate VP1, VP2

    public void setCUIvisitaPrenotata(int numVisitePrenotate)
    {

        String temp = "VP" +  (numVisitePrenotate+1);
        this.CUIvisita = temp;

    }

    public void setDataPrenotazione(LocalDateTime dataOraVisitaPrenotata)
    {

        this.dataOraVisita = dataOraVisitaPrenotata;

    }

    public LocalDateTime getDataOraVisita() {
        return dataOraVisita;
    }

    public void setDataOraVisita() {
        this.dataOraVisita = LocalDateTime.now();
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnosi() {
        return diagnosi;
    }

    public void setDiagnosi(String diagnosi) {
        this.diagnosi = diagnosi;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    //crea il cui della visita con il prefisso V e il numero delle visite effettuate +1
    public void setCUIvisita(int numVisite)
    {

        String temp = "V" + (numVisite + 1);
        this.CUIvisita = temp;

    }

    public String getCUIvisita() {
        return CUIvisita;
    }

    public String getCUImedico() {
        return CUImedico;
    }

    //dovrò controllare prima che esista, dopo lo passo
    public void setCUImedico(String CUImedico) {
        this.CUImedico = CUImedico;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Visita visita = (Visita) o;
        return Objects.equals(CUImedico, visita.CUImedico) && Objects.equals(dataOraVisita, visita.dataOraVisita) && Objects.equals(motivo, visita.motivo) && Objects.equals(diagnosi, visita.diagnosi) && Objects.equals(note, visita.note) && Objects.equals(CUIvisita, visita.CUIvisita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CUImedico, dataOraVisita, motivo, diagnosi, note, CUIvisita);
    }
}
