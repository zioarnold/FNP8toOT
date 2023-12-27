# FNP8toOT by MrArni_ZIO
### Description
Tool per export dei documenti fisici tramite classi documentali, oppure dalle cartelle.
Attualmente bisogna passarli un file json strutturato in questo modo:<br>
`{
"sourceCPE": "http://xxx:000/wsi/FNCEWS40MTOM/",
"sourceCPEObjectStore": "XXX",
"sourceCPEUsername": "XXX",
"sourceCPEPassword": "XXX",
"whatToProcess": "Folders",
"documentClass": "Document,CustomObject",
"pathToStore": "D:/FileNetExport/",
"csv": "path\\file_da_esportare.csv",
"phase": "3",
"objectClasses": [
{
"CustomObject": [
"customobjects=true"
],
"Document": [
"documents=true"
],
"Folder": [
"folders=true"
]
}
],
"objectFolder": [
"paths"
]
}`
#### whatToProcess
variabile con la quale potete giocare: riceve uno di questi comandi `DocumentClasses` o `Folders`. Quindi, se
si vuole lavorare con le classi documentali allora passare `DocumentClasses` altrimenti `Folders` che ci pensa lui
ad estrarre i documenti e salvarveli al path indicato nel `pathToStore`.
#### csv
riceve in input un file csv (contenente due colonne: path completo dei file originali da rinominare, il nome del file rinominato).
#### phase
variabile di comando, gestiti valori: 1, 2 e 3. La `1` avvia la fase 1 cioe' scaricamento dei dati dal CPE. 
La `2` avvia la fase 2, cioe' la fase di rinomina dei file. La `3` avvia entrambi le fasi, uno dientro altro.
