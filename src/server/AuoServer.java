package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * class AuoServer
 * 
 * This launches a Jetty server to serve the AuO standalone application at its root. Uses SSL to
 * enable the use of the Web Audio API.
 * 
 * @author wqian94
 */
public class AuoServer extends AbstractHandler {
	private final String path;  // Relative path to the root of the standalone application.
	private final int port;  // The port to run this server at.
	private final Server server;  // The Jetty Server that's backing the AuO server.
	
	private volatile ServerState state;  // The current state of the server: RUNNING or STOPPED.
	
	/**
	 * Launches a new Jetty server for the AuO standalone application, with SSL enabled. Requires
	 * the path (absolute or relative) to the file to be served and a port for the server.
	 * 
	 * @param path the absolute or relative path to the standalone application.
	 * @param port the port to use for the server.
	 * @throws Exception if an error occurs when starting the server.
	 */
	public static AuoServer start(final String path, final int port) throws Exception {
		final Server server = new Server(port);
		final AuoServer auoServer = new AuoServer(path, port, server);
		server.setHandler(auoServer);
		server.start();
		
		return auoServer;
	}
	
	/**
	 * Creates a new AuoServer instance to handle incoming requests. Upon termination of the
	 * server, will join the Jetty server to properly exit.
	 * 
	 * @param path the absolute or relative path to the standalone application.
	 * @param port the port to use for the server.
	 * @param server the Jetty Server object encapsulating this server.
	 */
	private AuoServer(final String path, final int port, final Server server) {
		this.path = path;
		this.port = port;
		this.server = server;
		this.state = ServerState.RUNNING;
	    
		Log.log(Log.INFO, "Server running in localhost on port %d.", port);
	}
	
	/**
	 * Returns the URL that locates the root of this server.
	 * 
	 * @return A String, the URL for the root of this server.
	 */
	public String getURL() {
		return String.format("http://localhost:%d/", port);
	}
	
	/**
	 * Handles a connection to this server.
	 * 
	 * @param socket the Socket corresponding to the connection.
	 */
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		// Reformat the target string a little.
		target = target.replaceAll("/+", "/");  // Removes all duplicate slashes.
		
		if ("/stop".equalsIgnoreCase(target)) {
			terminate();
			return;
		}
		
		// Prepare to write document body.
		final PrintWriter out = response.getWriter();
		
		// Search for files down a list of priorities.
		final String[] suffices = {"", "/index.php", "/index.html"};
		File targetFile = null;
		for (final String suffix : suffices) {
			final String filePath = (path + target + suffix).replaceAll("/+",  "/");
			final File file = new File(filePath);
			
			// Select the file and exit the loop.
			if (file.isFile()) {
				targetFile = file;
				break;
			}
		}
		
		// Read (if possible) and output.
		if (null != targetFile) {
			response.setContentType(Files.probeContentType(targetFile.toPath()) +
					"; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			
			final BufferedReader fin = new BufferedReader(new FileReader(targetFile));
			fin.lines().forEachOrdered(line -> out.println(line));
		} else {
			response.setContentType("text/html; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		// Tell Jetty that we have handled this request.
		baseRequest.setHandled(true);
	}
	
	/**
	 * Terminates the server and joins all threads back together.
	 * 
	 * @throws Exception 
	 */
	public void terminate() {
		Log.log(Log.INFO, "Stopping server...");
		state = ServerState.STOPPED;
		try {
			server.stop();
			server.getThreadPool().join();
			Log.log(Log.INFO, "Terminated server running in localhost:%d.", port);
		} catch (Exception exp) {
			Log.log(Log.ERROR, "Failed to terminate server running in localhost:%d.", port);
			exp.printStackTrace();
		}
	}
	
	/**
	 * Runs this server as a standalone instance.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final AuoServer server = AuoServer.start("../", 4444);
		server.terminate();
	}
}