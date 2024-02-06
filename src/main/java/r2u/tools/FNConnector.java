package r2u.tools;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.UserContext;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.json.JSONArray;

import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class FNConnector {
    private final String uriSource;
    private final String objectStoreSource;
    private final String sourceCPEUsername;
    private final String sourceCPEPassword;
    private final String documentClass;
    private final String whatToProcess;
    private final String phase;
    private final String jaasStanzaName;
    private final JSONArray objectClasses;
    private final JSONArray objectFolder;
    private final FNExportWorker fnExportWorker;

    Logger logger;

    public FNConnector(String uriSource,
                       String objectStoreSource,
                       String sourceCPEUsername,
                       String sourceCPEPassword,
                       String jaasStanzaName,
                       String documentClass,
                       String pathToStore,
                       JSONArray objectClasses,
                       JSONArray objectFolder,
                       String whatToProcess,
                       String csv,
                       String phase,
                       String regex,
                       Logger logger) {
        this.uriSource = uriSource;
        this.objectStoreSource = objectStoreSource;
        this.sourceCPEUsername = sourceCPEUsername;
        this.sourceCPEPassword = sourceCPEPassword;
        this.jaasStanzaName = jaasStanzaName;
        this.documentClass = documentClass;
        this.objectClasses = objectClasses;
        this.objectFolder = objectFolder;
        this.whatToProcess = whatToProcess;
        this.phase = phase;
        this.logger = logger;
        fnExportWorker = new FNExportWorker(pathToStore, regex, logger, csv);
    }

    public void startExport() {
        String[] documentClass = this.documentClass.split(",");
        List<Object> customObject = this.objectClasses.getJSONObject(0).getJSONArray("CustomObject").toList();
        List<Object> document = this.objectClasses.getJSONObject(0).getJSONArray("Document").toList();
        List<Object> folder = this.objectClasses.getJSONObject(0).getJSONArray("Folder").toList();
        List<Object> folderList = this.objectFolder.toList();
        //Nei hashmap memorizzo il symbolic_name della classe documentale a fianco il suo valore true/false.
        //Se e` true, allora da importare, diversamente - no.
        HashMap<String, Boolean> customObjectMap = new HashMap<>();
        for (Object o : customObject) {
            String[] s = o.toString().split("=");
            customObjectMap.put(s[0], Boolean.parseBoolean(s[1]));
        }
        HashMap<String, Boolean> documentClassMap = new HashMap<>();
        for (Object o : document) {
            String[] s = o.toString().split("=");
            documentClassMap.put(s[0], Boolean.parseBoolean(s[1]));
        }
        HashMap<String, Boolean> folderMap = new HashMap<>();
        for (Object o : folder) {
            String[] s = o.toString().split("=");
            folderMap.put(s[0], Boolean.parseBoolean(s[1]));
        }

        long startTime, endTime;
        switch (whatToProcess) {
            case "DocumentClasses":
                logger.info("Working with: " + whatToProcess);
                startTime = System.currentTimeMillis();
                for (String docClass : documentClass) {
                    fnExportWorker.extractByObjectClass(docClass,
                            getObjectStoreSource(),
                            customObjectMap,
                            documentClassMap,
                            folderMap);
                }
                endTime = System.currentTimeMillis();
                logger.info("Work with (" + whatToProcess + ") is done within: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                break;
            case "Folders":
                switch (phase) {
                    default:
                        logger.info("Please, specify variale PHASE. Variable managed are: 1,2 and 3, All.");
                        System.exit(-1);
                        break;
                    case "1":
                        //Scarica fi file dalla cartella dall'object store indicata nel "objectFolder"
                        logger.info("Started phase 1:");
                        logger.info("Working with: " + whatToProcess);
                        startTime = System.currentTimeMillis();
                        fnExportWorker.processFolders(getObjectStoreSource(), folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Job done with: " + whatToProcess);
                        logger.info("Terminated phase 1: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                        break;
                    case "2":
                        //Rinomina i file in accordo con file csv
                        logger.info("Started phase 2:");
                        startTime = System.currentTimeMillis();
                        fnExportWorker.renameFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 2: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                        break;
                    case "3":
                        //Elimina i file che non c'entrano coi file presenti nel csv
                        logger.info("Started phase 3:");
                        startTime = System.currentTimeMillis();
                        fnExportWorker.deleteRemnantsFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 3: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                        break;
                    case "All":
                        //Fa tutto
                        logger.info("Started phase 1:");
                        startTime = System.currentTimeMillis();
                        fnExportWorker.processFolders(getObjectStoreSource(), folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 1: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                        logger.info("Started phase 2:");
                        startTime = System.currentTimeMillis();
                        fnExportWorker.renameFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 2: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                        logger.info("Started phase 3:");
                        startTime = System.currentTimeMillis();
                        fnExportWorker.deleteRemnantsFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 3: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                        break;
                }
                break;
        }
    }

    private ObjectStore getObjectStoreSource() {
        Domain sourceDomain;
        Connection sourceConnection;
        ObjectStore objectStoreSource = null;
        try {
            sourceConnection = Factory.Connection.getConnection(uriSource);
            Subject subject = UserContext.createSubject(sourceConnection, sourceCPEUsername, sourceCPEPassword, jaasStanzaName);
            UserContext.get().pushSubject(subject);
            sourceDomain = Factory.Domain.fetchInstance(sourceConnection, null, null);
            logger.info("FileNet sourceDomain name: " + sourceDomain.get_Name());
            objectStoreSource = Factory.ObjectStore.fetchInstance(sourceDomain, this.objectStoreSource, null);
            logger.info("Object Store source: " + objectStoreSource.get_DisplayName());
            logger.info("Connected to Source CPE successfully:" + sourceConnection.getURI() + " " + sourceConnection.getConnectionType());
            logger.info("Switching extractByObjectClass work (DocumentClasses or Folders): " + whatToProcess);
        } catch (EngineRuntimeException exception) {
            logger.severe(exception.toString());
            System.exit(-1);
        }
        return objectStoreSource;
    }
}
