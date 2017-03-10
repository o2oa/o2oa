package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoIdEmptyException() {
		super("应用栏目信息ID为空，无法继续查询数据。" );
	}
}
