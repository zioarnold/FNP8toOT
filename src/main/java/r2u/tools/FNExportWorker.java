package r2u.tools;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.core.*;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

@SuppressWarnings({"SpellCheckingInspection", "DuplicateBranchesInSwitch", "DuplicatedCode"})
public class FNExportWorker {
    private String path;
    Logger logger = null;

    public void process(String docClass,
                        ObjectStore objectStoreSource,
                        HashMap<String, Boolean> customObjectMap,
                        HashMap<String, Boolean> documentClassMap,
                        HashMap<String, Boolean> folderMap,
                        String pathToStore,
                        Logger logger) {
        this.path = pathToStore;
        this.logger = logger;
        if (docClass.equalsIgnoreCase("Document")) {
            extractMetadataDocument(docClass, objectStoreSource, documentClassMap);
        }
        if (docClass.equalsIgnoreCase("CustomObject")) {
            extractMetadataCustomObject(docClass, objectStoreSource, customObjectMap);
        }
        if (docClass.equalsIgnoreCase("Folder")) {
            extractMetadataFolder(docClass, objectStoreSource, folderMap);
        }
    }


    public void processFolders(ObjectStore objectStoreSource, String pathToStore, List<Object> folderList, Logger logger) {
        this.logger = logger;
        for (Object folder : folderList) {
            Folder folderInstance = Factory.Folder.fetchInstance(objectStoreSource, (String) folder, null);
            storeData(pathToStore, folderInstance);
        }
    }

    private void storeData(String pathToStore, Folder folderInstance) {

        if (!folderInstance.get_SubFolders().isEmpty()) {
            Iterator iterator = folderInstance.get_SubFolders().iterator();
            while (iterator.hasNext()) {
                Folder currentFolder = (Folder) iterator.next();
                logger.info("Working on: " + currentFolder.get_PathName());
                if (!currentFolder.get_ContainedDocuments().isEmpty()) {
                    DocumentSet containedDocuments = currentFolder.get_ContainedDocuments();
                    Iterator containedDocIterator = containedDocuments.iterator();
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
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
                storeData(pathToStore, currentFolder);
            }
        } else {
            if (!folderInstance.get_ContainedDocuments().isEmpty()) {
                DocumentSet containedDocuments = folderInstance.get_ContainedDocuments();
                Iterator containedDocIterator = containedDocuments.iterator();
                while (containedDocIterator.hasNext()) {
                    Document document = (Document) containedDocIterator.next();
                    ContentElementList contentElements = document.get_ContentElements();
                    if (!contentElements.isEmpty()) {
                        for (Object docContentElement : contentElements) {
                            ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                            InputStream inputStream = contentTransfer.accessContentStream();
                            logger.info("Trying to save file under path: " + pathToStore + folderInstance.get_PathName() + "/" + document.get_Name());
                            try {
                                FileUtils.copyInputStreamToFile(inputStream, new File(pathToStore + folderInstance.get_PathName() + "/" + document.get_Name()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    private RepositoryRowSet fetchRows(String docClass, ObjectStore objectStoreSource) {
        String querySource = "SELECT * FROM " + docClass;
        SearchSQL searchSQL = new SearchSQL();
        searchSQL.setQueryString(querySource);
        SearchScope searchScope = new SearchScope(objectStoreSource);
        return searchScope.fetchRows(searchSQL, null, null, Boolean.TRUE);
    }

    private void extractMetadataDocument(String docClass, ObjectStore objectStoreSource, HashMap<String, Boolean> documentClassMap) {
        Iterator iterator = fetchRows(docClass, objectStoreSource).iterator();
        while (iterator.hasNext()) {
            RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
            try {
                Properties properties = repositoryRow.getProperties();
                String id = properties.getIdValue("ID").toString();
                Document documentSource = Factory.Document.fetchInstance(objectStoreSource, id, null);
                switch (documentSource.getClassName()) {
                    case "DRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "AllegatoFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "AllegatoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentiNecessariPerCategoriaFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoArchibus":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoFADOVersioning":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "FADOMappingCategorie":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "FaldoniListHolder":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportAllegatoX":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportCaricamentoMassivoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportDocumentiDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportDocumentiPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportGruppiLottiCategorie":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportImmobiliDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                }
            } catch (Exception exception) {
                logger.severe(exception.toString());
            }
        }
    }

    private void extractMetadataCustomObject(String docClass, ObjectStore objectStoreSource, HashMap<String, Boolean> customObjectMap) {

        Iterator iterator = fetchRows(docClass, objectStoreSource).iterator();
        while (iterator.hasNext()) {
            RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
            try {
                Properties properties = repositoryRow.getProperties();
                String id = properties.getIdValue("ID").toString();
                CustomObject documentSource = Factory.CustomObject.fetchInstance(objectStoreSource, id, null);
                switch (documentSource.getClassName()) {
                    case "ImmobileFaldoneSecProxyObject":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "LottoSecProxyObject":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ProgressiveNumberDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "FaldoneDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "LottoDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                logger.info("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    logger.info("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                }
            } catch (Exception exception) {
                logger.severe(exception.toString());
            }
        }
    }

    private void extractMetadataFolder(String docClass, ObjectStore objectStoreSource, HashMap<String, Boolean> folderMap) {

        Iterator iterator = fetchRows(docClass, objectStoreSource).iterator();
        while (iterator.hasNext()) {
            RepositoryRow repositoryRow = (RepositoryRow) iterator.next();
            try {
                Properties properties = repositoryRow.getProperties();
                String id = properties.getIdValue("ID").toString();
                CustomObject documentSource = Factory.CustomObject.fetchInstance(objectStoreSource, id, null);
                switch (documentSource.getClassName()) {
                    case "DREFolder":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "CartellaContatori":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "Categoria":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "CategoriaFado":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "CategoriaPatrimoniale":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "Faldone":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "Immobile":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "ImmobilePatrimoniale":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "LottoImmobili":
                        if (folderMap.get(documentSource.getClassName())) {
                            logger.info("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                }
            } catch (Exception exception) {
                logger.severe(exception.toString());
            }
        }
    }

    public void processCSVRenameFiles(String csv, String pathToStore, Logger logger) {
        this.path = pathToStore;
        List<String> pathList = null;
        try {
            pathList = FileUtils.readLines(new File(csv), "UTF-8");
        } catch (IOException e) {
            logger.info("Unable to read the: " + csv + " somehow. Aborting!!!!");
            System.exit(-1);
        }
        int rowNumber = 1;//xk skippo prima riga di intestazione
        List<String> path;
        //Skippo prima riga
        for (int i = 1; i < pathList.size(); i++) {
            String originalPath = "";
            try {
                rowNumber++;
                logger.info("Working on: " + rowNumber + " line of CSV file given.");
                String[] splitTwoColumns = pathList.get(i).split(";");
                //Splitto la prima colonna del path
                String[] splitFirstColumn = splitTwoColumns[0].toString().split("/");
                path = asList(splitFirstColumn);
                //Ricostruisco il path senza il file
                for (int j = 1; j < path.size() - 1; j++) {
                    originalPath = originalPath + path.get(j) + "/";
                }
                File originalFile = new File(this.path + splitTwoColumns[0]);
                File newFile = new File(this.path + originalPath + "/" + splitTwoColumns[1]);
                //vedo se il file indicato nel csv nella prima colonna esiste
                if (Files.exists(originalFile.toPath())) {
                    logger.info("File: " + originalFile + " exist!");
                    //Lo rinomino come e` indicato nella seconda colonna.
                    FileUtils.moveFile(originalFile, newFile);
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                logger.severe("Empty row detected on line: " + rowNumber + ".");
            } catch (IOException e) {
                logger.severe("Unable to rename file somehow. Row affected: " + rowNumber + ".");
            }
        }
    }
}
