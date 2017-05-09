package com.x.cms.assemble.control.jaxrs.appinfo.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoNameAlreadyExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoNameAlreadyExistsException( String name ) {
		super("应用名称已经被占用，无法继续保存数据。Name:" + name );
	}
}
