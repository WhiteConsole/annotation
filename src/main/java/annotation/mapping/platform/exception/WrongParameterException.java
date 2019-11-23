package annotation.mapping.platform.exception;

public class WrongParameterException extends RuntimeException {
	public WrongParameterException(String message) {
		super(message);
	}

	public WrongParameterException(String message, Throwable cause) {
		super(message, cause);
	}
}
