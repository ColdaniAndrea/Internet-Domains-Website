# Progetto Sistemi Distribuiti 2023-2024

**Nome progetto: DOMINI_GIUSTINI**

Il progetto prevede la realizzazione di un sistema distribuito per la gestione di domini Internet, composto da tre componenti principali:

1. **Client Web**: Offre un'interfaccia per gli utenti, permettendo loro di acquistare nuovi domini e gestire quelli già posseduti (rinnovo, visualizzazione degli ordini, ecc.).
2. **Server Web**: Gestisce la logica di business, coordinando le operazioni di acquisto e gestione dei domini tramite API REST.
3. **Database Documentale**: Archivia tutte le informazioni sui domini, sugli acquisti e sugli utenti (lavorando con file JSON), comunicando con il server web attraverso un protocollo personalizzato su socket TCP.

## Componenti del Gruppo

* **Andrea Coldani** (xxxxxx)
* **Fabio Colonetti** (xxxxxx)

## Compilazione ed Esecuzione

Il server Web e il database sono progetti Java che utilizzano Maven per gestire le dipendenze, la compilazione e l'esecuzione.
In particolare l'esecuzione delle applicazioni deve avvenire nel seguente modo:

1) ### Database

Aprire un terminale e dalla cartella principale spostarsi nella cartella "database" ed eseguire con Maven:  
 `cd database`  `mvn exec:java`

Il database è una semplice applicazione Java.
Si pone in ascolto all'indirizzo `localhost` alla porta `3030`.

2) ### Server

Aprire un terminale e dalla cartella principale spostarsi nella cartella "server-web" ed eseguire con Maven:  
`cd server-web`  `mvn jetty:run`

Il server Web utilizza Jetty e Jersey.
Espone le API REST all'indirizzo `localhost` alla porta `8080`.

3) ### Client Web

Per avviare il client Web è necessario utilizzare l'estensione "Live Preview" su Visual Studio Code: show preview -> open on browser

**Attenzione**: è inoltre necessario configurare CORS in Google Chrome .


