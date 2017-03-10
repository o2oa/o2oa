package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingQueryByCodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingQueryByCodeException( Throwable e, String code ) {
		super("系统在根据编码获取BBS系统设置信息时发生异常！Code:" + code, e );
	}
}
