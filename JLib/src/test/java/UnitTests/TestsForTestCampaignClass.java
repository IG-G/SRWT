package UnitTests;

import Enums.CampaignStatus;
import Enums.HttpMethod;
import JSON.JsonApiHandler;
import ReportTests.ConnectionClient;
import ReportTests.TestCampaign;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestsForTestCampaignClass {

    private ConnectionClient conn;
    private TestCampaign testCampaign;
    private final String campaignName = "unit test";
    private final String envName = "local";
    private final String url = "campaigns/";
    private final long dummyCampaignID = 123;

    @BeforeAll
    void setupConnection() {
        conn = mock(ConnectionClient.class);
    }

    @BeforeEach
    void setup() throws Exception {
        String data = JsonApiHandler.createJSONForNewTestCampaign(campaignName, envName);
        when(conn.sendRequest(HttpMethod.POST, url + "begin", data)).thenReturn(
                "{\"campaignName\":\"Test strony www\",\"envName\":\"CI pipeline #1\"," +
                        "\"id\":" + dummyCampaignID + " ,\"begTime\":\"2022-03-03T17:50:31\"," +
                        "\"endTime\":null,\"status\":\"RUNNING\"}"

        );
        data = JsonApiHandler.createJSONForEndingOfTestCampaign(CampaignStatus.FINISHED.toString());
        when(conn.sendRequest(HttpMethod.PUT, url + dummyCampaignID + "/end", data)).thenReturn(null);
        testCampaign = new TestCampaign(conn, campaignName, envName);
    }

    @Test
    void testCampaignShouldBeginAndEndCorrectly() throws Exception {
        testCampaign.beginTestCampaign();
        assertEquals(dummyCampaignID, testCampaign.getID());
        assertEquals(conn, testCampaign.getConnection());
        testCampaign.endTestCampaign(CampaignStatus.FINISHED);
        assertFalse(testCampaign.wasFailReported());
    }

    @Test
    void testCampaignShouldBeginAndReportErrorAndEndCorrectly() throws Exception {
        testCampaign.beginTestCampaign();
        testCampaign.setFailStatus();
        assertTrue(testCampaign.wasFailReported());
        testCampaign.endTestCampaign(CampaignStatus.FINISHED);
    }

    @Test
    void testCampaignShouldFailWhenEndedWithoutStart() {
        assertThrows(Exception.class, () -> testCampaign.endTestCampaign(CampaignStatus.FINISHED));
    }

    @Test
    void testCampaignShouldFailWhenStartedForTheSecondTime() throws Exception {
        testCampaign.beginTestCampaign();
        assertThrows(Exception.class, () -> testCampaign.beginTestCampaign());
    }
}
