package ReportTests;

import JSON.JsonApiHandler;
import JSON.JsonConfigHandler;

public class ReportTests {

    final private ConnectionClient connection;

    final private String repositoryName;
    final private String envName;

    public ReportTests(String confFile) throws Exception {
        JsonConfigHandler handler = new JsonConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        this.connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        repositoryName = handler.getParamFromJSONConfig("repositoryName");
        envName = handler.getParamFromJSONConfig("testEnvironment");
    }

    public void beginTestCampaign() throws Exception {
        String data = JsonApiHandler.createJSONForTestCampaign(repositoryName, envName);
        String response = connection.sendPostRequest("campaign/", data);
        System.out.println(response);
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
