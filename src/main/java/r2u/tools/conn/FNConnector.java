package r2u.tools.conn;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.UserContext;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import r2u.tools.config.Configurator;
import r2u.tools.constants.Constants;
import r2u.tools.utils.FileOperations;
import r2u.tools.worker.FNExportWorker;

import javax.security.auth.Subject;
import java.util.List;

public class FNConnector {
    private final Configurator instance = Configurator.getInstance();
    private final FNExportWorker fnExportWorker;
    private static final Logger logger = Logger.getLogger(FNConnector.class.getName());

    public FNConnector() {
        fnExportWorker = new FNExportWorker();
        ObjectStore objectStore = null;
        int indexAttempt = 1, maxAttempts = 5;
        while (objectStore == null) {
            objectStore = objectStoreSetUp(indexAttempt);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error("SOMETHING WRONG WITH THREAD.SLEEP. Aborting!", e);
                System.exit(-1);
            }
            if (indexAttempt == maxAttempts) {
                break;
            }
            indexAttempt++;
        }
        if (objectStore != null) {
            instance.setObjectStore(objectStore);
        } else {
            logger.error("AFTER " + indexAttempt + " ATTEMPTS TO ESTABLISH THE CONNECTION TO: " + instance.getUriSource() + " PROGRAM IS ABORTED!");
            System.exit(-1);
        }
    }

    public void startExport() {
        String[] documentClass = instance.getDocumentClass().split(",");
        List<Object> folderList = instance.getObjectFolder().toList();
        //Nei hashmap memorizzo il symbolic_name della classe documentale a fianco il suo valore true/false.
        //Se e` true, allora da importare, diversamente - no.
        long startTime, endTime;
        switch (instance.getWhatToProcess()) {
            case "DocumentClasses": {
                logger.info("Working with: " + instance.getWhatToProcess());
                startTime = System.currentTimeMillis();
                for (String docClass : documentClass) {
                    fnExportWorker.extractByObjectClass(docClass);
                }
                endTime = System.currentTimeMillis();
                logger.info("Work with (" + instance.getWhatToProcess() + ") is done within: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
            }
            break;
            case "Folders": {
                switch (instance.getPhase()) {
                    default: {
                        logger.info("Please, specify variale PHASE. Variable managed are: 1,2 and 3, All.");
                        System.exit(-1);
                    }
                    break;
                    case "1": {
                        //Scarica fi file dalla cartella dal object store indicata nel "objectFolder"
                        logger.info("Started phase 1:");
                        logger.info("Working with: " + instance.getWhatToProcess());
                        startTime = System.currentTimeMillis();
                        fnExportWorker.processFolders(instance.getObjectStore(), folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Job done with: " + instance.getWhatToProcess());
                        logger.info("Terminated phase 1: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
                    }
                    break;
                    case "2": {
                        //Rinomina i file in accordo con file csv
                        logger.info("Started phase 2:");
                        startTime = System.currentTimeMillis();
                        FileOperations.renameFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 2: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
                    }
                    break;
                    case "3": {
                        //Elimina i file che non c'entrano coi file presenti nel csv
                        logger.info("Started phase 3:");
                        startTime = System.currentTimeMillis();
                        FileOperations.deleteRemnantsFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 3: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
                    }
                    break;
                    case "All": {
                        //Fa tutto
                        logger.info("Started phase 1:");
                        startTime = System.currentTimeMillis();
                        fnExportWorker.processFolders(instance.getObjectStore(), folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 1: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
                        logger.info("Started phase 2:");
                        startTime = System.currentTimeMillis();
                        FileOperations.renameFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 2: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
                        logger.info("Started phase 3:");
                        startTime = System.currentTimeMillis();
                        FileOperations.deleteRemnantsFiles(folderList);
                        endTime = System.currentTimeMillis();
                        logger.info("Terminated phase 3: " + DurationFormatUtils.formatDuration(endTime - startTime, Constants.dateTimeFormat, true));
                    }
                    break;
                }
            }
            break;
        }
    }

    private ObjectStore objectStoreSetUp(int indexAttempt) {
        Domain sourceDomain;
        Connection sourceConnection;
        logger.info("Trying to establish connection to: " + instance.getUriSource() + "; Attempt number: " + indexAttempt);
        try {
            sourceConnection = Factory.Connection.getConnection(instance.getUriSource());
            Subject subject = UserContext.createSubject(Factory.Connection.getConnection(instance.getUriSource()),
                    instance.getSourceCPEUsername(), instance.getSourceCPEPassword(), instance.getJaasStanzaName());
            UserContext.get().pushSubject(subject);
            sourceDomain = Factory.Domain.fetchInstance(sourceConnection, null, null);
            logger.info("FileNet sourceDomain name: " + sourceDomain.get_Name());
            ObjectStore objectStore = Factory.ObjectStore.fetchInstance(sourceDomain, instance.getSourceCPEObjectStore(), null);
            logger.info("Object Store source: " + objectStore.get_DisplayName());
            logger.info("Connected to Source CPE successfully: " + sourceConnection.getURI() + " " + sourceConnection.getConnectionType());
            return objectStore;
        } catch (EngineRuntimeException exception) {
            if (exception.getExceptionCode().getErrorId().equals("FNRCA0031")) {
                logger.error("CONNECTION TIMEOUT, PLEASE CHECK THE WSDL: " + instance.getUriSource(), exception);
            } else {
                logger.error("UNMANAGED ERROR IS CAUGHT: " + instance.getUriSource(), exception);
            }
            return null;
        }
    }
}
