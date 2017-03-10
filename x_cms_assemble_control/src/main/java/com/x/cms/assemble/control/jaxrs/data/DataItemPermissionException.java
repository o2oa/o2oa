package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.exception.PromptException;

class DataItemPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DataItemPermissionException( Throwable e, String docId ) {
		super("系统在进行文档操作权限查询时发生异常。ID:" + docId, e );
	}
}
