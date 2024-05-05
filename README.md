# FNP8toOT ver1.2 by MrArni_ZIO - Released
### Description
Tool per scaricare dei documenti popolati su FileNet ovvero nel FileSystem, per poi lavorarli. 
Vedi `phase` per il funzionamento.
### ObjectFolder
sostituire `/somepath/` con il path reale, che si trova aprendo l'ACCE poi aprendo object store e navigando sul panello sinistro.
### CustomObject/Document/Folder
Riempire con elenco delle classi documentali in questo modo: `myDocumentClass=true`, il `true/false` è un flag di controllo, 
cosicché il tool capisce se bisogna sudare o no.
#### whatToProcess
Variabile con la quale potete giocare: riceve uno di questi comandi `DocumentClasses` o `Folders`. Quindi, se
si vuole lavorare con le classi documentali allora passare `DocumentClasses` altrimenti `Folders` che ci pensa lui
a estrarre i documenti e salvarveli al path indicato nel `pathToStore`+(lista dei)`objectFolder`.<br>Scempio:<br>
`file_exported/somepath/`<br>`file_exported/somepath1/`
#### regex
Introdotta a causa della presenza dei caratteri speciali nei AbsolutePath. Regex introdotta li sega via.
#### csv
Riceve in ingresso un file csv (contenente due colonne: path completo dei file originali da rinominare, il nome del file rinominato).
#### phase
Variabile di comando, gestiti valori: 1, 2, 3 e All. La `1` avvia la fase 1 cioè scaricamento dei dati dal CPE. 
La `2` avvia la fase 2, cioè la fase di rinomina dei file. La `3` elimina i file che non sono presenti nel file csv.
La `All` avvia tutte le fasi. Le fasi `2` e `3` non hanno bisogno di connessione al filenet. Mentre nelle fasi `1` e `all`
e' necessario avere la connessione stabile col FileNet (CPE).
#### _Usage_
`java -jar path\filename.jar path\config.json`.<br>
Per qualsiasi bug, feature request - non esitate a chiamarmi, messagarmi.
