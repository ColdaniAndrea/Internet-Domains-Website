package it.unimib.sd2024;

public class CreditCard {
    private String numero, scadenza, intestatario;
    private int cvv;

    public CreditCard(String numero, String scadenza, int cvv, String intestatario){
        this.numero = numero;
        this.scadenza = scadenza;
        this.intestatario = intestatario;
        this.cvv = cvv;
    }

    public String getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(String intestatario) {
        this.intestatario = intestatario;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
