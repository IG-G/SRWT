import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

import ReportTests.RemoteTestReporter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SampleTest {

    private RemoteTestReporter reporter;
    private final Iterator<Method> methodIterator = Arrays.stream(SampleTest.class.getMethods()).iterator();
    private long testCaseID;

    @BeforeAll
    void init() throws Exception {
        reporter = new RemoteTestReporter("conf/conf.json");
        reporter.beginTestCampaign();
    }

    @BeforeEach
    void beginTestCase() throws Exception {
        testCaseID = reporter.beginTestCase(methodIterator.next().toString().substring(0, 20));
    }

    @AfterEach
    void endTestCase() throws Exception {
        reporter.endTestCase(testCaseID);
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
            reporter.reportFailure(testCaseID, err.getMessage(), false);
            throw err;
        }
    }
}
