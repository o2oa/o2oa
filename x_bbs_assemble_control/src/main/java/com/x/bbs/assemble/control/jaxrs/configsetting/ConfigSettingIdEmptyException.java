package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingIdEmptyException() {
		super("id为空， 无法进行查询." );
	}
}
