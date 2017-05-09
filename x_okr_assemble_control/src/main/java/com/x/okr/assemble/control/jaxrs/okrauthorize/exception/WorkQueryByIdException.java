package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.exception.PromptException;

public class WorkQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkQueryByIdException( Throwable e, String id ) {
		super("根据ID查询工作信息时发生异常。Id:" + id );
	}
}
