package loggers;

public abstract class Logger implements ILogger {

	/* ATTRIBUTES */
	/**
	 * This Logger's successor in the chain of responsibility.
	 */
	protected ILogger nextLogger;

	/**
	 * This Logger's level of requests for which it is responsible.
	 */
	protected LogRequestLevel level;

	/* CONSTRUCTORS */
	/**
	 * Creates a Logger with an associated request level level.
	 * 
	 * @param level The log request level associated with the Logger to create.
	 */
	public Logger(LogRequestLevel level) {
		this.level = level;
	}

	/* METHODS */
	@Override
	public void setNextLogger(ILogger nextLogger) {
		this.nextLogger = nextLogger;

	}

	/**
	 * Logs request if this logger's associated request level is less than or equal
	 * to that of request. It also forwards the request to its successor, if one is
	 * defined.
	 */
	@Override
	public void log(LogRequest request) {
		if (this.level.value() <= request.getLevel().value())
			write(request);

		if (nextLogger != null)
			nextLogger.log(request);
	}

	/**
	 * Writes request's message during the logging process.
	 * 
	 * @param request The log request whose message will be written during the
	 *                logging process.
	 */
	protected abstract void write(LogRequest request);
}