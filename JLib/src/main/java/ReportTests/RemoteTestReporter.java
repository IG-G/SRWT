package ReportTests;

import Enums.CampaignStatus;
import JSON.JsonConfigHandler;

import java.util.logging.Logger;

public class RemoteTestReporter {
    private final Logger log;

    private final TestCampaign campaign;
    private TestCase testCase;

    public RemoteTestReporter(String confFile) throws Exception {
        log = Logger.getLogger(RemoteTestReporter.class.getName());

        JsonConfigHandler handler = new JsonConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        log.info("Connection params - baseURI: " + baseURI + ", timeout: " + timeout);
        ConnectionClient connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        String campaignName = handler.getParamFromJSONConfig("campaignName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        log.info("Repository params - campaign name: " + campaignName + ", env name: " + envName);
        campaign = new TestCampaign(connection, campaignName, envName);
        testCase = null;
    }

    public void beginTestCampaign() throws Exception {
        campaign.beginTestCampaign();
        log.info("Successfully began test campaign");
    }

    public void beginTestCase(String testCaseName) throws Exception {
        if (testCase != null)
            throw new Exception("Cannot start second test case");
        testCase = new TestCase(campaign.getConnection(), campaign.getID());
        testCase.beginTestCase(testCaseName);
        log.info("Successfully began test case " + testCaseName + " with id " + testCase.getTestCaseID());
    }

    public void reportFailure(String message, Boolean shouldEndTestCase) throws Exception {
        testCase.failTestCase(message);
        campaign.setFailStatus();
        log.info("Successfully reported failure for test case " + testCase.getTestCaseID());
        if (shouldEndTestCase)
            endTestCase();
    }

    public void endTestCase() throws Exception {
        if (testCase == null)
            throw new Exception("Cannot end ended test case");
        testCase.endTestCase();
        log.info("Successfully ended test case " + testCase.getTestCaseID());
        testCase = null;
    }

    public void endTestCampaign() throws Exception {
        if (campaign.wasFailReported())
            campaign.endTestCampaign(CampaignStatus.FINISHED_WITH_FAILS);
        else
            campaign.endTestCampaign(CampaignStatus.FINISHED);
        log.info("Successfully ended test campaign");
    }
}
