package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingUpdateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingUpdateException( Throwable e, String id ) {
		super("根据ID更新BBS系统设置信息时发生异常.ID:" + id, e );
	}
}
