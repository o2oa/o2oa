package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.exception.PromptException;

class PermissionWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissionWrapOutException( Throwable e ) {
		super("将查询结果转换为可输出的数据信息时发生异常.", e );
	}
}
