package com.x.base.core.project.exception;

public class ExceptionFieldEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 8181296584823275140L;

	public static String defaultMessage = "参数: {} 值无效.";

	public ExceptionFieldEmpty(String field) {
		super(defaultMessage, field);
	}

}
