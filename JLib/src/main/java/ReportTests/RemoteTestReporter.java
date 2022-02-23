package ReportTests;

import Enums.CampaignStatus;
import JSON.JsonConfigHandler;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class RemoteTestReporter {

    private final TestCampaign campaign;
    private final ArrayList<TestCase> testCases;
    private final Logger log;

    private TestCase getTestCaseByID(long testCaseID) {
        for (TestCase testCase : testCases) {
            if (testCase.getTestCaseID() == testCaseID)
                return testCase;
        }
        throw new NoSuchElementException("Cannot find test case with id " + testCaseID);
    }

    private void removeTestCase(long testCaseID) {
        if (!testCases.removeIf(testCase -> testCase.getTestCaseID() == testCaseID))
            throw new NoSuchElementException("Cannot find test case with id " + testCaseID);
    }

    public RemoteTestReporter(String confFile) throws Exception {
        log = Logger.getLogger(RemoteTestReporter.class.getName());
        log.addHandler(new FileHandler("lib.log"));

        testCases = new ArrayList<>();

        JsonConfigHandler handler = new JsonConfigHandler(confFile);
        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        log.info("Connection params - baseURI: " + baseURI + ", timeout: " + timeout);
        ConnectionClient connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        String campaignName = handler.getParamFromJSONConfig("campaignName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        log.info("Repository params - campaign name: " + campaignName + ", env name: " + envName);
        campaign = new TestCampaign(connection, campaignName, envName);
    }

    public void beginTestCampaign() throws Exception {
        campaign.beginTestCampaign();
        log.info("Successfully began test campaign");
    }

    public long beginTestCase(String testCaseName) throws Exception {
        TestCase newTestCase = new TestCase(campaign.getConnection(), campaign.getID());
        long testCaseID = newTestCase.beginTestCase(testCaseName);
        testCases.add(newTestCase);
        log.info("Successfully began test case " + testCaseName + " with id " + testCaseID);
        return testCaseID;
    }

    public void reportFailure(long testCaseID, String message, Boolean shouldEndTestCase) throws Exception {
        getTestCaseByID(testCaseID).failTestCase(message);
        campaign.setFailStatus();
        log.info("Successfully reported failure for test case " + testCaseID);
        if (shouldEndTestCase)
            endTestCase(testCaseID);
    }

    public void endTestCase(long testCaseID) throws Exception {
        getTestCaseByID(testCaseID).endTestCase();
        removeTestCase(testCaseID);
        log.info("Successfully ended test case " + testCaseID);
    }

    public void endTestCampaign() throws Exception {
        if (campaign.wasFailReported())
            campaign.endTestCampaign(CampaignStatus.FINISHED_WITH_FAILS);
        else
            campaign.endTestCampaign(CampaignStatus.FINISHED);
        log.info("Successfully ended test campaign");
    }
}
