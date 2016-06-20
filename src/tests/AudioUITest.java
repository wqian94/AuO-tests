package tests;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
        
        System.out.printf("%d %d %d %d\n", displayWidth, startTrimmerWidth, endTrimmerWidth,
                audioUiWidth);
        
        assertEquals("expected display, start trimmer, and end trimmer to fill the width of the UI",
                audioUiWidth, displayWidth + startTrimmerWidth + endTrimmerWidth);
    }
    
    // TODO: Run drag-and-drop tests, once proper support via Selenium is available.
    
    @Ignore
    @Test
    public void testEndTrimmerDragAndDrop() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int dragDistance = 100;
        
        final WebElement endTrimmer = Web.cssSelect(driver,
                ".AuO .auo-audio-ui .auo-audio-end-trimmer");
        final int endTrimmerInitialWidth = endTrimmer.getSize().getWidth();
        
        // Drag the end trimmer by dragDistance to the left.
        new Actions(driver).dragAndDropBy(endTrimmer, -dragDistance, 0).perform();
        
        final int endTrimmerFinalWidth = Web
                .cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-end-trimmer").getSize()
                .getWidth();
        
        assertEquals("expected trimmer to widen by exactly dragged offset.", dragDistance,
                endTrimmerFinalWidth - endTrimmerInitialWidth);
    }
    
    @Ignore
    @Test
    public void testStartTrimmerDragAndDrop() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int dragDistance = 100;
        
        final WebElement startTrimmer = Web.cssSelect(driver,
                ".AuO .auo-audio-ui .auo-audio-start-trimmer");
        final int startTrimmerInitialWidth = startTrimmer.getSize().getWidth();
        
        // Drag the end trimmer by dragDistance to the right.
        new Actions(driver).dragAndDropBy(startTrimmer, dragDistance, 0).perform();
        
        final int startTrimmerFinalWidth = Web
                .cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-start-trimmer").getSize()
                .getWidth();
        
        assertEquals("expected trimmer to widen by exactly dragged offset.", dragDistance,
                startTrimmerFinalWidth - startTrimmerInitialWidth);
    }
    
    @Ignore
    @Test
    public void testTickerDragAndDrop() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int dragDistance = 100;
        
        final WebElement ticker = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker");
        final int tickerInitialLeft = ticker.getLocation().getX();
        
        // Drag the ticker by dragDistance to the right.
        new Actions(driver).dragAndDropBy(ticker, dragDistance, 0).perform();
        
        final int tickerFinalLeft = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker")
                .getLocation().getX();
        
        assertEquals("expected ticker to move by exactly dragged offset.", dragDistance,
                tickerFinalLeft - tickerInitialLeft);
    }
    
    @Ignore
    @Test
    public void testTickerDragAndDropDoesNotMoveBeyondEndTrimmer() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int displayWidth = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-display")
                .getSize().getWidth();
        final int dragDistance = displayWidth * 2;
        
        final WebElement ticker = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker");
        final int tickerInitialLeft = ticker.getLocation().getX();
        
        // Drag the ticker by dragDistance to the right.
        new Actions(driver).dragAndDropBy(ticker, dragDistance, 0).perform();
        
        final int tickerFinalLeft = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker")
                .getLocation().getX();
        
        assertEquals("expected ticker to move exactly as far as the end trimmer.", displayWidth,
                tickerFinalLeft - tickerInitialLeft);
    }
    
    @Ignore
    @Test
    public void testTickerDragAndDropDoesNotMoveBeyondStartTrimmer() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int displayWidth = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-display")
                .getSize().getWidth();
        final int dragDistance = displayWidth;
        
        final WebElement ticker = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker");
        final int tickerInitialLeft = ticker.getLocation().getX();
        
        // Drag the ticker by dragDistance to the left.
        new Actions(driver).dragAndDropBy(ticker, -dragDistance, 0).perform();
        
        final int tickerFinalLeft = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker")
                .getLocation().getX();
        
        assertEquals("expected ticker to move exactly as far as the start trimmer.", 0,
                tickerFinalLeft - tickerInitialLeft);
    }
    
    @Ignore
    @Test
    public void testEndTrimmerDragAndDropRepositionsTicker() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int displayWidth = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-display")
                .getSize().getWidth();
        final int dragDistance = displayWidth;
        
        // Drag the ticker all the way to the right.
        new Actions(driver)
                .dragAndDropBy(Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker"),
                        dragDistance, 0)
                .perform();
        
        final int tickerInitialLeft = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker")
                .getLocation().getX();
        
        // Drag the end trimmer all the way to the left.
        new Actions(driver)
                .dragAndDropBy(Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-end-trimmer"),
                        -dragDistance, 0)
                .perform();
        
        final int tickerFinalLeft = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker")
                .getLocation().getX();
        
        assertEquals("expected ticker to move with the end trimmer.", -displayWidth,
                tickerFinalLeft - tickerInitialLeft);
    }
    
    @Ignore
    @Test
    public void testStartTrimmerDragAndDropRepositionsTicker() throws InterruptedException {
        final WebDriver driver = getDriver();
        final int displayWidth = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-display")
                .getSize().getWidth();
        final int dragDistance = displayWidth;
        
        final WebElement ticker = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker");
        final int tickerInitialLeft = ticker.getLocation().getX();
        
        // Drag the start trimmer all the way to the right.
        new Actions(driver)
                .dragAndDropBy(Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-start-trimmer"),
                        -dragDistance, 0)
                .perform();
        
        final int tickerFinalLeft = Web.cssSelect(driver, ".AuO .auo-audio-ui .auo-audio-ticker")
                .getLocation().getX();
        
        assertEquals("expected ticker to move with the start trimmer.", displayWidth,
                tickerFinalLeft - tickerInitialLeft);
    }
}