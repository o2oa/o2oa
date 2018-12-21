package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTaskDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTaskDelete( Throwable e, String id ) {
		super("系统根据ID删除指定的待办信息时发生异常!ID:" + id, e );
	}
}
