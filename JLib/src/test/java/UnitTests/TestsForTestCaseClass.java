package UnitTests;

import Enums.HttpMethod;
import Enums.TestCaseStatus;
import JSON.JsonApiHandler;
import ReportTests.ConnectionClient;
import ReportTests.TestCase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestsForTestCaseClass {

    private TestCase testCase;
    private ConnectionClient conn;
    long dummyCampaignID = 1;
    private final String testCaseName = "test";
    private final int testID = 2;

    @BeforeAll
    void setupConnection() {
        conn = mock(ConnectionClient.class);
    }

    @BeforeEach
    void setup() throws Exception {
        when(conn.sendRequest(HttpMethod.POST, "testcases/" + dummyCampaignID + "/begin",
                JsonApiHandler.createJSONForNewTestCase(testCaseName))
        ).thenReturn("{\"campaignName\":\"Test strony www\",\"envName\":\"CI pipeline #1\",\"id\": " + testID + "," +
                "\"begTime\":\"2022-02-24T16:01:56\",\"endTime\":null,\"status\":\"RUNNING\"}");
        testCase = new TestCase(conn, dummyCampaignID);
        when(conn.sendRequest(HttpMethod.PUT, "testcases/" + dummyCampaignID + "/end",
                JsonApiHandler.createJSONForEndingOfTestCase(TestCaseStatus.FAILED.toString()))
        ).thenReturn(null);
        when(conn.sendRequest(HttpMethod.PUT, "testcases/" + dummyCampaignID + "/end",
                JsonApiHandler.createJSONForEndingOfTestCase(TestCaseStatus.PASSED.toString()))
        ).thenReturn(null);
    }

    @Test
    void testCaseShouldBeginAndEndProperly() throws Exception {
        testCase.beginTestCase(testCaseName);
        assertEquals(testID, testCase.getTestCaseID());
        testCase.endTestCase();
    }

    @Test
    void testCaseShouldBeginAndEndProperlyAfterReportingFailure() throws Exception {
        testCase.beginTestCase(testCaseName);
        testCase.failTestCase("Stack overflow");
        testCase.endTestCase();
    }

    @Test
    void testCaseShouldFailWhenEndedWithoutStart() {
        assertThrows(Exception.class, () -> testCase.endTestCase());
    }

    @Test
    void testCaseShouldFailWhenStartedForTheSecondTime() throws Exception {
        testCase.beginTestCase(testCaseName);
        assertThrows(Exception.class, () -> testCase.beginTestCase(testCaseName));
    }
}
