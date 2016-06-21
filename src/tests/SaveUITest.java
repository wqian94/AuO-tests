package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import client.Web;
import server.AuoServer;

/**
 * SaveUITest
 * 
 * Unit test suite for ensuring that the save UI is set up and receives messages correctly.
 * 
 * @author wqian94
 */
public class SaveUITest extends AbstractAuoTest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Check that the dropdown menu and the save button are displayed.
     * - Check that the save button is disabled and displays the correct label at initial launch.
     * - Check that dropdown menu to select the save format has at least one option.
     * - Check that the save button is disabled and displays the correct label during recording.
     * - Check that the save button is enabled and displays the correct label during idle state.
     * - Check that clicking the save button produces the correct alert message response from the
     *   server.
     * 
     * @formatter:on
     */
    
    private static final String saveMessage = "Save";
    
    @Test
    public void testSaveFormatDropdownMenuExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected save format dropdown menu to display.",
                Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-options").isDisplayed());
    }
    
    @Test
    public void testSaveButtonExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected save button to display.",
                Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").isDisplayed());
    }
    
    @Test
    public void testSaveFormatDropdownMenuHasAtLeastOneOption() {
        final WebDriver driver = getDriver();
        assertNotEquals("expected save format dropdown menu to have at least one element.", 0,
                Web.cssSelects(driver, ".AuO .auo-save-ui .auo-save-options > *").size());
    }
    
    @Test
    public void testSaveButtonInitialLaunchState() {
        final WebDriver driver = getDriver();
        assertFalse("expected save button to be disabled in initial launch state.",
                Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").isEnabled());
        assertEquals("expected save button to display correct message in initial launch state.",
                saveMessage, Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").getText());
    }
    
    @Test
    public void testSaveButtonRecordingState() {
        final WebDriver driver = getDriver();
        
        Web.cssSelects(driver, ".AuO .auo-controls-ui > button").get(0).click(); // Record button.
        
        assertFalse("expected save button to be disabled in recording state.",
                Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").isEnabled());
        assertEquals("expected save button to display correct message in recording state.",
                saveMessage, Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").getText());
    }
    
    @Test
    public void testSaveButtonIdleState() {
        final WebDriver driver = getDriver();
        
        Web.cssSelects(driver, ".AuO .auo-controls-ui > button").get(0).click(); // Record button.
        Web.cssSelects(driver, ".AuO .auo-controls-ui > button").get(2).click(); // Stop button.
        
        Web.test(driver, 5, (client) -> {
            return Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").isEnabled();
        });
        
        assertTrue("expected save button to be enabled in idle state.",
                Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").isEnabled());
        assertEquals("expected save button to display correct message in idle state.", saveMessage,
                Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").getText());
    }
    
    @Test
    public void testSaveServerRequestAndResponse() {
        final WebDriver driver = getDriver();
        
        Web.cssSelects(driver, ".AuO .auo-controls-ui > button").get(0).click(); // Record button.
        Web.cssSelects(driver, ".AuO .auo-controls-ui > button").get(2).click(); // Stop button.
        
        Web.test(driver, 5, (client) -> {
            return Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").isEnabled();
        });
        
        Web.cssSelect(driver, ".AuO .auo-save-ui .auo-save-button").click();
        
        new WebDriverWait(driver, 1, 100).until(ExpectedConditions.alertIsPresent());
        
        final Alert alert = driver.switchTo().alert();
        
        assertEquals("expected to receive server OK message on save.", AuoServer.SAVE_OK_RESPONSE,
                alert.getText());
        
        alert.accept();
    }
}