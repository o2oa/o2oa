package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoSaveException( Throwable e ) {
		super("应用栏目信息保存时发生异常。", e );
	}
}
