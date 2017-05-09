package com.x.cms.assemble.control.jaxrs.documentviewrecord.exception;

import com.x.base.core.exception.PromptException;

public class DocumentIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DocumentIdEmptyException() {
		super( "文档ID为空，无法查询文档访问记录信息列表。" );
	}
}
