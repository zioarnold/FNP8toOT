package r2u.tools.worker;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.core.*;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import r2u.tools.config.Configurator;
import r2u.tools.entities.FNCustomObject;
import r2u.tools.entities.FNDocument;
import r2u.tools.entities.FNFolder;
import r2u.tools.utils.DataFetcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

    private void fileFolderLookup(String pathToStore, Folder folderInstance) {
        //se la cartella contiene sotto cartelle vedo per la cartella corrente se ci sono i file da scaricare,
        //scarico i file nella cartella corrente e poi procedo col altra in modo recursivo
        //spero che funzioni cosi`:-D
        if (!folderInstance.get_SubFolders().isEmpty()) {
            Iterator<?> iterator = folderInstance.get_SubFolders().iterator();
            while (iterator.hasNext()) {
                Folder currentFolder = (Folder) iterator.next();
                logger.info("Working on: " + currentFolder.get_PathName());
                if (!currentFolder.get_ContainedDocuments().isEmpty()) {
                    storeFile(pathToStore, currentFolder);
                }
                fileFolderLookup(pathToStore, currentFolder);
            }
        } else {
            if (!folderInstance.get_ContainedDocuments().isEmpty()) {
                //se la cartella contiene i file
                storeFile(pathToStore, folderInstance);
            }
        }
    }

    private void storeFile(String pathToStore, Folder currentFolder) {
        DocumentSet containedDocuments = currentFolder.get_ContainedDocuments();
        Iterator<?> containedDocIterator = containedDocuments.iterator();
        while (containedDocIterator.hasNext()) {
            Document document = (Document) containedDocIterator.next();
            ContentElementList contentElements = document.get_ContentElements();
            if (!contentElements.isEmpty()) {
                for (Object docContentElement : contentElements) {
                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                    InputStream inputStream = contentTransfer.accessContentStream();
                    logger.info("Trying to save file under path: " + pathToStore + currentFolder.get_PathName() + "/" + document.get_Name());
                    try {
                        FileUtils.copyInputStreamToFile(inputStream, new File(pathToStore + currentFolder.get_PathName() + "/" + document.get_Name()));
                    } catch (IOException e) {
                        logger.error("Unable to save file.", e);
                    }
                }
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
