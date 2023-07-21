package com.x.base.core.project.exception;

import org.slf4j.helpers.MessageFormatter;

public abstract class PromptException extends Exception {

	protected static String format(String message, Object... os) {
		return MessageFormatter.arrayFormat(message, os).getMessage();
	}

	private static final long serialVersionUID = -6306372564902986731L;

	protected PromptException() {
		super();
	}

	protected PromptException(String message) {
		super(message);
	}

	protected PromptException(String message, Object... os) {
		super(format(message, os));
	}

	protected PromptException(Throwable cause) {
		super(cause);
	}

	protected PromptException(Throwable cause, String message) {
		super(message, cause);
	}

	protected PromptException(Throwable cause, String message, Object... os) {
		super(format(message, os), cause);
	}

}