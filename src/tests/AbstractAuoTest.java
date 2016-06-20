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
    private static AuoServer server;
    
    @BeforeClass
    public static void setUpClass() {
        server = AuoServer.start("../lib/", 0); // Randomly-allocated port.
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
    
    /**
     * Produces the underlying AuoServer. Abstracted away to enforce encapsulation in the abstract
     * class and prevent tests from ``accidentally" modifying the server directly.
     * 
     * @return The currently-running AuoServer instance.
     */
    protected static AuoServer getServer() {
        return server;
    }
    
    /**
     * Produces a WebDriver that loads the AuO standalone project page and ensures that AuO is
     * launched.
     * 
     * @return The WebDriver that was created, with the page loaded and AuO launched.
     */
    protected WebDriver getDriver() {
        return Web.getDriver(Browser.CHROME, server.getURL() + "TEST", By.className("AuO"));
    }
    
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }
}