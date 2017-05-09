package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoNotExistsException( String id ) {
		super("指定ID的应用栏目信息对象不存在。ID:" + id );
	}
}
