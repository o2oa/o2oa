package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.exception.PromptException;

class AppInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoNotExistsException( String id ) {
		super("指定ID的应用栏目信息对象不存在。ID:" + id );
	}
}
