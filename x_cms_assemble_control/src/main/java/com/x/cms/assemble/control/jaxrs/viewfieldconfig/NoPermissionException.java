package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.exception.PromptException;

class NoPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public NoPermissionException( String personName ) {
		super( "person{name:" + personName + "} 用户没有内容管理展示列配置信息信息操作的权限！" );
	}
}
