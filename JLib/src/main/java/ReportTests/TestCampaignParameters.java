package ReportTests;

public class TestCampaignParameters {

    final private String repositoryName;
    final private String envName;

    private long campaignId;

    public TestCampaignParameters(String repositoryName, String envName) {
        this.repositoryName = repositoryName;
        this.envName = envName;
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
}
