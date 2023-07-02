package com.x.base.core.project.exception;

public class ExceptionDeprecatedAction extends LanguagePromptException {

	private static final long serialVersionUID = 8181296584823275140L;

	public static String defaultMessage = "action is deprecated, see {}.";

	public ExceptionDeprecatedAction(String action) {
		super(defaultMessage, action);
	}

}
