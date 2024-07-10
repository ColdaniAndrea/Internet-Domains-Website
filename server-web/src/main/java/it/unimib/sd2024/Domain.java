package it.unimib.sd2024;

import java.time.LocalDate;

public class Domain{
    private String nomeDominio; 
    private LocalDate dataRegistrazione;
    private int anniScadenza;
    private int idUtente, prezzo;

    // Metodi Getter e Setter
    public int getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(int prezzo) {
        this.prezzo = prezzo;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }
    
    public String getNomeDominio() {
        return nomeDominio;
    }

    public void setNomeDominio(String nomeDominio) {
        this.nomeDominio = nomeDominio;
    }

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public int getAnniScadenza() {
        return anniScadenza;
    }

    public void setAnniScadenza(int anniScadenza) {
        this.anniScadenza = anniScadenza;
    }
}

