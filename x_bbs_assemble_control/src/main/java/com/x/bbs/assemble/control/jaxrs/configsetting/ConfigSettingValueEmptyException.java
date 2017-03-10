package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingValueEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingValueEmptyException() {
		super("配置内容value为空， 无法进行保存." );
	}
}
