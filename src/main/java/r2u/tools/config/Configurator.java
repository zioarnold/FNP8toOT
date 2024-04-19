package r2u.tools.config;

import com.filenet.api.core.ObjectStore;
import org.json.JSONArray;

import java.util.HashMap;

public class Configurator {
    private static Configurator instance = null;
    private String uriSource, objectStoreSource, sourceCPEUsername, sourceCPEPassword, documentClass, whatToProcess, phase, jaasStanzaName, pathToStore, regex, csv;
    private JSONArray objectFolder;
    private HashMap<String, Boolean> customObjectMap, documentClassMap, folderMap;
    private ObjectStore objectStore;

    private Configurator() {

    }

    public static synchronized Configurator getInstance() {
        if (instance == null) {
            instance = new Configurator();
        }
        return instance;
    }

    public String getUriSource() {
        return uriSource;
    }

    public void setUriSource(String uriSource) {
        this.uriSource = uriSource;
    }

    public String getObjectStoreSource() {
        return objectStoreSource;
    }

    public void setObjectStoreSource(String objectStoreSource) {
        this.objectStoreSource = objectStoreSource;
    }

    public String getSourceCPEUsername() {
        return sourceCPEUsername;
    }

    public void setSourceCPEUsername(String sourceCPEUsername) {
        this.sourceCPEUsername = sourceCPEUsername;
    }

    public String getSourceCPEPassword() {
        return sourceCPEPassword;
    }

    public void setSourceCPEPassword(String sourceCPEPassword) {
        this.sourceCPEPassword = sourceCPEPassword;
    }

    public String getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(String documentClass) {
        this.documentClass = documentClass;
    }

    public String getWhatToProcess() {
        return whatToProcess;
    }

    public void setWhatToProcess(String whatToProcess) {
        this.whatToProcess = whatToProcess;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getJaasStanzaName() {
        return jaasStanzaName;
    }

    public void setJaasStanzaName(String jaasStanzaName) {
        this.jaasStanzaName = jaasStanzaName;
    }

    public JSONArray getObjectFolder() {
        return objectFolder;
    }

    public void setObjectFolder(JSONArray objectFolder) {
        this.objectFolder = objectFolder;
    }

    public HashMap<String, Boolean> getCustomObjectMap() {
        return customObjectMap;
    }

    public void setCustomObjectMap(HashMap<String, Boolean> customObjectMap) {
        this.customObjectMap = customObjectMap;
    }

    public HashMap<String, Boolean> getDocumentClassMap() {
        return documentClassMap;
    }

    public void setDocumentClassMap(HashMap<String, Boolean> documentClassMap) {
        this.documentClassMap = documentClassMap;
    }

    public HashMap<String, Boolean> getFolderMap() {
        return folderMap;
    }

    public void setFolderMap(HashMap<String, Boolean> folderMap) {
        this.folderMap = folderMap;
    }

    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public void setObjectStore(ObjectStore objectStore) {
        this.objectStore = objectStore;
    }

    public String getPathToStore() {
        return pathToStore;
    }

    public void setPathToStore(String pathToStore) {
        this.pathToStore = pathToStore;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
