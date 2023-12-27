import com.filenet.api.collection.*;
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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Arrays.asList;

@SuppressWarnings({"DuplicatedCode", "SpellCheckingInspection", "DuplicateBranchesInSwitch"})
public class FNExportWorker {
    private String path;
    private HashMap<String, Boolean> customObjectMap = null,
            documentClassMap = null,
            folderMap = null;

    public void process(String docClass,
                        ObjectStore objectStoreSource,
                        HashMap<String, Boolean> customObjectMap,
                        HashMap<String, Boolean> documentClassMap,
                        HashMap<String, Boolean> folderMap,
                        String pathToStore) {
        this.customObjectMap = customObjectMap;
        this.documentClassMap = documentClassMap;
        this.folderMap = folderMap;
        this.path = pathToStore;
        if (docClass.equalsIgnoreCase("Document")) {
            extractMetadataDocument(docClass, objectStoreSource);
        }
        if (docClass.equalsIgnoreCase("CustomObject")) {
            extractMetadataCustomObject(docClass, objectStoreSource);
        }
        if (docClass.equalsIgnoreCase("Folder")) {
            extractMetadataFolder(docClass, objectStoreSource);
        }
    }


    public void processFolders(ObjectStore objectStoreSource, String pathToStore, List<Object> folderList) throws IOException {
        for (Object folder : folderList) {
            Folder folderInstance = Factory.Folder.fetchInstance(objectStoreSource, (String) folder, null);
            storeData(pathToStore, folderInstance);
        }
    }

    private static void storeData(String pathToStore, Folder folderInstance) throws IOException {
        if (!folderInstance.get_SubFolders().isEmpty()) {
            Iterator iterator = folderInstance.get_SubFolders().iterator();
            while (iterator.hasNext()) {
                Folder currentFolder = (Folder) iterator.next();
                System.out.println("Working on: " + currentFolder.get_PathName());
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
                                System.out.println("Trying to save file under path: " + pathToStore + currentFolder.get_PathName() + "/" + document.get_Name());
                                FileUtils.copyInputStreamToFile(inputStream, new File(pathToStore + currentFolder.get_PathName() + "/" + document.get_Name()));
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
                            System.out.println("Trying to save file under path: " + pathToStore + folderInstance.get_PathName() + "/" + document.get_Name());
                            FileUtils.copyInputStreamToFile(inputStream, new File(pathToStore + folderInstance.get_PathName() + "/" + document.get_Name()));
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

    private void extractMetadataDocument(String docClass, ObjectStore objectStoreSource) {
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
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "AllegatoFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "AllegatoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentiNecessariPerCategoriaFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoArchibus":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoFADO":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoFADOVersioning":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "DocumentoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "FADOMappingCategorie":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "FaldoniListHolder":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportAllegatoX":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportCaricamentoMassivoPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportDocumentiDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportDocumentiPatrimoniale":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportGruppiLottiCategorie":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ReportImmobiliDRE":
                        if (documentClassMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className: " + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void extractMetadataCustomObject(String docClass, ObjectStore objectStoreSource) {
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
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "LottoSecProxyObject":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "ProgressiveNumberDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "FaldoneDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                    case "LottoDispenser":
                        if (customObjectMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                            Document doc = Factory.Document.fetchInstance(objectStoreSource, id, null);
                            ContentElementList docContentElements = doc.get_ContentElements();
                            if (!docContentElements.isEmpty()) {
                                System.out.println("Found some files, working on export");
                                for (Object docContentElement : docContentElements) {
                                    ContentTransfer contentTransfer = (ContentTransfer) docContentElement;
                                    InputStream inputStream = contentTransfer.accessContentStream();
                                    System.out.println("Trying to save file under path: " + path + doc.get_Name());
                                    FileUtils.copyInputStreamToFile(inputStream, new File(path + doc.get_Name()));
                                }
                            }
                        }
                        break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void extractMetadataFolder(String docClass, ObjectStore objectStoreSource) {
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
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "CartellaContatori":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "Categoria":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "CategoriaFado":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "CategoriaPatrimoniale":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "Faldone":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "Immobile":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "ImmobilePatrimoniale":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                    case "LottoImmobili":
                        if (folderMap.get(documentSource.getClassName())) {
                            System.out.println("Working on: " + id + " className:" + documentSource.getClassName());
                        }
                        break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void processCSVRenameFiles(String csv, String pathToStore) {
        if (csv.isEmpty()) {
            System.out.println("No CSV file given to process");
            System.exit(-1);
        }
        this.path = pathToStore;
        List<String> pathList = null;
        try {
            pathList = FileUtils.readLines(new File(csv), "UTF-8");
        } catch (IOException e) {
            System.out.println("Unable to read the: " + csv + " somehow. Aborting!!!!");
            System.exit(-1);
        }
        int rowNumber = 1;//xk skippo prima riga di intestazione
        List<String> path;
        //Skippo prima riga
        for (int i = 1; i < pathList.size(); i++) {
            String originalPath = "";
            try {
                rowNumber++;
                System.out.println("Working on: " + rowNumber + " line of CSV file given.");
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
                    System.out.println("File: " + originalFile + " exist!");
                    //Lo rinomino come e` indicato nella seconda colonna.
                    FileUtils.moveFile(originalFile, newFile);
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                System.out.println("Empty row detected on line: " + rowNumber + ".");
            } catch (IOException e) {
                System.out.println("Unable to rename file somehow. Row affected: " + rowNumber + ".");
            }
        }
    }
}
