package it.unimib.sd2024;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Rappresenta la risorsa "domains" in "http://localhost:8080/domains".
 */

@Path("domains")
public class DomainResource {
    private static RequestHandler requestHandler = new RequestHandler();
    private AtomicBoolean acquistoInCorso = new AtomicBoolean(false);
    String patternIntestatario = "^[a-zA-Z]+\\s[a-zA-Z]+$";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yy");
    private static final String LAST_ID_CMD = "LAST_ID";
    private static final String NEW_USER_CMD = "NEW_USER";
    private static final String RETURN_DOMAINS_CMD = "RETURN_DOMAINS";
    private static final String IS_AVAILABLE_CMD = "IS_AVAILABLE";
    private static final String RETURN_ORDERS_CMD = "RETURN_ORDERS";
    private static final String REGISTER_CMD = "REGISTER";
    private static final String RENEW_CMD = "RENEW";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMainPage() {
        String message = "Server-web funzionante";
        return Response.ok().entity(message).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        StringBuilder responseID = new StringBuilder();
        StringBuilder response = new StringBuilder();
        URI uri = null;
        if(user.getNome().equals("") || user.getCognome().equals("") || user.getEmail().equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Campi mancanti").build();
        }
        try {
            //cerco il primo id disponibile
            responseID.append(requestHandler.newRequest(LAST_ID_CMD));
            if(responseID.toString().equals("ERROR")){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            } 
            // assegno al nuovo utente il primo id disponibile evitando la concorrenza con AtomicInteger 
            Integer lastId = Integer.parseInt(responseID.toString());
            requestHandler.setLastId(new AtomicInteger(lastId));
            Integer idNewUserId = requestHandler.createUniqueId();
            user.setId(idNewUserId);

            response.append(requestHandler.newRequest(NEW_USER_CMD + "'" +
                                                    user.getNome() + "$" +
                                                    user.getCognome() + "$" +
                                                    user.getEmail() + "$" +
                                                    user.getId() + "'"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.toString().equals("NOT_CREATED")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (response.toString().equals("ALREADY_EXISTS")) {
            return Response.status(Response.Status.CONFLICT).build();
        } else if (response.toString().equals("CREATED")){
            try {
                uri = new URI("http://localhost:8080/domains/" + user.getId());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return Response.created(uri).header("Access-Control-Expose-Headers", "Location").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // api per la pagina dell'utente
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPage(@PathParam("id") Integer id) {
         // controllo esistenza utente
         try {
            if (!requestHandler.newRequest("IS_USER" + "'" + id + "'").equals("EXISTS")) {
                return Response.status(Response.Status.NOT_FOUND).entity("Utente Non Trovato").build();
            } else {
                return Response.ok().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }

    @Path("/{id}/userDomains")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDomains(@PathParam("id") Integer id) {
        StringBuilder response = new StringBuilder();
        try {
            response.append(requestHandler.newRequest(RETURN_DOMAINS_CMD + "'" + id + "'"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.toString().equals("NOT_FOUND")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(response.toString()).build();
        } 
    }

    @Path("{id}/{domain}/availability")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailabilityDomain(@PathParam("id") Integer id, @PathParam("domain") String domain) {
        StringBuilder response = new StringBuilder();
        try {
            response.append(requestHandler.newRequest(IS_AVAILABLE_CMD + "'" + domain + "'"));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        if (response.toString().equals("AVAILABLE")) {
            return Response.ok().entity("Dominio disponibile").build();
        } else if (response.toString().equals("ERROR")) {
            return Response.status(Response.Status.NOT_FOUND).entity("Errore").build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(response.toString()).build();
        }
    }

    @Path("{id}/orders")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@PathParam("id") Integer id) {
        StringBuilder response = new StringBuilder();
        try {
            response.append(requestHandler.newRequest(RETURN_ORDERS_CMD + "'" + id + "'"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.toString().equals("NOT_FOUND")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(response.toString()).build();
        }
    }

    @Path("/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerNewDomain(@PathParam("id") Integer id, DomainCarta domainCarta) {
        //recupero oggetti da domainCarta
        Domain domain = domainCarta.getDomain();
        CartaCredito cartaUtente = domainCarta.getCartaUtente();
        if (domain == null || cartaUtente == null) {
            return Response.status(Status.BAD_REQUEST).entity("Oggetti null").build();
        }

        //verifico che il dominio sia available
        StringBuilder responseAvailability = new StringBuilder();
        try {
            responseAvailability.append(requestHandler.newRequest(IS_AVAILABLE_CMD + "'" + domain.getNomeDominio() + "'"));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        if(!responseAvailability.toString().equals("AVAILABLE")){
            return Response.status(Response.Status.BAD_REQUEST).entity("Dominio not available").build();
        }

        //controllo che gli anni di scadenza siano minori di 10
        if(domain.getAnniScadenza() > 10){
            return Response.status(Status.BAD_REQUEST).entity("Anni scandenza superiori a 10").build();
        }
        //controllo concorrenza
        synchronized(this) {
            if(acquistoInCorso.get()) {
                return Response.status(Status.BAD_REQUEST).entity("Acquisto già in corso").build();
            }
            acquistoInCorso.set(true);

            StringBuilder response = new StringBuilder();
            URI uri = null;

            // prezzo dominio
            requestHandler.setAnniScadenza(domain.getAnniScadenza());
            domain.setPrezzo(requestHandler.nuovoPrezzo()); 

            // data registrazione
            LocalDate currenDate = LocalDate.now();
            domain.setDataRegistrazione(currenDate);

            //controllo formato di intestatario
            if(!cartaUtente.getIntestatario().matches(patternIntestatario)) {
                return Response.status(Status.BAD_REQUEST).entity("Formato intestatario errato").build();
            }

            //controllo che scadenza sia nel formato corretto
            try {
                YearMonth dataInserita = YearMonth.parse(cartaUtente.getScadenza(), formatter);
                YearMonth meseCorrente = YearMonth.now();

                if (!dataInserita.isAfter(meseCorrente)) {
                    return Response.status(Status.BAD_REQUEST).entity("Formato data errata").build();
                }

            } catch (DateTimeParseException e) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            
            try {
                response.append(requestHandler.newRequest(REGISTER_CMD + "'" +
                                domain.getNomeDominio() + "$" + 
                                domain.getAnniScadenza() + "$" +
                                id + "$" +
                                domain.getPrezzo() + "$" + 
                                cartaUtente.getNumeroCarta() + "$" +
                                cartaUtente.getScadenza() + "$" +
                                cartaUtente.getCvv() + "$" +
                                cartaUtente.getIntestatario()  +  "'"));
        
            } catch (IOException e) {
                e.printStackTrace();
            } 
            if (response.toString().equals("UNSUCCESS")) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            } else if (response.toString().equals("NO_USER")) {
                return Response.status(Response.Status.NOT_FOUND).entity("User inesistente").build();
            } else if (response.toString().equals("SUCCESS")){
                try {
                    uri = new URI("http://localhost:8080/domains/" + id + "/" + domain.getNomeDominio());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return Response.created(uri).header("Access-Control-Expose-Headers", "Location").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }   
    }

    @Path("/{id}/{domain}/renew")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renewDomain(@PathParam("id") Integer id, @PathParam("domain") String domainName, DomainCarta domainCarta) {
        //recupero oggetti da domainCarta
        Domain domain = domainCarta.getDomain();
        CartaCredito cartaUtente = domainCarta.getCartaUtente();
        StringBuilder response = new StringBuilder();

        if (domain == null || cartaUtente == null) {
            return Response.status(Status.CONFLICT).entity("Oggetti null").build();
        }

        // prezzo dominio
        requestHandler.setAnniScadenza(domain.getAnniScadenza());
        domain.setPrezzo(requestHandler.nuovoPrezzo());

        //controllo formato di intestatario
        if(!cartaUtente.getIntestatario().matches(patternIntestatario)) {
            return Response.status(Status.CONFLICT).entity("Formato intestatario errato").build();
        }

        //controllo che scadenza sia nel formato corretto
        try {
            YearMonth dataInserita = YearMonth.parse(cartaUtente.getScadenza(), formatter);
            YearMonth meseCorrente = YearMonth.now();

            if (!dataInserita.isAfter(meseCorrente)) {
                return Response.status(Status.CONFLICT).entity("Formato data errata").build();
            }

        } catch (DateTimeParseException e) {
            return Response.status(Status.CONFLICT).build();
        }

        try {
            response.append(requestHandler.newRequest(RENEW_CMD + "'" +
                            domain.getNomeDominio() + "$" + 
                            domain.getAnniScadenza() + "$" +
                            domain.getPrezzo() + "$" + 
                            cartaUtente.getNumeroCarta() + "$" +
                            cartaUtente.getScadenza() + "$" +
                            cartaUtente.getCvv() + "$" +
                            cartaUtente.getIntestatario()  +  "'"));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        if (response.toString().equals("UNSUCCESS")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (response.toString().equals("NOT_USER")) {
            return Response.status(Response.Status.NOT_FOUND).entity("Non esiste dominio").build();
        } else if (response.toString().equals("CANNOT_RENEW")) {
            return Response.status(Response.Status.CONFLICT).entity("Superata la soglia dei 10 anni").build();
        } else if (response.toString().equals("SUCCESS")){
            return Response.status(Response.Status.NO_CONTENT).entity("").build();
            // se ci sono problemi con 204
            //return Response.ok().build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}