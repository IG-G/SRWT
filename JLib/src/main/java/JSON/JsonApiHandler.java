package JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonApiHandler {
    public static String createJSONForNewTestCampaign(String campaignName, String envName) {
        JSONObject json = new JSONObject();
        json.put("campaignName", campaignName);
        json.put("envName", envName);
        return json.toJSONString();
    }

    public static String createJSONForEndingOfTestCampaign(String status) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        return json.toJSONString();
    }

    public static String createJSONForNewTestCase(String name) {
        JSONObject json = new JSONObject();
        json.put("testCaseName", name);
        return json.toJSONString();
    }

    public static String createJSONForEndingOfTestCase(String status) {
        JSONObject json = new JSONObject();
        json.put("status", status);
        return json.toJSONString();
    }

    public static String createJSONForReportingFailure(String message) {
        JSONObject json = new JSONObject();
        json.put("message", message);
        return json.toJSONString();
    }

    public static long getIDFromResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        return (long) json.get("id");
    }
}
