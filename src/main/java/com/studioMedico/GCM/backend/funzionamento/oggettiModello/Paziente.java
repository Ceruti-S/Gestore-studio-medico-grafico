package com.studioMedico.GCM.backend.funzionamento.oggettiModello;

import com.studioMedico.GCM.backend.funzionamento.*;
import com.studioMedico.GCM.backend.gestioneFile.modifica.ScritturaFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * si noti che per il corretto funzionamento di questa classe col salvataggio file bisogna sovrascrivere nel codice
 * frontend il file del paziente con l'oggetto del paziente che si sarà salvato in locale e i darti che le vengono
 * passati devono essere già validati!
 * ATTENZIONE: le terapie, esami, visite effettuate non possono essere eliminate!!!
 */

public class Paziente extends Persona implements Serializable
{

    public Paziente()
    {}

    private String telefono; //senza prefisso
    private String indirizzo;

    private String gruppoSanguigno;
    private List<String> allergie = new ArrayList<>();
    private List<String> patologie = new ArrayList<>();
    private List<Visita> visite = new ArrayList<>();
    private List<Visita> visitePrenotate = new ArrayList<>();
    private List<Terapia> terapie = new ArrayList<>();
    private List<Esame> esami = new ArrayList<>();
    private List<Esame> esamiPrenotati = new ArrayList<>();
    private int numVisite = 0; //quante visite ha fatto il paziente
    private int numTerapie = 0; //quante terapie ha fatto il paziente
    private int numEsami = 0; //quanti esami ha fatto il paziente
    //prenotato o prescritto
    private int numVisitePrenotate = 0; //quante visite ha prenotato il paziente
    private int numEsamiPrenotate = 0; //quanti esami ha prenotato il paziente

    public boolean modificaEsameEffettuato(String CUIesame, String risultato, String nomeEsame, String note)
    {

        Esame esameTemp = null;
        int index = -1;
        for(int i=0; i<esami.size();i++)
        {

            if(esami.get(i).getCUIesame().equals(CUIesame))
            {

                esameTemp = esami.get(i);
                index = i;
                break;

            }

        }

        if(esameTemp == null)
            return false;

        if(!hasPermessoAggiungere(esameTemp.getCUImedico()))
            return false;

        esameTemp.setRisultato(risultato);
        esameTemp.setNomeEsame(nomeEsame);
        esameTemp.setNote(note);

        esami.set(index, esameTemp);

        return true;

    }

    public boolean modificaVisitaEffettuata(String CUIvisita, String diagnosi, String note)
    {

        Visita visitaTemp = null;
        int index = -1;
        for(int i=0; i<visite.size(); i++)
        {

            if(visite.get(i).getCUIvisita().equals(CUIvisita))
            {

                visitaTemp = visite.get(i);
                index = i;
                break;

            }

        }

        if(visitaTemp == null)
            return false;

        if(!hasPermessoAggiungere(visitaTemp.getCUImedico()))
            return false;

        visitaTemp.setDiagnosi(diagnosi);
        visitaTemp.setNote(note);

        visite.set(index, visitaTemp);

        return true;

    }

    public boolean modificaVisitaPrenotata(String CUIvisita, LocalDateTime dataOraVisita)
    {

        Visita visitaTemp = null;
        int index = -1;
        for(int i=0; i<visitePrenotate.size(); i++)
        {

            if(visitePrenotate.get(i).getCUIvisita().equals(CUIvisita))
            {

                visitaTemp = visitePrenotate.get(i);
                index = i;
                break;

            }

        }

        if(visitaTemp == null)
            return false;

        if(!hasPermessoPrenotare(visitaTemp.getCUImedico()))
            return false;

        visitaTemp.setDataPrenotazione(dataOraVisita);

        visitePrenotate.set(index, visitaTemp);

        return true;

    }

    public boolean modificaEsamePrenotato(String CUIesame, LocalDateTime dataOraEsame)
    {

        Esame esameTemp = null;
        int index = -1;
        for(int i=0; i<esamiPrenotati.size(); i++)
        {

            if(esamiPrenotati.get(i).getCUIesame().equals(CUIesame))
            {

                esameTemp = esamiPrenotati.get(i);
                index = i;
                break;

            }

        }

        if(esameTemp == null)
            return false;

        if(!hasPermessoPrenotare(esameTemp.getCUImedico()))
            return false;

        esameTemp.setDataOraEsame(dataOraEsame);

        esamiPrenotati.set(index, esameTemp);

        return true;

    }

    public boolean modificaTerapia(String CUIterapia, String dosaggio, String frequenza, String nomeFarmaco, LocalDate dataInizio, LocalDate dataFine)
    {

        //prima di tutto controllo che la terapia esista
        Terapia terapiaTemp = null;
        int index = -1;
        for(int i=0; i<terapie.size(); i++)
        {

            if(terapie.get(i).getCUIterapia().equals(CUIterapia))
            {

                terapiaTemp = terapie.get(i);
                index = i;
                break;

            }

        }

        if(terapiaTemp == null)
            return false;

        //i permessi sono gli stessi di quando si deve aggiungere la terapia
        //poi controllo l'utente attivo se ha il permesso di modificare una terapia
        if(!hasPermessoAggiungere(terapiaTemp.getCUImedico()))
            return false;

        terapiaTemp.setDosaggio(dosaggio);
        terapiaTemp.setFrequenza(frequenza);
        terapiaTemp.setNomeFarmaco(nomeFarmaco);
        terapiaTemp.setDataInizio(dataInizio);
        terapiaTemp.setDataFine(dataFine);

        terapie.set(index, terapiaTemp);

        return true;

    }

    public boolean eliminaEsamePrenotato(String CUIesamePrenotato)
    {

        //se l'utente attivo è un amministratore/ITinrecovery a priori non può eliminare esami prenotati
        char prefissoUtenteAttivo = ControlloLogin.utenteAttivo.charAt(0);
        if(prefissoUtenteAttivo == 'A' || prefissoUtenteAttivo == 'R')
            return false;

        Esame esameTemp = null;

        for(int i=0; i<esamiPrenotati.size(); i++)
        {

            if(esamiPrenotati.get(i).getCUIesame().equals(CUIesamePrenotato))
            {

                esameTemp = esamiPrenotati.get(i);
                break;

            }

        }

        //se è null vuol dire che non esiste
        if(esameTemp == null)
            return false;

        //se l'utetente attivo è un medico può eliminare la visita/esame solo se la elimina con lui stesso
        if(prefissoUtenteAttivo == 'M')
        {

            //se il CUI dell'utetne attivo e del medico sono diversi non può eliminare
            if(!ControlloLogin.utenteAttivo.equals(esameTemp.getCUImedico()))
                return false;

        }

        esamiPrenotati.remove(esameTemp);

        return true;

    }

    public boolean eliminaVisitaPrenotata(String CUIvisitaPrenotata)
    {

        //se l'utente attivo è un amministratore/ITinrecovery a priori non può eliminare visite prenotate
        char prefissoUtenteAttivo = ControlloLogin.utenteAttivo.charAt(0);
        if(prefissoUtenteAttivo == 'A' || prefissoUtenteAttivo == 'R')
            return false;

        Visita visitaTemp = null;

        for(int i=0; i<visitePrenotate.size(); i++)
        {

            if(visitePrenotate.get(i).getCUIvisita().equals(CUIvisitaPrenotata))
            {

                visitaTemp = visitePrenotate.get(i);
                break;

            }

        }

        //se è null vuol dire che non esiste
        if(visitaTemp == null)
            return false;

        //se l'utetente attivo è un medico può eliminare la visita/esame solo se la elimina con lui stesso
        if(prefissoUtenteAttivo == 'M')
        {

            //se il CUI dell'utetne attivo e del medico sono diversi non può eliminare
            if(!ControlloLogin.utenteAttivo.equals(visitaTemp.getCUImedico()))
                return false;

        }

        visitePrenotate.remove(visitaTemp);

        return true;

    }

    public List<Visita> getVisite()
    {
        return new ArrayList<>(visite);
    }
    public List<Visita> getVisitePrenotate()
    {
        return new ArrayList<>(visitePrenotate);
    }

    public List<Terapia> getTerapie()
    {
        return new ArrayList<>(terapie);
    }

    public List<Esame> getEsami()
    {
        return new ArrayList<>(esami);
    }
    public List<Esame> getEsamiPrenotati()
    {
        return  new ArrayList<>(esamiPrenotati);
    }

    public boolean aggiungiEsameEffettuato(String CUImedico, String risultato, String nomeEsame, String note)
    {

        //prima di tutto controllo se il CUI del medico esiste davvero
        if(CUImedico == null || CUImedico.isEmpty())
            return false;
        if(!ValidatoreDati.controllaEsistenzaCui(CUImedico))
            return false;

        //poi controllo l'utente attivo se ha il permesso di aggiungere un esame effettuato
        if(!hasPermessoAggiungere(CUImedico))
            return false;

        //poi aggiungo l'esame effettuato e returno true se va tutto a buon fine

        Esame esameTemp = new Esame(CUImedico, LocalDateTime.now(), risultato, nomeEsame, note, numEsami);
        esami.add(esameTemp);

        numEsami++;

        Log logTemp = new Log(LocalDateTime.now(), ControlloLogin.utenteAttivo, "Ha effettuato un esame al paziente: " + getCUI());
        try
        {

            Log.aggiungiLog(logTemp);

        }
        catch(IOException e)
        {

            System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e.getMessage());

        }

        return true;

    }

    public boolean aggiungiEsamePrenotato(String CUImedico,  LocalDateTime dataOraEsame, String nomeEsame)
    {

        //prima di tutto controllo se il CUI del medico esiste davvero
        if(CUImedico == null || CUImedico.isEmpty())
            return false;
        if(!ValidatoreDati.controllaEsistenzaCui(CUImedico))
            return false;

        //poi controllo se l'utente attivo può aggiungere la prenotazione
        if(!hasPermessoPrenotare(CUImedico))
            return false;

        //poi aggiungo la prenotazione
        Esame esameTemp = new Esame(CUImedico, dataOraEsame, nomeEsame, numEsamiPrenotate);
        esamiPrenotati.add(esameTemp);

        numEsamiPrenotate++;

        return true;

    }

    public boolean aggiungiTerapia(String CUImedico, LocalDate dataInizio, LocalDate dataFine, String nomeFarmaco, String dosaggio, String frequenza)
    {

        //prima di tutto controllo se il CUI del medico esiste davvero
        if(CUImedico == null || CUImedico.isEmpty())
            return false;
        if(!ValidatoreDati.controllaEsistenzaCui(CUImedico))
            return false;

        //poi controllo l'utente attivo se ha il permesso di aggiungere una terapia
        if(!hasPermessoAggiungere(CUImedico))
            return false;

        //poi aggiungo la terapia prescritta e returno true se va tutto a buon fine

        Terapia terapiaTemp = new Terapia(CUImedico, dataInizio, dataFine, nomeFarmaco, dosaggio, frequenza, numTerapie);
        terapie.add(terapiaTemp);

        numTerapie++;

        Log logTemp = new Log(LocalDateTime.now(), ControlloLogin.utenteAttivo, "Ha prescritto una terapia al paziente: " + getCUI());
        try
        {

            Log.aggiungiLog(logTemp);

        }
        catch(IOException e)
        {

            System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e.getMessage());

        }

        return true;

    }

    public boolean aggiungiVisitaEffettuata(String CUImedico, String motivo, String diagnosi, String note)
    {

        //prima di tutto controllo se il CUI del medico esiste davvero
        if(CUImedico == null || CUImedico.isEmpty())
            return false;
        if(!ValidatoreDati.controllaEsistenzaCui(CUImedico))
            return false;

        //poi controllo l'utente attivo se ha il permesso di aggiungere una visita effettuata
        if(!hasPermessoAggiungere(CUImedico))
            return false;

        //poi aggiungo la visita effettiva, se va a buon fine returno true (quindi sempre apparte se va in eccezione)

        Visita visitaTemp = new Visita(CUImedico, motivo, diagnosi, note, this.numVisite);
        visite.add(visitaTemp);

        numVisite++;

        Log logTemp = new Log(LocalDateTime.now(), ControlloLogin.utenteAttivo, "Ha effettuato una visita al paziente: " + getCUI());
        try
        {

            Log.aggiungiLog(logTemp);

        }
        catch(IOException e)
        {

            System.err.println("Errore non fatale[1] durante l'aggiunta di un log: " + e.getMessage());

        }

        return true;

    }

    public boolean aggiungiVisitaPrenotata(String CUImedico, String motivo, String note, LocalDateTime dataOraPrenotazione)
    {

        //prima di tutto controllo se il CUI del medico esiste davvero
        if(CUImedico == null || CUImedico.isEmpty())
            return false;
        if(!ValidatoreDati.controllaEsistenzaCui(CUImedico))
            return false;

        //poi controllo se l'utente attivo può aggiungere la prenotazione
        if(!hasPermessoPrenotare(CUImedico))
            return false;

        //poi aggiungo la prenotazione
        Visita visitaTemp = new Visita(CUImedico, motivo, note, this.numVisitePrenotate, dataOraPrenotazione);
        visitePrenotate.add(visitaTemp);

        numVisitePrenotate++;

        return true;

    }

    private boolean hasPermessoPrenotare(String CUImedico)
    {

        //se l'utente attivo è un amministratore/ITinrecovery a priori non può aggiungere visite prenotate
        char prefissoUtenteAttivo = ControlloLogin.utenteAttivo.charAt(0);
        if(prefissoUtenteAttivo == 'A' || prefissoUtenteAttivo == 'R')
            return false;

        //se l'utetente attivo è un medico può prenotare visita solo se la prenota con lui stesso
        if(prefissoUtenteAttivo == 'M')
        {

            //se il CUI dell'utetne attivo e del medico sono diversi non può aggiungere la visita prenotata
            if(!ControlloLogin.utenteAttivo.equals(CUImedico))
                return false;

        }

        return true;

    }

    private boolean hasPermessoAggiungere(String CUImedico)
    {

        //se l'utente attivo è un amministratore/ITinrecovery/segretario a priori non può aggiungere visite/esami/terapie effettuate
        char prefissoUtenteAttivo = ControlloLogin.utenteAttivo.charAt(0);
        if(prefissoUtenteAttivo == 'A' || prefissoUtenteAttivo == 'R' || prefissoUtenteAttivo == 'S')
            return false;

        //se l'utente attivo è un medico, può aggiugere una visita solo a suo nome
        if(prefissoUtenteAttivo == 'M')
        {

            //se il CUI dell'utetne attivo e del medico sono diversi non può aggiungere la visita
            if(!ControlloLogin.utenteAttivo.equals(CUImedico))
                return false;

        }

        return true;

    }

    public String getPatologie()
    {

        if(this.patologie == null || this.patologie.isEmpty())
        {

            return "Nessuna patologia registrata.";

        }

        return String.join("\n", this.patologie);

    }

    public boolean aggiungiPatologia(String patologia)
    {

        if(!controllaDoppioniPatologia(patologia))
        {

            patologie.add(patologia);
            return true;

        }

        return false;

    }
    private boolean  controllaDoppioniPatologia(String patologia)
    {

        for(int i = 0; i < this.patologie.size(); i++)
        {

            if(this.patologie.get(i).equals(patologia))
            {

                return true;

            }

        }

        return false;

    }

    public String getAllergie()
    {

        if(this.allergie == null || this.allergie.isEmpty())
        {

            return "Nessuna allergia registrata.";

        }

        return String.join("\n", this.allergie);

    }

    //returna true se ho inserito il dato (lo inserisco se è valido)
    public boolean aggiungiAllergia(String allergia)
    {

        allergia = allergia.trim().toLowerCase();

        //se non è già registrata l'allergia
        if(!controlloDoppioniAllergie(allergia))
        {

            allergie.add(allergia);

            return true;

        }

        return false;

    }
    //returna false se non trova doppioni
    private boolean controlloDoppioniAllergie(String allergia)
    {

        for(int i = 0; i < this.allergie.size(); i++)
        {

            if(this.allergie.get(i).equals(allergia))
            {

                return true;

            }

        }

        return false;

    }

    public String getGruppoSanguigno() {
        return gruppoSanguigno;
    }

    //returna true se ho inserito il dato (lo inserisco se è valido)
    public boolean setGruppoSanguigno(String gruppoSanguigno)
    {

        gruppoSanguigno = pulisciStringa(gruppoSanguigno);

        //se il gruppo sanguigno è valido lo setto
        if(ValidatoreDati.isGruppoSanguignoValido(gruppoSanguigno))
        {

            this.gruppoSanguigno = gruppoSanguigno;
            return true;

        }

        return false;

    }

    public String getTelefono() {
        return telefono;
    }

    public boolean setTelefono(String telefono)
    {

        telefono = pulisciStringa(telefono);

        if(ValidatoreDati.isTelefonoValido(telefono))
        {

            this.telefono = telefono;
            return true;

        }
        else
            return false;

    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public boolean setIndirizzo(String indirizzo) {

        if(indirizzo == null || indirizzo.isEmpty())
            return false;

        this.indirizzo = indirizzo;

        return true;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Paziente paziente = (Paziente) o;
        return numVisite == paziente.numVisite && numTerapie == paziente.numTerapie && numEsami == paziente.numEsami && numVisitePrenotate == paziente.numVisitePrenotate && numEsamiPrenotate == paziente.numEsamiPrenotate && Objects.equals(telefono, paziente.telefono) && Objects.equals(indirizzo, paziente.indirizzo) && Objects.equals(gruppoSanguigno, paziente.gruppoSanguigno) && Objects.equals(allergie, paziente.allergie) && Objects.equals(patologie, paziente.patologie) && Objects.equals(visite, paziente.visite) && Objects.equals(visitePrenotate, paziente.visitePrenotate) && Objects.equals(terapie, paziente.terapie) && Objects.equals(esami, paziente.esami) && Objects.equals(esamiPrenotati, paziente.esamiPrenotati);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), telefono, indirizzo, gruppoSanguigno, allergie, patologie, visite, visitePrenotate, terapie, esami, esamiPrenotati, numVisite, numTerapie, numEsami, numVisitePrenotate, numEsamiPrenotate);
    }
}
