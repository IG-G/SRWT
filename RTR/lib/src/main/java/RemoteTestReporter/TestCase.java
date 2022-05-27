package RemoteTestReporter;

import Enums.HttpMethod;
import Enums.TestCaseStatus;
import JSON.JsonApiHandler;

public class TestCase {
    private final String baseEndpoint = "testcases/";
    private final ConnectionClient connection;

    private final long campaignID;
    private long testCaseID;
    private TestCaseStatus status;

    public TestCase(ConnectionClient connection, long campaignID) {
        this.campaignID = campaignID;
        this.status = TestCaseStatus.NOT_STARTED;
        this.connection = connection;
    }

    public long getTestCaseID() {
        return testCaseID;
    }

    public void beginTestCase(String testCaseName) throws Exception {
        if (status != TestCaseStatus.NOT_STARTED)
            throw new Exception("Cannot start started test case");
        String data = JsonApiHandler.createJSONForNewTestCase(testCaseName);
        String response = connection.sendRequest(HttpMethod.POST, baseEndpoint + campaignID + "/begin", data);
        testCaseID = JsonApiHandler.getIDFromResponse(response, "testCaseID");
        status = TestCaseStatus.IN_PROGRESS;
    }

    public void endTestCase() throws Exception {
        if (status == TestCaseStatus.NOT_STARTED)
            throw new Exception("Cannot end test case that wasn't started");
        if (status != TestCaseStatus.FAILED)
            status = TestCaseStatus.PASSED;
        String data = JsonApiHandler.createJSONForEndingOfTestCase(status.toString());
        connection.sendRequest(HttpMethod.PUT, baseEndpoint + campaignID + "/" + testCaseID + "/end", data);
    }

    public void failTestCase(String message) throws Exception {
        String data = JsonApiHandler.createJSONForReportingFailure(message);
        connection.sendRequest(HttpMethod.POST, "testcases/" + campaignID + "/" + testCaseID + "/fail", data);
        status = TestCaseStatus.FAILED;
    }
}
