package com.x.base.core.project.exception;

import org.slf4j.helpers.MessageFormatter;

public class RunningException extends Exception {

	private static String format(String message, Object... os) {
		return MessageFormatter.arrayFormat(message, os).getMessage();
	}

	private static final long serialVersionUID = 2197866911052294881L;

	public RunningException() {
		super();
	}

	public RunningException(String message) {
		super(message);
	}

	public RunningException(String message, Object... os) {
		super(format(message, os));
	}

	public RunningException(Throwable cause) {
		super(cause);
	}

	public RunningException(Throwable cause, String message) {
		super(message, cause);
	}

	public RunningException(Throwable cause, String message, Object... os) {
		super(format(message, os), cause);
	}

}