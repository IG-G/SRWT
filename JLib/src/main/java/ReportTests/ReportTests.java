package ReportTests;

import Enums.CampaignStatus;
import Enums.TestCaseStatus;
import JSON.JsonApiHandler;
import JSON.JsonConfigHandler;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class ReportTests {

    private final ConnectionClient connection;
    private final TestCampaign campaign;
    Logger log_debug; // for debug ReportTest library purposes

    public ReportTests(String confFile) throws Exception {
        log_debug = Logger.getLogger(TestCampaign.class.getName());
        log_debug.addHandler(new FileHandler("lib.log"));

        JsonConfigHandler handler = new JsonConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        this.connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        String repositoryName = handler.getParamFromJSONConfig("campaignName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        campaign = new TestCampaign(repositoryName, envName);
    }

    public void beginTestCampaign() throws Exception {
        String data = JsonApiHandler.createJSONForNewTestCampaign(campaign.getCampaignName(),
                campaign.getEnvName());
        log_debug.info("Data to be send to server: " + data);
        String response = connection.sendPostRequest("campaigns/", data);
        log_debug.info("Response from server: " + response);
        campaign.setCampaignId(JsonApiHandler.getIDFromResponse(response));
        campaign.setCampaignStatus(CampaignStatus.RUNNING);
    }

    public void beginTestCase(String testCaseName) throws Exception {
        String data = JsonApiHandler.createJSONForNewTestCase(testCaseName);
        log_debug.info("Data to be send to server: " + data);
        String campaignId = String.valueOf(campaign.getCampaignId());
        String response = connection.sendPostRequest("testcases/" + campaignId, data);
        log_debug.info("Response from server: " + response);
        campaign.setCurrentTestCaseId(JsonApiHandler.getIDFromResponse(response));
        campaign.setCurrentTestCaseStatus(TestCaseStatus.IN_PROGRESS);
    }

    public void reportFailure(String message, Boolean end_test_case) throws Exception {
        String data = JsonApiHandler.createJSONForReportingFailure(message);
        log_debug.info("Data to be send to server: " + data);
        String testCaseId = String.valueOf(campaign.getCurrentTestCaseId());
        String campaignId = String.valueOf(campaign.getCampaignId());
        connection.sendPostRequest("testcases/" + campaignId + "/" + testCaseId + "/fail", data);
        campaign.setCurrentTestCaseStatus(TestCaseStatus.FAILED);
        if(end_test_case) {
            endTestCase();
        }
    }

    public void endTestCase() throws Exception {
        if(campaign.getCurrentTestCaseStatus() != TestCaseStatus.FAILED)
            campaign.setCurrentTestCaseStatus(TestCaseStatus.PASSED);

        String data = JsonApiHandler.createJSONForEndingOfTestCase(campaign.getCurrentTestCaseStatus().toString());
        log_debug.info("Data to be send to server: " + data);
        String testCaseId = String.valueOf(campaign.getCurrentTestCaseId());
        String campaignId = String.valueOf(campaign.getCampaignId());
        connection.sendPutRequest("testcases/" + campaignId + "/" + testCaseId + "/end", data);
        campaign.setCurrentTestCaseId(-1);
        campaign.setCurrentTestCaseStatus(TestCaseStatus.NOT_STARTED);
    }

    public void endTestCampaign() throws Exception {
        campaign.setCampaignStatus(CampaignStatus.FINISHED);
        String data = JsonApiHandler.createJSONForEndingOfTestCampaign(campaign.getCampaignStatus().toString());
        log_debug.info("Data to be send to server: " + data);
        String campaignId = String.valueOf(campaign.getCampaignId());
        connection.sendPutRequest("campaigns/" + campaignId, data);
    }
}
