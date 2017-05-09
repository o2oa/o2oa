package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.exception.PromptException;

public class SystemConfigNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SystemConfigNotExistsException( String flag ) {
		super("指定的系统配置信息不存在。Flag:" + flag );
	}
}
