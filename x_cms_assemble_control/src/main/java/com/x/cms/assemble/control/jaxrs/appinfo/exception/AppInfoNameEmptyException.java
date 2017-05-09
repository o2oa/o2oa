package com.x.cms.assemble.control.jaxrs.appinfo.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoNameEmptyException() {
		super("应用栏目信息名称AppName为空，无法继续保存数据。" );
	}
}
