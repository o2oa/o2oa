package com.x.base.core.project.exception;

public class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionPersonNotExist(String flag) {
		super("指定的用户不存在:{}.", flag);
	}

}
