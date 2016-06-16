package client;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import server.Log;

/**
 * class Web
 * 
 * Provides methods that wrap the driver production steps to be browser-independent, so that the
 * tests only need to be written once (presumably).
 * 
 * @author wqian94
 */
public class Web {
    private static final int DISPLAY = 42; // Currently hardcoded, should move to dynamic later.
    
    private static boolean initiated = false;
    
    private static LinkedList<WebDriver> activeDrivers = new LinkedList<>();
    private static Process processXvfb = null;
    
    /**
     * The initiation method, for setting up the environment. Requires that all previous initiations
     * have been terminated.
     */
    public static void initiate() {
        if (initiated) {
            throw new RuntimeException("Active Web environment currently exists. "
                    + "Call terminate() before initiating a new session.");
        }
        
        final ProcessBuilder pb = new ProcessBuilder(
                new String[] { "Xvfb", ":" + DISPLAY, "-screen", "0", "1600x1200x24" });
        pb.inheritIO();
        pb.redirectError();
        pb.redirectOutput();
        try {
            processXvfb = pb.start();
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
        
        System.setProperty("webdriver.chrome.bin", "/usr/bin/google-chrome");
        System.setProperty("webdriver.chrome.driver", "lib/chromedriver");
        
        Log.log(Log.INFO, "Web session successfully initiated on display %d.", DISPLAY);
        
        initiated = true;
    }
    
    /**
     * The termination method, for tearing down the environment. Requires that an active, initiated
     * session exists.
     */
    public static void terminate() {
        if (!initiated) {
            throw new RuntimeException("No active Web environment currently exists. "
                    + "Call initiated() before attempting to terminate a session.");
        }
        
        endDrivers();
        processXvfb.destroy();
    }
    
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
    public static void test(final WebDriver driver, final long timeout,
            final Predicate<WebDriver> func) {
        final int sleep = 100; // Sleep for 100ms at a time during waits.
        new WebDriverWait(driver, timeout, sleep).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver driver) {
                try {
                    return func.test(driver);
                } catch (Exception exp) {
                    throw exp;
                }
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
    public static void wait(final WebDriver driver, final long timeout,
            final Consumer<WebDriver> func) {
        test(driver, timeout, (client) -> {
            func.accept(client);
            return true;
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
     * Dynamically creates a WebDriver to use in tests, based on the selected browser type. Will
     * load the page and wait until the page loads such that the element specified by the condition
     * has been displayed.
     * 
     * @param browser
     *            the type of browser to emulate.
     * @param target
     *            the page to load.
     * @param condition
     *            a By stating what element to wait on. If null, no wait will occur.
     */
    public static WebDriver getDriver(final Browser browser, final String target,
            final By condition) {
        WebDriver driver;
        
        switch (browser) {
            case CHROME:
                driver = getDriverChrome();
                break;
            // As long as we implement a case for each enum value, we shouldn't get here.
            default:
                throw new IllegalArgumentException("Browser support for " + browser + " missing!");
        }
        
        // Keep track of the created driver.
        activeDrivers.add(driver);
        
        // Retrieve and load the page.
        driver.get(target);
        test(driver, 60, (client) -> {
            return client.findElement(condition).isDisplayed();
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
        options.addArguments("display=:42");
        options.addArguments("start-maximized");
        options.addArguments("use-fake-ui-for-media-stream=true");
        final WebDriver driver = new ChromeDriver(options);
        return driver;
    }
}