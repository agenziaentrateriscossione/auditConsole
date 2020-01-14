# Gestione documentale DocWay - Modulo AUDIT

## Modulo per la registrazione delle azioni su DocWay

#### Servizio attraverso il quale tutte le azioni svolte da operatori o processi sull'applicazione DocWay vengono tracciate e registrate all'interno di uno specifico database su MongoDB.
___

Il modulo di Audit traccia tutte le azioni svolte su DocWay (e ACL) da parte degli utenti registrandole in un apposito archivio mongoDB, autonomo dall'archivio DocWay (o ACL), in modo da garantire l'integrità e l'indipendenza dei dati registrati.
I dati registrati nel modulo di audit (messi a disposizione per la consultazione) garantiscono l'inalterabilità e la non modificabilità delle informazioni stesse e la sicurezza contro manomissioni da parte di terzi.

Nello specifico:
- in caso di modifica di record (documenti XML su eXtraWay), all'interno del record di audit verranno registrate tutte le differenze (calcolate in formato JSON per successivo salvataggio su MongoDB) rispetto alla versione precedente del record;
- la registrazione delle differenze in salvataggio avviene con una procedura a 2 step per avere la certezza di non perdere informazioni di audit (e garantire quindi un tracciamento completo delle modifiche) anche in caso di arresto del server Tomcat:
    * prima del salvataggio del record su eXtraWay, viene prodotto un file di lavoro dell'autit (contenente tutte le informazioni necessarie alla registrazione su MongoDb) e salvato su una specifica directory su file system come "//lavoro in attesa//"
    * se il salvataggio su eXtraWay restituisce errore, il file di lavoro in attesa viene rimosso dal disco
    * se il salvataggio su eXtraWay si completa con successo, il file di lavoro in attesa viene riversato su MongoDB (specifica chiamata di save al client MongoDB) e rimosso dal disco
- in caso di errore riscontrato in salvataggio dell'audit su MongoDB (es. database MongoDB non raggiungibile) il file in attesa viene rinominato come "//fallito//". E' presente uno specifico JOB su DocWay che si occupa di analizzare la directory di lavoro dell'audit e verifica la presenza di file di errore e ritenta il salvataggio su MongoDB.
- la registrazione dell'audit sul salvataggio di record (calcolo delle differenze con la versione precedente) può essere abilitata anche su tutte le applicazioni integrate con DocWay, ad esempio 3diWS o MSA
- in caso di fallimento della registrazione dell'audit (o di altri errori ritenuti gravi) è possibile attivare l'invio di email di notifica ad amministratori di sistema. In questo modo sarà possibile riabilitare il tracciamento nel più breve tempo possibile
- è possibile configurare l'audit in modo da ignorare interventi su interi archivi eXtraWay o specifiche tipologie di record o operazioni
#### Come si compone
All'apertura dell'interfaccia sono immediatamente visibili due sezioni, una più piccola nella quale è possibile inserire i filtri per la ricerca; una più grande che contiene la lista di tutti i record presenti in ordine cronologico.
___
![homeAUDIT](https://user-images.githubusercontent.com/9255029/64541686-97c30500-d322-11e9-990b-3d9c1f2c18f7.png)
___
##### Filtri di ricerca
* Archivio: consente di filtrare i record in base all'archivio eXtraWay di riferimento; lo scenario standard prevede gli archivi acl (modulo ACL) e xdocwaydoc (modulo DocWay). Potrebbero esserci scenari in cui sono presenti altri archivi eXtraWay (cosidetti "periferici") che fanno riferimento a AOO differenti.
* Tipologia di record: questo filtro si attiva una volta selezionato un archivio e contiene l'elenco delle tipologie di record a cui si riferiscono i record di audit. Ad esempio, nel caso venisse selezionato l'archivio xdocwaydoc, il filtro presenta le tipologie doc, fascicolo (e eventualmente raccoglitore).
* ID Record: consente di filtrare i risultati sulla base di uno specifico identificativo; per ID Record si intende il codice identificativo univoco che identifica un record in un archivio eXtraWay (nrecord); tale identificativo viene visualizzato nella sezione Informazioni di servizio presente in ogni record visualizzato nell'applicativo DocWay/ACL. Pertanto questo filtro consente di individuare tutti i record di audit ascrivibili ad uno specifico record di Docway o di ACL.
* Azione: il filtro si attiva una volta selezionato un archivio; consente di filtrare la lista dei risultati in base al tipo di azione registrata nel record di audit. È possibile selezionare una o più azioni in base a cui filtrare i risultati.
* Codice utente: consente di filtrare i risultati di audit in base al codice dell'utente che ha effettuato l'operazione; il codice utente equivale alla matricola dell'utente registrato in ACL.
* Username: consente di filtrare i risultati dei record di audit in base allo username che ha effettuato l'operazione; lo username equivale al login dell'utente registrato in ACL.
* Data e ora inizio: consente di filtrare i risultati dei record di audit impostando un timestamp (data e ora) di inizio a partire dal quale verranno visualizzati i risultati. Il filtro sull'orario si attiva solo se è stata selezionata una data.
* Data e ora fine: consente di filtrare i risultati dei record di audit impostando un timestamp (data e ora) di fine fino a cui verranno visualizzati i risultati. Il filtro sull'orario si attiva solo se è stata selezionata una data.
##### Dati identificativi record
* Azione: tipo di azione effettuata dall'utente;
* Archivio: indicazione dell'archivio eXtraWay su cui è stata effettuata l'azione; può assumere come valori xdocwaydoc se l'azione è stata effettuata sull'archivio documentale (modulo DocWay) o acl se l'azione è stata effettuata sull'archivio delle anagrafiche (modulo ACL);
* Tipo record: tipologia del record eXtraWay su cui è stata effettuata l'azione; per l'archivio acl può assumere i seguenti valori: persona_interna - persona_esterna - struttura_interna - struttura_esterna – ruolo - gruppo - comune - aoo - casellaPostaElettronica ; per l'archivio xdocwaydoc può assumere i valori doc o fascicolo - raccoglitore;
* Id record: identificativo univoco del record eXtraWay su cui è stata effettuata l'azione;
* Utente: username dell'utente che ha effettuato l'azione;
* Data: timestamp (data e ora) in cui è stata eseguita l'azione.
##### Dettaglio record
La pagina di visualizzazione del record di audit riporta le informazioni suddivise in quattro sezioni:
1. Informazioni sull'azione
    * Operazioni effettuate: contiene le indicazioni sull'operazione effettuata
    * Data: contiene le informazioni sulla data e ora in cui è stata effettuata l'operazione
2. Informazioni sull'utente
    * Username: contiene l'indicazione dell'utente che ha effettuato l'operazione
    * Codice utente: contiene la matricola dell'utente che ha effettuato l'operazione
3. Informazioni sul record
    * Archivio: contiene l'indicazione dell'archivio eXtraWay su cui è stata effettuata l'operazione
    * Tipologia di record: tipologia del record eXtraWay su cui è stata effettuata l'azione
    * Id record: identificativo univoco del record eXtraWay su cui è stata effettuata l'azione
    * Link: collegamento diretto al record eXtraWay su cui è stata effettuata l'azione
4. Inserimento o Informazioni Extra
    * QR_Elements
    * ReportFile
    * JreportParams
    * Title: titolo del record eXtraWay su cui è stata effettuata l'azione
    * FileId: Identificativo univoco del record eXtraWay su cui è stata effettuata l'azione
___
![recordAUDIT](https://user-images.githubusercontent.com/9255029/64541689-9abdf580-d322-11e9-9215-1a215c412fcf.png)
___
#### Integrità e autenticità delle informazioni
L'adozione della soluzione proposta all'interno di un sistema di gestione elettronica dei record elettronici come DocWay, garantisce caratteristiche di integrità e autenticità alle informazioni trattate e è conforme alle specifiche MorReq2 in materia di Audit Trail:
* consente la memorizzazione immodificabile di tutte le transazioni che riguardano il sistema di gestione informatico dei documenti;
* consente la conservazione di un audit trail capace di catturare e memorizzazione automaticamente le informazioni riguardanti ogni atto riguardante ciascun documento, ciascuna aggregazione archivistica e lo schema di classificazione, l'utente che agisce e il momento in cui si svolge l'azione;
* assicura che ogni attività non autorizzata possa essere identificata e tracciata;
* assicura che i dati di audit trail siano disponibili per verifiche a richiesta in modo da assicurare che ogni specifico evento sia identificabile e accessibile.

È stata inoltre prestata particolare attenzione alla sicurezza dei dati su MongoDB:
* è stato inserito un hash in maniera che i dati non possano essere alterati nemmeno da un utente amministratore di MongoDB. Questo controllo di integrità può essere effettuato on-demand tramite un apposito pulsante e viene effettuato automaticamente quando un utente della console di Audit effettua una esportazione dei dati (quando si esportano dei dati deve essere garantita di default l'integrità degli stessi);
* è stato realizzato un agent che rimane in ascolto dell'OpLog di MongoDB per individuare eventuali operazioni di cancellazione/modifica documenti(record) di MongoDB (il db di Audit deve consentire solo nuovi inserimenti). In caso in cui vengano individuate manomissioni (che potrebbero avvenire unicamente per mano di un amministratore di MongoDB) vengono immediatamente sollevati degli allarmi via email.
___

> #### Per una visione complessiva del modulo e delle sue dipendenze si rimanda alla pagina [riuso](https://github.com/agenziaentrateriscossione/riuso)
___
### Configurazione

Per attivare il modulo di audit su DocWay (o altre applicazioni integrate con esso, quali 3diWS o MSA) occorre abilitarlo attraverso uno specifico set di properties definite all'interno del file //it.highwaytech.broker.properties//:
```
audit.enabled=false

# Parametri di connessione a MongoDB
#audit.mongodb.uri=mongodb://localhost:27017/dw4audit?safe=true&w=1
audit.mongodb.uri=
#audit.mongodb.dbName=dw4audit
audit.mongodb.dbName=

# Directory all'interno della quale registrare i dati temporanei e gli errori riscontrati sulla registrazione dell'audit. Se non viene specificata alcuna directory, verra'
# creata ed utilizzata una directory 'dw4audit' all'interno della directory dei temporanei.
audit.workDir=

# Regular Expression da utilizzare per identificare eventuali seriali presenti all'interno dell'XML aggiornato dall'utente e che saranno poi assegnati tramite eXtraWay. Nei
# casi previsti dalla regex il documento XML verra' ricaricato dopo il salvataggio su eXtraWay in modo da registrare il seriale asssegnato all'interno dell'archivio
# di audit. Se non viene specificata alcuna regex si disabilitera' questo controllo
audit.serial.regex=(num_prot|numero)[ ]{0,1}=[ ]{0,1}\"\\S*-\\.\"

# Definizione di xpath conosciuti per i quali e' possibile definire un set di attributi (o sottoelementi testo) che definiscono la chiave del nodo all'interno della
# ripetizione. In questo modo le differenze possono essere valutate sul singolo nodo di elementi ripetibili e non sull'intera lista. Tutti i classici xpath ripetibili
# di docway e acl sono gia' gestiti a livello di codice (occorre definire eventuali xpath ripetibili di campi custom).
# Il formato di definizione e' il seguente:
# audit.repeatableXPath.know.N=XPATH_DOT_NOTATION|KEY_VALUE[,KEY_VALUE]
# dove:
# - N rappresenta il numero progressivo di definizione del path
# - XPATH_DOT_NOTATION rappresenta l'xpath con separatore il punto (/doc/rif_esterni/rif -> doc.rif_esterni.rif)
# - KEY_VALUE corrisponde ad un attributo o un sottoelemento di tipo test del nodo. E' possibile specificare piu' valori separandoli con la virgola
# - la definizione del path deve essere separata dalle chiavi tramite pipe
# Esempio (in realta' gli xpath seguenti sono gia' gestiti di default):
# audit.repeatableXPath.know.1=doc.postit|@cod_operatore,@data,@ora
# audit.repeatableXPath.know.2=doc.link_interno|@href
# audit.repeatableXPath.know.3=fascicolo.link_interno|@href

# Tempo (in minuti) di sleep del thread di che si occupa di ritentare il salvataggio dell'audit su MongoDB in caso di precedenti errori (default = 1 min)
audit.errorsJob.sleep=1

# Configurazione della casella per l'invio di email di notifica
# host
#audit.email_host=localhost
# porta
#audit.email_port=2525
# credenziali di accesso
#audit.email_username=
#audit.email_password=
# protocollo di invio
#audit.email_protocol=smtp
# indirizzo email utilizzato per l'invio
#audit.email_from_address=notifier@audit.3di
# nome alternativo dell'indirizzo email di invio
#audit.email_from_nickname=Audit Notifier
# indirizzo email al quale inviare le notifiche
#audit.email_to_address=admin@audit.3di

# Eventuale elenco di tipologie di record da ignorare su uno specifico archivio. Il formato di definizione e' il seguente:
# audit.pne.ignore.N=DB_NAME|ROOT_NAME[,ROOT_NAME]
# dove:
# - N rappresenta il numero progressivo
# - DB_NAME corrisponde all'archivio sul quale applicare le esclusioni di tipologie di record
# - ROOT_NAME corrisponde al nome dell'elemento radice dei record da non registrare in audit. E' possibile specificare piu' valori separandoli con la virgola
# - la definizione del nome db deve essere separata dagli xpath tramite pipe
# Esempio:
# audit.pne.ignore.1=acl|comune

# Elenco di archivi che devono essere ignorati dalla procedura di audit (nessun intervento sugli archivi specificati verra' tracciato)
audit.dbNames.ignore=

# Elenco di azioni che devono essere ignorate dalla procedura di audit
audit.actions.ignore=

# Elenco di diritti (codici separati da virgola) per i quali deve essere mappata una specifica azione in fase di audit delle attivita' degli utenti. Verranno analizzati
# tutti gli interventi svolti su diritti e identificate le variazioni su diritti speciali o diritti di amministrazione
audit.acl.rights.dirittiSpeciali=ACL-16,ACL-30,ACL-DL01,ACL-SP01,ACL-AU01
# Per quanto riguarda i diritti di amministrazione, sono stati inseriti i codici degli applicativi classici, vanno aggiunti tutti i casi specifici dell'installazione
# presso il cliente
audit.acl.rights.amministrazione=ACL-25,ACL-24-ACL,ACL-24-ACLCRAWLER,ACL-24-DW,ACL-24-TO,ACL-24-SOGINSAP
```
  * //audit.enabled// deve essere settato per abilitare l'audit sull'applicazione. Con l'attivazione occorre sicuramente settare anche le properties di connessione al database MongoDB
  * //audit.workDir// corrisponde al percorso assoluto alla directory sulla quale verranno salvati i file temporanei di elaborazione dell'audit (prima di essere riversati su MongoDB)
___
### Accesso da DocWay
L'accesso alla console di Audit deve essere abilitato per gli operatori di DocWay, in base ad uno specifico diritto definito in ACL.

Il link di accesso alla console è visibile dal menù in altro di DocWay, sezione '//ALTRE FUNZIONI//' >> '//Console di Audit//'.

Per poter abilitare il link occorre configurare le necessarie properties sul file //it.highwaytech.apps.generic.properties//:
```
# Abilita il link di accesso alla console di audit dall'applicativo docway ('si', 'no' - Default = 'no')
abilitaConsoleAudit=si

# Eventuale URL di accesso alla console di audit dall'applicativo docway (se non specificato non verra' visualizzato il link per l'accesso alla
# console di audit direttamente da docway)
auditConsole.url=http://HOST[:PORT]/auditConsole/login
```
Oltre alle properties occorre verificare la presenza del diritto di accesso alla console sul file dei diritti //acl.xml//:
```
...
<group label="Diritti speciali">
  ...
  <right cod="ACL-AU01" label="Accesso alla console di Audit applicativo"></right>
</group>
...
```
**N.B.**: Se il file è stato personalizzato per la specifica installazione, occorre aggiungere la porzione XML relativa al diritto al file acl.xml presente all'interno della directory del configuratore (es. '///opt/3di.it/confDocWay4-service/base/acl/diritti//')
___
#### Status del progetto:

- stabile

#### Limitazioni sull'utilizzo del progetto:

Il presente modulo della piattaforma documentale è stato realizzato facendo uso di MongoDB.

Visto che la registrazione temporanea dei dati di audit viene fatta su una specifica directory su file system, **è importante tenere conto della velocità di scrittura su questa directory per mantenere delle performance accettabili**.
___
#### Detentore del copyright:
Agenzia delle Entrate-Riscossione (ADER)
___
#### Soggetto incaricato del mantenimento del progetto open source:
| 3D Informatica srl |
| :------------------- |
| Via Speranza, 35 - 40068 S. Lazzaro di Savena |
| Tel. 051.450844 - Fax 051.451942 |
| http://www.3di.it |
___
#### Indirizzo e-mail a cui inviare segnalazioni di sicurezza:
tickets@3di.it
