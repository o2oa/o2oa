package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.exception.PromptException;

class ConfigSettingNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ConfigSettingNotExistsException( String id ) {
		super("指定ID的BBS系统设置不存在.ID:" + id );
	}
}
