# Peer-Review 2: Sequence diagram
Gruppo AM08: Francesco Caracciolo, Antonino Ciancimino, Gianluca Di Paola, Davide Greco

Valutazione del sequence diagram del gruppo AM17.

## Lati negativi

### Considerazioni generali

### Your turn sequence diagram
- Se la comunicazione da `MatchController` a `ClientController` è unicast, non è necessario alcun parametro nei metodi di risposta (ad esempio `confirmPlacedCard(...)`).

### Game setup sequence diagram
- Se la comunicazione da `MatchController` a `ClientController` è unicast, non è necessario mandare lo `starterCardID` nel metodo `selectedStarterSide(...)`. La presenza di parametri superflui è presente anche in `confirmStartedSide(...)`.
- Col metodo `sendInitializedVisibleDecks(...)` non vengono inviate informazioni sulla carta in cima a ciascun mazzo (si noti che servirebbe solo il seme del retro della carta).
- Osserviamo che, al fine di evitare ritardi di rete, potrebbe convenire accorpare le cinque comunicazioni (`sendOpponetsStarter(...)`, `sendToken(...)`, `sendHand(...)`, `sendCommonObjectives(...)`, `sendSecretObjectives(...)`) in una sola. Ciò risulta utile per le connessioni RMI nel caso in cui il server invochi metodi remoti dei client *non parallelamente* (così come avviene nativamente nel protocollo RMI). 

### Start socket connection sequence diagram
- Successivamente all'analisi di `SocketController`, sembra che esso sia più un *Handler* che un *Controller* vero e proprio. Un elemento *Controller* è strettamente connesso alla corrispettiva *View*, cosa che nel caso di `SocketController` non avviene. È meramente un fatto di nomenclatura.
- Abbiamo dei dubbi che non siamo riusciti a chiarire su questo sequence diagram:
  - Perché in questo documento sparisce l'entità `ServerDecoder/Encoder`? È solo una rappresentazione grafica di comodo per altri oggetti? 
  - Perché la comunicazione di rete è gestita sia da `SocketController` che da `GameController` (entrambi possono e comunicano con `ClientDecoder/Encoder`)?
- In diversi momenti della comunicazione, l'identificazione dei match avviene tramite `matchID` e `matchesNicknames` in modo apparentemente arbitrario. Pensiamo si tratti di un refuso; se così non fosse, non ci è ben chiaro come avviene l'identificazione *univoca* dei match.

### Start RMI connection sequence diagram
- Ci sono degli aspetti poco chiari:
  - Perché in questo documento sparisce l'entità `ServerDecoder/Encoder`? È solo una rappresentazione grafica di comodo per altri oggetti?
  - Perché la comunicazione di rete è gestita sia da `RMIreceiver` che da `GameController` (entrambi possono e comunicano con `ClientDecoder/Encoder`)?
- Rispetto a come avviene nello *Start socket connection sequence diagram*, a seguito della chiamata al metodo `startRMI(...)`, `ServerMain` non manda a `RMIreceiver` un'istanza di `GameController`.
- Non viene mai inviato al `CientController` un oggetto remoto attraverso cui comunicare col server: in tal caso la comunicazione RMI da client a server non può avvenre.

## Lati positivi generici
- La scelta del lato della carta iniziale viene effettuato in modo asincrono tra tutti i giocatori, evitando possibili delay della rete.
- La scelta degli obiettivi segreti viene effettuata in modo asincrono tra tutti i giocatori, evitando che ogni giocatore debba necessariamente aspettare ogni altro player prima di effettuare la sua mossa.
- La parte di rete RMI e TCP mantengono una struttura omogenea; ciò permette di utilizzare le funzionalità di rete indipendentemente dalla specifica implementazione.

## Confronto tra le architetture
- Nella vostra architettura, quando un giocatore entra in un match ottiene subito la lista di tutti gli altri giocatori già nel match.
- Nella nostra architettura di gioco, l'azione di scelta della carta iniziale e degli obiettivi è permessa ai giocatori in maniera sequenziale (uno per volta).
- Noi abbiamo adottato una distinzione più netta tra RMI e TCP, implementando ciascun controller in modo specifico (classe astratta `PlayerController`, con due sottoclassi concrete `PlayerControllerTCP` e `PlayerControllerRMI`).
