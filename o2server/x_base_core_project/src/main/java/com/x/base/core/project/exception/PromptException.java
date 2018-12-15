package com.x.base.core.project.exception;

import org.slf4j.helpers.MessageFormatter;

public abstract class PromptException extends Exception {

	protected static String format(String message, Object... os) {
		return MessageFormatter.arrayFormat(message, os).getMessage();
	}

	private static final long serialVersionUID = -6306372564902986731L;

	public PromptException() {
		super();
	}

	public PromptException(String message) {
		super(message);
	}

	public PromptException(String message, Object... os) {
		super(format(message, os));
	}

	public PromptException(Throwable cause) {
		super(cause);
	}

	public PromptException(Throwable cause, String message) {
		super(message, cause);
	}

	public PromptException(Throwable cause, String message, Object... os) {
		super(format(message, os), cause);
	}

}