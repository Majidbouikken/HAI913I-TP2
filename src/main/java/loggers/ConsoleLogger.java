package loggers;

public class ConsoleLogger extends Logger {

	// CONSTRUCTORS
	public ConsoleLogger(LogRequestLevel level) {
		super(level);
	}

	// METHODS
	@Override
	protected void write(LogRequest request) {
		System.out.println(request.getMessage());
	}
}