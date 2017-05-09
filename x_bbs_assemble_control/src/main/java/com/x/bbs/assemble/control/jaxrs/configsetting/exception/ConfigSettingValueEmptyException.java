package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.exception.PromptException;

public class ConfigSettingValueEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ConfigSettingValueEmptyException() {
		super("配置内容value为空， 无法进行保存." );
	}
}
