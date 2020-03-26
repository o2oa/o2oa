package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemConfigQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemConfigQueryById( Throwable e, String id ) {
		super("根据指定的ID查询系统配置时发生异常。ID:" + id, e );
	}
}
