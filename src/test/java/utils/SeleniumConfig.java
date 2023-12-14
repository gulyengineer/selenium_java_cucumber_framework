package utils;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.io.File.separator;

public class SeleniumConfig {

    private static final List<WebDriver> drivers = new ArrayList<>();
    private static int activeDriverIndex = 0;
    public static String DOWNLOAD_PATH = System.getProperty("user.dir") + separator + "externalFiles" + separator + "downloadFiles";
    public static Scenario scenario;

    public static WebDriver driver() {
        if (drivers.isEmpty()) {
            newBrowser();
        }
        return drivers.get(activeDriverIndex);
    }

    public static WebDriver getLocalDriver() {

        WebDriver newDriver = null;
        if (getBrowser().equals("CHROME")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            if (Boolean.parseBoolean(System.getProperty("headless"))) {
                options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--remote-allow-origins=*");
                DesiredCapabilities dc = new DesiredCapabilities();
                dc.setCapability("screen-resolution", "1920x1080");
            }
            newDriver = new ChromeDriver(options);

        }
        if (getBrowser().equals("FIREFOX")) {
            FirefoxOptions options = new FirefoxOptions();
            options.setBinary("C:\\Program Files\\Mozilla Firefox/firefox.exe");

            if (Boolean.parseBoolean(System.getProperty("headless"))) {
                options.addArguments("--headless");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--window-size=1920,1080");
                DesiredCapabilities dc = new DesiredCapabilities();
                dc.setCapability("screen-resolution", "1920x1080");
            }
            newDriver = new FirefoxDriver(options);
        }

        if (getBrowser().equals("EDGE")) {
            EdgeOptions options = new EdgeOptions();

            if (Boolean.parseBoolean(System.getProperty("headless"))) {
                options.addArguments("--headless");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--window-size=1920,1080");
                DesiredCapabilities dc = new DesiredCapabilities();
                dc.setCapability("screen-resolution", "1920x1080");
            }
            newDriver = new EdgeDriver(options);
        }

        if (getBrowser().equals("SAFARI")) {
            SafariOptions options = new SafariOptions();
            newDriver = new SafariDriver(options);
        }
        addShutdownHook(newDriver);
        assert newDriver != null;
        newDriver.manage().window().maximize();
        return newDriver;
    }

    @After("@delete")
    public void delete() {

        if (scenario.isFailed()) {
            System.out.println("Scenario failed. Did not delete!");
        }
        // Perform any cleanup actions after the scenario
    }

    @Before
    public void before(Scenario scenario) {
        SeleniumConfig.scenario = scenario;

        emptyDownloadFolder();
        drivers.forEach(WebDriver::quit);
        drivers.clear();
    }

    private static Object[] winHandles() {
        return driver().getWindowHandles().toArray();
    }

    private static void switchToFirstWindow() {
        switchToWindow(1);
    }

    public static void switchToLastWindow() {
        switchToWindow(winHandles().length);
    }

    public static void switchToWindow(int windowIndex) {
        driver().switchTo().window(winHandles()[windowIndex - 1].toString());
    }

    public static void switchToBrowser(int browserIndex) {
        if (browserIndex > drivers.size()) {
            newBrowser();
            return;
        }

        activeDriverIndex = browserIndex - 1;
    }

    public static void newBrowser() {
        drivers.add(getLocalDriver());
        activeDriverIndex = drivers.size() - 1;
    }

    private void emptyDownloadFolder() {
        File downloadFolder = new File(DOWNLOAD_PATH);
        if (downloadFolder.exists()) {
            for (File file : Objects.requireNonNull(downloadFolder.listFiles())) {
                file.delete();
            }
        }
    }

    private static void addShutdownHook(WebDriver driver) {
        Runtime.getRuntime().addShutdownHook(new Thread("Driver shutdown thread") {
            public void run() {
                driver.quit();
            }
        });
    }

    public static String getBrowser() {
        return System.getProperty("browser") != null ? System.getProperty("browser") : "CHROME";
    }

}
