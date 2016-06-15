package server;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum Log {
	INFO("[INFO] "), ERROR("[ERROR] ");
	
	private final static PrintStream logStream = new PrintStream(System.err);
	private final static DateTimeFormatter logDateFormatter =
			DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS:");  // Used for logs.
	
	private final String tag;  // The tag printed along with the log entry.
	
	/**
	 * Makes a log entry using the given timestamp and message.
	 * 
	 * @param timestamp the Instant to use as the timestamp.
	 * @param message the formatted string of the message to deliver.
	 * @param arguments the arguments to pass into the formatted string.
	 */
	public static void log(final LocalDateTime timestamp, final String message,
			final Object... arguments) {
		final Object[] newArguments = new Object[arguments.length + 1];
		newArguments[0] = getLogTimestamp(timestamp);
		System.arraycopy(arguments, 0, newArguments, 1, arguments.length);
		logStream.printf("%s" + message + "%n", newArguments);
	}
	
	/**
	 * Makes a log entry using the current time as the timestamp, with the given message.
	 * 
	 * @param channel the log channel (enum value of Log) to use.
	 * @param message the formatted string of the message to deliver.
	 * @param arguments the arguments to pass into the formatted string.
	 */
	public static void log(final Log channel, final String message, final Object... arguments) {
		log(LocalDateTime.now(), channel.tag + message, arguments);
	}
	
	/**
	 * Makes a log entry with the "[INFO]" tag using the current time as the timestamp, with the
	 * given message.
	 * 
	 * @param message the formatted string of the message to deliver.
	 * @param arguments the arguments to pass into the formatted string.
	 */
	public static void log(final String message, final Object... arguments) {
		log(LocalDateTime.now(), message, arguments);
	}
	
	/**
	 * Returns a timestamp used for logging purposes, based on the provided time.
	 * 
	 * @param timestamp the Instant to convert.
	 * @return A String, the timestamp formatted using the static logDateFormat.
	 */
	private static String getLogTimestamp(final LocalDateTime timestamp) {
		return logDateFormatter.format(timestamp);
	}
	
	private Log(final String tag) {
		this.tag = tag;
	}
}