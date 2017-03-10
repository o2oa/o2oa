package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID获取BBS系统设置信息时发生异常！ID:" + id, e );
	}
}
