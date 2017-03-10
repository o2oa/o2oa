package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为一个应用栏目信息对象时发生异常。", e );
	}
}
