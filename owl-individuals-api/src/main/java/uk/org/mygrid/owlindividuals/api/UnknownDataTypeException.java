package uk.org.mygrid.owlindividuals.api;

public class UnknownDataTypeException extends BuilderException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownDataTypeException() {
		super();
	}

	public UnknownDataTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownDataTypeException(String message) {
		super(message);
	}

	public UnknownDataTypeException(Throwable cause) {
		super(cause);
	}

}
