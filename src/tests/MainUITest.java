package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import client.Browser;
import client.Web;
import server.AuoServer;

/**
 * MainUITest
 * 
 * Unit test suite for ensuring that the main UI framework is set up correctly.
 * 
 * @author wqian94
 */
public class MainUITest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Main UI should exist.
     * - Sub-UIs should all exist: the title bar, controls UI, zoom UI, audio UI, and save UI.
     * 
     * @formatter:on
     */
    
    private final static AuoServer server = AuoServer.start("../", 0); // Randomly-allocated port.
    
    @BeforeClass
    public static void setUpClass() {
        Web.initiate();
    }
    
    @AfterClass
    public static void tearDownClass() {
        server.terminate();
        Web.terminate();
    }
    
    @After
    public void tearDown() {
        Web.endDrivers();
    }
    
    private WebDriver getDriver() {
        return Web.getDriver(Browser.CHROME, server.getURL(), By.className("AuO"));
    }
    
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }
    
    @Test
    public void testServerIsRunning() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            assertEquals("loaded URL should be server root", server.getURL(),
                    client.getCurrentUrl());
        });
    }
    
    @Test
    public void testMainUiExistence() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            final WebElement mainUI = Web.cssSelect(client, ".AuO .auo-main-ui");
            assertTrue("expecting main UI to display", mainUI.isDisplayed());
        });
    }
    
    @Test
    public void testTitleBarExistence() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            final WebElement titleBar = Web.cssSelect(driver, ".AuO .auo-title-bar");
            assertTrue("expecting title bar to display", titleBar.isDisplayed());
        });
    }
    
    @Test
    public void testControlsUiExistence() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            final WebElement controlsUI = Web.cssSelect(driver, ".AuO .auo-controls-ui");
            assertTrue("expecting controls UI to display", controlsUI.isDisplayed());
        });
    }
    
    @Test
    public void testZoomUiExistence() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            final WebElement zoomUI = Web.cssSelect(driver, ".AuO .auo-zoom-ui");
            assertTrue("expecting zoom UI to display", zoomUI.isDisplayed());
        });
    }
    
    @Test
    public void testAudioUiExistence() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            final WebElement audioUI = Web.cssSelect(driver, ".AuO .auo-audio-ui");
            assertTrue("expecting audio UI to display", audioUI.isDisplayed());
        });
    }
    
    @Test
    public void testSaveUiExistence() {
        final WebDriver driver = getDriver();
        Web.wait(driver, 1, (client) -> {
            final WebElement saveUI = Web.cssSelect(driver, ".AuO .auo-save-ui");
            assertTrue("expecting save UI to display", saveUI.isDisplayed());
        });
    }
}