package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoListAllException( Throwable e ) {
		super("查询所有应用栏目信息对象时发生异常。", e );
	}
}
