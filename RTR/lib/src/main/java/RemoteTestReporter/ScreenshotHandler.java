package RemoteTestReporter;

import Enums.HttpMethod;
import JSON.JsonApiHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ScreenshotHandler {
    private final ConnectionClient connection;

    private final long campaignID;
    private final ArrayList<ScreenshotElement> screenshotElements;
    private final String baseEndpoint = "screenshots/";
    private int screenshotCount = 0;

    public ScreenshotHandler(ConnectionClient connection, long campaignID) {
        this.connection = connection;
        this.campaignID = campaignID;
        screenshotElements = new ArrayList<>();
    }

    private String generateFileName() {
        screenshotCount += 1;
        return "s_" + screenshotCount + ".bmp";
    }

    public void takeScreenshot(long testCaseID) throws Exception {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            String fileName = generateFileName();
            ImageIO.write(capture, "bmp", new File(fileName));
            screenshotElements.add(new ScreenshotElement(fileName, testCaseID));
        } catch (AWTException | IOException e) {
            throw new Exception("Exception occurred when taking screenshot: " + e.getMessage());
        }
    }

    public void sendScreenshots(long testCaseID) throws Exception {
        ArrayList<ScreenshotElement> toRemove = new ArrayList<>();
        for (ScreenshotElement el : screenshotElements) {
            if (testCaseID == el.getTestCaseID()) {
                el.sendScreenshot();
                toRemove.add(el);
            }
        }
        for (ScreenshotElement e : toRemove) {
            e.deleteLocalFile();
        }
        screenshotElements.removeAll(toRemove);
    }

    class ScreenshotElement {
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

        public void deleteLocalFile() throws IOException {
            Files.delete(Paths.get(path));
        }

        public void sendScreenshot() throws Exception {
            String data = createJSONFromScreenshotElement();
            String response = connection.sendRequest(HttpMethod.POST,
                    baseEndpoint + campaignID + "/" + getTestCaseID() + "/add", data);
            long screenshotID = JsonApiHandler.getIDFromResponse(response, "screenshotID");
            connection.sendImage(baseEndpoint + screenshotID + "/", path);
        }
    }
}
