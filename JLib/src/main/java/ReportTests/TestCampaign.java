package ReportTests;

import Enums.CampaignStatus;
import Enums.TestCaseStatus;

import java.util.logging.Logger;

public class TestCampaign {
    private final Logger log;
    final private String campaignName;
    final private String envName;

    private CampaignStatus campaignStatus;
    private long campaignId;

    private long currentTestCaseId;
    private TestCaseStatus currentTestCaseCampaignStatus;

    public TestCampaign(String campaignName, String envName) {
        log = Logger.getLogger(TestCampaign.class.getName());
        this.campaignName = campaignName;
        this.envName = envName;
        this.campaignStatus = CampaignStatus.CREATED;
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

    public CampaignStatus getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(CampaignStatus newCampaignStatus) {
        log.info("New status to be set:" + newCampaignStatus);
        campaignStatus = newCampaignStatus;
    }

    public void setCurrentTestCaseId(long currentTestCaseId) {
        this.currentTestCaseId = currentTestCaseId;
    }

    public long getCurrentTestCaseId() {
        return this.currentTestCaseId;
    }

    public TestCaseStatus getCurrentTestCaseStatus() {
        return this.currentTestCaseCampaignStatus;
    }

    public void setCurrentTestCaseStatus(TestCaseStatus newCampaignStatus) {
        log.info("New status to be set:" + newCampaignStatus);
        currentTestCaseCampaignStatus = newCampaignStatus;
    }

    /*
    private Boolean validateStatusChange(CampaignStatus newCampaignStatus) throws Exception {
        //TODO popraw
        switch (newCampaignStatus) {
            case CREATED:
                throw new Exception("Cannot assign status created to already existing test campaign");
            case STARTED:
                if (campaignStatus == CampaignStatus.CREATED)
                    return true;
                else
                    throw new Exception("Cannot start test campaign from other status than CREATED");
            case PASSED:
            case FAILED:
                if (campaignStatus == CampaignStatus.STARTED)
                    return true;
                else
                    throw new Exception("Test campaign can be passed/failed only from status STARTED");
            default:
                throw new UnsupportedOperationException("CampaignStatus: " + newCampaignStatus + " not implemented");
        }
    }*/
}
