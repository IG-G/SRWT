package JSON;

import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonApiHandler {

    private static String getCurrentDayAndTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String createJSONForTestCampaign(String repositoryName, String envName) {
        JSONObject json = new JSONObject();
        json.put("repositoryName", repositoryName);
        json.put("envName", envName);
        json.put("dateOfBeginning", getCurrentDayAndTime());
        return json.toJSONString();
    }
}
