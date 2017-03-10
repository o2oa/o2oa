package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentViewByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentViewByIdException( Throwable e, String id, String name ) {
		super("文档信息访问操作时发生异常。Id:" + id + ", Name:" + name, e );
	}
}
