package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoDeleteException( Throwable e, String id ) {
		super("应用栏目信息删除时发生异常。ID:" + id, e );
	}
}
