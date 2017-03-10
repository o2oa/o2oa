package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.exception.PromptException;

class DocumentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentNotExistsException( String docId ) {
		super("文档信息不存在，无法进行数据保存。ID:" + docId );
	}
}
