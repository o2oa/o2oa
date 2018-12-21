package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTaskInfoIsNotForRead extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTaskInfoIsNotForRead( String id ) {
		super("您尝试处理的信息并不是待阅信息，无法进一步处理。!ID:" + id );
	}
}
