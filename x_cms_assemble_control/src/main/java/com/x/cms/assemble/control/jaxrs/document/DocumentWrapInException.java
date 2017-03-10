package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为一个文档信息对象时发生异常。", e );
	}
}
