package com.x.base.core.project;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFindApplicationName extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionFindApplicationName(String name) {
		super("can not find application with name:{}.", name);
	}

}
