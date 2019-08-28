package com.x.base.core.project.exception;

public class ExceptionUnknowValue extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionUnknowValue(Object value) {
		super("未知的值:{}.", value);
	}

}
