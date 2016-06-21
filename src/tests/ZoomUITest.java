package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import client.Web;

/**
 * ZoomUITest
 * 
 * Unit test suite for ensuring that the zoom UI is set up correctly and works as expected.
 * 
 * @author wqian94
 */
public class ZoomUITest extends AbstractAuoTest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Check that zoom in, zoom out, and zoom reset buttons exist on launch. Since these aren't
     *   classes, we just expect there to be three buttons; 0 is zoom in, 1 is zoom out, and 2 is
     *   zoom reset.
     * - Check that zoom in, zoom out, and zoom reset buttons display the correct messages.
     * - Check that zoom in and zoom reset buttons are enabled on launch.
     * - Check that zoom out button is disabled on launch.
     * - Check that zooming in enables the zoom out button.
     * - Check that zoom reset after zooming in causes zoom in and zoom reset to remain enabled but
     *   zoom out to be disabled.
     * - Check that zooming out fewer times than zooming in causes all three buttons to remain
     *   enabled.
     * - Check that zooming out an equal number of times as zooming in results in the zoom in
     *   and zoom reset buttons to remain enabled but the zoom out button to become disabled.
     * - Check that zooming out fewer times than zooming in and then resetting the zoom causes the
     *   zoom in and zoom reset buttons to remain enabled but the zoom out button to become
     *   disabled.
     * - Check that zooming in after zooming out re-enables the zoom out button.
     * - Check that zooming in after resetting zoom re-enables the zoom out button.
     * 
     * @formatter:on
     */
    
    private static final String cssButtonSelector = ".AuO .auo-zoom-ui > button";
    private static final String zoomInMessage = "Zoom in";
    private static final String zoomOutMessage = "Zoom out";
    private static final String zoomResetMessage = "Zoom reset";
    
    /**
     * Produces the WebElement corresponding to the zoom in button.
     * 
     * @param driver
     *            the WebDriver to retrieve the WebElement from.
     * @return The WebElement that corresponds to the zoom in button.
     */
    private WebElement getZoomInButton(final WebDriver driver) {
        return Web.cssSelects(driver, cssButtonSelector).get(0);
    }
    
    /**
     * Produces the WebElement corresponding to the zoom out button.
     * 
     * @param driver
     *            the WebDriver to retrieve the WebElement from.
     * @return The WebElement that corresponds to the zoom out button.
     */
    private WebElement getZoomOutButton(final WebDriver driver) {
        return Web.cssSelects(driver, cssButtonSelector).get(1);
    }
    
    /**
     * Produces the WebElement corresponding to the zoom reset button.
     * 
     * @param driver
     *            the WebDriver to retrieve the WebElement from.
     * @return The WebElement that corresponds to the zoom reset button.
     */
    private WebElement getZoomResetButton(final WebDriver driver) {
        return Web.cssSelects(driver, cssButtonSelector).get(2);
    }
    
    @Test
    public void testZoomButtonsExist() {
        final WebDriver driver = getDriver();
        final List<WebElement> buttons = Web.cssSelects(driver, cssButtonSelector);
        
        assertEquals("expected exactly three buttons in the controls UI.", 3, buttons.size());
        
        assertTrue("expected zoom in button to be displayed.", buttons.get(0).isDisplayed());
        assertTrue("expected zoom out button to be displayed.", buttons.get(1).isDisplayed());
        assertTrue("expected zoom reset button to be displayed.", buttons.get(2).isDisplayed());
    }
    
    @Test
    public void testZoomInButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        final WebElement zoomIn = getZoomInButton(driver);
        
        assertEquals("expected zoom in button to display correct message in initial launch state.",
                zoomInMessage, zoomIn.getText());
        assertTrue("expected zoom in button to be enabled in initial launch state.",
                zoomIn.isEnabled());
    }
    
    @Test
    public void testZoomOutButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        final WebElement zoomOut = getZoomOutButton(driver);
        
        assertEquals("expected zoom out button to display correct message in initial launch state.",
                zoomOutMessage, zoomOut.getText());
        assertFalse("expected zoom out button to be disabled in initial launch state.",
                zoomOut.isEnabled());
    }
    
    @Test
    public void testZoomResetButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        final WebElement zoomReset = getZoomResetButton(driver);
        
        assertEquals(
                "expected zoom reset button to display correct message in initial launch state.",
                zoomResetMessage, zoomReset.getText());
        assertTrue("expected zoom reset button to be enabled in initial launch state.",
                zoomReset.isEnabled());
    }
    
    @Test
    public void testZoomedInState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in zoomed in state.",
                getZoomInButton(driver).isEnabled());
        assertTrue("expected zoom out button to be enabled in zoomed in state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in zoomed in state.",
                getZoomResetButton(driver).isEnabled());
    }
    
    @Test
    public void testZoomedOutLessThanZoomedInState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomOutButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in more zoomed in state.",
                getZoomInButton(driver).isEnabled());
        assertTrue("expected zoom out button to be enabled in more zoomed in state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in more zoomed in state.",
                getZoomResetButton(driver).isEnabled());
    }
    
    @Test
    public void testZoomResetAfterZoomedInState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomResetButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in resetted zoom state.",
                getZoomInButton(driver).isEnabled());
        assertFalse("expected zoom out button to be disabled in resetted zoom state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in resetted zoom state.",
                getZoomResetButton(driver).isEnabled());
    }
    
    @Test
    public void testZoomedOutAsMuchAsZoomedInState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomOutButton(driver).click();
        getZoomOutButton(driver).click();
        getZoomOutButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in equally zoomed in and out state.",
                getZoomInButton(driver).isEnabled());
        assertFalse("expected zoom out button to be disabled in equally zoomed in and out state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in equally zoomed in and out state.",
                getZoomResetButton(driver).isEnabled());
    }
    
    @Test
    public void testZoomResetAfterZoomedInThenZoomOutState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomInButton(driver).click();
        getZoomOutButton(driver).click();
        getZoomResetButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in zoomed out then resetted state.",
                getZoomInButton(driver).isEnabled());
        assertFalse("expected zoom out button to be disabled in zoomed out then resetted state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in zoomed out then resetted state.",
                getZoomResetButton(driver).isEnabled());
    }
    
    @Test
    public void testZoomInAfterZoomOutState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        getZoomOutButton(driver).click();
        getZoomInButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in zoom in-out-in state.",
                getZoomInButton(driver).isEnabled());
        assertTrue("expected zoom out button to be enabled in zoom in-out-in state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in zoom in-out-in state.",
                getZoomResetButton(driver).isEnabled());
    }
    
    @Test
    public void testZoomedInAfterZoomResetState() {
        final WebDriver driver = getDriver();
        
        getZoomInButton(driver).click();
        getZoomResetButton(driver).click();
        getZoomInButton(driver).click();
        
        assertTrue("expected zoom in button to be enabled in zoom in-reset-in state.",
                getZoomInButton(driver).isEnabled());
        assertTrue("expected zoom out button to be enabled in zoom in-reset-in state.",
                getZoomOutButton(driver).isEnabled());
        assertTrue("expected zoom reset button to be enabled in zoom in-reset-in state.",
                getZoomResetButton(driver).isEnabled());
    }
}