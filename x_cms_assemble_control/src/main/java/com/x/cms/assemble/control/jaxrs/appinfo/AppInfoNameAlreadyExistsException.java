package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoNameAlreadyExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoNameAlreadyExistsException( String name ) {
		super("应用名称已经被占用，无法继续保存数据。Name:" + name );
	}
}
