import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Se vogliate finire la giornata di lavoro sani, chiudete subito sto codice.
 * Diversamente, benvenuti nell'incubo! Si raccomanda di lavorare con sale e pepe nel sedere.
 * Questa versione dell'Incubo 1 attualmente necessita di 5 parametri configurabili su JSON:
 * MTOM dell'Origine e del suo nome objectStore
 * Utente e Password.
 * Classi documentali (Document,CustomObject,Folder)
 */
public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String jsonPath = args[0];
        JSONParser jsonParser = new JSONParser();
        jsonParser.parseJson(jsonPath);
    }
}