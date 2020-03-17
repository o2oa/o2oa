package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataItemPermission extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDataItemPermission( Throwable e, String docId ) {
		super("系统在进行文档操作权限查询时发生异常。ID:" + docId, e );
	}
}
