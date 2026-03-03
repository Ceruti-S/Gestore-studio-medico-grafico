package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Medico extends Persona implements Serializable
{

    public Medico()
    {}

    private String specializzazione;
    //lista dei CUI dei pazienti che ha in cura
    private List<String> pazientiInCarico = new ArrayList<>();
    //lo userò per tenere conto di che visite ha in programma il medico
    private List<Visita> agendaAppuntamentiVisite = new ArrayList<>();
    private List<Esame> agendaAppuntamentiEsame = new ArrayList<>();
    private int numEventiAgendaVisite = 0; //servirà per dare il cui alle visite prenotate per vederle dalla parte del medico
    //sarà tipo: VP1, VP2
    private int numEventiAgendaEsame = 0;

    public List<String> getPazientiInCarico()
    {
        return new ArrayList<>(pazientiInCarico);
    }

    public List<Visita> getAgendaAppuntamentiVisite() {
        return new ArrayList<>(agendaAppuntamentiVisite);
    }

    public List<Esame> getAgendaAppuntamentiEsami() {
        return new ArrayList<>(agendaAppuntamentiEsame);
    }

    public boolean aggiungiAppuntamentoEsame(String CUIpaziente, LocalDateTime dataOraEsame, String nomeEsame)
    {

        //poi aggiungo la prenotazione
        Esame esametemp = new Esame(CUIpaziente, dataOraEsame, nomeEsame, this.numEventiAgendaEsame);
        agendaAppuntamentiEsame.add(esametemp);

        numEventiAgendaEsame++;

        return true;

    }

    public boolean modificaAppuntamentoEsame(String CUIappuntamento, LocalDateTime dataOraPrenotazione)
    {

        for (Esame e : agendaAppuntamentiEsame)
        {

            if (e.getCUIesame().equals(CUIappuntamento))
            {

                e.setDataOraEsame(dataOraPrenotazione);
                return true;

            }

        }

        return false;

    }

    public boolean rimuoviAppuntamentoEsame(String CUIappuntamento)
    {

        for(int i=0; i<agendaAppuntamentiEsame.size(); i++)
        {

            if(agendaAppuntamentiEsame.get(i).getCUIesame().equals(CUIappuntamento))
            {

                agendaAppuntamentiEsame.remove(i);

                return true;

            }

        }

        return false;

    }

    public boolean aggiungiAppuntamentoVisita(String CUIpaziente, String motivo, String note, LocalDateTime dataOraPrenotazione)
    {

        //poi aggiungo la prenotazione
        Visita visitaTemp = new Visita(CUIpaziente, motivo, note, this.numEventiAgendaVisite, dataOraPrenotazione);
        agendaAppuntamentiVisite.add(visitaTemp);

        numEventiAgendaVisite++;

        return true;

    }

    public boolean rimuoviAppuntamentoVisita(String CUIappuntamento)
    {

        for(int i=0; i<agendaAppuntamentiVisite.size(); i++)
        {

            if(agendaAppuntamentiVisite.get(i).getCUIvisita().equals(CUIappuntamento))
            {

                agendaAppuntamentiVisite.remove(i);

                return true;

            }

        }

        return false;

    }

    public boolean modificaAppuntamentoVisita(String CUIappuntamento, LocalDateTime dataOraPrenotazione)
    {

        for (Visita v : agendaAppuntamentiVisite)
        {

            if (v.getCUIvisita().equals(CUIappuntamento))
            {

                v.setDataPrenotazione(dataOraPrenotazione);
                return true;

            }

        }
        return false;

    }

    public boolean aggiungiPaziente(String CUIpaziente)
    {

        if(CUIpaziente != null && !pazientiInCarico.contains(CUIpaziente))
        {

            this.pazientiInCarico.add(CUIpaziente);
            return true;

        }

        return false;

    }

    public boolean rimuoviPaziente(String CUIpaziente)
    {

        //rimuove il CUI se presente, returna true se esiste e lo elimina
        return pazientiInCarico.remove(CUIpaziente);

    }

    public String getSpecializzazione() {
        return specializzazione;
    }

    //settati tramite un box di scelta multipla
    public void setSpecializzazione(String specializzazione) {
        this.specializzazione = specializzazione;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Medico medico = (Medico) o;
        return numEventiAgendaVisite == medico.numEventiAgendaVisite && numEventiAgendaEsame == medico.numEventiAgendaEsame && Objects.equals(specializzazione, medico.specializzazione) && Objects.equals(pazientiInCarico, medico.pazientiInCarico) && Objects.equals(agendaAppuntamentiVisite, medico.agendaAppuntamentiVisite) && Objects.equals(agendaAppuntamentiEsame, medico.agendaAppuntamentiEsame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specializzazione, pazientiInCarico, agendaAppuntamentiVisite, agendaAppuntamentiEsame, numEventiAgendaVisite, numEventiAgendaEsame);
    }

}
