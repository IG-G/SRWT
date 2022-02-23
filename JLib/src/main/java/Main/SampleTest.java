package Main;

import ReportTests.RemoteTestReporter;

public class SampleTest {
    public static void main(String[] args) throws Exception {
        RemoteTestReporter report = new RemoteTestReporter("conf/conf.json");
        report.beginTestCampaign();
        long testCaseID = report.beginTestCase("Test 1");
        report.reportFailure(testCaseID, "To sie nie udalo", false);
        report.endTestCase(testCaseID);
        testCaseID = report.beginTestCase("test 2");
        report.endTestCase(testCaseID);
        report.endTestCampaign();
    }
}
