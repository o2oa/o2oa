package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.exception.PromptException;

public class SystemConfigQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SystemConfigQueryByIdException( Throwable e, String id ) {
		super("根据指定的ID查询系统配置时发生异常。ID:" + id, e );
	}
}
