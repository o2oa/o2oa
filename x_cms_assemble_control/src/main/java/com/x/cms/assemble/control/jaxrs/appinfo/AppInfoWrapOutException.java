package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoWrapOutException( Throwable e ) {
		super("将查询出来的应用栏目信息对象转换为可输出的数据信息时发生异常。", e );
	}
}
