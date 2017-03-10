package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingSaveException( Throwable e ) {
		super("系统在保存BBS系统设置信息时发生异常.", e );
	}
}
