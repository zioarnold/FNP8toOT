package r2u.tools.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import r2u.tools.config.Configurator;
import r2u.tools.conn.FNConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser {
    Logger logger = Logger.getLogger(JSONParser.class.getName());

    public void parseJson(String json) {
        URI uri = Paths.get(json).toUri();
        JSONObject jsonObject;
        try {
            jsonObject = getJSON(new URI(uri.toString()).toURL());
        } catch (IOException | URISyntaxException e) {
            logger.error("Unable initialize jsonObject", e);
            throw new RuntimeException(e);
        }

        String sourceCPE = jsonObject.getString("sourceCPE"),
                sourceCPEObjectStore = jsonObject.getString("sourceCPEObjectStore"),
                sourceCPEUsername = jsonObject.getString("sourceCPEUsername"),
                sourceCPEPassword = jsonObject.getString("sourceCPEPassword"),
                jaasStanzaName = jsonObject.getString("jaasStanzaName"),
                documentClass = jsonObject.getString("documentClass"),
                pathToStore = jsonObject.getString("pathToStore"),
                whatToProcess = jsonObject.getString("whatToProcess"),
                csv = jsonObject.getString("csv"),
                phase = jsonObject.getString("phase"),
                fileLogPath = jsonObject.getString("fileLogPath"),
                regex = jsonObject.getString("regex");

        if (sourceCPE.isEmpty()) {
            logger.error("SourceCPE is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEObjectStore.isEmpty()) {
            logger.error("sourceCPEObjectStore is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEUsername.isEmpty()) {
            logger.error("sourceCPEUsername is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEPassword.isEmpty()) {
            logger.error("sourceCPEPassword is empty. Aborting!");
            System.exit(-1);
        }
        if (jaasStanzaName.isEmpty()) {
            logger.error("jaasStanzaName is empty. Aborting!");
            System.exit(-1);
        }
        if (documentClass.isEmpty()) {
            logger.error("documentClass is empty. Aborting!");
            System.exit(-1);
        }
        if (pathToStore.isEmpty()) {
            logger.error("pathToStore is empty. Aborting!");
            System.exit(-1);
        }
        if (csv.isEmpty()) {
            logger.error("csv is empty. Aborting!");
            System.exit(-1);
        }
        if (phase.isEmpty()) {
            logger.error("phase is empty. Aborting!");
            System.exit(-1);
        }
        if (fileLogPath.isEmpty()) {
            logger.error("fileLogPath is empty. Aborting!");
            System.exit(-1);
        }

        if (regex.isEmpty()) {
            logger.error("regex is empty. Aborting!");
            System.exit(-1);
        }

        HashMap<String, Boolean> customObjectMap = convertArrayList2HashMap(
                convertObject2StringArrayList(
                        jsonObject.getJSONArray("objectClasses")
                                .getJSONObject(0)
                                .getJSONArray("CustomObject")
                                .toList()
                )
        );

        HashMap<String, Boolean> documentClassMap = convertArrayList2HashMap(
                convertObject2StringArrayList(
                        jsonObject.getJSONArray("objectClasses")
                                .getJSONObject(0)
                                .getJSONArray("Document")
                                .toList()
                )
        );

        HashMap<String, Boolean> folderMap = convertArrayList2HashMap(
                convertObject2StringArrayList(
                        jsonObject.getJSONArray("objectClasses")
                                .getJSONObject(0)
                                .getJSONArray("Folder")
                                .toList()
                )
        );

        JSONArray objectFolder = jsonObject.getJSONArray("objectFolder");

        if (customObjectMap.isEmpty()) {
            logger.error("customObjectMap is empty. Aborting!");
            System.exit(-1);
        }
        if (documentClassMap.isEmpty()) {
            logger.error("documentClassMap is empty. Aborting!");
            System.exit(-1);
        }
        if (folderMap.isEmpty()) {
            logger.error("objectClasses is empty. Aborting!");
            System.exit(-1);
        }

        if (objectFolder.isEmpty()) {
            logger.error("folderMap is empty. Aborting!");
            System.exit(-1);
        }

        Path path = Paths.get(fileLogPath);

        if (Files.exists(path)) {
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                logger.error("Unable to cleanup logPath: " + path);
                System.exit(-1);
            }
        } else {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.error("Unable to create logPath under: " + path);
                System.exit(-1);
            }
        }
        Configurator instance = Configurator.getInstance();
        instance.setUriSource(sourceCPE);
        instance.setObjectStoreSource(sourceCPEObjectStore);
        instance.setSourceCPEUsername(sourceCPEUsername);
        instance.setSourceCPEPassword(sourceCPEPassword);
        instance.setJaasStanzaName(jaasStanzaName);
        instance.setDocumentClass(documentClass);
        instance.setPathToStore(pathToStore);
        instance.setObjectFolder(objectFolder);
        instance.setWhatToProcess(whatToProcess);
        instance.setCsv(csv);
        instance.setPhase(phase);
        instance.setRegex(regex);
        instance.setCustomObjectMap(customObjectMap);
        instance.setDocumentClassMap(documentClassMap);
        instance.setFolderMap(folderMap);
        FNConnector fnConnector = new FNConnector();
        fnConnector.startExport();
    }

    private static JSONObject getJSON(URL url) throws IOException {
        return new JSONObject(IOUtils.toString(url, StandardCharsets.UTF_8));
    }

    private static ArrayList<String> convertObject2StringArrayList(List<Object> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        //Converto oggetti in stringhe
        for (Object object : list) {
            arrayList.add(object.toString());
        }
        return arrayList;
    }

    private static HashMap<String, Boolean> convertArrayList2HashMap(ArrayList<String> objectList) {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String object : objectList) {
            map.put(object.split("=")[0], Boolean.valueOf(object.split("=")[1]));
        }
        return map;
    }
}
