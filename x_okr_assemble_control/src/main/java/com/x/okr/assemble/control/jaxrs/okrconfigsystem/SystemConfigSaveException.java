package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import com.x.base.core.exception.PromptException;

class SystemConfigSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SystemConfigSaveException( Throwable e ) {
		super("Okr系统配置信息保存时发生异常。", e );
	}
}
