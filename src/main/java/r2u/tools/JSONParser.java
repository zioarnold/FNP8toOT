package r2u.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JSONParser {
    Logger logger = Logger.getLogger("r2u.tools.FNP8toOT");
    private FileHandler fileHandler;

    public void parseJson(String json) throws IOException, URISyntaxException {
        URI uri = Paths.get(json).toUri();
        JSONObject jsonObject = getJSON(new URI(uri.toString()).toURL());

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
            System.out.println("SourceCPE is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEObjectStore.isEmpty()) {
            System.out.println("sourceCPEObjectStore is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEUsername.isEmpty()) {
            System.out.println("sourceCPEUsername is empty. Aborting!");
            System.exit(-1);
        }
        if (sourceCPEPassword.isEmpty()) {
            System.out.println("sourceCPEPassword is empty. Aborting!");
            System.exit(-1);
        }
        if (jaasStanzaName.isEmpty()) {
            System.out.println("jaasStanzaName is empty. Aborting!");
            System.exit(-1);
        }
        if (documentClass.isEmpty()) {
            System.out.println("documentClass is empty. Aborting!");
            System.exit(-1);
        }
        if (pathToStore.isEmpty()) {
            System.out.println("pathToStore is empty. Aborting!");
            System.exit(-1);
        }
        if (csv.isEmpty()) {
            System.out.println("csv is empty. Aborting!");
            System.exit(-1);
        }
        if (phase.isEmpty()) {
            System.out.println("phase is empty. Aborting!");
            System.exit(-1);
        }
        if (fileLogPath.isEmpty()) {
            System.out.println("fileLogPath is empty. Aborting!");
            System.exit(-1);
        }

        if (regex.isEmpty()) {
            System.out.println("regex is empty. Aborting!");
            System.exit(-1);
        }

        JSONArray objectClasses = jsonObject.getJSONArray("objectClasses");
        JSONArray objectFolder = jsonObject.getJSONArray("objectFolder");

        if (objectClasses.isEmpty()) {
            System.out.println("objectClasses is empty. Aborting!");
            System.exit(-1);
        }

        if (objectFolder.isEmpty()) {
            System.out.println("objectFolder is empty. Aborting!");
            System.exit(-1);
        }

        Path path = Paths.get(fileLogPath);

        if (Files.exists(path)) {
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                System.out.println("Unable to cleanup logPath: " + path);
                System.exit(-1);
            }
        }

        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.out.println("Unable to create logPath under: " + path);
                System.exit(-1);
            }
        }

        try {
            fileHandler = new FileHandler(fileLogPath + "log.txt");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        logger.addHandler(fileHandler);

        FNConnector fnConnector = new FNConnector(
                sourceCPE,
                sourceCPEObjectStore,
                sourceCPEUsername,
                sourceCPEPassword,
                jaasStanzaName,
                documentClass,
                pathToStore,
                objectClasses,
                objectFolder,
                whatToProcess,
                csv,
                phase,
                regex,
                logger
        );
        fnConnector.startExport();
    }

    private static JSONObject getJSON(URL url) throws IOException {
        String string = IOUtils.toString(url, StandardCharsets.UTF_8);
        return new JSONObject(string);
    }
}
