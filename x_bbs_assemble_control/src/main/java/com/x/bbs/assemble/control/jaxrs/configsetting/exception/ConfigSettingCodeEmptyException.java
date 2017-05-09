package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.exception.PromptException;

public class ConfigSettingCodeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ConfigSettingCodeEmptyException() {
		super("code为空， 无法进行查询或者保存." );
	}
}
