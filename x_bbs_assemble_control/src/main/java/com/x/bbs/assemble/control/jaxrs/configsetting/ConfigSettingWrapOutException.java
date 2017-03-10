package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingWrapOutException( Throwable e ) {
		super("系统在转换所有BBS系统设置信息为输出对象时发生异常.", e );
	}
}
