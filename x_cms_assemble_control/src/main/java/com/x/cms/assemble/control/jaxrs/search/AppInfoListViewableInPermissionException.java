package com.x.cms.assemble.control.jaxrs.search;

import com.x.base.core.exception.PromptException;

class AppInfoListViewableInPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoListViewableInPermissionException( Throwable e, String name ) {
		super("系统在根据用户权限查询所有可见的栏目信息时发生异常。Name:" + name );
	}
}
