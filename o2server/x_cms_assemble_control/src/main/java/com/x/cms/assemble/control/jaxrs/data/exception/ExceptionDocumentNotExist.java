package com.x.cms.assemble.control.jaxrs.data.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDocumentNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDocumentNotExist( String docId ) {
		super("文档信息不存在，无法进行数据操作。ID:" + docId );
	}
}
