package utils;

import models.Memory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

import static java.time.Duration.ofSeconds;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;
import static stepdefinitions.UtilitySteps.savePageSource;
import static stepdefinitions.UtilitySteps.takeScreenshot;
import static utils.SeleniumConfig.driver;

public class PageUtil {
    public static final ZoneId zoneId = ZoneId.of("Europe/London");

    private static String dateFormatPattern() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (SeleniumConfig.getBrowser().equals("FIREFOX")) {
            return "yyyy-MM-dd";
        }

        if (osName.contains("win")) {
            return "dd/MM/yyyy";
        }

        if (osName.contains("linux")) {
            return "MM/dd/yyyy";
        }
        throw new RuntimeException("OS not supported");
    }

    public static DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern(dateFormatPattern()).withZone(zoneId);
    }

    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(zoneId);
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:00").withZone(zoneId);
    public static final long timeOutForWait = 90;
    public static final Duration timeOutForWaitInSeconds = ofSeconds(timeOutForWait);
    public static final Duration shorTimeOutForWaitInSeconds = ofSeconds(10);
    public static final Duration extendedTimeOutForWaitInSeconds = ofSeconds(500);

    public static Instant now() {
        return Instant.from(dateTimeFormatter.parse(Instant.now().atZone(zoneId).format(dateTimeFormatter)));
    }

    public static int minuteOfHour(Instant instant) {
        return instant.atZone(zoneId).get(MINUTE_OF_HOUR);
    }

    public static int hourOfDay(Instant instant) {
        return instant.atZone(zoneId).get(HOUR_OF_DAY);
    }

    public static List<WebElement> filterVisible(List<WebElement> webElements) {
        return webElements.stream().filter(WebElement::isDisplayed).collect(toList());
    }

    public static SearchContext shadowRootOf(SearchContext root, By locator) {
        return findElement(root, locator).getShadowRoot();
    }

    public static SearchContext shadowRootOf(By locator) {
        return shadowRootOf(driver(), locator);
    }

    public static WebElement findVisibleElement(By locator) {
        List<WebElement> webElements = findVisibleElements(locator);
        if (webElements.size() == 1) {
            return webElements.get(0);
        }
        throw new RuntimeException("No or multiple element found with :" + locator);
    }

    public static WebElement findElement(By locator) {
        return findElement(driver(), locator);
    }

    public static WebElement findNthElement(By locator, Integer indexOfItem) {
        return findElements(locator).get(indexOfItem - 1);
    }

    public static WebElement findElement(SearchContext root, By locator) {
        return root.findElement(locator);
    }

    public static void click(By locator) {
        findElement(locator).click();
    }

    public static void clickViaJavascriptExecutor(By locator) {
        clickViaJavascriptExecutor(driver(), locator);
    }

    public static void clickViaJavascriptExecutor(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click()", element);
    }

    public static void clickViaJavascriptExecutor(SearchContext root, By locator) {
        clickViaJavascriptExecutor(root.findElement(locator));
    }

    public static void scrollIntoViewViaJavascriptExecutor(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView();", element);
    }

    public static void waitForPageLoad() {
        ((JavascriptExecutor) driver()).executeScript("return document.readyState").toString().equals("complete");
    }

    public static boolean elementExists(By locator) {
        return !elementDoesNotExist(locator);
    }

    public static boolean elementDoesNotExist(By locator) {
        return filterVisible(findElements(locator)).isEmpty();
    }

    public static List<WebElement> findVisibleElements(By locator) {
        return filterVisible(findElements(locator));
    }

    public static List<WebElement> findElements(By locator) {
        return driver().findElements(locator);
    }

    public static void waitFor(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForInvisibilityOfElementLocated(By locator) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.invisibilityOfElementLocated((locator)));
    }

    public static void extendedWaitForInvisibilityOfElementLocated(By locator) {
        new WebDriverWait(driver(), extendedTimeOutForWaitInSeconds).until(ExpectedConditions.invisibilityOfElementLocated((locator)));
    }

    public static void extendedWaitForVisibilityOfElementLocated(By locator) {
        new WebDriverWait(driver(), extendedTimeOutForWaitInSeconds).until(ExpectedConditions.visibilityOfElementLocated((locator)));
    }

    public static WebElement waitForVisibilityOfElementLocated(By locator) {
        return new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.visibilityOfElementLocated((locator)));
    }

    public static void waitForPresenceOfElementLocated(SearchContext root, By locator) {
        new SearchContextWait(root, timeOutForWaitInSeconds).until(CustomExpectedConditions.presenceOfElementLocated(locator));
    }

    public static void waitForVisibilityOfElementLocated(SearchContext root, By locator) {
        new SearchContextWait(root, timeOutForWaitInSeconds).until(CustomExpectedConditions.presenceOfElementLocated(locator));
    }

    public static void waitForElementToBeClickable(SearchContext root, By locator) {
        new SearchContextWait(root, timeOutForWaitInSeconds).until(CustomExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForAttributeToContain(By locator, String attributeName, String value) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.attributeContains(locator, attributeName, value));
    }

    public static void waitForAttributeToBe(By locator, String attributeName, String value) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.attributeToBe(locator, attributeName, value));
    }

    public static void waitForPageSourceNotToContain(String value) {
        for (int i = 0; i < timeOutForWait; i++) {
            if (!driver().getPageSource().toLowerCase().contains(value)) {
                return;
            }
            waitFor(1);
        }

        throw new RuntimeException("Page source does contain '" + value + "'");
    }

    public static void waitForPageSourceToContain(String value) {
        for (int i = 0; i < timeOutForWait; i++) {
            if (driver().getPageSource().toLowerCase().contains(value)) {
                return;
            }
            waitFor(1);
        }

        long epochMilli = now().toEpochMilli();
        takeScreenshot("Unexpected-error-waitForPageSourceToContain-text_" + epochMilli + ".png");
        savePageSource("waitForPageSourceToContain_pageContent_" + epochMilli + ".txt");
        throw new RuntimeException("Page source does not contain '" + value + "'");
    }

    public static void waitForAttributeToContain(Supplier<WebElement> finder, String attributeName, String value) {
        for (int i = 0; i < timeOutForWait; i++) {
            WebElement element = finder.get();
            if (element.getAttribute(attributeName).contains(value)) {
                return;
            }
            waitFor(1);
        }

        throw new RuntimeException("Attribute '" + attributeName + "' does not contain '" + value + "'");
    }

    public static void waitForValueToChange(Supplier<WebElement> finder, String initialValue) {
        for (int i = 0; i < timeOutForWait; i++) {
            WebElement element = finder.get();
            if (!initialValue.equals(element.getText())) {
                return;
            }
            waitFor(1);
        }

        throw new RuntimeException("Initial value did not change: " + initialValue);
    }

    public static void waitForNumberOfElementsToBe(By locator, Integer number) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.numberOfElementsToBe(locator, number));
    }

    public static WebElement waitForPresenceOfElementLocated(By locator) {
        return new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement shortWaitForVisibilityOfElementLocated(By locator) {
        return new WebDriverWait(driver(), shorTimeOutForWaitInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static List<WebElement> waitForPresenceOfAllElementsLocated(By locator) {
        return new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static WebElement waitForElementToBeClickable(Supplier<WebElement> finder) {
        for (int i = 0; i < timeOutForWait; i++) {
            WebElement element = finder.get();
            try {
                if (element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
            } catch (StaleElementReferenceException staleException) {
                i++;
                waitFor(1);
            }
        }

        throw new RuntimeException("Element is not clickable: " + finder);
    }

    public static WebElement waitForElementsToBeClickable(Supplier<List<WebElement>> finder) {
        for (int i = 0; i < timeOutForWait; i++) {
            List<WebElement> elements = finder.get();
            try {
                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                }
            } catch (StaleElementReferenceException staleException) {
                i++;
                waitFor(1);
            }
        }
        throw new RuntimeException("Element is not clickable: " + finder);
    }

    public static WebElement waitForElementToBeClickableBy(By locator) {
        return new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForElementToBeClickable(By locator) {
        return waitForElementToBeClickable(() -> findElement(locator));
    }

    public static WebElement extendedWaitForElementToBeClickable(By locator) {
        return new WebDriverWait(driver(), extendedTimeOutForWaitInSeconds).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForPageLoadToFinish(long timeout) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout * 1000) {
            if (isPageLoading(driver())) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    public static void waitForStyleToContainString(By locator, String string) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.attributeContains(locator, "style", string));
    }

    public static void extendedWaitForStyleToContainString(By locator, String string) {
        new WebDriverWait(driver(), extendedTimeOutForWaitInSeconds).until(ExpectedConditions.attributeContains(locator, "style", string));
    }

    public static void waitForTextToBePresentInElementLocated(By locator, String string) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.textToBe(locator, string));
    }

    public static void waitForTitleOfPageToBe(String title) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.titleIs(title));
    }

    public static void shortWaitForTexNotToBePresentInElementLocatedBy(By locator, String string) {
        new WebDriverWait(driver(), shorTimeOutForWaitInSeconds).until(ExpectedConditions.not(ExpectedConditions.textToBe(locator, string)));
    }

    public static void extendedWaitForTextToBePresentInElementLocated(By locator, String string) {
        new WebDriverWait(driver(), extendedTimeOutForWaitInSeconds).until(textToBePresentInElementLocated(locator, string));
    }

    public static void waitForElementLocatedByToBeRefreshedAndTextToBePresent(By locator, String text) {
        new WebDriverWait(driver(), timeOutForWaitInSeconds).until(ExpectedConditions.refreshed(textToBePresentInElementLocated(locator, text)));
    }

    public static void clickTheLastRowInTheTable(String tableName) {
        WebElement row = findElement(xpath("//div[@id='" + tableName + "']/div[contains(@class, 'row')][last()]"));
        Memory.setValue(tableName + "-id", row.getAttribute("id"));
        waitForElementToBeClickable(xpath("//div[@id='" + tableName + "']/div[contains(@class, 'row')][last()]")).click();
    }

    public static Actions newActions() {
        return new Actions(driver());
    }

    public static WebElement moveToElement(WebElement element) {
        newActions().moveToElement(element).build().perform();
        return element;
    }

    public static WebElement moveToElement(By locator) {
        return moveToElement(findElement(locator));
    }

    public static void selectFromDropdown(By locator, String selection) {
        new Select(findElement(locator)).selectByVisibleText(selection);
    }

    public static void switchToDefaultContent() {
        driver().switchTo().defaultContent();
    }

    public static void switchTo(By locator) {
        driver().switchTo().frame(findElement(locator));
    }

    public static void typeInto(By locator, String value) {
        typeInto(driver(), locator, value);
    }

    public static void typeInto(SearchContext root, By locator, String value) {
        typeInto(findElement(root, locator), value);
    }

    public static void typeInto(WebElement element, String value) {
        element.sendKeys(value);
    }

    public static void clearAndTypeInto(By locator, String value) {
        clearAndTypeInto(driver(), locator, value);
    }

    public static void clearAndTypeInto(WebElement element, String value) {
        element.clear();
        typeInto(element, value);
    }

    public static void clearAndTypeInto(SearchContext root, By locator, String value) {
        WebElement element = findElement(root, locator);
        element.clear();
        element.sendKeys(value);
    }

    private static boolean isPageLoading(WebDriver driver) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return (document.readyState == 'complete')");
    }
}
