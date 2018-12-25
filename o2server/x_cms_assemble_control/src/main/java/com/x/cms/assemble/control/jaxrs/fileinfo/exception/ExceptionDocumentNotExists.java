package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDocumentNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDocumentNotExists( String id ) {
		super("文档信息不存在，无法继续进行操作。Id:" + id );
	}
}
