package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.exception.PromptException;

public class SystemConfigDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SystemConfigDeleteException( Throwable e, String flag ) {
		super("删除指定的系统配置时发生异常。Flag:" + flag, e );
	}
}
