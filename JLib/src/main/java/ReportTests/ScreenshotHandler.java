package ReportTests;

import Enums.HttpMethod;
import JSON.JsonApiHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class ScreenshotHandler {
    private final ConnectionClient connection;

    private final long campaignID;
    private final ArrayList<ScreenshotElement> screenshotElements;
    private final String baseEndpoint = "screenshots/";

    static class ScreenshotElement {
        private final LocalDateTime dateTime;
        private final String path;
        private final long testCaseID;

        ScreenshotElement(String path, long testCaseID) {
            this.dateTime = LocalDateTime.now();
            this.path = path;
            this.testCaseID = testCaseID;
        }

        public String createJSONFromScreenshotElement() {
            return JsonApiHandler.createJSONObjectForSendingScreenshots(path, dateTime.toString());
        }

        public long getTestCaseID() {
            return testCaseID;
        }

        public void deleteLocalFile() {
            //TODO remove file stored under path var
        }
    }

    private String generateFileName(long testCaseID) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return "s_" + timeStamp + "_" + campaignID + "_" + testCaseID;
    }

    public ScreenshotHandler(ConnectionClient connection, long campaignID) {
        this.connection = connection;
        this.campaignID = campaignID;
        screenshotElements = new ArrayList<>();
    }

    public void takeScreenshot(long testCaseID) throws Exception {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            String fileName = generateFileName(testCaseID);
            ImageIO.write(capture, "bmp", new File(fileName));
            screenshotElements.add(new ScreenshotElement(fileName + ".bmp", testCaseID));
        } catch (AWTException | IOException e) {
            throw new Exception("Excpetion occured when taking screenshot: " + e.getMessage());
        }
    }

    public void sendScreenshots(long testCaseID) throws Exception {
        ArrayList<ScreenshotElement> toRemove = new ArrayList<>();
        for (ScreenshotElement el : screenshotElements) {
            if (testCaseID == el.getTestCaseID()) {
                String data = el.createJSONFromScreenshotElement();
                connection.sendRequest(HttpMethod.POST, baseEndpoint + campaignID + "/" + el.getTestCaseID() + "/add", data);
                el.deleteLocalFile();
                toRemove.add(el);
            }
        }
        screenshotElements.removeAll(toRemove);
    }
}
