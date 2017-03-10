package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentArchiveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentArchiveException( Throwable e, String id ) {
		super("系统将文档状态修改为归档状态时发生异常。Id:" + id, e );
	}
}
