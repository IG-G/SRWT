package Main;

import ReportTests.RemoteTestReporter;

import java.util.logging.Level;

public class SampleTest {
    public static void main(String[] args) throws Exception {
        RemoteTestReporter report = new RemoteTestReporter("conf/conf.json");
        report.beginTestCampaign();
        report.beginTestCase("Test 1");
        report.log(Level.INFO, "Poczatek");
        report.log(Level.SEVERE, "Brak danych");
        report.log(Level.SEVERE, "Brak danych2");
        report.reportFailure("To sie nie udalo", false);
        report.takeScreenshot();
        report.endTestCase();
        //report.beginTestCase("test 2");
        //report.endTestCase();
        report.endTestCampaign();
    }
}
