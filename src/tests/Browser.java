package tests;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * class DriverSetup
 * 
 * Provides factory methods that wrap the driver production steps to be browser-independent, so that
 * the tests only need to be written once (presumably).
 * 
 * @author wqian94
 */
public enum Browser {
    CHROME;
    
    private static LinkedList<WebDriver> activeDrivers = new LinkedList<>();
    
    /**
     * Wraps the WebDriverWait-and-promise routine to enable easier wait-and-tests.
     * 
     * @param driver
     *            the driver to wait on.
     * @param timeout
     *            the timeout for the driver wait.
     * @param functor
     *            the predicate functor to use as the promise-deliverer.
     */
    public static void act(final WebDriver driver, final long timeout,
            final Predicate<WebDriver> func) {
        final int sleep = 100; // Sleep for 100ms at a time during waits.
        new WebDriverWait(driver, timeout, sleep).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver driver) {
                return func.test(driver);
            }
        });
    }
    
    /**
     * Wraps the WebDriverWait-and-promise routine to enable easier wait-and-tests for when no
     * boolean return is expected.
     * 
     * @param driver
     *            the driver to wait on.
     * @param timeout
     *            the timeout for the driver wait.
     * @param functor
     *            the consumer functor to use as the promise-deliverer.
     */
    public static void act(final WebDriver driver, final long timeout,
            final Consumer<WebDriver> func) {
        final int sleep = 100; // Sleep for 100ms at a time during waits.
        new WebDriverWait(driver, timeout, sleep).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver driver) {
                func.accept(driver);
                return true;
            }
        });
    }
    
    /**
     * Cleans up all active drivers by closing all active windows and ending all sessions.
     */
    public static void endDrivers() {
        for (final WebDriver driver : activeDrivers) {
            driver.close();
            driver.quit();
        }
        
        // Reset the list.
        activeDrivers = new LinkedList<>();
    }
    
    /**
     * Dynamically creates a WebDriver to use in tests, based on the selected browser type.
     * 
     * @return A WebDriver that has already loaded the page and enabled microphone permissions.
     */
    public static WebDriver getDriver(final Browser browser, final String target) {
        WebDriver driver = null;
        
        switch (browser) {
            case CHROME:
                driver = getDriverChrome();
                break;
            // As long as we implement a case for each enum value, we don't need a default.
        }
        
        // Keep track of the created driver.
        activeDrivers.add(driver);
        
        // Retrieve and load the page.
        driver.get(target);
        act(driver, 60, (client) -> {
            return client.findElement(By.className("AuO")).isDisplayed();
        });
        
        return driver;
    }
    
    /**
     * Dynamically creates a ChromeDriver.
     * 
     * @return A WebDriver that emulates the Chrome environment, with media security disabled.
     */
    private static WebDriver getDriverChrome() {
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("use-fake-ui-for-media-stream=true");
        final WebDriver driver = new ChromeDriver(options);
        return driver;
    }
}
