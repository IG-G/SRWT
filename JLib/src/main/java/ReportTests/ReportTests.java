package ReportTests;

import Connection.ConnectionClient;
import JSON.JSONAPIHandler;
import JSON.JSONConfigHandler;

public class ReportTests {

    final private ConnectionClient connection;

    final private String repositoryName;
    final private String envName;

    public ReportTests(String confFile) throws Exception {
        JSONConfigHandler handler = new JSONConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        this.connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        repositoryName = handler.getParamFromJSONConfig("repositoryName");
        envName = handler.getParamFromJSONConfig("testEnvironment");
    }

    public void beginTestCampaign() throws Exception {
        String data = JSONAPIHandler.createJSONForTestCampaign(repositoryName, envName);
        connection.sendRequest("PUT", "campaign/begin", data);
    }

    public void beginTestCase() {

    }

    public void reportFailure() {

    }

    public void endTestCase() {

    }

    public void endTestCampaign() {

    }
}
