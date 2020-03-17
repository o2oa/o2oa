package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExists( String id ) {
		super("文档信息不存在，无法继续进行操作。Id:" + id );
	}
}
