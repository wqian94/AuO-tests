package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import client.Web;

/**
 * AudioUITest
 * 
 * Unit test suite for ensuring that the audio UI is set up correctly.
 * 
 * @author wqian94
 */
public class AudioUITest extends AbstractAuoTest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Check that the display, end trimmer, start trimmer, and ticker all exist.
     * - Check that the sum of the widths of the display and trimmers equal the width of the UI in
     *   the initial launch state.
     * - Check that drag-and-drop for the end trimmer correctly repositions and resizes the end
     *   trimmer.
     * - Check that drag-and-drop for the start trimmer correctly repositions and resizes the start
     *   trimmer.
     * - Check that drag-and-drop for the ticker correctly repositions the ticker.
     * - Check that drag-and-drop for ticker does not drop ticker beyond end trimmer.
     * - Check that drag-and-drop for ticker does not drop ticker beyond start trimmer.
     * - Check that drag-and-drop for the end trimmer moves the ticker when the ticker's position
     *   becomes invalid.
     * - Check that drag-and-drop for the start trimmer moves the ticker when the ticker's position
     *   becomes invalid.
     * 
     * @formatter:on
     */
    
    @Test
    public void testDisplayExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected visual display to display",
                Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-display").isDisplayed());
    }
    
    @Test
    public void testEndTrimmerExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected end trimmer to display",
                Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-end-trimmer").isDisplayed());
    }
    
    @Test
    public void testStartTrimmerExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected start trimmer to display",
                Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-start-trimmer").isDisplayed());
    }
    
    @Test
    public void testTickerExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected ticker to display",
                Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker").isDisplayed());
    }
    
    @Test
    public void testWidthsOfInitialLaunchState() {
        final WebDriver driver = getDriver();
        
        final int audioUiWidth = Web.cssSelect(driver, ".AuO .auo-audio-ui").getSize().getWidth();
        final int displayWidth = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-display")
                .getSize().getWidth();
        final int startTrimmerWidth = Web
                .cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-start-trimmer").getSize()
                .getWidth();
        final int endTrimmerWidth = Web
                .cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-end-trimmer").getSize()
                .getWidth();
        
        System.out.printf("%d %d %d %d\n", displayWidth, startTrimmerWidth, endTrimmerWidth, audioUiWidth);
        
        assertEquals("expected display, start trimmer, and end trimmer to fill the width of the UI",
                audioUiWidth, displayWidth + startTrimmerWidth + endTrimmerWidth);
    }
    
    // TODO: Drag-and-drop tests, once proper support via Selenium is available.
}