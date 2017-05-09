package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.exception.PromptException;

public class ConfigSettingProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ConfigSettingProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
