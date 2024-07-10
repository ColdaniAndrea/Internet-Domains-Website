package it.unimib.sd2024;

import net.sf.json.JSONObject;

public class Order {
    private String dominioInternet, dataOrdine, oggetto;
    private int prezzo, idUtente;
    private JSONObject cartaCredito;

    // constructor
    public Order(String dominioInternet, String dataOrdine, String oggetto, int prezzo, int idUtente, JSONObject cartaCredito) {
        this.dominioInternet = dominioInternet;
        this.dataOrdine = dataOrdine;
        this.oggetto = oggetto;
        this.prezzo = prezzo;
        this.idUtente = idUtente;
        this.cartaCredito = cartaCredito;
    }

    // getter e setter
   
    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getDominioInternet() {
        return dominioInternet;
    }

    public void setDominioInternet(String dominioInternet) {
        this.dominioInternet = dominioInternet;
    }

    public String getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(String dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public int getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(int prezzo) {
        this.prezzo = prezzo;
    }

    public JSONObject getCartaCredito() {
        return cartaCredito;
    }

    public void setCartaCredito(JSONObject cartaCredito) {
        this.cartaCredito = cartaCredito;
    }
}