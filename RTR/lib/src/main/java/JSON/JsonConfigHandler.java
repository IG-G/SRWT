package JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.NoSuchElementException;

public class JsonConfigHandler {
    JSONObject json;

    public JsonConfigHandler(String pathToConfigFile) throws Exception {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(pathToConfigFile);
        json = (JSONObject) parser.parse(fileReader);
    }

    public String getParamFromJSONConfig(String attributeName) throws NoSuchElementException {
        String attribute = (String) json.get(attributeName);
        if (attribute == null)
            throw new NoSuchElementException("No such attribute as " + attributeName);
        return attribute;
    }
}

