package ReportTests;

import Enums.CampaignStatus;
import JSON.JsonConfigHandler;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RemoteTestReporter {
    private final Logger log;

    private final TestCampaign campaign;
    private TestCase testCase;
    private LoggerHandler remoteLoggerHandler = null;
    private final boolean publishLogsLocally;


    private Logger setupLogger(boolean enableDebugLogs) {
        Logger log = Logger.getLogger(RemoteTestReporter.class.getName());
        ConsoleHandler logHandler = new ConsoleHandler();
        if (enableDebugLogs) {
            logHandler.setLevel(Level.FINE);
            log.setLevel(Level.FINE);
        }
        else
            logHandler.setLevel(Level.INFO);
        log.addHandler(logHandler);
        return log;
    }

    public RemoteTestReporter(String confFile) throws Exception {
        JsonConfigHandler handler = new JsonConfigHandler(confFile);

        boolean enableLibraryDebugLogs = Boolean.parseBoolean(handler.getParamFromJSONConfig("enableDebugLogs"));
        log = setupLogger(enableLibraryDebugLogs);
        publishLogsLocally = Boolean.parseBoolean(handler.getParamFromJSONConfig("publishLogsLocally"));

        String baseURI = handler.getParamFromJSONConfig("baseURI");
        String timeout = handler.getParamFromJSONConfig("timeout");
        log.fine("Connection params - baseURI: " + baseURI + ", timeout: " + timeout);
        ConnectionClient connection = new ConnectionClient(baseURI, Integer.parseInt(timeout));

        String campaignName = handler.getParamFromJSONConfig("campaignName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        log.fine("Repository params - campaign name: " + campaignName + ", env name: " + envName);
        campaign = new TestCampaign(connection, campaignName, envName);
        testCase = null;
    }

    public void beginTestCampaign() throws Exception {
        campaign.beginTestCampaign();
        log.fine("Successfully began test campaign");
    }

    public void beginTestCase(String testCaseName) throws Exception {
        if (testCase != null)
            throw new Exception("Cannot start second test case");
        testCase = new TestCase(campaign.getConnection(), campaign.getID());
        testCase.beginTestCase(testCaseName);
        log.fine("Successfully began test case " + testCaseName + " with id " + testCase.getTestCaseID());
        remoteLoggerHandler = new LoggerHandler(campaign.getConnection(), testCase.getTestCaseID());
    }

    public void reportFailure(String message, Boolean shouldEndTestCase) throws Exception {
        testCase.failTestCase(message);
        campaign.setFailStatus();
        log.fine("Successfully reported failure for test case " + testCase.getTestCaseID());
        if (shouldEndTestCase)
            endTestCase();
    }

    public void endTestCase() throws Exception {
        if (testCase == null)
            throw new Exception("Cannot end ended test case");
        testCase.endTestCase();
        log.fine("Successfully ended test case " + testCase.getTestCaseID());
        testCase = null;
        remoteLoggerHandler.sendLogsToServer();
        log.fine("Successfully sent logs to server");
        if(publishLogsLocally)
            remoteLoggerHandler.publishLogsLocally(log);
    }

    public void endTestCampaign() throws Exception {
        if (campaign.wasFailReported())
            campaign.endTestCampaign(CampaignStatus.FINISHED_WITH_FAILS);
        else
            campaign.endTestCampaign(CampaignStatus.FINISHED);
        log.fine("Successfully ended test campaign");
    }

    public void log(String message, Level level) throws Exception {
        if(testCase == null) {
            throw new Exception("Cannot log without test case");
        }
        if(remoteLoggerHandler == null) {
            throw new Exception("New logger has not been created yet");
        }
        remoteLoggerHandler.addLogElement(message, level);
    }
}
