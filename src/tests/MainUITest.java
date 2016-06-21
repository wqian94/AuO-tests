package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import client.Web;

/**
 * MainUITest
 * 
 * Unit test suite for ensuring that the main UI framework is set up correctly.
 * 
 * @author wqian94
 */
public class MainUITest extends AbstractAuoTest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Main UI should exist.
     * - Sub-UIs should all exist: the title bar, controls UI, zoom UI, audio UI, and save UI.
     * 
     * @formatter:on
     */
    
    @Test
    public void testServerIsRunning() {
        final WebDriver driver = getDriver();
        assertEquals("expected loaded URL to be server root.", getServer().getTestURL(),
                driver.getCurrentUrl());
    }
    
    @Test
    public void testMainUiExistence() {
        final WebDriver driver = getDriver();
        assertTrue("expected main UI to display.",
                Web.cssSelect(driver, ".AuO .auo-main-ui").isDisplayed());
    }
    
    @Test
    public void testTitleBarExistence() {
        final WebDriver driver = getDriver();
        assertTrue("expected title bar to display.",
                Web.cssSelect(driver, ".AuO .auo-title-bar").isDisplayed());
    }
    
    @Test
    public void testControlsUiExistence() {
        final WebDriver driver = getDriver();
        assertTrue("expected controls UI to display.",
                Web.cssSelect(driver, ".AuO .auo-controls-ui").isDisplayed());
    }
    
    @Test
    public void testZoomUiExistence() {
        final WebDriver driver = getDriver();
        assertTrue("expected zoom UI to display.",
                Web.cssSelect(driver, ".AuO .auo-zoom-ui").isDisplayed());
    }
    
    @Test
    public void testAudioUiExistence() {
        final WebDriver driver = getDriver();
        assertTrue("expected audio UI to display.",
                Web.cssSelect(driver, ".AuO .auo-audio-ui").isDisplayed());
    }
    
    @Test
    public void testSaveUiExistence() {
        final WebDriver driver = getDriver();
        assertTrue("expected save UI to display.",
                Web.cssSelect(driver, ".AuO .auo-save-ui").isDisplayed());
    }
}