package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemConfigNotExists( String flag ) {
		super("指定的系统配置信息不存在。Flag:" + flag );
	}
}
