import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;

import ReportTests.RemoteTestReporter;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SampleTest {

    private RemoteTestReporter reporter;

    @BeforeAll
    void init() throws Exception {
        reporter = new RemoteTestReporter("conf/conf.json");
        reporter.beginTestCampaign();
    }

    @BeforeEach
    void beginTestCase(TestInfo testInfo) throws Exception {
        reporter.beginTestCase(testInfo.getDisplayName());
    }

    @AfterEach
    void endTestCase() throws Exception {
        reporter.endTestCase();
    }

    @AfterAll
    void tearDown() throws Exception {
        reporter.endTestCampaign();
    }

    @Test
    void addition() {
        assertEquals(2, 1 + 1);
    }

    @Test
    void subtraction() {
        assertEquals(3, 4 - 1);
    }

    @Test
    void multiply() {
        assertEquals(10, 5 * 2);
    }

    @Test
    void divide() throws Exception {
        try {
            assertEquals(1, 3 - 4);
        }
        catch (AssertionError err) {
            reporter.reportFailure(err.getMessage(), false);
            throw err;
        }
    }
}
