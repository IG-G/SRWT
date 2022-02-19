package ReportTests;

public class TestCampaign {

    final private String repositoryName;
    final private String envName;
    private Status status;

    private long campaignId;

    public TestCampaign(String repositoryName, String envName) {
        this.repositoryName = repositoryName;
        this.envName = envName;
        this.status = Status.CREATED;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getCampaignId() {
        return this.campaignId;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public String getEnvName() {
        return this.envName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status newStatus) throws Exception {
        switch (newStatus) {
            case CREATED:
                throw new Exception("Cannot assign status created to already existing test campaign");
            case STARTED:
                if (status == Status.CREATED)
                    this.status = newStatus;
                else
                    throw new Exception("Cannot start test campaign from other status than CREATED");
                break;
            case PASSED:
            case FAILED:
                if (status == Status.STARTED)
                    this.status = newStatus;
                else
                    throw new Exception("Test campaign can be passed/failed only from status STARTED");
                break;
            default:
                throw new UnsupportedOperationException("Status: " + newStatus + " not implemented");
        }
    }

    public void endTestCampaign() {
        if (status == Status.STARTED) {
            System.out.println("Campaign with id: " + campaignId + " ended without reporting failure");
            status = Status.PASSED;
        }
    }
}
