package RemoteTestReporterExample;

import RemoteTestReporter.RemoteTestReporter;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.NoSuchElementException;
import java.util.logging.Level;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppTest {

    private final String baseUrl = "http://demo.guru99.com/test/newtours/";
    private RemoteTestReporter reporter;
    private WebDriver driver;

    @BeforeAll
    void init() throws Exception {
        reporter = new RemoteTestReporter("src/test/resources/conf.json");
        reporter.beginTestCampaign();

        System.setProperty("webdriver.gecko.driver", "src/test/resources/geckodriver.exe");
    }

    @BeforeEach
    void beginTestCase(TestInfo testInfo) throws Exception {
        reporter.beginTestCase(testInfo.getDisplayName());
        driver = new FirefoxDriver();
        driver.get(baseUrl);
    }

    @AfterEach
    void endTestCase() throws Exception {
        driver.close();
        reporter.endTestCase();
    }

    @AfterAll
    void tearDown() throws Exception {
        reporter.endTestCampaign();
    }

    @Test
    public void testTileOfThePage() throws Exception {
        String expectedTitle = "Welcome: Mercury Tours";
        String actualTitle = "";
        actualTitle = driver.getTitle();
        if (actualTitle.contentEquals(expectedTitle)) {
            reporter.log(Level.FINE, "Titles match");
        } else {
            reporter.takeScreenshot();
            reporter.reportFailure("Title doesn't match, got: " + actualTitle +
                    " but expected " + expectedTitle);
        }
    }

    @Test
    public void testSubmitButtonWorksCorrectly() throws Exception {
        try {
            reporter.log(Level.FINE, "Clicking on submit button");
            driver.findElement(By.name("submit")).submit();
            reporter.takeScreenshot();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            reporter.reportFailure("Submit button was not found on a page. Error: " + e.getMessage());
            reporter.takeScreenshot();
            throw e;
        }
        reporter.log(Level.FINE, "Successfully clicked on submit button");
        try {
            new WebDriverWait(driver, 10)
                    .until(ExpectedConditions.titleIs("Login: Mercury Tours"));
        } catch (TimeoutException e) {
            reporter.reportFailure("Page after login has not shown in 10 seconds");
            throw e;
        }
        reporter.log(Level.FINE, "Successfully loaded login page");
        try {
            String text = driver.findElement(By.tagName("h3")).getText();
            if (text.equals("Login Successfully")) {
                reporter.log(Level.FINER, "Correct message is shown on login screen");
            } else {
                reporter.reportFailure("Incorrect message is shown on login screen. Message received: " + text);
            }
        } catch (NoSuchElementException e) {
            reporter.reportFailure("H3 tag is missing - could not read login message. Error: " + e.getMessage());
            throw e;
        }
        reporter.log(Level.FINE, "Successfully ended test case");
    }
}
