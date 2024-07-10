package it.unimib.sd2024;

public class Domain{
    private String dominio, dataRegistrazione, dataScadenza;
    private int idUtente, prezzo;

    // Costruttore
    public Domain(String dominio, String dataRegistrazione, String dataScadenza, int idUtente, int prezzo) {
        this.dominio = dominio;
        this.dataRegistrazione = dataRegistrazione;
        this.dataScadenza = dataScadenza;
        this.idUtente = idUtente;
        this.prezzo = prezzo;
    }

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
    
    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(String dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public String getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }
}