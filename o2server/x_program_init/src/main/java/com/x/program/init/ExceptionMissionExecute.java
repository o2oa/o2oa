package com.x.program.init;

public class ExceptionMissionExecute extends RuntimeException {

	private static final long serialVersionUID = 2268014792668176062L;

	public ExceptionMissionExecute(Throwable cause) {
		super(cause);
	}

	public ExceptionMissionExecute(String message) {
		super(message);
	}

}
