package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class TaskInfoIsNotForReadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskInfoIsNotForReadException( String id ) {
		super("您尝试处理的信息并不是待阅信息，无法进一步处理。!ID:" + id );
	}
}
