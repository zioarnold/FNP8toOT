package r2u.tools.entities;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("DuplicatedCode")
public class FNCustomObject {
    private final String path;
    private static final Logger logger = Logger.getLogger(FNCustomObject.class.getName());

    public FNCustomObject(String path) {
        this.path = path;
    }

    public void extractImmovableBatchSecProxyObj(ObjectStore objectStoreSource, CustomObject documentSource, String id) {
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

    public void extractSecProxyObjLot(ObjectStore objectStoreSource, CustomObject documentSource, String id) {
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

    public void extractProgressiveNumDispenser(ObjectStore objectStoreSource, CustomObject documentSource, String id) {
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

    public void extractFlapDispenser(ObjectStore objectStoreSource, CustomObject documentSource, String id) {
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

    public void extractLotDispenser(ObjectStore objectStoreSource, CustomObject documentSource, String id) {
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
}
