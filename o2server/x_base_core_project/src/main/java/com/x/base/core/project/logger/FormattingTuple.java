package com.x.base.core.project.logger;

/**
 * Holds the results of formatting done by {@link MessageFormatter}.
 * 
 * @author Joern Huxhorn
 */
public class FormattingTuple {

	public static FormattingTuple NULL = new FormattingTuple(null);

	private String message;
	private Throwable throwable;
	private Object[] argArray;

	public FormattingTuple(String message) {
		this(message, null, null);
	}

	public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
		this.message = message;
		this.throwable = throwable;
		this.argArray = argArray;
	}

	public String getMessage() {
		return message;
	}

	public Object[] getArgArray() {
		return argArray;
	}

	public Throwable getThrowable() {
		return throwable;
	}

}
