package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentPermissionTransfer extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentPermissionTransfer( Throwable e, String id ) {
		super( "系统在转换文档权限数据结构时发生异常。ID:{}" ,id, e );
	}
}
