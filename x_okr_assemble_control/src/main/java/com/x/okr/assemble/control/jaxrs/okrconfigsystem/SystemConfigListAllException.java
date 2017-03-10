package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.exception.PromptException;

class SystemConfigListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SystemConfigListAllException( Throwable e ) {
		super("查询所有的OKR系统配置时发生异常。", e );
	}
}
