package tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import client.Browser;
import client.Web;
import server.AuoServer;

/**
 * AbstractAuoTest
 * 
 * Abstract class for the AuO test suite, which deals with most of the common setup and teardown.
 * 
 * @author wqian94
 */
public abstract class AbstractAuoTest {
    protected final static AuoServer server = AuoServer.start("../", 0); // Randomly-allocated port.
    
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
    
    protected WebDriver getDriver() {
        return Web.getDriver(Browser.CHROME, server.getURL(), By.className("AuO"));
    }
    
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }
}