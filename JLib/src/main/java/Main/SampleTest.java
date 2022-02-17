package Main;

import ReportTests.ReportTests;

public class SampleTest {
    public static void main(String[] args) throws Exception {
        ReportTests report = new ReportTests("conf/conf.json");
        report.beginTestCampaign();
        report.endTestCampaign();
    }
}
