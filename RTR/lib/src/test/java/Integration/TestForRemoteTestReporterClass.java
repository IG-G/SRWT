package Integration;

import RemoteTestReporter.RemoteTestReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertThrows;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestForRemoteTestReporterClass {

    private final String testCaseName = "dummy test case name";
    private RemoteTestReporter reporter;

    /**
     * Before all - run server manually for this tests to pass
     */

    @BeforeEach
    void setup() throws Exception {
        reporter = new RemoteTestReporter("src/test/java/Integration/conf.json");
    }

    @Test
    void testShouldPassWithTwoScreenshotsBeingMade() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase("Test first");
        reporter.log(Level.INFO, "Login successful");
        reporter.log(Level.FINEST, "Dummy message ");
        reporter.endTestCase();
        reporter.beginTestCase("Test second");
        reporter.log(Level.INFO, "Logout successful");
        reporter.log(Level.INFO, "Dummy message 2");
        reporter.takeScreenshot();
        reporter.reportFailure("test");
        reporter.takeScreenshot();
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenConfigFileDoesNotExists() {
        assertThrows(Exception.class, () -> reporter = new RemoteTestReporter("i_am_not_exisitng.json"));
    }

    @Test
    void testShouldFailWhenServerIsUnreachable() throws Exception {
        reporter = new RemoteTestReporter("src/test/java/Integration/conf_with_errors.json");
        assertThrows(Exception.class, () -> reporter.beginTestCampaign());
    }

    @Test
    void testShouldBeginAndEndCorrectly() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenTestCaseEndedWithoutBeginning() throws Exception {
        reporter.beginTestCampaign();
        assertThrows(Exception.class, () -> reporter.endTestCase());
    }

    @Test
    void testShouldFailWhenCampaignIsStartedTwice() throws Exception {
        reporter.beginTestCampaign();
        assertThrows(Exception.class, () -> reporter.beginTestCampaign());
    }

    @Test
    void testShouldFailWhenTestCaseIsStartedTwice() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        assertThrows(Exception.class, () -> reporter.beginTestCase(testCaseName));
    }

    @Test
    void testShouldFailWhenCampaignEndedWithoutBeginning() {
        assertThrows(Exception.class, () -> reporter.endTestCampaign());
    }

    @Test
    void testShouldFailWhenTestCaseBeginWithoutBeginningOfCampaign() {
        assertThrows(Exception.class, () -> reporter.beginTestCase(testCaseName));
    }

    @Test
    void testShouldBeginAndReportErrorCorrectlyWithoutEndingTestCase() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.reportFailure("Wrong format");
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledWithoutTestCase() throws Exception {
        reporter.beginTestCampaign();
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed"));
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledWithoutCampaign() {
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed"));
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledAfterTestCaseIsClosed() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed"));
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledAfterCampaignIsClosed() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        reporter.endTestCampaign();
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed"));
    }

    @Test
    void testShouldPassWhenLogsAreBeginCollected() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.log(Level.INFO, "Dummy message");
        reporter.log(Level.INFO, "Dummy message 2");
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenLogsAreLoggedWithoutTestCase() throws Exception {
        reporter.beginTestCampaign();
        assertThrows(Exception.class, () -> reporter.log(Level.FINE, "Should Fail"));
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        assertThrows(Exception.class, () -> reporter.log(Level.FINE, "Should Fail"));
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenLogsAreLoggedWithoutCampaign() throws Exception {
        assertThrows(Exception.class, () -> reporter.log(Level.FINE, "Should Fail"));
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        reporter.endTestCampaign();
        assertThrows(Exception.class, () -> reporter.log(Level.FINE, "Should Fail"));
    }

    @Test
    void testShouldPassWhenScreenshotIsTaken() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.takeScreenshot();
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenScreenshotIsTakenAfterEndOfTestCaseOrTestCampaign() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.takeScreenshot();
        reporter.endTestCase();
        assertThrows(Exception.class, () -> reporter.takeScreenshot());
        reporter.endTestCampaign();
        assertThrows(Exception.class, () -> reporter.takeScreenshot());
    }

    @Test
    void testShouldFailWhenScreenshotTakenBeforeBeginningOfTestCaseOrTestCampaign() throws Exception {
        assertThrows(Exception.class, () -> reporter.takeScreenshot());
        reporter.beginTestCampaign();
        assertThrows(Exception.class, () -> reporter.takeScreenshot());
        reporter.beginTestCase(testCaseName);
        reporter.takeScreenshot();
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

}
