package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class DocumentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentNotExistsException( String id ) {
		super("文档信息不存在，无法继续进行操作。id:" + id );
	}
}
