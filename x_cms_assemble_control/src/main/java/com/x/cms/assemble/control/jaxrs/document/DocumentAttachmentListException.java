package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentAttachmentListException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentAttachmentListException( Throwable e, String id ) {
		super("系统获取文档附件内容列表时发生异常。Id:" + id, e );
	}
}
