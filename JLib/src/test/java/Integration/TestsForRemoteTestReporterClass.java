package Integration;

import ReportTests.RemoteTestReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertThrows;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestsForRemoteTestReporterClass {

    private RemoteTestReporter reporter;
    private final String testCaseName = "dummy test case name";

    /**
     * Before all - run server manually for this tests to pass
     */

    @BeforeEach
    void setup() throws Exception {
        reporter = new RemoteTestReporter("src/test/java/Integration/conf.json");
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
        reporter.reportFailure("Wrong format", false);
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldBeginAndReportErrorCorrectlyWithEndingTestCase() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.reportFailure("Wrong format", true);
        assertThrows(Exception.class, () -> reporter.endTestCase());
        reporter.beginTestCase(testCaseName + "2");
        reporter.endTestCase();
        reporter.endTestCampaign();
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledWithoutTestCase() throws Exception {
        reporter.beginTestCampaign();
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed", false));
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledWithoutCampaign() {
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed", false));
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledAfterTestCaseIsClosed() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed", false));
    }

    @Test
    void testShouldFailWhenReportFailureIsCalledAfterCampaignIsClosed() throws Exception {
        reporter.beginTestCampaign();
        reporter.beginTestCase(testCaseName);
        reporter.endTestCase();
        reporter.endTestCampaign();
        assertThrows(Exception.class, () -> reporter.reportFailure("Failed", true));
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
}
