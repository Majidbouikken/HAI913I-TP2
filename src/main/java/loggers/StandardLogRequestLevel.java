package loggers;

public enum StandardLogRequestLevel implements LogRequestLevel {

	INFO(1), DEBUG(2), ERROR(3);

	/**
	 * This StandardLogRequestLevel's value.
	 */
	private int value;

	/**
	 * Creates a StandardLogRequestLevel enumeration having value as its value.
	 * 
	 * @param value The value of the StandardLogRequestLevel enumeration to create.
	 */
	private StandardLogRequestLevel(int value) {
		this.value = value;
	}

	/**
	 * Gets this StandardLogRequestLevel's value.
	 */
	@Override
	public int value() {
		return this.value;
	}
}