package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoNameEmptyException() {
		super("应用栏目信息名称AppName为空，无法继续保存数据。" );
	}
}
