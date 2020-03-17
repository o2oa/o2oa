package com.x.cms.assemble.control.queue;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExists( String docId ) {
		super("文档信息不存在，无法进行数据保存。ID:" + docId );
	}
}
