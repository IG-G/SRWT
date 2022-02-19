package ReportTests;

import JSON.JsonApiHandler;
import JSON.JsonConfigHandler;

public class ReportTests {

    final private ConnectionClient connection;
    private final TestCampaign campaign;

    public ReportTests(String confFile) throws Exception {
        JsonConfigHandler handler = new JsonConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        this.connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        String repositoryName = handler.getParamFromJSONConfig("repositoryName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        campaign = new TestCampaign(repositoryName, envName);
    }

    public void beginTestCampaign() throws Exception {
        String data = JsonApiHandler.createJSONForNewTestCampaign(campaign.getRepositoryName(),
                campaign.getEnvName());
        String response = connection.sendPostRequest("campaigns/", data);
        campaign.setCampaignId(JsonApiHandler.getCampaignIDFromResponse(response));
        campaign.setStatus(Status.STARTED);
        System.out.println("Response for beginning campaign: " + response);
    }

    public void beginTestCase() {

    }

    public void reportFailure() {

    }

    public void endTestCase() {

    }

    public void endTestCampaign() throws Exception {
        if(campaign.getStatus() != Status.FAILED)
            campaign.setStatus(Status.PASSED);

        String data = JsonApiHandler.createJSONForEndingOfTestCampaign(campaign.getStatus().toString());
        String campaignId = String.valueOf(campaign.getCampaignId());
        connection.sendPutRequest("campaigns/" + campaignId, data);
    }
}
