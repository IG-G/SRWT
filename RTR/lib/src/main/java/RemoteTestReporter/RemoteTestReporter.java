package RemoteTestReporter;

import Enums.CampaignStatus;
import JSON.JsonConfigHandler;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteTestReporter {
    private final Logger log;

    private final TestCampaign campaign;
    private final boolean publishLogsLocally;
    private TestCase testCase;
    private LoggerHandler remoteLoggerHandler = null;
    private ScreenshotHandler screenshotHandler = null;


    public RemoteTestReporter(String confFile) throws Exception {
        JsonConfigHandler handler = new JsonConfigHandler(confFile);

        boolean enableLibraryDebugLogs = Boolean.parseBoolean(handler.getParamFromJSONConfig("enableDebugLogs"));
        log = setupLogger(enableLibraryDebugLogs);
        publishLogsLocally = Boolean.parseBoolean(handler.getParamFromJSONConfig("publishLogsLocally"));

        String baseURI = handler.getParamFromJSONConfig("baseURI");
        log.fine("Connection params - baseURI: " + baseURI);
        String username = handler.getParamFromJSONConfig("user");
        String password = handler.getParamFromJSONConfig("password");
        ConnectionClient connection = new ConnectionClient(baseURI, username, password);

        String campaignName = handler.getParamFromJSONConfig("campaignName");
        String envName = handler.getParamFromJSONConfig("testEnvironment");
        log.fine("Repository params - campaign name: " + campaignName + ", env name: " + envName);
        campaign = new TestCampaign(connection, campaignName, envName, username);
        testCase = null;
    }

    private Logger setupLogger(boolean enableDebugLogs) {
        Logger log = Logger.getLogger(RemoteTestReporter.class.getName());
        ConsoleHandler logHandler = new ConsoleHandler();
        if (enableDebugLogs) {
            logHandler.setLevel(Level.FINE);
            log.setLevel(Level.FINE);
        } else
            logHandler.setLevel(Level.INFO);
        log.addHandler(logHandler);
        return log;
    }

    public void beginTestCampaign() throws Exception {
        campaign.beginTestCampaign();
        log.fine("Successfully began test campaign");
        screenshotHandler = new ScreenshotHandler(campaign.getConnection(), campaign.getID());
    }

    public void beginTestCase(String testCaseName) throws Exception {
        if (testCase != null)
            throw new Exception("Cannot start second test case");
        testCase = new TestCase(campaign.getConnection(), campaign.getID());
        testCase.beginTestCase(testCaseName);
        log.fine("Successfully began test case " + testCaseName + " with id " + testCase.getTestCaseID());
        remoteLoggerHandler = new LoggerHandler(campaign.getConnection(), campaign.getID(), testCase.getTestCaseID());
    }

    public void reportFailure(String message) throws Exception {
        testCase.failTestCase(message);
        campaign.setFailStatus();
        log.fine("Successfully reported failure for test case " + testCase.getTestCaseID());
    }

    public void endTestCase() throws Exception {
        if (testCase == null)
            throw new Exception("Cannot end ended test case");
        testCase.endTestCase();
        log.fine("Successfully ended test case " + testCase.getTestCaseID());
        screenshotHandler.sendScreenshots(testCase.getTestCaseID());
        log.fine("Successfully sent screenshots to server");
        testCase = null;
        remoteLoggerHandler.sendLogsToServer();
        log.fine("Successfully sent logs to server");
        if (publishLogsLocally)
            remoteLoggerHandler.publishLogsLocally(log);
    }

    public void endTestCampaign() throws Exception {
        if (campaign.wasFailReported())
            campaign.endTestCampaign(CampaignStatus.FINISHED_WITH_FAILS);
        else
            campaign.endTestCampaign(CampaignStatus.FINISHED);
        screenshotHandler = null;
        log.fine("Successfully ended test campaign");
    }

    public void log(Level level, String message) throws Exception {
        if (testCase == null) {
            throw new Exception("Cannot log without test case");
        }
        if (remoteLoggerHandler == null) {
            throw new Exception("New logger has not been created yet");
        }
        remoteLoggerHandler.addLogElement(message, level);
    }

    public void takeScreenshot() throws Exception {
        if (testCase == null)
            throw new Exception("Cannot take screenshot without testcase");
        if (screenshotHandler == null) {
            throw new Exception("Cannot take screenshot without test campaign");
        }
        screenshotHandler.takeScreenshot(testCase.getTestCaseID());
    }

}
