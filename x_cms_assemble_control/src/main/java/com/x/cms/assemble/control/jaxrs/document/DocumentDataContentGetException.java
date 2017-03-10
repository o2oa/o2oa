package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentDataContentGetException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentDataContentGetException( Throwable e, String id ) {
		super("系统获取文档数据内容信息时发生异常。Id:" + id, e );
	}
}
