package loggers;

public interface ILogger {
	/* METHODS */
	/**
	 * Sets nextLogger as this logger's successor in the chain of responsibility.
	 * 
	 * @param nextLogger The ILogger successor of this ILogger in the chain of
	 *                   responsibility.
	 */
	void setNextLogger(ILogger nextLogger);

	/**
	 * Logs request if it's responsible for handling it, or forwards it to its
	 * ILogger successor in the chain of responsibility otherwise.
	 * 
	 * @param request The request to handle by this ILogger.
	 */
	void log(LogRequest request);
}