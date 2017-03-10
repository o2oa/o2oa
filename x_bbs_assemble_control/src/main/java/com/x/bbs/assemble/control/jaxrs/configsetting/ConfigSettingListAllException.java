package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingListAllException( Throwable e ) {
		super("系统在获取所有BBS系统设置信息时发生异常.", e);
	}
}
