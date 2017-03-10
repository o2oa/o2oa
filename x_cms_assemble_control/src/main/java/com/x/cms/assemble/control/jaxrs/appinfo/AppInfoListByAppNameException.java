package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoListByAppNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoListByAppNameException( Throwable e, String appName ) {
		super("系统根据应用栏目名称查询应用栏目信息对象时发生异常。AppName:" + appName, e );
	}
}
