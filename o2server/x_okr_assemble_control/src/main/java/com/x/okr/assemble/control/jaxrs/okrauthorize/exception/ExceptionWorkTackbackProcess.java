package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkTackbackProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkTackbackProcess( Throwable e, String id ) {
		super("对工作进行授权收回操作过程中发生异常。Id:" + id );
	}
}
