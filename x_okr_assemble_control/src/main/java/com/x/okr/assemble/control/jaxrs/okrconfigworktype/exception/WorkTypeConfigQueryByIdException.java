package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.exception.PromptException;

public class WorkTypeConfigQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkTypeConfigQueryByIdException( Throwable e, String id ) {
		super("系统根据ID删除指定的工作类别配置时发生异常。ID:" + id, e);
	}
}
