package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * class AuoServer
 * 
 * This launches a Jetty server to serve an AuO standalone application at its root. Uses SSL to
 * enable the use of the Web Audio API.
 * 
 * @author wqian94
 */
public class AuoServer extends AbstractHandler {
    public static final String SAVE_OK_RESPONSE = "Save acknowledged.";
    
    private static final String TEST_URL = "TEST"; // The special-cased test page URL.
    private static final String LIB_FILE = "AuO.js"; // The library file to use.
    
    private final String path; // Relative path to the root of the AuO.js file.
    private final Server server; // The Jetty Server that's backing the AuO server.
    
    private String auoJS; // Server-cached value of the AuO.js file.
    private long auoJsModified; // Server-cached last modification time of the AuO.js file.
    
    private volatile ServerState state; // The current state of the server: RUNNING or STOPPED.
    
    /**
     * Launches a new Jetty server for the AuO standalone application, with SSL enabled. Requires
     * the path (absolute or relative) to AuO.js file to be served and a port for the server.
     * 
     * @param path
     *            the absolute or relative path to the AuO.js file.
     * @param port
     *            the port to use for the server. A value of 0 results in a randomly-chosen open
     *            port.
     * @return An instance of AuoServer if the construction and start succeeded.
     * @throws RuntimeException
     *             if an error occurs when starting the server.
     */
    public static AuoServer start(final String path, final int port) {
        final Server server = new Server(port);
        final AuoServer auoServer = new AuoServer(path, server);
        server.setHandler(auoServer);
        try {
            server.start();
            auoServer.onStart();
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
        return auoServer;
    }
    
    /**
     * Creates a new AuoServer instance to handle incoming requests. Upon termination of the server,
     * will join the Jetty server to properly exit.
     * 
     * @param path
     *            the absolute or relative path to the AuO.js file.
     * @param server
     *            the Jetty Server object encapsulating this server.
     */
    private AuoServer(final String path, final Server server) {
        this.path = path;
        this.server = server;
        this.state = ServerState.STOPPED;
        auoJsModified = -1;
        try {
            cacheAuoJs();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    
    /**
     * Caches the AuO.js file if it has been modified since the last cache. Is thread-safe.
     * 
     * @throws IOException
     *             if an error occurs during the reading of the AuO.js file to cache.
     */
    private synchronized void cacheAuoJs() throws IOException {
        final long lastModified = new File(path + LIB_FILE).lastModified();
        if (lastModified > auoJsModified) {
            final StringBuilder auoJsBuilder = new StringBuilder();
            final BufferedReader fin = new BufferedReader(new FileReader(path + LIB_FILE));
            fin.lines().forEachOrdered(line -> auoJsBuilder.append(line + "\n"));
            fin.close();
            
            auoJS = auoJsBuilder.toString();
            auoJsModified = lastModified;
        }
    }
    
    /**
     * Sets the configuration of the server when the Jetty server starts.
     */
    private void onStart() {
        this.state = ServerState.RUNNING;
        Log.log(Log.INFO, "Server running in localhost on port %d.", getPort());
    }
    
    /**
     * Returns the port that this server is running on.
     * 
     * @return An int, the port that this server is running on.
     */
    public int getPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }
    
    /**
     * Returns the URL that locates the root of this server.
     * 
     * @return A String, the URL for the root of this server.
     */
    public String getURL() {
        return String.format("http://localhost:%d/", getPort());
    }
    
    /**
     * Returns the URL for the test page of this server.
     * 
     * @return A String, the special-cased URL for the test page.
     */
    public String getTestURL() {
        return getURL() + TEST_URL;
    }
    
    /**
     * Handles a connection to this server.
     * 
     * @param socket
     *            the Socket corresponding to the connection.
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        // If server has been logically stopped, send a 404: File not found.
        if (ServerState.STOPPED == state) {
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            baseRequest.setHandled(true);
            return;
        }
        
        // Reformat the target string a little.
        target = target.replaceAll("/+", "/"); // Removes all duplicate slashes.
        target = target.replaceAll("^/*", ""); // Removes initial slash from the target.
        
        if ("stop".equalsIgnoreCase(target)) {
            terminate();
            return;
        } else if (TEST_URL.equals(target)) { // Generates testing AuO HTML page.
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    // @formatter:off
  "<html>"
+ "<head>"
+ "<script type=\"application/javascript\" src=\"AuO.js\"></script>"
+ "</head>"
+ "<body>"
+ "<script type=\"application/javascript\">"
+ "new AuO(\"" + getURL() + "TEST-SAVE\", function (request) {alert(request.response);}).launch();"
+ "</script>"
+ "</body>"
+ "</html>"
                    // @formatter:on
            );
        } else if ((TEST_URL + "-SAVE").equals(target)) { // Generates responses to save requests.
            response.setContentType("text/plain; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(SAVE_OK_RESPONSE);
        } else if (LIB_FILE.equalsIgnoreCase(target)) {
            cacheAuoJs();
            response.setContentType("application/javascript; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(auoJS);
        } else {
            // Prepare to write document body.
            final PrintWriter out = response.getWriter();
            
            // Search for files down a list of priorities.
            final String[] suffices = { "", "/index.php", "/index.html" };
            File targetFile = null;
            for (final String suffix : suffices) {
                final String filePath = (path + target + suffix).replaceAll("/+", "/");
                final File file = new File(filePath);
                
                // Select the file and exit the loop.
                if (file.isFile()) {
                    targetFile = file;
                    break;
                }
            }
            
            // Read (if possible) and output.
            if (null != targetFile) {
                response.setContentType(
                        Files.probeContentType(targetFile.toPath()) + "; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                
                final BufferedReader fin = new BufferedReader(new FileReader(targetFile));
                fin.lines().forEachOrdered(line -> out.println(line));
                fin.close();
            } else { // Send a 404: File not found.
                response.setContentType("text/html; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        
        // Tell Jetty that we have handled this request.
        baseRequest.setHandled(true);
    }
    
    /**
     * Terminates the server and joins all threads back together.
     * 
     * @throws RuntimeException
     *             if termination does not succeed.
     */
    public void terminate() {
        Log.log(Log.INFO, "Stopping server...");
        
        final int port = getPort(); // Grab this value before the server terminates.
        state = ServerState.STOPPED;
        
        try {
            server.stop();
            server.getThreadPool().join();
            Log.log(Log.INFO, "Terminated server running in localhost:%d.", port);
        } catch (Exception exp) {
            Log.log(Log.ERROR, "Failed to terminate server running in localhost:%d.", port);
            throw new RuntimeException(exp);
        }
    }
    
    /**
     * Runs this server as a standalone instance.
     * 
     * @throws Exception
     */
    public static void main(String[] args) {
        final AuoServer server = AuoServer.start("../lib/", 4444);
        server.terminate(); // Comment this line out if you want a living server.
    }
}