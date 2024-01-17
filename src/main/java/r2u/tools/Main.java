package r2u.tools;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String jsonPath = args[0];
        JSONParser jsonParser = new JSONParser();
        jsonParser.parseJson(jsonPath);
    }
}