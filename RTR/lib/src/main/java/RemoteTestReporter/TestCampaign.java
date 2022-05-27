package RemoteTestReporter;

import Enums.CampaignStatus;
import Enums.HttpMethod;
import JSON.JsonApiHandler;

public class TestCampaign {
    private final ConnectionClient connection;

    private final String baseEndpoint = "campaigns/";
    final private String campaignName;
    final private String envName;
    final private String username;
    private CampaignStatus status;
    private long id;


    public TestCampaign(ConnectionClient connection, String campaignName, String envName, String username) {
        this.campaignName = campaignName;
        this.envName = envName;
        this.status = CampaignStatus.CREATED;
        this.username = username;
        this.connection = connection;
    }

    public ConnectionClient getConnection() {
        return this.connection;
    }

    public long getID() {
        return this.id;
    }

    public void setFailStatus() {
        status = CampaignStatus.REPORTED_FAIL;
    }

    public boolean wasFailReported() {
        return this.status == CampaignStatus.REPORTED_FAIL;
    }


    public void beginTestCampaign() throws Exception {
        if (status != CampaignStatus.CREATED)
            throw new Exception("Cannot start campaign for second time");
        String data = JsonApiHandler.createJSONForNewTestCampaign(campaignName, envName, username);
        String response = connection.sendRequest(HttpMethod.POST, baseEndpoint + "begin", data);
        this.id = JsonApiHandler.getIDFromResponse(response, "testCampaignID");
        this.status = CampaignStatus.RUNNING;
    }

    public void endTestCampaign(CampaignStatus endingStatus) throws Exception {
        if (status != CampaignStatus.RUNNING && status != CampaignStatus.REPORTED_FAIL)
            throw new Exception("Cannot end ended campaign");
        this.status = endingStatus;
        String data = JsonApiHandler.createJSONForEndingOfTestCampaign(status.toString());
        connection.sendRequest(HttpMethod.PUT, baseEndpoint + id + "/end", data);
    }
}
