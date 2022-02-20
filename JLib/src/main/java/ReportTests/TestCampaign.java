package ReportTests;

import java.util.logging.Logger;

public class TestCampaign {
    private final Logger log;
    final private String campaignName;
    final private String envName;

    private Status campaignStatus;
    private long campaignId;

    private long currentTestCaseId;
    private Status currentTestCaseStatus;

    public TestCampaign(String campaignName, String envName) {
        log = Logger.getLogger(TestCampaign.class.getName());
        this.campaignName = campaignName;
        this.envName = envName;
        this.campaignStatus = Status.CREATED;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getCampaignId() {
        return this.campaignId;
    }

    public String getCampaignName() {
        return this.campaignName;
    }

    public String getEnvName() {
        return this.envName;
    }

    public Status getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(Status newStatus) throws Exception {
        log.info("New status to be set:" + newStatus);
        if (validateStatusChange(newStatus))
            campaignStatus = newStatus;
    }

    public void setCurrentTestCaseId(long currentTestCaseId) {
        this.currentTestCaseId = currentTestCaseId;
    }

    public long getCurrentTestCaseId() {
        return this.currentTestCaseId;
    }

    public Status getCurrentTestCaseStatus() {
        return this.currentTestCaseStatus;
    }

    public void setCurrentTestCaseStatus(Status newStatus) {
        log.info("New status to be set:" + newStatus);
        currentTestCaseStatus = newStatus;
    }

    private Boolean validateStatusChange(Status newStatus) throws Exception {
        //TODO popraw
        switch (newStatus) {
            case CREATED:
                throw new Exception("Cannot assign status created to already existing test campaign");
            case STARTED:
                if (campaignStatus == Status.CREATED)
                    return true;
                else
                    throw new Exception("Cannot start test campaign from other status than CREATED");
            case PASSED:
            case FAILED:
                if (campaignStatus == Status.STARTED)
                    return true;
                else
                    throw new Exception("Test campaign can be passed/failed only from status STARTED");
            default:
                throw new UnsupportedOperationException("Status: " + newStatus + " not implemented");
        }
    }
}
