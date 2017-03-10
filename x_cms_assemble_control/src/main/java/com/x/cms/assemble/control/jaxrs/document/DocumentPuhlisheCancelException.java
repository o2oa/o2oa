package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentPuhlisheCancelException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentPuhlisheCancelException( Throwable e, String id ) {
		super("系统将文档状态修改为发布状态时发生异常。Id:" + id, e );
	}
}
