package JSON;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class JsonConfigHandler {
    JSONObject json;

    public JsonConfigHandler(String pathToConfigFile) throws Exception {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(pathToConfigFile);
        json = (JSONObject) parser.parse(fileReader);
    }

    public String getParamFromJSONConfig(String attributeName) {
        return (String)json.get(attributeName);
    }
}

