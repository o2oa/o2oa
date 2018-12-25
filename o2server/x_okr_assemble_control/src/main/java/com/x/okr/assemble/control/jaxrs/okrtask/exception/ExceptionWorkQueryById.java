package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkQueryById( Throwable e, String id ) {
		super("系统根据ID查询指定的具体工作信息时发生异常!ID:" + id, e );
	}
}
