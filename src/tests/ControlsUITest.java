package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import client.Web;

/**
 * ControlsUITest
 * 
 * Unit test suite for ensuring that the controls UI is set up correctly and works as expected.
 * 
 * @author wqian94
 */
public class ControlsUITest extends AbstractAuoTest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Check that the record, play, and stop buttons exist. Since these aren't classes, we just
     *   expect there to be three displayed buttons; 0 is record, 1 is play, 2 is stop.
     * - Check that the record button says "Record" in the initial launch.
     * - Check that the play button says "Play" in the initial launch.
     * - Check that the stop button says "Stop" in the initial launch.
     * - Check that the record button is enabled in the initial launch.
     * - Check that the play and stop buttons are disabled in the initial launch.
     * - Check that clicking the record button disables the record and play buttons.
     * - Check that clicking the record button enables the stop button.
     * - Check that after recording and stopping, the UI enters the idle state:
     *   - Record and play buttons are enabled.
     *   - Stop button is disabled.
     *   - Record button says "Record".
     *   - Play button says "Play".
     *   - Stop button says "Stop".
     * - Check that clicking the play button disables the record and play buttons.
     * - Check that clicking the play button enables the stop button.
     * - Check that stopping the playback results in the idle state.
     * - Check that letting the play run through to the end results in the idle state.
     * 
     * @formatter:on
     */
    
    private final String cssButtonSelector = ".AuO .auo-controls-ui > button";
    private final String playMessage = "Play";
    private final String recordMessage = "Record";
    private final String stopMessage = "Stop";
    
    /**
     * Produces the WebElement corresponding to the record button.
     * 
     * @param driver
     *            the WebDriver to retrieve the WebElement from.
     * @return The WebElement that corresponds to the record button.
     */
    private WebElement getRecordButton(final WebDriver driver) {
        return Web.cssSelects(driver, cssButtonSelector).get(0);
    }
    
    /**
     * Produces the WebElement corresponding to the play button.
     * 
     * @param driver
     *            the WebDriver to retrieve the WebElement from.
     * @return The WebElement that corresponds to the play button.
     */
    private WebElement getPlayButton(final WebDriver driver) {
        return Web.cssSelects(driver, cssButtonSelector).get(1);
    }
    
    /**
     * Produces the WebElement corresponding to the stop button.
     * 
     * @param driver
     *            the WebDriver to retrieve the WebElement from.
     * @return The WebElement that corresponds to the stop button.
     */
    private WebElement getStopButton(final WebDriver driver) {
        return Web.cssSelects(driver, cssButtonSelector).get(2);
    }
    
    @Test
    public void testControlsButtonsExist() {
        final WebDriver driver = getDriver();
        final List<WebElement> buttons = Web.cssSelects(driver, cssButtonSelector);
        
        assertEquals("expected exactly three buttons in the controls UI.", 3, buttons.size());
        
        assertTrue("expected record button to be displayed", buttons.get(0).isDisplayed());
        assertTrue("expected play button to be displayed", buttons.get(1).isDisplayed());
        assertTrue("expected stop button to be displayed", buttons.get(2).isDisplayed());
    }
    
    @Test
    public void testRecordButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        final WebElement record = getRecordButton(driver);
        
        assertEquals("expected record button to display correct message in initial launch state.",
                recordMessage, record.getText());
        assertTrue("expected record button to be enabled in initial launch state",
                record.isEnabled());
    }
    
    @Test
    public void testPlayButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        final WebElement play = getPlayButton(driver);
        
        assertEquals("expected play button to display correct message in initial launch state.",
                playMessage, play.getText());
        assertFalse("expected play button to be disabled in initial launch state",
                play.isEnabled());
    }
    
    @Test
    public void testStopButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        final WebElement stop = getStopButton(driver);
        
        assertEquals("expected stop button to display correct message in initial launch state.",
                stopMessage, stop.getText());
        assertFalse("expected stop button to be disabled in initial launch state",
                stop.isEnabled());
    }
    
    @Test
    public void testRecordingState() {
        final WebDriver driver = getDriver();
        
        getRecordButton(driver).click();
        
        assertFalse("expected record button to be disabled in recording state.",
                getRecordButton(driver).isEnabled());
        assertFalse("expected play button to be disabled in recording state.",
                getPlayButton(driver).isEnabled());
        assertTrue("expected stop button to be enabled in recording state.",
                getStopButton(driver).isEnabled());
    }
    
    @Test
    public void testIdleState() {
        final WebDriver driver = getDriver();
        
        getRecordButton(driver).click();
        
        Web.test(driver, 1, (client) -> {
            return !getRecordButton(driver).isEnabled();
        });
        
        getStopButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getRecordButton(driver).isEnabled();
        });
        
        assertEquals("expected record button to display correct message in idle state.",
                recordMessage, getRecordButton(driver).getText());
        assertEquals("expected play button to display correct message in idle state.", playMessage,
                getPlayButton(driver).getText());
        assertEquals("expected stop button to display correct message in idle state.", stopMessage,
                getStopButton(driver).getText());
        assertTrue("expected record button to be enabled in idle state.",
                getRecordButton(driver).isEnabled());
        assertTrue("expected play button to be enabled in idle state.",
                getPlayButton(driver).isEnabled());
        assertFalse("expected stop button to be disabled in idle state.",
                getStopButton(driver).isEnabled());
    }
    
    @Test
    public void testPlayState() throws InterruptedException {
        final WebDriver driver = getDriver();
        
        getRecordButton(driver).click();
        
        Web.test(driver, 1, (client) -> {
            return !getRecordButton(driver).isEnabled();
        });
        
        Thread.sleep(1000); // 1-second recording.
        
        getStopButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getPlayButton(driver).isEnabled();
        });
        
        getPlayButton(driver).click();
        
        assertFalse("expected record button to be disabled in playback state.",
                getRecordButton(driver).isEnabled());
        assertFalse("expected play button to be disabled in playback state.",
                getPlayButton(driver).isEnabled());
        assertTrue("expected stop button to be enabled in playback state.",
                getStopButton(driver).isEnabled());
    }
    
    @Test
    public void testStoppingPlaybackResultsInIdleState() throws InterruptedException {
        final WebDriver driver = getDriver();
        
        getRecordButton(driver).click();
        
        Web.test(driver, 1, (client) -> {
            return !getRecordButton(driver).isEnabled();
        });
        
        Thread.sleep(1000); // 1-second recording.
        
        getStopButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getPlayButton(driver).isEnabled();
        });
        
        getPlayButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getStopButton(driver).isEnabled();
        });
        
        getStopButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getPlayButton(driver).isEnabled();
        });
        
        assertEquals("expected record button to display correct message in idle state.",
                recordMessage, getRecordButton(driver).getText());
        assertEquals("expected play button to display correct message in idle state.", playMessage,
                getPlayButton(driver).getText());
        assertEquals("expected stop button to display correct message in idle state.", stopMessage,
                getStopButton(driver).getText());
        assertTrue("expected record button to be enabled in idle state.",
                getRecordButton(driver).isEnabled());
        assertTrue("expected play button to be enabled in idle state.",
                getPlayButton(driver).isEnabled());
        assertFalse("expected stop button to be disabled in idle state.",
                getStopButton(driver).isEnabled());
    }
    
    @Test
    public void testRunningPlaybackToEndResultsInIdleState() throws InterruptedException {
        final WebDriver driver = getDriver();
        
        getRecordButton(driver).click();
        
        Web.test(driver, 1, (client) -> {
            return !getRecordButton(driver).isEnabled();
        });
        
        Thread.sleep(1000); // 1-second recording.
        
        getStopButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getPlayButton(driver).isEnabled();
        });
        
        getPlayButton(driver).click();
        
        Web.test(driver, 5, (client) -> {
            return getStopButton(driver).isEnabled();
        });
        
        Web.test(driver, 5, (client) -> {
            return getPlayButton(driver).isEnabled();
        });
        
        assertEquals("expected record button to display correct message in idle state.",
                recordMessage, getRecordButton(driver).getText());
        assertEquals("expected play button to display correct message in idle state.", playMessage,
                getPlayButton(driver).getText());
        assertEquals("expected stop button to display correct message in idle state.", stopMessage,
                getStopButton(driver).getText());
        assertTrue("expected record button to be enabled in idle state.",
                getRecordButton(driver).isEnabled());
        assertTrue("expected play button to be enabled in idle state.",
                getPlayButton(driver).isEnabled());
        assertFalse("expected stop button to be disabled in idle state.",
                getStopButton(driver).isEnabled());
    }
}