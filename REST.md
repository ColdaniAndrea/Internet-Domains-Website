# Progetto Sistemi Distribuiti 2023-2024 - API REST

Documentazione delle API REST del web-server della risorsa "Domini Giustini"

**Attenzione**: l'unica rappresentazione ammessa è in formato JSON. Pertanto vengono assunti gli header `Content-Type: application/json` e `Accept: application/json`.

## `/domains`

Ogni risorsa ha la sua sezione dedicata con i metodi ammessi. In questo caso si riferisce alla risorsa `/domains`.

### GET `/domains`

**Descrizione**: Restituisce la pagina principale della risorsa.

**Parametri**: nessuno.

**Header**: nessuno.

**Body richiesta**: nessuna.

**Risposta**: Ritorna una stringa che indica che il server è in funzione.

**Codici di stato restituiti**: 
* restituisce sempre 200 OK.


### GET `/domains/{id}`

**Descrizione**: Restituisce la pagina associata all'utente.

**Parametri**: Parametro "PathParam" id di tipo int che indica l'utente che ha effettuato la richiesta.

**Header**: nessuno.

**Body richiesta**: nessuna.

**Risposta**: In caso di successo mostra la pagina associata all'utente.

**Codici di stato restituiti**: 
* 200 OK : in caso di successo.
* 404 Non Found : se l'id dell'utente non esiste.


### GET `/domains/{id}/userDomains`

**Descrizione**: Restituisce la pagina di tutti i domini associati all'utente.

**Parametri**: Parametro "PathParam" id di tipo int che indica l'utente che ha effettuato la richiesta.

**Header**: nessuno.

**Body richiesta**: nessuna.

**Risposta**: viene restituita un array json con tutti i domini:
[
  {
    "dominio": String,
    "prezzo": int,
    "dataRegistrazione": String ("AAAA-MM-DD"),
    "idUtente": int,
    "dataScadenza": String ("AAAA-MM-DD")
  }, ..
]

**Codici di stato restituiti**: 
* 200 OK : in caso di successo.
* 404 Non Found : se ci sono errori.


### GET `/domains/{id}/orders`

**Descrizione**: Restituisce la pagina di tutti gli ordini associati all'utente.

**Parametri**: Parametro "PathParam" id di tipo int che indica l'utente che ha effettuato la richiesta.

**Header**: nessuno.

**Body richiesta**: nessuna.

**Risposta**: 
[
  {
    "cvv": int,
    "scadenza": String ("MM-AA"),
    "intestatario": String,
    "numeroCarta": String,
    "oggetto": String ("Registrazione/Rinnovo"),
    "prezzo": int,
    "idUtente": int,
    "dataOrdine": String ("AAAA-MM-DD"),
    "dominioInternet": String
  }
]

**Codici di stato restituiti**: 
* 200 OK : in caso di successo.
* 404 Non Found : se ci sono errori.


### GET `/domains/{id}/{domain}/availability`

**Descrizione**: Restituisce la disponibilità di un dominio

**Parametri**: 
1. Parametro "PathParam" id di tipo int che indica l'utente che ha effettuato la richiesta.
2. Parametro "PathParam" domain di tipo String che indica il dominio a cui rinnovare la scadenza.

**Header**: nessuno.

**Body richiesta**: nessuna.

**Risposta**: 
In caso di successo resituisce la stringa (json) "Dominio disponibile"
Se ci sono errori una stringa (json) "Errore"
Altrimenti se il dominio non è disponibile un oggetto json con le informazioni del proprietario:
{
    "name": String,
    "surname": String,
    "email": String
}

**Codici di stato restituiti**: 
* 200 OK : in caso di successo.
* 404 Non Found : se ci sono errori.
* 409 Conflict : se il dominio non è disponibile.


### POST `/domains`

**Descrizione**: Aggiunge un nuovo utente nel database.

**Parametri**: Parametro user di tipo User che indica l'utente da inserire.

**Header**: nessuno.

**Body richiesta**: rappresentazione in formato json del seguenti campi:
{
  "nome": String,
  "cognome": String,
  "email": String
}

**Risposta**: In caso di successo il body è vuoto e la risorsa creata è indicata nell'header `Location`.

**Codici di stato restituiti**:
* 201 Created : in caso di successo.
* 400 Bad Request : in caso di campi mancanti
* 404 Non Found: in caso di errori.
* 409 Conflict: se l'utente esiste già.

### POST `/domains/{id}`

**Descrizione**: Aggiunge un nuovo dominio nel database.

**Parametri**: 
1. Parametro "PathParam" id di tipo int che indica l'utente che ha effettuato la richiesta.
2. Parametro DomainCarta, cioè un wrapper di dominio e carta.

**Header**: nessuno.

**Body richiesta**: rappresentazione in formato json dei due oggetti dominio e cartaCredito
{
    "domain": {
        "nomeDominio": String,
        "anniScadenza": int (tra 1-10)
    },
    "cartaUtente": {
        "intestatario": String,
        "numeroCarta": String,
        "scadenza": String ("MM-AA"),
        "cvv": int
    }
}

**Risposta**: In caso di successo il body è vuoto e la risorsa creata è indicata nell'header `Location`.

**Codici di stato restituiti**:
* 201 Created : in caso di successo.
* 400 Bad Request: in caso di richiesta errata
* 404 Non Found: in caso di utente non trovato
* 409 Conflict: 
    in diversi casi tra cui: 
        Dominio non available
        Acquisto già in corso
        Anni scadenza maggiori di 10
        Formato data/intestatario errato
     

### PUT `/{id}/{domain}/renew`

**Descrizione**: Aumenta il numero anni, nella quantità fornita dall'utente fino a un massimo di 10, alla data di scadenza del dominio.

**Parametri**: 
1. Parametro "PathParam" id di tipo int che indica l'utente che ha effettuato la richiesta.
2. Parametro "PathParam" domain di tipo String che indica il dominio su cui aggiornare la scadenza.
3. Parametro DomainCarta, cioè un wrapper di dominio e carta.

**Header**: nessuno.

**Body richiesta**: 
{
    "domain": {
        "nomeDominio": String,
        "anniScadenzaAggiuntivi": int
    },
    "cartaUtente": {
        "intestatario": String,
        "numeroCarta": String,
        "scadenza": String ("MM-AA"),
        "cvv": int
    }
}

**Risposta**: In caso di successo body vuoto.

**Codici di stato restituiti**:
* 204 No Content : in caso di successo.
* 400 Bad Request: in caso di errori.
* 404 Not Found: 
    in caso:
      non esista il dominio da aggiornare.
      non esista l'utente con id della richiesta.
* 409 Conflict : in caso di anni scandenza maggiori di 10.
