package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkQueryById( Throwable e, String id ) {
		super("根据ID查询工作信息时发生异常。Id:" + id );
	}
}
