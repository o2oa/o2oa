package com.x.cms.assemble.control.jaxrs.data.exception;

import com.x.base.core.exception.PromptException;

public class DataItemPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DataItemPermissionException( Throwable e, String docId ) {
		super("系统在进行文档操作权限查询时发生异常。ID:" + docId, e );
	}
}
