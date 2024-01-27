# FNP8toOT by MrArni_ZIO
### Description
Tool per export dei documenti fisici tramite classi documentali, oppure dalle cartelle.
Attualmente bisogna passarli un file json strutturato in questo modo:<br>
`{
  "sourceCPE": "http://xxxyyyzzz:000/wsi/FNCEWS40MTOM/",
  "sourceCPEObjectStore": "ObjectStoreName",
  "sourceCPEUsername": "xxx",
  "sourceCPEPassword": "yyy",
  "jaasStanzaName": "FileNetP8WSI",
  "whatToProcess": "Folders",
  "documentClass": "Document,CustomObject",
  "pathToStore": "file_exported/",
  "csv": "file_to_export.csv",
  "phase": "3",
  "fileLogPath": "logs/",
  "regex": "[&#,+()$~%!„'\"*‚^¤?<>|@ª{«»§}©®™ìùéòèàÙÒÌÉÈÀ√]",
  "objectFolder": [
    "/somepath/"
  ],
  "objectClasses": [
    {
      "CustomObject": [
      ],
      "Document": [
      ],
      "Folder": [
      ]
    }
  ]
}`
#### whatToProcess
variabile con la quale potete giocare: riceve uno di questi comandi `DocumentClasses` o `Folders`. Quindi, se
si vuole lavorare con le classi documentali allora passare `DocumentClasses` altrimenti `Folders` che ci pensa lui
ad estrarre i documenti e salvarveli al path indicato nel `pathToStore`.
#### regex
introdotta a causa della presenza dei caratteri speciali nei AbsolutePath. Regex introdotta li sega via.
#### csv
riceve in input un file csv (contenente due colonne: path completo dei file originali da rinominare, il nome del file rinominato).
#### phase
variabile di comando, gestiti valori: 1, 2, 3 e All. La `1` avvia la fase 1 cioe' scaricamento dei dati dal CPE. 
La `2` avvia la fase 2, cioe' la fase di rinomina dei file. La `3` elimina i file che non sono presenti nel file csv.
La `All` avvia tutte le fasi. Le fasi `2` e `3` non hanno bisogno di connessione al filenet. mentre nelle fasi `1` e `all`
e' necessario avere la connessione stabile col FileNet (CPE).
#### _Usage_
`java -jar path\filename.jar path\config.json`.<br>
Per qualsiasi bug, feature request - non esitate a chiamarmi, messagarmi.
