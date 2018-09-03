package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemConfigDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemConfigDelete( Throwable e, String flag ) {
		super("删除指定的系统配置时发生异常。Flag:" + flag, e );
	}
}
