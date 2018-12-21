package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.CallbackPromptException;

public class ExceptionDocumentNotExistsCallback extends CallbackPromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDocumentNotExistsCallback( String callbackName, String id ) {
		super(callbackName, "文档信息不存在，无法继续进行操作。Id:" + id );
	}
}
