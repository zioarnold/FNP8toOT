package r2u.tools.entities;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("DuplicatedCode")
public class FNDocument {
    private final String path;
    private static final Logger logger = Logger.getLogger(FNDocument.class.getName());

    public FNDocument(String path) {
        this.path = path;
    }

    public void extractDREDocument(ObjectStore objectStoreSource, Document documentSource, String id) {
        logger.info("Working on: " + id + " className:" + documentSource.getClassName());
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractAllFADO(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractAllPatrimonial(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractDocumentsRequiredForFADOCategory(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractArchibusDocument(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractDocumentFADO(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractFADODocumentVersioning(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractDocPatrimonial(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractFADOCategoryMapping(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractFolderListHolder(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractDREReport(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractAllXReport(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractReportMassUploadPatrimonial(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractDREDocumentReport(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractReportDocPatrimonial(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractBatchesGroupCategoryReport(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void extractDREReportImmovables(ObjectStore objectStoreSource, Document documentSource, String id) {
        try {
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
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
