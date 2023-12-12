package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java8.En;
import models.Memory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.PageUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Set;

import static java.lang.String.format;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofMinutes;
import static models.Memory.forgetAll;
import static models.Memory.setValue;
import static org.openqa.selenium.OutputType.FILE;
import static utils.SeleniumConfig.driver;

public class UtilitySteps implements En {

    public UtilitySteps() {

        When("I take and screenshot with the filename {string}", UtilitySteps::takeScreenshot);

        When("I switch to the other tab", () -> {
            String currentHandle = driver().getWindowHandle();
            Set<String> handles = driver().getWindowHandles();
            for (String actual : handles) {
                if (!actual.equalsIgnoreCase(currentHandle)) {
                    driver().switchTo().window(actual);
                    break;
                }
            }
        });

        When("I wait for {int} seconds", (Integer seconds) -> Thread.sleep(seconds * 1000));

        When("I create a random string called {string}", Memory::generateAndSetRandomValue);

        When("I set current time to CurrentDateTime", () -> {
            Instant now = PageUtil.now();
            setValue("CurrentDateTime", now);
            setValue("CurrentDate", now);
            setValue("CurrentTime", now);
            setValue("CurrentDateTime+1H", now.plus(ofHours(1)));
            setValue("CurrentTime+1H", now.plus(ofHours(1)));
            setValue("CurrentTime+1M", now.plus(ofMinutes(1)));
            setValue("CurrentTime+2M", now.plus(ofMinutes(2)));
            setValue("CurrentTime+3M", now.plus(ofMinutes(3)));
        });
    }

    public static void takeScreenshot(String filename) {
        TakesScreenshot screenshot = ((TakesScreenshot) driver());
        File screenshotFile = screenshot.getScreenshotAs(FILE);
        File savedFile = new File(format("%s/test-reports/screenshots/%s", System.getProperty("user.dir"), filename));
        try {
            FileUtils.copyFile(screenshotFile, savedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePageSource(String filename) {
        File savedFile = new File(format("%s/target/screenshots/%s", System.getProperty("user.dir"), filename));
        try {
            FileWriter writer = new FileWriter(savedFile);
            writer.write(driver().getPageSource().toLowerCase());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void ResetMemory() {
        forgetAll();
    }

    @After()
    public void takeScreenShotsOnStepFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BYTES);
            // Attach screenshot to the report
            scenario.attach(screenshot, "image/png", "Screenshot");
        }
    }

}
