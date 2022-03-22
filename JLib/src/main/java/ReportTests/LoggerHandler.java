package ReportTests;

import Enums.HttpMethod;
import JSON.JsonApiHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class LoggerHandler {
    private final String baseEndpoint = "logs/";
    private final ConnectionClient connection;

    private final long testCaseID;
    private final long campaignID;
    private final ArrayList<LogElement> logs;

    static class LogElement {
        private final LocalDateTime dateTime;
        private final String message;
        private final Level level;

        public LogElement(String message, Level level) {
            this.message = message;
            this.level = level;
            dateTime = LocalDateTime.now();
        }

        public JSONObject serializeToJSONObject() {
            return JsonApiHandler.createJSONObjectForSendingLogs(message, level.toString(), dateTime.toString());
        }

        public void publishLog(Logger logger) {
            logger.log(level, dateTime + ": " + message);
        }
    }

    private String createJSONWithLogsForServer() {
        JSONArray jsonLogs = new JSONArray();
        for (LogElement el : logs) {
            jsonLogs.add(el.serializeToJSONObject());
        }
        JSONObject json = new JSONObject();
        json.put("logs", jsonLogs);
        return json.toJSONString();
    }

    public LoggerHandler(ConnectionClient client, long campaignID, long testCaseID) {
        this.connection = client;
        this.campaignID = campaignID;
        this.testCaseID = testCaseID;
        logs = new ArrayList<>();
    }

    public void addLogElement(String message, Level level) {
        logs.add(new LogElement(message, level));
    }

    public void publishLogsLocally(Logger log) {
        for (LogElement el : logs) {
            el.publishLog(log);
        }
    }

    public void sendLogsToServer() throws Exception {
        String data = createJSONWithLogsForServer();
        connection.sendRequest(HttpMethod.POST, baseEndpoint + campaignID + "/" + testCaseID + "/create", data);
    }

}
