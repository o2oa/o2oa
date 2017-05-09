package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.exception.PromptException;

public class WorkQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的具体工作信息时发生异常。ID：" + id, e );
	}
}
