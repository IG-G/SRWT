package JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonApiHandler {
    public static String createJSONForNewTestCampaign(String campaignName, String envName, String username) {
        JSONObject json = new JSONObject();
        json.put("campaignName", campaignName);
        json.put("envName", envName);
        json.put("username", username);
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

    public static long getIDFromResponse(String response, String id) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        return (long) json.get(id);
    }

    public static String getTokenFromResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        return (String) json.get("access_token");
    }

    public static JSONObject createJSONObjectForSendingLogs(String message, String level, String datetime) {
        JSONObject json = new JSONObject();
        json.put("reportTime", datetime);
        json.put("levelStatus", level);
        json.put("message", message);
        return json;
    }

    public static String createJSONObjectForSendingScreenshots(String path, String datetime) {
        JSONObject json = new JSONObject();
        json.put("path", path);
        json.put("reportTime", datetime);
        return json.toJSONString();
    }
}
