import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.core.*;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.json.JSONArray;

import javax.security.auth.Subject;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class FNConnector {
    private final String uriSource;
    private final String objectStoreSource;
    private final String sourceCPEUsername;
    private final String sourceCPEPassword;
    private final String documentClass;
    private final String whatToProcess;
    private final String csv;
    private final String phase;
    private final JSONArray objectClasses;
    private final JSONArray objectFolder;
    private final String pathToStore;
    private FNExportWorker fnExportWorker = null;
    static Connection sourceConnection = null;
    private HashMap<String, Boolean> customObjectMap = null,
            documentClassMap = null, folderMap = null;

    public FNConnector(String uriSource,
                       String objectStoreSource,
                       String sourceCPEUsername,
                       String sourceCPEPassword,
                       String documentClass,
                       String pathToStore,
                       JSONArray objectClasses,
                       JSONArray objectFolder,
                       String whatToProcess,
                       String csv,
                       String phase) {
        this.uriSource = uriSource;
        this.objectStoreSource = objectStoreSource;
        this.sourceCPEUsername = sourceCPEUsername;
        this.sourceCPEPassword = sourceCPEPassword;
        this.documentClass = documentClass;
        this.pathToStore = pathToStore;
        this.objectClasses = objectClasses;
        this.objectFolder = objectFolder;
        this.whatToProcess = whatToProcess;
        this.csv = csv;
        this.phase = phase;
        fnExportWorker = new FNExportWorker();
    }

    private boolean isSourceConnected() {
        boolean connected = false;
        if (sourceConnection == null) {
            sourceConnection = Factory.Connection.getConnection(uriSource);
            Subject subject = UserContext.createSubject(sourceConnection, sourceCPEUsername, sourceCPEPassword, "FileNetP8WSI");
            UserContext.get().pushSubject(subject);
            connected = true;
        }
        return connected;
    }

    public void startExport() throws IOException {
        Domain sourceDomain;
        String[] documentClass = this.documentClass.split(",");
        List<Object> customObject = this.objectClasses.getJSONObject(0).getJSONArray("CustomObject").toList();
        List<Object> document = this.objectClasses.getJSONObject(0).getJSONArray("Document").toList();
        List<Object> folder = this.objectClasses.getJSONObject(0).getJSONArray("Folder").toList();
        List<Object> folderList = this.objectFolder.toList();
        //Nei hashmap memorizzo il symbolic_name della classe documentale a fianco il suo valore true/false.
        //Se e` true, allora da importare, diversamente - no.
        customObjectMap = new HashMap<>();
        for (Object o : customObject) {
            String[] s = o.toString().split("=");
            customObjectMap.put(s[0], Boolean.parseBoolean(s[1]));
        }
        documentClassMap = new HashMap<>();
        for (Object o : document) {
            String[] s = o.toString().split("=");
            documentClassMap.put(s[0], Boolean.parseBoolean(s[1]));
        }
        folderMap = new HashMap<>();
        for (Object o : folder) {
            String[] s = o.toString().split("=");
            folderMap.put(s[0], Boolean.parseBoolean(s[1]));
        }

        //Verifico se raggiungo CPE dell'ingresso, se non va uno di sopra menzionati, manco lo si fa il lavour
        if (isSourceConnected()) {
            sourceDomain = Factory.Domain.fetchInstance(sourceConnection, null, null);
            System.out.println("FileNet sourceDomain name: " + sourceDomain.get_Name());

            ObjectStore objectStoreSource = Factory.ObjectStore.fetchInstance(sourceDomain, this.objectStoreSource, null);
            System.out.println("Object Store source: " + objectStoreSource.get_DisplayName());
            System.out.println("Connected to Source CPE successfully:" + sourceConnection.getURI() + " " + sourceConnection.getConnectionType());
            System.out.println("Switching process work (DocumentClasses or Folders): " + whatToProcess);
            long startTime = 0, endTime = 0;
            switch (whatToProcess) {
                case "DocumentClasses":
                    System.out.println("Working with: " + whatToProcess);
                    startTime = System.currentTimeMillis();
                    for (String docClass : documentClass) {
                        fnExportWorker.process(docClass,
                                objectStoreSource,
                                customObjectMap,
                                documentClassMap,
                                folderMap,
                                pathToStore);
                    }
                    endTime = System.currentTimeMillis();
                    System.out.println("Work with (" + whatToProcess + ") is done within: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                    break;
                case "Folders":
                    switch (phase) {
                        default:
                            System.out.println("Please, specify variale PHASE. Variable managed are: 1,2 and 3.");
                            System.exit(-1);
                            break;
                        case "1":
                            System.out.println("Started phase 1:");
                            System.out.println("Working with: " + whatToProcess);
                            startTime = System.currentTimeMillis();
                            fnExportWorker.processFolders(objectStoreSource, pathToStore, folderList);
                            endTime = System.currentTimeMillis();
                            System.out.println("Job done with: " + whatToProcess);
                            System.out.println("Terminated phase 1: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                            break;
                        case "2":
                            System.out.println("Started phase 2: ");
                            startTime = System.currentTimeMillis();
                            fnExportWorker.processCSVRenameFiles(csv, pathToStore);
                            endTime = System.currentTimeMillis();
                            System.out.println("Terminated phase 2: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                            break;
                        case "3":
                            System.out.println("Started phase 1:");
                            System.out.println("Working with: " + whatToProcess);
                            startTime = System.currentTimeMillis();
                            fnExportWorker.processFolders(objectStoreSource, pathToStore, folderList);
                            endTime = System.currentTimeMillis();
                            System.out.println("Job done with: " + whatToProcess);
                            System.out.println("Terminated phase 1: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                            System.out.println("Started phase 2: ");
                            startTime = System.currentTimeMillis();
                            fnExportWorker.processCSVRenameFiles(csv, pathToStore);
                            endTime = System.currentTimeMillis();
                            System.out.println("Terminated phase 2: " + DurationFormatUtils.formatDuration(endTime - startTime, "HH:MM:SS", true));
                            break;
                    }
                    break;
            }
        } else {
            System.out.println("Source isn't connected. Make sure your Internet is ok, VPN is on.");
            System.exit(-1);
        }
    }
}
