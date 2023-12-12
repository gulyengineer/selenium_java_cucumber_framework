package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;

import java.time.Clock;
import java.time.Duration;
import java.util.function.Function;

public class SearchContextWait extends FluentWait<SearchContext> {
    private final SearchContext root;

    public SearchContextWait(SearchContext root, Duration timeout) {
        this(root, timeout, Duration.ofMillis(DEFAULT_SLEEP_TIMEOUT), Clock.systemDefaultZone(), Sleeper.SYSTEM_SLEEPER);
    }

    public SearchContextWait(WebDriver driver, Duration timeout, Duration sleep) {
        this(driver, timeout, sleep, Clock.systemDefaultZone(), Sleeper.SYSTEM_SLEEPER);
    }

    public SearchContextWait(SearchContext root, Duration timeout, Duration sleep, Clock clock, Sleeper sleeper) {
        super(root, clock, sleeper);
        withTimeout(timeout);
        pollingEvery(sleep);
        ignoring(NotFoundException.class);
        this.root = root;
    }

    @Override
    protected RuntimeException timeoutException(String message, Throwable lastException) {
        SearchContext exceptionDriver = root;
        TimeoutException exception = new TimeoutException(message, lastException);
        exception.addInfo(WebDriverException.DRIVER_INFO, exceptionDriver.getClass().getName());
        while (exceptionDriver instanceof WrapsDriver) {
            exceptionDriver = ((WrapsDriver) exceptionDriver).getWrappedDriver();
        }
        if (exceptionDriver instanceof RemoteWebDriver remote) {
            if (remote.getSessionId() != null) {
                exception.addInfo(WebDriverException.SESSION_ID, remote.getSessionId().toString());
            }
            if (remote.getCapabilities() != null) {
                exception.addInfo("Capabilities", remote.getCapabilities().toString());
            }
        }
        throw exception;
    }
}

class CustomExpectedConditions {
    private CustomExpectedConditions() {
    }

    public static CustomExpectedCondition<WebElement> presenceOfElementLocated(final By locator) {
        return new CustomExpectedCondition<>() {
            public WebElement apply(SearchContext searchContext) {
                return searchContext.findElement(locator);
            }

            @Override
            public String toString() {
                return "presence of element located by: " + locator;
            }
        };
    }

    public static CustomExpectedCondition<WebElement> elementToBeClickable(final By locator) {
        return new CustomExpectedCondition<>() {
            public WebElement apply(SearchContext searchContext) {
                return searchContext.findElement(locator);
            }

            @Override
            public String toString() {
                return "element to be clickable: " + locator;
            }
        };
    }
}

interface CustomExpectedCondition<T> extends Function<SearchContext, T> {
}