# Progetto Sistemi Distribuiti 2023-2024 - TCP

## Protocollo Testuale

### Formato delle Richieste

1. **IS_AVAILABLE**
   - **Descrizione**: Controlla se un dominio è disponibile.
   - **Formato**: `IS_AVAILABLE'nomeDominio'`
   - **Esempio**: `IS_AVAILABLE'Amazon.com'`

2. **REGISTER**
   - **Descrizione**: Registra un dominio con i dettagli forniti.
   - **Formato**: `REGISTER'nomeDominio$anniDurata$idUtente$prezzo$numeroCartaDiCredito$scadenza$cvv$intestatarioCarta'`
   - **Esempio**: `REGISTER'Amazon.com$2$8647$20$7624504525140789$10/27$627$Luca Rossi'`

3. **RETURN_DOMAINS**
   - **Descrizione**: Restituisce i domini di un utente.
   - **Formato**: `RETURN_DOMAINS'idUtente'`
   - **Esempio**: `RETURN_DOMAINS'8647'`

4. **RETURN_ORDERS**
   - **Descrizione**: Restituisce gli ordini di un utente.
   - **Formato**: `RETURN_ORDERS'idUtente'`
   - **Esempio**: `RETURN_ORDERS'8647'`

5. **RENEW**
   - **Descrizione**: Rinnova un dominio.
   - **Formato**: `RENEW'nomeDominio$anniDurata$prezzo$numeroCartaDiCredito$scadenza$cvv$intestatarioCarta'`
   - **Esempio**: `RENEW'Amazon.com$5$30$564052349216783$20/27$738$Luca Rossi'`

6. **NEW_USER**
   - **Descrizione**: Crea un nuovo utente.
   - **Formato**: `NEW_USER'nome$cognome$email$idUtente'`
   - **Esempio**: `NEW_USER'Luca$Rossi$l.rossi@campus.unimib.it$8645'`

7. **LAST_ID**
   - **Descrizione**: Restituisce l'ultimo ID usato per un utente.
   - **Formato**: `LAST_ID`
   - **Esempio**: `LAST_ID`

8. **IS_USER**
   - **Descrizione**: Controlla se l'utente associato all'ID esiste.
   - **Formato**: `IS_USER'idUtente'`
   - **Esempio**: `IS_USER'1'`

### Formato delle Risposte

1. **IS_AVAILABLE**
   - **Risposta**: `"AVAILABLE"` se disponibile, JSONObject con nome, cognome ed email se il dominio è occupato, `"ERROR"` se c'è qualche errore.

2. **REGISTER**
   - **Risposta**: `"SUCCESS"` se viene registrato, `"UNSUCCESS"` se c'è un errore nella registrazione, `"NO_USER"` se non c'è un utente registrato associato all'ID.

3. **RETURN_DOMAINS**
   - **Risposta**: JSONArray contenente i domini di un utente se non si verificano problemi, `"NOT_FOUND"` se c'è un errore.

4. **RETURN_ORDERS**
   - **Risposta**: JSONArray contenente gli ordini di un utente se non si verificano problemi, `"NOT_FOUND"` se c'è un errore.

5. **RENEW**
   - **Risposta**: `"SUCCESS"` se viene rinnovato, `"UNSUCCESS"` se c'è un errore nel rinnovo, `"NO_USER"` se non c'è un dominio (non scaduto) da rinnovare, `"CANNOT_RENEW"` se si vuole rinnovare per più di 10 anni.

6. **NEW_USER**
   - **Risposta**: `"CREATED"` se l'utente è stato creato con successo, `"NOT_CREATED"` se c'è stato un errore nella creazione dell'utente, `"ALREADY_EXISTS"` se l'utente esiste già.

7. **LAST_ID**
   - **Risposta**: Restituisce l'ultimo ID usato per un utente, `"ERROR"` se c'è qualche errore.

8. **IS_USER**
   - **Risposta**: `"EXISTS"` se l'utente associato all'ID esiste, `"NOT_EXISTS"` se non esiste.