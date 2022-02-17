package JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonApiHandler {
    public static String createJSONForNewTestCampaign(String repositoryName, String envName) {
        JSONObject json = new JSONObject();
        json.put("repositoryName", repositoryName);
        json.put("envName", envName);
        return json.toJSONString();
    }

    public static String createJSONForEndingOfTestCampaign(String status) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        return json.toJSONString();
    }

    public static long getCampaignIDFromResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        return (long) json.get("id");
    }
}
