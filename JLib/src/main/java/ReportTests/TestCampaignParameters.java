package ReportTests;

public class TestCampaign {

    final private String repositoryName;
    final private String envName;

    private int campaignId;

    public TestCampaign(String repositoryName, String envName) {
        this.repositoryName = repositoryName;
        this.envName = envName;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getCampaignId() {
        return this.campaignId;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public String getEnvName() {
        return this.envName;
    }
}
