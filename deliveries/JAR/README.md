# Eseguire i JAR
## Jar del server

```bash
java -jar Server.jar [RMI Port] [TCP Port]
```
`RMI Port` e `TCP Port` sono opzionali, se non specificate assumono rispettivamente i valori `2222` e `9999`.

## Jar della TUI

```bash
java -jar TUI.jar
```
Per eseguire la TUI e' consigliato utilizzare un nerd font.

## Jar della GUI
```bash
java -jar GUI.jar
```

# Compilare i JAR
1. (Opzionale, senza la GUI non funziona) Scaricare la cartella risorse da [qui](https://drive.google.com/file/d/15CvIqbfmjXKmvkwzr2-L_PMIKgQnIvA3/view?usp=sharing).
2. Estrarre le immagini nella cartella `src/main/resources/images`
3. Eseguire il comando:

```bash
./mvnw clean package
```

I file risultanti si troveranno nella cartella target.