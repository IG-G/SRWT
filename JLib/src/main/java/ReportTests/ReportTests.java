package ReportTests;

import JSON.JsonApiHandler;
import JSON.JsonConfigHandler;

public class ReportTests {

    final private ConnectionClient connection;
    private final TestCampaignParameters campaignParameters;

    public ReportTests(String confFile) throws Exception {
        JsonConfigHandler handler = new JsonConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        this.connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        String repositoryName = handler.getParamFromJSONConfig("repositoryName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        campaignParameters = new TestCampaignParameters(repositoryName, envName);
    }

    public void beginTestCampaign() throws Exception {
        String data = JsonApiHandler.createJSONForNewTestCampaign(campaignParameters.getRepositoryName(),
                campaignParameters.getEnvName());
        String response = connection.sendPostRequest("campaigns/", data);
        campaignParameters.setCampaignId(JsonApiHandler.getCampaignIDFromResponse(response));
        System.out.println(response);
    }

    public void beginTestCase() {

    }

    public void reportFailure() {

    }

    public void endTestCase() {

    }

    public void endTestCampaign() throws Exception {
        String data = JsonApiHandler.createJSONForEndingOfTestCampaign("PASSED");
        String campaignId = String.valueOf(campaignParameters.getCampaignId());
        System.out.println(campaignId);
        connection.sendPutRequest("campaigns/" + campaignId, data);
    }
}
