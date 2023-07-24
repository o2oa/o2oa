package com.x.base.core.project.exception;

public class ExceptionUnauthorized extends LanguagePromptException {

	private static final long serialVersionUID = 8121998765154409958L;

	public ExceptionUnauthorized() {
		super("会话已过期或未登录.");
	}
}
