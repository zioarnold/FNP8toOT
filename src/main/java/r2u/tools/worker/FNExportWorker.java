package r2u.tools.worker;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.core.*;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.util.Id;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import r2u.tools.config.Configurator;
import r2u.tools.entities.FNCustomObject;
import r2u.tools.entities.FNDocument;
import r2u.tools.entities.FNFolder;
import r2u.tools.utils.DataFetcher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FNExportWorker {
    private final Configurator instance = Configurator.getInstance();
    private static final Logger logger = Logger.getLogger(FNExportWorker.class.getName());
    FNCustomObject fnCustomObject;
    FNDocument fnDocument;
    FNFolder fnFolder;

    public FNExportWorker() {
        fnCustomObject = new FNCustomObject(instance.getPathToStore());
        fnDocument = new FNDocument(instance.getPathToStore());
        fnFolder = new FNFolder(instance.getPathToStore());
    }

    //Viene invocato qualora "whatToProcess" e` impostato a "DocumentClasses".
    public void extractByObjectClass(String docClass) {
        if (docClass.equalsIgnoreCase("Document")) {
            extractMetadataDocument(docClass, instance.getObjectStore(), instance.getDocumentClassMap());
        }
        if (docClass.equalsIgnoreCase("CustomObject")) {
            extractMetadataCustomObject(docClass, instance.getObjectStore(), instance.getCustomObjectMap());
        }
        if (docClass.equalsIgnoreCase("Folder")) {
            extractMetadataFolder(docClass, instance.getObjectStore(), instance.getFolderMap());
        }
    }

    public void processFolders(ObjectStore objectStoreSource, List<Object> folderList) {
        for (Object folder : folderList) {
            Folder folderInstance = Factory.Folder.fetchInstance(objectStoreSource, (String) folder, null);
            fileFolderLookup(instance.getPathToStore(), folderInstance);
        }
    }

    private void fileFolderLookup(String pathToStore, Folder currentFolder) {
        //verifico se la cartella corrente contiene delle sottocartelle
        logger.info("Looking if current folder: '" + currentFolder.get_FolderName() + "' contains subfolders");
        if (!currentFolder.get_SubFolders().isEmpty()) {
            logger.info("It does so...");
            Iterator<?> iterator = currentFolder.get_SubFolders().iterator();
            while (iterator.hasNext()) {
                Folder subFolder = (Folder) iterator.next();
                logger.info("Working on subfolder: " + subFolder.get_PathName());
                //Se si, allora le scorro una x una e verifico in presenza dei file
                if (!subFolder.get_ContainedDocuments().isEmpty()) {
                    //Se ci sono dei file allora li scarico
                    checkFiles(pathToStore, subFolder);
                }
                fileFolderLookup(pathToStore, subFolder);
            }
        } else {
            //Se non ci sono delle sottocartelle allora verifico se ci sono dei file in cartella in cui mi trovo x scaricarli se ci sono
            logger.info("It doesn't so...");
            if (!currentFolder.get_ContainedDocuments().isEmpty()) {
                //se la cartella contiene i file
                checkFiles(pathToStore, currentFolder);
            }
        }
    }

    private void checkFiles(String pathToStore, Folder currentFolder) {
        DocumentSet containedDocuments = currentFolder.get_ContainedDocuments();
        Iterator<?> containedDocIterator = containedDocuments.iterator();
        Id id = null, currentId;
        int idx = 0;
        //Sapendo che i file sono referenziati per GUID
        //Al primo passaggio mi assegno GUID e salvo il file
        //Al successivo controllo che siano diversi gli GUID
        //Se sono diversi allora salvo, altrimenti - nah.
        while (containedDocIterator.hasNext()) {
            Document document = (Document) containedDocIterator.next();
            ContentElementList contentElements = document.get_ContentElements();
            if (idx == 0) {
                id = document.getProperties().getIdValue("ID");
                storeFiles(contentElements, document, pathToStore, currentFolder);
            }
            if (idx > 0) {
                currentId = document.getProperties().getIdValue("ID");
                if (!id.equals(currentId)) {
                    storeFiles(contentElements, document, pathToStore, currentFolder);
                }
                id = currentId;
            }
            idx++;
        }
    }

    private void storeFiles(ContentElementList contentElements, Document document, String pathToStore, Folder currentFolder) {
        String openBracket = "[", closingBracket = "]";
        String fileName = pathToStore + currentFolder.get_PathName() + "/" + document.get_Name(),
                jsonFileName = pathToStore + currentFolder.get_PathName() + "/" + document.get_Name() + ".json";
        if (!contentElements.isEmpty()) {
            for (Object docContentElement : contentElements) {
                ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                InputStream inputStream = contentTransfer.accessContentStream();
                logger.info("Trying to save file under path: " + fileName);
                try {
                    FileUtils.copyInputStreamToFile(inputStream, new File(fileName));
                } catch (IOException e) {
                    logger.error("Unable to save file.", e);
                }
                if (instance.isMakeJson()) {
                    logger.info("Building json metadata for file: " + document.get_Name());
                    JSONObject json = new JSONObject();
                    json.put("SistemaDiRiferimento", document.getProperties().getStringValue("SistemaDiRiferimento"));
                    json.put("IdDocumento", document.getProperties().getStringValue("IdDocumento"));
                    json.put("Provincia", document.getProperties().getStringValue("Provincia"));
                    json.put("Descrizione", document.getProperties().getStringValue("Descrizione"));
                    json.put("Comune", document.getProperties().getStringValue("Comune"));
                    json.put("CodiceImmobile", document.getProperties().getStringValue("CodiceImmobile"));
                    json.put("Commentoversione", document.getProperties().getStringValue("Commentoversione"));
                    json.put("Cestinato", document.getProperties().getBooleanValue("Cestinato"));
                    json.put("CodiceSito", document.getProperties().getStringValue("CodiceSito"));
                    json.put("DataDocumento", document.getProperties().getDateTimeValue("DataDocumento"));
                    json.put("TipoProprieta", document.getProperties().getStringValue("TipoProprieta"));
                    json.put("DataRinnovo", document.getProperties().getDateTimeValue("DataRinnovo"));
                    json.put("Note", document.getProperties().getStringValue("Note"));
                    json.put("Regione", document.getProperties().getStringValue("Regione"));
                    json.put("CodiceFaldone", document.getProperties().getStringValue("CodiceFaldone"));
                    json.put("CAP", document.getProperties().getStringValue("CAP"));
                    json.put("Indirizzo", document.getProperties().getStringValue("Indirizzo"));
                    json.put("Nazione", document.getProperties().getStringValue("Nazione"));
                    json.put("IdDocumentoPrincipale", document.getProperties().getStringValue("IdDocumentoPrincipale"));
                    json.put("NumeroVersione", document.getProperties().getInteger32Value("NumeroVersione"));
                    json.put("CodiceStanza", document.getProperties().getStringValue("CodiceStanza"));
                    json.put("CodicePiano", document.getProperties().getStringValue("CodicePiano"));
                    json.put("CodiceEdificio", document.getProperties().getStringValue("CodiceEdificio"));
                    json.put("Cancellato", document.getProperties().getBooleanValue("Cancellato"));
                    try {
                        BufferedWriter createFile = new BufferedWriter(new FileWriter(jsonFileName, true));
                        createFile.write(openBracket + json + closingBracket);
                        createFile.close();
                    } catch (IOException e) {
                        logger.error("Unable to save file.", e);
                    }
                }
            }
        }
        //Normalizzazione del JSON.
        if (instance.isMakeJson()) {
            try {
                Path path = Paths.get(jsonFileName);
                logger.info("Normalizing JSON file: " + jsonFileName);
                String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                Pattern pattern = Pattern.compile("]\\[");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    content = content.replace("][", ",");
                }
                BufferedWriter createFile = new BufferedWriter(new FileWriter(jsonFileName, false));
                createFile.write(content);
                createFile.close();
            } catch (IOException e) {
                logger.error("Unable to read file.", e);
            }
        }
    }

    //TODO: Da capire se va usato
    // Forse verrà usato negli futuri sviluppi
    private void extractMetadataDocument(String docClass, ObjectStore objectStoreSource, HashMap<String, Boolean> documentClassMap) {
        Iterator<?> iterator = DataFetcher.fetchRows(docClass, objectStoreSource);
        while (iterator.hasNext()) {
            RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
            try {
                Properties properties = repositoryRow.getProperties();
                String id = properties.getIdValue("ID").toString();
                Document documentSource = Factory.Document.fetchInstance(objectStoreSource, id, null);
                switch (documentSource.getClassName()) {
                    case "DRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDREDocument(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "AllegatoFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractAllFADO(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "AllegatoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractAllPatrimonial(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "DocumentiNecessariPerCategoriaFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDocumentsRequiredForFADOCategory(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "DocumentoArchibus":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractArchibusDocument(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "DocumentoFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDocumentFADO(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "DocumentoFADOVersioning":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractFADODocumentVersioning(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "DocumentoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDocPatrimonial(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "FADOMappingCategorie":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractFADOCategoryMapping(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "FaldoniListHolder":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractFolderListHolder(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDREReport(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportAllegatoX":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractAllXReport(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportCaricamentoMassivoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractReportMassUploadPatrimonial(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportDocumentiDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDREDocumentReport(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportDocumentiPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractReportDocPatrimonial(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportGruppiLottiCategorie":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractBatchesGroupCategoryReport(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ReportImmobiliDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            fnDocument.extractDREReportImmovables(objectStoreSource, documentSource, id);
                        }
                        break;
                }
            } catch (Exception exception) {
                logger.error("Something went wrong", exception);
            }
        }
    }

    //TODO: Da capire se va usato
    // Forse verrà usato negli futuri sviluppi
    private void extractMetadataCustomObject(String docClass, ObjectStore objectStoreSource, HashMap<String, Boolean> customObjectMap) {
        Iterator<?> iterator = DataFetcher.fetchRows(docClass, objectStoreSource);
        while (iterator.hasNext()) {
            RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
            try {
                Properties properties = repositoryRow.getProperties();
                String id = properties.getIdValue("ID").toString();
                CustomObject documentSource = Factory.CustomObject.fetchInstance(objectStoreSource, id, null);
                switch (documentSource.getClassName()) {
                    case "ImmobileFaldoneSecProxyObject":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            fnCustomObject.extractImmovableBatchSecProxyObj(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "LottoSecProxyObject":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            fnCustomObject.extractSecProxyObjLot(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ProgressiveNumberDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            fnCustomObject.extractProgressiveNumDispenser(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "FaldoneDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            fnCustomObject.extractFlapDispenser(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "LottoDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            fnCustomObject.extractLotDispenser(objectStoreSource, documentSource, id);
                        }
                        break;
                }
            } catch (Exception exception) {
                logger.error("Something went wrong", exception);
            }
        }
    }

    //TODO: Da capire se va usato
    // Forse verrà usato negli futuri sviluppi
    private void extractMetadataFolder(String docClass, ObjectStore objectStoreSource, HashMap<String, Boolean> folderMap) {
        Iterator<?> iterator = DataFetcher.fetchRows(docClass, objectStoreSource);
        while (iterator.hasNext()) {
            RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
            try {
                Properties properties = repositoryRow.getProperties();
                String id = properties.getIdValue("ID").toString();
                Folder documentSource = Factory.Folder.fetchInstance(objectStoreSource, id, null);
                switch (documentSource.getClassName()) {
                    case "DREFolder":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.dreFolder(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "CartellaContatori":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.counterFolders(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "Categoria":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.categoryFolder(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "CategoriaFado":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.fadoCategory(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "CategoriaPatrimoniale":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.categoryPatrimonial(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "Faldone":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.faldon(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "Immobile":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.immovable(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "ImmobilePatrimoniale":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.immovablePatrimonial(objectStoreSource, documentSource, id);
                        }
                        break;
                    case "LottoImmobili":
                        if (folderMap.get(documentSource.getClassName())) {
                            fnFolder.lottoImmovable(objectStoreSource, documentSource, id);
                        }
                        break;
                }
            } catch (Exception exception) {
                logger.error("Something went wrong", exception);
            }
        }
    }
}
