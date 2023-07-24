package com.x.base.core.project.exception;

public class ExceptionPersonNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static String defaultMessage = "用户不存在:{}.";

	public ExceptionPersonNotExist(String flag) {
		super(defaultMessage, flag);
	}

}
