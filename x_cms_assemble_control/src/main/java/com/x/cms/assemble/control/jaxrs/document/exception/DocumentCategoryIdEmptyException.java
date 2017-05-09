package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class DocumentCategoryIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DocumentCategoryIdEmptyException() {
		super("文档所属分类ID为空，无法创建文档信息。" );
	}
}
