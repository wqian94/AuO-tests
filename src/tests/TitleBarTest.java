package tests;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import client.Web;

/**
 * TitleBarTest
 * 
 * Unit test suite for ensuring that the title bar is set up correctly and works as expected.
 * 
 * @author wqian94
 */
public class TitleBarTest extends AbstractAuoTest {
    /**
     * @formatter:off
     * 
     * Testing strategy:
     * - Check that the title and close elements exist.
     * - Check that clicking the close element actually closes the UI.
     * 
     * @formatter:on
     */
    
    @Test
    public void testTitleElementExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected title to be displayed",
                Web.cssSelect(driver, ".AuO .auo-title-bar .auo-title").isDisplayed());
    }
    
    @Test
    public void testTitleCloseElementExists() {
        final WebDriver driver = getDriver();
        assertTrue("expected title close box to be displayed",
                Web.cssSelect(driver, ".AuO .auo-title-bar .auo-title-close").isDisplayed());
    }
    
    @Test
    public void testTitleCloseResultsInClosedUI() {
        final WebDriver driver = getDriver();
        
        assertTrue("expected AuO UI to be displayed to begin with.",
                Web.cssSelect(driver, ".AuO").isDisplayed());
        
        Web.cssSelect(driver, ".AuO .auo-title-close").click();
        
        assertEquals("expected no more AuO UI instances to exist.", Collections.emptyList(),
                Web.cssSelects(driver, ".AuO"));
    }
}