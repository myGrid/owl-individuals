package uk.org.mygrid.owlindividuals.api;

public class MultipleFoundException extends BuilderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleFoundException() {
		super();
	}
	
	public MultipleFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public MultipleFoundException(String message) {
		super(message);
	}

	public MultipleFoundException(Throwable cause) {
		super(cause);
	}

}
