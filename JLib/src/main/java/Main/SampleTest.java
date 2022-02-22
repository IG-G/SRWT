package Main;

import ReportTests.ReportTests;

public class SampleTest {
    public static void main(String[] args) throws Exception {
        ReportTests report = new ReportTests("conf/conf.json");
        report.beginTestCampaign();
        report.beginTestCase("Test 1");
        report.reportFailure("To sie nie udalo", false);
        report.endTestCase();
        report.beginTestCase("test 2");
        report.endTestCase();
        report.endTestCampaign();
    }
}
