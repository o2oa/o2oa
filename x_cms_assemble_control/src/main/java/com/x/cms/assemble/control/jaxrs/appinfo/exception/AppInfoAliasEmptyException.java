package com.x.cms.assemble.control.jaxrs.appinfo.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoAliasEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoAliasEmptyException() {
		super("应用栏目信息标识为空，无法继续查询数据。" );
	}
}
