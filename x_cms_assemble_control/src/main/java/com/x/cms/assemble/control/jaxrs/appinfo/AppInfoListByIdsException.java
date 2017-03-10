package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoListByIdsException( Throwable e ) {
		super("系统根据ID列表查询应用栏目信息对象时发生异常。", e );
	}
}
