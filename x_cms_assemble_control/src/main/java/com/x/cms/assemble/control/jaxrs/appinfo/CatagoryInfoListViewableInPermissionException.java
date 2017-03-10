package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoListViewableInPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoListViewableInPermissionException( Throwable e, String name ) {
		super("系统在根据用户权限查询所有可见的分类信息时发生异常。Name:" + name );
	}
}
